package org.swinburne.engine.Parser;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.NodeType;
import org.swinburne.model.Way;
import org.swinburne.util.UnitConverter;

import java.io.*;
import java.util.ArrayList;

// Reference: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
public class MapTrafficSignalCSVParser {

    public static Graph parseFromTrafficSignal(String filename) {
        return parse(filename, null, null, null, null);
    }

    public static Graph parseFromTrafficSignal(String filename, double top, double left, double bottom, double right) {
        return parse(filename, top, left, bottom, right);
    }

    //@TODO: this whole thing might slow things down
    public static Graph parse(String csvFileName, Double top, Double left, Double bottom, Double right) {
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";

        Graph graph = new Graph();
        boolean bounded = false;
        if (top != null && left != null && bottom != null && right != null) bounded = true;

        try {
            br = new BufferedReader(new FileReader(csvFileName));

            boolean headingPassed = false;
            while ((line = br.readLine()) != null) {
                if (!headingPassed) {
                    headingPassed = true;
                    continue;
                }

                String[] rawData = line.split(csvSplitBy);

                // May be cleared up later
                double y = Double.parseDouble(rawData[0]);
                double x = Double.parseDouble(rawData[1]);
                String objectID = rawData[2];
                int tlights = Integer.parseInt(rawData[3]);
                int tlightsId = Integer.parseInt(rawData[4]);
                String siteNo = rawData[5];
                String siteName = rawData[6];
                String siteType = rawData[7];
                String directory = rawData[8];
                String dirRef = rawData[9];
                String dAdded = rawData[10];
                String dTowns = rawData[11];
                String dEdited = rawData[12];
                String dRemoved = rawData[13];
                String linkMode = rawData[14];
                String status = rawData[15];
                String comments = rawData[16];

                if (bounded)
                    if (x > top || x < bottom || y < left || y > right) continue;

                if (siteType.equalsIgnoreCase("INT")) {
                    Node intersectionNode = new Node(x, y);
                    intersectionNode.setId(objectID);
                    intersectionNode.setLabel(siteName);
                    graph.addNode(intersectionNode);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (Node n : graph.getNodeList()) {
                String[] streetList = getStreetList(n);
                for (String s : streetList) {
                    boolean streetExist = false;
                    for (Way w : n.getWayArrayList()) {
                        if (w.getLabel().equalsIgnoreCase(s)) {
                            streetExist = true;
                            break;
                        }
                    }

                    if (streetExist) continue;

                    Way foundWay = findWayWithName(n, s);
                    if (foundWay != null) continue;

                    Node findNodeWithWay = findClosestNode(graph, n, s, true);

                    if (findNodeWithWay != null) {
                        Way selectedWay = null;

                        for (Way w : findNodeWithWay.getWayArrayList()) {
                            if (w.getLabel().equalsIgnoreCase(s)) {
                                selectedWay = w;
                                break;
                            }
                        }

                        selectedWay.addNode(n);
                    } else {
                        if (n.getLabel().equals("ELIZABETH/FLINDERS")) {
                            System.out.println("FOUND");
                        }

                        Way way = new Way();
                        way.setLabel(s);
                        way.addNode(n);
                        graph.addWay(way);
                    }
                }
            }

            sortWayPoint(graph);
        }

        return graph;
    }

    private static void sortWayPoint(Graph graph) {
        for (Way w : graph.getWayList()) {
            if (w.getNodeOrderedList().size() == 0) continue;
            System.out.println("ORIGINAL SIZE: " + w.getNodeOrderedList().size());
            int original = w.getNodeOrderedList().size();

            if (original == 1) {
                for (Node n : w.getNodeOrderedList()) {
                    System.out.println(n.getLabel());
                }
            }

            Node topMostNode = null;
            double topLat = -Double.MAX_VALUE;
            Node leftMostNode = null;
            double leftLon = Double.MAX_VALUE;
            Node bottomMostNode = null;
            double bottomLat = Double.MAX_VALUE;
            Node rightMostNode = null;
            double rightLon = -Double.MAX_VALUE;

            for (Node n : w.getNodeOrderedList()) {
                if (n.getLatitude() > topLat) {
                    topMostNode = n;
                    topLat = n.getLatitude();
                }

                if (n.getLatitude() < bottomLat) {
                    bottomMostNode = n;
                    bottomLat = n.getLatitude();
                }

                if (n.getLongitude() < leftLon) {
                    leftMostNode = n;
                    leftLon = n.getLongitude();
                }

                if (n.getLongitude() > rightLon) {
                    rightMostNode = n;
                    rightLon = n.getLongitude();
                }
            }

            Node startingNode;
            if (topMostNode == leftMostNode) startingNode = topMostNode;
            else if (bottomMostNode == leftMostNode) startingNode = bottomMostNode;
            else startingNode = leftMostNode;

            boolean vertical = true;

            ArrayList<Node> visited = new ArrayList<>();
            ArrayList<Node> sorted = new ArrayList<>();
            Node selectedNode = startingNode;
            sorted.add(selectedNode);

            for (int i = 0; i < w.getNodeOrderedList().size() - 1; i++) {
                Node closestNode = null;
                double closestDistance = Double.MAX_VALUE;

                for (Node n : w.getNodeOrderedList()) {
                    if (n == selectedNode) continue;
                    if (visited.contains(n)) continue;

                    double distance = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), selectedNode.getLatitude(), selectedNode.getLongitude());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestNode = n;
                    }
                }

                if (closestNode != null) {
                    visited.add(selectedNode);
                    selectedNode = closestNode;
                    sorted.add(selectedNode);
                }
            }

            w.setNodeList(sorted);
            int modified = w.getNodeOrderedList().size();
            System.out.println("MODIFIED: " + modified);
            if (modified != original) {
                System.out.println("FAILED");
            }

        }
    }

    private static Way findWayWithName(Node node, String street) {
        for (Way w : node.getWayArrayList()) {
            if (w.getLabel().equalsIgnoreCase(street)) {
                return w;
            }
        }

        return null;
    }


    //@TODO: crazy idea, but can we make the array of node sorted based on the location of the node in regards to everything? like the first element is the left top most one and the next one is the one on the right?

    private static Node findClosestNode(Graph graph, Node selectedNode, String street, boolean withWay) {
        Node closestNode = null;
        double closestDistance = Double.MAX_VALUE;

        for (Node n : graph.getNodeList()) {
            if (n == selectedNode) continue;

            if (containStreet(n, street)) {
                if (withWay) {
                    if (findWayWithName(n, street) != null) {
                        double distance = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), selectedNode.getLatitude(), selectedNode.getLongitude());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestNode = n;
                        }
                    }
                } else {
                    double distance = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), selectedNode.getLatitude(), selectedNode.getLongitude());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestNode = n;
                    }
                }
            }
        }

        return closestNode;
    }

    private static boolean containStreet(Node node, String street) {
        String[] streetList = getStreetList(node);
        for (String s : streetList) {
            if (s.equalsIgnoreCase(street)) return true;
        }

        return false;
    }

    private static String[] getStreetList(Node node) {
        return node.getLabel().split("/|( / )");
    }



    private static Node getClosestNode(Graph graph, double lat, double lon) {
        Node closestNode = null;
        // The closest distance that is written here is the maximum threshold distance in meters
        double closestDistance = 10;

        for (Node n : graph.getNodeList()) {
            double calculatedDistance = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), lat, lon);
            if (calculatedDistance < closestDistance) {
                closestDistance = calculatedDistance;
                closestNode = n;
            }
        }
        return closestNode;
    }
}

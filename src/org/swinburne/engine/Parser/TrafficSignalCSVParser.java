package org.swinburne.engine.Parser;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.NodeType;
import org.swinburne.util.UnitConverter;

import java.io.*;

/**
 * Read a traffic signal CSV file based on the VicRoad data set format.
 */
public class TrafficSignalCSVParser {

    /**
     * Modify a given graph with the new traffic signal intersection CSV file and add new traffic intersection signal points.
     * @param graph graph
     * @param csvFile file with the CSV data set of traffic signal
     * @return modified graph with traffic signal
     */
    public static Graph setTrafficIntersection(Graph graph, File csvFile) {
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";

        int count = 0;

        try {
            br = new BufferedReader(new FileReader(csvFile));

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

                if (siteType.equalsIgnoreCase("INT")) {
                    Node closestNode = getClosestNode(graph, x, y);
                    if (closestNode != null) {
                        closestNode.setType(NodeType.Intersection);
                        count++;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Intersection count: " + count);

        return graph;
    }

    /**
     * Get the closest node from the coordinate specified in the CSV file within a specified threshold.
     * @param graph graph
     * @param lat latitude
     * @param lon longitude
     * @return closet node within graph in form the coordinate
     */
    private static Node getClosestNode(Graph graph, double lat, double lon) {
        Node closestNode = null;
        // The closest distance that is written here is the maximum threshold distance in meters
        double closestDistance = 10;

        for (Node n : graph.getNodeMap().values()) {
            double calculatedDistance = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), lat, lon);
            if (calculatedDistance < closestDistance) {
                closestDistance = calculatedDistance;
                closestNode = n;
            }
        }
        return closestNode;
    }
}

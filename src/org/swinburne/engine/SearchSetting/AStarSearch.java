package org.swinburne.engine.SearchSetting;

import org.swinburne.engine.FScoreComparator;
import org.swinburne.engine.HeuristicEngine;
import org.swinburne.engine.HeuristicSetting.TimeHeuristic;
import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.util.UnitConverter;
import org.swinburne.view.controller.MapController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class AStarSearch {
    private ArrayList<Node> path = new ArrayList<>();
    private boolean solutionFound = false;

    private Node start;
    private Node destination;
    private double totalDistance = 0;
    private double timeTaken = 0;
    private int frontierCount = 0;
    private int visitedCount = 0;
    private long processTimeMS = 0;
    private double sldStartToFinish = 0;

    private long startTime = 0;

    private int trafficSignalPassed = 0;
    private final double AVERAGE_INTERSECTION_WAITING_TIME_S = 30;

    private MapController.SearchTask mapTask;
    private MapController mapController = null;

    public void computeDirectionTime(Graph graph, Node start, Node destination) {
        this.start = start;
        this.destination = destination;
        solutionFound = false;
        timeTaken = 0;
        totalDistance = 0;
        trafficSignalPassed = 0;
        frontierCount = 0;
        visitedCount = 0;

        sldStartToFinish = UnitConverter.geopositionDistance(start, destination);

        startTime = System.nanoTime();

        try {
            new TimeHeuristic().generateHeuristic(graph, start, destination);
            path = new ArrayList<>();
            PriorityQueue<Node> frontiers = new PriorityQueue<Node>(50, new FScoreComparator());

            Node rootNode = start;

            ArrayList<Node> visited = new ArrayList<>();

            rootNode.setGCost(0);
            rootNode.setFValue(UnitConverter.geopositionDistance(rootNode.getLatitude(), rootNode.getLongitude(), destination.getLatitude(), destination.getLongitude()));

            frontiers.add(rootNode);

            int test = 0;
            Node selectedNode;
            while ((selectedNode = frontiers.poll()) != null) {
                if (selectedNode == destination) {
                    deriveSolutionTime(selectedNode);
                    return;
                }
                visitedCount++;
                visited.add(selectedNode);
                for (Way w : selectedNode.getWayArrayList()) {
                    Node[] adjacentNodes = w.getAdjacents(selectedNode);
                    if (adjacentNodes == null) {
                        continue;
                    }
//                double speedLimitS = UnitConverter.kmhToMs(w.getSpeedLimitKmh());
                    for (Node n : adjacentNodes) {
                        if (visited.contains(n)) continue;

                        double timeTraversed = UnitConverter.geopositionDistance(selectedNode.getLatitude(), selectedNode.getLongitude(), n.getLatitude(), n.getLongitude()) / w.getSpeedLimitKmh();
                        double totalGScore = selectedNode.getGCost() + timeTraversed;

                        boolean contained = true;
                        if (!frontiers.contains(n)) contained = false;
                        else if (totalGScore >= n.getGCost()) continue;

                        n.setParent(selectedNode);
                        selectedNode.addChild(n);

                        double distanceToGoal = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), destination.getLatitude(), destination.getLongitude());
                        double timeToGoal = distanceToGoal / UnitConverter.kmhToMs(50);

                        n.setGCost(totalGScore);
                        n.setFValue(n.getGCost() + timeToGoal);

//                        System.out.println("Node " + test++);

                        if (!contained) {
                            frontiers.add(n);
                            frontierCount++;
                        }

                        if (mapTask != null) {
                            Node tn = selectedNode;
                            mapTask.drawFrontier(tn, n);
                        }

                        if (mapController != null) {
                            Node tn = selectedNode;
                            mapController.drawFrontier(tn, n);
                        }
                    }
                }
            }

//            System.out.println("Break");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void computeDirectionDistance(Graph graph, Node start, Node destination) {
        this.start = start;
        this.destination = destination;
        solutionFound = false;
        timeTaken = 0;
        totalDistance = 0;
        trafficSignalPassed = 0;
        frontierCount = 0;
        visitedCount = 0;

        sldStartToFinish = UnitConverter.geopositionDistance(start, destination);

        startTime = System.nanoTime();

        try {

            new TimeHeuristic().generateHeuristic(graph, start, destination);

            path = new ArrayList<>();
            PriorityQueue<Node> frontiers = new PriorityQueue<Node>(50, new FScoreComparator());

            Node rootNode = start;
//        Tree<Node> tree = new Tree<Node>(rootNode);

            ArrayList<Node> visited = new ArrayList<>();

            rootNode.setGCost(0);
            rootNode.setFValue(UnitConverter.geopositionDistance(rootNode.getLatitude(), rootNode.getLongitude(), destination.getLatitude(), destination.getLongitude()));

            frontiers.add(rootNode);

            Node selectedNode;
            while ((selectedNode = frontiers.poll()) != null) {
                if (selectedNode == destination) {
                    deriveSolutionDistance(selectedNode);
                    return;
                }
                visitedCount++;
                visited.add(selectedNode);

                for (Way w : selectedNode.getWayArrayList()) {
                    Node[] adjacentNodes = w.getAdjacents(selectedNode);
                    if (adjacentNodes == null) {
                        continue;
                    }
                    for (Node n : adjacentNodes) {
                        if (visited.contains(n)) continue;

                        double timeTraversed = UnitConverter.geopositionDistance(selectedNode.getLatitude(), selectedNode.getLongitude(), n.getLatitude(), n.getLongitude()) / w.getSpeedLimitKmh();
                        double totalGScore = selectedNode.getGCost();

                        boolean contained = true;
                        if (!frontiers.contains(n)) contained = false;
                        else if (totalGScore >= n.getGCost()) continue;

                        n.setParent(selectedNode);
                        selectedNode.addChild(n);

                        double distanceToGoal = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), destination.getLatitude(), destination.getLongitude());

                        n.setGCost(totalGScore);
                        n.setFValue(n.getGCost() + distanceToGoal);
                        n.setTimeTravelled(selectedNode.getTimeTravelled() + timeTraversed);

                        if (!contained) {
                            frontiers.add(n);
                            frontierCount++;
                        }

                        if (mapTask != null) {
                            Node tn = selectedNode;
                            mapTask.drawFrontier(tn, n);
                        }

                        if (mapController != null) {
                            Node tn = selectedNode;
                            mapController.drawFrontier(tn, n);
                        }
                    }
                }
            }

//            System.out.println("Break");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculateTravelTime(Way way, double totalTime) {
        double speedLimit = way.getSpeedLimitKmh();
        // If speed limit is not defined, assume running at 40km/h
        if (speedLimit == 0) speedLimit = 40;

        double time = way.getDistance() / UnitConverter.kmhToMs(speedLimit);

        return time;
    }

    private boolean isRecurringNode(TreeNode node) {
        TreeNode selectedNode = node;
        while (selectedNode.getParent() != null) {
            selectedNode = selectedNode.getParent();
            if (selectedNode.getObject() == node.getObject()) return true;
        }
        return false;
    }

    private ArrayList<Node> deriveSolutionTime(Node destination) {
        long endTime = System.nanoTime();
        processTimeMS = (endTime - startTime) / 1000000;

        ArrayList<Node> result = new ArrayList<>();
        Node selectedTreeNode = destination;
        timeTaken = destination.getGCost();
        totalDistance = 0;
        while (selectedTreeNode != null) {
            result.add(selectedTreeNode);
            if (selectedTreeNode.getParent() != null) {
                totalDistance += UnitConverter.geopositionDistance(selectedTreeNode, selectedTreeNode.getParent());
            }
            selectedTreeNode = selectedTreeNode.getParent();
        }
        Collections.reverse(result);
        path = result;
        solutionFound = true;

        return result;
    }

    private ArrayList<Node> deriveSolutionDistance(Node destination) {
        long endTime = System.nanoTime();
        processTimeMS = (endTime - startTime) / 1000000;

        ArrayList<Node> result = new ArrayList<>();
        Node selectedTreeNode = destination;
        timeTaken = destination.getTimeTravelled();
        totalDistance = 0;
        while (selectedTreeNode != null) {
            result.add(selectedTreeNode);
            if (selectedTreeNode.getParent() != null) {
                totalDistance += UnitConverter.geopositionDistance(selectedTreeNode, selectedTreeNode.getParent());
            }
            selectedTreeNode = selectedTreeNode.getParent();
        }
        Collections.reverse(result);
        path = result;
        solutionFound = true;

        return result;
    }

    public ArrayList<Node> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    public int getTrafficSignalPassed() {
        return trafficSignalPassed;
    }

    public void setMapController(MapController.SearchTask mapTask) {
        this.mapTask = mapTask;
    }

    public void setMapControllerv2(MapController mapControllerv2) {
        this.mapController = mapControllerv2;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }

    public int getFrontierCount() {
        return frontierCount;
    }

    public int getVisitedCount() {
        return visitedCount;
    }

    public long getProcessTimeMS() {
        return processTimeMS;
    }

    public double getDistanceStartFinish() { return sldStartToFinish; }

    @Override
    public String toString() {
        if (solutionFound) {
            return new StringBuilder()
                    .append("Start: " + start.getId() + "\n")
                    .append("Finish: " + destination.getId() + "\n")
                    .append("Total Distance: " + totalDistance + "\n")
                    .append("Total Time: " + timeTaken + "\n")
                    .append("Traffic Signal Passed: " + trafficSignalPassed + "\n")
                    .append("Passed: " + path.size() + "\n")
                    .append("Visited" + visitedCount + "\n")
                    .append("Frontier: " + frontierCount + "\n")
                    .append("Processed Time (ms): " + processTimeMS)
                    .toString();
        } else {
            return new StringBuilder()
                    .append("Start: " + start.getId() + "\n")
                    .append("Finish: " + destination.getId() + "\n")
                    .append("Solution not found...")
                    .toString();
        }
    }
}
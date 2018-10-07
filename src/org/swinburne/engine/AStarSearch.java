package org.swinburne.engine;

import javafx.application.Platform;
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
    private double totalDistance;
    private double timeTaken;

    private int intersectionPassed = 0;
    private final double AVERAGE_INTERSECTION_WAITING_TIME_S = 30;

    private MapController.SearchTask mapTask;

    public void computeDirection(Graph graph, Node start, Node destination) {
        HeuristicEngine.generateHeuristic(graph, destination);

        path = new ArrayList<>();
        PriorityQueue<Node> frontiers = new PriorityQueue<Node>(50, new FScoreComparator());

        timeTaken = 0;
        totalDistance = 0;
        intersectionPassed = 0;

        Node rootNode = start;
//        Tree<Node> tree = new Tree<Node>(rootNode);

        ArrayList<Node> visited = new ArrayList<>();

        rootNode.setGCost(0);
        rootNode.setFValue(UnitConverter.geopositionDistance(rootNode.getLatitude(), rootNode.getLongitude(), destination.getLatitude(), destination.getLongitude()));

        frontiers.add(rootNode);

        Node selectedNode;
        while ((selectedNode = frontiers.poll()) != null) {
            if (selectedNode == destination) {
                deriveSolution(selectedNode);
                return;
            }
            visited.add(selectedNode);

            for (Way w : selectedNode.getWayArrayList()) {

                Node[] adjacentNodes = w.getAdjacents(selectedNode);
                if (adjacentNodes == null) {
                    continue;
                }
//                double speedLimitS = UnitConverter.kmhToMs(w.getSpeedLimitKmh());
                for (Node n : adjacentNodes) {
                    if (visited.contains(n)) continue;

                    double totalGScore = selectedNode.getGCost() + UnitConverter.geopositionDistance(selectedNode.getLatitude(), selectedNode.getLongitude(), n.getLatitude(), n.getLongitude());

                    boolean contained = true;
                    if (!frontiers.contains(n)) contained = false;
                    else if (totalGScore >= n.getGCost()) continue;

                    n.setParent(selectedNode);
                    selectedNode.addChild(n);

                    double distanceToGoal = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), destination.getLatitude(), destination.getLongitude());

                    n.setGCost(totalGScore);
                    n.setFValue(n.getGCost() + distanceToGoal);

                    if (!contained)
                        frontiers.add(n);

//                    if (treeNode.getObject().getWayArrayList().size() > 1) {
//                        intersection = true;
//                        intersectionPassed++;
//                    }

//                    treeNode.setTime(selectedNode.getTime() + timeS + (intersection ? 30 : 0));
//                    treeNode.setDistance(selectedNode.getDistance() + distance);
//                    treeNode.setCost(selectedNode.getCost() + distance + n.getFValue());

//                    treeNode.putMetaData("time", calculateTravelTime(w, selectedNode.getMetaData("time")) + (intersection ? 30 : 0));



                    if (mapTask != null) {
                        Node tn = selectedNode;
                        mapTask.drawFrontier(tn, n);
                    }
                }
            }
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

    private ArrayList<Node> deriveSolution(Node destination) {
        ArrayList<Node> result = new ArrayList<>();

        Node selectedTreeNode = destination;
//        totalDistance = destination.getDistance();
//        timeTaken = destination.getTime();
        while (selectedTreeNode != null) {
            result.add(selectedTreeNode);
            selectedTreeNode = selectedTreeNode.getParent();
        }
        Collections.reverse(result);
        path = result;

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

    public int getIntersectionPassed() {
        return intersectionPassed;
    }

    public void setMapController(MapController.SearchTask mapTask) {
        this.mapTask = mapTask;
    }

}

package org.swinburne.engine;

import javafx.application.Platform;
import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.util.UnitConverter;
import org.swinburne.view.controller.MapController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class AStarSearch {
    private ArrayList<Node> path = new ArrayList<>();
    private double totalDistance;
    private double timeTaken;

    private int intersectionPassed = 0;
    private final double AVERAGE_INTERSECTION_WAITING_TIME_S = 30;

    private MapController mapController;

    public void computeDirection(Graph graph, Node start, Node destination) {
        HeuristicEngine.generateHeuristic(graph, destination);

        path = new ArrayList<>();
        PriorityQueue<TreeNode<Node>> frontiers = new PriorityQueue<>(50, new CostComparator());

        timeTaken = 0;
        totalDistance = 0;
        intersectionPassed = 0;

        TreeNode<Node> rootNode = new TreeNode<>(start);
        Tree<Node> tree = new Tree<>(rootNode);

        rootNode.setCost(start.getHeuristic());
        frontiers.add(rootNode);

        ArrayList<Node> visited = new ArrayList<>();

        TreeNode<Node> selectedNode;
        while ((selectedNode = frontiers.poll()) != null) {
            if (selectedNode.getObject() == destination) {
                deriveSolution(selectedNode);
                return;
            }


            boolean test = false;
            if (selectedNode.getObject().getId().equalsIgnoreCase("1877118943")) test = true;
            int waycount = 0;
            for (Way w : selectedNode.getObject().getWayArrayList()) {
                waycount++;
//                if (waycount >= 2) System.out.println("Node ID " +  selectedNode.getObject().getId() + ", Way count: " + waycount);
                if (test) {
                    System.out.println("Ways: " + w.getId());
                }


                Node[] adjacentNodes = w.getAdjacents(selectedNode.getObject());
                if (adjacentNodes == null) {
//                    System.out.println("Node ID " + selectedNode.getObject().getId() + " dead-end");
                    continue;
                }

                double speedLimitS = UnitConverter.kmhToMs(w.getSpeedLimitKmh());
                for (Node n : adjacentNodes) {
                    System.out.println(n.getId());
                    if (visited.contains(n)) continue;

                    visited.add(n);
                    boolean intersection = false;
                    TreeNode<Node> treeNode = new TreeNode<>(n);
                    selectedNode.addChild(treeNode);

                    double distance = UnitConverter.geopositionDistance(selectedNode.getObject().getLatitude(), selectedNode.getObject().getLongitude(), n.getLatitude(), n.getLongitude());

                    double timeS = distance / speedLimitS;
                    if (treeNode.getObject().getWayArrayList().size() > 1) {
                        intersection = true;
                        intersectionPassed++;
                    }

                    treeNode.setTime(selectedNode.getTime() + timeS + (intersection ? 30 : 0));
                    treeNode.setDistance(selectedNode.getDistance() + distance);
                    treeNode.setCost(selectedNode.getCost() + distance + n.getHeuristic());

//                    treeNode.putMetaData("time", calculateTravelTime(w, selectedNode.getMetaData("time")) + (intersection ? 30 : 0));

                    if (mapController != null) {
                        Node tn = selectedNode.getObject();
                        Platform.runLater(() -> mapController.drawFrontier(tn, n));
                    }

                    frontiers.add(treeNode);
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

    private ArrayList<Node> deriveSolution(TreeNode<Node> destination) {
        ArrayList<Node> result = new ArrayList<>();

        TreeNode<Node> selectedTreeNode = destination;
        totalDistance = destination.getDistance();
        timeTaken = destination.getTime();
        while (selectedTreeNode != null) {
            result.add(selectedTreeNode.getObject());
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

    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }

}

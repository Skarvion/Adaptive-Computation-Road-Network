package org.swinburne.engine;

import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.util.UnitConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;


// @Note: at the moment this class is used for
public class AStarSearch {
    private ArrayList<Node> path = new ArrayList<>();
    private double totalDistance;
    private double timeTaken;

    private int intersectionPassed = 0;
    private final double AVERAGE_INTERSECTION_WAITING_TIME_S = 30;

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

        TreeNode<Node> selectedNode;
        while ((selectedNode = frontiers.poll()) != null) {
            if (selectedNode.getObject() == destination) {
                deriveSolution(selectedNode);
                return;
            }

            for (Way w : selectedNode.getObject().getWayArrayList()) {
                Node[] adjacentNodes = w.getAdjacents(selectedNode.getObject());
                if (adjacentNodes == null) continue;

                double speedLimitS = UnitConverter.kmhToMs(w.getSpeedLimitKmh());
                for (Node n : adjacentNodes) {
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

//    // For now not being used, @TODO maybe look over here again later
//    private double calculateTravelCost(Node node, Way way) {
//        return way.getDistance() + node.getHeuristic();
//    }
//
//    private ArrayList<Node> deriveSolution(TreeNode<Node> endNode) {
//        ArrayList<Node> result = new ArrayList<>();
//
//        while (endNode != null)  {
//            result.add(endNode.getObject());
//            endNode = endNode.getParent();
//        }
//        Collections.reverse(result);
//        return result;
//    }
//
//    public double calculateTravelTime(ArrayList<Node> path) {
//        if (path == null) return -1;
//        if (path.size() <= 1) return 0;
//
//        double total = 0;
//
//        for (int i = 1; i < path.size(); i++) {
//            double distance = UnitConverter.geopositionDistance(path.get(i - 1).getLatitude(), path.get(i - 1).getLongitude(), path.get(i).getLatitude(), path.get(i).getLongitude());
//
//
//        }
//    }

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
}

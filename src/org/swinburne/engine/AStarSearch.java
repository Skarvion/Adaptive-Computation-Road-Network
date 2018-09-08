package org.swinburne.engine;

import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;


// @Note: at the moment this class is used for
public class AStarSearch {
    public ArrayList<Node> computeDirection(Graph graph, Node start, Node destination) {
        HeuristicEngine.generateHeuristic(graph, destination);

        PriorityQueue<TreeNode<Node>> frontiers = new PriorityQueue<>(50, new CostComparator());

        TreeNode<Node> rootNode = new TreeNode<>(start);
        Tree<Node> tree = new Tree<>(rootNode);

        frontiers.add(rootNode);

        TreeNode<Node> selectedNode;
        while ((selectedNode = frontiers.poll()) != null) {
            if (selectedNode.getObject() == destination) return deriveSolution(selectedNode);

            for (Way w : selectedNode.getObject().getWayArrayList()) {
                Node[] adjacentNodes = w.getAdjacents(selectedNode.getObject());
                if (adjacentNodes == null) continue;

                for (Node n : adjacentNodes) {
    //                newLocationNode.setCost(calculateTravelCost(newLocationNode, e));
                    TreeNode<Node> treeNode = new TreeNode<>(n);

                    // Putting the speed cost in the TreeNode now rather than the graph Node
                    // @TODO fix this one later after the massive changes
                    treeNode.putMetaData("time", calculateTravelTime(w, selectedNode.getMetaData("time")));
                    selectedNode.addChild(treeNode);
                    frontiers.add(treeNode);
                }
            }
        }

        return null;
    }

    private double calculateTravelTime(Way way, double totalTime) {
        double speedLimit = way.getSpeedLimit();
        // If speed limit is not defined, assume running at 40km/h
        if (speedLimit == 0) speedLimit = 40;

        double time = way.getDistance() / speedLimit;

        return time;
    }

    // For now not being used, @TODO maybe look over here again later
    private double calculateTravelCost(Node node, Way way) {
        return way.getDistance() + node.getHeuristic();
    }

    private ArrayList<Node> deriveSolution(TreeNode<Node> endNode) {
        ArrayList<Node> result = new ArrayList<>();

        while (endNode != null)  {
            result.add(endNode.getObject());
            endNode = endNode.getParent();
        }
        Collections.reverse(result);
        return result;
    }

}

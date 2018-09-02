package org.swinburne.engine;

import org.swinburne.model.Edge;
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

            for (Edge e : selectedNode.getObject().getOutEdge()) {
                Node newLocationNode = e.getDestination();
//                newLocationNode.setCost(calculateTravelCost(newLocationNode, e));
                TreeNode<Node> treeNode = new TreeNode<>(newLocationNode);

                // Putting the speed cost in the TreeNode now rather than the graph Node
                treeNode.putMetaData("time", calculateTravelTime(e, selectedNode.getMetaData("time")));
                selectedNode.addChild(treeNode);
                frontiers.add(treeNode);
            }
        }

        return null;
    }

    private double calculateTravelTime(Edge edge, double totalTime) {
        double speedLimit = edge.getSpeedLimit();
        // If speed limit is not defined, assume running at 40km/h
        if (speedLimit == 0) speedLimit = 40;

        double time = edge.getDistance() / speedLimit;
    }

    // For now not being used, @TODO maybe look over here again later
    private double calculateTravelCost(Node node, Edge edge) {
        return edge.getDistance() + node.getHeuristic();
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

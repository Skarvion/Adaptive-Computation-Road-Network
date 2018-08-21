package org.swinburne.engine;

import org.swinburne.model.Edge;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class AStarSearch {
    public ArrayList<Node> computeDirection(Graph graph, Node start, Node destination) {
        HeuristicEngine.generateHeuristic(graph, destination);

        TreeNode<Node> rootNode = new TreeNode<>(null);
        Tree<Node> tree = new Tree<>(rootNode);
        PriorityQueue<Node> frontiers = new PriorityQueue<Node>(50, new HeuristicComparator());

        TreeNode<Node> selectedNode;
//        while ((selectedNode = frontiers.poll()) != null) {
//            if (selectedNode == destination) return deriveSolution(selectedNode);
//
//            for (Edge e : selectedNode.getDestination().getOutEdge()) {
//                TreeNode<Edge> nodes = new TreeNode<>(e);
//                selectedNode.addChild(nodes);
//                frontiers.add(nodes);
//            }
//        }

        return null;
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

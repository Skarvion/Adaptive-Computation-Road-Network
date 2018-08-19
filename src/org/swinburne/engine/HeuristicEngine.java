package org.swinburne.engine;

import org.swinburne.model.Edge;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;

public class HeuristicEngine {
    public static void generateHeuristic(Graph graph, Node destination) {
        Tree<Edge> edgeTree = new Tree<>(new TreeNode<>(new Edge()));
        LinkedList<TreeNode<Edge>> frontier = new LinkedList<>();
        for (Edge e : destination.getInEdge()) {
            TreeNode<Edge> temp = new TreeNode<>(e);
            edgeTree.getRoot().addChild(temp);
            frontier.add(temp);
        }

        TreeNode<Edge> selectedEdge = null;
        while((selectedEdge = frontier.poll()) != null) {
            if (selectedEdge.getObject() == null) continue;

            selectedEdge.getObject().setHeuristic(calculateHeuristic(selectedEdge));
            ArrayList<TreeNode<Edge>> foundEdge = new ArrayList<>();
            for (Edge e : selectedEdge.getObject().getSource().getInEdge()) {
                TreeNode<Edge> edgeTreeNode = new TreeNode<>(e);
                edgeTreeNode.setParent(selectedEdge);

                frontier.push(edgeTreeNode);
            }
        }
    }

    private static float calculateHeuristic(TreeNode<Edge> edgeNode) {
        if (edgeNode.getParent() == null) return edgeNode.getObject().getDistance();
        return edgeNode.getParent().getObject().getHeuristic() + edgeNode.getObject().getDistance();
    }
}

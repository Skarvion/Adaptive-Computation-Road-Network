package org.swinburne.engine;

import org.swinburne.model.Edge;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStarSearch {
    public ArrayList<Node> computeDirection(Graph graph, Node start, Node destination) {
        ArrayList<Node> result = new ArrayList<>();
        HeuristicEngine.generateHeuristic(graph, destination);

        PriorityQueue<TreeNode<Edge>> frontiers = new PriorityQueue<>(50);


        return null;
    }

}

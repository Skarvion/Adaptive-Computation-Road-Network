package org.swinburne;

import org.swinburne.engine.HeuristicEngine;
import org.swinburne.model.Edge;
import org.swinburne.model.Graph;
import org.swinburne.model.GraphParser;
import org.swinburne.model.Node;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();

        Node nodeA = new Node();
        nodeA.setId("A");
        nodeA.setLabel("Node A");

        Node nodeB = new Node();
        nodeB.setId("B");
        nodeB.setLabel("Node B");

        Node nodeC = new Node();
        nodeC.setId("C");
        nodeC.setLabel("Node C");

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);

        Edge edgeA = new Edge();
        edgeA.setSource(nodeA);
        edgeA.setDestination(nodeB);
        edgeA.setName("Edge A");
        edgeA.setDistance(100);

        Edge edgeB = new Edge();
        edgeB.setSource(nodeB);
        edgeB.setDestination(nodeC);
        edgeB.setName("Edge B");
        edgeB.setDistance(200);

        ArrayList<Edge> edgeList = new ArrayList<>();
        edgeList.add(edgeA);
        edgeList.add(edgeB);

        HeuristicEngine.generateHeuristic(graph, nodeC);
        for (Edge e : edgeList) {
            System.out.println("Heuristic of " + e.getName() + ": " + e.getHeuristic());
        }

        GraphParser.saveGraph(graph, "Test.txt");
    }
}

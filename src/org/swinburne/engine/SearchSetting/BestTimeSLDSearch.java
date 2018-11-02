package org.swinburne.engine.SearchSetting;

import org.swinburne.engine.FScoreComparator;
import org.swinburne.engine.HeuristicSetting.StraightLineDistanceHeuristic;
import org.swinburne.engine.HeuristicSetting.TimeHeuristic;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Way;
import org.swinburne.util.UnitConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class BestTimeSLDSearch extends SearchSetting {

    public BestTimeSLDSearch() {
        super("Best Time SLD Search", new String[]{"bestTimeSLD", "timeSLD", "speedSLD"});
    }

    @Override
    public void computeDirection(Graph graph, Node start, Node destination) {
        resetSearch();

        this.startNode = start;
        this.destinationNode = destination;

        sldStartToFinish = UnitConverter.geopositionDistance(start, destination);
        startTime = System.nanoTime();

        try {
            new StraightLineDistanceHeuristic().generateHeuristic(graph, start, destination);
            path = new ArrayList<>();
            PriorityQueue<Node> frontiers = new PriorityQueue<Node>(50, new FScoreComparator());

            Node rootNode = start;

            ArrayList<Node> visited = new ArrayList<>();

            rootNode.setGCost(0);
            rootNode.setFValue(rootNode.getHeuristic());

            frontiers.add(rootNode);

            int test = 0;
            Node selectedNode;

            while ((selectedNode = frontiers.poll()) != null) {
                if (selectedNode == destination) {
                    deriveSolution(selectedNode);
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
                        double totalGScore = selectedNode.getGCost() + timeTraversed;

                        boolean contained = true;
                        if (!frontiers.contains(n)) contained = false;
                        else if (totalGScore >= n.getGCost()) continue;

                        n.setParent(selectedNode);
                        selectedNode.addChild(n);

                        double distanceToGoal = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), destination.getLatitude(), destination.getLongitude());
                        double timeToGoal = distanceToGoal / UnitConverter.kmhToMs(50);

                        n.setGCost(totalGScore);
                        n.setFValue(n.getGCost() + n.getHeuristic());

                        if (!contained) {
                            frontiers.add(n);
                            frontierCount++;
                        }

                        drawFrontier(selectedNode, n);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ArrayList<Node> deriveSolution(Node destination) {
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
}

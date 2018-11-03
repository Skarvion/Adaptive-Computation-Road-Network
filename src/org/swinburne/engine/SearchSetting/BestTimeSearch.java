package org.swinburne.engine.SearchSetting;

import org.swinburne.engine.FScoreTreeComparator;
import org.swinburne.engine.HeuristicSetting.StraightLineDistanceTimeHeuristic;
import org.swinburne.engine.HeuristicSetting.TimeHeuristic;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.model.Way;
import org.swinburne.util.UnitConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class BestTimeSearch extends SearchSetting {

    public BestTimeSearch() {
        super("Best Time Search", new String[]{"besttime", "time", "speed"});
    }

    @Override
    public void computeDirection(Graph graph, Node start, Node destination) {
        resetSearch();

        this.startNode = start;
        this.destinationNode = destination;

        sldStartToFinish = UnitConverter.geopositionDistance(start, destination);
        startTime = System.nanoTime();

        try {
            new StraightLineDistanceTimeHeuristic().generateHeuristic(graph, start, destination);
            path = new ArrayList<>();
            PriorityQueue<TreeNode<Node>> frontiers = new PriorityQueue<>(50, new FScoreTreeComparator());

            TreeNode<Node> rootNode = new TreeNode<>(start);

            ArrayList<Node> visited = new ArrayList<>();

            rootNode.getObject().setGCost(0);
            rootNode.getObject().setFValue(rootNode.getHeuristic());

            frontiers.add(rootNode);

            TreeNode<Node> selectedTreeNode;
            while ((selectedTreeNode = frontiers.poll()) != null) {
                Node selectedNode = selectedTreeNode.getObject();

                if (selectedNode == destination) {
                    deriveSolution(selectedTreeNode);
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

                        double timeTraversed = UnitConverter.geopositionDistance(selectedNode.getLatitude(), selectedNode.getLongitude(), n.getLatitude(), n.getLongitude()) / UnitConverter.kmhToMs(w.getSpeedLimitKmh());
                        double totalGScore = selectedNode.getGCost() + timeTraversed;

                        if (selectedTreeNode.getWay() != null && selectedTreeNode.getWay() != w) totalGScore += SearchSetting.AVERAGE_TURNING_TIME;

                        boolean contained = true;
                        if (!frontiers.contains(n)) contained = false;
                        else if (totalGScore >= n.getGCost()) continue;

                        TreeNode<Node> newTreeNode = new TreeNode<>(n);

                        newTreeNode.setWay(w);
                        selectedTreeNode.addChild(newTreeNode);

                        double distanceToGoal = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), destination.getLatitude(), destination.getLongitude());
                        double timeToGoal = distanceToGoal / UnitConverter.kmhToMs(50);

                        n.setGCost(totalGScore);
                        n.setFValue(n.getGCost() + n.getHeuristic());

                        if (!contained) {
                            frontiers.add(newTreeNode);
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
    protected ArrayList<Node> deriveSolution(TreeNode<Node> destination) {
        long endTime = System.nanoTime();
        processTimeMS = (endTime - startTime) / 1000000;

        ArrayList<Node> result = new ArrayList<>();
        TreeNode<Node> selectedTreeNode = destination;
        timeTaken = destination.getObject().getGCost();
        totalDistance = 0;
        while (selectedTreeNode != null) {
            result.add(selectedTreeNode.getObject());
            if (selectedTreeNode.getParent() != null) {
                totalDistance += UnitConverter.geopositionDistance(selectedTreeNode.getObject(), selectedTreeNode.getParent().getObject());
            }
            selectedTreeNode = selectedTreeNode.getParent();
        }
        Collections.reverse(result);
        path = result;
        solutionFound = true;

        return result;
    }
}

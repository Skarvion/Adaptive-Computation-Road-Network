package org.swinburne.engine;

import org.swinburne.engine.HeuristicSetting.HeuristicSetting;
import org.swinburne.engine.HeuristicSetting.StraightLineDistance;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.util.ArrayList;

public class HeuristicEngine {

    private static ArrayList<HeuristicSetting> settingList = new ArrayList<>();

    private static HeuristicSetting selectedHeuristic;

    static {
        settingList.add(new StraightLineDistance());

        selectedHeuristic = settingList.get(0);
    }

    public static void generateHeuristic(Graph graph, Node destination) {
        for (Node n : graph.getNodeList()) {
            if (n == destination) continue;
            calculateHeuristic(graph, n, destination);
        }
    }

    private static double calculateHeuristic(Graph graph, Node node, Node destination) {
        return selectedHeuristic.calculateHeuristic(graph, node, destination);
    }
}

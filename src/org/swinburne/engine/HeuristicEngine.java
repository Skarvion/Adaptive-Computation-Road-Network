package org.swinburne.engine;

import org.swinburne.engine.HeuristicSetting.HeuristicSetting;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.util.HashSet;

public class HeuristicEngine {

    private static HashSet<HeuristicSetting> settingList = new HashSet<>();

    private static HeuristicSetting selectedHeuristic;

    public static boolean generateHeuristic(String heuristicName, Graph graph, Node start, Node destination) {
        for (HeuristicSetting he : settingList) {
            if (he.isID(heuristicName)) {
                selectedHeuristic = he;
                return generateHeuristic(graph, start, destination);
            }
        }
        return false;
    }

    public static boolean generateHeuristic(Graph graph, Node start, Node destination) {
        try {
            selectedHeuristic.generateHeuristic(graph, start, destination);
            return true;
        } catch (NullPointerException npe) {
            throw new IllegalStateException("Heuristic setting has not been selected yet! Cannot proceed");
        }
    }

    public static boolean addHeuristicSetting(HeuristicSetting heuristicSetting) { return settingList.add(heuristicSetting); }
}

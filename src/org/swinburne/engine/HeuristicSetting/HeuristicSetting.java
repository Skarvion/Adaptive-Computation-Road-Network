package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.util.ArrayList;

public abstract class HeuristicSetting {

    protected ArrayList<String> idList;

    public boolean isID(String id) {
        for (String s : idList) if (s.equalsIgnoreCase(id)) return true;
        return false;
    }

    // @TODO: perhaps don't set heuristic in this one function???
    public abstract double calculateHeuristic(Graph graph, Node node, Node destination);
}

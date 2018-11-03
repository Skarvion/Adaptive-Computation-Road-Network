package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.util.ArrayList;

public abstract class HeuristicSetting {

    public static final double AVERAGE_INTERSECTION_TIME = 20;

    protected ArrayList<String> idList = new ArrayList<>();

    protected long startTime;
    protected long endTime;

    public boolean isID(String id) {
        for (String s : idList) if (s.equalsIgnoreCase(id)) return true;
        return false;
    }

    public boolean generateHeuristic(Graph graph, Node start, Node destination) {
        startTime = System.nanoTime();
        boolean result = processHeuristic(graph, start, destination);
        endTime = System.nanoTime();

        return result;
    }

    protected abstract boolean processHeuristic(Graph graph, Node start, Node destination);

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDurationNS() { return endTime - startTime; }
}

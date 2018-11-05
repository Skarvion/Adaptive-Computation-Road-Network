package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.util.ArrayList;

public abstract class HeuristicSetting {
    public static final double AVERAGE_SPEED_LIMIT = 50;

    protected ArrayList<String> idList = new ArrayList<>();

    protected long startTime;
    protected long endTime;

    public boolean isID(String id) {
        for (String s : idList) if (s.equalsIgnoreCase(id)) return true;
        return false;
    }

    public boolean generateHeuristic(Graph graph, Node start, Node destination) {
        startTime = System.nanoTime();
        boolean result = processGraphHeuristic(graph, start, destination);
        endTime = System.nanoTime();

        return result;
    }

    public abstract double calculateHeuristic(Graph graph, Node selected, Node start, Node destination);

    protected abstract boolean processGraphHeuristic(Graph graph, Node start, Node destination);

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDurationNS() { return endTime - startTime; }
}

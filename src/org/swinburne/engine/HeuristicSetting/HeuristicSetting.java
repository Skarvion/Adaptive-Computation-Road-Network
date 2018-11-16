package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.util.ArrayList;

/**
 * Abstract class of heuristic setting that is to be used in {@link org.swinburne.engine.SearchSetting.SearchSetting}. It is created as separated class for easier organization purpose in future updates. Has list of ID and can keep track of time when generating heuristic for the entire graph.
 */
public abstract class HeuristicSetting {
    public static final double AVERAGE_SPEED_LIMIT = 50;

    protected ArrayList<String> idList = new ArrayList<>();

    protected long startTime;
    protected long endTime;

    /**
     * Check whether this setting has specified ID.
     * @param id check ID
     * @return true if this setting has the ID, else false
     */
    public boolean isID(String id) {
        for (String s : idList) if (s.equalsIgnoreCase(id)) return true;
        return false;
    }

    /**
     * Generate the heuristic value for the  entire graph. Keeps track of the time.
     * @param graph graph
     * @param start start node
     * @param destination destination node
     * @return true if the graph heuristic generation is successful
     */
    public boolean generateHeuristic(Graph graph, Node start, Node destination) {
        startTime = System.nanoTime();
        boolean result = processGraphHeuristic(graph, start, destination);
        endTime = System.nanoTime();

        return result;
    }

    /**
     * Abstract function to calculate individual node heuristic value.
     * @param graph graph
     * @param selected selected node to be calculated
     * @param start start node of search
     * @param destination destination node of search
     * @return calculated value
     */
    public abstract double calculateHeuristic(Graph graph, Node selected, Node start, Node destination);

    /**
     * Process the heuristic of entire graph. Abstract function.
     * @param graph graph
     * @param start start node of search
     * @param destination destination node of search
     * @return true if the graph heuristic generation is successful
     */
    protected abstract boolean processGraphHeuristic(Graph graph, Node start, Node destination);

    /**
     * Get start process time.
     * @return process time in long
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get end process time.
     * @return process time in long
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Get duration in millisecond.
     * @return duration
     */
    public long getDurationmS() { return (endTime - startTime) / 10000000; }
}

package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.util.UnitConverter;

/**
 * Implemented {@link HeuristicSetting} that generates heuristic value for {@link org.swinburne.engine.SearchSetting.BestDistanceSearch}. Using haversine formula to calculate and divided by default speed limit.
 */
public class StraightLineDistanceTimeHeuristic extends HeuristicSetting {

    /**
     * Default constructor.
     */
    public StraightLineDistanceTimeHeuristic() {
        idList.add("sld");
        idList.add("Straight Line Distance");
        idList.add("StraightLineDistanceHeuristic");
    }

    /**
     * Override method to generate heuristic value for the entire graph based on the straight line distance over default speed limit from the selected node to the destination node.
     * @param graph graph
     * @param start start node of search
     * @param destination destination node of search
     * @return true
     */
    @Override
    protected boolean processGraphHeuristic(Graph graph, Node start, Node destination) {
        for (Node n : graph.getNodeMap().values()) {
            if (n == destination) {
                n.setHeuristic(0);
                continue;
            }

            double d = calculateHeuristic(graph, n, start, destination);

            n.setHeuristic(d);
        }

        return true;
    }

    /**
     * Calculate the heuristic value based on time to travel straight line distance from selected node to the destination node using haversine formula and divided by the default speed limit.
     * @param graph graph
     * @param selected selected node to be calculated
     * @param start start node of search
     * @param destination destination node of search
     * @return straight line distance over time in meter per second
     */
    public double calculateHeuristic(Graph graph, Node selected, Node start, Node destination) {
        return UnitConverter.geopositionDistance(selected.getLatitude(), selected.getLongitude(), destination.getLatitude(), destination.getLongitude()) / UnitConverter.kmhToMs(AVERAGE_SPEED_LIMIT);
    }
}

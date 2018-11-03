package org.swinburne.engine.HeuristicSetting;

import org.swinburne.engine.HeuristicEngine;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.NodeType;
import org.swinburne.util.UnitConverter;

public class StraightLineDistanceTimeHeuristic extends HeuristicSetting {
    public StraightLineDistanceTimeHeuristic() {
        idList.add("sld");
        idList.add("Straight Line Distance");
        idList.add("StraightLineDistanceHeuristic");
    }

    static {
        HeuristicEngine.addHeuristicSetting(new StraightLineDistanceTimeHeuristic());
    }

    //    Reference: http://www.movable-type.co.uk/scripts/latlong.html
    //    Reference: https://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
    //    Using haversine calculation
    @Override
    protected boolean processHeuristic(Graph graph, Node start, Node destination) {
        for (Node n : graph.getNodeMap().values()) {
            if (n == destination) {
                n.setHeuristic(0);
                continue;
            }

            double d = UnitConverter.geopositionDistance(n.getLatitude(), n.getLongitude(), destination.getLatitude(), destination.getLongitude());
            d /= UnitConverter.kmhToMs(50);

            if (n.getType() == NodeType.Intersection) d += HeuristicSetting.AVERAGE_INTERSECTION_TIME;

            n.setHeuristic(d);
        }

        return true;
    }
}

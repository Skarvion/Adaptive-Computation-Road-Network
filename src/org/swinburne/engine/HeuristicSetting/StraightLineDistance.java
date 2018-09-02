package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;

public class StraightLineDistance extends HeuristicSetting {

    private final float EARTH_RADIUS_METRE = 6371000;

    public StraightLineDistance() {
        idList.add("sld");
        idList.add("Straight Line Distance");
        idList.add("StraightLineDistance");
    }

//    Reference: http://www.movable-type.co.uk/scripts/latlong.html
//    Reference: https://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
//    Using haversine calculation

    //@TODO: fix the heuristic here, it's so great that distance cost does not affect this in any significant way
    @Override
    public double calculateHeuristic(Graph graph, Node node, Node destination) {
        double dLat = toRadian(destination.getLatitude() - node.getLatitude());
        double dLon = toRadian(destination.getLongitude() - node.getLongitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadian(node.getLatitude())) * Math.cos(toRadian(destination.getLatitude())) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RADIUS_METRE * c;

        node.setHeuristic(d);

        return d;
    }

    private double toRadian(double value) {
        return value * Math.PI / 180;
    }
}

package org.swinburne.engine.HeuristicSetting;

import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.Tree;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.util.UnitConverter;

public class StraightLineDistance extends HeuristicSetting {



    public StraightLineDistance() {
        idList.add("sld");
        idList.add("Straight Line Distance");
        idList.add("StraightLineDistance");
    }

//    Reference: http://www.movable-type.co.uk/scripts/latlong.html
//    Reference: https://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
//    Using haversine calculation
    @Override
    public double calculateHeuristic(Graph graph, Node node, Node destination) {
        double d = UnitConverter.geopositionDistance(node.getLatitude(), node.getLongitude(), destination.getLatitude(), destination.getLongitude());
        node.setHeuristic(d);

        return d;
    }
}

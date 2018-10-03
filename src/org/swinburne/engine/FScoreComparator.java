package org.swinburne.engine;

import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;

import java.util.Comparator;

public class FScoreComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        double h1 = o1.getFValue();
        double h2 = o2.getFValue();

        if (h1 > h2) return 1;
        else if (h1 == h2) return 0;
        else return -1;
    }
}

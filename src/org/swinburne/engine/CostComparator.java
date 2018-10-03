package org.swinburne.engine;

import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;

import java.util.Comparator;

public class CostComparator implements Comparator<TreeNode<Node>> {

    @Override
    public int compare(TreeNode<Node> o1, TreeNode<Node> o2) {
        double h1 = o1.getObject().getGCost();
        double h2 = o2.getObject().getGCost();

        if (h1 > h2) return -1;
        else return 1;
    }
}

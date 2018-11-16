package org.swinburne.engine;

import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;

import java.util.Comparator;

/**
 * F-Score comparator used for priority queueing. Used to compare {@link TreeNode} object
 */
public class FScoreTreeComparator implements Comparator<TreeNode<Node>> {

    /**
     * Compare two {@link TreeNode} F-Score.
     * @param o1 first tree node
     * @param o2 second tree node
     * @return return 1 if first tree node is larger, 0 if equal and -1 if second tree node is larger
     */
    @Override
    public int compare(TreeNode<Node> o1, TreeNode<Node> o2) {
        double h1 = o1.getObject().getFValue();
        double h2 = o2.getObject().getFValue();

        if (h1 > h2) return 1;
        else if (h1 == h2) return -1;
        else return -1;
    }
}

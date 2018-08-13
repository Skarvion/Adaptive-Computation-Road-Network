package org.swinburne.model;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodeList = new ArrayList<>();

    public boolean addNode(Node node) {
        if (node.getId() != null) {
            for (Node n : nodeList) {
                if (n.getId().equalsIgnoreCase(node.getId()))
                    return false;
            }
        }
//        else {
//            node.setId();
//        }
        nodeList.add(node);

        return true;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }
}

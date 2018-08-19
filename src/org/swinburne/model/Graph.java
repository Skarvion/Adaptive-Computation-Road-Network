package org.swinburne.model;

import org.swinburne.util.RandomStringGenerator;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodeList = new ArrayList<>();

    public boolean addNode(Node node) {
        if (node.getId() != null) {
            if (findNodeByID(node.getId()) != null) {
                return false;
            }
        } else {
            boolean found = false;
            String generatedKey;
            do {
                found = false;
                generatedKey = RandomStringGenerator.generateRandomString(20);
                if (findNodeByID(generatedKey) != null) found = true;
            } while (found);

            node.setId(generatedKey);
        }
        nodeList.add(node);

        return true;
    }

    public Node findNodeByID(String id) {
        for (Node n : nodeList) {
            if (n.getId().equals(id)) return n;
        }
        return null;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }
}

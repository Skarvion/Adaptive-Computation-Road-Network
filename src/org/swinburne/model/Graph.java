package org.swinburne.model;

import org.swinburne.util.RandomStringGenerator;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodeList = new ArrayList<>();

    private ArrayList<Way> wayList = new ArrayList<>();

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

    public Way findWayById(String id) {
        for (Way w : wayList) {
            if (w.getId().equals(id)) return w;
        }
        return null;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public ArrayList<Way> getWayList() {
        return wayList;
    }

    public boolean addWay(Way way) {
        if (way.getId() != null) {
            if (findNodeByID(way.getId()) != null) {
                return false;
            }
        } else {
            boolean found = false;
            String generatedKey;
            do {
                found = false;
                generatedKey = RandomStringGenerator.generateRandomString(20);
                if (findWayById(generatedKey) != null) found = true;
            } while (found);

            way.setId(generatedKey);
        }
        wayList.add(way);

        return true;
    }
}

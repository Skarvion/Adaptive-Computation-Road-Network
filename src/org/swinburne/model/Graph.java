package org.swinburne.model;

import org.swinburne.util.RandomStringGenerator;

import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable {
    private Map<String, Node> nodeMap = new HashMap<>();
    private Map<String, Way> wayMap = new HashMap<>();

    public boolean addNode(Node node) {
        if (node.getId() != null) {
            if (getNode(node.getId()) != null) {
                return false;
            }
        } else {
            boolean found = false;
            String generatedKey;
            do {
                found = false;
                generatedKey = RandomStringGenerator.generateRandomString(20);
                if (getNode(generatedKey) != null) found = true;
            } while (found);

            node.setId(generatedKey);
        }
        nodeMap.put(node.getId(), node);

        return true;
    }

    public Node getNode(String id) {
        return nodeMap.get(id);
    }

    public Way getWay(String id) {
        return wayMap.get(id);
    }

    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    public Map<String, Way> getWayMap() {
        return wayMap;
    }

    public void reset() {
        for (Node n : nodeMap.values()) {
            n.setFValue(0);
            n.setGCost(0);
            n.getChildren().clear();
            n.setParent(null);
            n.setTimeTravelled(0);
        }
    }

    public Node removeNode(Node node) {
        for (Way w : node.getWayArrayList()) {
            w.removeNode(node);
        }
        return nodeMap.remove(node.getId());
    }

    public boolean addWay(Way way) {
        if (way.getId() != null) {
            if (getNode(way.getId()) != null) {
                return false;
            }
        } else {
            boolean found = false;
            String generatedKey;
            do {
                found = false;
                generatedKey = RandomStringGenerator.generateRandomString(20);
                if (getWay(generatedKey) != null) found = true;
            } while (found);

            way.setId(generatedKey);
        }
        wayMap.put(way.getId(), way);

        return true;
    }
}
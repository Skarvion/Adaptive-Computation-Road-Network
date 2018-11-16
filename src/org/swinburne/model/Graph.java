package org.swinburne.model;

import org.swinburne.util.RandomStringGenerator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The container class for the entirety of a map. Contains list of interconnected {@link Node} and {@link Way}. The graph also ensures that each one of them will have unique ID. It utilizes {@link Map} to improve search performance based on each individual ID.
 */
public class Graph implements Serializable {
    private Map<String, Node> nodeMap = new HashMap<>();
    private Map<String, Way> wayMap = new HashMap<>();

    /**
     * Add new {@link Node} to this graph. If the new node ID is already discovered in the map, then it is rejected and return false. If the node does not have any ID yet, then it will be assigned a new random one.
     * @param node new node
     * @return true if node successfully added and assigned new ID if needed, otherwise return false when the node ID is already present in the graph.
     */
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

    /**
     * Get {@link Node} based on ID.
     * @param id node ID
     * @return found {@link Node}
     */
    public Node getNode(String id) {
        return nodeMap.get(id);
    }

    /**
     * Get {@link Way} based on ID.
     * @param id way ID
     * @return found {@link Way}
     */
    public Way getWay(String id) {
        return wayMap.get(id);
    }

    /**
     * Get the entirety {@link Map} of {@link Node}.
     * @return map of node and its ID
     */
    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    /**
     * Get the entirety {@link Map} of {@link Way}.
     * @return map of way and its ID
     */
    public Map<String, Way> getWayMap() {
        return wayMap;
    }

    /**
     * Resest the state of every {@link Node} in the graph.
     */
    public void reset() {
        for (Node n : nodeMap.values()) {
            n.setFValue(Double.MAX_VALUE);
            n.setGCost(Double.MAX_VALUE);
            n.setHeuristic(Double.MAX_VALUE);
        }
    }

    /**
     * Remove {@link Node} in the graph and all of {@link Way} references to it.
     * @param node node to be removed
     * @return successful removal from the map
     */
    public Node removeNode(Node node) {
        for (Way w : node.getWayArrayList()) {
            w.removeNode(node);
        }
        return nodeMap.remove(node.getId());
    }

    /**
     * Add new {@link Way} to this graph. If the new way ID is already discovered in the map, then it is rejected and return false. If the way does not have any ID yet, then it will be assigned a new random one.
     * @param way new way
     * @return true if way successfully added and assigned new ID if needed, otherwise return false when the way ID is already present in the graph.
     */
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
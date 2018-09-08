package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Way {
    // Road name
    private String label;
    private String id;

    // Distance measured in km
    private float distance;
    // Speed measured in km/h
    private float speedLimit;
    // Traffic coefficient limit 0.01 to 1
    private float traffic;
    private boolean twoWay;

    private ArrayList<Node> nodeOrderedList = new ArrayList<>();
    private Map<String, Double> metaData = new HashMap<>();

    public Way() {
        this.distance = 0;
        this.speedLimit = 0;
        this.traffic = 0;
    }

    public Way(float distance, float speedLimit, float traffic) {
        this.distance = distance;
        this.speedLimit = speedLimit;
        this.traffic = traffic;
    }

    public Way(Way way) {
        this.label = way.label;
        this.distance = way.distance;
        this.speedLimit = way.speedLimit;
        this.traffic = way.traffic;
    }

    public ArrayList<Node> getNodeOrderedList() {
        return nodeOrderedList;
    }

    public int addNode(Node node) {
        if (nodeOrderedList.add(node)) {
            node.addWay(this);
            return nodeOrderedList.size();
        }
        return -1;
    }

    /**
     * Return the adjacent nodes in this way. Return an array of possible maximum two adjacent node in this ordered list.
     * If there are two possibilities, first element will always be the one before, and the second one is the one after
     * Return an empty array if no adjacent (e.g. only one node in the entire way) or size 1 if node is in corner.
     * Return null if no such node is found
     * @param node node to be checked
     * @return an array of adjacent nodes
     */
    public Node[] getAdjacents(Node node) {
        int pos = nodeOrderedList.indexOf(node);
        if (pos == -1) return null;

        if (nodeOrderedList.size() == 1) return new Node[0];
        else {
            // @TODO: i know i can make this part shorter, but that's future me problem
            if (pos == 0) {
                Node[] result = new Node[1];
                result[0] = nodeOrderedList.get(1);
                return result;
            } else if (pos == (nodeOrderedList.size() - 1)) {
                Node[] result = new Node[1];
                result[0] = nodeOrderedList.get(nodeOrderedList.size() - 2);
                return  result;
            } else {
                Node[] result = new Node[2];
                result[0] = nodeOrderedList.get(pos - 1);
                result[1] = nodeOrderedList.get(pos + 1);
                return result;
            }
        }
    }

    public Map<String, Double> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Double> metaData) {
        this.metaData = metaData;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(float speedLimit) {
        this.speedLimit = speedLimit;
    }

    public float getTraffic() {
        return traffic;
    }

    public void setTraffic(float traffic) {
        this.traffic = traffic;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }
}

package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private String id;
    private String label;
    private double latitude;
    private double longitude;
    private NodeType type = NodeType.Road;

    private double fValue;
    private double gCost;

    public Node() {

    }

    public Node(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private ArrayList<Way> wayArrayList = new ArrayList<>();

    private Map<String, Float> metadata = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean addWay(Way way) {
        return wayArrayList.add(way);
    }

    public ArrayList<Way> getWayArrayList() { return wayArrayList; }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getFValue() {
        return fValue;
    }

    public void setFValue(double fValue) {
        this.fValue = fValue;
    }

    public double getGCost() {
        return gCost;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }
}
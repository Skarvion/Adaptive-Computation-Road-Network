package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private String id;
    private String label;
    private double latitude;
    private double longtitude;
    private double heuristic;
    private NodeType type = NodeType.Road;

    //@TODO: decide whether leave it here or separate it to another class
    private double cost;

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
        if (wayArrayList.size() > 0) {
            System.out.println("Node ID: " + id);
            System.out.println("Multiple way: " + (wayArrayList.size() + 1));
        }
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
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }
}
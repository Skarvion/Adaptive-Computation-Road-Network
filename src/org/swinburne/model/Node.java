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

    //@TODO: decide whether leave it here or separate it to another class
    private double cost;

    // Array of outgoing connector
    private ArrayList<Edge> outEdge = new ArrayList<>();
    // Array of incoming connector
    private ArrayList<Edge> inEdge = new ArrayList<>();

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

    public boolean addInEdge(Edge edge) {
        inEdge.add(edge);
        return true;
    }

    public boolean addOutEdge(Edge edge) {
        outEdge.add(edge);
        return true;
    }

    public ArrayList<Edge> getOutEdge() {
        return outEdge;
    }

    public ArrayList<Edge> getInEdge() {
        return inEdge;
    }

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
}
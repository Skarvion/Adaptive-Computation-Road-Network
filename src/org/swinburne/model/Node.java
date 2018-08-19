package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private String id;
    private String label;

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
}

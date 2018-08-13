package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private String id;
    private String label;

    // Array of outgoing connector
    private ArrayList<Connector> outConnector = new ArrayList<>();
    // Array of incoming connector
    private ArrayList<Connector> inConnector = new ArrayList<>();

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

    public ArrayList<Connector> getOutConnector() {
        return outConnector;
    }

    public ArrayList<Connector> getInConnector() {
        return inConnector;
    }
}

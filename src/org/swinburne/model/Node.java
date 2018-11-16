package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a coordinate of an object and it is the smallest component in a map . This holds information such as latitude, longitude, ID, map of metadata and values used to navigate through such as heuristic, F-score and G-score, useful for A Star Search algorithms. It also keeps track the ways it is referenced to.
 */
public class Node {
    private String id;
    private String label;
    private double latitude;
    private double longitude;
    private NodeType type = NodeType.Road;

    private double heuristic;
    private double fValue;
    private double gCost;

    private ArrayList<Way> wayArrayList = new ArrayList<>();
    private Map<String, String> metadata = new HashMap<>();

    /**
     * Empty constructor.
     */
    public Node() {

    }

    /**
     * Constructor to set the initial coordinate.
     * @param latitude latitude
     * @param longitude longitude
     */
    public Node(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Get the {@link String }ID.
     * @return ID.
     */
    public String getId() {
        return id;
    }

    /***
     * Set the {@link String} ID.
     * @param id ID.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the {@link String} label.
     * @return label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the {@link String} label.
     * @param label label string
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Add {@link Way }to the list of connected ways.
     * @param way new connected way
     * @return successfully added to the list
     */
    public boolean addWay(Way way) {
        return wayArrayList.add(way);
    }

    /**
     * Get the list of connected {@link Way}.
     * @return list of ways
     */
    public ArrayList<Way> getWayArrayList() { return wayArrayList; }

    /**
     * Get latitude.
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set latitude.
     * @param latitude latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get longitude.
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set longitude.
     * @param longitude longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get heuristic value in double type.
     * @return heuristic
     */
    public double getHeuristic() {
        return heuristic;
    }

    /**
     * Set heuristic value in double type.
     * @param heuristic new heuristic value
     */
    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Get F-Value (for A Star Search).
     * @return F-Value
     */
    public double getFValue() {
        return fValue;
    }

    /**
     * Set F-Value (for A Star Search).
     * @param fValue F-Value
     */
    public void setFValue(double fValue) { this.fValue = fValue; }

    /**
     * Get G-Cost (for A Star Search).
     * @return G-Cost
     */
    public double getGCost() {
        return gCost;
    }

    /**
     * Set G-Cost (for A Star Search).
     * @param gCost G-Cost
     */
    public void setGCost(double gCost) {
        this.gCost = gCost;
    }

    /**
     * Get the {@link WayType} of this node.
     * @return type
     */
    public NodeType getType() {
        return type;
    }

    /**
     * SEt the {@link WayType} of this node.
     * @param type type
     */
    public void setType(NodeType type) {
        this.type = type;
    }

    /**
     * Put new meta data entry.
     * @param key string key
     * @param value string value
     * @return successfully put entry
     */
    public String putMetadata(String key, String value) { return this.metadata.put(key, value); }

    /**
     * Put new meta data entry. Will convert double value to {@link String}.
     * @param key string key
     * @param value double value
     * @return successfully put entry
     */
    public String putMetadata(String key, double value) { return this.metadata.put(key, Double.toString(value)); }
}
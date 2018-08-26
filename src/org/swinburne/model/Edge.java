package org.swinburne.model;

public class Edge {
    // Road name
    private String label;
    private String id;

    // Distance measured in km
    private float distance;

    // Speed measured in km/h
    private float speedLimit;

    // Traffic coefficient limit 0.01 to 1
    private float traffic;

    // Node destination
    private Node source;
    private Node destination;

    public Edge() {

    }

    public Edge(float distance, float speedLimit, float traffic, Node source, Node destination) {
        this.distance = distance;
        this.speedLimit = speedLimit;
        this.traffic = traffic;
        this.source = source;
        this.destination = destination;
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

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
        source.addOutEdge(this);
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
        destination.addInEdge(this);
    }
}

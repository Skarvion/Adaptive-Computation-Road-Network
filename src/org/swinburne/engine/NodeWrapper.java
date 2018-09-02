package org.swinburne.engine;

import org.swinburne.model.Node;

public class NodeWrapper {
    private Node node;
    private double timeTaken;
    private double cost;

    public NodeWrapper(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(double timeTaken) {
        this.timeTaken = timeTaken;
    }

}

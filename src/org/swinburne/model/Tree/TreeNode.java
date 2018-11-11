package org.swinburne.model.Tree;

import org.swinburne.model.Way;

import java.util.ArrayList;
import java.util.HashMap;

// Consider again the efficiency of making another class for heuristic calculation
public class TreeNode<T> {
    private T object;
    private TreeNode<T> parent;
    private HashMap<String, Double> metaData = new HashMap<>();
    private ArrayList<TreeNode<T>> children = new ArrayList<>();

    //@TODO: fix this one later
    private Way way;

    private double cost; // in term of distance (for now)
    private double heuristic;
    private double time; // in second
    private double distance; // total distance traversed

    // @TODO: remember to remove fScore and gScore from model Node
    private double fScore = Double.MAX_VALUE;
    private double gScore = Double.MAX_VALUE;

    public TreeNode(T object) {
        this.object = object;
    }

    public boolean isNodeExistInParent(TreeNode<T> node) {
        if (parent != null) {
            return parent.isNodeExistInParent(node);
        } else {
            return this == node;
        }
    }

    public boolean isNodeRecurred() {
        return isNodeExistInParent(this);
    }

    public void addChild(TreeNode<T> child) {
        child.setParent(this);
        children.add(child);
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public ArrayList<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TreeNode<T>> children) {
        this.children = children;
    }

//    public void putMetaData(String name, double value) { metaData.put(name, value); }
//
//    public double getMetaData(String name) { return metaData.get(name); }

    public HashMap<String, Double> getMetaData() { return metaData; }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getfScore() {
        return fScore;
    }

    public void setfScore(double fScore) {
        this.fScore = fScore;
    }

    public double getgScore() {
        return gScore;
    }

    public void setgScore(double gScore) {
        this.gScore = gScore;
    }

    public Way getWay() {
        return way;
    }

    public void setWay(Way way) {
        this.way = way;
    }
}

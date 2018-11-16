package org.swinburne.model.Tree;

import org.swinburne.model.Way;
import org.swinburne.model.Node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A temporary tree node used to expand search space when performing search function. Contains reference to a linked object, way, metadata and values used for A Star Search such as F-score and G-cost. Acts like a Tree data structure with child list and parent object.
 * @param <T> used for {@link Node} operation only for now.
 */
public class TreeNode<T> {
    private T object;
    private TreeNode<T> parent;
    private HashMap<String, Double> metaData = new HashMap<>();
    private ArrayList<TreeNode<T>> children = new ArrayList<>();

    private Way way;

    private double time; // in second
    private double distance; // total distance traversed in meter

    private double heuristic;
    private double fScore = Double.MAX_VALUE;
    private double gScore = Double.MAX_VALUE;

    /**
     * Constructor and set the attached object.
     * @param object attached object
     */
    public TreeNode(T object) {
        this.object = object;
    }

    /**
     * Check if a tree node exist recursively as parent.
     * @param node check tree node recurs or not in the parent
     * @return true if a node recurs in the parent, else return false
     */
    public boolean isNodeExistInParent(TreeNode<T> node) {
        if (this != node && parent != null) {
            return parent.isNodeExistInParent(node);
        } else {
            return this == node;
        }
    }

    /**
     * Check if this node recurs itself in the parent.
     * @return true if this node recurs in the parent, else return false
     */
    public boolean isNodeExistInParent() {
        return isNodeExistInParent(this);
    }

    /**
     * Add new child tree node.
     * @param child
     */
    public void addChild(TreeNode<T> child) {
        child.setParent(this);
        children.add(child);
    }

    /**
     * Get the attached object.
     * @return attached object
     */
    public T getObject() {
        return object;
    }

    /**
     * Set the new attached object.
     * @param object new attached object
     */
    public void setObject(T object) {
        this.object = object;
    }

    /**
     * Get a tree node parent.
     * @return parent
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * Set a tree node parent
     * @param parent parent
     */
    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    /**
     * Get a list of children tree node.
     * @return list of children tree node
     */
    public ArrayList<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * Set a list of children tree node.
     * @param children list of children tree node
     */
    public void setChildren(ArrayList<TreeNode<T>> children) {
        this.children = children;
    }

    /**
     * Get the {@link HashMap} of metadata.
     * @return metadata map.
     */
    public HashMap<String, Double> getMetaData() { return metaData; }

    /**
     * Set the heuristic value.
     * @return heuristic
     */
    public double getHeuristic() {
        return heuristic;
    }

    /**
     * Set the heuristic value.
     * @param heuristic heuristic
     */
    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Get travel time by this node.
     * @return time in second.
     */
    public double getTime() {
        return time;
    }

    /**
     * Set travel time by this node.
     * @param time time in second.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Get the distance travelled by this node.
     * @return distance in meter
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Set the distance travelled by this node.
     * @param distance distance in meter
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Get the F-Score of this tree node.
     * @return F-Score
     */
    public double getfScore() {
        return fScore;
    }

    /**
     * Set the F-Score of this tree node.
     * @param fScore F-Score
     */
    public void setfScore(double fScore) {
        this.fScore = fScore;
    }

    /**
     * Get the G-Cost of this tree node.
     * @return G-Cost
     */
    public double getgScore() {
        return gScore;
    }

    /**
     * Set the G-Cost of this tree node.
     * @param gScore G-Cost
     */
    public void setgScore(double gScore) {
        this.gScore = gScore;
    }

    /**
     * Get attached {@link Way}.
     * @return attached way
     */
    public Way getWay() {
        return way;
    }

    /**
     * Set attached {@link Way}.
     * @param way attached way
     */
    public void setWay(Way way) {
        this.way = way;
    }
}

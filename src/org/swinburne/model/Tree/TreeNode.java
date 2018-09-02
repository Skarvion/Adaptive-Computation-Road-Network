package org.swinburne.model.Tree;

import java.util.ArrayList;
import java.util.HashMap;

// Consider again the efficiency of making another class for heuristic calculation
public class TreeNode<T> {
    private T object;
    private TreeNode<T> parent;
    private HashMap<String, Double> metaData = new HashMap<>();
    private ArrayList<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T object) {
        this.object = object;
    }

    public TreeNode(T object, TreeNode<T> parent) {
        this.object = object;
        this.parent = parent;
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

    public void putMetaData(String name, double value) { metaData.put(name, value); }

    public double getMetaData(String name) { return metaData.get(name); }
}

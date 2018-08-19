package org.swinburne.model.Tree;

public class Tree<T> {
    private TreeNode<T> root;

    public Tree(TreeNode<T> root) {
        this.root = root;
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public void setRoot(TreeNode<T> root) {
        this.root = root;
    }
}


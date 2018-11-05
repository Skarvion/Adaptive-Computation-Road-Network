//package org.swinburne.engine.HeuristicSetting;
//
//import org.swinburne.engine.HeuristicEngine;
//import org.swinburne.model.Graph;
//import org.swinburne.model.Node;
//import org.swinburne.model.Tree.TreeNode;
//import org.swinburne.model.Way;
//import org.swinburne.util.UnitConverter;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//
//public class PathDistanceHeuristic extends HeuristicSetting {
//    public PathDistanceHeuristic() {
//        idList.add("distance");
//        idList.add("Path Distance Heuristic");
//        idList.add("pathDistance");
//        idList.add("path");
//    }
//
//    static {
//        HeuristicEngine.addHeuristicSetting(new TimeHeuristic());
//    }
//
//
//    //@TODO: due to time constraint, let's bruteforce this one, then we shall see what's up
//    @Override
//    protected boolean processGraphHeuristic(Graph graph, Node start, Node destination) {
//        LinkedList<TreeNode<Node>> frontier = new LinkedList<>();
//        ArrayList<Node> visited = new ArrayList<>();
//
//        TreeNode selectedTreeNode;
//        frontier.add(new TreeNode<>(destination));
//
//        while ((selectedTreeNode = frontier.pollFirst()) != null) {
//            Node selectedNode = (Node) selectedTreeNode.getObject();
//            visited.add(selectedNode);
//
//            for (Way w :selectedNode.getWayArrayList()) {
//                for (Node n : w.getAdjacents(selectedNode)) {
////                    if (visited.contains(n)) continue;
//
//                    double distance = UnitConverter.geopositionDistance(selectedNode, n);
//                    distance += selectedNode.getHeuristic();
//
//                    if (distance < n.getHeuristic()) {
//                        n.setHeuristic(distance + + n.getParent().getHeuristic());
//
//                        TreeNode<Node> newTreeNode = new TreeNode<>(n);
//                        newTreeNode.setParent(selectedTreeNode);
//                        selectedTreeNode.addChild(newTreeNode);
//
//                        frontier.add(newTreeNode);
//                    }
//                }
//            }
//        }
//
//        return true;
//    }
//}

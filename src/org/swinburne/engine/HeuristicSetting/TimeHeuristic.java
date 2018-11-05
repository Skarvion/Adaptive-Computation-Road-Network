//package org.swinburne.engine.HeuristicSetting;
//
//import org.swinburne.engine.HeuristicEngine;
//import org.swinburne.model.Graph;
//import org.swinburne.model.Node;
//import org.swinburne.model.NodeType;
//import org.swinburne.model.Tree.TreeNode;
//import org.swinburne.model.Way;
//import org.swinburne.util.UnitConverter;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//
//public class TimeHeuristic extends HeuristicSetting {
//    private final double AVERAGE_INTERSECTION_WAITING_TIME = 30;
//
//    public TimeHeuristic() {
//        idList.add("time");
//        idList.add("Time Heuristic");
//        idList.add("time");
//        idList.add("best time");
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
//        destination.setHeuristic(0);
//
//        TreeNode selectedTreeNode;
//        frontier.add(new TreeNode<>(destination));
//
//        while ((selectedTreeNode = frontier.pollFirst()) != null) {
//            Node selectedNode = (Node) selectedTreeNode.getObject();
//            if (visited.contains(selectedNode)) continue;
//            else visited.add(selectedNode);
//
//            if (selectedTreeNode.getParent() != null) {
//                Node parentNode = (Node) selectedTreeNode.getParent().getObject();
//
//                double distance = UnitConverter.geopositionDistance(selectedNode, parentNode);
//                double travelTime = distance / UnitConverter.kmhToMs(selectedTreeNode.getWay().getSpeedLimitKmh());
//                travelTime += parentNode.getHeuristic();
//
//                // If path is passing through a traffic signal, it would add the average waiting time at intersection
//                if (selectedNode.getType() == NodeType.Intersection) travelTime += AVERAGE_INTERSECTION_WAITING_TIME;
//
//                selectedNode.setHeuristic(travelTime);
//            }
//
//            for (Way w :selectedNode.getWayArrayList()) {
//                for (Node n : w.getAdjacents(selectedNode)) {
//                    if (visited.contains(n)) continue;
//
////                    if (n.getId().equalsIgnoreCase("518303955"))
////                    {
////                        System.out.println("Passed!");
////                    }
//                    TreeNode<Node> newTreeNode = new TreeNode<>(n);
//                    newTreeNode.setWay(w);
//                    newTreeNode.setParent(selectedTreeNode);
//                    selectedTreeNode.addChild(newTreeNode);
//
//                    frontier.add(newTreeNode);
//                }
//            }
//        }
//
//        return true;
//    }
//}

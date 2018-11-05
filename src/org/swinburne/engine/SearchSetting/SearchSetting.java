package org.swinburne.engine.SearchSetting;

import org.swinburne.engine.HeuristicSetting.HeuristicSetting;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.view.controller.MapController;

import java.util.ArrayList;

public abstract class SearchSetting {
    public static final double AVERAGE_TURNING_TIME = 5;
    public static final double AVERAGE_INTERSECTION_TIME = 20;

    private static ArrayList<SearchSetting> searchSettingList = new ArrayList<>();

    static {
        searchSettingList.add(new BestTimeSearch());
        searchSettingList.add(new BestDistanceSearch());
//        searchSettingList.add(new BestTimeSLDSearch());
//        searchSettingList.add(new BestDistanceSLDSearch());
    }

    protected ArrayList<String> ids = new ArrayList<>();
    protected String name;

    protected HeuristicSetting heuristic;

    protected ArrayList<Node> path = new ArrayList<>();
    protected boolean solutionFound = false;

    protected Node startNode;
    protected Node destinationNode;

    protected double totalDistance = 0;
    protected double timeTaken = 0;
    protected int frontierCount = 0;
    protected int visitedCount = 0;
    protected long processTimeMS = 0;
    protected double sldStartToFinish = 0;

    protected long startTime = 0;
    protected long endTime = 0;

    protected int trafficSignalPassed = 0;

    protected MapController.SearchTask mapTask = null;
    protected MapController mapController = null;

    public SearchSetting(String name, String[] ids) {
        this.name = name;
        for (String s : ids) this.ids.add(s);
    }

    public boolean isID(String id) {
        for (String s : ids)
            if (s.equalsIgnoreCase(id)) return true;

        return false;
    }

    protected void resetSearch() {
        this.solutionFound = false;
        this.path = new ArrayList<>();

        this.startNode = null;
        this.destinationNode = null;

        this.totalDistance = 0;
        this.timeTaken = 0;
        this.frontierCount = 0;
        this.visitedCount = 0;
        this.processTimeMS = 0;
        this.sldStartToFinish = 0;
        this.trafficSignalPassed = 0;

        this.startTime = 0;
        this.endTime = 0;

    }

    public abstract void computeDirection(Graph graph, Node start, Node destination);
    protected abstract ArrayList<Node> deriveSolution(TreeNode<Node> destination);

    public static ArrayList<SearchSetting> getSearchSettingList() { return searchSettingList; }

    protected boolean drawFrontier(Node firstNode, Node secondNode) {
        if (mapTask != null) {
            mapTask.drawFrontier(firstNode, secondNode);
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        if (solutionFound) {
            return new StringBuilder()
                    .append("Search setting: " + name + "\n")
                    .append("Start: " + startNode.getId() + "\n")
                    .append("Finish: " + destinationNode.getId() + "\n")
                    .append("Total Distance: " + totalDistance + "\n")
                    .append("Total Time: " + timeTaken + "\n")
                    .append("Traffic Signal Passed: " + trafficSignalPassed + "\n")
                    .append("Passed: " + path.size() + "\n")
                    .append("Visited" + visitedCount + "\n")
                    .append("Frontier: " + frontierCount + "\n")
                    .append("Processed Time (ms): " + processTimeMS)
                    .toString();
        } else {
            if (startNode != null && destinationNode != null) {
                return new StringBuilder()
                        .append("Search setting: " + name + "\n")
                        .append("Start: " + startNode.getId() + "\n")
                        .append("Finish: " + destinationNode.getId() + "\n")
                        .append("Solution not found...")
                        .toString();
            } else return "Search Setting: " + name;
        }
    }

    public String getSearchName() {
        return name;
    }

    public ArrayList<Node> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    public int getTrafficSignalPassed() {
        return trafficSignalPassed;
    }

    public void setMapController(MapController.SearchTask mapTask) {
        this.mapTask = mapTask;
    }

    public void setMapControllerv2(MapController mapControllerv2) {
        this.mapController = mapControllerv2;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }

    public int getFrontierCount() {
        return frontierCount;
    }

    public int getVisitedCount() {
        return visitedCount;
    }

    public long getProcessTimeMS() {
        return processTimeMS;
    }

    public double getDistanceStartFinish() { return sldStartToFinish; }
}

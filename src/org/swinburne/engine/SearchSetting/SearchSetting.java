package org.swinburne.engine.SearchSetting;

import org.swinburne.engine.HeuristicSetting.HeuristicSetting;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.Tree.TreeNode;
import org.swinburne.view.controller.MapController;

import java.util.ArrayList;

/**
 * An abstract class of search algorithm used to find solution path from starting node to finish node in a graph. Implementation of this class and then adding them to the static list is required to ccreate a new search algorithm of this framework. Has some built-in global value. It contains the ID of the search setting. Each individual search result is stored within the object, and thus contains the starting and destination {@link Node}, total distance travelled, total travel time, frontier node count, solution path, visited node count, process time, straight line distance from start to finish and traffic signal passed. Use rmust call the computeDirection function first before getting the values.
 */
public abstract class SearchSetting {
    public static final double AVERAGE_TURNING_TIME = 5;
    public static final double AVERAGE_INTERSECTION_TIME = 20;

    private static ArrayList<SearchSetting> searchSettingList = new ArrayList<>();

    /**
     * Static settings that add each individual search setting to its global static list.
     */
    static {
        searchSettingList.add(new BestTimeSearch());
        searchSettingList.add(new BestDistanceSearch());
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

    /**
     * Constructor setting the search setting name and its list of ID.
     * @param name search setting name
     * @param ids list of search setting ID
     */
    public SearchSetting(String name, String[] ids) {
        this.name = name;
        for (String s : ids) this.ids.add(s);
    }

    /**
     * Check whether this setting has a specified ID.
     * @param id check ID
     * @return true if ID is found
     */
    public boolean isID(String id) {
        for (String s : ids)
            if (s.equalsIgnoreCase(id)) return true;

        return false;
    }

    /**
     * Reset the search setting result and state.
     */
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

    /**
     * Abstract function to implement the search function from start to destination {@link Node} in a given {@link Graph}.
     * @param graph graph
     * @param start start node
     * @param destination destination node
     */
    public abstract void computeDirection(Graph graph, Node start, Node destination);

    /**
     * Unique function used to derive the solution path while also acting as the finishing procedure to the search function.
     * @param destination final destination {@link TreeNode}
     * @return {@link ArrayList} of solution path node
     */
    protected abstract ArrayList<Node> deriveSolution(TreeNode<Node> destination);

    /**
     * Get the list of implemented search setting within the static list.
     * @return list of search setting
     */
    public static ArrayList<SearchSetting> getSearchSettingList() { return searchSettingList; }

    /**
     * Draw frontier on the attached {@link MapController}.
     * @param firstNode first node
     * @param secondNode second node
     * @return true if attached map controller is not null
     */
    protected boolean drawFrontier(Node firstNode, Node secondNode) {
        if (mapTask != null) {
            mapTask.drawFrontier(firstNode, secondNode);
            return true;
        } else return false;
    }

    /**
     * Return the string value of the property of this search result.
     * @return string value
     */
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

    /**
     * Get the search setting name.
     * @return name string
     */
    public String getSearchName() {
        return name;
    }

    /**
     * Get the solution path of this search setting. Must call the computeDirection function first.
     * @return solution path
     */
    public ArrayList<Node> getPath() {
        return path;
    }

    /**
     * Get total distance travelled in meter.
     * @return distance in meter
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Get the total travel time in second.
     * @return travel time in second
     */
    public double getTimeTaken() {
        return timeTaken;
    }

    /**
     * Get the count of traffic signal paseed.
     * @return traffic signal passed
     */
    public int getTrafficSignalPassed() {
        return trafficSignalPassed;
    }

    /**
     * Set the attached {@link MapController}.
     * @param mapTask attached map controller
     */
    public void setMapController(MapController.SearchTask mapTask) {
        this.mapTask = mapTask;
    }

    /**
     * Check whether a solution path is found.
     * @return true if solution path is found, else false
     */
    public boolean isSolutionFound() {
        return solutionFound;
    }

    /**
     * Get the number of node visited in frontier phase.
     * @return frontier node count
     */
    public int getFrontierCount() {
        return frontierCount;
    }

    /**
     * Get the number of visited node when performing search.
     * @return visited node count
     */
    public int getVisitedCount() {
        return visitedCount;
    }

    /**
     * Get the total process time.
     * @return process time in millisecond
     */
    public long getProcessTimeMS() {
        return processTimeMS;
    }

    public double getDistanceStartFinish() { return sldStartToFinish; }
}

package org.swinburne.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the connection between {@link Node}, or in this case which based on OSM file, represents the collective ordered list of {@link Node} that is located within a road or line. Has unique ID to distinguish itself and contains ordered list of {@link Way} along information such as speed limit and whether it is a one-way.
 */
public class Way {
    // Road name
    private String label;
    private String id;

    // Distance measured in km
    private float distance;
    // Speed measured in km/h
    private float speedLimit = 50; // speed limit is 40 km/h by default
    // Traffic coefficient limit 0.01 to 1
    private float traffic;

    // ArrayList should be counted from top to bottom
    private boolean oneway = false;

    private WayType wayType = WayType.Road;

    private ArrayList<Node> nodeOrderedList = new ArrayList<>();
    private Map<String, Double> metaData = new HashMap<>();

    /**
     * Constructor of an empty way.
     */
    public Way() {
        this.distance = 0;
        this.traffic = 0;
    }

    /**
     * Constructor that sets basic information this way.
     * @param distance distance in meter
     * @param speedLimit speed limit
     * @param traffic traffic coefficient
     */
    public Way(float distance, float speedLimit, float traffic) {
        this.distance = distance;
        this.speedLimit = speedLimit;
        this.traffic = traffic;
    }

    /**
     * Copy constructor of another way instance.
     * @param way way to be copied
     */
    public Way(Way way) {
        this.label = way.label;
        this.distance = way.distance;
        this.speedLimit = way.speedLimit;
        this.traffic = way.traffic;
    }

    /**
     * Get the ordered {@link ArrayList} of {@link Node}.
     * @return ordered list of node
     */
    public ArrayList<Node> getNodeOrderedList() {
        return nodeOrderedList;
    }

    /**
     * Add node to its list of {@link Node}. Then return new size of list.
     * @param node node to be added
     * @return new node list size
     */
    public int addNode(Node node) {
        if (nodeOrderedList.add(node)) {
            node.addWay(this);
            return nodeOrderedList.size();
        }
        return -1;
    }

    /**
     * Remove the selcted {@link Node} from the list.
     * @param node node to be removed
     * @return whether it is successfully removed or not
     */
    public boolean removeNode(Node node) {
        return nodeOrderedList.remove(node);
    }

    /**
     * Set this the ordered node list of this way to copy another one. Ensure that there is no duplicate of ways for each {@link Node}.
     * @param nodeList node list to be copied.
     */
    public void setNodeList(ArrayList<Node> nodeList) {
        this.nodeOrderedList = nodeList;
        for (Node n : nodeList) {
            boolean found = false;
            for (Way w : n.getWayArrayList()) {
                if (w == this) {
                    found = true;
                    break;
                }
            }
            if (found) break;
            else n.addWay(this);
        }
    }

    /**
     * Return the adjacent nodes in this way. Return an array of possible maximum two adjacent node in this ordered list. If there are two possibilities, first element will always be the one before, and the second one is the one after. Return an empty array if no adjacent (e.g. only one node in the entire way) or size 1 if node is in corner.
     * <p>
     * Return null if no such node is found
     * </p>
     * @param node node to be checked
     * @return an array of adjacent nodes
     */
    public Node[] getAdjacents(Node node) {
        int pos = nodeOrderedList.indexOf(node);
        if (pos == -1) return null;

        if (nodeOrderedList.size() == 1) return new Node[0];

        if (!oneway) {
            // @TODO: i know i can make this part shorter, but that's future me
            // problem
            if (pos == 0) {
                if (nodeOrderedList.get(0) == nodeOrderedList.get(nodeOrderedList.size() - 1)) {
                    Node[] result = new Node[2];
                    result[1] = nodeOrderedList.get(nodeOrderedList.size() - 2);
                    result[0] = nodeOrderedList.get(1);
                    return result;
                } else {
                    Node[] result = new Node[1];
                    result[0] = nodeOrderedList.get(1);
                    return result;
                }
            } else if (pos == (nodeOrderedList.size() - 1)) {
                if (nodeOrderedList.get(0) == nodeOrderedList.get(nodeOrderedList.size() - 1)) {
                    Node[] result = new Node[2];
                    result[1] = nodeOrderedList.get(nodeOrderedList.size() - 2);
                    result[0] = nodeOrderedList.get(1);
                    return result;
                } else {
                    Node[] result = new Node[1];
                    result[0] = nodeOrderedList.get(nodeOrderedList.size() - 2);
                    return result;
                }
            } else {
                Node[] result = new Node[2];
                result[0] = nodeOrderedList.get(pos - 1);
                result[1] = nodeOrderedList.get(pos + 1);
                return result;
            }
        } else {
            if (pos == (nodeOrderedList.size() - 1)) {
                return new Node[0];
            } else {
                Node[] result = new Node[1];
                result[0] = nodeOrderedList.get(pos + 1);
                return result;
            }
        }
    }

    /**
     * Get the metadata map.
     * @return {@link Map} of metadata
     */
    public Map<String, Double> getMetaData() {
        return metaData;
    }

    /**
     * Set the metadata map.
     * @param metaData new metadata
     */
    public void setMetaData(Map<String, Double> metaData) {
        this.metaData = metaData;
    }

    /**
     * Get the {@link String} label.
     * @return label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the {@link String} label
     * @param label new label string
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get this way ID.
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set this way ID
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get total distance in this way.
     * @return distance in meter
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Set total distance in this way
     * @param distance distance in meter
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Get speed limit.
     * @return speed limit in km/h
     */
    public float getSpeedLimitKmh() {
        return speedLimit;
    }

    /**
     * Set speed limit.
     * @param speedLimit speed limit in km/h
     */
    public void setSpeedLimitKmh(float speedLimit) {
        this.speedLimit = speedLimit;
    }

    /**
     * Get traffic coefficient.
     * @return traffic coefficient
     */
    public float getTraffic() {
        return traffic;
    }

    /**
     * Set traffic coefficient.
     * @param traffic new traffic coefficient
     */
    public void setTraffic(float traffic) {
        this.traffic = traffic;
    }

    /**
     * Get {@link WayType}.
     * @return type
     */
    public WayType getWayType() {
        return wayType;
    }

    /**
     * Set {@link WayType}.
     * @param wayType type
     */
    public void setWayType(WayType wayType) {
        this.wayType = wayType;
    }

    /**
     * Check whether this is one way or not.
     * @return true if it's one way
     */
    public boolean isOneway() {
        return oneway;
    }

    /**
     * Set whether this is one way or not.
     * @param oneway true if one way
     */
    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }
}

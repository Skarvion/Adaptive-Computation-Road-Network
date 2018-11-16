package org.swinburne.engine;

import javafx.concurrent.Task;
import org.swinburne.engine.SearchSetting.SearchSetting;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.view.controller.MapController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Generates test cases for the map and store the data to a specified CSV file. Implements JavaFX {@link Task} and hence can be run on separate thread if needed. Acts as individual objects with different settings per new object of the generator.
 */
public class TestCaseGenerator extends Task<Integer> {
    private Graph graph;
    private ArrayList<Node> nodeList;

    private String filename;
    private int testCase;
    private int startID;

    private long startTime = 0;
    private long processTimeMS = 0;

    private MapController mapController = null;

    private boolean simulateTraffic;

    /**
     * Cosntructor class that sets the graph, filename, and test cases.
     * @param graph connected test graph
     * @param filename prefix file name
     * @param testCase number of test cases
     */
    public TestCaseGenerator(Graph graph, String filename, int testCase) {
        this.graph = graph;
        this.filename = filename;
        this.testCase = testCase;
        this.startID = 0;
        this.simulateTraffic = false;
    }

    /**
     * Cosntructor class that sets the graph, filename, and test cases. Also sets the initial ID stated in the test case file later.
     * @param graph connected test graph
     * @param filename prefix file name
     * @param testCase number of test cases
     * @param startID starting ID in CSV file
     */
    public TestCaseGenerator(Graph graph, String filename, int testCase, int startID) {
        this.graph = graph;
        this.filename = filename;
        this.testCase = testCase;
        this.startID = startID;
        this.simulateTraffic = false;
    }

    /**
     * Synchronous call to start generating test cases with given settings.
     */
    public void generateTestCase() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronous handler when running on a separate thread to start the test case generation. Test case data set is stored in separate CSV file for each {@link SearchSetting} listed in its static list with the same prefix name. The test case is done as many times as specified. <p>
     *     Each entry contains the properties of a given search result such as the total distance travelled, travell time, starting and finish {@link Node} ID, solution path node count, frontier node count, visited node count and execution time.
     * </p>
     * @return current test case number
     */
    @Override
    protected Integer call() {
        int iteration = startID;
        startTime = System.nanoTime();

        try {

            HashMap<SearchSetting, FileWriter> fileWriterMap = new HashMap<>();

            for (SearchSetting se : SearchSetting.getSearchSettingList()) {
                fileWriterMap.put(se, new FileWriter(new File(filename + se.getSearchName().toUpperCase() + ".csv")));
                fileWriterMap.get(se).write("Test ID, Start Node ID, Destination Node ID, Straight Line Distance, Total Distance, Total Time, Traffic Signal Passed, Path Size, Visited Count, Frontier Count, Process Time (MS)\n");
            }

            nodeList = new ArrayList(graph.getNodeMap().values());

            for (iteration = startID; iteration < (startID + testCase); iteration++) {

                Random random = new Random();
                int startIndex = random.nextInt(nodeList.size());
                int destinationIndex;
                do {
                    destinationIndex = random.nextInt(nodeList.size());
                } while (destinationIndex == startIndex);

                Node start = nodeList.get(startIndex);
                Node destination = nodeList.get(destinationIndex);
                System.out.println("Test case " + iteration + " | Start: " + start.getId() + " | " + destination.getId());

                graph.reset();

                for (SearchSetting se : SearchSetting.getSearchSettingList()) {
                    se.computeDirection(graph, start, destination);
                    if (se.isSolutionFound()) {
                        fileWriterMap.get(se).write(parseResult(iteration,
                                start.getId(),
                                destination.getId(),
                                se.getDistanceStartFinish(),
                                se.getTotalDistance(),
                                se.getTimeTaken(),
                                se.getTrafficSignalPassed(),
                                se.getPath().size(),
                                se.getVisitedCount(),
                                se.getFrontierCount(),
                                se.getProcessTimeMS()) + "\n");
                    } else {
                        fileWriterMap.get(se).write(parseEmpty(iteration, start.getId(), destination.getId()) + "\n");
                    }
                }
            }

            for (FileWriter fw : fileWriterMap.values()) fw.close();

            System.out.println("Test case generation done");

        } catch (IOException e) {
            e.printStackTrace();
            updateMessage("Test case generator IOException Error");
        } catch (Exception e) {
            e.printStackTrace();
            updateMessage("Test case generator Cancelled");
        }
        processTimeMS = (System.nanoTime() - startTime) / 1000000;

        return iteration;
    }

    /**
     * Write empty content to the CSV file row.
     * @param iteration iteration ID
     * @param startID starting node ID
     * @param destinationID finish node ID
     * @return compiled string
     */
    private String parseEmpty(int iteration, String startID, String destinationID) {
        StringBuilder sb = new StringBuilder();
        sb.append(quoteMarks(Integer.toString(iteration)));
        sb.append(quoteMarks(startID));
        sb.append(quoteMarks(destinationID));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.append(quoteMarks("-"));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Write data string based on the test case result.
     * @param iteration iteration ID
     * @param startID starting node ID
     * @param destinationID finish node ID
     * @param sldDistance straight line distance from start to finish node in meter
     * @param totalDistance total distance travelled in meter
     * @param timeTaken total tiime take in second
     * @param trafficSignalpassed traffic signal passed
     * @param size size of solution path
     * @param visitedCount visited node count
     * @param frontierCount frontier ndoe count
     * @param processTimeMS processing time in millisecond
     * @return compiled string
     */
    private String parseResult(int iteration, String startID, String destinationID, double sldDistance, double totalDistance, double timeTaken, int trafficSignalpassed, int size, int visitedCount, int frontierCount, long processTimeMS) {
        StringBuilder sb = new StringBuilder();
        sb.append(quoteMarks(Integer.toString(iteration)));
        sb.append(quoteMarks(startID));
        sb.append(quoteMarks(destinationID));
        sb.append(quoteMarks(Double.toString(sldDistance)));
        sb.append(quoteMarks(Double.toString(totalDistance)));
        sb.append(quoteMarks(Double.toString(timeTaken)));
        sb.append(quoteMarks(Integer.toString(trafficSignalpassed)));
        sb.append(quoteMarks(Integer.toString(size)));
        sb.append(quoteMarks(Integer.toString(visitedCount)));
        sb.append(quoteMarks(Integer.toString(frontierCount)));
        sb.append(quoteMarks(Long.toString(processTimeMS)));
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Add quote mark around string and comma
     * @param content content string
     * @return quoted string
     */
    private String quoteMarks(String content) {
        return "\"" + content + "\",";
    }

    /**
     * Get the processing time of the test case geenration.
     * @return time in millisecond
     */
    public long processTimeMS() { return processTimeMS; }
}

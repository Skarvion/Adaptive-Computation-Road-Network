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

    public TestCaseGenerator(Graph graph, String filename, int testCase) {
        this.graph = graph;
        this.filename = filename;
        this.testCase = testCase;
        this.startID = 0;
        this.simulateTraffic = false;
    }

    public TestCaseGenerator(Graph graph, String filename, int testCase, MapController mapController) {
        this.graph = graph;
        this.filename = filename;
        this.testCase = testCase;
        this.startID = 0;
        this.simulateTraffic = false;
        this.mapController = mapController;
    }

    public TestCaseGenerator(Graph graph, String filename, int testCase, int startID) {
        this.graph = graph;
        this.filename = filename;
        this.testCase = testCase;
        this.startID = startID;
        this.simulateTraffic = false;
    }

    public void generateTestCase() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer call() throws Exception {
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
//                updateMessage("Test case " + iteration + "\n" + search.toString());
//                updateProgress(iteration, startID + testCase);
            }

            for (FileWriter fw : fileWriterMap.values()) fw.close();

            System.out.println("Test case generation done");

//            Platform.runLater(() -> {
//                Alert completedAlert = new Alert(Alert.AlertType.INFORMATION, "Test case generation completed...");
//                completedAlert.show();
//            });

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

    public long processTimeMS() { return processTimeMS; }
}

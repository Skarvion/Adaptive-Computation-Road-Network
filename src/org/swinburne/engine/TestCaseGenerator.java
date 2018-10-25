package org.swinburne.engine;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.view.controller.MapController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        Platform.runLater(() -> {
            Alert startAlert = new Alert(Alert.AlertType.INFORMATION, "Test case generation started!");
            startAlert.show();
        });

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("ID",
                            "Start Node ID",
                            "End Node ID",
                            "SLD Start to Finish",
                            "Total Distance",
                            "Total Travel Time",
                            "Traffic Signal Passed",
                            "Path Node Count",
                            "Visited Node Count",
                            "Frontier Node Count",
                            "Processed Time"));

            nodeList = new ArrayList(graph.getNodeMap().values());

            for (iteration = startID; iteration < startID + testCase; iteration++) {

                Random random = new Random();
                int startIndex = random.nextInt(nodeList.size());
                int destinationIndex;
                do {
                    destinationIndex = random.nextInt(nodeList.size());
                } while (destinationIndex == startIndex);

                Node start = nodeList.get(startIndex);
                Node destination = nodeList.get(destinationIndex);
                System.out.println("Test case " + iteration + " | Start: " + start.getId() + " | " + destination.getId());

                AStarSearch search = new AStarSearch();
                search.computeDirection(graph, start, destination);

                if (search.isSolutionFound()) {
                    printer.printRecord(iteration,
                            start.getId(),
                            destination.getId(),
                            search.getDistanceStartFinish(),
                            search.getTotalDistance(),
                            search.getTimeTaken(),
                            search.getTrafficSignalPassed(),
                            search.getPath().size(),
                            search.getVisitedCount(),
                            search.getFrontierCount(),
                            search.getProcessTimeMS());
                } else {
                    printer.printRecord(iteration,
                            start.getId(),
                            destination.getId(),
                            "-",
                            "-",
                            "-",
                            "-",
                            "-",
                            "-",
                            "-",
                            "-");
                }

                updateMessage("Test case " + iteration + "\n" + search.toString());
                updateProgress(iteration, startID + testCase);
            }

            printer.flush();

            Platform.runLater(() -> {
                Alert completedAlert = new Alert(Alert.AlertType.INFORMATION, "Test case generation completed...");
                completedAlert.show();
            });

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

    public long processTimeMS() { return processTimeMS; }
}

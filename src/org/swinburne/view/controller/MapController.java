package org.swinburne.view.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.swinburne.engine.AStarSearch;
import org.swinburne.engine.Parser.OSMParser;
import org.swinburne.engine.Parser.TrafficSignalCSVParser;
import org.swinburne.model.NodeType;
import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private AnchorPane drawPane;

    @FXML
    private ComboBox<SimpleObjectProperty<MapNode>> sourceNodeComboBox;

    @FXML
    private ComboBox<SimpleObjectProperty<MapNode>> destinationNodeComboBox;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Button calculateButton;

    @FXML
    private RadioButton noneRadioButton;

    @FXML
    private RadioButton startRadioButton;

    @FXML
    private RadioButton finishRadioButton;

    private final double PANE_OFFSET = 2;

    private ToggleGroup radioGroup = new ToggleGroup();
    private ObservableMap<Node, MapNode> graphNodeMap = FXCollections.observableHashMap();
    private ObservableList<MapEdge> graphEdgeObservableList = FXCollections.observableArrayList();
    private ObservableList<Line> solutionObservableList = FXCollections.observableArrayList();

    private Graph graph;
    private Image startPin;
    private Image finishPin;
    private ImageView startPinView;
    private ImageView finishPinView;

    private MapNode selectedStartNode;
    private MapNode selectedFinishNode;

    private enum NodeSelectionState { None , Start, Finish }
    private NodeSelectionState state = NodeSelectionState.None;

    private float zoomFactor = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            startPin = new Image(new FileInputStream("start-pin.png"));
            finishPin = new Image(new FileInputStream("finish-pin.png"));
            startPinView = new ImageView(startPin);
            finishPinView = new ImageView(finishPin);

            startPinView.setVisible(false);
            finishPinView.setVisible(false);

            startPinView.setPreserveRatio(true);
            startPinView.setFitWidth(25);
            startPinView.setFitHeight(startPinView.getImage().getHeight() / startPinView.getImage().getWidth() * startPinView.getFitWidth());

            finishPinView.setPreserveRatio(true);
            finishPinView.setFitWidth(25);
            finishPinView.setFitHeight(finishPinView.getImage().getHeight() / finishPinView.getImage().getWidth() * finishPinView.getFitWidth());

            drawPane.getChildren().add(startPinView);
            drawPane.getChildren().add(finishPinView);
//            startPinView.setLayoutX(100);
//            startPinView.setLayoutY(100);

            noneRadioButton.setToggleGroup(radioGroup);
            startRadioButton.setToggleGroup(radioGroup);
            finishRadioButton.setToggleGroup(radioGroup);

            radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == noneRadioButton) state = NodeSelectionState.None;
                else if (newValue == startRadioButton) state = NodeSelectionState.Start;
                else state = NodeSelectionState.Finish;
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reload(ActionEvent event) {
        graph = OSMParser.parseFromOSM(new File("Test.osm"));
//        graph = TrafficSignalCSVParser.setTrafficIntersection(graph, "Traffic-Signal.csv");
        drawGraph();
    }

    @FXML
    void mapClick(MouseEvent event) {
        closestNodeClick(event);
    }

    @FXML
    private void calculateAction(ActionEvent event) {
        if (selectedStartNode == null || selectedFinishNode == null) return;

        drawPane.getChildren().removeAll(solutionObservableList);
        solutionObservableList.clear();

        ArrayList<Node> searchPath = new ArrayList<>();
        new Thread(new SearchTask(searchPath)).run();
        if (searchPath.size() == 0) System.out.println("Search is null?");

        String path = "";
        for (Node n : searchPath) {
            path += n.getId() + "\n|\nV\n";
        }
        outputTextArea.setText(path);
    }

    private void drawGraph() {
        selectedStartNode = null;
        selectedFinishNode = null;
        displayPin(startPinView, null);
        displayPin(finishPinView, null);

        drawPane.getChildren().clear();
        drawPane.getChildren().add(startPinView);
        drawPane.getChildren().add(finishPinView);

        graphNodeMap.clear();
        graphEdgeObservableList.clear();

        if (graph.getNodeList().isEmpty()) return;

        System.out.println("==============");
        System.out.println("Rendering graph...");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date startDate = new Date();
        System.out.println("Node Rendering\nStart: " + sdf.format(startDate));

        double topLat, botLat, leftLon, rightLon = 0;

        Node firstNode = graph.getNodeList().get(0);

        topLat = firstNode.getLatitude();
        botLat = firstNode.getLatitude();
        leftLon = firstNode.getLongitude();
        rightLon = firstNode.getLongitude();

        //@TODO: I messed around here to make test the first 10 isntances
//        int instances = 0;
        for (Node n : graph.getNodeList()) {
//            instances++;
//            if (instances > 10) break;

            if (n.getLatitude() > topLat) topLat = n.getLatitude();
            if (n.getLatitude() < botLat) botLat = n.getLatitude();
            if (n.getLongitude() > rightLon) rightLon = n.getLongitude();
            if (n.getLongitude() < leftLon) leftLon = n.getLongitude();
        }

        double paneWidth = drawPane.getWidth() - (2 * PANE_OFFSET);
        double paneHeight = drawPane.getHeight() - (2 * PANE_OFFSET);
        double graphWidth = Math.abs(rightLon - leftLon);
        double graphHeight = Math.abs(topLat - botLat);

//        instances = 0;
        for (Node n : graph.getNodeList()) {
//            instances++;
//            if (instances > 10) break;
            double relY = Math.abs(topLat - n.getLatitude());
            double relX = Math.abs(leftLon - n.getLongitude());

            double convertedX = (relX * paneWidth / graphWidth) + PANE_OFFSET;
            double convertedY = (relY * paneHeight / graphHeight) + PANE_OFFSET;

            MapNode temp = new MapNode(n, convertedX, convertedY);
            drawPane.getChildren().add(temp);
            graphNodeMap.put(n, temp);
        }

        System.out.println("Node finished...");
        System.out.println("Edge rendering\nStart" + sdf.format(new Date()));

        for (Way w : graph.getWayList()) {
            MapEdge mapEdge = new MapEdge(w);
            graphEdgeObservableList.add(mapEdge);
        }

        System.out.println("Rendering complete...\nEnd: " + sdf.format(new Date()));
    }

    private MapNode closestNodeClick(MouseEvent event) {
        MapNode closestNode = null;

        double closeX = drawPane.getWidth() + 10;
        double closeY = drawPane.getHeight() + 10;

        double distance = Math.sqrt(Math.pow(closeX, 2) + Math.pow(closeY, 2));

        for (MapNode mn : graphNodeMap.values()) {
            double temp = Math.sqrt(Math.pow(mn.getPosX() - event.getX(), 2) + Math.pow(mn.getPosY() - event.getY(), 2));
            if (temp < distance) {
                distance = temp;
                closestNode = mn;
            }
        }

        if (closestNode != null) {
            switch (state) {
                case None:
                    System.out.println("Node ID: " + closestNode.getNode().getId());
                    System.out.println("Ways: " + closestNode.getNode().getWayArrayList().size());
                    System.out.println("Type: " + closestNode.getNode().getType());
                    break;
                case Start:
                    selectedStartNode = closestNode;
                    displayPin(startPinView, closestNode);
                    break;
                case Finish:
                    selectedFinishNode = closestNode;
                    displayPin(finishPinView, closestNode);
                    break;
            }

//            System.out.println("Closest node is " + closestNode.getNode().getId() + "\nX: " + closestNode.getPosX() + "\nY: " + closestNode.getPosY());
        }
        return closestNode;
    }

    private void displayPin(ImageView view, MapNode node) {
        if (node == null) {
            view.setVisible(false);
            return;
        }
        view.setVisible(true);
        view.setLayoutX(node.getPosX() - (view.getFitWidth() / 2));
        view.setLayoutY(node.getPosY() - view.getFitHeight());
        view.toFront();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }


    // Reference: https://stackoverflow.com/questions/40444966/javafx-making-an-object-with-a-shape-and-text
    private class MapNode extends StackPane {
        private Circle circle;
        private Node node;
        private Line line;

        private final double RADIUS = 2;

        public MapNode(Node node, double x, double y) {
            circle = new Circle();
            circle.setRadius(RADIUS);
            circle.setFill(Color.YELLOW);
            circle.setStroke(Color.BLACK);
            getChildren().add(circle);
            setLayoutX(x);
            setLayoutY(y);
            this.node = node;

            if (node.getType() == NodeType.Intersection) {
                line = new Line();
                line.setStartX(0);
                line.setStartY(0);

                line.setEndX(10);
                line.setEndY(10);

                line.setStroke(Color.PURPLE);
                line.setStrokeWidth(3);
                getChildren().add(line);
            }

            if (node.getId().equalsIgnoreCase("1877118943")) {
                circle.setFill(Color.RED);
                circle.setRadius(RADIUS + 2);
            }
        }

        // This is because cannot override getLayoutX()
        public double getPosX() {
            return getLayoutX() + RADIUS;
//            return getLayoutX() + (label.getBoundsInLocal().getWidth() / 2);
        }

        public double getPosY() {
            return getLayoutY() + RADIUS;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        @Override
        public String toString() {
            return node.getLabel();
        }
    }

    private class MapEdge {
        private Way way;

        private ArrayList<Line> lineArrayList = new ArrayList<>();

        private final double STROKE_WIDTH = 2;

        public MapEdge(Way way) {
            this.way = way;

            if (way.getNodeOrderedList().size() <= 1) return;

            MapNode origin = getMapNode(way.getNodeOrderedList().get(0));
            if (origin == null) return;

            for (int i = 1; i < way.getNodeOrderedList().size(); i++) {
                try {
                    MapNode start = getMapNode(way.getNodeOrderedList().get(i));
                } catch (IndexOutOfBoundsException ioobe) {
                    break;
                }

                Line line = new Line();

                line.setStartX(getMapNode(way.getNodeOrderedList().get(i - 1)).getPosX());
                line.setStartY(getMapNode(way.getNodeOrderedList().get(i - 1)).getPosY());

                double newX = getMapNode(way.getNodeOrderedList().get(i)).getPosX();
                double newY = getMapNode(way.getNodeOrderedList().get(i)).getPosY();

                line.setEndX(newX);
                line.setEndY(newY);

                line.setStroke(Color.BLUE);
                line.setStrokeWidth(STROKE_WIDTH);

                drawPane.getChildren().add(line);

                lineArrayList.add(line);
            }
        }

        private MapNode getMapNode(Node node) {
            return graphNodeMap.get(node);
        }
    }

    private class MapNodeListCell extends ListCell<SimpleObjectProperty<MapNode>> {
        @Override
        protected void updateItem(SimpleObjectProperty<MapNode> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setText(null);
            else setText(item.get().getNode().getLabel());
        }
    }

    private class StringConverter extends javafx.util.StringConverter<SimpleObjectProperty<MapNode>> {
        @Override
        public String toString(SimpleObjectProperty<MapNode> object) {
            return object.get().getNode().getLabel();
        }

        @Override
        public SimpleObjectProperty<MapNode> fromString(String string) {
            return null;
        }
    }

    public void drawFrontier(Node source, Node destination) {
        MapNode sourceMapNode = graphNodeMap.get(source);
        MapNode destinationMapNode = graphNodeMap.get(destination);

        if (sourceMapNode == null || destinationMapNode == null) return;

        Line line = new Line();

        line.setStartX(sourceMapNode.getPosX());
        line.setStartY(sourceMapNode.getPosY());

        line.setEndX(destinationMapNode.getPosX());
        line.setEndY(destinationMapNode.getPosY());

        line.setStrokeWidth(4);
        line.setStroke(Color.YELLOW);

        drawPane.getChildren().add(line);
        solutionObservableList.add(line);
    }

    private class SearchTask extends Task<Void> {

        private ArrayList<Node> resultPath;

        private SearchTask(ArrayList<Node> resultPath) {
            this.resultPath = resultPath;
        }

        @Override
        protected Void call() throws Exception {
            AStarSearch search = new AStarSearch();
            search.setMapController(MapController.this);
            search.computeDirection(graph, selectedStartNode.getNode(), selectedFinishNode.getNode());

            ArrayList<Node> test = search.getPath();
            resultPath = test;
            if (resultPath.size() == 0) System.out.println("Test is null?");

            ArrayList<MapNode> foundMapNode = new ArrayList<>();

            for (Node n : resultPath) {
                MapNode found = graphNodeMap.get(n);
                if (found != null) foundMapNode.add(found);
            }

            System.out.println("Distance travelled: " + search.getTotalDistance());
            System.out.println("Time taken: " + search.getTimeTaken());
            System.out.println("Intersection passed: " + search.getIntersectionPassed());

            if (foundMapNode.size() <= 1) return null;

            Platform.runLater(() -> {
                for (int i = 1; i < foundMapNode.size(); i++) {
                    Line solutionLine = new Line();
                    solutionLine.setStartX(foundMapNode.get(i - 1).getPosX());
                    solutionLine.setStartY(foundMapNode.get(i - 1).getPosY());

                    solutionLine.setEndX(foundMapNode.get(i).getPosX());
                    solutionLine.setEndY(foundMapNode.get(i).getPosY());
                    solutionLine.setStrokeWidth(4);
                    solutionLine.setStroke(Color.GREEN);
                    solutionLine.toFront();

                    drawPane.getChildren().add(solutionLine);
                    solutionObservableList.add(solutionLine);
                }
            });

            return null;
        }
    }
}

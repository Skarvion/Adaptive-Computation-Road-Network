package org.swinburne.view.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.swinburne.engine.Parser.OSMParser;
import org.swinburne.engine.Parser.TrafficSignalCSVParser;
import org.swinburne.engine.SearchSetting.SearchSetting;
import org.swinburne.engine.TestCaseGenerator;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.swinburne.model.NodeType;
import org.swinburne.model.Way;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MapController implements Initializable {

    @FXML
    private ComboBox<SearchSetting> searchSettingCombo;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane drawPane;

    @FXML
    private Button openMapFileButton;

    @FXML
    private Button reloadButton;

    @FXML
    private RadioButton noneRadioButton;

    @FXML
    private RadioButton startRadioButton;

    @FXML
    private RadioButton finishRadioButton;

    @FXML
    private Button clearPinsButton;

    @FXML
    private Button calculateButton;

    @FXML
    private TextField nodeSearchText;

    @FXML
    private Button searchNodeButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private TableView<PropertyEntry> propertiesTableView;

    @FXML
    private TableColumn<PropertyEntry, String> keyTableCol;

    @FXML
    private TableColumn<PropertyEntry, String> valueTableCol;

    @FXML
    private Button generateTestCaseButton;

    @FXML
    private Label statusLabel;

    private final double PANE_OFFSET = 2;

    private ToggleGroup radioGroup = new ToggleGroup();
    private ObservableMap<Node, MapNode> graphNodeMap = FXCollections.observableHashMap();
    private ObservableList<MapEdge> graphEdgeObservableList = FXCollections.observableArrayList();
    private ObservableList<Line> solutionObservableList = FXCollections.observableArrayList();

    private ObservableList<PropertyEntry> propertyEntries = FXCollections.observableArrayList();

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
    private double initialPaneWidth;
    private double initialPaneHeight;

    private File mapFile;
    private Double topLat;
    private Double leftLon;
    private Double botLat;
    private Double rightLon;

    private long delayMS;

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

            startRadioButton.setToggleGroup(radioGroup);
            finishRadioButton.setToggleGroup(radioGroup);
            noneRadioButton.setToggleGroup(radioGroup);

            radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == startRadioButton) state = NodeSelectionState.Start;
                else if (newValue == finishRadioButton) state = NodeSelectionState.Finish;
                else state = NodeSelectionState.None;
            });

            initialPaneWidth = drawPane.getPrefWidth();
            initialPaneHeight = drawPane.getPrefHeight();

            Callback<ListView<SearchSetting>, ListCell<SearchSetting>> cellFactory = new Callback<ListView<SearchSetting>, ListCell<SearchSetting>>() {
                @Override
                public ListCell<SearchSetting> call(ListView<SearchSetting> param) {
                    return new ListCell<SearchSetting>() {
                        @Override
                        protected void updateItem(SearchSetting item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) setGraphic(null);
                            else setText(item.getSearchName());
                        }
                    };
                }
            };

            searchSettingCombo.setCellFactory(cellFactory);
            searchSettingCombo.setButtonCell(cellFactory.call(null));
            searchSettingCombo.getItems().addAll(SearchSetting.getSearchSettingList());

            propertiesTableView.setItems(propertyEntries);
            keyTableCol.setCellValueFactory(new PropertyValueFactory<>("key"));
            valueTableCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clearPin(ActionEvent event) {
        drawPane.getChildren().removeAll(solutionObservableList);
        solutionObservableList.clear();

        selectedStartNode = null;
        selectedFinishNode = null;

        startPinView.setVisible(false);
        finishPinView.setVisible(false);
    }

    @FXML
    void searchNode(ActionEvent event) {
        Node searchedNode = graph.getNode(nodeSearchText.getText());
        if (searchedNode == null) {
            System.out.println("Cannot search node with such ID!");
            return;
        }
        MapNode searchedMapNode = graphNodeMap.get(searchedNode);
        if (searchedMapNode == null) {
            System.out.println("Error! Node exists but not map node!");
            return;
        }

        System.out.println("Node ID: " + searchedMapNode.getNode().getId());
        System.out.println("Ways: " + searchedMapNode.getNode().getWayArrayList().size());
        System.out.println("Type: " + searchedMapNode.getNode().getType());
        System.out.println("Node Heuristic: " + searchedMapNode.getNode().getHeuristic());

        switch (state) {
            case None:
                break;
            case Start:
                selectedStartNode = searchedMapNode;
                displayPin(startPinView, searchedMapNode);
                break;
            case Finish:
                selectedFinishNode = searchedMapNode;
                displayPin(finishPinView, searchedMapNode);
                break;
        }
    }

    public void loadOSMFile(Map<String, Object> map) {
        topLat = (Double) map.get("topLat");
        leftLon = (Double) map.get("leftLon");
        botLat = (Double) map.get("botLat");
        rightLon = (Double) map.get("rightLon");
        mapFile = (File) map.get("file");

        reload(null);
    }

    @FXML
    void openMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/openMap.fxml"));

            Parent root = loader.load();
            FileController fileController = loader.getController();

            fileController.setMapController(this);
            Scene scene = new Scene(root, 300, 400);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(openMapFileButton.getScene().getWindow());
            stage.setScene(scene);
            stage.show();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    private void reload(ActionEvent event) {
        if (mapFile != null) {
            //@TODO: chagne here later
            graph = OSMParser.parseFromOSM(mapFile, -37.811323, 145.022338, -37.825929, 145.046812);
            graph = TrafficSignalCSVParser.setTrafficIntersection(graph, "Traffic-Signal.csv");
            drawGraph();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Map file is not found!");
            alert.showAndWait();
            graph = null;
        }
    }

    @FXML
    void mapClick(MouseEvent event) {
        closestNodeClick(event);
    }

    @FXML
    private void calculateAction(ActionEvent event) {
        if (selectedStartNode == null || selectedFinishNode == null) return;

        if (searchSettingCombo.getValue() == null) return;

        graph.reset();
        drawPane.getChildren().removeAll(solutionObservableList);
        solutionObservableList.clear();

        scrollPane.setHvalue(scrollPane.getHvalue() / 2);
        scrollPane.setVvalue(scrollPane.getVvalue() / 2);

        ArrayList<Node> searchPath = new ArrayList<>();
        new Thread(new SearchTask(searchPath, searchSettingCombo.getValue())).start();
    }

    @FXML
    void zoomOut(ActionEvent event) {
        if (zoomFactor <= 1) return;

        float oldZoomFactor = zoomFactor;
        zoomFactor -= 1;

        drawPane.setPrefWidth(initialPaneWidth * zoomFactor);
        drawPane.setPrefHeight(initialPaneHeight * zoomFactor);

        for (MapNode mn : graphNodeMap.values()) {
            mn.zoomPos(zoomFactor, oldZoomFactor);
        }

        if (selectedStartNode != null) displayPin(startPinView, selectedStartNode);
        if (selectedFinishNode != null) displayPin(finishPinView, selectedFinishNode);

        for (Line l : solutionObservableList) {
            l.setStartX(l.getStartX() * (zoomFactor) / oldZoomFactor);
            l.setStartY(l.getStartY() * (zoomFactor) / oldZoomFactor);
            l.setEndX(l.getEndX() * (zoomFactor) / oldZoomFactor);
            l.setEndY(l.getEndY() * (zoomFactor) / oldZoomFactor);
        }

        redrawEdges();
    }

    @FXML
    void zoomIn(ActionEvent event) {
        float oldZoomFactor = zoomFactor;
        zoomFactor += 1;

        drawPane.setPrefWidth(initialPaneWidth * zoomFactor);
        drawPane.setPrefHeight(initialPaneHeight * zoomFactor);

        for (MapNode mn : graphNodeMap.values()) {
            mn.zoomPos(zoomFactor, oldZoomFactor);
        }

        if (selectedStartNode != null) displayPin(startPinView, selectedStartNode);
        if (selectedFinishNode != null) displayPin(finishPinView, selectedFinishNode);

        for (Line l : solutionObservableList) {
            l.setStartX(l.getStartX() * (zoomFactor) / oldZoomFactor);
            l.setStartY(l.getStartY() * (zoomFactor) / oldZoomFactor);
            l.setEndX(l.getEndX() * (zoomFactor) / oldZoomFactor);
            l.setEndY(l.getEndY() * (zoomFactor) / oldZoomFactor);
        }

        redrawEdges();
    }

    @FXML
    void generateTestCase(ActionEvent event) {
        TestCaseGenerator generator = new TestCaseGenerator(graph, "Hawthorn", 10000);
        generator.progressProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("Test case: " + newValue);
        });
        generator.messageProperty().addListener(((observable, oldValue, newValue) -> {
            outputTextArea.setText(newValue);
        }));

        generator.generateTestCase();
//        new Thread(generator).startNode();
    }

    private void redrawEdges() {
        for (MapEdge me : graphEdgeObservableList) {
            me.clearLines();
        }
        graphEdgeObservableList.clear();

        for (Way w : graph.getWayMap().values()) {
            MapEdge mapEdge = new MapEdge(w);
            graphEdgeObservableList.add(mapEdge);
        }
    }

    private void populateProperty(Node node) {
        propertyEntries.clear();

        propertyEntries.add(new PropertyEntry("Name", node.getLabel()));
        propertyEntries.add(new PropertyEntry("ID", node.getId()));
        propertyEntries.add(new PropertyEntry("Latitude", Double.toString(node.getLatitude())));
        propertyEntries.add(new PropertyEntry("Longitude", Double.toString(node.getLongitude())));
        propertyEntries.add(new PropertyEntry("Type", node.getType().toString()));
        propertyEntries.add(new PropertyEntry("Ways", Integer.toString(node.getWayArrayList().size())));
        propertyEntries.add(new PropertyEntry("F Score", node.getFValue() == Double.MAX_VALUE ? "UNDEFINED" : Double.toString(node.getFValue())));
        propertyEntries.add(new PropertyEntry("G Cost", node.getGCost() == Double.MAX_VALUE ? "UNDEFINED" : Double.toString(node.getGCost())));
        propertyEntries.add(new PropertyEntry("Heuristic", node.getHeuristic() == Double.MAX_VALUE ? "UNDEFINED" : Double.toString(node.getHeuristic())));
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

        if (graph.getNodeMap().isEmpty()) return;

        System.out.println("==============");
        System.out.println("Rendering graph...");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date startDate = new Date();
        System.out.println("Node Rendering\nStart: " + sdf.format(startDate));

        double topLat, botLat, leftLon, rightLon = 0;

        topLat = -Double.MAX_VALUE;
        botLat = Double.MAX_VALUE;
        leftLon = Double.MAX_VALUE;
        rightLon = -Double.MAX_VALUE;

        for (Node n : graph.getNodeMap().values()) {
            if (n.getLatitude() > topLat) topLat = n.getLatitude();
            if (n.getLatitude() < botLat) botLat = n.getLatitude();
            if (n.getLongitude() > rightLon) rightLon = n.getLongitude();
            if (n.getLongitude() < leftLon) leftLon = n.getLongitude();
        }

        double paneWidth = drawPane.getWidth() - (2 * PANE_OFFSET);
        double paneHeight = drawPane.getHeight() - (2 * PANE_OFFSET);
        double graphWidth = Math.abs(rightLon - leftLon);
        double graphHeight = Math.abs(topLat - botLat);

        graph.getNodeMap().entrySet().removeIf(e -> e.getValue().getWayArrayList().isEmpty());
        for (Node n : graph.getNodeMap().values()) {

            if (n.getWayArrayList().size() == 0) {
                graph.removeNode(n);
                continue;
            }

            double relY = Math.abs(topLat - n.getLatitude());
            double relX = Math.abs(leftLon - n.getLongitude());

            double convertedX = ((relX * paneWidth / graphWidth) * zoomFactor) + PANE_OFFSET;
            double convertedY = ((relY * paneHeight / graphHeight) * zoomFactor) + PANE_OFFSET;

            MapNode temp = new MapNode(n, convertedX, convertedY);
            graphNodeMap.put(n, temp);
        }

        System.out.println("Node finished...");
        System.out.println("Edge rendering\nStart" + sdf.format(new Date()));

        Platform.runLater(() -> {
            redrawEdges();
        });

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
            populateProperty(closestNode.getNode());

            System.out.println("Node ID: " + closestNode.getNode().getId());
            System.out.println("Ways: " + closestNode.getNode().getWayArrayList().size());
            System.out.println("Type: " + closestNode.getNode().getType());
            System.out.println("Node Heuristic: " + closestNode.getNode().getHeuristic());
            System.out.println("NOde F Score: " + closestNode.getNode().getFValue());

            switch (state) {
                case None:
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
    private class MapNode {
        private Circle circle;
        private Node node;
        private Line line;
        private Label label;

        private double initialPosX;
        private double initialPosY;

        private final double RADIUS = 2;

        public MapNode(Node node, double x, double y) {
            circle = new Circle();
            circle.setRadius(RADIUS);
            circle.setFill(Color.YELLOW);
            circle.setStroke(Color.BLACK);
            drawPane.getChildren().add(circle);
            this.node = node;

            label = new Label(node.getLabel());
            drawPane.getChildren().add(label);

            if (node.getType() == NodeType.Intersection) {
                line = new Line();

                line.setStroke(Color.RED);
                line.setStrokeWidth(3);
                drawPane.getChildren().add(line);
            }

//            circle.setLayoutX(label.getBoundsInLocal().getWidth() / 2);
//            circle.setLayoutY(0);
//            drawPane.getChildren().add(this);

            Platform.runLater(() -> {
                circle.setLayoutX(x - (RADIUS / 2));
                circle.setLayoutY(y - (RADIUS / 2));

                label.setLayoutX(getPosX() - (label.getWidth() / 2));
                label.setLayoutY(getPosY() - (label.getHeight() / 2));

                if (line != null) {
                    line.setStartX(getPosX());
                    line.setStartY(getPosY());
                    line.setEndX(getPosX() + 10);
                    line.setEndY(getPosY() + 10);
                }

                initialPosX = getPosX();
                initialPosY = getPosY();
            });
        }

        public void setText(String text) {
            label.setText(text);
            label.toFront();
            label.setLayoutX(getPosX() - (label.getWidth() / 2));
            label.setLayoutY(getPosY() - (label.getHeight() / 2));
        }

        public void clearText() {
            label.setText("");
        }

        public void redraw() {
            label.setText(node.getLabel());
        }

        // This is because cannot override getLayoutX()
        public double getPosX() {
            return circle.getLayoutX() + (RADIUS / 2);
        }

        public double getPosY() {
            return circle.getLayoutY() + (RADIUS / 2);
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

        public void zoomPos(float newZoomFactor, float oldZoomFactor) {
            circle.setLayoutX(circle.getLayoutX() * newZoomFactor / oldZoomFactor);
            circle.setLayoutY(circle.getLayoutY() * newZoomFactor / oldZoomFactor);

            if (line != null) {
                line.setStartX(line.getStartX() * newZoomFactor / oldZoomFactor);
                line.setStartY(line.getStartY() * newZoomFactor / oldZoomFactor);
                line.setEndX(line.getStartX() + 10);
                line.setEndY(line.getStartY() + 10);
            }

            label.setLayoutX(getPosX() - (label.getWidth() / 2));
            label.setLayoutY(getPosY() - (label.getHeight() / 2));
        }
    }


    private class MapEdge {
        private Way way;

        private ArrayList<Line> lineArrayList = new ArrayList<>();
        private Label wayName;

        private final double STROKE_WIDTH = 2;

        public MapEdge(Way way) {
            this.way = way;
            boolean labeled = (way.getLabel() != null);
            if (labeled) wayName = new Label(way.getLabel());

            if (way.getNodeOrderedList().size() <= 1) return;

            MapNode origin = getMapNode(way.getNodeOrderedList().get(0));
            if (origin == null) return;

            double longest = 0;
            double streetStartX = 0, streetStartY = 0, streetEndX = 0, streetEndY = 0;
            for (int i = 1; i < way.getNodeOrderedList().size(); i++) {
//                try {
//                    MapNode startNode = getMapNode(way.getNodeOrderedList().get(i));
//                } catch (IndexOutOfBoundsException ioobe) {
//                    break;
//                }

                Line line = new Line();
                line.setStartX(getMapNode(way.getNodeOrderedList().get(i - 1)).getPosX());
                line.setStartY(getMapNode(way.getNodeOrderedList().get(i - 1)).getPosY());

                double newX = getMapNode(way.getNodeOrderedList().get(i)).getPosX();
                double newY = getMapNode(way.getNodeOrderedList().get(i)).getPosY();

                line.setEndX(newX);
                line.setEndY(newY);

                if (labeled) {
                    double distance = Math.sqrt(Math.pow(line.getStartX() - line.getEndX(), 2) + Math.pow(line.getStartY() - line.getEndY(), 2));
                    if (distance > longest) {
                        longest = distance;
                        streetStartX = line.getStartX();
                        streetStartY = line.getStartY();
                        streetEndX = line.getEndX();
                        streetEndY = line.getEndY();
                    }
                }

                line.setStroke(Color.BLUE);
                line.setStrokeWidth(STROKE_WIDTH);

                drawPane.getChildren().add(line);
                line.toBack();

                lineArrayList.add(line);
            }

            if (labeled) {
                wayName.setLayoutX(Math.abs(streetEndX - streetStartX) / 2 + Math.min(streetStartX, streetEndX));
                wayName.setLayoutY(Math.abs(streetEndY - streetStartY) / 2 + Math.min(streetStartY, streetEndY));
                drawPane.getChildren().add(wayName);
            }
        }

        private void clearLines() {
            for (Line l : lineArrayList) {
                drawPane.getChildren().remove(l);
            }
            if (wayName != null) drawPane.getChildren().remove(wayName);
//            drawPane.getChildren().remove(lineArrayList);
            lineArrayList.clear();
            wayName = null;
        }

        private MapNode getMapNode(Node node) {
            return graphNodeMap.get(node);
        }
    }

    public class SearchTask extends Task<Void> {

        private ArrayList<Node> resultPath;
        private SearchSetting searchSetting;

        private SearchTask(ArrayList<Node> resultPath, SearchSetting searchSetting) {
            this.resultPath = resultPath;
            this.searchSetting = searchSetting;
        }

        @Override
        protected Void call() throws Exception {
            Platform.runLater(() -> {
                for (MapNode mn : graphNodeMap.values()) {
                    mn.clearText();
                }
            });

            searchSetting.setMapController(this);
            searchSetting.computeDirection(graph, selectedStartNode.getNode(), selectedFinishNode.getNode());

            if (!searchSetting.isSolutionFound()) {
                System.out.println("No solution found");
                return null;
            }

            resultPath.clear();
            resultPath.addAll(searchSetting.getPath());

            ArrayList<MapNode> foundMapNode = new ArrayList<>();

            for (Node n : resultPath) {
                MapNode found = graphNodeMap.get(n);
                if (found != null) foundMapNode.add(found);
            }

            System.out.println("Distance travelled: " + searchSetting.getTotalDistance());
            System.out.println("Time taken: " + searchSetting.getTimeTaken());
            System.out.println("Intersection passed: " + searchSetting.getTrafficSignalPassed());

            String path = "";
            for (Node n : resultPath) {
                path += n.getId() + "\n|\nV\n";
            }

            String output = "Time: " + searchSetting.getTimeTaken() + "\n"
                    + "Distance: " + searchSetting.getTotalDistance() + "\n"
                    + path;
            outputTextArea.setText(output);

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

        public void drawFrontier(Node source, Node destination) {
            try {
                if (delayMS > 0) Thread.sleep(delayMS);
                Platform.runLater(() -> {
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

//                    sourceMapNode.setText(Double.toString(source.getFValue()));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class PropertyEntry {
        private SimpleStringProperty key;
        private SimpleStringProperty value;

        public PropertyEntry(String key, String value) {
            this.key = new SimpleStringProperty(key);
            this.value = new SimpleStringProperty(value);
        }

        public String getKey() {
            return key.getValue();
        }

        public void setKey(String key) {
            this.key.set(key);
        }

        public String getValue() {
            return value.getValue();
        }

        public void setValue(String value) {
            this.value.set(value);
        }

        public SimpleStringProperty keyProperty() {
            return key;
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }
    }
}

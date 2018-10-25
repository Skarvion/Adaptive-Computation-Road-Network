package org.swinburne.view.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.swinburne.engine.AStarSearch;
import org.swinburne.engine.Parser.OSMParser;
import org.swinburne.engine.Parser.TrafficSignalCSVParser;
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
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane drawPane;

    @FXML
    private Button openMapFileButton;

    @FXML
    private Button reloadButton;

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

            radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == startRadioButton) state = NodeSelectionState.Start;
                else state = NodeSelectionState.Finish;
            });

            initialPaneWidth = drawPane.getPrefWidth();
            initialPaneHeight = drawPane.getPrefHeight();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clearPin(ActionEvent event) {

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

        switch (state) {
            case None:
                System.out.println("Node ID: " + searchedMapNode.getNode().getId());
                System.out.println("Ways: " + searchedMapNode.getNode().getWayArrayList().size());
                System.out.println("Type: " + searchedMapNode.getNode().getType());
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

        drawPane.getChildren().removeAll(solutionObservableList);
        solutionObservableList.clear();

        scrollPane.setHvalue(scrollPane.getHvalue() / 2);
        scrollPane.setVvalue(scrollPane.getVvalue() / 2);

        ArrayList<Node> searchPath = new ArrayList<>();
        new Thread(new SearchTask(searchPath)).start();
    }

    @FXML
    void zoomIn(ActionEvent event) {
        zoomFactor += 1;

        drawPane.setPrefWidth(initialPaneWidth * zoomFactor);
        drawPane.setPrefHeight(initialPaneHeight * zoomFactor);


        for (MapNode mn : graphNodeMap.values()) {
            mn.zoomPos(zoomFactor);
        }

        redrawEdges();
    }

    @FXML
    void zoomOut(ActionEvent event) {
        zoomFactor -= 1;

        drawPane.setPrefWidth(initialPaneWidth * zoomFactor);
        drawPane.setPrefHeight(initialPaneHeight * zoomFactor);

        for (MapNode mn : graphNodeMap.values()) {
            mn.zoomPos(zoomFactor);
        }

        redrawEdges();
    }

    @FXML
    void generateTestCase(ActionEvent event) {
        TestCaseGenerator generator = new TestCaseGenerator(graph, "TestCase1.csv", 500);
        generator.progressProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("Test case: " + newValue);
        });
        generator.messageProperty().addListener(((observable, oldValue, newValue) -> {
            outputTextArea.setText(newValue);
        }));

        generator.generateTestCase();
//        new Thread(generator).start();
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

        for (Node n : graph.getNodeMap().values()) {
            if (n.getWayArrayList().size() == 0) continue;

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
        private Label label;

        private double initialPosX;
        private double initialPosY;

        private final double RADIUS = 2;

        public MapNode(Node node, double x, double y) {
            circle = new Circle();
            circle.setRadius(RADIUS);
            circle.setFill(Color.YELLOW);
            circle.setStroke(Color.BLACK);
            getChildren().add(circle);
            this.node = node;

            label = new Label(node.getLabel());
            label.setLayoutX(0);
            label.setLayoutY(0);
            getChildren().add(label);

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

            circle.setLayoutX(label.getBoundsInLocal().getWidth() / 2);
            circle.setLayoutY(0);
            drawPane.getChildren().add(this);

            Platform.runLater(() -> {
                setLayoutX(x - (label.getWidth() / 2));
                setLayoutY(y);

                initialPosX = getPosX();
                initialPosY = getPosY();
            });
        }

        public void setText(String text) { label.setText(text); }

        public void redraw() {
            label.setText(node.getLabel());
        }

        // This is because cannot override getLayoutX()
        public double getPosX() {
            return getLayoutX() + (label.getWidth() / 2);
        }

        public double getPosY() {
            return getLayoutY() + (getHeight() / 2);
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

        public void zoomPos(float newZoomFactor) {
//            setLayoutX(getLayoutX() * newZoomFactor);
//            setLayoutY(getLayoutY() * newZoomFactor);

            setLayoutX((initialPosX * newZoomFactor) - (label.getWidth() / 2));
            setLayoutY((initialPosY * newZoomFactor) - (getHeight() / 2));
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
//                    MapNode start = getMapNode(way.getNodeOrderedList().get(i));
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

    private class MapNodeListCell extends ListCell<SimpleObjectProperty<MapNode>> {
        @Override
        protected void updateItem(SimpleObjectProperty<MapNode> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setText(null);
            else setText(item.get().getNode().getLabel());
        }
    }

    private class NodeToStringConverter extends javafx.util.StringConverter<SimpleObjectProperty<MapNode>> {
        @Override
        public String toString(SimpleObjectProperty<MapNode> object) {
            return object.get().getNode().getLabel();
        }

        @Override
        public SimpleObjectProperty<MapNode> fromString(String string) {
            return null;
        }
    }

    public class SearchTask extends Task<Void> {

        private ArrayList<Node> resultPath;

        private SearchTask(ArrayList<Node> resultPath) {
            this.resultPath = resultPath;
        }

        @Override
        protected Void call() throws Exception {
            AStarSearch search = new AStarSearch();
            search.setMapController(this);
            search.computeDirection(graph, selectedStartNode.getNode(), selectedFinishNode.getNode());

            resultPath.clear();
            resultPath.addAll(search.getPath());
            if (resultPath.size() == 0) {
                System.out.println("Test is null?");
                return null;
            }

            ArrayList<MapNode> foundMapNode = new ArrayList<>();

            for (Node n : resultPath) {
                MapNode found = graphNodeMap.get(n);
                if (found != null) foundMapNode.add(found);
            }

            System.out.println("Distance travelled: " + search.getTotalDistance());
            System.out.println("Time taken: " + search.getTimeTaken());
            System.out.println("Intersection passed: " + search.getTrafficSignalPassed());

            String path = "";
            for (Node n : resultPath) {
                path += n.getId() + "\n|\nV\n";
            }
            outputTextArea.setText(path);

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
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class PropertyEntry {
        private String key;
        private String value;

        public PropertyEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

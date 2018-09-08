package org.swinburne.view.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.swinburne.engine.AStarSearch;
import org.swinburne.engine.Parser.OSMParser;
import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.GraphParser;
import org.swinburne.model.Node;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    private Graph graph;

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

    private final double PANE_OFFSET = 2;

    // Work around to a weird bug where the node will "go" to the combo box if selected, thus now wrap it in around simple object proeperty
    private ObservableList<SimpleObjectProperty<MapNode>> mapNodeObservableList = FXCollections.observableArrayList();

    private ObservableList<SimpleObjectProperty<MapEdge>> mapEdgeObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        graph = GraphParser.generateGraph("hawthorn.json");
        // @TODO: move the graph generation to outside the controller
        graph = OSMParser.parseFromOSM(new File("Swinburne.osm"));

        sourceNodeComboBox.setItems(mapNodeObservableList);
        sourceNodeComboBox.setCellFactory(param -> new MapNodeListCell());
        sourceNodeComboBox.setConverter(new StringConverter());

        destinationNodeComboBox.setItems(mapNodeObservableList);
        destinationNodeComboBox.setCellFactory(param -> new MapNodeListCell());
        destinationNodeComboBox.setConverter(new StringConverter());
    }

    @FXML
    private void reload(ActionEvent event) {
        drawGraph();
    }

    @FXML
    private void calculateAction(ActionEvent event) {
        MapNode start = sourceNodeComboBox.getSelectionModel().getSelectedItem().get();
        MapNode finish = destinationNodeComboBox.getSelectionModel().getSelectedItem().get();
        if (start == null || finish == null) {
            new Alert(Alert.AlertType.ERROR, "Source or destination node can't be empty!").showAndWait();
            return;
        }

        if (start == finish) {
            new Alert(Alert.AlertType.ERROR, "Source and destination can't be the same!").showAndWait();
            return;
        }

        ArrayList<Node> result = calculateDirection(start, finish);

        StringBuilder output = new StringBuilder();

        if (result.size() <= 1) {
            output.append("Result is empty!");
        }

        for (int i = 0; i < result.size(); i++) {
            if (i > 0) output.append("|\nV\n");
            output.append(result.get(i).getLabel() + "\n");
        }

        outputTextArea.setText(output.toString());
    }

    private ArrayList<Node> calculateDirection(MapNode start, MapNode finish) {
        AStarSearch aStarSearch = new AStarSearch();

        return aStarSearch.computeDirection(graph, start.getNode(), finish.getNode());
    }

    private void drawGraph() {
        if (graph.getNodeList().isEmpty()) return;

        drawPane.getChildren().clear();
        mapNodeObservableList.clear();
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
            mapNodeObservableList.add(new SimpleObjectProperty<MapNode>(temp));
        }

        // Draw lines between nodes
//        for (SimpleObjectProperty<MapNode> mn : mapNodeObservableList) {
//            Node node = mn.get().getNode();
//
//            for (Way eo : node.getOutEdge()) {
//                Node destinationNode = eo.getDestination();
//
//                for (SimpleObjectProperty<MapNode> mn1 : mapNodeObservableList) {
//                    if (mn1.get() == mn.get()) continue;
//
//                    if (mn1.get().getNode() == destinationNode) {
//                        MapEdge tempMapEdge = new MapEdge(eo, mn.get(), mn1.get());
//                        drawPane.getChildren().add(tempMapEdge);
//                        break;
//                    }
//                }
//            }
//        }

        for (Way w : graph.getWayList()) {
            MapEdge mapEdge = new MapEdge(w);
            drawPane.getChildren().add(mapEdge);
            mapEdgeObservableList.add(new SimpleObjectProperty<MapEdge>(mapEdge));
        }


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

        private final double RADIUS = 2;

        public MapNode(Node node, double x, double y) {
            circle = new Circle();
            circle.setRadius(RADIUS);
            circle.setFill(Color.YELLOW);
            circle.setStroke(Color.BLACK);

            getChildren().add(circle);

//            setLayoutX(x - (label.getBoundsInLocal().getWidth() / 2));
            setLayoutX(x);
            setLayoutY(y);

            System.out.println("Node: " + node.getId());
            System.out.println("PosX: " + getPosX());
            System.out.println("PosY: " + getPosY());
            System.out.println("-------------");

            this.node = node;
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

    private class MapEdge extends StackPane {
        private Way way;

        private ArrayList<Line> lineArrayList = new ArrayList<>();

        private final double STROKE_WIDTH = 1;

        public MapEdge(Way way) {
            this.way = way;

//            AnchorPane.setTopAnchor(this, 0.0);
//            AnchorPane.setLeftAnchor(this, 0.0);
//            AnchorPane.setRightAnchor(this, 0.0);
//            AnchorPane.setBottomAnchor(this, 0.0);

            if (way.getNodeOrderedList().size() <= 1) return;

            MapNode origin = getMapNode(way.getNodeOrderedList().get(0));
            if (origin == null) return;
            setLayoutX(origin.getPosX());
            setLayoutY(origin.getPosY());

            double lastX = 0;
            double lastY = 0;

            for (int i = 1; i < way.getNodeOrderedList().size(); i++) {
                MapNode start = getMapNode(way.getNodeOrderedList().get(i - 1));
                MapNode end = getMapNode(way.getNodeOrderedList().get(i));

                if (start == null || end == null) continue;

                Line line = new Line();

                double tempX = start.getPosX();
                double tempY = start.getPosY();
                double endX = end.getPosX();
                double endY = end.getPosY();

                double relX = endX - tempX;
                double relY = endY - tempY;

                line.setStartX(lastX);
                line.setStartY(lastY);
                line.setEndX(lastX + relX);
                line.setEndY(lastY + relY);

                lastX = lastX + relX;
                lastY = lastY + relY;

//                System.out.println("Start X " + line.getStartX());
//                System.out.println("Start Y " + line.getStartY());
//                System.out.println("End X " + line.getEndX());
//                System.out.println("End Y " + line.getEndY());
//                System.out.println("====================");

                line.setStroke(Color.BLUE);
                line.setStrokeWidth(STROKE_WIDTH);

                getChildren().add(line);
                lineArrayList.add(line);
            }
        }

        private MapNode getMapNode(Node node) {
            for (SimpleObjectProperty<MapNode> mapNode : mapNodeObservableList) {
                if (mapNode.get().getNode() == node) {
                    return mapNode.get();
                }
            }
            throw new NullPointerException();
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


}

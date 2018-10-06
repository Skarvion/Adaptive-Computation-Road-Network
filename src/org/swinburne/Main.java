package org.swinburne;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.swinburne.engine.Parser.OSMParser;
import org.swinburne.model.Graph;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/map.fxml"));
        primaryStage.setTitle("Adaptive Map Navigation");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);

//        Graph graph = OSMParser.parseFromOSM(new File("Hawthorn.osm"));


//        Graph graph = GraphParser.generateGraph("hawthorn.json");
//        for (Node n : graph.getNodeMap()) {
//            System.out.println("Found " + n.getId());
//        }
//
//        Node startingNode = graph.getNode("A");
//        Node finishNode = graph.getNode("G");
//        System.out.println("Starting from " + startingNode.getLabel());
//        System.out.println("Ending in " + finishNode.getLabel());
//
//        AStarSearch search = new AStarSearch();
//        ArrayList<Node> result = search.computeDirection(graph, startingNode, finishNode);
//        System.out.println("\nResult:");
//        for (Node n : result) {
//            System.out.println(n.getLabel());
//        }
//
//        GraphParser.saveGraph(graph, "Test.json");
    }
}

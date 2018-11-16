package org.swinburne;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.swinburne.engine.Parser.OSMParser;
import org.swinburne.model.Graph;

import java.io.File;

/**
 * The main entry point of the program.
 */
public class Main extends Application {

    /**
     * Start JavaFX GUI.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/map.fxml"));
        primaryStage.setTitle("Adaptive Map Navigation");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    /**
     * Main entry point of the program.
     * @param args args.
     */
    public static void main(String[] args) {

        launch(args);
    }
}

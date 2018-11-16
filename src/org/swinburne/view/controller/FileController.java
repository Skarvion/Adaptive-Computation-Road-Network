package org.swinburne.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A controller used to prompt the user to open the OSM file. Works in tandem with {@link MapController} as it calls its function to open the file based on the information given here.
 */
public class FileController implements Initializable {

    @FXML
    private Button openTrafficFileButton;

    @FXML
    private Label trafficSignalLabel;

    @FXML
    private Button openFileButton;

    @FXML
    private Label filenameLabel;

    @FXML
    private RadioButton unboundedRadio;

    @FXML
    private RadioButton boundedRadio;

    @FXML
    private TextField topLatText;

    @FXML
    private TextField leftLonText;

    @FXML
    private TextField bottomLatText;

    @FXML
    private TextField rightLonText;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private File selectedFile;
    private File selectedTrafficCSV;

    private MapController mapController;
    private ToggleGroup toggleGroup = new ToggleGroup();

    /**
     * Initialize the components.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boundedRadio.setToggleGroup(toggleGroup);
        unboundedRadio.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == boundedRadio) {
                topLatText.setDisable(false);
                bottomLatText.setDisable(false);
                leftLonText.setDisable(false);
                rightLonText.setDisable(false);
            } else {
                topLatText.setDisable(true);
                bottomLatText.setDisable(true);
                leftLonText.setDisable(true);
                rightLonText.setDisable(true);
            }
        });

        unboundedRadio.setSelected(true);
    }

    /**
     * Close this prompt window.
     */
    private void closeDialog() { ((Stage) cancelButton.getScene().getWindow()).close(); }

    /**
     * Event handler for open file button to open a file chooser dialog and select the OSM file that the user wants to load.
     * @param event
     */
    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open OSM file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OSM file (*.osm)", "*.osm");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(openFileButton.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            filenameLabel.setText(file.getName());
        }
    }

    /**
     * Event handler for open file button to open a file chooser dialog and select the traffic signal CSV file that the user wants to load.
     * @param event
     */
    @FXML
    void openTrafficFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open traffic CSV file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(openFileButton.getScene().getWindow());
        if (file != null) {
            selectedTrafficCSV = file;
            trafficSignalLabel.setText(file.getName());
        }
    }

    /**
     * Event handler for cancel button to cancel and close the prompt.
     * @param event
     */
    @FXML
    private void cancelMap(ActionEvent event) {
        closeDialog();
    }

    /**
     * Event handler for ok button to confirm the selection of OSM file and validate the file and boundary selection before calling the attached {@link MapController} to load the file.
     * @param event
     */
    @FXML
    private void okMap(ActionEvent event) {
        Map<String, Object> result = new HashMap<>();
        result.put("file", selectedFile);
        result.put("csvFile", selectedTrafficCSV);
        if (boundedRadio.isSelected()) {
            if (topLatText.getText() == null || leftLonText.getText() == null || bottomLatText.getText() == null || rightLonText.getText() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Bounded box must be filled!");
                alert.showAndWait();
                return;
            } else {
                try {
                    double topLat = Double.parseDouble(topLatText.getText());
                    double leftLon = Double.parseDouble(leftLonText.getText());
                    double botLat = Double.parseDouble(bottomLatText.getText());
                    double rightLon = Double.parseDouble(rightLonText.getText());

                    result.put("topLat", topLat);
                    result.put("leftLon", leftLon);
                    result.put("botLat", botLat);
                    result.put("rightLon", rightLon);

                } catch (NumberFormatException pe) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Number format is incorrect!");
                    alert.showAndWait();
                    return;
                }
            }
        }

        if (mapController != null) {
            mapController.loadOSMFile(result);
            closeDialog();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Map Controller is null!");
            alert.showAndWait();
        }
    }

    /**
     * Set the attached {@link MapController}
     * @param mapController referenced map
     */
    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }
}

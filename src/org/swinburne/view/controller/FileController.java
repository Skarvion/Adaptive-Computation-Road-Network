package org.swinburne.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FileController implements Initializable {

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

    private MapController mapController;
    private ToggleGroup toggleGroup = new ToggleGroup();

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
    }

    private void closeDialog() { ((Stage) cancelButton.getScene().getWindow()).close(); }

    @FXML
    void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open OSM file");
        File file = fileChooser.showOpenDialog(openFileButton.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            filenameLabel.setText(file.getName());
        }
    }

    @FXML
    void cancelMap(ActionEvent event) {
        closeDialog();
    }

    @FXML
    void okMap(ActionEvent event) {
        Map<String, Object> result = new HashMap<>();
        result.put("file", selectedFile);
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
                    result.put("file", selectedFile);


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

    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }
}

package org.swinburne.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class TestCaseGeneratorController implements Initializable {

    @FXML
    private Button selectDirectoryButton;

    @FXML
    private TextField prefixTextField;

    @FXML
    private Spinner<Integer> testCaseSpinner;

    @FXML
    private Label directoryLabel;

    @FXML
    private Button generateTestCaseButton;

    @FXML
    private Button cancelButton;

    private MapController mapController;
    private File selectedOutputDirectory = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        testCaseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 100));
        testCaseSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) testCaseSpinner.increment(0);
        });
    }

    @FXML
    void cancel(ActionEvent event) {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    void generate(ActionEvent event) {
        if (mapController == null) return;

        if (testCaseSpinner.getValue() > 0) {
            if (prefixTextField.getText() != null && prefixTextField.getText().length() > 0) {
                if (selectedOutputDirectory != null) {
                    mapController.generateTestCase(selectedOutputDirectory, prefixTextField.getText(), testCaseSpinner.getValue());
                } else {
                    new Alert(Alert.AlertType.ERROR, "Output directory has not been specified yet.");
                }

            } else {
                new Alert(Alert.AlertType.ERROR, "Prefix file name must be filled.").showAndWait();
            }
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Number of test case must be more than 0.").showAndWait();
        }

    }

    @FXML
    void selectDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select test case output directory");
        File initialDirectory = new File("/");
        directoryChooser.setInitialDirectory(initialDirectory);
        selectedOutputDirectory = directoryChooser.showDialog((Stage) cancelButton.getScene().getWindow());

        directoryLabel.setText(selectedOutputDirectory.getAbsolutePath());
    }

    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }
}

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

/**
 * This class is used as the controller for test case generation prompt. It shows simple input such as the number of test case, prefix file name and the output directory. Once all set up, it will call the generate test case function in the {@link MapController}. Must setup the reference to {@link MapController} first before use.
 */
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

    /**
     * Initialize the stage and components.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        testCaseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 100));
        testCaseSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) testCaseSpinner.increment(0);
        });
    }

    /**
     * Event handler for cancel button to cancel the test case generation and close down the stage.
     * @param event
     */
    @FXML
    void cancel(ActionEvent event) {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    /**
     * Event handler for generate button to generate the test case. Validate input first before calling the {@link MapController} generate test case function.
     * @param event
     */
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

    /**
     * Event handler for output directory button to call a {@link DirectoryChooser} to select the output directory.
     * @param event
     */
    @FXML
    void selectDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select test case output directory");
        File initialDirectory = new File("/");
        directoryChooser.setInitialDirectory(initialDirectory);
        selectedOutputDirectory = directoryChooser.showDialog((Stage) cancelButton.getScene().getWindow());

        directoryLabel.setText(selectedOutputDirectory.getAbsolutePath());
    }

    /**
     * Set the reference to a {@link MapController}.
     * @param mapController reference
     */
    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }
}

package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.calibration.CalibrationType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class CalibrationController implements Initializable {

    @FXML
    VBox calibrationContainer;

    @FXML
    VBox notificationContainer;
    @FXML
    GridPane contentContainer;
    @FXML
    HBox actionContainer;

    // notificationContainer

    // contentContainer
    @FXML
    TextField calibrationInput;
    @FXML
    TextField electricalLengthInput;

    @FXML
    ToggleGroup calibrationType;
    @FXML
    RadioButton shortType;
    @FXML
    RadioButton loadType;
    @FXML
    RadioButton openType;
    // actionContainer
    @FXML
    Button runCalibrationBtn;


    public void runCalibration(MouseEvent event) {
        if (AppMain.calibrationService.isCalibrated()) {
            AppMain.calibrationService.closeCalibration();
        }
        RadioButton selectedRadioButton = (RadioButton) calibrationType.getSelectedToggle();
        AppMain.calibrationService.runCalibration(selectedRadioButton.getText());
        calibrationInput.setDisable(true);
        electricalLengthInput.setDisable(true);
        if (AppMain.calibrationService.isCalibrated()) {
            runCalibrationBtn.setText("Close");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppMain.calibrationService.addRadioButtons(new LinkedList<>(Arrays.asList(shortType, openType, loadType)));

        if (AppMain.calibrationService.isCalibrated()) {
            runCalibrationBtn.setText("Close");
        }
        calibrationInput.setText(AppMain.environmentParameters.getOther().getCapacitance() + "");
        electricalLengthInput.setText(AppMain.environmentParameters.getOther().getElectricalLength() + "");
        if (AppMain.calibrationService.isCalibrationInProcess()) {
            calibrationInput.setDisable(true);
            electricalLengthInput.setDisable(true);
        }
    }

}

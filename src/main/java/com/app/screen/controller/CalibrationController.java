package com.app.screen.controller;

import com.app.service.AppMain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;

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
        calibrationInput.setText(AppMain.environmentParameters.getActive().getOther().getCapacitance() + "");
        electricalLengthInput.setText(AppMain.environmentParameters.getActive().getOther().getElectricalLength() + "");
        if (AppMain.calibrationService.isCalibrationInProcess()) {
            calibrationInput.setDisable(true);
            electricalLengthInput.setDisable(true);
        }
    }

}

package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.calibration.CalibrationService;
import com.app.service.calibration.CalibrationState;
import com.app.service.calibration.CalibrationType;
import com.app.service.notification.NotificationType;
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
import java.util.*;

public class CalibrationController implements Initializable {

    CalibrationService cs;
    Timer calibrationWatcher;
    CalibrationState oldCalibrationState;

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
        toggleButtons();
        calibrationInput.setDisable(true);
        electricalLengthInput.setDisable(true);
    }

    private void toggleButtons() {
        boolean isRunning = cs.getState().equals(CalibrationState.RUNNING);
        boolean isDone = cs.getState().equals(CalibrationState.DONE);
        boolean isShort = cs.getCalibrationStates().get(CalibrationType.SHORT);
        boolean isOpen = cs.getCalibrationStates().get(CalibrationType.OPEN);

        runCalibrationBtn.setText(isDone ? "Close":"Run");
        runCalibrationBtn.setDisable(isRunning);
        shortType.setDisable(isRunning || isDone || isOpen || isShort);
        openType.setDisable(isRunning || isDone || isOpen);
        loadType.setDisable(isRunning || isDone);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cs = AppMain.calibrationService;
        oldCalibrationState = cs.getState();
        cs.addRadioButtons(new HashMap<>() {
            {
                put(CalibrationType.SHORT, shortType);
                put(CalibrationType.OPEN, openType);
                put(CalibrationType.LOAD, loadType);
            }
        });

        toggleButtons();
        calibrationInput.setText(AppMain.environmentParameters.getActive().getOther().getCapacitance() + "");
        electricalLengthInput.setText(AppMain.environmentParameters.getActive().getOther().getElectricalLength() + "");
        if (cs.isCalibrationInProcess()) {
            calibrationInput.setDisable(true);
            electricalLengthInput.setDisable(true);
        }

        if (!cs.getState().equals(CalibrationState.DONE)) {
            calibrationWatcher = new Timer();
            calibrationWatcher.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!oldCalibrationState.equals(cs.getState())) {
                        toggleButtons();
                        oldCalibrationState = cs.getState();
                        if (cs.getState().equals(CalibrationState.DONE)) {
                            calibrationWatcher.cancel();
                        }
                        cs.showNotification("Calibrating processed successfully. " + (cs.getState().equals(CalibrationState.DONE) ? "Check with machine, please!":"Change standard, please!"), NotificationType.SUCCESS);
                    }
                }
            }, 100,10);
        }
    }

}

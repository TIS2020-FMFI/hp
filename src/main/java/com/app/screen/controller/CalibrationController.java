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
    TextField calibrationFromInput;
    @FXML
    TextField calibrationToInput;

    @FXML
    ToggleGroup calibrationSpeed;
    @FXML
    RadioButton highSpeed;
    @FXML
    RadioButton lowSpeed;
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
        RadioButton selectedRadioButtonType = (RadioButton) calibrationType.getSelectedToggle();
        RadioButton selectedRadioButtonSpeed = (RadioButton) calibrationSpeed.getSelectedToggle();
        AppMain.calibrationService.runCalibration(selectedRadioButtonType.getText(), calibrationFromInput.getText(), calibrationToInput.getText(), selectedRadioButtonSpeed == highSpeed);
        toggleButtons();

        cs.setFrom(calibrationFromInput.getText());
        cs.setTo(calibrationToInput.getText());
        cs.setHighSpeed(selectedRadioButtonSpeed == highSpeed);
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
        calibrationFromInput.setDisable(isRunning);
        calibrationToInput.setDisable(isRunning);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cs = AppMain.calibrationService;
        cs.addRadioButtons(new HashMap<>() {
            {
                put(CalibrationType.SHORT, shortType);
                put(CalibrationType.OPEN, openType);
                put(CalibrationType.LOAD, loadType);
            }
        });

        calibrationFromInput.setText(AppMain.environmentParameters.getActive().getOther().getCapacitance() + "");
        calibrationToInput.setText(AppMain.environmentParameters.getActive().getOther().getElectricalLength() + "");

        if (!cs.getState().equals(CalibrationState.DONE)) {
            Timer watcher = new Timer();
            watcher.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!cs.getOldCalibrationState().equals(cs.getState())) {
                        toggleButtons();
                        cs.setOldCalibrationState(cs.getState());
                        if (cs.getState().equals(CalibrationState.DONE)) {
                            cs.getCalibrationWatcher().cancel();
                        }
                        cs.showNotification("Calibrating processed successfully. " + (cs.getState().equals(CalibrationState.DONE) ? "Check with machine, please!":"Change standard, please!"), NotificationType.SUCCESS);
                    }
                }
            }, 100,10);
            cs.setCalibrationWatcher(watcher);
        }
        toggleButtons();
    }

}

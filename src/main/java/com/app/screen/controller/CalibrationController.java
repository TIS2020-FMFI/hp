package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.calibration.CalibrationService;
import com.app.service.calibration.CalibrationState;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Controller for calibration
 */
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
    Label calibrationType;

    // actionContainer
    @FXML
    Button runCalibrationBtn;


    /**
     * Calibration trigger
     *
     * @param event triggered mouse event
     */
    public void runCalibration(MouseEvent event) {
        if (AppMain.calibrationService.isCalibrated()) {
            AppMain.calibrationService.close();
            return;
        }

        RadioButton selectedRadioButtonSpeed = (RadioButton) calibrationSpeed.getSelectedToggle();
        AppMain.calibrationService.runCalibration(cs.getType().toString().toLowerCase(), calibrationFromInput.getText(), calibrationToInput.getText(), selectedRadioButtonSpeed == highSpeed);
        toggleForm();

        cs.setFrom(calibrationFromInput.getText());
        cs.setTo(calibrationToInput.getText());
        cs.setHighSpeed(selectedRadioButtonSpeed == highSpeed);
    }

    /**
     * Toggles calibration form
     */
    private void toggleForm() {
        boolean isRunning = cs.getState().equals(CalibrationState.RUNNING);
        boolean inProcess = cs.isCalibrationInProcess();

        Platform.runLater(() -> {
            calibrationFromInput.setDisable(inProcess);
            calibrationToInput.setDisable(inProcess);
            calibrationType.setText(cs.getType().toString());
            runCalibrationBtn.setText(cs.isCalibrated() ? "Close":"Run");
            runCalibrationBtn.setDisable(isRunning);
        });
    }

    /**
     * Initializes controller
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cs = AppMain.calibrationService;

        calibrationType.setText(cs.getType().toString());
        calibrationFromInput.setText(cs.getFrom() != null ? cs.getFrom():"");
        calibrationToInput.setText(cs.getTo() != null ? cs.getTo():"");
        highSpeed.setSelected(cs.isHighSpeed());
        lowSpeed.setSelected(!cs.isHighSpeed());

        if (!cs.getState().equals(CalibrationState.DONE)) {
            Timer watcher = new Timer();
            watcher.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!cs.getOldState().equals(cs.getState())) {
                        cs.setOldState(cs.getState());
                        toggleForm();
                        if (cs.isCalibrated()) {
                            cs.showNotification("Calibrating processed successfully. " + (cs.getState().equals(CalibrationState.DONE) ? "Check with machine, please!":"Change standard, please!"), NotificationType.SUCCESS);
                            watcher.cancel();
                        } else if (!cs.getState().equals(CalibrationState.RUNNING)) {
                            cs.showNotification("Please, switch standards as required below.", NotificationType.ANNOUNCEMENT);
                        }
                    }
                }
            }, 100,10);
            cs.setStateWatcher(watcher);
        }
        toggleForm();
    }

}

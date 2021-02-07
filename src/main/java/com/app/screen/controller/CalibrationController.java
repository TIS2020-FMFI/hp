package com.app.screen.controller;

import com.app.service.AppMain;
import com.app.service.calibration.CalibrationService;
import com.app.service.calibration.CalibrationState;
import com.app.service.calibration.CalibrationType;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
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
    @FXML
    Button closeCalibrationBtn;


    /**
     * Calibration trigger
     *
     * @param event triggered mouse event
     */
    public void runCalibration(MouseEvent event) {
        RadioButton selectedRadioButtonSpeed = (RadioButton) calibrationSpeed.getSelectedToggle();
        if (!cs.isCalibrationInProcess()) {
            cs.setFrom(calibrationFromInput.getText());
            cs.setTo(calibrationToInput.getText());
            cs.setHighSpeed(selectedRadioButtonSpeed == highSpeed);

            cs.setState(CalibrationState.READY);
            cs.setType(CalibrationType.SHORT);

            if (!cs.getState().equals(CalibrationState.DONE)) {
                Timer watcher = new Timer();
                watcher.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cs.getOldState().equals(cs.getState())) {
                            cs.setOldState(cs.getState());
                            toggleForm();
                            if (cs.isCalibrated()) {
                                cs.showNotification("Calibrating processed successfully. Check with machine, please!", NotificationType.SUCCESS);
                                watcher.cancel();
                                Platform.runLater(() -> calibrationType.setText("SHORT"));
                            } else if (List.of(CalibrationState.DONE, CalibrationState.READY).contains(cs.getState())) {
                                cs.showNotification("Please, switch standards as required below.", NotificationType.ANNOUNCEMENT);
                            }
                        }
                    }
                }, 0,100);
                cs.setStateWatcher(watcher);
            }
        }
        AppMain.calibrationService.runCalibration(cs.getType().toString().toLowerCase(), calibrationFromInput.getText(), calibrationToInput.getText(), selectedRadioButtonSpeed == highSpeed);
        toggleForm();
    }

    /**
     * Requests to close calibration window
     *
     * @param event triggered mouse event
     */
    public void closeCalibration(MouseEvent event) {
        if (!cs.isCalibrationInProcess()) {
            AppMain.calibrationService.close();
        }
    }

    /**
     * Toggles calibration form
     */
    private void toggleForm() {
        boolean isRunning = cs.getState().equals(CalibrationState.RUNNING);
        boolean inProcess = cs.isCalibrationInProcess();
        boolean isFormValid = !calibrationFromInput.getText().isEmpty()
                && !calibrationToInput.getText().isEmpty()
                && !(calibrationFromInput.getStyleClass().stream().anyMatch(style -> style.equals("invalid-input")) || calibrationToInput.getStyleClass().stream().anyMatch(style -> style.equals("invalid-input")));
        boolean isRangeChanged = cs.getFrom() == null || cs.getTo() == null || !(cs.getFrom().equals(calibrationFromInput.getText()) && cs.getTo().equals(calibrationToInput.getText()));

        Platform.runLater(() -> {
            calibrationFromInput.setDisable(inProcess);
            calibrationToInput.setDisable(inProcess);
            calibrationType.setText(cs.isCalibrated() ? "SHORT":cs.getType().toString());
            runCalibrationBtn.setDisable(!isFormValid || isRunning || (cs.isCalibrated() && !isRangeChanged));
            closeCalibrationBtn.setDisable(inProcess);
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

        calibrationFromInput.setText(cs.getFrom() != null ? cs.getFrom():"");
        calibrationToInput.setText(cs.getTo() != null ? cs.getTo():"");
        highSpeed.setSelected(cs.isHighSpeed());
        lowSpeed.setSelected(!cs.isHighSpeed());

        toggleForm();
    }

    /**
     * Form input parser
     *
     * @param inputMethodEvent event that triggers this method
     */
    public void onInputChange(KeyEvent inputMethodEvent) {
        TextField source = (TextField)inputMethodEvent.getSource();
        if (source.getText().isEmpty()) {
            source.getStyleClass().removeAll("invalid-input");
        } else {
            try {
                double temp = Double.parseDouble(source.getText());
                if (temp >= 1 && temp <= 1000) {
                    source.getStyleClass().removeAll("invalid-input");
                } else {
                    source.getStyleClass().add("invalid-input");
                }
            } catch (NumberFormatException e) {
                source.getStyleClass().add("invalid-input");
            }
        }
        toggleForm();
    }
}

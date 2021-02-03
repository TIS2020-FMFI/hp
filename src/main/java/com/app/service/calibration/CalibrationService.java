package com.app.service.calibration;

import com.app.service.AppMain;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.naming.directory.NoSuchAttributeException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class CalibrationService {
    private final String path;
    private CalibrationState state;
    private NotificationService notificationService;
    private Map<CalibrationType, RadioButton> calibrationButtons;
    private Map<CalibrationType, Boolean> calibrationStates;
    private Stage stage;
    private double electricalLength;
    private double capacitance;

    public CalibrationService(String controllerPath) {
        path = controllerPath;
        state = CalibrationState.READY;
        calibrationStates = new HashMap<>() {
            {
                put(CalibrationType.SHORT, false);
                put(CalibrationType.OPEN, false);
                put(CalibrationType.LOAD, false);
            }
        };
    }

    public void openCalibration() {
        try {
            stage = new Stage();

            Parent calibrationRoot = FXMLLoader.load(getClass().getResource(path));
            stage.setScene(new Scene(calibrationRoot));
            stage.setTitle("Calibration");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            VBox notificationContainer = (VBox) calibrationRoot.lookup("#notificationContainer");
            if (notificationContainer == null) {
                throw new NoSuchElementException("Notification container not found in calibration window!");
            }
            notificationService = new NotificationService(notificationContainer);
            stage.show();
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

    public CalibrationState getState() {
        return state;
    }

    public Map<CalibrationType, Boolean> getCalibrationStates() {
        return calibrationStates;
    }

    public void setCalibrationState(CalibrationState state) {
        if (state.equals(CalibrationState.READY) && calibrationStates.get(CalibrationType.LOAD)) {
            this.state = CalibrationState.DONE;
            AppMain.communicationService.leaveCalibration();
            return;
        }
        this.state = state;
        if (state.equals(CalibrationState.READY)) {
            setAnotherActive();
        }
    }

    public void closeCalibration() {
        stage.close();
    }

    public void showNotification(String content, NotificationType type) {
        notificationService.createNotification(content, type);
    }

    public void addRadioButtons(Map<CalibrationType, RadioButton> radioButtons) {
        calibrationButtons = radioButtons;
        setAnotherActive();
    }

    public boolean isCalibrated() {
        return state.equals(CalibrationState.DONE);
    }
    public boolean isCalibrationInProcess() {
        return state.equals(CalibrationState.RUNNING);
    }

    private CalibrationType getActiveType() {
        for (Map.Entry<CalibrationType, RadioButton> button : calibrationButtons.entrySet()) {
            if (button.getValue().isSelected()) {
                return button.getKey();
            }
        }
        return setAnotherActive();
    }

    private CalibrationType setAnotherActive() {
        for (Map.Entry<CalibrationType, Boolean> cal : calibrationStates.entrySet()) {
            if (!cal.getValue()) {
                calibrationButtons.get(cal.getKey()).selectedProperty().set(true);
                return cal.getKey();
            }
        }
        return CalibrationType.SHORT;
    }

    public void runCalibration(String calibrationType, String from, String to, boolean isHighSpeed) {
        try {
            if (isCalibrationInProcess()) {
                throw new RuntimeException("Calibration in progress!");
            }
            CalibrationType requestedCalibrationType = CalibrationType.getTypeFromString(calibrationType);
            state = CalibrationState.RUNNING;
            AppMain.communicationService.runCalibration(requestedCalibrationType, Double.parseDouble(from), Double.parseDouble(to), isHighSpeed);
        } catch(NumberFormatException e) {
            AppMain.calibrationService.showNotification("Could not parse inputs, please, check their formats! -> " + e.getMessage(), NotificationType.ERROR);
        } catch (NoSuchAttributeException | RuntimeException e) {
            showNotification("upss! -> " + e.getMessage(), NotificationType.ERROR);
        }
    }
}

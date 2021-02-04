package com.app.service.calibration;

import com.app.service.AppMain;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Timer;


public class CalibrationService {
    private final String path;
    private CalibrationState state;
    private CalibrationType type;
    private NotificationService notificationService;
    private Stage stage;
    private String from;
    private String to;
    private boolean isHighSpeed;
    private Timer calibrationWatcher;
    private CalibrationState oldCalibrationState;

    public CalibrationService(String controllerPath) {
        path = controllerPath;
        state = CalibrationState.READY;
        type = CalibrationType.SHORT;
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
            oldCalibrationState = state;
            stage.show();
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

    public CalibrationState getState() {
        return state;
    }
    public CalibrationType getType() { return type; }

    public void setCalibrationState(CalibrationState state) {
        this.state = state;
        if (state.equals(CalibrationState.DONE)) {
            if (type.equals(CalibrationType.LOAD)) {
                AppMain.communicationService.leaveCalibration();
            } else {
                type = type.equals(CalibrationType.SHORT) ? CalibrationType.OPEN:CalibrationType.LOAD;
                this.state = CalibrationState.READY;
            }
        }
    }

    public CalibrationState getOldCalibrationState() { return oldCalibrationState; }
    public void setOldCalibrationState(CalibrationState oldCalibrationState) { this.oldCalibrationState = oldCalibrationState; }

    public Timer getCalibrationWatcher() { return calibrationWatcher; }
    public void setCalibrationWatcher(Timer calibrationWatcher) { this.calibrationWatcher = calibrationWatcher; }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setHighSpeed(boolean highSpeed) {
        isHighSpeed = highSpeed;
    }

    public void closeCalibration() {
        calibrationWatcher.cancel();
        stage.close();
    }

    public void showNotification(String content, NotificationType type) {
        notificationService.createNotification(content, type);
    }

    public boolean isCalibrated() {
        return state.equals(CalibrationState.DONE) && type.equals(CalibrationType.LOAD);
    }
    public boolean isCalibrationInProcess() {
        return !(type.equals(CalibrationType.SHORT) && state.equals(CalibrationState.READY)) && !isCalibrated();
    }

    public void runCalibration(String calibrationType, String from, String to, boolean isHighSpeed) {
        try {
            state = CalibrationState.RUNNING;
            double fromFreq = Double.parseDouble(from);
            double toFreq = Double.parseDouble(to);
            if (toFreq < fromFreq) {
                AppMain.calibrationService.showNotification("End frequency cannot be smaller than starting frequency!", NotificationType.WARNING);
                state = CalibrationState.READY;
            } else {
                if (AppMain.debugMode) {
                    setCalibrationState(CalibrationState.DONE);
                } else {
                    AppMain.communicationService.runCalibration(CalibrationType.valueOf(calibrationType.toUpperCase()), fromFreq, toFreq, isHighSpeed);
                }
            }
        } catch(NumberFormatException e) {
            AppMain.calibrationService.showNotification("Could not parse inputs, please, check the formats! -> " + e.getMessage(), NotificationType.ERROR);
        } catch (RuntimeException e) {
            showNotification("upss! -> " + e.getMessage(), NotificationType.ERROR);
        }
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public boolean isHighSpeed() {
        return isHighSpeed;
    }
}

package com.app.service.calibration;

import com.app.service.AppMain;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import com.app.service.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Timer;



public class CalibrationService implements Window {
    private final String path;
    private CalibrationState state;
    private CalibrationType type;
    private NotificationService notificationService;
    private Stage stage;
    private String from;
    private String to;
    private boolean isHighSpeed;
    private Timer stateWatcher;
    private CalibrationState oldState;

    /**
     * Initializes calibration service
     *
     * @param controllerPath path of the view controller
     */
    public CalibrationService(String controllerPath) {
        path = controllerPath;
        state = CalibrationState.READY;
        type = CalibrationType.SHORT;
    }

    @Override
    public void open() {
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
            oldState = state;
            stage.show();
            AppMain.communicationService.toggleCalibrationMode();
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
    }

    @Override
    public void close() {
        if (isCalibrationInProcess()) {
            showNotification("Please, finish up whole calibration process.", NotificationType.ANNOUNCEMENT);
        } else {
            AppMain.communicationService.toggleCalibrationMode();
            stateWatcher.cancel();
            stage.close();
        }
    }

    /**
     * Current calibration state
     *
     * @return calibration state
     */
    public synchronized CalibrationState getState() {
        return state;
    }

    /**
     * Returns previous calibration state
     *
     * @return previous calibration state
     */
    public CalibrationState getOldState() { return oldState; }

    /**
     * Check if calibration has been done yet
     *
     * @return if calibration has been done
     */
    public boolean isCalibrated() {
        return state.equals(CalibrationState.DONE) && type.equals(CalibrationType.LOAD);
    }

    /**
     * Check if calibration is in process
     *
     * @return if calibration is in process
     */
    public boolean isCalibrationInProcess() {
        return state.equals(CalibrationState.RUNNING) || (!(type.equals(CalibrationType.SHORT) && state.equals(CalibrationState.READY)) && !isCalibrated());
    }

    /**
     * Current calibration type
     *
     * @return calibration type
     */
    public CalibrationType getType() { return type; }

    /**
     * Gets lower calibration boundary
     *
     * @return the lower boundary of calibration
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets upper calibration boundary
     *
     * @return the upper boundary of calibration
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets is high speed on
     *
     * @return is high speed on
     */
    public boolean isHighSpeed() {
        return isHighSpeed;
    }

    /**
     * Sets new calibration state
     *
     * @param state new calibration state
     */
    public synchronized void setState(CalibrationState state) {
        this.oldState = this.state;
        this.state = state;
        if (state.equals(CalibrationState.DONE)) {
            if (!type.equals(CalibrationType.LOAD)) {
                type = type.equals(CalibrationType.SHORT) ? CalibrationType.OPEN:CalibrationType.LOAD;
                this.oldState = this.state;
                this.state = CalibrationState.READY;
            }
        }
    }

    /**
     * Sets new calibration type
     *
     * @param type new calibration type
     */
    public void setType(CalibrationType type) {
        this.type = type;
    }

    /**
     * Sets previous calibration state
     *
     * @param oldState previous calibration state
     */
    public void setOldState(CalibrationState oldState) { this.oldState = oldState; }

    /**
     * Sets state watcher
     *
     * @param stateWatcher timer which watches state change
     */
    public void setStateWatcher(Timer stateWatcher) { this.stateWatcher = stateWatcher; }

    /**
     * Sets lower calibration boundary
     *
     * @param from lower boundary
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Sets upper calibration boundary
     *
     * @param to upper boundary
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Sets is high speed on
     *
     * @param highSpeed high speed switch
     */
    public void setHighSpeed(boolean highSpeed) {
        isHighSpeed = highSpeed;
    }

    /**
     * Notification visualizer for calibration window
     *
     * @param content Content to display with the messing
     * @param type Type of notification to display
     */
    public void showNotification(String content, NotificationType type) {
        notificationService.createNotification(content, type);
    }

    /**
     * Triggers calibration process
     *
     * @param calibrationType required calibration type
     * @param from lower boundary of calibration
     * @param to upper boundary of calibration
     * @param isHighSpeed is high speed on
     */
    public void runCalibration(String calibrationType, String from, String to, boolean isHighSpeed) {
        try {
            if (from.isEmpty() || to.isEmpty()) {
                throw new NumberFormatException("Empty input");
            }
            double fromFreq = Double.parseDouble(from);
            double toFreq = Double.parseDouble(to);
            if (toFreq < fromFreq) {
                AppMain.calibrationService.showNotification("End frequency cannot be smaller than starting frequency!", NotificationType.WARNING);
            } else {
                if (AppMain.debugMode) {
                    setState(CalibrationState.DONE);
                } else {
                    AppMain.communicationService.runCalibration(CalibrationType.valueOf(calibrationType.toUpperCase()), fromFreq, toFreq, isHighSpeed);
                }
            }
        } catch(NumberFormatException e) {
            AppMain.calibrationService.showNotification("Could not parse inputs, please, check the formats! -> " + e.getMessage(), NotificationType.ERROR);
        }
    }
}

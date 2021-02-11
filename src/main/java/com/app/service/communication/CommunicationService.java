package com.app.service.communication;

import com.app.machineCommunication.Connection;
import com.app.service.AppMain;
import com.app.service.calibration.CalibrationType;
import com.app.service.measurement.Measurement;
import com.app.service.notification.NotificationType;

import java.io.IOException;


public class CommunicationService {
    private final Connection connection;

    /**
     * Initializes calibration service
     */
    public CommunicationService() {
        connection = new Connection();
    }

    /**
     * @return state of connection
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * Triggers connection
     */
    public boolean connect() {
        boolean success = false;
        try {
            success = connection.connect();
        } catch (RuntimeException | IOException | InterruptedException e) {
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
        return success;
    }

    public void killCommunicator() {
        Process temp = connection.getCommunicator();
        if (temp != null) {
            temp.destroy();
        }
    }

    /**
     * Calls function to enter calibration mode in Connection
     */
    public void toggleCalibrationMode() {
        connection.toggleCalibrationMode();
    }

    /**
     * Notification if autoConnection was successful
     */
    public void autoConnect() {
        if (connect()) {
            AppMain.notificationService.createNotification("Connected successfully", NotificationType.SUCCESS);
        } else {
            AppMain.notificationService.createNotification("Failed to connect automatically", NotificationType.ANNOUNCEMENT);
        }
    }

    /**
     * Triggers measurement
     *
     * @param measurement measurement type (frequency/voltage)
     */
    public void runMeasurement(Measurement measurement) throws IOException, InterruptedException {
        connection.initMeasurement(measurement.getParameters().getDisplayYY().getX(), measurement, measurement.getParameters().getOther().isAutoSweep());
        if (measurement.getParameters().getOther().isAutoSweep()) {
            connection.startAutoMeasurement(measurement);
        }
    }

    /**
     * Triggers next-step in manual measurement
     *
     * @param measurement measurement type (frequency/voltage)
     */
    public void nextStep(Measurement measurement) throws IOException, InterruptedException {
        connection.stepMeasurement(measurement);
    }

    /**
     * Triggers chosen calibration
     *
     * @param calibrationType
     * @param from start range of calibration
     * @param to end range of calibration
     * @param isHighSpeed high-speed state
     */
    public void runCalibration(CalibrationType calibrationType, double from, double to, boolean isHighSpeed) {
        connection.calibrationHandler(calibrationType, from, to, isHighSpeed);
    }

    /**
     * Triggers measurement-abort
     * Throws notification
     */
    public void abortMeasurement() {
        connection.abortMeasurement();
        AppMain.notificationService.createNotification("Measurement aborted", NotificationType.ANNOUNCEMENT);
    }
}

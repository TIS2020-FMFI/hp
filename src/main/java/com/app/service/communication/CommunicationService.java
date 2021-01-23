package com.app.service.communication;

import com.app.machineCommunication.Connection;
import com.app.service.AppMain;
import com.app.service.calibration.CalibrationType;
import com.app.service.measurement.Measurement;
import com.app.service.notification.NotificationType;

import java.io.IOException;


public class CommunicationService {
    private final Connection connection;

    public CommunicationService() {
        connection = new Connection();
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public boolean connect() {
        return connection.connect();
    }

    public void killCommunicator() {
        Process temp = connection.getCommunicator();
        if (temp != null) {
            temp.destroy();
        }
    }

    public void runMeasurement(Measurement measurement) throws IOException {
        connection.initMeasurement(measurement.getParameters().getDisplayYY().getX());
        if (measurement.getParameters().getOther().isAutoSweep()) {
            connection.startAutoMeasurement(measurement);
        }
    }

    public void nextStep(Measurement measurement) throws IOException {
        connection.stepMeasurement(measurement);
    }

    public boolean runCalibration(CalibrationType calibrationType) throws IOException, InterruptedException {
        return connection.calibrationHandler(calibrationType);
    }

    public void abortMeasurement() {
        connection.abortMeasurement();
        AppMain.notificationService.createNotification("Measurement aborted", NotificationType.ANNOUNCEMENT);
    }
}

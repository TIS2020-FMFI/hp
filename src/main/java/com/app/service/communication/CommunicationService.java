package com.app.service.communication;


import com.app.machineCommunication.Connection;
import com.app.service.calibration.CalibrationType;

import java.io.IOException;

public class CommunicationService {
    Connection connection;

    public CommunicationService() throws IOException {
        connection = new Connection();
    }

    public boolean connect() throws Exception {
        try {
            return connection.connect();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void runMeasurement() throws IOException, InterruptedException {
        connection.measurement();
    }

    public boolean runCalibration(CalibrationType calibrationType) throws IOException {
        return connection.calibrationHandler(calibrationType);
    }
}

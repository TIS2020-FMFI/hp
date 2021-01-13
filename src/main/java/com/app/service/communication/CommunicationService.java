package com.app.service.communication;


import com.app.machineCommunication.Connection;
import com.app.service.calibration.CalibrationType;
import com.app.service.file.parameters.MeasuredQuantity;

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
        // TODO: frequency or voltage ?
        connection.measurement(MeasuredQuantity.FREQUENCY);
    }

    public boolean runCalibration(CalibrationType calibrationType) throws IOException, InterruptedException {
        return connection.calibrationHandler(calibrationType);
    }
}

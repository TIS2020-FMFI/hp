package com.app.service.communication;


import com.app.machineCommunication.Connection;

import java.io.IOException;

public class CommunicationService {
    Connection connection;

    public CommunicationService() throws IOException {
        connection = new Connection();
    }

    public boolean connect() throws Exception {
        return connection.connect();
    }

    public void runMeasurement() throws IOException, InterruptedException {
        connection.measurement();
    }
}

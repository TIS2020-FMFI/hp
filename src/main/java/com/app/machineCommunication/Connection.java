package com.app.machineCommunication;



import com.app.service.AppMain;
import com.app.service.calibration.CalibrationType;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.file.parameters.MeasuredQuantity;
import com.app.service.graph.GraphService;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Connection extends Thread{


    private boolean connected = false;
    boolean cmd = false;
    boolean calibrationMode = false;
    boolean manualSweep = false;
    private NotificationService notificationService;
    Process p;
    BufferedReader readEnd;
    BufferedWriter writeEnd;
    EnvironmentParameters environmentParameters;
    ArrayList<String> commands;

    public Connection() throws Exception {
        try {
            p = Runtime.getRuntime().exec("D:/hpctrl-main/src/Debug/hpctrl.exe -i"); // TODO: set to default within project
            readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        } catch (IOException e) {
            AppMain.notificationService.createNotification("hpctrl script missing, read help for more info", NotificationType.ERROR);
        }
        environmentParameters = new EnvironmentParameters();
        commands = new ArrayList<String>();
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainScreen.fxml"));


        VBox notificationContainer = (VBox) root.lookup("#notificationContainer");
        if (notificationContainer == null) {
            throw new Exception("Notification container not found in this window!");
        }
        notificationService = new NotificationService(notificationContainer);

    }

    public boolean connect() throws IOException, InterruptedException {
        if (connected){
            if(cmd)
                write(".");
            write("exit");
        }
        else{
            write("connect");
            writer();
        }
        //TODO: what happens if it doesn't connect?
        cmd = false;
        connected = !connected;

        return connected;
    }

    public void toggleCmdMode() throws IOException, InterruptedException {
        if (connected) {
            write("cmd");
            StringBuilder result = read();

            if (!result.toString().equals("!not ready, try again later (cmd)")) {
                cmd = !cmd;

            } else {notificationService.createNotification("CMD connection failed, try to press any button on machine", NotificationType.ERROR);}

        } else { notificationService.createNotification("Connection error", NotificationType.ERROR);}
    }

    public StringBuilder read() throws IOException {
        StringBuilder result = new StringBuilder();
        while (readEnd.ready()) {
            result.append((char) readEnd.read());
        }
        return result;

    }


    private void write(String text){
        commands.add(text);
    }

    public void writer() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!commands.isEmpty()){
                    try {
                        writeEnd.write(commands.get(0));
                        commands.remove(0);
                        writeEnd.newLine();
                        writeEnd.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, 0, 1000);

        }



    public void startMeasurement() throws IOException, InterruptedException {
       if (environmentParameters.getOther().isAutoSweep()) {
           write("s WU");
           write("c");
           StringBuilder result = new StringBuilder();
           while (readEnd.ready()) {
               char letter = (char) readEnd.read();
               if (letter == '\n') {
                   if (result.charAt(0) == 'N') {
                       write("n");
                       break;
                   } else {

                       // TODO: tu bude posli result
                       result = new StringBuilder();
                   }
               } else
                   result.append(letter);
           }
       }
       else {
           write("s SU");
           write("q 1");
           StringBuilder result = read();
          // TODO: Tu bude posli result
           // TODO: Kedy je koniec ? manualSweep = false
       }

    }

    public void measurement(MeasuredQuantity type) throws IOException, InterruptedException {
        if (connected) {
            if (!cmd)
                toggleCmdMode();
            if (cmd && !manualSweep) {
                // TODO:function for display functions

                highSpeed();
                if (type == MeasuredQuantity.FREQUENCY)
                    frequencySweep();
                if (type == MeasuredQuantity.VOLTAGE)
                    voltageSweep();
                if (!environmentParameters.getOther().isAutoSweep()) manualSweep=true;
                startMeasurement();
            }
        }
    }

    public void highSpeed() throws IOException, InterruptedException {
        if (environmentParameters.getOther().isHighSpeed())
            write("s H1");
        else
            write("s H0");
    }

    public void frequencySweep() throws IOException, InterruptedException {
        write("s TF" + environmentParameters.getFrequencySweep().getStart() + "EN");
        write("s PF" + environmentParameters.getFrequencySweep().getStop() + "EN");
        write("s SF" + environmentParameters.getFrequencySweep().getStep() + "EN");
        write("s FR" + environmentParameters.getFrequencySweep().getSpot() + "EN");
    }

    public void voltageSweep()  throws IOException, InterruptedException {
        write("s TB" + environmentParameters.getVoltageSweep().getStart() + "EN");
        write("s PB" + environmentParameters.getVoltageSweep().getStop() + "EN");
        write("s SB" + environmentParameters.getVoltageSweep().getStep() + "EN");
        write("s BI" + environmentParameters.getVoltageSweep().getSpot() + "EN");
    }
//TODO: calibration parameters
    public void openCalibration() throws IOException, InterruptedException {
        write("s A4");
        write("s CS");
        read(); // we don't need results for now
    }
    public void shortCalibration() throws IOException, InterruptedException {
        write("s A5");
        write("s CS");
        read(); // we don't need results for now
    }
    public void loadCalibration() throws IOException, InterruptedException {
        write("s A5");
        write("s CS");
        read(); // we don't need results for now
    }


    public boolean calibrationHandler(CalibrationType calibrationType) throws IOException, InterruptedException {
        if (connected) {
            if (!cmd) toggleCmdMode();
            if (cmd){
                if (!calibrationMode){
                    write("s C1");
                    write("s EL" + environmentParameters.getOther().getElectricalLength() + "EN");
                    highSpeed();
                    calibrationMode = !calibrationMode;
                    }
                if (calibrationMode){

                    switch (calibrationType) {
                        case OPEN:
                            openCalibration();
                            break;
                        case SHORT:
                            shortCalibration();
                            break;
                        case LOAD:
                            loadCalibration();
                            write("s C0");
                            calibrationMode = !calibrationMode;
                            break;
                    }
                }
            }
        } else {notificationService.createNotification("Connection error", NotificationType.ERROR);}

        return true;
    }



}


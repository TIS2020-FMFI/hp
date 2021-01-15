package com.app.machineCommunication;



import com.app.service.AppMain;
import com.app.service.calibration.CalibrationType;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.file.parameters.MeasuredQuantity;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import com.app.service.notification.NotificationService;
import com.app.service.notification.NotificationType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
            p = Runtime.getRuntime().exec("C:/s/hp/hpctrl.exe -i"); // TODO: set to default within project
            readEnd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            writeEnd = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        } catch (IOException e) {
            AppMain.notificationService.createNotification("hpctrl script missing, read help for more info", NotificationType.ERROR);
        }
        environmentParameters = AppMain.environmentParameters;
        commands = new ArrayList<>();
        Parent root = FXMLLoader.load(getClass().getResource("/views/mainScreen.fxml"));


        VBox notificationContainer = (VBox) root.lookup("#notificationContainer");
        if (notificationContainer == null) {
            throw new Exception("Notification container not found in this window!");
        }
        notificationService = new NotificationService(notificationContainer);

    }

    public boolean isConnected() {
        return connected;
    }



    public boolean connect() {
        connected = true; // TODO: remove this line and uncomment all lines below when testing with machine

//        if (connected){
//            if(cmd)
//                write(".");
//            write("exit");
//        }
//        else{
//            write("connect 19");
//            writer();
//        }
//        //TODO: what happens if it doesn't connect?
//        cmd = false;
//        connected = !connected;

        return connected;
    }

    public void toggleCmdMode() throws IOException {
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
                        System.out.println("sending '" + commands.get(0) + "'");
                        if (commands.size() > 0) {
                            writeEnd.write(commands.get(0));
                            commands.remove(0);
                            writeEnd.newLine();
                            writeEnd.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, 0, 200);

        }



    public void startMeasurement(Measurement data) throws IOException {
       if (environmentParameters.getOther().isAutoSweep()) {
           write("s WU");
           write("c");
           StringBuilder result = new StringBuilder();
           LocalDateTime readingStarted = LocalDateTime.now();
           LocalDateTime readingTimeouts = readingStarted.plus(20, ChronoUnit.SECONDS);
           while (LocalDateTime.now().compareTo(readingTimeouts) < 0) {
               if (!readEnd.ready())
               {
                   try {
                       Thread.sleep(1);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   continue;
               }
               char letter = (char) readEnd.read();
               if ((letter == '\n') && (result.length() > 1)) {
                   if (result.charAt(1) == 'N') {
                       write("n");
                       break;
                   } else {
                       data.addSingleValue(new SingleValue(result.toString()));
                       result = new StringBuilder();
                   }
               } else
                   result.append(letter);
           }
           if (LocalDateTime.now().compareTo(readingTimeouts) >= 0)
           {
               System.out.println("c cmd timeouted");
           }
       }
       else {
           write("s SU");
           write("q 1");
           StringBuilder result = read();
           data.addSingleValue(new SingleValue(result.toString()));
           // TODO: Kedy je koniec ? manualSweep = false
       }

    }

    public void measurement(MeasuredQuantity type) throws IOException {
        if (connected) {
            if (!cmd)
                toggleCmdMode();
            if (cmd && !manualSweep) {
                // TODO:function for display functions
                write("s A7");
                write("s B1");
                highSpeed();
                if (type == MeasuredQuantity.FREQUENCY)
                    frequencySweep();
                if (type == MeasuredQuantity.VOLTAGE)
                    voltageSweep();
                if (!environmentParameters.getOther().isAutoSweep()) manualSweep=true;
                startMeasurement(AppMain.graphService.getRunningGraph().getMeasurement());
            }
        }
    }

    public void highSpeed() {
        if (environmentParameters.getOther().isHighSpeed())
            write("s H1");
        else
            write("s H0");
    }

    public void frequencySweep() {
        write("s TF" + environmentParameters.getFrequencySweep().getStart() + "EN");
        write("s PF" + environmentParameters.getFrequencySweep().getStop() + "EN");
        write("s SF" + environmentParameters.getFrequencySweep().getStep() + "EN");
        write("s FR" + environmentParameters.getFrequencySweep().getSpot() + "EN");
    }

    public void voltageSweep()  {
        write("s TB" + environmentParameters.getVoltageSweep().getStart() + "EN");
        write("s PB" + environmentParameters.getVoltageSweep().getStop() + "EN");
        write("s SB" + environmentParameters.getVoltageSweep().getStep() + "EN");
        write("s BI" + environmentParameters.getVoltageSweep().getSpot() + "EN");
    }
//TODO: calibration parameters
    public void openCalibration() throws IOException {
        write("s A4");
        write("s CS");
        read(); // we don't need results for now
    }
    public void shortCalibration() throws IOException {
        write("s A5");
        write("s CS");
        read(); // we don't need results for now
    }
    public void loadCalibration() throws IOException {
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


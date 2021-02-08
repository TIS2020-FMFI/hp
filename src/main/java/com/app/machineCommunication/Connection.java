package com.app.machineCommunication;

import com.app.service.AppMain;
import com.app.service.calibration.CalibrationState;
import com.app.service.calibration.CalibrationType;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.file.parameters.MeasuredQuantity;
import com.app.service.file.parameters.SweepType;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.measurement.SingleValue;
import com.app.service.notification.NotificationType;
import com.app.service.utils.Utils;
import java.io.*;
import java.util.*;


public class Connection extends Thread {
    boolean cmd = false;
    private boolean connected = false;
    private boolean calibrationMode = false;
    private Process process;
    private BufferedReader readEnd;
    private BufferedWriter writeEnd;
    private final EnvironmentParameters environmentParameters;
    private final Vector<String> commands;
    private Timer timer;
    private double finalCalibrationFrequency;

    public Connection() {
        environmentParameters = AppMain.environmentParameters;
        commands = new Vector<>();
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean reconnect(){
        try {
            process = Runtime.getRuntime().exec("C:/s/hp/hpctrl.exe -i"); // TODO: set to default within project
            readEnd = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writeEnd = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            return true;
        } catch (IOException e) {
            AppMain.notificationService.createNotification("hpctrl.exe missing, read help for more info", NotificationType.ERROR);
        }
        return false;
    }

    public boolean connect() throws RuntimeException, IOException, InterruptedException {
        if (!AppMain.debugMode) {
            if (connected) {
                if (cmd) {
                    write(".");
                    cmd = false;
                }
                write("exit");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println(" sleeping interrupted");
                }
                timer.cancel();
                timer = null;
                process = null;

            } else {
                if (!reconnect()) return connected;
                write("connect 19");
                write("LOGON");
                write("cmd");
                if (timer == null) {
                    writer();
                }
                if (!checkConnection())
                    throw new RuntimeException("Auto-Connection failed, try it manually");
                cmd = true;
            }
        } else {
            cmd = true;
        }
        connected = !connected;
        return connected;
    }

    public boolean checkConnection() throws IOException, InterruptedException {
        write("a");
        StringBuilder result = read(false);
        return result.length() > 0;
    }

    public Process getCommunicator() {
        return process;
    }

    public void toggleCmdMode() {
        try {
            if (connected) {
                write("cmd");
                StringBuilder result = read(false);
                if (!result.toString().equals("!not ready, try again later (cmd)")) {
                    cmd = !cmd;
                } else {
                    AppMain.notificationService.createNotification("CMD connection failed, try to press any button on machine", NotificationType.ERROR);
                }
            } else {
                AppMain.notificationService.createNotification("Can not toggle cmd mode, machine not connected", NotificationType.ERROR);
            }
        } catch (IOException | InterruptedException e) {
            AppMain.notificationService.createNotification("Attempted to toggle cmd mode, but failed!", NotificationType.ERROR);
        }
    }

    private StringBuilder read(boolean isStepMeasurement) throws IOException, InterruptedException, NullPointerException {
        StringBuilder result = new StringBuilder();
        int count = 0;
        if (readEnd == null) {
            throw new NullPointerException("EndRead is null in debug mode, nothing to worry about :)");
        }
        while (result.length() == 0 || result.toString().charAt(result.length()-1) != '\n') {
            if (!readEnd.ready() && !isStepMeasurement) {
                System.out.println("readEnd not ready");
                Thread.sleep(500);
                count++;
                if (count > 12) break;
            } else {
                result.append((char) readEnd.read());
            }
        }
        if (result.length() > 1 && result.charAt(1) == 'U') {
            AppMain.calibrationService.setState(CalibrationState.REQUIRED);
        }
        System.out.println("reading '" + result.toString() + "'");
        return result;
    }

    private void write(String text) {
        commands.add(text);
    }

    public void writer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!commands.isEmpty()) {
                    try {
                        String temp = commands.remove(0);
                        System.out.println("sending '" + temp + "'");
                        writeEnd.write(temp);
                        writeEnd.newLine();
                        writeEnd.flush();
                    } catch (IOException e) {
                        AppMain.notificationService.createNotification("Problem with writer -> " + e.getMessage(), NotificationType.ERROR);
                    }
                }
            }
        }, 0, 10);
    }

    public void abortMeasurement() {
        write("s AB");
        System.out.println("aborting measurement");
    }

    public void startAutoMeasurement(Measurement measurement) {
        if (AppMain.debugMode) {
            System.out.println("-- Running auto sweep measurement --");
            new Thread(() -> {
                SingleValue value = generateRandomSingeValue(measurement.getData().size() + 2);
                measurement.addSingleValue(value);
                while (value != null && !measurement.getState().equals(MeasurementState.ABORTED)) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println("Thread sleep interrupted!");
                    }
                    value = generateRandomSingeValue(measurement.getData().size() + 2);
                    measurement.addSingleValue(value);
                }
            }).start();
        } else {
            write("s WU");
            try {
                while (readEnd.ready()) readEnd.read();
            } catch (Exception e) {}

            write("c");
            new Thread(() -> {
                boolean some_data_arrived = false;
                StringBuilder result = new StringBuilder();
                while (!List.of(MeasurementState.ABORTED, MeasurementState.FINISHED).contains(measurement.getState())) {
                    try {
                        if (!readEnd.ready()) {
                            sleep(1);
                            continue;
                        }
                        char letter = (char) readEnd.read();
                        if ((letter == '\n') && (result.length() > 2)) {
                            if ((result.charAt(1) != 'F') && (result.charAt(2) != 'F') && (result.charAt(1) != 'V') && (result.charAt(2) != 'V')){ //&& some_data_arrived) {
//                                if (result.charAt(2) == 'X') {
//                                    result = new StringBuilder();
//                                } else {
                                    System.out.println("result: " + result.toString());
                                    System.out.flush();
                                    write("n");
                                    measurement.addSingleValue(null);
                                    Thread.currentThread().interrupt();
                                    return;
//                                }
                            } else {
                                System.out.println("reading " + result.toString());
                                System.out.flush();
                                if (!result.toString().startsWith(" ULN 9.999")) {
                                    SingleValue newValue = new SingleValue(result.toString());
                                    measurement.addSingleValue(newValue);
                                    result = new StringBuilder();
//                                    if (AppMain.graphService.getRunningGraph().getMeasurement().getnewValue.getDisplayX())
                                } else {
                                    AppMain.notificationService.createNotification("Measurement in non-calibrated range", NotificationType.WARNING);
                                    write("n");
                                    measurement.addSingleValue(null);
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                                some_data_arrived = true;
                            }
                        } else
                            result.append(letter);
                    } catch (IOException | InterruptedException e) {
                        AppMain.notificationService.createNotification("Problem at autoRunMeasurement -> " + e.getMessage(), NotificationType.ERROR);
                    }
                }
            }).start();
        }
    }

    public void stepMeasurement(Measurement measurement) throws IOException, InterruptedException {
        if (AppMain.debugMode) {
            measurement.addSingleValue(generateRandomSingeValue(measurement.getData().size() + 2));
        } else {
            write("q SU");
            StringBuilder result = read(true);
            System.out.println("reading " + result.toString());
            if (result.charAt(1) != '9'){
                SingleValue next = new SingleValue(result.toString(),measurement);
                measurement.addSingleValue(next);
                if (Double.compare(next.getDisplayX(), (measurement.getParameters().getDisplayYY().getX().equals(MeasuredQuantity.FREQUENCY) ? measurement.getParameters().getFrequencySweep().getStop() : measurement.getParameters().getVoltageSweep().getStop())) >= 0) {
                    measurement.addSingleValue(null);
                }
            }
        }
    }

    public void initMeasurement(MeasuredQuantity type, Measurement measurement, boolean isAutoSweepOn) throws IOException, InterruptedException {
        if (connected) {
            if (!cmd) {
                toggleCmdMode();
            }
            displayFunctions();
            sweepType();
            highSpeed(environmentParameters.getActive().getOther().isHighSpeed());
            if (type == MeasuredQuantity.FREQUENCY) {
                frequencySweep();
            } else {
                voltageSweep();
            }
            if (!isAutoSweepOn) {
                write("a");
                StringBuilder result = read(true);
                System.out.println("reading " + result.toString());
                SingleValue next = new SingleValue(result.toString(),measurement);
                measurement.addSingleValue(next);
                if (Double.compare(next.getDisplayX(), (measurement.getParameters().getDisplayYY().getX().equals(MeasuredQuantity.FREQUENCY) ? measurement.getParameters().getFrequencySweep().getStop() : measurement.getParameters().getVoltageSweep().getStop())) >= 0) {
                    measurement.addSingleValue(null);
                }
            }
        }
    }

    public void highSpeed(boolean highspeed) {
        if (highspeed)
            write("s H1");
        else {
            write("s H0");
        }
    }

    public void sweepType() {
        if (environmentParameters.getActive().getOther().getSweepType() == SweepType.LINEAR)
            write("s G0");
        else
            write("s G1");
    }

    public void displayFunctions() {
        switch (environmentParameters.getActive().getDisplayYY().getA()) {
            case "L":
                write("s A7");
                break;
            case "C":
                write("s A8");
                break;
            case "|Z|":
                write("s A1");
                break;
            case "|Y|":
                write("s A2");
                break;
            case "|Γ|":
                write("s A3");
                break;
            case "Γx":
                write("s A6");
                break;
            case "G":
                write("s A5");
                break;
            case "R":
                write("s A4");
                break;
        }
        switch (environmentParameters.getActive().getDisplayYY().getB()) {
            case "R":
                write("s B1");
                break;
            case "G":
                write("s B2");
                break;
            case "D":
                write("s B3");
                break;
            case "Q":
                write("s B4");
                break;
            case "Θ(rad)":
                write("s B2");
                break;
            case "Θ(deg)":
                if (environmentParameters.getActive().getDisplayYY().getA().contains("|"))
                    write("B3"); //B4 ?
                else
                    write("s B1");
                break;
        }
    }


    public void frequencySweep() {
        write("s BI" + environmentParameters.getActive().getVoltageSweep().getSpot() + "EN");
        write("s TF" + environmentParameters.getActive().getFrequencySweep().getStart() + "EN");
        write("s PF" + environmentParameters.getActive().getFrequencySweep().getStop() + "EN");
        write("s SF" + environmentParameters.getActive().getFrequencySweep().getStep() + "EN");
        if (!environmentParameters.getActive().getOther().isAutoSweep())
            write("s FR" + environmentParameters.getActive().getFrequencySweep().getStart() + "EN");
    }

    public void voltageSweep() {
        write("s FR" + environmentParameters.getActive().getFrequencySweep().getSpot() + "EN");
        write("s TB" + environmentParameters.getActive().getVoltageSweep().getStart() + "EN");
        write("s PB" + environmentParameters.getActive().getVoltageSweep().getStop() + "EN");
        write("s SB" + environmentParameters.getActive().getVoltageSweep().getStep() + "EN");
        if (!environmentParameters.getActive().getOther().isAutoSweep())
            write("s BI" + environmentParameters.getActive().getVoltageSweep().getStart() + "EN");
    }

    public void calibrationReader() {
        new Thread(() -> {
            StringBuilder result = new StringBuilder();
            write("a");
            while (true) {
                try {
                    if (!readEnd.ready()) {
                        sleep(1);
                        continue;
                    }
                    char letter = (char) readEnd.read();
                    if ((letter == '\n') && (result.length() > 1)) {
                        try {
                            if (Double.parseDouble(Utils.lineSplitAndExtractNumbers(result.toString(),",")[0]) == finalCalibrationFrequency) {
                                AppMain.calibrationService.setState(CalibrationState.DONE);
                                Thread.currentThread().interrupt();
                                return;
                            }
                        } catch (NumberFormatException ignore) {}
                        System.out.println("reading cal" + result.toString());
                        result = new StringBuilder();
                        write("a");
                    } else {
                        result.append(letter);
                    }
                    System.out.println("sb:" + result);
                } catch (IOException | InterruptedException e) {
                    AppMain.notificationService.createNotification("Problem at calibration -> " + e.getMessage(), NotificationType.ERROR);
                }
            }
        }).start();
    }

    public void toggleCalibrationMode() {
        if (calibrationMode) {
            write("s C0");
        } else {
            write("s C1");
        }
        calibrationMode = !calibrationMode;
    }

    public void calibrationHandler(CalibrationType calibrationType, double from, double to, boolean isHighSpeed) {
        if (connected) {
            if (!cmd) toggleCmdMode();
            if (cmd) {
                if (!calibrationMode) toggleCalibrationMode();
                if (calibrationMode) {
                    AppMain.calibrationService.setState(CalibrationState.RUNNING);
                    finalCalibrationFrequency = to;
                    highSpeed(isHighSpeed);
                    write("s TF" + from + "EN");
                    write("s PF" + to + "EN");
                    switch (calibrationType) {
                        case SHORT:
                            write("s A4");
                            break;
                        case OPEN:
                            write("s A5");
                            break;
                        case LOAD:
                            write("s A6");
                            break;
                    }
                    write("s CS");
                    calibrationReader();
                }
            }
        } else {
            AppMain.notificationService.createNotification("Machine not connected!", NotificationType.ERROR);
        }
    }

    private SingleValue generateRandomSingeValue(double X) {
        if (new Random().nextInt(100) > 95) {
            return null;
        }
        return new SingleValue(Math.random() * 20 + 80, Math.random() * 20 + 40, X);
    }
}
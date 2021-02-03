package com.app.machineCommunication;


import com.app.service.AppMain;
import com.app.service.calibration.CalibrationService;
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
    private EnvironmentParameters environmentParameters;
    private Vector<String> commands;
    private Timer timer;

    public Connection() {
        try {
            process = Runtime.getRuntime().exec("C:/s/hp/hpctrl.exe -i"); // TODO: set to default within project
            readEnd = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writeEnd = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        } catch (IOException e) {
            AppMain.notificationService.createNotification("hpctrl.exe missing, read help for more info", NotificationType.ERROR);
        }
        environmentParameters = AppMain.environmentParameters;
        commands = new Vector<>();
    }

    public boolean isConnected() {
        return connected;
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

            } else if (process != null) {
                write("connect 19");
                write("cmd");
                if (timer == null) {
                    writer();
                }
                if (!checkConnection())
                    throw new RuntimeException("Auto-Connection failed, try it manually");
                cmd = true;
            } else {
                throw new RuntimeException("hpctrl.exe could not be lunched, read help for more info");
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

    private StringBuilder read(boolean isStepMeasurement) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        int count = 0;
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
            AppMain.calibrationService.setCalibrationState(CalibrationState.REQUIRED);
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
            write("c");
            new Thread(() -> {
                StringBuilder result = new StringBuilder();
                while (!List.of(MeasurementState.ABORTED, MeasurementState.FINISHED).contains(measurement.getState())) {
                    try {
                        if (!readEnd.ready()) {
                            sleep(1);
                            continue;
                        }
                        char letter = (char) readEnd.read();
                        if ((letter == '\n') && (result.length() > 1)) {
                            if (result.charAt(1) == 'N') {
                                write("n");
                                measurement.addSingleValue(null);
                                Thread.currentThread().interrupt();
                                return;
                            } else {
                                System.out.println("reading " + result.toString());
                                measurement.addSingleValue(new SingleValue(result.toString()));
                                result = new StringBuilder();
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
            SingleValue next = new SingleValue(result.toString(),measurement);
            measurement.addSingleValue(next);
            if (Double.compare(next.getDisplayX(), (measurement.getParameters().getDisplayYY().getX().equals(MeasuredQuantity.FREQUENCY) ? measurement.getParameters().getFrequencySweep().getStop() : measurement.getParameters().getVoltageSweep().getStop())) >= 0) {
                measurement.addSingleValue(null);
            }
        }
    }

    public void initMeasurement(MeasuredQuantity type) throws IOException, InterruptedException {
        if (connected) {
            if (!cmd) {
                toggleCmdMode();
            }
            displayFunctions();
            sweepType();
            highSpeed();
            if (type == MeasuredQuantity.FREQUENCY) {
                frequencySweep();
            } else {
                voltageSweep();
            }
        }
    }

    public void highSpeed() {
        if (environmentParameters.getActive().getOther().isHighSpeed())
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
            case "Z":
                write("s A1");
                break;
            case "Y":
                write("s A2");
                break;
            case "r":
                write("s A3");
                break;
            case "rx":
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
            case "0(rad)":
                write("s B2");
                break;
            case "0(deg)":
                if (environmentParameters.getActive().getDisplayYY().getA().contains("|"))
                    write("B3"); //B4 ?
                else
                    write("s B1");
                break;
        }
    }


    public void frequencySweep() {
        write("s TF" + environmentParameters.getActive().getFrequencySweep().getStart() + "EN");
        write("s PF" + environmentParameters.getActive().getFrequencySweep().getStop() + "EN");
        write("s SF" + environmentParameters.getActive().getFrequencySweep().getStep() + "EN");
        write("s FR" + environmentParameters.getActive().getFrequencySweep().getStart() + "EN");
    }

    public void voltageSweep() {
        write("s TB" + environmentParameters.getActive().getVoltageSweep().getStart() + "EN");
        write("s PB" + environmentParameters.getActive().getVoltageSweep().getStop() + "EN");
        write("s SB" + environmentParameters.getActive().getVoltageSweep().getStep() + "EN");
        write("s BI" + environmentParameters.getActive().getVoltageSweep().getStart() + "EN");
    }

    public void openCalibration() {
        write("s A4");
        write("s CS");
        calibrationReader();
    }

    public void shortCalibration() {
        write("s A5");
        write("s CS");
        calibrationReader();
    }

    public void loadCalibration() {
        write("s A6");
        write("s CS");
        calibrationReader();
    }

    public void calibrationReader() {
        AppMain.calibrationService.setCalibrationState(CalibrationState.RUNNING);
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
                            if (Double.parseDouble(Utils.lineSplitAndExtractNumbers(result.toString(),",")[0]) == 600) {
                                AppMain.calibrationService.setCalibrationState(CalibrationState.READY);
                                Thread.currentThread().interrupt();
                                return;
                            }
                        } catch (NumberFormatException ignore) {
                        }
                        System.out.println("reading cal" + result.toString());
                        result = new StringBuilder();
                        write("a");
                    } else {
                        result.append(letter);
                        // toto nie je velmi rozumne to parsovat za kazdym znakom
                        // nemozeme to parsovat az na konci riadku vzdy?

                    }
                    System.out.println("sb:" + result);

                } catch (IOException | InterruptedException e) {
                    AppMain.notificationService.createNotification("Problem at calibration -> " + e.getMessage(), NotificationType.ERROR);
                }
            }
        }).start();
    }

    public void leaveCalbration() {
        write("s C0");
        calibrationMode = !calibrationMode;
    }

    public boolean calibrationHandler(CalibrationType calibrationType) {
        if (connected) {
            if (!cmd) toggleCmdMode();
            if (cmd) {
                if (!calibrationMode) {
                    write("s C1");
                    highSpeed();
                    calibrationMode = !calibrationMode;
                }
                if (calibrationMode) {
                    switch (calibrationType) {
                        case OPEN:
                            openCalibration();
                            break;
                        case SHORT:
                            shortCalibration();
                            break;
                        case LOAD:
                            loadCalibration();
                            break;
                    }
                }
            }
        } else {
            AppMain.notificationService.createNotification("Machine not connected!", NotificationType.ERROR);
        }
        return true;
    }

    private SingleValue generateRandomSingeValue(double X) {
        if (new Random().nextInt(100) > 95) {
            return null;
        }
        return new SingleValue(Math.random() * 20 + 80, Math.random() * 20 + 80, X);
    }
}


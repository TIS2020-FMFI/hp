package com.app.service.file;

import com.app.persistent.JsonParser;
import com.app.service.AppMain;
import com.app.service.exceptions.WrongDataFormatException;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.measurement.SingleValue;
import com.app.service.notification.NotificationType;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.MissingFormatArgumentException;
import java.util.Timer;
import java.util.TimerTask;


public class FileService {
    private final String configPath;
    private String autoSavingDir;
    private String fileName;
    private boolean autoSave;
    private Timer timerAutoSave;
    private int autosavingInterval = 500;

    /**
     * Constructor sets the path to the parameters
     * and sets the user's current working directory as the path for auto save measurements.
     *
     * @param configPath
     */

    public FileService(String configPath) {
        this.configPath = configPath;
        LocalDate localDate = LocalDate.now();
        String dir = System.getProperty("user.dir");
        dir = dir.replaceAll("\\\\", "/");
        autoSavingDir = dir + "/" + localDate.getYear() + "/" + localDate.getMonthValue() + "/" + localDate.getDayOfMonth() + "/";
    }


    /**
     *
     * @return
     */

    public String getAutoSavingDir() {
        return autoSavingDir;
    }

    /**
     *
     * @return
     */

    public boolean isAutoSave() {
        return autoSave;
    }

    /**
     *
     * @param autoSave
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * Writes parameters to .json.
     * Sends notification if error occurs during saving.
     *
     * @return
     */
    public boolean saveConfig() {
        try {
            return JsonParser.saveEnvironmentParameters(configPath, AppMain.environmentParameters);
        } catch (IOException e) {
            AppMain.notificationService.createNotification("Failed to save config -> " + e.getMessage(), NotificationType.ERROR);
        }
        return false;
    }

    /**
     *
     * @return
     */

    public EnvironmentParameters loadConfig() {
        return JsonParser.readEnvironmentParameters(configPath);
    }

    /**
     * Saves a finished, saved or loaded measurement to .json along the selected path.
     * Sends notification if measurement state is different.
     *
     * @param measurement
     * @return
     */
    public boolean saveAsMeasurement(Measurement measurement) {
        if (measurement != null && (measurement.getState().equals(MeasurementState.LOADED) || measurement.getState().equals(MeasurementState.FINISHED) ||
                measurement.getState().equals(MeasurementState.SAVED))) {
            String path = chooseSavingDirectory();
            if (!path.equals("")) {
                fileName = setTimeAndDisplayToPath(measurement) + ".json";
                return JsonParser.writeNewMeasurement(path, fileName, measurement);
            }
        } else {
            AppMain.notificationService.createNotification("Only a completed measurement can be saved.", NotificationType.ERROR);
        }
        return false;
    }

    /**
     * Creates a measurement from the current time and values of the selected displays A, B, X.
     *
     * @param measurement
     * @return
     */

    public String setTimeAndDisplayToPath(Measurement measurement) {
        LocalTime localTime = LocalTime.now();
        return localTime.getHour() + "-" + localTime.getMinute() +
                "-" + measurement.getParameters().getDisplayYY().getA() + "-" +
                measurement.getParameters().getDisplayYY().getB() + "-" +
                measurement.getParameters().getDisplayYY().getX().toString();
    }

    /**
     * Saves finished or started measurement to .json.
     * Sends notification if measurement state is different.
     *
     * @param measurement
     * @return
     */

    public boolean autoSaveMeasurement(Measurement measurement) {
        if (MeasurementState.FINISHED.equals(measurement.getState()) || MeasurementState.STARTED.equals(measurement.getState())) {
            fileName = setTimeAndDisplayToPath(measurement) + ".json";
            return JsonParser.writeNewMeasurement(autoSavingDir, fileName, measurement);
        }
        AppMain.notificationService.createNotification("Only a completed measurement can be saved.", NotificationType.WARNING);
        return false;
    }

    /**
     * Loads measurement from .json.
     * Sends notification if error occurs during loading.
     *
     * @param path
     * @return
     * @throws MissingFormatArgumentException
     */

    public Measurement loadMeasurement(String path) throws MissingFormatArgumentException {
        try {
            return JsonParser.readMeasurement(path);
        } catch (WrongDataFormatException e) {
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
        return null;
    }


    /**
     * Creates a new path for auto saving measurement.
     *
     * @return
     */
    public String setNewAutoSaveDirectory() {
        String newAutoSavingDir = chooseSavingDirectory();
        if (!newAutoSavingDir.isEmpty()) {
            LocalDate localDate = LocalDate.now();
            newAutoSavingDir = newAutoSavingDir + "/" +
                    localDate.getYear() + "/" + localDate.getMonthValue() + "/" +
                    localDate.getDayOfMonth() + "/";
            autoSavingDir = newAutoSavingDir.replaceAll("\\\\", "/");
        }
        return autoSavingDir;
    }

    /**
     * Opens a window for choosing a path.
     *
     * @return
     */

    private String chooseSavingDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose directory to save the measurement");
        File dir = directoryChooser.showDialog(AppMain.ps);
        String newSavingDir = "";
        if (dir != null) {
            newSavingDir = dir.getAbsolutePath();
        }
        newSavingDir = newSavingDir.replaceAll("\\\\", "/");
        return newSavingDir;
    }

    /**
     * Writes measured values to .txt along the selected path.
     * Only finished, saved or loaded measurement can be used.
     * Sends notification if measurement state is different.
     *
     * @param measurement
     * @return
     */

    public boolean exportAs(Measurement measurement) {
        if (measurement != null && (MeasurementState.FINISHED.equals(measurement.getState()) ||
                MeasurementState.SAVED.equals(measurement.getState()) || MeasurementState.LOADED.equals(measurement.getState()))) {
            String path = chooseSavingDirectory();
            if (!path.equals("")) {
                path = path + "/" + setTimeAndDisplayToPath(measurement) + ".txt";
                try (FileWriter writer = new FileWriter(path, false)) {
                    String string = "";
                    if (measurement.getData().size() > 0) {
                        for (SingleValue singleValue : measurement.getData()) {
                            if (singleValue == null) break;
                            string = singleValue.getDisplayX() + " " + singleValue.getDisplayA() + " " + singleValue.getDisplayB() + "\n";
                            writer.write(string);
                        }
                        writer.flush();
                        return true;
                    }
                } catch (IOException ex) {
                    return false;
                }
            }
        } else {
            AppMain.notificationService.createNotification("Only a completed measurement can be exported.", NotificationType.ERROR);
        }
        return false;
    }

    /**
     * Creates a new .json file and writes there new intention values
     * until the end of the measurement.
     *
     * @param measurement
     */
    public void autoSaveDuringMeasurement(Measurement measurement) {
        String savedDir;
        if (autoSave) {
            autoSaveMeasurement(measurement);
            savedDir = autoSavingDir + fileName;
            String finalSavedDir = savedDir;
//            System.out.println("creating new timer");
            timerAutoSave = new Timer();
            timerAutoSave.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (measurement.getData().size() > measurement.getIndexOfTheValueToSave()) {
                        JsonParser.writeNewValues(finalSavedDir, measurement);
                        if (measurement.getData().elementAt(measurement.getData().size() - 1) == null) {
                            measurement.setState(MeasurementState.SAVED);
                            timerAutoSave.cancel();
                        }
                    }
                }
            }, 100, autosavingInterval);
        }
    }

    /**
     * Ends the auto save timer.
     *
     */
    public void cancelTimer() {
        timerAutoSave.cancel();
    }
}

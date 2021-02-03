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


public class FileService {
    private final String configPath;
    private String autoSavingDir;
    private boolean autoSave;
    private boolean stopSave = false;


    public FileService(String configPath) {
        this.configPath = configPath;
        LocalDate localDate = LocalDate.now();
        String dir = System.getProperty("user.dir");
        dir = dir.replaceAll("\\\\", "/");
        autoSavingDir = dir + "/" + localDate.getYear() + "/" + localDate.getMonthValue() + "/" + localDate.getDayOfMonth() + "/";
    }

    public String getAutoSavingDir() {
        return autoSavingDir;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean saveConfig() {
        try {
            return JsonParser.saveEnvironmentParameters(configPath, AppMain.environmentParameters);
        } catch (IOException e) {
            AppMain.notificationService.createNotification("Failed to save config -> " + e.getMessage(), NotificationType.ERROR);
        }
        return false;
    }

    public EnvironmentParameters loadConfig() {
        return JsonParser.readEnvironmentParameters(configPath);
    }

    public boolean saveAsMeasurement(Measurement measurement) {
        if (measurement != null && (measurement.getState().equals(MeasurementState.LOADED) || measurement.getState().equals(MeasurementState.FINISHED) ||
                measurement.getState().equals(MeasurementState.SAVED))) {
            String path = chooseSavingDirectory();
            if(!path.equals("")){
                path = setTimeAndDisplayToPath(path, measurement);
                path = path + ".json";
                return JsonParser.writeNewMeasurement(path, measurement);
            }else{
                AppMain.notificationService.createNotification("Save path was not select.", NotificationType.ERROR);
            }
        }else {
            AppMain.notificationService.createNotification("Only the completed measurement can be saved.", NotificationType.ERROR);
        }
        return false;
    }

    public String setTimeAndDisplayToPath(String path, Measurement measurement){
        LocalTime localTime = LocalTime.now();
        path = path + localTime.getHour() + "-" + localTime.getMinute() +
                "-" + measurement.getParameters().getDisplayYY().getA() + "-" +
                measurement.getParameters().getDisplayYY().getB() + "-" +
                measurement.getParameters().getDisplayYY().getX().toString();
        return path;
    }

    public boolean autoSaveMeasurement(Measurement measurement) {
        if (MeasurementState.FINISHED.equals(measurement.getState())) {
            autoSavingDir = setTimeAndDisplayToPath(autoSavingDir, measurement);
            autoSavingDir = autoSavingDir + ".json";
            return JsonParser.writeNewMeasurement(autoSavingDir, measurement);
        }
        AppMain.notificationService.createNotification("Only completed measurement can be saved.", NotificationType.WARNING);
        return false;
    }

    public Measurement loadMeasurement(String path) {
        try {
            return JsonParser.readMeasurement(path);
        } catch (WrongDataFormatException e) {
            AppMain.notificationService.createNotification(e.getMessage(), NotificationType.ERROR);
        }
        return null;
    }

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

    private String chooseSavingDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(AppMain.ps);
        String newSavingDir = "";
        if (dir != null) {
            newSavingDir = dir.getAbsolutePath();
        }
        newSavingDir = newSavingDir.replaceAll("\\\\", "/");
        return newSavingDir;
    }

    public boolean exportAs(Measurement measurement){
        if(measurement != null && (MeasurementState.FINISHED.equals(measurement.getState()) ||
                MeasurementState.SAVED.equals(measurement.getState()) || MeasurementState.LOADED.equals(measurement.getState()))) {
            String path = chooseSavingDirectory();
            if(!path.equals("")) {
                path = setTimeAndDisplayToPath(path, measurement) + ".txt";
                try (FileWriter writer = new FileWriter(path, false)) {
                    String string = "";
                    if (measurement.getData().size() > 0) {
                        for (SingleValue singleValue : measurement.getData()) {
                            if (singleValue == null) break;
                            string = singleValue.getDisplayA() + " " + singleValue.getDisplayB() + " " + singleValue.getDisplayX() + "\n";
                            writer.write(string);
                        }
                        writer.flush();
                        return true;
                    }
                }
                catch(IOException ex){
                    return false;
                }
            }else{
                AppMain.notificationService.createNotification("Export path was not select.", NotificationType.ERROR);
            }
        }else {
            AppMain.notificationService.createNotification("Only the completed measurement can be exported.", NotificationType.ERROR);
        }
        return false;
    }
}

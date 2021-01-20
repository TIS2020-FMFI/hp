package com.app.service.file;

import com.app.persistent.JsonParser;
import com.app.service.AppMain;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

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

    public void setAutoSavingDir(String autoSavingDir) {
        this.autoSavingDir = autoSavingDir.replaceAll("\\\\", "/");
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean saveConfig() throws IOException {
        return JsonParser.saveEnvironmentParameters(configPath, AppMain.environmentParameters);
    }

    public EnvironmentParameters loadConfig() {
        return JsonParser.readEnvironmentParameters(configPath);
    }

    public boolean saveAsMeasurement(Measurement measurement, String path){
        if(measurement != null) {
            if (measurement.getState().equals(MeasurementState.LOADED) || measurement.getState().equals(MeasurementState.FINISHED) ||
                    measurement.getState().equals(MeasurementState.SAVED)) {
                LocalTime localTime = LocalTime.now();
                path = path + localTime.getHour() + "." + localTime.getMinute() +
                        "-" + measurement.getParameters().getDisplayYY().getA() + "-" +
                        measurement.getParameters().getDisplayYY().getB() + "-" +
                        measurement.getParameters().getDisplayYY().getX().toString();
                return JsonParser.writeNewMeasurement(path, measurement);
            }
        }
        return false;
    }



    public Measurement loadMeasurement(String path){
        return JsonParser.readMeasurement(path);
    }

    public String chooseSavingDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(AppMain.ps);
        String newSavingDir = "";
        if (dir != null) {
            newSavingDir = dir.getAbsolutePath();
        }
        newSavingDir = newSavingDir.replaceAll("\\\\", "/");
        return newSavingDir;
    }
}

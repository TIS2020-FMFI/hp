package com.app.service.file;

import com.app.persistent.JsonParser;
import com.app.service.file.parameters.EnvironmentParameters;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;

import java.io.FileNotFoundException;
import java.time.LocalTime;

public class FileService {
    private final String configPath;
    private String autoSavingDir;
    private boolean autoSave;
    private Measurement measurement;


    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public FileService(String configPath) {
        this.configPath = configPath;
    }

    public String getAutoSavingDir() {
        return autoSavingDir;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSavingDir(String autoSavingDir) {
        this.autoSavingDir = autoSavingDir;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean saveConfig() throws FileNotFoundException {
        return JsonParser.writeParameters(configPath, measurement.getParameters());
    }

    public EnvironmentParameters loadConfig() throws Exception {
        return JsonParser.readParameters(configPath);
    }

    public boolean saveData(){
        if (measurement != null && (measurement.getState().equals(MeasurementState.STARTED) ||
                measurement.getState().equals(MeasurementState.FINISHED))){
            if(measurement.getIndexOfTheValueToSave() == 0 && measurement.getData().size() > 0){
                LocalTime localTime = LocalTime.now();
                autoSavingDir = autoSavingDir + localTime.getHour() + "." + localTime.getMinute() +
                        "-" + measurement.getParameters().getDisplayYY().getA() + "-" +
                        measurement.getParameters().getDisplayYY().getB() + "-" +
                        measurement.getParameters().getDisplayYY().getX().toString();
                return JsonParser.writeNewMeasurement(autoSavingDir,measurement);
            }
            else if(measurement.getIndexOfTheValueToSave() < measurement.getData().size()-1){
                return JsonParser.writeNewValues(autoSavingDir, measurement);
            }
        }
        return false;
    }

    public Measurement loadData(String path){
        return measurement;
    }
}

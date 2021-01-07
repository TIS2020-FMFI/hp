package com.app.service.file;

import com.app.persistent.JsonParser;
import com.app.service.file.parameters.EnvironmentParameters;

import java.io.FileNotFoundException;

public class FileService {
    private final String configPath;
    private String autoSavingDir;
    private boolean autoSave;
    private EnvironmentParameters environmentParameters;

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

    public EnvironmentParameters getEnvironmentParameters() {
        return environmentParameters;
    }

    public void setEnvironmentParameters(EnvironmentParameters environmentParameters) {
        this.environmentParameters = environmentParameters;
    }

    public boolean saveConfig() throws FileNotFoundException {
        return JsonParser.writeConfig(configPath, environmentParameters);
    }

    public EnvironmentParameters loadConfig() throws Exception {
        return JsonParser.readConfig(configPath);
    }
}

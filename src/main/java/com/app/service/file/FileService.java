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

    public boolean saveConfig() throws FileNotFoundException {
        return JsonParser.write(configPath);
    }

    public EnvironmentParameters loadConfig() throws Exception {
        return JsonParser.readConfig(configPath);
    }
}

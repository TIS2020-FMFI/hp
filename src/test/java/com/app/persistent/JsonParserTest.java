package com.app.persistent;

import com.app.service.file.parameters.*;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void writeTest() throws FileNotFoundException {
        EnvironmentParameters parameters = new EnvironmentParameters();

        parameters.setDisplayYY(new DisplayYY());
        parameters.setOther(new Other());
        parameters.setFrequencySweep(new FrequencySweep());
        parameters.setVoltageSweep(new VoltageSweep());

        JsonParser.write("config", parameters);
    }
}
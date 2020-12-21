package com.app.persistent;

import com.app.service.file.parameters.*;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static com.app.service.file.parameters.MeasuredQuantity.FREQUENCY;
import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void writeTestEmpty() throws FileNotFoundException {
        EnvironmentParameters parameters = new EnvironmentParameters();

        parameters.setDisplayYY(new DisplayYY());
        parameters.setOther(new Other());
        parameters.setFrequencySweep(new FrequencySweep());
        parameters.setVoltageSweep(new VoltageSweep());

        JsonParser.write("config", parameters);
    }

    @Test
    void writeTestWithValues() throws FileNotFoundException {
        EnvironmentParameters parameters = new EnvironmentParameters();

        DisplayYY displayYY = new DisplayYY();
        displayYY.setX(FREQUENCY);
        displayYY.setB("R");
        displayYY.setA("L");

        parameters.setDisplayYY(displayYY);

        Other other = new Other();
        other.setSweepType(SweepType.LOG);
        other.setHighSpeed(false);
        other.setAutoSweep(true);
        other.setCapacitance(0.0);
        other.setElectricalLength(1.0);

        parameters.setOther(other);

        FrequencySweep frequencySweep = new FrequencySweep();
        frequencySweep.setStart(100.);
        frequencySweep.setStop(600.);
        frequencySweep.setStep(20.);
        frequencySweep.setSpot(30.1);

        parameters.setFrequencySweep(frequencySweep);

        VoltageSweep voltageSweep = new VoltageSweep();
        voltageSweep.setStart(0.);
        voltageSweep.setStop(40.);
        voltageSweep.setStep(1.);
        voltageSweep.setSpot(-3.);
        parameters.setVoltageSweep(voltageSweep);

        JsonParser.write("config-with-val", parameters);
    }

}
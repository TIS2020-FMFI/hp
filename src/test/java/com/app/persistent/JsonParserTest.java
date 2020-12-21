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

        JsonParser.writeConfig("config", parameters);
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

        JsonParser.writeConfig("config-with-val", parameters);
    }

    @Test
    void readTestException() throws Exception {

        JsonParser.readConfig("empty");
    }

    @Test
    void readTest() throws Exception {

        EnvironmentParameters param = JsonParser.readConfig("config-with-val");

        assertEquals(FREQUENCY, param.getDisplayYY().getX());
        assertEquals("R", param.getDisplayYY().getB());
        assertEquals("L", param.getDisplayYY().getA());


        assertEquals(SweepType.LOG, param.getOther().getSweepType());
        assertFalse(param.getOther().isHighSpeed());
        assertTrue(param.getOther().isAutoSweep());
        assertEquals(0.0, param.getOther().getCapacitance());
        assertEquals(1.0, param.getOther().getElectricalLength());

        assertEquals(100., param.getFrequencySweep().getStart());
        assertEquals(600., param.getFrequencySweep().getStop());
        assertEquals(20., param.getFrequencySweep().getStep());
        assertEquals(30.1, param.getFrequencySweep().getSpot());

        assertEquals(0., param.getVoltageSweep().getStart());
        assertEquals(40., param.getVoltageSweep().getStop());
        assertEquals(1., param.getVoltageSweep().getStep());
        assertEquals(-3., param.getVoltageSweep().getSpot());


    }

}
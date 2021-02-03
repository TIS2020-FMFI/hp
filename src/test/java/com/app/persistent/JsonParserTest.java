package com.app.persistent;

import com.app.service.exceptions.WrongDataFormatException;
import com.app.service.file.FileService;
import com.app.service.file.parameters.*;
import com.app.service.graph.GraphType;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.SingleValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JsonParserTest {

    FileService fileService = new FileService("persistent/config.json");
    EnvironmentParameters ep = new EnvironmentParameters();

    @Test
    void saveEmptyConfig() throws IOException {
        Parameters paramsUpper = new Parameters();
        Parameters paramsLower = new Parameters();

        paramsUpper.setDisplayYY(new DisplayYY());
        paramsUpper.setFrequencySweep(new FrequencySweep());
        paramsUpper.setVoltageSweep(new VoltageSweep());
        paramsUpper.setOther(new Other());

        paramsLower.setDisplayYY(new DisplayYY());
        paramsLower.setFrequencySweep(new FrequencySweep());
        paramsLower.setVoltageSweep(new VoltageSweep());
        paramsLower.setOther(new Other());

        ep.setUpperGraphParameters(paramsUpper);
        ep.setLowerGraphParameters(paramsLower);
//        JsonParser.saveEnvironmentParameters(getClass().getResource("persistent/config2.json").getPath(), ep);

        JsonParser.saveEnvironmentParameters("config1.json", ep);
    }

    @Test
    void saveValidConfig() throws IOException {
        Parameters paramsUpper = new Parameters();

        DisplayYY displayYY = new DisplayYY();
        displayYY.setX(MeasuredQuantity.FREQUENCY);
        displayYY.setB("R");
        displayYY.setA("L");

        FrequencySweep frequencySweep = new FrequencySweep();
        frequencySweep.setStart(100.);
        frequencySweep.setStop(600.);
        frequencySweep.setStep(20.);
        frequencySweep.setSpot(30.1);

        VoltageSweep voltageSweep = new VoltageSweep();
        voltageSweep.setStart(0.);
        voltageSweep.setStop(40.);
        voltageSweep.setStep(1.);
        voltageSweep.setSpot(-3.);

        Other other = new Other();
        other.setSweepType(SweepType.LOG);
        other.setHighSpeed(false);
        other.setAutoSweep(true);
        other.setCapacitance(0.0);
        other.setElectricalLength(1.0);

        paramsUpper.setDisplayYY(displayYY);
        paramsUpper.setFrequencySweep(frequencySweep);
        paramsUpper.setVoltageSweep(voltageSweep);
        paramsUpper.setOther(other);

        ep.setUpperGraphParameters(paramsUpper);
        ep.setLowerGraphParameters(paramsUpper);
        JsonParser.saveEnvironmentParameters("config2.json", ep);
    }

    @Test
    void readTestException() {
        JsonParser.readEnvironmentParameters("empty.json");
    }

    @Test
    void readConfig() {
        EnvironmentParameters ep = fileService.loadConfig();

        assertEquals(MeasuredQuantity.FREQUENCY, ep.getByType(GraphType.UPPER).getDisplayYY().getX());
        assertEquals("L", ep.getByType(GraphType.UPPER).getDisplayYY().getA());
        assertEquals("R", ep.getByType(GraphType.UPPER).getDisplayYY().getB());

        assertEquals(SweepType.LINEAR, ep.getByType(GraphType.UPPER).getOther().getSweepType());
        assertFalse(ep.getByType(GraphType.UPPER).getOther().isHighSpeed());
        assertFalse(ep.getByType(GraphType.UPPER).getOther().isAutoSweep());
        assertEquals(0.0, ep.getByType(GraphType.UPPER).getOther().getCapacitance());
        assertEquals(3.0, ep.getByType(GraphType.UPPER).getOther().getElectricalLength());

        assertEquals(100., ep.getByType(GraphType.UPPER).getFrequencySweep().getStart());
        assertEquals(600., ep.getByType(GraphType.UPPER).getFrequencySweep().getStop());
        assertEquals(20., ep.getByType(GraphType.UPPER).getFrequencySweep().getStep());
        assertEquals(30.1, ep.getByType(GraphType.UPPER).getFrequencySweep().getSpot());

        assertEquals(0., ep.getByType(GraphType.UPPER).getVoltageSweep().getStart());
        assertEquals(40., ep.getByType(GraphType.UPPER).getVoltageSweep().getStop());
        assertEquals(1., ep.getByType(GraphType.UPPER).getVoltageSweep().getStep());
        assertEquals(-3., ep.getByType(GraphType.UPPER).getVoltageSweep().getSpot());
    }

    @Test
    void writeMeasurementData() {
        Parameters parameters = new Parameters();

        DisplayYY displayYY = new DisplayYY();
        displayYY.setX(MeasuredQuantity.FREQUENCY);
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

        Measurement measurement = new Measurement(parameters);
        measurement.updateComment("hello world");

        measurement.addSingleValue(new SingleValue(0.1,30.5,8.06666));
        measurement.addSingleValue(new SingleValue(1,0.5,111111.8));

        System.out.println(measurement.getIndexOfTheValueToSave());
        JsonParser.writeNewMeasurement("hello","/measurementData.json", measurement);
        System.out.println(measurement.getIndexOfTheValueToSave());
    }

    @Test
    void saveMeasurement() {
        Parameters parameters = new Parameters();

        DisplayYY displayYY = new DisplayYY();
        displayYY.setX(MeasuredQuantity.FREQUENCY);
        displayYY.setB("R");
        displayYY.setA("L");
        parameters.setDisplayYY(displayYY);

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

        Other other = new Other();
        other.setSweepType(SweepType.LOG);
        other.setHighSpeed(false);
        other.setAutoSweep(true);
        other.setCapacitance(0.0);
        other.setElectricalLength(1.0);
        parameters.setOther(other);

        Measurement measurement = new Measurement(parameters);
        measurement.updateComment("hello world");

        measurement.addSingleValue(new SingleValue(0.1,30.5,8.66));
        measurement.addSingleValue(new SingleValue(1,0.5,18.8));

        JsonParser.writeNewMeasurement("new/" ,"measurementNewData.json", measurement);

        measurement.setIndexOfTheValueToSave(2);
        measurement.addSingleValue(new SingleValue(5.0,5.0,28));
        measurement.addSingleValue(new SingleValue(155,15,38));

        JsonParser.writeNewValues("new/measurementNewData.json", measurement);
    }

    @Test
    void readData() throws WrongDataFormatException {
        Measurement measurement = JsonParser.readMeasurement("measurementNewData.json");

        assertEquals("hello world", measurement.getComment().toString());

        Vector<SingleValue> data = measurement.getData();

        assertEquals(0.1, data.get(0).getDisplayA());
        assertEquals(15, data.get(2).getDisplayB());
    }
}
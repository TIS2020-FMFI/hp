package com.app.persistent;

import com.app.service.file.parameters.*;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.measurement.SingleValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class JsonParser {

    public static boolean writeParameters(String fileName, EnvironmentParameters environmentParameters) throws FileNotFoundException {

        environmentParameters.checkAll();

        // creating JSONObject
        JSONObject jo = new JSONObject();

        // putting data to JSONObject
        jo.put("displayA", environmentParameters.getDisplayYY().getA());
        jo.put("displayB", environmentParameters.getDisplayYY().getB());
        jo.put("displayX", environmentParameters.getDisplayYY().getX().toString());

        // for frequency data, create HashMap
        Map<String, java.io.Serializable> m = new HashMap<>(4);
        m.put("start", environmentParameters.getFrequencySweep().getStart());
        m.put("stop", environmentParameters.getFrequencySweep().getStop());
        m.put("step", environmentParameters.getFrequencySweep().getStep());
        m.put("spot", environmentParameters.getFrequencySweep().getSpot());

        // putting frequency to JSONObject
        jo.put("frequency", m);

        // for voltage data, create HashMap
        m = new HashMap<>(4);
        m.put("start", environmentParameters.getVoltageSweep().getStart());
        m.put("stop", environmentParameters.getVoltageSweep().getStop());
        m.put("step", environmentParameters.getVoltageSweep().getStep());
        m.put("spot", environmentParameters.getVoltageSweep().getSpot());

        // putting voltage to JSONObject
        jo.put("voltage", m);

        // for other data, create HashMap
        m = new HashMap<>(5);
        m.put("electricalLength", environmentParameters.getOther().getElectricalLength());
        m.put("capacitance", environmentParameters.getOther().getCapacitance());
        m.put("sweepType", environmentParameters.getOther().getSweepType().toString());
        m.put("highSpeed", environmentParameters.getOther().isHighSpeed());
        m.put("autoSweep", environmentParameters.getOther().isAutoSweep());


        // putting other to JSONObject
        jo.put("other", m);


        // writing JSON to file: "filename.json" in cwd
        PrintWriter pw = new PrintWriter(fileName);
        pw.write(jo.toJSONString());

        pw.flush();
        pw.close();

        return true;
    }

    public static EnvironmentParameters readParameters(String fileName) throws Exception {
        try {
            Object obj = new JSONParser().parse(new FileReader(fileName));

            EnvironmentParameters environmentParameters = new EnvironmentParameters();

            JSONObject jo = (JSONObject) obj;
            DisplayYY displayYY = new DisplayYY();
            displayYY.setA((String) jo.get("displayA"));
            displayYY.setB((String) jo.get("displayB"));
            String tempX = (String) jo.get("displayX");
            displayYY.setX(tempX != null ? MeasuredQuantity.valueOf(tempX) : null);
            environmentParameters.setDisplayYY(displayYY);

            Map frequency = (HashMap) jo.get("frequency");
            FrequencySweep frequencySweep = new FrequencySweep();
            frequencySweep.setStart((Double) frequency.get("start"));
            frequencySweep.setStop((Double) frequency.get("stop"));
            frequencySweep.setStep((Double) frequency.get("step"));
            frequencySweep.setSpot((Double) frequency.get("spot"));
            environmentParameters.setFrequencySweep(frequencySweep);

            Map voltage = (HashMap) jo.get("voltage");
            VoltageSweep voltageSweep = new VoltageSweep();
            voltageSweep.setStart((Double) voltage.get("start"));
            voltageSweep.setStop((Double) voltage.get("stop"));
            voltageSweep.setStep((Double) voltage.get("step"));
            voltageSweep.setSpot((Double) voltage.get("spot"));
            environmentParameters.setVoltageSweep(voltageSweep);

            Map o = (HashMap) jo.get("other");
            Other other = new Other();
            other.setCapacitance((Double) o.get("capacitance"));
            other.setElectricalLength((Double) o.get("electricalLength"));
            String tempSweepType = (String) o.get("sweepType");
            other.setSweepType(tempSweepType != null ? SweepType.valueOf(tempSweepType) : null);
            other.setHighSpeed((Boolean) o.get("highSpeed"));
            other.setAutoSweep((Boolean) o.get("autoSweep"));
            environmentParameters.setOther(other);
            environmentParameters.checkAll();
            return environmentParameters;
        }catch (Exception e){
            EnvironmentParameters parameters = new EnvironmentParameters();

            DisplayYY displayYY = new DisplayYY();
            displayYY.setX(MeasuredQuantity.FREQUENCY);
            displayYY.setB("R");
            displayYY.setA("L");

            parameters.setDisplayYY(displayYY);

            Other other = new Other();

            other.setSweepType(SweepType.LOG);
            other.setHighSpeed(true);
            other.setAutoSweep(true);
            other.setCapacitance(other.getMinCapacitance());
            other.setElectricalLength(other.getMinElectricalLength());

            parameters.setOther(other);

            FrequencySweep frequencySweep = new FrequencySweep();
            frequencySweep.setStart(frequencySweep.getMinStart());
            frequencySweep.setStop(frequencySweep.getMinStop());
            frequencySweep.setStep(frequencySweep.getMinStep());
            frequencySweep.setSpot(frequencySweep.getMinSpot());

            parameters.setFrequencySweep(frequencySweep);

            VoltageSweep voltageSweep = new VoltageSweep();
            voltageSweep.setStart(voltageSweep.getMinStart());
            voltageSweep.setStop(voltageSweep.getMinStop());
            voltageSweep.setStep(voltageSweep.getMinStep());
            voltageSweep.setSpot(voltageSweep.getMinSpot());
            parameters.setVoltageSweep(voltageSweep);
            parameters.checkAll();

            return parameters;
        }

    }

    public static boolean writeNewMeasurement(String autoSavingDir, Measurement measurement) {
        try {
            writeParameters(autoSavingDir, measurement.getParameters());
            Object obj = new JSONParser().parse(new FileReader(autoSavingDir));
            JSONObject jo = (JSONObject) obj;

            if(measurement.getComment() != null){
                jo.put("comment", measurement.getComment().toString());
            }

            JSONArray jsonArray = new JSONArray();

            for(int i=0; i < measurement.getData().size(); i++){
                JSONObject singleValue = new JSONObject();
                SingleValue singleV = measurement.getData().get(i);
                singleValue.put("valueDisplayA", singleV.getDisplayA());
                singleValue.put("valueDisplayB", singleV.getDisplayB());
                singleValue.put("valueDisplayX", singleV.getDisplayX());

                jsonArray.add(singleValue);
            }
            measurement.setIndexOfTheValueToSave(jsonArray.size());

            jo.put("values", jsonArray);

            PrintWriter pw = new PrintWriter(autoSavingDir);
            pw.write(jo.toJSONString());

            pw.flush();
            pw.close();

            return true;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean writeNewValues(String autoSavingDir, Measurement measurement) {
        try {
            Object obj = new JSONParser().parse(new FileReader(autoSavingDir));
            JSONObject jo = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jo.get("values");

            int index = measurement.getIndexOfTheValueToSave();
            for(int i = index; i < measurement.getData().size(); i++){
                JSONObject singleValue = new JSONObject();
                SingleValue singleV = measurement.getData().get(i);
                singleValue.put("valueDisplayA", singleV.getDisplayA());
                singleValue.put("valueDisplayB", singleV.getDisplayB());
                singleValue.put("valueDisplayX", singleV.getDisplayX());

                jsonArray.add(singleValue);
            }
            measurement.setIndexOfTheValueToSave(jsonArray.size());

            jo.put("values", jsonArray);

            PrintWriter pw = new PrintWriter(autoSavingDir);

            pw.write(jo.toJSONString());

            pw.flush();
            pw.close();

            return true;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Measurement readMeasurement(String fileName){
        Measurement measurement = null;
        try {
            measurement = new Measurement(readParameters(fileName));

            Object obj = new JSONParser().parse(new FileReader(fileName));
            JSONObject jo = (JSONObject) obj;
            String comment = jo.get("comment").toString();
            if(comment != null) {
                measurement.updateComment(comment);
            }

            JSONArray jsonArray = (JSONArray) jo.get("values");


            for (Object o : jsonArray) {
                JSONObject value = (JSONObject) o;
                SingleValue singleValue = new SingleValue((Double) value.get("valueDisplayA"),
                        (Double) value.get("valueDisplayB"), (Double) value.get("valueDisplayX"));
                measurement.addSingleValue(singleValue);
            }

            measurement.setState(MeasurementState.SAVED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return measurement;
    }


}
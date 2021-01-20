package com.app.persistent;

import com.app.service.AppMain;
import com.app.service.file.parameters.*;
import com.app.service.graph.GraphType;
import com.app.service.measurement.Measurement;
import com.app.service.measurement.MeasurementState;
import com.app.service.measurement.SingleValue;
import com.app.service.notification.NotificationType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class JsonParser {

    public static boolean saveEnvironmentParameters(String fileName, EnvironmentParameters ep) throws IOException {
        ep.getByType(GraphType.UPPER).checkAll();
        ep.getByType(GraphType.LOWER).checkAll();

        JSONObject jo = new JSONObject();
        jo.put("upper", getParametersMap(ep.getByType(GraphType.UPPER)));
        jo.put("lower", getParametersMap(ep.getByType(GraphType.LOWER)));
        Files.write(Paths.get("persistent/" + fileName), jo.toJSONString().getBytes());
        return true;
    }

    private static Map<String, Object> getParametersMap(Parameters params) {
//        Parameters params = ep.getByType(type);
        Map<String, Object> temp = new HashMap<>();

        temp.put("displayA", params.getDisplayYY().getA());
        temp.put("displayB", params.getDisplayYY().getB());
        temp.put("displayX", params.getDisplayYY().getX().toString());

        Map<String, Object> frequency = new HashMap<>();
        frequency.put("start", params.getFrequencySweep().getStart());
        frequency.put("stop", params.getFrequencySweep().getStop());
        frequency.put("step", params.getFrequencySweep().getStep());
        frequency.put("spot", params.getFrequencySweep().getSpot());

        Map<String, Object> voltage = new HashMap<>();
        voltage.put("start", params.getVoltageSweep().getStart());
        voltage.put("stop", params.getVoltageSweep().getStop());
        voltage.put("step", params.getVoltageSweep().getStep());
        voltage.put("spot", params.getVoltageSweep().getSpot());

        Map<String, Object> other = new HashMap<>();
        other.put("electricalLength", params.getOther().getElectricalLength());
        other.put("capacitance", params.getOther().getCapacitance());
        other.put("sweepType", params.getOther().getSweepType().toString());
        other.put("highSpeed", params.getOther().isHighSpeed());
        other.put("autoSweep", params.getOther().isAutoSweep());

        temp.put("frequency", frequency);
        temp.put("voltage", voltage);
        temp.put("other", other);
        return temp;
    }

    public static EnvironmentParameters readEnvironmentParameters(String fileName) {
        EnvironmentParameters ep = new EnvironmentParameters();
        Parameters paramsUpper = new Parameters();
        Parameters paramsLower = new Parameters();

        try {
            Object obj = new JSONParser().parse(new FileReader(fileName));

            JSONObject jo = (JSONObject) obj;

            paramsUpper = readParameters((HashMap) jo.get("upper"));
            paramsLower = readParameters((HashMap) jo.get("lower"));

//            paramsUpper = readParameters(GraphType.UPPER, jo);
//            paramsLower = readParameters(GraphType.LOWER, jo);
            paramsUpper.checkAll();
            paramsLower.checkAll();
        } catch (IOException | ParseException e) {
            if (!AppMain.debugMode) {
                AppMain.notificationService.createNotification("Failed to load previous values, setting to default", NotificationType.ANNOUNCEMENT);
            }
            DisplayYY displayYY = new DisplayYY();
            FrequencySweep frequencySweep = new FrequencySweep();
            VoltageSweep voltageSweep = new VoltageSweep();
            Other other = new Other();

            displayYY.setX(MeasuredQuantity.FREQUENCY);
            displayYY.setB("R");
            displayYY.setA("L");

            frequencySweep.setStart(frequencySweep.getMinStart());
            frequencySweep.setStop(frequencySweep.getMinStop());
            frequencySweep.setStep(frequencySweep.getMinStep());
            frequencySweep.setSpot(frequencySweep.getMinSpot());

            voltageSweep.setStart(voltageSweep.getMinStart());
            voltageSweep.setStop(voltageSweep.getMinStop());
            voltageSweep.setStep(voltageSweep.getMinStep());
            voltageSweep.setSpot(voltageSweep.getMinSpot());

            other.setSweepType(SweepType.LOG);
            other.setHighSpeed(true);
            other.setAutoSweep(true);
            other.setCapacitance(other.getMinCapacitance());
            other.setElectricalLength(other.getMinElectricalLength());

            paramsUpper.setDisplayYY(displayYY);
            paramsUpper.setFrequencySweep(frequencySweep);
            paramsUpper.setVoltageSweep(voltageSweep);
            paramsUpper.setOther(other);
        }
        ep.setUpperGraphParameters(paramsUpper);
        ep.setLowerGraphParameters(paramsLower);
        ep.setActive(GraphType.UPPER);
        return ep;
    }

//    private static Parameters readParameters(GraphType type, JSONObject obj) {
    private static Parameters readParameters(Map<String, Object> graphParams) {
        Parameters params = new Parameters();

        DisplayYY displayYY = new DisplayYY();
        FrequencySweep frequencySweep = new FrequencySweep();
        VoltageSweep voltageSweep = new VoltageSweep();
        Other other = new Other();

        displayYY.setA((String) graphParams.get("displayA"));
        displayYY.setB((String) graphParams.get("displayB"));
        String tempX = (String) graphParams.get("displayX");
        displayYY.setX(tempX != null ? MeasuredQuantity.valueOf(tempX) : null);

        Map<String, Object> frequency = (HashMap) graphParams.get("frequency");
        frequencySweep.setStart((Double) frequency.get("start"));
        frequencySweep.setStop((Double) frequency.get("stop"));
        frequencySweep.setStep((Double) frequency.get("step"));
        frequencySweep.setSpot((Double) frequency.get("spot"));

        Map<String, Object> voltage = (HashMap) graphParams.get("voltage");
        voltageSweep.setStart((Double) voltage.get("start"));
        voltageSweep.setStop((Double) voltage.get("stop"));
        voltageSweep.setStep((Double) voltage.get("step"));
        voltageSweep.setSpot((Double) voltage.get("spot"));

        Map<String, Object> o = (HashMap) graphParams.get("other");
        other.setCapacitance((Double) o.get("capacitance"));
        other.setElectricalLength((Double) o.get("electricalLength"));
        String tempSweepType = (String) o.get("sweepType");
        other.setSweepType(tempSweepType != null ? SweepType.valueOf(tempSweepType) : null);
        other.setHighSpeed((Boolean) o.get("highSpeed"));
        other.setAutoSweep((Boolean) o.get("autoSweep"));

        params.setComment("manually added");
        params.setDisplayYY(displayYY);
        params.setFrequencySweep(frequencySweep);
        params.setVoltageSweep(voltageSweep);
        params.setOther(other);
        return params;
    }

    public static boolean writeNewMeasurement(String autoSavingDir, Measurement measurement) {
        try {
            Object obj = new JSONParser().parse(new FileReader(autoSavingDir));
            JSONObject jo = (JSONObject) obj;
            measurement.getParameters().checkAll();

            jo.put("parameters", getParametersMap(measurement.getParameters()));

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
        Measurement measurement = new Measurement(new Parameters());
        try {

            Object obj = new JSONParser().parse(new FileReader(fileName));
            JSONObject jo = (JSONObject) obj;

            Parameters parameters = readParameters((HashMap) jo.get("parameters"));
            parameters.checkAll();
            measurement.setParameters(parameters);

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

            measurement.setState(MeasurementState.LOADED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return measurement;
    }


}
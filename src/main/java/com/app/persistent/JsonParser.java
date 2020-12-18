package com.app.persistent;

import com.app.service.file.parameters.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonParser {

    public static boolean write(String fileName, EnvironmentParameters environmentParameters) throws FileNotFoundException {
        // creating JSONObject
        JSONObject jo = new JSONObject();

        // putting data to JSONObject
        jo.put("displayA", environmentParameters.getDisplayYY().getA());
        jo.put("displayB", environmentParameters.getDisplayYY().getB());
        jo.put("displayX", environmentParameters.getDisplayYY().getX()); // netreba tu zmenit typ do String?

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
        m.put("sweepType", environmentParameters.getOther().getSweepType());   //toString?
        m.put("highSpeed", environmentParameters.getOther().isHighSpeed());    // boolean dobre sa skonvertuje?
        m.put("autoSweep", environmentParameters.getOther().isAutoSweep());


        // putting other to JSONObject
        jo.put("other", m);


        // writing JSON to file: "filename.json" in cwd
        PrintWriter pw = new PrintWriter(fileName + ".json");
        pw.write(jo.toJSONString());

        pw.flush();
        pw.close();

        return true;
    }

    public static EnvironmentParameters readConfig(String fileName) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(fileName));

        EnvironmentParameters environmentParameters = new EnvironmentParameters();

        JSONObject jo = (JSONObject) obj;
        DisplayYY displayYY = new DisplayYY();
        displayYY.setA((String) jo.get("displayA"));
        displayYY.setB((String) jo.get("displayB"));
        String tempX = (String) jo.get("displayX");
        displayYY.setX(tempX != null ? MeasuredQuantity.valueOf(tempX):null);
        environmentParameters.setDisplayYY(displayYY);

        Map frequency = (HashMap)jo.get("frequency");
        FrequencySweep frequencySweep = new FrequencySweep();
        frequencySweep.setStart((Double)frequency.get("start"));
        frequencySweep.setStop((Double)frequency.get("stop"));
        frequencySweep.setStep((Double)frequency.get("step"));
        frequencySweep.setSpot((Double)frequency.get("spot"));
        environmentParameters.setFrequencySweep(frequencySweep);

        Map voltage = (HashMap)jo.get("voltage");
        VoltageSweep voltageSweep = new VoltageSweep();
        voltageSweep.setStart((Double)voltage.get("start"));
        voltageSweep.setStop((Double)voltage.get("stop"));
        voltageSweep.setStep((Double)voltage.get("step"));
        voltageSweep.setSpot((Double)voltage.get("spot"));
        environmentParameters.setVoltageSweep(voltageSweep);

        Map o = (HashMap)jo.get("other");
        Other other = new Other();
        other.setCapacitance((String) o.get("capacitance"));
        other.setElectricalLength((String) o.get("electricalLength"));
        String tempSweepType = (String) o.get("sweepType");
        other.setSweepType(tempSweepType != null ? SweepType.valueOf(tempSweepType):null);
        other.setHighSpeed((Boolean) o.get("highSpeed"));
        other.setAutoSweep((Boolean) o.get("autoSweep"));
        environmentParameters.setOther(other);

        // TODO: add reading saving dir + auto save

        // getting map
//        Map address = ((Map)jo.get("address"));
        // iterating map
//        Iterator<Map.Entry> itr1 = address.entrySet().iterator();
//        while (itr1.hasNext()) {
//            Map.Entry pair = itr1.next();
//            System.out.println(pair.getKey() + " : " + pair.getValue());
//        }

        // getting array
//        JSONArray ja = (JSONArray) jo.get("phoneNumbers");
        // iterating array
//        Iterator itr2 = ja.iterator();
//
//        while (itr2.hasNext()) {
//            itr1 = ((Map) itr2.next()).entrySet().iterator();
//            while (itr1.hasNext()) {
//                Map.Entry pair = itr1.next();
//                System.out.println(pair.getKey() + " : " + pair.getValue());
//            }
//        }
        return new EnvironmentParameters();
    }
}

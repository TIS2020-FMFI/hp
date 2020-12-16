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

    public static boolean write(String fileName) throws FileNotFoundException {
        // creating JSONObject
        JSONObject jo = new JSONObject();

        // putting data to JSONObject
        jo.put("firstName", "John");
        jo.put("lastName", "Smith");
        jo.put("age", 25);

        // for address data, first create LinkedHashMap
        Map<String, java.io.Serializable> m = new LinkedHashMap<>(4);
        m.put("streetAddress", "21 2nd Street");
        m.put("city", "New York");
        m.put("state", "NY");
        m.put("postalCode", 10021);

        // putting address to JSONObject
        jo.put("address", m);

        // for phone numbers, first create JSONArray
        JSONArray ja = new JSONArray();

        m = new LinkedHashMap<>(2);
        m.put("type", "home");
        m.put("number", "212 555-1234");

        // adding map to list
        ja.add(m);

        m = new LinkedHashMap<>(2);
        m.put("type", "fax");
        m.put("number", "212 555-1234");

        // adding map to list
        ja.add(m);

        // putting phoneNumbers to JSONObject
        jo.put("phoneNumbers", ja);

        // writing JSON to file:"JSONExample.json" in cwd
        PrintWriter pw = new PrintWriter("JSONExample.json");
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
        displayYY.setX(tempX != null ? MeasuredQuantity.valueOf((String) jo.get("displayX")):null);
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

package com.lowes;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

    public static void main(String[] args) {
        List<String> SS = new ArrayList<>();
        System.out.println(SS.iterator().next());


    }

    private static List<Map<String, String>> convertJson(JSONArray data) throws JSONException {
        List<Map<String, String>> converted = new ArrayList<>();

        data.iterator().forEachRemaining(obj -> {
            if (obj instanceof JSONObject) {
                Map<String, String> jsonMap = new HashMap<>();
                JSONObject json = (JSONObject) obj;
                json.keySet().iterator().forEachRemaining(key -> {
                    jsonMap.put((String) key, String.valueOf(json.get(key)));
                });
                converted.add(jsonMap);
            }
        });
        return converted;
    }
}

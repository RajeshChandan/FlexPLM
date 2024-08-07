package com.lowes.notification.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lcs.wc.util.LCSProperties;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import wt.log4j.LogR;
import wt.util.WTProperties;

import java.io.FileReader;
import java.io.FileWriter;

public class TriggerConfigUtil {
    private static final Logger logger = LogR.getLogger(TriggerConfigUtil.class.getName());
    String location = LCSProperties.get("com.lowes.wc.notification.outputFilePath", "/codebase/notificationTriggerConfig.json");

    public boolean updateConfig(String jsonInput) {
        logger.debug("TriggerConfigUtil.updateConfig() starts");
        boolean result = false;
        try {
            JSONParser parser = new JSONParser();
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            WTProperties wtproperties = WTProperties.getLocalProperties();
            String wtHome = wtproperties.getProperty("wt.home");
            String filePath = wtHome + location;

            JSONObject jsonInputObject = (JSONObject) parser.parse(jsonInput);

            JSONArray dataToBeUpdated = new JSONArray();

            JSONObject jsonfileConfigFile = (JSONObject) parser.parse(new FileReader(filePath));
            JSONArray jsonfileConfigFileArray = (JSONArray) jsonfileConfigFile.get("notificationObjects");
            boolean updateFound = false;
            for (Object o : jsonfileConfigFileArray) {
                JSONObject jsonObject = (JSONObject) o;
                //className": "com.lcs.wc.sample.LCSSample",
                //      "notificationType": "SampleResubmit",
                String oldClassName = (String) jsonObject.get("className");
                String newClassName = (String) jsonInputObject.get("className");
                String oldNotificationType = (String) jsonObject.get("notificationType");
                String newNotificationType = (String) jsonInputObject.get("notificationType");

                if (oldClassName.equals(newClassName) && oldNotificationType.equals(newNotificationType)) {
                    dataToBeUpdated.add(jsonInputObject);
                    updateFound = true;
                } else {
                    dataToBeUpdated.add(jsonObject);
                }
            }

            if (!updateFound) {
                dataToBeUpdated.add(jsonInputObject);
            }
            JSONObject tobeUpdatedObject = new JSONObject();
            tobeUpdatedObject.put("sendNotification", jsonfileConfigFile.get("sendNotification"));
            tobeUpdatedObject.put("notificationObjects", dataToBeUpdated);
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.write(gson.toJson(tobeUpdatedObject));
            fileWriter.flush();
            fileWriter.close();

            result = true;
            logger.debug("successfully updated TRIGGER CONFIG File");
        } catch (Exception e) {
            logger.error("", e);

        }
        logger.debug("TriggerConfigUtil.updateConfig() starts");
        return result;
    }


}

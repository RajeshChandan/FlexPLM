package com.lowes.notification.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.util.LCSProperties;
import com.lowes.notification.model.config.NotificationConfigs;
import com.lowes.notification.model.config.NotificationObject;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * To read the notificationTriggerConfig.json file from <wthome>/codebase
 */
public class NotificationCache {
    /**
     * To hide the public implicit one
     */
    private NotificationCache() {
    }
    private static final Logger LOGGER = LogR.getLogger(NotificationCache.class.getName());

    /**
     * read notificationTriggerConfig.json
     *
     * @return NotificationConfigs
     */
    public static NotificationConfigs readNotificationTriggerConfig() {

        LOGGER.debug("readNotificationTriggerConfig-  Start");

        NotificationConfigs jsonPayload = null;
        try {
            String location = LCSProperties.get("com.lowes.wc.notification.outputFilePath", "/codebase/notificationTriggerConfig.json");
            WTProperties wtproperties = WTProperties.getLocalProperties();
            String wtHome = wtproperties.getProperty("wt.home");
            String filePath = wtHome + location;
            JSONParser parser = new JSONParser();
            JSONObject jsonobject = (JSONObject) parser.parse(new FileReader(filePath));
            ObjectMapper objectMapper = new ObjectMapper();
            jsonPayload = objectMapper.readValue(jsonobject.toString(), NotificationConfigs.class);
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }
        LOGGER.debug("readNotificationTriggerConfig-  End");

        return jsonPayload;
    }

    /**
     * @param wtObj WTObject
     * @return List<NotificationObject>
     * @throws WTException WTException
     */
    public static List<NotificationObject> getNotificationObjectsForTriggeredObject(WTObject wtObj, NotificationConfigs notificationConfigs) throws WTException {
        String methodName = "getNotificationObjectsForTriggeredObject- ";
        LOGGER.debug(methodName, " {} start");

        List<NotificationObject> notificationObjectList = new ArrayList<>();
        List<NotificationObject> notificationObjects = notificationConfigs.getNotificationObjects();
        String className = wtObj.getClass().getName();
        //Return the matching object from JSON based on classname and flex type
        for (NotificationObject notifyObject : notificationObjects) {
            if (className.equalsIgnoreCase(notifyObject.getClassName()) && Boolean.TRUE.equals(checkFlexType(wtObj, notifyObject.getFlexType()))) {
                //To filter out Flex Objects based on Classname and Flex type provided in the JSON file
                notificationObjectList.add(notifyObject);
            }
        }
        LOGGER.debug(methodName, " {} End");
        return notificationObjectList;
    }

    /**
     * @param obj      WTObject
     * @param flextype String
     * @return Boolean
     * @throws WTException WTException
     */
    private static Boolean checkFlexType(WTObject obj, String flextype) throws WTException {
        String methodName = "checkFlexType- ";
        LOGGER.debug(methodName, " {} start");
        if (obj instanceof FlexTyped) {
            FlexTyped typed = (FlexTyped) obj;
            //Just the specific type display name to be added in JSON file if required, else can leave it ALL
            if ("ALL".equalsIgnoreCase(flextype)) {
                return true;
            } else {
                //Check if FlexType provided in JSON matched with the Plugin WTObject
                if (flextype.equalsIgnoreCase(typed.getFlexType().getTypeDisplayName())) {
                    return true;
                }
            }
        }
        LOGGER.debug(methodName, " {} End");
        return false;
    }
}

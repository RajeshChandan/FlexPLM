package com.lowes.notification.plugins;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowes.notification.model.config.NotificationConfigs;
import com.lowes.notification.model.config.NotificationObject;
import com.lowes.notification.service.NotificationCriteriaValidator;
import com.lowes.notification.util.NotificationCache;
import com.lowes.notification.util.NotificationConstants;
import com.lowes.util.queue.ProcessingQueueHelper;
import org.apache.logging.log4j.Logger;
import wt.fc.WTObject;
import wt.log4j.LogR;

import java.util.List;

/**
 * @author Jeyaganeshan R
 * SendNotifictaion.java
 * lowes.plugins.properties plugin entries:
 * Create: com.lcs.wc.foundation.LCSPluginManager.eventPlugin.2001=targetClass|wt.fc.WTObject^targetType|ALL^pluginClass|
 * com.lowes.notification.NotificationPlugin^pluginMethod|triggerNotification^event|POST_CREATE_PERSIST^priority|100
 * Update: com.lcs.wc.foundation.LCSPluginManager.eventPlugin.2002=targetClass|wt.fc.WTObject^targetType|ALL^pluginClass|
 * com.lowes.notification.NotificationPlugin^pluginMethod|triggerNotification^event|PRE_UPDATE_PERSIST^priority|100
 */
public class NotificationPlugin {

    private static final Logger LOGGER = LogR.getLogger(NotificationPlugin.class.getName());

    /**
     * To hide the public implicit one
     */
    private NotificationPlugin() {
    }

    /**
     * This method is used in plugin entry and sends notification via email to pre-determined users for certain objects based
     * on certain conditions defined under JSON file
     *
     * @param wtObj - tirggered object on object update event
     */
    public static void triggerNotification(WTObject wtObj) {

        String methodName = "triggerNotification- ";
        LOGGER.debug(methodName, " {} start");
        LOGGER.debug(methodName, "{} wtObj {} ", wtObj);
        try {
            NotificationConfigs notificationConfigs = NotificationCache.readNotificationTriggerConfig();
            //Checking if notifications are enabled
            if (Boolean.FALSE.equals(NotificationCache.readNotificationTriggerConfig().getSendNotification())) {
                LOGGER.debug("NOTIFICATION TRIGGER IS disabled, please enable it at notificationTriggerConfig.json");
                LOGGER.debug("NotificationPlugin.triggerNotification() Execution Completed");
                return;
            }

            //Get notificationObjectList from the JSON
            List<NotificationObject> notificationObjectList = NotificationCache.getNotificationObjectsForTriggeredObject(wtObj, notificationConfigs);
            LOGGER.info("matched trigger config rules found {}", notificationObjectList);
            WTObject prevWtobj = (WTObject) VersionHelper.getPreSavePersistable(wtObj);

            //JSON can have multiple entries for same flex object
            for (NotificationObject notifyObject : notificationObjectList) {

                //Check if notification criteria is met, if ned add entry to processing queue for farther processing
                boolean criteriaMatched = new NotificationCriteriaValidator().checkCriteria(wtObj, notifyObject, prevWtobj);
                if (criteriaMatched) {

                    Class[] argTypes = {String.class, String.class, String.class, String.class, String.class,};
                    Object[] args = {
                            notifyObject.getRoles(),
                            notifyObject.getNotificationType(),
                            notifyObject.getSubscriber(),
                            notifyObject.getRecipientType().value(),
                            FormatHelper.getObjectId(wtObj)
                    };

                    new ProcessingQueueHelper().addQueueEntry(NotificationConstants.NOTIFICATION_QUEUE_NAME, argTypes, args, "processQueueEntry", "com.lowes.notification.service.NotificationQueueProcessor");
                }
            }

        } catch (Exception e) {
            LOGGER.error("{} error- ", methodName, e);
        }
        LOGGER.debug("{} End", methodName);
    }
}

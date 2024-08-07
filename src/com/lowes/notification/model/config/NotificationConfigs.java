package com.lowes.notification.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;


/**
 * NotificationConfigs
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "sendNotification",
        "notificationObjects"
})
public class NotificationConfigs {

    /**
     * (Required)
     */
    @JsonProperty("sendNotification")
    private Boolean sendNotification;
    /**
     * (Required)
     */
    @JsonProperty("notificationObjects")
    private List<NotificationObject> notificationObjects;

    /**
     * (Required)
     */
    @JsonProperty("sendNotification")
    public Boolean getSendNotification() {
        return sendNotification;
    }

    /**
     * (Required)
     */
    @JsonProperty("sendNotification")
    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    /**
     * (Required)
     */
    @JsonProperty("notificationObjects")
    public List<NotificationObject> getNotificationObjects() {
        return notificationObjects;
    }

    /**
     * (Required)
     */
    @JsonProperty("notificationObjects")
    public void setNotificationObjects(List<NotificationObject> notificationObjects) {
        this.notificationObjects = notificationObjects;
    }

    @Override
    public String toString() {
        return "NotificationConfigs{" +
                "sendNotification=" + sendNotification +
                ", notificationObjects=" + notificationObjects +
                '}';
    }
}

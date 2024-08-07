package com.lowes.notification.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;


/**
 * notificationObject
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "className",
        "notificationType",
        "flexType",
        "validationType",
        "criteria",
        "roles",
        "recipientType",
        "subscriber"
})
public class NotificationObject {

    @JsonProperty("className")
    private String className;
    /**
     * (Required)
     */
    @JsonProperty("flexType")
    private String flexType;
    @JsonProperty("notificationType")
    private String notificationType;
    @JsonProperty("validationType")
    private ValidationType validationType;
    /**
     * (Required)
     */
    @JsonProperty("criteria")
    private List<Criteria> criteria;
    /**
     * (Required)
     */
    @JsonProperty("roles")
    private String roles;
    /**
     * (Required)
     */
    @JsonProperty("recipientType")
    private RecipientType recipientType;
    /**
     * (Required)
     */
    @JsonProperty("subscriber")
    private String subscriber;

    @JsonProperty("className")
    public String getClassName() {
        return className;
    }

    @JsonProperty("className")
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * (Required)
     */
    @JsonProperty("flexType")
    public String getFlexType() {
        return flexType;
    }

    /**
     * (Required)
     */
    @JsonProperty("flexType")
    public void setFlexType(String flexType) {
        this.flexType = flexType;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteria")
    public List<Criteria> getCriteria() {
        return criteria;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteria")
    public void setCriteria(List<Criteria> criteria) {
        this.criteria = criteria;
    }

    /**
     * (Required)
     */
    @JsonProperty("roles")
    public String getRoles() {
        return roles;
    }

    /**
     * (Required)
     */
    @JsonProperty("roles")
    public void setRoles(String roles) {
        this.roles = roles;
    }

    @JsonProperty("notificationType")
    public String getNotificationType() {
        return notificationType;
    }

    @JsonProperty("notificationType")
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    @JsonProperty("validationType")
    public ValidationType getValidationType() {
        return validationType;
    }

    @JsonProperty("validationType")
    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    @JsonProperty("recipientType")
    public RecipientType getRecipientType() {
        return recipientType;
    }

    @JsonProperty("recipientType")
    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    /**
     * (Required)
     */
    @JsonProperty("subscriber")
    public String getSubscriber() {
        return subscriber;
    }

    /**
     * (Required)
     */
    @JsonProperty("subscriber")
    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "NotificationObject{" +
                "className='" + className + '\'' +
                ", flexType='" + flexType + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", validationType=" + validationType +
                ", criteria=" + criteria +
                ", roles='" + roles + '\'' +
                ", recipientType=" + recipientType +
                ", subscriber='" + subscriber + '\'' +
                '}';
    }
}

package com.lowes.notification.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * This is used in the notificationTriggerConfig.json file and defines the CriteriaType
 */
public enum CriteriaType {
    /**
     * TRIGGERED_OBJECT :  To check the below criteria when the mentioned Flex Object is triggered via SSP
     * LINKED_OBJECT : To check the below criteria for a linked flex object
     * REFERENCED_OBJECT : To check the below criteria for a referenced flex object
     */
    TRIGGERED_OBJECT("TRIGGERED_OBJECT"),
    LINKED_OBJECT("LINKED_OBJECT"),
    REFERENCED_OBJECT("REFERENCED_OBJECT");
    private static final Map<String, CriteriaType> CONSTANTS = new HashMap<>();

    static {
        for (CriteriaType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    CriteriaType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CriteriaType fromValue(String value) {
        CriteriaType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}

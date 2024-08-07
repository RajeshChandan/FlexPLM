package com.lowes.notification.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * This is used in the notificationTriggerConfig.json file and defines the CriteriaSubType
 */
public enum CriteriaSubType {
    /**
     * EXACT_CONDITION :  Entries used here to be matched on the Flex Object
     * ANY_CHANGE : Entries used here to be checked if any change in value of the given attribute
     * FIXED_CHANGE : Entries used here to be checked if there is a fixed change in value of the given attribute from & to
     */
    EXACT_CONDITION("EXACT_CONDITION"),
    ANY_CHANGE("ANY_CHANGE"),
    FIXED_CHANGE("FIXED_CHANGE");
    private static final Map<String, CriteriaSubType> CONSTANTS = new HashMap<>();

    static {
        for (CriteriaSubType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    CriteriaSubType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CriteriaSubType fromValue(String value) {
        CriteriaSubType constant = CONSTANTS.get(value);
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

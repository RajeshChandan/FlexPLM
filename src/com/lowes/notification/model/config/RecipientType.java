package com.lowes.notification.model.config;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum RecipientType {

    TEAM_TEMPLATE("TEAM_TEMPLATE"),
    SYSTEM_ROLEUSER("SYSTEM_ROLEUSER");
    private static final Map<String, RecipientType> CONSTANTS = new HashMap<>();

    static {
        for (RecipientType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    RecipientType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RecipientType fromValue(String value) {
        RecipientType constant = CONSTANTS.get(value);
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

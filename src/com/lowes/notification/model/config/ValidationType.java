package com.lowes.notification.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ValidationType {

    AND("and"),
    OR("or");
    private static final Map<String, ValidationType> CONSTANTS = new HashMap<>();

    static {
        for (ValidationType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    ValidationType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ValidationType fromValue(String value) {
        ValidationType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "ValidationType{" +
                "value='" + value + '\'' +
                '}';
    }
}

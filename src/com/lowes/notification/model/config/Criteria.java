package com.lowes.notification.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * criteria
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "criteriaType",
        "criteriaSubType",
        "criteriaValue"

})
public class Criteria {

    /**
     * (Required)
     */
    @JsonProperty("criteriaType")
    private CriteriaType criteriaType;
    /**
     * (Required)
     */
    @JsonProperty("criteriaValue")
    private String criteriaValue;
    /**
     * (Required)
     */
    @JsonProperty("criteriaSubType")
    private CriteriaSubType criteriaSubType;

    /**
     * (Required)
     */

    public Criteria() {
        // Required for the trigger config JSON
    }

    /**
     * (Required)
     */
    @JsonProperty("criteriaType")
    public CriteriaType getCriteriaType() {
        return criteriaType;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteriaType")
    public void setCriteriaType(CriteriaType criteriaType) {
        this.criteriaType = criteriaType;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteriaValue")
    public String getCriteriaValue() {
        return criteriaValue;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteriaValue")
    public void setCriteriaValue(String criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteriaSubType")
    public CriteriaSubType getCriteriaSubType() {
        return criteriaSubType;
    }

    /**
     * (Required)
     */
    @JsonProperty("criteriaSubType")
    public void setCriteriaSubType(CriteriaSubType criteriaSubType) {
        this.criteriaSubType = criteriaSubType;
    }

    @Override
    public String toString() {
        return "Criteria{" +
                "criteriaType=" + criteriaType +
                ", criteriaValue='" + criteriaValue + '\'' +
                ", criteriaSubType=" + criteriaSubType +
                '}';
    }
}
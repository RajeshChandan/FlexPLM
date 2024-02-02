
package com.burberry.wc.integration.planningapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Plan
 * <p>
 * Schema for Plan Reporting Service
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "planning"
})
public class PlanningAPI implements Serializable
{

    @JsonProperty("planning")
    private List<Planning> planning = null;
    private final static long serialVersionUID = 6418018624809220527L;

    @JsonProperty("planning")
    public List<Planning> getPlanning() {
        return planning;
    }

    @JsonProperty("planning")
    public void setPlanning(List<Planning> planning) {
        this.planning = planning;
    }

}

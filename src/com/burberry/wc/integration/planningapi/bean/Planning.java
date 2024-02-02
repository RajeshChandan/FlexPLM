
package com.burberry.wc.integration.planningapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "planName",
    "planId",
    "previousPlan",
    "previousPlanId",
    "genPlaceholder",
    "planStatus",
    "planLastMod",
    "planDetails",
    "associatedSeasons"
})
public class Planning implements Serializable
{

    /**
     * Plan Name
     * 
     */
    @JsonProperty("planName")
    @JsonPropertyDescription("Plan Name")
    private String planName;
    /**
     * Unique-Plan Id
     * 
     */
    @JsonProperty("planId")
    @JsonPropertyDescription("Unique-Plan Id")
    private Integer planId;
    /**
     * Previous Plan
     * 
     */
    @JsonProperty("previousPlan")
    @JsonPropertyDescription("Previous Plan")
    private String previousPlan;
    /**
     * Unique-Previous Plan Id
     * 
     */
    @JsonProperty("previousPlanId")
    @JsonPropertyDescription("Unique-Previous Plan Id")
    private Integer previousPlanId;
    /**
     * Allow Generate Placeholders from Plan?
     * 
     */
    @JsonProperty("genPlaceholder")
    @JsonPropertyDescription("Allow Generate Placeholders from Plan?")
    private Boolean genPlaceholder;
    /**
     * Plan Status
     * 
     */
    @JsonProperty("planStatus")
    @JsonPropertyDescription("Plan Status")
    private String planStatus;
    /**
     * Plan Last Modified Time- format:date-time
     * 
     */
    @JsonProperty("planLastMod")
    @JsonPropertyDescription("Plan Last Modified Time- format:date-time")
    private Object planLastMod;
    @JsonProperty("planDetails")
    private List<PlanDetail> planDetails = null;
    @JsonProperty("associatedSeasons")
    private List<AssociatedSeason> associatedSeasons = null;
    private final static long serialVersionUID = -2501297883580681534L;

    /**
     * Plan Name
     * 
     */
    @JsonProperty("planName")
    public String getPlanName() {
        return planName;
    }

    /**
     * Plan Name
     * 
     */
    @JsonProperty("planName")
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    /**
     * Unique-Plan Id
     * 
     */
    @JsonProperty("planId")
    public Integer getPlanId() {
        return planId;
    }

    /**
     * Unique-Plan Id
     * 
     */
    @JsonProperty("planId")
    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    /**
     * Previous Plan
     * 
     */
    @JsonProperty("previousPlan")
    public String getPreviousPlan() {
        return previousPlan;
    }

    /**
     * Previous Plan
     * 
     */
    @JsonProperty("previousPlan")
    public void setPreviousPlan(String previousPlan) {
        this.previousPlan = previousPlan;
    }

    /**
     * Unique-Previous Plan Id
     * 
     */
    @JsonProperty("previousPlanId")
    public Integer getPreviousPlanId() {
        return previousPlanId;
    }

    /**
     * Unique-Previous Plan Id
     * 
     */
    @JsonProperty("previousPlanId")
    public void setPreviousPlanId(Integer previousPlanId) {
        this.previousPlanId = previousPlanId;
    }

    /**
     * Allow Generate Placeholders from Plan?
     * 
     */
    @JsonProperty("genPlaceholder")
    public Boolean getGenPlaceholder() {
        return genPlaceholder;
    }

    /**
     * Allow Generate Placeholders from Plan?
     * 
     */
    @JsonProperty("genPlaceholder")
    public void setGenPlaceholder(Boolean genPlaceholder) {
        this.genPlaceholder = genPlaceholder;
    }

    /**
     * Plan Status
     * 
     */
    @JsonProperty("planStatus")
    public String getPlanStatus() {
        return planStatus;
    }

    /**
     * Plan Status
     * 
     */
    @JsonProperty("planStatus")
    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    /**
     * Plan Last Modified Time- format:date-time
     * 
     */
    @JsonProperty("planLastMod")
    public Object getPlanLastMod() {
        return planLastMod;
    }

    /**
     * Plan Last Modified Time- format:date-time
     * 
     */
    @JsonProperty("planLastMod")
    public void setPlanLastMod(Object planLastMod) {
        this.planLastMod = planLastMod;
    }

    @JsonProperty("planDetails")
    public List<PlanDetail> getPlanDetails() {
        return planDetails;
    }

    @JsonProperty("planDetails")
    public void setPlanDetails(List<PlanDetail> planDetails) {
        this.planDetails = planDetails;
    }

    @JsonProperty("associatedSeasons")
    public List<AssociatedSeason> getAssociatedSeasons() {
        return associatedSeasons;
    }

    @JsonProperty("associatedSeasons")
    public void setAssociatedSeasons(List<AssociatedSeason> associatedSeasons) {
        this.associatedSeasons = associatedSeasons;
    }

}

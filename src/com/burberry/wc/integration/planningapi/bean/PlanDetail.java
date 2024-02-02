
package com.burberry.wc.integration.planningapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pdOperationCat",
    "pdPlanComments",
    "pdDivision",
    "pdDeptGroup",
    "pdProdType",
    "pdPlanGroup",
    "pdTargetMargin",
    "pdcolourCount",
    "pdProdCount",
    "pdFabricCount",
    "pdAge",
    "pdBranchId",
    "pdSortingNumber",
    "pdParentId",
    "pdUniqueId"
})
public class PlanDetail implements Serializable
{

    /**
     * Operational Category
     * 
     */
    @JsonProperty("pdOperationCat")
    @JsonPropertyDescription("Operational Category")
    private String pdOperationCat;
    /**
     * Plan Comments
     * 
     */
    @JsonProperty("pdPlanComments")
    @JsonPropertyDescription("Plan Comments")
    private String pdPlanComments;
    /**
     * Division
     * 
     */
    @JsonProperty("pdDivision")
    @JsonPropertyDescription("Division")
    private String pdDivision;
    /**
     * Dept/Group
     * 
     */
    @JsonProperty("pdDeptGroup")
    @JsonPropertyDescription("Dept/Group")
    private String pdDeptGroup;
    /**
     * Product Type
     * 
     */
    @JsonProperty("pdProdType")
    @JsonPropertyDescription("Product Type")
    private String pdProdType;
    /**
     * Plan Group
     * 
     */
    @JsonProperty("pdPlanGroup")
    @JsonPropertyDescription("Plan Group")
    private String pdPlanGroup;
    /**
     * Target GBP Notional Margin (%)
     * 
     */
    @JsonProperty("pdTargetMargin")
    @JsonPropertyDescription("Target GBP Notional Margin (%)")
    private Double pdTargetMargin;
    /**
     * Colourway Count
     * 
     */
    @JsonProperty("pdcolourCount")
    @JsonPropertyDescription("Colourway Count")
    private Integer pdcolourCount;
    /**
     * Product Count
     * 
     */
    @JsonProperty("pdProdCount")
    @JsonPropertyDescription("Product Count")
    private Integer pdProdCount;
    /**
     * Fabric Count
     * 
     */
    @JsonProperty("pdFabricCount")
    @JsonPropertyDescription("Fabric Count")
    private Integer pdFabricCount;
    /**
     * Age/Group
     * 
     */
    @JsonProperty("pdAge")
    @JsonPropertyDescription("Age/Group")
    private String pdAge;
    /**
     * Branch Id -System Field
     * 
     */
    @JsonProperty("pdBranchId")
    @JsonPropertyDescription("Branch Id -System Field")
    private String pdBranchId;
    /**
     * Sorting Number -System Field
     * 
     */
    @JsonProperty("pdSortingNumber")
    @JsonPropertyDescription("Sorting Number -System Field")
    private String pdSortingNumber;
    /**
     * Parent Id -System Field
     * 
     */
    @JsonProperty("pdParentId")
    @JsonPropertyDescription("Parent Id -System Field")
    private String pdParentId;
    /**
     * Unique Id -System Field
     * 
     */
    @JsonProperty("pdUniqueId")
    @JsonPropertyDescription("Unique Id -System Field")
    private String pdUniqueId;
    private final static long serialVersionUID = 6071484528648122599L;

    /**
     * Operational Category
     * 
     */
    @JsonProperty("pdOperationCat")
    public String getPdOperationCat() {
        return pdOperationCat;
    }

    /**
     * Operational Category
     * 
     */
    @JsonProperty("pdOperationCat")
    public void setPdOperationCat(String pdOperationCat) {
        this.pdOperationCat = pdOperationCat;
    }

    /**
     * Plan Comments
     * 
     */
    @JsonProperty("pdPlanComments")
    public String getPdPlanComments() {
        return pdPlanComments;
    }

    /**
     * Plan Comments
     * 
     */
    @JsonProperty("pdPlanComments")
    public void setPdPlanComments(String pdPlanComments) {
        this.pdPlanComments = pdPlanComments;
    }

    /**
     * Division
     * 
     */
    @JsonProperty("pdDivision")
    public String getPdDivision() {
        return pdDivision;
    }

    /**
     * Division
     * 
     */
    @JsonProperty("pdDivision")
    public void setPdDivision(String pdDivision) {
        this.pdDivision = pdDivision;
    }

    /**
     * Dept/Group
     * 
     */
    @JsonProperty("pdDeptGroup")
    public String getPdDeptGroup() {
        return pdDeptGroup;
    }

    /**
     * Dept/Group
     * 
     */
    @JsonProperty("pdDeptGroup")
    public void setPdDeptGroup(String pdDeptGroup) {
        this.pdDeptGroup = pdDeptGroup;
    }

    /**
     * Product Type
     * 
     */
    @JsonProperty("pdProdType")
    public String getPdProdType() {
        return pdProdType;
    }

    /**
     * Product Type
     * 
     */
    @JsonProperty("pdProdType")
    public void setPdProdType(String pdProdType) {
        this.pdProdType = pdProdType;
    }

    /**
     * Plan Group
     * 
     */
    @JsonProperty("pdPlanGroup")
    public String getPdPlanGroup() {
        return pdPlanGroup;
    }

    /**
     * Plan Group
     * 
     */
    @JsonProperty("pdPlanGroup")
    public void setPdPlanGroup(String pdPlanGroup) {
        this.pdPlanGroup = pdPlanGroup;
    }

    /**
     * Target GBP Notional Margin (%)
     * 
     */
    @JsonProperty("pdTargetMargin")
    public Double getPdTargetMargin() {
        return pdTargetMargin;
    }

    /**
     * Target GBP Notional Margin (%)
     * 
     */
    @JsonProperty("pdTargetMargin")
    public void setPdTargetMargin(Double pdTargetMargin) {
        this.pdTargetMargin = pdTargetMargin;
    }

    /**
     * Colourway Count
     * 
     */
    @JsonProperty("pdcolourCount")
    public Integer getPdcolourCount() {
        return pdcolourCount;
    }

    /**
     * Colourway Count
     * 
     */
    @JsonProperty("pdcolourCount")
    public void setPdcolourCount(Integer pdcolourCount) {
        this.pdcolourCount = pdcolourCount;
    }

    /**
     * Product Count
     * 
     */
    @JsonProperty("pdProdCount")
    public Integer getPdProdCount() {
        return pdProdCount;
    }

    /**
     * Product Count
     * 
     */
    @JsonProperty("pdProdCount")
    public void setPdProdCount(Integer pdProdCount) {
        this.pdProdCount = pdProdCount;
    }

    /**
     * Fabric Count
     * 
     */
    @JsonProperty("pdFabricCount")
    public Integer getPdFabricCount() {
        return pdFabricCount;
    }

    /**
     * Fabric Count
     * 
     */
    @JsonProperty("pdFabricCount")
    public void setPdFabricCount(Integer pdFabricCount) {
        this.pdFabricCount = pdFabricCount;
    }

    /**
     * Age/Group
     * 
     */
    @JsonProperty("pdAge")
    public String getPdAge() {
        return pdAge;
    }

    /**
     * Age/Group
     * 
     */
    @JsonProperty("pdAge")
    public void setPdAge(String pdAge) {
        this.pdAge = pdAge;
    }

    /**
     * Branch Id -System Field
     * 
     */
    @JsonProperty("pdBranchId")
    public String getPdBranchId() {
        return pdBranchId;
    }

    /**
     * Branch Id -System Field
     * 
     */
    @JsonProperty("pdBranchId")
    public void setPdBranchId(String pdBranchId) {
        this.pdBranchId = pdBranchId;
    }

    /**
     * Sorting Number -System Field
     * 
     */
    @JsonProperty("pdSortingNumber")
    public String getPdSortingNumber() {
        return pdSortingNumber;
    }

    /**
     * Sorting Number -System Field
     * 
     */
    @JsonProperty("pdSortingNumber")
    public void setPdSortingNumber(String pdSortingNumber) {
        this.pdSortingNumber = pdSortingNumber;
    }

    /**
     * Parent Id -System Field
     * 
     */
    @JsonProperty("pdParentId")
    public String getPdParentId() {
        return pdParentId;
    }

    /**
     * Parent Id -System Field
     * 
     */
    @JsonProperty("pdParentId")
    public void setPdParentId(String pdParentId) {
        this.pdParentId = pdParentId;
    }

    /**
     * Unique Id -System Field
     * 
     */
    @JsonProperty("pdUniqueId")
    public String getPdUniqueId() {
        return pdUniqueId;
    }

    /**
     * Unique Id -System Field
     * 
     */
    @JsonProperty("pdUniqueId")
    public void setPdUniqueId(String pdUniqueId) {
        this.pdUniqueId = pdUniqueId;
    }

}

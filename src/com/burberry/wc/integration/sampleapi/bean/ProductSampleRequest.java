
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sampleRequestNum",
    "sampleRequestName",
    "sampleDueDate",
    "product",
    "sampleRequestStatus",
    "sampleCreateBy",
    "sampleModBy",
    "sampleRequestComments",
    "seasonRequest",
    "sampleCost",
    "sampleCostCurr",
    "sampleType",
    "shipToDest",
    "generalComments",
    "testLab",
    "sampleOrderAmendComment",
    "sampleOrderAmendDate",
    "sampleOrderCost",
    "sampleRequestUniqId",
    "reqDate",
    "sampleCostStatus",
    "sampleOrderUnits",
    "sampleNonMeasuredSize",
    "prodSample"
})
public class ProductSampleRequest implements Serializable
{

    /**
     * Request Number
     * 
     */
    @JsonProperty("sampleRequestNum")
    @JsonPropertyDescription("Request Number")
    private Integer sampleRequestNum;
    /**
     * Request Name
     * 
     */
    @JsonProperty("sampleRequestName")
    @JsonPropertyDescription("Request Name")
    private String sampleRequestName;
    /**
     * Sample Due Date- format:Date-time
     * 
     */
    @JsonProperty("sampleDueDate")
    @JsonPropertyDescription("Sample Due Date- format:Date-time")
    private String sampleDueDate;
    @JsonProperty("product")
    private Product product;
    /**
     * Request Status
     * 
     */
    @JsonProperty("sampleRequestStatus")
    @JsonPropertyDescription("Request Status")
    private String sampleRequestStatus;
    /**
     * Created By
     * 
     */
    @JsonProperty("sampleCreateBy")
    @JsonPropertyDescription("Created By")
    private String sampleCreateBy;
    /**
     * Modified By
     * 
     */
    @JsonProperty("sampleModBy")
    @JsonPropertyDescription("Modified By")
    private String sampleModBy;
    /**
     * Request Comments
     * 
     */
    @JsonProperty("sampleRequestComments")
    @JsonPropertyDescription("Request Comments")
    private String sampleRequestComments;
    /**
     * Season Requested
     * 
     */
    @JsonProperty("seasonRequest")
    @JsonPropertyDescription("Season Requested")
    private String seasonRequest;
    /**
     * Sample Cost
     * 
     */
    @JsonProperty("sampleCost")
    @JsonPropertyDescription("Sample Cost")
    private Double sampleCost;
    /**
     * Sample Cost Currency
     * 
     */
    @JsonProperty("sampleCostCurr")
    @JsonPropertyDescription("Sample Cost Currency")
    private String sampleCostCurr;
    /**
     * Sample Type
     * 
     */
    @JsonProperty("sampleType")
    @JsonPropertyDescription("Sample Type")
    private String sampleType;
    /**
     * Ship to Destination
     * 
     */
    @JsonProperty("shipToDest")
    @JsonPropertyDescription("Ship to Destination")
    private String shipToDest;
    /**
     * General Comments
     * 
     */
    @JsonProperty("generalComments")
    @JsonPropertyDescription("General Comments")
    private String generalComments;
    /**
     * Testing Lab
     * 
     */
    @JsonProperty("testLab")
    @JsonPropertyDescription("Testing Lab")
    private String testLab;
    /**
     * Sample Order Amendment Comments
     * 
     */
    @JsonProperty("sampleOrderAmendComment")
    @JsonPropertyDescription("Sample Order Amendment Comments")
    private String sampleOrderAmendComment;
    /**
     * Sample Order Amendment Date- format: date-time
     * 
     */
    @JsonProperty("sampleOrderAmendDate")
    @JsonPropertyDescription("Sample Order Amendment Date- format: date-time")
    private String sampleOrderAmendDate;
    /**
     * Sample Order Cost
     * 
     */
    @JsonProperty("sampleOrderCost")
    @JsonPropertyDescription("Sample Order Cost")
    private Double sampleOrderCost;
    /**
     * Sample Request Unique Id
     * 
     */
    @JsonProperty("sampleRequestUniqId")
    @JsonPropertyDescription("Sample Request Unique Id")
    private Object sampleRequestUniqId;
    /**
     * Request Date- format:date-time
     * 
     */
    @JsonProperty("reqDate")
    @JsonPropertyDescription("Request Date- format:date-time")
    private String reqDate;
    /**
     * Sample Cost Status
     * 
     */
    @JsonProperty("sampleCostStatus")
    @JsonPropertyDescription("Sample Cost Status")
    private String sampleCostStatus;
    /**
     * Sample Order Units
     * 
     */
    @JsonProperty("sampleOrderUnits")
    @JsonPropertyDescription("Sample Order Units")
    private Double sampleOrderUnits;
    /**
     * Non Measured Sample Size
     * 
     */
    @JsonProperty("sampleNonMeasuredSize")
    @JsonPropertyDescription("Non Measured Sample Size")
    private String sampleNonMeasuredSize;
    @JsonProperty("prodSample")
    private List<ProdSample> prodSample = null;
    private final static long serialVersionUID = -6116492014580172164L;

    /**
     * Request Number
     * 
     */
    @JsonProperty("sampleRequestNum")
    public Integer getSampleRequestNum() {
        return sampleRequestNum;
    }

    /**
     * Request Number
     * 
     */
    @JsonProperty("sampleRequestNum")
    public void setSampleRequestNum(Integer sampleRequestNum) {
        this.sampleRequestNum = sampleRequestNum;
    }

    /**
     * Request Name
     * 
     */
    @JsonProperty("sampleRequestName")
    public String getSampleRequestName() {
        return sampleRequestName;
    }

    /**
     * Request Name
     * 
     */
    @JsonProperty("sampleRequestName")
    public void setSampleRequestName(String sampleRequestName) {
        this.sampleRequestName = sampleRequestName;
    }

    /**
     * Sample Due Date- format:Date-time
     * 
     */
    @JsonProperty("sampleDueDate")
    public String getSampleDueDate() {
        return sampleDueDate;
    }

    /**
     * Sample Due Date- format:Date-time
     * 
     */
    @JsonProperty("sampleDueDate")
    public void setSampleDueDate(String sampleDueDate) {
        this.sampleDueDate = sampleDueDate;
    }

    @JsonProperty("product")
    public Product getProduct() {
        return product;
    }

    @JsonProperty("product")
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Request Status
     * 
     */
    @JsonProperty("sampleRequestStatus")
    public String getSampleRequestStatus() {
        return sampleRequestStatus;
    }

    /**
     * Request Status
     * 
     */
    @JsonProperty("sampleRequestStatus")
    public void setSampleRequestStatus(String sampleRequestStatus) {
        this.sampleRequestStatus = sampleRequestStatus;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("sampleCreateBy")
    public String getSampleCreateBy() {
        return sampleCreateBy;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("sampleCreateBy")
    public void setSampleCreateBy(String sampleCreateBy) {
        this.sampleCreateBy = sampleCreateBy;
    }

    /**
     * Modified By
     * 
     */
    @JsonProperty("sampleModBy")
    public String getSampleModBy() {
        return sampleModBy;
    }

    /**
     * Modified By
     * 
     */
    @JsonProperty("sampleModBy")
    public void setSampleModBy(String sampleModBy) {
        this.sampleModBy = sampleModBy;
    }

    /**
     * Request Comments
     * 
     */
    @JsonProperty("sampleRequestComments")
    public String getSampleRequestComments() {
        return sampleRequestComments;
    }

    /**
     * Request Comments
     * 
     */
    @JsonProperty("sampleRequestComments")
    public void setSampleRequestComments(String sampleRequestComments) {
        this.sampleRequestComments = sampleRequestComments;
    }

    /**
     * Season Requested
     * 
     */
    @JsonProperty("seasonRequest")
    public String getSeasonRequest() {
        return seasonRequest;
    }

    /**
     * Season Requested
     * 
     */
    @JsonProperty("seasonRequest")
    public void setSeasonRequest(String seasonRequest) {
        this.seasonRequest = seasonRequest;
    }

    /**
     * Sample Cost
     * 
     */
    @JsonProperty("sampleCost")
    public Double getSampleCost() {
        return sampleCost;
    }

    /**
     * Sample Cost
     * 
     */
    @JsonProperty("sampleCost")
    public void setSampleCost(Double sampleCost) {
        this.sampleCost = sampleCost;
    }

    /**
     * Sample Cost Currency
     * 
     */
    @JsonProperty("sampleCostCurr")
    public String getSampleCostCurr() {
        return sampleCostCurr;
    }

    /**
     * Sample Cost Currency
     * 
     */
    @JsonProperty("sampleCostCurr")
    public void setSampleCostCurr(String sampleCostCurr) {
        this.sampleCostCurr = sampleCostCurr;
    }

    /**
     * Sample Type
     * 
     */
    @JsonProperty("sampleType")
    public String getSampleType() {
        return sampleType;
    }

    /**
     * Sample Type
     * 
     */
    @JsonProperty("sampleType")
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /**
     * Ship to Destination
     * 
     */
    @JsonProperty("shipToDest")
    public String getShipToDest() {
        return shipToDest;
    }

    /**
     * Ship to Destination
     * 
     */
    @JsonProperty("shipToDest")
    public void setShipToDest(String shipToDest) {
        this.shipToDest = shipToDest;
    }

    /**
     * General Comments
     * 
     */
    @JsonProperty("generalComments")
    public String getGeneralComments() {
        return generalComments;
    }

    /**
     * General Comments
     * 
     */
    @JsonProperty("generalComments")
    public void setGeneralComments(String generalComments) {
        this.generalComments = generalComments;
    }

    /**
     * Testing Lab
     * 
     */
    @JsonProperty("testLab")
    public String getTestLab() {
        return testLab;
    }

    /**
     * Testing Lab
     * 
     */
    @JsonProperty("testLab")
    public void setTestLab(String testLab) {
        this.testLab = testLab;
    }

    /**
     * Sample Order Amendment Comments
     * 
     */
    @JsonProperty("sampleOrderAmendComment")
    public String getSampleOrderAmendComment() {
        return sampleOrderAmendComment;
    }

    /**
     * Sample Order Amendment Comments
     * 
     */
    @JsonProperty("sampleOrderAmendComment")
    public void setSampleOrderAmendComment(String sampleOrderAmendComment) {
        this.sampleOrderAmendComment = sampleOrderAmendComment;
    }

    /**
     * Sample Order Amendment Date- format: date-time
     * 
     */
    @JsonProperty("sampleOrderAmendDate")
    public String getSampleOrderAmendDate() {
        return sampleOrderAmendDate;
    }

    /**
     * Sample Order Amendment Date- format: date-time
     * 
     */
    @JsonProperty("sampleOrderAmendDate")
    public void setSampleOrderAmendDate(String sampleOrderAmendDate) {
        this.sampleOrderAmendDate = sampleOrderAmendDate;
    }

    /**
     * Sample Order Cost
     * 
     */
    @JsonProperty("sampleOrderCost")
    public Double getSampleOrderCost() {
        return sampleOrderCost;
    }

    /**
     * Sample Order Cost
     * 
     */
    @JsonProperty("sampleOrderCost")
    public void setSampleOrderCost(Double sampleOrderCost) {
        this.sampleOrderCost = sampleOrderCost;
    }

    /**
     * Sample Request Unique Id
     * 
     */
    @JsonProperty("sampleRequestUniqId")
    public Object getSampleRequestUniqId() {
        return sampleRequestUniqId;
    }

    /**
     * Sample Request Unique Id
     * 
     */
    @JsonProperty("sampleRequestUniqId")
    public void setSampleRequestUniqId(Object sampleRequestUniqId) {
        this.sampleRequestUniqId = sampleRequestUniqId;
    }

    /**
     * Request Date- format:date-time
     * 
     */
    @JsonProperty("reqDate")
    public String getReqDate() {
        return reqDate;
    }

    /**
     * Request Date- format:date-time
     * 
     */
    @JsonProperty("reqDate")
    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    /**
     * Sample Cost Status
     * 
     */
    @JsonProperty("sampleCostStatus")
    public String getSampleCostStatus() {
        return sampleCostStatus;
    }

    /**
     * Sample Cost Status
     * 
     */
    @JsonProperty("sampleCostStatus")
    public void setSampleCostStatus(String sampleCostStatus) {
        this.sampleCostStatus = sampleCostStatus;
    }

    /**
     * Sample Order Units
     * 
     */
    @JsonProperty("sampleOrderUnits")
    public Double getSampleOrderUnits() {
        return sampleOrderUnits;
    }

    /**
     * Sample Order Units
     * 
     */
    @JsonProperty("sampleOrderUnits")
    public void setSampleOrderUnits(Double sampleOrderUnits) {
        this.sampleOrderUnits = sampleOrderUnits;
    }

    /**
     * Non Measured Sample Size
     * 
     */
    @JsonProperty("sampleNonMeasuredSize")
    public String getSampleNonMeasuredSize() {
        return sampleNonMeasuredSize;
    }

    /**
     * Non Measured Sample Size
     * 
     */
    @JsonProperty("sampleNonMeasuredSize")
    public void setSampleNonMeasuredSize(String sampleNonMeasuredSize) {
        this.sampleNonMeasuredSize = sampleNonMeasuredSize;
    }

    @JsonProperty("prodSample")
    public List<ProdSample> getProdSample() {
        return prodSample;
    }

    @JsonProperty("prodSample")
    public void setProdSample(List<ProdSample> prodSample) {
        this.prodSample = prodSample;
    }

}

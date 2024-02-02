
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sampleName",
    "sampleId",
    "estShipDate",
    "lcState",
    "rejectReason",
    "sampleComments",
    "sampleAddlComments",
    "sampleDevComments",
    "sampleEnggComments",
    "sampleDtReceived",
    "sampleColourway",
    "sampleCreatedOn",
    "sampleLastModified",
    "sampleStatus",
    "sampleEvalDate",
    "sampleDesc",
    "shipComments",
    "shipDate",
    "shipMethod",
    "shipStatus",
    "measureSet",
    "sampleSize",
    "trackNum",
    "trackURL",
    "prodDocuments"
})
public class ProdSample implements Serializable
{

    /**
     * Sample Name
     * 
     */
    @JsonProperty("sampleName")
    @JsonPropertyDescription("Sample Name")
    private String sampleName;
    /**
     * Sample Id
     * 
     */
    @JsonProperty("sampleId")
    @JsonPropertyDescription("Sample Id")
    private String sampleId;
    /**
     * Estimated Ship Date- format:date-time
     * 
     */
    @JsonProperty("estShipDate")
    @JsonPropertyDescription("Estimated Ship Date- format:date-time")
    private String estShipDate;
    /**
     * Lifecycle State
     * 
     */
    @JsonProperty("lcState")
    @JsonPropertyDescription("Lifecycle State")
    private String lcState;
    /**
     * Rejected - Reason
     * 
     */
    @JsonProperty("rejectReason")
    @JsonPropertyDescription("Rejected - Reason")
    private String rejectReason;
    /**
     * Sample Comments
     * 
     */
    @JsonProperty("sampleComments")
    @JsonPropertyDescription("Sample Comments")
    private String sampleComments;
    /**
     * Sample Additional Comments
     * 
     */
    @JsonProperty("sampleAddlComments")
    @JsonPropertyDescription("Sample Additional Comments")
    private String sampleAddlComments;
    /**
     * Sample Development Comments
     * 
     */
    @JsonProperty("sampleDevComments")
    @JsonPropertyDescription("Sample Development Comments")
    private String sampleDevComments;
    /**
     * Sample Engineering Comments
     * 
     */
    @JsonProperty("sampleEnggComments")
    @JsonPropertyDescription("Sample Engineering Comments")
    private String sampleEnggComments;
    /**
     * Date Received
     * 
     */
    @JsonProperty("sampleDtReceived")
    @JsonPropertyDescription("Date Received")
    private String sampleDtReceived;
    /**
     * Sample Colourway
     * 
     */
    @JsonProperty("sampleColourway")
    @JsonPropertyDescription("Sample Colourway")
    private String sampleColourway;
    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("sampleCreatedOn")
    @JsonPropertyDescription("Created on- format:date-time")
    private String sampleCreatedOn;
    /**
     * Last Modified- format:date-time
     * 
     */
    @JsonProperty("sampleLastModified")
    @JsonPropertyDescription("Last Modified- format:date-time")
    private String sampleLastModified;
    /**
     * Sample Status
     * 
     */
    @JsonProperty("sampleStatus")
    @JsonPropertyDescription("Sample Status")
    private String sampleStatus;
    /**
     * Sample Evaluation Date- format:date-time
     * 
     */
    @JsonProperty("sampleEvalDate")
    @JsonPropertyDescription("Sample Evaluation Date- format:date-time")
    private String sampleEvalDate;
    /**
     * Sample Description
     * 
     */
    @JsonProperty("sampleDesc")
    @JsonPropertyDescription("Sample Description")
    private String sampleDesc;
    /**
     * Shipping Comments
     * 
     */
    @JsonProperty("shipComments")
    @JsonPropertyDescription("Shipping Comments")
    private String shipComments;
    /**
     * Ship Date- format:date-time
     * 
     */
    @JsonProperty("shipDate")
    @JsonPropertyDescription("Ship Date- format:date-time")
    private String shipDate;
    /**
     * Shipping Method
     * 
     */
    @JsonProperty("shipMethod")
    @JsonPropertyDescription("Shipping Method")
    private String shipMethod;
    /**
     * Shipping Status
     * 
     */
    @JsonProperty("shipStatus")
    @JsonPropertyDescription("Shipping Status")
    private String shipStatus;
    /**
     * Measurement Set
     * 
     */
    @JsonProperty("measureSet")
    @JsonPropertyDescription("Measurement Set")
    private String measureSet;
    /**
     * Sample Size
     * 
     */
    @JsonProperty("sampleSize")
    @JsonPropertyDescription("Sample Size")
    private String sampleSize;
    /**
     * Tracking Number
     * 
     */
    @JsonProperty("trackNum")
    @JsonPropertyDescription("Tracking Number")
    private String trackNum;
    /**
     * Tracking URL
     * 
     */
    @JsonProperty("trackURL")
    @JsonPropertyDescription("Tracking URL")
    private String trackURL;
    @JsonProperty("prodDocuments")
    private List<ProdDocument> prodDocuments = null;
    private final static long serialVersionUID = -841068938098925112L;

    /**
     * Sample Name
     * 
     */
    @JsonProperty("sampleName")
    public String getSampleName() {
        return sampleName;
    }

    /**
     * Sample Name
     * 
     */
    @JsonProperty("sampleName")
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    /**
     * Sample Id
     * 
     */
    @JsonProperty("sampleId")
    public String getSampleId() {
        return sampleId;
    }

    /**
     * Sample Id
     * 
     */
    @JsonProperty("sampleId")
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * Estimated Ship Date- format:date-time
     * 
     */
    @JsonProperty("estShipDate")
    public String getEstShipDate() {
        return estShipDate;
    }

    /**
     * Estimated Ship Date- format:date-time
     * 
     */
    @JsonProperty("estShipDate")
    public void setEstShipDate(String estShipDate) {
        this.estShipDate = estShipDate;
    }

    /**
     * Lifecycle State
     * 
     */
    @JsonProperty("lcState")
    public String getLcState() {
        return lcState;
    }

    /**
     * Lifecycle State
     * 
     */
    @JsonProperty("lcState")
    public void setLcState(String lcState) {
        this.lcState = lcState;
    }

    /**
     * Rejected - Reason
     * 
     */
    @JsonProperty("rejectReason")
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * Rejected - Reason
     * 
     */
    @JsonProperty("rejectReason")
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    /**
     * Sample Comments
     * 
     */
    @JsonProperty("sampleComments")
    public String getSampleComments() {
        return sampleComments;
    }

    /**
     * Sample Comments
     * 
     */
    @JsonProperty("sampleComments")
    public void setSampleComments(String sampleComments) {
        this.sampleComments = sampleComments;
    }

    /**
     * Sample Additional Comments
     * 
     */
    @JsonProperty("sampleAddlComments")
    public String getSampleAddlComments() {
        return sampleAddlComments;
    }

    /**
     * Sample Additional Comments
     * 
     */
    @JsonProperty("sampleAddlComments")
    public void setSampleAddlComments(String sampleAddlComments) {
        this.sampleAddlComments = sampleAddlComments;
    }

    /**
     * Sample Development Comments
     * 
     */
    @JsonProperty("sampleDevComments")
    public String getSampleDevComments() {
        return sampleDevComments;
    }

    /**
     * Sample Development Comments
     * 
     */
    @JsonProperty("sampleDevComments")
    public void setSampleDevComments(String sampleDevComments) {
        this.sampleDevComments = sampleDevComments;
    }

    /**
     * Sample Engineering Comments
     * 
     */
    @JsonProperty("sampleEnggComments")
    public String getSampleEnggComments() {
        return sampleEnggComments;
    }

    /**
     * Sample Engineering Comments
     * 
     */
    @JsonProperty("sampleEnggComments")
    public void setSampleEnggComments(String sampleEnggComments) {
        this.sampleEnggComments = sampleEnggComments;
    }

    /**
     * Date Received
     * 
     */
    @JsonProperty("sampleDtReceived")
    public String getSampleDtReceived() {
        return sampleDtReceived;
    }

    /**
     * Date Received
     * 
     */
    @JsonProperty("sampleDtReceived")
    public void setSampleDtReceived(String sampleDtReceived) {
        this.sampleDtReceived = sampleDtReceived;
    }

    /**
     * Sample Colourway
     * 
     */
    @JsonProperty("sampleColourway")
    public String getSampleColourway() {
        return sampleColourway;
    }

    /**
     * Sample Colourway
     * 
     */
    @JsonProperty("sampleColourway")
    public void setSampleColourway(String sampleColourway) {
        this.sampleColourway = sampleColourway;
    }

    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("sampleCreatedOn")
    public String getSampleCreatedOn() {
        return sampleCreatedOn;
    }

    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("sampleCreatedOn")
    public void setSampleCreatedOn(String sampleCreatedOn) {
        this.sampleCreatedOn = sampleCreatedOn;
    }

    /**
     * Last Modified- format:date-time
     * 
     */
    @JsonProperty("sampleLastModified")
    public String getSampleLastModified() {
        return sampleLastModified;
    }

    /**
     * Last Modified- format:date-time
     * 
     */
    @JsonProperty("sampleLastModified")
    public void setSampleLastModified(String sampleLastModified) {
        this.sampleLastModified = sampleLastModified;
    }

    /**
     * Sample Status
     * 
     */
    @JsonProperty("sampleStatus")
    public String getSampleStatus() {
        return sampleStatus;
    }

    /**
     * Sample Status
     * 
     */
    @JsonProperty("sampleStatus")
    public void setSampleStatus(String sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    /**
     * Sample Evaluation Date- format:date-time
     * 
     */
    @JsonProperty("sampleEvalDate")
    public String getSampleEvalDate() {
        return sampleEvalDate;
    }

    /**
     * Sample Evaluation Date- format:date-time
     * 
     */
    @JsonProperty("sampleEvalDate")
    public void setSampleEvalDate(String sampleEvalDate) {
        this.sampleEvalDate = sampleEvalDate;
    }

    /**
     * Sample Description
     * 
     */
    @JsonProperty("sampleDesc")
    public String getSampleDesc() {
        return sampleDesc;
    }

    /**
     * Sample Description
     * 
     */
    @JsonProperty("sampleDesc")
    public void setSampleDesc(String sampleDesc) {
        this.sampleDesc = sampleDesc;
    }

    /**
     * Shipping Comments
     * 
     */
    @JsonProperty("shipComments")
    public String getShipComments() {
        return shipComments;
    }

    /**
     * Shipping Comments
     * 
     */
    @JsonProperty("shipComments")
    public void setShipComments(String shipComments) {
        this.shipComments = shipComments;
    }

    /**
     * Ship Date- format:date-time
     * 
     */
    @JsonProperty("shipDate")
    public String getShipDate() {
        return shipDate;
    }

    /**
     * Ship Date- format:date-time
     * 
     */
    @JsonProperty("shipDate")
    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }

    /**
     * Shipping Method
     * 
     */
    @JsonProperty("shipMethod")
    public String getShipMethod() {
        return shipMethod;
    }

    /**
     * Shipping Method
     * 
     */
    @JsonProperty("shipMethod")
    public void setShipMethod(String shipMethod) {
        this.shipMethod = shipMethod;
    }

    /**
     * Shipping Status
     * 
     */
    @JsonProperty("shipStatus")
    public String getShipStatus() {
        return shipStatus;
    }

    /**
     * Shipping Status
     * 
     */
    @JsonProperty("shipStatus")
    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
    }

    /**
     * Measurement Set
     * 
     */
    @JsonProperty("measureSet")
    public String getMeasureSet() {
        return measureSet;
    }

    /**
     * Measurement Set
     * 
     */
    @JsonProperty("measureSet")
    public void setMeasureSet(String measureSet) {
        this.measureSet = measureSet;
    }

    /**
     * Sample Size
     * 
     */
    @JsonProperty("sampleSize")
    public String getSampleSize() {
        return sampleSize;
    }

    /**
     * Sample Size
     * 
     */
    @JsonProperty("sampleSize")
    public void setSampleSize(String sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * Tracking Number
     * 
     */
    @JsonProperty("trackNum")
    public String getTrackNum() {
        return trackNum;
    }

    /**
     * Tracking Number
     * 
     */
    @JsonProperty("trackNum")
    public void setTrackNum(String trackNum) {
        this.trackNum = trackNum;
    }

    /**
     * Tracking URL
     * 
     */
    @JsonProperty("trackURL")
    public String getTrackURL() {
        return trackURL;
    }

    /**
     * Tracking URL
     * 
     */
    @JsonProperty("trackURL")
    public void setTrackURL(String trackURL) {
        this.trackURL = trackURL;
    }

    @JsonProperty("prodDocuments")
    public List<ProdDocument> getProdDocuments() {
        return prodDocuments;
    }

    @JsonProperty("prodDocuments")
    public void setProdDocuments(List<ProdDocument> prodDocuments) {
        this.prodDocuments = prodDocuments;
    }

}

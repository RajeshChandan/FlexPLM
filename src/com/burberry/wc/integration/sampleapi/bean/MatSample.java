
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
    "sampleCreatedOn",
    "sampleLastModified",
    "sampleStatus",
    "sampleComments",
    "appvLabDip",
    "sampleDtReceived",
    "shipDate",
    "trackNum",
    "sampleEvalDate",
    "receivedQty",
    "matDocuments"
})
public class MatSample implements Serializable
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
     * Sample Comments
     * 
     */
    @JsonProperty("sampleComments")
    @JsonPropertyDescription("Sample Comments")
    private String sampleComments;
    /**
     * Approved Lab Dip
     * 
     */
    @JsonProperty("appvLabDip")
    @JsonPropertyDescription("Approved Lab Dip")
    private String appvLabDip;
    /**
     * Date Received
     * 
     */
    @JsonProperty("sampleDtReceived")
    @JsonPropertyDescription("Date Received")
    private String sampleDtReceived;
    /**
     * Ship Date- format:date-time
     * 
     */
    @JsonProperty("shipDate")
    @JsonPropertyDescription("Ship Date- format:date-time")
    private String shipDate;
    /**
     * Tracking Number
     * 
     */
    @JsonProperty("trackNum")
    @JsonPropertyDescription("Tracking Number")
    private String trackNum;
    /**
     * Sample Evaluation Date- format:date-time
     * 
     */
    @JsonProperty("sampleEvalDate")
    @JsonPropertyDescription("Sample Evaluation Date- format:date-time")
    private String sampleEvalDate;
    /**
     * Received Quantity
     * 
     */
    @JsonProperty("receivedQty")
    @JsonPropertyDescription("Received Quantity")
    private Double receivedQty;
    @JsonProperty("matDocuments")
    private List<MatDocument> matDocuments = null;
    private final static long serialVersionUID = -5826579277737247372L;

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
     * Approved Lab Dip
     * 
     */
    @JsonProperty("appvLabDip")
    public String getAppvLabDip() {
        return appvLabDip;
    }

    /**
     * Approved Lab Dip
     * 
     */
    @JsonProperty("appvLabDip")
    public void setAppvLabDip(String appvLabDip) {
        this.appvLabDip = appvLabDip;
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
     * Received Quantity
     * 
     */
    @JsonProperty("receivedQty")
    public Double getReceivedQty() {
        return receivedQty;
    }

    /**
     * Received Quantity
     * 
     */
    @JsonProperty("receivedQty")
    public void setReceivedQty(Double receivedQty) {
        this.receivedQty = receivedQty;
    }

    @JsonProperty("matDocuments")
    public List<MatDocument> getMatDocuments() {
        return matDocuments;
    }

    @JsonProperty("matDocuments")
    public void setMatDocuments(List<MatDocument> matDocuments) {
        this.matDocuments = matDocuments;
    }

}

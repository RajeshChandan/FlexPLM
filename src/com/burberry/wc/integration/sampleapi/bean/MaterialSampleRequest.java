
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
    "sampleRequestStatus",
    "sampleCreateBy",
    "sampleModBy",
    "sampleRequestComments",
    "qtyRequested",
    "reqDate",
    "reqType",
    "sampleRequestUniqId",
    "orderQuantity",
    "trimSampleSize",
    "expectedDate",
    "samplesReqFor",
    "material",
    "matSource",
    "colour",
    "matSample"
})
public class MaterialSampleRequest implements Serializable
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
     * Quantity Requested
     * 
     */
    @JsonProperty("qtyRequested")
    @JsonPropertyDescription("Quantity Requested")
    private Integer qtyRequested;
    /**
     * Request Date- format:date-time
     * 
     */
    @JsonProperty("reqDate")
    @JsonPropertyDescription("Request Date- format:date-time")
    private String reqDate;
    /**
     * Request Type
     * 
     */
    @JsonProperty("reqType")
    @JsonPropertyDescription("Request Type")
    private String reqType;
    /**
     * Sample Request Unique Id
     * 
     */
    @JsonProperty("sampleRequestUniqId")
    @JsonPropertyDescription("Sample Request Unique Id")
    private Object sampleRequestUniqId;
    /**
     * Order Quantity
     * 
     */
    @JsonProperty("orderQuantity")
    @JsonPropertyDescription("Order Quantity")
    private Double orderQuantity;
    /**
     * Trim Sample Size
     * 
     */
    @JsonProperty("trimSampleSize")
    @JsonPropertyDescription("Trim Sample Size")
    private String trimSampleSize;
    /**
     * Expected Date- format:date-time
     * 
     */
    @JsonProperty("expectedDate")
    @JsonPropertyDescription("Expected Date- format:date-time")
    private String expectedDate;
    /**
     * Samples Request For
     * 
     */
    @JsonProperty("samplesReqFor")
    @JsonPropertyDescription("Samples Request For")
    private String samplesReqFor;
    @JsonProperty("material")
    private Material material;
    @JsonProperty("matSource")
    private MatSource matSource;
    @JsonProperty("colour")
    private Colour colour;
    @JsonProperty("matSample")
    private List<MatSample> matSample = null;
    private final static long serialVersionUID = -4731410601515178267L;

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
     * Quantity Requested
     * 
     */
    @JsonProperty("qtyRequested")
    public Integer getQtyRequested() {
        return qtyRequested;
    }

    /**
     * Quantity Requested
     * 
     */
    @JsonProperty("qtyRequested")
    public void setQtyRequested(Integer qtyRequested) {
        this.qtyRequested = qtyRequested;
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
     * Request Type
     * 
     */
    @JsonProperty("reqType")
    public String getReqType() {
        return reqType;
    }

    /**
     * Request Type
     * 
     */
    @JsonProperty("reqType")
    public void setReqType(String reqType) {
        this.reqType = reqType;
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
     * Order Quantity
     * 
     */
    @JsonProperty("orderQuantity")
    public Double getOrderQuantity() {
        return orderQuantity;
    }

    /**
     * Order Quantity
     * 
     */
    @JsonProperty("orderQuantity")
    public void setOrderQuantity(Double orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    /**
     * Trim Sample Size
     * 
     */
    @JsonProperty("trimSampleSize")
    public String getTrimSampleSize() {
        return trimSampleSize;
    }

    /**
     * Trim Sample Size
     * 
     */
    @JsonProperty("trimSampleSize")
    public void setTrimSampleSize(String trimSampleSize) {
        this.trimSampleSize = trimSampleSize;
    }

    /**
     * Expected Date- format:date-time
     * 
     */
    @JsonProperty("expectedDate")
    public String getExpectedDate() {
        return expectedDate;
    }

    /**
     * Expected Date- format:date-time
     * 
     */
    @JsonProperty("expectedDate")
    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    /**
     * Samples Request For
     * 
     */
    @JsonProperty("samplesReqFor")
    public String getSamplesReqFor() {
        return samplesReqFor;
    }

    /**
     * Samples Request For
     * 
     */
    @JsonProperty("samplesReqFor")
    public void setSamplesReqFor(String samplesReqFor) {
        this.samplesReqFor = samplesReqFor;
    }

    @JsonProperty("material")
    public Material getMaterial() {
        return material;
    }

    @JsonProperty("material")
    public void setMaterial(Material material) {
        this.material = material;
    }

    @JsonProperty("matSource")
    public MatSource getMatSource() {
        return matSource;
    }

    @JsonProperty("matSource")
    public void setMatSource(MatSource matSource) {
        this.matSource = matSource;
    }

    @JsonProperty("colour")
    public Colour getColour() {
        return colour;
    }

    @JsonProperty("colour")
    public void setColour(Colour colour) {
        this.colour = colour;
    }

    @JsonProperty("matSample")
    public List<MatSample> getMatSample() {
        return matSample;
    }

    @JsonProperty("matSample")
    public void setMatSample(List<MatSample> matSample) {
        this.matSample = matSample;
    }

}

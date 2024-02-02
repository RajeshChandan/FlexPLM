
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pmMatColour",
    "pmGuideClrPrice",
    "pmBulkPrice",
    "pmSamplePrice",
    "pmMatColourStatus",
    "pmMatSize",
    "pmUniqId",
    "CRUD",
    "createSampleReq"
})
public class MatPriceManagement implements Serializable
{

    /**
     * Material Colour
     * 
     */
    @JsonProperty("pmMatColour")
    @JsonPropertyDescription("Material Colour")
    private String pmMatColour;
    /**
     * Guide Colour Price
     * 
     */
    @JsonProperty("pmGuideClrPrice")
    @JsonPropertyDescription("Guide Colour Price")
    private Boolean pmGuideClrPrice;
    /**
     * Bulk Price
     * 
     */
    @JsonProperty("pmBulkPrice")
    @JsonPropertyDescription("Bulk Price")
    private Double pmBulkPrice;
    /**
     * Sample Price
     * 
     */
    @JsonProperty("pmSamplePrice")
    @JsonPropertyDescription("Sample Price")
    private Double pmSamplePrice;
    /**
     * Material Colour Status
     * 
     */
    @JsonProperty("pmMatColourStatus")
    @JsonPropertyDescription("Material Colour Status")
    private String pmMatColourStatus;
    /**
     * Material Size
     * 
     */
    @JsonProperty("pmMatSize")
    @JsonPropertyDescription("Material Size")
    private String pmMatSize;
    /**
     * Material Price Mgmt Unique ID- internal key
     * 
     */
    @JsonProperty("pmUniqId")
    @JsonPropertyDescription("Material Price Mgmt Unique ID- internal key")
    private String pmUniqId;
    /**
     * Return 'DELETE' in case material price management row is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case material price management row is deleted")
    private String cRUD;
    /**
     * Create Sample Request?
     * 
     */
    @JsonProperty("createSampleReq")
    @JsonPropertyDescription("Create Sample Request?")
    private Boolean createSampleReq;
    private final static long serialVersionUID = 1698319040053281009L;

    /**
     * Material Colour
     * 
     */
    @JsonProperty("pmMatColour")
    public String getPmMatColour() {
        return pmMatColour;
    }

    /**
     * Material Colour
     * 
     */
    @JsonProperty("pmMatColour")
    public void setPmMatColour(String pmMatColour) {
        this.pmMatColour = pmMatColour;
    }

    /**
     * Guide Colour Price
     * 
     */
    @JsonProperty("pmGuideClrPrice")
    public Boolean getPmGuideClrPrice() {
        return pmGuideClrPrice;
    }

    /**
     * Guide Colour Price
     * 
     */
    @JsonProperty("pmGuideClrPrice")
    public void setPmGuideClrPrice(Boolean pmGuideClrPrice) {
        this.pmGuideClrPrice = pmGuideClrPrice;
    }

    /**
     * Bulk Price
     * 
     */
    @JsonProperty("pmBulkPrice")
    public Double getPmBulkPrice() {
        return pmBulkPrice;
    }

    /**
     * Bulk Price
     * 
     */
    @JsonProperty("pmBulkPrice")
    public void setPmBulkPrice(Double pmBulkPrice) {
        this.pmBulkPrice = pmBulkPrice;
    }

    /**
     * Sample Price
     * 
     */
    @JsonProperty("pmSamplePrice")
    public Double getPmSamplePrice() {
        return pmSamplePrice;
    }

    /**
     * Sample Price
     * 
     */
    @JsonProperty("pmSamplePrice")
    public void setPmSamplePrice(Double pmSamplePrice) {
        this.pmSamplePrice = pmSamplePrice;
    }

    /**
     * Material Colour Status
     * 
     */
    @JsonProperty("pmMatColourStatus")
    public String getPmMatColourStatus() {
        return pmMatColourStatus;
    }

    /**
     * Material Colour Status
     * 
     */
    @JsonProperty("pmMatColourStatus")
    public void setPmMatColourStatus(String pmMatColourStatus) {
        this.pmMatColourStatus = pmMatColourStatus;
    }

    /**
     * Material Size
     * 
     */
    @JsonProperty("pmMatSize")
    public String getPmMatSize() {
        return pmMatSize;
    }

    /**
     * Material Size
     * 
     */
    @JsonProperty("pmMatSize")
    public void setPmMatSize(String pmMatSize) {
        this.pmMatSize = pmMatSize;
    }

    /**
     * Material Price Mgmt Unique ID- internal key
     * 
     */
    @JsonProperty("pmUniqId")
    public String getPmUniqId() {
        return pmUniqId;
    }

    /**
     * Material Price Mgmt Unique ID- internal key
     * 
     */
    @JsonProperty("pmUniqId")
    public void setPmUniqId(String pmUniqId) {
        this.pmUniqId = pmUniqId;
    }

    /**
     * Return 'DELETE' in case material price management row is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case material price management row is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    /**
     * Create Sample Request?
     * 
     */
    @JsonProperty("createSampleReq")
    public Boolean getCreateSampleReq() {
        return createSampleReq;
    }

    /**
     * Create Sample Request?
     * 
     */
    @JsonProperty("createSampleReq")
    public void setCreateSampleReq(Boolean createSampleReq) {
        this.createSampleReq = createSampleReq;
    }

}

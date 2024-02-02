
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ccLastMod",
    "ccGroup",
    "ccSubGroup",
    "ccSubGroup2",
    "ccStatus",
    "ccCountry",
    "ccPrimRecord",
    "ccDesc",
    "ccDuty",
    "ccType",
    "ccUniqId"
})
public class CommodityCode implements Serializable
{

    /**
     * Commodity Code Last Madified Timestamp- format:date-time
     * 
     */
    @JsonProperty("ccLastMod")
    @JsonPropertyDescription("Commodity Code Last Madified Timestamp- format:date-time")
    private String ccLastMod;
    /**
     * Commodity Code Group
     * 
     */
    @JsonProperty("ccGroup")
    @JsonPropertyDescription("Commodity Code Group")
    private String ccGroup;
    /**
     * Commodity Code Sub Group
     * 
     */
    @JsonProperty("ccSubGroup")
    @JsonPropertyDescription("Commodity Code Sub Group")
    private String ccSubGroup;
    /**
     * Commodity Code Sub Group2
     * 
     */
    @JsonProperty("ccSubGroup2")
    @JsonPropertyDescription("Commodity Code Sub Group2")
    private String ccSubGroup2;
    /**
     * Commodity Code Status
     * 
     */
    @JsonProperty("ccStatus")
    @JsonPropertyDescription("Commodity Code Status")
    private String ccStatus;
    /**
     * Commodity Code Country
     * 
     */
    @JsonProperty("ccCountry")
    @JsonPropertyDescription("Commodity Code Country")
    private String ccCountry;
    /**
     * Commodity Code Primary Record
     * 
     */
    @JsonProperty("ccPrimRecord")
    @JsonPropertyDescription("Commodity Code Primary Record")
    private String ccPrimRecord;
    /**
     * Commodity Code Description
     * 
     */
    @JsonProperty("ccDesc")
    @JsonPropertyDescription("Commodity Code Description")
    private String ccDesc;
    /**
     * Commodity Code Duty Rate
     * 
     */
    @JsonProperty("ccDuty")
    @JsonPropertyDescription("Commodity Code Duty Rate")
    private String ccDuty;
    
    /**
     * Commodity Code Type
     * 
     */
    @JsonProperty("ccType")
    @JsonPropertyDescription("CommodityCodeType")
    private String ccType;
    
    /**
     * Commodity Code UniqueID
     * 
     */
    @JsonProperty("ccUniqId")
    @JsonPropertyDescription("Commodity Code UniqueID")
    private String ccUniqId;
    private final static long serialVersionUID = -2418403349314527943L;

    /**
     * Commodity Code Last Madified Timestamp- format:date-time
     * 
     */
    @JsonProperty("ccLastMod")
    public String getCcLastMod() {
        return ccLastMod;
    }

    /**
     * Commodity Code Last Madified Timestamp- format:date-time
     * 
     */
    @JsonProperty("ccLastMod")
    public void setCcLastMod(String ccLastMod) {
        this.ccLastMod = ccLastMod;
    }

    /**
     * Commodity Code Group
     * 
     */
    @JsonProperty("ccGroup")
    public String getCcGroup() {
        return ccGroup;
    }

    /**
     * Commodity Code Group
     * 
     */
    @JsonProperty("ccGroup")
    public void setCcGroup(String ccGroup) {
        this.ccGroup = ccGroup;
    }

    /**
     * Commodity Code Sub Group
     * 
     */
    @JsonProperty("ccSubGroup")
    public String getCcSubGroup() {
        return ccSubGroup;
    }

    /**
     * Commodity Code Sub Group
     * 
     */
    @JsonProperty("ccSubGroup")
    public void setCcSubGroup(String ccSubGroup) {
        this.ccSubGroup = ccSubGroup;
    }

    /**
     * Commodity Code Sub Group2
     * 
     */
    @JsonProperty("ccSubGroup2")
    public String getCcSubGroup2() {
        return ccSubGroup2;
    }

    /**
     * Commodity Code Sub Group2
     * 
     */
    @JsonProperty("ccSubGroup2")
    public void setCcSubGroup2(String ccSubGroup2) {
        this.ccSubGroup2 = ccSubGroup2;
    }

    /**
     * Commodity Code Status
     * 
     */
    @JsonProperty("ccStatus")
    public String getCcStatus() {
        return ccStatus;
    }

    /**
     * Commodity Code Status
     * 
     */
    @JsonProperty("ccStatus")
    public void setCcStatus(String ccStatus) {
        this.ccStatus = ccStatus;
    }

    /**
     * Commodity Code Country
     * 
     */
    @JsonProperty("ccCountry")
    public String getCcCountry() {
        return ccCountry;
    }

    /**
     * Commodity Code Country
     * 
     */
    @JsonProperty("ccCountry")
    public void setCcCountry(String ccCountry) {
        this.ccCountry = ccCountry;
    }

    /**
     * Commodity Code Primary Record
     * 
     */
    @JsonProperty("ccPrimRecord")
    public String getCcPrimRecord() {
        return ccPrimRecord;
    }

    /**
     * Commodity Code Primary Record
     * 
     */
    @JsonProperty("ccPrimRecord")
    public void setCcPrimRecord(String ccPrimRecord) {
        this.ccPrimRecord = ccPrimRecord;
    }

    /**
     * Commodity Code Description
     * 
     */
    @JsonProperty("ccDesc")
    public String getCcDesc() {
        return ccDesc;
    }

    /**
     * Commodity Code Description
     * 
     */
    @JsonProperty("ccDesc")
    public void setCcDesc(String ccDesc) {
        this.ccDesc = ccDesc;
    }

    /**
     * Commodity Code Duty Rate
     * 
     */
    @JsonProperty("ccDuty")
    public String getCcDuty() {
        return ccDuty;
    }

    /**
     * Commodity Code Duty Rate
     * 
     */
    @JsonProperty("ccDuty")
    public void setCcDuty(String ccDuty) {
        this.ccDuty = ccDuty;
    }

    /**
     * CommodityCodeType
     * 
     */
    @JsonProperty("ccType")
    public String getCcType() {
        return ccType;
    }

    /**
     * CommodityCodeType
     * 
     */
    @JsonProperty("ccType")
    public void setCcType(String ccType) {
        this.ccType = ccType;
    }

    /**
     * CommodityCodeUniqueID
     * 
     */
    @JsonProperty("ccUniqId")
    public String getCcUniqId() {
        return ccUniqId;
    }

    /**
     * CommodityCodeUniqueID
     * 
     */
    @JsonProperty("ccUniqId")
    public void setCcUniqId(String ccUniqId) {
        this.ccUniqId = ccUniqId;
    }

}

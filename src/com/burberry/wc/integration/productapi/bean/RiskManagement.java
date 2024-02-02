
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "rmStage",
    "rmType",
    "rmLevel",
    "rmRsnCode",
    "rmDesc",
    "rmOwner",
    "rmStatus",
    "rmTechAct",
    "rmSeason",
    "rmYear",
    "rmLastMod",
    "rmUniqId",
    "rmTestingRequested",
    "rmComments",
    "CRUD"
})
public class RiskManagement implements Serializable
{

    /**
     * Risk Stage
     * 
     */
    @JsonProperty("rmStage")
    @JsonPropertyDescription("Risk Stage")
    private String rmStage;
    /**
     * Risk Type
     * 
     */
    @JsonProperty("rmType")
    @JsonPropertyDescription("Risk Type")
    private String rmType;
    /**
     * Risk Type
     * 
     */
    @JsonProperty("rmLevel")
    @JsonPropertyDescription("Risk Type")
    private String rmLevel;
    /**
     * Risk Reason Code
     * 
     */
    @JsonProperty("rmRsnCode")
    @JsonPropertyDescription("Risk Reason Code")
    private String rmRsnCode;
    /**
     * Risk Description
     * 
     */
    @JsonProperty("rmDesc")
    @JsonPropertyDescription("Risk Description")
    private String rmDesc;
    /**
     * Risk Owner
     * 
     */
    @JsonProperty("rmOwner")
    @JsonPropertyDescription("Risk Owner")
    private String rmOwner;
    /**
     * Risk Status
     * 
     */
    @JsonProperty("rmStatus")
    @JsonPropertyDescription("Risk Status")
    private String rmStatus;
    /**
     * Risk Technical Action
     * 
     */
    @JsonProperty("rmTechAct")
    @JsonPropertyDescription("Risk Technical Action")
    private String rmTechAct;
    /**
     * Risk Season
     * 
     */
    @JsonProperty("rmSeason")
    @JsonPropertyDescription("Risk Season")
    private String rmSeason;
    /**
     * Risk Year
     * 
     */
    @JsonProperty("rmYear")
    @JsonPropertyDescription("Risk Year")
    private Integer rmYear;
    /**
     * Risk Last Madified Timestamp- format:date-time
     * 
     */
    @JsonProperty("rmLastMod")
    @JsonPropertyDescription("Risk Last Madified Timestamp- format:date-time")
    private String rmLastMod;
    /**
     * Risk Mgmt Unique ID- internal key
     * 
     */
    @JsonProperty("rmUniqId")
    @JsonPropertyDescription("Risk Mgmt Unique ID- internal key")
    private String rmUniqId;
    /**
     * Testing Requested- format:date-time
     * 
     */
    @JsonProperty("rmTestingRequested")
    @JsonPropertyDescription("Testing Requested- format:date-time")
    private String rmTestingRequested;
    /**
     * Risk Comments
     * 
     */
    @JsonProperty("rmComments")
    @JsonPropertyDescription("Risk Comments")
    private String rmComments;
    /**
     * Return 'DELETE' in case product risk management row is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case product risk management row is deleted")
    private String cRUD;
    private final static long serialVersionUID = -8420087992895941122L;

    /**
     * Risk Stage
     * 
     */
    @JsonProperty("rmStage")
    public String getRmStage() {
        return rmStage;
    }

    /**
     * Risk Stage
     * 
     */
    @JsonProperty("rmStage")
    public void setRmStage(String rmStage) {
        this.rmStage = rmStage;
    }

    /**
     * Risk Type
     * 
     */
    @JsonProperty("rmType")
    public String getRmType() {
        return rmType;
    }

    /**
     * Risk Type
     * 
     */
    @JsonProperty("rmType")
    public void setRmType(String rmType) {
        this.rmType = rmType;
    }

    /**
     * Risk Type
     * 
     */
    @JsonProperty("rmLevel")
    public String getRmLevel() {
        return rmLevel;
    }

    /**
     * Risk Type
     * 
     */
    @JsonProperty("rmLevel")
    public void setRmLevel(String rmLevel) {
        this.rmLevel = rmLevel;
    }

    /**
     * Risk Reason Code
     * 
     */
    @JsonProperty("rmRsnCode")
    public String getRmRsnCode() {
        return rmRsnCode;
    }

    /**
     * Risk Reason Code
     * 
     */
    @JsonProperty("rmRsnCode")
    public void setRmRsnCode(String rmRsnCode) {
        this.rmRsnCode = rmRsnCode;
    }

    /**
     * Risk Description
     * 
     */
    @JsonProperty("rmDesc")
    public String getRmDesc() {
        return rmDesc;
    }

    /**
     * Risk Description
     * 
     */
    @JsonProperty("rmDesc")
    public void setRmDesc(String rmDesc) {
        this.rmDesc = rmDesc;
    }

    /**
     * Risk Owner
     * 
     */
    @JsonProperty("rmOwner")
    public String getRmOwner() {
        return rmOwner;
    }

    /**
     * Risk Owner
     * 
     */
    @JsonProperty("rmOwner")
    public void setRmOwner(String rmOwner) {
        this.rmOwner = rmOwner;
    }

    /**
     * Risk Status
     * 
     */
    @JsonProperty("rmStatus")
    public String getRmStatus() {
        return rmStatus;
    }

    /**
     * Risk Status
     * 
     */
    @JsonProperty("rmStatus")
    public void setRmStatus(String rmStatus) {
        this.rmStatus = rmStatus;
    }

    /**
     * Risk Technical Action
     * 
     */
    @JsonProperty("rmTechAct")
    public String getRmTechAct() {
        return rmTechAct;
    }

    /**
     * Risk Technical Action
     * 
     */
    @JsonProperty("rmTechAct")
    public void setRmTechAct(String rmTechAct) {
        this.rmTechAct = rmTechAct;
    }

    /**
     * Risk Season
     * 
     */
    @JsonProperty("rmSeason")
    public String getRmSeason() {
        return rmSeason;
    }

    /**
     * Risk Season
     * 
     */
    @JsonProperty("rmSeason")
    public void setRmSeason(String rmSeason) {
        this.rmSeason = rmSeason;
    }

    /**
     * Risk Year
     * 
     */
    @JsonProperty("rmYear")
    public Integer getRmYear() {
        return rmYear;
    }

    /**
     * Risk Year
     * 
     */
    @JsonProperty("rmYear")
    public void setRmYear(Integer rmYear) {
        this.rmYear = rmYear;
    }

    /**
     * Risk Last Madified Timestamp- format:date-time
     * 
     */
    @JsonProperty("rmLastMod")
    public String getRmLastMod() {
        return rmLastMod;
    }

    /**
     * Risk Last Madified Timestamp- format:date-time
     * 
     */
    @JsonProperty("rmLastMod")
    public void setRmLastMod(String rmLastMod) {
        this.rmLastMod = rmLastMod;
    }

    /**
     * Risk Mgmt Unique ID- internal key
     * 
     */
    @JsonProperty("rmUniqId")
    public String getRmUniqId() {
        return rmUniqId;
    }

    /**
     * Risk Mgmt Unique ID- internal key
     * 
     */
    @JsonProperty("rmUniqId")
    public void setRmUniqId(String rmUniqId) {
        this.rmUniqId = rmUniqId;
    }

    /**
     * Testing Requested- format:date-time
     * 
     */
    @JsonProperty("rmTestingRequested")
    public String getRmTestingRequested() {
        return rmTestingRequested;
    }

    /**
     * Testing Requested- format:date-time
     * 
     */
    @JsonProperty("rmTestingRequested")
    public void setRmTestingRequested(String rmTestingRequested) {
        this.rmTestingRequested = rmTestingRequested;
    }

    /**
     * Risk Comments
     * 
     */
    @JsonProperty("rmComments")
    public String getRmComments() {
        return rmComments;
    }

    /**
     * Risk Comments
     * 
     */
    @JsonProperty("rmComments")
    public void setRmComments(String rmComments) {
        this.rmComments = rmComments;
    }

     /**
     * Return 'DELETE' in case product risk management row is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case product risk management row is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

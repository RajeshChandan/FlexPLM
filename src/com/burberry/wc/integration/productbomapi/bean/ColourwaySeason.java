
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "clrSeasonName",
    "collMkt",
    "themeFlst",
    "cfClrway",
    "skuStatus",
    "CRUD"
})
public class ColourwaySeason implements Serializable
{

    /**
     * Season
     * 
     */
    @JsonProperty("clrSeasonName")
    @JsonPropertyDescription("Season")
    private String clrSeasonName;
    /**
     * Collection/Market
     * 
     */
    @JsonProperty("collMkt")
    @JsonPropertyDescription("Collection/Market")
    private String collMkt;
    /**
     * Theme/Floorset
     * 
     */
    @JsonProperty("themeFlst")
    @JsonPropertyDescription("Theme/Floorset")
    private String themeFlst;
    /**
     * Carry forward Colourway
     * 
     */
    @JsonProperty("cfClrway")
    @JsonPropertyDescription("Carry forward Colourway")
    private Boolean cfClrway;
    /**
     * Colourway Status
     * 
     */
    @JsonProperty("skuStatus")
    @JsonPropertyDescription("Colourway Status")
    private String skuStatus;
    /**
     * Return 'DELETE' in case colourway is removed from season
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case colourway is removed from season")
    private String cRUD;
    private final static long serialVersionUID = 8556651196616163736L;

    /**
     * Season
     * 
     */
    @JsonProperty("clrSeasonName")
    public String getClrSeasonName() {
        return clrSeasonName;
    }

    /**
     * Season
     * 
     */
    @JsonProperty("clrSeasonName")
    public void setClrSeasonName(String clrSeasonName) {
        this.clrSeasonName = clrSeasonName;
    }

    /**
     * Collection/Market
     * 
     */
    @JsonProperty("collMkt")
    public String getCollMkt() {
        return collMkt;
    }

    /**
     * Collection/Market
     * 
     */
    @JsonProperty("collMkt")
    public void setCollMkt(String collMkt) {
        this.collMkt = collMkt;
    }

    /**
     * Theme/Floorset
     * 
     */
    @JsonProperty("themeFlst")
    public String getThemeFlst() {
        return themeFlst;
    }

    /**
     * Theme/Floorset
     * 
     */
    @JsonProperty("themeFlst")
    public void setThemeFlst(String themeFlst) {
        this.themeFlst = themeFlst;
    }

    /**
     * Carry forward Colourway
     * 
     */
    @JsonProperty("cfClrway")
    public Boolean getCfClrway() {
        return cfClrway;
    }

    /**
     * Carry forward Colourway
     * 
     */
    @JsonProperty("cfClrway")
    public void setCfClrway(Boolean cfClrway) {
        this.cfClrway = cfClrway;
    }

    /**
     * Colourway Status
     * 
     */
    @JsonProperty("skuStatus")
    public String getSkuStatus() {
        return skuStatus;
    }

    /**
     * Colourway Status
     * 
     */
    @JsonProperty("skuStatus")
    public void setSkuStatus(String skuStatus) {
        this.skuStatus = skuStatus;
    }

    /**
     * Return 'DELETE' in case colourway is removed from season
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case colourway is removed from season
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

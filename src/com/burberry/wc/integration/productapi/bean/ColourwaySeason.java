
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "clrSeasonName",
    "seaClrLastMod",
    "skuStatus",
    "collMkt",
    "themeFlst",
    "adCamp",
    "fixture",
    "stlValidRsn",
    "gsr",
    "overmakes",
    "lookbook",
    "fsrStatus",
    "cfClrway",
    "coCoding",
    "brandBuy",
    "capsule",
    "definer",
    "ltdAvail",
    "validStat",
    "validSummary",
    "runway",
    "CRUD",
    "look",
    "developedBy",
    "core"
})
public class ColourwaySeason implements Serializable
{

    /**
     * Season Full Name (system)
     * (Required)
     * 
     */
    @JsonProperty("clrSeasonName")
    @JsonPropertyDescription("Season Full Name (system)")
    private String clrSeasonName;
    /**
     * Colourway Season Modified Timestamp- format:date-time
     * 
     */
    @JsonProperty("seaClrLastMod")
    @JsonPropertyDescription("Colourway Season Modified Timestamp- format:date-time")
    private String seaClrLastMod;
    /**
     * Colourway Status
     * 
     */
    @JsonProperty("skuStatus")
    @JsonPropertyDescription("Colourway Status")
    private String skuStatus;
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
     * Ad Campaign
     * 
     */
    @JsonProperty("adCamp")
    @JsonPropertyDescription("Ad Campaign")
    private String adCamp;
    /**
     * Fixture
     * 
     */
    @JsonProperty("fixture")
    @JsonPropertyDescription("Fixture")
    private String fixture;
    /**
     * Still Valid Reason
     * 
     */
    @JsonProperty("stlValidRsn")
    @JsonPropertyDescription("Still Valid Reason")
    private String stlValidRsn;
    /**
     * GSR
     * 
     */
    @JsonProperty("gsr")
    @JsonPropertyDescription("GSR")
    private String gsr;
    /**
     * Overmakes
     * 
     */
    @JsonProperty("overmakes")
    @JsonPropertyDescription("Overmakes")
    private String overmakes;
    /**
     * Lookbook
     * 
     */
    @JsonProperty("lookbook")
    @JsonPropertyDescription("Lookbook")
    private String lookbook;
    /**
     * FSR Status
     * 
     */
    @JsonProperty("fsrStatus")
    @JsonPropertyDescription("FSR Status")
    private String fsrStatus;
    /**
     * Carry forward Colourway
     * 
     */
    @JsonProperty("cfClrway")
    @JsonPropertyDescription("Carry forward Colourway")
    private Boolean cfClrway;
    /**
     * Carry Out Coding
     * 
     */
    @JsonProperty("coCoding")
    @JsonPropertyDescription("Carry Out Coding")
    private String coCoding;
    /**
     * Brand Buy
     * 
     */
    @JsonProperty("brandBuy")
    @JsonPropertyDescription("Brand Buy")
    private String brandBuy;
    /**
     * Capsule
     * 
     */
    @JsonProperty("capsule")
    @JsonPropertyDescription("Capsule")
    private String capsule;
    /**
     * Definer
     * 
     */
    @JsonProperty("definer")
    @JsonPropertyDescription("Definer")
    private String definer;
    /**
     * Limited Availibility
     * 
     */
    @JsonProperty("ltdAvail")
    @JsonPropertyDescription("Limited Availibility")
    private String ltdAvail;
    /**
     * Validation Status
     * 
     */
    @JsonProperty("validStat")
    @JsonPropertyDescription("Validation Status")
    private String validStat;
    /**
     * Validation Summary
     * 
     */
    @JsonProperty("validSummary")
    @JsonPropertyDescription("Validation Summary")
    private String validSummary;
    /**
     * Runway
     * 
     */
    @JsonProperty("runway")
    @JsonPropertyDescription("Runway")
    private String runway;
    /**
     * Return 'DELETE' in case colourway is removed from season
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case colourway is removed from season")
    private String cRUD;
    /**
     * Look
     * 
     */
    @JsonProperty("look")
    @JsonPropertyDescription("Look")
    private String look;
    /**
     * Developed By
     * 
     */
    @JsonProperty("developedBy")
    @JsonPropertyDescription("Developed By")
    private String developedBy;
    /**
     * Core
     * 
     */
    @JsonProperty("core")
    @JsonPropertyDescription("Core")
    private String core;
    private final static long serialVersionUID = -8107556920089560575L;

    /**
     * Season Full Name (system)
     * (Required)
     * 
     */
    @JsonProperty("clrSeasonName")
    public String getClrSeasonName() {
        return clrSeasonName;
    }

    /**
     * Season Full Name (system)
     * (Required)
     * 
     */
    @JsonProperty("clrSeasonName")
    public void setClrSeasonName(String clrSeasonName) {
        this.clrSeasonName = clrSeasonName;
    }

    /**
     * Colourway Season Modified Timestamp- format:date-time
     * 
     */
    @JsonProperty("seaClrLastMod")
    public String getSeaClrLastMod() {
        return seaClrLastMod;
    }

    /**
     * Colourway Season Modified Timestamp- format:date-time
     * 
     */
    @JsonProperty("seaClrLastMod")
    public void setSeaClrLastMod(String seaClrLastMod) {
        this.seaClrLastMod = seaClrLastMod;
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
     * Ad Campaign
     * 
     */
    @JsonProperty("adCamp")
    public String getAdCamp() {
        return adCamp;
    }

    /**
     * Ad Campaign
     * 
     */
    @JsonProperty("adCamp")
    public void setAdCamp(String adCamp) {
        this.adCamp = adCamp;
    }

    /**
     * Fixture
     * 
     */
    @JsonProperty("fixture")
    public String getFixture() {
        return fixture;
    }

    /**
     * Fixture
     * 
     */
    @JsonProperty("fixture")
    public void setFixture(String fixture) {
        this.fixture = fixture;
    }

    /**
     * Still Valid Reason
     * 
     */
    @JsonProperty("stlValidRsn")
    public String getStlValidRsn() {
        return stlValidRsn;
    }

    /**
     * Still Valid Reason
     * 
     */
    @JsonProperty("stlValidRsn")
    public void setStlValidRsn(String stlValidRsn) {
        this.stlValidRsn = stlValidRsn;
    }

    /**
     * GSR
     * 
     */
    @JsonProperty("gsr")
    public String getGsr() {
        return gsr;
    }

    /**
     * GSR
     * 
     */
    @JsonProperty("gsr")
    public void setGsr(String gsr) {
        this.gsr = gsr;
    }

    /**
     * Overmakes
     * 
     */
    @JsonProperty("overmakes")
    public String getOvermakes() {
        return overmakes;
    }

    /**
     * Overmakes
     * 
     */
    @JsonProperty("overmakes")
    public void setOvermakes(String overmakes) {
        this.overmakes = overmakes;
    }

    /**
     * Lookbook
     * 
     */
    @JsonProperty("lookbook")
    public String getLookbook() {
        return lookbook;
    }

    /**
     * Lookbook
     * 
     */
    @JsonProperty("lookbook")
    public void setLookbook(String lookbook) {
        this.lookbook = lookbook;
    }

    /**
     * FSR Status
     * 
     */
    @JsonProperty("fsrStatus")
    public String getFsrStatus() {
        return fsrStatus;
    }

    /**
     * FSR Status
     * 
     */
    @JsonProperty("fsrStatus")
    public void setFsrStatus(String fsrStatus) {
        this.fsrStatus = fsrStatus;
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
     * Carry Out Coding
     * 
     */
    @JsonProperty("coCoding")
    public String getCoCoding() {
        return coCoding;
    }

    /**
     * Carry Out Coding
     * 
     */
    @JsonProperty("coCoding")
    public void setCoCoding(String coCoding) {
        this.coCoding = coCoding;
    }

    /**
     * Brand Buy
     * 
     */
    @JsonProperty("brandBuy")
    public String getBrandBuy() {
        return brandBuy;
    }

    /**
     * Brand Buy
     * 
     */
    @JsonProperty("brandBuy")
    public void setBrandBuy(String brandBuy) {
        this.brandBuy = brandBuy;
    }

    /**
     * Capsule
     * 
     */
    @JsonProperty("capsule")
    public String getCapsule() {
        return capsule;
    }

    /**
     * Capsule
     * 
     */
    @JsonProperty("capsule")
    public void setCapsule(String capsule) {
        this.capsule = capsule;
    }

    /**
     * Definer
     * 
     */
    @JsonProperty("definer")
    public String getDefiner() {
        return definer;
    }

    /**
     * Definer
     * 
     */
    @JsonProperty("definer")
    public void setDefiner(String definer) {
        this.definer = definer;
    }

    /**
     * Limited Availibility
     * 
     */
    @JsonProperty("ltdAvail")
    public String getLtdAvail() {
        return ltdAvail;
    }

    /**
     * Limited Availibility
     * 
     */
    @JsonProperty("ltdAvail")
    public void setLtdAvail(String ltdAvail) {
        this.ltdAvail = ltdAvail;
    }

    /**
     * Validation Status
     * 
     */
    @JsonProperty("validStat")
    public String getValidStat() {
        return validStat;
    }

    /**
     * Validation Status
     * 
     */
    @JsonProperty("validStat")
    public void setValidStat(String validStat) {
        this.validStat = validStat;
    }

    /**
     * Validation Summary
     * 
     */
    @JsonProperty("validSummary")
    public String getValidSummary() {
        return validSummary;
    }

    /**
     * Validation Summary
     * 
     */
    @JsonProperty("validSummary")
    public void setValidSummary(String validSummary) {
        this.validSummary = validSummary;
    }

    /**
     * Runway
     * 
     */
    @JsonProperty("runway")
    public String getRunway() {
        return runway;
    }

    /**
     * Runway
     * 
     */
    @JsonProperty("runway")
    public void setRunway(String runway) {
        this.runway = runway;
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

    /**
     * Look
     * 
     */
    @JsonProperty("look")
    public String getLook() {
        return look;
    }

    /**
     * Look
     * 
     */
    @JsonProperty("look")
    public void setLook(String look) {
        this.look = look;
    }

    /**
     * Developed By
     * 
     */
    @JsonProperty("developedBy")
    public String getDevelopedBy() {
        return developedBy;
    }

    /**
     * Developed By
     * 
     */
    @JsonProperty("developedBy")
    public void setDevelopedBy(String developedBy) {
        this.developedBy = developedBy;
    }

    /**
     * Core
     * 
     */
    @JsonProperty("core")
    public String getCore() {
        return core;
    }

    /**
     * Core
     * 
     */
    @JsonProperty("core")
    public void setCore(String core) {
        this.core = core;
    }

}


package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "burIP",
    "colourCode",
    "colourGroup",
    "colourName",
    "colourStatus",
    "creatSeasonType",
    "creatYear",
    "colourLastMod",
    "legColourCode",
    "colourType",
    "finishNickel",
    "finishType",
    "redValue",
    "greenValue",
    "blueValue",
    "clrHexValue",
    "thumbnail",
    "aColour",
    "bColour",
    "LColour",
    "digitStandard"
})
public class Colour implements Serializable
{

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("burIP")
    @JsonPropertyDescription("Burberry IP")
    private Boolean burIP;
    /**
     * Colour Code
     * 
     */
    @JsonProperty("colourCode")
    @JsonPropertyDescription("Colour Code")
    private String colourCode;
    /**
     * Colour Group
     * 
     */
    @JsonProperty("colourGroup")
    @JsonPropertyDescription("Colour Group")
    private String colourGroup;
    /**
     * Colour Name
     * 
     */
    @JsonProperty("colourName")
    @JsonPropertyDescription("Colour Name")
    private String colourName;
    /**
     * Colour Status
     * 
     */
    @JsonProperty("colourStatus")
    @JsonPropertyDescription("Colour Status")
    private String colourStatus;
    /**
     * Creation Season Type
     * 
     */
    @JsonProperty("creatSeasonType")
    @JsonPropertyDescription("Creation Season Type")
    private String creatSeasonType;
    /**
     * Creation Year
     * 
     */
    @JsonProperty("creatYear")
    @JsonPropertyDescription("Creation Year")
    private String creatYear;
    /**
     * Last Modified - format:date-time
     * 
     */
    @JsonProperty("colourLastMod")
    @JsonPropertyDescription("Last Modified - format:date-time")
    private String colourLastMod;
    /**
     * Legacy Colour Code
     * 
     */
    @JsonProperty("legColourCode")
    @JsonPropertyDescription("Legacy Colour Code")
    private String legColourCode;
    /**
     * Colour Type
     * 
     */
    @JsonProperty("colourType")
    @JsonPropertyDescription("Colour Type")
    private String colourType;
    /**
     * Finish Nickel Content
     * 
     */
    @JsonProperty("finishNickel")
    @JsonPropertyDescription("Finish Nickel Content")
    private String finishNickel;
    /**
     * Finish Type
     * 
     */
    @JsonProperty("finishType")
    @JsonPropertyDescription("Finish Type")
    private String finishType;
    /**
     * Red
     * 
     */
    @JsonProperty("redValue")
    @JsonPropertyDescription("Red")
    private Integer redValue;
    /**
     * Green
     * 
     */
    @JsonProperty("greenValue")
    @JsonPropertyDescription("Green")
    private Integer greenValue;
    /**
     * Blue
     * 
     */
    @JsonProperty("blueValue")
    @JsonPropertyDescription("Blue")
    private Integer blueValue;
    /**
     * Colour Hexadecimal Value
     * 
     */
    @JsonProperty("clrHexValue")
    @JsonPropertyDescription("Colour Hexadecimal Value")
    private String clrHexValue;
    /**
     * Thumbnail
     * 
     */
    @JsonProperty("thumbnail")
    @JsonPropertyDescription("Thumbnail")
    private String thumbnail;
    /**
     * a
     * 
     */
    @JsonProperty("aColour")
    @JsonPropertyDescription("a")
    private Double aColour;
    /**
     * b
     * 
     */
    @JsonProperty("bColour")
    @JsonPropertyDescription("b")
    private Double bColour;
    /**
     * L
     * 
     */
    @JsonProperty("LColour")
    @JsonPropertyDescription("L")
    private Double lColour;
    /**
     * Digital Standard
     * 
     */
    @JsonProperty("digitStandard")
    @JsonPropertyDescription("Digital Standard")
    private String digitStandard;
    private final static long serialVersionUID = 8303607508083535300L;

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("burIP")
    public Boolean getBurIP() {
        return burIP;
    }

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("burIP")
    public void setBurIP(Boolean burIP) {
        this.burIP = burIP;
    }

    /**
     * Colour Code
     * 
     */
    @JsonProperty("colourCode")
    public String getColourCode() {
        return colourCode;
    }

    /**
     * Colour Code
     * 
     */
    @JsonProperty("colourCode")
    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

    /**
     * Colour Group
     * 
     */
    @JsonProperty("colourGroup")
    public String getColourGroup() {
        return colourGroup;
    }

    /**
     * Colour Group
     * 
     */
    @JsonProperty("colourGroup")
    public void setColourGroup(String colourGroup) {
        this.colourGroup = colourGroup;
    }

    /**
     * Colour Name
     * 
     */
    @JsonProperty("colourName")
    public String getColourName() {
        return colourName;
    }

    /**
     * Colour Name
     * 
     */
    @JsonProperty("colourName")
    public void setColourName(String colourName) {
        this.colourName = colourName;
    }

    /**
     * Colour Status
     * 
     */
    @JsonProperty("colourStatus")
    public String getColourStatus() {
        return colourStatus;
    }

    /**
     * Colour Status
     * 
     */
    @JsonProperty("colourStatus")
    public void setColourStatus(String colourStatus) {
        this.colourStatus = colourStatus;
    }

    /**
     * Creation Season Type
     * 
     */
    @JsonProperty("creatSeasonType")
    public String getCreatSeasonType() {
        return creatSeasonType;
    }

    /**
     * Creation Season Type
     * 
     */
    @JsonProperty("creatSeasonType")
    public void setCreatSeasonType(String creatSeasonType) {
        this.creatSeasonType = creatSeasonType;
    }

    /**
     * Creation Year
     * 
     */
    @JsonProperty("creatYear")
    public String getCreatYear() {
        return creatYear;
    }

    /**
     * Creation Year
     * 
     */
    @JsonProperty("creatYear")
    public void setCreatYear(String creatYear) {
        this.creatYear = creatYear;
    }

    /**
     * Last Modified - format:date-time
     * 
     */
    @JsonProperty("colourLastMod")
    public String getColourLastMod() {
        return colourLastMod;
    }

    /**
     * Last Modified - format:date-time
     * 
     */
    @JsonProperty("colourLastMod")
    public void setColourLastMod(String colourLastMod) {
        this.colourLastMod = colourLastMod;
    }

    /**
     * Legacy Colour Code
     * 
     */
    @JsonProperty("legColourCode")
    public String getLegColourCode() {
        return legColourCode;
    }

    /**
     * Legacy Colour Code
     * 
     */
    @JsonProperty("legColourCode")
    public void setLegColourCode(String legColourCode) {
        this.legColourCode = legColourCode;
    }

    /**
     * Colour Type
     * 
     */
    @JsonProperty("colourType")
    public String getColourType() {
        return colourType;
    }

    /**
     * Colour Type
     * 
     */
    @JsonProperty("colourType")
    public void setColourType(String colourType) {
        this.colourType = colourType;
    }

    /**
     * Finish Nickel Content
     * 
     */
    @JsonProperty("finishNickel")
    public String getFinishNickel() {
        return finishNickel;
    }

    /**
     * Finish Nickel Content
     * 
     */
    @JsonProperty("finishNickel")
    public void setFinishNickel(String finishNickel) {
        this.finishNickel = finishNickel;
    }

    /**
     * Finish Type
     * 
     */
    @JsonProperty("finishType")
    public String getFinishType() {
        return finishType;
    }

    /**
     * Finish Type
     * 
     */
    @JsonProperty("finishType")
    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    /**
     * Red
     * 
     */
    @JsonProperty("redValue")
    public Integer getRedValue() {
        return redValue;
    }

    /**
     * Red
     * 
     */
    @JsonProperty("redValue")
    public void setRedValue(Integer redValue) {
        this.redValue = redValue;
    }

    /**
     * Green
     * 
     */
    @JsonProperty("greenValue")
    public Integer getGreenValue() {
        return greenValue;
    }

    /**
     * Green
     * 
     */
    @JsonProperty("greenValue")
    public void setGreenValue(Integer greenValue) {
        this.greenValue = greenValue;
    }

    /**
     * Blue
     * 
     */
    @JsonProperty("blueValue")
    public Integer getBlueValue() {
        return blueValue;
    }

    /**
     * Blue
     * 
     */
    @JsonProperty("blueValue")
    public void setBlueValue(Integer blueValue) {
        this.blueValue = blueValue;
    }

    /**
     * Colour Hexadecimal Value
     * 
     */
    @JsonProperty("clrHexValue")
    public String getClrHexValue() {
        return clrHexValue;
    }

    /**
     * Colour Hexadecimal Value
     * 
     */
    @JsonProperty("clrHexValue")
    public void setClrHexValue(String clrHexValue) {
        this.clrHexValue = clrHexValue;
    }

    /**
     * Thumbnail
     * 
     */
    @JsonProperty("thumbnail")
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Thumbnail
     * 
     */
    @JsonProperty("thumbnail")
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * a
     * 
     */
    @JsonProperty("aColour")
    public Double getAColour() {
        return aColour;
    }

    /**
     * a
     * 
     */
    @JsonProperty("aColour")
    public void setAColour(Double aColour) {
        this.aColour = aColour;
    }

    /**
     * b
     * 
     */
    @JsonProperty("bColour")
    public Double getBColour() {
        return bColour;
    }

    /**
     * b
     * 
     */
    @JsonProperty("bColour")
    public void setBColour(Double bColour) {
        this.bColour = bColour;
    }

    /**
     * L
     * 
     */
    @JsonProperty("LColour")
    public Double getLColour() {
        return lColour;
    }

    /**
     * L
     * 
     */
    @JsonProperty("LColour")
    public void setLColour(Double lColour) {
        this.lColour = lColour;
    }

    /**
     * Digital Standard
     * 
     */
    @JsonProperty("digitStandard")
    public String getDigitStandard() {
        return digitStandard;
    }

    /**
     * Digital Standard
     * 
     */
    @JsonProperty("digitStandard")
    public void setDigitStandard(String digitStandard) {
        this.digitStandard = digitStandard;
    }

}


package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "colourwayName",
    "sapMatNum",
    "clrLastMod",
    "legPlmId",
    "legClrCode",
    "clrCode",
    "mktClr",
    "colour",
    "prntPat",
    "iconic",
    "treatment",
    "gender",
    "heritage",
    "personlsn",
    "retailEndDt",
    "retailStartDt",
    "exRoute",
    "clrImageUrl",
    "colourwaySeason"
})
public class Colourway implements Serializable
{

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("colourwayName")
    @JsonPropertyDescription("Colourway Name")
    private String colourwayName;
    /**
     * SAP Material Number
     * (Required)
     * 
     */
    @JsonProperty("sapMatNum")
    @JsonPropertyDescription("SAP Material Number")
    private Integer sapMatNum;
    /**
     * Colourway Modified Timestamp- format:date-time
     * (Required)
     * 
     */
    @JsonProperty("clrLastMod")
    @JsonPropertyDescription("Colourway Modified Timestamp- format:date-time")
    private String clrLastMod;
    /**
     * Legacy PLM ID
     * 
     */
    @JsonProperty("legPlmId")
    @JsonPropertyDescription("Legacy PLM ID")
    private String legPlmId;
    /**
     * Legacy Colour Code
     * 
     */
    @JsonProperty("legClrCode")
    @JsonPropertyDescription("Legacy Colour Code")
    private String legClrCode;
    /**
     * Colour Code
     * 
     */
    @JsonProperty("clrCode")
    @JsonPropertyDescription("Colour Code")
    private String clrCode;
    /**
     * Marketing Colour
     * 
     */
    @JsonProperty("mktClr")
    @JsonPropertyDescription("Marketing Colour")
    private String mktClr;
    /**
     * Coulourway Library Name
     * 
     */
    @JsonProperty("colour")
    @JsonPropertyDescription("Coulourway Library Name")
    private String colour;
    /**
     * Print/Pattern
     * 
     */
    @JsonProperty("prntPat")
    @JsonPropertyDescription("Print/Pattern")
    private String prntPat;
    /**
     * Iconic
     * 
     */
    @JsonProperty("iconic")
    @JsonPropertyDescription("Iconic")
    private String iconic;
    /**
     * Treatment
     * 
     */
    @JsonProperty("treatment")
    @JsonPropertyDescription("Treatment")
    private String treatment;
    /**
     * Gender
     * 
     */
    @JsonProperty("gender")
    @JsonPropertyDescription("Gender")
    private String gender;
    /**
     * Heritage
     * 
     */
    @JsonProperty("heritage")
    @JsonPropertyDescription("Heritage")
    private String heritage;
    /**
     * Personalisation
     * 
     */
    @JsonProperty("personlsn")
    @JsonPropertyDescription("Personalisation")
    private String personlsn;
    /**
     * Retail End Date
     * 
     */
    @JsonProperty("retailEndDt")
    @JsonPropertyDescription("Retail End Date")
    private String retailEndDt;
    /**
     * Retail Start Date
     * 
     */
    @JsonProperty("retailStartDt")
    @JsonPropertyDescription("Retail Start Date")
    private String retailStartDt;
    /**
     * Exit Route
     * 
     */
    @JsonProperty("exRoute")
    @JsonPropertyDescription("Exit Route")
    private String exRoute;
    /**
     * Colourway Primary Image URL
     * 
     */
    @JsonProperty("clrImageUrl")
    @JsonPropertyDescription("Colourway Primary Image URL")
    private String clrImageUrl;
    @JsonProperty("colourwaySeason")
    private List<ColourwaySeason> colourwaySeason = null;
    private final static long serialVersionUID = -5343560937461524752L;

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("colourwayName")
    public String getColourwayName() {
        return colourwayName;
    }

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("colourwayName")
    public void setColourwayName(String colourwayName) {
        this.colourwayName = colourwayName;
    }

    /**
     * SAP Material Number
     * (Required)
     * 
     */
    @JsonProperty("sapMatNum")
    public Integer getSapMatNum() {
        return sapMatNum;
    }

    /**
     * SAP Material Number
     * (Required)
     * 
     */
    @JsonProperty("sapMatNum")
    public void setSapMatNum(Integer sapMatNum) {
        this.sapMatNum = sapMatNum;
    }

    /**
     * Colourway Modified Timestamp- format:date-time
     * (Required)
     * 
     */
    @JsonProperty("clrLastMod")
    public String getClrLastMod() {
        return clrLastMod;
    }

    /**
     * Colourway Modified Timestamp- format:date-time
     * (Required)
     * 
     */
    @JsonProperty("clrLastMod")
    public void setClrLastMod(String clrLastMod) {
        this.clrLastMod = clrLastMod;
    }

    /**
     * Legacy PLM ID
     * 
     */
    @JsonProperty("legPlmId")
    public String getLegPlmId() {
        return legPlmId;
    }

    /**
     * Legacy PLM ID
     * 
     */
    @JsonProperty("legPlmId")
    public void setLegPlmId(String legPlmId) {
        this.legPlmId = legPlmId;
    }

    /**
     * Legacy Colour Code
     * 
     */
    @JsonProperty("legClrCode")
    public String getLegClrCode() {
        return legClrCode;
    }

    /**
     * Legacy Colour Code
     * 
     */
    @JsonProperty("legClrCode")
    public void setLegClrCode(String legClrCode) {
        this.legClrCode = legClrCode;
    }

    /**
     * Colour Code
     * 
     */
    @JsonProperty("clrCode")
    public String getClrCode() {
        return clrCode;
    }

    /**
     * Colour Code
     * 
     */
    @JsonProperty("clrCode")
    public void setClrCode(String clrCode) {
        this.clrCode = clrCode;
    }

    /**
     * Marketing Colour
     * 
     */
    @JsonProperty("mktClr")
    public String getMktClr() {
        return mktClr;
    }

    /**
     * Marketing Colour
     * 
     */
    @JsonProperty("mktClr")
    public void setMktClr(String mktClr) {
        this.mktClr = mktClr;
    }

    /**
     * Coulourway Library Name
     * 
     */
    @JsonProperty("colour")
    public String getColour() {
        return colour;
    }

    /**
     * Coulourway Library Name
     * 
     */
    @JsonProperty("colour")
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Print/Pattern
     * 
     */
    @JsonProperty("prntPat")
    public String getPrntPat() {
        return prntPat;
    }

    /**
     * Print/Pattern
     * 
     */
    @JsonProperty("prntPat")
    public void setPrntPat(String prntPat) {
        this.prntPat = prntPat;
    }

    /**
     * Iconic
     * 
     */
    @JsonProperty("iconic")
    public String getIconic() {
        return iconic;
    }

    /**
     * Iconic
     * 
     */
    @JsonProperty("iconic")
    public void setIconic(String iconic) {
        this.iconic = iconic;
    }

    /**
     * Treatment
     * 
     */
    @JsonProperty("treatment")
    public String getTreatment() {
        return treatment;
    }

    /**
     * Treatment
     * 
     */
    @JsonProperty("treatment")
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * Gender
     * 
     */
    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    /**
     * Gender
     * 
     */
    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Heritage
     * 
     */
    @JsonProperty("heritage")
    public String getHeritage() {
        return heritage;
    }

    /**
     * Heritage
     * 
     */
    @JsonProperty("heritage")
    public void setHeritage(String heritage) {
        this.heritage = heritage;
    }

    /**
     * Personalisation
     * 
     */
    @JsonProperty("personlsn")
    public String getPersonlsn() {
        return personlsn;
    }

    /**
     * Personalisation
     * 
     */
    @JsonProperty("personlsn")
    public void setPersonlsn(String personlsn) {
        this.personlsn = personlsn;
    }

    /**
     * Retail End Date
     * 
     */
    @JsonProperty("retailEndDt")
    public String getRetailEndDt() {
        return retailEndDt;
    }

    /**
     * Retail End Date
     * 
     */
    @JsonProperty("retailEndDt")
    public void setRetailEndDt(String retailEndDt) {
        this.retailEndDt = retailEndDt;
    }

    /**
     * Retail Start Date
     * 
     */
    @JsonProperty("retailStartDt")
    public String getRetailStartDt() {
        return retailStartDt;
    }

    /**
     * Retail Start Date
     * 
     */
    @JsonProperty("retailStartDt")
    public void setRetailStartDt(String retailStartDt) {
        this.retailStartDt = retailStartDt;
    }

    /**
     * Exit Route
     * 
     */
    @JsonProperty("exRoute")
    public String getExRoute() {
        return exRoute;
    }

    /**
     * Exit Route
     * 
     */
    @JsonProperty("exRoute")
    public void setExRoute(String exRoute) {
        this.exRoute = exRoute;
    }

    /**
     * Colourway Primary Image URL
     * 
     */
    @JsonProperty("clrImageUrl")
    public String getClrImageUrl() {
        return clrImageUrl;
    }

    /**
     * Colourway Primary Image URL
     * 
     */
    @JsonProperty("clrImageUrl")
    public void setClrImageUrl(String clrImageUrl) {
        this.clrImageUrl = clrImageUrl;
    }

    @JsonProperty("colourwaySeason")
    public List<ColourwaySeason> getColourwaySeason() {
        return colourwaySeason;
    }

    @JsonProperty("colourwaySeason")
    public void setColourwaySeason(List<ColourwaySeason> colourwaySeason) {
        this.colourwaySeason = colourwaySeason;
    }

}

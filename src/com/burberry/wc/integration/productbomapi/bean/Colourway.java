
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "skuName",
    "sapMatNum",
    "legPlmId",
    "legClrCode",
    "colourwaySeason"
})
public class Colourway implements Serializable
{

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("skuName")
    @JsonPropertyDescription("Colourway Name")
    private String skuName;
    /**
     * SAP Material Number
     * 
     */
    @JsonProperty("sapMatNum")
    @JsonPropertyDescription("SAP Material Number")
    private Integer sapMatNum;
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
    @JsonProperty("colourwaySeason")
    private List<ColourwaySeason> colourwaySeason = null;
    private final static long serialVersionUID = 862351856740374159L;

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("skuName")
    public String getSkuName() {
        return skuName;
    }

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("skuName")
    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    /**
     * SAP Material Number
     * 
     */
    @JsonProperty("sapMatNum")
    public Integer getSapMatNum() {
        return sapMatNum;
    }

    /**
     * SAP Material Number
     * 
     */
    @JsonProperty("sapMatNum")
    public void setSapMatNum(Integer sapMatNum) {
        this.sapMatNum = sapMatNum;
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

    @JsonProperty("colourwaySeason")
    public List<ColourwaySeason> getColourwaySeason() {
        return colourwaySeason;
    }

    @JsonProperty("colourwaySeason")
    public void setColourwaySeason(List<ColourwaySeason> colourwaySeason) {
        this.colourwaySeason = colourwaySeason;
    }

}

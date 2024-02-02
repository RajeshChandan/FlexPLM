
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "colourCode",
    "thumbnail",
    "colourGrp",
    "burIp"
})
public class MaterialColour implements Serializable
{

    /**
     * Material Colour Code
     * 
     */
    @JsonProperty("colourCode")
    @JsonPropertyDescription("Material Colour Code")
    private String colourCode;
    /**
     * Thumbnail
     * 
     */
    @JsonProperty("thumbnail")
    @JsonPropertyDescription("Thumbnail")
    private String thumbnail;
    /**
     * Colour Group
     * 
     */
    @JsonProperty("colourGrp")
    @JsonPropertyDescription("Colour Group")
    private String colourGrp;
    /**
     * Burberry IP
     * 
     */
    @JsonProperty("burIp")
    @JsonPropertyDescription("Burberry IP")
    private Boolean burIp;
    private final static long serialVersionUID = 9143539051602261069L;

    /**
     * Material Colour Code
     * 
     */
    @JsonProperty("colourCode")
    public String getColourCode() {
        return colourCode;
    }

    /**
     * Material Colour Code
     * 
     */
    @JsonProperty("colourCode")
    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
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
     * Colour Group
     * 
     */
    @JsonProperty("colourGrp")
    public String getColourGrp() {
        return colourGrp;
    }

    /**
     * Colour Group
     * 
     */
    @JsonProperty("colourGrp")
    public void setColourGrp(String colourGrp) {
        this.colourGrp = colourGrp;
    }

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("burIp")
    public Boolean getBurIp() {
        return burIp;
    }

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("burIp")
    public void setBurIp(Boolean burIp) {
        this.burIp = burIp;
    }

}

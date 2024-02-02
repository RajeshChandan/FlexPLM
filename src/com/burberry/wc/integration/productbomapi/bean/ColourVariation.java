
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "colourwayName",
    "cvMatSize",
    "bomLinkCVUniqId",
    "cvClrDesc",
    "materialColour"
})
public class ColourVariation implements Serializable
{

    /**
     * Colourway Name
     * 
     */
    @JsonProperty("colourwayName")
    @JsonPropertyDescription("Colourway Name")
    private String colourwayName;
    /**
     * CV Size
     * 
     */
    @JsonProperty("cvMatSize")
    @JsonPropertyDescription("CV Size")
    private String cvMatSize;
    /**
     * BOM Link Colour Variation Unique Id
     * 
     */
    @JsonProperty("bomLinkCVUniqId")
    @JsonPropertyDescription("BOM Link Colour Variation Unique Id")
    private Object bomLinkCVUniqId;
    /**
     * Colour
     * 
     */
    @JsonProperty("cvClrDesc")
    @JsonPropertyDescription("Colour")
    private String cvClrDesc;
    @JsonProperty("materialColour")
    private MaterialColour materialColour;
    private final static long serialVersionUID = -6978684027908012906L;

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
     * CV Size
     * 
     */
    @JsonProperty("cvMatSize")
    public String getCvMatSize() {
        return cvMatSize;
    }

    /**
     * CV Size
     * 
     */
    @JsonProperty("cvMatSize")
    public void setCvMatSize(String cvMatSize) {
        this.cvMatSize = cvMatSize;
    }

    /**
     * BOM Link Colour Variation Unique Id
     * 
     */
    @JsonProperty("bomLinkCVUniqId")
    public Object getBomLinkCVUniqId() {
        return bomLinkCVUniqId;
    }

    /**
     * BOM Link Colour Variation Unique Id
     * 
     */
    @JsonProperty("bomLinkCVUniqId")
    public void setBomLinkCVUniqId(Object bomLinkCVUniqId) {
        this.bomLinkCVUniqId = bomLinkCVUniqId;
    }

    /**
     * Colour
     * 
     */
    @JsonProperty("cvClrDesc")
    public String getCvClrDesc() {
        return cvClrDesc;
    }

    /**
     * Colour
     * 
     */
    @JsonProperty("cvClrDesc")
    public void setCvClrDesc(String cvClrDesc) {
        this.cvClrDesc = cvClrDesc;
    }

    @JsonProperty("materialColour")
    public MaterialColour getMaterialColour() {
        return materialColour;
    }

    @JsonProperty("materialColour")
    public void setMaterialColour(MaterialColour materialColour) {
        this.materialColour = materialColour;
    }

}


package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "matColourStatus",
    "suppColourName",
    "suppColourNum",
    "suppColourComment",
    "guideBulkPrice",
    "guideSamplePrice",
    "aestheticStatus",
    "technicalStatus",
    "rejectionReason",
    "clrComponentApproval",
    "matClrCreatedBy",
    "colour",
    "paletteInfo"
})
public class MaterialColour implements Serializable
{

    /**
     * Name
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Name")
    private String name;
    /**
     * Material Colour Status
     * 
     */
    @JsonProperty("matColourStatus")
    @JsonPropertyDescription("Material Colour Status")
    private String matColourStatus;
    /**
     * Supplier Colour Ref
     * 
     */
    @JsonProperty("suppColourName")
    @JsonPropertyDescription("Supplier Colour Ref")
    private String suppColourName;
    /**
     * Supplier Colour Number
     * 
     */
    @JsonProperty("suppColourNum")
    @JsonPropertyDescription("Supplier Colour Number")
    private String suppColourNum;
    /**
     * Supplier Colour Comments
     * 
     */
    @JsonProperty("suppColourComment")
    @JsonPropertyDescription("Supplier Colour Comments")
    private String suppColourComment;
    /**
     * Guide Bulk Price
     * 
     */
    @JsonProperty("guideBulkPrice")
    @JsonPropertyDescription("Guide Bulk Price")
    private Double guideBulkPrice;
    /**
     * Guide Sample Price
     * 
     */
    @JsonProperty("guideSamplePrice")
    @JsonPropertyDescription("Guide Sample Price")
    private Double guideSamplePrice;
    /**
     * Material Colour Aesthetic Status
     * 
     */
    @JsonProperty("aestheticStatus")
    @JsonPropertyDescription("Material Colour Aesthetic Status")
    private String aestheticStatus;
    /**
     * Material Colour Technical Status
     * 
     */
    @JsonProperty("technicalStatus")
    @JsonPropertyDescription("Material Colour Technical Status")
    private String technicalStatus;
    /**
     * Rejection Reason
     * 
     */
    @JsonProperty("rejectionReason")
    @JsonPropertyDescription("Rejection Reason")
    private String rejectionReason;
    /**
     * Colour Component Approval- format:date-time
     * 
     */
    @JsonProperty("clrComponentApproval")
    @JsonPropertyDescription("Colour Component Approval- format:date-time")
    private String clrComponentApproval;
    /**
     * Material Colour Created By
     * 
     */
    @JsonProperty("matClrCreatedBy")
    @JsonPropertyDescription("Material Colour Created By")
    private String matClrCreatedBy;
    @JsonProperty("colour")
    private Colour colour;
    @JsonProperty("paletteInfo")
    private List<PaletteInfo> paletteInfo = null;
    private final static long serialVersionUID = 7411663354272561298L;

    /**
     * Name
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Name
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Material Colour Status
     * 
     */
    @JsonProperty("matColourStatus")
    public String getMatColourStatus() {
        return matColourStatus;
    }

    /**
     * Material Colour Status
     * 
     */
    @JsonProperty("matColourStatus")
    public void setMatColourStatus(String matColourStatus) {
        this.matColourStatus = matColourStatus;
    }

    /**
     * Supplier Colour Ref
     * 
     */
    @JsonProperty("suppColourName")
    public String getSuppColourName() {
        return suppColourName;
    }

    /**
     * Supplier Colour Ref
     * 
     */
    @JsonProperty("suppColourName")
    public void setSuppColourName(String suppColourName) {
        this.suppColourName = suppColourName;
    }

    /**
     * Supplier Colour Number
     * 
     */
    @JsonProperty("suppColourNum")
    public String getSuppColourNum() {
        return suppColourNum;
    }

    /**
     * Supplier Colour Number
     * 
     */
    @JsonProperty("suppColourNum")
    public void setSuppColourNum(String suppColourNum) {
        this.suppColourNum = suppColourNum;
    }

    /**
     * Supplier Colour Comments
     * 
     */
    @JsonProperty("suppColourComment")
    public String getSuppColourComment() {
        return suppColourComment;
    }

    /**
     * Supplier Colour Comments
     * 
     */
    @JsonProperty("suppColourComment")
    public void setSuppColourComment(String suppColourComment) {
        this.suppColourComment = suppColourComment;
    }

    /**
     * Guide Bulk Price
     * 
     */
    @JsonProperty("guideBulkPrice")
    public Double getGuideBulkPrice() {
        return guideBulkPrice;
    }

    /**
     * Guide Bulk Price
     * 
     */
    @JsonProperty("guideBulkPrice")
    public void setGuideBulkPrice(Double guideBulkPrice) {
        this.guideBulkPrice = guideBulkPrice;
    }

    /**
     * Guide Sample Price
     * 
     */
    @JsonProperty("guideSamplePrice")
    public Double getGuideSamplePrice() {
        return guideSamplePrice;
    }

    /**
     * Guide Sample Price
     * 
     */
    @JsonProperty("guideSamplePrice")
    public void setGuideSamplePrice(Double guideSamplePrice) {
        this.guideSamplePrice = guideSamplePrice;
    }

    /**
     * Material Colour Aesthetic Status
     * 
     */
    @JsonProperty("aestheticStatus")
    public String getAestheticStatus() {
        return aestheticStatus;
    }

    /**
     * Material Colour Aesthetic Status
     * 
     */
    @JsonProperty("aestheticStatus")
    public void setAestheticStatus(String aestheticStatus) {
        this.aestheticStatus = aestheticStatus;
    }

    /**
     * Material Colour Technical Status
     * 
     */
    @JsonProperty("technicalStatus")
    public String getTechnicalStatus() {
        return technicalStatus;
    }

    /**
     * Material Colour Technical Status
     * 
     */
    @JsonProperty("technicalStatus")
    public void setTechnicalStatus(String technicalStatus) {
        this.technicalStatus = technicalStatus;
    }

    /**
     * Rejection Reason
     * 
     */
    @JsonProperty("rejectionReason")
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Rejection Reason
     * 
     */
    @JsonProperty("rejectionReason")
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * Colour Component Approval- format:date-time
     * 
     */
    @JsonProperty("clrComponentApproval")
    public String getClrComponentApproval() {
        return clrComponentApproval;
    }

    /**
     * Colour Component Approval- format:date-time
     * 
     */
    @JsonProperty("clrComponentApproval")
    public void setClrComponentApproval(String clrComponentApproval) {
        this.clrComponentApproval = clrComponentApproval;
    }

    /**
     * Material Colour Created By
     * 
     */
    @JsonProperty("matClrCreatedBy")
    public String getMatClrCreatedBy() {
        return matClrCreatedBy;
    }

    /**
     * Material Colour Created By
     * 
     */
    @JsonProperty("matClrCreatedBy")
    public void setMatClrCreatedBy(String matClrCreatedBy) {
        this.matClrCreatedBy = matClrCreatedBy;
    }

    @JsonProperty("colour")
    public Colour getColour() {
        return colour;
    }

    @JsonProperty("colour")
    public void setColour(Colour colour) {
        this.colour = colour;
    }

    @JsonProperty("paletteInfo")
    public List<PaletteInfo> getPaletteInfo() {
        return paletteInfo;
    }

    @JsonProperty("paletteInfo")
    public void setPaletteInfo(List<PaletteInfo> paletteInfo) {
        this.paletteInfo = paletteInfo;
    }

}

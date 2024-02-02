
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bomPlace",
    "bomPlaceNotes",
    "netConsumption",
    "wasteRatio",
    "totalConsumption",
    "highlight",
    "markUp",
    "material",
    "priceOvr",
    "Supplier",
    "totRollUp",
    "vendorCFPrice",
    "vendorCurrency",
    "blMatComments",
    "blMatFinish",
    "blMatSetColour",
    "blMatSize",
    "blMatStatus",
    "blTrimPrice",
    "clrDesc",
    "bomReporting",
    "bomSortNumber",
    "bomLinkUniqId",
    "CRUD",
    "colourVariation",
    "materialMaster"
})
public class SectionVariation implements Serializable
{

    /**
     * Placement
     * 
     */
    @JsonProperty("bomPlace")
    @JsonPropertyDescription("Placement")
    private String bomPlace;
    /**
     * Placement Notes
     * 
     */
    @JsonProperty("bomPlaceNotes")
    @JsonPropertyDescription("Placement Notes")
    private String bomPlaceNotes;
    /**
     * Net Consumption
     * 
     */
    @JsonProperty("netConsumption")
    @JsonPropertyDescription("Net Consumption")
    private Double netConsumption;
    /**
     * Wastage Ratio
     * 
     */
    @JsonProperty("wasteRatio")
    @JsonPropertyDescription("Wastage Ratio")
    private Double wasteRatio;
    /**
     * Total Consumption
     * 
     */
    @JsonProperty("totalConsumption")
    @JsonPropertyDescription("Total Consumption")
    private Double totalConsumption;
    /**
     * Highlight
     * 
     */
    @JsonProperty("highlight")
    @JsonPropertyDescription("Highlight")
    private String highlight;
    /**
     * Mark Up
     * 
     */
    @JsonProperty("markUp")
    @JsonPropertyDescription("Mark Up")
    private String markUp;
    /**
     * Material Description
     * 
     */
    @JsonProperty("material")
    @JsonPropertyDescription("Material Description")
    private String material;
    /**
     * Price OVR
     * 
     */
    @JsonProperty("priceOvr")
    @JsonPropertyDescription("Price OVR")
    private String priceOvr;
    /**
     * Supplier
     * 
     */
    @JsonProperty("Supplier")
    @JsonPropertyDescription("Supplier")
    private String supplier;
    /**
     * Total Roll Up
     * 
     */
    @JsonProperty("totRollUp")
    @JsonPropertyDescription("Total Roll Up")
    private Integer totRollUp;
    /**
     * Vendor CF Price
     * 
     */
    @JsonProperty("vendorCFPrice")
    @JsonPropertyDescription("Vendor CF Price")
    private Double vendorCFPrice;
    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("vendorCurrency")
    @JsonPropertyDescription("Vendor Currency")
    private String vendorCurrency;
    /**
     * Comments
     * 
     */
    @JsonProperty("blMatComments")
    @JsonPropertyDescription("Comments")
    private String blMatComments;
    /**
     * Finish
     * 
     */
    @JsonProperty("blMatFinish")
    @JsonPropertyDescription("Finish")
    private String blMatFinish;
    /**
     * Set to Colourway Colour
     * 
     */
    @JsonProperty("blMatSetColour")
    @JsonPropertyDescription("Set to Colourway Colour")
    private Boolean blMatSetColour;
    /**
     * Size
     * 
     */
    @JsonProperty("blMatSize")
    @JsonPropertyDescription("Size")
    private String blMatSize;
    /**
     * Status
     * 
     */
    @JsonProperty("blMatStatus")
    @JsonPropertyDescription("Status")
    private String blMatStatus;
    /**
     * Trim Price
     * 
     */
    @JsonProperty("blTrimPrice")
    @JsonPropertyDescription("Trim Price")
    private Double blTrimPrice;
    /**
     * Colour
     * 
     */
    @JsonProperty("clrDesc")
    @JsonPropertyDescription("Colour")
    private String clrDesc;
    /**
     * BOM Reporting
     * 
     */
    @JsonProperty("bomReporting")
    @JsonPropertyDescription("BOM Reporting")
    private Boolean bomReporting;
    /**
     * Sorting Number
     * 
     */
    @JsonProperty("bomSortNumber")
    @JsonPropertyDescription("Sorting Number")
    private Integer bomSortNumber;
    /**
     * BOM Link Unique Id
     * 
     */
    @JsonProperty("bomLinkUniqId")
    @JsonPropertyDescription("BOM Link Unique Id")
    private Object bomLinkUniqId;
    /**
     * Return 'DELETE' in case the Material from BOM-Link is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case the Material from BOM-Link is deleted")
    private String cRUD;
    @JsonProperty("colourVariation")
    private List<ColourVariation> colourVariation = null;
    @JsonProperty("materialMaster")
    private MaterialMaster materialMaster;
    private final static long serialVersionUID = -1848705412790695064L;

    /**
     * Placement
     * 
     */
    @JsonProperty("bomPlace")
    public String getBomPlace() {
        return bomPlace;
    }

    /**
     * Placement
     * 
     */
    @JsonProperty("bomPlace")
    public void setBomPlace(String bomPlace) {
        this.bomPlace = bomPlace;
    }

    /**
     * Placement Notes
     * 
     */
    @JsonProperty("bomPlaceNotes")
    public String getBomPlaceNotes() {
        return bomPlaceNotes;
    }

    /**
     * Placement Notes
     * 
     */
    @JsonProperty("bomPlaceNotes")
    public void setBomPlaceNotes(String bomPlaceNotes) {
        this.bomPlaceNotes = bomPlaceNotes;
    }

    /**
     * Net Consumption
     * 
     */
    @JsonProperty("netConsumption")
    public Double getNetConsumption() {
        return netConsumption;
    }

    /**
     * Net Consumption
     * 
     */
    @JsonProperty("netConsumption")
    public void setNetConsumption(Double netConsumption) {
        this.netConsumption = netConsumption;
    }

    /**
     * Wastage Ratio
     * 
     */
    @JsonProperty("wasteRatio")
    public Double getWasteRatio() {
        return wasteRatio;
    }

    /**
     * Wastage Ratio
     * 
     */
    @JsonProperty("wasteRatio")
    public void setWasteRatio(Double wasteRatio) {
        this.wasteRatio = wasteRatio;
    }

    /**
     * Total Consumption
     * 
     */
    @JsonProperty("totalConsumption")
    public Double getTotalConsumption() {
        return totalConsumption;
    }

    /**
     * Total Consumption
     * 
     */
    @JsonProperty("totalConsumption")
    public void setTotalConsumption(Double totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    /**
     * Highlight
     * 
     */
    @JsonProperty("highlight")
    public String getHighlight() {
        return highlight;
    }

    /**
     * Highlight
     * 
     */
    @JsonProperty("highlight")
    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    /**
     * Mark Up
     * 
     */
    @JsonProperty("markUp")
    public String getMarkUp() {
        return markUp;
    }

    /**
     * Mark Up
     * 
     */
    @JsonProperty("markUp")
    public void setMarkUp(String markUp) {
        this.markUp = markUp;
    }

    /**
     * Material Description
     * 
     */
    @JsonProperty("material")
    public String getMaterial() {
        return material;
    }

    /**
     * Material Description
     * 
     */
    @JsonProperty("material")
    public void setMaterial(String material) {
        this.material = material;
    }

    /**
     * Price OVR
     * 
     */
    @JsonProperty("priceOvr")
    public String getPriceOvr() {
        return priceOvr;
    }

    /**
     * Price OVR
     * 
     */
    @JsonProperty("priceOvr")
    public void setPriceOvr(String priceOvr) {
        this.priceOvr = priceOvr;
    }

    /**
     * Supplier
     * 
     */
    @JsonProperty("Supplier")
    public String getSupplier() {
        return supplier;
    }

    /**
     * Supplier
     * 
     */
    @JsonProperty("Supplier")
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    /**
     * Total Roll Up
     * 
     */
    @JsonProperty("totRollUp")
    public Integer getTotRollUp() {
        return totRollUp;
    }

    /**
     * Total Roll Up
     * 
     */
    @JsonProperty("totRollUp")
    public void setTotRollUp(Integer totRollUp) {
        this.totRollUp = totRollUp;
    }

    /**
     * Vendor CF Price
     * 
     */
    @JsonProperty("vendorCFPrice")
    public Double getVendorCFPrice() {
        return vendorCFPrice;
    }

    /**
     * Vendor CF Price
     * 
     */
    @JsonProperty("vendorCFPrice")
    public void setVendorCFPrice(Double vendorCFPrice) {
        this.vendorCFPrice = vendorCFPrice;
    }

    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("vendorCurrency")
    public String getVendorCurrency() {
        return vendorCurrency;
    }

    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("vendorCurrency")
    public void setVendorCurrency(String vendorCurrency) {
        this.vendorCurrency = vendorCurrency;
    }

    /**
     * Comments
     * 
     */
    @JsonProperty("blMatComments")
    public String getBlMatComments() {
        return blMatComments;
    }

    /**
     * Comments
     * 
     */
    @JsonProperty("blMatComments")
    public void setBlMatComments(String blMatComments) {
        this.blMatComments = blMatComments;
    }

    /**
     * Finish
     * 
     */
    @JsonProperty("blMatFinish")
    public String getBlMatFinish() {
        return blMatFinish;
    }

    /**
     * Finish
     * 
     */
    @JsonProperty("blMatFinish")
    public void setBlMatFinish(String blMatFinish) {
        this.blMatFinish = blMatFinish;
    }

    /**
     * Set to Colourway Colour
     * 
     */
    @JsonProperty("blMatSetColour")
    public Boolean getBlMatSetColour() {
        return blMatSetColour;
    }

    /**
     * Set to Colourway Colour
     * 
     */
    @JsonProperty("blMatSetColour")
    public void setBlMatSetColour(Boolean blMatSetColour) {
        this.blMatSetColour = blMatSetColour;
    }

    /**
     * Size
     * 
     */
    @JsonProperty("blMatSize")
    public String getBlMatSize() {
        return blMatSize;
    }

    /**
     * Size
     * 
     */
    @JsonProperty("blMatSize")
    public void setBlMatSize(String blMatSize) {
        this.blMatSize = blMatSize;
    }

    /**
     * Status
     * 
     */
    @JsonProperty("blMatStatus")
    public String getBlMatStatus() {
        return blMatStatus;
    }

    /**
     * Status
     * 
     */
    @JsonProperty("blMatStatus")
    public void setBlMatStatus(String blMatStatus) {
        this.blMatStatus = blMatStatus;
    }

    /**
     * Trim Price
     * 
     */
    @JsonProperty("blTrimPrice")
    public Double getBlTrimPrice() {
        return blTrimPrice;
    }

    /**
     * Trim Price
     * 
     */
    @JsonProperty("blTrimPrice")
    public void setBlTrimPrice(Double blTrimPrice) {
        this.blTrimPrice = blTrimPrice;
    }

    /**
     * Colour
     * 
     */
    @JsonProperty("clrDesc")
    public String getClrDesc() {
        return clrDesc;
    }

    /**
     * Colour
     * 
     */
    @JsonProperty("clrDesc")
    public void setClrDesc(String clrDesc) {
        this.clrDesc = clrDesc;
    }

    /**
     * BOM Reporting
     * 
     */
    @JsonProperty("bomReporting")
    public Boolean getBomReporting() {
        return bomReporting;
    }

    /**
     * BOM Reporting
     * 
     */
    @JsonProperty("bomReporting")
    public void setBomReporting(Boolean bomReporting) {
        this.bomReporting = bomReporting;
    }

    /**
     * Sorting Number
     * 
     */
    @JsonProperty("bomSortNumber")
    public Integer getBomSortNumber() {
        return bomSortNumber;
    }

    /**
     * Sorting Number
     * 
     */
    @JsonProperty("bomSortNumber")
    public void setBomSortNumber(Integer bomSortNumber) {
        this.bomSortNumber = bomSortNumber;
    }

    /**
     * BOM Link Unique Id
     * 
     */
    @JsonProperty("bomLinkUniqId")
    public Object getBomLinkUniqId() {
        return bomLinkUniqId;
    }

    /**
     * BOM Link Unique Id
     * 
     */
    @JsonProperty("bomLinkUniqId")
    public void setBomLinkUniqId(Object bomLinkUniqId) {
        this.bomLinkUniqId = bomLinkUniqId;
    }

    /**
     * Return 'DELETE' in case the Material from BOM-Link is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case the Material from BOM-Link is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    @JsonProperty("colourVariation")
    public List<ColourVariation> getColourVariation() {
        return colourVariation;
    }

    @JsonProperty("colourVariation")
    public void setColourVariation(List<ColourVariation> colourVariation) {
        this.colourVariation = colourVariation;
    }

    @JsonProperty("materialMaster")
    public MaterialMaster getMaterialMaster() {
        return materialMaster;
    }

    @JsonProperty("materialMaster")
    public void setMaterialMaster(MaterialMaster materialMaster) {
        this.materialMaster = materialMaster;
    }

}

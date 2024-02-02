
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "scSeason",
    "cutDate",
    "finalPPApvl",
    "wearTestApprovDeadline",
    "bulkTestApprovDeadline",
    "totMainMaterial",
    "leatherTrim",
    "linings",
    "buttons",
    "zippers",
    "furTrims",
    "downTrims",
    "otherTrims",
    "hardware",
    "labour",
    "testing",
    "packaging",
    "overhead",
    "profit",
    "financeCost",
    "otherUncatCost",
    "totalVendorCost",
    "totMainMaterialPer",
    "leatherTrimPer",
    "liningsPer",
    "buttonsPer",
    "zippersPer",
    "furTrimsPer",
    "downTrimsPer",
    "otherTrimsPer",
    "hardwarePer",
    "labourPer",
    "testingPer",
    "packagingPer",
    "overheadPer",
    "profitPer",
    "financeCostPer",
    "otherUncatCostPer",
    "specification"
})
public class SourceSeason implements Serializable
{

    /**
     * Season
     * 
     */
    @JsonProperty("scSeason")
    @JsonPropertyDescription("Season")
    private String scSeason;
    /**
     * Planned Cut Date- format:date-time
     * 
     */
    @JsonProperty("cutDate")
    @JsonPropertyDescription("Planned Cut Date- format:date-time")
    private String cutDate;
    /**
     * Final PP Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("finalPPApvl")
    @JsonPropertyDescription("Final PP Approval Deadline- format:date-time")
    private String finalPPApvl;
    /**
     * Wear Test Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("wearTestApprovDeadline")
    @JsonPropertyDescription("Wear Test Approval Deadline- format:date-time")
    private String wearTestApprovDeadline;
    /**
     * Bulk Test Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("bulkTestApprovDeadline")
    @JsonPropertyDescription("Bulk Test Approval Deadline- format:date-time")
    private String bulkTestApprovDeadline;
    /**
     * Total Main Material
     * 
     */
    @JsonProperty("totMainMaterial")
    @JsonPropertyDescription("Total Main Material")
    private Double totMainMaterial;
    /**
     * Leather Trims
     * 
     */
    @JsonProperty("leatherTrim")
    @JsonPropertyDescription("Leather Trims")
    private Double leatherTrim;
    /**
     * Linings
     * 
     */
    @JsonProperty("linings")
    @JsonPropertyDescription("Linings")
    private Double linings;
    /**
     * Buttons
     * 
     */
    @JsonProperty("buttons")
    @JsonPropertyDescription("Buttons")
    private Double buttons;
    /**
     * Zippers
     * 
     */
    @JsonProperty("zippers")
    @JsonPropertyDescription("Zippers")
    private Double zippers;
    /**
     * Fur Trims
     * 
     */
    @JsonProperty("furTrims")
    @JsonPropertyDescription("Fur Trims")
    private Double furTrims;
    /**
     * Down Trims
     * 
     */
    @JsonProperty("downTrims")
    @JsonPropertyDescription("Down Trims")
    private Double downTrims;
    /**
     * Other Trims
     * 
     */
    @JsonProperty("otherTrims")
    @JsonPropertyDescription("Other Trims")
    private Double otherTrims;
    /**
     * Hardware
     * 
     */
    @JsonProperty("hardware")
    @JsonPropertyDescription("Hardware")
    private Double hardware;
    /**
     * Labour
     * 
     */
    @JsonProperty("labour")
    @JsonPropertyDescription("Labour")
    private Double labour;
    /**
     * Testing
     * 
     */
    @JsonProperty("testing")
    @JsonPropertyDescription("Testing")
    private Double testing;
    /**
     * Packaging
     * 
     */
    @JsonProperty("packaging")
    @JsonPropertyDescription("Packaging")
    private Double packaging;
    /**
     * Overhead
     * 
     */
    @JsonProperty("overhead")
    @JsonPropertyDescription("Overhead")
    private Double overhead;
    /**
     * Profit
     * 
     */
    @JsonProperty("profit")
    @JsonPropertyDescription("Profit")
    private Double profit;
    /**
     * Finance Cost
     * 
     */
    @JsonProperty("financeCost")
    @JsonPropertyDescription("Finance Cost")
    private Double financeCost;
    /**
     * Other (uncat.) Costs
     * 
     */
    @JsonProperty("otherUncatCost")
    @JsonPropertyDescription("Other (uncat.) Costs")
    private Double otherUncatCost;
    /**
     * Total Vendor Cost
     * 
     */
    @JsonProperty("totalVendorCost")
    @JsonPropertyDescription("Total Vendor Cost")
    private Double totalVendorCost;
    /**
     * Total Main Material %
     * 
     */
    @JsonProperty("totMainMaterialPer")
    @JsonPropertyDescription("Total Main Material %")
    private Double totMainMaterialPer;
    /**
     * Leather Trims %
     * 
     */
    @JsonProperty("leatherTrimPer")
    @JsonPropertyDescription("Leather Trims %")
    private Double leatherTrimPer;
    /**
     * Linings %
     * 
     */
    @JsonProperty("liningsPer")
    @JsonPropertyDescription("Linings %")
    private Double liningsPer;
    /**
     * Buttons %
     * 
     */
    @JsonProperty("buttonsPer")
    @JsonPropertyDescription("Buttons %")
    private Double buttonsPer;
    /**
     * Zippers %
     * 
     */
    @JsonProperty("zippersPer")
    @JsonPropertyDescription("Zippers %")
    private Double zippersPer;
    /**
     * Fur Trims %
     * 
     */
    @JsonProperty("furTrimsPer")
    @JsonPropertyDescription("Fur Trims %")
    private Double furTrimsPer;
    /**
     * Down Trims %
     * 
     */
    @JsonProperty("downTrimsPer")
    @JsonPropertyDescription("Down Trims %")
    private Double downTrimsPer;
    /**
     * Other Trims %
     * 
     */
    @JsonProperty("otherTrimsPer")
    @JsonPropertyDescription("Other Trims %")
    private Double otherTrimsPer;
    /**
     * Hardware %
     * 
     */
    @JsonProperty("hardwarePer")
    @JsonPropertyDescription("Hardware %")
    private Double hardwarePer;
    /**
     * Labour %
     * 
     */
    @JsonProperty("labourPer")
    @JsonPropertyDescription("Labour %")
    private Double labourPer;
    /**
     * Testing %
     * 
     */
    @JsonProperty("testingPer")
    @JsonPropertyDescription("Testing %")
    private Double testingPer;
    /**
     * Packaging %
     * 
     */
    @JsonProperty("packagingPer")
    @JsonPropertyDescription("Packaging %")
    private Double packagingPer;
    /**
     * Overhead %
     * 
     */
    @JsonProperty("overheadPer")
    @JsonPropertyDescription("Overhead %")
    private Double overheadPer;
    /**
     * Profit %
     * 
     */
    @JsonProperty("profitPer")
    @JsonPropertyDescription("Profit %")
    private Double profitPer;
    /**
     * Finance Cost %
     * 
     */
    @JsonProperty("financeCostPer")
    @JsonPropertyDescription("Finance Cost %")
    private Double financeCostPer;
    /**
     * Other (uncat.) Costs %
     * 
     */
    @JsonProperty("otherUncatCostPer")
    @JsonPropertyDescription("Other (uncat.) Costs %")
    private Double otherUncatCostPer;
    @JsonProperty("specification")
    private List<Specification> specification = null;
    private final static long serialVersionUID = -3416664848595504736L;

    /**
     * Season
     * 
     */
    @JsonProperty("scSeason")
    public String getScSeason() {
        return scSeason;
    }

    /**
     * Season
     * 
     */
    @JsonProperty("scSeason")
    public void setScSeason(String scSeason) {
        this.scSeason = scSeason;
    }

    /**
     * Planned Cut Date- format:date-time
     * 
     */
    @JsonProperty("cutDate")
    public String getCutDate() {
        return cutDate;
    }

    /**
     * Planned Cut Date- format:date-time
     * 
     */
    @JsonProperty("cutDate")
    public void setCutDate(String cutDate) {
        this.cutDate = cutDate;
    }

    /**
     * Final PP Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("finalPPApvl")
    public String getFinalPPApvl() {
        return finalPPApvl;
    }

    /**
     * Final PP Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("finalPPApvl")
    public void setFinalPPApvl(String finalPPApvl) {
        this.finalPPApvl = finalPPApvl;
    }

    /**
     * Wear Test Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("wearTestApprovDeadline")
    public String getWearTestApprovDeadline() {
        return wearTestApprovDeadline;
    }

    /**
     * Wear Test Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("wearTestApprovDeadline")
    public void setWearTestApprovDeadline(String wearTestApprovDeadline) {
        this.wearTestApprovDeadline = wearTestApprovDeadline;
    }

    /**
     * Bulk Test Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("bulkTestApprovDeadline")
    public String getBulkTestApprovDeadline() {
        return bulkTestApprovDeadline;
    }

    /**
     * Bulk Test Approval Deadline- format:date-time
     * 
     */
    @JsonProperty("bulkTestApprovDeadline")
    public void setBulkTestApprovDeadline(String bulkTestApprovDeadline) {
        this.bulkTestApprovDeadline = bulkTestApprovDeadline;
    }

    /**
     * Total Main Material
     * 
     */
    @JsonProperty("totMainMaterial")
    public Double getTotMainMaterial() {
        return totMainMaterial;
    }

    /**
     * Total Main Material
     * 
     */
    @JsonProperty("totMainMaterial")
    public void setTotMainMaterial(Double totMainMaterial) {
        this.totMainMaterial = totMainMaterial;
    }

    /**
     * Leather Trims
     * 
     */
    @JsonProperty("leatherTrim")
    public Double getLeatherTrim() {
        return leatherTrim;
    }

    /**
     * Leather Trims
     * 
     */
    @JsonProperty("leatherTrim")
    public void setLeatherTrim(Double leatherTrim) {
        this.leatherTrim = leatherTrim;
    }

    /**
     * Linings
     * 
     */
    @JsonProperty("linings")
    public Double getLinings() {
        return linings;
    }

    /**
     * Linings
     * 
     */
    @JsonProperty("linings")
    public void setLinings(Double linings) {
        this.linings = linings;
    }

    /**
     * Buttons
     * 
     */
    @JsonProperty("buttons")
    public Double getButtons() {
        return buttons;
    }

    /**
     * Buttons
     * 
     */
    @JsonProperty("buttons")
    public void setButtons(Double buttons) {
        this.buttons = buttons;
    }

    /**
     * Zippers
     * 
     */
    @JsonProperty("zippers")
    public Double getZippers() {
        return zippers;
    }

    /**
     * Zippers
     * 
     */
    @JsonProperty("zippers")
    public void setZippers(Double zippers) {
        this.zippers = zippers;
    }

    /**
     * Fur Trims
     * 
     */
    @JsonProperty("furTrims")
    public Double getFurTrims() {
        return furTrims;
    }

    /**
     * Fur Trims
     * 
     */
    @JsonProperty("furTrims")
    public void setFurTrims(Double furTrims) {
        this.furTrims = furTrims;
    }

    /**
     * Down Trims
     * 
     */
    @JsonProperty("downTrims")
    public Double getDownTrims() {
        return downTrims;
    }

    /**
     * Down Trims
     * 
     */
    @JsonProperty("downTrims")
    public void setDownTrims(Double downTrims) {
        this.downTrims = downTrims;
    }

    /**
     * Other Trims
     * 
     */
    @JsonProperty("otherTrims")
    public Double getOtherTrims() {
        return otherTrims;
    }

    /**
     * Other Trims
     * 
     */
    @JsonProperty("otherTrims")
    public void setOtherTrims(Double otherTrims) {
        this.otherTrims = otherTrims;
    }

    /**
     * Hardware
     * 
     */
    @JsonProperty("hardware")
    public Double getHardware() {
        return hardware;
    }

    /**
     * Hardware
     * 
     */
    @JsonProperty("hardware")
    public void setHardware(Double hardware) {
        this.hardware = hardware;
    }

    /**
     * Labour
     * 
     */
    @JsonProperty("labour")
    public Double getLabour() {
        return labour;
    }

    /**
     * Labour
     * 
     */
    @JsonProperty("labour")
    public void setLabour(Double labour) {
        this.labour = labour;
    }

    /**
     * Testing
     * 
     */
    @JsonProperty("testing")
    public Double getTesting() {
        return testing;
    }

    /**
     * Testing
     * 
     */
    @JsonProperty("testing")
    public void setTesting(Double testing) {
        this.testing = testing;
    }

    /**
     * Packaging
     * 
     */
    @JsonProperty("packaging")
    public Double getPackaging() {
        return packaging;
    }

    /**
     * Packaging
     * 
     */
    @JsonProperty("packaging")
    public void setPackaging(Double packaging) {
        this.packaging = packaging;
    }

    /**
     * Overhead
     * 
     */
    @JsonProperty("overhead")
    public Double getOverhead() {
        return overhead;
    }

    /**
     * Overhead
     * 
     */
    @JsonProperty("overhead")
    public void setOverhead(Double overhead) {
        this.overhead = overhead;
    }

    /**
     * Profit
     * 
     */
    @JsonProperty("profit")
    public Double getProfit() {
        return profit;
    }

    /**
     * Profit
     * 
     */
    @JsonProperty("profit")
    public void setProfit(Double profit) {
        this.profit = profit;
    }

    /**
     * Finance Cost
     * 
     */
    @JsonProperty("financeCost")
    public Double getFinanceCost() {
        return financeCost;
    }

    /**
     * Finance Cost
     * 
     */
    @JsonProperty("financeCost")
    public void setFinanceCost(Double financeCost) {
        this.financeCost = financeCost;
    }

    /**
     * Other (uncat.) Costs
     * 
     */
    @JsonProperty("otherUncatCost")
    public Double getOtherUncatCost() {
        return otherUncatCost;
    }

    /**
     * Other (uncat.) Costs
     * 
     */
    @JsonProperty("otherUncatCost")
    public void setOtherUncatCost(Double otherUncatCost) {
        this.otherUncatCost = otherUncatCost;
    }

    /**
     * Total Vendor Cost
     * 
     */
    @JsonProperty("totalVendorCost")
    public Double getTotalVendorCost() {
        return totalVendorCost;
    }

    /**
     * Total Vendor Cost
     * 
     */
    @JsonProperty("totalVendorCost")
    public void setTotalVendorCost(Double totalVendorCost) {
        this.totalVendorCost = totalVendorCost;
    }

    /**
     * Total Main Material %
     * 
     */
    @JsonProperty("totMainMaterialPer")
    public Double getTotMainMaterialPer() {
        return totMainMaterialPer;
    }

    /**
     * Total Main Material %
     * 
     */
    @JsonProperty("totMainMaterialPer")
    public void setTotMainMaterialPer(Double totMainMaterialPer) {
        this.totMainMaterialPer = totMainMaterialPer;
    }

    /**
     * Leather Trims %
     * 
     */
    @JsonProperty("leatherTrimPer")
    public Double getLeatherTrimPer() {
        return leatherTrimPer;
    }

    /**
     * Leather Trims %
     * 
     */
    @JsonProperty("leatherTrimPer")
    public void setLeatherTrimPer(Double leatherTrimPer) {
        this.leatherTrimPer = leatherTrimPer;
    }

    /**
     * Linings %
     * 
     */
    @JsonProperty("liningsPer")
    public Double getLiningsPer() {
        return liningsPer;
    }

    /**
     * Linings %
     * 
     */
    @JsonProperty("liningsPer")
    public void setLiningsPer(Double liningsPer) {
        this.liningsPer = liningsPer;
    }

    /**
     * Buttons %
     * 
     */
    @JsonProperty("buttonsPer")
    public Double getButtonsPer() {
        return buttonsPer;
    }

    /**
     * Buttons %
     * 
     */
    @JsonProperty("buttonsPer")
    public void setButtonsPer(Double buttonsPer) {
        this.buttonsPer = buttonsPer;
    }

    /**
     * Zippers %
     * 
     */
    @JsonProperty("zippersPer")
    public Double getZippersPer() {
        return zippersPer;
    }

    /**
     * Zippers %
     * 
     */
    @JsonProperty("zippersPer")
    public void setZippersPer(Double zippersPer) {
        this.zippersPer = zippersPer;
    }

    /**
     * Fur Trims %
     * 
     */
    @JsonProperty("furTrimsPer")
    public Double getFurTrimsPer() {
        return furTrimsPer;
    }

    /**
     * Fur Trims %
     * 
     */
    @JsonProperty("furTrimsPer")
    public void setFurTrimsPer(Double furTrimsPer) {
        this.furTrimsPer = furTrimsPer;
    }

    /**
     * Down Trims %
     * 
     */
    @JsonProperty("downTrimsPer")
    public Double getDownTrimsPer() {
        return downTrimsPer;
    }

    /**
     * Down Trims %
     * 
     */
    @JsonProperty("downTrimsPer")
    public void setDownTrimsPer(Double downTrimsPer) {
        this.downTrimsPer = downTrimsPer;
    }

    /**
     * Other Trims %
     * 
     */
    @JsonProperty("otherTrimsPer")
    public Double getOtherTrimsPer() {
        return otherTrimsPer;
    }

    /**
     * Other Trims %
     * 
     */
    @JsonProperty("otherTrimsPer")
    public void setOtherTrimsPer(Double otherTrimsPer) {
        this.otherTrimsPer = otherTrimsPer;
    }

    /**
     * Hardware %
     * 
     */
    @JsonProperty("hardwarePer")
    public Double getHardwarePer() {
        return hardwarePer;
    }

    /**
     * Hardware %
     * 
     */
    @JsonProperty("hardwarePer")
    public void setHardwarePer(Double hardwarePer) {
        this.hardwarePer = hardwarePer;
    }

    /**
     * Labour %
     * 
     */
    @JsonProperty("labourPer")
    public Double getLabourPer() {
        return labourPer;
    }

    /**
     * Labour %
     * 
     */
    @JsonProperty("labourPer")
    public void setLabourPer(Double labourPer) {
        this.labourPer = labourPer;
    }

    /**
     * Testing %
     * 
     */
    @JsonProperty("testingPer")
    public Double getTestingPer() {
        return testingPer;
    }

    /**
     * Testing %
     * 
     */
    @JsonProperty("testingPer")
    public void setTestingPer(Double testingPer) {
        this.testingPer = testingPer;
    }

    /**
     * Packaging %
     * 
     */
    @JsonProperty("packagingPer")
    public Double getPackagingPer() {
        return packagingPer;
    }

    /**
     * Packaging %
     * 
     */
    @JsonProperty("packagingPer")
    public void setPackagingPer(Double packagingPer) {
        this.packagingPer = packagingPer;
    }

    /**
     * Overhead %
     * 
     */
    @JsonProperty("overheadPer")
    public Double getOverheadPer() {
        return overheadPer;
    }

    /**
     * Overhead %
     * 
     */
    @JsonProperty("overheadPer")
    public void setOverheadPer(Double overheadPer) {
        this.overheadPer = overheadPer;
    }

    /**
     * Profit %
     * 
     */
    @JsonProperty("profitPer")
    public Double getProfitPer() {
        return profitPer;
    }

    /**
     * Profit %
     * 
     */
    @JsonProperty("profitPer")
    public void setProfitPer(Double profitPer) {
        this.profitPer = profitPer;
    }

    /**
     * Finance Cost %
     * 
     */
    @JsonProperty("financeCostPer")
    public Double getFinanceCostPer() {
        return financeCostPer;
    }

    /**
     * Finance Cost %
     * 
     */
    @JsonProperty("financeCostPer")
    public void setFinanceCostPer(Double financeCostPer) {
        this.financeCostPer = financeCostPer;
    }

    /**
     * Other (uncat.) Costs %
     * 
     */
    @JsonProperty("otherUncatCostPer")
    public Double getOtherUncatCostPer() {
        return otherUncatCostPer;
    }

    /**
     * Other (uncat.) Costs %
     * 
     */
    @JsonProperty("otherUncatCostPer")
    public void setOtherUncatCostPer(Double otherUncatCostPer) {
        this.otherUncatCostPer = otherUncatCostPer;
    }

    @JsonProperty("specification")
    public List<Specification> getSpecification() {
        return specification;
    }

    @JsonProperty("specification")
    public void setSpecification(List<Specification> specification) {
        this.specification = specification;
    }

}

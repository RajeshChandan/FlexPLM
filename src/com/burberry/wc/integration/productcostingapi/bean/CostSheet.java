
package com.burberry.wc.integration.productcostingapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "csUniqId",
    "csName",
    "csNumber",
    "csProdSeasonName",
    "csRepColourways",
    "csProdSizeDefinition",
    "csSpec",
    "csApprovedClrCost",
    "csAgentCommPercent",
    "csAppColourway",
    "csColourwayStatus",
    "csType",
    "csStatus",
    "csEffDate",
    "csPlannedCOO",
    "csProdCommCode",
    "csThemeFlst",
    "csVendorCurrency",
    "csVendorCurrConvRate",
    "csFGCostGBP",
    "csFreightFOBPercent",
    "csLandCostGBP",
    "csLastModify",
    "csModifier",
    "csPrimary",
    "csProdDutyRate",
    "csNotionMarginPercent",
    "csTargetNotionMarginPercent",
    "csFGCostLocal",
    "csActualGBP",
    "csVATPercent",
    "csVendorFGCost",
    "CRUD",
    "colourways"
})
public class CostSheet implements Serializable
{

    /**
     * Cost Sheet Unique ID- internal key
     * 
     */
    @JsonProperty("csUniqId")
    @JsonPropertyDescription("Cost Sheet Unique ID- internal key")
    private String csUniqId;
    /**
     * (Cost Sheet) Name
     * 
     */
    @JsonProperty("csName")
    @JsonPropertyDescription("(Cost Sheet) Name")
    private String csName;
    /**
     * Cost Sheet Number
     * 
     */
    @JsonProperty("csNumber")
    @JsonPropertyDescription("Cost Sheet Number")
    private String csNumber;
    /**
     * Season
     * 
     */
    @JsonProperty("csProdSeasonName")
    @JsonPropertyDescription("Season")
    private String csProdSeasonName;
    /**
     * Representative Colourways
     * 
     */
    @JsonProperty("csRepColourways")
    @JsonPropertyDescription("Representative Colourways")
    private String csRepColourways;
    /**
     * Prodcut Size Definition
     * 
     */
    @JsonProperty("csProdSizeDefinition")
    @JsonPropertyDescription("Prodcut Size Definition")
    private String csProdSizeDefinition;
    /**
     * Specification
     * 
     */
    @JsonProperty("csSpec")
    @JsonPropertyDescription("Specification")
    private String csSpec;
    /**
     * Approved Colourway Cost
     * 
     */
    @JsonProperty("csApprovedClrCost")
    @JsonPropertyDescription("Approved Colourway Cost")
    private Boolean csApprovedClrCost;
    /**
     * Agent Commission (% of FG Cost GBP)
     * 
     */
    @JsonProperty("csAgentCommPercent")
    @JsonPropertyDescription("Agent Commission (% of FG Cost GBP)")
    private Double csAgentCommPercent;
    /**
     * Applicable Colourway
     * 
     */
    @JsonProperty("csAppColourway")
    @JsonPropertyDescription("Applicable Colourway")
    private String csAppColourway;
    /**
     * Colourway Status
     * 
     */
    @JsonProperty("csColourwayStatus")
    @JsonPropertyDescription("Colourway Status")
    private String csColourwayStatus;
    /**
     * Cost Sheet Type
     * 
     */
    @JsonProperty("csType")
    @JsonPropertyDescription("Cost Sheet Type")
    private String csType;
    /**
     * Cost Sheet Status
     * 
     */
    @JsonProperty("csStatus")
    @JsonPropertyDescription("Cost Sheet Status")
    private String csStatus;
    /**
     * Effective Date- Format:date-time
     * 
     */
    @JsonProperty("csEffDate")
    @JsonPropertyDescription("Effective Date- Format:date-time")
    private String csEffDate;
    /**
     * Planned COO
     * 
     */
    @JsonProperty("csPlannedCOO")
    @JsonPropertyDescription("Planned COO")
    private String csPlannedCOO;
    /**
     * Product Commodity Code
     * 
     */
    @JsonProperty("csProdCommCode")
    @JsonPropertyDescription("Product Commodity Code")
    private String csProdCommCode;
    /**
     * Theme/Floorset
     * 
     */
    @JsonProperty("csThemeFlst")
    @JsonPropertyDescription("Theme/Floorset")
    private String csThemeFlst;
    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("csVendorCurrency")
    @JsonPropertyDescription("Vendor Currency")
    private String csVendorCurrency;
    /**
     * Vendor Currency Conv. Rate
     * 
     */
    @JsonProperty("csVendorCurrConvRate")
    @JsonPropertyDescription("Vendor Currency Conv. Rate")
    private Double csVendorCurrConvRate;
    /**
     * FG Cost GBP
     * 
     */
    @JsonProperty("csFGCostGBP")
    @JsonPropertyDescription("FG Cost GBP")
    private Double csFGCostGBP;
    /**
     * Freight (% of FG Cost GBP)
     * 
     */
    @JsonProperty("csFreightFOBPercent")
    @JsonPropertyDescription("Freight (% of FG Cost GBP)")
    private Double csFreightFOBPercent;
    /**
     * Landed Cost GBP
     * 
     */
    @JsonProperty("csLandCostGBP")
    @JsonPropertyDescription("Landed Cost GBP")
    private Double csLandCostGBP;
    /**
     * Last Modified- formt:date-time
     * 
     */
    @JsonProperty("csLastModify")
    @JsonPropertyDescription("Last Modified- formt:date-time")
    private String csLastModify;
    /**
     * Modified By
     * 
     */
    @JsonProperty("csModifier")
    @JsonPropertyDescription("Modified By")
    private String csModifier;
    /**
     * Primary Cost Sheet
     * 
     */
    @JsonProperty("csPrimary")
    @JsonPropertyDescription("Primary Cost Sheet")
    private Boolean csPrimary;
    /**
     * Product Duty Rate
     * 
     */
    @JsonProperty("csProdDutyRate")
    @JsonPropertyDescription("Product Duty Rate")
    private Double csProdDutyRate;
    /**
     * GBP Notional Margin (%)
     * 
     */
    @JsonProperty("csNotionMarginPercent")
    @JsonPropertyDescription("GBP Notional Margin (%)")
    private Double csNotionMarginPercent;
    /**
     * Target GBP Notional Margin (%)
     * 
     */
    @JsonProperty("csTargetNotionMarginPercent")
    @JsonPropertyDescription("Target GBP Notional Margin (%)")
    private Double csTargetNotionMarginPercent;
    /**
     * Target FG Cost Local
     * 
     */
    @JsonProperty("csFGCostLocal")
    @JsonPropertyDescription("Target FG Cost Local")
    private Double csFGCostLocal;
    /**
     * Actual GBP Retail [£]
     * 
     */
    @JsonProperty("csActualGBP")
    @JsonPropertyDescription("Actual GBP Retail [\u00a3]")
    private Double csActualGBP;
    /**
     * VAT Rate (%)
     * 
     */
    @JsonProperty("csVATPercent")
    @JsonPropertyDescription("VAT Rate (%)")
    private Double csVATPercent;
    /**
     * Vendor FG cost
     * 
     */
    @JsonProperty("csVendorFGCost")
    @JsonPropertyDescription("Vendor FG cost")
    private Double csVendorFGCost;
    /**
     * Return 'DELETE' in case the cost sheet is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case the cost sheet is deleted")
    private String cRUD;
    @JsonProperty("colourways")
    private List<Colourway> colourways = null;
    private final static long serialVersionUID = -5010241699328818652L;

    /**
     * Cost Sheet Unique ID- internal key
     * 
     */
    @JsonProperty("csUniqId")
    public String getCsUniqId() {
        return csUniqId;
    }

    /**
     * Cost Sheet Unique ID- internal key
     * 
     */
    @JsonProperty("csUniqId")
    public void setCsUniqId(String csUniqId) {
        this.csUniqId = csUniqId;
    }

    /**
     * (Cost Sheet) Name
     * 
     */
    @JsonProperty("csName")
    public String getCsName() {
        return csName;
    }

    /**
     * (Cost Sheet) Name
     * 
     */
    @JsonProperty("csName")
    public void setCsName(String csName) {
        this.csName = csName;
    }

    /**
     * Cost Sheet Number
     * 
     */
    @JsonProperty("csNumber")
    public String getCsNumber() {
        return csNumber;
    }

    /**
     * Cost Sheet Number
     * 
     */
    @JsonProperty("csNumber")
    public void setCsNumber(String csNumber) {
        this.csNumber = csNumber;
    }

    /**
     * Season
     * 
     */
    @JsonProperty("csProdSeasonName")
    public String getCsProdSeasonName() {
        return csProdSeasonName;
    }

    /**
     * Season
     * 
     */
    @JsonProperty("csProdSeasonName")
    public void setCsProdSeasonName(String csProdSeasonName) {
        this.csProdSeasonName = csProdSeasonName;
    }

    /**
     * Representative Colourways
     * 
     */
    @JsonProperty("csRepColourways")
    public String getCsRepColourways() {
        return csRepColourways;
    }

    /**
     * Representative Colourways
     * 
     */
    @JsonProperty("csRepColourways")
    public void setCsRepColourways(String csRepColourways) {
        this.csRepColourways = csRepColourways;
    }

    /**
     * Prodcut Size Definition
     * 
     */
    @JsonProperty("csProdSizeDefinition")
    public String getCsProdSizeDefinition() {
        return csProdSizeDefinition;
    }

    /**
     * Prodcut Size Definition
     * 
     */
    @JsonProperty("csProdSizeDefinition")
    public void setCsProdSizeDefinition(String csProdSizeDefinition) {
        this.csProdSizeDefinition = csProdSizeDefinition;
    }

    /**
     * Specification
     * 
     */
    @JsonProperty("csSpec")
    public String getCsSpec() {
        return csSpec;
    }

    /**
     * Specification
     * 
     */
    @JsonProperty("csSpec")
    public void setCsSpec(String csSpec) {
        this.csSpec = csSpec;
    }

    /**
     * Approved Colourway Cost
     * 
     */
    @JsonProperty("csApprovedClrCost")
    public Boolean getCsApprovedClrCost() {
        return csApprovedClrCost;
    }

    /**
     * Approved Colourway Cost
     * 
     */
    @JsonProperty("csApprovedClrCost")
    public void setCsApprovedClrCost(Boolean csApprovedClrCost) {
        this.csApprovedClrCost = csApprovedClrCost;
    }

    /**
     * Agent Commission (% of FG Cost GBP)
     * 
     */
    @JsonProperty("csAgentCommPercent")
    public Double getCsAgentCommPercent() {
        return csAgentCommPercent;
    }

    /**
     * Agent Commission (% of FG Cost GBP)
     * 
     */
    @JsonProperty("csAgentCommPercent")
    public void setCsAgentCommPercent(Double csAgentCommPercent) {
        this.csAgentCommPercent = csAgentCommPercent;
    }

    /**
     * Applicable Colourway
     * 
     */
    @JsonProperty("csAppColourway")
    public String getCsAppColourway() {
        return csAppColourway;
    }

    /**
     * Applicable Colourway
     * 
     */
    @JsonProperty("csAppColourway")
    public void setCsAppColourway(String csAppColourway) {
        this.csAppColourway = csAppColourway;
    }

    /**
     * Colourway Status
     * 
     */
    @JsonProperty("csColourwayStatus")
    public String getCsColourwayStatus() {
        return csColourwayStatus;
    }

    /**
     * Colourway Status
     * 
     */
    @JsonProperty("csColourwayStatus")
    public void setCsColourwayStatus(String csColourwayStatus) {
        this.csColourwayStatus = csColourwayStatus;
    }

    /**
     * Cost Sheet Type
     * 
     */
    @JsonProperty("csType")
    public String getCsType() {
        return csType;
    }

    /**
     * Cost Sheet Type
     * 
     */
    @JsonProperty("csType")
    public void setCsType(String csType) {
        this.csType = csType;
    }

    /**
     * Cost Sheet Status
     * 
     */
    @JsonProperty("csStatus")
    public String getCsStatus() {
        return csStatus;
    }

    /**
     * Cost Sheet Status
     * 
     */
    @JsonProperty("csStatus")
    public void setCsStatus(String csStatus) {
        this.csStatus = csStatus;
    }

    /**
     * Effective Date- Format:date-time
     * 
     */
    @JsonProperty("csEffDate")
    public String getCsEffDate() {
        return csEffDate;
    }

    /**
     * Effective Date- Format:date-time
     * 
     */
    @JsonProperty("csEffDate")
    public void setCsEffDate(String csEffDate) {
        this.csEffDate = csEffDate;
    }

    /**
     * Planned COO
     * 
     */
    @JsonProperty("csPlannedCOO")
    public String getCsPlannedCOO() {
        return csPlannedCOO;
    }

    /**
     * Planned COO
     * 
     */
    @JsonProperty("csPlannedCOO")
    public void setCsPlannedCOO(String csPlannedCOO) {
        this.csPlannedCOO = csPlannedCOO;
    }

    /**
     * Product Commodity Code
     * 
     */
    @JsonProperty("csProdCommCode")
    public String getCsProdCommCode() {
        return csProdCommCode;
    }

    /**
     * Product Commodity Code
     * 
     */
    @JsonProperty("csProdCommCode")
    public void setCsProdCommCode(String csProdCommCode) {
        this.csProdCommCode = csProdCommCode;
    }

    /**
     * Theme/Floorset
     * 
     */
    @JsonProperty("csThemeFlst")
    public String getCsThemeFlst() {
        return csThemeFlst;
    }

    /**
     * Theme/Floorset
     * 
     */
    @JsonProperty("csThemeFlst")
    public void setCsThemeFlst(String csThemeFlst) {
        this.csThemeFlst = csThemeFlst;
    }

    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("csVendorCurrency")
    public String getCsVendorCurrency() {
        return csVendorCurrency;
    }

    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("csVendorCurrency")
    public void setCsVendorCurrency(String csVendorCurrency) {
        this.csVendorCurrency = csVendorCurrency;
    }

    /**
     * Vendor Currency Conv. Rate
     * 
     */
    @JsonProperty("csVendorCurrConvRate")
    public Double getCsVendorCurrConvRate() {
        return csVendorCurrConvRate;
    }

    /**
     * Vendor Currency Conv. Rate
     * 
     */
    @JsonProperty("csVendorCurrConvRate")
    public void setCsVendorCurrConvRate(Double csVendorCurrConvRate) {
        this.csVendorCurrConvRate = csVendorCurrConvRate;
    }

    /**
     * FG Cost GBP
     * 
     */
    @JsonProperty("csFGCostGBP")
    public Double getCsFGCostGBP() {
        return csFGCostGBP;
    }

    /**
     * FG Cost GBP
     * 
     */
    @JsonProperty("csFGCostGBP")
    public void setCsFGCostGBP(Double csFGCostGBP) {
        this.csFGCostGBP = csFGCostGBP;
    }

    /**
     * Freight (% of FG Cost GBP)
     * 
     */
    @JsonProperty("csFreightFOBPercent")
    public Double getCsFreightFOBPercent() {
        return csFreightFOBPercent;
    }

    /**
     * Freight (% of FG Cost GBP)
     * 
     */
    @JsonProperty("csFreightFOBPercent")
    public void setCsFreightFOBPercent(Double csFreightFOBPercent) {
        this.csFreightFOBPercent = csFreightFOBPercent;
    }

    /**
     * Landed Cost GBP
     * 
     */
    @JsonProperty("csLandCostGBP")
    public Double getCsLandCostGBP() {
        return csLandCostGBP;
    }

    /**
     * Landed Cost GBP
     * 
     */
    @JsonProperty("csLandCostGBP")
    public void setCsLandCostGBP(Double csLandCostGBP) {
        this.csLandCostGBP = csLandCostGBP;
    }

    /**
     * Last Modified- formt:date-time
     * 
     */
    @JsonProperty("csLastModify")
    public String getCsLastModify() {
        return csLastModify;
    }

    /**
     * Last Modified- formt:date-time
     * 
     */
    @JsonProperty("csLastModify")
    public void setCsLastModify(String csLastModify) {
        this.csLastModify = csLastModify;
    }

    /**
     * Modified By
     * 
     */
    @JsonProperty("csModifier")
    public String getCsModifier() {
        return csModifier;
    }

    /**
     * Modified By
     * 
     */
    @JsonProperty("csModifier")
    public void setCsModifier(String csModifier) {
        this.csModifier = csModifier;
    }

    /**
     * Primary Cost Sheet
     * 
     */
    @JsonProperty("csPrimary")
    public Boolean getCsPrimary() {
        return csPrimary;
    }

    /**
     * Primary Cost Sheet
     * 
     */
    @JsonProperty("csPrimary")
    public void setCsPrimary(Boolean csPrimary) {
        this.csPrimary = csPrimary;
    }

    /**
     * Product Duty Rate
     * 
     */
    @JsonProperty("csProdDutyRate")
    public Double getCsProdDutyRate() {
        return csProdDutyRate;
    }

    /**
     * Product Duty Rate
     * 
     */
    @JsonProperty("csProdDutyRate")
    public void setCsProdDutyRate(Double csProdDutyRate) {
        this.csProdDutyRate = csProdDutyRate;
    }

    /**
     * GBP Notional Margin (%)
     * 
     */
    @JsonProperty("csNotionMarginPercent")
    public Double getCsNotionMarginPercent() {
        return csNotionMarginPercent;
    }

    /**
     * GBP Notional Margin (%)
     * 
     */
    @JsonProperty("csNotionMarginPercent")
    public void setCsNotionMarginPercent(Double csNotionMarginPercent) {
        this.csNotionMarginPercent = csNotionMarginPercent;
    }

    /**
     * Target GBP Notional Margin (%)
     * 
     */
    @JsonProperty("csTargetNotionMarginPercent")
    public Double getCsTargetNotionMarginPercent() {
        return csTargetNotionMarginPercent;
    }

    /**
     * Target GBP Notional Margin (%)
     * 
     */
    @JsonProperty("csTargetNotionMarginPercent")
    public void setCsTargetNotionMarginPercent(Double csTargetNotionMarginPercent) {
        this.csTargetNotionMarginPercent = csTargetNotionMarginPercent;
    }

    /**
     * Target FG Cost Local
     * 
     */
    @JsonProperty("csFGCostLocal")
    public Double getCsFGCostLocal() {
        return csFGCostLocal;
    }

    /**
     * Target FG Cost Local
     * 
     */
    @JsonProperty("csFGCostLocal")
    public void setCsFGCostLocal(Double csFGCostLocal) {
        this.csFGCostLocal = csFGCostLocal;
    }

    /**
     * Actual GBP Retail [£]
     * 
     */
    @JsonProperty("csActualGBP")
    public Double getCsActualGBP() {
        return csActualGBP;
    }

    /**
     * Actual GBP Retail [£]
     * 
     */
    @JsonProperty("csActualGBP")
    public void setCsActualGBP(Double csActualGBP) {
        this.csActualGBP = csActualGBP;
    }

    /**
     * VAT Rate (%)
     * 
     */
    @JsonProperty("csVATPercent")
    public Double getCsVATPercent() {
        return csVATPercent;
    }

    /**
     * VAT Rate (%)
     * 
     */
    @JsonProperty("csVATPercent")
    public void setCsVATPercent(Double csVATPercent) {
        this.csVATPercent = csVATPercent;
    }

    /**
     * Vendor FG cost
     * 
     */
    @JsonProperty("csVendorFGCost")
    public Double getCsVendorFGCost() {
        return csVendorFGCost;
    }

    /**
     * Vendor FG cost
     * 
     */
    @JsonProperty("csVendorFGCost")
    public void setCsVendorFGCost(Double csVendorFGCost) {
        this.csVendorFGCost = csVendorFGCost;
    }

    /**
     * Return 'DELETE' in case the cost sheet is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case the cost sheet is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    @JsonProperty("colourways")
    public List<Colourway> getColourways() {
        return colourways;
    }

    @JsonProperty("colourways")
    public void setColourways(List<Colourway> colourways) {
        this.colourways = colourways;
    }

}


package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "prodSsnLastMod",
    "suggestCOO",
    "cfStyle",
    "prodStatus",
    "keyDrive",
    "actualGBP",
    "adjstEuro",
    "adjstHKD",
    "adjstUSD",
    "targetRMCost",
    "targetPrice",
    "targetMargin",
    "targetLandCost",
    "retailPriceLock",
    "initialGBPRetail",
    "priceValid",
    "derivedDuty",
    "derivedFreight",
    "protoFGCostGBP",
    "protoLandedCostGBP",
    "protoRetailGBP",
    "protoRetailMarginGBP",
    "protoFGVendorCost",
    "initialDutyRate",
    "initialFreight",
    "vatRate",
    "vendorCurrency",
    "vendorCurrencyConvRate",
    "proposedEuro",
    "proposedHKD",
    "proposedUSD",
    "season",
    "CRUD",
    "prodPlaceholder"
})
public class ProductSeason implements Serializable
{

    /**
     * Product Season Last Madified Timestamp-   format:date-time
     * (Required)
     * 
     */
    @JsonProperty("prodSsnLastMod")
    @JsonPropertyDescription("Product Season Last Madified Timestamp-   format:date-time")
    private String prodSsnLastMod;
    /**
     * Suggested COO
     * 
     */
    @JsonProperty("suggestCOO")
    @JsonPropertyDescription("Suggested COO")
    private String suggestCOO;
    /**
     * Carry Forward Style
     * 
     */
    @JsonProperty("cfStyle")
    @JsonPropertyDescription("Carry Forward Style")
    private Boolean cfStyle;
    /**
     * Product Status
     * 
     */
    @JsonProperty("prodStatus")
    @JsonPropertyDescription("Product Status")
    private String prodStatus;
    /**
     * Target Key Driver
     * 
     */
    @JsonProperty("keyDrive")
    @JsonPropertyDescription("Target Key Driver")
    private String keyDrive;
    /**
     * Actual GBP Retail  [£]
     * 
     */
    @JsonProperty("actualGBP")
    @JsonPropertyDescription("Actual GBP Retail  [\u00a3]")
    private Double actualGBP;
    /**
     * Adjusted EUR Retail [€]
     * 
     */
    @JsonProperty("adjstEuro")
    @JsonPropertyDescription("Adjusted EUR Retail [\u20ac]")
    private String adjstEuro;
    /**
     * Adjusted HKD Retail [HKD]
     * 
     */
    @JsonProperty("adjstHKD")
    @JsonPropertyDescription("Adjusted HKD Retail [HKD]")
    private String adjstHKD;
    /**
     * Adjusted USD Retail [$]
     * 
     */
    @JsonProperty("adjstUSD")
    @JsonPropertyDescription("Adjusted USD Retail [$]")
    private String adjstUSD;
    /**
     * Target RM Cost
     * 
     */
    @JsonProperty("targetRMCost")
    @JsonPropertyDescription("Target RM Cost")
    private Double targetRMCost;
    /**
     * Target Retail Price
     * 
     */
    @JsonProperty("targetPrice")
    @JsonPropertyDescription("Target Retail Price")
    private Double targetPrice;
    /**
     * Target Retail Margin
     * 
     */
    @JsonProperty("targetMargin")
    @JsonPropertyDescription("Target Retail Margin")
    private Double targetMargin;
    /**
     * Target Landed Cost
     * 
     */
    @JsonProperty("targetLandCost")
    @JsonPropertyDescription("Target Landed Cost")
    private Double targetLandCost;
    /**
     * Retail Price Lock
     * 
     */
    @JsonProperty("retailPriceLock")
    @JsonPropertyDescription("Retail Price Lock")
    private Boolean retailPriceLock;
    /**
     * Initial GBP Retail [£]
     * 
     */
    @JsonProperty("initialGBPRetail")
    @JsonPropertyDescription("Initial GBP Retail [\u00a3]")
    private Double initialGBPRetail;
    /**
     * Price Validation
     * 
     */
    @JsonProperty("priceValid")
    @JsonPropertyDescription("Price Validation")
    private String priceValid;
    /**
     * Derived Duty
     * 
     */
    @JsonProperty("derivedDuty")
    @JsonPropertyDescription("Derived Duty")
    private Double derivedDuty;
    /**
     * Derived Freight
     * 
     */
    @JsonProperty("derivedFreight")
    @JsonPropertyDescription("Derived Freight")
    private Double derivedFreight;
    /**
     * Proto FG Cost GBP
     * 
     */
    @JsonProperty("protoFGCostGBP")
    @JsonPropertyDescription("Proto FG Cost GBP")
    private Double protoFGCostGBP;
    /**
     * Proto Landed Cost GBP
     * 
     */
    @JsonProperty("protoLandedCostGBP")
    @JsonPropertyDescription("Proto Landed Cost GBP")
    private Double protoLandedCostGBP;
    /**
     * Proto Retail GBP
     * 
     */
    @JsonProperty("protoRetailGBP")
    @JsonPropertyDescription("Proto Retail GBP")
    private Double protoRetailGBP;
    /**
     * Proto Retail Margin GBP
     * 
     */
    @JsonProperty("protoRetailMarginGBP")
    @JsonPropertyDescription("Proto Retail Margin GBP")
    private Double protoRetailMarginGBP;
    /**
     * FG Vendor Proto Cost
     * 
     */
    @JsonProperty("protoFGVendorCost")
    @JsonPropertyDescription("FG Vendor Proto Cost")
    private Double protoFGVendorCost;
    /**
     * Initial Duty Rate (%)
     * 
     */
    @JsonProperty("initialDutyRate")
    @JsonPropertyDescription("Initial Duty Rate (%)")
    private String initialDutyRate;
    /**
     * Initial Freight (%)
     * 
     */
    @JsonProperty("initialFreight")
    @JsonPropertyDescription("Initial Freight (%)")
    private String initialFreight;
    /**
     * VAT Rate
     * 
     */
    @JsonProperty("vatRate")
    @JsonPropertyDescription("VAT Rate")
    private Double vatRate;
    /**
     * Vendor Currency
     * 
     */
    @JsonProperty("vendorCurrency")
    @JsonPropertyDescription("Vendor Currency")
    private String vendorCurrency;
    /**
     * Vendor Currency Conv. Rate
     * 
     */
    @JsonProperty("vendorCurrencyConvRate")
    @JsonPropertyDescription("Vendor Currency Conv. Rate")
    private Double vendorCurrencyConvRate;
    /**
     * Proposed EUR Retail [€]
     * 
     */
    @JsonProperty("proposedEuro")
    @JsonPropertyDescription("Proposed EUR Retail [\u20ac]")
    private Double proposedEuro;
    /**
     * Proposed HKD Retail [HKD]
     * 
     */
    @JsonProperty("proposedHKD")
    @JsonPropertyDescription("Proposed HKD Retail [HKD]")
    private Double proposedHKD;
    /**
     * Proposed USD Retail [$]
     * 
     */
    @JsonProperty("proposedUSD")
    @JsonPropertyDescription("Proposed USD Retail [$]")
    private Double proposedUSD;
    @JsonProperty("season")
    private Season season;
    /**
     * Return 'DELETE' in case product is removed from season
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case product is removed from season")
    private String cRUD;
    @JsonProperty("prodPlaceholder")
    private ProdPlaceholder prodPlaceholder;
    private final static long serialVersionUID = -8388767136085515334L;

    /**
     * Product Season Last Madified Timestamp-   format:date-time
     * (Required)
     * 
     */
    @JsonProperty("prodSsnLastMod")
    public String getProdSsnLastMod() {
        return prodSsnLastMod;
    }

    /**
     * Product Season Last Madified Timestamp-   format:date-time
     * (Required)
     * 
     */
    @JsonProperty("prodSsnLastMod")
    public void setProdSsnLastMod(String prodSsnLastMod) {
        this.prodSsnLastMod = prodSsnLastMod;
    }

    /**
     * Suggested COO
     * 
     */
    @JsonProperty("suggestCOO")
    public String getSuggestCOO() {
        return suggestCOO;
    }

    /**
     * Suggested COO
     * 
     */
    @JsonProperty("suggestCOO")
    public void setSuggestCOO(String suggestCOO) {
        this.suggestCOO = suggestCOO;
    }

    /**
     * Carry Forward Style
     * 
     */
    @JsonProperty("cfStyle")
    public Boolean getCfStyle() {
        return cfStyle;
    }

    /**
     * Carry Forward Style
     * 
     */
    @JsonProperty("cfStyle")
    public void setCfStyle(Boolean cfStyle) {
        this.cfStyle = cfStyle;
    }

    /**
     * Product Status
     * 
     */
    @JsonProperty("prodStatus")
    public String getProdStatus() {
        return prodStatus;
    }

    /**
     * Product Status
     * 
     */
    @JsonProperty("prodStatus")
    public void setProdStatus(String prodStatus) {
        this.prodStatus = prodStatus;
    }

    /**
     * Target Key Driver
     * 
     */
    @JsonProperty("keyDrive")
    public String getKeyDrive() {
        return keyDrive;
    }

    /**
     * Target Key Driver
     * 
     */
    @JsonProperty("keyDrive")
    public void setKeyDrive(String keyDrive) {
        this.keyDrive = keyDrive;
    }

    /**
     * Actual GBP Retail  [£]
     * 
     */
    @JsonProperty("actualGBP")
    public Double getActualGBP() {
        return actualGBP;
    }

    /**
     * Actual GBP Retail  [£]
     * 
     */
    @JsonProperty("actualGBP")
    public void setActualGBP(Double actualGBP) {
        this.actualGBP = actualGBP;
    }

    /**
     * Adjusted EUR Retail [€]
     * 
     */
    @JsonProperty("adjstEuro")
    public String getAdjstEuro() {
        return adjstEuro;
    }

    /**
     * Adjusted EUR Retail [€]
     * 
     */
    @JsonProperty("adjstEuro")
    public void setAdjstEuro(String adjstEuro) {
        this.adjstEuro = adjstEuro;
    }

    /**
     * Adjusted HKD Retail [HKD]
     * 
     */
    @JsonProperty("adjstHKD")
    public String getAdjstHKD() {
        return adjstHKD;
    }

    /**
     * Adjusted HKD Retail [HKD]
     * 
     */
    @JsonProperty("adjstHKD")
    public void setAdjstHKD(String adjstHKD) {
        this.adjstHKD = adjstHKD;
    }

    /**
     * Adjusted USD Retail [$]
     * 
     */
    @JsonProperty("adjstUSD")
    public String getAdjstUSD() {
        return adjstUSD;
    }

    /**
     * Adjusted USD Retail [$]
     * 
     */
    @JsonProperty("adjstUSD")
    public void setAdjstUSD(String adjstUSD) {
        this.adjstUSD = adjstUSD;
    }

    /**
     * Target RM Cost
     * 
     */
    @JsonProperty("targetRMCost")
    public Double getTargetRMCost() {
        return targetRMCost;
    }

    /**
     * Target RM Cost
     * 
     */
    @JsonProperty("targetRMCost")
    public void setTargetRMCost(Double targetRMCost) {
        this.targetRMCost = targetRMCost;
    }

    /**
     * Target Retail Price
     * 
     */
    @JsonProperty("targetPrice")
    public Double getTargetPrice() {
        return targetPrice;
    }

    /**
     * Target Retail Price
     * 
     */
    @JsonProperty("targetPrice")
    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

    /**
     * Target Retail Margin
     * 
     */
    @JsonProperty("targetMargin")
    public Double getTargetMargin() {
        return targetMargin;
    }

    /**
     * Target Retail Margin
     * 
     */
    @JsonProperty("targetMargin")
    public void setTargetMargin(Double targetMargin) {
        this.targetMargin = targetMargin;
    }

    /**
     * Target Landed Cost
     * 
     */
    @JsonProperty("targetLandCost")
    public Double getTargetLandCost() {
        return targetLandCost;
    }

    /**
     * Target Landed Cost
     * 
     */
    @JsonProperty("targetLandCost")
    public void setTargetLandCost(Double targetLandCost) {
        this.targetLandCost = targetLandCost;
    }

    /**
     * Retail Price Lock
     * 
     */
    @JsonProperty("retailPriceLock")
    public Boolean getRetailPriceLock() {
        return retailPriceLock;
    }

    /**
     * Retail Price Lock
     * 
     */
    @JsonProperty("retailPriceLock")
    public void setRetailPriceLock(Boolean retailPriceLock) {
        this.retailPriceLock = retailPriceLock;
    }

    /**
     * Initial GBP Retail [£]
     * 
     */
    @JsonProperty("initialGBPRetail")
    public Double getInitialGBPRetail() {
        return initialGBPRetail;
    }

    /**
     * Initial GBP Retail [£]
     * 
     */
    @JsonProperty("initialGBPRetail")
    public void setInitialGBPRetail(Double initialGBPRetail) {
        this.initialGBPRetail = initialGBPRetail;
    }

    /**
     * Price Validation
     * 
     */
    @JsonProperty("priceValid")
    public String getPriceValid() {
        return priceValid;
    }

    /**
     * Price Validation
     * 
     */
    @JsonProperty("priceValid")
    public void setPriceValid(String priceValid) {
        this.priceValid = priceValid;
    }

    /**
     * Derived Duty
     * 
     */
    @JsonProperty("derivedDuty")
    public Double getDerivedDuty() {
        return derivedDuty;
    }

    /**
     * Derived Duty
     * 
     */
    @JsonProperty("derivedDuty")
    public void setDerivedDuty(Double derivedDuty) {
        this.derivedDuty = derivedDuty;
    }

    /**
     * Derived Freight
     * 
     */
    @JsonProperty("derivedFreight")
    public Double getDerivedFreight() {
        return derivedFreight;
    }

    /**
     * Derived Freight
     * 
     */
    @JsonProperty("derivedFreight")
    public void setDerivedFreight(Double derivedFreight) {
        this.derivedFreight = derivedFreight;
    }

    /**
     * Proto FG Cost GBP
     * 
     */
    @JsonProperty("protoFGCostGBP")
    public Double getProtoFGCostGBP() {
        return protoFGCostGBP;
    }

    /**
     * Proto FG Cost GBP
     * 
     */
    @JsonProperty("protoFGCostGBP")
    public void setProtoFGCostGBP(Double protoFGCostGBP) {
        this.protoFGCostGBP = protoFGCostGBP;
    }

    /**
     * Proto Landed Cost GBP
     * 
     */
    @JsonProperty("protoLandedCostGBP")
    public Double getProtoLandedCostGBP() {
        return protoLandedCostGBP;
    }

    /**
     * Proto Landed Cost GBP
     * 
     */
    @JsonProperty("protoLandedCostGBP")
    public void setProtoLandedCostGBP(Double protoLandedCostGBP) {
        this.protoLandedCostGBP = protoLandedCostGBP;
    }

    /**
     * Proto Retail GBP
     * 
     */
    @JsonProperty("protoRetailGBP")
    public Double getProtoRetailGBP() {
        return protoRetailGBP;
    }

    /**
     * Proto Retail GBP
     * 
     */
    @JsonProperty("protoRetailGBP")
    public void setProtoRetailGBP(Double protoRetailGBP) {
        this.protoRetailGBP = protoRetailGBP;
    }

    /**
     * Proto Retail Margin GBP
     * 
     */
    @JsonProperty("protoRetailMarginGBP")
    public Double getProtoRetailMarginGBP() {
        return protoRetailMarginGBP;
    }

    /**
     * Proto Retail Margin GBP
     * 
     */
    @JsonProperty("protoRetailMarginGBP")
    public void setProtoRetailMarginGBP(Double protoRetailMarginGBP) {
        this.protoRetailMarginGBP = protoRetailMarginGBP;
    }

    /**
     * FG Vendor Proto Cost
     * 
     */
    @JsonProperty("protoFGVendorCost")
    public Double getProtoFGVendorCost() {
        return protoFGVendorCost;
    }

    /**
     * FG Vendor Proto Cost
     * 
     */
    @JsonProperty("protoFGVendorCost")
    public void setProtoFGVendorCost(Double protoFGVendorCost) {
        this.protoFGVendorCost = protoFGVendorCost;
    }

    /**
     * Initial Duty Rate (%)
     * 
     */
    @JsonProperty("initialDutyRate")
    public String getInitialDutyRate() {
        return initialDutyRate;
    }

    /**
     * Initial Duty Rate (%)
     * 
     */
    @JsonProperty("initialDutyRate")
    public void setInitialDutyRate(String initialDutyRate) {
        this.initialDutyRate = initialDutyRate;
    }

    /**
     * Initial Freight (%)
     * 
     */
    @JsonProperty("initialFreight")
    public String getInitialFreight() {
        return initialFreight;
    }

    /**
     * Initial Freight (%)
     * 
     */
    @JsonProperty("initialFreight")
    public void setInitialFreight(String initialFreight) {
        this.initialFreight = initialFreight;
    }

    /**
     * VAT Rate
     * 
     */
    @JsonProperty("vatRate")
    public Double getVatRate() {
        return vatRate;
    }

    /**
     * VAT Rate
     * 
     */
    @JsonProperty("vatRate")
    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
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
     * Vendor Currency Conv. Rate
     * 
     */
    @JsonProperty("vendorCurrencyConvRate")
    public Double getVendorCurrencyConvRate() {
        return vendorCurrencyConvRate;
    }

    /**
     * Vendor Currency Conv. Rate
     * 
     */
    @JsonProperty("vendorCurrencyConvRate")
    public void setVendorCurrencyConvRate(Double vendorCurrencyConvRate) {
        this.vendorCurrencyConvRate = vendorCurrencyConvRate;
    }

    /**
     * Proposed EUR Retail [€]
     * 
     */
    @JsonProperty("proposedEuro")
    public Double getProposedEuro() {
        return proposedEuro;
    }

    /**
     * Proposed EUR Retail [€]
     * 
     */
    @JsonProperty("proposedEuro")
    public void setProposedEuro(Double proposedEuro) {
        this.proposedEuro = proposedEuro;
    }

    /**
     * Proposed HKD Retail [HKD]
     * 
     */
    @JsonProperty("proposedHKD")
    public Double getProposedHKD() {
        return proposedHKD;
    }

    /**
     * Proposed HKD Retail [HKD]
     * 
     */
    @JsonProperty("proposedHKD")
    public void setProposedHKD(Double proposedHKD) {
        this.proposedHKD = proposedHKD;
    }

    /**
     * Proposed USD Retail [$]
     * 
     */
    @JsonProperty("proposedUSD")
    public Double getProposedUSD() {
        return proposedUSD;
    }

    /**
     * Proposed USD Retail [$]
     * 
     */
    @JsonProperty("proposedUSD")
    public void setProposedUSD(Double proposedUSD) {
        this.proposedUSD = proposedUSD;
    }

    @JsonProperty("season")
    public Season getSeason() {
        return season;
    }

    @JsonProperty("season")
    public void setSeason(Season season) {
        this.season = season;
    }

    /**
     * Return 'DELETE' in case product is removed from season
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case product is removed from season
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    @JsonProperty("prodPlaceholder")
    public ProdPlaceholder getProdPlaceholder() {
        return prodPlaceholder;
    }

    @JsonProperty("prodPlaceholder")
    public void setProdPlaceholder(ProdPlaceholder prodPlaceholder) {
        this.prodPlaceholder = prodPlaceholder;
    }

}

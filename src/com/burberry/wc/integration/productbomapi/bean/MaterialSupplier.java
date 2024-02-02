
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pricingUom",
    "suppMatCode",
    "matPrice",
    "supplierMaster"
})
public class MaterialSupplier implements Serializable
{

    /**
     * Pricing UOM
     * 
     */
    @JsonProperty("pricingUom")
    @JsonPropertyDescription("Pricing UOM")
    private String pricingUom;
    /**
     * Supplier Material Code
     * 
     */
    @JsonProperty("suppMatCode")
    @JsonPropertyDescription("Supplier Material Code")
    private String suppMatCode;
    /**
     * Material Price
     * 
     */
    @JsonProperty("matPrice")
    @JsonPropertyDescription("Material Price")
    private Double matPrice;
    @JsonProperty("supplierMaster")
    private SupplierMaster supplierMaster;
    private final static long serialVersionUID = -5375963134212808017L;

    /**
     * Pricing UOM
     * 
     */
    @JsonProperty("pricingUom")
    public String getPricingUom() {
        return pricingUom;
    }

    /**
     * Pricing UOM
     * 
     */
    @JsonProperty("pricingUom")
    public void setPricingUom(String pricingUom) {
        this.pricingUom = pricingUom;
    }

    /**
     * Supplier Material Code
     * 
     */
    @JsonProperty("suppMatCode")
    public String getSuppMatCode() {
        return suppMatCode;
    }

    /**
     * Supplier Material Code
     * 
     */
    @JsonProperty("suppMatCode")
    public void setSuppMatCode(String suppMatCode) {
        this.suppMatCode = suppMatCode;
    }

    /**
     * Material Price
     * 
     */
    @JsonProperty("matPrice")
    public Double getMatPrice() {
        return matPrice;
    }

    /**
     * Material Price
     * 
     */
    @JsonProperty("matPrice")
    public void setMatPrice(Double matPrice) {
        this.matPrice = matPrice;
    }

    @JsonProperty("supplierMaster")
    public SupplierMaster getSupplierMaster() {
        return supplierMaster;
    }

    @JsonProperty("supplierMaster")
    public void setSupplierMaster(SupplierMaster supplierMaster) {
        this.supplierMaster = supplierMaster;
    }

}

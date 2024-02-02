
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "suppRefNo",
    "suppName",
    "CRUD"
})
public class PaletteSupplier implements Serializable
{

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    @JsonPropertyDescription("Supplier Reference Number (sequence)")
    private String suppRefNo;
    /**
     * Supplier name
     * 
     */
    @JsonProperty("suppName")
    @JsonPropertyDescription("Supplier name")
    private String suppName;
    /**
     * Return 'DELETE' in case material supplier combination is deleted from the palette
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case material supplier combination is deleted from the palette")
    private String cRUD;
    private final static long serialVersionUID = -3928362965901579534L;

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    public String getSuppRefNo() {
        return suppRefNo;
    }

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    public void setSuppRefNo(String suppRefNo) {
        this.suppRefNo = suppRefNo;
    }

    /**
     * Supplier name
     * 
     */
    @JsonProperty("suppName")
    public String getSuppName() {
        return suppName;
    }

    /**
     * Supplier name
     * 
     */
    @JsonProperty("suppName")
    public void setSuppName(String suppName) {
        this.suppName = suppName;
    }

    /**
     * Return 'DELETE' in case material supplier combination is deleted from the palette
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case material supplier combination is deleted from the palette
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

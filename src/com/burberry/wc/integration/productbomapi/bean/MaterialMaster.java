
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "materialId",
    "materialName",
    "matStatus",
    "contentSearch",
    "rawMatCode",
    "matType",
    "devSeason",
    "swingTktId",
    "materialSupplier"
})
public class MaterialMaster implements Serializable
{

    /**
     * Material Id
     * 
     */
    @JsonProperty("materialId")
    @JsonPropertyDescription("Material Id")
    private Double materialId;
    /**
     * Material Name
     * 
     */
    @JsonProperty("materialName")
    @JsonPropertyDescription("Material Name")
    private String materialName;
    /**
     * Status
     * 
     */
    @JsonProperty("matStatus")
    @JsonPropertyDescription("Status")
    private String matStatus;
    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    @JsonPropertyDescription("Content (Searchable)")
    private String contentSearch;
    /**
     * Raw Material Code
     * 
     */
    @JsonProperty("rawMatCode")
    @JsonPropertyDescription("Raw Material Code")
    private String rawMatCode;
    /**
     * Material Type
     * 
     */
    @JsonProperty("matType")
    @JsonPropertyDescription("Material Type")
    private String matType;
    /**
     * Dev. Season
     * 
     */
    @JsonProperty("devSeason")
    @JsonPropertyDescription("Dev. Season")
    private String devSeason;
    /**
     * Swing Ticket Id
     * 
     */
    @JsonProperty("swingTktId")
    @JsonPropertyDescription("Swing Ticket Id")
    private String swingTktId;
    @JsonProperty("materialSupplier")
    private MaterialSupplier materialSupplier;
    private final static long serialVersionUID = -1687964160553789843L;

    /**
     * Material Id
     * 
     */
    @JsonProperty("materialId")
    public Double getMaterialId() {
        return materialId;
    }

    /**
     * Material Id
     * 
     */
    @JsonProperty("materialId")
    public void setMaterialId(Double materialId) {
        this.materialId = materialId;
    }

    /**
     * Material Name
     * 
     */
    @JsonProperty("materialName")
    public String getMaterialName() {
        return materialName;
    }

    /**
     * Material Name
     * 
     */
    @JsonProperty("materialName")
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    /**
     * Status
     * 
     */
    @JsonProperty("matStatus")
    public String getMatStatus() {
        return matStatus;
    }

    /**
     * Status
     * 
     */
    @JsonProperty("matStatus")
    public void setMatStatus(String matStatus) {
        this.matStatus = matStatus;
    }

    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    public String getContentSearch() {
        return contentSearch;
    }

    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    public void setContentSearch(String contentSearch) {
        this.contentSearch = contentSearch;
    }

    /**
     * Raw Material Code
     * 
     */
    @JsonProperty("rawMatCode")
    public String getRawMatCode() {
        return rawMatCode;
    }

    /**
     * Raw Material Code
     * 
     */
    @JsonProperty("rawMatCode")
    public void setRawMatCode(String rawMatCode) {
        this.rawMatCode = rawMatCode;
    }

    /**
     * Material Type
     * 
     */
    @JsonProperty("matType")
    public String getMatType() {
        return matType;
    }

    /**
     * Material Type
     * 
     */
    @JsonProperty("matType")
    public void setMatType(String matType) {
        this.matType = matType;
    }

    /**
     * Dev. Season
     * 
     */
    @JsonProperty("devSeason")
    public String getDevSeason() {
        return devSeason;
    }

    /**
     * Dev. Season
     * 
     */
    @JsonProperty("devSeason")
    public void setDevSeason(String devSeason) {
        this.devSeason = devSeason;
    }

    /**
     * Swing Ticket Id
     * 
     */
    @JsonProperty("swingTktId")
    public String getSwingTktId() {
        return swingTktId;
    }

    /**
     * Swing Ticket Id
     * 
     */
    @JsonProperty("swingTktId")
    public void setSwingTktId(String swingTktId) {
        this.swingTktId = swingTktId;
    }

    @JsonProperty("materialSupplier")
    public MaterialSupplier getMaterialSupplier() {
        return materialSupplier;
    }

    @JsonProperty("materialSupplier")
    public void setMaterialSupplier(MaterialSupplier materialSupplier) {
        this.materialSupplier = materialSupplier;
    }

}

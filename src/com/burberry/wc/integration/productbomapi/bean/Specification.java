
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "specName",
    "specUniqId",
    "primarySpec",
    "CRUD",
    "BOM"
})
public class Specification implements Serializable
{

    /**
     * Name
     * 
     */
    @JsonProperty("specName")
    @JsonPropertyDescription("Name")
    private String specName;
    /**
     * Specification Unique ID (System)
     * 
     */
    @JsonProperty("specUniqId")
    @JsonPropertyDescription("Specification Unique ID (System)")
    private String specUniqId;
    /**
     * Primary Specifcation?
     * 
     */
    @JsonProperty("primarySpec")
    @JsonPropertyDescription("Primary Specifcation?")
    private Boolean primarySpec;
    /**
     * Return 'DELETE' in case specification is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case specification is deleted")
    private String cRUD;
    @JsonProperty("BOM")
    private List<BOM> bOM = null;
    private final static long serialVersionUID = -1567671960005900827L;

    /**
     * Name
     * 
     */
    @JsonProperty("specName")
    public String getSpecName() {
        return specName;
    }

    /**
     * Name
     * 
     */
    @JsonProperty("specName")
    public void setSpecName(String specName) {
        this.specName = specName;
    }

    /**
     * Specification Unique ID (System)
     * 
     */
    @JsonProperty("specUniqId")
    public String getSpecUniqId() {
        return specUniqId;
    }

    /**
     * Specification Unique ID (System)
     * 
     */
    @JsonProperty("specUniqId")
    public void setSpecUniqId(String specUniqId) {
        this.specUniqId = specUniqId;
    }

    /**
     * Primary Specifcation?
     * 
     */
    @JsonProperty("primarySpec")
    public Boolean getPrimarySpec() {
        return primarySpec;
    }

    /**
     * Primary Specifcation?
     * 
     */
    @JsonProperty("primarySpec")
    public void setPrimarySpec(Boolean primarySpec) {
        this.primarySpec = primarySpec;
    }

    /**
     * Return 'DELETE' in case specification is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case specification is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    @JsonProperty("BOM")
    public List<BOM> getBOM() {
        return bOM;
    }

    @JsonProperty("BOM")
    public void setBOM(List<BOM> bOM) {
        this.bOM = bOM;
    }

}

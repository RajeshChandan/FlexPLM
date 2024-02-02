
package com.burberry.wc.integration.productapi.bean;

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
    "images",
    "specDocuments"
})
public class Specification implements Serializable
{

    /**
     * Specification Name
     * 
     */
    @JsonProperty("specName")
    @JsonPropertyDescription("Specification Name")
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
    @JsonProperty("images")
    private List<Image> images = null;
    @JsonProperty("specDocuments")
    private List<SpecDocument> specDocuments = null;
    private final static long serialVersionUID = -219268953330745370L;

    /**
     * Specification Name
     * 
     */
    @JsonProperty("specName")
    public String getSpecName() {
        return specName;
    }

    /**
     * Specification Name
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

    @JsonProperty("images")
    public List<Image> getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(List<Image> images) {
        this.images = images;
    }

    @JsonProperty("specDocuments")
    public List<SpecDocument> getSpecDocuments() {
        return specDocuments;
    }

    @JsonProperty("specDocuments")
    public void setSpecDocuments(List<SpecDocument> specDocuments) {
        this.specDocuments = specDocuments;
    }

}

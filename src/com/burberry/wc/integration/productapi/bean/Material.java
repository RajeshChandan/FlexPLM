
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "matType",
    "matSubType",
    "matDesc",
    "contentSearch",
    "matImgUrl"
})
public class Material implements Serializable
{

    /**
     * Material Type
     * 
     */
    @JsonProperty("matType")
    @JsonPropertyDescription("Material Type")
    private String matType;
    /**
     * Material Sub Type
     * 
     */
    @JsonProperty("matSubType")
    @JsonPropertyDescription("Material Sub Type")
    private String matSubType;
    /**
     * Material Description
     * 
     */
    @JsonProperty("matDesc")
    @JsonPropertyDescription("Material Description")
    private String matDesc;
    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    @JsonPropertyDescription("Content (Searchable)")
    private String contentSearch;
    /**
     * Material Primary Image URL
     * 
     */
    @JsonProperty("matImgUrl")
    @JsonPropertyDescription("Material Primary Image URL")
    private String matImgUrl;
    private final static long serialVersionUID = -1727695710832646975L;

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
     * Material Sub Type
     * 
     */
    @JsonProperty("matSubType")
    public String getMatSubType() {
        return matSubType;
    }

    /**
     * Material Sub Type
     * 
     */
    @JsonProperty("matSubType")
    public void setMatSubType(String matSubType) {
        this.matSubType = matSubType;
    }

    /**
     * Material Description
     * 
     */
    @JsonProperty("matDesc")
    public String getMatDesc() {
        return matDesc;
    }

    /**
     * Material Description
     * 
     */
    @JsonProperty("matDesc")
    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
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
     * Material Primary Image URL
     * 
     */
    @JsonProperty("matImgUrl")
    public String getMatImgUrl() {
        return matImgUrl;
    }

    /**
     * Material Primary Image URL
     * 
     */
    @JsonProperty("matImgUrl")
    public void setMatImgUrl(String matImgUrl) {
        this.matImgUrl = matImgUrl;
    }

}

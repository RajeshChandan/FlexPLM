
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "styleId",
    "styleName",
    "styleRef",
    "productSeason",
    "prodSource"
})
public class Product implements Serializable
{

    /**
     * Style Id
     * 
     */
    @JsonProperty("styleId")
    @JsonPropertyDescription("Style Id")
    private Integer styleId;
    /**
     * Style Name
     * 
     */
    @JsonProperty("styleName")
    @JsonPropertyDescription("Style Name")
    private String styleName;
    /**
     * Style Ref
     * 
     */
    @JsonProperty("styleRef")
    @JsonPropertyDescription("Style Ref")
    private String styleRef;
    @JsonProperty("productSeason")
    private List<ProductSeason> productSeason = null;
    @JsonProperty("prodSource")
    private ProdSource prodSource;
    private final static long serialVersionUID = -7263361785726239535L;

    /**
     * Style Id
     * 
     */
    @JsonProperty("styleId")
    public Integer getStyleId() {
        return styleId;
    }

    /**
     * Style Id
     * 
     */
    @JsonProperty("styleId")
    public void setStyleId(Integer styleId) {
        this.styleId = styleId;
    }

    /**
     * Style Name
     * 
     */
    @JsonProperty("styleName")
    public String getStyleName() {
        return styleName;
    }

    /**
     * Style Name
     * 
     */
    @JsonProperty("styleName")
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    /**
     * Style Ref
     * 
     */
    @JsonProperty("styleRef")
    public String getStyleRef() {
        return styleRef;
    }

    /**
     * Style Ref
     * 
     */
    @JsonProperty("styleRef")
    public void setStyleRef(String styleRef) {
        this.styleRef = styleRef;
    }

    @JsonProperty("productSeason")
    public List<ProductSeason> getProductSeason() {
        return productSeason;
    }

    @JsonProperty("productSeason")
    public void setProductSeason(List<ProductSeason> productSeason) {
        this.productSeason = productSeason;
    }

    @JsonProperty("prodSource")
    public ProdSource getProdSource() {
        return prodSource;
    }

    @JsonProperty("prodSource")
    public void setProdSource(ProdSource prodSource) {
        this.prodSource = prodSource;
    }

}


package com.burberry.wc.integration.productbomapi.bean;

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
    "division",
    "deptGrp",
    "mainRM",
    "colourways",
    "productSeason"
})
public class Style implements Serializable
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
    /**
     * Division
     * 
     */
    @JsonProperty("division")
    @JsonPropertyDescription("Division")
    private String division;
    /**
     * Dept/Group
     * 
     */
    @JsonProperty("deptGrp")
    @JsonPropertyDescription("Dept/Group")
    private String deptGrp;
    /**
     * Main RM
     * 
     */
    @JsonProperty("mainRM")
    @JsonPropertyDescription("Main RM")
    private String mainRM;
    @JsonProperty("colourways")
    private List<Colourway> colourways = null;
    @JsonProperty("productSeason")
    private List<ProductSeason> productSeason = null;
    private final static long serialVersionUID = 7441842051960809527L;

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

    /**
     * Division
     * 
     */
    @JsonProperty("division")
    public String getDivision() {
        return division;
    }

    /**
     * Division
     * 
     */
    @JsonProperty("division")
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * Dept/Group
     * 
     */
    @JsonProperty("deptGrp")
    public String getDeptGrp() {
        return deptGrp;
    }

    /**
     * Dept/Group
     * 
     */
    @JsonProperty("deptGrp")
    public void setDeptGrp(String deptGrp) {
        this.deptGrp = deptGrp;
    }

    /**
     * Main RM
     * 
     */
    @JsonProperty("mainRM")
    public String getMainRM() {
        return mainRM;
    }

    /**
     * Main RM
     * 
     */
    @JsonProperty("mainRM")
    public void setMainRM(String mainRM) {
        this.mainRM = mainRM;
    }

    @JsonProperty("colourways")
    public List<Colourway> getColourways() {
        return colourways;
    }

    @JsonProperty("colourways")
    public void setColourways(List<Colourway> colourways) {
        this.colourways = colourways;
    }

    @JsonProperty("productSeason")
    public List<ProductSeason> getProductSeason() {
        return productSeason;
    }

    @JsonProperty("productSeason")
    public void setProductSeason(List<ProductSeason> productSeason) {
        this.productSeason = productSeason;
    }

}

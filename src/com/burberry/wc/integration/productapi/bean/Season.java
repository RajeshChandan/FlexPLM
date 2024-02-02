
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "seasonName",
    "seasonType",
    "active",
    "year",
    "prodCollect",
    "strtDate",
    "endDate"
})
public class Season implements Serializable
{

    /**
     * Season Name
     * 
     */
    @JsonProperty("seasonName")
    @JsonPropertyDescription("Season Name")
    private String seasonName;
    /**
     * Season Type
     * 
     */
    @JsonProperty("seasonType")
    @JsonPropertyDescription("Season Type")
    private String seasonType;
    /**
     * Active
     * 
     */
    @JsonProperty("active")
    @JsonPropertyDescription("Active")
    private Boolean active;
    /**
     * Year
     * 
     */
    @JsonProperty("year")
    @JsonPropertyDescription("Year")
    private Integer year;
    /**
     * Product Collection
     * 
     */
    @JsonProperty("prodCollect")
    @JsonPropertyDescription("Product Collection")
    private String prodCollect;
    /**
     * Season Start Date- format:date-time
     * 
     */
    @JsonProperty("strtDate")
    @JsonPropertyDescription("Season Start Date- format:date-time")
    private String strtDate;
    /**
     * Season End Date- format:date-time
     * 
     */
    @JsonProperty("endDate")
    @JsonPropertyDescription("Season End Date- format:date-time")
    private String endDate;
    private final static long serialVersionUID = 6982675655459897150L;

    /**
     * Season Name
     * 
     */
    @JsonProperty("seasonName")
    public String getSeasonName() {
        return seasonName;
    }

    /**
     * Season Name
     * 
     */
    @JsonProperty("seasonName")
    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    /**
     * Season Type
     * 
     */
    @JsonProperty("seasonType")
    public String getSeasonType() {
        return seasonType;
    }

    /**
     * Season Type
     * 
     */
    @JsonProperty("seasonType")
    public void setSeasonType(String seasonType) {
        this.seasonType = seasonType;
    }

    /**
     * Active
     * 
     */
    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    /**
     * Active
     * 
     */
    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Year
     * 
     */
    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    /**
     * Year
     * 
     */
    @JsonProperty("year")
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Product Collection
     * 
     */
    @JsonProperty("prodCollect")
    public String getProdCollect() {
        return prodCollect;
    }

    /**
     * Product Collection
     * 
     */
    @JsonProperty("prodCollect")
    public void setProdCollect(String prodCollect) {
        this.prodCollect = prodCollect;
    }

    /**
     * Season Start Date- format:date-time
     * 
     */
    @JsonProperty("strtDate")
    public String getStrtDate() {
        return strtDate;
    }

    /**
     * Season Start Date- format:date-time
     * 
     */
    @JsonProperty("strtDate")
    public void setStrtDate(String strtDate) {
        this.strtDate = strtDate;
    }

    /**
     * Season End Date- format:date-time
     * 
     */
    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    /**
     * Season End Date- format:date-time
     * 
     */
    @JsonProperty("endDate")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}

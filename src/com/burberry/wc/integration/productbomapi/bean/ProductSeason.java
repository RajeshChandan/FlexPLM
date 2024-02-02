
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cfStyle",
    "CRUD",
    "season",
    "source"
})
public class ProductSeason implements Serializable
{

    /**
     * Carry Forward Style
     * 
     */
    @JsonProperty("cfStyle")
    @JsonPropertyDescription("Carry Forward Style")
    private Boolean cfStyle;
    /**
     * Return 'DELETE' in case product is removed from season
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case product is removed from season")
    private String cRUD;
    @JsonProperty("season")
    private Season season;
    @JsonProperty("source")
    private List<Source> source = null;
    private final static long serialVersionUID = 3296239547694855514L;

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

    @JsonProperty("season")
    public Season getSeason() {
        return season;
    }

    @JsonProperty("season")
    public void setSeason(Season season) {
        this.season = season;
    }

    @JsonProperty("source")
    public List<Source> getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(List<Source> source) {
        this.source = source;
    }

}

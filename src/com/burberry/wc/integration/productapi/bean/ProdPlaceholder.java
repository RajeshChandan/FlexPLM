
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "placeName",
    "placeNum"
})
public class ProdPlaceholder implements Serializable
{

    /**
     * Placeholder Name
     * 
     */
    @JsonProperty("placeName")
    @JsonPropertyDescription("Placeholder Name")
    private String placeName;
    /**
     * Placeholder Number
     * 
     */
    @JsonProperty("placeNum")
    @JsonPropertyDescription("Placeholder Number")
    private Double placeNum;
    private final static long serialVersionUID = 3193524447753463751L;

    /**
     * Placeholder Name
     * 
     */
    @JsonProperty("placeName")
    public String getPlaceName() {
        return placeName;
    }

    /**
     * Placeholder Name
     * 
     */
    @JsonProperty("placeName")
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    /**
     * Placeholder Number
     * 
     */
    @JsonProperty("placeNum")
    public Double getPlaceNum() {
        return placeNum;
    }

    /**
     * Placeholder Number
     * 
     */
    @JsonProperty("placeNum")
    public void setPlaceNum(Double placeNum) {
        this.placeNum = placeNum;
    }

}

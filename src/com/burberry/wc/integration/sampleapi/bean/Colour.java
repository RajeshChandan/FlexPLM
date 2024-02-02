
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "colourCode"
})
public class Colour implements Serializable
{

    /**
     * Colour Code
     * 
     */
    @JsonProperty("colourCode")
    @JsonPropertyDescription("Colour Code")
    private String colourCode;
    private final static long serialVersionUID = -2246462665759215941L;

    /**
     * Colour Code
     * 
     */
    @JsonProperty("colourCode")
    public String getColourCode() {
        return colourCode;
    }

    /**
     * Colour Code
     * 
     */
    @JsonProperty("colourCode")
    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

}

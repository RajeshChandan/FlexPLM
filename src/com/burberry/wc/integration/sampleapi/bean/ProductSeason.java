
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "prodSeasonName"
})
public class ProductSeason implements Serializable
{

    /**
     * Product Season Full Name (system)
     * 
     */
    @JsonProperty("prodSeasonName")
    @JsonPropertyDescription("Product Season Full Name (system)")
    private String prodSeasonName;
    private final static long serialVersionUID = 5508675119925500635L;

    /**
     * Product Season Full Name (system)
     * 
     */
    @JsonProperty("prodSeasonName")
    public String getProdSeasonName() {
        return prodSeasonName;
    }

    /**
     * Product Season Full Name (system)
     * 
     */
    @JsonProperty("prodSeasonName")
    public void setProdSeasonName(String prodSeasonName) {
        this.prodSeasonName = prodSeasonName;
    }

}

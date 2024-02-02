
package com.burberry.wc.integration.planningapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "seasonName"
})
public class AssociatedSeason implements Serializable
{

    /**
     * Season Name
     * 
     */
    @JsonProperty("seasonName")
    @JsonPropertyDescription("Season Name")
    private String seasonName;
    private final static long serialVersionUID = 3334171911511491351L;

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

}

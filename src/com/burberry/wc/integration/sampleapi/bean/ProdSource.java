
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sourceName",
    "primSource",
    "srcConfigUniqId"
})
public class ProdSource implements Serializable
{

    /**
     * Soucing Configuration Name
     * 
     */
    @JsonProperty("sourceName")
    @JsonPropertyDescription("Soucing Configuration Name")
    private String sourceName;
    /**
     * Primary Source (Product)
     * 
     */
    @JsonProperty("primSource")
    @JsonPropertyDescription("Primary Source (Product)")
    private Boolean primSource;
    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    @JsonPropertyDescription("Sourcing Configuration Unique Id")
    private String srcConfigUniqId;
    private final static long serialVersionUID = 854453879500508036L;

    /**
     * Soucing Configuration Name
     * 
     */
    @JsonProperty("sourceName")
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Soucing Configuration Name
     * 
     */
    @JsonProperty("sourceName")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * Primary Source (Product)
     * 
     */
    @JsonProperty("primSource")
    public Boolean getPrimSource() {
        return primSource;
    }

    /**
     * Primary Source (Product)
     * 
     */
    @JsonProperty("primSource")
    public void setPrimSource(Boolean primSource) {
        this.primSource = primSource;
    }

    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    public String getSrcConfigUniqId() {
        return srcConfigUniqId;
    }

    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    public void setSrcConfigUniqId(String srcConfigUniqId) {
        this.srcConfigUniqId = srcConfigUniqId;
    }

}

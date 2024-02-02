
package com.burberry.wc.integration.productcostingapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "primSource",
    "srcConfigUniqId",
    "costSheet"
})
public class Source implements Serializable
{

    /**
     * Source Name
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Source Name")
    private String name;
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
    @JsonProperty("costSheet")
    private List<CostSheet> costSheet = null;
    private final static long serialVersionUID = 3301598505359106221L;

    /**
     * Source Name
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Source Name
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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

    @JsonProperty("costSheet")
    public List<CostSheet> getCostSheet() {
        return costSheet;
    }

    @JsonProperty("costSheet")
    public void setCostSheet(List<CostSheet> costSheet) {
        this.costSheet = costSheet;
    }

}

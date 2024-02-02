
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "vendor",
    "manLeadTime",
    "primSource",
    "srcConfigUniqId",
    "specification"
})
public class Source implements Serializable
{

    /**
     * Finished Good Vendor
     * 
     */
    @JsonProperty("vendor")
    @JsonPropertyDescription("Finished Good Vendor")
    private String vendor;
    /**
     * Manufacturing Lead Time
     * 
     */
    @JsonProperty("manLeadTime")
    @JsonPropertyDescription("Manufacturing Lead Time")
    private Double manLeadTime;
    /**
     * Primary Source (Season)
     * 
     */
    @JsonProperty("primSource")
    @JsonPropertyDescription("Primary Source (Season)")
    private Boolean primSource;
    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    @JsonPropertyDescription("Sourcing Configuration Unique Id")
    private String srcConfigUniqId;
    @JsonProperty("specification")
    private List<Specification> specification = null;
    private final static long serialVersionUID = 4931257174312325253L;

    /**
     * Finished Good Vendor
     * 
     */
    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    /**
     * Finished Good Vendor
     * 
     */
    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * Manufacturing Lead Time
     * 
     */
    @JsonProperty("manLeadTime")
    public Double getManLeadTime() {
        return manLeadTime;
    }

    /**
     * Manufacturing Lead Time
     * 
     */
    @JsonProperty("manLeadTime")
    public void setManLeadTime(Double manLeadTime) {
        this.manLeadTime = manLeadTime;
    }

    /**
     * Primary Source (Season)
     * 
     */
    @JsonProperty("primSource")
    public Boolean getPrimSource() {
        return primSource;
    }

    /**
     * Primary Source (Season)
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

    @JsonProperty("specification")
    public List<Specification> getSpecification() {
        return specification;
    }

    @JsonProperty("specification")
    public void setSpecification(List<Specification> specification) {
        this.specification = specification;
    }

}

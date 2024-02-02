
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "section",
    "sectionVariation"
})
public class BOMLink implements Serializable
{

    /**
     * Section
     * 
     */
    @JsonProperty("section")
    @JsonPropertyDescription("Section")
    private String section;
    @JsonProperty("sectionVariation")
    private List<SectionVariation> sectionVariation = null;
    private final static long serialVersionUID = 5053471160319388376L;

    /**
     * Section
     * 
     */
    @JsonProperty("section")
    public String getSection() {
        return section;
    }

    /**
     * Section
     * 
     */
    @JsonProperty("section")
    public void setSection(String section) {
        this.section = section;
    }

    @JsonProperty("sectionVariation")
    public List<SectionVariation> getSectionVariation() {
        return sectionVariation;
    }

    @JsonProperty("sectionVariation")
    public void setSectionVariation(List<SectionVariation> sectionVariation) {
        this.sectionVariation = sectionVariation;
    }

}

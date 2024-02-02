
package com.burberry.wc.integration.productcostingapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "styleId",
    "source"
})
public class Style implements Serializable
{

    /**
     * Style Id
     * 
     */
    @JsonProperty("styleId")
    @JsonPropertyDescription("Style Id")
    private Integer styleId;
    @JsonProperty("source")
    private List<Source> source = null;
    private final static long serialVersionUID = -2734829334437551951L;

    /**
     * Style Id
     * 
     */
    @JsonProperty("styleId")
    public Integer getStyleId() {
        return styleId;
    }

    /**
     * Style Id
     * 
     */
    @JsonProperty("styleId")
    public void setStyleId(Integer styleId) {
        this.styleId = styleId;
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

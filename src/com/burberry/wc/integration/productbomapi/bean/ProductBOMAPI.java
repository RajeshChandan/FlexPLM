
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Product BOMs
 * <p>
 * Schema for BOM Reporting Service
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "style"
})
public class ProductBOMAPI implements Serializable
{

    @JsonProperty("style")
    private List<Style> style = null;
    private final static long serialVersionUID = 1387367995639768959L;

    @JsonProperty("style")
    public List<Style> getStyle() {
        return style;
    }

    @JsonProperty("style")
    public void setStyle(List<Style> style) {
        this.style = style;
    }

}

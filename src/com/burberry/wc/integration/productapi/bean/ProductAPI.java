
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Products
 * <p>
 * Schema for Product Reporting Service
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "style"
})
public class ProductAPI implements Serializable
{

    @JsonProperty("style")
    private List<Style> style = null;
    private final static long serialVersionUID = 2839305515178126799L;

    @JsonProperty("style")
    public List<Style> getStyle() {
        return style;
    }

    @JsonProperty("style")
    public void setStyle(List<Style> style) {
        this.style = style;
    }

}

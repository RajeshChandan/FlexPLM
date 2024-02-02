
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "suppRefNo"
})
public class MatSource implements Serializable
{

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    @JsonPropertyDescription("Supplier Reference Number (sequence)")
    private String suppRefNo;
    private final static long serialVersionUID = -1564488633962198167L;

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    public String getSuppRefNo() {
        return suppRefNo;
    }

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    public void setSuppRefNo(String suppRefNo) {
        this.suppRefNo = suppRefNo;
    }

}

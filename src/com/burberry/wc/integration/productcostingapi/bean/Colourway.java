
package com.burberry.wc.integration.productcostingapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sapMatNum"
})
public class Colourway implements Serializable
{

    /**
     * SAP Material Number
     * 
     */
    @JsonProperty("sapMatNum")
    @JsonPropertyDescription("SAP Material Number")
    private Integer sapMatNum;
    private final static long serialVersionUID = -1552122028928233621L;

    /**
     * SAP Material Number
     * 
     */
    @JsonProperty("sapMatNum")
    public Integer getSapMatNum() {
        return sapMatNum;
    }

    /**
     * SAP Material Number
     * 
     */
    @JsonProperty("sapMatNum")
    public void setSapMatNum(Integer sapMatNum) {
        this.sapMatNum = sapMatNum;
    }

}

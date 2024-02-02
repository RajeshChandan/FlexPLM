
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "materialId"
})
public class Material implements Serializable
{

    /**
     * Material Id
     * 
     */
    @JsonProperty("materialId")
    @JsonPropertyDescription("Material Id")
    private Integer materialId;
    private final static long serialVersionUID = 7572423642848490053L;

    /**
     * Material Id
     * 
     */
    @JsonProperty("materialId")
    public Integer getMaterialId() {
        return materialId;
    }

    /**
     * Material Id
     * 
     */
    @JsonProperty("materialId")
    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

}

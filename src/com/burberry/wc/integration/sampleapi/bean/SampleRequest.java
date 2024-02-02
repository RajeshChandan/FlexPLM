
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "productSampleRequest",
    "materialSampleRequest"
})
public class SampleRequest implements Serializable
{

    @JsonProperty("productSampleRequest")
    private List<ProductSampleRequest> productSampleRequest = null;
    @JsonProperty("materialSampleRequest")
    private List<MaterialSampleRequest> materialSampleRequest = null;
    private final static long serialVersionUID = -5284283022116091923L;

    @JsonProperty("productSampleRequest")
    public List<ProductSampleRequest> getProductSampleRequest() {
        return productSampleRequest;
    }

    @JsonProperty("productSampleRequest")
    public void setProductSampleRequest(List<ProductSampleRequest> productSampleRequest) {
        this.productSampleRequest = productSampleRequest;
    }

    @JsonProperty("materialSampleRequest")
    public List<MaterialSampleRequest> getMaterialSampleRequest() {
        return materialSampleRequest;
    }

    @JsonProperty("materialSampleRequest")
    public void setMaterialSampleRequest(List<MaterialSampleRequest> materialSampleRequest) {
        this.materialSampleRequest = materialSampleRequest;
    }

}


package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Samples
 * <p>
 * Schema for Sample Reporting Service
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sampleRequest"
})
public class SampleAPI implements Serializable
{

    @JsonProperty("sampleRequest")
    private List<SampleRequest> sampleRequest = null;
    private final static long serialVersionUID = 7645246060161883802L;

    @JsonProperty("sampleRequest")
    public List<SampleRequest> getSampleRequest() {
        return sampleRequest;
    }

    @JsonProperty("sampleRequest")
    public void setSampleRequest(List<SampleRequest> sampleRequest) {
        this.sampleRequest = sampleRequest;
    }

}

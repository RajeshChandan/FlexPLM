
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Material Palette 
 * <p>
 * Schema for Material Reporting Service
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "material"
})
public class PaletteMaterialAPI implements Serializable
{

    @JsonProperty("material")
    private List<Material> material = null;
    private final static long serialVersionUID = 6236809837749152067L;

    @JsonProperty("material")
    public List<Material> getMaterial() {
        return material;
    }

    @JsonProperty("material")
    public void setMaterial(List<Material> material) {
        this.material = material;
    }

}

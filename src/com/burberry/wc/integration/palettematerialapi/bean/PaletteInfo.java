
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "paletteName",
    "planRMUse",
    "planUseComment",
    "CRUD"
})
public class PaletteInfo implements Serializable
{

    /**
     * Palette Name
     * 
     */
    @JsonProperty("paletteName")
    @JsonPropertyDescription("Palette Name")
    private String paletteName;
    /**
     * Planned RM Use (multilist)
     * 
     */
    @JsonProperty("planRMUse")
    @JsonPropertyDescription("Planned RM Use (multilist)")
    private String planRMUse;
    /**
     * Planned Use Comments
     * 
     */
    @JsonProperty("planUseComment")
    @JsonPropertyDescription("Planned Use Comments")
    private String planUseComment;
    /**
     * Return 'DELETE' in case material colour is deleted from palette
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case material colour is deleted from palette")
    private String cRUD;
    private final static long serialVersionUID = -4337561384191691301L;

    /**
     * Palette Name
     * 
     */
    @JsonProperty("paletteName")
    public String getPaletteName() {
        return paletteName;
    }

    /**
     * Palette Name
     * 
     */
    @JsonProperty("paletteName")
    public void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }

    /**
     * Planned RM Use (multilist)
     * 
     */
    @JsonProperty("planRMUse")
    public String getPlanRMUse() {
        return planRMUse;
    }

    /**
     * Planned RM Use (multilist)
     * 
     */
    @JsonProperty("planRMUse")
    public void setPlanRMUse(String planRMUse) {
        this.planRMUse = planRMUse;
    }

    /**
     * Planned Use Comments
     * 
     */
    @JsonProperty("planUseComment")
    public String getPlanUseComment() {
        return planUseComment;
    }

    /**
     * Planned Use Comments
     * 
     */
    @JsonProperty("planUseComment")
    public void setPlanUseComment(String planUseComment) {
        this.planUseComment = planUseComment;
    }

    /**
     * Return 'DELETE' in case material colour is deleted from palette
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case material colour is deleted from palette
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

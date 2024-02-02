
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "paletteName",
    "prodCollect",
    "palSeasonType",
    "palYear",
    "CRUD",
    "paletteSupplier"
})
public class Palette implements Serializable
{

    /**
     * Palette Name
     * 
     */
    @JsonProperty("paletteName")
    @JsonPropertyDescription("Palette Name")
    private String paletteName;
    /**
     * Product Collection
     * 
     */
    @JsonProperty("prodCollect")
    @JsonPropertyDescription("Product Collection")
    private String prodCollect;
    /**
     * Season Type
     * 
     */
    @JsonProperty("palSeasonType")
    @JsonPropertyDescription("Season Type")
    private String palSeasonType;
    /**
     * Year
     * 
     */
    @JsonProperty("palYear")
    @JsonPropertyDescription("Year")
    private String palYear;
    /**
     * Return 'DELETE' in case material is deleted from the palette
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case material is deleted from the palette")
    private String cRUD;
    @JsonProperty("paletteSupplier")
    private List<PaletteSupplier> paletteSupplier = null;
    private final static long serialVersionUID = -7125490958084203619L;

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
     * Product Collection
     * 
     */
    @JsonProperty("prodCollect")
    public String getProdCollect() {
        return prodCollect;
    }

    /**
     * Product Collection
     * 
     */
    @JsonProperty("prodCollect")
    public void setProdCollect(String prodCollect) {
        this.prodCollect = prodCollect;
    }

    /**
     * Season Type
     * 
     */
    @JsonProperty("palSeasonType")
    public String getPalSeasonType() {
        return palSeasonType;
    }

    /**
     * Season Type
     * 
     */
    @JsonProperty("palSeasonType")
    public void setPalSeasonType(String palSeasonType) {
        this.palSeasonType = palSeasonType;
    }

    /**
     * Year
     * 
     */
    @JsonProperty("palYear")
    public String getPalYear() {
        return palYear;
    }

    /**
     * Year
     * 
     */
    @JsonProperty("palYear")
    public void setPalYear(String palYear) {
        this.palYear = palYear;
    }

    /**
     * Return 'DELETE' in case material is deleted from the palette
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case material is deleted from the palette
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    @JsonProperty("paletteSupplier")
    public List<PaletteSupplier> getPaletteSupplier() {
        return paletteSupplier;
    }

    @JsonProperty("paletteSupplier")
    public void setPaletteSupplier(List<PaletteSupplier> paletteSupplier) {
        this.paletteSupplier = paletteSupplier;
    }

}

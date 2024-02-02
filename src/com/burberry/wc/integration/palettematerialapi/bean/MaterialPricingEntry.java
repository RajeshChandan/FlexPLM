
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "inDate",
    "outDate",
    "mpeLastModified",
    "mpePrice",
    "mpeColour",
    "mpeUniqId",
    "CRUD"
})
public class MaterialPricingEntry implements Serializable
{

    /**
     * In Date -format:date-time
     * 
     */
    @JsonProperty("inDate")
    @JsonPropertyDescription("In Date -format:date-time")
    private String inDate;
    /**
     * Out Date -format:date-time
     * 
     */
    @JsonProperty("outDate")
    @JsonPropertyDescription("Out Date -format:date-time")
    private String outDate;
    /**
     * Last Modified -format:date-time
     * 
     */
    @JsonProperty("mpeLastModified")
    @JsonPropertyDescription("Last Modified -format:date-time")
    private String mpeLastModified;
    /**
     * Price
     * 
     */
    @JsonProperty("mpePrice")
    @JsonPropertyDescription("Price")
    private String mpePrice;
    /**
     * Material COlour
     * 
     */
    @JsonProperty("mpeColour")
    @JsonPropertyDescription("Material COlour")
    private String mpeColour;
    /**
     * Material Price Effectivity Unique ID- internal key
     * 
     */
    @JsonProperty("mpeUniqId")
    @JsonPropertyDescription("Material Price Effectivity Unique ID- internal key")
    private String mpeUniqId;
    /**
     * Return 'DELETE' in case a pricing entry is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case a pricing entry is deleted")
    private String cRUD;
    private final static long serialVersionUID = 882505292519894788L;

    /**
     * In Date -format:date-time
     * 
     */
    @JsonProperty("inDate")
    public String getInDate() {
        return inDate;
    }

    /**
     * In Date -format:date-time
     * 
     */
    @JsonProperty("inDate")
    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    /**
     * Out Date -format:date-time
     * 
     */
    @JsonProperty("outDate")
    public String getOutDate() {
        return outDate;
    }

    /**
     * Out Date -format:date-time
     * 
     */
    @JsonProperty("outDate")
    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    /**
     * Last Modified -format:date-time
     * 
     */
    @JsonProperty("mpeLastModified")
    public String getMpeLastModified() {
        return mpeLastModified;
    }

    /**
     * Last Modified -format:date-time
     * 
     */
    @JsonProperty("mpeLastModified")
    public void setMpeLastModified(String mpeLastModified) {
        this.mpeLastModified = mpeLastModified;
    }

    /**
     * Price
     * 
     */
    @JsonProperty("mpePrice")
    public String getMpePrice() {
        return mpePrice;
    }

    /**
     * Price
     * 
     */
    @JsonProperty("mpePrice")
    public void setMpePrice(String mpePrice) {
        this.mpePrice = mpePrice;
    }

    /**
     * Material COlour
     * 
     */
    @JsonProperty("mpeColour")
    public String getMpeColour() {
        return mpeColour;
    }

    /**
     * Material COlour
     * 
     */
    @JsonProperty("mpeColour")
    public void setMpeColour(String mpeColour) {
        this.mpeColour = mpeColour;
    }

    /**
     * Material Price Effectivity Unique ID- internal key
     * 
     */
    @JsonProperty("mpeUniqId")
    public String getMpeUniqId() {
        return mpeUniqId;
    }

    /**
     * Material Price Effectivity Unique ID- internal key
     * 
     */
    @JsonProperty("mpeUniqId")
    public void setMpeUniqId(String mpeUniqId) {
        this.mpeUniqId = mpeUniqId;
    }

    /**
     * Return 'DELETE' in case a pricing entry is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case a pricing entry is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}


package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "imageURL",
    "imageURLUniqId",
    "CRUD"
})
public class Layout implements Serializable
{

    /**
     * File URL
     * 
     */
    @JsonProperty("imageURL")
    @JsonPropertyDescription("File URL")
    private String imageURL;
    /**
     * Image URL Unique Id
     * 
     */
    @JsonProperty("imageURLUniqId")
    @JsonPropertyDescription("Image URL Unique Id")
    private String imageURLUniqId;
    /**
     * Return 'DELETE' in case the image is removed from image page
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case the image is removed from image page")
    private String cRUD;
    private final static long serialVersionUID = 8298885182070753261L;

    /**
     * File URL
     * 
     */
    @JsonProperty("imageURL")
    public String getImageURL() {
        return imageURL;
    }

    /**
     * File URL
     * 
     */
    @JsonProperty("imageURL")
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Image URL Unique Id
     * 
     */
    @JsonProperty("imageURLUniqId")
    public String getImageURLUniqId() {
        return imageURLUniqId;
    }

    /**
     * Image URL Unique Id
     * 
     */
    @JsonProperty("imageURLUniqId")
    public void setImageURLUniqId(String imageURLUniqId) {
        this.imageURLUniqId = imageURLUniqId;
    }

    /**
     * Return 'DELETE' in case the image is removed from image page
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case the image is removed from image page
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

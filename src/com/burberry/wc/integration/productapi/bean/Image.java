
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "imageName",
    "pageType",
    "CRUD",
    "layout"
})
public class Image implements Serializable
{

    /**
     * Document Name
     * 
     */
    @JsonProperty("imageName")
    @JsonPropertyDescription("Document Name")
    private String imageName;
    /**
     * Page Type
     * 
     */
    @JsonProperty("pageType")
    @JsonPropertyDescription("Page Type")
    private String pageType;
    /**
     * Return 'DELETE' in case image page is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case image page is deleted")
    private String cRUD;
    @JsonProperty("layout")
    private List<Layout> layout = null;
    private final static long serialVersionUID = 679754558265666795L;

    /**
     * Document Name
     * 
     */
    @JsonProperty("imageName")
    public String getImageName() {
        return imageName;
    }

    /**
     * Document Name
     * 
     */
    @JsonProperty("imageName")
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Page Type
     * 
     */
    @JsonProperty("pageType")
    public String getPageType() {
        return pageType;
    }

    /**
     * Page Type
     * 
     */
    @JsonProperty("pageType")
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    /**
     * Return 'DELETE' in case image page is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case image page is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    @JsonProperty("layout")
    public List<Layout> getLayout() {
        return layout;
    }

    @JsonProperty("layout")
    public void setLayout(List<Layout> layout) {
        this.layout = layout;
    }

}

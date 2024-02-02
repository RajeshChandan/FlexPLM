
package com.burberry.wc.integration.productbomapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bomName",
    "bomType",
    "bomDesc",
    "bomCreatedOn",
    "bomLastMod",
    "bomModBy",
    "bomCreatedBy",
    "bomMatAggStatus",
    "bomPrimMatTotal",
    "bomPrimMatUom",
    "bomMatComment",
    "bomPrimMaterial",
    "bomPrimMaterialQty",
    "CRUD",
    "bomHeaderUniqId",
    "primaryBOM",
    "BOMLink"
})
public class BOM implements Serializable
{

    /**
     * Name
     * 
     */
    @JsonProperty("bomName")
    @JsonPropertyDescription("Name")
    private String bomName;
    /**
     * BOM Type
     * 
     */
    @JsonProperty("bomType")
    @JsonPropertyDescription("BOM Type")
    private String bomType;
    /**
     * BOM Description
     * 
     */
    @JsonProperty("bomDesc")
    @JsonPropertyDescription("BOM Description")
    private String bomDesc;
    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("bomCreatedOn")
    @JsonPropertyDescription("Created on- format:date-time")
    private String bomCreatedOn;
    /**
     * BOM Last Modified Date- format:date-time
     * 
     */
    @JsonProperty("bomLastMod")
    @JsonPropertyDescription("BOM Last Modified Date- format:date-time")
    private String bomLastMod;
    /**
     * BOM Modified By
     * 
     */
    @JsonProperty("bomModBy")
    @JsonPropertyDescription("BOM Modified By")
    private String bomModBy;
    /**
     * Created By
     * 
     */
    @JsonProperty("bomCreatedBy")
    @JsonPropertyDescription("Created By")
    private String bomCreatedBy;
    /**
     * Material Aggregation Status
     * 
     */
    @JsonProperty("bomMatAggStatus")
    @JsonPropertyDescription("Material Aggregation Status")
    private String bomMatAggStatus;
    /**
     * Primary Material Total
     * 
     */
    @JsonProperty("bomPrimMatTotal")
    @JsonPropertyDescription("Primary Material Total")
    private Double bomPrimMatTotal;
    /**
     * Primary Material UOM
     * 
     */
    @JsonProperty("bomPrimMatUom")
    @JsonPropertyDescription("Primary Material UOM")
    private String bomPrimMatUom;
    /**
     * Comments
     * 
     */
    @JsonProperty("bomMatComment")
    @JsonPropertyDescription("Comments")
    private String bomMatComment;
    /**
     * Primary Material
     * 
     */
    @JsonProperty("bomPrimMaterial")
    @JsonPropertyDescription("Primary Material")
    private String bomPrimMaterial;
    /**
     * Primary Material Quantity
     * 
     */
    @JsonProperty("bomPrimMaterialQty")
    @JsonPropertyDescription("Primary Material Quantity")
    private Double bomPrimMaterialQty;
    /**
     * Return 'DELETE' in case the BOM is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case the BOM is deleted")
    private String cRUD;
    /**
     * BOM Header Unique Id
     * 
     */
    @JsonProperty("bomHeaderUniqId")
    @JsonPropertyDescription("BOM Header Unique Id")
    private Object bomHeaderUniqId;
    /**
     * Primary BOM
     * 
     */
    @JsonProperty("primaryBOM")
    @JsonPropertyDescription("Primary BOM")
    private Boolean primaryBOM;
    @JsonProperty("BOMLink")
    private List<BOMLink> bOMLink = null;
    private final static long serialVersionUID = -2772890134956173203L;

    /**
     * Name
     * 
     */
    @JsonProperty("bomName")
    public String getBomName() {
        return bomName;
    }

    /**
     * Name
     * 
     */
    @JsonProperty("bomName")
    public void setBomName(String bomName) {
        this.bomName = bomName;
    }

    /**
     * BOM Type
     * 
     */
    @JsonProperty("bomType")
    public String getBomType() {
        return bomType;
    }

    /**
     * BOM Type
     * 
     */
    @JsonProperty("bomType")
    public void setBomType(String bomType) {
        this.bomType = bomType;
    }

    /**
     * BOM Description
     * 
     */
    @JsonProperty("bomDesc")
    public String getBomDesc() {
        return bomDesc;
    }

    /**
     * BOM Description
     * 
     */
    @JsonProperty("bomDesc")
    public void setBomDesc(String bomDesc) {
        this.bomDesc = bomDesc;
    }

    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("bomCreatedOn")
    public String getBomCreatedOn() {
        return bomCreatedOn;
    }

    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("bomCreatedOn")
    public void setBomCreatedOn(String bomCreatedOn) {
        this.bomCreatedOn = bomCreatedOn;
    }

    /**
     * BOM Last Modified Date- format:date-time
     * 
     */
    @JsonProperty("bomLastMod")
    public String getBomLastMod() {
        return bomLastMod;
    }

    /**
     * BOM Last Modified Date- format:date-time
     * 
     */
    @JsonProperty("bomLastMod")
    public void setBomLastMod(String bomLastMod) {
        this.bomLastMod = bomLastMod;
    }

    /**
     * BOM Modified By
     * 
     */
    @JsonProperty("bomModBy")
    public String getBomModBy() {
        return bomModBy;
    }

    /**
     * BOM Modified By
     * 
     */
    @JsonProperty("bomModBy")
    public void setBomModBy(String bomModBy) {
        this.bomModBy = bomModBy;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("bomCreatedBy")
    public String getBomCreatedBy() {
        return bomCreatedBy;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("bomCreatedBy")
    public void setBomCreatedBy(String bomCreatedBy) {
        this.bomCreatedBy = bomCreatedBy;
    }

    /**
     * Material Aggregation Status
     * 
     */
    @JsonProperty("bomMatAggStatus")
    public String getBomMatAggStatus() {
        return bomMatAggStatus;
    }

    /**
     * Material Aggregation Status
     * 
     */
    @JsonProperty("bomMatAggStatus")
    public void setBomMatAggStatus(String bomMatAggStatus) {
        this.bomMatAggStatus = bomMatAggStatus;
    }

    /**
     * Primary Material Total
     * 
     */
    @JsonProperty("bomPrimMatTotal")
    public Double getBomPrimMatTotal() {
        return bomPrimMatTotal;
    }

    /**
     * Primary Material Total
     * 
     */
    @JsonProperty("bomPrimMatTotal")
    public void setBomPrimMatTotal(Double bomPrimMatTotal) {
        this.bomPrimMatTotal = bomPrimMatTotal;
    }

    /**
     * Primary Material UOM
     * 
     */
    @JsonProperty("bomPrimMatUom")
    public String getBomPrimMatUom() {
        return bomPrimMatUom;
    }

    /**
     * Primary Material UOM
     * 
     */
    @JsonProperty("bomPrimMatUom")
    public void setBomPrimMatUom(String bomPrimMatUom) {
        this.bomPrimMatUom = bomPrimMatUom;
    }

    /**
     * Comments
     * 
     */
    @JsonProperty("bomMatComment")
    public String getBomMatComment() {
        return bomMatComment;
    }

    /**
     * Comments
     * 
     */
    @JsonProperty("bomMatComment")
    public void setBomMatComment(String bomMatComment) {
        this.bomMatComment = bomMatComment;
    }

    /**
     * Primary Material
     * 
     */
    @JsonProperty("bomPrimMaterial")
    public String getBomPrimMaterial() {
        return bomPrimMaterial;
    }

    /**
     * Primary Material
     * 
     */
    @JsonProperty("bomPrimMaterial")
    public void setBomPrimMaterial(String bomPrimMaterial) {
        this.bomPrimMaterial = bomPrimMaterial;
    }

    /**
     * Primary Material Quantity
     * 
     */
    @JsonProperty("bomPrimMaterialQty")
    public Double getBomPrimMaterialQty() {
        return bomPrimMaterialQty;
    }

    /**
     * Primary Material Quantity
     * 
     */
    @JsonProperty("bomPrimMaterialQty")
    public void setBomPrimMaterialQty(Double bomPrimMaterialQty) {
        this.bomPrimMaterialQty = bomPrimMaterialQty;
    }

    /**
     * Return 'DELETE' in case the BOM is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case the BOM is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

    /**
     * BOM Header Unique Id
     * 
     */
    @JsonProperty("bomHeaderUniqId")
    public Object getBomHeaderUniqId() {
        return bomHeaderUniqId;
    }

    /**
     * BOM Header Unique Id
     * 
     */
    @JsonProperty("bomHeaderUniqId")
    public void setBomHeaderUniqId(Object bomHeaderUniqId) {
        this.bomHeaderUniqId = bomHeaderUniqId;
    }

    /**
     * Primary BOM
     * 
     */
    @JsonProperty("primaryBOM")
    public Boolean getPrimaryBOM() {
        return primaryBOM;
    }

    /**
     * Primary BOM
     * 
     */
    @JsonProperty("primaryBOM")
    public void setPrimaryBOM(Boolean primaryBOM) {
        this.primaryBOM = primaryBOM;
    }

    @JsonProperty("BOMLink")
    public List<BOMLink> getBOMLink() {
        return bOMLink;
    }

    @JsonProperty("BOMLink")
    public void setBOMLink(List<BOMLink> bOMLink) {
        this.bOMLink = bOMLink;
    }

}

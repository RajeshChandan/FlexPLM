
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "matSupplier",
    "bulkComments",
    "condApprovalComment",
    "aestheticApproval",
    "suppCurrency",
    "bulkMoq",
    "pricingUOM",
    "matPrice",
    "sampleLeadTime",
    "sampleMoq",
    "samplePrice",
    "suppComment",
    "suppMatCode",
    "suppMatName",
    "totLeadTime",
    "colorDyeLeadTime",
    "cutWidthUom",
    "cutWidth",
    "greigeLeadTime",
    "techApproval",
    "widthUom",
    "totWidth",
    "approvalRestrictions",
    "suppMatStatus",
    "materialCOO",
    "materialDevBy",
    "matSuppCreatedBy",
    "baseFabric",
    "matPriceManagement",
    "materialColour",
    "materialPricingEntry",
    "supplier",
    "materialSupplierDocuments"
})
public class MaterialSupplier implements Serializable
{

    /**
     * Supplier Material Ref
     * 
     */
    @JsonProperty("matSupplier")
    @JsonPropertyDescription("Supplier Material Ref")
    private String matSupplier;
    /**
     * Bulk Test Comments
     * 
     */
    @JsonProperty("bulkComments")
    @JsonPropertyDescription("Bulk Test Comments")
    private String bulkComments;
    /**
     * Conditional Approval Comments
     * 
     */
    @JsonProperty("condApprovalComment")
    @JsonPropertyDescription("Conditional Approval Comments")
    private String condApprovalComment;
    /**
     * Aesthetic Approval
     * 
     */
    @JsonProperty("aestheticApproval")
    @JsonPropertyDescription("Aesthetic Approval")
    private String aestheticApproval;
    /**
     * Supplier Currency
     * 
     */
    @JsonProperty("suppCurrency")
    @JsonPropertyDescription("Supplier Currency")
    private String suppCurrency;
    /**
     * Bulk MOQ
     * 
     */
    @JsonProperty("bulkMoq")
    @JsonPropertyDescription("Bulk MOQ")
    private String bulkMoq;
    /**
     * Pricing UOM
     * 
     */
    @JsonProperty("pricingUOM")
    @JsonPropertyDescription("Pricing UOM")
    private String pricingUOM;
    /**
     * Supplier Material Price
     * 
     */
    @JsonProperty("matPrice")
    @JsonPropertyDescription("Supplier Material Price")
    private Double matPrice;
    /**
     * Sample Lead Time
     * 
     */
    @JsonProperty("sampleLeadTime")
    @JsonPropertyDescription("Sample Lead Time")
    private Integer sampleLeadTime;
    /**
     * Sample MOQ
     * 
     */
    @JsonProperty("sampleMoq")
    @JsonPropertyDescription("Sample MOQ")
    private String sampleMoq;
    /**
     * Supplier Sample Price
     * 
     */
    @JsonProperty("samplePrice")
    @JsonPropertyDescription("Supplier Sample Price")
    private Double samplePrice;
    /**
     * Supplier Comment
     * 
     */
    @JsonProperty("suppComment")
    @JsonPropertyDescription("Supplier Comment")
    private String suppComment;
    /**
     * Supplier Material Code
     * 
     */
    @JsonProperty("suppMatCode")
    @JsonPropertyDescription("Supplier Material Code")
    private String suppMatCode;
    /**
     * Supplier Material Name
     * 
     */
    @JsonProperty("suppMatName")
    @JsonPropertyDescription("Supplier Material Name")
    private String suppMatName;
    /**
     * Total Lead Time
     * 
     */
    @JsonProperty("totLeadTime")
    @JsonPropertyDescription("Total Lead Time")
    private Integer totLeadTime;
    /**
     * Colouring/Dyeing Lead Time
     * 
     */
    @JsonProperty("colorDyeLeadTime")
    @JsonPropertyDescription("Colouring/Dyeing Lead Time")
    private Integer colorDyeLeadTime;
    /**
     * Supplier Cuttable Width with UOM
     * 
     */
    @JsonProperty("cutWidthUom")
    @JsonPropertyDescription("Supplier Cuttable Width with UOM")
    private String cutWidthUom;
    /**
     * Supplier Cuttable Width
     * 
     */
    @JsonProperty("cutWidth")
    @JsonPropertyDescription("Supplier Cuttable Width")
    private String cutWidth;
    /**
     * Greige Lead Time (Calender Days)
     * 
     */
    @JsonProperty("greigeLeadTime")
    @JsonPropertyDescription("Greige Lead Time (Calender Days)")
    private Integer greigeLeadTime;
    /**
     * Technical Approval
     * 
     */
    @JsonProperty("techApproval")
    @JsonPropertyDescription("Technical Approval")
    private String techApproval;
    /**
     * Width UOM
     * 
     */
    @JsonProperty("widthUom")
    @JsonPropertyDescription("Width UOM")
    private String widthUom;
    /**
     * Total Width
     * 
     */
    @JsonProperty("totWidth")
    @JsonPropertyDescription("Total Width")
    private String totWidth;
    /**
     * Approval Restrictions
     * 
     */
    @JsonProperty("approvalRestrictions")
    @JsonPropertyDescription("Approval Restrictions")
    private String approvalRestrictions;
    /**
     * Supplier Material Status
     * 
     */
    @JsonProperty("suppMatStatus")
    @JsonPropertyDescription("Supplier Material Status")
    private String suppMatStatus;
    /**
     * Material COO
     * 
     */
    @JsonProperty("materialCOO")
    @JsonPropertyDescription("Material COO")
    private String materialCOO;
    /**
     * Material Developed By
     * 
     */
    @JsonProperty("materialDevBy")
    @JsonPropertyDescription("Material Developed By")
    private String materialDevBy;
    /**
     * Material Supplier Created By
     * 
     */
    @JsonProperty("matSuppCreatedBy")
    @JsonPropertyDescription("Material Supplier Created By")
    private String matSuppCreatedBy;
    /**
     * Base Fabric
     * 
     */
    @JsonProperty("baseFabric")
    @JsonPropertyDescription("Base Fabric")
    private String baseFabric;
    @JsonProperty("matPriceManagement")
    private List<MatPriceManagement> matPriceManagement = null;
    @JsonProperty("materialColour")
    private List<MaterialColour> materialColour = null;
    @JsonProperty("materialPricingEntry")
    private List<MaterialPricingEntry> materialPricingEntry = null;
    @JsonProperty("supplier")
    private Supplier supplier;
    @JsonProperty("materialSupplierDocuments")
    private List<MaterialSupplierDocument> materialSupplierDocuments = null;
    private final static long serialVersionUID = -3234653725416082102L;

    /**
     * Supplier Material Ref
     * 
     */
    @JsonProperty("matSupplier")
    public String getMatSupplier() {
        return matSupplier;
    }

    /**
     * Supplier Material Ref
     * 
     */
    @JsonProperty("matSupplier")
    public void setMatSupplier(String matSupplier) {
        this.matSupplier = matSupplier;
    }

    /**
     * Bulk Test Comments
     * 
     */
    @JsonProperty("bulkComments")
    public String getBulkComments() {
        return bulkComments;
    }

    /**
     * Bulk Test Comments
     * 
     */
    @JsonProperty("bulkComments")
    public void setBulkComments(String bulkComments) {
        this.bulkComments = bulkComments;
    }

    /**
     * Conditional Approval Comments
     * 
     */
    @JsonProperty("condApprovalComment")
    public String getCondApprovalComment() {
        return condApprovalComment;
    }

    /**
     * Conditional Approval Comments
     * 
     */
    @JsonProperty("condApprovalComment")
    public void setCondApprovalComment(String condApprovalComment) {
        this.condApprovalComment = condApprovalComment;
    }

    /**
     * Aesthetic Approval
     * 
     */
    @JsonProperty("aestheticApproval")
    public String getAestheticApproval() {
        return aestheticApproval;
    }

    /**
     * Aesthetic Approval
     * 
     */
    @JsonProperty("aestheticApproval")
    public void setAestheticApproval(String aestheticApproval) {
        this.aestheticApproval = aestheticApproval;
    }

    /**
     * Supplier Currency
     * 
     */
    @JsonProperty("suppCurrency")
    public String getSuppCurrency() {
        return suppCurrency;
    }

    /**
     * Supplier Currency
     * 
     */
    @JsonProperty("suppCurrency")
    public void setSuppCurrency(String suppCurrency) {
        this.suppCurrency = suppCurrency;
    }

    /**
     * Bulk MOQ
     * 
     */
    @JsonProperty("bulkMoq")
    public String getBulkMoq() {
        return bulkMoq;
    }

    /**
     * Bulk MOQ
     * 
     */
    @JsonProperty("bulkMoq")
    public void setBulkMoq(String bulkMoq) {
        this.bulkMoq = bulkMoq;
    }

    /**
     * Pricing UOM
     * 
     */
    @JsonProperty("pricingUOM")
    public String getPricingUOM() {
        return pricingUOM;
    }

    /**
     * Pricing UOM
     * 
     */
    @JsonProperty("pricingUOM")
    public void setPricingUOM(String pricingUOM) {
        this.pricingUOM = pricingUOM;
    }

    /**
     * Supplier Material Price
     * 
     */
    @JsonProperty("matPrice")
    public Double getMatPrice() {
        return matPrice;
    }

    /**
     * Supplier Material Price
     * 
     */
    @JsonProperty("matPrice")
    public void setMatPrice(Double matPrice) {
        this.matPrice = matPrice;
    }

    /**
     * Sample Lead Time
     * 
     */
    @JsonProperty("sampleLeadTime")
    public Integer getSampleLeadTime() {
        return sampleLeadTime;
    }

    /**
     * Sample Lead Time
     * 
     */
    @JsonProperty("sampleLeadTime")
    public void setSampleLeadTime(Integer sampleLeadTime) {
        this.sampleLeadTime = sampleLeadTime;
    }

    /**
     * Sample MOQ
     * 
     */
    @JsonProperty("sampleMoq")
    public String getSampleMoq() {
        return sampleMoq;
    }

    /**
     * Sample MOQ
     * 
     */
    @JsonProperty("sampleMoq")
    public void setSampleMoq(String sampleMoq) {
        this.sampleMoq = sampleMoq;
    }

    /**
     * Supplier Sample Price
     * 
     */
    @JsonProperty("samplePrice")
    public Double getSamplePrice() {
        return samplePrice;
    }

    /**
     * Supplier Sample Price
     * 
     */
    @JsonProperty("samplePrice")
    public void setSamplePrice(Double samplePrice) {
        this.samplePrice = samplePrice;
    }

    /**
     * Supplier Comment
     * 
     */
    @JsonProperty("suppComment")
    public String getSuppComment() {
        return suppComment;
    }

    /**
     * Supplier Comment
     * 
     */
    @JsonProperty("suppComment")
    public void setSuppComment(String suppComment) {
        this.suppComment = suppComment;
    }

    /**
     * Supplier Material Code
     * 
     */
    @JsonProperty("suppMatCode")
    public String getSuppMatCode() {
        return suppMatCode;
    }

    /**
     * Supplier Material Code
     * 
     */
    @JsonProperty("suppMatCode")
    public void setSuppMatCode(String suppMatCode) {
        this.suppMatCode = suppMatCode;
    }

    /**
     * Supplier Material Name
     * 
     */
    @JsonProperty("suppMatName")
    public String getSuppMatName() {
        return suppMatName;
    }

    /**
     * Supplier Material Name
     * 
     */
    @JsonProperty("suppMatName")
    public void setSuppMatName(String suppMatName) {
        this.suppMatName = suppMatName;
    }

    /**
     * Total Lead Time
     * 
     */
    @JsonProperty("totLeadTime")
    public Integer getTotLeadTime() {
        return totLeadTime;
    }

    /**
     * Total Lead Time
     * 
     */
    @JsonProperty("totLeadTime")
    public void setTotLeadTime(Integer totLeadTime) {
        this.totLeadTime = totLeadTime;
    }

    /**
     * Colouring/Dyeing Lead Time
     * 
     */
    @JsonProperty("colorDyeLeadTime")
    public Integer getColorDyeLeadTime() {
        return colorDyeLeadTime;
    }

    /**
     * Colouring/Dyeing Lead Time
     * 
     */
    @JsonProperty("colorDyeLeadTime")
    public void setColorDyeLeadTime(Integer colorDyeLeadTime) {
        this.colorDyeLeadTime = colorDyeLeadTime;
    }

    /**
     * Supplier Cuttable Width with UOM
     * 
     */
    @JsonProperty("cutWidthUom")
    public String getCutWidthUom() {
        return cutWidthUom;
    }

    /**
     * Supplier Cuttable Width with UOM
     * 
     */
    @JsonProperty("cutWidthUom")
    public void setCutWidthUom(String cutWidthUom) {
        this.cutWidthUom = cutWidthUom;
    }

    /**
     * Supplier Cuttable Width
     * 
     */
    @JsonProperty("cutWidth")
    public String getCutWidth() {
        return cutWidth;
    }

    /**
     * Supplier Cuttable Width
     * 
     */
    @JsonProperty("cutWidth")
    public void setCutWidth(String cutWidth) {
        this.cutWidth = cutWidth;
    }

    /**
     * Greige Lead Time (Calender Days)
     * 
     */
    @JsonProperty("greigeLeadTime")
    public Integer getGreigeLeadTime() {
        return greigeLeadTime;
    }

    /**
     * Greige Lead Time (Calender Days)
     * 
     */
    @JsonProperty("greigeLeadTime")
    public void setGreigeLeadTime(Integer greigeLeadTime) {
        this.greigeLeadTime = greigeLeadTime;
    }

    /**
     * Technical Approval
     * 
     */
    @JsonProperty("techApproval")
    public String getTechApproval() {
        return techApproval;
    }

    /**
     * Technical Approval
     * 
     */
    @JsonProperty("techApproval")
    public void setTechApproval(String techApproval) {
        this.techApproval = techApproval;
    }

    /**
     * Width UOM
     * 
     */
    @JsonProperty("widthUom")
    public String getWidthUom() {
        return widthUom;
    }

    /**
     * Width UOM
     * 
     */
    @JsonProperty("widthUom")
    public void setWidthUom(String widthUom) {
        this.widthUom = widthUom;
    }

    /**
     * Total Width
     * 
     */
    @JsonProperty("totWidth")
    public String getTotWidth() {
        return totWidth;
    }

    /**
     * Total Width
     * 
     */
    @JsonProperty("totWidth")
    public void setTotWidth(String totWidth) {
        this.totWidth = totWidth;
    }

    /**
     * Approval Restrictions
     * 
     */
    @JsonProperty("approvalRestrictions")
    public String getApprovalRestrictions() {
        return approvalRestrictions;
    }

    /**
     * Approval Restrictions
     * 
     */
    @JsonProperty("approvalRestrictions")
    public void setApprovalRestrictions(String approvalRestrictions) {
        this.approvalRestrictions = approvalRestrictions;
    }

    /**
     * Supplier Material Status
     * 
     */
    @JsonProperty("suppMatStatus")
    public String getSuppMatStatus() {
        return suppMatStatus;
    }

    /**
     * Supplier Material Status
     * 
     */
    @JsonProperty("suppMatStatus")
    public void setSuppMatStatus(String suppMatStatus) {
        this.suppMatStatus = suppMatStatus;
    }

    /**
     * Material COO
     * 
     */
    @JsonProperty("materialCOO")
    public String getMaterialCOO() {
        return materialCOO;
    }

    /**
     * Material COO
     * 
     */
    @JsonProperty("materialCOO")
    public void setMaterialCOO(String materialCOO) {
        this.materialCOO = materialCOO;
    }

    /**
     * Material Developed By
     * 
     */
    @JsonProperty("materialDevBy")
    public String getMaterialDevBy() {
        return materialDevBy;
    }

    /**
     * Material Developed By
     * 
     */
    @JsonProperty("materialDevBy")
    public void setMaterialDevBy(String materialDevBy) {
        this.materialDevBy = materialDevBy;
    }

    /**
     * Material Supplier Created By
     * 
     */
    @JsonProperty("matSuppCreatedBy")
    public String getMatSuppCreatedBy() {
        return matSuppCreatedBy;
    }

    /**
     * Material Supplier Created By
     * 
     */
    @JsonProperty("matSuppCreatedBy")
    public void setMatSuppCreatedBy(String matSuppCreatedBy) {
        this.matSuppCreatedBy = matSuppCreatedBy;
    }

    /**
     * Base Fabric
     * 
     */
    @JsonProperty("baseFabric")
    public String getBaseFabric() {
        return baseFabric;
    }

    /**
     * Base Fabric
     * 
     */
    @JsonProperty("baseFabric")
    public void setBaseFabric(String baseFabric) {
        this.baseFabric = baseFabric;
    }

    @JsonProperty("matPriceManagement")
    public List<MatPriceManagement> getMatPriceManagement() {
        return matPriceManagement;
    }

    @JsonProperty("matPriceManagement")
    public void setMatPriceManagement(List<MatPriceManagement> matPriceManagement) {
        this.matPriceManagement = matPriceManagement;
    }

    @JsonProperty("materialColour")
    public List<MaterialColour> getMaterialColour() {
        return materialColour;
    }

    @JsonProperty("materialColour")
    public void setMaterialColour(List<MaterialColour> materialColour) {
        this.materialColour = materialColour;
    }

    @JsonProperty("materialPricingEntry")
    public List<MaterialPricingEntry> getMaterialPricingEntry() {
        return materialPricingEntry;
    }

    @JsonProperty("materialPricingEntry")
    public void setMaterialPricingEntry(List<MaterialPricingEntry> materialPricingEntry) {
        this.materialPricingEntry = materialPricingEntry;
    }

    @JsonProperty("supplier")
    public Supplier getSupplier() {
        return supplier;
    }

    @JsonProperty("supplier")
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @JsonProperty("materialSupplierDocuments")
    public List<MaterialSupplierDocument> getMaterialSupplierDocuments() {
        return materialSupplierDocuments;
    }

    @JsonProperty("materialSupplierDocuments")
    public void setMaterialSupplierDocuments(List<MaterialSupplierDocument> materialSupplierDocuments) {
        this.materialSupplierDocuments = materialSupplierDocuments;
    }

}

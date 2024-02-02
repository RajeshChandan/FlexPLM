
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "vendor",
    "primSource",
    "vendLastModified",
    "planFGCoo",
    "moqDetail",
    "manLeadTime",
    "name",
    "relToSupplier",
    "devStatus",
    "supplierCompName",
    "supplierCurr",
    "supplierCounty",
    "sapVendorId",
    "supplierRefNo",
    "approveBasedProd",
    "prodApprovComment",
    "srcConfigUniqId",
    "manufactureFacility",
    "sourceSeason"
})
public class Source implements Serializable
{

    /**
     * Finished Good Vendor
     * 
     */
    @JsonProperty("vendor")
    @JsonPropertyDescription("Finished Good Vendor")
    private String vendor;
    /**
     * Primary Source (Product)
     * (Required)
     * 
     */
    @JsonProperty("primSource")
    @JsonPropertyDescription("Primary Source (Product)")
    private Boolean primSource;
    /**
     * Sourcing Configuration Last Modified Timestamp- format:date-time
     * (Required)
     * 
     */
    @JsonProperty("vendLastModified")
    @JsonPropertyDescription("Sourcing Configuration Last Modified Timestamp- format:date-time")
    private String vendLastModified;
    /**
     * Planned FG COO
     * 
     */
    @JsonProperty("planFGCoo")
    @JsonPropertyDescription("Planned FG COO")
    private String planFGCoo;
    /**
     * MOQ Details
     * 
     */
    @JsonProperty("moqDetail")
    @JsonPropertyDescription("MOQ Details")
    private String moqDetail;
    /**
     * Manufacturing Lead Time
     * 
     */
    @JsonProperty("manLeadTime")
    @JsonPropertyDescription("Manufacturing Lead Time")
    private Integer manLeadTime;
    /**
     * Source Name
     * (Required)
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("Source Name")
    private String name;
    /**
     * Release to Supplier
     * 
     */
    @JsonProperty("relToSupplier")
    @JsonPropertyDescription("Release to Supplier")
    private String relToSupplier;
    /**
     * Development Status
     * 
     */
    @JsonProperty("devStatus")
    @JsonPropertyDescription("Development Status")
    private String devStatus;
    /**
     * Supplier- Company Name
     * 
     */
    @JsonProperty("supplierCompName")
    @JsonPropertyDescription("Supplier- Company Name")
    private String supplierCompName;
    /**
     * Supplier- Partner Trading Currency
     * 
     */
    @JsonProperty("supplierCurr")
    @JsonPropertyDescription("Supplier- Partner Trading Currency")
    private String supplierCurr;
    /**
     * Supplier- State/ Province/ County
     * 
     */
    @JsonProperty("supplierCounty")
    @JsonPropertyDescription("Supplier- State/ Province/ County")
    private String supplierCounty;
    /**
     * SAP Vendor Id
     * 
     */
    @JsonProperty("sapVendorId")
    @JsonPropertyDescription("SAP Vendor Id")
    private String sapVendorId;
    /**
     * Supplier Reference Number
     * 
     */
    @JsonProperty("supplierRefNo")
    @JsonPropertyDescription("Supplier Reference Number")
    private Integer supplierRefNo;
    /**
     * Approved Based on Product
     * 
     */
    @JsonProperty("approveBasedProd")
    @JsonPropertyDescription("Approved Based on Product")
    private String approveBasedProd;
    /**
     * Product Approval Comments
     * 
     */
    @JsonProperty("prodApprovComment")
    @JsonPropertyDescription("Product Approval Comments")
    private String prodApprovComment;
    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    @JsonPropertyDescription("Sourcing Configuration Unique Id")
    private String srcConfigUniqId;
    /**
     * Manufacturing Facility
     * 
     */
    @JsonProperty("manufactureFacility")
    @JsonPropertyDescription("Manufacturing Facility")
    private String manufactureFacility;
    @JsonProperty("sourceSeason")
    private List<SourceSeason> sourceSeason = null;
    private final static long serialVersionUID = -805039111501330844L;

    /**
     * Finished Good Vendor
     * 
     */
    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    /**
     * Finished Good Vendor
     * 
     */
    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * Primary Source (Product)
     * (Required)
     * 
     */
    @JsonProperty("primSource")
    public Boolean getPrimSource() {
        return primSource;
    }

    /**
     * Primary Source (Product)
     * (Required)
     * 
     */
    @JsonProperty("primSource")
    public void setPrimSource(Boolean primSource) {
        this.primSource = primSource;
    }

    /**
     * Sourcing Configuration Last Modified Timestamp- format:date-time
     * (Required)
     * 
     */
    @JsonProperty("vendLastModified")
    public String getVendLastModified() {
        return vendLastModified;
    }

    /**
     * Sourcing Configuration Last Modified Timestamp- format:date-time
     * (Required)
     * 
     */
    @JsonProperty("vendLastModified")
    public void setVendLastModified(String vendLastModified) {
        this.vendLastModified = vendLastModified;
    }

    /**
     * Planned FG COO
     * 
     */
    @JsonProperty("planFGCoo")
    public String getPlanFGCoo() {
        return planFGCoo;
    }

    /**
     * Planned FG COO
     * 
     */
    @JsonProperty("planFGCoo")
    public void setPlanFGCoo(String planFGCoo) {
        this.planFGCoo = planFGCoo;
    }

    /**
     * MOQ Details
     * 
     */
    @JsonProperty("moqDetail")
    public String getMoqDetail() {
        return moqDetail;
    }

    /**
     * MOQ Details
     * 
     */
    @JsonProperty("moqDetail")
    public void setMoqDetail(String moqDetail) {
        this.moqDetail = moqDetail;
    }

    /**
     * Manufacturing Lead Time
     * 
     */
    @JsonProperty("manLeadTime")
    public Integer getManLeadTime() {
        return manLeadTime;
    }

    /**
     * Manufacturing Lead Time
     * 
     */
    @JsonProperty("manLeadTime")
    public void setManLeadTime(Integer manLeadTime) {
        this.manLeadTime = manLeadTime;
    }

    /**
     * Source Name
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Source Name
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Release to Supplier
     * 
     */
    @JsonProperty("relToSupplier")
    public String getRelToSupplier() {
        return relToSupplier;
    }

    /**
     * Release to Supplier
     * 
     */
    @JsonProperty("relToSupplier")
    public void setRelToSupplier(String relToSupplier) {
        this.relToSupplier = relToSupplier;
    }

    /**
     * Development Status
     * 
     */
    @JsonProperty("devStatus")
    public String getDevStatus() {
        return devStatus;
    }

    /**
     * Development Status
     * 
     */
    @JsonProperty("devStatus")
    public void setDevStatus(String devStatus) {
        this.devStatus = devStatus;
    }

    /**
     * Supplier- Company Name
     * 
     */
    @JsonProperty("supplierCompName")
    public String getSupplierCompName() {
        return supplierCompName;
    }

    /**
     * Supplier- Company Name
     * 
     */
    @JsonProperty("supplierCompName")
    public void setSupplierCompName(String supplierCompName) {
        this.supplierCompName = supplierCompName;
    }

    /**
     * Supplier- Partner Trading Currency
     * 
     */
    @JsonProperty("supplierCurr")
    public String getSupplierCurr() {
        return supplierCurr;
    }

    /**
     * Supplier- Partner Trading Currency
     * 
     */
    @JsonProperty("supplierCurr")
    public void setSupplierCurr(String supplierCurr) {
        this.supplierCurr = supplierCurr;
    }

    /**
     * Supplier- State/ Province/ County
     * 
     */
    @JsonProperty("supplierCounty")
    public String getSupplierCounty() {
        return supplierCounty;
    }

    /**
     * Supplier- State/ Province/ County
     * 
     */
    @JsonProperty("supplierCounty")
    public void setSupplierCounty(String supplierCounty) {
        this.supplierCounty = supplierCounty;
    }

    /**
     * SAP Vendor Id
     * 
     */
    @JsonProperty("sapVendorId")
    public String getSapVendorId() {
        return sapVendorId;
    }

    /**
     * SAP Vendor Id
     * 
     */
    @JsonProperty("sapVendorId")
    public void setSapVendorId(String sapVendorId) {
        this.sapVendorId = sapVendorId;
    }

    /**
     * Supplier Reference Number
     * 
     */
    @JsonProperty("supplierRefNo")
    public Integer getSupplierRefNo() {
        return supplierRefNo;
    }

    /**
     * Supplier Reference Number
     * 
     */
    @JsonProperty("supplierRefNo")
    public void setSupplierRefNo(Integer supplierRefNo) {
        this.supplierRefNo = supplierRefNo;
    }

    /**
     * Approved Based on Product
     * 
     */
    @JsonProperty("approveBasedProd")
    public String getApproveBasedProd() {
        return approveBasedProd;
    }

    /**
     * Approved Based on Product
     * 
     */
    @JsonProperty("approveBasedProd")
    public void setApproveBasedProd(String approveBasedProd) {
        this.approveBasedProd = approveBasedProd;
    }

    /**
     * Product Approval Comments
     * 
     */
    @JsonProperty("prodApprovComment")
    public String getProdApprovComment() {
        return prodApprovComment;
    }

    /**
     * Product Approval Comments
     * 
     */
    @JsonProperty("prodApprovComment")
    public void setProdApprovComment(String prodApprovComment) {
        this.prodApprovComment = prodApprovComment;
    }

    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    public String getSrcConfigUniqId() {
        return srcConfigUniqId;
    }

    /**
     * Sourcing Configuration Unique Id
     * 
     */
    @JsonProperty("srcConfigUniqId")
    public void setSrcConfigUniqId(String srcConfigUniqId) {
        this.srcConfigUniqId = srcConfigUniqId;
    }

    /**
     * Manufacturing Facility
     * 
     */
    @JsonProperty("manufactureFacility")
    public String getManufactureFacility() {
        return manufactureFacility;
    }

    /**
     * Manufacturing Facility
     * 
     */
    @JsonProperty("manufactureFacility")
    public void setManufactureFacility(String manufactureFacility) {
        this.manufactureFacility = manufactureFacility;
    }

    @JsonProperty("sourceSeason")
    public List<SourceSeason> getSourceSeason() {
        return sourceSeason;
    }

    @JsonProperty("sourceSeason")
    public void setSourceSeason(List<SourceSeason> sourceSeason) {
        this.sourceSeason = sourceSeason;
    }

}

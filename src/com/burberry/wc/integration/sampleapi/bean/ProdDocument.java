
package com.burberry.wc.integration.sampleapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "docUniqId",
    "docName",
    "docAuditDate",
    "docAuditReportId",
    "docAuditStatus",
    "docBurIP",
    "docDIPatt",
    "docDevSeason",
    "docDevYear",
    "docImgComments",
    "docCreatedBy",
    "docDesc",
    "docLastMod",
    "docImgPgLayout",
    "docPageType",
    "docStage",
    "docTestStatus",
    "docTestDate",
    "docTestReport",
    "docThumbLoc",
    "docType",
    "docSuppType",
    "docValidEndDate",
    "docURL"
})
public class ProdDocument implements Serializable
{

    /**
     * Document Unique ID- internal key
     * 
     */
    @JsonProperty("docUniqId")
    @JsonPropertyDescription("Document Unique ID- internal key")
    private String docUniqId;
    /**
     * Name
     * 
     */
    @JsonProperty("docName")
    @JsonPropertyDescription("Name")
    private String docName;
    /**
     * Audit Date- format:date-time
     * 
     */
    @JsonProperty("docAuditDate")
    @JsonPropertyDescription("Audit Date- format:date-time")
    private String docAuditDate;
    /**
     * Audit Report ID
     * 
     */
    @JsonProperty("docAuditReportId")
    @JsonPropertyDescription("Audit Report ID")
    private Integer docAuditReportId;
    /**
     * Audit Status
     * 
     */
    @JsonProperty("docAuditStatus")
    @JsonPropertyDescription("Audit Status")
    private String docAuditStatus;
    /**
     * Burberry IP
     * 
     */
    @JsonProperty("docBurIP")
    @JsonPropertyDescription("Burberry IP")
    private Boolean docBurIP;
    /**
     * Design Intent (Pattern)
     * 
     */
    @JsonProperty("docDIPatt")
    @JsonPropertyDescription("Design Intent (Pattern)")
    private String docDIPatt;
    /**
     * Development Season
     * 
     */
    @JsonProperty("docDevSeason")
    @JsonPropertyDescription("Development Season")
    private String docDevSeason;
    /**
     * Development Year
     * 
     */
    @JsonProperty("docDevYear")
    @JsonPropertyDescription("Development Year")
    private String docDevYear;
    /**
     * Comments
     * 
     */
    @JsonProperty("docImgComments")
    @JsonPropertyDescription("Comments")
    private String docImgComments;
    /**
     * Created By
     * 
     */
    @JsonProperty("docCreatedBy")
    @JsonPropertyDescription("Created By")
    private String docCreatedBy;
    /**
     * Description
     * 
     */
    @JsonProperty("docDesc")
    @JsonPropertyDescription("Description")
    private String docDesc;
    /**
     * Last Modified Timestamp format:date-time
     * 
     */
    @JsonProperty("docLastMod")
    @JsonPropertyDescription("Last Modified Timestamp format:date-time")
    private String docLastMod;
    /**
     * Page Layout
     * 
     */
    @JsonProperty("docImgPgLayout")
    @JsonPropertyDescription("Page Layout")
    private String docImgPgLayout;
    /**
     * Page Layout
     * 
     */
    @JsonProperty("docPageType")
    @JsonPropertyDescription("Page Layout")
    private String docPageType;
    /**
     * Stage
     * 
     */
    @JsonProperty("docStage")
    @JsonPropertyDescription("Stage")
    private String docStage;
    /**
     * Testing Status
     * 
     */
    @JsonProperty("docTestStatus")
    @JsonPropertyDescription("Testing Status")
    private String docTestStatus;
    /**
     * Testing Date  - format:date-time
     * 
     */
    @JsonProperty("docTestDate")
    @JsonPropertyDescription("Testing Date  - format:date-time")
    private String docTestDate;
    /**
     * Testing Report Id
     * 
     */
    @JsonProperty("docTestReport")
    @JsonPropertyDescription("Testing Report Id")
    private String docTestReport;
    /**
     * Thumbnail Location
     * 
     */
    @JsonProperty("docThumbLoc")
    @JsonPropertyDescription("Thumbnail Location")
    private String docThumbLoc;
    /**
     * Type
     * 
     */
    @JsonProperty("docType")
    @JsonPropertyDescription("Type")
    private String docType;
    /**
     * Type
     * 
     */
    @JsonProperty("docSuppType")
    @JsonPropertyDescription("Type")
    private String docSuppType;
    /**
     * Validity End Date - format:date-time
     * 
     */
    @JsonProperty("docValidEndDate")
    @JsonPropertyDescription("Validity End Date - format:date-time")
    private String docValidEndDate;
    /**
     * File URL
     * 
     */
    @JsonProperty("docURL")
    @JsonPropertyDescription("File URL")
    private String docURL;
    private final static long serialVersionUID = -558474193473711336L;

    /**
     * Document Unique ID- internal key
     * 
     */
    @JsonProperty("docUniqId")
    public String getDocUniqId() {
        return docUniqId;
    }

    /**
     * Document Unique ID- internal key
     * 
     */
    @JsonProperty("docUniqId")
    public void setDocUniqId(String docUniqId) {
        this.docUniqId = docUniqId;
    }

    /**
     * Name
     * 
     */
    @JsonProperty("docName")
    public String getDocName() {
        return docName;
    }

    /**
     * Name
     * 
     */
    @JsonProperty("docName")
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * Audit Date- format:date-time
     * 
     */
    @JsonProperty("docAuditDate")
    public String getDocAuditDate() {
        return docAuditDate;
    }

    /**
     * Audit Date- format:date-time
     * 
     */
    @JsonProperty("docAuditDate")
    public void setDocAuditDate(String docAuditDate) {
        this.docAuditDate = docAuditDate;
    }

    /**
     * Audit Report ID
     * 
     */
    @JsonProperty("docAuditReportId")
    public Integer getDocAuditReportId() {
        return docAuditReportId;
    }

    /**
     * Audit Report ID
     * 
     */
    @JsonProperty("docAuditReportId")
    public void setDocAuditReportId(Integer docAuditReportId) {
        this.docAuditReportId = docAuditReportId;
    }

    /**
     * Audit Status
     * 
     */
    @JsonProperty("docAuditStatus")
    public String getDocAuditStatus() {
        return docAuditStatus;
    }

    /**
     * Audit Status
     * 
     */
    @JsonProperty("docAuditStatus")
    public void setDocAuditStatus(String docAuditStatus) {
        this.docAuditStatus = docAuditStatus;
    }

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("docBurIP")
    public Boolean getDocBurIP() {
        return docBurIP;
    }

    /**
     * Burberry IP
     * 
     */
    @JsonProperty("docBurIP")
    public void setDocBurIP(Boolean docBurIP) {
        this.docBurIP = docBurIP;
    }

    /**
     * Design Intent (Pattern)
     * 
     */
    @JsonProperty("docDIPatt")
    public String getDocDIPatt() {
        return docDIPatt;
    }

    /**
     * Design Intent (Pattern)
     * 
     */
    @JsonProperty("docDIPatt")
    public void setDocDIPatt(String docDIPatt) {
        this.docDIPatt = docDIPatt;
    }

    /**
     * Development Season
     * 
     */
    @JsonProperty("docDevSeason")
    public String getDocDevSeason() {
        return docDevSeason;
    }

    /**
     * Development Season
     * 
     */
    @JsonProperty("docDevSeason")
    public void setDocDevSeason(String docDevSeason) {
        this.docDevSeason = docDevSeason;
    }

    /**
     * Development Year
     * 
     */
    @JsonProperty("docDevYear")
    public String getDocDevYear() {
        return docDevYear;
    }

    /**
     * Development Year
     * 
     */
    @JsonProperty("docDevYear")
    public void setDocDevYear(String docDevYear) {
        this.docDevYear = docDevYear;
    }

    /**
     * Comments
     * 
     */
    @JsonProperty("docImgComments")
    public String getDocImgComments() {
        return docImgComments;
    }

    /**
     * Comments
     * 
     */
    @JsonProperty("docImgComments")
    public void setDocImgComments(String docImgComments) {
        this.docImgComments = docImgComments;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("docCreatedBy")
    public String getDocCreatedBy() {
        return docCreatedBy;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("docCreatedBy")
    public void setDocCreatedBy(String docCreatedBy) {
        this.docCreatedBy = docCreatedBy;
    }

    /**
     * Description
     * 
     */
    @JsonProperty("docDesc")
    public String getDocDesc() {
        return docDesc;
    }

    /**
     * Description
     * 
     */
    @JsonProperty("docDesc")
    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    /**
     * Last Modified Timestamp format:date-time
     * 
     */
    @JsonProperty("docLastMod")
    public String getDocLastMod() {
        return docLastMod;
    }

    /**
     * Last Modified Timestamp format:date-time
     * 
     */
    @JsonProperty("docLastMod")
    public void setDocLastMod(String docLastMod) {
        this.docLastMod = docLastMod;
    }

    /**
     * Page Layout
     * 
     */
    @JsonProperty("docImgPgLayout")
    public String getDocImgPgLayout() {
        return docImgPgLayout;
    }

    /**
     * Page Layout
     * 
     */
    @JsonProperty("docImgPgLayout")
    public void setDocImgPgLayout(String docImgPgLayout) {
        this.docImgPgLayout = docImgPgLayout;
    }

    /**
     * Page Layout
     * 
     */
    @JsonProperty("docPageType")
    public String getDocPageType() {
        return docPageType;
    }

    /**
     * Page Layout
     * 
     */
    @JsonProperty("docPageType")
    public void setDocPageType(String docPageType) {
        this.docPageType = docPageType;
    }

    /**
     * Stage
     * 
     */
    @JsonProperty("docStage")
    public String getDocStage() {
        return docStage;
    }

    /**
     * Stage
     * 
     */
    @JsonProperty("docStage")
    public void setDocStage(String docStage) {
        this.docStage = docStage;
    }

    /**
     * Testing Status
     * 
     */
    @JsonProperty("docTestStatus")
    public String getDocTestStatus() {
        return docTestStatus;
    }

    /**
     * Testing Status
     * 
     */
    @JsonProperty("docTestStatus")
    public void setDocTestStatus(String docTestStatus) {
        this.docTestStatus = docTestStatus;
    }

    /**
     * Testing Date  - format:date-time
     * 
     */
    @JsonProperty("docTestDate")
    public String getDocTestDate() {
        return docTestDate;
    }

    /**
     * Testing Date  - format:date-time
     * 
     */
    @JsonProperty("docTestDate")
    public void setDocTestDate(String docTestDate) {
        this.docTestDate = docTestDate;
    }

    /**
     * Testing Report Id
     * 
     */
    @JsonProperty("docTestReport")
    public String getDocTestReport() {
        return docTestReport;
    }

    /**
     * Testing Report Id
     * 
     */
    @JsonProperty("docTestReport")
    public void setDocTestReport(String docTestReport) {
        this.docTestReport = docTestReport;
    }

    /**
     * Thumbnail Location
     * 
     */
    @JsonProperty("docThumbLoc")
    public String getDocThumbLoc() {
        return docThumbLoc;
    }

    /**
     * Thumbnail Location
     * 
     */
    @JsonProperty("docThumbLoc")
    public void setDocThumbLoc(String docThumbLoc) {
        this.docThumbLoc = docThumbLoc;
    }

    /**
     * Type
     * 
     */
    @JsonProperty("docType")
    public String getDocType() {
        return docType;
    }

    /**
     * Type
     * 
     */
    @JsonProperty("docType")
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Type
     * 
     */
    @JsonProperty("docSuppType")
    public String getDocSuppType() {
        return docSuppType;
    }

    /**
     * Type
     * 
     */
    @JsonProperty("docSuppType")
    public void setDocSuppType(String docSuppType) {
        this.docSuppType = docSuppType;
    }

    /**
     * Validity End Date - format:date-time
     * 
     */
    @JsonProperty("docValidEndDate")
    public String getDocValidEndDate() {
        return docValidEndDate;
    }

    /**
     * Validity End Date - format:date-time
     * 
     */
    @JsonProperty("docValidEndDate")
    public void setDocValidEndDate(String docValidEndDate) {
        this.docValidEndDate = docValidEndDate;
    }

    /**
     * File URL
     * 
     */
    @JsonProperty("docURL")
    public String getDocURL() {
        return docURL;
    }

    /**
     * File URL
     * 
     */
    @JsonProperty("docURL")
    public void setDocURL(String docURL) {
        this.docURL = docURL;
    }

}

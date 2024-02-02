
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "docUniqId",
    "docName",
    "docSeason",
    "docApprover",
    "docAuthor",
    "checkComment",
    "docCreatedBy",
    "docCreatedOn",
    "docDesc",
    "docLastModBy",
    "docLastModDate",
    "docReviewer",
    "docTestStatus",
    "docTestDate",
    "docTestReport",
    "docTestStage",
    "docThumbLoc",
    "docType",
    "docValidEndDate",
    "docURL",
    "docTestLab",
    "docLook",
    "docSequence",
    "CRUD"
})
public class SpecDocument implements Serializable
{

    /**
     * DocumentUniqueID-internalkey
     * 
     */
    @JsonProperty("docUniqId")
    @JsonPropertyDescription("DocumentUniqueID-internalkey")
    private String docUniqId;
    /**
     * Name
     * 
     */
    @JsonProperty("docName")
    @JsonPropertyDescription("Name")
    private String docName;
    /**
     * Season
     * 
     */
    @JsonProperty("docSeason")
    @JsonPropertyDescription("Season")
    private String docSeason;
    /**
     * Approver
     * 
     */
    @JsonProperty("docApprover")
    @JsonPropertyDescription("Approver")
    private String docApprover;
    /**
     * Author
     * 
     */
    @JsonProperty("docAuthor")
    @JsonPropertyDescription("Author")
    private String docAuthor;
    /**
     * CheckinComment
     * 
     */
    @JsonProperty("checkComment")
    @JsonPropertyDescription("CheckinComment")
    private String checkComment;
    /**
     * CreatedBy
     * 
     */
    @JsonProperty("docCreatedBy")
    @JsonPropertyDescription("CreatedBy")
    private String docCreatedBy;
    /**
     * CreatedOn-format:date-time
     * 
     */
    @JsonProperty("docCreatedOn")
    @JsonPropertyDescription("CreatedOn-format:date-time")
    private String docCreatedOn;
    /**
     * Description
     * 
     */
    @JsonProperty("docDesc")
    @JsonPropertyDescription("Description")
    private String docDesc;
    /**
     * LastModifiedBy
     * 
     */
    @JsonProperty("docLastModBy")
    @JsonPropertyDescription("LastModifiedBy")
    private String docLastModBy;
    /**
     * LastModifiedTimestamp-format:date-time
     * 
     */
    @JsonProperty("docLastModDate")
    @JsonPropertyDescription("LastModifiedTimestamp-format:date-time")
    private String docLastModDate;
    /**
     * Reviewer
     * 
     */
    @JsonProperty("docReviewer")
    @JsonPropertyDescription("Reviewer")
    private String docReviewer;
    /**
     * TestingStatus
     * 
     */
    @JsonProperty("docTestStatus")
    @JsonPropertyDescription("TestingStatus")
    private String docTestStatus;
    /**
     * TestingDate-format:date-time
     * 
     */
    @JsonProperty("docTestDate")
    @JsonPropertyDescription("TestingDate-format:date-time")
    private String docTestDate;
    /**
     * TestingReportId
     * 
     */
    @JsonProperty("docTestReport")
    @JsonPropertyDescription("TestingReportId")
    private String docTestReport;
    /**
     * TestingReportStage
     * 
     */
    @JsonProperty("docTestStage")
    @JsonPropertyDescription("TestingReportStage")
    private String docTestStage;
    /**
     * ThumbnailLocation
     * 
     */
    @JsonProperty("docThumbLoc")
    @JsonPropertyDescription("ThumbnailLocation")
    private String docThumbLoc;
    /**
     * Type
     * 
     */
    @JsonProperty("docType")
    @JsonPropertyDescription("Type")
    private String docType;
    /**
     * ValidityEndDate-format:date-time
     * 
     */
    @JsonProperty("docValidEndDate")
    @JsonPropertyDescription("ValidityEndDate-format:date-time")
    private String docValidEndDate;
    /**
     * FileURL
     * 
     */
    @JsonProperty("docURL")
    @JsonPropertyDescription("FileURL")
    private String docURL;
    /**
     * TestLabName
     * 
     */
    @JsonProperty("docTestLab")
    @JsonPropertyDescription("TestLabName")
    private String docTestLab;
    /**
     * Look
     * 
     */
    @JsonProperty("docLook")
    @JsonPropertyDescription("Look")
    private String docLook;
    /**
     * Sequence
     * 
     */
    @JsonProperty("docSequence")
    @JsonPropertyDescription("Sequence")
    private String docSequence;
    /**
     * Return'DELETE'incasedocumentisdeleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return'DELETE'incasedocumentisdeleted")
    private String cRUD;
    private final static long serialVersionUID = -8252867893143741201L;

    /**
     * DocumentUniqueID-internalkey
     * 
     */
    @JsonProperty("docUniqId")
    public String getDocUniqId() {
        return docUniqId;
    }

    /**
     * DocumentUniqueID-internalkey
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
     * Season
     * 
     */
    @JsonProperty("docSeason")
    public String getDocSeason() {
        return docSeason;
    }

    /**
     * Season
     * 
     */
    @JsonProperty("docSeason")
    public void setDocSeason(String docSeason) {
        this.docSeason = docSeason;
    }

    /**
     * Approver
     * 
     */
    @JsonProperty("docApprover")
    public String getDocApprover() {
        return docApprover;
    }

    /**
     * Approver
     * 
     */
    @JsonProperty("docApprover")
    public void setDocApprover(String docApprover) {
        this.docApprover = docApprover;
    }

    /**
     * Author
     * 
     */
    @JsonProperty("docAuthor")
    public String getDocAuthor() {
        return docAuthor;
    }

    /**
     * Author
     * 
     */
    @JsonProperty("docAuthor")
    public void setDocAuthor(String docAuthor) {
        this.docAuthor = docAuthor;
    }

    /**
     * CheckinComment
     * 
     */
    @JsonProperty("checkComment")
    public String getCheckComment() {
        return checkComment;
    }

    /**
     * CheckinComment
     * 
     */
    @JsonProperty("checkComment")
    public void setCheckComment(String checkComment) {
        this.checkComment = checkComment;
    }

    /**
     * CreatedBy
     * 
     */
    @JsonProperty("docCreatedBy")
    public String getDocCreatedBy() {
        return docCreatedBy;
    }

    /**
     * CreatedBy
     * 
     */
    @JsonProperty("docCreatedBy")
    public void setDocCreatedBy(String docCreatedBy) {
        this.docCreatedBy = docCreatedBy;
    }

    /**
     * CreatedOn-format:date-time
     * 
     */
    @JsonProperty("docCreatedOn")
    public String getDocCreatedOn() {
        return docCreatedOn;
    }

    /**
     * CreatedOn-format:date-time
     * 
     */
    @JsonProperty("docCreatedOn")
    public void setDocCreatedOn(String docCreatedOn) {
        this.docCreatedOn = docCreatedOn;
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
     * LastModifiedBy
     * 
     */
    @JsonProperty("docLastModBy")
    public String getDocLastModBy() {
        return docLastModBy;
    }

    /**
     * LastModifiedBy
     * 
     */
    @JsonProperty("docLastModBy")
    public void setDocLastModBy(String docLastModBy) {
        this.docLastModBy = docLastModBy;
    }

    /**
     * LastModifiedTimestamp-format:date-time
     * 
     */
    @JsonProperty("docLastModDate")
    public String getDocLastModDate() {
        return docLastModDate;
    }

    /**
     * LastModifiedTimestamp-format:date-time
     * 
     */
    @JsonProperty("docLastModDate")
    public void setDocLastModDate(String docLastModDate) {
        this.docLastModDate = docLastModDate;
    }

    /**
     * Reviewer
     * 
     */
    @JsonProperty("docReviewer")
    public String getDocReviewer() {
        return docReviewer;
    }

    /**
     * Reviewer
     * 
     */
    @JsonProperty("docReviewer")
    public void setDocReviewer(String docReviewer) {
        this.docReviewer = docReviewer;
    }

    /**
     * TestingStatus
     * 
     */
    @JsonProperty("docTestStatus")
    public String getDocTestStatus() {
        return docTestStatus;
    }

    /**
     * TestingStatus
     * 
     */
    @JsonProperty("docTestStatus")
    public void setDocTestStatus(String docTestStatus) {
        this.docTestStatus = docTestStatus;
    }

    /**
     * TestingDate-format:date-time
     * 
     */
    @JsonProperty("docTestDate")
    public String getDocTestDate() {
        return docTestDate;
    }

    /**
     * TestingDate-format:date-time
     * 
     */
    @JsonProperty("docTestDate")
    public void setDocTestDate(String docTestDate) {
        this.docTestDate = docTestDate;
    }

    /**
     * TestingReportId
     * 
     */
    @JsonProperty("docTestReport")
    public String getDocTestReport() {
        return docTestReport;
    }

    /**
     * TestingReportId
     * 
     */
    @JsonProperty("docTestReport")
    public void setDocTestReport(String docTestReport) {
        this.docTestReport = docTestReport;
    }

    /**
     * TestingReportStage
     * 
     */
    @JsonProperty("docTestStage")
    public String getDocTestStage() {
        return docTestStage;
    }

    /**
     * TestingReportStage
     * 
     */
    @JsonProperty("docTestStage")
    public void setDocTestStage(String docTestStage) {
        this.docTestStage = docTestStage;
    }

    /**
     * ThumbnailLocation
     * 
     */
    @JsonProperty("docThumbLoc")
    public String getDocThumbLoc() {
        return docThumbLoc;
    }

    /**
     * ThumbnailLocation
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
     * ValidityEndDate-format:date-time
     * 
     */
    @JsonProperty("docValidEndDate")
    public String getDocValidEndDate() {
        return docValidEndDate;
    }

    /**
     * ValidityEndDate-format:date-time
     * 
     */
    @JsonProperty("docValidEndDate")
    public void setDocValidEndDate(String docValidEndDate) {
        this.docValidEndDate = docValidEndDate;
    }

    /**
     * FileURL
     * 
     */
    @JsonProperty("docURL")
    public String getDocURL() {
        return docURL;
    }

    /**
     * FileURL
     * 
     */
    @JsonProperty("docURL")
    public void setDocURL(String docURL) {
        this.docURL = docURL;
    }

    /**
     * TestLabName
     * 
     */
    @JsonProperty("docTestLab")
    public String getDocTestLab() {
        return docTestLab;
    }

    /**
     * TestLabName
     * 
     */
    @JsonProperty("docTestLab")
    public void setDocTestLab(String docTestLab) {
        this.docTestLab = docTestLab;
    }

    /**
     * Look
     * 
     */
    @JsonProperty("docLook")
    public String getDocLook() {
        return docLook;
    }

    /**
     * Look
     * 
     */
    @JsonProperty("docLook")
    public void setDocLook(String docLook) {
        this.docLook = docLook;
    }

    /**
     * Sequence
     * 
     */
    @JsonProperty("docSequence")
    public String getDocSequence() {
        return docSequence;
    }

    /**
     * Sequence
     * 
     */
    @JsonProperty("docSequence")
    public void setDocSequence(String docSequence) {
        this.docSequence = docSequence;
    }

    /**
     * Return'DELETE'incasedocumentisdeleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return'DELETE'incasedocumentisdeleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

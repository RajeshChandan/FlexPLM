
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
    "docSequence"
})
public class Document implements Serializable
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
     * Checkin Comment
     * 
     */
    @JsonProperty("checkComment")
    @JsonPropertyDescription("Checkin Comment")
    private String checkComment;
    /**
     * Created By
     * 
     */
    @JsonProperty("docCreatedBy")
    @JsonPropertyDescription("Created By")
    private String docCreatedBy;
    /**
     * Created On - format:date-time
     * 
     */
    @JsonProperty("docCreatedOn")
    @JsonPropertyDescription("Created On - format:date-time")
    private String docCreatedOn;
    /**
     * Description
     * 
     */
    @JsonProperty("docDesc")
    @JsonPropertyDescription("Description")
    private String docDesc;
    /**
     * Last Modified By
     * 
     */
    @JsonProperty("docLastModBy")
    @JsonPropertyDescription("Last Modified By")
    private String docLastModBy;
    /**
     * Last Modified Timestamp  - format:date-time
     * 
     */
    @JsonProperty("docLastModDate")
    @JsonPropertyDescription("Last Modified Timestamp  - format:date-time")
    private String docLastModDate;
    /**
     * Reviewer
     * 
     */
    @JsonProperty("docReviewer")
    @JsonPropertyDescription("Reviewer")
    private String docReviewer;
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
     * Testing Report Stage
     * 
     */
    @JsonProperty("docTestStage")
    @JsonPropertyDescription("Testing Report Stage")
    private String docTestStage;
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
    /**
     * Test Lab Name
     * 
     */
    @JsonProperty("docTestLab")
    @JsonPropertyDescription("Test Lab Name")
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
    private final static long serialVersionUID = 3348933080332056136L;

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
     * Checkin Comment
     * 
     */
    @JsonProperty("checkComment")
    public String getCheckComment() {
        return checkComment;
    }

    /**
     * Checkin Comment
     * 
     */
    @JsonProperty("checkComment")
    public void setCheckComment(String checkComment) {
        this.checkComment = checkComment;
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
     * Created On - format:date-time
     * 
     */
    @JsonProperty("docCreatedOn")
    public String getDocCreatedOn() {
        return docCreatedOn;
    }

    /**
     * Created On - format:date-time
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
     * Last Modified By
     * 
     */
    @JsonProperty("docLastModBy")
    public String getDocLastModBy() {
        return docLastModBy;
    }

    /**
     * Last Modified By
     * 
     */
    @JsonProperty("docLastModBy")
    public void setDocLastModBy(String docLastModBy) {
        this.docLastModBy = docLastModBy;
    }

    /**
     * Last Modified Timestamp  - format:date-time
     * 
     */
    @JsonProperty("docLastModDate")
    public String getDocLastModDate() {
        return docLastModDate;
    }

    /**
     * Last Modified Timestamp  - format:date-time
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
     * Testing Report Stage
     * 
     */
    @JsonProperty("docTestStage")
    public String getDocTestStage() {
        return docTestStage;
    }

    /**
     * Testing Report Stage
     * 
     */
    @JsonProperty("docTestStage")
    public void setDocTestStage(String docTestStage) {
        this.docTestStage = docTestStage;
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

    /**
     * Test Lab Name
     * 
     */
    @JsonProperty("docTestLab")
    public String getDocTestLab() {
        return docTestLab;
    }

    /**
     * Test Lab Name
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

}

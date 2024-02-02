
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "add1",
    "add2",
    "add3",
    "city",
    "contactRole",
    "corpRespApproval",
    "suppCreatedOn",
    "suppEmail",
    "finRisk",
    "suppFirstName",
    "suppLastName",
    "suppLastMod",
    "suppModBy",
    "manExcellenceRating",
    "migrated",
    "suppMobile",
    "notes",
    "partnerRank",
    "partnerTrade",
    "suppPostCode",
    "restSubsApproval",
    "sapVendorId",
    "skypeId",
    "suppStateProv",
    "suppStatus",
    "suppName",
    "suppCapable",
    "suppRefNo",
    "suppSubType",
    "susRawMat",
    "suppTelNo",
    "suppTitle",
    "vendGroup",
    "suppCountry",
    "rmSuppMoq",
    "rmSuppMoqUom",
    "rmSurcharge"
})
public class Supplier implements Serializable
{

    /**
     * Address Line 1
     * 
     */
    @JsonProperty("add1")
    @JsonPropertyDescription("Address Line 1")
    private String add1;
    /**
     * Address Line 2
     * 
     */
    @JsonProperty("add2")
    @JsonPropertyDescription("Address Line 2")
    private String add2;
    /**
     * Address Line 3
     * 
     */
    @JsonProperty("add3")
    @JsonPropertyDescription("Address Line 3")
    private String add3;
    /**
     * City
     * 
     */
    @JsonProperty("city")
    @JsonPropertyDescription("City")
    private String city;
    /**
     * Contact Role
     * 
     */
    @JsonProperty("contactRole")
    @JsonPropertyDescription("Contact Role")
    private String contactRole;
    /**
     * Corporate Responsibility Approval
     * 
     */
    @JsonProperty("corpRespApproval")
    @JsonPropertyDescription("Corporate Responsibility Approval")
    private String corpRespApproval;
    /**
     * Created On- format:date-time
     * 
     */
    @JsonProperty("suppCreatedOn")
    @JsonPropertyDescription("Created On- format:date-time")
    private String suppCreatedOn;
    /**
     * Email
     * 
     */
    @JsonProperty("suppEmail")
    @JsonPropertyDescription("Email")
    private String suppEmail;
    /**
     * Financial Risk
     * 
     */
    @JsonProperty("finRisk")
    @JsonPropertyDescription("Financial Risk")
    private String finRisk;
    /**
     * First Name
     * 
     */
    @JsonProperty("suppFirstName")
    @JsonPropertyDescription("First Name")
    private String suppFirstName;
    /**
     * Last Name
     * 
     */
    @JsonProperty("suppLastName")
    @JsonPropertyDescription("Last Name")
    private String suppLastName;
    /**
     * Last Modified- format:date-time
     * 
     */
    @JsonProperty("suppLastMod")
    @JsonPropertyDescription("Last Modified- format:date-time")
    private String suppLastMod;
    /**
     * Modified By
     * 
     */
    @JsonProperty("suppModBy")
    @JsonPropertyDescription("Modified By")
    private String suppModBy;
    /**
     * Manufacturing Excellence Rating
     * 
     */
    @JsonProperty("manExcellenceRating")
    @JsonPropertyDescription("Manufacturing Excellence Rating")
    private String manExcellenceRating;
    /**
     * Migrated?
     * 
     */
    @JsonProperty("migrated")
    @JsonPropertyDescription("Migrated?")
    private Boolean migrated;
    /**
     * Mobile No. (Incl. Country Code)
     * 
     */
    @JsonProperty("suppMobile")
    @JsonPropertyDescription("Mobile No. (Incl. Country Code)")
    private String suppMobile;
    /**
     * Notes
     * 
     */
    @JsonProperty("notes")
    @JsonPropertyDescription("Notes")
    private String notes;
    /**
     * Partner Rank
     * 
     */
    @JsonProperty("partnerRank")
    @JsonPropertyDescription("Partner Rank")
    private String partnerRank;
    /**
     * Partner Trading Currency
     * 
     */
    @JsonProperty("partnerTrade")
    @JsonPropertyDescription("Partner Trading Currency")
    private String partnerTrade;
    /**
     * Zip/PostCode
     * 
     */
    @JsonProperty("suppPostCode")
    @JsonPropertyDescription("Zip/PostCode")
    private String suppPostCode;
    /**
     * Restricted Substances Management Approval
     * 
     */
    @JsonProperty("restSubsApproval")
    @JsonPropertyDescription("Restricted Substances Management Approval")
    private String restSubsApproval;
    /**
     * SAP Vendor ID
     * 
     */
    @JsonProperty("sapVendorId")
    @JsonPropertyDescription("SAP Vendor ID")
    private String sapVendorId;
    /**
     * Skype ID
     * 
     */
    @JsonProperty("skypeId")
    @JsonPropertyDescription("Skype ID")
    private String skypeId;
    /**
     * State / Province / County
     * 
     */
    @JsonProperty("suppStateProv")
    @JsonPropertyDescription("State / Province / County")
    private String suppStateProv;
    /**
     * Status
     * 
     */
    @JsonProperty("suppStatus")
    @JsonPropertyDescription("Status")
    private String suppStatus;
    /**
     * Supplier name
     * 
     */
    @JsonProperty("suppName")
    @JsonPropertyDescription("Supplier name")
    private String suppName;
    /**
     * Supplier Capabilities (multi-list)
     * 
     */
    @JsonProperty("suppCapable")
    @JsonPropertyDescription("Supplier Capabilities (multi-list)")
    private String suppCapable;
    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    @JsonPropertyDescription("Supplier Reference Number (sequence)")
    private String suppRefNo;
    /**
     * Supplier Sub- Type (multi-list)
     * 
     */
    @JsonProperty("suppSubType")
    @JsonPropertyDescription("Supplier Sub- Type (multi-list)")
    private String suppSubType;
    /**
     * Sustainable Raw Material (multi-list)
     * 
     */
    @JsonProperty("susRawMat")
    @JsonPropertyDescription("Sustainable Raw Material (multi-list)")
    private String susRawMat;
    /**
     * Telephone No. (Incl. Country Code)
     * 
     */
    @JsonProperty("suppTelNo")
    @JsonPropertyDescription("Telephone No. (Incl. Country Code)")
    private String suppTelNo;
    /**
     * Supplier Title
     * 
     */
    @JsonProperty("suppTitle")
    @JsonPropertyDescription("Supplier Title")
    private String suppTitle;
    /**
     * Vendor Group
     * 
     */
    @JsonProperty("vendGroup")
    @JsonPropertyDescription("Vendor Group")
    private String vendGroup;
    /**
     * Country
     * 
     */
    @JsonProperty("suppCountry")
    @JsonPropertyDescription("Country")
    private String suppCountry;
    /**
     * RM Supplier MOQ
     * 
     */
    @JsonProperty("rmSuppMoq")
    @JsonPropertyDescription("RM Supplier MOQ")
    private String rmSuppMoq;
    /**
     * RM Supplier MOQ UOM
     * 
     */
    @JsonProperty("rmSuppMoqUom")
    @JsonPropertyDescription("RM Supplier MOQ UOM")
    private String rmSuppMoqUom;
    /**
     * RM Supplier MOQ UOM
     * 
     */
    @JsonProperty("rmSurcharge")
    @JsonPropertyDescription("RM Supplier MOQ UOM")
    private String rmSurcharge;
    private final static long serialVersionUID = -1224184287962280231L;

    /**
     * Address Line 1
     * 
     */
    @JsonProperty("add1")
    public String getAdd1() {
        return add1;
    }

    /**
     * Address Line 1
     * 
     */
    @JsonProperty("add1")
    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    /**
     * Address Line 2
     * 
     */
    @JsonProperty("add2")
    public String getAdd2() {
        return add2;
    }

    /**
     * Address Line 2
     * 
     */
    @JsonProperty("add2")
    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    /**
     * Address Line 3
     * 
     */
    @JsonProperty("add3")
    public String getAdd3() {
        return add3;
    }

    /**
     * Address Line 3
     * 
     */
    @JsonProperty("add3")
    public void setAdd3(String add3) {
        this.add3 = add3;
    }

    /**
     * City
     * 
     */
    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    /**
     * City
     * 
     */
    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Contact Role
     * 
     */
    @JsonProperty("contactRole")
    public String getContactRole() {
        return contactRole;
    }

    /**
     * Contact Role
     * 
     */
    @JsonProperty("contactRole")
    public void setContactRole(String contactRole) {
        this.contactRole = contactRole;
    }

    /**
     * Corporate Responsibility Approval
     * 
     */
    @JsonProperty("corpRespApproval")
    public String getCorpRespApproval() {
        return corpRespApproval;
    }

    /**
     * Corporate Responsibility Approval
     * 
     */
    @JsonProperty("corpRespApproval")
    public void setCorpRespApproval(String corpRespApproval) {
        this.corpRespApproval = corpRespApproval;
    }

    /**
     * Created On- format:date-time
     * 
     */
    @JsonProperty("suppCreatedOn")
    public String getSuppCreatedOn() {
        return suppCreatedOn;
    }

    /**
     * Created On- format:date-time
     * 
     */
    @JsonProperty("suppCreatedOn")
    public void setSuppCreatedOn(String suppCreatedOn) {
        this.suppCreatedOn = suppCreatedOn;
    }

    /**
     * Email
     * 
     */
    @JsonProperty("suppEmail")
    public String getSuppEmail() {
        return suppEmail;
    }

    /**
     * Email
     * 
     */
    @JsonProperty("suppEmail")
    public void setSuppEmail(String suppEmail) {
        this.suppEmail = suppEmail;
    }

    /**
     * Financial Risk
     * 
     */
    @JsonProperty("finRisk")
    public String getFinRisk() {
        return finRisk;
    }

    /**
     * Financial Risk
     * 
     */
    @JsonProperty("finRisk")
    public void setFinRisk(String finRisk) {
        this.finRisk = finRisk;
    }

    /**
     * First Name
     * 
     */
    @JsonProperty("suppFirstName")
    public String getSuppFirstName() {
        return suppFirstName;
    }

    /**
     * First Name
     * 
     */
    @JsonProperty("suppFirstName")
    public void setSuppFirstName(String suppFirstName) {
        this.suppFirstName = suppFirstName;
    }

    /**
     * Last Name
     * 
     */
    @JsonProperty("suppLastName")
    public String getSuppLastName() {
        return suppLastName;
    }

    /**
     * Last Name
     * 
     */
    @JsonProperty("suppLastName")
    public void setSuppLastName(String suppLastName) {
        this.suppLastName = suppLastName;
    }

    /**
     * Last Modified- format:date-time
     * 
     */
    @JsonProperty("suppLastMod")
    public String getSuppLastMod() {
        return suppLastMod;
    }

    /**
     * Last Modified- format:date-time
     * 
     */
    @JsonProperty("suppLastMod")
    public void setSuppLastMod(String suppLastMod) {
        this.suppLastMod = suppLastMod;
    }

    /**
     * Modified By
     * 
     */
    @JsonProperty("suppModBy")
    public String getSuppModBy() {
        return suppModBy;
    }

    /**
     * Modified By
     * 
     */
    @JsonProperty("suppModBy")
    public void setSuppModBy(String suppModBy) {
        this.suppModBy = suppModBy;
    }

    /**
     * Manufacturing Excellence Rating
     * 
     */
    @JsonProperty("manExcellenceRating")
    public String getManExcellenceRating() {
        return manExcellenceRating;
    }

    /**
     * Manufacturing Excellence Rating
     * 
     */
    @JsonProperty("manExcellenceRating")
    public void setManExcellenceRating(String manExcellenceRating) {
        this.manExcellenceRating = manExcellenceRating;
    }

    /**
     * Migrated?
     * 
     */
    @JsonProperty("migrated")
    public Boolean getMigrated() {
        return migrated;
    }

    /**
     * Migrated?
     * 
     */
    @JsonProperty("migrated")
    public void setMigrated(Boolean migrated) {
        this.migrated = migrated;
    }

    /**
     * Mobile No. (Incl. Country Code)
     * 
     */
    @JsonProperty("suppMobile")
    public String getSuppMobile() {
        return suppMobile;
    }

    /**
     * Mobile No. (Incl. Country Code)
     * 
     */
    @JsonProperty("suppMobile")
    public void setSuppMobile(String suppMobile) {
        this.suppMobile = suppMobile;
    }

    /**
     * Notes
     * 
     */
    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    /**
     * Notes
     * 
     */
    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Partner Rank
     * 
     */
    @JsonProperty("partnerRank")
    public String getPartnerRank() {
        return partnerRank;
    }

    /**
     * Partner Rank
     * 
     */
    @JsonProperty("partnerRank")
    public void setPartnerRank(String partnerRank) {
        this.partnerRank = partnerRank;
    }

    /**
     * Partner Trading Currency
     * 
     */
    @JsonProperty("partnerTrade")
    public String getPartnerTrade() {
        return partnerTrade;
    }

    /**
     * Partner Trading Currency
     * 
     */
    @JsonProperty("partnerTrade")
    public void setPartnerTrade(String partnerTrade) {
        this.partnerTrade = partnerTrade;
    }

    /**
     * Zip/PostCode
     * 
     */
    @JsonProperty("suppPostCode")
    public String getSuppPostCode() {
        return suppPostCode;
    }

    /**
     * Zip/PostCode
     * 
     */
    @JsonProperty("suppPostCode")
    public void setSuppPostCode(String suppPostCode) {
        this.suppPostCode = suppPostCode;
    }

    /**
     * Restricted Substances Management Approval
     * 
     */
    @JsonProperty("restSubsApproval")
    public String getRestSubsApproval() {
        return restSubsApproval;
    }

    /**
     * Restricted Substances Management Approval
     * 
     */
    @JsonProperty("restSubsApproval")
    public void setRestSubsApproval(String restSubsApproval) {
        this.restSubsApproval = restSubsApproval;
    }

    /**
     * SAP Vendor ID
     * 
     */
    @JsonProperty("sapVendorId")
    public String getSapVendorId() {
        return sapVendorId;
    }

    /**
     * SAP Vendor ID
     * 
     */
    @JsonProperty("sapVendorId")
    public void setSapVendorId(String sapVendorId) {
        this.sapVendorId = sapVendorId;
    }

    /**
     * Skype ID
     * 
     */
    @JsonProperty("skypeId")
    public String getSkypeId() {
        return skypeId;
    }

    /**
     * Skype ID
     * 
     */
    @JsonProperty("skypeId")
    public void setSkypeId(String skypeId) {
        this.skypeId = skypeId;
    }

    /**
     * State / Province / County
     * 
     */
    @JsonProperty("suppStateProv")
    public String getSuppStateProv() {
        return suppStateProv;
    }

    /**
     * State / Province / County
     * 
     */
    @JsonProperty("suppStateProv")
    public void setSuppStateProv(String suppStateProv) {
        this.suppStateProv = suppStateProv;
    }

    /**
     * Status
     * 
     */
    @JsonProperty("suppStatus")
    public String getSuppStatus() {
        return suppStatus;
    }

    /**
     * Status
     * 
     */
    @JsonProperty("suppStatus")
    public void setSuppStatus(String suppStatus) {
        this.suppStatus = suppStatus;
    }

    /**
     * Supplier name
     * 
     */
    @JsonProperty("suppName")
    public String getSuppName() {
        return suppName;
    }

    /**
     * Supplier name
     * 
     */
    @JsonProperty("suppName")
    public void setSuppName(String suppName) {
        this.suppName = suppName;
    }

    /**
     * Supplier Capabilities (multi-list)
     * 
     */
    @JsonProperty("suppCapable")
    public String getSuppCapable() {
        return suppCapable;
    }

    /**
     * Supplier Capabilities (multi-list)
     * 
     */
    @JsonProperty("suppCapable")
    public void setSuppCapable(String suppCapable) {
        this.suppCapable = suppCapable;
    }

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    public String getSuppRefNo() {
        return suppRefNo;
    }

    /**
     * Supplier Reference Number (sequence)
     * 
     */
    @JsonProperty("suppRefNo")
    public void setSuppRefNo(String suppRefNo) {
        this.suppRefNo = suppRefNo;
    }

    /**
     * Supplier Sub- Type (multi-list)
     * 
     */
    @JsonProperty("suppSubType")
    public String getSuppSubType() {
        return suppSubType;
    }

    /**
     * Supplier Sub- Type (multi-list)
     * 
     */
    @JsonProperty("suppSubType")
    public void setSuppSubType(String suppSubType) {
        this.suppSubType = suppSubType;
    }

    /**
     * Sustainable Raw Material (multi-list)
     * 
     */
    @JsonProperty("susRawMat")
    public String getSusRawMat() {
        return susRawMat;
    }

    /**
     * Sustainable Raw Material (multi-list)
     * 
     */
    @JsonProperty("susRawMat")
    public void setSusRawMat(String susRawMat) {
        this.susRawMat = susRawMat;
    }

    /**
     * Telephone No. (Incl. Country Code)
     * 
     */
    @JsonProperty("suppTelNo")
    public String getSuppTelNo() {
        return suppTelNo;
    }

    /**
     * Telephone No. (Incl. Country Code)
     * 
     */
    @JsonProperty("suppTelNo")
    public void setSuppTelNo(String suppTelNo) {
        this.suppTelNo = suppTelNo;
    }

    /**
     * Supplier Title
     * 
     */
    @JsonProperty("suppTitle")
    public String getSuppTitle() {
        return suppTitle;
    }

    /**
     * Supplier Title
     * 
     */
    @JsonProperty("suppTitle")
    public void setSuppTitle(String suppTitle) {
        this.suppTitle = suppTitle;
    }

    /**
     * Vendor Group
     * 
     */
    @JsonProperty("vendGroup")
    public String getVendGroup() {
        return vendGroup;
    }

    /**
     * Vendor Group
     * 
     */
    @JsonProperty("vendGroup")
    public void setVendGroup(String vendGroup) {
        this.vendGroup = vendGroup;
    }

    /**
     * Country
     * 
     */
    @JsonProperty("suppCountry")
    public String getSuppCountry() {
        return suppCountry;
    }

    /**
     * Country
     * 
     */
    @JsonProperty("suppCountry")
    public void setSuppCountry(String suppCountry) {
        this.suppCountry = suppCountry;
    }

    /**
     * RM Supplier MOQ
     * 
     */
    @JsonProperty("rmSuppMoq")
    public String getRmSuppMoq() {
        return rmSuppMoq;
    }

    /**
     * RM Supplier MOQ
     * 
     */
    @JsonProperty("rmSuppMoq")
    public void setRmSuppMoq(String rmSuppMoq) {
        this.rmSuppMoq = rmSuppMoq;
    }

    /**
     * RM Supplier MOQ UOM
     * 
     */
    @JsonProperty("rmSuppMoqUom")
    public String getRmSuppMoqUom() {
        return rmSuppMoqUom;
    }

    /**
     * RM Supplier MOQ UOM
     * 
     */
    @JsonProperty("rmSuppMoqUom")
    public void setRmSuppMoqUom(String rmSuppMoqUom) {
        this.rmSuppMoqUom = rmSuppMoqUom;
    }

    /**
     * RM Supplier MOQ UOM
     * 
     */
    @JsonProperty("rmSurcharge")
    public String getRmSurcharge() {
        return rmSurcharge;
    }

    /**
     * RM Supplier MOQ UOM
     * 
     */
    @JsonProperty("rmSurcharge")
    public void setRmSurcharge(String rmSurcharge) {
        this.rmSurcharge = rmSurcharge;
    }

}

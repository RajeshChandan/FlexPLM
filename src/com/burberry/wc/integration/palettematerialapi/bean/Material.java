
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "materialId",
    "materialName",
    "clrCntrled",
    "clrCntrlMode",
    "matCreatedOn",
    "matLastMod",
    "matModBy",
    "matCreatedBy",
    "matDesc",
    "devSeason",
    "devCOO",
    "dryClean",
    "farmWild",
    "finish",
    "matCitesCat",
    "matCitesType",
    "matComment",
    "matLibNode",
    "matRiskLevel",
    "matStatus",
    "matType",
    "matSubType",
    "matImgUrl",
    "rawMatCode",
    "swingTktId",
    "matTrimGrp",
    "varnishType",
    "washInstruct",
    "fibreContent",
    "coatProcess",
    "coatThick",
    "coatType",
    "coatTypeTrim",
    "commonName",
    "contentSearch",
    "drying",
    "latinName",
    "legacyMatCode",
    "matPricingMode",
    "stoneType",
    "subNicketContent",
    "thickness",
    "washTemp",
    "ironing",
    "chemFinish",
    "weight",
    "weightUom",
    "yarnCount",
    "gauge",
    "impressIntent",
    "treatment",
    "lastApplicable",
    "tannage",
    "ferrous",
    "size",
    "zipPullSize",
    "ligne",
    "reverseMaterial",
    "singleRecordTrim",
    "colourComp",
    "materialIP",
    "sustainMaterial",
    "compConfirmed",
    "texWeight",
    "matRecordType",
    "yarnDetails",
    "documents",
    "materialSupplier",
    "palette",
    "riskManagement"
})
public class Material implements Serializable
{

    /**
     * Material Id
     * (Required)
     * 
     */
    @JsonProperty("materialId")
    @JsonPropertyDescription("Material Id")
    private Integer materialId;
    /**
     * Material Name
     * 
     */
    @JsonProperty("materialName")
    @JsonPropertyDescription("Material Name")
    private String materialName;
    /**
     * Colour Controlled
     * 
     */
    @JsonProperty("clrCntrled")
    @JsonPropertyDescription("Colour Controlled")
    private String clrCntrled;
    /**
     * Colour Control Mode
     * 
     */
    @JsonProperty("clrCntrlMode")
    @JsonPropertyDescription("Colour Control Mode")
    private String clrCntrlMode;
    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("matCreatedOn")
    @JsonPropertyDescription("Created on- format:date-time")
    private String matCreatedOn;
    /**
     * Material Last Modified Date- format:date-time
     * 
     */
    @JsonProperty("matLastMod")
    @JsonPropertyDescription("Material Last Modified Date- format:date-time")
    private String matLastMod;
    /**
     * Material Modified By
     * 
     */
    @JsonProperty("matModBy")
    @JsonPropertyDescription("Material Modified By")
    private String matModBy;
    /**
     * Created By
     * 
     */
    @JsonProperty("matCreatedBy")
    @JsonPropertyDescription("Created By")
    private String matCreatedBy;
    /**
     * Description
     * 
     */
    @JsonProperty("matDesc")
    @JsonPropertyDescription("Description")
    private String matDesc;
    /**
     * Dev. Season
     * 
     */
    @JsonProperty("devSeason")
    @JsonPropertyDescription("Dev. Season")
    private String devSeason;
    /**
     * Development COO
     * 
     */
    @JsonProperty("devCOO")
    @JsonPropertyDescription("Development COO")
    private String devCOO;
    /**
     * Dry Clean Instructions
     * 
     */
    @JsonProperty("dryClean")
    @JsonPropertyDescription("Dry Clean Instructions")
    private String dryClean;
    /**
     * Farmed/Wild
     * 
     */
    @JsonProperty("farmWild")
    @JsonPropertyDescription("Farmed/Wild")
    private String farmWild;
    /**
     * Finish
     * 
     */
    @JsonProperty("finish")
    @JsonPropertyDescription("Finish")
    private String finish;
    /**
     * CITES Category
     * 
     */
    @JsonProperty("matCitesCat")
    @JsonPropertyDescription("CITES Category")
    private String matCitesCat;
    /**
     * CITES Material Type
     * 
     */
    @JsonProperty("matCitesType")
    @JsonPropertyDescription("CITES Material Type")
    private String matCitesType;
    /**
     * Material Comments
     * 
     */
    @JsonProperty("matComment")
    @JsonPropertyDescription("Material Comments")
    private String matComment;
    /**
     * Material Library Sub-Type
     * 
     */
    @JsonProperty("matLibNode")
    @JsonPropertyDescription("Material Library Sub-Type")
    private String matLibNode;
    /**
     * Material Risk Level
     * 
     */
    @JsonProperty("matRiskLevel")
    @JsonPropertyDescription("Material Risk Level")
    private String matRiskLevel;
    /**
     * Material Status
     * 
     */
    @JsonProperty("matStatus")
    @JsonPropertyDescription("Material Status")
    private String matStatus;
    /**
     * Material Library Type
     * 
     */
    @JsonProperty("matType")
    @JsonPropertyDescription("Material Library Type")
    private String matType;
    /**
     * Material Record Sub-Type
     * 
     */
    @JsonProperty("matSubType")
    @JsonPropertyDescription("Material Record Sub-Type")
    private String matSubType;
    /**
     * Primary Image URL
     * 
     */
    @JsonProperty("matImgUrl")
    @JsonPropertyDescription("Primary Image URL")
    private String matImgUrl;
    /**
     * Raw Material Code
     * 
     */
    @JsonProperty("rawMatCode")
    @JsonPropertyDescription("Raw Material Code")
    private String rawMatCode;
    /**
     * Swing Ticket Id
     * 
     */
    @JsonProperty("swingTktId")
    @JsonPropertyDescription("Swing Ticket Id")
    private String swingTktId;
    /**
     * Trim Group
     * 
     */
    @JsonProperty("matTrimGrp")
    @JsonPropertyDescription("Trim Group")
    private String matTrimGrp;
    /**
     * Varnish Type
     * 
     */
    @JsonProperty("varnishType")
    @JsonPropertyDescription("Varnish Type")
    private String varnishType;
    /**
     * Wash Instructions
     * 
     */
    @JsonProperty("washInstruct")
    @JsonPropertyDescription("Wash Instructions")
    private String washInstruct;
    /**
     * Fibre Content
     * 
     */
    @JsonProperty("fibreContent")
    @JsonPropertyDescription("Fibre Content")
    private String fibreContent;
    /**
     * Coating Process
     * 
     */
    @JsonProperty("coatProcess")
    @JsonPropertyDescription("Coating Process")
    private String coatProcess;
    /**
     * Coating Thickness (MM)
     * 
     */
    @JsonProperty("coatThick")
    @JsonPropertyDescription("Coating Thickness (MM)")
    private String coatThick;
    /**
     * Coating Type
     * 
     */
    @JsonProperty("coatType")
    @JsonPropertyDescription("Coating Type")
    private String coatType;
    /**
     * Coating Type (trim)
     * 
     */
    @JsonProperty("coatTypeTrim")
    @JsonPropertyDescription("Coating Type (trim)")
    private String coatTypeTrim;
    /**
     * Common Name
     * 
     */
    @JsonProperty("commonName")
    @JsonPropertyDescription("Common Name")
    private String commonName;
    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    @JsonPropertyDescription("Content (Searchable)")
    private String contentSearch;
    /**
     * Drying
     * 
     */
    @JsonProperty("drying")
    @JsonPropertyDescription("Drying")
    private String drying;
    /**
     * Latin Name
     * 
     */
    @JsonProperty("latinName")
    @JsonPropertyDescription("Latin Name")
    private String latinName;
    /**
     * Legacy Material Code
     * 
     */
    @JsonProperty("legacyMatCode")
    @JsonPropertyDescription("Legacy Material Code")
    private String legacyMatCode;
    /**
     * Material Pricing Mode
     * 
     */
    @JsonProperty("matPricingMode")
    @JsonPropertyDescription("Material Pricing Mode")
    private String matPricingMode;
    /**
     * Stone Type
     * 
     */
    @JsonProperty("stoneType")
    @JsonPropertyDescription("Stone Type")
    private String stoneType;
    /**
     * Substrate Nickel Content
     * 
     */
    @JsonProperty("subNicketContent")
    @JsonPropertyDescription("Substrate Nickel Content")
    private String subNicketContent;
    /**
     * Thickness -Fur
     * 
     */
    @JsonProperty("thickness")
    @JsonPropertyDescription("Thickness -Fur")
    private String thickness;
    /**
     * Wash Temparature
     * 
     */
    @JsonProperty("washTemp")
    @JsonPropertyDescription("Wash Temparature")
    private String washTemp;
    /**
     * Ironing
     * 
     */
    @JsonProperty("ironing")
    @JsonPropertyDescription("Ironing")
    private String ironing;
    /**
     * Chemical Finish (multilist)
     * 
     */
    @JsonProperty("chemFinish")
    @JsonPropertyDescription("Chemical Finish (multilist)")
    private String chemFinish;
    /**
     * Weight
     * 
     */
    @JsonProperty("weight")
    @JsonPropertyDescription("Weight")
    private String weight;
    /**
     * Weight UOM
     * 
     */
    @JsonProperty("weightUom")
    @JsonPropertyDescription("Weight UOM")
    private String weightUom;
    /**
     * Yarn Count
     * 
     */
    @JsonProperty("yarnCount")
    @JsonPropertyDescription("Yarn Count")
    private String yarnCount;
    /**
     * gauge
     * 
     */
    @JsonProperty("gauge")
    @JsonPropertyDescription("gauge")
    private String gauge;
    /**
     * Print/Dye Process
     * 
     */
    @JsonProperty("impressIntent")
    @JsonPropertyDescription("Print/Dye Process")
    private String impressIntent;
    /**
     * Treatment (multilist)
     * 
     */
    @JsonProperty("treatment")
    @JsonPropertyDescription("Treatment (multilist)")
    private String treatment;
    /**
     * Last Applicability
     * 
     */
    @JsonProperty("lastApplicable")
    @JsonPropertyDescription("Last Applicability")
    private String lastApplicable;
    /**
     * Last Applicability
     * 
     */
    @JsonProperty("tannage")
    @JsonPropertyDescription("Last Applicability")
    private String tannage;
    /**
     * Ferrous or Non Ferrous
     * 
     */
    @JsonProperty("ferrous")
    @JsonPropertyDescription("Ferrous or Non Ferrous")
    private String ferrous;
    /**
     * Size (trims)
     * 
     */
    @JsonProperty("size")
    @JsonPropertyDescription("Size (trims)")
    private String size;
    /**
     * Zip Pull Size
     * 
     */
    @JsonProperty("zipPullSize")
    @JsonPropertyDescription("Zip Pull Size")
    private String zipPullSize;
    /**
     * Ligne
     * 
     */
    @JsonProperty("ligne")
    @JsonPropertyDescription("Ligne")
    private String ligne;
    /**
     * Reversible Material
     * 
     */
    @JsonProperty("reverseMaterial")
    @JsonPropertyDescription("Reversible Material")
    private String reverseMaterial;
    /**
     * Single Record Trim
     * 
     */
    @JsonProperty("singleRecordTrim")
    @JsonPropertyDescription("Single Record Trim")
    private String singleRecordTrim;
    /**
     * Colour Components
     * 
     */
    @JsonProperty("colourComp")
    @JsonPropertyDescription("Colour Components")
    private String colourComp;
    /**
     * material IP
     * 
     */
    @JsonProperty("materialIP")
    @JsonPropertyDescription("material IP")
    private Boolean materialIP;
    /**
     * Sustainable Material
     * 
     */
    @JsonProperty("sustainMaterial")
    @JsonPropertyDescription("Sustainable Material")
    private String sustainMaterial;
    /**
     * Composition Confirmed
     * 
     */
    @JsonProperty("compConfirmed")
    @JsonPropertyDescription("Composition Confirmed")
    private Boolean compConfirmed;
    /**
     * Tex Weight
     * 
     */
    @JsonProperty("texWeight")
    @JsonPropertyDescription("Tex Weight")
    private String texWeight;
    /**
     * Material Record Type
     * 
     */
    @JsonProperty("matRecordType")
    @JsonPropertyDescription("Material Record Type")
    private String matRecordType;
    @JsonProperty("yarnDetails")
    private List<YarnDetail> yarnDetails = null;
    @JsonProperty("documents")
    private List<Document> documents = null;
    @JsonProperty("materialSupplier")
    private List<MaterialSupplier> materialSupplier = null;
    @JsonProperty("palette")
    private List<Palette> palette = null;
    @JsonProperty("riskManagement")
    private List<RiskManagement> riskManagement = null;
    private final static long serialVersionUID = 8685654553094908437L;

    /**
     * Material Id
     * (Required)
     * 
     */
    @JsonProperty("materialId")
    public Integer getMaterialId() {
        return materialId;
    }

    /**
     * Material Id
     * (Required)
     * 
     */
    @JsonProperty("materialId")
    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    /**
     * Material Name
     * 
     */
    @JsonProperty("materialName")
    public String getMaterialName() {
        return materialName;
    }

    /**
     * Material Name
     * 
     */
    @JsonProperty("materialName")
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    /**
     * Colour Controlled
     * 
     */
    @JsonProperty("clrCntrled")
    public String getClrCntrled() {
        return clrCntrled;
    }

    /**
     * Colour Controlled
     * 
     */
    @JsonProperty("clrCntrled")
    public void setClrCntrled(String clrCntrled) {
        this.clrCntrled = clrCntrled;
    }

    /**
     * Colour Control Mode
     * 
     */
    @JsonProperty("clrCntrlMode")
    public String getClrCntrlMode() {
        return clrCntrlMode;
    }

    /**
     * Colour Control Mode
     * 
     */
    @JsonProperty("clrCntrlMode")
    public void setClrCntrlMode(String clrCntrlMode) {
        this.clrCntrlMode = clrCntrlMode;
    }

    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("matCreatedOn")
    public String getMatCreatedOn() {
        return matCreatedOn;
    }

    /**
     * Created on- format:date-time
     * 
     */
    @JsonProperty("matCreatedOn")
    public void setMatCreatedOn(String matCreatedOn) {
        this.matCreatedOn = matCreatedOn;
    }

    /**
     * Material Last Modified Date- format:date-time
     * 
     */
    @JsonProperty("matLastMod")
    public String getMatLastMod() {
        return matLastMod;
    }

    /**
     * Material Last Modified Date- format:date-time
     * 
     */
    @JsonProperty("matLastMod")
    public void setMatLastMod(String matLastMod) {
        this.matLastMod = matLastMod;
    }

    /**
     * Material Modified By
     * 
     */
    @JsonProperty("matModBy")
    public String getMatModBy() {
        return matModBy;
    }

    /**
     * Material Modified By
     * 
     */
    @JsonProperty("matModBy")
    public void setMatModBy(String matModBy) {
        this.matModBy = matModBy;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("matCreatedBy")
    public String getMatCreatedBy() {
        return matCreatedBy;
    }

    /**
     * Created By
     * 
     */
    @JsonProperty("matCreatedBy")
    public void setMatCreatedBy(String matCreatedBy) {
        this.matCreatedBy = matCreatedBy;
    }

    /**
     * Description
     * 
     */
    @JsonProperty("matDesc")
    public String getMatDesc() {
        return matDesc;
    }

    /**
     * Description
     * 
     */
    @JsonProperty("matDesc")
    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    /**
     * Dev. Season
     * 
     */
    @JsonProperty("devSeason")
    public String getDevSeason() {
        return devSeason;
    }

    /**
     * Dev. Season
     * 
     */
    @JsonProperty("devSeason")
    public void setDevSeason(String devSeason) {
        this.devSeason = devSeason;
    }

    /**
     * Development COO
     * 
     */
    @JsonProperty("devCOO")
    public String getDevCOO() {
        return devCOO;
    }

    /**
     * Development COO
     * 
     */
    @JsonProperty("devCOO")
    public void setDevCOO(String devCOO) {
        this.devCOO = devCOO;
    }

    /**
     * Dry Clean Instructions
     * 
     */
    @JsonProperty("dryClean")
    public String getDryClean() {
        return dryClean;
    }

    /**
     * Dry Clean Instructions
     * 
     */
    @JsonProperty("dryClean")
    public void setDryClean(String dryClean) {
        this.dryClean = dryClean;
    }

    /**
     * Farmed/Wild
     * 
     */
    @JsonProperty("farmWild")
    public String getFarmWild() {
        return farmWild;
    }

    /**
     * Farmed/Wild
     * 
     */
    @JsonProperty("farmWild")
    public void setFarmWild(String farmWild) {
        this.farmWild = farmWild;
    }

    /**
     * Finish
     * 
     */
    @JsonProperty("finish")
    public String getFinish() {
        return finish;
    }

    /**
     * Finish
     * 
     */
    @JsonProperty("finish")
    public void setFinish(String finish) {
        this.finish = finish;
    }

    /**
     * CITES Category
     * 
     */
    @JsonProperty("matCitesCat")
    public String getMatCitesCat() {
        return matCitesCat;
    }

    /**
     * CITES Category
     * 
     */
    @JsonProperty("matCitesCat")
    public void setMatCitesCat(String matCitesCat) {
        this.matCitesCat = matCitesCat;
    }

    /**
     * CITES Material Type
     * 
     */
    @JsonProperty("matCitesType")
    public String getMatCitesType() {
        return matCitesType;
    }

    /**
     * CITES Material Type
     * 
     */
    @JsonProperty("matCitesType")
    public void setMatCitesType(String matCitesType) {
        this.matCitesType = matCitesType;
    }

    /**
     * Material Comments
     * 
     */
    @JsonProperty("matComment")
    public String getMatComment() {
        return matComment;
    }

    /**
     * Material Comments
     * 
     */
    @JsonProperty("matComment")
    public void setMatComment(String matComment) {
        this.matComment = matComment;
    }

    /**
     * Material Library Sub-Type
     * 
     */
    @JsonProperty("matLibNode")
    public String getMatLibNode() {
        return matLibNode;
    }

    /**
     * Material Library Sub-Type
     * 
     */
    @JsonProperty("matLibNode")
    public void setMatLibNode(String matLibNode) {
        this.matLibNode = matLibNode;
    }

    /**
     * Material Risk Level
     * 
     */
    @JsonProperty("matRiskLevel")
    public String getMatRiskLevel() {
        return matRiskLevel;
    }

    /**
     * Material Risk Level
     * 
     */
    @JsonProperty("matRiskLevel")
    public void setMatRiskLevel(String matRiskLevel) {
        this.matRiskLevel = matRiskLevel;
    }

    /**
     * Material Status
     * 
     */
    @JsonProperty("matStatus")
    public String getMatStatus() {
        return matStatus;
    }

    /**
     * Material Status
     * 
     */
    @JsonProperty("matStatus")
    public void setMatStatus(String matStatus) {
        this.matStatus = matStatus;
    }

    /**
     * Material Library Type
     * 
     */
    @JsonProperty("matType")
    public String getMatType() {
        return matType;
    }

    /**
     * Material Library Type
     * 
     */
    @JsonProperty("matType")
    public void setMatType(String matType) {
        this.matType = matType;
    }

    /**
     * Material Record Sub-Type
     * 
     */
    @JsonProperty("matSubType")
    public String getMatSubType() {
        return matSubType;
    }

    /**
     * Material Record Sub-Type
     * 
     */
    @JsonProperty("matSubType")
    public void setMatSubType(String matSubType) {
        this.matSubType = matSubType;
    }

    /**
     * Primary Image URL
     * 
     */
    @JsonProperty("matImgUrl")
    public String getMatImgUrl() {
        return matImgUrl;
    }

    /**
     * Primary Image URL
     * 
     */
    @JsonProperty("matImgUrl")
    public void setMatImgUrl(String matImgUrl) {
        this.matImgUrl = matImgUrl;
    }

    /**
     * Raw Material Code
     * 
     */
    @JsonProperty("rawMatCode")
    public String getRawMatCode() {
        return rawMatCode;
    }

    /**
     * Raw Material Code
     * 
     */
    @JsonProperty("rawMatCode")
    public void setRawMatCode(String rawMatCode) {
        this.rawMatCode = rawMatCode;
    }

    /**
     * Swing Ticket Id
     * 
     */
    @JsonProperty("swingTktId")
    public String getSwingTktId() {
        return swingTktId;
    }

    /**
     * Swing Ticket Id
     * 
     */
    @JsonProperty("swingTktId")
    public void setSwingTktId(String swingTktId) {
        this.swingTktId = swingTktId;
    }

    /**
     * Trim Group
     * 
     */
    @JsonProperty("matTrimGrp")
    public String getMatTrimGrp() {
        return matTrimGrp;
    }

    /**
     * Trim Group
     * 
     */
    @JsonProperty("matTrimGrp")
    public void setMatTrimGrp(String matTrimGrp) {
        this.matTrimGrp = matTrimGrp;
    }

    /**
     * Varnish Type
     * 
     */
    @JsonProperty("varnishType")
    public String getVarnishType() {
        return varnishType;
    }

    /**
     * Varnish Type
     * 
     */
    @JsonProperty("varnishType")
    public void setVarnishType(String varnishType) {
        this.varnishType = varnishType;
    }

    /**
     * Wash Instructions
     * 
     */
    @JsonProperty("washInstruct")
    public String getWashInstruct() {
        return washInstruct;
    }

    /**
     * Wash Instructions
     * 
     */
    @JsonProperty("washInstruct")
    public void setWashInstruct(String washInstruct) {
        this.washInstruct = washInstruct;
    }

    /**
     * Fibre Content
     * 
     */
    @JsonProperty("fibreContent")
    public String getFibreContent() {
        return fibreContent;
    }

    /**
     * Fibre Content
     * 
     */
    @JsonProperty("fibreContent")
    public void setFibreContent(String fibreContent) {
        this.fibreContent = fibreContent;
    }

    /**
     * Coating Process
     * 
     */
    @JsonProperty("coatProcess")
    public String getCoatProcess() {
        return coatProcess;
    }

    /**
     * Coating Process
     * 
     */
    @JsonProperty("coatProcess")
    public void setCoatProcess(String coatProcess) {
        this.coatProcess = coatProcess;
    }

    /**
     * Coating Thickness (MM)
     * 
     */
    @JsonProperty("coatThick")
    public String getCoatThick() {
        return coatThick;
    }

    /**
     * Coating Thickness (MM)
     * 
     */
    @JsonProperty("coatThick")
    public void setCoatThick(String coatThick) {
        this.coatThick = coatThick;
    }

    /**
     * Coating Type
     * 
     */
    @JsonProperty("coatType")
    public String getCoatType() {
        return coatType;
    }

    /**
     * Coating Type
     * 
     */
    @JsonProperty("coatType")
    public void setCoatType(String coatType) {
        this.coatType = coatType;
    }

    /**
     * Coating Type (trim)
     * 
     */
    @JsonProperty("coatTypeTrim")
    public String getCoatTypeTrim() {
        return coatTypeTrim;
    }

    /**
     * Coating Type (trim)
     * 
     */
    @JsonProperty("coatTypeTrim")
    public void setCoatTypeTrim(String coatTypeTrim) {
        this.coatTypeTrim = coatTypeTrim;
    }

    /**
     * Common Name
     * 
     */
    @JsonProperty("commonName")
    public String getCommonName() {
        return commonName;
    }

    /**
     * Common Name
     * 
     */
    @JsonProperty("commonName")
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    public String getContentSearch() {
        return contentSearch;
    }

    /**
     * Content (Searchable)
     * 
     */
    @JsonProperty("contentSearch")
    public void setContentSearch(String contentSearch) {
        this.contentSearch = contentSearch;
    }

    /**
     * Drying
     * 
     */
    @JsonProperty("drying")
    public String getDrying() {
        return drying;
    }

    /**
     * Drying
     * 
     */
    @JsonProperty("drying")
    public void setDrying(String drying) {
        this.drying = drying;
    }

    /**
     * Latin Name
     * 
     */
    @JsonProperty("latinName")
    public String getLatinName() {
        return latinName;
    }

    /**
     * Latin Name
     * 
     */
    @JsonProperty("latinName")
    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    /**
     * Legacy Material Code
     * 
     */
    @JsonProperty("legacyMatCode")
    public String getLegacyMatCode() {
        return legacyMatCode;
    }

    /**
     * Legacy Material Code
     * 
     */
    @JsonProperty("legacyMatCode")
    public void setLegacyMatCode(String legacyMatCode) {
        this.legacyMatCode = legacyMatCode;
    }

    /**
     * Material Pricing Mode
     * 
     */
    @JsonProperty("matPricingMode")
    public String getMatPricingMode() {
        return matPricingMode;
    }

    /**
     * Material Pricing Mode
     * 
     */
    @JsonProperty("matPricingMode")
    public void setMatPricingMode(String matPricingMode) {
        this.matPricingMode = matPricingMode;
    }

    /**
     * Stone Type
     * 
     */
    @JsonProperty("stoneType")
    public String getStoneType() {
        return stoneType;
    }

    /**
     * Stone Type
     * 
     */
    @JsonProperty("stoneType")
    public void setStoneType(String stoneType) {
        this.stoneType = stoneType;
    }

    /**
     * Substrate Nickel Content
     * 
     */
    @JsonProperty("subNicketContent")
    public String getSubNicketContent() {
        return subNicketContent;
    }

    /**
     * Substrate Nickel Content
     * 
     */
    @JsonProperty("subNicketContent")
    public void setSubNicketContent(String subNicketContent) {
        this.subNicketContent = subNicketContent;
    }

    /**
     * Thickness -Fur
     * 
     */
    @JsonProperty("thickness")
    public String getThickness() {
        return thickness;
    }

    /**
     * Thickness -Fur
     * 
     */
    @JsonProperty("thickness")
    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    /**
     * Wash Temparature
     * 
     */
    @JsonProperty("washTemp")
    public String getWashTemp() {
        return washTemp;
    }

    /**
     * Wash Temparature
     * 
     */
    @JsonProperty("washTemp")
    public void setWashTemp(String washTemp) {
        this.washTemp = washTemp;
    }

    /**
     * Ironing
     * 
     */
    @JsonProperty("ironing")
    public String getIroning() {
        return ironing;
    }

    /**
     * Ironing
     * 
     */
    @JsonProperty("ironing")
    public void setIroning(String ironing) {
        this.ironing = ironing;
    }

    /**
     * Chemical Finish (multilist)
     * 
     */
    @JsonProperty("chemFinish")
    public String getChemFinish() {
        return chemFinish;
    }

    /**
     * Chemical Finish (multilist)
     * 
     */
    @JsonProperty("chemFinish")
    public void setChemFinish(String chemFinish) {
        this.chemFinish = chemFinish;
    }

    /**
     * Weight
     * 
     */
    @JsonProperty("weight")
    public String getWeight() {
        return weight;
    }

    /**
     * Weight
     * 
     */
    @JsonProperty("weight")
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     * Weight UOM
     * 
     */
    @JsonProperty("weightUom")
    public String getWeightUom() {
        return weightUom;
    }

    /**
     * Weight UOM
     * 
     */
    @JsonProperty("weightUom")
    public void setWeightUom(String weightUom) {
        this.weightUom = weightUom;
    }

    /**
     * Yarn Count
     * 
     */
    @JsonProperty("yarnCount")
    public String getYarnCount() {
        return yarnCount;
    }

    /**
     * Yarn Count
     * 
     */
    @JsonProperty("yarnCount")
    public void setYarnCount(String yarnCount) {
        this.yarnCount = yarnCount;
    }

    /**
     * gauge
     * 
     */
    @JsonProperty("gauge")
    public String getGauge() {
        return gauge;
    }

    /**
     * gauge
     * 
     */
    @JsonProperty("gauge")
    public void setGauge(String gauge) {
        this.gauge = gauge;
    }

    /**
     * Print/Dye Process
     * 
     */
    @JsonProperty("impressIntent")
    public String getImpressIntent() {
        return impressIntent;
    }

    /**
     * Print/Dye Process
     * 
     */
    @JsonProperty("impressIntent")
    public void setImpressIntent(String impressIntent) {
        this.impressIntent = impressIntent;
    }

    /**
     * Treatment (multilist)
     * 
     */
    @JsonProperty("treatment")
    public String getTreatment() {
        return treatment;
    }

    /**
     * Treatment (multilist)
     * 
     */
    @JsonProperty("treatment")
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * Last Applicability
     * 
     */
    @JsonProperty("lastApplicable")
    public String getLastApplicable() {
        return lastApplicable;
    }

    /**
     * Last Applicability
     * 
     */
    @JsonProperty("lastApplicable")
    public void setLastApplicable(String lastApplicable) {
        this.lastApplicable = lastApplicable;
    }

    /**
     * Last Applicability
     * 
     */
    @JsonProperty("tannage")
    public String getTannage() {
        return tannage;
    }

    /**
     * Last Applicability
     * 
     */
    @JsonProperty("tannage")
    public void setTannage(String tannage) {
        this.tannage = tannage;
    }

    /**
     * Ferrous or Non Ferrous
     * 
     */
    @JsonProperty("ferrous")
    public String getFerrous() {
        return ferrous;
    }

    /**
     * Ferrous or Non Ferrous
     * 
     */
    @JsonProperty("ferrous")
    public void setFerrous(String ferrous) {
        this.ferrous = ferrous;
    }

    /**
     * Size (trims)
     * 
     */
    @JsonProperty("size")
    public String getSize() {
        return size;
    }

    /**
     * Size (trims)
     * 
     */
    @JsonProperty("size")
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Zip Pull Size
     * 
     */
    @JsonProperty("zipPullSize")
    public String getZipPullSize() {
        return zipPullSize;
    }

    /**
     * Zip Pull Size
     * 
     */
    @JsonProperty("zipPullSize")
    public void setZipPullSize(String zipPullSize) {
        this.zipPullSize = zipPullSize;
    }

    /**
     * Ligne
     * 
     */
    @JsonProperty("ligne")
    public String getLigne() {
        return ligne;
    }

    /**
     * Ligne
     * 
     */
    @JsonProperty("ligne")
    public void setLigne(String ligne) {
        this.ligne = ligne;
    }

    /**
     * Reversible Material
     * 
     */
    @JsonProperty("reverseMaterial")
    public String getReverseMaterial() {
        return reverseMaterial;
    }

    /**
     * Reversible Material
     * 
     */
    @JsonProperty("reverseMaterial")
    public void setReverseMaterial(String reverseMaterial) {
        this.reverseMaterial = reverseMaterial;
    }

    /**
     * Single Record Trim
     * 
     */
    @JsonProperty("singleRecordTrim")
    public String getSingleRecordTrim() {
        return singleRecordTrim;
    }

    /**
     * Single Record Trim
     * 
     */
    @JsonProperty("singleRecordTrim")
    public void setSingleRecordTrim(String singleRecordTrim) {
        this.singleRecordTrim = singleRecordTrim;
    }

    /**
     * Colour Components
     * 
     */
    @JsonProperty("colourComp")
    public String getColourComp() {
        return colourComp;
    }

    /**
     * Colour Components
     * 
     */
    @JsonProperty("colourComp")
    public void setColourComp(String colourComp) {
        this.colourComp = colourComp;
    }

    /**
     * material IP
     * 
     */
    @JsonProperty("materialIP")
    public Boolean getMaterialIP() {
        return materialIP;
    }

    /**
     * material IP
     * 
     */
    @JsonProperty("materialIP")
    public void setMaterialIP(Boolean materialIP) {
        this.materialIP = materialIP;
    }

    /**
     * Sustainable Material
     * 
     */
    @JsonProperty("sustainMaterial")
    public String getSustainMaterial() {
        return sustainMaterial;
    }

    /**
     * Sustainable Material
     * 
     */
    @JsonProperty("sustainMaterial")
    public void setSustainMaterial(String sustainMaterial) {
        this.sustainMaterial = sustainMaterial;
    }

    /**
     * Composition Confirmed
     * 
     */
    @JsonProperty("compConfirmed")
    public Boolean getCompConfirmed() {
        return compConfirmed;
    }

    /**
     * Composition Confirmed
     * 
     */
    @JsonProperty("compConfirmed")
    public void setCompConfirmed(Boolean compConfirmed) {
        this.compConfirmed = compConfirmed;
    }

    /**
     * Tex Weight
     * 
     */
    @JsonProperty("texWeight")
    public String getTexWeight() {
        return texWeight;
    }

    /**
     * Tex Weight
     * 
     */
    @JsonProperty("texWeight")
    public void setTexWeight(String texWeight) {
        this.texWeight = texWeight;
    }

    /**
     * Material Record Type
     * 
     */
    @JsonProperty("matRecordType")
    public String getMatRecordType() {
        return matRecordType;
    }

    /**
     * Material Record Type
     * 
     */
    @JsonProperty("matRecordType")
    public void setMatRecordType(String matRecordType) {
        this.matRecordType = matRecordType;
    }

    @JsonProperty("yarnDetails")
    public List<YarnDetail> getYarnDetails() {
        return yarnDetails;
    }

    @JsonProperty("yarnDetails")
    public void setYarnDetails(List<YarnDetail> yarnDetails) {
        this.yarnDetails = yarnDetails;
    }

    @JsonProperty("documents")
    public List<Document> getDocuments() {
        return documents;
    }

    @JsonProperty("documents")
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @JsonProperty("materialSupplier")
    public List<MaterialSupplier> getMaterialSupplier() {
        return materialSupplier;
    }

    @JsonProperty("materialSupplier")
    public void setMaterialSupplier(List<MaterialSupplier> materialSupplier) {
        this.materialSupplier = materialSupplier;
    }

    @JsonProperty("palette")
    public List<Palette> getPalette() {
        return palette;
    }

    @JsonProperty("palette")
    public void setPalette(List<Palette> palette) {
        this.palette = palette;
    }

    @JsonProperty("riskManagement")
    public List<RiskManagement> getRiskManagement() {
        return riskManagement;
    }

    @JsonProperty("riskManagement")
    public void setRiskManagement(List<RiskManagement> riskManagement) {
        this.riskManagement = riskManagement;
    }

}

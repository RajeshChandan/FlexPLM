
package com.burberry.wc.integration.productapi.bean;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "styleId",
    "styleName",
    "styleRef",
    "desc",
    "prodLastModified",
    "accessoriesGroup",
    "addCareWording",
    "ageGroup",
    "bleachInstruct",
    "bootCover",
    "prodBrand",
    "brandType",
    "chkBrand",
    "prodCites",
    "prodComCode",
    "dateClassified",
    "disclaim",
    "division",
    "downWt",
    "dryClean",
    "drying",
    "exotic",
    "fabType",
    "fit",
    "funcNeck",
    "gender",
    "glbTradeStus",
    "handMade",
    "heelHgt",
    "heightCM",
    "insLen",
    "ironing",
    "knitMthd",
    "kafFlag",
    "legPlmStyleId",
    "len",
    "lining",
    "mainRM",
    "mainRMCode",
    "mktDesc",
    "migrated",
    "narrowWaist",
    "neckType",
    "opCatg",
    "partImgUrl",
    "parVamp",
    "planGrp",
    "plugStrap",
    "pockBlWaist",
    "prodRiskLevel",
    "productType",
    "productionType",
    "boxHang",
    "shape",
    "sellSizeRange",
    "silhouette",
    "sizeGrp",
    "sktAboveKnee",
    "sleeve",
    "specSleeveType",
    "styleDetail",
    "subGrp",
    "subWorld",
    "prodTrim",
    "washInst",
    "waterProof",
    "wtGrp",
    "width",
    "world",
    "exFactDate",
    "appLength",
    "deptGrp",
    "grpDesc",
    "devGrp",
    "devStyleNote",
    "devBrand",
    "devCBLen",
    "devBlock",
    "bulkTestStatus",
    "bulkTestComment",
    "approvalRestrict",
    "type",
    "prodDeveloper",
    "prodEngineer",
    "designRef",
    "hubIndicatRiskLevel",
    "hubIndicatRiskReason",
    "hubIndicatRiskComment",
    "last",
    "liningComposition",
    "upperComposition",
    "measureSetRequired",
    "handleDrop",
    "strapDrop",
    "devSeason",
    "material",
    "colourways",
    "productSeason",
    "source",
    "commodityCode",
    "riskManagement",
    "documents",
    "required"
})
public class Style implements Serializable
{

    /**
     * Style Id
     * (Required)
     * 
     */
    @JsonProperty("styleId")
    @JsonPropertyDescription("Style Id")
    private Integer styleId;
    /**
     * Style Name
     * 
     */
    @JsonProperty("styleName")
    @JsonPropertyDescription("Style Name")
    private String styleName;
    /**
     * Style Ref
     * 
     */
    @JsonProperty("styleRef")
    @JsonPropertyDescription("Style Ref")
    private String styleRef;
    /**
     * Detailed Description
     * 
     */
    @JsonProperty("desc")
    @JsonPropertyDescription("Detailed Description")
    private String desc;
    /**
     * Product Last Mofied Timestamp
     * 
     */
    @JsonProperty("prodLastModified")
    @JsonPropertyDescription("Product Last Mofied Timestamp")
    private String prodLastModified;
    /**
     * Product Accessories Group
     * 
     */
    @JsonProperty("accessoriesGroup")
    @JsonPropertyDescription("Product Accessories Group")
    private String accessoriesGroup;
    /**
     * Additional Care Wording
     * 
     */
    @JsonProperty("addCareWording")
    @JsonPropertyDescription("Additional Care Wording")
    private String addCareWording;
    /**
     * Age Group
     * 
     */
    @JsonProperty("ageGroup")
    @JsonPropertyDescription("Age Group")
    private String ageGroup;
    /**
     * Bleach Instruction
     * 
     */
    @JsonProperty("bleachInstruct")
    @JsonPropertyDescription("Bleach Instruction")
    private String bleachInstruct;
    /**
     * Boot Coverage
     * 
     */
    @JsonProperty("bootCover")
    @JsonPropertyDescription("Boot Coverage")
    private String bootCover;
    /**
     * Brand at Product
     * 
     */
    @JsonProperty("prodBrand")
    @JsonPropertyDescription("Brand at Product")
    private String prodBrand;
    /**
     * Branding Type
     * 
     */
    @JsonProperty("brandType")
    @JsonPropertyDescription("Branding Type")
    private String brandType;
    /**
     * Check Branding
     * 
     */
    @JsonProperty("chkBrand")
    @JsonPropertyDescription("Check Branding")
    private String chkBrand;
    /**
     * Product CITES
     * 
     */
    @JsonProperty("prodCites")
    @JsonPropertyDescription("Product CITES")
    private String prodCites;
    /**
     * Product Commodity Code
     * 
     */
    @JsonProperty("prodComCode")
    @JsonPropertyDescription("Product Commodity Code")
    private String prodComCode;
    /**
     * Date Classified- format:date-time
     * 
     */
    @JsonProperty("dateClassified")
    @JsonPropertyDescription("Date Classified- format:date-time")
    private String dateClassified;
    /**
     * Disclaimer
     * 
     */
    @JsonProperty("disclaim")
    @JsonPropertyDescription("Disclaimer")
    private String disclaim;
    /**
     * Division
     * 
     */
    @JsonProperty("division")
    @JsonPropertyDescription("Division")
    private String division;
    /**
     * Down Weights Weight
     * 
     */
    @JsonProperty("downWt")
    @JsonPropertyDescription("Down Weights Weight")
    private Double downWt;
    /**
     * Dry Cleaning
     * 
     */
    @JsonProperty("dryClean")
    @JsonPropertyDescription("Dry Cleaning")
    private String dryClean;
    /**
     * Drying
     * 
     */
    @JsonProperty("drying")
    @JsonPropertyDescription("Drying")
    private String drying;
    /**
     * Exotic
     * 
     */
    @JsonProperty("exotic")
    @JsonPropertyDescription("Exotic")
    private String exotic;
    /**
     * Fabric Type
     * 
     */
    @JsonProperty("fabType")
    @JsonPropertyDescription("Fabric Type")
    private String fabType;
    /**
     * Fit
     * 
     */
    @JsonProperty("fit")
    @JsonPropertyDescription("Fit")
    private String fit;
    /**
     * Functional Neckline
     * 
     */
    @JsonProperty("funcNeck")
    @JsonPropertyDescription("Functional Neckline")
    private String funcNeck;
    /**
     * Gender
     * 
     */
    @JsonProperty("gender")
    @JsonPropertyDescription("Gender")
    private String gender;
    /**
     * Global Trade Status 
     * 
     */
    @JsonProperty("glbTradeStus")
    @JsonPropertyDescription("Global Trade Status ")
    private String glbTradeStus;
    /**
     * Handmade 100%
     * 
     */
    @JsonProperty("handMade")
    @JsonPropertyDescription("Handmade 100%")
    private String handMade;
    /**
     * Heel Height
     * 
     */
    @JsonProperty("heelHgt")
    @JsonPropertyDescription("Heel Height")
    private String heelHgt;
    /**
     * Height CM
     * 
     */
    @JsonProperty("heightCM")
    @JsonPropertyDescription("Height CM")
    private Double heightCM;
    /**
     * Insole Length above 24cm
     * 
     */
    @JsonProperty("insLen")
    @JsonPropertyDescription("Insole Length above 24cm")
    private Boolean insLen;
    /**
     * Ironing
     * 
     */
    @JsonProperty("ironing")
    @JsonPropertyDescription("Ironing")
    private String ironing;
    /**
     * Knit Method
     * 
     */
    @JsonProperty("knitMthd")
    @JsonPropertyDescription("Knit Method")
    private String knitMthd;
    /**
     * Kaff Flag
     * 
     */
    @JsonProperty("kafFlag")
    @JsonPropertyDescription("Kaff Flag")
    private Boolean kafFlag;
    /**
     * Legacy PLM Style ID
     * 
     */
    @JsonProperty("legPlmStyleId")
    @JsonPropertyDescription("Legacy PLM Style ID")
    private String legPlmStyleId;
    /**
     * Length CM
     * 
     */
    @JsonProperty("len")
    @JsonPropertyDescription("Length CM")
    private Double len;
    /**
     * Lining Type
     * 
     */
    @JsonProperty("lining")
    @JsonPropertyDescription("Lining Type")
    private String lining;
    /**
     * Main RM
     * 
     */
    @JsonProperty("mainRM")
    @JsonPropertyDescription("Main RM")
    private String mainRM;
    /**
     * Main RM Code
     * 
     */
    @JsonProperty("mainRMCode")
    @JsonPropertyDescription("Main RM Code")
    private String mainRMCode;
    /**
     * Marketing Description
     * 
     */
    @JsonProperty("mktDesc")
    @JsonPropertyDescription("Marketing Description")
    private String mktDesc;
    /**
     * Migrated Product?
     * 
     */
    @JsonProperty("migrated")
    @JsonPropertyDescription("Migrated Product?")
    private Boolean migrated;
    /**
     * Narrowing Waist?
     * 
     */
    @JsonProperty("narrowWaist")
    @JsonPropertyDescription("Narrowing Waist?")
    private Boolean narrowWaist;
    /**
     * Neck Type
     * 
     */
    @JsonProperty("neckType")
    @JsonPropertyDescription("Neck Type")
    private String neckType;
    /**
     * Operational Category
     * 
     */
    @JsonProperty("opCatg")
    @JsonPropertyDescription("Operational Category")
    private String opCatg;
    /**
     * Part Primary Image URL
     * 
     */
    @JsonProperty("partImgUrl")
    @JsonPropertyDescription("Part Primary Image URL")
    private String partImgUrl;
    /**
     * Partial Vamp
     * 
     */
    @JsonProperty("parVamp")
    @JsonPropertyDescription("Partial Vamp")
    private Boolean parVamp;
    /**
     * Plan Group
     * 
     */
    @JsonProperty("planGrp")
    @JsonPropertyDescription("Plan Group")
    private String planGrp;
    /**
     * Plugged Straps
     * 
     */
    @JsonProperty("plugStrap")
    @JsonPropertyDescription("Plugged Straps")
    private Boolean plugStrap;
    /**
     * Pocket Below Waist
     * 
     */
    @JsonProperty("pockBlWaist")
    @JsonPropertyDescription("Pocket Below Waist")
    private Boolean pockBlWaist;
    /**
     * Product Risk Level
     * 
     */
    @JsonProperty("prodRiskLevel")
    @JsonPropertyDescription("Product Risk Level")
    private String prodRiskLevel;
    /**
     * Product Type
     * 
     */
    @JsonProperty("productType")
    @JsonPropertyDescription("Product Type")
    private String productType;
    /**
     * Production Type
     * 
     */
    @JsonProperty("productionType")
    @JsonPropertyDescription("Production Type")
    private String productionType;
    /**
     * Ship Boxed/Hanging
     * 
     */
    @JsonProperty("boxHang")
    @JsonPropertyDescription("Ship Boxed/Hanging")
    private String boxHang;
    /**
     * Shape
     * 
     */
    @JsonProperty("shape")
    @JsonPropertyDescription("Shape")
    private String shape;
    /**
     * Selling Size Range
     * 
     */
    @JsonProperty("sellSizeRange")
    @JsonPropertyDescription("Selling Size Range")
    private String sellSizeRange;
    /**
     * Silhouette
     * 
     */
    @JsonProperty("silhouette")
    @JsonPropertyDescription("Silhouette")
    private String silhouette;
    /**
     * Size Grouping
     * 
     */
    @JsonProperty("sizeGrp")
    @JsonPropertyDescription("Size Grouping")
    private String sizeGrp;
    /**
     * Skirt/Dress Above Knee
     * 
     */
    @JsonProperty("sktAboveKnee")
    @JsonPropertyDescription("Skirt/Dress Above Knee")
    private Boolean sktAboveKnee;
    /**
     * Sleeve
     * 
     */
    @JsonProperty("sleeve")
    @JsonPropertyDescription("Sleeve")
    private String sleeve;
    /**
     * Specification Sleeve Type
     * 
     */
    @JsonProperty("specSleeveType")
    @JsonPropertyDescription("Specification Sleeve Type")
    private String specSleeveType;
    /**
     * Style Detailing
     * 
     */
    @JsonProperty("styleDetail")
    @JsonPropertyDescription("Style Detailing")
    private String styleDetail;
    /**
     * Sub Group
     * 
     */
    @JsonProperty("subGrp")
    @JsonPropertyDescription("Sub Group")
    private String subGrp;
    /**
     * Sub World
     * 
     */
    @JsonProperty("subWorld")
    @JsonPropertyDescription("Sub World")
    private String subWorld;
    /**
     * Trim
     * 
     */
    @JsonProperty("prodTrim")
    @JsonPropertyDescription("Trim")
    private String prodTrim;
    /**
     * Wash Instructions
     * 
     */
    @JsonProperty("washInst")
    @JsonPropertyDescription("Wash Instructions")
    private String washInst;
    /**
     * Water Proof
     * 
     */
    @JsonProperty("waterProof")
    @JsonPropertyDescription("Water Proof")
    private Boolean waterProof;
    /**
     * Weight Group
     * 
     */
    @JsonProperty("wtGrp")
    @JsonPropertyDescription("Weight Group")
    private String wtGrp;
    /**
     * Width CM
     * 
     */
    @JsonProperty("width")
    @JsonPropertyDescription("Width CM")
    private Double width;
    /**
     * World
     * 
     */
    @JsonProperty("world")
    @JsonPropertyDescription("World")
    private String world;
    /**
     * Ex - Factory Date- format:date-time
     * 
     */
    @JsonProperty("exFactDate")
    @JsonPropertyDescription("Ex - Factory Date- format:date-time")
    private String exFactDate;
    /**
     * Length (Apparel)
     * 
     */
    @JsonProperty("appLength")
    @JsonPropertyDescription("Length (Apparel)")
    private String appLength;
    /**
     * Dept/Group
     * 
     */
    @JsonProperty("deptGrp")
    @JsonPropertyDescription("Dept/Group")
    private String deptGrp;
    /**
     * Group Description
     * 
     */
    @JsonProperty("grpDesc")
    @JsonPropertyDescription("Group Description")
    private String grpDesc;
    /**
     * Development Grouping
     * 
     */
    @JsonProperty("devGrp")
    @JsonPropertyDescription("Development Grouping")
    private String devGrp;
    /**
     * Development Style Notes
     * 
     */
    @JsonProperty("devStyleNote")
    @JsonPropertyDescription("Development Style Notes")
    private String devStyleNote;
    /**
     * Development Branding
     * 
     */
    @JsonProperty("devBrand")
    @JsonPropertyDescription("Development Branding")
    private String devBrand;
    /**
     * Development CB Length
     * 
     */
    @JsonProperty("devCBLen")
    @JsonPropertyDescription("Development CB Length")
    private String devCBLen;
    /**
     * Development Block
     * 
     */
    @JsonProperty("devBlock")
    @JsonPropertyDescription("Development Block")
    private String devBlock;
    /**
     * Bulk Testing Status
     * 
     */
    @JsonProperty("bulkTestStatus")
    @JsonPropertyDescription("Bulk Testing Status")
    private String bulkTestStatus;
    /**
     * Bulk Testing Comments
     * 
     */
    @JsonProperty("bulkTestComment")
    @JsonPropertyDescription("Bulk Testing Comments")
    private String bulkTestComment;
    /**
     * Approval Restriction
     * 
     */
    @JsonProperty("approvalRestrict")
    @JsonPropertyDescription("Approval Restriction")
    private String approvalRestrict;
    /**
     * Type
     * 
     */
    @JsonProperty("type")
    @JsonPropertyDescription("Type")
    private String type;
    /**
     * Product Developer
     * 
     */
    @JsonProperty("prodDeveloper")
    @JsonPropertyDescription("Product Developer")
    private String prodDeveloper;
    /**
     * Product Engineer
     * 
     */
    @JsonProperty("prodEngineer")
    @JsonPropertyDescription("Product Engineer")
    private String prodEngineer;
    /**
     * Design Ref
     * 
     */
    @JsonProperty("designRef")
    @JsonPropertyDescription("Design Ref")
    private String designRef;
    /**
     * Hub Indication Risk Level
     * 
     */
    @JsonProperty("hubIndicatRiskLevel")
    @JsonPropertyDescription("Hub Indication Risk Level")
    private String hubIndicatRiskLevel;
    /**
     * Hub Indication Risk Reason
     * 
     */
    @JsonProperty("hubIndicatRiskReason")
    @JsonPropertyDescription("Hub Indication Risk Reason")
    private String hubIndicatRiskReason;
    /**
     * Hub Indication Risk Comments
     * 
     */
    @JsonProperty("hubIndicatRiskComment")
    @JsonPropertyDescription("Hub Indication Risk Comments")
    private String hubIndicatRiskComment;
    /**
     * Last
     * 
     */
    @JsonProperty("last")
    @JsonPropertyDescription("Last")
    private String last;
    /**
     * Lining Composition
     * 
     */
    @JsonProperty("liningComposition")
    @JsonPropertyDescription("Lining Composition")
    private String liningComposition;
    /**
     * Upper Composition
     * 
     */
    @JsonProperty("upperComposition")
    @JsonPropertyDescription("Upper Composition")
    private String upperComposition;
    /**
     * Measurement Set Required
     * 
     */
    @JsonProperty("measureSetRequired")
    @JsonPropertyDescription("Measurement Set Required")
    private Boolean measureSetRequired;
    /**
     * Handle Drop (cm)
     * 
     */
    @JsonProperty("handleDrop")
    @JsonPropertyDescription("Handle Drop (cm)")
    private Double handleDrop;
    /**
     * Strap Drop (cm)
     * 
     */
    @JsonProperty("strapDrop")
    @JsonPropertyDescription("Strap Drop (cm)")
    private Double strapDrop;
    /**
     * Development Season
     * 
     */
    @JsonProperty("devSeason")
    @JsonPropertyDescription("Development Season")
    private String devSeason;
    @JsonProperty("material")
    private Material material;
    @JsonProperty("colourways")
    private List<Colourway> colourways = null;
    @JsonProperty("productSeason")
    private List<ProductSeason> productSeason = null;
    @JsonProperty("source")
    private List<Source> source = null;
    @JsonProperty("commodityCode")
    private CommodityCode commodityCode;
    @JsonProperty("riskManagement")
    private List<RiskManagement> riskManagement = null;
    @JsonProperty("documents")
    private List<Document> documents = null;
    @JsonProperty("required")
    private Object required;
    private final static long serialVersionUID = -2852051754647577641L;

    /**
     * Style Id
     * (Required)
     * 
     */
    @JsonProperty("styleId")
    public Integer getStyleId() {
        return styleId;
    }

    /**
     * Style Id
     * (Required)
     * 
     */
    @JsonProperty("styleId")
    public void setStyleId(Integer styleId) {
        this.styleId = styleId;
    }

    /**
     * Style Name
     * 
     */
    @JsonProperty("styleName")
    public String getStyleName() {
        return styleName;
    }

    /**
     * Style Name
     * 
     */
    @JsonProperty("styleName")
    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    /**
     * Style Ref
     * 
     */
    @JsonProperty("styleRef")
    public String getStyleRef() {
        return styleRef;
    }

    /**
     * Style Ref
     * 
     */
    @JsonProperty("styleRef")
    public void setStyleRef(String styleRef) {
        this.styleRef = styleRef;
    }

    /**
     * Detailed Description
     * 
     */
    @JsonProperty("desc")
    public String getDesc() {
        return desc;
    }

    /**
     * Detailed Description
     * 
     */
    @JsonProperty("desc")
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Product Last Mofied Timestamp
     * 
     */
    @JsonProperty("prodLastModified")
    public String getProdLastModified() {
        return prodLastModified;
    }

    /**
     * Product Last Mofied Timestamp
     * 
     */
    @JsonProperty("prodLastModified")
    public void setProdLastModified(String prodLastModified) {
        this.prodLastModified = prodLastModified;
    }

    /**
     * Product Accessories Group
     * 
     */
    @JsonProperty("accessoriesGroup")
    public String getAccessoriesGroup() {
        return accessoriesGroup;
    }

    /**
     * Product Accessories Group
     * 
     */
    @JsonProperty("accessoriesGroup")
    public void setAccessoriesGroup(String accessoriesGroup) {
        this.accessoriesGroup = accessoriesGroup;
    }

    /**
     * Additional Care Wording
     * 
     */
    @JsonProperty("addCareWording")
    public String getAddCareWording() {
        return addCareWording;
    }

    /**
     * Additional Care Wording
     * 
     */
    @JsonProperty("addCareWording")
    public void setAddCareWording(String addCareWording) {
        this.addCareWording = addCareWording;
    }

    /**
     * Age Group
     * 
     */
    @JsonProperty("ageGroup")
    public String getAgeGroup() {
        return ageGroup;
    }

    /**
     * Age Group
     * 
     */
    @JsonProperty("ageGroup")
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    /**
     * Bleach Instruction
     * 
     */
    @JsonProperty("bleachInstruct")
    public String getBleachInstruct() {
        return bleachInstruct;
    }

    /**
     * Bleach Instruction
     * 
     */
    @JsonProperty("bleachInstruct")
    public void setBleachInstruct(String bleachInstruct) {
        this.bleachInstruct = bleachInstruct;
    }

    /**
     * Boot Coverage
     * 
     */
    @JsonProperty("bootCover")
    public String getBootCover() {
        return bootCover;
    }

    /**
     * Boot Coverage
     * 
     */
    @JsonProperty("bootCover")
    public void setBootCover(String bootCover) {
        this.bootCover = bootCover;
    }

    /**
     * Brand at Product
     * 
     */
    @JsonProperty("prodBrand")
    public String getProdBrand() {
        return prodBrand;
    }

    /**
     * Brand at Product
     * 
     */
    @JsonProperty("prodBrand")
    public void setProdBrand(String prodBrand) {
        this.prodBrand = prodBrand;
    }

    /**
     * Branding Type
     * 
     */
    @JsonProperty("brandType")
    public String getBrandType() {
        return brandType;
    }

    /**
     * Branding Type
     * 
     */
    @JsonProperty("brandType")
    public void setBrandType(String brandType) {
        this.brandType = brandType;
    }

    /**
     * Check Branding
     * 
     */
    @JsonProperty("chkBrand")
    public String getChkBrand() {
        return chkBrand;
    }

    /**
     * Check Branding
     * 
     */
    @JsonProperty("chkBrand")
    public void setChkBrand(String chkBrand) {
        this.chkBrand = chkBrand;
    }

    /**
     * Product CITES
     * 
     */
    @JsonProperty("prodCites")
    public String getProdCites() {
        return prodCites;
    }

    /**
     * Product CITES
     * 
     */
    @JsonProperty("prodCites")
    public void setProdCites(String prodCites) {
        this.prodCites = prodCites;
    }

    /**
     * Product Commodity Code
     * 
     */
    @JsonProperty("prodComCode")
    public String getProdComCode() {
        return prodComCode;
    }

    /**
     * Product Commodity Code
     * 
     */
    @JsonProperty("prodComCode")
    public void setProdComCode(String prodComCode) {
        this.prodComCode = prodComCode;
    }

    /**
     * Date Classified- format:date-time
     * 
     */
    @JsonProperty("dateClassified")
    public String getDateClassified() {
        return dateClassified;
    }

    /**
     * Date Classified- format:date-time
     * 
     */
    @JsonProperty("dateClassified")
    public void setDateClassified(String dateClassified) {
        this.dateClassified = dateClassified;
    }

    /**
     * Disclaimer
     * 
     */
    @JsonProperty("disclaim")
    public String getDisclaim() {
        return disclaim;
    }

    /**
     * Disclaimer
     * 
     */
    @JsonProperty("disclaim")
    public void setDisclaim(String disclaim) {
        this.disclaim = disclaim;
    }

    /**
     * Division
     * 
     */
    @JsonProperty("division")
    public String getDivision() {
        return division;
    }

    /**
     * Division
     * 
     */
    @JsonProperty("division")
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * Down Weights Weight
     * 
     */
    @JsonProperty("downWt")
    public Double getDownWt() {
        return downWt;
    }

    /**
     * Down Weights Weight
     * 
     */
    @JsonProperty("downWt")
    public void setDownWt(Double downWt) {
        this.downWt = downWt;
    }

    /**
     * Dry Cleaning
     * 
     */
    @JsonProperty("dryClean")
    public String getDryClean() {
        return dryClean;
    }

    /**
     * Dry Cleaning
     * 
     */
    @JsonProperty("dryClean")
    public void setDryClean(String dryClean) {
        this.dryClean = dryClean;
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
     * Exotic
     * 
     */
    @JsonProperty("exotic")
    public String getExotic() {
        return exotic;
    }

    /**
     * Exotic
     * 
     */
    @JsonProperty("exotic")
    public void setExotic(String exotic) {
        this.exotic = exotic;
    }

    /**
     * Fabric Type
     * 
     */
    @JsonProperty("fabType")
    public String getFabType() {
        return fabType;
    }

    /**
     * Fabric Type
     * 
     */
    @JsonProperty("fabType")
    public void setFabType(String fabType) {
        this.fabType = fabType;
    }

    /**
     * Fit
     * 
     */
    @JsonProperty("fit")
    public String getFit() {
        return fit;
    }

    /**
     * Fit
     * 
     */
    @JsonProperty("fit")
    public void setFit(String fit) {
        this.fit = fit;
    }

    /**
     * Functional Neckline
     * 
     */
    @JsonProperty("funcNeck")
    public String getFuncNeck() {
        return funcNeck;
    }

    /**
     * Functional Neckline
     * 
     */
    @JsonProperty("funcNeck")
    public void setFuncNeck(String funcNeck) {
        this.funcNeck = funcNeck;
    }

    /**
     * Gender
     * 
     */
    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    /**
     * Gender
     * 
     */
    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Global Trade Status 
     * 
     */
    @JsonProperty("glbTradeStus")
    public String getGlbTradeStus() {
        return glbTradeStus;
    }

    /**
     * Global Trade Status 
     * 
     */
    @JsonProperty("glbTradeStus")
    public void setGlbTradeStus(String glbTradeStus) {
        this.glbTradeStus = glbTradeStus;
    }

    /**
     * Handmade 100%
     * 
     */
    @JsonProperty("handMade")
    public String getHandMade() {
        return handMade;
    }

    /**
     * Handmade 100%
     * 
     */
    @JsonProperty("handMade")
    public void setHandMade(String handMade) {
        this.handMade = handMade;
    }

    /**
     * Heel Height
     * 
     */
    @JsonProperty("heelHgt")
    public String getHeelHgt() {
        return heelHgt;
    }

    /**
     * Heel Height
     * 
     */
    @JsonProperty("heelHgt")
    public void setHeelHgt(String heelHgt) {
        this.heelHgt = heelHgt;
    }

    /**
     * Height CM
     * 
     */
    @JsonProperty("heightCM")
    public Double getHeightCM() {
        return heightCM;
    }

    /**
     * Height CM
     * 
     */
    @JsonProperty("heightCM")
    public void setHeightCM(Double heightCM) {
        this.heightCM = heightCM;
    }

    /**
     * Insole Length above 24cm
     * 
     */
    @JsonProperty("insLen")
    public Boolean getInsLen() {
        return insLen;
    }

    /**
     * Insole Length above 24cm
     * 
     */
    @JsonProperty("insLen")
    public void setInsLen(Boolean insLen) {
        this.insLen = insLen;
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
     * Knit Method
     * 
     */
    @JsonProperty("knitMthd")
    public String getKnitMthd() {
        return knitMthd;
    }

    /**
     * Knit Method
     * 
     */
    @JsonProperty("knitMthd")
    public void setKnitMthd(String knitMthd) {
        this.knitMthd = knitMthd;
    }

    /**
     * Kaff Flag
     * 
     */
    @JsonProperty("kafFlag")
    public Boolean getKafFlag() {
        return kafFlag;
    }

    /**
     * Kaff Flag
     * 
     */
    @JsonProperty("kafFlag")
    public void setKafFlag(Boolean kafFlag) {
        this.kafFlag = kafFlag;
    }

    /**
     * Legacy PLM Style ID
     * 
     */
    @JsonProperty("legPlmStyleId")
    public String getLegPlmStyleId() {
        return legPlmStyleId;
    }

    /**
     * Legacy PLM Style ID
     * 
     */
    @JsonProperty("legPlmStyleId")
    public void setLegPlmStyleId(String legPlmStyleId) {
        this.legPlmStyleId = legPlmStyleId;
    }

    /**
     * Length CM
     * 
     */
    @JsonProperty("len")
    public Double getLen() {
        return len;
    }

    /**
     * Length CM
     * 
     */
    @JsonProperty("len")
    public void setLen(Double len) {
        this.len = len;
    }

    /**
     * Lining Type
     * 
     */
    @JsonProperty("lining")
    public String getLining() {
        return lining;
    }

    /**
     * Lining Type
     * 
     */
    @JsonProperty("lining")
    public void setLining(String lining) {
        this.lining = lining;
    }

    /**
     * Main RM
     * 
     */
    @JsonProperty("mainRM")
    public String getMainRM() {
        return mainRM;
    }

    /**
     * Main RM
     * 
     */
    @JsonProperty("mainRM")
    public void setMainRM(String mainRM) {
        this.mainRM = mainRM;
    }

    /**
     * Main RM Code
     * 
     */
    @JsonProperty("mainRMCode")
    public String getMainRMCode() {
        return mainRMCode;
    }

    /**
     * Main RM Code
     * 
     */
    @JsonProperty("mainRMCode")
    public void setMainRMCode(String mainRMCode) {
        this.mainRMCode = mainRMCode;
    }

    /**
     * Marketing Description
     * 
     */
    @JsonProperty("mktDesc")
    public String getMktDesc() {
        return mktDesc;
    }

    /**
     * Marketing Description
     * 
     */
    @JsonProperty("mktDesc")
    public void setMktDesc(String mktDesc) {
        this.mktDesc = mktDesc;
    }

    /**
     * Migrated Product?
     * 
     */
    @JsonProperty("migrated")
    public Boolean getMigrated() {
        return migrated;
    }

    /**
     * Migrated Product?
     * 
     */
    @JsonProperty("migrated")
    public void setMigrated(Boolean migrated) {
        this.migrated = migrated;
    }

    /**
     * Narrowing Waist?
     * 
     */
    @JsonProperty("narrowWaist")
    public Boolean getNarrowWaist() {
        return narrowWaist;
    }

    /**
     * Narrowing Waist?
     * 
     */
    @JsonProperty("narrowWaist")
    public void setNarrowWaist(Boolean narrowWaist) {
        this.narrowWaist = narrowWaist;
    }

    /**
     * Neck Type
     * 
     */
    @JsonProperty("neckType")
    public String getNeckType() {
        return neckType;
    }

    /**
     * Neck Type
     * 
     */
    @JsonProperty("neckType")
    public void setNeckType(String neckType) {
        this.neckType = neckType;
    }

    /**
     * Operational Category
     * 
     */
    @JsonProperty("opCatg")
    public String getOpCatg() {
        return opCatg;
    }

    /**
     * Operational Category
     * 
     */
    @JsonProperty("opCatg")
    public void setOpCatg(String opCatg) {
        this.opCatg = opCatg;
    }

    /**
     * Part Primary Image URL
     * 
     */
    @JsonProperty("partImgUrl")
    public String getPartImgUrl() {
        return partImgUrl;
    }

    /**
     * Part Primary Image URL
     * 
     */
    @JsonProperty("partImgUrl")
    public void setPartImgUrl(String partImgUrl) {
        this.partImgUrl = partImgUrl;
    }

    /**
     * Partial Vamp
     * 
     */
    @JsonProperty("parVamp")
    public Boolean getParVamp() {
        return parVamp;
    }

    /**
     * Partial Vamp
     * 
     */
    @JsonProperty("parVamp")
    public void setParVamp(Boolean parVamp) {
        this.parVamp = parVamp;
    }

    /**
     * Plan Group
     * 
     */
    @JsonProperty("planGrp")
    public String getPlanGrp() {
        return planGrp;
    }

    /**
     * Plan Group
     * 
     */
    @JsonProperty("planGrp")
    public void setPlanGrp(String planGrp) {
        this.planGrp = planGrp;
    }

    /**
     * Plugged Straps
     * 
     */
    @JsonProperty("plugStrap")
    public Boolean getPlugStrap() {
        return plugStrap;
    }

    /**
     * Plugged Straps
     * 
     */
    @JsonProperty("plugStrap")
    public void setPlugStrap(Boolean plugStrap) {
        this.plugStrap = plugStrap;
    }

    /**
     * Pocket Below Waist
     * 
     */
    @JsonProperty("pockBlWaist")
    public Boolean getPockBlWaist() {
        return pockBlWaist;
    }

    /**
     * Pocket Below Waist
     * 
     */
    @JsonProperty("pockBlWaist")
    public void setPockBlWaist(Boolean pockBlWaist) {
        this.pockBlWaist = pockBlWaist;
    }

    /**
     * Product Risk Level
     * 
     */
    @JsonProperty("prodRiskLevel")
    public String getProdRiskLevel() {
        return prodRiskLevel;
    }

    /**
     * Product Risk Level
     * 
     */
    @JsonProperty("prodRiskLevel")
    public void setProdRiskLevel(String prodRiskLevel) {
        this.prodRiskLevel = prodRiskLevel;
    }

    /**
     * Product Type
     * 
     */
    @JsonProperty("productType")
    public String getProductType() {
        return productType;
    }

    /**
     * Product Type
     * 
     */
    @JsonProperty("productType")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Production Type
     * 
     */
    @JsonProperty("productionType")
    public String getProductionType() {
        return productionType;
    }

    /**
     * Production Type
     * 
     */
    @JsonProperty("productionType")
    public void setProductionType(String productionType) {
        this.productionType = productionType;
    }

    /**
     * Ship Boxed/Hanging
     * 
     */
    @JsonProperty("boxHang")
    public String getBoxHang() {
        return boxHang;
    }

    /**
     * Ship Boxed/Hanging
     * 
     */
    @JsonProperty("boxHang")
    public void setBoxHang(String boxHang) {
        this.boxHang = boxHang;
    }

    /**
     * Shape
     * 
     */
    @JsonProperty("shape")
    public String getShape() {
        return shape;
    }

    /**
     * Shape
     * 
     */
    @JsonProperty("shape")
    public void setShape(String shape) {
        this.shape = shape;
    }

    /**
     * Selling Size Range
     * 
     */
    @JsonProperty("sellSizeRange")
    public String getSellSizeRange() {
        return sellSizeRange;
    }

    /**
     * Selling Size Range
     * 
     */
    @JsonProperty("sellSizeRange")
    public void setSellSizeRange(String sellSizeRange) {
        this.sellSizeRange = sellSizeRange;
    }

    /**
     * Silhouette
     * 
     */
    @JsonProperty("silhouette")
    public String getSilhouette() {
        return silhouette;
    }

    /**
     * Silhouette
     * 
     */
    @JsonProperty("silhouette")
    public void setSilhouette(String silhouette) {
        this.silhouette = silhouette;
    }

    /**
     * Size Grouping
     * 
     */
    @JsonProperty("sizeGrp")
    public String getSizeGrp() {
        return sizeGrp;
    }

    /**
     * Size Grouping
     * 
     */
    @JsonProperty("sizeGrp")
    public void setSizeGrp(String sizeGrp) {
        this.sizeGrp = sizeGrp;
    }

    /**
     * Skirt/Dress Above Knee
     * 
     */
    @JsonProperty("sktAboveKnee")
    public Boolean getSktAboveKnee() {
        return sktAboveKnee;
    }

    /**
     * Skirt/Dress Above Knee
     * 
     */
    @JsonProperty("sktAboveKnee")
    public void setSktAboveKnee(Boolean sktAboveKnee) {
        this.sktAboveKnee = sktAboveKnee;
    }

    /**
     * Sleeve
     * 
     */
    @JsonProperty("sleeve")
    public String getSleeve() {
        return sleeve;
    }

    /**
     * Sleeve
     * 
     */
    @JsonProperty("sleeve")
    public void setSleeve(String sleeve) {
        this.sleeve = sleeve;
    }

    /**
     * Specification Sleeve Type
     * 
     */
    @JsonProperty("specSleeveType")
    public String getSpecSleeveType() {
        return specSleeveType;
    }

    /**
     * Specification Sleeve Type
     * 
     */
    @JsonProperty("specSleeveType")
    public void setSpecSleeveType(String specSleeveType) {
        this.specSleeveType = specSleeveType;
    }

    /**
     * Style Detailing
     * 
     */
    @JsonProperty("styleDetail")
    public String getStyleDetail() {
        return styleDetail;
    }

    /**
     * Style Detailing
     * 
     */
    @JsonProperty("styleDetail")
    public void setStyleDetail(String styleDetail) {
        this.styleDetail = styleDetail;
    }

    /**
     * Sub Group
     * 
     */
    @JsonProperty("subGrp")
    public String getSubGrp() {
        return subGrp;
    }

    /**
     * Sub Group
     * 
     */
    @JsonProperty("subGrp")
    public void setSubGrp(String subGrp) {
        this.subGrp = subGrp;
    }

    /**
     * Sub World
     * 
     */
    @JsonProperty("subWorld")
    public String getSubWorld() {
        return subWorld;
    }

    /**
     * Sub World
     * 
     */
    @JsonProperty("subWorld")
    public void setSubWorld(String subWorld) {
        this.subWorld = subWorld;
    }

    /**
     * Trim
     * 
     */
    @JsonProperty("prodTrim")
    public String getProdTrim() {
        return prodTrim;
    }

    /**
     * Trim
     * 
     */
    @JsonProperty("prodTrim")
    public void setProdTrim(String prodTrim) {
        this.prodTrim = prodTrim;
    }

    /**
     * Wash Instructions
     * 
     */
    @JsonProperty("washInst")
    public String getWashInst() {
        return washInst;
    }

    /**
     * Wash Instructions
     * 
     */
    @JsonProperty("washInst")
    public void setWashInst(String washInst) {
        this.washInst = washInst;
    }

    /**
     * Water Proof
     * 
     */
    @JsonProperty("waterProof")
    public Boolean getWaterProof() {
        return waterProof;
    }

    /**
     * Water Proof
     * 
     */
    @JsonProperty("waterProof")
    public void setWaterProof(Boolean waterProof) {
        this.waterProof = waterProof;
    }

    /**
     * Weight Group
     * 
     */
    @JsonProperty("wtGrp")
    public String getWtGrp() {
        return wtGrp;
    }

    /**
     * Weight Group
     * 
     */
    @JsonProperty("wtGrp")
    public void setWtGrp(String wtGrp) {
        this.wtGrp = wtGrp;
    }

    /**
     * Width CM
     * 
     */
    @JsonProperty("width")
    public Double getWidth() {
        return width;
    }

    /**
     * Width CM
     * 
     */
    @JsonProperty("width")
    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * World
     * 
     */
    @JsonProperty("world")
    public String getWorld() {
        return world;
    }

    /**
     * World
     * 
     */
    @JsonProperty("world")
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * Ex - Factory Date- format:date-time
     * 
     */
    @JsonProperty("exFactDate")
    public String getExFactDate() {
        return exFactDate;
    }

    /**
     * Ex - Factory Date- format:date-time
     * 
     */
    @JsonProperty("exFactDate")
    public void setExFactDate(String exFactDate) {
        this.exFactDate = exFactDate;
    }

    /**
     * Length (Apparel)
     * 
     */
    @JsonProperty("appLength")
    public String getAppLength() {
        return appLength;
    }

    /**
     * Length (Apparel)
     * 
     */
    @JsonProperty("appLength")
    public void setAppLength(String appLength) {
        this.appLength = appLength;
    }

    /**
     * Dept/Group
     * 
     */
    @JsonProperty("deptGrp")
    public String getDeptGrp() {
        return deptGrp;
    }

    /**
     * Dept/Group
     * 
     */
    @JsonProperty("deptGrp")
    public void setDeptGrp(String deptGrp) {
        this.deptGrp = deptGrp;
    }

    /**
     * Group Description
     * 
     */
    @JsonProperty("grpDesc")
    public String getGrpDesc() {
        return grpDesc;
    }

    /**
     * Group Description
     * 
     */
    @JsonProperty("grpDesc")
    public void setGrpDesc(String grpDesc) {
        this.grpDesc = grpDesc;
    }

    /**
     * Development Grouping
     * 
     */
    @JsonProperty("devGrp")
    public String getDevGrp() {
        return devGrp;
    }

    /**
     * Development Grouping
     * 
     */
    @JsonProperty("devGrp")
    public void setDevGrp(String devGrp) {
        this.devGrp = devGrp;
    }

    /**
     * Development Style Notes
     * 
     */
    @JsonProperty("devStyleNote")
    public String getDevStyleNote() {
        return devStyleNote;
    }

    /**
     * Development Style Notes
     * 
     */
    @JsonProperty("devStyleNote")
    public void setDevStyleNote(String devStyleNote) {
        this.devStyleNote = devStyleNote;
    }

    /**
     * Development Branding
     * 
     */
    @JsonProperty("devBrand")
    public String getDevBrand() {
        return devBrand;
    }

    /**
     * Development Branding
     * 
     */
    @JsonProperty("devBrand")
    public void setDevBrand(String devBrand) {
        this.devBrand = devBrand;
    }

    /**
     * Development CB Length
     * 
     */
    @JsonProperty("devCBLen")
    public String getDevCBLen() {
        return devCBLen;
    }

    /**
     * Development CB Length
     * 
     */
    @JsonProperty("devCBLen")
    public void setDevCBLen(String devCBLen) {
        this.devCBLen = devCBLen;
    }

    /**
     * Development Block
     * 
     */
    @JsonProperty("devBlock")
    public String getDevBlock() {
        return devBlock;
    }

    /**
     * Development Block
     * 
     */
    @JsonProperty("devBlock")
    public void setDevBlock(String devBlock) {
        this.devBlock = devBlock;
    }

    /**
     * Bulk Testing Status
     * 
     */
    @JsonProperty("bulkTestStatus")
    public String getBulkTestStatus() {
        return bulkTestStatus;
    }

    /**
     * Bulk Testing Status
     * 
     */
    @JsonProperty("bulkTestStatus")
    public void setBulkTestStatus(String bulkTestStatus) {
        this.bulkTestStatus = bulkTestStatus;
    }

    /**
     * Bulk Testing Comments
     * 
     */
    @JsonProperty("bulkTestComment")
    public String getBulkTestComment() {
        return bulkTestComment;
    }

    /**
     * Bulk Testing Comments
     * 
     */
    @JsonProperty("bulkTestComment")
    public void setBulkTestComment(String bulkTestComment) {
        this.bulkTestComment = bulkTestComment;
    }

    /**
     * Approval Restriction
     * 
     */
    @JsonProperty("approvalRestrict")
    public String getApprovalRestrict() {
        return approvalRestrict;
    }

    /**
     * Approval Restriction
     * 
     */
    @JsonProperty("approvalRestrict")
    public void setApprovalRestrict(String approvalRestrict) {
        this.approvalRestrict = approvalRestrict;
    }

    /**
     * Type
     * 
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * Type
     * 
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Product Developer
     * 
     */
    @JsonProperty("prodDeveloper")
    public String getProdDeveloper() {
        return prodDeveloper;
    }

    /**
     * Product Developer
     * 
     */
    @JsonProperty("prodDeveloper")
    public void setProdDeveloper(String prodDeveloper) {
        this.prodDeveloper = prodDeveloper;
    }

    /**
     * Product Engineer
     * 
     */
    @JsonProperty("prodEngineer")
    public String getProdEngineer() {
        return prodEngineer;
    }

    /**
     * Product Engineer
     * 
     */
    @JsonProperty("prodEngineer")
    public void setProdEngineer(String prodEngineer) {
        this.prodEngineer = prodEngineer;
    }

    /**
     * Design Ref
     * 
     */
    @JsonProperty("designRef")
    public String getDesignRef() {
        return designRef;
    }

    /**
     * Design Ref
     * 
     */
    @JsonProperty("designRef")
    public void setDesignRef(String designRef) {
        this.designRef = designRef;
    }

    /**
     * Hub Indication Risk Level
     * 
     */
    @JsonProperty("hubIndicatRiskLevel")
    public String getHubIndicatRiskLevel() {
        return hubIndicatRiskLevel;
    }

    /**
     * Hub Indication Risk Level
     * 
     */
    @JsonProperty("hubIndicatRiskLevel")
    public void setHubIndicatRiskLevel(String hubIndicatRiskLevel) {
        this.hubIndicatRiskLevel = hubIndicatRiskLevel;
    }

    /**
     * Hub Indication Risk Reason
     * 
     */
    @JsonProperty("hubIndicatRiskReason")
    public String getHubIndicatRiskReason() {
        return hubIndicatRiskReason;
    }

    /**
     * Hub Indication Risk Reason
     * 
     */
    @JsonProperty("hubIndicatRiskReason")
    public void setHubIndicatRiskReason(String hubIndicatRiskReason) {
        this.hubIndicatRiskReason = hubIndicatRiskReason;
    }

    /**
     * Hub Indication Risk Comments
     * 
     */
    @JsonProperty("hubIndicatRiskComment")
    public String getHubIndicatRiskComment() {
        return hubIndicatRiskComment;
    }

    /**
     * Hub Indication Risk Comments
     * 
     */
    @JsonProperty("hubIndicatRiskComment")
    public void setHubIndicatRiskComment(String hubIndicatRiskComment) {
        this.hubIndicatRiskComment = hubIndicatRiskComment;
    }

    /**
     * Last
     * 
     */
    @JsonProperty("last")
    public String getLast() {
        return last;
    }

    /**
     * Last
     * 
     */
    @JsonProperty("last")
    public void setLast(String last) {
        this.last = last;
    }

    /**
     * Lining Composition
     * 
     */
    @JsonProperty("liningComposition")
    public String getLiningComposition() {
        return liningComposition;
    }

    /**
     * Lining Composition
     * 
     */
    @JsonProperty("liningComposition")
    public void setLiningComposition(String liningComposition) {
        this.liningComposition = liningComposition;
    }

    /**
     * Upper Composition
     * 
     */
    @JsonProperty("upperComposition")
    public String getUpperComposition() {
        return upperComposition;
    }

    /**
     * Upper Composition
     * 
     */
    @JsonProperty("upperComposition")
    public void setUpperComposition(String upperComposition) {
        this.upperComposition = upperComposition;
    }

    /**
     * Measurement Set Required
     * 
     */
    @JsonProperty("measureSetRequired")
    public Boolean getMeasureSetRequired() {
        return measureSetRequired;
    }

    /**
     * Measurement Set Required
     * 
     */
    @JsonProperty("measureSetRequired")
    public void setMeasureSetRequired(Boolean measureSetRequired) {
        this.measureSetRequired = measureSetRequired;
    }

    /**
     * Handle Drop (cm)
     * 
     */
    @JsonProperty("handleDrop")
    public Double getHandleDrop() {
        return handleDrop;
    }

    /**
     * Handle Drop (cm)
     * 
     */
    @JsonProperty("handleDrop")
    public void setHandleDrop(Double handleDrop) {
        this.handleDrop = handleDrop;
    }

    /**
     * Strap Drop (cm)
     * 
     */
    @JsonProperty("strapDrop")
    public Double getStrapDrop() {
        return strapDrop;
    }

    /**
     * Strap Drop (cm)
     * 
     */
    @JsonProperty("strapDrop")
    public void setStrapDrop(Double strapDrop) {
        this.strapDrop = strapDrop;
    }

    /**
     * Development Season
     * 
     */
    @JsonProperty("devSeason")
    public String getDevSeason() {
        return devSeason;
    }

    /**
     * Development Season
     * 
     */
    @JsonProperty("devSeason")
    public void setDevSeason(String devSeason) {
        this.devSeason = devSeason;
    }

    @JsonProperty("material")
    public Material getMaterial() {
        return material;
    }

    @JsonProperty("material")
    public void setMaterial(Material material) {
        this.material = material;
    }

    @JsonProperty("colourways")
    public List<Colourway> getColourways() {
        return colourways;
    }

    @JsonProperty("colourways")
    public void setColourways(List<Colourway> colourways) {
        this.colourways = colourways;
    }

    @JsonProperty("productSeason")
    public List<ProductSeason> getProductSeason() {
        return productSeason;
    }

    @JsonProperty("productSeason")
    public void setProductSeason(List<ProductSeason> productSeason) {
        this.productSeason = productSeason;
    }

    @JsonProperty("source")
    public List<Source> getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(List<Source> source) {
        this.source = source;
    }

    @JsonProperty("commodityCode")
    public CommodityCode getCommodityCode() {
        return commodityCode;
    }

    @JsonProperty("commodityCode")
    public void setCommodityCode(CommodityCode commodityCode) {
        this.commodityCode = commodityCode;
    }

    @JsonProperty("riskManagement")
    public List<RiskManagement> getRiskManagement() {
        return riskManagement;
    }

    @JsonProperty("riskManagement")
    public void setRiskManagement(List<RiskManagement> riskManagement) {
        this.riskManagement = riskManagement;
    }

    @JsonProperty("documents")
    public List<Document> getDocuments() {
        return documents;
    }

    @JsonProperty("documents")
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @JsonProperty("required")
    public Object getRequired() {
        return required;
    }

    @JsonProperty("required")
    public void setRequired(Object required) {
        this.required = required;
    }

}

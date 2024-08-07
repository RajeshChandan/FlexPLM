
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ProductSeasonLink complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductSeasonLink">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lifeCycleState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smRetailDestinationSync" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="smTargetPurchasePrice" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="smPlannedColorways" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBrandManager" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smCapsule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smCategoryManagement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMDMSeason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smProductionManager" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smProductSeasonStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smProductTechnologist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMDMProduct" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmIdProduct" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smSalesNewness" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smDevelopmentNewness" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smProductionGroup" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smCommercialSizesChina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smCommercialSizesRussia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMasterScaleChina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMasterScaleRussia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smRecommendedRetailPriceStyleRMB" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="smRecommendedRetailPriceStyle" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="initialFCStyle" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="sourcingConfig" type="{http://www.sportmaster.ru/plmproduct}SourcingConfig" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smDevelopmentCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type1Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type2Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smDevelopmentGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smPrototype" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="fastTrack" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nos" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smProductPlannedAssemblyType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="productCostSheetRFQ" type="{http://www.sportmaster.ru/plmproduct}ProductCostSheetRFQ" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smUpper" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smLining" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smSole" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smSourcing" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smUserIntTriggered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smLastIntTriggered" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="smDesignBrief" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smMDMSIZ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smPLMIDSIZ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSeasonLink", propOrder = {
    "mdmId",
    "objectType",
    "plmId",
    "lifeCycleState",
    "smRetailDestinationSync",
    "smTargetPurchasePrice",
    "smPlannedColorways",
    "smBrandManager",
    "smCapsule",
    "smCategoryManagement",
    "smMDMSeason",
    "smProductionManager",
    "smProductSeasonStatus",
    "smProductTechnologist",
    "smMDMProduct",
    "plmIdProduct",
    "smSalesNewness",
    "smDevelopmentNewness",
    "smProductionGroup",
    "smCommercialSizesChina",
    "smCommercialSizesRussia",
    "smMasterScaleChina",
    "smMasterScaleRussia",
    "smRecommendedRetailPriceStyleRMB",
    "smRecommendedRetailPriceStyle",
    "initialFCStyle",
    "sourcingConfig",
    "createdOn",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy",
    "smDevelopmentCategory",
    "type1Level",
    "type2Level",
    "smDevelopmentGroup",
    "smPrototype",
    "fastTrack",
    "nos",
    "smProductPlannedAssemblyType",
    "productCostSheetRFQ",
    "smUpper",
    "smLining",
    "smSole",
    "smSourcing",
    "smUserIntTriggered",
    "smLastIntTriggered",
    "smDesignBrief",
    "smMDMSIZ",
    "smPLMIDSIZ"
})
public class ProductSeasonLink {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String objectType;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String lifeCycleState;
    @XmlElement(required = true)
    protected List<String> smRetailDestinationSync;
    protected BigDecimal smTargetPurchasePrice;
    protected BigInteger smPlannedColorways;
    @XmlElement(required = true)
    protected String smBrandManager;
    protected String smCapsule;
    protected String smCategoryManagement;
    @XmlElement(required = true)
    protected String smMDMSeason;
    protected String smProductionManager;
    @XmlElement(required = true)
    protected String smProductSeasonStatus;
    protected String smProductTechnologist;
    @XmlElement(required = true)
    protected String smMDMProduct;
    @XmlElement(required = true)
    protected String plmIdProduct;
    protected String smSalesNewness;
    protected String smDevelopmentNewness;
    @XmlElement(required = true)
    protected String smProductionGroup;
    protected String smCommercialSizesChina;
    protected String smCommercialSizesRussia;
    protected String smMasterScaleChina;
    protected String smMasterScaleRussia;
    protected BigDecimal smRecommendedRetailPriceStyleRMB;
    protected BigDecimal smRecommendedRetailPriceStyle;
    protected BigInteger initialFCStyle;
    protected List<SourcingConfig> sourcingConfig;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdOn;
    @XmlElement(required = true)
    protected String createdBy;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    @XmlElement(required = true)
    protected String lastUpdatedBy;
    protected String smDevelopmentCategory;
    @XmlElement(required = true)
    protected String type1Level;
    @XmlElement(required = true)
    protected String type2Level;
    protected String smDevelopmentGroup;
    protected boolean smPrototype;
    protected String fastTrack;
    protected boolean nos;
    protected List<String> smProductPlannedAssemblyType;
    protected List<ProductCostSheetRFQ> productCostSheetRFQ;
    protected String smUpper;
    protected String smLining;
    protected List<String> smSole;
    protected String smSourcing;
    protected String smUserIntTriggered;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar smLastIntTriggered;
    protected boolean smDesignBrief;
    protected String smMDMSIZ;
    protected String smPLMIDSIZ;

    /**
     * Gets the value of the mdmId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmId() {
        return mdmId;
    }

    /**
     * Sets the value of the mdmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmId(String value) {
        this.mdmId = value;
    }

    /**
     * Gets the value of the objectType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the value of the objectType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectType(String value) {
        this.objectType = value;
    }

    /**
     * Gets the value of the plmId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlmId() {
        return plmId;
    }

    /**
     * Sets the value of the plmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlmId(String value) {
        this.plmId = value;
    }

    /**
     * Gets the value of the lifeCycleState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLifeCycleState() {
        return lifeCycleState;
    }

    /**
     * Sets the value of the lifeCycleState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLifeCycleState(String value) {
        this.lifeCycleState = value;
    }

    /**
     * Gets the value of the smRetailDestinationSync property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smRetailDestinationSync property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmRetailDestinationSync().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmRetailDestinationSync() {
        if (smRetailDestinationSync == null) {
            smRetailDestinationSync = new ArrayList<String>();
        }
        return this.smRetailDestinationSync;
    }

    /**
     * Gets the value of the smTargetPurchasePrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSmTargetPurchasePrice() {
        return smTargetPurchasePrice;
    }

    /**
     * Sets the value of the smTargetPurchasePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSmTargetPurchasePrice(BigDecimal value) {
        this.smTargetPurchasePrice = value;
    }

    /**
     * Gets the value of the smPlannedColorways property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmPlannedColorways() {
        return smPlannedColorways;
    }

    /**
     * Sets the value of the smPlannedColorways property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmPlannedColorways(BigInteger value) {
        this.smPlannedColorways = value;
    }

    /**
     * Gets the value of the smBrandManager property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmBrandManager() {
        return smBrandManager;
    }

    /**
     * Sets the value of the smBrandManager property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmBrandManager(String value) {
        this.smBrandManager = value;
    }

    /**
     * Gets the value of the smCapsule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCapsule() {
        return smCapsule;
    }

    /**
     * Sets the value of the smCapsule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCapsule(String value) {
        this.smCapsule = value;
    }

    /**
     * Gets the value of the smCategoryManagement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCategoryManagement() {
        return smCategoryManagement;
    }

    /**
     * Sets the value of the smCategoryManagement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCategoryManagement(String value) {
        this.smCategoryManagement = value;
    }

    /**
     * Gets the value of the smMDMSeason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMSeason() {
        return smMDMSeason;
    }

    /**
     * Sets the value of the smMDMSeason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMSeason(String value) {
        this.smMDMSeason = value;
    }

    /**
     * Gets the value of the smProductionManager property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProductionManager() {
        return smProductionManager;
    }

    /**
     * Sets the value of the smProductionManager property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProductionManager(String value) {
        this.smProductionManager = value;
    }

    /**
     * Gets the value of the smProductSeasonStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProductSeasonStatus() {
        return smProductSeasonStatus;
    }

    /**
     * Sets the value of the smProductSeasonStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProductSeasonStatus(String value) {
        this.smProductSeasonStatus = value;
    }

    /**
     * Gets the value of the smProductTechnologist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProductTechnologist() {
        return smProductTechnologist;
    }

    /**
     * Sets the value of the smProductTechnologist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProductTechnologist(String value) {
        this.smProductTechnologist = value;
    }

    /**
     * Gets the value of the smMDMProduct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMProduct() {
        return smMDMProduct;
    }

    /**
     * Sets the value of the smMDMProduct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMProduct(String value) {
        this.smMDMProduct = value;
    }

    /**
     * Gets the value of the plmIdProduct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlmIdProduct() {
        return plmIdProduct;
    }

    /**
     * Sets the value of the plmIdProduct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlmIdProduct(String value) {
        this.plmIdProduct = value;
    }

    /**
     * Gets the value of the smSalesNewness property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSalesNewness() {
        return smSalesNewness;
    }

    /**
     * Sets the value of the smSalesNewness property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSalesNewness(String value) {
        this.smSalesNewness = value;
    }

    /**
     * Gets the value of the smDevelopmentNewness property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmDevelopmentNewness() {
        return smDevelopmentNewness;
    }

    /**
     * Sets the value of the smDevelopmentNewness property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmDevelopmentNewness(String value) {
        this.smDevelopmentNewness = value;
    }

    /**
     * Gets the value of the smProductionGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProductionGroup() {
        return smProductionGroup;
    }

    /**
     * Sets the value of the smProductionGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProductionGroup(String value) {
        this.smProductionGroup = value;
    }

    /**
     * Gets the value of the smCommercialSizesChina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCommercialSizesChina() {
        return smCommercialSizesChina;
    }

    /**
     * Sets the value of the smCommercialSizesChina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCommercialSizesChina(String value) {
        this.smCommercialSizesChina = value;
    }

    /**
     * Gets the value of the smCommercialSizesRussia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCommercialSizesRussia() {
        return smCommercialSizesRussia;
    }

    /**
     * Sets the value of the smCommercialSizesRussia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCommercialSizesRussia(String value) {
        this.smCommercialSizesRussia = value;
    }

    /**
     * Gets the value of the smMasterScaleChina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMasterScaleChina() {
        return smMasterScaleChina;
    }

    /**
     * Sets the value of the smMasterScaleChina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMasterScaleChina(String value) {
        this.smMasterScaleChina = value;
    }

    /**
     * Gets the value of the smMasterScaleRussia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMasterScaleRussia() {
        return smMasterScaleRussia;
    }

    /**
     * Sets the value of the smMasterScaleRussia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMasterScaleRussia(String value) {
        this.smMasterScaleRussia = value;
    }

    /**
     * Gets the value of the smRecommendedRetailPriceStyleRMB property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSmRecommendedRetailPriceStyleRMB() {
        return smRecommendedRetailPriceStyleRMB;
    }

    /**
     * Sets the value of the smRecommendedRetailPriceStyleRMB property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSmRecommendedRetailPriceStyleRMB(BigDecimal value) {
        this.smRecommendedRetailPriceStyleRMB = value;
    }

    /**
     * Gets the value of the smRecommendedRetailPriceStyle property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSmRecommendedRetailPriceStyle() {
        return smRecommendedRetailPriceStyle;
    }

    /**
     * Sets the value of the smRecommendedRetailPriceStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSmRecommendedRetailPriceStyle(BigDecimal value) {
        this.smRecommendedRetailPriceStyle = value;
    }

    /**
     * Gets the value of the initialFCStyle property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getInitialFCStyle() {
        return initialFCStyle;
    }

    /**
     * Sets the value of the initialFCStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setInitialFCStyle(BigInteger value) {
        this.initialFCStyle = value;
    }

    /**
     * Gets the value of the sourcingConfig property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sourcingConfig property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSourcingConfig().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SourcingConfig }
     * 
     * 
     */
    public List<SourcingConfig> getSourcingConfig() {
        if (sourcingConfig == null) {
            sourcingConfig = new ArrayList<SourcingConfig>();
        }
        return this.sourcingConfig;
    }

    /**
     * Gets the value of the createdOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the value of the createdOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedOn(XMLGregorianCalendar value) {
        this.createdOn = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the lastUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the value of the lastUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastUpdated(XMLGregorianCalendar value) {
        this.lastUpdated = value;
    }

    /**
     * Gets the value of the lastUpdatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the value of the lastUpdatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUpdatedBy(String value) {
        this.lastUpdatedBy = value;
    }

    /**
     * Gets the value of the smDevelopmentCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmDevelopmentCategory() {
        return smDevelopmentCategory;
    }

    /**
     * Sets the value of the smDevelopmentCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmDevelopmentCategory(String value) {
        this.smDevelopmentCategory = value;
    }

    /**
     * Gets the value of the type1Level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType1Level() {
        return type1Level;
    }

    /**
     * Sets the value of the type1Level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType1Level(String value) {
        this.type1Level = value;
    }

    /**
     * Gets the value of the type2Level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType2Level() {
        return type2Level;
    }

    /**
     * Sets the value of the type2Level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType2Level(String value) {
        this.type2Level = value;
    }

    /**
     * Gets the value of the smDevelopmentGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmDevelopmentGroup() {
        return smDevelopmentGroup;
    }

    /**
     * Sets the value of the smDevelopmentGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmDevelopmentGroup(String value) {
        this.smDevelopmentGroup = value;
    }

    /**
     * Gets the value of the smPrototype property.
     * 
     */
    public boolean isSmPrototype() {
        return smPrototype;
    }

    /**
     * Sets the value of the smPrototype property.
     * 
     */
    public void setSmPrototype(boolean value) {
        this.smPrototype = value;
    }

    /**
     * Gets the value of the fastTrack property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFastTrack() {
        return fastTrack;
    }

    /**
     * Sets the value of the fastTrack property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFastTrack(String value) {
        this.fastTrack = value;
    }

    /**
     * Gets the value of the nos property.
     * 
     */
    public boolean isNos() {
        return nos;
    }

    /**
     * Sets the value of the nos property.
     * 
     */
    public void setNos(boolean value) {
        this.nos = value;
    }

    /**
     * Gets the value of the smProductPlannedAssemblyType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smProductPlannedAssemblyType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmProductPlannedAssemblyType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmProductPlannedAssemblyType() {
        if (smProductPlannedAssemblyType == null) {
            smProductPlannedAssemblyType = new ArrayList<String>();
        }
        return this.smProductPlannedAssemblyType;
    }

    /**
     * Gets the value of the productCostSheetRFQ property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productCostSheetRFQ property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductCostSheetRFQ().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductCostSheetRFQ }
     * 
     * 
     */
    public List<ProductCostSheetRFQ> getProductCostSheetRFQ() {
        if (productCostSheetRFQ == null) {
            productCostSheetRFQ = new ArrayList<ProductCostSheetRFQ>();
        }
        return this.productCostSheetRFQ;
    }

    /**
     * Gets the value of the smUpper property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmUpper() {
        return smUpper;
    }

    /**
     * Sets the value of the smUpper property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmUpper(String value) {
        this.smUpper = value;
    }

    /**
     * Gets the value of the smLining property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmLining() {
        return smLining;
    }

    /**
     * Sets the value of the smLining property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmLining(String value) {
        this.smLining = value;
    }

    /**
     * Gets the value of the smSole property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smSole property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmSole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmSole() {
        if (smSole == null) {
            smSole = new ArrayList<String>();
        }
        return this.smSole;
    }

    /**
     * Gets the value of the smSourcing property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSourcing() {
        return smSourcing;
    }

    /**
     * Sets the value of the smSourcing property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSourcing(String value) {
        this.smSourcing = value;
    }

    /**
     * Gets the value of the smUserIntTriggered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmUserIntTriggered() {
        return smUserIntTriggered;
    }

    /**
     * Sets the value of the smUserIntTriggered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmUserIntTriggered(String value) {
        this.smUserIntTriggered = value;
    }

    /**
     * Gets the value of the smLastIntTriggered property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSmLastIntTriggered() {
        return smLastIntTriggered;
    }

    /**
     * Sets the value of the smLastIntTriggered property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSmLastIntTriggered(XMLGregorianCalendar value) {
        this.smLastIntTriggered = value;
    }

    /**
     * Gets the value of the smDesignBrief property.
     * 
     */
    public boolean isSmDesignBrief() {
        return smDesignBrief;
    }

    /**
     * Sets the value of the smDesignBrief property.
     * 
     */
    public void setSmDesignBrief(boolean value) {
        this.smDesignBrief = value;
    }

    /**
     * Gets the value of the smMDMSIZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMSIZ() {
        return smMDMSIZ;
    }

    /**
     * Sets the value of the smMDMSIZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMSIZ(String value) {
        this.smMDMSIZ = value;
    }

    /**
     * Gets the value of the smPLMIDSIZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmPLMIDSIZ() {
        return smPLMIDSIZ;
    }

    /**
     * Sets the value of the smPLMIDSIZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmPLMIDSIZ(String value) {
        this.smPLMIDSIZ = value;
    }

}

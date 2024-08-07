
package com.sportmaster.wc.interfaces.webservices.bean;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="matTypeWithinHierarchy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="flexPLMMaterialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lifecycleState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gom" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="materialType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smGuidMAT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="uom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sizeQuality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smYarn1Count" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smYarn2Count" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smYarn3Count" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="waterproof" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="breathability" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="fabricTreatment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="composition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fabricFinishing" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="brand" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="materialFabricSupplierAttributes" type="{http://www.sportmaster.ru/plmservice}MaterialFabricSupplierAttributes" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requestId",
    "matTypeWithinHierarchy",
    "mdmId",
    "flexPLMMaterialName",
    "plmId",
    "materialStatus",
    "lifecycleState",
    "gom",
    "materialType",
    "smGuidMAT",
    "lastUpdated",
    "lastUpdatedBy",
    "uom",
    "sizeQuality",
    "smYarn1Count",
    "smYarn2Count",
    "smYarn3Count",
    "weight",
    "waterproof",
    "breathability",
    "fabricTreatment",
    "notes",
    "composition",
    "fabricFinishing",
    "brand",
    "materialFabricSupplierAttributes",
    "createdBy",
    "createdOn"
})
@XmlRootElement(name = "FabricMaterialInformationUpdatesRequest")
public class FabricMaterialInformationUpdatesRequest {

    @XmlElement(name = "RequestId")
    protected int requestId;
    @XmlElement(required = true)
    protected String matTypeWithinHierarchy;
    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String flexPLMMaterialName;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String materialStatus;
    @XmlElement(required = true)
    protected String lifecycleState;
    protected List<String> gom;
    @XmlElement(required = true)
    protected String materialType;
    protected String smGuidMAT;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    protected String lastUpdatedBy;
    protected String uom;
    protected String sizeQuality;
    protected String smYarn1Count;
    protected String smYarn2Count;
    protected String smYarn3Count;
    protected Float weight;
    protected Long waterproof;
    protected BigInteger breathability;
    protected String fabricTreatment;
    protected String notes;
    protected String composition;
    protected String fabricFinishing;
    protected String brand;
    protected List<MaterialFabricSupplierAttributes> materialFabricSupplierAttributes;
    @XmlElement(required = true)
    protected String createdBy;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdOn;

    /**
     * Gets the value of the requestId property.
     * 
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     */
    public void setRequestId(int value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the matTypeWithinHierarchy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatTypeWithinHierarchy() {
        return matTypeWithinHierarchy;
    }

    /**
     * Sets the value of the matTypeWithinHierarchy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatTypeWithinHierarchy(String value) {
        this.matTypeWithinHierarchy = value;
    }

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
     * Gets the value of the flexPLMMaterialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlexPLMMaterialName() {
        return flexPLMMaterialName;
    }

    /**
     * Sets the value of the flexPLMMaterialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlexPLMMaterialName(String value) {
        this.flexPLMMaterialName = value;
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
     * Gets the value of the materialStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialStatus() {
        return materialStatus;
    }

    /**
     * Sets the value of the materialStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialStatus(String value) {
        this.materialStatus = value;
    }

    /**
     * Gets the value of the lifecycleState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLifecycleState() {
        return lifecycleState;
    }

    /**
     * Sets the value of the lifecycleState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLifecycleState(String value) {
        this.lifecycleState = value;
    }

    /**
     * Gets the value of the gom property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gom property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGom().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGom() {
        if (gom == null) {
            gom = new ArrayList<String>();
        }
        return this.gom;
    }

    /**
     * Gets the value of the materialType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialType() {
        return materialType;
    }

    /**
     * Sets the value of the materialType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialType(String value) {
        this.materialType = value;
    }

    /**
     * Gets the value of the smGuidMAT property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmGuidMAT() {
        return smGuidMAT;
    }

    /**
     * Sets the value of the smGuidMAT property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmGuidMAT(String value) {
        this.smGuidMAT = value;
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
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUom(String value) {
        this.uom = value;
    }

    /**
     * Gets the value of the sizeQuality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSizeQuality() {
        return sizeQuality;
    }

    /**
     * Sets the value of the sizeQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSizeQuality(String value) {
        this.sizeQuality = value;
    }

    /**
     * Gets the value of the smYarn1Count property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmYarn1Count() {
        return smYarn1Count;
    }

    /**
     * Sets the value of the smYarn1Count property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmYarn1Count(String value) {
        this.smYarn1Count = value;
    }

    /**
     * Gets the value of the smYarn2Count property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmYarn2Count() {
        return smYarn2Count;
    }

    /**
     * Sets the value of the smYarn2Count property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmYarn2Count(String value) {
        this.smYarn2Count = value;
    }

    /**
     * Gets the value of the smYarn3Count property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmYarn3Count() {
        return smYarn3Count;
    }

    /**
     * Sets the value of the smYarn3Count property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmYarn3Count(String value) {
        this.smYarn3Count = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setWeight(Float value) {
        this.weight = value;
    }

    /**
     * Gets the value of the waterproof property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getWaterproof() {
        return waterproof;
    }

    /**
     * Sets the value of the waterproof property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setWaterproof(Long value) {
        this.waterproof = value;
    }

    /**
     * Gets the value of the breathability property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBreathability() {
        return breathability;
    }

    /**
     * Sets the value of the breathability property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBreathability(BigInteger value) {
        this.breathability = value;
    }

    /**
     * Gets the value of the fabricTreatment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFabricTreatment() {
        return fabricTreatment;
    }

    /**
     * Sets the value of the fabricTreatment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFabricTreatment(String value) {
        this.fabricTreatment = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the composition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComposition() {
        return composition;
    }

    /**
     * Sets the value of the composition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComposition(String value) {
        this.composition = value;
    }

    /**
     * Gets the value of the fabricFinishing property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFabricFinishing() {
        return fabricFinishing;
    }

    /**
     * Sets the value of the fabricFinishing property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFabricFinishing(String value) {
        this.fabricFinishing = value;
    }

    /**
     * Gets the value of the brand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the value of the brand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrand(String value) {
        this.brand = value;
    }

    /**
     * Gets the value of the materialFabricSupplierAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the materialFabricSupplierAttributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterialFabricSupplierAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MaterialFabricSupplierAttributes }
     * 
     * 
     */
    public List<MaterialFabricSupplierAttributes> getMaterialFabricSupplierAttributes() {
        if (materialFabricSupplierAttributes == null) {
            materialFabricSupplierAttributes = new ArrayList<MaterialFabricSupplierAttributes>();
        }
        return this.materialFabricSupplierAttributes;
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

}

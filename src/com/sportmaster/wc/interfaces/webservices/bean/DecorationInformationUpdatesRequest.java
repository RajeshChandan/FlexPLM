
package com.sportmaster.wc.interfaces.webservices.bean;

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
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="flexPLMMaterialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="matTypeWithinHierarchy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lifecycleState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gom" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smGuidMAT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="uom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sizeQuality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="diameterUOM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diameter" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="lengthUOM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trimSubtype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="widthUOM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="brand" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rawMaterial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smWeightUOM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="materialTrimsSupplierAttributes" type="{http://www.sportmaster.ru/plmservice}MaterialTrimsSupplierAttributes" maxOccurs="unbounded" minOccurs="0"/>
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
    "mdmId",
    "flexPLMMaterialName",
    "matTypeWithinHierarchy",
    "plmId",
    "materialStatus",
    "lifecycleState",
    "gom",
    "smGuidMAT",
    "uom",
    "sizeQuality",
    "weight",
    "diameterUOM",
    "diameter",
    "lengthUOM",
    "length",
    "notes",
    "trimSubtype",
    "widthUOM",
    "width",
    "brand",
    "rawMaterial",
    "createdBy",
    "createdOn",
    "lastUpdated",
    "lastUpdatedBy",
    "smWeightUOM",
    "requestId",
    "materialTrimsSupplierAttributes"
})
@XmlRootElement(name = "DecorationInformationUpdatesRequest")
public class DecorationInformationUpdatesRequest {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String flexPLMMaterialName;
    @XmlElement(required = true)
    protected String matTypeWithinHierarchy;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String materialStatus;
    @XmlElement(required = true)
    protected String lifecycleState;
    protected List<String> gom;
    protected String smGuidMAT;
    protected String uom;
    protected String sizeQuality;
    protected Float weight;
    protected String diameterUOM;
    protected Float diameter;
    protected String lengthUOM;
    protected Float length;
    protected String notes;
    @XmlElement(required = true)
    protected String trimSubtype;
    protected String widthUOM;
    protected Float width;
    protected String brand;
    protected String rawMaterial;
    @XmlElement(required = true)
    protected String createdBy;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdOn;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    @XmlElement(required = true)
    protected String lastUpdatedBy;
    protected String smWeightUOM;
    @XmlElement(name = "RequestId")
    protected int requestId;
    protected List<MaterialTrimsSupplierAttributes> materialTrimsSupplierAttributes;

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
     * Gets the value of the diameterUOM property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiameterUOM() {
        return diameterUOM;
    }

    /**
     * Sets the value of the diameterUOM property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiameterUOM(String value) {
        this.diameterUOM = value;
    }

    /**
     * Gets the value of the diameter property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDiameter() {
        return diameter;
    }

    /**
     * Sets the value of the diameter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDiameter(Float value) {
        this.diameter = value;
    }

    /**
     * Gets the value of the lengthUOM property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLengthUOM() {
        return lengthUOM;
    }

    /**
     * Sets the value of the lengthUOM property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLengthUOM(String value) {
        this.lengthUOM = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLength(Float value) {
        this.length = value;
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
     * Gets the value of the trimSubtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrimSubtype() {
        return trimSubtype;
    }

    /**
     * Sets the value of the trimSubtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrimSubtype(String value) {
        this.trimSubtype = value;
    }

    /**
     * Gets the value of the widthUOM property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidthUOM() {
        return widthUOM;
    }

    /**
     * Sets the value of the widthUOM property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidthUOM(String value) {
        this.widthUOM = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setWidth(Float value) {
        this.width = value;
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
     * Gets the value of the rawMaterial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRawMaterial() {
        return rawMaterial;
    }

    /**
     * Sets the value of the rawMaterial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRawMaterial(String value) {
        this.rawMaterial = value;
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
     * Gets the value of the smWeightUOM property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmWeightUOM() {
        return smWeightUOM;
    }

    /**
     * Sets the value of the smWeightUOM property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmWeightUOM(String value) {
        this.smWeightUOM = value;
    }

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
     * Gets the value of the materialTrimsSupplierAttributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the materialTrimsSupplierAttributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterialTrimsSupplierAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MaterialTrimsSupplierAttributes }
     * 
     * 
     */
    public List<MaterialTrimsSupplierAttributes> getMaterialTrimsSupplierAttributes() {
        if (materialTrimsSupplierAttributes == null) {
            materialTrimsSupplierAttributes = new ArrayList<MaterialTrimsSupplierAttributes>();
        }
        return this.materialTrimsSupplierAttributes;
    }

}

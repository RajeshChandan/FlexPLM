
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Colorway complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Colorway">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lifeCycleState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smSafetyStandard" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smSafetyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMDMProduct" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmIdProduct" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="skuName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smMDMCOL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="thumbnail" type="{http://www.sportmaster.ru/plmproduct}Thumbnail" minOccurs="0"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwaySeasonLink" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLink" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="type1Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type2Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smUserIntTriggered" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smLastIntTriggered" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Colorway", propOrder = {
    "mdmId",
    "objectType",
    "plmId",
    "lifeCycleState",
    "smSafetyStandard",
    "smSafetyCode",
    "smMDMProduct",
    "plmIdProduct",
    "skuName",
    "smMDMCOL",
    "thumbnail",
    "createdOn",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy",
    "colorwaySeasonLink",
    "type1Level",
    "type2Level",
    "smUserIntTriggered",
    "smLastIntTriggered"
})
public class Colorway {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String objectType;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String lifeCycleState;
    protected String smSafetyStandard;
    protected String smSafetyCode;
    @XmlElement(required = true)
    protected String smMDMProduct;
    @XmlElement(required = true)
    protected String plmIdProduct;
    @XmlElement(required = true)
    protected String skuName;
    @XmlElement(required = true)
    protected String smMDMCOL;
    protected Thumbnail thumbnail;
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
    protected List<ColorwaySeasonLink> colorwaySeasonLink;
    @XmlElement(required = true)
    protected String type1Level;
    @XmlElement(required = true)
    protected String type2Level;
    protected String smUserIntTriggered;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar smLastIntTriggered;

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
     * Gets the value of the smSafetyStandard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSafetyStandard() {
        return smSafetyStandard;
    }

    /**
     * Sets the value of the smSafetyStandard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSafetyStandard(String value) {
        this.smSafetyStandard = value;
    }

    /**
     * Gets the value of the smSafetyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSafetyCode() {
        return smSafetyCode;
    }

    /**
     * Sets the value of the smSafetyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSafetyCode(String value) {
        this.smSafetyCode = value;
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
     * Gets the value of the skuName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkuName() {
        return skuName;
    }

    /**
     * Sets the value of the skuName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkuName(String value) {
        this.skuName = value;
    }

    /**
     * Gets the value of the smMDMCOL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMCOL() {
        return smMDMCOL;
    }

    /**
     * Sets the value of the smMDMCOL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMCOL(String value) {
        this.smMDMCOL = value;
    }

    /**
     * Gets the value of the thumbnail property.
     * 
     * @return
     *     possible object is
     *     {@link Thumbnail }
     *     
     */
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the value of the thumbnail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Thumbnail }
     *     
     */
    public void setThumbnail(Thumbnail value) {
        this.thumbnail = value;
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
     * Gets the value of the colorwaySeasonLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorwaySeasonLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorwaySeasonLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorwaySeasonLink }
     * 
     * 
     */
    public List<ColorwaySeasonLink> getColorwaySeasonLink() {
        if (colorwaySeasonLink == null) {
            colorwaySeasonLink = new ArrayList<ColorwaySeasonLink>();
        }
        return this.colorwaySeasonLink;
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

}

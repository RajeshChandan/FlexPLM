
package com.sportmaster.wc.interfaces.webservices.bombean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BOMPart complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BOMPart">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BomPartBranchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BomName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BomStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SpecLastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SpecSeasonName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecSeasonMDMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SourcingPLMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SourcingName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BusinessSupplierPLMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BusinessSupplierMDMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BusinessSupplierName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CreatedON" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="CreatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="LastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BomLinkVariation" type="{http://www.sportmaster.ru/plmbom}BomLinkVariation" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BOMPart", propOrder = {
    "bomPartBranchID",
    "bomName",
    "bomStatus",
    "specID",
    "specName",
    "specStatus",
    "specCreated",
    "specLastUpdated",
    "specSeasonName",
    "specSeasonMDMID",
    "sourcingPLMID",
    "sourcingName",
    "businessSupplierPLMID",
    "businessSupplierMDMID",
    "businessSupplierName",
    "createdON",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy",
    "bomLinkVariation"
})
public class BOMPart {

    @XmlElement(name = "BomPartBranchID", required = true)
    protected String bomPartBranchID;
    @XmlElement(name = "BomName", required = true)
    protected String bomName;
    @XmlElement(name = "BomStatus", required = true)
    protected String bomStatus;
    @XmlElement(name = "SpecID", required = true)
    protected String specID;
    @XmlElement(name = "SpecName", required = true)
    protected String specName;
    @XmlElement(name = "SpecStatus", required = true)
    protected String specStatus;
    @XmlElement(name = "SpecCreated", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar specCreated;
    @XmlElement(name = "SpecLastUpdated", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar specLastUpdated;
    @XmlElement(name = "SpecSeasonName", required = true)
    protected String specSeasonName;
    @XmlElement(name = "SpecSeasonMDMID", required = true)
    protected String specSeasonMDMID;
    @XmlElement(name = "SourcingPLMID", required = true)
    protected String sourcingPLMID;
    @XmlElement(name = "SourcingName", required = true)
    protected String sourcingName;
    @XmlElement(name = "BusinessSupplierPLMID", required = true)
    protected String businessSupplierPLMID;
    @XmlElement(name = "BusinessSupplierMDMID", required = true)
    protected String businessSupplierMDMID;
    @XmlElement(name = "BusinessSupplierName", required = true)
    protected String businessSupplierName;
    @XmlElement(name = "CreatedON", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdON;
    @XmlElement(name = "CreatedBy", required = true)
    protected String createdBy;
    @XmlElement(name = "LastUpdated", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    @XmlElement(name = "LastUpdatedBy", required = true)
    protected String lastUpdatedBy;
    @XmlElement(name = "BomLinkVariation", required = true)
    protected List<BomLinkVariation> bomLinkVariation;

    /**
     * Gets the value of the bomPartBranchID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBomPartBranchID() {
        return bomPartBranchID;
    }

    /**
     * Sets the value of the bomPartBranchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBomPartBranchID(String value) {
        this.bomPartBranchID = value;
    }

    /**
     * Gets the value of the bomName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBomName() {
        return bomName;
    }

    /**
     * Sets the value of the bomName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBomName(String value) {
        this.bomName = value;
    }

    /**
     * Gets the value of the bomStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBomStatus() {
        return bomStatus;
    }

    /**
     * Sets the value of the bomStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBomStatus(String value) {
        this.bomStatus = value;
    }

    /**
     * Gets the value of the specID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecID() {
        return specID;
    }

    /**
     * Sets the value of the specID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecID(String value) {
        this.specID = value;
    }

    /**
     * Gets the value of the specName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * Sets the value of the specName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecName(String value) {
        this.specName = value;
    }

    /**
     * Gets the value of the specStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecStatus() {
        return specStatus;
    }

    /**
     * Sets the value of the specStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecStatus(String value) {
        this.specStatus = value;
    }

    /**
     * Gets the value of the specCreated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSpecCreated() {
        return specCreated;
    }

    /**
     * Sets the value of the specCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSpecCreated(XMLGregorianCalendar value) {
        this.specCreated = value;
    }

    /**
     * Gets the value of the specLastUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSpecLastUpdated() {
        return specLastUpdated;
    }

    /**
     * Sets the value of the specLastUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSpecLastUpdated(XMLGregorianCalendar value) {
        this.specLastUpdated = value;
    }

    /**
     * Gets the value of the specSeasonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecSeasonName() {
        return specSeasonName;
    }

    /**
     * Sets the value of the specSeasonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecSeasonName(String value) {
        this.specSeasonName = value;
    }

    /**
     * Gets the value of the specSeasonMDMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecSeasonMDMID() {
        return specSeasonMDMID;
    }

    /**
     * Sets the value of the specSeasonMDMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecSeasonMDMID(String value) {
        this.specSeasonMDMID = value;
    }

    /**
     * Gets the value of the sourcingPLMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourcingPLMID() {
        return sourcingPLMID;
    }

    /**
     * Sets the value of the sourcingPLMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourcingPLMID(String value) {
        this.sourcingPLMID = value;
    }

    /**
     * Gets the value of the sourcingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourcingName() {
        return sourcingName;
    }

    /**
     * Sets the value of the sourcingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourcingName(String value) {
        this.sourcingName = value;
    }

    /**
     * Gets the value of the businessSupplierPLMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessSupplierPLMID() {
        return businessSupplierPLMID;
    }

    /**
     * Sets the value of the businessSupplierPLMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessSupplierPLMID(String value) {
        this.businessSupplierPLMID = value;
    }

    /**
     * Gets the value of the businessSupplierMDMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessSupplierMDMID() {
        return businessSupplierMDMID;
    }

    /**
     * Sets the value of the businessSupplierMDMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessSupplierMDMID(String value) {
        this.businessSupplierMDMID = value;
    }

    /**
     * Gets the value of the businessSupplierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessSupplierName() {
        return businessSupplierName;
    }

    /**
     * Sets the value of the businessSupplierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessSupplierName(String value) {
        this.businessSupplierName = value;
    }

    /**
     * Gets the value of the createdON property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedON() {
        return createdON;
    }

    /**
     * Sets the value of the createdON property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedON(XMLGregorianCalendar value) {
        this.createdON = value;
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
     * Gets the value of the bomLinkVariation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bomLinkVariation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBomLinkVariation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BomLinkVariation }
     * 
     * 
     */
    public List<BomLinkVariation> getBomLinkVariation() {
        if (bomLinkVariation == null) {
            bomLinkVariation = new ArrayList<BomLinkVariation>();
        }
        return this.bomLinkVariation;
    }

}


package com.sportmaster.wc.interfaces.webservices.bombean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BomLinkVariation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BomLinkVariation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BOMLinkBranchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Consumption" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="colorwayPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwayMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwaySeasonPLMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwaySeasonMDMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SizeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SizeDefinitionPLMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialSupplierMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="materialSupplierPLMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ColorType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ColorProviderCatalog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ColorStandardRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ColorArtwork" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CreatedON" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="CreatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="LastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BomLinkVariation", propOrder = {
    "bomLinkBranchID",
    "consumption",
    "colorwayPLMId",
    "colorwayMDMId",
    "colorwaySeasonPLMID",
    "colorwaySeasonMDMID",
    "sizeName",
    "sizeDefinitionPLMID",
    "materialPLMId",
    "materialMDMId",
    "materialName",
    "materialSupplierMDMId",
    "materialSupplierPLMId",
    "colorType",
    "colorProviderCatalog",
    "colorStandardRef",
    "colorArtwork",
    "createdON",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy"
})
public class BomLinkVariation {

    @XmlElement(name = "BOMLinkBranchID", required = true)
    protected String bomLinkBranchID;
    @XmlElement(name = "Consumption", required = true)
    protected BigDecimal consumption;
    @XmlElement(required = true)
    protected String colorwayPLMId;
    @XmlElement(required = true)
    protected String colorwayMDMId;
    @XmlElement(required = true)
    protected String colorwaySeasonPLMID;
    @XmlElement(required = true)
    protected String colorwaySeasonMDMID;
    @XmlElement(name = "SizeName", required = true)
    protected String sizeName;
    @XmlElement(name = "SizeDefinitionPLMID", required = true)
    protected String sizeDefinitionPLMID;
    @XmlElement(required = true)
    protected String materialPLMId;
    @XmlElement(required = true)
    protected String materialMDMId;
    @XmlElement(required = true)
    protected String materialName;
    protected String materialSupplierMDMId;
    protected String materialSupplierPLMId;
    @XmlElement(name = "ColorType")
    protected String colorType;
    @XmlElement(name = "ColorProviderCatalog")
    protected String colorProviderCatalog;
    @XmlElement(name = "ColorStandardRef")
    protected String colorStandardRef;
    @XmlElement(name = "ColorArtwork")
    protected String colorArtwork;
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

    /**
     * Gets the value of the bomLinkBranchID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBOMLinkBranchID() {
        return bomLinkBranchID;
    }

    /**
     * Sets the value of the bomLinkBranchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBOMLinkBranchID(String value) {
        this.bomLinkBranchID = value;
    }

    /**
     * Gets the value of the consumption property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getConsumption() {
        return consumption;
    }

    /**
     * Sets the value of the consumption property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setConsumption(BigDecimal value) {
        this.consumption = value;
    }

    /**
     * Gets the value of the colorwayPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwayPLMId() {
        return colorwayPLMId;
    }

    /**
     * Sets the value of the colorwayPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwayPLMId(String value) {
        this.colorwayPLMId = value;
    }

    /**
     * Gets the value of the colorwayMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwayMDMId() {
        return colorwayMDMId;
    }

    /**
     * Sets the value of the colorwayMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwayMDMId(String value) {
        this.colorwayMDMId = value;
    }

    /**
     * Gets the value of the colorwaySeasonPLMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwaySeasonPLMID() {
        return colorwaySeasonPLMID;
    }

    /**
     * Sets the value of the colorwaySeasonPLMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwaySeasonPLMID(String value) {
        this.colorwaySeasonPLMID = value;
    }

    /**
     * Gets the value of the colorwaySeasonMDMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwaySeasonMDMID() {
        return colorwaySeasonMDMID;
    }

    /**
     * Sets the value of the colorwaySeasonMDMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwaySeasonMDMID(String value) {
        this.colorwaySeasonMDMID = value;
    }

    /**
     * Gets the value of the sizeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSizeName() {
        return sizeName;
    }

    /**
     * Sets the value of the sizeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSizeName(String value) {
        this.sizeName = value;
    }

    /**
     * Gets the value of the sizeDefinitionPLMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSizeDefinitionPLMID() {
        return sizeDefinitionPLMID;
    }

    /**
     * Sets the value of the sizeDefinitionPLMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSizeDefinitionPLMID(String value) {
        this.sizeDefinitionPLMID = value;
    }

    /**
     * Gets the value of the materialPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialPLMId() {
        return materialPLMId;
    }

    /**
     * Sets the value of the materialPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialPLMId(String value) {
        this.materialPLMId = value;
    }

    /**
     * Gets the value of the materialMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialMDMId() {
        return materialMDMId;
    }

    /**
     * Sets the value of the materialMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialMDMId(String value) {
        this.materialMDMId = value;
    }

    /**
     * Gets the value of the materialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialName() {
        return materialName;
    }

    /**
     * Sets the value of the materialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialName(String value) {
        this.materialName = value;
    }

    /**
     * Gets the value of the materialSupplierMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialSupplierMDMId() {
        return materialSupplierMDMId;
    }

    /**
     * Sets the value of the materialSupplierMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialSupplierMDMId(String value) {
        this.materialSupplierMDMId = value;
    }

    /**
     * Gets the value of the materialSupplierPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialSupplierPLMId() {
        return materialSupplierPLMId;
    }

    /**
     * Sets the value of the materialSupplierPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialSupplierPLMId(String value) {
        this.materialSupplierPLMId = value;
    }

    /**
     * Gets the value of the colorType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorType() {
        return colorType;
    }

    /**
     * Sets the value of the colorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorType(String value) {
        this.colorType = value;
    }

    /**
     * Gets the value of the colorProviderCatalog property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorProviderCatalog() {
        return colorProviderCatalog;
    }

    /**
     * Sets the value of the colorProviderCatalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorProviderCatalog(String value) {
        this.colorProviderCatalog = value;
    }

    /**
     * Gets the value of the colorStandardRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorStandardRef() {
        return colorStandardRef;
    }

    /**
     * Sets the value of the colorStandardRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorStandardRef(String value) {
        this.colorStandardRef = value;
    }

    /**
     * Gets the value of the colorArtwork property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorArtwork() {
        return colorArtwork;
    }

    /**
     * Sets the value of the colorArtwork property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorArtwork(String value) {
        this.colorArtwork = value;
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

}

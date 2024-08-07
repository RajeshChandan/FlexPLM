
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ProductBOM complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductBOM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BOMName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BOMStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smOrderDestination" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="colorwayMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwayPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwayColor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwayColorMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwaySeasonMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwaySeasonPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="businessSupplierMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="businessSupplierPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="businessSupplierName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="factoryMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="factoryPLMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="factoryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="productBOMComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}ProductBOMComponent" maxOccurs="unbounded"/>
 *         &lt;element name="BOMPartBranchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductBOM", propOrder = {
    "bomName",
    "bomStatus",
    "smOrderDestination",
    "colorwayMDMId",
    "colorwayPLMId",
    "colorwayColor",
    "colorwayColorMDMId",
    "colorwaySeasonMDMId",
    "colorwaySeasonPLMId",
    "businessSupplierMDMId",
    "businessSupplierPLMId",
    "businessSupplierName",
    "factoryMDMId",
    "factoryPLMId",
    "factoryName",
    "lastUpdated",
    "productBOMComponent",
    "bomPartBranchID"
})
public class ProductBOM {

    @XmlElement(name = "BOMName", required = true)
    protected String bomName;
    @XmlElement(name = "BOMStatus", required = true)
    protected String bomStatus;
    @XmlElement(required = true)
    protected List<String> smOrderDestination;
    @XmlElement(required = true)
    protected String colorwayMDMId;
    @XmlElement(required = true)
    protected String colorwayPLMId;
    @XmlElement(required = true)
    protected String colorwayColor;
    @XmlElement(required = true)
    protected String colorwayColorMDMId;
    @XmlElement(required = true)
    protected String colorwaySeasonMDMId;
    @XmlElement(required = true)
    protected String colorwaySeasonPLMId;
    @XmlElement(required = true)
    protected String businessSupplierMDMId;
    @XmlElement(required = true)
    protected String businessSupplierPLMId;
    @XmlElement(required = true)
    protected String businessSupplierName;
    protected String factoryMDMId;
    protected String factoryPLMId;
    protected String factoryName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    @XmlElement(required = true)
    protected List<ProductBOMComponent> productBOMComponent;
    @XmlElement(name = "BOMPartBranchID", required = true)
    protected String bomPartBranchID;

    /**
     * Gets the value of the bomName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBOMName() {
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
    public void setBOMName(String value) {
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
    public String getBOMStatus() {
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
    public void setBOMStatus(String value) {
        this.bomStatus = value;
    }

    /**
     * Gets the value of the smOrderDestination property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smOrderDestination property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmOrderDestination().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmOrderDestination() {
        if (smOrderDestination == null) {
            smOrderDestination = new ArrayList<String>();
        }
        return this.smOrderDestination;
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
     * Gets the value of the colorwayColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwayColor() {
        return colorwayColor;
    }

    /**
     * Sets the value of the colorwayColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwayColor(String value) {
        this.colorwayColor = value;
    }

    /**
     * Gets the value of the colorwayColorMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwayColorMDMId() {
        return colorwayColorMDMId;
    }

    /**
     * Sets the value of the colorwayColorMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwayColorMDMId(String value) {
        this.colorwayColorMDMId = value;
    }

    /**
     * Gets the value of the colorwaySeasonMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwaySeasonMDMId() {
        return colorwaySeasonMDMId;
    }

    /**
     * Sets the value of the colorwaySeasonMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwaySeasonMDMId(String value) {
        this.colorwaySeasonMDMId = value;
    }

    /**
     * Gets the value of the colorwaySeasonPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwaySeasonPLMId() {
        return colorwaySeasonPLMId;
    }

    /**
     * Sets the value of the colorwaySeasonPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwaySeasonPLMId(String value) {
        this.colorwaySeasonPLMId = value;
    }

    /**
     * Gets the value of the businessSupplierMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessSupplierMDMId() {
        return businessSupplierMDMId;
    }

    /**
     * Sets the value of the businessSupplierMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessSupplierMDMId(String value) {
        this.businessSupplierMDMId = value;
    }

    /**
     * Gets the value of the businessSupplierPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessSupplierPLMId() {
        return businessSupplierPLMId;
    }

    /**
     * Sets the value of the businessSupplierPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessSupplierPLMId(String value) {
        this.businessSupplierPLMId = value;
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
     * Gets the value of the factoryMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryMDMId() {
        return factoryMDMId;
    }

    /**
     * Sets the value of the factoryMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryMDMId(String value) {
        this.factoryMDMId = value;
    }

    /**
     * Gets the value of the factoryPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryPLMId() {
        return factoryPLMId;
    }

    /**
     * Sets the value of the factoryPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryPLMId(String value) {
        this.factoryPLMId = value;
    }

    /**
     * Gets the value of the factoryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryName() {
        return factoryName;
    }

    /**
     * Sets the value of the factoryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryName(String value) {
        this.factoryName = value;
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
     * Gets the value of the productBOMComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productBOMComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductBOMComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductBOMComponent }
     * 
     * 
     */
    public List<ProductBOMComponent> getProductBOMComponent() {
        if (productBOMComponent == null) {
            productBOMComponent = new ArrayList<ProductBOMComponent>();
        }
        return this.productBOMComponent;
    }

    /**
     * Gets the value of the bomPartBranchID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBOMPartBranchID() {
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
    public void setBOMPartBranchID(String value) {
        this.bomPartBranchID = value;
    }

}

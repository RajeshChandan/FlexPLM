
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
 * <p>Java class for SourcingConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SourcingConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smSCDestination" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smMDMProductSeasonLink" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smMDMSeason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smMDMVendor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorwaySourcingBoolean" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vrdSourcingStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smPrimarySourceForSeason" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smMDMColorway" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smMDMFactory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productCostSheet" type="{http://www.sportmaster.ru/plmproduct}ProductCostSheet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourcingConfig", propOrder = {
    "plmId",
    "smSCDestination",
    "smMDMProductSeasonLink",
    "smMDMSeason",
    "smMDMVendor",
    "colorwaySourcingBoolean",
    "vrdSourcingStatus",
    "smPrimarySourceForSeason",
    "smMDMColorway",
    "smMDMFactory",
    "productCostSheet",
    "createdOn",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy"
})
public class SourcingConfig {

    @XmlElement(required = true)
    protected String plmId;
    protected List<String> smSCDestination;
    @XmlElement(required = true)
    protected String smMDMProductSeasonLink;
    @XmlElement(required = true)
    protected String smMDMSeason;
    @XmlElement(required = true)
    protected String smMDMVendor;
    protected List<String> colorwaySourcingBoolean;
    @XmlElement(required = true)
    protected String vrdSourcingStatus;
    protected boolean smPrimarySourceForSeason;
    protected List<String> smMDMColorway;
    protected String smMDMFactory;
    protected List<ProductCostSheet> productCostSheet;
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
     * Gets the value of the smSCDestination property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smSCDestination property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmSCDestination().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmSCDestination() {
        if (smSCDestination == null) {
            smSCDestination = new ArrayList<String>();
        }
        return this.smSCDestination;
    }

    /**
     * Gets the value of the smMDMProductSeasonLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMProductSeasonLink() {
        return smMDMProductSeasonLink;
    }

    /**
     * Sets the value of the smMDMProductSeasonLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMProductSeasonLink(String value) {
        this.smMDMProductSeasonLink = value;
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
     * Gets the value of the smMDMVendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMVendor() {
        return smMDMVendor;
    }

    /**
     * Sets the value of the smMDMVendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMVendor(String value) {
        this.smMDMVendor = value;
    }

    /**
     * Gets the value of the colorwaySourcingBoolean property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorwaySourcingBoolean property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorwaySourcingBoolean().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getColorwaySourcingBoolean() {
        if (colorwaySourcingBoolean == null) {
            colorwaySourcingBoolean = new ArrayList<String>();
        }
        return this.colorwaySourcingBoolean;
    }

    /**
     * Gets the value of the vrdSourcingStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdSourcingStatus() {
        return vrdSourcingStatus;
    }

    /**
     * Sets the value of the vrdSourcingStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdSourcingStatus(String value) {
        this.vrdSourcingStatus = value;
    }

    /**
     * Gets the value of the smPrimarySourceForSeason property.
     * 
     */
    public boolean isSmPrimarySourceForSeason() {
        return smPrimarySourceForSeason;
    }

    /**
     * Sets the value of the smPrimarySourceForSeason property.
     * 
     */
    public void setSmPrimarySourceForSeason(boolean value) {
        this.smPrimarySourceForSeason = value;
    }

    /**
     * Gets the value of the smMDMColorway property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smMDMColorway property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmMDMColorway().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmMDMColorway() {
        if (smMDMColorway == null) {
            smMDMColorway = new ArrayList<String>();
        }
        return this.smMDMColorway;
    }

    /**
     * Gets the value of the smMDMFactory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMFactory() {
        return smMDMFactory;
    }

    /**
     * Sets the value of the smMDMFactory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMFactory(String value) {
        this.smMDMFactory = value;
    }

    /**
     * Gets the value of the productCostSheet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productCostSheet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductCostSheet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductCostSheet }
     * 
     * 
     */
    public List<ProductCostSheet> getProductCostSheet() {
        if (productCostSheet == null) {
            productCostSheet = new ArrayList<ProductCostSheet>();
        }
        return this.productCostSheet;
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

}

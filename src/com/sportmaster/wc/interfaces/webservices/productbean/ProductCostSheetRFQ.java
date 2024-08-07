
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ProductCostSheetRFQ complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductCostSheetRFQ">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="whatIf" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="quoteVendor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pricingCountry" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="rfq" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="quote" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smMDMColorway" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smMDMSeason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smQuotedPriceGSCurr" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="smFOBTotalIntGSCurr" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="smCsContractCurrency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smCsIncoterms" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinations" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smCostingStage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vrdCSStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="primaryCostSheet" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "ProductCostSheetRFQ", propOrder = {
    "name",
    "plmId",
    "whatIf",
    "quoteVendor",
    "pricingCountry",
    "rfq",
    "quote",
    "smMDMColorway",
    "smMDMSeason",
    "smQuotedPriceGSCurr",
    "smFOBTotalIntGSCurr",
    "smCsContractCurrency",
    "smCsIncoterms",
    "destinations",
    "smCostingStage",
    "vrdCSStatus",
    "primaryCostSheet",
    "createdOn",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy"
})
public class ProductCostSheetRFQ {

    protected String name;
    @XmlElement(required = true)
    protected String plmId;
    protected boolean whatIf;
    @XmlElement(required = true)
    protected String quoteVendor;
    @XmlElement(required = true)
    protected String pricingCountry;
    @XmlElement(required = true)
    protected String rfq;
    @XmlElement(required = true)
    protected String quote;
    protected List<String> smMDMColorway;
    @XmlElement(required = true)
    protected String smMDMSeason;
    protected BigDecimal smQuotedPriceGSCurr;
    protected BigDecimal smFOBTotalIntGSCurr;
    @XmlElement(required = true)
    protected String smCsContractCurrency;
    @XmlElement(required = true)
    protected String smCsIncoterms;
    protected List<String> destinations;
    @XmlElement(required = true)
    protected String smCostingStage;
    @XmlElement(required = true)
    protected String vrdCSStatus;
    @XmlElement(required = true)
    protected String primaryCostSheet;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Gets the value of the whatIf property.
     * 
     */
    public boolean isWhatIf() {
        return whatIf;
    }

    /**
     * Sets the value of the whatIf property.
     * 
     */
    public void setWhatIf(boolean value) {
        this.whatIf = value;
    }

    /**
     * Gets the value of the quoteVendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteVendor() {
        return quoteVendor;
    }

    /**
     * Sets the value of the quoteVendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteVendor(String value) {
        this.quoteVendor = value;
    }

    /**
     * Gets the value of the pricingCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPricingCountry() {
        return pricingCountry;
    }

    /**
     * Sets the value of the pricingCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPricingCountry(String value) {
        this.pricingCountry = value;
    }

    /**
     * Gets the value of the rfq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfq() {
        return rfq;
    }

    /**
     * Sets the value of the rfq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfq(String value) {
        this.rfq = value;
    }

    /**
     * Gets the value of the quote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuote() {
        return quote;
    }

    /**
     * Sets the value of the quote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuote(String value) {
        this.quote = value;
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
     * Gets the value of the smQuotedPriceGSCurr property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSmQuotedPriceGSCurr() {
        return smQuotedPriceGSCurr;
    }

    /**
     * Sets the value of the smQuotedPriceGSCurr property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSmQuotedPriceGSCurr(BigDecimal value) {
        this.smQuotedPriceGSCurr = value;
    }

    /**
     * Gets the value of the smFOBTotalIntGSCurr property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSmFOBTotalIntGSCurr() {
        return smFOBTotalIntGSCurr;
    }

    /**
     * Sets the value of the smFOBTotalIntGSCurr property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSmFOBTotalIntGSCurr(BigDecimal value) {
        this.smFOBTotalIntGSCurr = value;
    }

    /**
     * Gets the value of the smCsContractCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCsContractCurrency() {
        return smCsContractCurrency;
    }

    /**
     * Sets the value of the smCsContractCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCsContractCurrency(String value) {
        this.smCsContractCurrency = value;
    }

    /**
     * Gets the value of the smCsIncoterms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCsIncoterms() {
        return smCsIncoterms;
    }

    /**
     * Sets the value of the smCsIncoterms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCsIncoterms(String value) {
        this.smCsIncoterms = value;
    }

    /**
     * Gets the value of the destinations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDestinations() {
        if (destinations == null) {
            destinations = new ArrayList<String>();
        }
        return this.destinations;
    }

    /**
     * Gets the value of the smCostingStage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCostingStage() {
        return smCostingStage;
    }

    /**
     * Sets the value of the smCostingStage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCostingStage(String value) {
        this.smCostingStage = value;
    }

    /**
     * Gets the value of the vrdCSStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdCSStatus() {
        return vrdCSStatus;
    }

    /**
     * Sets the value of the vrdCSStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdCSStatus(String value) {
        this.vrdCSStatus = value;
    }

    /**
     * Gets the value of the primaryCostSheet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryCostSheet() {
        return primaryCostSheet;
    }

    /**
     * Sets the value of the primaryCostSheet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryCostSheet(String value) {
        this.primaryCostSheet = value;
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

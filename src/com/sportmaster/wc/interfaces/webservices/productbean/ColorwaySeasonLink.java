
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
 * <p>Java class for ColorwaySeasonLink complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonLink">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lifeCycleState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smRetailDestinationSync" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="smMDMColorway" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmIdColorway" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smMDMSeason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smCommentsColorway" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smFlow" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smManualHighPriority" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smColorwaySeasonStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smLongLeadTimeCW" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smInitialForecastCW" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="smEarlyBuy" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="type1Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type2Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smSEPDDevelopmentNewness" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smProductPlannedAssemblyType" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MktgImageStyle" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smHighPriority" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "ColorwaySeasonLink", propOrder = {
    "mdmId",
    "objectType",
    "plmId",
    "lifeCycleState",
    "smRetailDestinationSync",
    "smMDMColorway",
    "plmIdColorway",
    "smMDMSeason",
    "smCommentsColorway",
    "smFlow",
    "smManualHighPriority",
    "smColorwaySeasonStatus",
    "smLongLeadTimeCW",
    "createdOn",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy",
    "smInitialForecastCW",
    "smEarlyBuy",
    "type1Level",
    "type2Level",
    "smSEPDDevelopmentNewness",
    "smProductPlannedAssemblyType",
    "mktgImageStyle",
    "smHighPriority",
    "smUserIntTriggered",
    "smLastIntTriggered"
})
public class ColorwaySeasonLink {

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
    @XmlElement(required = true)
    protected String smMDMColorway;
    @XmlElement(required = true)
    protected String plmIdColorway;
    @XmlElement(required = true)
    protected String smMDMSeason;
    protected String smCommentsColorway;
    protected String smFlow;
    protected boolean smManualHighPriority;
    @XmlElement(required = true)
    protected String smColorwaySeasonStatus;
    protected boolean smLongLeadTimeCW;
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
    protected Integer smInitialForecastCW;
    protected Boolean smEarlyBuy;
    @XmlElement(required = true)
    protected String type1Level;
    @XmlElement(required = true)
    protected String type2Level;
    protected String smSEPDDevelopmentNewness;
    protected List<String> smProductPlannedAssemblyType;
    @XmlElement(name = "MktgImageStyle")
    protected boolean mktgImageStyle;
    protected boolean smHighPriority;
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
     * Gets the value of the smMDMColorway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMColorway() {
        return smMDMColorway;
    }

    /**
     * Sets the value of the smMDMColorway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMColorway(String value) {
        this.smMDMColorway = value;
    }

    /**
     * Gets the value of the plmIdColorway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlmIdColorway() {
        return plmIdColorway;
    }

    /**
     * Sets the value of the plmIdColorway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlmIdColorway(String value) {
        this.plmIdColorway = value;
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
     * Gets the value of the smCommentsColorway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmCommentsColorway() {
        return smCommentsColorway;
    }

    /**
     * Sets the value of the smCommentsColorway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmCommentsColorway(String value) {
        this.smCommentsColorway = value;
    }

    /**
     * Gets the value of the smFlow property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmFlow() {
        return smFlow;
    }

    /**
     * Sets the value of the smFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmFlow(String value) {
        this.smFlow = value;
    }

    /**
     * Gets the value of the smManualHighPriority property.
     * 
     */
    public boolean isSmManualHighPriority() {
        return smManualHighPriority;
    }

    /**
     * Sets the value of the smManualHighPriority property.
     * 
     */
    public void setSmManualHighPriority(boolean value) {
        this.smManualHighPriority = value;
    }

    /**
     * Gets the value of the smColorwaySeasonStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmColorwaySeasonStatus() {
        return smColorwaySeasonStatus;
    }

    /**
     * Sets the value of the smColorwaySeasonStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmColorwaySeasonStatus(String value) {
        this.smColorwaySeasonStatus = value;
    }

    /**
     * Gets the value of the smLongLeadTimeCW property.
     * 
     */
    public boolean isSmLongLeadTimeCW() {
        return smLongLeadTimeCW;
    }

    /**
     * Sets the value of the smLongLeadTimeCW property.
     * 
     */
    public void setSmLongLeadTimeCW(boolean value) {
        this.smLongLeadTimeCW = value;
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
     * Gets the value of the smInitialForecastCW property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSmInitialForecastCW() {
        return smInitialForecastCW;
    }

    /**
     * Sets the value of the smInitialForecastCW property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSmInitialForecastCW(Integer value) {
        this.smInitialForecastCW = value;
    }

    /**
     * Gets the value of the smEarlyBuy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmEarlyBuy() {
        return smEarlyBuy;
    }

    /**
     * Sets the value of the smEarlyBuy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmEarlyBuy(Boolean value) {
        this.smEarlyBuy = value;
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
     * Gets the value of the smSEPDDevelopmentNewness property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSEPDDevelopmentNewness() {
        return smSEPDDevelopmentNewness;
    }

    /**
     * Sets the value of the smSEPDDevelopmentNewness property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSEPDDevelopmentNewness(String value) {
        this.smSEPDDevelopmentNewness = value;
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
     * Gets the value of the mktgImageStyle property.
     * 
     */
    public boolean isMktgImageStyle() {
        return mktgImageStyle;
    }

    /**
     * Sets the value of the mktgImageStyle property.
     * 
     */
    public void setMktgImageStyle(boolean value) {
        this.mktgImageStyle = value;
    }

    /**
     * Gets the value of the smHighPriority property.
     * 
     */
    public boolean isSmHighPriority() {
        return smHighPriority;
    }

    /**
     * Sets the value of the smHighPriority property.
     * 
     */
    public void setSmHighPriority(boolean value) {
        this.smHighPriority = value;
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


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
 * <p>Java class for Product complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Product">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="objectType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lifeCycleState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smMacrobrand" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vrdBrand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smStyleNameRu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vrdDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smStyleCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smProject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smAge" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vrdStyleNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smStyleAnalogue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vrdGender" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vrdMaterialGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMDMDIV" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smFit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="thumbnail" type="{http://www.sportmaster.ru/plmproduct}Thumbnail" minOccurs="0"/>
 *         &lt;element name="createdOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="colorway" type="{http://www.sportmaster.ru/plmproduct}Colorway" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="productSeasonLink" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLink" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="type1Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type2Level" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smSEPDSpecificOfProduct" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "Product", propOrder = {
    "mdmId",
    "objectType",
    "plmId",
    "lifeCycleState",
    "smMacrobrand",
    "vrdBrand",
    "smStyleNameRu",
    "vrdDescription",
    "smStyleCode",
    "smProject",
    "smAge",
    "vrdStyleNum",
    "smStyleAnalogue",
    "vrdGender",
    "vrdMaterialGroup",
    "smMDMDIV",
    "smFit",
    "thumbnail",
    "createdOn",
    "createdBy",
    "lastUpdated",
    "lastUpdatedBy",
    "colorway",
    "productSeasonLink",
    "type1Level",
    "type2Level",
    "smSEPDSpecificOfProduct",
    "smUserIntTriggered",
    "smLastIntTriggered"
})
public class Product {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String objectType;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String lifeCycleState;
    protected String smMacrobrand;
    @XmlElement(required = true)
    protected String vrdBrand;
    protected String smStyleNameRu;
    protected String vrdDescription;
    protected String smStyleCode;
    protected String smProject;
    @XmlElement(required = true)
    protected String smAge;
    @XmlElement(required = true)
    protected String vrdStyleNum;
    protected String smStyleAnalogue;
    @XmlElement(required = true)
    protected String vrdGender;
    protected String vrdMaterialGroup;
    @XmlElement(required = true)
    protected String smMDMDIV;
    protected String smFit;
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
    protected List<Colorway> colorway;
    protected List<ProductSeasonLink> productSeasonLink;
    @XmlElement(required = true)
    protected String type1Level;
    @XmlElement(required = true)
    protected String type2Level;
    protected String smSEPDSpecificOfProduct;
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
     * Gets the value of the smMacrobrand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMacrobrand() {
        return smMacrobrand;
    }

    /**
     * Sets the value of the smMacrobrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMacrobrand(String value) {
        this.smMacrobrand = value;
    }

    /**
     * Gets the value of the vrdBrand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdBrand() {
        return vrdBrand;
    }

    /**
     * Sets the value of the vrdBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdBrand(String value) {
        this.vrdBrand = value;
    }

    /**
     * Gets the value of the smStyleNameRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmStyleNameRu() {
        return smStyleNameRu;
    }

    /**
     * Sets the value of the smStyleNameRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmStyleNameRu(String value) {
        this.smStyleNameRu = value;
    }

    /**
     * Gets the value of the vrdDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdDescription() {
        return vrdDescription;
    }

    /**
     * Sets the value of the vrdDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdDescription(String value) {
        this.vrdDescription = value;
    }

    /**
     * Gets the value of the smStyleCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmStyleCode() {
        return smStyleCode;
    }

    /**
     * Sets the value of the smStyleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmStyleCode(String value) {
        this.smStyleCode = value;
    }

    /**
     * Gets the value of the smProject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProject() {
        return smProject;
    }

    /**
     * Sets the value of the smProject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProject(String value) {
        this.smProject = value;
    }

    /**
     * Gets the value of the smAge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmAge() {
        return smAge;
    }

    /**
     * Sets the value of the smAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmAge(String value) {
        this.smAge = value;
    }

    /**
     * Gets the value of the vrdStyleNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdStyleNum() {
        return vrdStyleNum;
    }

    /**
     * Sets the value of the vrdStyleNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdStyleNum(String value) {
        this.vrdStyleNum = value;
    }

    /**
     * Gets the value of the smStyleAnalogue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmStyleAnalogue() {
        return smStyleAnalogue;
    }

    /**
     * Sets the value of the smStyleAnalogue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmStyleAnalogue(String value) {
        this.smStyleAnalogue = value;
    }

    /**
     * Gets the value of the vrdGender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdGender() {
        return vrdGender;
    }

    /**
     * Sets the value of the vrdGender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdGender(String value) {
        this.vrdGender = value;
    }

    /**
     * Gets the value of the vrdMaterialGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdMaterialGroup() {
        return vrdMaterialGroup;
    }

    /**
     * Sets the value of the vrdMaterialGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdMaterialGroup(String value) {
        this.vrdMaterialGroup = value;
    }

    /**
     * Gets the value of the smMDMDIV property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMDIV() {
        return smMDMDIV;
    }

    /**
     * Sets the value of the smMDMDIV property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMDIV(String value) {
        this.smMDMDIV = value;
    }

    /**
     * Gets the value of the smFit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmFit() {
        return smFit;
    }

    /**
     * Sets the value of the smFit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmFit(String value) {
        this.smFit = value;
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
     * Gets the value of the colorway property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorway property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorway().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Colorway }
     * 
     * 
     */
    public List<Colorway> getColorway() {
        if (colorway == null) {
            colorway = new ArrayList<Colorway>();
        }
        return this.colorway;
    }

    /**
     * Gets the value of the productSeasonLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSeasonLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSeasonLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductSeasonLink }
     * 
     * 
     */
    public List<ProductSeasonLink> getProductSeasonLink() {
        if (productSeasonLink == null) {
            productSeasonLink = new ArrayList<ProductSeasonLink>();
        }
        return this.productSeasonLink;
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
     * Gets the value of the smSEPDSpecificOfProduct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSEPDSpecificOfProduct() {
        return smSEPDSpecificOfProduct;
    }

    /**
     * Sets the value of the smSEPDSpecificOfProduct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSEPDSpecificOfProduct(String value) {
        this.smSEPDSpecificOfProduct = value;
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

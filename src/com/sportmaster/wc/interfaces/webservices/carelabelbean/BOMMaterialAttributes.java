
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BOMMaterialAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BOMMaterialAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="materialMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="materialPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialSupplierMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="materialSupplierPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smLaminationCoating" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vrdFabricFinish" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="smAdditionalCareMC" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="unitOfMeasure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="managingDepartment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="composition" type="{http://www.sportmaster.ru/plm-services-careLabel}Composition" minOccurs="0"/>
 *         &lt;element name="compositionRU" type="{http://www.sportmaster.ru/plm-services-careLabel}CompositionRU" minOccurs="0"/>
 *         &lt;element name="careWash" type="{http://www.sportmaster.ru/plm-services-careLabel}CareWash" minOccurs="0"/>
 *         &lt;element name="layer1Composition" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer1Composition" minOccurs="0"/>
 *         &lt;element name="layer2Composition" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer2Composition" minOccurs="0"/>
 *         &lt;element name="layer1CompositionRU" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer1CompositionRU" minOccurs="0"/>
 *         &lt;element name="layer2CompositionRU" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer2CompositionRU" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BOMMaterialAttributes", propOrder = {
    "materialMDMId",
    "materialPLMId",
    "materialName",
    "materialType",
    "materialSupplierMDMId",
    "materialSupplierPLMId",
    "smLaminationCoating",
    "vrdFabricFinish",
    "smAdditionalCareMC",
    "unitOfMeasure",
    "managingDepartment",
    "composition",
    "compositionRU",
    "careWash",
    "layer1Composition",
    "layer2Composition",
    "layer1CompositionRU",
    "layer2CompositionRU"
})
public class BOMMaterialAttributes {

    protected String materialMDMId;
    @XmlElement(required = true)
    protected String materialPLMId;
    @XmlElement(required = true)
    protected String materialName;
    @XmlElement(required = true)
    protected String materialType;
    @XmlElement(required = true)
    protected String materialSupplierMDMId;
    @XmlElement(required = true)
    protected String materialSupplierPLMId;
    protected List<String> smLaminationCoating;
    protected List<String> vrdFabricFinish;
    protected List<String> smAdditionalCareMC;
    protected String unitOfMeasure;
    protected String managingDepartment;
    protected Composition composition;
    protected CompositionRU compositionRU;
    protected CareWash careWash;
    protected Layer1Composition layer1Composition;
    protected Layer2Composition layer2Composition;
    protected Layer1CompositionRU layer1CompositionRU;
    protected Layer2CompositionRU layer2CompositionRU;

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
     * Gets the value of the smLaminationCoating property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smLaminationCoating property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmLaminationCoating().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmLaminationCoating() {
        if (smLaminationCoating == null) {
            smLaminationCoating = new ArrayList<String>();
        }
        return this.smLaminationCoating;
    }

    /**
     * Gets the value of the vrdFabricFinish property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vrdFabricFinish property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVrdFabricFinish().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getVrdFabricFinish() {
        if (vrdFabricFinish == null) {
            vrdFabricFinish = new ArrayList<String>();
        }
        return this.vrdFabricFinish;
    }

    /**
     * Gets the value of the smAdditionalCareMC property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the smAdditionalCareMC property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSmAdditionalCareMC().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSmAdditionalCareMC() {
        if (smAdditionalCareMC == null) {
            smAdditionalCareMC = new ArrayList<String>();
        }
        return this.smAdditionalCareMC;
    }

    /**
     * Gets the value of the unitOfMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Sets the value of the unitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setUnitOfMeasure(String value) {
        this.unitOfMeasure = value;
    }

    /**
     * Gets the value of the managingDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagingDepartment() {
        return managingDepartment;
    }

    /**
     * Sets the value of the managingDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagingDepartment(String value) {
        this.managingDepartment = value;
    }

    /**
     * Gets the value of the composition property.
     * 
     * @return
     *     possible object is
     *     {@link Composition }
     *     
     */
    public Composition getComposition() {
        return composition;
    }

    /**
     * Sets the value of the composition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Composition }
     *     
     */
    public void setComposition(Composition value) {
        this.composition = value;
    }

    /**
     * Gets the value of the compositionRU property.
     * 
     * @return
     *     possible object is
     *     {@link CompositionRU }
     *     
     */
    public CompositionRU getCompositionRU() {
        return compositionRU;
    }

    /**
     * Sets the value of the compositionRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositionRU }
     *     
     */
    public void setCompositionRU(CompositionRU value) {
        this.compositionRU = value;
    }

    /**
     * Gets the value of the careWash property.
     * 
     * @return
     *     possible object is
     *     {@link CareWash }
     *     
     */
    public CareWash getCareWash() {
        return careWash;
    }

    /**
     * Sets the value of the careWash property.
     * 
     * @param value
     *     allowed object is
     *     {@link CareWash }
     *     
     */
    public void setCareWash(CareWash value) {
        this.careWash = value;
    }

    /**
     * Gets the value of the layer1Composition property.
     * 
     * @return
     *     possible object is
     *     {@link Layer1Composition }
     *     
     */
    public Layer1Composition getLayer1Composition() {
        return layer1Composition;
    }

    /**
     * Sets the value of the layer1Composition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Layer1Composition }
     *     
     */
    public void setLayer1Composition(Layer1Composition value) {
        this.layer1Composition = value;
    }

    /**
     * Gets the value of the layer2Composition property.
     * 
     * @return
     *     possible object is
     *     {@link Layer2Composition }
     *     
     */
    public Layer2Composition getLayer2Composition() {
        return layer2Composition;
    }

    /**
     * Sets the value of the layer2Composition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Layer2Composition }
     *     
     */
    public void setLayer2Composition(Layer2Composition value) {
        this.layer2Composition = value;
    }

    /**
     * Gets the value of the layer1CompositionRU property.
     * 
     * @return
     *     possible object is
     *     {@link Layer1CompositionRU }
     *     
     */
    public Layer1CompositionRU getLayer1CompositionRU() {
        return layer1CompositionRU;
    }

    /**
     * Sets the value of the layer1CompositionRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link Layer1CompositionRU }
     *     
     */
    public void setLayer1CompositionRU(Layer1CompositionRU value) {
        this.layer1CompositionRU = value;
    }

    /**
     * Gets the value of the layer2CompositionRU property.
     * 
     * @return
     *     possible object is
     *     {@link Layer2CompositionRU }
     *     
     */
    public Layer2CompositionRU getLayer2CompositionRU() {
        return layer2CompositionRU;
    }

    /**
     * Sets the value of the layer2CompositionRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link Layer2CompositionRU }
     *     
     */
    public void setLayer2CompositionRU(Layer2CompositionRU value) {
        this.layer2CompositionRU = value;
    }

}

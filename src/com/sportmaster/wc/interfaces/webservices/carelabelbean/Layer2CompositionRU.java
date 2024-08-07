
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer2CompositionRU complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer2CompositionRU">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer2RUMaterialMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="layer2RUMaterialPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer2RUMaterialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer2CompositionRUComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer2CompositionRUComponent" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer2CompositionRU", propOrder = {
    "layer2RUMaterialMDMId",
    "layer2RUMaterialPLMId",
    "layer2RUMaterialName",
    "layer2CompositionRUComponent"
})
public class Layer2CompositionRU {

    protected String layer2RUMaterialMDMId;
    @XmlElement(required = true)
    protected String layer2RUMaterialPLMId;
    @XmlElement(required = true)
    protected String layer2RUMaterialName;
    @XmlElement(required = true)
    protected List<Layer2CompositionRUComponent> layer2CompositionRUComponent;

    /**
     * Gets the value of the layer2RUMaterialMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2RUMaterialMDMId() {
        return layer2RUMaterialMDMId;
    }

    /**
     * Sets the value of the layer2RUMaterialMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2RUMaterialMDMId(String value) {
        this.layer2RUMaterialMDMId = value;
    }

    /**
     * Gets the value of the layer2RUMaterialPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2RUMaterialPLMId() {
        return layer2RUMaterialPLMId;
    }

    /**
     * Sets the value of the layer2RUMaterialPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2RUMaterialPLMId(String value) {
        this.layer2RUMaterialPLMId = value;
    }

    /**
     * Gets the value of the layer2RUMaterialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2RUMaterialName() {
        return layer2RUMaterialName;
    }

    /**
     * Sets the value of the layer2RUMaterialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2RUMaterialName(String value) {
        this.layer2RUMaterialName = value;
    }

    /**
     * Gets the value of the layer2CompositionRUComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the layer2CompositionRUComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLayer2CompositionRUComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Layer2CompositionRUComponent }
     * 
     * 
     */
    public List<Layer2CompositionRUComponent> getLayer2CompositionRUComponent() {
        if (layer2CompositionRUComponent == null) {
            layer2CompositionRUComponent = new ArrayList<Layer2CompositionRUComponent>();
        }
        return this.layer2CompositionRUComponent;
    }

}


package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer1CompositionRU complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer1CompositionRU">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer1RUMaterialMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="layer1RUMaterialPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer1RUMaterialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer1CompositionRUComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer1CompositionRUComponent" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer1CompositionRU", propOrder = {
    "layer1RUMaterialMDMId",
    "layer1RUMaterialPLMId",
    "layer1RUMaterialName",
    "layer1CompositionRUComponent"
})
public class Layer1CompositionRU {

    protected String layer1RUMaterialMDMId;
    @XmlElement(required = true)
    protected String layer1RUMaterialPLMId;
    @XmlElement(required = true)
    protected String layer1RUMaterialName;
    @XmlElement(required = true)
    protected List<Layer1CompositionRUComponent> layer1CompositionRUComponent;

    /**
     * Gets the value of the layer1RUMaterialMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1RUMaterialMDMId() {
        return layer1RUMaterialMDMId;
    }

    /**
     * Sets the value of the layer1RUMaterialMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1RUMaterialMDMId(String value) {
        this.layer1RUMaterialMDMId = value;
    }

    /**
     * Gets the value of the layer1RUMaterialPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1RUMaterialPLMId() {
        return layer1RUMaterialPLMId;
    }

    /**
     * Sets the value of the layer1RUMaterialPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1RUMaterialPLMId(String value) {
        this.layer1RUMaterialPLMId = value;
    }

    /**
     * Gets the value of the layer1RUMaterialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1RUMaterialName() {
        return layer1RUMaterialName;
    }

    /**
     * Sets the value of the layer1RUMaterialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1RUMaterialName(String value) {
        this.layer1RUMaterialName = value;
    }

    /**
     * Gets the value of the layer1CompositionRUComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the layer1CompositionRUComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLayer1CompositionRUComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Layer1CompositionRUComponent }
     * 
     * 
     */
    public List<Layer1CompositionRUComponent> getLayer1CompositionRUComponent() {
        if (layer1CompositionRUComponent == null) {
            layer1CompositionRUComponent = new ArrayList<Layer1CompositionRUComponent>();
        }
        return this.layer1CompositionRUComponent;
    }

}

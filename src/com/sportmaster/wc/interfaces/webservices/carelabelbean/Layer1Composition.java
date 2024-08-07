
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer1Composition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer1Composition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer1MaterialMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="layer1MaterialPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer1MaterialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer1CompositionComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer1CompositionComponent" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer1Composition", propOrder = {
    "layer1MaterialMDMId",
    "layer1MaterialPLMId",
    "layer1MaterialName",
    "layer1CompositionComponent"
})
public class Layer1Composition {

    protected String layer1MaterialMDMId;
    @XmlElement(required = true)
    protected String layer1MaterialPLMId;
    @XmlElement(required = true)
    protected String layer1MaterialName;
    @XmlElement(required = true)
    protected List<Layer1CompositionComponent> layer1CompositionComponent;

    /**
     * Gets the value of the layer1MaterialMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1MaterialMDMId() {
        return layer1MaterialMDMId;
    }

    /**
     * Sets the value of the layer1MaterialMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1MaterialMDMId(String value) {
        this.layer1MaterialMDMId = value;
    }

    /**
     * Gets the value of the layer1MaterialPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1MaterialPLMId() {
        return layer1MaterialPLMId;
    }

    /**
     * Sets the value of the layer1MaterialPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1MaterialPLMId(String value) {
        this.layer1MaterialPLMId = value;
    }

    /**
     * Gets the value of the layer1MaterialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1MaterialName() {
        return layer1MaterialName;
    }

    /**
     * Sets the value of the layer1MaterialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1MaterialName(String value) {
        this.layer1MaterialName = value;
    }

    /**
     * Gets the value of the layer1CompositionComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the layer1CompositionComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLayer1CompositionComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Layer1CompositionComponent }
     * 
     * 
     */
    public List<Layer1CompositionComponent> getLayer1CompositionComponent() {
        if (layer1CompositionComponent == null) {
            layer1CompositionComponent = new ArrayList<Layer1CompositionComponent>();
        }
        return this.layer1CompositionComponent;
    }

}

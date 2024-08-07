
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer2Composition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer2Composition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer2MaterialMDMId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="layer2MaterialPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer2MaterialName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="layer2CompositionComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}Layer2CompositionComponent" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer2Composition", propOrder = {
    "layer2MaterialMDMId",
    "layer2MaterialPLMId",
    "layer2MaterialName",
    "layer2CompositionComponent"
})
public class Layer2Composition {

    protected String layer2MaterialMDMId;
    @XmlElement(required = true)
    protected String layer2MaterialPLMId;
    @XmlElement(required = true)
    protected String layer2MaterialName;
    @XmlElement(required = true)
    protected List<Layer2CompositionComponent> layer2CompositionComponent;

    /**
     * Gets the value of the layer2MaterialMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2MaterialMDMId() {
        return layer2MaterialMDMId;
    }

    /**
     * Sets the value of the layer2MaterialMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2MaterialMDMId(String value) {
        this.layer2MaterialMDMId = value;
    }

    /**
     * Gets the value of the layer2MaterialPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2MaterialPLMId() {
        return layer2MaterialPLMId;
    }

    /**
     * Sets the value of the layer2MaterialPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2MaterialPLMId(String value) {
        this.layer2MaterialPLMId = value;
    }

    /**
     * Gets the value of the layer2MaterialName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2MaterialName() {
        return layer2MaterialName;
    }

    /**
     * Sets the value of the layer2MaterialName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2MaterialName(String value) {
        this.layer2MaterialName = value;
    }

    /**
     * Gets the value of the layer2CompositionComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the layer2CompositionComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLayer2CompositionComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Layer2CompositionComponent }
     * 
     * 
     */
    public List<Layer2CompositionComponent> getLayer2CompositionComponent() {
        if (layer2CompositionComponent == null) {
            layer2CompositionComponent = new ArrayList<Layer2CompositionComponent>();
        }
        return this.layer2CompositionComponent;
    }

}

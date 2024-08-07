
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer2CompositionRUComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer2CompositionRUComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer2CompositionRUPercentage" type="{http://www.sportmaster.ru/plm-services-careLabel}percent"/>
 *         &lt;element name="layer2CompositionRUItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer2CompositionRUComponent", propOrder = {
    "layer2CompositionRUPercentage",
    "layer2CompositionRUItem"
})
public class Layer2CompositionRUComponent {

    @XmlElement(required = true)
    protected BigDecimal layer2CompositionRUPercentage;
    @XmlElement(required = true)
    protected String layer2CompositionRUItem;

    /**
     * Gets the value of the layer2CompositionRUPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLayer2CompositionRUPercentage() {
        return layer2CompositionRUPercentage;
    }

    /**
     * Sets the value of the layer2CompositionRUPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLayer2CompositionRUPercentage(BigDecimal value) {
        this.layer2CompositionRUPercentage = value;
    }

    /**
     * Gets the value of the layer2CompositionRUItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2CompositionRUItem() {
        return layer2CompositionRUItem;
    }

    /**
     * Sets the value of the layer2CompositionRUItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2CompositionRUItem(String value) {
        this.layer2CompositionRUItem = value;
    }

}

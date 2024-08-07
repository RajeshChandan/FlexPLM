
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer2CompositionComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer2CompositionComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer2CompositionPercentage" type="{http://www.sportmaster.ru/plm-services-careLabel}percent"/>
 *         &lt;element name="layer2CompositionItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer2CompositionComponent", propOrder = {
    "layer2CompositionPercentage",
    "layer2CompositionItem"
})
public class Layer2CompositionComponent {

    @XmlElement(required = true)
    protected BigDecimal layer2CompositionPercentage;
    @XmlElement(required = true)
    protected String layer2CompositionItem;

    /**
     * Gets the value of the layer2CompositionPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLayer2CompositionPercentage() {
        return layer2CompositionPercentage;
    }

    /**
     * Sets the value of the layer2CompositionPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLayer2CompositionPercentage(BigDecimal value) {
        this.layer2CompositionPercentage = value;
    }

    /**
     * Gets the value of the layer2CompositionItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer2CompositionItem() {
        return layer2CompositionItem;
    }

    /**
     * Sets the value of the layer2CompositionItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer2CompositionItem(String value) {
        this.layer2CompositionItem = value;
    }

}

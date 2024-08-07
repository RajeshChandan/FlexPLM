
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductBOMComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductBOMComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="componentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="placement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productBOMComponentDestination" type="{http://www.sportmaster.ru/plm-services-careLabel}ProductBOMComponentDestination" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductBOMComponent", propOrder = {
    "componentName",
    "placement",
    "productBOMComponentDestination"
})
public class ProductBOMComponent {

    protected String componentName;
    protected String placement;
    @XmlElement(required = true)
    protected List<ProductBOMComponentDestination> productBOMComponentDestination;

    /**
     * Gets the value of the componentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the value of the componentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponentName(String value) {
        this.componentName = value;
    }

    /**
     * Gets the value of the placement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlacement() {
        return placement;
    }

    /**
     * Sets the value of the placement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlacement(String value) {
        this.placement = value;
    }

    /**
     * Gets the value of the productBOMComponentDestination property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productBOMComponentDestination property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductBOMComponentDestination().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductBOMComponentDestination }
     * 
     * 
     */
    public List<ProductBOMComponentDestination> getProductBOMComponentDestination() {
        if (productBOMComponentDestination == null) {
            productBOMComponentDestination = new ArrayList<ProductBOMComponentDestination>();
        }
        return this.productBOMComponentDestination;
    }

}

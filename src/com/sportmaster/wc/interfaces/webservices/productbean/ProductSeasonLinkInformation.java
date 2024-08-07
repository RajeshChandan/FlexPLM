
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductSeasonLinkInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductSeasonLinkInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="productSeasonLinkInformationItem" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLinkInformationItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSeasonLinkInformation", propOrder = {
    "productSeasonLinkInformationItem"
})
public class ProductSeasonLinkInformation {

    protected List<ProductSeasonLinkInformationItem> productSeasonLinkInformationItem;

    /**
     * Gets the value of the productSeasonLinkInformationItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSeasonLinkInformationItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSeasonLinkInformationItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductSeasonLinkInformationItem }
     * 
     * 
     */
    public List<ProductSeasonLinkInformationItem> getProductSeasonLinkInformationItem() {
        if (productSeasonLinkInformationItem == null) {
            productSeasonLinkInformationItem = new ArrayList<ProductSeasonLinkInformationItem>();
        }
        return this.productSeasonLinkInformationItem;
    }

}

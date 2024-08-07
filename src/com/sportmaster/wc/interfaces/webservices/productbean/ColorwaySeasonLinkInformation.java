
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorwaySeasonLinkInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonLinkInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="colorwaySeasonLinkInformationItem" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLinkInformationItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwaySeasonLinkInformation", propOrder = {
    "colorwaySeasonLinkInformationItem"
})
public class ColorwaySeasonLinkInformation {

    protected List<ColorwaySeasonLinkInformationItem> colorwaySeasonLinkInformationItem;

    /**
     * Gets the value of the colorwaySeasonLinkInformationItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorwaySeasonLinkInformationItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorwaySeasonLinkInformationItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorwaySeasonLinkInformationItem }
     * 
     * 
     */
    public List<ColorwaySeasonLinkInformationItem> getColorwaySeasonLinkInformationItem() {
        if (colorwaySeasonLinkInformationItem == null) {
            colorwaySeasonLinkInformationItem = new ArrayList<ColorwaySeasonLinkInformationItem>();
        }
        return this.colorwaySeasonLinkInformationItem;
    }

}

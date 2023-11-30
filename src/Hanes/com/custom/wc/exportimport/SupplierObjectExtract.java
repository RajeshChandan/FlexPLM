/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.custom.wc.exportimport;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.supplier.*;
import com.lcs.wc.util.LCSException;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 *
 * @author daboisse
 */
public class SupplierObjectExtract {
    public static String hbiSupplierExtractByObject(WTObject object, String materialAction) throws LCSException, WTException, WTPropertyVetoException, Exception {

        LCSSupplier supplierObject = (LCSSupplier) object;
        FlexType ftSupplier = supplierObject.getFlexType();
        Vector objectsToExport = new Vector();

        String supplierType = "Material_" + ftSupplier.getFullName();
        objectsToExport.add(supplierObject);

        com.lcs.wc.util.XMLHelper xmlHelper = new com.lcs.wc.util.XMLHelper();
        Document xmlDoc = new com.sun.org.apache.xerces.internal.dom.DocumentImpl();
        Element root = xmlDoc.createElement("SupplierRoot");

        root.setAttribute("matType", supplierType);
        xmlDoc.appendChild(root);
        String theXml = xmlHelper.generateXML(objectsToExport, true, xmlDoc, root);
        return theXml.toString();  
    }
}

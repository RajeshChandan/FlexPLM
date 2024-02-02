package com.custom.wc.exportimport;

import java.io.*;

import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.util.*;
import wt.vc.wip.*;
import com.lcs.wc.db.*;
import com.lcs.wc.util.*;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.*;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.placeholder.Placeholder;
import com.lcs.wc.season.LCSSeasonProductLink;

/**
 * Helper class for generating XML
 * @Class name HBIXMLHelper.java
 * @author Dushyant Pathak Created on 11/19/2013
 *
 */
public class HBIXMLHelper extends XMLHelper{

    private Document xmlDoc        = null;
    private Element rootObject = null;

	final static String OBJECT_REF_LIST = "object_ref_list";
	final static String OBJECT_REF = "object_ref";
        
    public static final String DEFAULT_ENCODING = 
        LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset","UTF-8");
    public static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.util.XMLHelper.verbose");


    public HBIXMLHelper () {
    }

	public String generateXML(Collection objects, boolean expandReferences, Document aDoc, Element parentItem) {
	 return generateXML(objects,expandReferences,aDoc,parentItem,true);
	 }
	 public String generateXML(Collection objects, boolean expandReferences, Document aDoc, Element parentItem,boolean includeMetaData) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactoryImpl.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            if (xmlDoc != null) {
                this.rootObject = parentItem;
            } else {
                this.xmlDoc = docBuilder.newDocument();
                this.rootObject = xmlDoc.createElement("COLLECTION");
            }

            Iterator it = objects.iterator();
            HBIXMLWriter writer = null;
            while (it.hasNext()) {
                WTObject obj = (WTObject)it.next();
                writer = new HBIXMLWriter( );
                writer.toXml( obj, expandReferences, this.xmlDoc, this.rootObject,includeMetaData); 
                this.xmlDoc = writer.getXMLDoc();
            }

            xmlDoc.appendChild(rootObject);

            return writer.getXMLString();

        } catch(Exception e) {
             System.out.println("Error " + e);
        }
        return "";
    }
   }

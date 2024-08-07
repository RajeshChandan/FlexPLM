/**SMProductDocument.java.
 */
package com.sportmaster.wc.product;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import wt.util.WTException;

/**
 * This class will get the document attached to Product via reference Objects.
 * Calling this Class's Static methods from PDFProductSpecGenerator2.jsp Method
 * return document string
 * 
 * @author 'true' Monu Singh Jangra
 * @version 'true' 1.0 version number
 * @version 'true' 1.1 version number
 */
public class SMProductDocument {
	
	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMProductDocument.class);
	/**
	 * This is Default Constructor for Class SMProductDocument
	 */
	protected SMProductDocument(){
		
	}
	
	
	
	private static String document;
	
	//NewCodeEnd
	
	/**
	 * This method will get all the document associated with attributes which
	 * are mention in below property in custom.techpack.properties. Entry:
	 * com.sportmaster.wc.product.AttributeList
	 * 
	 * @param product
	 *            LCSProduct object
	 * @return String document list "|~*~|" separated
	 */
	public static String getAllProductDocument(LCSProduct product) {
		// String document = null;
		try {
			document = null;
				List listOfDocs = new ArrayList<String>();
				LCSDocument docObject = null;
			//PHASE4 CR:32 -- Start
				//String attributeList = LCSProperties.get("com.sportmaster.wc.product.AttributeList");
				String attributeList = null;
				String appendStrType[] = product.getFlexType().getFullNameDisplay().split("\\\\");
				attributeList = LCSProperties.get("com.sportmaster.wc.product."+appendStrType[0]+".AttributeList");
				if(FormatHelper.hasContent(attributeList)) {
		   //PHASE4 CR:32 -- End
					StringTokenizer attributeListST = new StringTokenizer(attributeList, ",");
					while (attributeListST.hasMoreElements()) {
						docObject = (LCSDocument) product.getValue(attributeListST.nextToken());

						if (docObject != null && document == null) {
							document = "VR:com.lcs.wc.document.LCSDocument:" + docObject.getBranchIdentifier();
							listOfDocs.add(docObject.getBranchIdentifier());
							LOGGER.debug("Getting Style Pattern Document");
							// This will be the string which can be
							// fetch from getDocument() method
						} else if (docObject != null && document != null) {
							if (!listOfDocs.contains(docObject.getBranchIdentifier())) {

								document = document + "|~*~|" + "VR:com.lcs.wc.document.LCSDocument:"
										+ docObject.getBranchIdentifier();
								listOfDocs.add(docObject.getBranchIdentifier());
							}
						}
					}
				}
				
		} catch (WTException e) {
			e.printStackTrace();
		}
		return document;
	}

	private static boolean isDocumentExist() {
		// TODO Auto-generated method stub
		return false;
	}
}

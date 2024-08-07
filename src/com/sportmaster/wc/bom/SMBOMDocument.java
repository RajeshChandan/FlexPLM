/**SMBOMDocument.java.
 *
 */
package com.sportmaster.wc.bom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.document.IteratedDocumentReferenceLink;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentToObjectLink;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.doc.WTDocumentMaster;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.session.SessionServerHelper;

/**
 * This class contain the logic for getting Document's. Material and
 * Material-Color Method called is addBOMDocuments(specPages) and this will
 * return the Document
 * 
 * @author 'true' Monu Singh Jangra
 * @version 'true' 1.0 version number
 */
public class SMBOMDocument {

	final static String materialTrimType = LCSProperties.get("com.sportMaster.wc.material.TrimType");
	final static String materialDecorationType = LCSProperties.get("com.sportMaster.wc.material.DecorationType");
	final static String materialProductPackagingType = LCSProperties.get("com.sportMaster.wc.material.ProductPackagingType");
	//ADDED FOR PHASE - 8 START
	final static String materialFootwearType = LCSProperties.get("com.sportMaster.wc.material.FootwearType");
	final static String materialSportsEquipmentType = LCSProperties.get("com.sportMaster.wc.material.SportsequipmentType");
	//ADDED FOR PHASE - 8 END
	final static String documentArtworkType = LCSProperties.get("com.sportMaster.wc.document.ArtworkType");
	//ADDED FOR PHASE - 8 START
	final static String documentSoleDesignDocumentType = LCSProperties.get("com.sportMaster.wc.document.SoleDesignDocumentsType");
	final static String documentTechnicalDocumentType = LCSProperties.get("com.sportMaster.wc.document.TechnicalDocumentsType");
	//ADDED FOR PHASE - 8 END
	
	//CR #37 START	
	// Boolean value check for including Product BOM Material's Color Documents and 
	// setting using JSP in setter method.
	static boolean productBomCheck;
	// Boolean Value check for including Product BOM Color Documents and
	// setting using JSP in setter method.
	static boolean productBOMColorCheck;
	
	/**
	 * Contains colorBOM list. List<LCSColor>
	 */
	private static List<LCSColor> colorListBOM;
	// CR #37 END

	// CR #63
	/**
	 * This will be variable which contains all the document. List<LCSDocument>
	 * object
	 */
	private static Map<WTDocumentMaster, LCSDocument> docCollection;

	/**
	 * This will be variable which contains all the material. List<LCSMaterial>
	 * object
	 */
	private static List<LCSMaterial> materialList;

	/**
	 * Contains materialColor list. List<LCSMaterialColor>
	 */
	private static List<LCSMaterialColor> materialColorListBOM;

	/**
	 * This will be variable which contains all the document. in the form of String
	 * array object
	 */
	private static String[] listOfDocs;

	/**
	 * Logger Initialization.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBOMDocument.class);

	/**
	 * Default Constructor of class SMBOMDocument.
	 */
	protected SMBOMDocument() {
	}

	/**
	 * getter method for String array object listOfDocs.
	 * 
	 * @return listOfDocs String array.
	 */
	public static String[] getListOfDocs() {
		if (null != listOfDocs) {
			return listOfDocs.clone();
		} else {
			return null;
		}
	}

	/**
	 * Method getting called from PDFProductSpecGenerator2.jsp file. and this
	 * will return boolean if any document need to attach in Tech pack zip file.
	 * 
	 * @param specPages
	 *            type string contains all specification comp selected.
	 * @return boolean true if any document need to print
	 */
	public static boolean addBOMDocuments(String specPages) {
		//CR #63
		docCollection = new HashMap<WTDocumentMaster, LCSDocument>();
		List<String> componentList = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(specPages, "|~*~|");
		while (st.hasMoreElements()) {
			String compItem = st.nextToken();
			compItem = compItem.replaceAll("Images Page-:-", "");
			compItem = compItem.replaceAll("BOM-:-", "");
			compItem = compItem.replaceAll("Measurements-:-", "");
			compItem = compItem.replaceAll("Construction-:-", "");
			if (compItem.contains("BOMPart")) {

				componentList.add(compItem);
			}
		}
		
		String comp = specPages;
		// Modifying specPages as comp
		comp = comp.replaceAll("Images Page-:-", "");
		comp = comp.replaceAll("BOM-:-", "");
		comp = comp.replaceAll("Measurements-:-", "");
		comp = comp.replaceAll("Construction-:-", "");
		comp = comp.replaceAll("[|~*~|]", "");
		
		//Phase- 8 release2 fixed ticket : 14977230 - Start
		comp =  comp.replaceAll("MeasuredSample-:-", "|MeasuredSample-:-");
		//Phase- 8 release2 fixed ticket : 14977230 - End
		
		// removing |~*~| from the comp String
		comp = comp.replaceAll("V", "|V");
		// replacing V to |V so it can be separated based on '|' character
		if (!comp.contains("OR")) {
			comp = comp.substring(2, comp.length());
			// this will create the substring of the comp so
			// that we will left with string|string|string form
		}
		// components converted into a list called componentList
		while (comp.contains("com")) {
			int i = 0;
			i = comp.indexOf('|');
			if (i == -1) {
				i = comp.length();
				componentList.add(comp);

				break;
			}
			String subStr = comp.substring(0, i);
			componentList.add(subStr);

			comp = comp.substring(i + 1, comp.length());
		}
		// Getting Documents after filtering BOM specific components
		for (int j = 0; j < componentList.size(); j++) {
			String component = componentList.get(j);
			if (component.contains("VR:com.lcs.wc.flexbom.FlexBOMPart:")
					|| component.contains("OR:com.lcs.wc.flexbom.FlexBOMPart:")) {
				// SPORTMASTER Custom to allow Vendor to generate the TechPack without error
				boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
				try {
					SessionServerHelper.manager.setAccessEnforced(false);

					getMaterial(component);
					// Calling getMaterial Method by passing
					// component list; This will update the materialList
					getMaterialColorDocument(materialColorListBOM);
					// Calling getMaterialColorDocument method by passing
					// materialList: this will update the docCollection
					getMaterialDocument(materialList);
					// Calling getMaterialDocument method by passing
					// materialList: this will update the docCollection

					// CR #37 START
					getColorDocument(colorListBOM);
					// Calling getColorDocument method by passing
					// colorListBOM: this will update docCollection
					// CR #37 END

					listOfDocs = null;
					// Declaring listOFDocs array as null so no document
					// reference should be available
					getDocumentArray(docCollection);
					// Calling getDocumentArray method by passing docCollection;
					// This will convert document collection into document
					// array of string

				} finally {
					SessionServerHelper.manager.setAccessEnforced(accessEnforced);
				}
			}

		}
		if (null != listOfDocs) {
			return listOfDocs.length > 0;
		} else {
			return false;
		}
	}

	// CR #37
	/**
	 * Method to Get the Documents associated to Color Document Tab.
	 * 
	 * @param colorList2
	 *            of type List<LCSColor>.
	 */
	private static void getColorDocument(List<LCSColor> colorList2) {		
		Iterator<LCSColor> iterator = colorList2.iterator();
		while (iterator.hasNext()) {
			getLCSDocumentObject((WTObject) iterator.next(), LCSDocumentToObjectLink.class);
		}
	}

	/**
	 * Method to Get the Documents associated to Material's Document Tab.
	 * 
	 * @param materialList2
	 *            of type List<LCSMaterial>.
	 */
	private static void getMaterialDocument(List<LCSMaterial> materialList2) {
		Iterator<LCSMaterial> iterator = materialList2.iterator();
		while (iterator.hasNext()) {
			getLCSDocumentObject((WTObject) iterator.next(), IteratedDocumentReferenceLink.class);

		}
	}

	// CR #63
	/**
	 * Method to get all the document in an array from a list.
	 * 
	 * @param docCollection2
	 *            Map of type <WTDocumentMaster,LCSDocument>.
	 */
	private static void getDocumentArray(Map<WTDocumentMaster, LCSDocument> docCollection2) {
		String docString = null;
		listOfDocs = new String[docCollection2.size()];
		Set dListSet = docCollection2.entrySet();
		Iterator iterator = dListSet.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			LCSDocument document = (LCSDocument) entry.getValue();
			docString = "VR:com.lcs.wc.document.LCSDocument:" + document.getBranchIdentifier();
			listOfDocs[i] = docString;
			i++;

		}
	}

	/**
	 * Method to get all the documents associated to Material-Color.
	 * 
	 * @param materialColorListBOM2
	 *            list of type <LCSMatreialColor>.
	 */
	private static void getMaterialColorDocument(List<LCSMaterialColor> materialColorListBOM2) {
		Iterator<LCSMaterialColor> itr = materialColorListBOM2.iterator();
		while (itr.hasNext()) {			
			getLCSDocumentObject((WTObject) itr.next(), LCSDocumentToObjectLink.class);
		}
	}

	/**
	 * Method to get the material of type Trims/Decoration/Product Packaging. in
	 * a list named materialList
	 * 
	 * @param component
	 *            string passing BOM objects.
	 */
	private static void getMaterial(String component) {
		materialList = new ArrayList<LCSMaterial>();
		materialColorListBOM = new ArrayList<LCSMaterialColor>();
		// CR #37
		colorListBOM = new ArrayList<LCSColor>();
		LOGGER.debug("In getMaterial method - start");
		try {
			FlexBOMPart bomPart = (FlexBOMPart) LCSQuery.findObjectById(component);
			Collection<FlexBOMLink> bomLink = LCSFlexBOMQuery.getAllFlexBOMLinks(bomPart, null, null, null, null, null);
			Iterator<FlexBOMLink> itr = bomLink.iterator();

			while (itr.hasNext()) {
				FlexBOMLink fBomLink = itr.next();
				if (!fBomLink.isDropped() && fBomLink.getOutDate() == null) {
					LCSMaterialMaster materialMaster = fBomLink.getChild();
					LCSMaterialColor materialColor = (LCSMaterialColor) fBomLink.getMaterialColor();
					if (materialColor != null || materialMaster != null) {
						LCSMaterial material = (LCSMaterial) VersionHelper.latestIterationOf(materialMaster);						
						if (null != material) {
							//CR #37 START
							// If productBOMColorCheck is true, include the color docs associated to bom materials			
							if (productBOMColorCheck) {		
								LCSColor color = null;
								if (materialColor != null) {
									color = materialColor.getColor();
									if (color != null) {
										colorListBOM.add(color);										
									}
								}
							}
							// CR #37 END	
							// Added Below for getting material document of Trim, Decoration, Product
							// packaging, Footwear and Sport Equipment Phase - 8 Start
							if (isOfMaterialType(material)) {
								materialList.add(material);
							} else {
								LOGGER.debug(material.getName() + " is not in Type");
							}
							// Added Below for getting material document of Trim, Decoration, Product
							// packaging, Footwear and Sport Equipment Phase - 8 End
							
							//Added below for getting material color doc of Trim, Decoration and Product packaging type Phase - 8  
							if (isOfMaterialTypeForMatColor(material) && materialColor != null) {
								
								// CR #37 START
								// If productBomCheck is true, include the material color docs associated to bom materials
								if (productBomCheck) {	
							    // CR #37 END
									materialColorListBOM.add(materialColor);										
								}
							}
							else {
								LOGGER.debug(material.getName() + " is not in Type for Material Color document");
							}
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		LOGGER.debug("In getMaterial method - end");
	}

	/**
	 * Method to get the LCSDocument object from link. by a
	 * preparedQueryStatement
	 * 
	 * @param obj
	 *            WTObject.
	 * @param className
	 *            Class.
	 */
	private static void getLCSDocumentObject(WTObject obj, Class className) {
		PreparedQueryStatement statement = new PreparedQueryStatement();
		String oid = FormatHelper.getNumericFromOid(obj.toString());
		// Calling getOid Method by passing the WTObject, This will return the
		// id/identifier for the WTObject
		String classNameStr = getClassNameString(className);
		// Calling getClassNameString Method by passing the class object,
		// This will return the Class name in form of string

		try {
			String documentTypeArtwork = FlexTypeCache.getFlexTypeFromPath(documentArtworkType).getIdPath();
			//ADDED FOR PHASE - 8 START
			String documentTypeSoleDesign = FlexTypeCache.getFlexTypeFromPath(documentSoleDesignDocumentType).getIdPath();
			String documentTypeTechnicalDocument = FlexTypeCache.getFlexTypeFromPath(documentTechnicalDocumentType).getIdPath();
			//ADDED FOR PHASE - 8 END
			statement.appendFromTable(className);
			statement.appendSelectColumn(classNameStr, "IDA3B5");
			statement.appendCriteria(new Criteria(classNameStr, "IDA3A5", oid, Criteria.EQUALS));
			SearchResults results = LCSQuery.runDirectQuery(statement);

			List<?> dataCollection = results.getResults();
			if (null != dataCollection && dataCollection.size() > 0) {
				for (Object obj1 : dataCollection) {
					FlexObject fo = (FlexObject) obj1;
					WTDocumentMaster documentMaster = (WTDocumentMaster) LCSQuery.findObjectById("OR:wt.doc.WTDocumentMaster:" + fo.getData(classNameStr + ".IDA3B5"));
					LCSDocument document = (LCSDocument) VersionHelper.latestIterationOf(documentMaster);
					if(null != document){
						if (document.getFlexTypeIdPath().contains(documentTypeArtwork)) {
							//CR #63
							docCollection.put(documentMaster, document);
						}
						//ADDED FOR PHASE - 8 START
						if (obj instanceof LCSMaterial && (document.getFlexTypeIdPath().contains(documentTypeSoleDesign)
								|| document.getFlexTypeIdPath().contains(documentTypeTechnicalDocument))) {
							docCollection.put(documentMaster, document);
						}
						//ADDED FOR PHASE - 8 END
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method will get the class name without package which can be used. in
	 * prepared Statement directly
	 * 
	 * @param className
	 *            Class.
	 * @return String with class package.
	 */
	private static String getClassNameString(Class className) {
		Package classPackage = className.getPackage();
		String classPackagestr = classPackage.toString();
		classPackagestr = classPackagestr.replaceAll("package ", "");
		classPackagestr = className.getName().replaceAll(classPackagestr + ".", "");
		return classPackagestr;
	}

	
	
	public static boolean isOfMaterialType(LCSMaterial material) {
		try {
			if (null != material) {
				String flexTypeTrim = FlexTypeCache.getFlexTypeFromPath(materialTrimType).getIdPath();
				String flexTypeDecoration = FlexTypeCache.getFlexTypeFromPath(materialDecorationType).getIdPath();
				String flexTypeProductPacakaging = FlexTypeCache.getFlexTypeFromPath(materialProductPackagingType).getIdPath();
				//ADDED FOR PHASE - 8 START
				String flexTypeFootwear = FlexTypeCache.getFlexTypeFromPath(materialFootwearType).getIdPath();
				String flexTypeSportsEquipment = FlexTypeCache.getFlexTypeFromPath(materialSportsEquipmentType).getIdPath();
				//ADDED FOR PHASE - 8 END
				
				//ADDED FOOTWEAR & SPORTEQUIPMENT SUBTYPE PHASE - 8 START
				if (material.getFlexTypeIdPath().startsWith(flexTypeTrim)
						|| material.getFlexTypeIdPath().startsWith(flexTypeProductPacakaging)
						|| material.getFlexTypeIdPath().startsWith(flexTypeDecoration)
						|| material.getFlexTypeIdPath().startsWith(flexTypeFootwear)
						|| material.getFlexTypeIdPath().startsWith(flexTypeSportsEquipment)) {
					return true;
				}
				//ADDED FOOTWEAR & SPORTEQUIPMENT SUBTYPE PHASE - 8 END
			}
		} catch (WTException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	//Splitted isOfMaterialType() method and changed the method name to isOfMaterialTypeForMatColor() for phase - 8
	public static boolean isOfMaterialTypeForMatColor(LCSMaterial material) {
		try {
			if (null != material) {
				
				String flexTypeTrim = FlexTypeCache.getFlexTypeFromPath(materialTrimType).getIdPath();
				String flexTypeDecoration = FlexTypeCache.getFlexTypeFromPath(materialDecorationType).getIdPath();
				String flexTypeProductPacakaging = FlexTypeCache.getFlexTypeFromPath(materialProductPackagingType).getIdPath();
				if (material.getFlexTypeIdPath().startsWith(flexTypeTrim)
						|| material.getFlexTypeIdPath().startsWith(flexTypeProductPacakaging)
						|| material.getFlexTypeIdPath().startsWith(flexTypeDecoration)) {
					return true;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	// CR #37 START
	
	/**Setter Method for productBomCheck using in PDFProductSpecGenerator2.jsp JSP
	 * @param productBomCheck
	 */
	
	public static void setProductBomCheck(boolean productBomCheck) {
		SMBOMDocument.productBomCheck = productBomCheck;
	}

	/**Setter Method for productBomCheck using in PDFProductSpecGenerator2.jsp JSP
	 * @param productBOMColorCheck
	 */
	public static void setProductBOMColorCheck(boolean productBOMColorCheck) {
		SMBOMDocument.productBOMColorCheck = productBOMColorCheck;
	}
	// CR #37 END
}
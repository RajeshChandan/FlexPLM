package com.hbi.wc.interfaces.outbound.product.plugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.hbi.wc.interfaces.outbound.product.HBIInterfaceUtil;
import com.hbi.wc.interfaces.outbound.product.translation.HBISellingProductTransformationProcessor;
import com.hbi.wc.interfaces.outbound.product.util.HBISPAutomationGenericMethods;
import com.hbi.wc.util.logger.HBIUtilLogger;
import com.ibm.icu.util.StringTokenizer;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.moa.LCSMOACollectionClientModel;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.skusize.SKUSize;
import com.lcs.wc.skusize.SKUSizeQuery;
import com.lcs.wc.skusize.SKUSizeToSeason;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FlexObjectUtil;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MultiObjectHelper;
import com.lcs.wc.util.VersionHelper;

import wt.enterprise.RevisionControlled;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
/**
 * HBISPAutomationValidationPlugin.java
 *
 *SAP Selling Product Validation, this will update Integration Log Comments on Selling Product Details Page.
 *If success, then only data to be sent to SAP via SOAP Message.
 * from plug-in entry to perform validation based on the flag status and populate the validation status (success/fail) in the corresponding attribute along with missing data attributes
 * @author UST
 * @since 03-03-2020
 */
public class HBISPAutomationValidationPlugin
{
	private static String businessObjectType = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.businessObjectType", "Business Object\\Integration\\Validation Attributes");
	private static String bomLinkObjectTypeSales = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.bomLinkObjectTypeSales", "BOM\\Materials\\HBI\\Sales BOM");
	private static String bomLinkObjectTypePC = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.bomLinkObjectTypePC", "BOM\\Materials\\HBI\\Selling\\Pack Case BOM");
	
	private static String colorObjectType = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.colorObjectType", "Color\\Colorway");
	private static String businessObjectName = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.businessObjectName", "SAPAndAPSValidationAttributes");
	private static String hbiValidationAttributesKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiValidationAttributesKey", "hbiValidationAttributes");
	private static String hbiAttributeDataTypeKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiAttributeDataTypeKey", "hbiAttributeDataType");
	private static String hbiAttributeKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiAttributeKey", "hbiAttributeKey");
	private static String hbiAttributeName = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiAttributeName", "hbiAttributeName");
	private static String hbiScopeKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiScopeKey", "hbiScope");
	private static String hbiRequiredSAPKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiRequiredSAPKey", "hbiRequiredSAP");
	
	private static String hbiFinalCompleteSetupKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiFinalCompleteSetupKey", "hbiFinalCompleteSetup");
	private static String hbiErpIntegrationLogKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbiIntegratinLogs", "hbiIntegratinLogs");
	private static String objectNameDelimiter = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.objectNameDelimiter", " in ");
	private static String hbiSellingSizeCategory = LCSProperties.get("com.hbi.wc.product.hbiSellingSizeCategory", "hbiSellingSizeCategory");
	private static String hbiPutUpCode = LCSProperties.get("com.hbi.wc.product.hbiPutUpCode", "hbiPutUpCode");
	private static String hbiReferenceSpecification = LCSProperties.get("com.hbi.wc.product.hbiReferenceSpecification", "hbiReferenceSpecification");
	private static String hbiErpMaterialType = LCSProperties.get("com.hbi.wc.product.hbiErpMaterialType", "hbiErpMaterialType");
	private static String hbiPrimarySecondary = LCSProperties.get("com.hbi.wc.uom.hbiPrimarySecondary", "hbiPrimarySecondary");
	private static String hbiFinishedPackageDepthLength = LCSProperties.get("com.hbi.wc.uom.hbiFPLength", "hbiFPLength");
	private static String hbiFinishedPackageLengthHeight = LCSProperties.get("com.hbi.wc.uom.hbiFPHeight", "hbiFPHeight");
	private static String hbiFinishedPackageWidth = LCSProperties.get("com.hbi.wc.uom.hbiFPWidth", "hbiFPWidth");
	private static String hbiPackagingWeightLbs = LCSProperties.get("com.hbi.wc.uom.hbiPackagingWeightLbs", "hbiPackagingWeightLbs");
	private static String hbiSellingStyleNumber = LCSProperties.get("com.hbi.wc.uom.hbiSellingStyleNumber", "hbiSellingStyleNumber");
	private static String hbiErpCartonID = LCSProperties.get("com.hbi.wc.uom.hbiErpCartonID", "hbiErpCartonID");
	private static String hbiErpComponentSize = LCSProperties.get("com.hbi.wc.uom.hbiErpComponentSize", "hbiErpComponentSize");
	
	
	private static String bomLengthStr = "bomLengthStr";
	private static String bomHeightStr = "bomHeightStr";
	private static String bomWidthStr = "bomWidthStr";
	private static String pkgWeightStr = "pkgWeightStr";
	private static String pkgInnerStr = "pkgInnerStr";
	private static String pkgCasesStr = "pkgCasesStr";
	private static String hbiPkgsOrInner = LCSProperties.get("com.hbi.wc.uom.hbiPkgsOrInner", "hbiPkgsOrInner");
	private static String hbiPksCases = LCSProperties.get("com.hbi.wc.uom.hbiPksCases", "hbiPksCases");
	private static String materialStr = "materialStr";
	
	private static String hbiErpComponentStyle = LCSProperties.get("com.hbi.wc.uom.hbiErpComponentStyle", "hbiErpComponentStyle");
	private static String hbiErpAccountName	 = LCSProperties.get("com.hbi.wc.uom.hbiErpAccountName", "hbiErpAccountName");

	
	private static String materialDescription = LCSProperties.get("com.hbi.wc.uom.materialDescription", "materialDescription");
	private static String materialCorrugated = LCSProperties.get("com.hbi.wc.material.material", "Material\\Casing\\Corrugated Carton");
	//Logging in separate file for SP Automation
		
	public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel","DEBUG");
	public static Logger utilLogger = HBIUtilLogger.createInstance(HBISPAutomationValidationPlugin.class, logLevel);
	
	/**
	 * Plug-in function which will invoke on POST_UPDATE_PERSIST of LCSProduct of type 'ERP FINISHED & PRE-PACKS' to validate all the required attributes and populate data flow status
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static boolean validateSPAutomationAttributes(WTObject wtObj, Boolean fcs){
		//validateSPAutomationAttributes(linkObj,true);
		//System.out.println(">>>>>>>>>fcs>>>>>>>>"+fcs);
		boolean success=true;
		LCSSeasonProductLink seasonProductLinkObj = (LCSSeasonProductLink) wtObj;
		try {
		LCSProduct productObj = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
		LCSProduct prodArev=(LCSProduct)VersionHelper.getVersion(productObj, "A");
		utilLogger.debug("### START HBISPAutomationValidationPlugin.validateSPAutomationAttributes(wtObj, fcs) ###");
	//	System.out.println("### START HBISPAutomationValidationPlugin.validateSPAutomationAttributes(wtObj, fcs) ###");
		HBISPAutomationValidationPlugin erpInterfaceValidationPluginObj = new HBISPAutomationValidationPlugin();
		String missingAttributes = "";
		String teamName=productObj.getTeamTemplateName();
		utilLogger.debug("TeamName "+teamName);
		//System.out.println(">>>>>>>>>>>>>TeamName>>>>>>>>>>"+teamName);
		
		if(FormatHelper.hasContent(teamName)&&teamName.contains("HBI")){

			utilLogger.debug("SPValidation Product name "+prodArev.getName());
			//System.out.println(">>>>>>>>>>>>>SPValidation Product name>>>>>>>>>>"+prodArev.getName());
			HBIInterfaceUtil util=new HBIInterfaceUtil();
			LCSLifecycleManaged businessObject = util.getLifecycleManagedByNameType("name", businessObjectName,  businessObjectType);
			
			Map<String, Map<String, String>> validationAttributesMap = new HashMap<String, Map<String, String>>();
			if(fcs) {
				utilLogger.debug("SPValidation STARTED FOR FINAL SETUP "+fcs);
			//	System.out.println(">>>>>>>>>>>>>SPValidation STARTED FOR FINAL SETUP>>>>>>>>>>"+fcs);
				validationAttributesMap = erpInterfaceValidationPluginObj.getValidationAttributesMap(businessObject, hbiFinalCompleteSetupKey);
				System.out.println(">>>>>>>>>validationAttributesMap>>>>>"+validationAttributesMap);
			}else {
				utilLogger.debug("SPValidation STARTED FOR EARLY SETUP "+fcs);
			//	System.out.println(">>>>>>>>>>>>>SPValidation STARTED FOR EARLY SETUP>>>>>>>>>>"+fcs);
				 validationAttributesMap = erpInterfaceValidationPluginObj.getValidationAttributesMap(businessObject, hbiRequiredSAPKey);
			//	 System.out.println(">>>>>>>>>validationAttributesMap>>>>>"+validationAttributesMap);
			}
			missingAttributes = erpInterfaceValidationPluginObj.getFlexObjectAttributesValidationStatus(seasonProductLinkObj, validationAttributesMap, missingAttributes,fcs,businessObject);
			//System.out.println(">>>>>>>>>>>>>>>>missingAttributes>>>>>>>>>>>>>: "+missingAttributes);
		}else{
			missingAttributes="Team Template not selected hence workflow not triggered - Contact FlexPLM Admin";
		}
		utilLogger.debug("Final SPValidation Message: "+missingAttributes);
		System.out.println("Final SPValidation Message: "+missingAttributes);
		Folder hbiCheckedout = null;
		
		
		if(FormatHelper.hasContent(missingAttributes)){
			success=false;
			/*if (WorkInProgressHelper.isCheckedOut((Workable) prodArev)) {
				System.out.println(">>>>>>>>>>>>>isCheckedOut>......");
				prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			}else {
				System.out.println(">>>>>>>>>>>>>isCheckedIn>......");
				hbiCheckedout = WorkInProgressHelper.service.getCheckoutFolder();
				WorkInProgressHelper.service.checkout(prodArev, hbiCheckedout, "").getWorkingCopy();
				prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			}*/
			
			prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			//LCSProductLogic.persist(prodArev,true);
			new LCSProductLogic().saveProduct(prodArev);
			//WorkInProgressHelper.service.checkin(prodArev, "");
		}
		erpInterfaceValidationPluginObj.validateAndPopulateDataLoadStatus(seasonProductLinkObj, missingAttributes, "SAP");
		
		new HBISPAutomationValidationPlugin().populateLockStatusOnRefObject(seasonProductLinkObj, missingAttributes);
		utilLogger.debug("### END HBISPAutomationValidationPlugin.validateSPAutomationAttributes ###");
	}catch(Exception e) {
		//If any exception, do not send success.
		e.printStackTrace();
		success=false;
		
		e.printStackTrace();
		try {
			LCSProduct productObj = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
			LCSProduct prodArev=(LCSProduct)VersionHelper.getVersion(productObj, "A");
			
			Folder hbiCheckedout = null;
			if (WorkInProgressHelper.isCheckedOut((Workable) prodArev)) {
				//System.out.println(">>>>>>>>>>>>>isCheckedOut>......");
				prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			}else {
			//	System.out.println(">>>>>>>>>>>>>isCheckedIn>......");
				hbiCheckedout = WorkInProgressHelper.service.getCheckoutFolder();
				WorkInProgressHelper.service.checkout(prodArev, hbiCheckedout, "").getWorkingCopy();
				prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			}
			//prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			//LCSProductLogic.persist(prodArev,true);
			WorkInProgressHelper.service.checkin(prodArev, "");
			
			//prodArev.setValue("hbiSellingProductStatus","hbiValidationFailed");
			//LCSProductLogic.persist(prodArev,true);
			new HBISPAutomationValidationPlugin().validateAndPopulateDataLoadStatus(seasonProductLinkObj, "Technical Error in Validation - Contact FlexPLM Admin", "SAP");
		} catch (Exception e1) {
			
			e1.printStackTrace();
			success=false;
		}
		
	}
	return success;	
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////					Following functions are using to perform data validation from all the reference objects				//////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* This function is using to validate each associated object data for all the corresponding attributes and format/prepare missing attributes set using to return from function header
	* @param seasonProductLinkObj - LCSSeasonProductLink
	* @param validationAttributesMap - Map<String, Map<String, String>>
	 * @param businessObject 
	* @return missingAttributes - String
	* @throws WTException
	* @throws NoSuchMethodException
	* @throws InvocationTargetException
	* @throws IllegalAccessException
	*/
	public String getFlexObjectAttributesValidationStatus(LCSSeasonProductLink seasonProductLinkObj, Map<String, Map<String, String>> validationAttributesMap, String missingAttributes,Boolean fcs, LCSLifecycleManaged businessObject) throws WTException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
	
	LCSProduct productObj = SeasonProductLocator.getProductARev(seasonProductLinkObj);
	LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonProductLinkObj.getSeasonMaster());
	
	if(productObj!=null && seasonObj!=null) {
		//This block of code is to validate the Product Level Attributes, populating the missing attributes set based on the actual data validation 
		missingAttributes = getFlexObjectAttributesValidationStatus(productObj, validationAttributesMap.get("Product"), missingAttributes, "");
		//To validate based on Catalog Item boolean attribute on product.
		missingAttributes=validateCatalogItem(productObj,businessObject,missingAttributes);
		missingAttributes=ValidateAndDeriveDivsion(productObj,missingAttributes);
		
		
		//This block of code is to validate the Colorway level Attributes data forSAP required fields, populating the missing attributes set based on the actual data validation
		//System.out.println(">>>>>>>>>>>>>validationAttributesMap.get "+validationAttributesMap.get("Colorway"));
		missingAttributes = getColorwayValidationStatus(productObj, seasonObj,validationAttributesMap.get("Colorway"), missingAttributes);
		
		//This  populating the missing attributes set based on the actual data validation 
		
		missingAttributes = getFlexObjectAttributesValidationStatus(seasonProductLinkObj, validationAttributesMap.get("SeasonProduct"), missingAttributes, "");
		
		//, populating the missing attributes set based on the data validation
		missingAttributes = getPlantExtensionsTableDataFromMOA(productObj,validationAttributesMap.get("PlantExtMOAObject"), missingAttributes);
		
		//This block of code is to validate the Put Up CodeAttribute data for APS and SAP required fields, populating the missing attributes set based on the data validation
		missingAttributes = getPutUpCodeDataFromMOA(productObj,validationAttributesMap.get("PutUpMOAObject"), missingAttributes,fcs);
		
		//This block of code is to validate the Size Definition Level Attributes, populating the missing attributes set based on the data validation
		missingAttributes = getProductSizeCategoryValidationStatus(productObj, missingAttributes);
		
		//This block of code is to validate the Colorway-Size Level Attributes, populating the missing attributes set based on the data validation
		
		missingAttributes = getColorwaySizeValidationStatus(productObj, seasonObj,validationAttributesMap.get("ColorwaySize"), missingAttributes);	 
		
		
		missingAttributes = bomValidation(productObj, missingAttributes, validationAttributesMap,fcs);
		
		//To check whether the PUC selected are primary ones for respective material types
		//Check whether has sales bom and pack case bom
		missingAttributes = missingAttributes + validatePrimaryPutUps(productObj);
	
		missingAttributes = missingAttributes + validatePlants(productObj);
		
		missingAttributes = missingAttributes.replaceFirst(",", "").trim();
		
	}
	// utilLogger.debug("### END HBISPAutomationValidationPlugin.getSeasonAttributesValidationStatus(LCSProduct productObj, Map<String, Map<String, String>> validationAttributesMap) ###");
	return missingAttributes;
 }

	private String ValidateAndDeriveDivsion(LCSProduct productObj, String missingAttributes) {
		// TODO Auto-generated method stub
		try {
			//System.out.println(">>>>>>>>>>>>>>>productObj>>>>>"+productObj);
			Map divisionMap=new HBISellingProductTransformationProcessor().findDivisonFromDerivationTable(productObj,null,false);
			//System.out.println("divisionMap::::::::::::"+divisionMap);
			String existingDivision=(String)productObj.getValue("hbiErpDivision");
			String value= (String)divisionMap.get("hbiSAPDivision");
			
			boolean skipValidation=false;
			//System.out.println("existingDivision::::::::::::"+existingDivision);

			//System.out.println("divisionMap::::value::::::::"+value);


		    if(FormatHelper.hasContent(existingDivision)&&FormatHelper.hasContent(value)&& value.contains(existingDivision)){
		    	skipValidation=true;
			//	System.out.println("divisionMap::::Match::::::::"+skipValidation);

		    }
		    
		    if(!skipValidation){
			if(divisionMap.containsKey("Error")){
				missingAttributes=(String) divisionMap.get("Error");
			}
			else{
				productObj.setValue("hbiErpDivision", value);	
				LCSProductLogic logic=new LCSProductLogic();
				LCSLog.debug("value::::::::::Division::::::"+value);
				//logic.save(productObj);
             // String legacyDivision=new HBISellingProductTransformationProcessor().getCODEFromAlternateKeys("hbiErpDivision", value, "LEGACY_DIVISION") ; 
             // productObj.setValue("hbiErpLegacyDivision", legacyDivision);
			}
		    }
		} catch (WTException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return missingAttributes;
	}
	//To check based on Catalog Item on product
	//These attributes are in General Attributes of Business Object
	private String validateCatalogItem(WTObject wtObj, LCSLifecycleManaged businessObject, String missingAttributes) {
		String hbiErpCatalogItem=null;
        LCSProduct productObj=(LCSProduct)wtObj;
		Object objAttributeValue = null;

		try {
			Method method = wtObj.getClass().getMethod("getValue", new Class[] { String.class });
			//Boolean.toString(b1);
			//((Boolean) someObject).booleanValue()
			boolean catBool = ((Boolean) productObj.getValue("hbiErpCatalogItem")).booleanValue();
			//System.out.println(">>>>>>>>>>>>>>>>> catBool "+catBool);
			hbiErpCatalogItem = Boolean.toString(catBool);
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> hbiErpCatalogItem "+hbiErpCatalogItem);
			if("true".equals(hbiErpCatalogItem)) {
				String hbiErpCatalogItemAttributes =(String)businessObject.getValue("hbiErpCatalogItemAttributes");
				if(FormatHelper.hasContent(hbiErpCatalogItemAttributes)) {
					StringTokenizer st =   new StringTokenizer(hbiErpCatalogItemAttributes, ",");
					 while (st.hasMoreTokens()) {
						String catalogAttsKey=(String) st.nextElement();
						String display=productObj.getFlexType().getAttribute(catalogAttsKey).getAttDisplay();
						//System.out.println(">>>>>>>>>>>>>>>>>>> catalogAttsKey,display "+catalogAttsKey + " "+display);
						objAttributeValue = getFlexObjectAttributesValidationStatus(method, wtObj, catalogAttsKey);
						missingAttributes = getCatelogAttributeValidationStatus(catalogAttsKey, display, objAttributeValue,missingAttributes);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return missingAttributes;
	}
	
	/**
	 * @param attributeKey - String
	 * @param attributeDataType - String
	 * @param objAttributeValue - Object
	 * @param missingAttributes 
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public String getCatelogAttributeValidationStatus(String attributeKey, String attributeDisplayName, Object objAttributeValue, String missingAttributes) throws WTException
	{
		//If catalogue is yes, then only validate
		if(!(objAttributeValue != null && FormatHelper.hasContent((String) objAttributeValue)))
		{
			missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
		}	
		return missingAttributes;
	}
	
	private String bomValidation(LCSProduct productObj, String missingAttributes,Map<String, Map<String, String>>  validationAttributesMap,Boolean fcs) {
		try {
			utilLogger.debug("<<<<<<<<<<<<<< BOM SPValidation STARTED>>>>>>>>>>>>>>>>>>>>>>>>>");
			productObj=(LCSProduct)VersionHelper.getVersion(productObj,"A");
			LCSMOATable table=(LCSMOATable)productObj.getValue("hbiPutUpCode");	
			Collection moaColl=table.getRows();
			
			boolean zppkValidation=false;
			
			String erpMaterialType = "";
			if(productObj.getValue(hbiErpMaterialType)!=null) {
				erpMaterialType =(String) productObj.getValue(hbiErpMaterialType);
				if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
					zppkValidation=true;
				}
			}
			String putUpCodeStr="";
		if(moaColl != null && moaColl.size() > 0){
			Iterator moaIterator=moaColl.iterator();
			 while(moaIterator.hasNext()){
			 	FlexObject flexObject=(FlexObject)moaIterator.next();
				String moaIDA2A2 = flexObject.getString("OID");
				LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaIDA2A2); 
				if(moaPUC_Obj != null){
					if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {
						LCSLifecycleManaged businessObjPUC = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
						putUpCodeStr = (String) businessObjPUC.getValue(hbiPutUpCode);
					}
					if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null && fcs) {
						FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
						Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
						if(specComponentColl.isEmpty()) {
							missingAttributes = missingAttributes.concat(", No BOMs ");
						}else {
							for (FlexBOMPart bomPartObj : specComponentColl) {
								utilLogger.debug("SPValidation bomPartObj name: "+bomPartObj.getName());
								if(bomLinkObjectTypePC.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))) {
									//Materials\HBI\Selling\Pack Case BOM Validation
									
									missingAttributes = getPackCaseBOMValidationStatus(productObj, bomPartObj,validationAttributesMap.get("BOMLinkPC"),
										missingAttributes, putUpCodeStr, zppkValidation);
								}else if(bomLinkObjectTypeSales.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))    ) {
								
									missingAttributes = getValidationStatusFromSalesBOMPart(productObj, zppkValidation, bomPartObj, validationAttributesMap.get("BOMLink"),missingAttributes);				
								}
							} 
						}
						
					}
					else {
						utilLogger.debug("------------No BOM Validation as its ESU--------------------");
					
					}
					utilLogger.debug("<<<<<<<<<<<<<< BOM Validation ENDED>>>>>>>>>>>>>>>>>>>>>>>>>");

					}
		 		}
			}else {
				missingAttributes = missingAttributes.concat(", Putup MOA is empty ");
			}
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		return missingAttributes;
	}
		

	/**
	 * This function is using to validate the 'Load Status' invocation point, validate the missingAttributes data and format the 'Load Status' flag to populate on the given Product object
	 * @param seasonProductLinkObj - LCSSeasonProductLink
	 * @param missingAttributes - String
	 * @param validationStartPoint - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateAndPopulateDataLoadStatus(LCSSeasonProductLink seasonProductLinkObj, String missingAttributes, String validationStartPoint) throws WTException, WTPropertyVetoException
	{
		// utilLogger.debug("### START HBISPAutomationValidationPlugin.validateAndPopulateDataLoadStatus(seasonProductLinkObj, missingAttributes, validationTriggerMessage) ###");
		
		if(FormatHelper.hasContent(missingAttributes))
		{
			
		missingAttributes = "SAP Attributes Missing : ".concat(missingAttributes);
			
			
		}	
		
		//Creating LCSMOATable instance to populate the new data(missing attributes information) and setting the load status flag (validation success or failed due to missing attributes) 
		
		
		if(FormatHelper.hasContent(missingAttributes))
		{
			formatAndPopulateIntegrationLogComments(seasonProductLinkObj, missingAttributes);
		}
		else if(!FormatHelper.hasContent(missingAttributes) && "SAP".equalsIgnoreCase(validationStartPoint))
		{
			missingAttributes = "Validation successful.Validation completed for SAP Loading";
			//Calling a function to populate the data in discussion attribute (a attribute which contains error log for failure of APS/SAP validation along with the trigger user, time)
			populateIntegrationLogComments(seasonProductLinkObj, missingAttributes);
		}
		
		// utilLogger.debug("### END HBISPAutomationValidationPlugin.validateAndPopulateDataLoadStatus(seasonProductLinkObj, missingAttributes, validationTriggerMessage) ###");
	}
	
	/**
	 * This function is using to populate the data in discussion attribute (a attribute which contains error log for failure of APS/SAP validation along with the trigger user and time)
	 * @param seasonProductLinkObj - LCSSeasonProductLink
	 * @param missingAttributes - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void formatAndPopulateIntegrationLogComments(LCSSeasonProductLink seasonProductLinkObj, String missingAttributes) throws WTException, WTPropertyVetoException
	{
		// utilLogger.debug("### START HBISPAutomationValidationPlugin.formatAndPopulateIntegrationLogComments(seasonProductLinkObj, missingAttributes) ###");
		String formatedMissingAttributes = missingAttributes;
		String formatedMissingAttributesTemp = "";
		
		do
		{
			//Validating the given missingAttributes and splitting into smaller groups (in ATT max limit to store is 2000 chars) to populate the comments in more than one row in MOA
			formatedMissingAttributesTemp = "";
			if(missingAttributes.length() > 2000)
			{
				formatedMissingAttributes = missingAttributes.substring(0, 2000);
				formatedMissingAttributes = formatedMissingAttributes.substring(0, formatedMissingAttributes.lastIndexOf(","));
				missingAttributes = missingAttributes.substring(formatedMissingAttributes.length(), missingAttributes.length());
				formatedMissingAttributesTemp = missingAttributes;
			}
			
			//Calling a function to populate the data in discussion attribute (a attribute which contains error log for failure of APS/SAP validation along with the trigger user, time)
			populateIntegrationLogComments(seasonProductLinkObj, formatedMissingAttributes);
			formatedMissingAttributes = missingAttributes;
		}
		while(FormatHelper.hasContent(formatedMissingAttributesTemp));
		
		// utilLogger.debug("### END HBISPAutomationValidationPlugin.formatAndPopulateIntegrationLogComments(seasonProductLinkObj, missingAttributes) ###");
	}
	
	/**
	 * This function is using to populate the data in discussion attribute (a attribute which contains error log for failure of APS/SAP validation along with the trigger user and time)
	 * @param seasonProductLinkObj - LCSSeasonProductLink
	 * @param missingAttributes - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	public void populateIntegrationLogComments(LCSSeasonProductLink seasonProductLinkObj, String missingAttributes) throws WTException, WTPropertyVetoException
	{
		LCSProduct productObj = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
		productObj=(LCSProduct)VersionHelper.getVersion(productObj,"A");
		if(productObj!=null) {
			utilLogger.debug("ProductObj not null for printing integration log ");
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			String sortingNumber = "1";
	        SearchResults moaResults = LCSMOAObjectQuery.findMOACollectionData((LCSPartMaster)productObj.getMaster(),  productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey), "LCSMOAObject.createStampA2", true);
	        utilLogger.debug("moaResults    " +moaResults.getResults().size());
	        if(moaResults != null && moaResults.getResultsFound() > 0)
	        {
	        	Collection moaData = moaResults.getResults();
	        	String maxSortingNumber = FlexObjectUtil.maxValueForFlexObjects(moaData, "LCSMOAOBJECT.SORTINGNUMBER", "int");
	        	sortingNumber = Integer.toString((Integer.parseInt(maxSortingNumber) + 1));
	        }
	       
	      //Code Commented by Wipro Team
	        //getOwnerVersion
//	        LCSMOAObject moaObject = LCSMOAObject.newLCSMOAObject();
//	        moaObject.setFlexType(productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey).getRefType());
//	        moaObject.setOwnerReference(((RevisionControlled)productObj).getMasterReference());
//	        moaObject.setOwnerVersion(productObj.getVersionIdentifier().getValue());
//	       // moaObject.setOwnerAttribute(productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey));
//	        moaObject.setBranchId(Integer.parseInt(sortingNumber));
//	        moaObject.setDropped(false);
//	        moaObject.setSortingNumber(Integer.parseInt(sortingNumber));
//	        moaObject.getFlexType().getAttribute("comments").setValue(moaObject, missingAttributes);
//	        moaObject.getFlexType().getAttribute("user").setValue(moaObject, user);
//	        LCSMOAObjectLogic.persist(moaObject);
	        
	      //Code Upgrade by Wipro Team
			LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
			StringBuffer dataBuffer = new StringBuffer();
			dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "ID", sortingNumber );
			dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "sortingnumber", sortingNumber );
            dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "user", FormatHelper.getNumericObjectIdFromObject(user) );
            dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "comments", missingAttributes );
            dataBuffer.append(MultiObjectHelper.ROW_DELIMITER);
			moaModel.load(FormatHelper.getObjectId(productObj),hbiErpIntegrationLogKey);
			moaModel.updateMOACollection(dataBuffer.toString());
	       
	        LCSMOATable.clearTableFromMethodContextCache((FlexTyped)productObj, productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey));	
		}
	}
	
	/**
	 * This function is using to iterate on the attributes map, get attribute-value from the given FlexObject, validate attribute value in comparison with default value and return status
	 * @param wtObj - WTObject
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public String getFlexObjectAttributesValidationStatus(WTObject wtObj, Map<String, String> validationAttributes, String missingAttributes, String flexObjectName) throws WTException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		String attributeDataTypeName= "";
		Object objAttributeValue = null;
		String attributeDataType = "";
		String attributeDisplayName = "";
		String[] attributeNameDataType = null;
		String missingAttributeKeys = "";
		
		//Using Reflection API to form getValue() function dynamically using the given WTObject, this function is using get fetch data from WTObject instances dynamically without casting
		Method method = wtObj.getClass().getMethod("getValue", new Class[] { String.class });
	
		//Iterating on each Attribute, get the data type of attribute which is using in data down casting, validating/comparing actual data in comparison with the default values
		for(String attributeKey : validationAttributes.keySet())
		{
			attributeDataTypeName = validationAttributes.get(attributeKey);
			attributeNameDataType = attributeDataTypeName.split("~"); 
			attributeDataType = attributeNameDataType[0];
			attributeDisplayName = attributeNameDataType[1];
			objAttributeValue = getFlexObjectAttributesValidationStatus(method, wtObj, attributeKey);
			
			//Calling a function which is using to validate the given attribute data (comparing the actual data with the default values) and populating the missing attributes set
			missingAttributeKeys = getAllAttributeValidationStatus(attributeKey, attributeDataType, attributeDisplayName, objAttributeValue, missingAttributeKeys);
		}
		if(wtObj instanceof LCSProduct){
			LCSProduct product=(LCSProduct)wtObj;
			product=(LCSProduct)VersionHelper.getVersion(product, "A");
			String erpMaterialType = "";
			if(product.getValue(hbiErpMaterialType)!=null) {
				erpMaterialType =(String) product.getValue(hbiErpMaterialType);
				if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
					objAttributeValue = getFlexObjectAttributesValidationStatus(method, wtObj, hbiErpAccountName);

					validationAttributes.put(hbiErpAccountName	,hbiErpAccountName	);
					missingAttributeKeys = getAllAttributeValidationStatus(hbiErpAccountName, "Object Reference", "SAP Account Name", objAttributeValue, missingAttributeKeys);
					}
			}
			
			
		}
		//Formatting the missingAttributes string to remove certain special character, sort the attribute-key in ascending order and format the final string in a specific format
		//missingAttributeKeys = missingAttributeKeys.replaceFirst(",", "").trim();
		if(!(wtObj instanceof FlexBOMLink))
		{
			missingAttributeKeys = sortMissingAttributesByAscendingOrder(wtObj, missingAttributeKeys, flexObjectName, false);
			
			missingAttributeKeys = missingAttributes.concat(missingAttributeKeys);
		}
	
		return missingAttributeKeys;
	}
		
	/**
	 * This function is using to get ProductSizeCategory Attribute Validation status from given owner or Product object and validation attributes set, returning attribute validation status
	 * @param productObj - LCSProduct
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public String getProductSizeCategoryValidationStatus(LCSProduct productObj, String missingAttributes) throws WTException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
	{
		ProductSizeCategory productSizeCategoryObj = null;
		
		//Get ProductSizeCategory object from given Product, validate each required attribute data from ProductSizeCategory object and populate the missing attributes set
		SearchResults results = SizingQuery.findProductSizeCategoriesForProduct(productObj);
		
		if(results != null ){ //Check whether Size definition selected
			if(results.getResultsFound() ==1) { //Should be only one
				FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
				productSizeCategoryObj = (ProductSizeCategory) LCSQuery.findObjectById("VR:com.lcs.wc.sizing.ProductSizeCategory:"+flexObj.getString("ProductSizeCategory.BRANCHIDITERATIONINFO"));
				productSizeCategoryObj = (ProductSizeCategory) VersionHelper.latestIterationOf(productSizeCategoryObj);
				
				String sizeValues = productSizeCategoryObj.getSizeValues();
				
				LCSLifecycleManaged spSizeBO=(LCSLifecycleManaged)productObj.getValue(hbiSellingSizeCategory);
				//************** Sizing from Business Object
				
				StringBuilder nrfSCodeSB = new StringBuilder();
				StringBuilder sizeXrefSB = new StringBuilder();
				String missingSize="";
				for(String size1 :sizeValues.split("\\|\\~\\*\\~\\|")) {
				String searchString="";
					if(spSizeBO!=null){
					 searchString=spSizeBO.getValue("name")+" - "+size1;
					}
					
					LCSLifecycleManaged bo=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
					
					String hbiNRFSize ="";
					
					if(bo!=null) {
					
						if(bo.getValue("hbiNRFSize")!=null) {
							hbiNRFSize = (String) bo.getValue("hbiNRFSize"); 
							
						} 
						
						if(! FormatHelper.hasContent(hbiNRFSize)) {
							
							nrfSCodeSB.append(size1);
							nrfSCodeSB.append(",");
							
						}
					  
					}else {
						sizeXrefSB.append(size1);
						sizeXrefSB.append(",");
					}
				}
				//sizeDefinationName = objectNameDelimiter.concat(productSizeCategoryObj.getName());
				if(nrfSCodeSB.length()>0) {
				
					
					nrfSCodeSB.setLength(nrfSCodeSB.length() - 1);
					missingSize = ", NRF Size Code missing in Size XRef table: "+nrfSCodeSB.toString();
				}
				if(sizeXrefSB.length()>0) {
					
					sizeXrefSB.setLength(sizeXrefSB.length() - 1);
					
					missingSize = missingSize+", Size XRef Table: "+sizeXrefSB.toString();
				}
				
				missingAttributes= missingAttributes +missingSize;
			}else {
				missingAttributes = missingAttributes +", Multiple Product Size Definitions)";
			}
		}//No Sizing slected 
		else{
			missingAttributes = missingAttributes +", PRODUCTSIZECATEGORY - (No Product Size Definition)";
		}
			
		return missingAttributes;
	}
	
	/**
	 * This function is using to get Colorway Size Attribute Validation status from given Product, Season object and validation attributes set, returning attribute validation status
	 * @param productObj - LCSProduct
	 * @param seasonObj - LCSSeason
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public String getColorwaySizeValidationStatus(LCSProduct productObj, LCSSeason seasonObj, Map<String, String> validationAttributes, String missingAttributes) throws WTException,NoSuchMethodException,InvocationTargetException,IllegalAccessException{ 	
		boolean zintValidation=false;
		boolean zppkValidation=false;
		boolean activeColorwaySize=false;
		
		String erpMaterialType = "";
		if(productObj.getValue(hbiErpMaterialType)!=null) {
			erpMaterialType = String.valueOf(productObj.getValue(hbiErpMaterialType));
			if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
				zppkValidation=true;
			}
		}
		
		if(productObj.getValue("hbiOmniSelection")!=null && "true".equalsIgnoreCase(String.valueOf(productObj.getValue("hbiOmniSelection")))) {
			zintValidation=true;
		}
		String zppkValidationStr = "";
		
		Collection<SKUSize> listOfSKUSizeObjects =new ArrayList<SKUSize>();

	
		 SizingQuery sizingQuery = new SizingQuery();
		 SearchResults results = sizingQuery.findPSDByProductAndSeason(productObj); 
		 String missingAttributesTemp="";
		 //Get ProductSizeCategory object from given Product, validate each required attribute data from ProductSizeCategory object and populate the missing attributes set
	 
	 utilLogger.debug("sizingQuery results size "+results.getResults().size());
	 if(results != null && results.getResultsFound() > 0)
	 {
	    FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
	    
	    ProductSizeCategory productSizeCategoryObj = (ProductSizeCategory) LCSQuery.findObjectById("OR:com.lcs.wc.sizing.ProductSizeCategory:"+flexObj.getString("ProductSizeCategory.IDA2A2"));
	   
	    productSizeCategoryObj = (ProductSizeCategory) VersionHelper.latestIterationOf(productSizeCategoryObj);
		if(productSizeCategoryObj != null) {
			
			//Get list of Colorway Size from Product and season
			listOfSKUSizeObjects = getActiveColorwaySizeToSeasonFromProductSeasonAndPSD(productObj,seasonObj,productSizeCategoryObj);
			
			if( listOfSKUSizeObjects.size() > 0) {
				if(  listOfSKUSizeObjects.size()!=1){
					zppkValidationStr = ", For ZPPK: only one Colorway Size should be selected";
				  }
			   for (SKUSize colorwaySizeObj : listOfSKUSizeObjects) {
				   
				   if(colorwaySizeObj.isActive()){
					  activeColorwaySize=true; 
				   }
				   if(zintValidation&&colorwaySizeObj.isActive()){
					   //Below function is using to validate each Attribute data from the given Colorway Size object and populate missing attributes set and returning to the calling function
					  missingAttributesTemp = missingAttributesTemp+getFlexObjectAttributesValidationStatus(colorwaySizeObj, validationAttributes, missingAttributes,"");     
				   }
			   }	
		    }else {
		      if(zintValidation){
		        missingAttributes = missingAttributes.concat(", No Colorway Sizing Details");
		       }
		  
				zppkValidationStr = ", For ZPPK select one Colorway Size";
		   }
	    }	
	 }	
	 utilLogger.debug("SKUSize missingAttributesTemp "+missingAttributesTemp);
	 if(FormatHelper.hasContent(missingAttributesTemp) && missingAttributesTemp.contains("SKUSIZE")) {
		 missingAttributes =missingAttributes.concat(", Zint info is not filled");
	 }
	 if(!activeColorwaySize){
		 missingAttributes = missingAttributes.concat(", No active colorway sizes found");
	 }
	 if(zppkValidation ) {
		 missingAttributes = missingAttributes.concat(zppkValidationStr);
	 }
	 return missingAttributes;
  }
  
	/**
	 * This function is using to get list of flexobjects in the from Colorway Size from given Product,Season and PSD.
	 * @param productObj - LCSProduct
	 * @param seasonObj - LCSSeason
	 * @param productSizeCategoryObj - ProductSizeCategory
	 * @return listOfSKUSizeObjs - Vector<FlexObject>
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SKUSize> getActiveColorwaySizeToSeasonFromProductSeasonAndPSD(LCSProduct productObj,LCSSeason seasonObj,ProductSizeCategory productSizeCategoryObj){	
		
		Collection<LCSSKU> listOfSKUObjects =new ArrayList<LCSSKU>();
		Collection<SKUSize> listOfSKUSizeObjects =new ArrayList<SKUSize>();
		try {
		SearchResults resultsOfSKUSize = null;
		Vector<FlexObject> listOfSKUSizeObjs = null;
		SKUSizeToSeason colorwaySizeToSeasonObj=null;
		
		SKUSizeQuery skuSizeQuery = new SKUSizeQuery();
		//Get list of Colorway from Product and season
		listOfSKUObjects = getSKUFromProductSeason(productObj, seasonObj);
		//utilLogger.debug(" #### listOfSKUObjects from SKUSIze #### " +listOfSKUObjects.size());
		
		if( listOfSKUObjects.size() > 0){
		  for(LCSSKU skuObj : listOfSKUObjects){
			//Get Colorway Size by Passing Product Size Category and SKU Master
			
				//resultsOfSKUSize = SKUSizeQuery.findSKUSizesForPSC(productSizeCategoryObj,(WTPartMaster)skuObj.getMaster(),null,null);
			  resultsOfSKUSize = SKUSizeQuery.findSKUSizesForPSC(productSizeCategoryObj,(LCSPartMaster) skuObj.getMaster(),null,null);
			if(resultsOfSKUSize != null && resultsOfSKUSize.getResultsFound() > 0){
				listOfSKUSizeObjs = resultsOfSKUSize.getResults();
				utilLogger.debug(" <<< listOfSKUSizeObjs >>>"+listOfSKUSizeObjs.size());
				//System.out.println(" <<< listOfSKUSizeObjs >>>"+listOfSKUSizeObjs.size());
				for (FlexObject flexObject : listOfSKUSizeObjs){
					//utilLogger.debug(" #listOfSKUSizeObjs flexObject " +flexObject);
					/*SKUSIZEMASTER.IDA3A6 - 108357101
					SKUSIZE.BRANCHIDITERATIONINFO - 108357219
					SKUSIZEMASTER.SIZE2VALUE - 
				    SKUSIZEMASTER.SIZEVALUE - 56*/
					
					SKUSize skuSizeObj = (SKUSize) LCSQuery.findObjectById("VR:com.lcs.wc.skusize.SKUSize:"+ flexObject.getString("SKUSize.branchIditerationInfo"));
					skuSizeObj = (SKUSize) VersionHelper.latestIterationOf(skuSizeObj);
					colorwaySizeToSeasonObj =skuSizeQuery.getSKUSizeToSeasonBySKUSizeSeason(skuSizeObj , seasonObj) ;
					
					if(colorwaySizeToSeasonObj !=null && colorwaySizeToSeasonObj.isActive()){							
						listOfSKUSizeObjects.add(skuSizeObj);
					}
				}	
			}	
		 }
		} 
		} catch (WTException e) {
			e.printStackTrace();
		}

		return listOfSKUSizeObjects;
	}
		
	/**
	 * This function is using to get LCSSourcingConfig Attribute Validation status from given Product, Season object and validation attributes set, returning attribute validation status
	 * @param spl - LCSSeasonProductLink
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public String getColorwayValidationStatus(LCSProduct productObj,LCSSeason seasonObj,Map<String, String> validationAttributes, String missingAttributes) throws WTException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
	{
		LCSColor colorObj = null;
		Collection<LCSSKU> listOfSKUObjects =new ArrayList<LCSSKU>();
		String colorwayCode = "";
		String NRFGroupName ="";
		String NRFFamily ="";
		Map colorwayMap=new HashMap();
		
		//Get list of Colorway from Product and season
		listOfSKUObjects = getSKUFromProductSeason(productObj,seasonObj);
		utilLogger.debug(" #### listOfSKUObjects from SKUSIze #### " +listOfSKUObjects.size());
		//System.out.println(" #### listOfSKUObjects from SKUSIze #### " +listOfSKUObjects.size());
		if( listOfSKUObjects.size() > 0)
		{
		  for(LCSSKU skuObj : listOfSKUObjects)
		  { 
			String skuName = (String) skuObj.getValue("skuName");
			colorObj = (LCSColor)skuObj.getValue("color");
			String hbiColorwayDescription = (String)colorObj.getValue("hbiColorwayDescription");
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> skuName,"+skuName);
			
			
			if(colorObj != null && colorObjectType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)))
			{ 
				if(!colorwayMap.isEmpty() && colorwayMap.containsKey(hbiColorwayDescription)) {
					missingAttributes = missingAttributes.concat(", Colorway Description: "+hbiColorwayDescription+": is used multiple times for this Style ,");

				}
				
				colorwayCode = (String) colorObj.getValue("hbiColorwayCodeNew");
				if(!FormatHelper.hasContent(colorwayCode))
				{
					missingAttributes = missingAttributes.concat(", "+skuName+": Colorway Code is not provided");
				}
				NRFGroupName = (String) colorObj.getValue("vrdNRFGroupName");
			//	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> NRFGroupName,"+NRFGroupName);
				if(!FormatHelper.hasContent(NRFGroupName))
				{
					missingAttributes = missingAttributes.concat(", "+skuName+": NRFGroupName");
				}
				NRFFamily = (String) colorObj.getValue("vrdNRFFamily");
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> NRFFamily,"+NRFFamily);
				if(!FormatHelper.hasContent(NRFFamily))
				{
					missingAttributes = missingAttributes.concat(", "+skuName+": NRFFamily");
				}
				colorwayMap.put(hbiColorwayDescription, hbiColorwayDescription);
			}
			else
			{
				missingAttributes = missingAttributes.concat(", "+skuName+": Color type should be Color Colorway");
			}
		  }
		}else {
			missingAttributes = missingAttributes.concat(", No Colorways ");
		}
		return missingAttributes;
	}
	
	/**
	 * This function is using to get SKU object from given Product & Season, 
	 * @param productObj - LCSProduct
	 * @param seasonObj - LCSSeason
	 * @return listOfSKUObjects - ArrayList<LCSSKU>
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LCSSKU> getSKUFromProductSeason(LCSProduct productObj,LCSSeason seasonObj) 
	{
		Collection<LCSSKU> listOfSKUObjects =new ArrayList<LCSSKU>();
		
		try {
			//Collection<WTPartMaster> skuMasterCollection = LCSSeasonQuery.getSKUMastersForSeasonAndProduct(seasonObj, productObj, false);
			Collection<LCSPartMaster> skuMasterCollection = LCSSeasonQuery.getSKUMastersForSeasonAndProduct(seasonObj, productObj, false);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>skuMasterCollection "+skuMasterCollection);
			if(skuMasterCollection != null && skuMasterCollection.size() > 0){
				for(LCSPartMaster skuMasterObj : skuMasterCollection){
					
					LCSSKU skuObj = (LCSSKU) VersionHelper.latestIterationOf(skuMasterObj);
					
					skuObj = SeasonProductLocator.getSKUARev(skuObj);
					
					if(skuObj!=null) {
						//utilLogger.debug("getSKUFromProductSeason skuObj name "+skuObj.getName());
						System.out.println("getSKUFromProductSeason skuObj name "+skuObj.getName());
						listOfSKUObjects.add(skuObj);
					}
					
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return listOfSKUObjects;
	}
	public String getPackCaseBOMValidationStatus(LCSProduct productObj, FlexBOMPart bomPartObj, Map<String, String> validationAttributes,
			String missingAttributes, String putUpCodeStr, boolean zppkValidation) {
		HashMap<String,String> prodSizeMap = new HashMap();
		try { 
		String[] sizeValuesArr = new String[100];
		SearchResults sizing_SR = SizingQuery.findProductSizeCategoriesForProduct(productObj);
		
		
		Collection<FlexObject> sizing_coll = sizing_SR.getResults();
		String splitStr = "\\|\\~\\*\\~\\|";
		for(FlexObject sizingFO :sizing_coll) {
		
			String sizeValues = sizingFO.getString("PRODUCTSIZECATEGORY.SIZEVALUES");
			sizeValuesArr = sizeValues.split(splitStr);
		
		}
		
		//to validate bom variation row sizes and product size def values- Both should be same
		
		for(String size1 :sizeValuesArr) {
			prodSizeMap.put(size1, size1);
		}
		} catch (WTException e) {
			e.printStackTrace();
		}
		
		
		if(zppkValidation) {
			//No pack case bom variation for ZPPK and CV1 should be there as primary secondary
			//Not needed, as for ZPPK SP,  sales bom enough, no need of pack case BOM, 21 August 19
			//missingAttributes = zppkPCBOMValidation(bomPartObj, validationAttributes,missingAttributes);
		}else {
			//ZFRT needs 1 Pack Case BOM with size variation and should have EA, CV values
			
			missingAttributes = zfrtPCBOMValidation(bomPartObj, validationAttributes,missingAttributes,putUpCodeStr, prodSizeMap);
			
		}
	
		return missingAttributes;
	}
	
	
	@SuppressWarnings("unchecked")
	public String getValidationStatusFromSalesBOMPart(LCSProduct productObj, boolean zppkValidation, FlexBOMPart bomPartObj, Map<String, String> validationAttributes, String missingAttributes){
		
		try {
		Collection<FlexObject> bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		
		FlexBOMLink flexBOMLinkObj = null;
		String bomPartName = objectNameDelimiter.concat(bomPartObj.getName());
		String missingAttributesTemp = "";
		String missingAttributeKeys = "";
		String section="";
		String styleNo ="";
		boolean componentSection=false;
		boolean productionSection=false;
		boolean productionSectionComments=false;
		if(bomPartObj.getValue(hbiErpCartonID)==null && zppkValidation) {
			missingAttributes = missingAttributes.concat(", Carton Id is blank in Sales BOM");
		}
		//
		if(bomLinkCollection != null && bomLinkCollection.size() > 0){
		   for(FlexObject flexObj : bomLinkCollection){
			  flexBOMLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
			  
			  
			  section = (String)flexBOMLinkObj.getValue("section");
			  if("production".equalsIgnoreCase(section)){
				  productionSection=true;
				  String comments = (String) flexBOMLinkObj.getValue("partName");
				  if(FormatHelper.hasContent(comments)){
					  productionSectionComments=true; 
				  }
			  }	
			  if("components".equalsIgnoreCase(section)){
				  
				  missingAttributes.concat(validateXREFSalesBOM(flexBOMLinkObj, productObj));
				 
				  if(zppkValidation){
					  componentSection=true;
					  LCSProduct componentSP = (LCSProduct) flexBOMLinkObj.getValue(hbiErpComponentStyle);
					  if(componentSP!=null){
						  HashMap<String, Boolean> componentCheck = checkSalesBOMComponent(componentSP);
						  if (componentSP.getValue(hbiSellingStyleNumber)!=null){
								 styleNo = (String)componentSP.getValue(hbiSellingStyleNumber);
							 }
							 if(componentCheck.get("cParentPutup")) {
								 if(componentCheck.get("cRefSpec")){
									 if(componentCheck.get("cZPPK")){
										 if(!componentCheck.get("cSalesBOM")) {
											 missingAttributes = missingAttributes.concat(", Sales BOM missing in Selling Product of SalesBOM Component SP: "+styleNo);
										 }
									 }else{
										 if(!componentCheck.get("cPackCaseBOM")) {
											 missingAttributes = missingAttributes.concat(", PackCase BOM missing in Selling Product of SalesBOM Component SP: "+styleNo);
										 } 
									 }
									 
								 }else{
									 missingAttributes = missingAttributes.concat(", Reference Spec missing in Selling Product of SalesBOM Component SP: "+styleNo);
								 }
								
								 
								 
							 }else {
								 String erpMaterialType =(String) componentSP.getValue(hbiErpMaterialType);
									if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
										 missingAttributes = missingAttributes.concat(", 0911 Putup missing in SalesBOM component (Material Type = ZPPK)");
									}
									else {
										
										 missingAttributes = missingAttributes.concat(",  0000 Putup missing in SalesBOM component (Material Type = ZFRT or ZOFQ)");

									}
							 }
						 
							 
					  }else {
						  missingAttributes = missingAttributes.concat(", No Selling Product in Sales BOM Component");
					  }
					  
					  missingAttributesTemp = checkSalesBOMLink(flexBOMLinkObj, missingAttributesTemp);
					  
					  if(FormatHelper.hasContent(missingAttributesTemp)){
						 missingAttributeKeys = getBOMLinkValidation(missingAttributesTemp, missingAttributeKeys);
					  } 
				  } //ZPPK only
				  
			  } //Component Section
		    }
		}
		
		if(!componentSection && zppkValidation){
			missingAttributes = missingAttributes.concat(", SALES BOM - Missing Component Section values");
		}
		if(productionSection){
			if(!productionSectionComments){
				missingAttributes = missingAttributes.concat(", SALES BOM - Packaging BOM Instructions Missing");
			}
			
		}else{
			missingAttributes = missingAttributes.concat(", SALES BOM - Packaging BOM Instructions Missing");
		}
		//
		missingAttributeKeys = missingAttributeKeys.replaceFirst(",", "");
		if(FormatHelper.hasContent(missingAttributeKeys))
		{
			missingAttributeKeys = sortMissingAttributesByAscendingOrder(flexBOMLinkObj, missingAttributeKeys, "", true);
			missingAttributes = missingAttributes.concat(", SALES BOM - ").concat(missingAttributeKeys).concat(" Missing for one or more rows").concat(bomPartName);
		}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return missingAttributes;
	}
	
	private String validateXREFSalesBOM(FlexBOMLink flexBOMLinkObj, LCSProduct productObj) {
		//To validate Sales BOM, component size from XRef table in business object
		String validationMessage="";
		try {
		  LCSLifecycleManaged spSizeBO=(LCSLifecycleManaged)productObj.getValue(hbiSellingSizeCategory);
		  
		  String searchString="";
		  String size1=(String) flexBOMLinkObj.getValue(hbiErpComponentSize);
		  LCSLifecycleManaged bo = null;
		  if(spSizeBO!=null && FormatHelper.hasContent(size1)){
				
			searchString=spSizeBO.getValue("name")+" - "+size1;
			bo=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
			  	
		  }
				
		  if(bo==null && FormatHelper.hasContent(size1)){
			 validationMessage="Size xref not found  for component size "+size1+" of the Sales BOM";
			 
		  }
		 }catch (WTException e) {	
			e.printStackTrace();
		}
		return validationMessage;
	}
	@SuppressWarnings("unchecked")
	public String zfrtPCBOMValidation(FlexBOMPart bomPartObj, Map<String, String> validationAttributes, String missingAttributes,  String putUpCodeStr, HashMap<String, String> prodSizeMap){
		
	try {
		StringBuilder missingBOMSB=new StringBuilder();
		//Parent row only, this will not have variation data.
		Collection<FlexObject> bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,
				LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		

		//String missingAttributeKeys = "";
		String componentSection="";
		String bomName="";
		if(FormatHelper.hasContent(bomPartObj.getName())){
			
			 bomName=bomPartObj.getName();
		}
		
		utilLogger.debug("***** ZFRT PC bomPartObjName "+bomName);
		utilLogger.debug("***** ZFRT PC bomLinkCollection parent rows size "+bomLinkCollection.size());
		//
		
		HashMap<Integer,String> branchIdMap = new HashMap();
		//This bomSizeMap used to check the variation row size values from the existing size def in product.
		//Sometimes the BOM variation will be set early and then new size definition is used in product,
		//In ui, bom variation rows will not display but in DB the old size values will be there.
		//Compare if size values present in product and from bomlink size values are same, if not give validation message.
		HashMap<String,String> bomSizeMap = new HashMap();
		HashMap parentRowMap = new HashMap();
		HashMap childRowMap = new HashMap();
		
		if(bomLinkCollection != null && bomLinkCollection.size() > 0){
		
		   for(FlexObject flexObj : bomLinkCollection){
			   FlexBOMLink flexBOMLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
			   
			  componentSection = (String)flexBOMLinkObj.getValue("section");
			  componentSection = flexBOMLinkObj.getFlexType().getAttribute("section").getAttValueList().getValue(componentSection,Locale.getDefault());
			  utilLogger.debug("***ZFRT PC BOM Parent rows componentSection "+componentSection);
			  //Consider only casing section, other section data not needed
			  if("Casing BOM".equalsIgnoreCase(componentSection))
			  {   
				  String primarySecondary = (String) flexBOMLinkObj.getValue(hbiPrimarySecondary);
			  	  //branch id is unique for parent rows
				  int branchId = flexBOMLinkObj.getBranchId();
				  utilLogger.debug("*** ZFRT PC BOM Casing Section Parent Rows branchID "+branchId);
				  Double pkgInner = getDoubleValue(flexBOMLinkObj, hbiPkgsOrInner);
				  Double pkgCases = getDoubleValue(flexBOMLinkObj, hbiPksCases);
				 // System.out.println("pkgCases>>>>>>>>>>>>>>>>>>>>>"+pkgCases);
				  Double pkgWeights = getDoubleValue(flexBOMLinkObj, hbiPackagingWeightLbs);
				  LCSMaterial material = null;
				  if(FormatHelper.hasContent(flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"))) {
					   material = (LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" +
							   flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
					   if(!(material!=null && materialCorrugated.equalsIgnoreCase(material.getFlexType().getFullName(true)) && flexBOMLinkObj.getValue(materialDescription)!=null)) {
						   material=null;
					   }
				  }	
				  Double bomLength =  getDoubleValue(flexBOMLinkObj, hbiFinishedPackageDepthLength);
				  Double bomHeight = getDoubleValue(flexBOMLinkObj, hbiFinishedPackageLengthHeight); 
				  Double bomWidth =  getDoubleValue(flexBOMLinkObj, hbiFinishedPackageWidth);
				 
				 // parentRowMap.put(primarySecondary, branchId);
				  if(checkDataMap(parentRowMap, String.valueOf(branchId))) {
					  parentRowMap.put(branchId, primarySecondary);  
				  }
				  if(checkDataMap(parentRowMap, primarySecondary)) {
					  parentRowMap.put(primarySecondary, branchId);  
				  }
				  
				  
				  parentRowMap.put(branchId+bomLengthStr, bomLength);
				  parentRowMap.put(branchId+bomHeightStr, bomHeight);
				  parentRowMap.put(branchId+bomWidthStr, bomWidth);
				  parentRowMap.put(branchId+pkgWeightStr, pkgWeights);
				  parentRowMap.put(branchId+materialStr, material);
				  parentRowMap.put(branchId+pkgInnerStr, pkgInner);
				  parentRowMap.put(branchId+pkgCasesStr, pkgCases);
			  }//Casing Section data only
			}//Parent row iteration
		}//Parent row iteration end
//*******************************************************************************************************************************************
		 utilLogger.debug("parentRowMap "+parentRowMap);
		//Checking only for parent putup i.e. 0000 for EA as child putups may not have EA
		//CV can be at variation row
		if("0000".equalsIgnoreCase(putUpCodeStr)) {
			
			 if(! parentRowMap.containsKey("hbiEA")) {
					missingBOMSB.append(", Missing EA at parent row in Pack Case BOM");
			  }
		}
		
//*******************************************************************************************************************************************	
		//Branch ID matching primary secondary and Child Row preparation - Start
		Collection<FlexObject> pcBOMLink_Coll = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, 
				  null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,"ALL_SIZE1").getResults();
		/*SearchResults com.lcs.wc.flexbom.LCSFlexBOMQuery.findFlexBOMData(FlexBOMPart part, String scMasterId, String skuMasterId, String size1,
		 *  String size2,String destDimId, String wipMode, Date effectiveDate,
				boolean dropped, boolean linkDataOnly, String dimensionMode, String skuMode, String sourceMode, String sizeMode) throws WTException*/
		utilLogger.debug("*** ZFRT variation pcBOMLink_Coll size "+ pcBOMLink_Coll.size());
		if(pcBOMLink_Coll != null && pcBOMLink_Coll.size() > 0){
			
		   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll){ 
			  FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" +  
					  pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
		     
			  int branchId = pcBOMlink.getBranchId();
			  utilLogger.debug("*** ZFRT variation all branchId "+ branchId);
			  //To check only casing section branch IDs.
			  if(parentRowMap.containsKey(branchId)){
				  String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
				  utilLogger.debug("***    primarySecondary "+ primarySecondary);
				  if (FormatHelper.hasContent(primarySecondary) ) {
					  branchIdMap.put(branchId, primarySecondary );  
				  } 
				  
				  String size1 = pcBOMlink.getSize1();
				  utilLogger.debug("********BOM link size1 "+size1);
				  //Child Row preparation
				  if(FormatHelper.hasContent(size1) && prodSizeMap.containsKey(size1)) {
					  //need unique size values from BOMLinks variation rows as stored in db
					  bomSizeMap.put(branchId+size1, size1);
					 
					  Double pkgInner = getDoubleValue(pcBOMlink, hbiPkgsOrInner);
					  Double pkgCases = getDoubleValue(pcBOMlink, hbiPksCases);
					  Double pkgWeights =getDoubleValue(pcBOMlink, hbiPackagingWeightLbs); 
					  LCSMaterial material = null;
					  if(FormatHelper.hasContent(pcBOMLinkFO.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"))) {
						   material = (LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" +
								  pcBOMLinkFO.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
						   if(!(material!=null && materialCorrugated.equalsIgnoreCase(material.getFlexType().getFullName(true)) 
									  && pcBOMlink.getValue(materialDescription)!=null)) {
							   material=null;
						   }
					  }	
					  Double bomLength = getDoubleValue(pcBOMlink, hbiFinishedPackageDepthLength);
					  Double bomHeight = getDoubleValue(pcBOMlink, hbiFinishedPackageLengthHeight); 
					  Double bomWidth = getDoubleValue(pcBOMlink, hbiFinishedPackageWidth); 
					
					 // parentRowMap.put(primarySecondary, branchId);
					  if(checkDataMap(childRowMap, String.valueOf(branchId))) {
						  childRowMap.put(branchId, primarySecondary);  
					  }
					  if(checkDataMap(childRowMap, primarySecondary)) {
						  childRowMap.put(primarySecondary, branchId);  
					  }
					  if(checkDataMap(childRowMap, branchId+bomLengthStr)) {
						  childRowMap.put(branchId+bomLengthStr, bomLength);
					  }
					  if(checkDataMap(childRowMap, branchId+bomHeightStr)) {
						  childRowMap.put(branchId+bomHeightStr, bomHeight);	 
					  }
					  if(checkDataMap(childRowMap, branchId+bomWidthStr)) {
						  childRowMap.put(branchId+bomWidthStr, bomWidth);	 
					  }
					  if(checkDataMap(childRowMap, branchId+pkgWeightStr)) {
						  childRowMap.put(branchId+pkgWeightStr, pkgWeights);	 
					  }
					  //Enter value only if it is null, do not over ride with null when it already has material.
					  if(checkDataMap( childRowMap,branchId+materialStr)) {
						  childRowMap.put(branchId+materialStr, material);	 
					  }
					  if(checkDataMap(childRowMap, branchId+pkgInnerStr)) {
						  childRowMap.put(branchId+pkgInnerStr, pkgInner);	 
					  }
					  if(checkDataMap(childRowMap, branchId+pkgCasesStr)) {
						  childRowMap.put(branchId+pkgCasesStr, pkgCases);	 
					  }
				  }
			  } //Only Casing section values considered by checking the parent row map which is taken only from casing section
	

		   }//For loop
		}
	
		
		//Checking for sizes for each UOM like ip, cv and EA, all sizes should be filled if variation there, else issue in SAP
		//For e.g, new
		HashMap<String,String> eaBOMSize = new HashMap();
		HashMap<String,String> cvBOMSize = new HashMap();
		HashMap<String,String> ipBOMSize = new HashMap();
		for(int branchId: branchIdMap.keySet()){
			String prim = branchIdMap.get(branchId);
			
			for(String bomSize:bomSizeMap.keySet()){
				if(bomSize.length()>0){
					bomSize = bomSize.substring(1,bomSize.length());
					if(bomSizeMap.containsKey(branchId+bomSize ) && FormatHelper.hasContent(bomSizeMap.get(branchId+bomSize)) ){
						if(prim.contains("hbiEA")){
							eaBOMSize.put(bomSizeMap.get(branchId+bomSize), bomSizeMap.get(branchId+bomSize));
						}
						if(prim.contains("hbiCV")){
							cvBOMSize.put(bomSizeMap.get(branchId+bomSize), bomSizeMap.get(branchId+bomSize));
						}
						if(prim.contains("hbiIP")){
							ipBOMSize.put(bomSizeMap.get(branchId+bomSize), bomSizeMap.get(branchId+bomSize));
						}
					}
					
				}
			}
		}
		StringBuilder missingBOMsizesEA=new StringBuilder();
		StringBuilder missingBOMsizesCV=new StringBuilder();
		StringBuilder missingBOMsizesIP=new StringBuilder();
		utilLogger.debug("*** ZFRT PC BOM eaBOMSize "+eaBOMSize);
		utilLogger.debug("*** ZFRT PC BOM cvBOMSize "+cvBOMSize);
		utilLogger.debug("*** ZFRT PC BOM ipBOMSize "+ipBOMSize);
		//This condition, as there can be cases where no BOM Size variation.
		if(!eaBOMSize.isEmpty()){ 
			for(String sizeKey:prodSizeMap.keySet()){
				if(!eaBOMSize.containsKey(sizeKey)){
					missingBOMsizesEA.append(sizeKey+",");
				}
			}
		}
		if(!cvBOMSize.isEmpty()){ 
			
			
			for(String sizeKey:prodSizeMap.keySet()){
				if(!cvBOMSize.containsKey(sizeKey)){
					missingBOMsizesCV.append(sizeKey+",");
				}
			}
		}
		if(!ipBOMSize.isEmpty()){ 
			for(String sizeKey:prodSizeMap.keySet()){
				if(!ipBOMSize.containsKey(sizeKey)){
					missingBOMsizesIP.append(sizeKey+",");
				}
			}
		}
		
		utilLogger.debug("missingBOMsizesEA "+missingBOMsizesEA);
		utilLogger.debug("missingBOMsizesCV "+missingBOMsizesCV);
		utilLogger.debug("missingBOMsizesIP "+missingBOMsizesIP);
		//This will check if a variation row is ignored completely and no values filled for any column. This is a general validation of variation row.
		//However, if any one column is filled, then this validation will skip.
		//Ensure required validation of specific columns in another logic.
		if(missingBOMsizesEA.length()!=0){
			
			String missingSize = missingBOMsizesEA.substring(0,missingBOMsizesEA.length()-1);
			utilLogger.debug("EA missingSize "+missingSize);
			missingBOMSB.append(", EA BOM Size Variation Missing for "+missingSize);
		}
		if(missingBOMsizesCV.length()!=0){
			
			String missingSize = missingBOMsizesCV.substring(0,missingBOMsizesCV.length()-1);
			utilLogger.debug("CV missingSize "+missingSize);
			missingBOMSB.append(", CV BOM Size Variation Missing for "+missingSize);
		}
		if(missingBOMsizesIP.length()!=0){
	
			String missingSize = missingBOMsizesIP.substring(0,missingBOMsizesIP.length()-1);
			utilLogger.debug("IP missingSize "+missingSize);
			missingBOMSB.append(", IP BOM Size Variation Missing for "+missingSize);
		}
		
		
		utilLogger.debug("Full branchIdMap "+branchIdMap);
		utilLogger.debug("Full childRowMap "+childRowMap);
		utilLogger.debug("Full contains CV "+branchIdMap.values());
		//Branch ID matching primary secondary & child Row preparation - End
		
//*******************************************************************************************************************************************	
		//As the OOTB query gives previous size values as well , to avoid multiple duplicate entries in the parent row entries when size does not match
		//in else loop, when size does not match we do not want duplicate values as bomlink size becomes large with old size values which not there now
		HashMap<String, String> missingBOMValuesMap = new HashMap();
		HashMap<String, String> materialCVMessage = new HashMap();
		boolean cvParentChild=false;
		boolean ipParentChild=false;
		
		if(!branchIdMap.isEmpty() ){
			//Check CV - CV is compulsory
			boolean cv = false;
			boolean ip = false;
			for(String primary :branchIdMap.values()) {
				utilLogger.debug("Check primary "+primary);
				if(primary.contains("CV")){
					cv=true;
				}
				if(primary.contains("IP")){
					ip=true;
				}
			}
			if(!cv) {
				missingBOMSB.append(", Missing CV in Pack Case BOM");
			}
			if(!ip) {
				missingBOMSB.append(", Missing IP in Pack Case BOM");
			}
			//Check CV - CV is compulsory in each variation row if present in any variation row or it should exist at parent only.
			LCSMaterial material=null;
			
		   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll){ 
			  FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + 
					  						pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
			  if(FormatHelper.hasContent(pcBOMLinkFO.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"))) {
			   material = (LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" +
					  pcBOMLinkFO.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));

			   utilLogger.debug("Check materialDescription "+pcBOMlink.getValue(materialDescription));
			   if(!(material!=null && materialCorrugated.equalsIgnoreCase(material.getFlexType().getFullName(true)) 
						  && pcBOMlink.getValue(materialDescription)!=null)) {
				   material=null;
			   }
			  }
			  int branchId = pcBOMlink.getBranchId();
			  //String primarySecBranchIdMap = (String) branchIdMap.get(branchId);
			  
			  String primarySecondarySize1 = (String) pcBOMlink.getValue(hbiPrimarySecondary);
			  
			  Double bomLength =getDoubleValue(pcBOMlink, hbiFinishedPackageDepthLength); 
			  Double bomHeight = getDoubleValue(pcBOMlink, hbiFinishedPackageLengthHeight);
			  Double bomWidth = getDoubleValue(pcBOMlink, hbiFinishedPackageWidth); 
			  Double pkgWeights = getDoubleValue(pcBOMlink, hbiPackagingWeightLbs);
			  Double pkgInner = getDoubleValue(pcBOMlink, hbiPkgsOrInner);
			  Double pkgCases =getDoubleValue(pcBOMlink, hbiPksCases); 
			  Boolean ipDoNotCheckDimensions= false;
			  String size1 = pcBOMlink.getSize1();
			  utilLogger.debug("Actual pkgInner "+pkgInner);
			  utilLogger.debug("Actual bomLength "+bomLength);
			  utilLogger.debug("Actual bomHeight "+bomHeight);
			  utilLogger.debug("Actual bomWidth "+bomWidth);
			  utilLogger.debug("Actual branchId "+branchId);
			  utilLogger.debug("branchIdMap branchId "+branchIdMap.get(branchId));
			  utilLogger.debug("size1 primarySecondary "+primarySecondarySize1);
			  if(FormatHelper.hasContent(size1) && branchIdMap.get(branchId)!=null && prodSizeMap.containsKey(size1)) {
				  //Variation rows
				  //To check whether parent row contains EA and corresponding variation row contains other values in primary secondary.
				  if("hbiEA".equalsIgnoreCase( branchIdMap.get(branchId)) && FormatHelper.hasContent(primarySecondarySize1)) {
					  if(!"hbiEA".equalsIgnoreCase(primarySecondarySize1)) {
						  //Parent row contains EA but variation row has different value
						 // masterRowRemoveData.put(size1+splitFormat+branchId+"EA", "DifferentPrimarySecondary");
						  missingBOMSB.append(", EA is selected at parent row and "+ primarySecondarySize1 +" selected at child row");
					  }
				  }
				  //branchIdMap will store random CVs and IPs for same branch ID
				  //If IP1, IP2, IP3 exists, then branchIdMap will store only IP3 for same branch ID.
				  //Hence use primarySecondary from actual size1 variation row if exists.
				  String branchIDPrimSec = branchIdMap.get(branchId);
				 
				 utilLogger.debug("branchIDPrimSec "+branchIDPrimSec);
				 utilLogger.debug("branchIDPrimSec contains IP "+ branchIDPrimSec.contains("IP"));
				 utilLogger.debug("branchIDPrimSec contains CV "+ branchIDPrimSec.contains("CV"));
				 
				 if(FormatHelper.hasContent(primarySecondarySize1) && primarySecondarySize1.contains("hbi")) {
					 branchIDPrimSec=primarySecondarySize1.substring(3, primarySecondarySize1.length());
				 }else if(FormatHelper.hasContent(branchIDPrimSec) && branchIDPrimSec.contains("hbi")) { 
					 branchIDPrimSec=branchIDPrimSec.substring(3, branchIDPrimSec.length());
				 }
				 
				 
				 Double bomLengthParent =  (Double) parentRowMap.get(branchId+bomLengthStr);
				 if(branchIDPrimSec.contains("IP") && pkgInner<=1.0){
					ipDoNotCheckDimensions=true;
				 }
				 utilLogger.debug("ipDoNotCheckDimensions "+ ipDoNotCheckDimensions);
				 if(!branchIDPrimSec.contains("CV") && bomLength==0.0 && bomLengthParent==0.0 && !ipDoNotCheckDimensions) {
					 
					missingBOMSB.append(", Length missing for size: "+size1+" in "+branchIDPrimSec);
					 
				 }
				 Double bomHeightParent =  (Double) parentRowMap.get(branchId+bomHeightStr);
				 if(!branchIDPrimSec.contains("CV") && bomHeight==0.0 &&bomHeightParent==0.0 && !ipDoNotCheckDimensions) {
					
					missingBOMSB.append(", Height missing for size: "+size1+" in "+branchIDPrimSec);
					 
				 }
				 //******************Width-start
				 Double bomWidthParent =  (Double) parentRowMap.get(branchId+bomWidthStr);

				 if(!branchIDPrimSec.contains("CV") && bomWidth==0.0 &&bomWidthParent==0.0 && !ipDoNotCheckDimensions) {
					
					missingBOMSB.append(", Width missing for size: "+size1+" in "+branchIDPrimSec);
					 
				 }
				 //*******************Width-end
				 //pkgInner must for IP, even it is 1, user is supposed to enter as 1
				 Double pkgInnerParent =  (Double) parentRowMap.get(branchId+pkgInnerStr);
				 if(pkgInner ==0.0 && pkgInnerParent ==0.0 && branchIDPrimSec.contains("IP") ) {
					 missingBOMSB.append(", Pkgs/Inner missing for size: "+size1+" in "+branchIDPrimSec);
				 }
				 
				 Double pkgWeightsParent =  (Double) parentRowMap.get(branchId+pkgWeightStr);
	
				 if(pkgWeights ==0.0 && pkgWeightsParent ==0.0 && "EA".equalsIgnoreCase(branchIDPrimSec)) {
					 missingBOMSB.append(", Pkgs/Weights missing for size: "+size1+" in "+branchIDPrimSec);
				 } 
				 Double pkgCasesParent =  (Double) parentRowMap.get(branchId+pkgCasesStr);
				 if(pkgCases ==0.0 && pkgCasesParent ==0.0 && branchIDPrimSec.contains("CV")) {
					 missingBOMSB.append(", Pkgs/Cases missing for size: "+size1+" in "+branchIDPrimSec);
				 }
				 utilLogger.debug("material Str: "+parentRowMap.get(branchId+materialStr));
		
				 if(material ==null && parentRowMap.get(branchId+materialStr)==null &&  branchIDPrimSec.contains("CV")) {
					 missingBOMSB.append(", Material missing for size: "+size1+" in "+branchIDPrimSec);
					 materialCVMessage.put("materialPresentCV", "AlreadyMissingMessage");
					
				 }
	//Check if CV not there in parent row, and not all variation rows having CV, for e.g. S, M, L having primSec CV values but XL primSec is empty
				 //Parent row = No CV
				 //Branch ID is of type CV from some variation row this is found
				 //Variation Row primary Sec is not of type CV for subsequent variation rows.
				
				 if(  branchIDPrimSec.contains("CV")) {
					 List<String> cvArr = Arrays.asList("hbiCV1","hbiCV2","hbiCV3","hbiCV4","hbiCV5","hbiCV6","hbiCV7","hbiCV8");
					 Boolean cvPresentInParentRow=false;
					 for(String cvSingleListKey : cvArr){
						 if(parentRowMap.containsKey(cvSingleListKey)){
							 cvPresentInParentRow = true;
						 }
					 }
					 
					 utilLogger.debug("cvPresentInParentRow: "+cvPresentInParentRow);
						
					 if(!FormatHelper.hasContent(primarySecondarySize1)  && !cvPresentInParentRow ) {
						//primary sec is blank
						 missingBOMSB.append(", CV missing for size: "+size1+" in PrimarySecondary");
					 }
					 //When parent row CV there and also at child row different CV selected
					  if(cvPresentInParentRow && FormatHelper.hasContent(primarySecondarySize1) && primarySecondarySize1.contains("hbiCV")) {
						  utilLogger.debug("***CV selected in Parent and Child check "+ primarySecondarySize1);
						  if(!parentRowMap.containsKey(primarySecondarySize1)){
							  cvParentChild=true;	 
						  }	  
					  } 
				 }
				 
				 
				 if(  branchIDPrimSec.contains("IP")) {
					 List<String> ipArr = Arrays.asList("hbiIP1","hbiIP2","hbiIP3","hbiIP4","hbiIP5");
					 Boolean ipPresentInParentRow=false;
					 for(String ipSingleListKey : ipArr){
						 if(parentRowMap.containsKey(ipSingleListKey)){
							 ipPresentInParentRow = true;
						 }
					 }
					 
					 utilLogger.debug("ipPresentInParentRow: "+ipPresentInParentRow);
						
					 if(!FormatHelper.hasContent(primarySecondarySize1)  && !ipPresentInParentRow ) {
						//primary sec is blank
						 missingBOMSB.append(", IP missing for size: "+size1+" in PrimarySecondary");
					 }
					 //When parent row IP there and also at child row different IP selected
					  if(ipPresentInParentRow && FormatHelper.hasContent(primarySecondarySize1)&& primarySecondarySize1.contains("hbiIP")) {
						  utilLogger.debug("***IP selected in Parent and Child check "+ primarySecondarySize1);
						  if(!parentRowMap.containsKey(primarySecondarySize1)){
							  ipParentChild=true;
							 
						  }
						  
					  }
					 
				 }
				 
				 
			  }else if(branchIdMap.get(branchId)!=null ){//Top row checking, when there is no variation
				  
				  //childRowMap.get(branchId) will be null when only top row exists, hence for double use null check.
				  //To get values from parent row, as directly it might give other older values
				  bomLength = (Double) parentRowMap.get(branchId+bomLengthStr);
				  bomHeight = (Double) parentRowMap.get(branchId+bomHeightStr);
				  bomWidth = (Double) parentRowMap.get(branchId+bomWidthStr);
				  pkgWeights = (Double) parentRowMap.get(branchId+pkgWeightStr);
				  pkgInner = (Double) parentRowMap.get(branchId+pkgInnerStr);
				  pkgCases = (Double) parentRowMap.get(branchId+pkgCasesStr);
				  
				  material = (LCSMaterial) parentRowMap.get(branchId+materialStr);
				 
				  
				  String branchIDPrimSec = branchIdMap.get(branchId);
				  
				  utilLogger.debug("ESLE branchIDPrimSec "+branchIDPrimSec);
				  utilLogger.debug("ELSE branchIDPrimSec contains IP "+ branchIDPrimSec.contains("IP"));
				  utilLogger.debug("ELSE branchIDPrimSec contains CV "+ branchIDPrimSec.contains("CV"));
				 if(branchIDPrimSec.contains("hbi")) {
						 branchIDPrimSec=branchIDPrimSec.substring(3, branchIDPrimSec.length());
				 }
				 if(childRowMap.isEmpty()){
					if(branchIDPrimSec.contains("IP") && pkgInner<=1.0){
						ipDoNotCheckDimensions=true;
					}
					 utilLogger.debug("ESLE childRowMap empty ipDoNotCheckDimensions "+ipDoNotCheckDimensions);
					 if(!branchIDPrimSec.contains("CV") && bomLength==0.0  && !ipDoNotCheckDimensions) {					
						missingBOMValuesMap.put(", Length missing in "+branchIDPrimSec, ", Length missing in "+branchIDPrimSec);						 
					 }					 
					 if(!branchIDPrimSec.contains("CV") && bomHeight==0.0 && !ipDoNotCheckDimensions) {			
						missingBOMValuesMap.put(", Height missing in "+branchIDPrimSec, ", Height missing in "+branchIDPrimSec);						 
					 }					 
					 if(!branchIDPrimSec.contains("CV") && bomWidth==0.0 && !ipDoNotCheckDimensions) {						
					   missingBOMValuesMap.put(", Width missing in "+branchIDPrimSec, ", Width missing in "+branchIDPrimSec);					 
					 }
					 if(pkgInner ==0.0  && branchIDPrimSec.contains("IP") ) {						
						 missingBOMValuesMap.put(", Pkgs/Inner missing in "+branchIDPrimSec, ", Pkgs/Inner missing in "+branchIDPrimSec);
					 }
					 if(pkgWeights ==0.0  && "EA".equalsIgnoreCase(branchIDPrimSec)) {						
						 missingBOMValuesMap.put(", Pkgs/Weights missing in "+branchIDPrimSec, ", Pkgs/Weights missing in "+branchIDPrimSec);
					 } 					
					 if(pkgCases ==0.0  && branchIDPrimSec.contains("CV")) {						
						 missingBOMValuesMap.put(", Pkgs/Cases missing in "+branchIDPrimSec, ", Pkgs/Cases missing in "+branchIDPrimSec);
					 }				
					 if(material ==null &&  branchIDPrimSec.contains("CV")) {					
						missingBOMValuesMap.put(", Material missing in "+branchIDPrimSec, ", Material missing in "+branchIDPrimSec); 
					 }
					 
				 }else{
					 Double bomLengthChild=0.0;
					 Double bomHeightChild=0.0;
					 Double bomWidthChild =0.0;
					 utilLogger.debug("Validation BOM childRowMap:::::::::::::::"+childRowMap);
					 if(branchIDPrimSec.contains("IP") && pkgInner<=1.0){
							ipDoNotCheckDimensions=true;
					 }
					 if( childRowMap.containsKey(branchId+bomLengthStr)) {
					  bomLengthChild =  (Double) childRowMap.get(branchId+bomLengthStr);
					 }
					 
					
					 if(!branchIDPrimSec.contains("CV") && bomLength==0.0 && bomLengthChild==0.0 && !ipDoNotCheckDimensions) {				
						missingBOMValuesMap.put(", Length missing in "+branchIDPrimSec, ", Length missing in "+branchIDPrimSec);						 
					 }
					 
					if( childRowMap.containsKey(branchId+bomHeightStr)) {

					  bomHeightChild =  (Double) childRowMap.get(branchId+bomHeightStr);
					}
					 if(!branchIDPrimSec.contains("CV") && bomHeight==0.0 && bomHeightChild==0.0&& !ipDoNotCheckDimensions) {			
						missingBOMValuesMap.put(", Height missing in "+branchIDPrimSec, ", Height missing in "+branchIDPrimSec);						 
					 }
					if( childRowMap.containsKey(branchId+bomWidthStr)) {
						bomWidthChild =  (Double) childRowMap.get(branchId+bomWidthStr);					 
					}
						
					 if(!branchIDPrimSec.contains("CV") && bomWidth==0.0 && bomWidthChild==0.0 && !ipDoNotCheckDimensions) {						
					   missingBOMValuesMap.put(", Width missing in "+branchIDPrimSec, ", Width missing in "+branchIDPrimSec);						 
					 }				 
					 Double pkgInnerChild =  (Double) childRowMap.get(branchId+pkgInnerStr);
					 if(pkgInner ==0.0 && pkgInnerChild ==null && branchIDPrimSec.contains("IP") ) {						
						 missingBOMValuesMap.put(", Pkgs/Inner missing in "+branchIDPrimSec, ", Pkgs/Inner missing in "+branchIDPrimSec);
					 }					 
					 Double pkgWeightsChild =  (Double) childRowMap.get(branchId+pkgWeightStr);		
					 if(pkgWeights ==0.0 && pkgWeightsChild ==null && "EA".equalsIgnoreCase(branchIDPrimSec)) {						
						 missingBOMValuesMap.put(", Pkgs/Weights missing in "+branchIDPrimSec, ", Pkgs/Weights missing in "+branchIDPrimSec);
					 } 
					 Double pkgCasesChild =  (Double) childRowMap.get(branchId+pkgCasesStr);
					 if(pkgCases ==0.0 && pkgCasesChild ==null && branchIDPrimSec.contains("CV")) {						
						 missingBOMValuesMap.put(", Pkgs/Cases missing in "+branchIDPrimSec, ", Pkgs/Cases missing in "+branchIDPrimSec);
					 }					
					 if(material ==null && childRowMap.get(branchId+materialStr) ==null &&  branchIDPrimSec.contains("CV")) {						
						missingBOMValuesMap.put(", Material missing in "+branchIDPrimSec, ", Material missing in "+branchIDPrimSec); 
					 } 
				 }								 
			  }
		   }
		}
//*******************************************************************************************************************************************		
		if(cvParentChild){
			missingBOMSB.append(", CV selected in Parent and Child row");
			
		}
		if(ipParentChild){
			missingBOMSB.append(", IP selected in Parent and Child row");
			
		}
		
		for(String missingValue: missingBOMValuesMap.keySet()){
			missingBOMSB.append(missingValue);
		}
		if(FormatHelper.hasContent(missingBOMSB.toString())) {
			missingAttributes =missingAttributes.concat(" ,BOM: "+bomName+" : "+missingBOMSB.toString().substring(1));
		}
	
		} catch (Exception e) {
			e.printStackTrace();
			missingAttributes =missingAttributes.concat("Unknown BOM issue");
		}
		return missingAttributes;
	}
	/*
	@SuppressWarnings("unchecked")
	public String zppkPCBOMValidation(FlexBOMPart bomPartObj, Map<String, String> validationAttributes, String missingAttributes){
		
		try {
		Collection<FlexObject> bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
	
		String bomPartName = objectNameDelimiter.concat(bomPartObj.getName());
		String missingAttributesTemp = "";
		String missingAttributeKeys = "";
		String componentSection="";
		utilLogger.debug("bomLinkCollection "+bomLinkCollection.size());
		//
		String primarySec ="";
		if(bomLinkCollection != null && bomLinkCollection.size() > 0)
		{
		   for(FlexObject flexObj : bomLinkCollection)
		   {
			   FlexBOMLink flexBOMLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
			   utilLogger.debug("flexBOMLinkObj hbiPrimarySecondary: "+flexBOMLinkObj.getValue("hbiPrimarySecondary"));
			  componentSection = (String)flexBOMLinkObj.getValue("section");
			  componentSection = flexBOMLinkObj.getFlexType().getAttribute("section").getAttValueList().getValue(componentSection,Locale.getDefault());
			  utilLogger.debug("componentSection "+componentSection);
			  
			  if("Casing BOM".equalsIgnoreCase(componentSection))
			  {    primarySec = (String) flexBOMLinkObj.getValue("hbiPrimarySecondary");
				  
				  missingAttributesTemp= getFlexObjectAttributesValidationStatus(flexBOMLinkObj, validationAttributes, missingAttributes, " ");
				  utilLogger.debug("BOM missingAttributes "+missingAttributes);
				  if(FormatHelper.hasContent(missingAttributesTemp))
				  {
					 missingAttributeKeys = getBOMLinkValidation(missingAttributesTemp, missingAttributeKeys);
				  }
			  }
			}
		}
		
		//
		missingAttributeKeys = missingAttributeKeys.replaceFirst(",", "");
		if(FormatHelper.hasContent(missingAttributeKeys))
		{
			//missingAttributeKeys = sortMissingAttributesByAscendingOrder(flexBOMLinkObj, missingAttributeKeys, "", true);
			missingAttributes = missingAttributes.concat(", Pack Case BOM - ").concat(missingAttributeKeys).concat(" Missing for one or more rows").concat(bomPartName);
		}
		utilLogger.debug("*** ZPPK primarySec "+primarySec);
		if(!FormatHelper.hasContent(primarySec) || !"hbiCV1".equalsIgnoreCase(primarySec)) {

			missingAttributes =missingAttributes.concat(", CV1 should be selected in Pack Case BOM");
		}
		utilLogger.debug("*** ZPPK  missingAttributes "+missingAttributes);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return missingAttributes;
	}*/
	
	/**
	 * This function is using to compare two given strings (one string contains validated attribute information and other string contains raw attribute information using for validation)
	 * @param missingAttributesTemp - String
	 * @param missingAttributeKeys - String
	 * @return  missingAttributeKeys - String
	 * @throws WTException
	 */
	public String getBOMLinkValidation(String missingAttributesTemp, String missingAttributeKeys)
	{
		
		if(missingAttributesTemp.split(",") != null && missingAttributesTemp.split(",").length > 1)
		{
			String[] missingAttributesArray = missingAttributesTemp.split(",");
			for(String attributeDisplayName : missingAttributesArray)
			{
				if(!missingAttributeKeys.contains(attributeDisplayName))
				{
					missingAttributeKeys = missingAttributeKeys.concat(", ").concat(attributeDisplayName);
				}
			}
		}
		else
		{
			if(!missingAttributeKeys.contains(missingAttributesTemp))
			{
				missingAttributeKeys = missingAttributeKeys.concat(", ").concat(missingAttributesTemp);
			}
		}
		
		return missingAttributeKeys;
	}

	/**
	 * This function is using to validate each required attribute value (attribute required for APS and SAP validation) with the default values and populating the missing attributes set
	 * @param attributeKey - String
	 * @param attributeDataType - String
	 * @param objAttributeValue - Object
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public String getAllAttributeValidationStatus(String attributeKey, String attributeDataType, String attributeDisplayName, Object objAttributeValue, String missingAttributes) throws WTException
	{
		// utilLogger.debug("### START HBISPAutomationValidationPlugin.getAllAttributeValidationStatus(attributeKey, attributeDataType, objAttributeValue, missingAttributes) ###");
		
		if("Single List".equals(attributeDataType) || "Multi List".equals(attributeDataType) || "Text".equals(attributeDataType) || "Text Area".equals(attributeDataType) || "Composite".equals(attributeDataType) || "Driven".equals(attributeDataType) || "Derived String".equals(attributeDataType) || "URL".equals(attributeDataType))
		{
			if(!(objAttributeValue != null && FormatHelper.hasContent((String) objAttributeValue)))
			{
				missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
			}	
		}
		else if("Float".equals(attributeDataType) || "Integer".equals(attributeDataType) || "Currency".equals(attributeDataType) || "Sequence".equals(attributeDataType))
		{
			if(!(objAttributeValue != null && (Double)objAttributeValue != 0.0))
			{
				missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
			}	
		}
		else if("Boolean".equals(attributeDataType))
		{
			if(!(objAttributeValue != null))
			{
				missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
			}	
		}
		else if("Date".equals(attributeDataType))
		{
			if(!(objAttributeValue != null))
			{
				missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
			}	
		}
		else if("Object Reference".equals(attributeDataType) || "Object Reference List".equals(attributeDataType))
		{
			if(!(objAttributeValue != null))
			{
				missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
			}	
		}
		else if("User List".equals(attributeDataType))
		{
			if(!(objAttributeValue != null))
			{
				missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
			}	
		}
		
		// utilLogger.debug("### END HBISPAutomationValidationPlugin.getAllAttributeValidationStatus(attributeKey, attributeDataType, objAttributeValue, missingAttributes) ###");
		return missingAttributes;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////					Following functions are using to get All Validation Attributes from Look-up Table					//////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function is using to get APS and SAP validation attributes map for the given business object instance (a map which contains FlexObject, attribute and attribute-data types)
	 * @param businessObject - LCSLifecycleManaged
	 * @return validationAttributesMap - Map<String, Map<String, String>>
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> getValidationAttributesMap(LCSLifecycleManaged businessObject, String requiredFlagIdentifier) throws WTException
	{
		// utilLogger.debug("### START HBISPAutomationValidationPlugin.getValidationAttributesMap(LCSLifecycleManaged businessObject) ###");
		Map<String, Map<String, String>> validationAttributesMap = new HashMap<String, Map<String,String>>();
		Map<String, String> seasonProductAttributeMap = new HashMap<String, String>();
		Map<String, String> productAttributeMap = new HashMap<String, String>();
		Map<String, String> sizeDefinitionAttributeMap = new HashMap<String, String>();
		Map<String, String> colorwaySizeAttributeMap = new HashMap<String, String>();
		Map<String, String> bomLinkAttributeMap = new HashMap<String, String>();
		Map<String, String> bomLinkPCAttributeMap = new HashMap<String, String>();
		
		
		Map<String, String> colorwayAttributeMap = new HashMap<String, String>();
		Map<String, String> putUpMOAAttributeMap = new HashMap<String, String>();  
		Map<String, String> plantExtMOAAttributeMap = new HashMap<String, String>(); 
		String attributeKey = "";
		String attributeDataType = "";
		String attributeName = "";
		String attributeScope = "";
		String dataType = "";
		boolean attributeRequiredFlag = false;
		
		//Get validation table from the given business object, get all rows from the validation table, fetch the data for required attributes which are a part of initial validation
		LCSMOATable validationAttributesMOA = (LCSMOATable) businessObject.getValue(hbiValidationAttributesKey);
		if(validationAttributesMOA != null && validationAttributesMOA.getRows() != null && validationAttributesMOA.getRows().size() > 0)
		{
			Collection<FlexObject> validationAttributesColl = validationAttributesMOA.getRows();
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> validationAttributesColl"+validationAttributesColl);
			for(FlexObject flexObj : validationAttributesColl)
			{
				attributeKey = flexObj.getString(hbiAttributeKey.toUpperCase());
				dataType = flexObj.getString(hbiAttributeDataTypeKey.toUpperCase());
				attributeName = flexObj.getString(hbiAttributeName.toUpperCase());
				attributeDataType = dataType +"~" +attributeName;
				attributeScope = flexObj.getString(hbiScopeKey.toUpperCase());
				attributeRequiredFlag = flexObj.getBoolean(requiredFlagIdentifier.toUpperCase());
				
				//validating the attribute scope (actual FlexObject where attribute resides), required flag, attribute-key  and attribute-data type which are using validation criteria
				if(attributeRequiredFlag && "Product".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					productAttributeMap.put(attributeKey, attributeDataType);	
				}
				else if(attributeRequiredFlag && "SeasonProduct".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					seasonProductAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "Size Definition".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					sizeDefinitionAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "ColorwaySize".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					colorwaySizeAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "BOMLink".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					bomLinkAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "BOMLinkPC".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					bomLinkPCAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "Colorway".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					colorwayAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "PutUpMOAObject".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					//System.out.println(">>>>>>>>>>>>>>>>>>>>>>> putUpMOAAttributeMap attributeKey >>>"+attributeKey +" attributeDataType "+ attributeDataType);
					putUpMOAAttributeMap.put(attributeKey, attributeDataType);
				}
				else if(attributeRequiredFlag && "PlantExtMOAObject".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					//System.out.println(">>>>>>>>>>>>>>>>>>>>>>> plantExtMOAAttributeMap attributeKey >>>"+attributeKey +" attributeDataType "+ attributeDataType);
					plantExtMOAAttributeMap.put(attributeKey, attributeDataType);
				}
			}
			
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> putUpMOAAttributeMap"+putUpMOAAttributeMap);
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> putUpMOAAttributeMap"+plantExtMOAAttributeMap);
			//System.out.println(""+plantExtMOAAttributeMap.get("hbiPrimaryDeliverPlant");
			
			
			//Adding all the map instances (map contains each attribute-key and attribute data type, which are using for data validation from actual FlexObject) to the parent map
			validationAttributesMap.put("Product", productAttributeMap);
			validationAttributesMap.put("SeasonProduct", seasonProductAttributeMap);
			validationAttributesMap.put("Colorway", colorwayAttributeMap);
			validationAttributesMap.put("PutUpMOAObject", putUpMOAAttributeMap); 
			validationAttributesMap.put("PlantExtMOAObject", plantExtMOAAttributeMap); 
			validationAttributesMap.put("SizeDefinition", sizeDefinitionAttributeMap);
			validationAttributesMap.put("ColorwaySize", colorwaySizeAttributeMap);
			validationAttributesMap.put("BOMLink", bomLinkAttributeMap);
			validationAttributesMap.put("BOMLinkPC", bomLinkPCAttributeMap);
			
		}
		
		// utilLogger.debug("### END HBISPAutomationValidationPlugin.getValidationAttributesMap(LCSLifecycleManaged businessObject) ###");
		return validationAttributesMap;
	}
	
	/**
	 * This function is using to get LCSLifecycleManaged object for the given business object name and type path, this business object contains all the validation attributes of APS & SAP
	 * @param businessObjectName - String
	 * @param businessObjectTypePath - String
	 * @return businessObject - LCSLifecycleManaged
	 * @throws WTException
	 */
	public LCSLifecycleManaged findValidationBOByNameAndType(String businessObjectName, String businessObjectTypePath) throws WTException
	{
		// utilLogger.debug("### START HBISPAutomationValidationPlugin.findValidationBOByNameAndType(String businessObjectName, String businessObjectPath) ###");
		//System.out.println(">>>>>>>>>>>>>>businessObjectName and businessObjectTypePath"+businessObjectName+" "+businessObjectTypePath);
		LCSLifecycleManaged businessObject = null;
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath); 
		String nameDBColumn = boFlexTypeObj.getAttribute("name").getColumnDescriptorName();//getColumnName();//.getVariableName();
		//String typeIdPath = String.valueOf(boFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		String typeIdPath = FormatHelper.getNumericObjectIdFromObject(boFlexTypeObj);
		//System.out.println(">>>>>>>>>>>>>>typeIdPath>>>>>>>>>>>>>>>>"+typeIdPath);
		
		//Initializing the PreparedQueryStatement, which is using to get LCSLifecycleManaged object based on the given set of parameters(like FlexTypePath of the object and unique name)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendFromTable(LCSLifecycleManaged.class);
    	statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, nameDBColumn), businessObjectName, Criteria.EQUALS));
    	statement.appendAndIfNeeded();
    	//statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeReference.key.id"), "?", "="), new Long(typeIdPath));
    	//statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "TypeReferenceBranchId.key.id"), "?", "="), new Long(typeIdPath));
    	//branchIdA2typeDefinitionRefe
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "typeDefinitionReference.key.branchId"), "?", "="), new Long(typeIdPath));
    	
    	//Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSLifecycleManaged object, needed to return to the calling function
       // System.out.println(">>>>>>>>>>>>>>>statement>>>>>>>>>>>>>>>>");
    	SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
        	FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
        	businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
        }
    	
		// utilLogger.debug("### END HBISPAutomationValidationPlugin.findValidationBOByNameAndType(String businessObjectName, String businessObjectPath) ###");
		return businessObject;
	}
	
	/**
	 * This function is using to sort the given string (list of missing attributes with comma separator as delimiter) in ascending order and return the formatted string from the function 
	 * @param wtObj - WTObject
	 * @param missingAttributes - String
	 * @param flexObjectName - String
	 * @param skipSpecificFormatting - boolean
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public String sortMissingAttributesByAscendingOrder(WTObject wtObj, String missingAttributes, String flexObjectName, boolean skipSpecificFormatting) throws WTException
	{
		String temp = "";
		
		//Validate the given string (missingAttributes) and proceed only if the given string contains delimiter and has more than one attribute-key in the given string (missingAttributes)
		if(FormatHelper.hasContent(missingAttributes) && missingAttributes.contains(",") && missingAttributes.split(",").length > 1)
		{
			//Iterating on each attribute-key, validating based on the unicode and formating the given missingAttributes to sort the given data in Ascending order and returning
			String[] missingAttributesArray = missingAttributes.split(",");
		    for (int i = 0; i < missingAttributesArray.length; i++)
		    {
		    	temp = missingAttributesArray[i];
		    	for (int j = 0; j < missingAttributesArray.length; j++)
		    	{
		    		//If current attribute-key unicode is larger than the next available attribute-key, interchange the attribute-key location within a string array
		    		if(i == j) continue;
		    		if(temp.compareToIgnoreCase(missingAttributesArray[j]) < 0 )
		    		{
		    			temp = missingAttributesArray[j];
		    			missingAttributesArray[j] = missingAttributesArray[i];
		    			missingAttributesArray[i] = temp;
		    		}
		    	}
		    }
		    
		    //Converting the Arrays of String into a standard string which is needed to return from the function header as calling function needed data in terms of string object
		    missingAttributes = Arrays.toString(missingAttributesArray).trim();
		}
		
		if(!skipSpecificFormatting && FormatHelper.hasContent(missingAttributes))
		{
			String flexObjectType = wtObj.getConceptualClassname();
		    flexObjectType = flexObjectType.substring(flexObjectType.lastIndexOf(".") + 1, flexObjectType.length()).trim().toUpperCase();
		    if(flexObjectType.startsWith("LCS"))
		    	flexObjectType = flexObjectType.replaceFirst("LCS", "");
		    	
		    missingAttributes = flexObjectType.concat(" - (").concat(missingAttributes).concat(flexObjectName).concat(")");
			
		}
		return missingAttributes;
	}
	
	/**
	 * This function is using to get LCSProduct Attribute Validation status from given Product, Season object and validation attributes set, returning attribute validation status
	 * @param spl - LCSSeasonProductLink
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @param fcs 
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public String getPutUpCodeDataFromMOA(LCSProduct productObj,Map<String, String> validationAttributes, String missingAttributes, Boolean fcs) throws WTException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
	{
		
		productObj=(LCSProduct)VersionHelper.getVersion(productObj,"A");
		
		LCSMOATable table=(LCSMOATable)productObj.getValue(hbiPutUpCode);
		
		Collection moaColl=table.getRows();
		//System.out.println(">>>>>>>>>>> getPutUpCodeDataFromMOA moaColl >>>>>>>>>"+moaColl);
		if(moaColl != null && moaColl.size() > 0){
			Iterator moaIterator=moaColl.iterator();
			
			while(moaIterator.hasNext()){
		    
				FlexObject flexObject=(FlexObject)moaIterator.next();
		
			String moaIDA2A2 = flexObject.getString("OID");
			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaIDA2A2); 
			if(moaPUC_Obj != null){
				
				if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null && fcs) {
				 FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
				// WTPartMaster specProductMaster = spec.getSpecOwner();
				 LCSPartMaster specProductMaster = spec.getSpecOwner();
			
				// WTPartMaster productMaster = (WTPartMaster) productObj.getMaster();
				 LCSPartMaster productMaster = (LCSPartMaster) productObj.getMaster();
				
				 if(!FormatHelper.getNumericObjectIdFromObject(productMaster).equals(FormatHelper.getNumericObjectIdFromObject(specProductMaster))) {
					
					 missingAttributes = missingAttributes +", Reference Spec chosen from another product";
				 }else {
					 String erpMaterialType = (String) productObj.getValue(hbiErpMaterialType);
					 if(FormatHelper.hasContent(erpMaterialType)) {
						 if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
							
							 missingAttributes=missingAttributes.concat(validateZPPKBOM(spec));
							
						 }else  {
							 //validateZFRT();
							
							 missingAttributes=missingAttributes.concat(validateZFRT(spec));
							
						 }
					 }
				 }
				
				}
				else {
				//missingAttributes=missingAttributes.concat(",spec mapping for putup is missing");
				}
				
				missingAttributes = getFlexObjectAttributesValidationStatus(moaPUC_Obj, validationAttributes, missingAttributes, " ");
				} //MOA PUC Check	
			 }
        }
		else
		{
			missingAttributes = missingAttributes.concat(", PutUpCode Table Does not have data"); 
		}

    	return missingAttributes;
	}
	
	private String validateZFRT(FlexSpecification spec) {
		
			StringBuilder zfrtBOMSB= new StringBuilder();
			try {
			
			Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
			
			//utilLogger.debug("ZFRT Spec specComponentColl should be 2: "+specComponentColl.size());	
			HashMap specBOMComponent = new HashMap();
		
			Collection pcBOM = new ArrayList();
			
			
			for (FlexBOMPart bomPartObj : specComponentColl) {
				if(bomLinkObjectTypePC.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))) {
						pcBOM.add(bomPartObj);
				}
				
			}
			if(pcBOM.isEmpty()) {
				//utilLogger.debug("No Pack Case BOM for ZFRT ");
				zfrtBOMSB.append(", No Pack Case BOM for ZFRT");
			}
			utilLogger.debug("2.ZFRT missingAttributes "+zfrtBOMSB.toString());
			
			} catch (WTException e) {
				e.printStackTrace();
			}
			return zfrtBOMSB.toString();
		}
	
	private  String validateZPPKBOM(FlexSpecification spec) {
		StringBuilder zppkBOMSB= new StringBuilder();
		try {
		
		Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
		
		HashMap specBOMComponent = new HashMap();
		Collection salesBOM = new ArrayList();
		
		for (FlexBOMPart bomPartObj : specComponentColl) {
		
			if(bomLinkObjectTypeSales.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true)) ) {
					salesBOM.add(bomPartObj);
			}	
		}

		if(salesBOM.isEmpty()) {
			utilLogger.debug("No Sales BOM found for ZPPK ");
			zppkBOMSB.append(", No Sales BOM for ZPPK");
		}
		utilLogger.debug("2.validateZPPK missingAttributes "+zppkBOMSB.toString());
		
		} catch (WTException e) {
			e.printStackTrace();
		}
		return zppkBOMSB.toString();
	}

	/**
	 * This function is using to get LCSProduct Attribute Validation status from given Product, Season object and validation attributes set, returning attribute validation status
	 * @param spl - LCSSeasonProductLink
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public String getPlantExtensionsTableDataFromMOA(LCSProduct productObj,Map<String, String> validationAttributes, String missingAttributes) throws WTException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
	{
		String moaIDA2A2 = "";
		LCSMOAObject moaObject = null;
		LCSMOATable table=(LCSMOATable)productObj.getValue("hbiErpPlantExtensions");
		System.out.println();
		
		/*Map searchmap=new HashMap();
		searchmap.put("hbiPrimaryDeliverPlant", "true");
		Collection coll=(Collection)table.getRows(searchmap);
		if (coll.size()>1) {
	   
		missingAttributes=missingAttributes +"You cannot select more than one Plant as primary plant";
 
		}
		else if (coll.isEmpty()) {
		missingAttributes=missingAttributes +"Atleast one primary Plant should be selected";

		}*/
		Map<String, String> filter = new HashMap();
		//filter.put("hbiPrimaryDeliverPlant", "true");
		filter.put("hbiPrimaryDeliverPlant", "1");
		
		Collection coll= table.getRows(filter);
		//System.out.println(" getPlantExtensionsTableDataFromMOA coll "+ coll);
		//System.out.println("plant--Size-------------------------------"+coll.size());
		utilLogger.debug("plant--Size-------------------------------"+coll.size());
		
		
		if(coll != null && coll.size() > 0)
        {
			Iterator moaItr= coll.iterator();

			while(moaItr.hasNext())
			{
				FlexObject flexObject=(FlexObject)moaItr.next();
				moaIDA2A2 = flexObject.getString("OID");
				moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaIDA2A2); 
				System.out.println(">>>>>>>>>> plant moaObject >>>>> "+moaObject);
				if(moaObject != null){      
					if(moaObject.getValue("hbiPlantName1")!=null) {
						LCSSupplier supplier =(LCSSupplier) moaObject.getValue("hbiPlantName1");
						String sapPLantCode = (String) supplier.getValue("hbiSAPPlantCode");
						utilLogger.debug("sapPLantCode "+sapPLantCode);
						if(!FormatHelper.hasContent(sapPLantCode)) {
							utilLogger.debug("Attribute missing sapPLantCode "+sapPLantCode);
							missingAttributes=missingAttributes +"SAP Plant Code,";
							
						}
					}	
					missingAttributes = getFlexObjectAttributesValidationStatus(moaObject, validationAttributes, missingAttributes, " ");
				}	
			}
        }
		else
		{
			missingAttributes = missingAttributes.concat(",Plant Ext Table Does not have required fields"); 
		}

    	return missingAttributes;
	}

	public static String checkSalesBOMLink(FlexBOMLink flexBOMLinkObj,String missingAttributes)throws WTException
	{
		Integer quantity = ((Double) flexBOMLinkObj.getValue("quantity")).intValue();
		if(quantity == 0)
		{
			String attributeDisplayName = "Quantity";
			missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
		}
		
		String componentColor = (String) flexBOMLinkObj.getValue("hbiErpComponentColor");
		if(!FormatHelper.hasContent(componentColor))
		{
			String attributeDisplayName = "Component Color";
			missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
		}
		
		String componentSize = (String) flexBOMLinkObj.getValue("hbiErpComponentSize");
		if(!FormatHelper.hasContent(componentSize))
		{
			String attributeDisplayName = "Component Size";
			missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
		}
		
		String componentPutUp = (String) flexBOMLinkObj.getValue("hbiErpComponentPutUp");
		if(!FormatHelper.hasContent(componentPutUp))
		{
			String attributeDisplayName = "Component Put Up Code";
			missingAttributes = missingAttributes.concat(", "+attributeDisplayName);
		}
		return missingAttributes;
	}
	
	/**
	 * This function is using to validate attributeKey and WTObject instance-type, based on the validation initialize attribute value to return from a header
	 * @param method - Method
	 * @param wtObj - WTObject
	 * @param attributeKey - String
	 * @return objAttributeValue - Object
	 * @throws WTException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public Object getFlexObjectAttributesValidationStatus(Method method, WTObject wtObj, String attributeKey) throws WTException, InvocationTargetException, IllegalAccessException
	{
		// utilLogger.debug("### START HBISPAutomationValidationPlugin.getSeasonAttributesValidationStatus(method, wtObj, String attributeKey");
		Object objAttributeValue = null;
		
		if("name".equalsIgnoreCase(attributeKey) && wtObj instanceof SKUSize)
		{
			objAttributeValue = "SKUSize Name";
		}
		else
		{
			objAttributeValue = method.invoke(wtObj, attributeKey);
		}
		
		// utilLogger.debug("### END HBISPAutomationValidationPlugin.getSeasonAttributesValidationStatus(method, wtObj, String attributeKey");
		return objAttributeValue;
	}
	
	public void populateLockStatusOnRefObject(LCSSeasonProductLink seasonProductLinkObj, String missingAttributes) throws WTException, WTPropertyVetoException
	{
		//
		LCSProduct productObj = SeasonProductLocator.getProductARev(seasonProductLinkObj);
		LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonProductLinkObj.getSeasonMaster());
		
		Collection<LCSSKU> listOfSKUObjects =new ArrayList<LCSSKU>();
		
		listOfSKUObjects = new HBISPAutomationGenericMethods().getSKUFromProductSeason(productObj,seasonObj);
		//utilLogger.debug(" #### listOfSKUObject #### " +listOfSKUObjects.size());
			
		
	}
	public static String validatePrimaryPutUps(LCSProduct product) throws WTException{
		String missingPUC="";
		
		String erpMaterialType = (String)product.getValue(hbiErpMaterialType);
		if( FormatHelper.hasContent(erpMaterialType)){
			//Check for ZPPK
			if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
				boolean primaryPUCDoesNotExist = findPutUpCodeExists(product,"0911");
				if(primaryPUCDoesNotExist){
					missingPUC =", There must be primary putupcode 0911 for ZPPK";
				}
			}
			//Check for ZFRT
			else if("hbiZFRT".equalsIgnoreCase(erpMaterialType)) {
				boolean primaryPUCDoesNotExist = findPutUpCodeExists(product,"0000");
				if(primaryPUCDoesNotExist){
					missingPUC =", There must be primary putupcode 0000 for ZFRT";
				}
			}
			//Check for ZOFQ
			else if("hbiZOFQ".equalsIgnoreCase(erpMaterialType)) {
				boolean primaryPUCDoesNotExist = findPutUpCodeExists(product,"0000");
				if(primaryPUCDoesNotExist){
					missingPUC =", There must be primary putupcode 0000 for ZOFQ";
				}
			}
		}
		return missingPUC;
	}
	private static boolean findPutUpCodeExists(LCSProduct product, String str) {
		boolean primaryPUCDoesNotExist = true;
		try {
		/*SearchResults moaPUC_SR = LCSMOAObjectQuery.findMOACollectionData((WTPartMaster)product.getMaster(),  
					product.getFlexType().getAttribute(hbiPutUpCode), "LCSMOAObject.createStampA2", true);*/
		SearchResults moaPUC_SR = LCSMOAObjectQuery.findMOACollectionData((LCSPartMaster)product.getMaster(),  
					product.getFlexType().getAttribute(hbiPutUpCode), "LCSMOAObject.createStampA2", true);
		
		if(moaPUC_SR != null && moaPUC_SR.getResultsFound() > 0){
        	Collection<FlexObject> moaPUC_Collection = moaPUC_SR.getResults();
        	for(FlexObject moaPUC_FO : moaPUC_Collection){
    			String moaPUC_IDA2A2 = moaPUC_FO.getString("LCSMOAOBJECT.IDA2A2");
    			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
    			 
    			utilLogger.debug(" ## moaObject " +moaPUC_Obj);
    			
    			if(moaPUC_Obj != null){
    				
    				if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {
    					LCSLifecycleManaged putUpCodeBO = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
    					
    					if(putUpCodeBO != null && FormatHelper.hasContent(putUpCodeBO.getName())){
							String putUpCode = putUpCodeBO.getName();
							if(putUpCode.startsWith(str)){
								primaryPUCDoesNotExist = false;
							}
						}
    				}
    			}
        	}
		}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return primaryPUCDoesNotExist;
	}
	
public static Boolean checkDataMap(HashMap dataMap, String key) {
		
		Boolean putValue = false;
		Object value = dataMap.get(key);
	
		if (value != null) {
			if(value instanceof Double) {
				Double doubleValue = (Double) value;
				
				
				if(doubleValue==0.0) {
					
					putValue=true;
				}
			}else if(value instanceof String){
				
				if(!FormatHelper.hasContent((String) value)) {
				
					putValue=true;
				}
				
			}
		} else {
			putValue=true;
		}
		//System.out.println("dataMap>>>>>>>"+dataMap);
		//System.out.println("putValue>>>>>>>"+putValue);
		return putValue;
		
	}
public String validatePlants(LCSProduct product) 	
{
	String plantError="";
	try {
		Map searchmap=new HashMap();
		searchmap.put("hbiPrimaryDeliverPlant","1");
	    LCSMOATable plantExtMOATable = (LCSMOATable) product.getValue("hbiErpPlantExtensions");
		
	   if( plantExtMOATable.getRows(searchmap).size()>1){
		   plantError=",You Cannot select 2 Primary Plants as Primary Plants"; 
		   
	   }
	   else if(plantExtMOATable.getRows(searchmap).size()==0) {
		   plantError=", Primary Plant is not selected"; 
	   }
	   if (plantExtMOATable.getRows(searchmap).size()==1) {
		   
		   FlexObject fob=(FlexObject)plantExtMOATable.getRows(searchmap).iterator().next();
		   LCSMOAObject moa=(LCSMOAObject)LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+fob.getData("OID"));
	
		   LCSSupplier plant=(LCSSupplier)moa.getValue("hbiPlantName1");
		   		
		   if(plant!=null && plant.getName().contains("OMNI OMNI")) {
			   
	    	   plantError=", OMNI OMNI CHANNEL Cannot be selected as a primary plant"; 
	
		   }
	   }
	} catch (WTException e) {
		e.printStackTrace();
	}
	return plantError;

}
//Used in ZPPK sales BOM extraction for each component style pack case BOM details
	private static HashMap<String, Boolean> checkSalesBOMComponent(LCSProduct componentSP) {
		//FlexBOMPart componentSPSpecBOM =null;
		HashMap<String, Boolean> componentCheck = new HashMap();
		componentCheck.put("cPackCaseBOM", false);
		componentCheck.put("cSalesBOM", false);
		componentCheck.put("cParentPutup", false);
		componentCheck.put("cRefSpec", false);
		componentCheck.put("cZPPK", false);
		String putUptoBeChecked="";
		
		try {
			 componentSP=(LCSProduct)VersionHelper.getVersion(componentSP, "A");
			 String erpMaterialType = "";
			 
				if(componentSP.getValue(hbiErpMaterialType)!=null) {
					erpMaterialType =(String) componentSP.getValue(hbiErpMaterialType);
					if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
						putUptoBeChecked="0911";
						componentCheck.put("cZPPK", true);
					}
					else {
						putUptoBeChecked="0000";

					}
					
				}
				
			
			 LCSMOATable moaPUCTable = (LCSMOATable) componentSP.getValue(hbiPutUpCode);

			  Collection pucMOAColl = moaPUCTable.getRows();

			  Iterator pucCompSPMoaCollItr=pucMOAColl.iterator();
			  while(pucCompSPMoaCollItr.hasNext()) {
				  FlexObject fob=(FlexObject)pucCompSPMoaCollItr.next();
				  LCSMOAObject moaPUC_Obj=(LCSMOAObject)LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+fob.getData("OID"));
				  if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {

					  LCSLifecycleManaged businessObjPUC = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
		
					  String putUpCodeBO = (String) businessObjPUC.getValue(hbiPutUpCode);
					  

					  if(FormatHelper.hasContent(putUpCodeBO) && putUptoBeChecked.equalsIgnoreCase(putUpCodeBO)){
						  componentCheck.put("cParentPutup", true);
						  if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null) {
							  componentCheck.put("cRefSpec", true);
							  FlexSpecification componentSPSpec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
							  Collection<FlexBOMPart> componentSPSpecColl = FlexSpecQuery.getSpecComponents(componentSPSpec, "BOM");
								
							
							  for ( FlexBOMPart bompart : componentSPSpecColl) {
								  if(bomLinkObjectTypeSales.equalsIgnoreCase(bompart.getFlexType().getFullName(true))) {
									  componentCheck.put("cSalesBOM", true);
									
								  }
								  if(bomLinkObjectTypePC.equalsIgnoreCase(bompart.getFlexType().getFullName(true))) {
									  componentCheck.put("cPackCaseBOM", true);
									
								  }
							  }
						  }
						   
					  }
					  
				  }	
				
			  }
		//  }
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		return componentCheck;
	}
	public static Double getDoubleValue(WTObject wtObj, String attributeKey){
		Double value =0.0;
		try {
		 Method method = wtObj.getClass().getMethod("getValue", new Class[] { String.class });
		
		 if (method!=null && method.invoke(wtObj, attributeKey) != null) {
			 Object objAttributeValue = method.invoke(wtObj, attributeKey);
			
			 if(objAttributeValue instanceof Double) {
				 value=(Double) objAttributeValue;
				// System.out.println("double value>>>>>>>>>>>>>>"+value);
				 BigDecimal bd = new BigDecimal( value);
				 bd = bd.setScale(3, RoundingMode.HALF_UP);
				 value= bd.doubleValue();
			 } else if(objAttributeValue instanceof Long) {
				 value=((Long) objAttributeValue).doubleValue();
				// System.out.println("long value>>>>>>>>>>>>>>"+value);
			 }
			 else if(objAttributeValue instanceof String) {
				 value= Double.parseDouble((String) objAttributeValue);
				 BigDecimal bd = new BigDecimal( value);
				 bd = bd.setScale(3, RoundingMode.HALF_UP);
				 value= bd.doubleValue();
				// System.out.println("string value>>>>>>>>>>>>>>"+value);
			 }
		 }
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//System.out.println("final value>>>>>>>>>>>>>>"+value);
		return value;
	}
	
	
	
}	
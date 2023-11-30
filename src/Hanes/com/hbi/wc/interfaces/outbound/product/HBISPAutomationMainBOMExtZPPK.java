package com.hbi.wc.interfaces.outbound.product;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.io.File;
import java.util.TimeZone;
import java.io.FileWriter;

import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.hbi.wc.interfaces.outbound.product.HBIInterfaceUtil;
import com.hbi.wc.interfaces.outbound.product.HBISellingProductExtractor;
import com.hbi.wc.interfaces.outbound.product.HBIUOM;
import com.hbi.wc.util.logger.HBIUtilLogger;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FlexObjectUtil;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.season.LCSSeasonQuery;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.hbi.wc.util.logger.HBIUtilLogger;


/**
 * HBISPAutomationMainBOMExtZPPK.java
 * 
 * This class contains functions for extracting data from Sales BOM sections ,those are 'Components' and 'Packing BOM Instructions' for SP Automation to SAP system.
 * data extracting is specific to the SAP system, And constructing SOAP message for maintianace BOM which will be de classified by IIB system and will be passed to SAP system.
 * @author vijayalaxmi.shetty@Hanes.com
 * @since OCT-26-2018
 */
public class HBISPAutomationMainBOMExtZPPK implements RemoteAccess
{

	public static final String PUT_UP_CODE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE", "hbiPutUpCode");
	public static final String PUT_UP_CODE_MOA = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE_MOA", "hbiPutUpCode");
	public static final String PLANT_EXTENSION_MOA = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PLANT_EXTENSION_MOA", "hbiErpPlantExtensions");
	public static final String PLANT_NAME_1 = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlantName1","hbiPlantName1");
	private static String bomLinkObjectType = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.bomLinkObjectType", "BOM\\Materials\\HBI\\Sales BOM");

	private static String productObjectTypePath = LCSProperties.get("com.hbi.wc.product.interfaces.outbound.processor.productObjectTypePath", "Product\\BASIC CUT & SEW - SELLING");
	
    public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel");
    static Logger utilLogger = HBIUtilLogger.createInstance(HBISPAutomationMainBOMExtZPPK.class, logLevel);		
	
	public void setMaintainanceBOMdataZPPK(LCSProduct producObj,LCSSeason seasonObj, SOAPBody soapBody, SOAPEnvelope soapEnvelope) throws WTException, IOException, SOAPException
	{
		String erpMaterialType = (String) producObj.getValue("hbiErpMaterialType");
	
		if("hbiZPPK".equalsIgnoreCase(erpMaterialType))
		{
			utilLogger.debug("<<<<<<<<<<ZPPK BOM EXTRACTION>>>>>>>");

			Collection<FlexBOMPart> listOfFlexBOMPart = null;
			LCSMOAObject putUpMOAObject = null;
			int uniqueBOMCount = 0;
			Map<String,Object> uniqueBOMLinkMap = new HashMap<String, Object>();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			 
			String hbiPrimaryPlantName = getPrimaryPlantFromPlantExtMOA(producObj);
			if(!FormatHelper.hasContent(hbiPrimaryPlantName))
			hbiPrimaryPlantName = "";
			
			Collection<FlexObject> putUpCodeMOAFlexObj = getPutUpCodeMOAFlexObject(producObj);
			
			for(FlexObject flexObject : putUpCodeMOAFlexObj)
			{
				
				putUpMOAObject = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + flexObject.getData("OID"));
				Map<String, Object>  putUpCodeMOADataMap = getFlexSpecFromPutUpCode(putUpMOAObject);
				String hbiPutUpCode = (String) putUpCodeMOADataMap.get("hbiPutUpCode");
				if(!FormatHelper.hasContent(hbiPutUpCode))
				hbiPutUpCode = "";
				FlexSpecification flexSpecObj = (FlexSpecification) putUpCodeMOADataMap.get("hbiReferenceSpecification");
				String hbiMaterialNumber = (String) putUpCodeMOADataMap.get("hbiMaterialNumber");
				if(!FormatHelper.hasContent(hbiMaterialNumber))
				hbiMaterialNumber = "";
				
				int hbiAlternative =0;
				listOfFlexBOMPart = getFlexBOMPartFromFlexSpec(flexSpecObj);
				SOAPElement maintainanceBOMElement=null;
				if(!listOfFlexBOMPart.isEmpty()){
				maintainanceBOMElement = soapBody.addBodyElement(soapEnvelope.createName("Maintainace_BOM"));

				}
				for(FlexBOMPart flexBOMPartObj :listOfFlexBOMPart) 
				{
					if(bomLinkObjectType.equalsIgnoreCase(flexBOMPartObj.getFlexType().getFullName(true)))
					{
						String hbiSalesBOMCreateDate = dateFormat.format(flexBOMPartObj.getCreateTimestamp());
						String hbiSalesBOMName =flexBOMPartObj.getName();
						boolean ispalette=false;
						
						
						if(hbiSalesBOMName.contains("PALLET")) {
							ispalette=true;	
						}

                       if(ispalette) {
                    	   hbiAlternative=2;
                    	   SOAPElement salesBOMElement=maintainanceBOMElement.addChildElement("Sales_BOM");
   						SOAPElement headerBOMElement=salesBOMElement.addChildElement("BOM_Header");
   												
   						SOAPElement hbiMaterialNumberElement=headerBOMElement.addChildElement("hbiMaterialNumber");
   						hbiMaterialNumberElement.addTextNode(hbiMaterialNumber);
   						headerBOMElement.addChildElement(hbiMaterialNumberElement);  
   						
   						SOAPElement hbiPutUpCodeElement=headerBOMElement.addChildElement("hbiPutUpCode");
   						hbiPutUpCodeElement.addTextNode(hbiPutUpCode);
   						headerBOMElement.addChildElement(hbiPutUpCodeElement);  
   						
   						SOAPElement primaryPlantNameElement=headerBOMElement.addChildElement("hbiPrimaryPlantName");
   						primaryPlantNameElement.addTextNode(hbiPrimaryPlantName);
   						headerBOMElement.addChildElement(primaryPlantNameElement);  
   						
   						SOAPElement hbiAlternativeElement=headerBOMElement.addChildElement("hbiAlternative");
   						hbiAlternativeElement.addTextNode(Integer.toString(hbiAlternative));
   						headerBOMElement.addChildElement(hbiAlternativeElement);
   							
   						SOAPElement hbiSalesBOMCreateDateElement=headerBOMElement.addChildElement("hbiSalesBOMCreateDate");
   						hbiSalesBOMCreateDateElement.addTextNode(hbiSalesBOMCreateDate);
   						headerBOMElement.addChildElement(hbiSalesBOMCreateDateElement);
   						
   						getBOMIndicatorForMaintainceBOM(producObj,seasonObj,salesBOMElement);
   						
   						uniqueBOMLinkMap = getItemLevelDataForMaintainceBOM(flexBOMPartObj,true);
   						
   						Map<String, Object> itemLevelDataMap = getItemLevelDataForCompSection(uniqueBOMLinkMap,salesBOMElement);
   						Integer hbiItemNumber= (Integer)itemLevelDataMap.get("hbiItemNumber");
   						uniqueBOMCount = (int)hbiItemNumber;
   						SOAPElement bomLineItemsElement = (SOAPElement)itemLevelDataMap.get("bomLineItemsElement");
   						System.out.println("  uniqueBOMCount  " +uniqueBOMCount);
   						
   						getItemLevelDataForPackingBOMInstructionSection(flexBOMPartObj,uniqueBOMCount,bomLineItemsElement);
   						
   						Map<String, Integer> uniqueItemDataMap = (Map<String, Integer>) itemLevelDataMap.get("uniqueItemDataMap");
   						SOAPElement bomSKUComponentsElement = getBOMComponentsSKUForCompSection(uniqueItemDataMap, flexBOMPartObj, uniqueBOMCount, salesBOMElement,true);
   						
   						getBOMComponentsSKUForPackingBOMInstructionSection(flexBOMPartObj,uniqueBOMCount,bomSKUComponentsElement);   
                       }
                        hbiAlternative=1;
						SOAPElement salesBOMElement=maintainanceBOMElement.addChildElement("Sales_BOM");
						SOAPElement headerBOMElement=salesBOMElement.addChildElement("BOM_Header");
												
						SOAPElement hbiMaterialNumberElement=headerBOMElement.addChildElement("hbiMaterialNumber");
						hbiMaterialNumberElement.addTextNode(hbiMaterialNumber);
						headerBOMElement.addChildElement(hbiMaterialNumberElement);  
						
						SOAPElement hbiPutUpCodeElement=headerBOMElement.addChildElement("hbiPutUpCode");
						hbiPutUpCodeElement.addTextNode(hbiPutUpCode);
						headerBOMElement.addChildElement(hbiPutUpCodeElement);  
						
						SOAPElement primaryPlantNameElement=headerBOMElement.addChildElement("hbiPrimaryPlantName");
						primaryPlantNameElement.addTextNode(hbiPrimaryPlantName);
						headerBOMElement.addChildElement(primaryPlantNameElement);  
						
						SOAPElement hbiAlternativeElement=headerBOMElement.addChildElement("hbiAlternative");
						hbiAlternativeElement.addTextNode(Integer.toString(hbiAlternative));
						headerBOMElement.addChildElement(hbiAlternativeElement);
							
						SOAPElement hbiSalesBOMCreateDateElement=headerBOMElement.addChildElement("hbiSalesBOMCreateDate");
						hbiSalesBOMCreateDateElement.addTextNode(hbiSalesBOMCreateDate);
						headerBOMElement.addChildElement(hbiSalesBOMCreateDateElement);
						
						getBOMIndicatorForMaintainceBOM(producObj,seasonObj,salesBOMElement);
						
						uniqueBOMLinkMap = getItemLevelDataForMaintainceBOM(flexBOMPartObj,false);
						
						Map<String, Object> itemLevelDataMap = getItemLevelDataForCompSection(uniqueBOMLinkMap,salesBOMElement);
						Integer hbiItemNumber= (Integer)itemLevelDataMap.get("hbiItemNumber");
						System.out.println("hbiItemNumber------------>"+hbiItemNumber);
						uniqueBOMCount = (int)hbiItemNumber;
						SOAPElement bomLineItemsElement = (SOAPElement)itemLevelDataMap.get("bomLineItemsElement");
						System.out.println("  uniqueBOMCount  " +uniqueBOMCount);
						
						getItemLevelDataForPackingBOMInstructionSection(flexBOMPartObj,uniqueBOMCount,bomLineItemsElement);
						
						Map<String, Integer> uniqueItemDataMap = (Map<String, Integer>) itemLevelDataMap.get("uniqueItemDataMap");
						SOAPElement bomSKUComponentsElement = getBOMComponentsSKUForCompSection(uniqueItemDataMap, flexBOMPartObj, uniqueBOMCount, salesBOMElement,false);
						
						getBOMComponentsSKUForPackingBOMInstructionSection(flexBOMPartObj,uniqueBOMCount,bomSKUComponentsElement);
					}
				}
			}
		}
	}
	
	
	/**
	 * This function is using to get BOMLink Attribute Validation status from given Product, Season object and validation attributes set, returning attribute validation status
	 * @param productObj - LCSProduct
	 * @param seasonObj - LCSSeason
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public Collection<FlexBOMPart> getFlexBOMPartFromFlexSpec(FlexSpecification flexSpecObj) throws WTException
	{
		Collection<FlexBOMPart> listOfFlexBOMPart =new ArrayList<FlexBOMPart>();
		Collection<String> collComponents = null;
		FlexBOMPart flexBOMPartObj = null;
		
		if(flexSpecObj != null)
		{
			collComponents =FlexSpecQuery.getSpecComponentsIds(flexSpecObj);
			for(String flexBOMPartID : collComponents)
			{
				if(flexBOMPartID.contains("FlexBOMPart"))
				{
					flexBOMPartObj =(FlexBOMPart)LCSQuery.findObjectById(flexBOMPartID);
					listOfFlexBOMPart.add(flexBOMPartObj);
				}
			}		
		}	
		return listOfFlexBOMPart;
    }
	
	public String getPrimaryPlantFromPlantExtMOA(LCSProduct producObj) throws WTException
	{
		String hbiPrimaryPlantName = "";
		LCSMOAObject plantExtMOAObject = null;
		LCSSupplier hbiPrimaryPlantNameObj = null;
		String hbiPrimaryDeliverPlantValue = "";
		
		Vector sortKeys = new Vector();
		sortKeys.add("SORTINGNUMBER");

		LCSMOATable plantExtTable = (LCSMOATable) producObj.getValue("hbiErpPlantExtensions");
		Collection<FlexObject> plantExtCollection = plantExtTable.getRows();
		plantExtCollection = FlexObjectUtil.sortFlexObjects(plantExtCollection, sortKeys);
		for(FlexObject flexObject : plantExtCollection)
		{
			plantExtMOAObject = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + flexObject.getData("OID"));
			hbiPrimaryDeliverPlantValue = String.valueOf(plantExtMOAObject.getValue("hbiPrimaryDeliverPlant"));
			if("true".equalsIgnoreCase(hbiPrimaryDeliverPlantValue))
			{
				hbiPrimaryPlantNameObj  = (LCSSupplier) plantExtMOAObject.getValue("hbiPlantName1");
				hbiPrimaryPlantName = (String) hbiPrimaryPlantNameObj.getName();
			}
		}
        return hbiPrimaryPlantName;		
	}
	
	
	public void getBOMIndicatorForMaintainceBOM(LCSProduct producObj,LCSSeason seasonObj,SOAPElement salesBOMElement) throws WTException,SOAPException
	{
		LCSSeasonProductLink  seasonProductLinkObj = null;
		String productSychedToSAP = "";
		String hbiNoChangeDoc = "";
		String hbiCreateBOM = "";

		seasonProductLinkObj = LCSSeasonQuery.findSeasonProductLink(producObj,seasonObj);
		productSychedToSAP = (String) seasonProductLinkObj.getValue("hbiSynchedFlag");
		SOAPElement bomIndicatorElement=salesBOMElement.addChildElement("BOM_Indicator");
		if("hbi1".equalsIgnoreCase(productSychedToSAP))
		{
			hbiNoChangeDoc = "X";
			hbiCreateBOM = "";
			
			SOAPElement hbiNoChangeDocElement=bomIndicatorElement.addChildElement("hbiNoChangeDoc");
			hbiNoChangeDocElement.addTextNode(hbiNoChangeDoc);
			bomIndicatorElement.addChildElement(hbiNoChangeDocElement);
						
			SOAPElement hbiCreateBOMElement=bomIndicatorElement.addChildElement("hbiCreateBOM");
			hbiCreateBOMElement.addTextNode(hbiCreateBOM);
			bomIndicatorElement.addChildElement(hbiCreateBOMElement);
		}
		else
		{
			hbiNoChangeDoc = "";
			hbiCreateBOM = "X";
			
			SOAPElement hbiNoChangeDocElement=bomIndicatorElement.addChildElement("hbiNoChangeDoc");
			hbiNoChangeDocElement.addTextNode(hbiNoChangeDoc);
			bomIndicatorElement.addChildElement(hbiNoChangeDocElement);
						
			SOAPElement hbiCreateBOMElement=bomIndicatorElement.addChildElement("hbiCreateBOM");
			hbiCreateBOMElement.addTextNode(hbiCreateBOM);
			bomIndicatorElement.addChildElement(hbiCreateBOMElement);
		}
	}
	
	
	public Collection<FlexObject> getPutUpCodeMOAFlexObject(LCSProduct producObj) throws WTException
	{
		LCSMOAObject putUpMOAObject = null;
		
		Vector sortKeys = new Vector();
		sortKeys.add("SORTINGNUMBER");
		
		LCSMOATable putUpTable = (LCSMOATable) producObj.getValue(PUT_UP_CODE_MOA);
		Collection<FlexObject> putUpCollection = putUpTable.getRows();
		putUpCollection = FlexObjectUtil.sortFlexObjects(putUpCollection, sortKeys);
	
		return putUpCollection;
	}
	
	public Map<String, Object> getFlexSpecFromPutUpCode(LCSMOAObject putUpMOAObject) throws WTException
	{
		LCSLifecycleManaged sapPutUpCodeObj = null;
		String hbiPutUpCode = "";
		String hbiMaterialNumber ="";
		FlexSpecification flexSpecObj = null;
		Map<String, Object> putUpCodeMOADataMap = new HashMap<String, Object>();

		sapPutUpCodeObj  = (LCSLifecycleManaged) putUpMOAObject.getValue(PUT_UP_CODE);
	    if(sapPutUpCodeObj != null)
		{
			hbiPutUpCode = (String) sapPutUpCodeObj.getValue(PUT_UP_CODE);
			hbiPutUpCode = hbiPutUpCode.substring(1);
		}
		flexSpecObj =(FlexSpecification)putUpMOAObject.getValue("hbiReferenceSpecification");
		hbiMaterialNumber = (String) putUpMOAObject.getValue("hbiMaterialNumber");
		
		putUpCodeMOADataMap.put("hbiPutUpCode",hbiPutUpCode);
		putUpCodeMOADataMap.put("hbiReferenceSpecification",flexSpecObj);
		putUpCodeMOADataMap.put("hbiMaterialNumber",hbiMaterialNumber);
		return putUpCodeMOADataMap;
	}
	
	public Map<String,Object> getItemLevelDataForMaintainceBOM(FlexBOMPart flexBOMPartObj, boolean palette) throws WTException
	{
		FlexBOMLink bomLinkObj = null;
		Collection<FlexObject> bomLinkCollection = null;
		Map<String,Object> uniqueBOMLinkMap = new HashMap<String, Object>();
		LCSProduct erpComponentStyleObj = null;
		String hbiSellingStyleNumber = "";
		String hbiErpComponentPutUp = "";
		String styleNumberAndPutUpCode = "";
		String componentSection = "";
		bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		if(bomLinkCollection != null && bomLinkCollection.size() > 0)
		{
			for(FlexObject flexObj : bomLinkCollection)
			{
				bomLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
				componentSection = (String)bomLinkObj.getValue("section");
				if("components".equalsIgnoreCase(componentSection))
				{
					erpComponentStyleObj = (LCSProduct) bomLinkObj.getValue("hbiErpComponentStyle"); 
					if(erpComponentStyleObj != null)
					{
						String compMaterialType=(String) erpComponentStyleObj.getValue("hbiErpMaterialType");

						if("hbiZPPK".equalsIgnoreCase(compMaterialType) && !palette ) {
							HBIUOM uomclass=new HBIUOM();
							LCSMOATable moaPUCTable = (LCSMOATable) erpComponentStyleObj.getValue("hbiPutUpCode");

							 Collection<FlexObject> moaPUC_Collection = moaPUCTable.getRows();
							  moaPUC_Collection = uomclass.sortPutUpCode(moaPUC_Collection, "0911", false);

							 for(FlexObject moaPUC_FO : moaPUC_Collection){
								String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
								LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
								if(moaPUC_Obj.getValue("hbiReferenceSpecification") !=null) {
									FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue("hbiReferenceSpecification");
									Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
									Collection<FlexBOMPart> listOfFlexBOMPart = null;

									if(spec!=null){
										 listOfFlexBOMPart = new HBISPAutomationMainBOMExtZPPK().getFlexBOMPartFromFlexSpec(spec);
										}
									
									for(FlexBOMPart flexBOMPartObj1 :listOfFlexBOMPart) 
									{
										utilLogger.debug("flexBOMPartObj1:::::::::"+flexBOMPartObj1.getName());

										if(bomLinkObjectType.equalsIgnoreCase(flexBOMPartObj1.getFlexType().getFullName(true))) {
											
											Collection<FlexObject> bomLinkCollection1 = null;
										     bomLinkCollection1 = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj1,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();

										 {
											for(FlexObject flexObj1 : bomLinkCollection1)
											{
												FlexBOMLink parentBOMLinkObject = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj1.getString("FLEXBOMLINK.IDA2A2"));
												Collection<FlexBOMLink> variationCollection = null;

												componentSection = (String)parentBOMLinkObject.getValue("section");
												if("components".equalsIgnoreCase(componentSection))
												{


									
														
															erpComponentStyleObj = (LCSProduct) parentBOMLinkObject.getValue("hbiErpComponentStyle"); 
															if(erpComponentStyleObj != null)
															hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber"); 
															else
															{
																erpComponentStyleObj = (LCSProduct) parentBOMLinkObject.getValue("hbiErpComponentStyle"); 
																hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber"); 
															}	
	
															hbiErpComponentPutUp = (String) parentBOMLinkObject.getValue("hbiErpComponentPutUp"); 
															if(!FormatHelper.hasContent(hbiErpComponentPutUp))
															hbiErpComponentPutUp = (String) parentBOMLinkObject.getValue("hbiErpComponentPutUp"); 
															String styleNumberAndPutUpCode1 = "";

															styleNumberAndPutUpCode = hbiSellingStyleNumber+hbiErpComponentPutUp;
															hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber"); 
															hbiErpComponentPutUp = (String) bomLinkObj.getValue("hbiErpComponentPutUp"); 
															styleNumberAndPutUpCode = hbiSellingStyleNumber+hbiErpComponentPutUp;
															uniqueBOMLinkMap.put(styleNumberAndPutUpCode,parentBOMLinkObject);
															
												} 
											}	
										}

											}
									}

									
								}
							 }
						}

						else {	
						hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber"); 
						hbiErpComponentPutUp = (String) bomLinkObj.getValue("hbiErpComponentPutUp"); 
						styleNumberAndPutUpCode = hbiSellingStyleNumber+hbiErpComponentPutUp;
						uniqueBOMLinkMap.put(styleNumberAndPutUpCode,bomLinkObj);
						}
					}	
				} 
			}	
		}

		return uniqueBOMLinkMap;
    }

    public static Map<String, Object> getItemLevelDataForCompSection(Map<String,Object> uniqueBOMLinkMap,SOAPElement salesBOMElement) throws WTException,SOAPException
	{
		int hbiItemNumber = 0;
		Map<String, Object> itemLevelDataMap = new HashMap<String, Object>();
		Map<String, Integer> uniqueItemDataMap = new HashMap<String, Integer>();
		Map<String,Object> uniqueBOMLinkMapComp = new HashMap<String, Object>();

		if(uniqueBOMLinkMap != null && uniqueBOMLinkMap.size() > 0)
		{
			System.out.println("---------------- uniqueBOMLinkMap.size()---------------"+ uniqueBOMLinkMap.size());
			SOAPElement bomLineItemsElement=salesBOMElement.addChildElement("BOM_LineItems");
			SOAPElement componentsSectionElement=bomLineItemsElement.addChildElement("Components_section");
			
			FlexBOMLink bomLinkObj = null;
			String componentSection = "";
			LCSProduct erpComponentStyleObj = null;
			String hbiSellingStyleNumber = "";
			String hbiErpComponentPutUp ="";
			LCSLifecycleManaged attributionCodeObject = null;
			String hbiErpAttributionCode = "";
			for(String uniqueBOMlinkKey : uniqueBOMLinkMap.keySet())
			{
				bomLinkObj = (FlexBOMLink) uniqueBOMLinkMap.get(uniqueBOMlinkKey);
				componentSection = (String)bomLinkObj.getValue("section");
				if("components".equalsIgnoreCase(componentSection))
				{
					SOAPElement bomLineItemElement=componentsSectionElement.addChildElement("BOM_LineItem");
					erpComponentStyleObj = (LCSProduct) bomLinkObj.getValue("hbiErpComponentStyle");
					String compMaterialType=(String) erpComponentStyleObj.getValue("hbiErpMaterialType");		

					if(erpComponentStyleObj != null)
					{
						hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber");  
						attributionCodeObject = (LCSLifecycleManaged) erpComponentStyleObj.getValue("hbiErpAttributionCode");
					}	
					if(attributionCodeObject != null)
					hbiErpAttributionCode = (String) attributionCodeObject.getValue("hbiErpAttributionCode");
					
					hbiErpComponentPutUp = (String) bomLinkObj.getValue("hbiErpComponentPutUp"); 
					if(!FormatHelper.hasContent(hbiErpComponentPutUp))
					hbiErpComponentPutUp = "";
						
					hbiItemNumber = hbiItemNumber+10;
					System.out.println("---------------- hbiItemNumber---------------"+ hbiItemNumber);

								
					/*SOAPElement hbiItemNumberElement=bomLineItemElement.addChildElement("hbiItemNumber");
					hbiItemNumberElement.addTextNode("00"+hbiItemNumber);
					bomLineItemElement.addChildElement(hbiItemNumberElement);*/
						
					SOAPElement hbiSellingStyleNumberElement=bomLineItemElement.addChildElement("hbiSellingStyleNumber");
					hbiSellingStyleNumberElement.addTextNode(hbiSellingStyleNumber);
					bomLineItemElement.addChildElement(hbiSellingStyleNumberElement);
						
					SOAPElement hbiErpAttributionCodeElement=bomLineItemElement.addChildElement("hbiErpAttributionCode");
					hbiErpAttributionCodeElement.addTextNode(hbiErpAttributionCode);
					bomLineItemElement.addChildElement(hbiErpAttributionCodeElement);
						
					SOAPElement hbiErpComponentPutUpElement=bomLineItemElement.addChildElement("hbiErpComponentPutUp");
					hbiErpComponentPutUpElement.addTextNode(hbiErpComponentPutUp);
					bomLineItemElement.addChildElement(hbiErpComponentPutUpElement);
					
					uniqueItemDataMap.put(uniqueBOMlinkKey, hbiItemNumber);
				
				}
			}
			itemLevelDataMap.put("hbiItemNumber",hbiItemNumber);
			itemLevelDataMap.put("bomLineItemsElement",bomLineItemsElement);
			itemLevelDataMap.put("uniqueItemDataMap", uniqueItemDataMap);
			System.out.println("itemLevelDataMap-----------------"+itemLevelDataMap);
		}
		return itemLevelDataMap;
	}
	
	public static void getItemLevelDataForPackingBOMInstructionSection(FlexBOMPart flexBOMPartObj,int uniqueBOMCount,SOAPElement bomLineItemsElement) throws WTException,SOAPException
	{
		FlexBOMLink bomLinkObj = null;
		Collection<FlexObject> bomLinkCollection = null;
		String productionSection= "";
		String partName = "";
		bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		SOAPElement productionSectionElement=bomLineItemsElement.addChildElement("Packing_BOM_Instructions_section");
		if(bomLinkCollection != null && bomLinkCollection.size() > 0)
		{
			for(FlexObject flexObj : bomLinkCollection)
			{
				bomLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
				productionSection = (String)bomLinkObj.getValue("section");
				if("production".equalsIgnoreCase(productionSection))
				{
					SOAPElement bomLineItemElement=productionSectionElement.addChildElement("BOM_LineItem");
					
					partName = (String) bomLinkObj.getValue("partName"); 
					if(!FormatHelper.hasContent(partName))
					partName = "";
					
					uniqueBOMCount = uniqueBOMCount+10;
					
				/*	SOAPElement hbiItemNumberElement=bomLineItemElement.addChildElement("hbiItemNumber");
					hbiItemNumberElement.addTextNode("00"+uniqueBOMCount);
					bomLineItemElement.addChildElement(hbiItemNumberElement);*/
			
					SOAPElement partNameElement=bomLineItemElement.addChildElement("partName");
					partNameElement.addTextNode(partName);
					bomLineItemElement.addChildElement(partNameElement);
				}
			}	
		}
	}

	public static SOAPElement getBOMComponentsSKUForCompSection(Map<String, Integer> uniqueItemDataMap, FlexBOMPart flexBOMPartObj,int uniqueBOMCount,SOAPElement salesBOMElement,boolean pallet) throws WTException,SOAPException
	{
		FlexBOMLink bomLinkObj = null;
		Collection<FlexObject> bomLinkCollection = null;
		String componentSection= "";
		LCSProduct erpComponentStyleObj = null;
		LCSLifecycleManaged attributionCodeObject = null;
		String hbiSellingStyleNumber = "";
		String hbiErpAttributionCode = "";
		String hbiErpComponentPutUp = "";
		String hbiHDRGridValue = "";
		String hbiErpComponentColor = "";
		String hbiErpComponentSize = "";
		double hbiQuantity = 0.0;
		double ZPPKCompQuantity=0.0;
		String hbiZeroQuantity = "";
		String styleNumberAndPutUpCode = "";
		int hbiItemNumber = 0;
		
		SOAPElement bomSKUComponentsElement=salesBOMElement.addChildElement("BOM_SKU_Components");
		SOAPElement componentsSectionElement=bomSKUComponentsElement.addChildElement("Components_section");

		bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		if(bomLinkCollection != null && bomLinkCollection.size() > 0)
		{
			for(FlexObject flexObj : bomLinkCollection)
			{
				bomLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
				componentSection = (String)bomLinkObj.getValue("section");
				if("components".equalsIgnoreCase(componentSection))
				{
					erpComponentStyleObj = (LCSProduct) bomLinkObj.getValue("hbiErpComponentStyle"); 
					
					
					String compMaterialType=(String) erpComponentStyleObj.getValue("hbiErpMaterialType");
					ZPPKCompQuantity=(Double)bomLinkObj.getValue("quantity"); 

					if("hbiZPPK".equalsIgnoreCase(compMaterialType) && !pallet) {
						HBIUOM uomclass=new HBIUOM();
						LCSMOATable moaPUCTable = (LCSMOATable) erpComponentStyleObj.getValue("hbiPutUpCode");

						 Collection<FlexObject> moaPUC_Collection = moaPUCTable.getRows();
						  moaPUC_Collection = uomclass.sortPutUpCode(moaPUC_Collection, "0911", false);	
						  for(FlexObject moaPUC_FO : moaPUC_Collection){
								String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
								LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
								if(moaPUC_Obj.getValue("hbiReferenceSpecification") !=null) {
									FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue("hbiReferenceSpecification");
									Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
									Collection<FlexBOMPart> listOfFlexBOMPart = null;

									if(spec!=null){
										 listOfFlexBOMPart = new HBISPAutomationMainBOMExtZPPK().getFlexBOMPartFromFlexSpec(spec);
										}
									
									for(FlexBOMPart flexBOMPartObj1 :listOfFlexBOMPart) 
									{

										if(bomLinkObjectType.equalsIgnoreCase(flexBOMPartObj1.getFlexType().getFullName(true))) {
											
											Collection<FlexObject> bomLinkCollection1 = null;
										 bomLinkCollection1 = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj1,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();

										 {
											for(FlexObject flexObj1 : bomLinkCollection1)
											{
												FlexBOMLink parentBOMLinkObject = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj1.getString("FLEXBOMLINK.IDA2A2"));
												Collection<FlexBOMLink> variationCollection = null;

												componentSection = (String)parentBOMLinkObject.getValue("section");
												if("components".equalsIgnoreCase(componentSection))
												{


													variationCollection = LCSFlexBOMQuery.getAllLinksForBranch(parentBOMLinkObject);

													for(FlexBOMLink childBOMLinkObj : variationCollection)
													{
														
															erpComponentStyleObj = (LCSProduct) childBOMLinkObj.getValue("hbiErpComponentStyle"); 
															if(erpComponentStyleObj != null)
															hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber"); 
															else
															{
																erpComponentStyleObj = (LCSProduct) parentBOMLinkObject.getValue("hbiErpComponentStyle"); 
																hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber"); 
															}	
																
															hbiErpComponentPutUp = (String) childBOMLinkObj.getValue("hbiErpComponentPutUp"); 
															if(!FormatHelper.hasContent(hbiErpComponentPutUp))
															hbiErpComponentPutUp = (String) parentBOMLinkObject.getValue("hbiErpComponentPutUp"); 
															
															SOAPElement bomSKUComponentElement=componentsSectionElement.addChildElement("BOM_SKU_Component");

															SOAPElement hbiSellingStyleNumberElement=bomSKUComponentElement.addChildElement("hbiSellingStyleNumber");
															hbiSellingStyleNumberElement.addTextNode(hbiSellingStyleNumber);
															bomSKUComponentElement.addChildElement(hbiSellingStyleNumberElement);
															
															if(erpComponentStyleObj != null)
															{
																attributionCodeObject = (LCSLifecycleManaged) erpComponentStyleObj.getValue("hbiErpAttributionCode");
															}	
																
															if(attributionCodeObject != null)
															hbiErpAttributionCode = (String) attributionCodeObject.getValue("hbiErpAttributionCode");
																
																	
															SOAPElement hbiErpAttributionCodeElement=bomSKUComponentElement.addChildElement("hbiErpAttributionCode");
															hbiErpAttributionCodeElement.addTextNode(hbiErpAttributionCode);
															bomSKUComponentElement.addChildElement(hbiErpAttributionCodeElement);
																	
															SOAPElement hbiErpComponentPutUpElement=bomSKUComponentElement.addChildElement("hbiErpComponentPutUp");
															hbiErpComponentPutUpElement.addTextNode(hbiErpComponentPutUp);
															bomSKUComponentElement.addChildElement(hbiErpComponentPutUpElement);
																
															
															hbiErpComponentColor = (String) parentBOMLinkObject.getValue("hbiErpComponentColor"); 
															if(!FormatHelper.hasContent(hbiErpComponentColor))
															hbiErpComponentColor = "";
																
															hbiErpComponentSize = (String) parentBOMLinkObject.getValue("hbiErpComponentSize"); 
															if(!FormatHelper.hasContent(hbiErpComponentSize))
															hbiErpComponentSize = "";
																	
															hbiQuantity = (Double) parentBOMLinkObject.getValue("quantity"); 
															hbiQuantity=hbiQuantity*ZPPKCompQuantity;
															int quantity = (int)hbiQuantity;
															
															
															SOAPElement hbiErpComponentColorElement=bomSKUComponentElement.addChildElement("hbiErpComponentColor");
															if(FormatHelper.hasContent(hbiErpComponentColor)){
					                hbiErpComponentColorElement.addTextNode(hbiErpComponentColor.toUpperCase());
					                            } else{
											hbiErpComponentColorElement.addTextNode(hbiErpComponentColor);

												}
															bomSKUComponentElement.addChildElement(hbiErpComponentColorElement);
															
															

															String hbiErpComponentSizegrid= " ";

															try {

															FlexType businessObjFlexType = FlexTypeCache.getFlexTypeFromPath("Business Object\\Automation Support Tables\\Size Xref");
															Map<String, String> criteriaMap = new HashMap<String, String>();
															erpComponentStyleObj=(LCSProduct)VersionHelper.getVersion(erpComponentStyleObj, "A");
															LCSLifecycleManaged Componentsize=(LCSLifecycleManaged)erpComponentStyleObj.getValue("hbiSellingSizeCategory");
															String searchString=null;
															LCSLifecycleManaged businessObject=null;

															if(Componentsize!=null){
															searchString=Componentsize.getValue("name")+" - "+hbiErpComponentSize;
															businessObject=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
															}
															if (businessObject!=null) {
																
																hbiErpComponentSizegrid=(String)businessObject.getValue("hbiSAPGridSize");
															}
															
															}
															catch (Exception e) {
																
																e.printStackTrace();
															}
															
															SOAPElement hbiErpComponentSizeElement=bomSKUComponentElement.addChildElement("hbiErpComponentSize");
															//hbiErpComponentSizeElement.addTextNode(hbiErpComponentSize);
															hbiErpComponentSizeElement.addTextNode(hbiErpComponentSizegrid);

															bomSKUComponentElement.addChildElement(hbiErpComponentSizeElement);
																	
															SOAPElement quantityElement=bomSKUComponentElement.addChildElement("quantity");
															quantityElement.addTextNode(Integer.toString(quantity));
															bomSKUComponentElement.addChildElement(quantityElement);
																	
															SOAPElement hbiZeroQuantityElement=bomSKUComponentElement.addChildElement("hbiZeroQuantity");
															hbiZeroQuantityElement.addTextNode(hbiZeroQuantity);
															bomSKUComponentElement.addChildElement(hbiZeroQuantityElement);
																
													}		
												} 
											}	
										}

											}
									}

									
								}
							 }
						
					}
					else {
						
						
					
					if(erpComponentStyleObj != null)
					{
						hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber");  
						attributionCodeObject = (LCSLifecycleManaged) erpComponentStyleObj.getValue("hbiErpAttributionCode");
					}	
						
					if(attributionCodeObject != null)
					hbiErpAttributionCode = (String) attributionCodeObject.getValue("hbiErpAttributionCode");
						
					hbiErpComponentPutUp = (String) bomLinkObj.getValue("hbiErpComponentPutUp");
					if(!FormatHelper.hasContent(hbiErpComponentPutUp))
					hbiErpComponentPutUp = "";
	
					hbiHDRGridValue = "0001000";
					styleNumberAndPutUpCode = hbiSellingStyleNumber+hbiErpComponentPutUp;
					//hbiItemNumber = uniqueItemDataMap.get(styleNumberAndPutUpCode);
					
					hbiErpComponentColor = (String) bomLinkObj.getValue("hbiErpComponentColor"); 
					if(!FormatHelper.hasContent(hbiErpComponentColor))
					hbiErpComponentColor = "";
						
					hbiErpComponentSize = (String) bomLinkObj.getValue("hbiErpComponentSize"); 
					if(!FormatHelper.hasContent(hbiErpComponentSize))
					hbiErpComponentSize = "";
							
					hbiQuantity = (Double) bomLinkObj.getValue("quantity"); 
					
					int quantity = (int)hbiQuantity;
						
					if(quantity == 0)
					hbiZeroQuantity = "X";
							
					SOAPElement bomSKUComponentElement=componentsSectionElement.addChildElement("BOM_SKU_Component");
														
					/*SOAPElement hbiItemNumberElement=bomSKUComponentElement.addChildElement("hbiItemNumber");
					hbiItemNumberElement.addTextNode("00"+Integer.toString(hbiItemNumber));
					bomSKUComponentElement.addChildElement(hbiItemNumberElement);*/
					
					SOAPElement hbiSellingStyleNumberElement=bomSKUComponentElement.addChildElement("hbiSellingStyleNumber");
					hbiSellingStyleNumberElement.addTextNode(hbiSellingStyleNumber);
					bomSKUComponentElement.addChildElement(hbiSellingStyleNumberElement);
							
					SOAPElement hbiErpAttributionCodeElement=bomSKUComponentElement.addChildElement("hbiErpAttributionCode");
					hbiErpAttributionCodeElement.addTextNode(hbiErpAttributionCode);
					bomSKUComponentElement.addChildElement(hbiErpAttributionCodeElement);
							
					SOAPElement hbiErpComponentPutUpElement=bomSKUComponentElement.addChildElement("hbiErpComponentPutUp");
					hbiErpComponentPutUpElement.addTextNode(hbiErpComponentPutUp);
					bomSKUComponentElement.addChildElement(hbiErpComponentPutUpElement);
							
					/*SOAPElement hbiHDRGridValueElement=bomSKUComponentElement.addChildElement("hbiHDRGridValue");
					hbiHDRGridValueElement.addTextNode(hbiHDRGridValue);
					bomSKUComponentElement.addChildElement(hbiHDRGridValueElement);*/
					SOAPElement hbiErpComponentColorElement=bomSKUComponentElement.addChildElement("hbiErpComponentColor");
					if(FormatHelper.hasContent(hbiErpComponentColor)){
					hbiErpComponentColorElement.addTextNode(hbiErpComponentColor.toUpperCase());
					}else{
										hbiErpComponentColorElement.addTextNode(hbiErpComponentColor);

					}
					bomSKUComponentElement.addChildElement(hbiErpComponentColorElement);
					
					String hbiErpComponentSizegrid= " ";

					try {

					FlexType businessObjFlexType = FlexTypeCache.getFlexTypeFromPath("Business Object\\Automation Support Tables\\Size Xref");
					Map<String, String> criteriaMap = new HashMap<String, String>();
					erpComponentStyleObj=(LCSProduct)VersionHelper.getVersion(erpComponentStyleObj, "A");
					LCSLifecycleManaged Componentsize=(LCSLifecycleManaged)erpComponentStyleObj.getValue("hbiSellingSizeCategory");
					String searchString=null;
					LCSLifecycleManaged businessObject=null;

					if(Componentsize!=null){
					searchString=Componentsize.getValue("name")+" - "+hbiErpComponentSize;
					businessObject=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
					}
											
								
					
					
					if (businessObject!=null) {
						
						hbiErpComponentSizegrid=(String)businessObject.getValue("hbiSAPGridSize");
					}
					
					}
					catch (Exception e) {
						
						e.printStackTrace();
					}
							
					SOAPElement hbiErpComponentSizeElement=bomSKUComponentElement.addChildElement("hbiErpComponentSize");
					//hbiErpComponentSizeElement.addTextNode(hbiErpComponentSize);
					hbiErpComponentSizeElement.addTextNode(hbiErpComponentSizegrid);

					bomSKUComponentElement.addChildElement(hbiErpComponentSizeElement);
							
					SOAPElement quantityElement=bomSKUComponentElement.addChildElement("quantity");
					quantityElement.addTextNode(Integer.toString(quantity));
					bomSKUComponentElement.addChildElement(quantityElement);
							
					SOAPElement hbiZeroQuantityElement=bomSKUComponentElement.addChildElement("hbiZeroQuantity");
					hbiZeroQuantityElement.addTextNode(hbiZeroQuantity);
					bomSKUComponentElement.addChildElement(hbiZeroQuantityElement);
				}	
				}
			}
		}
		return bomSKUComponentsElement;		
	}

	public static void getBOMComponentsSKUForPackingBOMInstructionSection(FlexBOMPart flexBOMPartObj,int uniqueBOMCount,SOAPElement bomSKUComponentsElement) throws WTException,SOAPException
	{
		FlexBOMLink bomLinkObj = null;
		Collection<FlexObject> bomLinkCollection = null;
		String productionSection= "";
		String partName = "";
		bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		SOAPElement productionSectionElement=bomSKUComponentsElement.addChildElement("Packing_BOM_Instructions_section");
		if(bomLinkCollection != null && bomLinkCollection.size() > 0)
		{
			for(FlexObject flexObj : bomLinkCollection)
			{
				bomLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
				productionSection = (String)bomLinkObj.getValue("section");
				if("production".equalsIgnoreCase(productionSection))
				{
					SOAPElement bomSKUComponentElement=productionSectionElement.addChildElement("BOM_SKU_Component");
					uniqueBOMCount = uniqueBOMCount+10;
									
					
				}
			}	
		}
	}
}
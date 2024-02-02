package com.hbi.wc.interfaces.outbound.product;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
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

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedQuery;
import com.hbi.wc.interfaces.outbound.product.HBIInterfaceUtil;
import com.hbi.wc.interfaces.outbound.product.HBISPAutomationMainBOMExtZPPK;
import com.lcs.wc.color.LCSColor;
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
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.season.LCSSeasonQuery;
import wt.fc.WTObject;
import com.lcs.wc.flexbom.LCSFindFlexBOMHelper;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Mastered;

/**
 * HBISPAutomationMainBOMExtZFRT.java
 * 
 * This class contains functions for extracting data from Sales BOM sections ,those are 'Components' and 'Packing BOM Instructions' for SP Automation to SAP system.
 * data extracting is specific to the SAP system, And constructing SOAP message for maintianace BOM which will be de classified by IIB system and will be passed to SAP system.
 * @author vijayalaxmi.shetty@Hanes.com
 * @since OCT-26-2018
 */
public class HBISPAutomationMainBOMExtZFRT implements RemoteAccess
{
	public static final String PUT_UP_CODE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE", "hbiPutUpCode");
	public static final String PUT_UP_CODE_MOA = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE_MOA", "hbiPutUpCode");
	public static final String PLANT_EXTENSION_MOA = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PLANT_EXTENSION_MOA", "hbiErpPlantExtensions");
	public static final String PLANT_NAME_1 = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlantName1","hbiPlantName1");
	private static String bomLinkObjectType = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.bomLinkObjectType", "BOM\\Materials\\HBI\\Sales BOM");

	private static String productObjectTypePath = LCSProperties.get("com.hbi.wc.product.interfaces.outbound.processor.productObjectTypePath", "Product\\BASIC CUT & SEW - SELLING");
	private static String businessObjectTypePath = LCSProperties.get("com.hbi.wc.product.interfaces.outbound.processor.businessObjectTypePath", "Business Object\\Automation Support Tables\\GenderAge SAP XRef");
	private static String businessObjDerivationTabTypePath = LCSProperties.get("com.hbi.wc.product.interfaces.outbound.processor.businessObjDerivationTabTypePath", "Business Object\\Automation Support Tables\\SAP Derivation Table");
	private static String businessObjCommGroupLookUpTypePath = LCSProperties.get("com.hbi.wc.product.interfaces.outbound.processor.businessObjCommGroupLookUpTypePath", "Business Object\\Automation Support Tables\\SAP Commission Group Lookup");
			
	public void setMaintainanceBOMdataZFRTAndZOFQ(LCSProduct producObj,LCSSeason seasonObj, SOAPBody soapBody, SOAPEnvelope soapEnvelope) throws WTException, IOException, SOAPException,Exception
	{
		String erpMaterialType = (String) producObj.getValue("hbiErpMaterialType");
		
		if("hbiZFRT".equalsIgnoreCase(erpMaterialType) || "hbiZOFQ".equalsIgnoreCase(erpMaterialType))
		{
		LCSLog.debug("Calling new File---------------");
			Collection<FlexBOMPart> listOfFlexBOMPart = null;
			LCSMOAObject putUpMOAObject = null;
			int uniqueBOMCount = 0;
			Map<String,Object> uniqueBOMLinkMap = new HashMap<String, Object>();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			//dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
					 
			String hbiPrimaryPlantName = new HBISPAutomationMainBOMExtZPPK().getPrimaryPlantFromPlantExtMOA(producObj);
			if(!FormatHelper.hasContent(hbiPrimaryPlantName))
			hbiPrimaryPlantName = "";
			
			Collection<FlexObject> putUpCodeMOAFlexObj = new HBISPAutomationMainBOMExtZPPK().getPutUpCodeMOAFlexObject(producObj);
			
			for(FlexObject flexObject : putUpCodeMOAFlexObj)
			{
				putUpMOAObject = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + flexObject.getData("OID"));
				Map<String, Object>  putUpCodeMOADataMap = new HBISPAutomationMainBOMExtZPPK().getFlexSpecFromPutUpCode(putUpMOAObject);
				String hbiPutUpCode = (String) putUpCodeMOADataMap.get("hbiPutUpCode");
				LCSLog.debug("ZFRT flexSpecObj hbiPutUpCode "+hbiPutUpCode);
				if(!FormatHelper.hasContent(hbiPutUpCode))
				hbiPutUpCode = "";
				FlexSpecification flexSpecObj = (FlexSpecification) putUpCodeMOADataMap.get("hbiReferenceSpecification");
				
				//LCSLog.debug("ZFRT flexSpecObj name"+flexSpecObj.getName());
				String hbiMaterialNumber = (String) putUpCodeMOADataMap.get("hbiMaterialNumber");
				if(!FormatHelper.hasContent(hbiMaterialNumber))
				hbiMaterialNumber = "";
				
				int hbiAlternative =0;
				if(flexSpecObj!=null){
				listOfFlexBOMPart = new HBISPAutomationMainBOMExtZPPK().getFlexBOMPartFromFlexSpec(flexSpecObj);
				}
				SOAPElement maintainanceBOMElement=null;
				if(listOfFlexBOMPart!=null && !listOfFlexBOMPart.isEmpty()){
				maintainanceBOMElement = soapBody.addBodyElement(soapEnvelope.createName("Maintainace_BOM"));

				
				for(FlexBOMPart flexBOMPartObj :listOfFlexBOMPart) 
				{
					if(bomLinkObjectType.equalsIgnoreCase(flexBOMPartObj.getFlexType().getFullName(true)))
					{
						String hbiSalesBOMCreateDate = dateFormat.format(flexBOMPartObj.getCreateTimestamp());
						
						hbiAlternative = 1+hbiAlternative;
						
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
						
						new HBISPAutomationMainBOMExtZPPK().getBOMIndicatorForMaintainceBOM(producObj,seasonObj,salesBOMElement);
						
						uniqueBOMLinkMap = getItemLevelDataForMaintainceBOM(flexBOMPartObj);
						
						Map<String, Object> itemLevelDataMap = getItemLevelDataForCompSection(uniqueBOMLinkMap,salesBOMElement);
						if(itemLevelDataMap != null && !itemLevelDataMap.isEmpty() && itemLevelDataMap.size() > 0) 
						{
							Integer hbiItemNumber= (Integer)itemLevelDataMap.get("hbiItemNumber");
							uniqueBOMCount = (int)hbiItemNumber;
							SOAPElement bomLineItemsElement = (SOAPElement)itemLevelDataMap.get("bomLineItemsElement");
							System.out.println(" ZFRT uniqueBOMCount  " +uniqueBOMCount);
						
							getItemLevelDataForPackingBOMInstructionSection(flexBOMPartObj,uniqueBOMCount,bomLineItemsElement);
						
							Map<String, Integer> uniqueItemDataMap = (Map<String, Integer>) itemLevelDataMap.get("uniqueItemDataMap");
							
							SOAPElement bomSKUComponentsElement = getBOMComponentsSKUForCompSection(uniqueItemDataMap, flexBOMPartObj, uniqueBOMCount, salesBOMElement);
						
							getBOMComponentsSKUForPackingBOMInstructionSection(flexBOMPartObj,uniqueBOMCount,bomSKUComponentsElement);
						}	
					}	
				}
				}
			}
        }
	}
				
	public Map<String,Object> getItemLevelDataForMaintainceBOM(FlexBOMPart flexBOMPartObj) throws WTException
	{
		FlexBOMLink parentBOMLinkObject = null;
		Collection<FlexObject> bomLinkCollection = null;
		Collection<FlexBOMLink> variationCollection = null;
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
				parentBOMLinkObject = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
				componentSection = (String)parentBOMLinkObject.getValue("section");
				if("components".equalsIgnoreCase(componentSection))
				{
					variationCollection = LCSFlexBOMQuery.getAllLinksForBranch(parentBOMLinkObject);
					for(FlexBOMLink childBOMLinkObj : variationCollection)
					{
						if(FormatHelper.hasContent(childBOMLinkObj.getDimensionName()))
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
							styleNumberAndPutUpCode = hbiSellingStyleNumber+hbiErpComponentPutUp;
							uniqueBOMLinkMap.put(styleNumberAndPutUpCode,childBOMLinkObj);
						}
					}		
				} 
			}	
		}
		System.out.println(" ## ZFRT uniqueBOMLinkMap ## "+uniqueBOMLinkMap);
		return uniqueBOMLinkMap;
    }

    public static Map<String, Object> getItemLevelDataForCompSection(Map<String,Object> uniqueBOMLinkMap,SOAPElement salesBOMElement) throws WTException,SOAPException
	{
		int hbiItemNumber = 0;
		Map<String, Object> itemLevelDataMap = new HashMap<String, Object>();
		Map<String, Integer> uniqueItemDataMap = new HashMap<String, Integer>();
		if(uniqueBOMLinkMap != null && uniqueBOMLinkMap.size() > 0)
		{
			SOAPElement bomLineItemsElement=salesBOMElement.addChildElement("BOM_LineItems");
			SOAPElement componentsSectionElement=bomLineItemsElement.addChildElement("Components_section");
			
			FlexBOMLink childBOMLinkObj = null;
			FlexBOMLink primaryFlexBOMLinkObj = null;
			String componentSection = "";
			LCSProduct erpComponentStyleObj = null;
			String hbiSellingStyleNumber = "";
			String hbiErpComponentPutUp ="";
			LCSLifecycleManaged attributionCodeObject = null;
			String hbiErpAttributionCode = "";
			for(String uniqueBOMlinkKey : uniqueBOMLinkMap.keySet())
			{
				childBOMLinkObj = (FlexBOMLink) uniqueBOMLinkMap.get(uniqueBOMlinkKey);
				if(FormatHelper.hasContent(childBOMLinkObj.getDimensionName()))
				{
					primaryFlexBOMLinkObj = LCSFlexBOMQuery.getToplevelLinkForBranch(childBOMLinkObj);
					componentSection = (String)primaryFlexBOMLinkObj.getValue("section");
					if("components".equalsIgnoreCase(componentSection))
					{
						SOAPElement bomLineItemElement=componentsSectionElement.addChildElement("BOM_LineItem");
						erpComponentStyleObj = (LCSProduct) childBOMLinkObj.getValue("hbiErpComponentStyle"); 
						if(erpComponentStyleObj != null)
						hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber");  
						else
						{
							erpComponentStyleObj = (LCSProduct) primaryFlexBOMLinkObj.getValue("hbiErpComponentStyle"); 
							hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber");  
						}
						attributionCodeObject = (LCSLifecycleManaged) erpComponentStyleObj.getValue("hbiErpAttributionCode");
						if(attributionCodeObject != null)
						hbiErpAttributionCode = (String) attributionCodeObject.getValue("hbiErpAttributionCode");
						if(!FormatHelper.hasContent(hbiErpAttributionCode))
						hbiErpAttributionCode = "";
						
						hbiErpComponentPutUp = (String) childBOMLinkObj.getValue("hbiErpComponentPutUp"); 
						if(!FormatHelper.hasContent(hbiErpComponentPutUp))
						hbiErpComponentPutUp = (String) primaryFlexBOMLinkObj.getValue("hbiErpComponentPutUp"); 
						if(!FormatHelper.hasContent(hbiErpComponentPutUp))
						hbiErpComponentPutUp = "";
						
						hbiItemNumber = hbiItemNumber+10;
							
						
						
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
			}
			itemLevelDataMap.put("hbiItemNumber",hbiItemNumber);
			itemLevelDataMap.put("bomLineItemsElement",bomLineItemsElement);	
			itemLevelDataMap.put("uniqueItemDataMap", uniqueItemDataMap);
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
						
					/*SOAPElement hbiItemNumberElement=bomLineItemElement.addChildElement("hbiItemNumber");
					hbiItemNumberElement.addTextNode("00"+uniqueBOMCount);
					bomLineItemElement.addChildElement(hbiItemNumberElement);*/
			
					SOAPElement partNameElement=bomLineItemElement.addChildElement("partName");
					partNameElement.addTextNode(partName);
					bomLineItemElement.addChildElement(partNameElement);
				}
			}	
		}
	}

	public static SOAPElement getBOMComponentsSKUForCompSection(Map<String, Integer> uniqueItemDataMap, FlexBOMPart flexBOMPartObj,int uniqueBOMCount,SOAPElement salesBOMElement) throws WTException,SOAPException
	{
		FlexBOMLink parentBOMLinkObj = null;
		Collection<FlexObject> bomLinkCollection = null;
		Collection<FlexBOMLink> variationCollection = null;
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
		String hbiZeroQuantity = "";
		String styleNumberAndPutUpCode = "";
		int hbiItemNumber = 0;
		
		SOAPElement bomSKUComponentsElement=salesBOMElement.addChildElement("BOM_SKU_Components");
		SOAPElement componentsSectionElement=bomSKUComponentsElement.addChildElement("Components_section");
		
		LCSLog.debug("ZFRT flexBOMPartObj name: "+flexBOMPartObj.getName());
		LCSLog.debug("1ZFRT flexBOMPartObj : "+flexBOMPartObj);
		LCSLog.debug("ZFRT flexBOMPartObj isLatestIteration: "+flexBOMPartObj.isLatestIteration());
		
		flexBOMPartObj=(FlexBOMPart)VersionHelper.latestIterationOf(flexBOMPartObj);
		LCSLog.debug("2ZFRT flexBOMPartObj : "+flexBOMPartObj);
		
		bomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(flexBOMPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		if(bomLinkCollection != null && bomLinkCollection.size() > 0)
		{
			for(FlexObject flexObj : bomLinkCollection)
			{//LCSLog.debug("ZFRT variationCollection  flexObj "+flexObj);
				parentBOMLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FLEXBOMLINK.IDA2A2"));
				
				LCSLog.debug("ZFRT parentBOMLinkObj  isDropped "+parentBOMLinkObj.isDropped());
				
				
				componentSection = (String)parentBOMLinkObj.getValue("section");
				if("components".equalsIgnoreCase(componentSection))
				{
				
					variationCollection = LCSFlexBOMQuery.getAllLinksForBranch(parentBOMLinkObj);
					
					LCSLog.debug("ZFRT   variationCollection "+variationCollection.size());
					if(variationCollection != null && variationCollection.size() > 0)
					{
						for(FlexBOMLink bomLinkObj : variationCollection)
						{	
							
							LCSLog.debug("ZFRT variationCollection  bomLinkObj "+bomLinkObj);
							LCSLog.debug("ZFRT variationCollection  isDropped "+bomLinkObj.isDropped());
							
							if(FormatHelper.hasContent(bomLinkObj.getDimensionName()) && !bomLinkObj.isDropped() )
							{
								erpComponentStyleObj = (LCSProduct) bomLinkObj.getValue("hbiErpComponentStyle"); 
								if(erpComponentStyleObj != null)
								{
									hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber");  
									attributionCodeObject = (LCSLifecycleManaged) erpComponentStyleObj.getValue("hbiErpAttributionCode");
								}
								else
								{
									erpComponentStyleObj = (LCSProduct) parentBOMLinkObj.getValue("hbiErpComponentStyle"); 
									hbiSellingStyleNumber = (String) erpComponentStyleObj.getValue("hbiSellingStyleNumber");  
									attributionCodeObject = (LCSLifecycleManaged) erpComponentStyleObj.getValue("hbiErpAttributionCode");
								}		
								if(attributionCodeObject != null)
								hbiErpAttributionCode = (String) attributionCodeObject.getValue("hbiErpAttributionCode");
								if(!FormatHelper.hasContent(hbiErpAttributionCode))
								hbiErpAttributionCode = "";
								
								hbiErpComponentPutUp = (String) bomLinkObj.getValue("hbiErpComponentPutUp"); 
								
								if(!FormatHelper.hasContent(hbiErpComponentPutUp))
								hbiErpComponentPutUp = (String) parentBOMLinkObj.getValue("hbiErpComponentPutUp"); 
								if(!FormatHelper.hasContent(hbiErpComponentPutUp))
								hbiErpComponentPutUp = "";
								System.out.println(" <<< ZFRT  hbiErpComponentPutUp >>>" +hbiErpComponentPutUp);
								
								hbiHDRGridValue = "0001000";
									
								styleNumberAndPutUpCode = hbiSellingStyleNumber+hbiErpComponentPutUp;
								hbiItemNumber = uniqueItemDataMap.get(styleNumberAndPutUpCode);
								hbiErpComponentColor = (String) bomLinkObj.getValue("hbiErpComponentColor"); 
								if(!FormatHelper.hasContent(hbiErpComponentColor))
								hbiErpComponentColor = (String) parentBOMLinkObj.getValue("hbiErpComponentColor"); 
								if(!FormatHelper.hasContent(hbiErpComponentColor))
								hbiErpComponentColor = "";
								System.out.println(" <<< ZFRT  hbiErpComponentColor >>>" +hbiErpComponentColor);
								
								hbiErpComponentSize = (String) bomLinkObj.getValue("hbiErpComponentSize"); 
								if(!FormatHelper.hasContent(hbiErpComponentSize))
								hbiErpComponentSize = (String) parentBOMLinkObj.getValue("hbiErpComponentSize"); 
								if(!FormatHelper.hasContent(hbiErpComponentSize))
								hbiErpComponentSize = "";
								System.out.println(" <<< ZFRT  hbiErpComponentSize >>>" +hbiErpComponentSize);
									
								hbiQuantity = (Double) bomLinkObj.getValue("quantity"); 
								if(hbiQuantity==0.0) {
									hbiQuantity =(Double) parentBOMLinkObj.getValue("quantity");
								}
								System.out.println(" <<< ZFRT  hbiErpComponentSize >>>" +hbiQuantity);

								int quantity = (int)hbiQuantity;
								if(quantity == 0)
								hbiZeroQuantity = " ";
								
								System.out.println(" <<< ZFRT  hbiErpComponentColor >>>" +VersionHelper.latestIterationOf((Mastered) bomLinkObj.getColorDimension()));
								
								String hbiHdrColSize=" ";
								String hbiHdrCol=" ";

								if( bomLinkObj.getColorDimension()!=null) {
								
									LCSSKU sku=(LCSSKU)VersionHelper.latestIterationOf((Mastered) bomLinkObj.getColorDimension());
									
									System.out.println(" sku--------------------" +sku.getName());
									sku=(LCSSKU)VersionHelper.getVersion(sku, "A");
									LCSColor color = (LCSColor) sku.getValue("color");
									String colorcode = (String) color.getValue("hbiColorwayCode");
	
									LCSProduct headerProduct=sku.getProduct();
									hbiHdrCol=(String) color.getValue("hbiColorwayCode");
									headerProduct=(LCSProduct)VersionHelper.getVersion(headerProduct, "A");
									LCSLifecycleManaged spsize=(LCSLifecycleManaged)headerProduct.getValue("hbiSellingSizeCategory");
									
									
									LCSLog.debug("bomLinkObj.getSize1() "+bomLinkObj.getSize1());
									
									
									String searchString=null;
									LCSLifecycleManaged bo=null;
									if(spsize!=null) {
										searchString=spsize.getValue("name")+" - "+bomLinkObj.getSize1();
										System.out.println("searchString"+searchString);
										 bo=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
									
										if(bo!=null) {
											hbiHdrColSize=(String) bo.getValue("hbiSAPGridSize");	
										}
									}
								}
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
								hbiErpComponentColorElement.addTextNode(hbiErpComponentColor);
								bomSKUComponentElement.addChildElement(hbiErpComponentColorElement);
								String hbiErpComponentSizegrid= " ";

								try {

								FlexType businessObjFlexType = FlexTypeCache.getFlexTypeFromPath("Business Object\\Automation Support Tables\\Size Xref");
								Map<String, String> criteriaMap = new HashMap<String, String>();
								erpComponentStyleObj=(LCSProduct)VersionHelper.getVersion(erpComponentStyleObj, "A");
								LCSLifecycleManaged Componentsize=(LCSLifecycleManaged)erpComponentStyleObj.getValue("hbiSellingSizeCategory");
								String ComponentsizeString=FormatHelper.getNumericObjectIdFromObject(Componentsize);
								
                               
									//LCSLifecycleManaged businessObject=null;
								//
								String searchString=null;
								LCSLifecycleManaged businessObject=null;

								if(Componentsize!=null){
								searchString=Componentsize.getValue("name")+" - "+hbiErpComponentSize;
								businessObject=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
								}
											
								/*String apscodeatt=businessObjFlexType.getAttribute("hbiAPSSizeCode").getVariableName();
								String hbiAPSSizeCategoryAtt=businessObjFlexType.getAttribute("hbiAPSSizeCategory").getVariableName();
								criteriaMap.put(hbiAPSSizeCategoryAtt, ComponentsizeString);
								criteriaMap.put(apscodeatt, hbiErpComponentSize);

								System.out.println("criteriaMap:::::::::::::::"+criteriaMap);
							//	SearchResults results = new LCSLifecycleManagedQuery().findLifecycleManagedsByCriteria(criteriaMap, businessObjFlexType, null, null, null);
								PreparedQueryStatement statement = new PreparedQueryStatement();
								statement.appendSelectColumn(
										new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
								statement.appendFromTable(LCSLifecycleManaged.class);
								statement.appendAndIfNeeded();
								statement.appendCriteria(
										new Criteria(new QueryColumn(LCSLifecycleManaged.class, businessObjFlexType.getAttribute("hbiAPSSizeCode").getVariableName()), "?", Criteria.EQUALS),
										hbiErpComponentSize);
								statement.appendAndIfNeeded();
								statement.appendCriteria(
										new Criteria(new QueryColumn(LCSLifecycleManaged.class, businessObjFlexType.getAttribute("hbiAPSSizeCategory").getVariableName()), "?", Criteria.EQUALS),
										ComponentsizeString);
								SearchResults results = LCSQuery.runDirectQuery(statement);

								System.out.println("results::::::bo:::::::::"+results);
									
								
								if(results != null && results.getResultsFound() > 0)
								{
									FlexObject boflexObj = (FlexObject) results.getResults().firstElement();
									businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+boflexObj.getData("LCSLIFECYCLEMANAGED.IDA2A2"));
								}*/
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
								
								SOAPElement hbiHdrColelement=bomSKUComponentElement.addChildElement("hbiHdrCol");
								hbiHdrColelement.addTextNode(hbiHdrCol);
								bomSKUComponentElement.addChildElement(hbiHdrColelement);
								
								SOAPElement hbiHdrColSizeElement=bomSKUComponentElement.addChildElement("hbiHdrColSize");
								hbiHdrColSizeElement.addTextNode(hbiHdrColSize);
								bomSKUComponentElement.addChildElement(hbiHdrColSizeElement);
							}	
						}	
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
					
					/*SOAPElement hbiItemNumberElement=bomSKUComponentElement.addChildElement("hbiItemNumber");
					hbiItemNumberElement.addTextNode("00"+uniqueBOMCount);
					bomSKUComponentElement.addChildElement(hbiItemNumberElement);*/
				}
			}	
		}
	}
}
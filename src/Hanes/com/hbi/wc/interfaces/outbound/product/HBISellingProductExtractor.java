package com.hbi.wc.interfaces.outbound.product;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map;
import java.util.Vector;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.client.ClientContext;

import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.skusize.SKUSize;
import com.lcs.wc.skusize.SKUSizeMaster;
import com.lcs.wc.skusize.SKUSizeQuery;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.hbi.wc.interfaces.outbound.product.mq.HBISellingProductSAPRequestQueueProcessor;
import com.hbi.wc.interfaces.outbound.product.translation.HBISellingProductTransformationProcessor;
import com.hbi.wc.interfaces.outbound.product.translation.HBISellingProductTransformationProcessorUtility;

import com.hbi.wc.interfaces.outbound.product.HBISPAutomationMainBOMExtZFRT;
import com.hbi.wc.interfaces.outbound.product.HBISPAutomationMainBOMExtZPPK;
import com.hbi.wc.util.logger.HBIUtilLogger;
import com.ibm.icu.util.StringTokenizer;
import com.ibm.mq.MQException;

/**
 * @author UST
 *
 */
public class HBISellingProductExtractor {
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
	private static String PRODUCT_TYPE_NAME = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.PRODUCT_TYPE_NAME");
	private static String MOA_TYPE_NAME = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.MOA_TYPE_NAME");
	private static String SAP_Integration = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.SAP_Integration");
	private static String General_Attributes = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.General_Attributes");
	private static String Marketing_BI = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.Marketing_BI");
	private static final String className = HBISellingProductExtractor.class.getName();
	//private static final Logger LOGGER = LogR.getLogger(className);
	public static final String PUT_UP_CODE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE",
			"hbiPutUpCode");
	public static final String PUT_UP_CODE_MOA = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE_MOA", "hbiPutUpCode");
	public static final String PLANT_EXTENSION_MOA = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.PLANT_EXTENSION_MOA", "hbiErpPlantExtensions");
	public static final String PLANT_NAME_1 = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlantName1",
			"hbiPlantName1");
	public static final String PLANT_TYPE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlantType",
			"hbiPlantType");
	public static final String PRIMARY_DELIVERY_PLANT = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiPrimaryDeliverPlant", "hbiPrimaryDeliverPlant");
	public static final String MAX_LOT_SIZE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiMaxLotSize",
			"hbiMaxLotSize");
	public static final String PLANNED_DELIVERY_TIME = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiPlannedDelTime", "hbiPlannedDelTime");
	public static final String PROCUREMENT_TYPE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProcurementType", "hbiProcurementType");
	public static final String SPECIAL_PROCUREMENT = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiSpecialProcurement", "hbiSpecialProcurement");
	public static final String TOTAL_REP_LEAD_TIME = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiTotalRepLeadTme", "hbiTotalRepLeadTme");
	public static final String COLORWAY_CODE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiColorwayCode", "hbiColorwayCodeNew");
	public static final String NRF_FAMILY = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.vrdNRFFamily",
			"vrdNRFFamily");
	public static final String NRF_GROUP = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.vrdNRFGroupName",
			"vrdNRFGroupName");

	public static final String SALES_ORG = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiSalesOrg",
			"hbiSalesOrg");
	public static final String SALES_ORG2 = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiSalesOrg2",
			"hbiSalesOrg2");
	public static final String ERP_COLOR_DIVISION = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpColorDivision", "hbiErpColorDivision");
	public static final String ERP_MATERIAL_TYPE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpMaterialType", "hbiErpMaterialType");
	public static final String ATTRIBUTION_CODE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpAttributionCode", "hbiErpAttributionCode");
	public static final String MATERIAL_GRID = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbimaterialGrid", "hbimaterialGrid");
	public static final String PLANT_CODE = LCSProperties.get("com.hbi.wc.interfaces.outbound.factory.hbiSAPPlantCode",
			"hbiSAPPlantCode");
	public static final String SELLING_SIZE_CAT = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiSellingSizeCategory", "hbiSellingSizeCategory");
	public static final String SAP_GRID_SIZE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.sizexref.hbiSAPGridSize", "hbiSAPGridSize");
	public static final String ERP_LIKE_STYLE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpLikeStyle", "hbiErpLikeStyle");
	public static final String SELLING_STYLE_NUMBER = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiSellingStyleNumber", "hbiSellingStyleNumber");
	public static final String SPECIAL_PROCUREMENT_TYPE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiSpecialProcurementType", "hbiSpecialProcurementType");
	public static final String SELLING_PRODUCT_STATUS = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiSellingProductStatus", "hbiSellingProductStatus");

	public static final String SELLING_PRODUCT_STATUS_ESU_IN_P = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiESUInProgress", "hbiESUInProgress");

	public static final String OTC_SYNCHED_ALREADY = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiOTCSynchedAlready", "hbiOTCSynchedAlready");

	public static final String SKU_ALREADY_SENT = LCSProperties.get("com.hbi.wc.interfaces.outbound.sku.HbiAlreadySent",
			"HbiAlreadySent");

	public static final String SKU_ALREADY_SENT_YES = LCSProperties.get("com.hbi.wc.interfaces.outbound.sku.hbiYes",
			"hbiYes");

	public static final String APS_SHORT_DESC = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.sizexref.hbiAPSShortSizeDescription", "hbiAPSShortSizeDescription");

	public static final String APS_SIZE_CODE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.sizexref.hbiAPSSizeCode", "hbiAPSSizeCode");
	public static final String NRF_SIZE = LCSProperties.get("com.hbi.wc.interfaces.outbound.sizexref.hbiNRFSize",
			"hbiNRFSize");
	public static final String ERP_DIVISION = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiErpDivision",
			"hbiErpDivision");
	public static final String ERP_LEGACY_DIVISION = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiErpLegacyDivision",
			"hbiErpLegacyDivision");
	
	
	

	
	private static FlexType PRODUCT_TYPE = null;
	private static FlexType MOA_TYPE = null;

	private static Map productAttMap = new HashMap();
	private static Map productseasonAttMap = new HashMap();
	
	 public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel","DEBUG");
    static Logger utilLogger = HBIUtilLogger.createInstance(HBISellingProductExtractor.class, logLevel);		

	/**
	 * @param spl
	 * @throws Exception
	 * @Description This method will be called from Workflow , When called it will
	 *              extract the selling style from PLM to SAP
	 */
	@SuppressWarnings("unchecked")
	public static String export(LCSSeasonProductLink spl) throws Exception {
		LCSSeasonProductLink theSPL = null;
		try {
			long start = System.currentTimeMillis();

			utilLogger.debug("<<<<<<<<<<DATA EXTRACTION STARTED FROM THE FILE HBI>>>>>>>" + start);
			System.out.println("<<<<<<<<<<DATA EXTRACTION STARTED FROM THE FILE HBI>>>>>>>" + start);
			PRODUCT_TYPE = FlexTypeCache.getFlexTypeFromPath(PRODUCT_TYPE_NAME);
			MOA_TYPE = FlexTypeCache.getFlexTypeFromPath(MOA_TYPE_NAME);

			Collection allAttCollection = new ArrayList();
			// below attribute groups data will be extracted
			allAttCollection.addAll(SortHelper
					.sort(PRODUCT_TYPE.getAttributeGroup(SAP_Integration, "PRODUCT", "PRODUCT"), "attDisplay"));
			allAttCollection.addAll(SortHelper
					.sort(PRODUCT_TYPE.getAttributeGroup(General_Attributes, "PRODUCT", "PRODUCT"), "attDisplay"));
			allAttCollection.addAll(SortHelper.sort(
					PRODUCT_TYPE.getAttributeGroup(General_Attributes, "PRODUCT-SEASON", "PRODUCT"), "attDisplay"));
			allAttCollection.addAll(
					SortHelper.sort(PRODUCT_TYPE.getAttributeGroup(Marketing_BI, "PRODUCT", "PRODUCT"), "attDisplay"));
			allAttCollection.addAll(SortHelper
					.sort(PRODUCT_TYPE.getAttributeGroup(Marketing_BI, "PRODUCT-SEASON", "PRODUCT"), "attDisplay"));

			Iterator itr = allAttCollection.iterator();
			while (itr.hasNext()) {
				FlexTypeAttribute att = (FlexTypeAttribute) itr.next();
				if (!att.isAttHidden()) {
					if ("PRODUCT".equals(att.getAttScope())) {
						productAttMap.put(att.getAttKey(), att.getAttKey());
					} else {
						productseasonAttMap.put(att.getAttKey(), att.getAttKey());

					}
				}
			}

			theSPL = spl;
			LCSProduct product = SeasonProductLocator.getProductARev(theSPL);

			Map finalDatamap = new LinkedHashMap();
			product = (LCSProduct) VersionHelper.latestIterationOf(product);
			String productID = Long.toString(product.getBranchIdentifier());
			LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(theSPL.getSeasonMaster());
			String seasonID = Long.toString(season.getBranchIdentifier());

			finalDatamap.put("hbiTransactionID", productID + "-" + seasonID);
			finalDatamap.put("hbiMaterialNumber", product.getValue("hbiMasterGrid"));

			String soapMessage = "";

			SOAPElement childElement = null;
			MessageFactory messageFactory = MessageFactory.newInstance();

			SOAPMessage soapMessageObj = messageFactory.createMessage();
			SOAPPart soapPart = soapMessageObj.getSOAPPart();
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			SOAPBody soapBody = soapEnvelope.getBody();

			childElement = soapBody.addChildElement("hbiTransactionID");
			childElement.addTextNode(productID + "-" + seasonID);


			// --Set Product data
			setProductdata(productAttMap, product, finalDatamap, PRODUCT_TYPE, soapBody);

			// --Set Product Season data
			setProductSeasondata(productseasonAttMap, theSPL, finalDatamap, PRODUCT_TYPE);
			// --Set Plant Extension data
			setPlantTableData(PLANT_EXTENSION_MOA, product, finalDatamap, soapEnvelope, soapBody);
			// --Set SKU Size data
			setSkuSizeData(theSPL, finalDatamap, soapEnvelope, soapBody);
			// --Set Sales org data
			setSalesOrgData(product, finalDatamap, soapEnvelope, soapBody);

			// --set UOM data
			String SPStatus = (String) product.getValue(SELLING_PRODUCT_STATUS);
						System.out.println("=======================SPStatus---------------------------"+SPStatus);

			HBIUOM.startUOMExtraction(product, season, soapBody, soapEnvelope);
			
			

			// -- set BOM data
			new HBISPAutomationMainBOMExtZPPK().setMaintainanceBOMdataZPPK(product, season, soapBody, soapEnvelope);
			new HBISPAutomationMainBOMExtZFRT().setMaintainanceBOMdataZFRTAndZOFQ(product, season, soapBody,
					soapEnvelope);

			soapMessageObj.saveChanges();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			soapMessageObj.writeTo(baos);
			soapMessage = new String(baos.toByteArray(), "UTF-8");

			// --publish the message to IIB Queue
			new HBISellingProductSAPRequestQueueProcessor().plmSellingProductRequest(soapMessage);
			// -- print soap message
			printSoapMessageFile(soapMessage, product);

			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");

			utilLogger.debug("<<<<<<<<<<DATA EXTRACTION ENDED>>>>>>>" + end);
			utilLogger.debug("Execution time is :-->" + formatter.format((end - start) / 1000d) + " seconds");
			System.out.println("<<<<<<<<<<DATA EXTRACTION ENDED>>>>>>>" + end);
			System.out.println("Execution time is :-->" + formatter.format((end - start) / 1000d) + " seconds");

		} catch (WTException e) {
			e.printStackTrace();
		} catch (javax.xml.soap.SOAPException e) {
			e.printStackTrace();

		} catch (java.io.IOException e) {
			e.printStackTrace();

		}/* catch (MQException e) {
			e.printStackTrace();

		}*/
		catch (Exception e) {
			e.printStackTrace();
		}
		return "completed";

	}

	/**
	 * @param product
	 * @param finalDatamap
	 * @param soapEnvelope
	 * @param soapBody
	 * @throws WTException
	 */
	private static void setSalesOrgData(LCSProduct product, Map finalDatamap, SOAPEnvelope soapEnvelope,
			SOAPBody soapBody) throws WTException {
		// TODO Auto-generated method stub
		FlexTypeAttribute hbiSalesOrgatt = PRODUCT_TYPE.getAttribute(SALES_ORG);
		FlexTypeAttribute hbiSalesOrg2att = PRODUCT_TYPE.getAttribute(SALES_ORG2);

		String hbiSalesOrg = getAttListValue(hbiSalesOrgatt, product);
		String stringValue = hbiSalesOrg2att.getStringValue(product);
		String hbiSalesOrg2val = null;
		String hbiSalesOrg2 = null;
		hbiSalesOrg2 = hbiSalesOrg;

		if (FormatHelper.hasContent(hbiSalesOrg2)) {
			hbiSalesOrg2val = com.lcs.wc.util.MOAHelper.parseOutDelimsLocalized(stringValue, true,
					hbiSalesOrg2att.getAttValueList(), ClientContext.getContext().getLocale());
			StringTokenizer hbiSalesOrg2tokenizer = new StringTokenizer(hbiSalesOrg2val, ",");

			while (hbiSalesOrg2tokenizer.hasMoreTokens()) {
				String hbiSalesOrg2token = hbiSalesOrg2tokenizer.nextToken();
				hbiSalesOrg2token = hbiSalesOrg2token.trim();
				if (!hbiSalesOrg2token.equals(hbiSalesOrg)) {
					if (FormatHelper.hasContent(hbiSalesOrg2)) {
						hbiSalesOrg2 = hbiSalesOrg2 + "," + hbiSalesOrg2token;

					} else {
						hbiSalesOrg2 = hbiSalesOrg2token;
					}

				}

			}

			try {
				SOAPElement element1 = soapBody.addBodyElement(soapEnvelope.createName("SalesOrg"));
				SOAPElement childElement = null;
				StringTokenizer tokenizer = new StringTokenizer(hbiSalesOrg2, ",");
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					childElement = element1.addChildElement("hbiSalesOrg");
					childElement.addTextNode(token);
					element1.addChildElement(childElement);

				}
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @param hm
	 * @param product
	 * @param finalDatamap
	 * @param PRODUCT_TYPE
	 * @param soapBody
	 * @throws WTPropertyVetoException
	 */
	private static void setProductdata(Map hm, LCSProduct product, Map finalDatamap, FlexType PRODUCT_TYPE,
			SOAPBody soapBody) throws WTPropertyVetoException {

		Set set = hm.entrySet();
		SOAPElement childElement = null;

		// Get an iterator
		Iterator i = set.iterator();
		try {
			// Display elements
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				FlexTypeAttribute att = PRODUCT_TYPE.getAttribute((String) me.getKey());
				String attType = att.getAttVariableType();
				Object value = null;
				String mapkey = me.getKey().toString();
				if (!att.isAttHidden() && !SALES_ORG.equals(mapkey)) {

					if (isAttTypeNum(attType)) {
						String numberAsString = new String();
						//Double val = (Double) product.getValue(me.getKey().toString());
						try{
							Long val = (Long) product.getValue(me.getKey().toString());
							numberAsString = val.toString();
						}catch(Exception e){
							Double val = (Double) product.getValue(me.getKey().toString());
							numberAsString = val.toString();
						}
						
						//numberAsString = val.toString();
						value = numberAsString;
						
					}

					else if ("choice".equals(attType) || "driven".equals(attType)) {
						if (product.getValue(me.getKey().toString()) != null) {
							value = getAttListValue(att, product);
							String valueKey = (String) product.getValue(me.getKey().toString());
							//Long valueKey1 = (Long) product.getValue(me.getKey().toString());
							//String valueKey = valueKey1.toString();
							if (ERP_COLOR_DIVISION.equals(me.getKey().toString())&&FormatHelper.hasContent(valueKey)) {
								value = valueKey.substring(1, 4);
							}

							if (ERP_MATERIAL_TYPE.equals(me.getKey().toString())) {
								value = getAttListValue(att, product);

							}
							/*if (ERP_MATERIAL_TYPE.equals(me.getKey().toString())) {
								value = getAttListValue(att, product);

							}
							if (ERP_MATERIAL_TYPE.equals(me.getKey().toString())) {
								value = getAttListValue(att, product);

							}*/
							if(ERP_DIVISION.equals(me.getKey().toString())){
							value=valueKey.replace("hbi", "").trim();
							SOAPElement hbiErpLegacyDivisionElement = soapBody.addChildElement(ERP_LEGACY_DIVISION);
				               //Map divisionMap=new HBISellingProductTransformationProcessor().findDivisonFromDerivationTable(product,value.toString(),true);

				               //String legacy= (String) divisionMap.get("hbiLegacyDivision");
							//changes done by Wipro Team for 664076 to send across Legacy Division-START
				           	Map divisionMap=new HBISellingProductTransformationProcessor().findDivisonFromDerivationTable(product,null,false);
				           
				            
				           
				          //changes done by Wipro Team for 664076 to send across Legacy Division -END

                                               
								if (divisionMap != null) {

									if (!divisionMap.containsKey("Error")) {

										String legacy = (String) divisionMap.get("hbiLegacyDivision");
										if (legacy != null) {
											LCSLog.debug("Legacy is  ::::::::::::::::" + legacy);
											hbiErpLegacyDivisionElement.addTextNode(legacy);
										}

									} else {

										String legacyfromTable = (String) new HBISellingProductTransformationProcessorUtility()
												.findLegacyDivisonFromDerivationTable(product, null, false);
										if (legacyfromTable != null)

										{

											LCSLog.debug("Legacy is  ::::::::::::::::" + legacyfromTable);
											hbiErpLegacyDivisionElement.addTextNode(legacyfromTable);
										}

									}

								}

								else {
									hbiErpLegacyDivisionElement.addTextNode("");

								}
						}

						}

					}

					else if ("object_ref".equals(attType) || "object_ref_list".equals(attType)) {
						if (ERP_LIKE_STYLE.equals((String) me.getKey().toString())
								&& product.getValue(me.getKey().toString()) != null) {
							LCSProduct bo = (LCSProduct) product.getValue(me.getKey().toString());
							value = bo.getValue("productName");

						}

						else if (ATTRIBUTION_CODE.equals((String) me.getKey().toString())
								&& product.getValue(me.getKey().toString()) != null) {
							LCSLifecycleManaged bo = (LCSLifecycleManaged) product.getValue(me.getKey().toString());
							if (bo != null) {
								value = bo.getValue(ATTRIBUTION_CODE);
							}

						} else if (product.getValue(me.getKey().toString()) != null) {
							LCSLifecycleManaged bo = (LCSLifecycleManaged) product.getValue(me.getKey().toString());
							value = bo.getValue("name");

						}

					} else if ("moaList".equals(attType)) {

						String display = "";
						String stringValue = att.getStringValue(product);
						if (FormatHelper.hasContent(stringValue))
							value = com.lcs.wc.util.MOAHelper.parseOutDelimsLocalized(stringValue, true,
									att.getAttValueList(), ClientContext.getContext().getLocale());

					} else {
						value = product.getValue(me.getKey().toString());
						if (SELLING_STYLE_NUMBER.equals(mapkey) && value != null) {
							value = value.toString().toUpperCase();
						}

					}
					try {
					
					if(mapkey.equals("hbiSilhouetteGroupNEW")){
					   mapkey="hbiSilhouetteGroup";
					}
					if(mapkey.equals("hbiErpGPCFamily")){
						   mapkey="hbiErpGpcFamily";
					}
					if(mapkey.equals("hbiErpGPCClass")){
						   mapkey="hbiErpGpcClass";
					}
						childElement = soapBody.addChildElement(mapkey);
               
						if (value != null) {
							childElement.addTextNode(value.toString());

						} else {
							childElement.addTextNode(" ");

						}
					} catch (SOAPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			
			String hbiMasterGrid=(String)product.getValue("hbiMasterGrid");
			SOAPElement childElement1 = soapBody.addChildElement("hbiMasterGrid");
			SOAPElement childElement2 = soapBody.addChildElement("hbiMasterGridExist");

			if(FormatHelper.hasContent(hbiMasterGrid)){
				childElement1.addTextNode(hbiMasterGrid);
				childElement2.addTextNode("X");
			}

		} catch (WTException e) {
		   
              e.printStackTrace();

		}
		catch (SOAPException e) {
			   
            e.printStackTrace();

		}

	}

	/**
	 * @param hm
	 * @param product
	 * @param finalDatamap
	 * @param PRODUCT_TYPE
	 */
	private static void setProductSeasondata(Map hm, LCSSeasonProductLink product, Map finalDatamap,
			FlexType PRODUCT_TYPE) {

		Set set = hm.entrySet();

		// Get an iterator
		Iterator i = set.iterator();
		try {
			// Display elements
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				FlexTypeAttribute att = PRODUCT_TYPE.getAttribute((String) me.getKey());
				String attType = att.getAttVariableType();
				Object value = null;
				if (!att.isAttHidden()) {
					// value=(Object)product.getValue(me.getKey().toString());
					//changes done by Wipro Team -START
					if (isAttTypeNum(attType)) {
						value = product.getValue(me.getKey().toString());
						if (value instanceof Double) {
							value = ((Double) value).toString();
							//value = val.toString();
						}else if (value instanceof Long) {
							value = ((Long)value).toString();	
							//value = val.toString();
						}
						//Double val = (Double) product.getValue(me.getKey().toString());
						
//						String numberAsString = val.toString();
//						value = numberAsString;
						//changes done by Wipro Team -END						
					}

					else if ("choice".equals(attType) || "driven".equals(attType)) {
						if (product.getValue(me.getKey().toString()) != null) {
							value = getAttListValue(att, product);

						}

					}

					else if ("object_ref".equals(attType) || "object_ref_list".equals(attType)) {

						if (product.getValue(me.getKey().toString()) != null) {
							LCSLifecycleManaged bo = (LCSLifecycleManaged) product.getValue(me.getKey().toString());
							value = bo.getValue("name");

						}

					} else if ("moaList".equals(attType)) {

						String display = "";
						String stringValue = att.getStringValue(product);
						if (FormatHelper.hasContent(stringValue))
							value = com.lcs.wc.util.MOAHelper.parseOutDelimsLocalized(stringValue, true,
									att.getAttValueList(), ClientContext.getContext().getLocale());

					} else {
						value = product.getValue(me.getKey().toString());

					}
					if (value != null)
						finalDatamap.put(me.getKey(), value.toString());
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param moaKey
	 * @param product
	 * @param finalDatamap
	 * @param soapBody
	 * @param soapEnvelope
	 */
	private static void setPlantTableData(String moaKey, LCSProduct product, Map finalDatamap,
			SOAPEnvelope soapEnvelope, SOAPBody soapBody) {
		
		System.out.println(">>>> inside setPlantTableData >>>>>>>>>");
		// Get an iterator
		try {
			SOAPElement element2 = soapBody.addBodyElement(soapEnvelope.createName("PLANT_EXTENSION_MOA"));
			//SOAPElement childElement = null;
			
			// Display elements
			LCSMOATable table = (LCSMOATable) product.getValue(moaKey);
		//	System.out.println( ">>>>>>>>>>>>>>>>>>>>>> table "+ table);
			Map filter = new HashMap();
			filter.put(PRIMARY_DELIVERY_PLANT, "1");
			// Temp fix for price controll issue
			Collection coll = table.getRows(filter);
		//	System.out.println(">>>>>>>>>>>>>>>>>>>> coll"+ coll.size());
			coll = sortPlant(coll);
			Iterator itr = coll.iterator();
			//StringBuffer sf = new StringBuffer();
			while (itr.hasNext()) {
				SOAPElement element1 = soapBody.addBodyElement(soapEnvelope.createName("PLANT_EXTENSION_MOA_ROW"));

				FlexObject fob = (FlexObject) itr.next();
				LCSMOAObject moa = (LCSMOAObject) LCSQuery
						.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + fob.getData("OID"));
				com.lcs.wc.supplier.LCSSupplier plant = (com.lcs.wc.supplier.LCSSupplier) moa.getValue("hbiPlantName1");
				String plantName = null;
				String plantCode = null;

				if (plant != null) {
					plantName = (String) plant.getValue("name");
					plantCode = (String) plant.getValue(PLANT_CODE);
				}
				if(!"OMNI OMNI CHANNEL".equals(plantName)) {
					String primary = " ";
					//primary = (String) moa.getValue(PRIMARY_DELIVERY_PLANT);
					primary = String.valueOf(moa.getValue(PRIMARY_DELIVERY_PLANT));
					System.out.println(">>>>>>>>>>>>>>> primary "+ primary);
					//if("1".equals(primary)) primary ="true";else primary = "false";
					//System.out.println(">>>>>>>>>>>>>>>>> primary after setting "+ primary);
					if (FormatHelper.hasContent(primary) && "true".equalsIgnoreCase(primary)) {
						primary = "true";
						//primary = "1";
						System.out.println(">>>>>>>>>>>>>>>>> primary in if "+ primary);
					} else {

						primary = "false";
						System.out.println(">>>>>>>>>>>>>>>>> primary in else "+ primary);
						//primary = "0";

					}

					createSoapElement(element1, PLANT_NAME_1, plantName);
					createSoapElement(element1, PLANT_CODE, plantCode);
					createSoapElement(element1, PLANT_TYPE,
							getAttListValue(moa.getFlexType().getAttribute(PLANT_TYPE), moa));
					createSoapElement(element1, PRIMARY_DELIVERY_PLANT, primary);
					createSoapElement(element1, MAX_LOT_SIZE, MOA_TYPE.getAttribute(MAX_LOT_SIZE).getAttValueList()
							.getValue((String) moa.getValue(MAX_LOT_SIZE), null));
					//Double val = (Double) moa.getValue(PLANNED_DELIVERY_TIME);
					Long val = (Long)moa.getValue(PLANNED_DELIVERY_TIME);
					String numberAsString = val.toString();
					 
					createSoapElement(element1, PLANNED_DELIVERY_TIME, numberAsString);
					createSoapElement(element1, PROCUREMENT_TYPE, (String) moa.getValue(PROCUREMENT_TYPE));

					com.lcs.wc.foundation.LCSLifecycleManaged pt = (com.lcs.wc.foundation.LCSLifecycleManaged) moa
							.getValue(SPECIAL_PROCUREMENT);
					String hbiSpecialProcurement = " ";
					if (pt != null) {
						hbiSpecialProcurement = (String) pt.getValue(SPECIAL_PROCUREMENT_TYPE);
					}
					createSoapElement(element1, SPECIAL_PROCUREMENT, hbiSpecialProcurement);

					//Double hbiTotalRepLeadTme = (Double) moa.getValue(TOTAL_REP_LEAD_TIME);
					Long hbiTotalRepLeadTme = (Long) moa.getValue(TOTAL_REP_LEAD_TIME);
					
					String numberAsString1 = hbiTotalRepLeadTme.toString();

					createSoapElement(element1, TOTAL_REP_LEAD_TIME, numberAsString1);
					element2.addChildElement(element1);

				}
			}

		} catch (WTException e) {
			e.printStackTrace();
		} catch (SOAPException ex) {

		}

	}

	/**
	 * @param element1
	 * @param key
	 * @param val
	 */
	private static void createSoapElement(SOAPElement element1, String key, String val) {
		try {
			SOAPElement childElement = element1.addChildElement(key);
			if (val!=null) {
				childElement.addTextNode(val);
			} else {
				childElement.addTextNode(" ");

			}
			element1.addChildElement(childElement);

		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void setSkuSizeData(LCSSeasonProductLink productLink, Map finalDatamap, SOAPEnvelope soapEnvelope,
			SOAPBody soapBody) {
		try {
			LCSProduct product = SeasonProductLocator.getProductARev(productLink);
			boolean zppk = false;
		
    	String erpMaterialType = (String) product.getValue("hbiErpMaterialType");
		 String zppkSize = (String) product.getValue("hbiZPPKSize");

		
	
    	if(FormatHelper.hasContent(erpMaterialType) && "hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
    		zppk=true;
    	}
			HBIGetRoundValue hbiroundval = new HBIGetRoundValue();
			HashMap<String, String> roundValMAp = hbiroundval.getRoundVal(product);
			utilLogger.debug("Outside While roundValMAp "+roundValMAp);
			
			SOAPElement elementa = soapBody.addBodyElement(soapEnvelope.createName("COLORWAY_SIZE_TABLE"));
			//SOAPElement elementc = null;

			LCSSeason season = SeasonProductLocator.getSeasonRev(productLink);
			FlexType skuSizeType = product.getFlexType().getReferencedFlexType("SKU_SIZE_TYPE_ID");
			SizingQuery query = new SizingQuery();
			SearchResults psd = query.findPSDByProductAndSeason(product);
			Collection viewableSKUs = new ProductHeaderQuery().findSKUs(product, null, season, true, false);
			Iterator viewableSKUitr = viewableSKUs.iterator();

			Collection psdcoll = psd.getResults();
			Iterator itr = psdcoll.iterator();
			String ida2a2 = null;
			ProductSizeCategory psd1 = null;
			LCSSourcingConfigMaster sourceMaster = null;

			if (psdcoll != null && psdcoll.size() == 1) {
				while (itr.hasNext()) {
					FlexObject flexObj = (FlexObject) itr.next();
					ida2a2 = flexObj.getString("PRODUCTSIZECATEGORY.IDA2A2");
					psd1 = (ProductSizeCategory) LCSQuery
							.findObjectById("OR:com.lcs.wc.sizing.ProductSizeCategory:" + ida2a2);

				}
			}

			StringBuffer sf = new StringBuffer();
			// productAttributesDataMap.put("COLORWAY_SIZE_TABLE","hbiColorwayCode:YD2,hbiColorwayname:a,hbiSize:38B=ada-36D=bbb-36C=ccc|~*~|hbiColorwayCode:3GT,hbiColorwayname:b,hbiSize:38B=q-36D=p-36C=r|~*~|");

			while (viewableSKUitr.hasNext()) {

				Collection viewableSKU = new ArrayList();
				LCSSKU sku = (LCSSKU) viewableSKUitr.next();
				viewableSKU.add(sku);
				Collection skuSizes = new ArrayList();
				skuSizes = new SKUSizeQuery().findViewableSKUSizesForPSC(new HashMap(), new Vector(), skuSizeType, psd1,
						season, null, null, null, null, false, true, false, false, viewableSKU).getResults();
				Iterator skuSizesItr = skuSizes.iterator();
				sku = (LCSSKU) VersionHelper.latestIterationOf(sku);
				LCSColor color = (LCSColor) sku.getValue("color");

				String colorcode = null;
				String colorGroup = null;
				String colorfamily = null;
				if (color != null) {
					colorcode = (String) color.getValue(COLORWAY_CODE);

					colorGroup = (String) color.getValue(NRF_GROUP);
					colorGroup = getAttListValue(color.getFlexType().getAttribute(NRF_GROUP), color);
					colorfamily = (String) color.getValue(NRF_FAMILY);
					colorfamily = getAttListValue(color.getFlexType().getAttribute(NRF_FAMILY), color);

				}

				String SAPGrid = " ";
             if(!skuSizes.isEmpty()) {
 				SOAPElement elementb = soapBody.addBodyElement(soapEnvelope.createName("COLORWAY_SIZE"));

				createSoapElement(elementb, "hbiColorwayCode", colorcode);
				createSoapElement(elementb, NRF_FAMILY, colorfamily);
				createSoapElement(elementb, NRF_GROUP, colorGroup);
				createSoapElement(elementb, "hbiColorwayname", (String) sku.getValue("skuName"));
				elementa.addChildElement(elementb);

				//String Size = "";

				while (skuSizesItr.hasNext()) {
					SOAPElement elementsize = soapBody.addBodyElement(soapEnvelope.createName("SizeType"));

					FlexObject fob = (FlexObject) skuSizesItr.next();
					if ("1".equals(fob.getData("SKUSIZE.ACTIVE"))) {
						String Size1 = fob.getData("SKUSIZEMASTER.SIZEVALUE");
						String skusize = fob.getData("SKUSIZE.IDA2A2");
						SKUSize skusizeobj = (SKUSize) LCSQuery
								.findObjectById("OR:com.lcs.wc.skusize.SKUSize:" + skusize);

						Map criteriaMap = new HashMap();
						String searchString = null;
						LCSLifecycleManaged spsize = (LCSLifecycleManaged) product.getValue(SELLING_SIZE_CAT);
						if (spsize != null) {
						//-- added zppk logic to consider zppk size from general attributes section than sizeing definition as per stephanie's advice.
						  if(!zppk){
							searchString = spsize.getValue("name") + " - " + Size1;
							}
							else {
							searchString = spsize.getValue("name") + " - " + zppkSize;

							}
						}

						LCSLifecycleManaged bo = (LCSLifecycleManaged) new HBIInterfaceUtil()
								.getLifecycleManagedByNameType("name", searchString,
										"Business Object\\Automation Support Tables\\Size Xref");
						String hbiSAPGridSize = " ";
						String hbiAPSShortSizeDescription = " ";
						String hbiAPSSizeCode = " ";
						String hbiNRFSize = " ";

						if (bo != null) {
							hbiSAPGridSize = (String) bo.getValue(SAP_GRID_SIZE);
							// hbiAPSShortSizeDescription=(String)bo.getValue("hbiAPSShortSizeDescription");
							hbiAPSShortSizeDescription = FormatHelper.getObjectId(bo);

							hbiAPSShortSizeDescription = (String) bo.getValue(APS_SHORT_DESC);
							hbiAPSSizeCode = (String) bo.getValue(APS_SIZE_CODE);
							hbiNRFSize = (String) bo.getValue(NRF_SIZE);

						}

						createSoapElement(elementsize, "hbiSizeType", hbiSAPGridSize);
						createSoapElement(elementsize, APS_SHORT_DESC, hbiAPSShortSizeDescription);
						if (!zppk){
						createSoapElement(elementsize, APS_SIZE_CODE, hbiAPSSizeCode);
						}
						else{						
						createSoapElement(elementsize, APS_SIZE_CODE, zppkSize);
						}
						createSoapElement(elementsize, NRF_SIZE, hbiNRFSize);
						String roundVal = "0";
						String salStatus = "";
						
						SKUSizeMaster skuSizeMaster =(SKUSizeMaster) skusizeobj.getMaster();
						String sizeVal = skuSizeMaster.getSizeValue();
						utilLogger.debug("::::::::::sizeVal:::::::::::::::"+sizeVal);
						//Use Map
						utilLogger.debug("::::::::::Extractor roundValMAp:::::::::::::::"+roundValMAp);
						if(!roundValMAp.isEmpty()){
							if(roundValMAp.containsKey("roundValZPPK")){ //ZPPK material type will not have variation in pack case BOM, get pkgs case from CV
								utilLogger.debug("::::::::::roundValZPPK:::::::::::::::");
								roundVal = roundValMAp.get("roundValZPPK");
							}else{//ZFRT or ZOFQ material types may have variation or not for CV
								utilLogger.debug("::::::::::roundVal ZFRT:::::::::::::::");
								if(roundValMAp.containsKey("NoSize")){
									utilLogger.debug("::::::::::NoSize:::::::::::::::");
									roundVal = roundValMAp.get("NoSize");
								}else{
									//If size exists on the Map, else 0
									if(roundValMAp.containsKey(sizeVal)){
										utilLogger.debug("::::::::::Inside Variation Row SIZE Matched:::::::::::::::");
										roundVal = roundValMAp.get(sizeVal);
									}								
								}
							}	
						}
	
						utilLogger.debug("::::::::::roundVal:::::::::::::::"+roundVal);
						LCSLog.debug("::::::::::roundVal:::::::::::::::"+roundVal);
						createSoapElement(elementsize, "RoundVal", roundVal);
						if(skusizeobj.getValue("hbiZZDColor")!=null) {
							createSoapElement(elementsize, "hbiZZDColor", ((String) skusizeobj.getValue("hbiZZDColor")).toUpperCase());
						}
						else {
							createSoapElement(elementsize, "hbiZZDColor", (String) skusizeobj.getValue("hbiZZDColor"));

						}
						if(skusizeobj.getValue("hbiZZDColorDesc")!=null) {
							createSoapElement(elementsize, "hbiZZDColorDesc",
									((String) skusizeobj.getValue("hbiZZDColorDesc")).toUpperCase());	
						}else {
							createSoapElement(elementsize, "hbiZZDColorDesc",
									(String) skusizeobj.getValue("hbiZZDColorDesc"));
						}

						if(skusizeobj.getValue("hbiZZDSize")!=null) {
							createSoapElement(elementsize, "hbiZZDSize", ((String) skusizeobj.getValue("hbiZZDSize")).toUpperCase());

						}
						else {
							createSoapElement(elementsize, "hbiZZDSize", (String) skusizeobj.getValue("hbiZZDSize"));
						}

						if(skusizeobj.getValue("hbiZZDStyle")!=null) {
							createSoapElement(elementsize, "hbiZZDStyle", ((String) skusizeobj.getValue("hbiZZDStyle")).toUpperCase());

						}
						else
						{
							createSoapElement(elementsize, "hbiZZDStyle", (String) skusizeobj.getValue("hbiZZDStyle"));
						}

						if(skusizeobj.getValue("hbiZZDSKUDesc")!=null) {
							createSoapElement(elementsize, "hbiZZDSKUDesc", ((String) skusizeobj.getValue("hbiZZDSKUDesc")).toUpperCase());

						}else {


							createSoapElement(elementsize, "hbiZZDSKUDesc", (String) skusizeobj.getValue("hbiZZDSKUDesc"));
						}
						elementsize.addChildElement("hbiExternalAttributeCode");
						elementsize.addChildElement("hbiExternalPutupCode");
						elementsize.addChildElement("hbiExternalVersion");
						elementsize.addChildElement("hbiExternalAttributeDescription");

						String HbiAlreadySent = (String) skusizeobj.getValue(SKU_ALREADY_SENT);
						String SPStatus = (String) product.getValue(SELLING_PRODUCT_STATUS);
						String hbiFSUDoneAlready = (String) product.getValue(OTC_SYNCHED_ALREADY);
						if (SKU_ALREADY_SENT_YES.equals(HbiAlreadySent)) {
							if (SKU_ALREADY_SENT_YES.equals(hbiFSUDoneAlready)) {
								salStatus = " ";
							} else {
								if (SELLING_PRODUCT_STATUS_ESU_IN_P.equals(SPStatus)) {
									salStatus = "10";
								} else {
									salStatus = "20";

								}
							}

						} else {
							if (SELLING_PRODUCT_STATUS_ESU_IN_P.equals(SPStatus)) {
								salStatus = "10";
							} else {
								salStatus = "20";

							}

						}
						createSoapElement(elementsize, "hbiSalStatus", salStatus);
						//skusizeobj.setValue(SKU_ALREADY_SENT, SKU_ALREADY_SENT_YES);
						//LCSLogic.persist(skusizeobj, true);

						elementb.addChildElement(elementsize);
					}

				}
				elementa.addChildElement(elementb);

			}
			}


		} catch (WTException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/**
	 * @param attType
	 * @return
	 */
	private static boolean isAttTypeList(String attType) {
		if (!FormatHelper.hasContent(attType)) {
			return false;
		}
		return attType.equals("moaList") || attType.equals("choice");
	}

	/**
	 * @param attType
	 * @return
	 */
	private static boolean isAttTypeText(String attType) {
		if (!FormatHelper.hasContent(attType)) {
			return false;
		}
		return attType.equals("boolean") || attType.equals("careWashImages") || attType.equals("careWashImages")
				|| attType.equals("colorSelect") || attType.equals("image") || attType.equals("moaEntry")
				|| attType.equals("moaList") || attType.equals("multiobject") || attType.equals("choice")
				|| attType.equals("text") || attType.equals("textArea") || attType.equals("url");
	}

	/**
	 * @param attType
	 * @return
	 */
	private static boolean isAttTypeNum(String attType) {
		if (!FormatHelper.hasContent(attType)) {
			return false;
		}
		return attType.equals("currency") || attType.equals("float") || attType.equals("integer")
				|| attType.equals("uom") || attType.equals("userList");
	}

	/**
	 * Get attribute(single list or driven) value of objects like product, season,
	 * source
	 * 
	 * @param att,
	 *            typed
	 * @return String
	 */
	private static String getAttListValue(FlexTypeAttribute att, FlexTyped typed) {
		String key = "";
		String value = "";
		try {
			key = att.getAttKey();
			value = (String) typed.getValue(key);
			AttributeValueList valueList = att.getAttValueList();
			if (valueList != null) {
				value = valueList.getValue(value, null);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return value;
	}

	private static Collection sortPlant(Collection<FlexObject> moaPUC_Collection) {
		Collection<FlexObject> primaryPlant = new LinkedList();
		Collection<FlexObject> restOfThePlant = new LinkedList();
		Collection<FlexObject> sortedPlants = new LinkedList();
		try {
			for (FlexObject moaPUC_FO : moaPUC_Collection) {
				String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
			//	System.out.println(">>>>>>>>>>>>> moaPUC_IDA2A2"+moaPUC_IDA2A2);
				LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery
						.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + moaPUC_IDA2A2);

				String putUpCodeBO = "";
				String materialNumber = "";
				if (moaPUC_Obj != null) {

					//String primaryplant = (String) moaPUC_Obj.getValue(PRIMARY_DELIVERY_PLANT);
					String primaryplant = String.valueOf(moaPUC_Obj.getValue(PRIMARY_DELIVERY_PLANT));

					//if (FormatHelper.hasContent(primaryplant) && "true".equalsIgnoreCase(primaryplant)) {
					if (FormatHelper.hasContent(primaryplant) && "1".equalsIgnoreCase(primaryplant)) {
						primaryPlant.add(moaPUC_FO);
					} else {
						restOfThePlant.add(moaPUC_FO);
					}


				}
			}
			sortedPlants.addAll(primaryPlant);
			sortedPlants.addAll(restOfThePlant);

		} catch (WTException e) {
			e.printStackTrace();
		}
		return sortedPlants;
	}

	public static String printSoapMessageFile(String message, LCSProduct productObj) {
		String outputFile = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
			String date = dateFormat.format(new Date());
			String temp = File.separator + "SOAP_MESSAGES_GENEREATED";
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String wtHome = wtproperties.getProperty("wt.home");
			String outputFilePath = wtHome + temp + File.separator;

			String styleNo = (String) productObj.getValue("hbiSellingStyleNumber");
			String plmNo = (String) productObj.getValue("hbiPLMNo");

			outputFile = outputFilePath + plmNo + "-" + styleNo + "-SoapMessage-" + date + ".xml";

			File log = new File(outputFile);
			log.createNewFile();
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true));
			writer.println(message);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputFile;

	}
}

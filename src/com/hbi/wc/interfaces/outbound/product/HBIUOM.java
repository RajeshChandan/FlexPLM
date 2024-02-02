package com.hbi.wc.interfaces.outbound.product;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

import com.hbi.wc.util.logger.HBIUtilLogger;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
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
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;

public class HBIUOM {
	
	public static String bomLinkObjectTypeSales = LCSProperties.get("com.hbi.wc.uom.bomLinkObjectTypeSales", "BOM\\Materials\\HBI\\Sales BOM");
	private static String bomLinkObjectTypePC = LCSProperties.get("com.hbi.wc.uom.bomLinkObjectTypePC", "BOM\\Materials\\HBI\\Selling\\Pack Case BOM");
	private static String materialCorrugated = LCSProperties.get("com.hbi.wc.material.material", "Material\\Casing\\Corrugated Carton");
	private static String sellingProduct = LCSProperties.get("com.hbi.wc.uom.sellingProduct", "Product\\BASIC CUT & SEW - SELLING");
	private static String hbiAPSPackQuantity = LCSProperties.get("com.hbi.wc.uom.hbiAPSPackQuantity", "hbiAPSPackQuantity");
	private static String hbiErpMaterialType = LCSProperties.get("com.hbi.wc.uom.hbiErpMaterialType", "hbiErpMaterialType");
	private static String hbiErpFreeGoods = LCSProperties.get("com.hbi.wc.uom.hbiErpFreeGoods", "hbiErpFreeGoods");
	private static String hbiPutUpCode = LCSProperties.get("com.hbi.wc.uom.hbiPutUpCode", "hbiPutUpCode");
	private static String hbiReferenceSpecification = LCSProperties.get("com.hbi.wc.uom.hbiReferenceSpecification", "hbiReferenceSpecification");
	private static String hbiPrimarySecondary = LCSProperties.get("com.hbi.wc.uom.hbiPrimarySecondary", "hbiPrimarySecondary");
	private static String materialDescription = LCSProperties.get("com.hbi.wc.uom.materialDescription", "materialDescription");
	private static String hbiFluteTypeCd = LCSProperties.get("com.hbi.wc.uom.hbiFluteTypeCd", "hbiFluteTypeCd");
	private static String hbiInnerLengthIn = LCSProperties.get("com.hbi.wc.uom.hbiInnerLengthIn", "hbiInnerLengthIn");
	private static String hbiInnerWidthIn = LCSProperties.get("com.hbi.wc.uom.hbiInnerWidthIn", "hbiInnerWidthIn");
	private static String hbiInnerHeightIn = LCSProperties.get("com.hbi.wc.uom.hbiInnerHeightIn", "hbiInnerHeightIn");
	private static String hbiPalletTi = LCSProperties.get("com.hbi.wc.uom.hbiPalletTi", "hbiPalletTi");
	private static String hbiPalletHi = LCSProperties.get("com.hbi.wc.uom.hbiPalletHi", "hbiPalletHi");
	private static String hbiVSCPalletTi = LCSProperties.get("com.hbi.wc.uom.hbiPalletTi", "hbiVSCPalletTi");
	private static String hbiVSCPalletHi = LCSProperties.get("com.hbi.wc.uom.hbiPalletHi", "hbiVSCPalletHi");
	public static final String PLANT_EXTENSION_MOA = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.PLANT_EXTENSION_MOA", "hbiErpPlantExtensions");
	public static final String PRIMARY_DELIVERY_PLANT = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiPrimaryDeliverPlant", "hbiPrimaryDeliverPlant");
	public static final String PLANT_CODE = LCSProperties.get("com.hbi.wc.interfaces.outbound.factory.hbiSAPPlantCode",
			"hbiSAPPlantCode");
	private static String hbiCaseWeight = LCSProperties.get("com.hbi.wc.uom.hbiCaseWeight", "hbiCaseWeight");
	private static String hbiErpComponentStyle = LCSProperties.get("com.hbi.wc.uom.hbiErpComponentStyle", "hbiErpComponentStyle");
	private static String hbiErpComponentSize = LCSProperties.get("com.hbi.wc.uom.hbiErpComponentSize", "hbiErpComponentSize");
	private static String hbiPackagingWeightLbs = LCSProperties.get("com.hbi.wc.uom.hbiPackagingWeightLbs", "hbiPackagingWeightLbs");
	private static String quantity = LCSProperties.get("com.hbi.wc.uom.quantity", "quantity");
	private static String hbiFPLength = LCSProperties.get("com.hbi.wc.uom.hbiFPLength", "hbiFPLength");
	private static String hbiFPHeight = LCSProperties.get("com.hbi.wc.uom.hbiFPHeight", "hbiFPHeight");
	private static String hbiFPWidth = LCSProperties.get("com.hbi.wc.uom.hbiFPWidth", "hbiFPWidth");
	private static String hbiPkgsOrInner = LCSProperties.get("com.hbi.wc.uom.hbiPkgsOrInner", "hbiPkgsOrInner");
	private static String hbiPksCases = LCSProperties.get("com.hbi.wc.uom.hbiPksCases", "hbiPksCases");
	private static String hbiSellingSizeCategory = LCSProperties.get("com.hbi.wc.uom.hbiSellingSizeCategory", "hbiSellingSizeCategory");
	private static String hbiMaterialNumber = LCSProperties.get("com.hbi.wc.uom.hbiMaterialNumber", "hbiMaterialNumber");
	//--new attribute
	private static String hbiErpCartonID = LCSProperties.get("com.hbi.wc.uom.hbiErpCartonID", "hbiErpCartonID");
	
	
	public static final String FSU_DONE = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiFSUDoneAlready", "hbiFSUDoneAlready");
	private static String netweight = "netweight";
	private static String grossweight ="grossweight";
	
	private static String outerHeightStr = "outerHeight";
	private static String outerLengthStr ="outerLength";
	private static String outerWidthStr ="outerWidth";
	
	private static String bomLengthStr = "bomLengthStr";
	private static String bomHeightStr ="bomHeightStr";
	private static String bomWidthStr ="bomWidthStr";
	
	private static String splitFormat = "|~*~|";
	private static String splitStr = "\\|\\~\\*\\~\\|";
	private static String eaDataMapStr = "eaDataMap";
	private static String ipMainDataMapStr ="ipMainDataMap";
	private static String cvMainDataMapStr ="cvMainDataMap";
	private static String Volume ="Volume";
	private static String BOMVolume ="BOMVolume";
	private static String sizeValuesBO_Map ="sizeValuesBO_Map";
	private static String eaNetWeightZPPK ="eaNetWeightZPPK";
	
	public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel","DEBUG");
	public static Logger utilLogger = HBIUtilLogger.createInstance(HBIUOM.class, logLevel);
	//For ESU, i.e. early set up, uom bom data is not needed but putup is needed.
	//As this assumes that BOM has values, for esu, this 
	public static void startUOMExtraction(LCSProduct productObject, LCSSeason season, SOAPBody soapBody,SOAPEnvelope soapEnvelope )  {
		utilLogger.debug("************** startUOMExtraction product:  "+productObject.getName());
		
		try {
			LCSProduct product=(LCSProduct)VersionHelper.getVersion(productObject, "A");
			LCSLifecycleManaged spSizeBO=(LCSLifecycleManaged)product.getValue(hbiSellingSizeCategory);
			
			FlexType ft = FlexTypeCache.getFlexTypeFromPath(sellingProduct);
			FlexTypeAttribute fta = ft.getAttribute(hbiAPSPackQuantity);
		
			String apsPackQuantityStr = fta.getAttValueList().getValue((String) product.getValue(hbiAPSPackQuantity), Locale.getDefault());
			String erpMaterialType = (String) product.getValue(hbiErpMaterialType);
			int apsPackQuantity =0;
			try {
				if(FormatHelper.hasContent(apsPackQuantityStr)) {
					apsPackQuantity = Integer.parseInt(apsPackQuantityStr);
				}
			 
			}catch (NumberFormatException e) {
				e.printStackTrace();
			} 
					
			Long ErpFreeGoods = (Long) product.getValue(hbiErpFreeGoods);
			
			LinkedHashMap zppkFinal_Map = new LinkedHashMap();
			LinkedHashMap zfrtFinal_Map = new LinkedHashMap();
			
			LCSMOATable moaPUCTable = (LCSMOATable) product.getValue(hbiPutUpCode);
			 
			Collection moaPUC_Collection = moaPUCTable.getRows();
			
			if(moaPUC_Collection != null && moaPUC_Collection.size() > 0){
	        	
	        	if("hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
	        		zppkFinal_Map = zppkExtraction(moaPUC_Collection,product);
	        		
	        		if(!zppkFinal_Map.isEmpty()) {
	        			utilLogger.debug("**************zppkFinal_Map:  "+zppkFinal_Map);
		        		processZPPKSoapMessage(zppkFinal_Map, soapBody, soapEnvelope);
	        		}
	        		
	        		//SOAPBody soapBody, SOAPEnvelope soapEnvelope
	        	}else if("hbiZFRT".equalsIgnoreCase(erpMaterialType)||"hbiZOFQ".equalsIgnoreCase(erpMaterialType)) {
	        		if(spSizeBO!=null) {
	        			zfrtFinal_Map = zfrtExtraction(product,apsPackQuantity, ErpFreeGoods, moaPUC_Collection, spSizeBO);
		        		if(!zfrtFinal_Map.isEmpty()) {
		        			utilLogger.debug("**************zfrtFinal_Map:  "+zfrtFinal_Map);
			        		processZFRTSoapMessage(zfrtFinal_Map, soapBody, soapEnvelope);
		        		}
	        		} // Business Object	        		
	        	}
	        }
			
		} catch (WTException e) {
			e.printStackTrace();
		
		}
		
		
		
	}
	private static void processZFRTSoapMessage(LinkedHashMap<String, Object> zfrtFinal_Map, SOAPBody soapBody, SOAPEnvelope soapEnvelope) {

		try {
			SOAPElement puc_Element = soapBody.addBodyElement(soapEnvelope.createName("PUTUP_CODE_MOA"));
			//Should contain parent put up in the MOA, value should be 000. Handle it in validation.
			LinkedHashMap zfrtEA_Map = new LinkedHashMap();
			if(zfrtFinal_Map.containsKey("000")) {
				 zfrtEA_Map =(LinkedHashMap) zfrtFinal_Map.get("000");
			}
			
			for(String putUpCode :zfrtFinal_Map.keySet()) {
				
				LinkedHashMap zfrt_Map =(LinkedHashMap) zfrtFinal_Map.get(putUpCode);
				//Collection containing sizing values matching from BO Size Table.
				//Matching values between product sizing and BO are must, while validation this should have been checked.
				LinkedHashMap<String, String> sizeValuesBO = (LinkedHashMap) zfrt_Map.get(sizeValuesBO_Map);
				Collection<String> sizeValuesColl = sizeValuesBO.values();
				//For EA get only for parent putup 0000 only and pass it for all child putups
				LinkedHashMap eaDataMap =new LinkedHashMap();
				if(zfrtEA_Map.isEmpty()) {
					//This should not enter ideally. As for EA even for child put up, copy values from 0000 putup
					eaDataMap =(LinkedHashMap) zfrt_Map.get(eaDataMapStr);
				}else {
					eaDataMap =(LinkedHashMap) zfrtEA_Map.get(eaDataMapStr);
				}
				
				//LinkedHashMap ipDataMap =(LinkedHashMap) zfrt_Map.get(ipDataMapStr);
				LinkedHashMap<String, Object> cvMainDataMap =(LinkedHashMap) zfrt_Map.get(cvMainDataMapStr);
				
				LinkedHashMap<String, Object> ipMainDataMap =(LinkedHashMap) zfrt_Map.get(ipMainDataMapStr);
				
				
				SOAPElement puc_ROW_Element=puc_Element.addChildElement("PUTUP_CODE_MOA_ROW");

				SOAPElement hbiPutUpCodeElement=puc_ROW_Element.addChildElement("hbiPutUpCode");
				hbiPutUpCodeElement.addTextNode(putUpCode);
				
				SOAPElement hbiMaterialNumber_Element=puc_ROW_Element.addChildElement("hbiMaterialNumber");
				if(zfrt_Map.get(putUpCode+hbiMaterialNumber)!=null) {
					hbiMaterialNumber_Element.addTextNode(zfrt_Map.get(putUpCode+hbiMaterialNumber).toString());
				}
				
				
				SOAPElement garElement=puc_ROW_Element.addChildElement("GAR");
				if(zfrt_Map.get("GAR")!=null){
					garElement.addTextNode( zfrt_Map.get("GAR").toString());
					
				}else{
					utilLogger.debug("** ZFRT GAR is null");
					garElement.addTextNode("0.0");
				}
				
				puc_ROW_Element.addChildElement(garElement);
				
				
				Long erpFreeGoods = 0L;
				if(zfrt_Map.get("FRE")!=null){
					erpFreeGoods = (Long)zfrt_Map.get("FRE") ;
				}
				
				if(erpFreeGoods>0) {
					
					SOAPElement fre_Element=puc_ROW_Element.addChildElement("FRE");
					fre_Element.addTextNode(erpFreeGoods.toString());
					puc_ROW_Element.addChildElement(fre_Element);  
				}
				//For ZFRT/ZOFQ, EA should be sent for parent putup only i.e. 0000, ignore the primary put up, 
				
				SOAPElement ea_Element=puc_ROW_Element.addChildElement("EA");
				
				for(String size1:sizeValuesColl) {
					
					SOAPElement Size_Element=ea_Element.addChildElement("Size");
					
					SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
					SizeCode_Element.addTextNode(size1);
					Size_Element.addChildElement(SizeCode_Element);
					
					String eaWeight ="0.0";
					if(eaDataMap.get(size1+hbiPackagingWeightLbs)!=null) {
						eaWeight= eaDataMap.get(size1+hbiPackagingWeightLbs).toString();
					}
					SOAPElement netweight_Element=Size_Element.addChildElement("netweight");
					netweight_Element.addTextNode(eaWeight);
					Size_Element.addChildElement(netweight_Element);
					
					SOAPElement grossweight_Element=Size_Element.addChildElement("grossweight");
					grossweight_Element.addTextNode(eaWeight);
					Size_Element.addChildElement(grossweight_Element);
					
					if(eaDataMap.get(size1+bomLengthStr)!=null) {
						SOAPElement hbiFPLength_Element=Size_Element.addChildElement("Length");
						hbiFPLength_Element.addTextNode(eaDataMap.get(size1+bomLengthStr).toString());	
						Size_Element.addChildElement(hbiFPLength_Element);
					}				
					if(eaDataMap.get(size1+bomHeightStr)!=null) {
						SOAPElement hbiFinishedPackageLength_Element=Size_Element.addChildElement("Height");
						hbiFinishedPackageLength_Element.addTextNode(eaDataMap.get(size1+bomHeightStr).toString());
						Size_Element.addChildElement(hbiFinishedPackageLength_Element);
					}
					if(eaDataMap.get(size1+bomWidthStr)!=null) {
						SOAPElement hbiFPWidth_Element=Size_Element.addChildElement("Width");
						hbiFPWidth_Element.addTextNode(eaDataMap.get(size1+bomWidthStr).toString());
						Size_Element.addChildElement(hbiFPWidth_Element);
					}
			
					}
				
				// cvMainDataMap and ipMainDataMap should not be empty
				if(!cvMainDataMap.isEmpty()  || !ipMainDataMap.isEmpty()) {
					//Preparing CV INT 
					LinkedHashMap<String,Collection<String>> cvIntMap = new LinkedHashMap<String, Collection<String>>();
					/*cvKey hbiCV1|~*~|002
					cvKey hbiCV2|~*~|003
					cvKey hbiCV1|~*~|004
					cvKey hbiCV2|~*~|005*/
					Collection<String> cvSizeCol = new ArrayList<String>();
					Collection<String> cvNoSizeCol = new ArrayList<String>();
					cvNoSizeCol.addAll(sizeValuesColl);
				
					for(String cvKey : cvMainDataMap.keySet()) {
						
						String[] cvSplit = cvKey.split(splitStr);
						String cvIntKey = cvSplit[0];
						String cvIntSize = cvSplit[1];
						if(cvIntMap.containsKey(cvIntKey)) {
							
							cvSizeCol =cvIntMap.get(cvIntKey);
							cvSizeCol.add(cvIntSize);
							if(cvNoSizeCol.contains(cvIntSize)) {
								cvNoSizeCol.remove(cvIntSize);
							}
							
							cvIntMap.put(cvIntKey, cvSizeCol);
							
						}else {
							
							cvSizeCol = new ArrayList<String>();
							cvSizeCol.add(cvIntSize);
							if(cvNoSizeCol.contains(cvIntSize)) {
								cvNoSizeCol.remove(cvIntSize);
							}
							cvIntMap.put(cvIntKey, cvSizeCol);
							
						}
					}
					//This cvIntMap contains the size details for CVs
					//CV Int preparation end
					
					SOAPElement AdditionalUOMS_Element=puc_ROW_Element.addChildElement("AdditionalUOMS");
					//CVs
					LinkedHashMap avoidDuplicateCVs = new LinkedHashMap();
					for(String cvKey : cvMainDataMap.keySet()) {
						
						/* Sample cvKey value, dont remove these. The cvKey come like this and useful later if any modification
						hbiCV1|~*~|002						
						hbiCV2|~*~|004
						hbiCV2|~*~|005
						hbiCV1|~*~|003
						hbiCV1|~*~|NoSize*/
						String[] cvSplit = cvKey.split(splitStr);
						String cvIntKey = cvSplit[0];
						String cvSize = cvSplit[1];
						System.out.println("  >>>>>>>>>>>>>>>>>>>>cvSize "+cvSize);
						//When top row only exists, take any size and use as values will be same from the map
						//user should remove top row if variation is there. This case not handled.
						if("NoSize".equalsIgnoreCase(cvSize)) {
							for(String size1:sizeValuesColl) {
								cvSize=size1;
							}
						}
						LinkedHashMap cvMap = (LinkedHashMap) cvMainDataMap.get(cvKey);
						//For each CV, user should not change values. If two CV1s are there then values should be same.
						if(!avoidDuplicateCVs.containsKey(cvMap.get(hbiPrimarySecondary).toString())) {
							SOAPElement cv_Element=AdditionalUOMS_Element.addChildElement("CV");
							
							SOAPElement hbiPrimarySecondary_Element=cv_Element.addChildElement("hbiPrimarySecondary");
							hbiPrimarySecondary_Element.addTextNode(cvMap.get(hbiPrimarySecondary).toString());
							cv_Element.addChildElement(hbiPrimarySecondary_Element);
							
							SOAPElement hbiFinishedPackageDepth_Element=cv_Element.addChildElement("Length");
							hbiFinishedPackageDepth_Element.addTextNode(cvMap.get(cvSize+outerLengthStr).toString());
							cv_Element.addChildElement(hbiFinishedPackageDepth_Element);
							
							SOAPElement hbiFinishedPackageLength_Element=cv_Element.addChildElement("Height");
							hbiFinishedPackageLength_Element.addTextNode(cvMap.get(cvSize+outerHeightStr).toString());
							cv_Element.addChildElement(hbiFinishedPackageLength_Element);
							
							SOAPElement hbiFinishedPackageWidth_Element=cv_Element.addChildElement("Width");
							hbiFinishedPackageWidth_Element.addTextNode(cvMap.get(cvSize+outerWidthStr).toString());
							cv_Element.addChildElement(hbiFinishedPackageWidth_Element);
							
							SOAPElement CVGrossWeight_Element=cv_Element.addChildElement("CVGrossWeight");
							CVGrossWeight_Element.addTextNode(cvMap.get(cvSize+hbiCaseWeight).toString());
							cv_Element.addChildElement(CVGrossWeight_Element);
							//This is for UOM tab in SAP where net weight to be zero but AFS should have values as sent below for each size.
							SOAPElement CVNetWeight_Element=cv_Element.addChildElement("CVNetWeight");
							CVNetWeight_Element.addTextNode("0.00");
							cv_Element.addChildElement(CVNetWeight_Element);
							
							SOAPElement Volume_Element=cv_Element.addChildElement(Volume);
							Volume_Element.addTextNode(cvMap.get(cvSize+Volume).toString());
							cv_Element.addChildElement(Volume_Element);
							
							SOAPElement hbiPksCases_Element=cv_Element.addChildElement(hbiPksCases);
							System.out.println(">>>>>>>>>>>>>>>>>>>>>> cvMap.get(cvSize+hbiPksCases ) "+ cvMap.get(cvSize+hbiPksCases));
							if(cvMap.get(cvSize+hbiPksCases)!=null) {
								System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> in if block hbiPksCases_Element ");
								hbiPksCases_Element.addTextNode(cvMap.get(cvSize+hbiPksCases).toString());
							}else {
								System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> in else block hbiPksCases_Element ");
								hbiPksCases_Element.addTextNode("0.0");
							}
							
							cv_Element.addChildElement(hbiPksCases_Element);
							//
						
							Collection cvIntColl = cvIntMap.get(cvIntKey);
						
							//sizes
							for(String size1:sizeValuesColl) {
							
								SOAPElement Size_Element=cv_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
								
								SOAPElement netweight_Element=Size_Element.addChildElement(netweight);
								SOAPElement grossweight_Element=Size_Element.addChildElement(grossweight);
								SOAPElement hbiPalletTi_Element=Size_Element.addChildElement(hbiPalletTi);
								SOAPElement hbiPalletHi_Element=Size_Element.addChildElement(hbiPalletHi);
								/* Sample cvKey value
								hbiCV1|~*~|002
								hbiCV1|~*~|003
								hbiCV2|~*~|004
								hbiCV2|~*~|005*/
								//cvIntMap = {hbiCV1=[002], hbiCV2=[003]}
								//cvIntKey = hbiCV1
								//Final CV IntMap {hbiCV2=[004],hbiCV3=[005] hbiCV1=[NoSize]}  Top row cv1, 1 variation row cv2, cv3
								//Final CV IntMap {hbiCV2=[004],hbiCV3=[005] hbiCV1=[NoSize,001]}  Top row cv1=001,002,003, 1 variation row cv2, cv3
								
								
								
								//To print the CV values for respective sizes only or for top row when no variation i.e. NoSize
								//Ignoring when top row and variation exists, user should remove top row if variation is there. This case not handled
								if(cvIntColl.contains(size1) || cvIntColl.contains("NoSize")) {
									
									netweight_Element.addTextNode(cvMap.get(size1+netweight).toString());
									grossweight_Element.addTextNode(cvMap.get(size1+grossweight).toString());
									hbiPalletTi_Element.addTextNode(cvMap.get(size1+hbiPalletTi).toString());
									hbiPalletHi_Element.addTextNode(cvMap.get(size1+hbiPalletHi).toString());
								}else {
									netweight_Element.addTextNode("0.0");
									grossweight_Element.addTextNode("0.0");
									hbiPalletTi_Element.addTextNode("0");
									hbiPalletHi_Element.addTextNode("0");
								}
								
								Size_Element.addChildElement(netweight_Element);
								Size_Element.addChildElement(grossweight_Element);						
								Size_Element.addChildElement(hbiPalletTi_Element);
								Size_Element.addChildElement(hbiPalletHi_Element);			
							}	
						}
						avoidDuplicateCVs.put(cvMap.get(hbiPrimarySecondary).toString(), "CV");
										
					}
					
					
					for(String cvIntKey : cvIntMap.keySet()) {
						SOAPElement cv_Element=AdditionalUOMS_Element.addChildElement("CVINT");
						
						SOAPElement hbiPrimarySecondary_Element=cv_Element.addChildElement("hbiPrimarySecondary");
						hbiPrimarySecondary_Element.addTextNode(cvIntKey.substring(3,cvIntKey.length()));
						cv_Element.addChildElement(hbiPrimarySecondary_Element);
						 cvSizeCol = new ArrayList();
						cvSizeCol=cvIntMap.get(cvIntKey);
						//sizes
						
						if(cvSizeCol.size()==1 && cvSizeCol.contains("NoSize")) {
							
							for(String size1:cvNoSizeCol) {
								
								SOAPElement Size_Element=cv_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
								
							}
						}else if(cvSizeCol.size()>1 && cvSizeCol.contains("NoSize")) { 
							cvSizeCol.remove("NoSize");
							for(String size1:cvSizeCol) {
								
								SOAPElement Size_Element=cv_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
							
							}
						}else {
							for(String size1:cvSizeCol) {
								
								SOAPElement Size_Element=cv_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
							
							}
						}
					}	

					//IPs 
					
					//Preparing IP INT 
					LinkedHashMap<String,Collection<String>> ipIntMap = new LinkedHashMap<String, Collection<String>>();
					/*cvKey hbiCV1|~*~|002
					cvKey hbiCV1|~*~|002
					cvKey hbiCV1|~*~|002
					cvKey hbiCV1|~*~|002*/
					Collection<String> ipSizeCol = new ArrayList();
				
					Collection<String> ipNoSizeCol = new ArrayList<String>();
					ipNoSizeCol.addAll(sizeValuesColl);
					
				
					for(String ipKey : ipMainDataMap.keySet()) {
						
						
						String[] ipSplit = ipKey.split(splitStr);
						String ipIntKey = ipSplit[0];
						String ipIntSize = ipSplit[1];
						if(ipIntMap.containsKey(ipIntKey)) {
							
							ipSizeCol =ipIntMap.get(ipIntKey);
							ipSizeCol.add(ipIntSize);
							if(ipNoSizeCol.contains(ipIntSize)) {
								ipNoSizeCol.remove(ipIntSize);
							}
							ipIntMap.put(ipIntKey, ipSizeCol);
						}else {
							ipSizeCol = new ArrayList();
							ipSizeCol.add(ipIntSize);
							if(ipNoSizeCol.contains(ipIntSize)) {
								ipNoSizeCol.remove(ipIntSize);
							}
							ipIntMap.put(ipIntKey, ipSizeCol);
						}
					}
					//End - preparation of IP sizes map
					
					LinkedHashMap avoidDuplicate = new LinkedHashMap();
					for(String ipKey : ipMainDataMap.keySet()) {
						/* Sample ipKey value
						When no size 
						hbiIP1|~*~|NoSize
						When sizes there
						hbiIP1|~*~|002
						hbiIP1|~*~|003
						hbiIP2|~*~|004
						hbiIP2|~*~|005*/
						String[] ipSplit = ipKey.split(splitStr);
						String ipIntKey = ipSplit[0];
						String ipSize = ipSplit[1];
						//When top row only used, then use any size as all values will be same from Map
						//user should remove top row if variation is there. This case not handled.
						if("NoSize".equalsIgnoreCase(ipSize)) {
							for(String size1:sizeValuesColl) {
								ipSize=size1;
							}
						}
						
						LinkedHashMap ipMap = (LinkedHashMap) ipMainDataMap.get(ipKey);
						Collection ipIntColl = ipIntMap.get(ipIntKey);
						if(!avoidDuplicate.containsKey(ipMap.get(hbiPrimarySecondary).toString())){
							SOAPElement ip_Element=AdditionalUOMS_Element.addChildElement("IP");
							
							SOAPElement hbiPrimarySecondary_Element=ip_Element.addChildElement("hbiPrimarySecondary");
							hbiPrimarySecondary_Element.addTextNode(ipMap.get(hbiPrimarySecondary).toString());
							ip_Element.addChildElement(hbiPrimarySecondary_Element);
							
							SOAPElement hbiPkgInner_Element=ip_Element.addChildElement(hbiPkgsOrInner);
							if(ipMap.get(ipSize+hbiPkgsOrInner)!=null) {
								hbiPkgInner_Element.addTextNode(ipMap.get(ipSize+hbiPkgsOrInner).toString());
							}else {
								hbiPkgInner_Element.addTextNode("0");
							}
							ip_Element.addChildElement(hbiPkgInner_Element);
							
							
							//sizes
							
							for(String size1:sizeValuesColl) {
							
								SOAPElement Size_Element=ip_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
								
								//Both netweight and grossweight are same
								SOAPElement netweight_ElementSize=Size_Element.addChildElement(netweight);
								netweight_ElementSize.addTextNode(ipMap.get(size1+netweight).toString());
								Size_Element.addChildElement(netweight_ElementSize);
								
								//Print only for respective IP sizes
								if(ipIntColl.contains(size1) || ipIntColl.contains("NoSize"))  {
									SOAPElement grossweight_ElementSize=Size_Element.addChildElement(grossweight);
									grossweight_ElementSize.addTextNode(ipMap.get(size1+grossweight).toString());
									Size_Element.addChildElement(grossweight_ElementSize);
										
									//Moved to top row, expecting all values to be same, users should maintain same data
									if(ipMap.get(size1+bomLengthStr)!=null) {
										SOAPElement hbiFinishedPackageDepth_ElementSize=Size_Element.addChildElement("Length");
										hbiFinishedPackageDepth_ElementSize.addTextNode(ipMap.get(size1+bomLengthStr).toString());
										Size_Element.addChildElement(hbiFinishedPackageDepth_ElementSize);
									}
									if(ipMap.get(size1+bomHeightStr)!=null) {
										SOAPElement hbiFinishedPackageLength_ElementSize=Size_Element.addChildElement("Height");
										hbiFinishedPackageLength_ElementSize.addTextNode(ipMap.get(size1+bomHeightStr).toString());
										Size_Element.addChildElement(hbiFinishedPackageLength_ElementSize);
									}
									if(ipMap.get(size1+bomWidthStr)!=null) {
										SOAPElement hbiFinishedPackageWidth_ElementSize=Size_Element.addChildElement("Width");
										hbiFinishedPackageWidth_ElementSize.addTextNode(ipMap.get(size1+bomWidthStr).toString());
										Size_Element.addChildElement(hbiFinishedPackageWidth_ElementSize);
									}
								}
													
							}	
						}
						
						avoidDuplicate.put(ipMap.get(hbiPrimarySecondary).toString(), "IP");
										
					}
					
					// Printing ip sizes
					
					for(String ipIntKey : ipIntMap.keySet()) {
						SOAPElement ip_Element=AdditionalUOMS_Element.addChildElement("IPINT");
						
						SOAPElement hbiPrimarySecondary_Element=ip_Element.addChildElement("hbiPrimarySecondary");
						hbiPrimarySecondary_Element.addTextNode(ipIntKey.substring(3,ipIntKey.length()));
						ip_Element.addChildElement(hbiPrimarySecondary_Element);
						 ipSizeCol = new ArrayList();
						ipSizeCol=ipIntMap.get(ipIntKey);
						//sizes
						if(ipSizeCol.size()==1 && ipSizeCol.contains("NoSize")) {
							for(String size1:ipNoSizeCol) {
								
								SOAPElement Size_Element=ip_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
								
							}	
						}else if(ipSizeCol.size()>1 && ipSizeCol.contains("NoSize")) { 
							ipSizeCol.remove("NoSize");
							for(String size1:ipSizeCol) {
								
								SOAPElement Size_Element=ip_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
								
							}
						}else {
							for(String size1:ipSizeCol) {
								
								SOAPElement Size_Element=ip_Element.addChildElement("Size");
								
								SOAPElement SizeCode_Element=Size_Element.addChildElement("SizeCode");
								SizeCode_Element.addTextNode(size1);
								Size_Element.addChildElement(SizeCode_Element);
								
							}
						}
						
					}	
				}	
			}
		} catch (SOAPException e) {
			e.printStackTrace();
		} 
	}//ZFRT
	
	//ZPPK
	private static void processZPPKSoapMessage(LinkedHashMap<String, Object> zppkFinal_Map,SOAPBody soapBody, SOAPEnvelope soapEnvelope) {
		
		try {
			SOAPElement puc_Element = soapBody.addBodyElement(soapEnvelope.createName("PUTUP_CODE_MOA"));
			for(String putUpCode :zppkFinal_Map.keySet()) {
				LinkedHashMap zppkSalesBOM_Map =(LinkedHashMap) zppkFinal_Map.get(putUpCode);
				LinkedHashMap zppkCartonMaterialMap =(LinkedHashMap) zppkSalesBOM_Map.get("cartonMaterialMap");
				
				SOAPElement puc_ROW_Element=puc_Element.addChildElement("PUTUP_CODE_MOA_ROW");

				SOAPElement hbiPutUpCodeElement=puc_ROW_Element.addChildElement("hbiPutUpCode");
				hbiPutUpCodeElement.addTextNode(putUpCode);
				
				SOAPElement hbiMaterialNumber_Element=puc_ROW_Element.addChildElement("hbiMaterialNumber");

				if(zppkSalesBOM_Map.get(putUpCode+hbiMaterialNumber)!=null) {
					hbiMaterialNumber_Element.addTextNode(zppkSalesBOM_Map.get(putUpCode+hbiMaterialNumber).toString());
				}
				
				SOAPElement UOMDETAILSElement=puc_ROW_Element.addChildElement("UOMDETAILS");
				
				SOAPElement garElement=UOMDETAILSElement.addChildElement("GAR");
				garElement.addTextNode( zppkSalesBOM_Map.get("GAR").toString());
				UOMDETAILSElement.addChildElement(garElement);
				//Long erpFreeGoods = (Long) zppkSalesBOM_Map.get("FRE");
				//Long erpFreeGoods = Double.valueOf(zppkSalesBOM_Map.get("FRE").toString()).longValue();
				Double erpFreeGoods = (Double) zppkSalesBOM_Map.get("FRE");
				//FRE is from the sales bom each component multiplied by respective quantity
				if(erpFreeGoods>0.0) {
					SOAPElement fre_Element=UOMDETAILSElement.addChildElement("FRE");
					fre_Element.addTextNode(zppkSalesBOM_Map.get("FRE").toString());
					UOMDETAILSElement.addChildElement(fre_Element); 
				}

				SOAPElement stElement=UOMDETAILSElement.addChildElement("ST");
				stElement.addTextNode(zppkSalesBOM_Map.get("ST").toString());
				UOMDETAILSElement.addChildElement(stElement);
				
				//***************************************** Additional UOMs ****************************************
				SOAPElement AdditionalUOMs_Element=UOMDETAILSElement.addChildElement("AdditionalUOMs");
				
				SOAPElement hbiPrimarySecondary_Element=AdditionalUOMs_Element.addChildElement("hbiPrimarySecondary");
				hbiPrimarySecondary_Element.addTextNode(zppkCartonMaterialMap.get(hbiPrimarySecondary).toString());
				AdditionalUOMs_Element.addChildElement(hbiPrimarySecondary_Element); 
				
				SOAPElement hbiPalletTi_Element=AdditionalUOMs_Element.addChildElement(hbiPalletTi);
				hbiPalletTi_Element.addTextNode(zppkCartonMaterialMap.get(hbiPalletTi).toString());
				AdditionalUOMs_Element.addChildElement(hbiPalletTi_Element);
				
				SOAPElement hbiPalletHi_Element=AdditionalUOMs_Element.addChildElement(hbiPalletHi);
				hbiPalletHi_Element.addTextNode(zppkCartonMaterialMap.get(hbiPalletHi).toString());
				AdditionalUOMs_Element.addChildElement(hbiPalletHi_Element);  
				SOAPElement hbiPksCases_Element=AdditionalUOMs_Element.addChildElement(hbiPksCases);
				hbiPksCases_Element.addTextNode(zppkCartonMaterialMap.get(hbiPksCases).toString());
				AdditionalUOMs_Element.addChildElement(hbiPksCases_Element); 
				
				
				SOAPElement Length_Element=AdditionalUOMs_Element.addChildElement("Length");
				Length_Element.addTextNode(zppkCartonMaterialMap.get(outerLengthStr).toString());
				AdditionalUOMs_Element.addChildElement(Length_Element);  
				
				SOAPElement width_Element=AdditionalUOMs_Element.addChildElement("Width");
				width_Element.addTextNode(zppkCartonMaterialMap.get(outerWidthStr).toString());
				AdditionalUOMs_Element.addChildElement(width_Element);  
				
				SOAPElement height_Element=AdditionalUOMs_Element.addChildElement("Height");
				height_Element.addTextNode(zppkCartonMaterialMap.get(outerHeightStr).toString());
				AdditionalUOMs_Element.addChildElement(height_Element);  
				
				SOAPElement Volume_Element=AdditionalUOMs_Element.addChildElement("Volume");
				Volume_Element.addTextNode(zppkCartonMaterialMap.get("volume").toString());
				AdditionalUOMs_Element.addChildElement(Volume_Element); 
				
				SOAPElement CV1NetWeight_Element=AdditionalUOMs_Element.addChildElement("NetWeight");
				CV1NetWeight_Element.addTextNode(zppkSalesBOM_Map.get("CV1NetWeight").toString());
				AdditionalUOMs_Element.addChildElement(CV1NetWeight_Element); 
				
				SOAPElement CV1GrossWeight_Element=AdditionalUOMs_Element.addChildElement("GrossWeight");
				CV1GrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("CV1GrossWeight").toString());
				AdditionalUOMs_Element.addChildElement(CV1GrossWeight_Element); 
				//***************************************** Additional UOMs ****************************************
				
				SOAPElement Weightdetails_Element=UOMDETAILSElement.addChildElement("Weightdetails");
				
				SOAPElement EANetWeight_Element=Weightdetails_Element.addChildElement("EANetWeight");
				EANetWeight_Element.addTextNode(zppkSalesBOM_Map.get("EANetWeight").toString());
				Weightdetails_Element.addChildElement(EANetWeight_Element); 
				
				SOAPElement EAGrossWeight_Element=Weightdetails_Element.addChildElement("EAGrossWeight");
				EAGrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("EAGrossWeight").toString());
				Weightdetails_Element.addChildElement(EAGrossWeight_Element); 
				
				SOAPElement GARNetWeight_Element=Weightdetails_Element.addChildElement("GARNetWeight");
				GARNetWeight_Element.addTextNode(zppkSalesBOM_Map.get("GARNetWeight").toString());
				Weightdetails_Element.addChildElement(GARNetWeight_Element); 
				
				SOAPElement GARGrossWeight_Element=Weightdetails_Element.addChildElement("GARGrossWeight");
				GARGrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("GARGrossWeight").toString());
				Weightdetails_Element.addChildElement(GARGrossWeight_Element); 
				
				SOAPElement STNetWeight_Element=Weightdetails_Element.addChildElement("STNetWeight");
				STNetWeight_Element.addTextNode(zppkSalesBOM_Map.get("STNetWeight").toString());
				Weightdetails_Element.addChildElement(STNetWeight_Element); 
				
				SOAPElement STGrossWeight_Element=Weightdetails_Element.addChildElement("STGrossWeight");
				STGrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("STGrossWeight").toString());
				Weightdetails_Element.addChildElement(STGrossWeight_Element); 
				
				if(erpFreeGoods>0.0) {
					SOAPElement FRENetWeight_Element=Weightdetails_Element.addChildElement("FRENetWeight");
					FRENetWeight_Element.addTextNode(zppkSalesBOM_Map.get("FRENetWeight").toString());
					Weightdetails_Element.addChildElement(FRENetWeight_Element); 
					
					SOAPElement FREGrossWeight_Element=Weightdetails_Element.addChildElement("FREGrossWeight");
					FREGrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("FREGrossWeight").toString());
					Weightdetails_Element.addChildElement(FREGrossWeight_Element);  
				}
				
				
				SOAPElement DZNetWeight_Element=Weightdetails_Element.addChildElement("DZNetWeight");
				DZNetWeight_Element.addTextNode(zppkSalesBOM_Map.get("DZNetWeight").toString());
				Weightdetails_Element.addChildElement(DZNetWeight_Element); 
				
				SOAPElement DZGrossWeight_Element=Weightdetails_Element.addChildElement("DZGrossWeight");
				DZGrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("DZGrossWeight").toString());
				Weightdetails_Element.addChildElement(DZGrossWeight_Element); 
				
				SOAPElement IP1NetWeight_Element=Weightdetails_Element.addChildElement("IP1NetWeight");
				IP1NetWeight_Element.addTextNode(zppkSalesBOM_Map.get("IP1NetWeight").toString());
				Weightdetails_Element.addChildElement(IP1NetWeight_Element); 
				
				SOAPElement IP1GrossWeight_Element=Weightdetails_Element.addChildElement("IP1GrossWeight");
				IP1GrossWeight_Element.addTextNode(zppkSalesBOM_Map.get("IP1GrossWeight").toString());
				Weightdetails_Element.addChildElement(IP1GrossWeight_Element); 
					
			}
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	} //ZPPK
	
	
	
	//******************************************** ZFRT Extraction ********************************************
	private static LinkedHashMap zfrtExtraction(LCSProduct prodObj ,int apsPackQuantity, Long ErpFreeGoods,
			Collection<FlexObject> moaPUC_Collection, LCSLifecycleManaged spSizeBO)  {
		LinkedHashMap zfrtFinal_Map = new LinkedHashMap();
		LinkedHashMap zfrt_Map = new LinkedHashMap();
		LinkedHashMap eaParentDataMap = new LinkedHashMap();
		LinkedHashMap<String, String> prodSizeMap = new LinkedHashMap();
		String spstatus="";
		//String hbiFSUDoneAlready="";
		try {
			
			String[] sizeValuesArr = new String[100];
			LinkedHashMap sizeValuesBO = new LinkedHashMap();
			 spstatus=(String)prodObj.getValue("hbiSellingProductStatus");
			 SearchResults sizing_SR = SizingQuery.findProductSizeCategoriesForProduct(prodObj);
			 Collection<FlexObject> sizing_coll = sizing_SR.getResults();
			
			 for(FlexObject sizingFO :sizing_coll) {
				
				String sizeValues = sizingFO.getString("PRODUCTSIZECATEGORY.SIZEVALUES");
				sizeValuesArr = sizeValues.split(splitStr);
				
			 }
			 //************** Sizing from Business Object
			 for(String size1 :sizeValuesArr) {
				prodSizeMap.put(size1, size1);
				String searchString=spSizeBO.getValue("name")+" - "+size1;
				
				
				LCSLifecycleManaged bo=(LCSLifecycleManaged)getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
				String hbiHdrColSize ="";
				if(bo!=null) {
					  hbiHdrColSize=(String) bo.getValue("hbiSAPGridSize");   
					  sizeValuesBO.put(size1 ,hbiHdrColSize);
				}	
			} 
			
		
		String putUpCodeBO ="";
		//String materialNumber = "";
		Collection<FlexObject>  sortedPutUpCodeCol = sortPutUpCode(moaPUC_Collection, "0000", false);
		if("hbiOTCSynchedInProgress".equals(spstatus)){
		eaParentDataMap = getParentEAdataMap(sortedPutUpCodeCol,sizeValuesBO, prodSizeMap);
		}
		utilLogger.debug("** extractZFRT eaParentDataMap "+eaParentDataMap);
		Iterator itr=sortedPutUpCodeCol.iterator();
		while(itr.hasNext()){
			String materialNumber="";
			FlexObject moaPUC_FO=(FlexObject)itr.next();
			String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
		
			if(moaPUC_Obj != null){
				
				if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {
					LCSLifecycleManaged businessObjPUC = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
						
					putUpCodeBO = (String) businessObjPUC.getValue(hbiPutUpCode);
					if(FormatHelper.hasContent(putUpCodeBO)){
						putUpCodeBO=putUpCodeBO.substring(1);
					}
				}
				//Put all values in zfrt_Map here, after method extractZFR.
				if(moaPUC_Obj.getValue(hbiMaterialNumber) !=null) {
					materialNumber=(String) moaPUC_Obj.getValue(hbiMaterialNumber);
					
				}
				 //hbiFSUDoneAlready=(String)moaPUC_Obj.getValue(FSU_DONE);

				if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null && "hbiOTCSynchedInProgress".equals(spstatus)) {
					FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
	
					//Get BOM from spec
					Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
					
					for (FlexBOMPart bomPartObj : specComponentColl) {
						if(bomLinkObjectTypePC.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))) {
							zfrt_Map = extractZFRT(apsPackQuantity, ErpFreeGoods, bomPartObj, sizeValuesBO, putUpCodeBO, eaParentDataMap, prodSizeMap,prodObj);
							
						}					
					}
				}
				else {
					
					LinkedHashMap<String, String> skuSizeValuesBO = getActiveSKUSize(prodObj,spSizeBO);
					LinkedHashMap eaDataMap =new LinkedHashMap();
					zfrt_Map.put(sizeValuesBO_Map,skuSizeValuesBO);
					for(String size1:skuSizeValuesBO.values()) {
						eaDataMap.put(size1+hbiPackagingWeightLbs,"0.0" );
					}
					 zfrt_Map.put(cvMainDataMapStr,new LinkedHashMap());
					 zfrt_Map.put(eaDataMapStr, eaDataMap);
					 zfrt_Map.put(ipMainDataMapStr,new LinkedHashMap());
					
					 zfrt_Map.put("GAR", apsPackQuantity);
					 zfrt_Map.put("FRE", ErpFreeGoods);	// MOA should have a spec

				}
				
			} // MOA should be there

				zfrt_Map.put(putUpCodeBO+hbiMaterialNumber, materialNumber);	
				
				zfrtFinal_Map.put(putUpCodeBO, zfrt_Map);
		 	
			}
		
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return zfrtFinal_Map;
	}
	
	
	private static LinkedHashMap getActiveSKUSize(LCSProduct productObj, LCSLifecycleManaged spSizeBO) {
		LinkedHashMap skuSizeValuesBO = new LinkedHashMap();
		Collection<String> skuSize = new ArrayList();
		SKUSizeQuery skuSizeQuery = new SKUSizeQuery();
		try {
		
			
			 LCSSeasonProductLink spl = SeasonProductLocator.getSeasonProductLink(productObj);
			 LCSSeason seasonObj = SeasonProductLocator.getSeasonRev(spl);
			
			SizingQuery sizingQuery = new SizingQuery();
			 SearchResults results = sizingQuery.findPSDByProductAndSeason(productObj); 
			 if(results != null && results.getResultsFound() > 0)
			 {
			    FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			    
			    ProductSizeCategory productSizeCategoryObj = (ProductSizeCategory) LCSQuery.findObjectById("OR:com.lcs.wc.sizing.ProductSizeCategory:"+flexObj.getString("ProductSizeCategory.IDA2A2"));
			   
			    productSizeCategoryObj = (ProductSizeCategory) VersionHelper.latestIterationOf(productSizeCategoryObj);
				if(productSizeCategoryObj != null) {
					
					Collection<WTPartMaster> skuMasterCollection = LCSSeasonQuery.getSKUMastersForSeasonAndProduct(seasonObj, productObj, false);
					if(skuMasterCollection != null && skuMasterCollection.size() > 0){
						for(WTPartMaster skuMasterObj : skuMasterCollection){
							LCSSKU skuObj = (LCSSKU) VersionHelper.latestIterationOf(skuMasterObj);
							skuObj = SeasonProductLocator.getSKUARev(skuObj);
							
							if(skuObj!=null) {
								//SearchResults resultsOfSKUSize = SKUSizeQuery.findSKUSizesForPSC(productSizeCategoryObj,(WTPartMaster)skuObj.getMaster(),null,null);
								SearchResults resultsOfSKUSize = SKUSizeQuery.findSKUSizesForPSC(productSizeCategoryObj,(LCSPartMaster)skuObj.getMaster(),null,null);
								if(resultsOfSKUSize != null && resultsOfSKUSize.getResultsFound() > 0){
									Vector<FlexObject> listOfSKUSizeObjs = resultsOfSKUSize.getResults();
									for (FlexObject flexObject : listOfSKUSizeObjs){
										SKUSize skuSizeObj = (SKUSize) LCSQuery.findObjectById("VR:com.lcs.wc.skusize.SKUSize:"+ flexObject.getString("SKUSize.branchIditerationInfo"));
										skuSizeObj = (SKUSize) VersionHelper.latestIterationOf(skuSizeObj);
										SKUSizeToSeason colorwaySizeToSeasonObj =skuSizeQuery.getSKUSizeToSeasonBySKUSizeSeason(skuSizeObj , seasonObj) ;
										
										
										if(colorwaySizeToSeasonObj !=null && colorwaySizeToSeasonObj.isActive()){	
											String skuSizeStr =flexObject.getString("SKUSIZEMASTER.SIZEVALUE");
											skuSize.add(skuSizeStr);
										}
									}
								}
							
							}
						}
					}
				}
			}
			 for(String size1 :skuSize) {
					
					String searchString=spSizeBO.getValue("name")+" - "+size1;
					
					LCSLifecycleManaged bo=(LCSLifecycleManaged)getLifecycleManagedByNameType("name",searchString, "Business Object\\Automation Support Tables\\Size Xref");
					String hbiHdrColSize ="";
					if(bo!=null) {
						  hbiHdrColSize=(String) bo.getValue("hbiSAPGridSize");   
						  skuSizeValuesBO.put(size1 ,hbiHdrColSize);
					}	
				}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return skuSizeValuesBO;
	}
	// ZFRT 
	private static LinkedHashMap extractZFRT(int apsPackQuantity, Long ErpFreeGoods,
			FlexBOMPart bomPartObj, LinkedHashMap<String, String> sizeValuesBO, String putUpCode, LinkedHashMap eaParentDataMap, LinkedHashMap<String, String> prodSizeMap, LCSProduct prodObj)  {
		LinkedHashMap zfrt_Map =new LinkedHashMap();
		try {
		
		Double materialWeight =0.0;
		/*LCSFlexBOMQuery.findFlexBOMData(part, scMasterId, skuMasterId, size1, size2,
				destDimId, wipMode, effectiveDate, dropped, linkDataOnly, dimensionMode, skuMode, sourceMode, sizeMode)*/
		Collection<FlexObject> pcBOMLink_Coll = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, 
					  null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,"ALL_SIZE1").getResults();
		
		utilLogger.debug("** extractZFRT pcBOMLink_Coll size "+pcBOMLink_Coll.size());
		
		//Collection<FlexObject> pcBOMLink_Coll = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, new Date(),false,false,null,null,null,null).getResults();
		
		
		//String componentSection="";
		LinkedHashMap dataMap = new LinkedHashMap();
		if(pcBOMLink_Coll != null && pcBOMLink_Coll.size() > 0)
		{
		   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll)
		   { 
			   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
			  String pcBOMlinkSize1 = pcBOMlink.getSize1();
			  //componentSection = (String)pcBOMlink.getValue("section");
			  //componentSection = pcBOMlink.getFlexType().getAttribute("section").getAttValueList().getValue(componentSection,Locale.getDefault());

			
			  int branchId = pcBOMlink.getBranchId();
			  
			  String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
			 
			  LCSMaterial material = (LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" + pcBOMLinkFO.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
		
			  Double pkgWeight = getDoubleValue(pcBOMlink,hbiPackagingWeightLbs);
			 
			  Double pkgInner =  getDoubleValue(pcBOMlink,hbiPkgsOrInner);
			  Double pkgCases =  getDoubleValue(pcBOMlink,hbiPksCases);
			  Double bomDimensionsVolume =0.0;
			  Double bomLength =  getDoubleValue(pcBOMlink,hbiFPLength);
			  Double bomHeight =  getDoubleValue(pcBOMlink,hbiFPHeight);
			  Double bomWidth =  getDoubleValue(pcBOMlink,hbiFPWidth);
			  
				
			  bomDimensionsVolume = bomLength*bomHeight*bomWidth;
			
			  
			  
			  Double outerHeight = 0.0;
			  Double outerLength = 0.0;
			  Double outerWidth = 0.0;
			  Double palletTi = 0.0;
			  Double palletHi = 0.0;
			  String fluteTypeCD = "";
			  Double materialInnerVolume =0.0;
			  
			
			 
			  if(material!=null && materialCorrugated.equalsIgnoreCase(material.getFlexType().getFullName(true)) 
					  && pcBOMlink.getValue(materialDescription)!=null) {
				  
				 
				  fluteTypeCD = (String) material.getValue(hbiFluteTypeCd);
				
				  Double innerLength =getDoubleValue(material,hbiInnerLengthIn);
				  Double innerWidth =getDoubleValue(material,hbiInnerWidthIn);
				  Double innerHeight = getDoubleValue(material,hbiInnerHeightIn);
				  materialInnerVolume = innerLength * innerWidth *innerHeight;
				  
				  materialInnerVolume = formatDecimal(materialInnerVolume);
				  if(checkVSCPlant(prodObj)){
				LCSLog.debug("<<<<<<<<<<<<<Given Plant is a VSCPlant>>>>>>>>>>>>>");
				
				  palletTi =  getDoubleValue(material,hbiVSCPalletTi);
				  palletHi =  getDoubleValue(material,hbiVSCPalletHi);
				  }
				  else{
			     LCSLog.debug("<<<<<<<<<<<<<Given Plant is a Normal Plant>>>>>>>>>>>>>");

					  palletTi =  getDoubleValue(material,hbiPalletTi);
					  palletHi =  getDoubleValue(material,hbiPalletHi);
				  }
				  						 

				  Double variableData =0.00;
				  if("b".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.25;
					  
				  }else if("c".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.35;
					  
				  }else if("bc".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.5;
					 
				  }else if("be".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.375;
					 
				  }

				   outerHeight = variableData*2 +innerHeight;
				   outerLength = variableData +innerLength;
				   outerWidth = variableData +innerWidth;
				   //Net Weight and Gross Weight are similar for EA and IP, diff in CV
				   //Material weight used for CV gross weight calculation
				   materialWeight = getDoubleValue(material,hbiCaseWeight );
				   
				   outerHeight = formatDecimal(outerHeight);
				   outerLength =  formatDecimal(outerLength);
				   outerWidth =  formatDecimal(outerWidth);
			  }
			
			  //********************************************* Data Map *************************************************
			  if(FormatHelper.hasContent(pcBOMlinkSize1) && prodSizeMap.containsKey(pcBOMlinkSize1)) {
				 
				  String size1 =  sizeValuesBO.get(pcBOMlinkSize1);
				  
				  if(FormatHelper.hasContent(primarySecondary) ) {
					
					  dataMap.put(primarySecondary+splitFormat+size1, String.valueOf(branchId));
					  //hbiCV1|~*~|001
				  }
				 
				if (bomLength > 0.0) {
					dataMap.put(size1 + branchId + bomLengthStr,formatDecimal(bomLength));
				}
				if (bomHeight > 0.0) {
					dataMap.put(size1 + branchId + bomHeightStr,formatDecimal(bomHeight));
				}
				if (bomWidth > 0.0) {
					dataMap.put(size1 + branchId + bomWidthStr,formatDecimal(bomWidth));
				}
				
				if (pkgWeight > 0.0) {
					dataMap.put(size1 + branchId + hbiPackagingWeightLbs,formatDecimal(pkgWeight));
				}

				if (pkgInner > 0.0) {
					dataMap.put(size1 + branchId + hbiPkgsOrInner, formatDecimal(pkgInner));
				}
				
				if (pkgCases > 0.0) {
					dataMap.put(size1 + branchId + hbiPksCases, formatInt(pkgCases));
					
				}
				if (materialWeight > 0.0) {
					dataMap.put(size1 + branchId + hbiCaseWeight, formatDecimal(materialWeight));
				}

				if (outerHeight > 0.0) {
					dataMap.put(size1 + branchId + outerHeightStr, formatDecimal(outerHeight));
				}

				if (outerLength > 0.0) {

					dataMap.put(size1 + branchId + outerLengthStr, formatDecimal(outerLength));
				}

				if (outerWidth > 0.0) {
					dataMap.put(size1 + branchId + outerWidthStr, formatDecimal(outerWidth));
				}

				if (palletTi > 0.0) {
					dataMap.put(size1 + branchId + hbiPalletTi, formatInt(palletTi));
				}

				if (palletHi > 0.0) {
					dataMap.put(size1 + branchId + hbiPalletHi, formatInt(palletHi));
				}

				if (materialInnerVolume > 0.0) {
					dataMap.put(size1 + branchId + "Volume",formatDecimal( materialInnerVolume));
				}
				if (bomDimensionsVolume > 0.0) {
					dataMap.put(size1 + branchId + BOMVolume,formatDecimal( bomDimensionsVolume));
				}
			  }
		   }
		}	

		//*********************************************** Parent Row preparation with repeat new query **************************
		//As old sizes were coming in all size query and filtering was not possible in the else part when size not matching with PSD, it was adding to top rows as no size.
		utilLogger.debug("** extractZFRT  bomPartObj getName "+bomPartObj.getName());
		utilLogger.debug("** extractZFRT  bomPartObj "+bomPartObj.getVersionDisplayIdentity());
		Collection<FlexObject> bomLinkParentRows= LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,
				LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		utilLogger.debug("** extractZFRT bomLinkParentRows size "+bomLinkParentRows.size());
		// WIP not affecting the query results
		
		if(bomLinkParentRows != null && bomLinkParentRows.size() > 0)
		{
		   for(FlexObject pcBOMLinkFO : bomLinkParentRows)
		   { 
			   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
			  String pcBOMlinkSize1 = pcBOMlink.getSize1();
			  //componentSection = (String)pcBOMlink.getValue("section");
			  //componentSection = pcBOMlink.getFlexType().getAttribute("section").getAttValueList().getValue(componentSection,Locale.getDefault());

			
			  int branchId = pcBOMlink.getBranchId();
			  
			  String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
			 
			  LCSMaterial material = (LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" + pcBOMLinkFO.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
		
			  Double pkgWeight = getDoubleValue(pcBOMlink,hbiPackagingWeightLbs);
			 
			  Double pkgInner =  getDoubleValue(pcBOMlink,hbiPkgsOrInner);
			  Double pkgCases =  getDoubleValue(pcBOMlink,hbiPksCases);
			  Double bomDimensionsVolume =0.0;
			  Double bomLength =  getDoubleValue(pcBOMlink,hbiFPLength);
			  Double bomHeight =  getDoubleValue(pcBOMlink,hbiFPHeight);
			  Double bomWidth =  getDoubleValue(pcBOMlink,hbiFPWidth);
			  utilLogger.debug("** extractZFRT pcBOMLink_Coll P bomLength "+bomLength);
			  utilLogger.debug("** extractZFRT pcBOMLink_Coll P bomHeight "+bomHeight);
			  utilLogger.debug("** extractZFRT pcBOMLink_Coll P bomWidth "+bomWidth);
			  bomDimensionsVolume = bomLength*bomHeight*bomWidth;
				  
	  
			  
			  Double outerHeight = 0.0;
			  Double outerLength = 0.0;
			  Double outerWidth = 0.0;
			  Double palletTi = 0.0;
			  Double palletHi = 0.0;
			  String fluteTypeCD = "";
			  Double materialInnerVolume =0.0;
			  
			
			 
			  if(material!=null && materialCorrugated.equalsIgnoreCase(material.getFlexType().getFullName(true)) 
					  && pcBOMlink.getValue(materialDescription)!=null) {
				  
				 
				  fluteTypeCD = (String) material.getValue(hbiFluteTypeCd);
				
				  Double innerLength =getDoubleValue(material,hbiInnerLengthIn);
				  Double innerWidth =getDoubleValue(material,hbiInnerWidthIn);
				  Double innerHeight = getDoubleValue(material,hbiInnerHeightIn);
				  materialInnerVolume = innerLength * innerWidth *innerHeight;
				  
				  materialInnerVolume = formatDecimal(materialInnerVolume);
				 
				  if(checkVSCPlant(prodObj)){
						LCSLog.debug("<<<<<<<<<<<<<Given Plant is a VSCPlant>>>>>>>>>>>>>");
						
						  palletTi =  getDoubleValue(material,hbiVSCPalletTi);
						  palletHi =  getDoubleValue(material,hbiVSCPalletHi);
						  }
						  else{
					     LCSLog.debug("<<<<<<<<<<<<<Given Plant is a Normal Plant>>>>>>>>>>>>>");

							  palletTi =  getDoubleValue(material,hbiPalletTi);
							  palletHi =  getDoubleValue(material,hbiPalletHi);
						  }
						  	
				  
				  
				 
				  						 
				  Double variableData =0.00;
				  if("b".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.25;
					  
				  }else if("c".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.35;
					  
				  }else if("bc".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.5;
					 
				  }else if("be".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.375;
					 
				  }

				   outerHeight = variableData*2 +innerHeight;
				   outerLength = variableData +innerLength;
				   outerWidth = variableData +innerWidth;
				
				   //Net Weight and Gross Weight are similar for EA and IP, diff in CV
				   //Material weight used for CV gross weight calculation
				   materialWeight = getDoubleValue(material,hbiCaseWeight );
				   
				   outerHeight = formatDecimal(outerHeight);
				   outerLength =  formatDecimal(outerLength);
				   outerWidth =  formatDecimal(outerWidth);
			  }

			  if(FormatHelper.hasContent(primarySecondary)  ) {
					 
				  dataMap.put(primarySecondary+splitFormat+"NoSize", String.valueOf(branchId));

			  }
			  for(String size1:sizeValuesBO.values()) {
				 
				  if(  checkDataMap(dataMap,size1+branchId+bomLengthStr )) {
					 
					dataMap.put(size1+branchId+bomLengthStr,  formatDecimal(bomLength));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+bomHeightStr )) {  
					dataMap.put(size1+branchId+bomHeightStr,  formatDecimal(bomHeight));  
				  }
				  if(  checkDataMap(dataMap,size1+branchId+bomWidthStr )) {
					dataMap.put(size1+branchId+bomWidthStr, formatDecimal(bomWidth));  
				  }
				  
				  if(  checkDataMap(dataMap,size1+branchId+hbiPackagingWeightLbs )) {
					  dataMap.put(size1+branchId+hbiPackagingWeightLbs, formatDecimal(pkgWeight));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+hbiPkgsOrInner )) {
					  dataMap.put(size1+branchId+hbiPkgsOrInner, formatDecimal(pkgInner));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+hbiPksCases )) {
					  dataMap.put(size1+branchId+hbiPksCases, formatInt(pkgCases));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+hbiCaseWeight )) {
					  dataMap.put(size1+branchId+hbiCaseWeight, formatDecimal(materialWeight));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+outerHeightStr )) {
					  dataMap.put(size1+branchId+outerHeightStr, formatDecimal(outerHeight));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+outerLengthStr )) {
					  dataMap.put(size1+branchId+outerLengthStr, formatDecimal(outerLength));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+outerWidthStr )) {
					  dataMap.put(size1+branchId+outerWidthStr, formatDecimal(outerWidth));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+hbiPalletTi )) {
					
					  dataMap.put(size1+branchId+hbiPalletTi, formatInt(palletTi));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+hbiPalletHi )) {
					  dataMap.put(size1+branchId+hbiPalletHi, formatInt(palletHi));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+"Volume" )) {
					  dataMap.put(size1+branchId+"Volume", formatDecimal(materialInnerVolume));
				  }
				  if(  checkDataMap(dataMap,size1+branchId+BOMVolume )) {
					  dataMap.put(size1 + branchId + BOMVolume,formatDecimal( bomDimensionsVolume));
				  }
			  }
		   }
		}
		
		
		LinkedHashMap eaDataMap = new LinkedHashMap();
		
		LinkedHashMap<String, LinkedHashMap> cvMainDataMap = new LinkedHashMap();
		LinkedHashMap<String, LinkedHashMap> ipMainDataMap = new LinkedHashMap();
		
		 utilLogger.debug("**** Prepared dataMap: "+dataMap);
		 Collection<String> keySetColl = dataMap.keySet();
		
		 //For CV values sizeValuesArr = sizeValues.split("\\|\\~\\*\\~\\|");d
		 for(String key :keySetColl) {
			 if(key.length()<20 && key.contains("hbiCV")) { //To avoid CV keys with pkg cases
	
				 LinkedHashMap cvDataMap = new LinkedHashMap();
				 String cvBranchId = (String) dataMap.get(key);
				 
				 String[] keySplit =key.split(splitStr);
				 String primarySecondary = (String) keySplit[0];
		
				 String cvSize = (String) keySplit[1];
				
				
				 cvDataMap.put(hbiPrimarySecondary, primarySecondary.substring(3,primarySecondary.length()));
				 //This is not used in the Soap Message for pkgCases
				 
				// cvDataMap.put(primarySecondary+splitFormat+"NoSize"+ hbiPksCases, dataMap.get(primarySecondary+splitFormat+"NoSize"+ hbiPksCases));
				//Can be removed, as it was used earlier
				 
				 for(String size1:sizeValuesBO.values()) {
						 
					 Integer pkgCases = (Integer) dataMap.get(size1+cvBranchId+hbiPksCases);
					
					
					 Double eaPkgWeight =0.0;
					 if(eaParentDataMap.get(size1+hbiPackagingWeightLbs)!=null) {
						  eaPkgWeight =(Double) eaParentDataMap.get(size1+hbiPackagingWeightLbs);
					 }
					 
					
					 Double cvMaterialWt=0.0;
					 if(dataMap.get(size1+cvBranchId+hbiCaseWeight)!=null) {
						  cvMaterialWt =(Double) dataMap.get(size1+cvBranchId+hbiCaseWeight);
					 }
					 
					 Double cvNetWeight =pkgCases*eaPkgWeight ;
					
					 Double cvGrossWeight =cvNetWeight + cvMaterialWt;
					
					 //Weight of material used in the CV row
					 cvDataMap.put(size1+hbiCaseWeight, formatDecimal(cvMaterialWt));
					 
					 cvDataMap.put(size1+netweight, formatDecimal(cvNetWeight));
					 cvDataMap.put(size1+grossweight, formatDecimal(cvGrossWeight));
					 
					 cvDataMap.put(size1+outerHeightStr, dataMap.get(size1+cvBranchId+outerHeightStr));
					 cvDataMap.put(size1+outerLengthStr, dataMap.get(size1+cvBranchId+outerLengthStr));
					 cvDataMap.put(size1+outerWidthStr, dataMap.get(size1+cvBranchId+outerWidthStr));
					
					 cvDataMap.put(size1+hbiPalletTi, dataMap.get(size1+cvBranchId+hbiPalletTi));
					 cvDataMap.put(size1+hbiPalletHi, dataMap.get(size1+cvBranchId+hbiPalletHi)); 
					
					 
					 //This is used in the Soap Message for pkgCases
					 cvDataMap.put(size1+hbiPksCases, dataMap.get(size1+cvBranchId+hbiPksCases));
					 
					 cvDataMap.put(size1+Volume, dataMap.get(size1+cvBranchId+Volume));
				 }
				 cvMainDataMap.put(key, cvDataMap);
			 } // Key - Each CV
		 }
		 //For IP values
		 Double pkgInner =0.0;
		 Double ipNetWeight=0.0;
		 Double eaPkgWeight=0.0;
		 Double eaParentFPLength=0.0;
		 Double eaParentFPHeight=0.0;
		 Double eaParentFPWidth=0.0;
		 /*
		  When Inner Pack is =1 meaning pkgInner is entered as 1, consider user entered pkgInner value and use EA dimensions even if user has entered value.
		  This is for top row i.e. non variation row
		 */
		 for(String key :keySetColl) {
			 if(key.contains("hbiIP")) { //To include key values like hbiIP only
				
				 LinkedHashMap ipDataMap = new LinkedHashMap();
				 String ipBranchId = (String) dataMap.get(key);
				
				 String[] keySplit =key.split(splitStr);
				 String primarySecondary = (String) keySplit[0];
				 
				 String ipSize = (String) keySplit[1];
				 ipDataMap.put(hbiPrimarySecondary, primarySecondary.substring(3,primarySecondary.length()));
				 
				 for(String size1:sizeValuesBO.values()) {
						 
					  pkgInner = (Double) dataMap.get(size1+ipBranchId+hbiPkgsOrInner); // user should enter pkgInner as 1 for IP1, no validation for this
					
					  if(eaParentDataMap.get(size1+hbiPackagingWeightLbs)!=null) {
						  eaPkgWeight =(Double) eaParentDataMap.get(size1+hbiPackagingWeightLbs);
					 }
					 ipDataMap.put(size1+hbiPkgsOrInner, formatInt(pkgInner));
					 ipNetWeight =pkgInner*eaPkgWeight ;
					 ipDataMap.put(size1+netweight, formatDecimal(ipNetWeight));
					 ipDataMap.put(size1+grossweight, formatDecimal(ipNetWeight));
					 if(pkgInner==1.0){		 //Pick from EA dimensions even if user leaves it blank or fills for IP dimensions
						 if(eaParentDataMap.get(size1+hbiFPLength)!=null) {
							 eaParentFPLength = (Double) eaParentDataMap.get(size1+hbiFPLength);
						 }
						 if(eaParentDataMap.get(size1+hbiFPHeight)!=null) {
							 eaParentFPHeight = (Double) eaParentDataMap.get(size1+hbiFPHeight);
						 }
						 if(eaParentDataMap.get(size1+hbiFPWidth)!=null) {
							 eaParentFPWidth = (Double) eaParentDataMap.get(size1+hbiFPWidth);
						 }
						 ipDataMap.put(size1+bomLengthStr,  formatDecimal(eaParentFPLength)); // EA Length
						 ipDataMap.put(size1+bomHeightStr,  formatDecimal(eaParentFPHeight));// EA Height
						 ipDataMap.put(size1+bomWidthStr,  formatDecimal(eaParentFPWidth));// EA Width
					 }else{
						 
						 ipDataMap.put(size1+bomLengthStr, dataMap.get(size1+ipBranchId+bomLengthStr));
						 ipDataMap.put(size1+bomHeightStr, dataMap.get(size1+ipBranchId+bomHeightStr));
						 ipDataMap.put(size1+bomWidthStr, dataMap.get(size1+ipBranchId+bomWidthStr));
					 }
				 }
				 ipMainDataMap.put(key, ipDataMap);
			 }
			 // Key - Each IP
		 }
		 
		 //Only parent putup 0000 will have EA
		 String eaBranchId  ="";
		 if("000".equalsIgnoreCase(putUpCode)) {
			 for(String key :keySetColl) {
				 if(key.contains("hbiEA")) {
					  eaBranchId  = (String) dataMap.get(key);
				 }
			 } 
			 
			 //EA
			 for(String size1:sizeValuesBO.values()) {
				 
				 eaDataMap.put(size1+bomLengthStr, dataMap.get(size1+eaBranchId+bomLengthStr));
				 eaDataMap.put(size1+bomHeightStr, dataMap.get(size1+eaBranchId+bomHeightStr));
				 eaDataMap.put(size1+bomWidthStr, dataMap.get(size1+eaBranchId+bomWidthStr));
				 eaDataMap.put(size1+outerHeightStr, dataMap.get(size1+eaBranchId+outerHeightStr));
				 eaDataMap.put(size1+outerLengthStr, dataMap.get(size1+eaBranchId+outerLengthStr));
				 eaDataMap.put(size1+outerWidthStr, dataMap.get(size1+eaBranchId+outerWidthStr));
				
				 eaDataMap.put(size1+hbiPackagingWeightLbs, dataMap.get(size1+eaBranchId+hbiPackagingWeightLbs));
				 eaDataMap.put(size1+hbiPalletHi, dataMap.get(size1+eaBranchId+hbiPalletHi));
				 eaDataMap.put(size1+hbiPalletTi, dataMap.get(size1+eaBranchId+hbiPalletTi));
				 eaDataMap.put(size1+hbiPkgsOrInner, dataMap.get(size1+eaBranchId+hbiPkgsOrInner));
				 eaDataMap.put(size1+Volume, dataMap.get(size1+eaBranchId+Volume));
			 }
		 }
		 utilLogger.debug("**** Prepared eaDataMap: "+eaDataMap);
		 utilLogger.debug("**** Prepared cvMainDataMap: "+cvMainDataMap);
		 utilLogger.debug("**** Prepared ipMainDataMap: "+ipMainDataMap);
		 zfrt_Map.put(sizeValuesBO_Map, sizeValuesBO);
		 zfrt_Map.put("GAR", apsPackQuantity);
		 zfrt_Map.put("FRE", ErpFreeGoods);
		 zfrt_Map.put(eaDataMapStr, eaDataMap);
		 zfrt_Map.put(ipMainDataMapStr, ipMainDataMap);
		 zfrt_Map.put(cvMainDataMapStr, cvMainDataMap);
		 

		} catch (WTException e) {
			e.printStackTrace();
		}
		return zfrt_Map;
	}
	
	
	private static boolean checkVSCPlant(LCSProduct prodObj) {
		boolean isVSCPlant=false;

		try {
			prodObj=(LCSProduct)VersionHelper.getVersion(prodObj, "A");
			LCSMOATable table = (LCSMOATable) prodObj.getValue(PLANT_EXTENSION_MOA);
			Map filter = new HashMap();
			filter.put(PRIMARY_DELIVERY_PLANT, "true");
			Collection coll = table.getRows(filter);
			Iterator itr = coll.iterator();
			while (itr.hasNext()) {
				FlexObject fob = (FlexObject) itr.next();
				LCSMOAObject moa = (LCSMOAObject) LCSQuery
						.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + fob.getData("OID"));
				com.lcs.wc.supplier.LCSSupplier plant = (com.lcs.wc.supplier.LCSSupplier) moa.getValue("hbiPlantName1");
				String plantName = null;
				String plantCode = null;

				if (plant != null) {
					plantName = (String) plant.getValue("name");
					plantCode = (String) plant.getValue(PLANT_CODE);
					LCSLifecycleManaged bo= new HBIInterfaceUtil().getLifecycleManagedByNameType("name", "SAP Team Template", "Business Object\\SAP Team Template");
					if(bo!=null){
						String vscplants=(String)bo.getValue("hbiVSCPlants");
						if(FormatHelper.hasContent(vscplants)&&vscplants.contains(plantCode)){
							isVSCPlant=true;
						}
						
					}
				}
				
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		return isVSCPlant;
	}
	private static LinkedHashMap zppkExtraction( Collection<FlexObject> moaPUC_Collection, LCSProduct product)  {
		//Sales BOM and Pack case bom needed for ZPPK, pack case should have CV1 in casing section.
		//Only one of each should be there, user shoul keep only 1 sales and 1 pack case bom for a spec. Code not checking it.
		LinkedHashMap<String, Object> zppkFinal_Map = new LinkedHashMap();
		
		LinkedHashMap zppkCartonMaterial = new LinkedHashMap();
		LinkedHashMap zppkSalesBOM_Map = new LinkedHashMap();
		
		try {
			
			Collection<FlexObject>  sortedPutUpCodeCol = sortPutUpCode(moaPUC_Collection, "0911", false);
		
			for(FlexObject moaPUC_FO : sortedPutUpCodeCol){
			String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
			 
			
			String putUpCodeBO = "";
			String materialNumber = "";
			
			if(moaPUC_Obj != null){
				
				if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {
					LCSLifecycleManaged businessObjPUC = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
					putUpCodeBO = (String) businessObjPUC.getValue(hbiPutUpCode);
					if(FormatHelper.hasContent(putUpCodeBO)){
						putUpCodeBO=putUpCodeBO.substring(1);
					}
				}

				if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null) {
					FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
					Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
			
					
					for (FlexBOMPart bomPartObj : specComponentColl) {		
						utilLogger.debug("**********ZPPK Extraction Check for SALESBOMPartType: "+bomPartObj.getFlexType().getFullName(true));
						if(bomLinkObjectTypeSales.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))) {
							utilLogger.debug("**********ZPPK Extraction BOMPartType is SALES BOM, proceed for SALES BOM Map preparation: ");
							zppkCartonMaterial = extractZPPKCartonID(bomPartObj,product);
							
							Double packCaseBOMMaterialCaseWeight = (Double) zppkCartonMaterial.get(hbiCaseWeight);
							zppkSalesBOM_Map =extractZPPK_SalesBOM(bomPartObj,  packCaseBOMMaterialCaseWeight );
						}	
					}	
				}

			}//MOA object null check
			materialNumber = (String) moaPUC_Obj.getValue(hbiMaterialNumber); 
			if(materialNumber !=null) {
				
				zppkSalesBOM_Map.put(putUpCodeBO+hbiMaterialNumber, materialNumber);
			}
			zppkSalesBOM_Map.put("cartonMaterialMap", zppkCartonMaterial);
			zppkFinal_Map.put(putUpCodeBO, zppkSalesBOM_Map);
			
		}//PutUpCode MOA rows in the product
		
		} catch (WTException e) {
			e.printStackTrace();
		}
		return zppkFinal_Map;
	}
	
	// ZPPK - Shipper/Assorted Case/Pallet | Pack Case BOM
	private static LinkedHashMap extractZPPKCartonID(FlexBOMPart bomPartObj,LCSProduct product) {
		Double packCaseBOMMaterialCaseWeight =0.0;
		int pksCases = 0;
		LinkedHashMap zppkCartonMaterial = new LinkedHashMap();
		zppkCartonMaterial.put(hbiPrimarySecondary, "CV1");
		try {
			
		  if(bomPartObj.getValue(hbiErpCartonID)!=null) {
			  LCSMaterial material = (LCSMaterial)bomPartObj.getValue(hbiErpCartonID);
			  
			 
			  Double innerLength = getDoubleValue(material, hbiInnerLengthIn);
			  zppkCartonMaterial.put(hbiInnerLengthIn, getDoubleValue(material, hbiInnerLengthIn));
			
			  Double innerWidth = getDoubleValue(material, hbiInnerWidthIn);
			  zppkCartonMaterial.put(hbiInnerWidthIn, getDoubleValue(material, hbiInnerWidthIn));
			  
			  Double innerHeight = (Double) material.getValue(hbiInnerHeightIn);
			  zppkCartonMaterial.put(hbiInnerHeightIn, innerHeight);
			  //Volume Calculation for ZPPK
			  Double volume = innerLength * innerWidth *innerHeight;
			  volume = formatDecimal(volume);
			 
			  zppkCartonMaterial.put("volume", volume);
			  Double palletTi=0.0;
			  Double palletHi=0.0;

			  if(checkVSCPlant(product)){
					LCSLog.debug("<<<<<<<<<<<<<Given Plant is a VSCPlant>>>>>>>>>>>>>");
					
					  palletTi =  getDoubleValue(material,hbiVSCPalletTi);
					  palletHi =  getDoubleValue(material,hbiVSCPalletHi);
					  }
					  else{
				     LCSLog.debug("<<<<<<<<<<<<<Given Plant is a Normal Plant>>>>>>>>>>>>>");

						  palletTi =  getDoubleValue(material,hbiPalletTi);
						  palletHi =  getDoubleValue(material,hbiPalletHi);
					  }
			  
			  zppkCartonMaterial.put(hbiPalletTi, formatInt(palletTi));			 
			  zppkCartonMaterial.put(hbiPalletHi,  formatInt(palletHi));
			  String fluteTypeCD = (String) material.getValue(hbiFluteTypeCd);
			 
			  if(FormatHelper.hasContent(fluteTypeCD)) {
				  Double variableData =0.00;
				  if("b".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.25;
					  
				  }else if("c".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.35;
					  
				  }else if("bc".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.5;
					 
				  }else if("be".equalsIgnoreCase(fluteTypeCD)) {
					   variableData = 0.375;
					 
				  }
	
				  Double outerHeight = variableData*2 +innerHeight;
				  Double outerLength = variableData +innerLength;
				  Double outerWidth = variableData +innerWidth;
				  zppkCartonMaterial.put("outerHeight", formatDecimal(outerHeight));
				  zppkCartonMaterial.put("outerLength", formatDecimal(outerLength));
				  zppkCartonMaterial.put("outerWidth", formatDecimal(outerWidth));
			  }else {
				  zppkCartonMaterial.put("outerHeight", 0.0);
				  zppkCartonMaterial.put("outerLength", 0.0);
				  zppkCartonMaterial.put("outerWidth", 0.0);
			  }
			   
			   packCaseBOMMaterialCaseWeight =getDoubleValue(material, hbiCaseWeight);
			   
			   zppkCartonMaterial.put(hbiCaseWeight, packCaseBOMMaterialCaseWeight);
			 
			   zppkCartonMaterial.put(hbiPksCases,1.0 );
			   
		  }//If casing bom has material
							
		
		} catch (WTException e) {
			e.printStackTrace();
		}
		return zppkCartonMaterial;
		
	}
	//Enter only for SALES BOM PART, this is checked when this method is called
	private static LinkedHashMap  extractZPPK_SalesBOM(FlexBOMPart salesBOMPartObj, Double packCaseBOMMaterialCaseWeight ) {
		//Sales BOM should have ZFRT/ZOFQ as a selling product component, if not, if ZPPK then go to its Sales BOM and find ZFRT/ZOFQ SP component
		LinkedHashMap zppkSalesBOM_Map = new LinkedHashMap();
		//Initialize
		Double salesBOMQuantity = 0.0;
		Double gar =0.0;
		Double st =0.0;
		Double fre =0.0;
		Double eaGrossWeight = 0.0;
		Double garNetWeight =0.000;
		Double eaNetWeight = 0.0;
		Double STNetWeight = 0.000;
		Double GARGrossWeight = 0.000;
		
		
		Double CV1GrossWeight = eaGrossWeight;
		Double FREGrossWeight = 0.0;
		Double DZNetWeight = 0.0;
		Double DZGrossWeight = 0.0;
		Double FRENetWeight =0.000;
		Double STGrossWeight = 0.000;
		
		//Initialization needed to avoid exceptions as it is not handled above if map not having these values.
		zppkSalesBOM_Map.put("GAR", formatDecimal(gar));
		zppkSalesBOM_Map.put("FRE", formatDecimal(fre));
		zppkSalesBOM_Map.put("ST", formatDecimal(st));
		
		zppkSalesBOM_Map.put("EANetWeight", formatDecimal(eaNetWeight));
		zppkSalesBOM_Map.put("EAGrossWeight", formatDecimal(eaGrossWeight));
		zppkSalesBOM_Map.put("GARNetWeight", formatDecimal(garNetWeight));
		zppkSalesBOM_Map.put("GARGrossWeight", formatDecimal(GARGrossWeight));
		zppkSalesBOM_Map.put("STNetWeight", formatDecimal(STNetWeight));
		zppkSalesBOM_Map.put("STGrossWeight", formatDecimal(STGrossWeight));
		zppkSalesBOM_Map.put("FRENetWeight", formatDecimal(FRENetWeight));
		zppkSalesBOM_Map.put("FREGrossWeight", formatDecimal(FREGrossWeight));
		zppkSalesBOM_Map.put("DZNetWeight", formatDecimal(DZNetWeight));
		zppkSalesBOM_Map.put("DZGrossWeight", formatDecimal(DZGrossWeight));
		zppkSalesBOM_Map.put("IP1NetWeight", formatDecimal(eaNetWeight));
		zppkSalesBOM_Map.put("IP1GrossWeight", formatDecimal(eaGrossWeight));
		zppkSalesBOM_Map.put("CV1NetWeight", formatDecimal(eaNetWeight));
		zppkSalesBOM_Map.put("CV1GrossWeight", formatDecimal(CV1GrossWeight));
		
		//For using in calculations only, not in soap message
		zppkSalesBOM_Map.put("compBOMLinkCount", 0);
		zppkSalesBOM_Map.put("totalWeightOfGarments", 0.0);
		// *************************************** Sales BOM ************************************
		try {
			//No variation in sales BOM, rows should have component size, quantity and style i.e. Selling Product.
			Collection<FlexObject> salesBomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(salesBOMPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
			
			utilLogger.debug("*****ZPPK salesBomLinkCollection size "+salesBomLinkCollection.size());
			//String componentSection="";
			if(salesBomLinkCollection != null && salesBomLinkCollection.size() >0) {
			  //Iterating the Sales BOM rows
			   for(FlexObject salesBomLinkflexObj : salesBomLinkCollection){  
				   
				  FlexBOMLink salesFlexBOMLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" +
						  	salesBomLinkflexObj.getString("FLEXBOMLINK.IDA2A2"));
				
				  LCSProduct componentSP = (LCSProduct) salesFlexBOMLinkObj.getValue(hbiErpComponentStyle);
				
				  if(componentSP!=null){
					  utilLogger.debug("*****ZPPK SALES BOM Component Name "+componentSP.getName());
					  String compERPMaterialType = (String) componentSP.getValue(hbiErpMaterialType);
					  //ZFRT/ZOFQ ->Use Component SP and Sales BOM Link 
					  //ZPPK  -> Use Component SP only and get Sales BOM link from the SP
					  utilLogger.debug("*****ZPPK SALES BOM Component type "+compERPMaterialType);
					  if("hbiZPPK".equalsIgnoreCase(compERPMaterialType)) {
						  //Get Sales BOM component
						  Double parentSalesBOMLinkQuantity = (Double) salesFlexBOMLinkObj.getValue(quantity);
						  zppkSalesBOM_Map = getSalesBOMfromZPPKSP(zppkSalesBOM_Map, componentSP, packCaseBOMMaterialCaseWeight, parentSalesBOMLinkQuantity);
						 
						
						  
					  }else if("hbiZFRT".equalsIgnoreCase(compERPMaterialType) || "hbiZOFQ".equalsIgnoreCase(compERPMaterialType)) {
						  zppkSalesBOM_Map = getSalesBOMfromZFRT(zppkSalesBOM_Map, componentSP,salesFlexBOMLinkObj, packCaseBOMMaterialCaseWeight,null );
					  }  
					  utilLogger.debug("*****ZPPK InProgress 1st level Component Loop: ST: "+zppkSalesBOM_Map.get("ST"));
					  utilLogger.debug("*****ZPPK InProgress 1st level Component Loop: GAR: "+zppkSalesBOM_Map.get("GAR"));
					  utilLogger.debug("*****ZPPK InProgress 1st level Component Loop: FRE: "+zppkSalesBOM_Map.get("FRE"));
				  }
				  
				 
			   }//Sales BOM FlexObject iteration 
			}//Sales BOM Collection check
		} catch (WTException e) {
			e.printStackTrace();
		}
		
		return zppkSalesBOM_Map;
		
	}
	
	public static LinkedHashMap getSalesBOMfromZPPKSP(LinkedHashMap zppkSalesBOM_Map, LCSProduct componentSPZPPK,Double packCaseBOMMaterialCaseWeight, 
			Double parentSalesBOMLinkQuantity) {
		try {
		 LCSMOATable moaPUCTable = (LCSMOATable) componentSPZPPK.getValue(hbiPutUpCode);
		 Collection<FlexObject> moaPUC_Collection = moaPUCTable.getRows();
		//Get only the parent putup code and its spec
		//Get parent MOA i.e. 911 for a ZPPK, it should be only one, ensure user keeps unique putup code
		Collection<FlexObject>  sortedPutUpCodeCol = sortPutUpCode(moaPUC_Collection, "0911", true);
		
		for(FlexObject moaPUC_FO : sortedPutUpCodeCol){
			String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
			
			
			if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null) {
				FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
				Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
				//Ensure only one sale BOM there in spec, user responsibility. 
				for (FlexBOMPart bomPartObj : specComponentColl) {		
					utilLogger.debug("**********ZPPK Extraction BOMPartType: "+bomPartObj.getFlexType().getFullName(true));
					if(bomLinkObjectTypeSales.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true)) ) {
						utilLogger.debug("**********ZPPK Extraction BOMPartType is SALES BOM, proceed for SALES BOM Map preparation: ");
						//No variation in sales BOM, rows should have component size, quantity and style i.e. Selling Product.
						Collection<FlexObject> salesBomLinkCollection = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
						
						
						utilLogger.debug("*****ZPPK salesBomLinkCollection size "+salesBomLinkCollection.size());
						//String componentSection="";
						if(salesBomLinkCollection != null && salesBomLinkCollection.size() >0) {
						  
						 for(FlexObject salesBomLinkflexObj : salesBomLinkCollection){  
							   
							  FlexBOMLink salesFlexBOMLinkObj = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" +
									  	salesBomLinkflexObj.getString("FLEXBOMLINK.IDA2A2"));
							  //Should be ZFRT/ZOFQ component in the SALES BOM component. Further levels are not handled.
							  //If it is ZPPK, then this is not handled. To one level down it is handled as on Sep 2019 requirements.
							  LCSProduct componentSPZFRT = (LCSProduct) salesFlexBOMLinkObj.getValue(hbiErpComponentStyle);
							  
							  zppkSalesBOM_Map = getSalesBOMfromZFRT(zppkSalesBOM_Map, componentSPZFRT,salesFlexBOMLinkObj, packCaseBOMMaterialCaseWeight, parentSalesBOMLinkQuantity );
							  
						 }
						
						}
					}	
				}	
			}

		}//MOA object null check
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		return zppkSalesBOM_Map;
	}
	private static LinkedHashMap getSalesBOMfromZFRT(LinkedHashMap zppkSalesBOM_Map, LCSProduct componentSP,
			FlexBOMLink salesFlexBOMLinkObj,Double packCaseBOMMaterialCaseWeight, Double parentSalesBOMLinkQuantity) {
		Double gar=(Double) zppkSalesBOM_Map.get("GAR");
		Double fre=(Double) zppkSalesBOM_Map.get("FRE");
		Double st=(Double) zppkSalesBOM_Map.get("ST");
		//Used in STNetWeight, total weight / count of the components used
		int compBOMLinkCount=(Integer) zppkSalesBOM_Map.get("compBOMLinkCount"); 
		Double totalWeightOfGarments = (Double) zppkSalesBOM_Map.get("totalWeightOfGarments");
		Double eaNetWeight = (Double) zppkSalesBOM_Map.get("EANetWeight"); 
		
		Double eaGrossWeight = 0.0;
		Double garNetWeight =0.000;
		Double STNetWeight = 0.000;
		Double FRENetWeight =0.000;

		try {
		//The size should match in EA details on the pack case BOM. User's responsibility to put correct sizes, it is case sensitive.
			//User should use exact size as there is size def used in pack case BOM
			//Code will give blank if size mismatch for EA details. 
		String componentSizeSalesBOM = (String) salesFlexBOMLinkObj.getValue(hbiErpComponentSize);
			
		  // ********************************** Component Style: Selling Product ****************************************
		  //Selling Product is must and the SIZE value as well if Product material type is ZFRT/ZOFQ with 0000 put up code MOA, 
		  //ref spec PackCase BOM and variation with EA details
		  //If component Selling Product material type is ZPPK, then size can be blank, so using the ZPPK SP sale bom zfrt SP where size is must.
		  String bomSection =(String)salesFlexBOMLinkObj.getValue("section");
		  //components
		  utilLogger.debug("*****ZPPK componentSizeSalesBOM "+componentSizeSalesBOM);
		  if(componentSP!=null && FormatHelper.hasContent(componentSizeSalesBOM) && "components".equalsIgnoreCase(bomSection)) {
			  
			
			Double salesBOMLinkQuantity = (Double) salesFlexBOMLinkObj.getValue(quantity);
			utilLogger.debug("*****ZPPK parentSalesBOMLinkQuantity "+parentSalesBOMLinkQuantity);
			if(parentSalesBOMLinkQuantity!=null){
				salesBOMLinkQuantity = salesBOMLinkQuantity *parentSalesBOMLinkQuantity;
			}
			
			utilLogger.debug("*****ZPPK salesBOMLinkQuantity "+salesBOMLinkQuantity);
			//Size details matching not done for ST. User should maintain proper size data.
			//ST will be calculated even if size did not match with EA.
			//ST is addition of all respective Sales BOM Link quantity from each BOM row
			st = st+salesBOMLinkQuantity;
			zppkSalesBOM_Map.put("ST", formatDecimal(st));
			  
			// Get component style Pack Quantity 
			FlexType ft = FlexTypeCache.getFlexTypeFromPath(sellingProduct);
			FlexTypeAttribute fta = ft.getAttribute(hbiAPSPackQuantity);
			String apsPackQuantityStr = fta.getAttValueList().getValue((String) componentSP.getValue(hbiAPSPackQuantity), Locale.getDefault());
			
			//Double erpFreeGoodsComponent = (Double) componentSP.getValue(hbiErpFreeGoods);
			Long erpFreeGoodsComponent = (Long) componentSP.getValue(hbiErpFreeGoods); 
			
			int componentApsPackQuantity =0;
			try {
				if(FormatHelper.hasContent(apsPackQuantityStr)) {
					componentApsPackQuantity = Integer.parseInt(apsPackQuantityStr);
					gar= gar+salesBOMLinkQuantity*componentApsPackQuantity;  
					zppkSalesBOM_Map.put("GAR", formatDecimal(gar));
				}
				
				fre=fre+salesBOMLinkQuantity*erpFreeGoodsComponent;
				zppkSalesBOM_Map.put("FRE", formatDecimal(fre));
			}catch (NumberFormatException e) {
			
				e.printStackTrace();
			}
			
			//To get the pack case BOM part from Product (SP in the sales BOM component, each BOM row in Sales BOM should have a SP)
			//This BOM should have EA details in the rows.
			//Generally for parent put up MOA i.e 0000 only EA details are mentioned for a ZFRT/ZOFQ product. Get from 0000 put up only.
			 FlexBOMPart componentSPSpecPCBOM = getComponentSPPackCaseBOM(componentSP);
			 
			 if(componentSPSpecPCBOM!=null){
				 //For each component style SP, get the ea net weight(pkg wt * quantity) and total weights(pkg wt) and add for all components
				//Size is case sensitive from the map as contains key is used. If size is M, in the sales BOM M should be there, not m.
				 LinkedHashMap<String, Double> eaPkgWtMap  =  getEADetailsMap(componentSP, componentSPSpecPCBOM, componentSizeSalesBOM, salesBOMLinkQuantity);
				 utilLogger.debug("*****ZPPK eaPkgWtMap "+eaPkgWtMap);
				 //Increase the count for each sales bom row, assumig all the rows in sales bom having values like quantity etc. are filled in
				 compBOMLinkCount++;
				 zppkSalesBOM_Map.put("compBOMLinkCount", compBOMLinkCount);
				 totalWeightOfGarments = totalWeightOfGarments+ eaPkgWtMap.get("totalWeightOfGarments");
				 
				 eaNetWeight = eaNetWeight + eaPkgWtMap.get(eaNetWeightZPPK);
			 }
			 utilLogger.debug("*****ZPPK eaNetWeight "+eaNetWeight);
			 utilLogger.debug("*****ZPPK totalWeightOfGarments "+totalWeightOfGarments);
			 zppkSalesBOM_Map.put("totalWeightOfGarments", formatDecimal(totalWeightOfGarments));
			 
			  zppkSalesBOM_Map.put("EANetWeight", formatDecimal(eaNetWeight));
			
			  
			  eaGrossWeight = eaNetWeight + packCaseBOMMaterialCaseWeight;
			  zppkSalesBOM_Map.put("EAGrossWeight", formatDecimal(eaGrossWeight));
			  
			 
			 
			  if(gar>0.0) {
				  garNetWeight = totalWeightOfGarments / gar;
			  }
			  zppkSalesBOM_Map.put("GARNetWeight", formatDecimal(garNetWeight));
			  
			 
			  if(compBOMLinkCount>0) {
				  STNetWeight =totalWeightOfGarments/compBOMLinkCount;
			  }
			 
			  zppkSalesBOM_Map.put("STNetWeight", formatDecimal(STNetWeight));
			  
			
			  if(fre>0.0) {
				  FRENetWeight = eaNetWeight/fre; 
			  }
			  
			  zppkSalesBOM_Map.put("FRENetWeight", formatDecimal(FRENetWeight)); 
			  zppkSalesBOM_Map.put("IP1NetWeight", formatDecimal(eaNetWeight));
			  zppkSalesBOM_Map.put("CV1NetWeight", formatDecimal(eaNetWeight));
			  zppkSalesBOM_Map.put("CV1GrossWeight", formatDecimal(eaGrossWeight));
		   }//Component Style
		  
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return zppkSalesBOM_Map;
	}
	public static Double formatDecimal(Double value){
		Double formattedValue=0.000;
		if(value!=null && !value.isNaN()) {
			
			
			BigDecimal bd = new BigDecimal(value);
			
		    bd = bd.setScale(3, RoundingMode.HALF_UP);
		    formattedValue= bd.doubleValue();
		   
		}
	
	    return formattedValue;
	}
	
	public static int formatInt(Double value){
		int intVal=0;
		if(value!=null && !value.isNaN()) {
			intVal =value.intValue(); 
		}
		return intVal;
	}
	
	/*public static Double getDoubleValue(WTObject wtObj, String attributeKey){
		Double value =0.000;
		try {
		 Method method = wtObj.getClass().getMethod("getValue", new Class[] { String.class });
		
		 if (method!=null && method.invoke(wtObj, attributeKey) != null) {
			 Object objAttributeValue = method.invoke(wtObj, attributeKey);

			 if(objAttributeValue instanceof Double) {
				 value=(Double) objAttributeValue;
				 BigDecimal bd = new BigDecimal( value);
				 bd = bd.setScale(3, RoundingMode.HALF_UP);
				 value= bd.doubleValue();
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
		return value;
	}
	*/
	public static Double getDoubleValue(WTObject wtObj, String attributeKey){
		Double value =0.0;
		try {
		 Method method = wtObj.getClass().getMethod("getValue", new Class[] { String.class });
		
		 if (method!=null && method.invoke(wtObj, attributeKey) != null) {
			 Object objAttributeValue = method.invoke(wtObj, attributeKey);
			
			 if(objAttributeValue instanceof Double) {
				 value=(Double) objAttributeValue;
				 System.out.println("double value>>>>>>>>>>>>>>"+value);
				 BigDecimal bd = new BigDecimal( value);
				 bd = bd.setScale(3, RoundingMode.HALF_UP);
				 value= bd.doubleValue();
			 } else if(objAttributeValue instanceof Long) {
				 value=((Long) objAttributeValue).doubleValue();
				 System.out.println("long value>>>>>>>>>>>>>>"+value);
			 }
			 else if(objAttributeValue instanceof String) {
				 value= Double.parseDouble((String) objAttributeValue);
				 BigDecimal bd = new BigDecimal( value);
				 bd = bd.setScale(3, RoundingMode.HALF_UP);
				 value= bd.doubleValue();
				 System.out.println("string value>>>>>>>>>>>>>>"+value);
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
		System.out.println("final value>>>>>>>>>>>>>>"+value);
		return value;
	}
	public static Boolean checkDataMap(LinkedHashMap dataMap, String key) {
		
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
		
		return putValue;
		
	}
	
	// This method will sort putup code based on following logic
	// fetch 000 put up for ZFRT/ZOFQ as first occurrence and 911 for ZPPK
	public static Collection sortPutUpCode(Collection<FlexObject> moaPUC_Collection, String putUpCodeStr, boolean parentPutUp) {
		//To store first PutUp here
		Collection<FlexObject> firstPutUp = new LinkedList();
		//Rest
		Collection<FlexObject> restPutUp = new LinkedList();
		//All together final 
		Collection<FlexObject> sortedPutUpCode = new LinkedList();
		try {
			for(FlexObject moaPUC_FO : moaPUC_Collection){
				String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
				LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);

				String putUpCodeBO = "";
				String materialNumber = "";
				
				if(moaPUC_Obj != null){
					
					if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {
						LCSLifecycleManaged businessObjPUC = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
						
						
						putUpCodeBO = (String) businessObjPUC.getValue(hbiPutUpCode);
						if(FormatHelper.hasContent(putUpCodeBO) && putUpCodeStr.equalsIgnoreCase(putUpCodeBO)){
							firstPutUp.add(moaPUC_FO);
						}else {
							restPutUp.add(moaPUC_FO);
						}
					}
					
				}
			}
			sortedPutUpCode.addAll(firstPutUp);
			if(!parentPutUp){
				sortedPutUpCode.addAll(restPutUp);
			}
			
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		return sortedPutUpCode;
	}
	public static LCSLifecycleManaged getLifecycleManagedByNameType(String key,String searchString, String businessObjectTypePath) throws WTException
	{
		LCSLifecycleManaged businessObject = null;		
		FlexType businessObjFlexType = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		Map<String, String> criteriaMap = new HashMap<String, String>();
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, businessObjFlexType.getAttribute(key).getColumnDescriptorName()), "?", Criteria.EQUALS),
				searchString);
		statement.appendAndIfNeeded();
		/*statement.appendCriteria(
				new Criteria(new QueryColumn("LCSLifecycleManaged", "IDA3A8"), "?", Criteria.EQUALS),
				FormatHelper.getNumericObjectIdFromObject(businessObjFlexType));*/
		statement.appendCriteria(
				new Criteria(new QueryColumn("LCSLifecycleManaged", "BRANCHIDA2TYPEDEFINITIONREFE"), "?", Criteria.EQUALS),
				FormatHelper.getNumericObjectIdFromObject(businessObjFlexType));
		SearchResults results = LCSQuery.runDirectQuery(statement);

		//
		if(results != null && results.getResultsFound() > 0)
		{
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
		}
		return businessObject;
	}
	//Used in ZPPK sales BOM extraction for each component style pack case BOM details
	private static FlexBOMPart getComponentSPPackCaseBOM(LCSProduct componentSP) {
		 utilLogger.debug("*****ZPPK SalesBOM SP Component's BOM should have Pack Case BOM with Size Variation");
		FlexBOMPart componentSPSpecBOM =null;
		try {
			 LCSMOATable moaPUCTable = (LCSMOATable) componentSP.getValue(hbiPutUpCode);
 
			  Collection pucMOAColl = moaPUCTable.getRows();
			  //Get only parent PUC MOA
			  Collection<FlexObject>  sortedPutUpCodeCol = sortPutUpCode(pucMOAColl, "0000", true);
			  
			  Iterator pucCompSPMoaCollItr=sortedPutUpCodeCol.iterator();
			  while(pucCompSPMoaCollItr.hasNext()) {
				  FlexObject fob=(FlexObject)pucCompSPMoaCollItr.next();
				  LCSMOAObject moaPUC_Obj=(LCSMOAObject)LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+fob.getData("OID"));
				  
				  FlexSpecification componentSPSpec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
				  Collection<FlexBOMPart> componentSPSpecColl = FlexSpecQuery.getSpecComponents(componentSPSpec, "BOM");
					
				  //Complete pack case bom extraction first in ZPPK. This is used in Sales BOM for the material weight calculation.
				  for ( FlexBOMPart bompart : componentSPSpecColl) {
					  utilLogger.debug("*****ZPPK SalesBOM ZFRT/ZOFQ Component's BOMPartType: "+bompart.getFlexType().getFullName(true));
					  if(bomLinkObjectTypePC.equalsIgnoreCase(bompart.getFlexType().getFullName(true))) {
						  
						  componentSPSpecBOM=bompart;
					  }
				  }  
					  	
			  }
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		return componentSPSpecBOM;
	}
	
	private static LinkedHashMap getParentEAdataMap(Collection<FlexObject>  sortedPutUpCodeCol, LinkedHashMap<String, String> sizeValuesBO, LinkedHashMap<String, String> prodSizeMap) {
		
		LinkedHashMap eaParentDataMap = new LinkedHashMap();
		try {
		Iterator itr=sortedPutUpCodeCol.iterator();
		while(itr.hasNext()){
			FlexObject moaPUC_FO=(FlexObject)itr.next();
			String moaPUC_IDA2A2 = moaPUC_FO.getString("OID");
			LCSMOAObject moaPUC_Obj;
			
				moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
		
		
			if(moaPUC_Obj != null){
				
				if(moaPUC_Obj.getValue(hbiPutUpCode) !=null) {
					LCSLifecycleManaged businessObjPUC = (LCSLifecycleManaged) moaPUC_Obj.getValue(hbiPutUpCode);
					String putUpCodeParent = (String) businessObjPUC.getValue(hbiPutUpCode);
					if(FormatHelper.hasContent(putUpCodeParent) && "0000".equalsIgnoreCase(putUpCodeParent)) {
						if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null ) {
							FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
							Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
							 utilLogger.debug("*****getEAparentMap specComponentColl size "+specComponentColl.size());
							for (FlexBOMPart bomPartObj : specComponentColl) {
								 utilLogger.debug("*****getEAparentMap bomPartObj "+bomPartObj.getName());
								if(bomLinkObjectTypePC.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))) {
									//query with size variation included
									Collection<FlexObject> pcBOMLink_Coll_size = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, 
											  null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,"ALL_SIZE1").getResults();
									
									String componentSection="";
									LinkedHashMap<String, String> eaMap = new LinkedHashMap();
									if(pcBOMLink_Coll_size != null && pcBOMLink_Coll_size.size() > 0){
									   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll_size){ 
										   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
										   String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
										   int branchId = pcBOMlink.getBranchId();
										   if(FormatHelper.hasContent(primarySecondary) && "hbiEA".equalsIgnoreCase(primarySecondary)) {
												
											   eaMap.put(primarySecondary, String.valueOf(branchId));
										   }
										   
									   }
									  
									   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll_size){ 
										   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
										   String pcBOMlinkSize1 = pcBOMlink.getSize1();
										   Double pkgWeight = getDoubleValue(pcBOMlink,hbiPackagingWeightLbs);
										   Double bomLength =  getDoubleValue(pcBOMlink,hbiFPLength);
										   Double bomHeight =  getDoubleValue(pcBOMlink,hbiFPHeight);
										   Double bomWidth =  getDoubleValue(pcBOMlink,hbiFPWidth);
										   int branchId = pcBOMlink.getBranchId();
										   String eaBranchId = eaMap.get("hbiEA");
										   if(eaBranchId.equalsIgnoreCase(String.valueOf(branchId))) {
											  
											   if(FormatHelper.hasContent(pcBOMlinkSize1) && prodSizeMap.containsKey(pcBOMlinkSize1)) {
												   		utilLogger.debug("*****getEAparentMap pcBOMlinkSize1 "+pcBOMlinkSize1);
												   	 utilLogger.debug("*****getEAparentMap bomLength "+bomLength);
													   utilLogger.debug("*****getEAparentMap bomHeight "+bomHeight);
													   utilLogger.debug("*****getEAparentMap bomWidth "+bomWidth);
													   utilLogger.debug("*****getEAparentMap pkgWeight "+pkgWeight);
													  String size1 =  sizeValuesBO.get(pcBOMlinkSize1);
													  eaParentDataMap.put(size1+hbiPackagingWeightLbs, pkgWeight);
													  eaParentDataMap.put(size1+hbiFPLength, bomLength);
													  eaParentDataMap.put(size1+hbiFPHeight, bomHeight);
													  eaParentDataMap.put(size1+hbiFPWidth, bomWidth);
											   }else {//Not needed as doing separately
												  /* for(String size1:sizeValuesBO.values()) {
													   if(  checkDataMap(eaParentDataMap,size1+hbiPackagingWeightLbs )) {
													
														   eaParentDataMap.put(size1+hbiPackagingWeightLbs, pkgWeight);
													   }
													   if(  checkDataMap(eaParentDataMap,size1+hbiFPLength )) {
															
														   eaParentDataMap.put(size1+hbiFPLength, bomLength);
													   }
													   if(  checkDataMap(eaParentDataMap,size1+hbiFPHeight )) {
															
														   eaParentDataMap.put(size1+hbiFPHeight, bomHeight);
													   }
													   if(  checkDataMap(eaParentDataMap,size1+hbiFPWidth )) {
															
														   eaParentDataMap.put(size1+hbiFPWidth, bomWidth);
													   }
													   
												   } */
											   }
											    
										   }
										  
									   } 
									  
									} //Has contents in pc bom
						//**************************************************************************** Now only parent row			
								 //Only parent rows in this query
								   if(eaParentDataMap.isEmpty()) {
									   Collection<FlexObject> bomLinkParentRows= LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,
												LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
									   if(bomLinkParentRows != null && bomLinkParentRows.size() > 0){
										   for(FlexObject pcBOMLinkFO : bomLinkParentRows){ 
											   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
											   String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
											   if(FormatHelper.hasContent(primarySecondary) && "hbiEA".equalsIgnoreCase(primarySecondary)) {
												   Double pkgWeight = getDoubleValue(pcBOMlink,hbiPackagingWeightLbs);
												   Double bomLength =  getDoubleValue(pcBOMlink,hbiFPLength);
												   Double bomHeight =  getDoubleValue(pcBOMlink,hbiFPHeight);
												   Double bomWidth =  getDoubleValue(pcBOMlink,hbiFPWidth);
												   
												   utilLogger.debug("*****getEAparentMap P bomLength "+bomLength);
												   utilLogger.debug("*****getEAparentMap P bomHeight "+bomHeight);
												   utilLogger.debug("*****getEAparentMap P bomWidth "+bomWidth);
												   utilLogger.debug("*****getEAparentMap P pkgWeight "+pkgWeight);
												   for(String size1:sizeValuesBO.values()) {   
													  eaParentDataMap.put(size1+hbiPackagingWeightLbs, pkgWeight);
													  eaParentDataMap.put(size1+hbiFPLength, bomLength);
													  eaParentDataMap.put(size1+hbiFPHeight, bomHeight);
													  eaParentDataMap.put(size1+hbiFPWidth, bomWidth);
												   
												   }
											   }
										   }
									   }
								   }//Parent row only
								}	//Pack Case BOM check
							}
						}
					}
				}
			}
		}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return eaParentDataMap;
	}
	
	private static LinkedHashMap<String, Double> getEADetailsMap(LCSProduct componentSP, FlexBOMPart componentSPSpecBOM,
			String componentSizeSalesBOM, Double salesBOMLinkQuantity) {
		LinkedHashMap<String, Double> eaDetailsMap = new LinkedHashMap();
		eaDetailsMap.put("totalWeightOfGarments", 0.0);
		eaDetailsMap.put(eaNetWeightZPPK, 0.0);
		LinkedHashMap dataMap = new LinkedHashMap();
		try {
			Double eaNetWeight = 0.0;
			
			Double pkgWeight =0.0;
			Collection<FlexObject> componentBomLinkCollection = LCSFlexBOMQuery
					.findFlexBOMData(componentSPSpecBOM, null, null, null, null, null, LCSFlexBOMQuery.WIP_ONLY,
							null, false, false, null, null, null, "ALL_SIZE1")
					.getResults();
			
			utilLogger.debug("*****getEADetailsMap componentBomLinkCollection "+componentBomLinkCollection.size());
			
			LinkedHashMap eaMap = new LinkedHashMap();
			if (componentBomLinkCollection != null && componentBomLinkCollection.size() > 0) {
				for (FlexObject componentBOMLinkFo : componentBomLinkCollection) { 
					FlexBOMLink componentflexBOMLinkObj = (FlexBOMLink) LCSQuery.findObjectById(
							"OR:com.lcs.wc.flexbom.FlexBOMLink:" + componentBOMLinkFo.getString("FLEXBOMLINK.IDA2A2"));
					// hbiEA
					int branchId = componentflexBOMLinkObj.getBranchId();
					String primarySecondary = (String) componentflexBOMLinkObj.getValue(hbiPrimarySecondary);
					
					if (FormatHelper.hasContent(primarySecondary) && "hbiEA".equalsIgnoreCase(primarySecondary)) {
						eaMap.put(primarySecondary, String.valueOf(branchId));
					}
				}
			}

			// The sales bom component SP should have Pack Case BOM with variation and EA
			// only we considering in primary secondary .
			if (eaMap.containsKey("hbiEA")) {

				Collection<String> compSizes = getSizesComponentStyle(componentSP);

				for (FlexObject componentBOMLinkFo : componentBomLinkCollection) {
					// utilLogger.debug("componentBOMLinkFo "+componentBOMLinkFo);
					FlexBOMLink componentflexBOMLinkObj = (FlexBOMLink) LCSQuery.findObjectById(
							"OR:com.lcs.wc.flexbom.FlexBOMLink:" + componentBOMLinkFo.getString("FLEXBOMLINK.IDA2A2"));

					int branchId = componentflexBOMLinkObj.getBranchId();
					String branchIdStr = String.valueOf(branchId);
					String eaBranchIdStr = (String) eaMap.get("hbiEA");
					String componentSize = componentflexBOMLinkObj.getSize1();
					
					// Considering only EA values
					if (branchIdStr.equalsIgnoreCase(eaBranchIdStr)) {
						// Checking variation sizes
						if (FormatHelper.hasContent(componentSize)) {

							//if (componentSizeSalesBOM.equalsIgnoreCase(componentSize)) {
								 pkgWeight = getDoubleValue(componentflexBOMLinkObj,hbiPackagingWeightLbs);
								
								 eaNetWeight =  pkgWeight * salesBOMLinkQuantity;
								 dataMap.put(componentSize  + hbiPackagingWeightLbs, pkgWeight);
								 dataMap.put(componentSize  + eaNetWeightZPPK, eaNetWeight);
							//}
						} else {
							
							for (String size1 : compSizes) {
								if (checkDataMap(dataMap, size1 + hbiPackagingWeightLbs)) {
									pkgWeight = getDoubleValue(componentflexBOMLinkObj, hbiPackagingWeightLbs);

									eaNetWeight = pkgWeight * salesBOMLinkQuantity;
									dataMap.put(size1 + hbiPackagingWeightLbs, pkgWeight);
									dataMap.put(size1 + eaNetWeightZPPK, eaNetWeight);
								}
							}
						}
					}
				}
				Double totalWeightOfGarments=0.0;
				Double finalEANetWeight=0.0;
				
				if(dataMap.containsKey(componentSizeSalesBOM+hbiPackagingWeightLbs)) {
					totalWeightOfGarments=(Double) dataMap.get(componentSizeSalesBOM+hbiPackagingWeightLbs);
					finalEANetWeight=(Double) dataMap.get(componentSizeSalesBOM+eaNetWeightZPPK);
				}
				eaDetailsMap.put("totalWeightOfGarments", totalWeightOfGarments);
				eaDetailsMap.put(eaNetWeightZPPK, finalEANetWeight);
			}

		} catch (WTException e) {
			e.printStackTrace();
		}
		return eaDetailsMap;
	}

	private static Collection<String> getSizesComponentStyle(LCSProduct prodObj) {
		Collection<String> compSizes =new ArrayList();
		try {
			SearchResults sizing_SR = SizingQuery.findProductSizeCategoriesForProduct(prodObj);

			Collection<FlexObject> sizing_coll = sizing_SR.getResults();
			String[] sizeValuesArr = new String[100];
			for (FlexObject sizingFO : sizing_coll) {

				String sizeValues = sizingFO.getString("PRODUCTSIZECATEGORY.SIZEVALUES");
				sizeValuesArr = sizeValues.split(splitStr);

			}
			
			for (String size1 : sizeValuesArr) {
				compSizes.add(size1);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return compSizes;
	}
	
}

package com.hbi.wc.interfaces.inbound.global.material.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import wt.util.WTException;

import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.VersionHelper;

/**
 * HBIMaterialDataLoadServiceImpl.java
 *
 * This class contains web-services invocation function as well as other generic functions, which are using to convert the soap message into native variables
 * and invoking internal functions to validate the global material systems data, trigger create and update events on material object, return feedback message. 
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-10-2018
 */
@WebService(endpointInterface = "com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoadService")
public class HBIMaterialDataLoadServiceImpl implements HBIMaterialDataLoadService
{
	public static String strFlexTypeAttributesKeys = "hbiItemDescription,hbiCommentOne,hbiFabMSCategory,hbiFabMSConstruction,hbiFabricType,hbiWhereMake,hbiFiberContent,hbiFiberType,hbiFabricGroup,hbiMachineGuage,hbiSpecialFeature,hbiWeightOzSqYd,hbiInnerHeight,hbiInnerWidth,hbiMaterialContent,hbiOverallHeight,hbiOverallWidth,hbiShapeType,hbiEdgeType,hbiPrimaryUse,hbiVerbiage,hbiButtonType,hbiDrawCordType,hbiDrawCordTipType,hbiBackingType,hbiPutup,hbiLinkType,hbiFormingSystem,hbiThreadType,hbiMoldedCupShape,hbiFoamFiberContent,hbiLinerFiberContent,hbiLiningType,hbiMoldedCupPurpose,hbiSloper,hbiStrapPlatform,hbiCupType,hbiMaterialSourceType,hbiMajorCategory,hbiMinorCategory,hbiAttrCode,hbiColorCode,hbiSizeCode,hbiConstructionType";
	public static String dblFlexTypeAttributesKeys = "hbiThickness,hbiLengthAcc,hbiFoamDensity,hbiSkip";
	public static String intFlexTypeAttributesKeys = "hbiNumAdjustments,hbiNumRows";
	public static String boolFlexTypeAttributesKeys = "hbiPrinted,hbiStretch,hbiHasInserts,hbiCupSetFlange";
	private static String skipAttributesList = "MaterialLibrary,MaterialLibraryLiteral";
	
	Mapper mapper = new DozerBeanMapper();
	
	/* This function is using as a web-service invocation point, receiving soapMessage as input parameter to validate and create/update material object in PLM
	 * @see com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoadService#loadGlobalRawMaterialToPLM(java.lang.String)
	 */
	@Override
	public String loadGlobalRawMaterialToPLM(String soapMessage) throws Exception
	{
		LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.loadGlobalRawMaterialToPLM(String soapMessage) ###");
		String materialLoadStatus = "Material Loaded Successfully";
		
		//This function is using to convert SOAP message into a Map containing Key and Value to prepare a dataMap contains attribute key and value. 
		Map<String, String> materialAttributesDataMap = getMaterialDataFromSOAPMessage(soapMessage);
		String materialFlexTypePath = materialAttributesDataMap.get("MaterialLibraryLiteral");
		String materialName = materialAttributesDataMap.get("name");
		
		//This function is using to iterate through the given map, format the attribute value to downcast to specific data type, prepare dataMap with keys-values.
		Map<String, Object> flexTypeAttributesDataMap = getFlexTypeAttributesDataMap(materialAttributesDataMap);
		LCSLog.debug("###  flexTypeAttributesDataMap ###" +flexTypeAttributesDataMap);
		
		//This function is using to prepare a criteriaMap (contains attribute key and value using as where-clause parameters in material search query). 
		Map<String, String> materialCriteriaMap = getRawMaterialCriteriaMap(materialAttributesDataMap, materialName, materialFlexTypePath);
		
		//This function is using to prepare a criteriaMap (contains attribute key and value using as where-clause parameters in material search query). 
		LCSMaterial materialObj = new HBIMaterialQuery().findMaterialByMaterialSKUAndMaterialType(materialCriteriaMap, materialFlexTypePath);
		
		LCSMaterial placeHolderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
		
		//UPDATE EVENT:- Given Material(details provided from global material system) exists in FlexPLM, invoking internal functions to update the given data and persist the Material object
		if (materialObj != null && materialObj != placeHolderMaterialObj) 
		{
			materialLoadStatus = new HBIMaterialDataLoader().updateMaterialObject(materialObj, flexTypeAttributesDataMap);
			LCSLog.debug("###  materialLoadStatus on UPDATE EVENT ###" +materialLoadStatus);
			System.out.println("###  materialLoadStatus on UPDATE EVENT ###" +materialLoadStatus);
		} 
		//CREATE EVENT:- Given Material(details provided from global material system) does not exists in FlexPLM, invoking internal functions to create new Material with the given data set
		else 
		{
			materialLoadStatus = new HBIMaterialDataLoader().createMaterialObject(flexTypeAttributesDataMap, materialName, materialFlexTypePath);
			LCSLog.debug("###  materialLoadStatus on CREATE EVENT ###" +materialLoadStatus);
			System.out.println("###  materialLoadStatus on CREATE EVENT ###" +materialLoadStatus);
		}
		
		LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.loadGlobalRawMaterialToPLM(String soapMessage) ###");
		return materialLoadStatus;
	}
	
	/**
	 * This function is using to prepare a criteriaMap (contains attribute key and value using as where-clause parameters in material search query) and return
	 * @param materialAttributesDataMap - Map<String, String>
	 * @param materialName - String
	 * @param materialFlexTypePath - String
	 * @return materialCriteriaMap - Map<String, String>
	 */
	public Map<String, String> getRawMaterialCriteriaMap(Map<String, String> materialAttributesDataMap, String materialName, String materialFlexTypePath)
	{
		// LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.getRawMaterialCriteriaMap(materialAttributesDataMap, materialName, materialTypePath) ###");
		Map<String, String> materialCriteriaMap = new HashMap<String, String>();
		String attributeCode = materialAttributesDataMap.get(HBIProperties.hbiAttrCodeKey);
		String colorCode = materialAttributesDataMap.get(HBIProperties.hbiColorCodeKey);
		String sizeCode = materialAttributesDataMap.get(HBIProperties.hbiSizeCodeKey);
		String materialNameKey = "name";
		
		if(!FormatHelper.hasContent(attributeCode))
		{
			attributeCode = "------";
		}
		if(!FormatHelper.hasContent(colorCode))
		{
			colorCode = "000";
		}
		if(!FormatHelper.hasContent(sizeCode))
		{
			sizeCode = "00";
		}
		if(materialFlexTypePath.startsWith("Material\\Material SKU"))
		{
			materialNameKey = HBIMaterialDataLoader.materialCodeKey;
		}
		
		materialCriteriaMap.put(HBIProperties.hbiAttrCodeKey, attributeCode);
		materialCriteriaMap.put(HBIProperties.hbiColorCodeKey, colorCode);
		materialCriteriaMap.put(HBIProperties.hbiSizeCodeKey, sizeCode);
		materialCriteriaMap.put(materialNameKey, materialName);
		
		// LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.getRawMaterialCriteriaMap(materialAttributesDataMap, materialName, materialTypePath) ###");
		return materialCriteriaMap;
	}
	
	/**
	 * This function is using to unMarshall the soapMessage to convert from XML format into java native format and returning a map contains attribute key-value
	 * @param soapMessage - String
	 * @return materialAttributesDataMap - Map<String, String>
	 * @throws SOAPException
	 * @throws IOException
	 */
	public Map<String, String> getMaterialDataFromSOAPMessage(String soapMessage) throws SOAPException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.getMaterialDataFromSOAPMessage(String soapMessage) ###");
		Map<String, String> materialAttributesDataMap = new HashMap<String, String>();
		Node materialNode = null;
		String flexTypeAttributeKey = "";
		String flexTypeAttributeValue = "";
		
		//Preparing SOAPMessage using the given XML format string (this string contains soap message in the form of XML data) to initialize SOAP Body NodesList
		MessageFactory messageFactory = MessageFactory.newInstance();
	    SOAPMessage soapMessageObj = messageFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapMessage.getBytes(Charset.forName("UTF-8"))));
	    SOAPBody body = soapMessageObj.getSOAPBody();
		
	    //Initializing each nodes as per the incoming soap message (as per the pre-defined message we have 3 nodes (loadGlobalRawMaterialToPLM, params, param)
	    NodeList rawMaterialNodesList = body.getElementsByTagName("proc:loadGlobalRawMaterialToPLM");
	    Node rawMaterialToPLMNode = rawMaterialNodesList.item(0);
	    NodeList paramsNodeList = rawMaterialToPLMNode.getChildNodes();
	    Node paramsNode = paramsNodeList.item(0);
	    NodeList materialAttributesList = paramsNode.getChildNodes();
	    
	    //Iterating through each node from a params node, initializing attribute key and attribute value, preparing a dataMap contains attribute key and value
	    for(int nodeIndex = 0; nodeIndex < materialAttributesList.getLength(); nodeIndex++)
        {
	    	materialNode = materialAttributesList.item(nodeIndex);
	    	flexTypeAttributeKey = materialNode.getNodeName();
	    	flexTypeAttributeValue = ""+materialNode.getTextContent();
	    	materialAttributesDataMap.put(flexTypeAttributeKey, flexTypeAttributeValue);
        }
	    
		// LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.getMaterialDataFromSOAPMessage(String soapMessage) ###");
		return materialAttributesDataMap;
	}
	
	/**
	 * This function is using to iterate through the given map, format the attribute value to downcast to specific data type, prepare dataMap with keys-values
	 * @param materialAttributesDataMap - Map<String, String>
	 * @return flexTypeAttributesDataMap - Map<String, Object>
	 * @throws WTException
	 */
	public Map<String, Object> getFlexTypeAttributesDataMap(Map<String, String> materialAttributesDataMap) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.getFlexTypeAttributesDataMap(Map<String, String> materialAttributesDataMap) ###");
		Map<String, Object> flexTypeAttributesDataMap = new HashMap<String, Object>();
		String flexTypeAttributeValue = "";
		
		for(String flexTypeAttributeKey : materialAttributesDataMap.keySet())
		{
			flexTypeAttributeValue = materialAttributesDataMap.get(flexTypeAttributeKey);
			if(strFlexTypeAttributesKeys.contains(flexTypeAttributeKey))
			{
				flexTypeAttributesDataMap = populateStringTypeAttributeData(flexTypeAttributesDataMap, flexTypeAttributeKey, flexTypeAttributeValue);
			}
			else if(dblFlexTypeAttributesKeys.contains(flexTypeAttributeKey) || intFlexTypeAttributesKeys.contains(flexTypeAttributeKey))
			{
				flexTypeAttributesDataMap = populateDoubleTypeAttributeData(flexTypeAttributesDataMap, flexTypeAttributeKey, flexTypeAttributeValue);
			}
			else if(boolFlexTypeAttributesKeys.contains(flexTypeAttributeKey))
			{
				flexTypeAttributesDataMap = populateBooleanAttributeData(flexTypeAttributesDataMap, flexTypeAttributeKey, flexTypeAttributeValue);
			}
			else if(!skipAttributesList.contains(flexTypeAttributeKey))
			{
				flexTypeAttributesDataMap = populateStringTypeAttributeData(flexTypeAttributesDataMap, flexTypeAttributeKey, flexTypeAttributeValue);
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.getFlexTypeAttributesDataMap(Map<String, String> materialAttributesDataMap) ###");
		return flexTypeAttributesDataMap;
	}
	
	/**
	 * This function is using to validate the given attribute-key and attribute-value, downcast to specific data type, update the dataMap and return to caller
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributeValue - String
	 * @return flexTypeAttributesDataMap - Map<String, Object>
	 */
	public Map<String, Object> populateStringTypeAttributeData(Map<String, Object> flexTypeAttributesDataMap, String flexTypeAttributeKey, String flexTypeAttributeValue)
	{
		// LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.populateStringTypeAttributeData(flexTypeAttributesDataMap, attributeKey, attributeVal) ###");
		
		if(FormatHelper.hasContent(flexTypeAttributeValue))
		{
			flexTypeAttributesDataMap.put(flexTypeAttributeKey, flexTypeAttributeValue);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.populateStringTypeAttributeData(flexTypeAttributesDataMap, attributeKey, attributeVal) ###");
		return flexTypeAttributesDataMap;
	}
	
	/**
	 * This function is using to validate the given attribute-key and attribute-value, downcast to specific data type, update the dataMap and return to caller
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributeValue - String
	 * @return flexTypeAttributesDataMap - Map<String, Object>
	 */
	public Map<String, Object> populateDoubleTypeAttributeData(Map<String, Object> flexTypeAttributesDataMap, String flexTypeAttributeKey, String flexTypeAttributeValue)
	{
		// LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.populateDoubleTypeAttributeData(flexTypeAttributesDataMap, attributeKey, attributeVal) ###");
		
		if(FormatHelper.hasContent(flexTypeAttributeValue))
		{
			double dblFlexTypeAttributeValue = (double) Double.parseDouble(flexTypeAttributeValue);
			flexTypeAttributesDataMap.put(flexTypeAttributeKey, dblFlexTypeAttributeValue);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.populateDoubleTypeAttributeData(flexTypeAttributesDataMap, attributeKey, attributeVal) ###");
		return flexTypeAttributesDataMap;
	}
	
	/**
	 * This function is using to validate the given attribute-key and attribute-value, downcast to specific data type, update the dataMap and return to caller
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributeValue - String
	 * @return flexTypeAttributesDataMap - Map<String, Object>
	 */
	public Map<String, Object> populateBooleanAttributeData(Map<String, Object> flexTypeAttributesDataMap, String flexTypeAttributeKey, String flexTypeAttributeValue)
	{
		// LCSLog.debug("### START HBIMaterialDataLoadServiceImpl.populateBooleanAttributeData(flexTypeAttributesDataMap, attributeKey, attributeVal) ###");
		boolean boolFlexTypeAttributeValue = false;
		
		if(FormatHelper.hasContent(flexTypeAttributeValue))
		{
			if("true".equalsIgnoreCase(flexTypeAttributeValue) || "yes".equalsIgnoreCase(flexTypeAttributeValue))
			{
				boolFlexTypeAttributeValue = true;
			}
			flexTypeAttributesDataMap.put(flexTypeAttributeKey, boolFlexTypeAttributeValue);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadServiceImpl.populateBooleanAttributeData(flexTypeAttributesDataMap, attributeKey, attributeVal) ###");
		return flexTypeAttributesDataMap;
	}
}
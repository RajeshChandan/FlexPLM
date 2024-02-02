package com.hbi.wc.interfaces.inbound.global.material.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialLogic;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIMaterialDataLoader.java
 *
 * This class is used to create and update material object in FlexPLM using the given data (material name, material type path and material level attributes value
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-14-2018
 */
public class HBIMaterialDataLoader
{
	public static String materialCodeKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoader.materialCodeKey", "hbiMaterialCode");
	private static String materialSKUAttriutes = "hbiAttrCode,hbiColorCode,hbiSizeCode";
	private static String skipAttributesOnUpdate = "hbiAttrCode,hbiColorCode,hbiSizeCode,name,hbiMaterialCode";
	private static String attributeVariableDataTypes = "choice,moaList,composite,driven";
	
	private static String hbiMaterialSourceTypeKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoader.hbiMaterialSourceTypeKey", "hbiMaterialSourceType");
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////	Following functions are specific to CREATE Material Event in FlexPLM    ///////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This function is using to create material object in FlexPLM using the given data (material name, material type path and material level attributes value
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param materialName - String
	 * @param materialFlexTypePath - String
	 * @return materialLoadStatus - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public String createMaterialObject(Map<String, Object> flexTypeAttributesDataMap, String materialName, String materialFlexTypePath) throws WTException, WTPropertyVetoException 
	{
		LCSLog.debug("### START HBIMaterialDataLoader.createMaterialObject(Map<String, Object> flexTypeAttributesDataMap, materialName, materialTypePath) ###");
		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePath);
		String materialLoadStatus = "Material Loaded Successfully";
		
		//Initializing Material object (creating new Material instance) and populating necessary information (Master Reference, FlexTypePath & Material_Name) to the initialized object
		LCSMaterial materialObj = LCSMaterial.newLCSMaterial();
		materialObj.setMaster(new LCSMaterialMaster());
		materialObj.setFlexType(materialFlexTypeObj);
		materialObj.setName(materialName.trim());
		
		//This function is using to set default values(defaulting attribute values) to the newly created/initialized Material object as per the system configuration and Hane's business process
		materialObj = populateAttributeDefaultValuesOnMaterialCreateEvent(materialObj, materialFlexTypePath);
		
		//Setting Matterial Code value on Material object because this attribute is one of the parameter to form complete Material Object name.
		if(materialFlexTypePath.startsWith("Material\\Material SKU"))
		{
			materialObj.setValue(materialCodeKey, materialName.trim());
		}
		else
		{
			materialObj.setValue(HBIProperties.materialNameKey, materialName.trim());
		}
		
		//This function is using to handle attribute does not exists issue, populate the error message across each rows to populate the logical info message. If data are proper function is using to set value on newly created Material Object.
		materialLoadStatus = populateMaterialAttributesData(flexTypeAttributesDataMap, materialObj, materialFlexTypeObj);
		if ("Material Loaded Successfully".equalsIgnoreCase(materialLoadStatus))
		{
			LCSMaterialLogic.deriveFlexTypeValues(materialObj);
			LCSMaterialHelper.service.saveMaterial(materialObj);
		}
		
		LCSLog.debug("### END HBIMaterialDataLoader.createMaterialObject(Map<String, Object> flexTypeAttributesDataMap, materialName, materialTypePath) ###");
		return materialLoadStatus;
	}
	
	/**
	 * This function is using to handle attribute does not exists issue, populate the error message across each rows to populate the logical info message. 
	 * To set Material SKU attribute values to the newly created/initialized Material object and to to validate the attribute variable type, based on the type of the variable modify attributeValue and return value.
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param materialFlexTypeObj - FlexType
	 * @return materialLoadStatus - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private String populateMaterialAttributesData(Map<String, Object> flexTypeAttributesDataMap, LCSMaterial materialObj, FlexType materialFlexTypeObj) throws WTException, WTPropertyVetoException 
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateMaterialAttributesData(flexTypeAttributesDataMap, materialObj, materialFlexTypeObj) ###");
		String materialLoadStatus = "Material Loaded Successfully";
		Object flexTypeAttributeValue = null;
		
		//Iterating attribute Map to get Key from the Map which is used to set value on newly created Material Object.
		for(String flexTypeAttributeKey : flexTypeAttributesDataMap.keySet())
		{
			flexTypeAttributeValue = flexTypeAttributesDataMap.get(flexTypeAttributeKey);
			LCSLog.debug("HBIMaterialDataLoader.populateMaterialAttributesData :: Attribute-Key = "+ flexTypeAttributeKey +" Attribute-Value = "+ flexTypeAttributeValue);
			
			//This block of code is to handle attribute does not exists issue, populate the error message across each rows to populate the logical info message
			Collection<String> flexTypePathAttributeKeys = getAllAttributeKeysForFlexType(materialFlexTypeObj);
			if(!flexTypePathAttributeKeys.contains(flexTypeAttributeKey))
			{
				materialLoadStatus = "Attribute-Key = "+flexTypeAttributeKey +" does not exists in FlexPLM for "+ materialFlexTypeObj.getFullName(true) +", Data-Load Skipping";
				return materialLoadStatus;
			}
			
			if(materialSKUAttriutes.contains(flexTypeAttributeKey))
			{
				materialObj = validateAndPopulateMaterialSKUAttributes(flexTypeAttributeKey, (String)flexTypeAttributeValue, materialObj);
			}
			else
			{
				populateMaterialAttributesData(materialObj, materialFlexTypeObj, flexTypeAttributeKey, flexTypeAttributeValue);
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateMaterialAttributesData(flexTypeAttributesDataMap, materialObj, materialFlexTypeObj) ###");
		return materialLoadStatus;
	}
	
	/**
	 * This function is using to set Material SKU attribute values to the newly created/initialized Material object and to to validate the attribute variable type, based on the type of the variable modify attributeValue and return value
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributeValue - String
	 * @param flexTypeAttributeValue - Object
	 * @throws WTException 
	 * @throws WTPropertyVetoException
	 */
	private void populateMaterialAttributesData(LCSMaterial materialObj, FlexType materialFlexTypeObj, String flexTypeAttributeKey, Object flexTypeAttributeValue) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateMaterialAttributesData(materialObj, materialFlexTypeObj, attributeKey, attributeValue) ###");
		
		if(flexTypeAttributeValue != null && flexTypeAttributeValue != "" && flexTypeAttributeValue != " ")
		{
			if(HBIMaterialDataLoadServiceImpl.strFlexTypeAttributesKeys.contains(flexTypeAttributeKey))
			{
				//Calling a function to validate the attribute variable type, based on the type of the variable modify attributeValue and return value.
				flexTypeAttributeValue = getKeyFromAttributeDisplayValue(materialFlexTypeObj, flexTypeAttributeKey, (String) flexTypeAttributeValue);
			}
			
			materialObj.setValue(flexTypeAttributeKey, flexTypeAttributeValue);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateMaterialAttributesData(materialObj, materialFlexTypeObj, attributeKey, attributeValue) ###");
	}
	
	/**
	 * This function is using to set Material SKU attribute values to the newly created/initialized Material object.
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributeValue - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 */
	private LCSMaterial validateAndPopulateMaterialSKUAttributes(String flexTypeAttributeKey, String flexTypeAttributeValue, LCSMaterial materialObj) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.validateAndPopulateMaterialSKUAttributes(flexTypeAttributeKey, attributeValue, materialObj) ###");
		
		//This function is using to set default value if in case attribute values are empty.
		if("hbiAttrCode".equalsIgnoreCase(flexTypeAttributeKey) && !FormatHelper.hasContent(flexTypeAttributeValue))
		{
			flexTypeAttributeValue = "------";
		}
		else if("hbiColorCode".equalsIgnoreCase(flexTypeAttributeKey) && !FormatHelper.hasContent(flexTypeAttributeValue))
		{
			flexTypeAttributeValue = "000";
		}
		else if("hbiSizeCode".equalsIgnoreCase(flexTypeAttributeKey) && !FormatHelper.hasContent(flexTypeAttributeValue))
		{
			flexTypeAttributeValue = "00";
		}
		
		//This function is using to set Material SKU attribute values to the newly created/initialized Material object.
		materialObj.setValue(flexTypeAttributeKey, flexTypeAttributeValue);
		
		// LCSLog.debug("### END HBIMaterialDataLoader.validateAndPopulateMaterialSKUAttributes(flexTypeAttributeKey, attributeValue, materialObj) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to set default values(defaulting attribute values) to the newly created/initialized Material object as per the system configuration and Hane's business process
	 * @param materialObj - LCSMaterial
	 * @param materialFlexTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private LCSMaterial populateAttributeDefaultValuesOnMaterialCreateEvent(LCSMaterial materialObj, String materialFlexTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateAttributeDefaultValuesOnMaterialCreateEvent(materialObj, materialFlexTypePath) ###");
		
		//Setting default values(defaulting attribute values) to the newly created/initialized Material object as per the system configuration and Hane's business process
		materialObj.setValue(HBIProperties.hbiWorkflowProcessKey, "notRequired");
		materialObj.setValue(HBIProperties.hbiLeadTestingRequiredKey, "no");
		materialObj.setValue(HBIProperties.hbiUsageUOMKey, "ea");
		materialObj.setValue(HBIProperties.hbiConversionUsageKey, 1.00);
		materialObj.setValue(HBIProperties.materialColorControlledKey, "2");
		materialObj.setValue(HBIProperties.hbiMaterialSourceTypeKey, "createdFromGlobalMatSystems");
		materialObj.setValue(HBIProperties.hbiHemisphereKey, "both");
		materialObj.setValue(HBIProperties.hbiConversionFactorKey, 1.0000);
		materialObj.setValue("hbiViewRestrictor", "hbiIntegrationAdminOnly");
		
		//calling a function to validate and set 'Buy Group' for a material object (deriving the "Buyer Group' based on the material type path of the object)
		materialObj = populateBuyerGroupOnMaterialCreateEvent(materialObj, materialFlexTypePath);
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateAttributeDefaultValuesOnMaterialCreateEvent(materialObj, materialFlexTypePath) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to validate and set 'Buy Group' for a material object (deriving the "Buyer Group' based on the material type path of the object)
	 * @param materialObj - LCSMaterial
	 * @param materialFlexTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private LCSMaterial populateBuyerGroupOnMaterialCreateEvent(LCSMaterial materialObj, String materialFlexTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateBuyerGroupOnMaterialCreateEvent(materialObj, materialFlexTypePath) ###");
		
		if(materialFlexTypePath.contains("Accessories"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiAccessoriesRCM");
		}
		else if(materialFlexTypePath.contains("Fabric") || materialFlexTypePath.contains("Fabrics"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiBuyFabricRCM");
		}
		else if(materialFlexTypePath.contains("Process Chemical"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiChemicalRCM");
		}
		else if(materialFlexTypePath.contains("Elastics"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiElasticRCM");
		}
		else if(materialFlexTypePath.contains("Garment Label"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiLabelRCM");
		}
		else if(materialFlexTypePath.contains("Casing"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiCasingRCM");
		}
		else if(materialFlexTypePath.contains("Packaging"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiPackagingRCM");
		}
		else if(materialFlexTypePath.contains("Threads"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiThreadRCM");
		}
		else if(materialFlexTypePath.contains("Yarn"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiYarnRCM");
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateBuyerGroupOnMaterialCreateEvent(materialObj, materialFlexTypePath) ###");
		return materialObj;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////	Following functions are specific to UPDATE Material Event in FlexPLM    ///////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This function is using to update material object in FlexPLM using the given material object and material level/scope attributes map contains key-values
	 * @param materialObj
	 * @param flexTypeAttributesDataMap
	 * @return materialLoadStatus - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public String updateMaterialObject(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap) throws WTException, WTPropertyVetoException 
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.updateMaterialObject(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap) ###");
		Map<String, String> changeSetMap = new HashMap<String, String>();
		String materialLoadStatus = "Material Loaded Successfully";
		
		boolean hasUpdateMaterialAccess = hasUpdateMaterialAccess(materialObj, flexTypeAttributesDataMap);
		if(!hasUpdateMaterialAccess)
		{
			materialLoadStatus = "Load Failed!, This Material Already Exists in PLM, Only material owner can update material data, material owner in PLM is "+ materialObj.getValue(hbiMaterialSourceTypeKey);
			return materialLoadStatus;
		}
		
		for(String flexTypeAttributeKey : flexTypeAttributesDataMap.keySet())
		{
			//This block of code is to handle attribute does not exists issue, populate the error message across each rows to populate the logical info message
			Collection<String> flexTypePathAttributeKeys = getAllAttributeKeysForFlexType(materialObj.getFlexType());
			if(!flexTypePathAttributeKeys.contains(flexTypeAttributeKey))
			{
				materialLoadStatus = "Attribute-Key = "+flexTypeAttributeKey +" does not exists in FlexPLM for "+ materialObj.getFlexType().getFullName(true) +", Data-Load Skipping";
				return materialLoadStatus;
			}
			
			changeSetMap = getMaterialUpdateChangeSetMap(materialObj, flexTypeAttributesDataMap, flexTypeAttributeKey, changeSetMap);
		}
		
		//This logic is to update Material Object only if there is any changes in the data provided from global system and in the existing material object data
		if(changeSetMap.size() > 0 && changeSetMap.containsValue("true"))
		{
			LCSMaterialLogic.deriveFlexTypeValues(materialObj);
			LCSMaterialHelper.service.saveMaterial(materialObj);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.updateMaterialObject(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap) ###");
		return materialLoadStatus;
	}
	
	/**
	 * This function is using to validate the changeSet for each of the FlexTypeAttribute based on the given dataSet, FlexTypeAttriute key and material object
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param flexTypeAttributeKey - String 
	 * @param changeSetMap - Map<String, String>
	 * @return changeSetMap - Map<String, String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private Map<String, String> getMaterialUpdateChangeSetMap(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap, String flexTypeAttributeKey, Map<String, String> changeSetMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.getMaterialUpdateChangeSetMap(materialObj, TypeAttributesDataMap, attributeKey, changeSetMap) ###");
		
		if(HBIMaterialDataLoadServiceImpl.strFlexTypeAttributesKeys.contains(flexTypeAttributeKey) && !skipAttributesOnUpdate.contains(flexTypeAttributeKey))
		{
			changeSetMap = populateStringTypeAttributeData(materialObj, flexTypeAttributeKey, flexTypeAttributesDataMap, changeSetMap);
		}
		else if(HBIMaterialDataLoadServiceImpl.dblFlexTypeAttributesKeys.contains(flexTypeAttributeKey) || HBIMaterialDataLoadServiceImpl.intFlexTypeAttributesKeys.contains(flexTypeAttributeKey))
		{
			changeSetMap = populateDoubleTypeAttributeData(materialObj, flexTypeAttributeKey, flexTypeAttributesDataMap, changeSetMap);
		}
		else if(HBIMaterialDataLoadServiceImpl.boolFlexTypeAttributesKeys.contains(flexTypeAttributeKey))
		{
			changeSetMap = populateBooleanAttributeData(materialObj, flexTypeAttributeKey, flexTypeAttributesDataMap, changeSetMap);
		}
		else if(!skipAttributesOnUpdate.contains(flexTypeAttributeKey))
		{
			changeSetMap = populateStringTypeAttributeData(materialObj, flexTypeAttributeKey, flexTypeAttributesDataMap, changeSetMap);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.getMaterialUpdateChangeSetMap(materialObj, TypeAttributesDataMap, attributeKey, changeSetMap) ###");
		return changeSetMap;
	}
	
	/**
	 * This function is using to validate the attribute value on Material Object before we set Load Value on that Material Object, If Material Object has the same value on attribute then we will not set the load value.
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param changeSetMap - Map<String, Object>
	 * @return changeSetMap - Map<String, String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private Map<String, String> populateStringTypeAttributeData(LCSMaterial materialObj, String flexTypeAttributeKey, Map<String, Object> flexTypeAttributesDataMap, Map<String, String> changeSetMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateStringTypeAttributeData(materialObj, flexAttributeKey, matAttrsDataMap, changeSetMap) ###");
		String attributeValueFromMap = ""+(String) flexTypeAttributesDataMap.get(flexTypeAttributeKey);
		String attributeValueFromMat = ""+(String) materialObj.getValue(flexTypeAttributeKey);
		
		//Calling a function to validate the attribute variable type, based on the type of the variable modify attribute-value and return value to a caller
		attributeValueFromMap = getKeyFromAttributeDisplayValue(materialObj.getFlexType(), flexTypeAttributeKey, attributeValueFromMap);
		
		if(!attributeValueFromMap.equalsIgnoreCase(attributeValueFromMat))
		{
			materialObj.setValue(flexTypeAttributeKey, attributeValueFromMap);
			changeSetMap.put(flexTypeAttributeKey, "true");
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateStringTypeAttributeData(materialObj, flexAttributeKey, matAttrsDataMap, changeSetMap) ###");
		return changeSetMap;
	}
	
	/**
	 * This function is using to validate the attribute value on Material Object before we set Load Value on that Material Object, If Material Object has the same value on attribute then we will not set the load value.
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param changeSetMap - Map<String, Object>
	 * @return changeSetMap - Map<String, String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private Map<String, String> populateDoubleTypeAttributeData(LCSMaterial materialObj, String flexTypeAttributeKey, Map<String, Object> flexTypeAttributesDataMap, Map<String, String> changeSetMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateStringTypeAttributeData(materialObj, flexAttributeKey, matAttrsDataMap, changeSetMap) ###");
		double attributeValueFromMap = (Double) flexTypeAttributesDataMap.get(flexTypeAttributeKey);
		double attributeValueFromMat = (Double) materialObj.getValue(flexTypeAttributeKey);
		
		if(attributeValueFromMap != attributeValueFromMat)
		{
			materialObj.setValue(flexTypeAttributeKey, attributeValueFromMap);
			changeSetMap.put(flexTypeAttributeKey, "true");
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateStringTypeAttributeData(materialObj, flexAttributeKey, matAttrsDataMap, changeSetMap) ###");
		return changeSetMap;
	}
	
	/**
	 * This function is using to validate the attribute value on Material Object before we set Load Value on that Material Object, If Material Object has the same value on attribute then we will not set the load value.
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @param changeSetMap - Map<String, Object>
	 * @return changeSetMap - Map<String, String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private Map<String, String> populateBooleanAttributeData(LCSMaterial materialObj, String flexTypeAttributeKey, Map<String, Object> flexTypeAttributesDataMap, Map<String, String> changeSetMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateStringTypeAttributeData(materialObj, flexAttributeKey, matAttrsDataMap, changeSetMap) ###");
		boolean attributeValueFromMap = (Boolean) flexTypeAttributesDataMap.get(flexTypeAttributeKey);
		boolean attributeValueFromMat = (Boolean) materialObj.getValue(flexTypeAttributeKey);
		
		if(attributeValueFromMap != attributeValueFromMat)
		{
			materialObj.setValue(flexTypeAttributeKey, attributeValueFromMap);
			changeSetMap.put(flexTypeAttributeKey, "true");
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateStringTypeAttributeData(materialObj, flexAttributeKey, matAttrsDataMap, changeSetMap) ###");
		return changeSetMap;
	}
	
	/**
	 * Calling a function to validate the attribute variable type, based on the type of the variable modify attributeValue and return value.
	 * @param materialFlexTypeObj - FlexType
	 * @param flexTypeAttributeKey - String
	 * @param flexTypeAttributeValue - String
	 * @return flexTypeAttributeValue - String
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	private String getKeyFromAttributeDisplayValue(FlexType materialFlexTypeObj, String flexTypeAttributeKey, String flexTypeAttributeValue) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.getKeyFromAttributeDisplayValue(materialFlexType, flexTypeAttributeKey, flexTypeAttributeValue) ###");
		String attributeVariableDataType = materialFlexTypeObj.getAttribute(flexTypeAttributeKey).getAttVariableType();
		
		if(attributeVariableDataTypes.contains(attributeVariableDataType))
		{
			String attributeValueListValue = "";
			Collection<FlexObject> attributeValueListCollection = materialFlexTypeObj.getAttribute(flexTypeAttributeKey).getAttValueList().getDataSet();
			
			//Iterating on each FlexObject to get AttributeValueList Key and Display Name, attributeValueList value is needed to return from function header
			for(FlexObject flexObj : attributeValueListCollection)
			{
				//Get AttributeValueList Key and Display Name from the given FlexObject, compare with the given AttributeValueList Display Name and return AttributeValueList Key
				attributeValueListValue = flexObj.getString("VALUE");
				if(FormatHelper.hasContent(attributeValueListValue) && attributeValueListValue.equalsIgnoreCase(flexTypeAttributeValue))
				{
					flexTypeAttributeValue = flexObj.getString("KEY");
					break;
				}
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.getKeyFromAttributeDisplayValue(materialFlexType, flexTypeAttributeKey, flexTypeAttributeValue) ###");
		return flexTypeAttributeValue;
	}
	
	/**
	 * This function is using to return a collection of FlexTypeAttribute keys for the given FlexType Path and returning attribute keys collection from header
	 * @param materialFlexTypeObj - FlexType
	 * @return flexTypeAttributeKeysCollection - Collection<String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private Collection<String> getAllAttributeKeysForFlexType(FlexType materialFlexTypeObj) throws WTException, WTPropertyVetoException 
	{
		// LCSLog.debug("### END HBIMaterialDataLoader.getAllAttributeKeysForFlexType(FlexType materialFlexTypeObj) ###");
		Collection<String> flexTypeAttributeKeysCollection = new ArrayList<String>();
		String flexTypeAttributeKey = "";
		
		//Get all FlexTypeAttribute from the given FlexType object, Iterate on each attributes to get the attribute key, add attribute-key to a collection and return
		Collection<FlexTypeAttribute> flexTypeAttributeCollection = materialFlexTypeObj.getAllAttributes();
		if(flexTypeAttributeCollection != null && flexTypeAttributeCollection.size() > 0)
		{
			for(FlexTypeAttribute flexTypeAttributeObj : flexTypeAttributeCollection)
			{
				flexTypeAttributeKey = flexTypeAttributeObj.getAttKey();
				flexTypeAttributeKeysCollection.add(flexTypeAttributeKey);
				
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.getAllAttributeKeysForFlexType(FlexType materialFlexTypeObj) ###");
		return flexTypeAttributeKeysCollection;
	}
	
	/**
	 * This function is using to validate the material update access, material update access is derived using an attribute 'Material Source Type' on material
	 * @param materialObj - LCSMaterial
	 * @param flexTypeAttributesDataMap - Map<String, Object>
	 * @return hasUpdateMaterialAccess - boolean
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public boolean hasUpdateMaterialAccess(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.hasUpdateMaterialAccess(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap) ###");
		boolean hasUpdateMaterialAccess = false;
		
		//Initialize MaterialSourcetype value from the a material object as well as material dataMap contains attribute key-value, using to check update access
		String materialSourceType = (String) materialObj.getValue(hbiMaterialSourceTypeKey);
		String materialSourceType_DataFile = (String) flexTypeAttributesDataMap.get(hbiMaterialSourceTypeKey);
		String materialSourceTypeKey = getKeyFromAttributeDisplayValue(materialObj.getFlexType(), hbiMaterialSourceTypeKey, materialSourceType_DataFile);
		
		//
		if(materialSourceType_DataFile.equalsIgnoreCase(materialSourceType) || materialSourceTypeKey.equalsIgnoreCase(materialSourceType))
		{
			hasUpdateMaterialAccess = true;
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.hasUpdateMaterialAccess(LCSMaterial materialObj, Map<String, Object> flexTypeAttributesDataMap) ###");
		return hasUpdateMaterialAccess;
	}
}
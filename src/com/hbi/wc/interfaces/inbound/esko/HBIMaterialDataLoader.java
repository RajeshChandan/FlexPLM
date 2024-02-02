package com.hbi.wc.interfaces.inbound.esko;

import java.util.Map;

import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.load.LoadMaterial;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialLogic;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.part.LCSPart;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.supplier.LCSSupplierQuery;
import com.lcs.wc.util.AttributeValueSetter;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.method.RemoteAccess;

/**
 * HBIMaterialDataLoader.java
 *
 * This class contains generic functions which are using to fetch(read) Material object for the given data(Material Name, Material Type, Attribute Code, Color Code and Size Code), based 
 * the Material Object status perform create/update event in FlexPLM with the data (attribute data) provided from ESKO server and populating some of the attributes configured default data 
 * @author Abdul.Patel@Hanes.com
 * @since June-9-2015
 */
public class HBIMaterialDataLoader implements RemoteAccess
{
	/**
	 * This function is used to Process Material(perform create/update event) in FlexPLM based on the given material data map from ESKO server & return material process status/comments
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @return materialDataLoadComments - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public String validateAndProcessMaterialDataLoadRequest(Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException
	{
		LCSLog.debug("### START HBIMaterialDataLoader.validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj) ###");
		LCSMaterial placeHolderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
		String materialDataLoadComments = "";

		//Get Material Name and Material FlexType Path from the given data map, which are using as unique parameter to fetch/get Material object and process ESKO Material load request
		String materialName = (String) eskoMaterialDataMapObj.get("flexAttname");
		LCSLog.debug(" HBIMaterialDataLoader.validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj) :: Material Name = " + materialName);
		String materialFlexTypePath = (String) eskoMaterialDataMapObj.get(HBIProperties.materialFlexTypePathKey);
		
		if(FormatHelper.hasContent(materialName)&&materialName.startsWith("AQ")){

			materialFlexTypePath="Material\\Accessories";
			
			if(eskoMaterialDataMapObj.containsKey("flexAtthbiApplication")){
				String flexAtthbiMinorCategory=(String)eskoMaterialDataMapObj.get("flexAtthbiApplication");
	       	 	if("heatSeal".equals(flexAtthbiMinorCategory)){
	       	 	flexAtthbiMinorCategory="heatSeals";	       	 		
	       	 	}
	       	 	else if("heatTransfers".equals(flexAtthbiMinorCategory)){
		       	 	flexAtthbiMinorCategory="heatTransfer";	       	 		
		       	 	}
	       	 	else{
	       	 	eskoMaterialDataMapObj.put("flexAtthbiMinorCategory", flexAtthbiMinorCategory);
	       	 	}
				eskoMaterialDataMapObj.remove("flexAtthbiApplication");

			}
			
			if(eskoMaterialDataMapObj.containsKey("hbiCountryCode")){
				eskoMaterialDataMapObj.remove("hbiCountryCode");
			}
			if(eskoMaterialDataMapObj.containsKey("hbiGarmentSize")){
				eskoMaterialDataMapObj.remove("hbiGarmentSize");
			}
			if(eskoMaterialDataMapObj.containsKey("flexAtthbiLabelFormat")){
				eskoMaterialDataMapObj.remove("flexAtthbiLabelFormat");
			}
			if(eskoMaterialDataMapObj.containsKey("flexAtthbiLanguage")){
				eskoMaterialDataMapObj.remove("flexAtthbiLanguage");
			}
			if(eskoMaterialDataMapObj.containsKey("flexAtthbiMatLabelType")){
				eskoMaterialDataMapObj.remove("flexAtthbiMatLabelType");
			}
			if(eskoMaterialDataMapObj.containsKey("flexAtthbiRetailMarket")){
				eskoMaterialDataMapObj.remove("flexAtthbiRetailMarket");
			}


	}
		if(FormatHelper.hasContent(materialName) && FormatHelper.hasContent(materialFlexTypePath)) 
		{
			LCSMaterial materialObj = findMaterialByMaterialSKUAndMaterialType(eskoMaterialDataMapObj, materialName, materialFlexTypePath);
			LCSLog.debug(" HBIMaterialDataLoader.validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj) :: Material Object = " + materialObj);
			
			//UPDATE EVENT:- Given Material(details provided from ESKO server) exists in FlexPLM, invoking internal functions to update the given data and persist the Material object
			if (materialObj != null && materialObj != placeHolderMaterialObj) 
			{
				//Calling a function which is using to validate the 'Buy' flag change set status, based on the status get all the suppliers associated with the material to set template
				validateAndSetLifeCycleTemplateOnMaterialSupplier(materialObj, eskoMaterialDataMapObj);
				materialDataLoadComments = validateAndUpdateMaterialObject(materialObj, eskoMaterialDataMapObj, materialName);
			}
			//CREATE EVENT:- Given Material(details provided from ESKO server) does not exists in FlexPLM, invoking internal functions to create new Material with the given data set
			else 
			{
				materialDataLoadComments = validateAndCreateMaterialObject(eskoMaterialDataMapObj, materialName, materialFlexTypePath);
			}
		}
		else
		{
			materialDataLoadComments = getMaterialDataLoadErrorComments(materialName, materialFlexTypePath, (String)eskoMaterialDataMapObj.get(HBIProperties.eskoProjectIDKey));
		}

		LCSLog.debug(" HBIMaterialDataLoader.validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj) materialDataLoadComments = "+ materialDataLoadComments);
		LCSLog.debug("### END HBIMaterialDataLoader.validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj) ###");
		return materialDataLoadComments;
	}
	
	/**
	 * This function is using to validate the given 'material_name/id' and 'material_flextype', formating 'material data load error comments' based on the given data validation status
	 * @param materialName - String
	 * @param materialFlexTypePath - String
	 * @param eskoProjectID - String
	 * @return materialDataLoadComments - String
	 * @throws WTException
	 */
	public String getMaterialDataLoadErrorComments(String materialName, String materialFlexTypePath, String eskoProjectID) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.getMaterialDataLoadErrorComments(materialName, materialFlexTypePath) ###");
		String materialDataLoadComments = "";
		
		if(!FormatHelper.hasContent(materialName))
		{	
			materialDataLoadComments = "Invalid Material Name(Part No) in the given Material Data where ESKO Project ID = " + eskoProjectID;
		}
		else if(!FormatHelper.hasContent(materialFlexTypePath))
		{
			materialDataLoadComments = "Invalid Material Type/FlexType Path in the given Material Data where ESKO Project ID = " + eskoProjectID;
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.getMaterialDataLoadErrorComments(materialName, materialFlexTypePath) ###");
		return materialDataLoadComments;
	}

	/**
	 * This function is using to process supplier(perform create) in FlexPLM based on the given supplier name data map from ESKO server and return supplier process status or comments
	 * @param materialObj - LCSMaterial
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @return supplierDataLoadComments - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public String validateAndProcessSupplierDataLoadRequest(LCSMaterial materialObj, String supplierName, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException 
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.validateAndProcessSupplierDataLoadRequest(materialObj, eskoMaterialDataMapObj) ###");
		String supplierDataLoadComments = "";
		
		//Validate the 'Supplier Name', get 'Supplier Object' for the given 'Supplier Name', validate the existing of 'Material-Supplier' link for the given Material and Supplier
		if (FormatHelper.hasContent(supplierName)) 
		{
			LCSSupplier supplierObj = findSupplierByNameType(supplierName);
			if(supplierObj != null && !supplierObj.isPlaceholder())
			{
				//LCSMaterialSupplier materialSupplierObj = LCSMaterialSupplierQuery.findMaterialSupplier((WTPartMaster)materialObj.getMaster(), (LCSSupplierMaster)supplierObj.getMaster());
				LCSMaterialSupplier materialSupplierObj = LCSMaterialSupplierQuery.findMaterialSupplier(materialObj.getMaster(), supplierObj.getMaster());
				if(materialSupplierObj != null && !materialSupplierObj.isPlaceholder() && materialSupplierObj.isActive())
				{
					supplierDataLoadComments = "Material-Supplier association already exists FlexPLM where Material Name = " + materialObj.getName() + " and Supplier Name = " + supplierName + " hence skipping the process of associating Material with the Supplier";
				}
				else
				{
					//Formating necessary parameters (Collection of Supplier OID's) and invoking an existing API to associate 'Material' and 'Supplier' (add Supplier to the Material)
					String supplierOID = FormatHelper.getVersionId(supplierObj);
					Collection<String> supplierOIDCollection = new ArrayList<String>();
					supplierOIDCollection.add(supplierOID);
					LCSMaterialHelper.service.addMaterialSuppliers(materialObj, supplierOIDCollection);
				}
			}
			else
			{
				supplierDataLoadComments = "Given Vendor/Supplier does not exists FlexPLM where Supplier Name = " + supplierName + " and ESKO Project ID = "+ eskoMaterialDataMapObj.get(HBIProperties.eskoProjectIDKey);
			}
		}
		else
		{
			supplierDataLoadComments = "Invalid Vendor/Supplier Name in the given Material Data where ESKO Project ID = "+ eskoMaterialDataMapObj.get(HBIProperties.eskoProjectIDKey);
		}

		// LCSLog.debug("### END HBIMaterialDataLoader.validateAndProcessSupplierDataLoadRequest(materialObj, eskoMaterialDataMapObj) ###");
		return supplierDataLoadComments;
	}

	/**
	 * This function is using to create/initialize new material object with the given data map(Like Material Name, Material FlexType Id Path and Material-Attributes data) and persist
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @param materialName - String
	 * @param materialFlexTypePath - String
	 * @return materialDataLoadComments - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private String validateAndCreateMaterialObject(Map<String, Object> eskoMaterialDataMapObj, String materialName, String materialFlexTypePath) throws WTException, WTPropertyVetoException 
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.validateAndCreateMaterialObject(eskoMaterialDataMapObj, materialName, materialFlexTypePath) ###");
		String materialDataLoadComments = "";
		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePath);

		//Initializing Material object (creating new Material instance) and populating necessary information (Master Reference, FlexTypePath & Material_Name) to the initialized object
		LCSMaterial materialObj = LCSMaterial.newLCSMaterial();
		materialObj.setMaster(new LCSMaterialMaster());
		materialObj.setFlexType(materialFlexTypeObj);
		materialObj.setName(materialName.trim());
		materialObj.setValue(HBIProperties.materialNameKey, materialName.trim());

		//Calling a function which is using to set Attribute-Default values (Attribute default values configured in type manager as well specified by the business user in requirement)
		materialObj = populateAttributeDefaultValuesOnMaterialCreateEvent(materialObj, eskoMaterialDataMapObj);
		
		//Calling a function which is using to set Attribute-Default values (Attribute default values configured in type manager for each drop down attributes) on material object
		String majorMinorCategoryType = (String) eskoMaterialDataMapObj.get("MajorMinorCategory");
		populateAttributeDefaultValuesForMaterialObject(materialObj, materialFlexTypePath, majorMinorCategoryType);
		
		if(materialFlexTypePath.contains("Garment Label")){
		materialObj.setValue("hbiGarmentSize", (String) eskoMaterialDataMapObj.get("hbiGarmentSize"));
		}


		//Calling a function which is using to update the given Material object/instance to populate the given attribute data (attribute data provided from ESKO server) and persist
		validateAndUpdateMaterialObject(materialObj, eskoMaterialDataMapObj, materialName);

		materialDataLoadComments = "Material = " + materialName + " created in the Hierarchy " + materialFlexTypePath + " with all the latest attributes data which is provided from ESKO server";

		// LCSLog.debug("### END HBIMaterialDataLoader.validateAndCreateMaterialObject(eskoMaterialDataMapObj, materialName, materialFlexTypePath) ###");
		return materialDataLoadComments;
	}
	
	/**
	 * This function is using to set the default values for a material object on create event (default data for each attributes are decided based on the business process and data process
	 * @param materialObj - LCSMaterial
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private LCSMaterial populateAttributeDefaultValuesOnMaterialCreateEvent(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateAttributeDefaultValuesOnMaterialCreateEvent(LCSMaterial materialObj, eskoMaterialDataMapObj) ###");
		
		//Setting default values(defaulting attribute values) to the newly created/initialized Material object as per the system configuration and Hane's business process
		materialObj.setValue(HBIProperties.hbiWorkflowProcessKey, "notRequired");
		materialObj.setValue(HBIProperties.hbiLeadTestingRequiredKey, "no");
		materialObj.setValue(HBIProperties.hbiUsageUOMKey, "ea");
		materialObj.setValue(HBIProperties.hbiConversionUsageKey, 1.00);
		materialObj.setValue(HBIProperties.materialColorControlledKey, "2");
		materialObj.setValue(HBIProperties.hbiMaterialSourceTypeKey, "createdInEsko");
		materialObj.setValue(HBIProperties.hbiMatSpecFinalKey, new Date());
		materialObj.setValue(HBIProperties.hbiHemisphereKey, "both");
		materialObj.setValue(HBIProperties.hbiConversionFactorKey, 1.0000);

		
		//Get Color_Code from the given data Map, validate the Color_Code and populate Color_code, Attribute_Code and Size_Code (which are derived from 17 Digit Part Name) on the Material
		String colorCode = (String) eskoMaterialDataMapObj.get(HBIProperties.hbiColorCodeKey);
		if(FormatHelper.hasContent(colorCode))
		{
			materialObj.setValue(HBIProperties.hbiColorCodeKey, colorCode);
			materialObj.setValue(HBIProperties.hbiAttrCodeKey, (String) eskoMaterialDataMapObj.get(HBIProperties.hbiAttrCodeKey));
			materialObj.setValue(HBIProperties.hbiSizeCodeKey, (String) eskoMaterialDataMapObj.get(HBIProperties.hbiSizeCodeKey));

		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateAttributeDefaultValuesOnMaterialCreateEvent(LCSMaterial materialObj, eskoMaterialDataMapObj) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to populate attribute default values for a given object by reading pre-configured default values from properties file based on the unique property keys
	 * @param materialObj - LCSMaterial
	 * @param materialFlexTypePath - String
	 * @param majorMinorCategoryType - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private LCSMaterial populateAttributeDefaultValuesForMaterialObject(LCSMaterial materialObj, String materialFlexTypePath, String majorMinorCategoryType) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.populateAttributeDefaultValuesForMaterialObject(materialObj, materialFlexTypePath, majorMinorCategoryType) ###");
		String majorCategoryAttributePropertyEntryKey = "com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataLoader.MajorCategory.";
		String minorCategoryAttributePropertyEntryKey = "com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataLoader.MinorCategory.";
		
		//Validating the given 'material_type' (Packaging/Casing), updating the material object for set of default values(as per the type manager setup) based on the material types
		if(FormatHelper.hasContent(materialFlexTypePath) && materialFlexTypePath.startsWith("Material\\Packaging\\"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiPackagingRCM");
		}
		else if(FormatHelper.hasContent(materialFlexTypePath) && materialFlexTypePath.startsWith("Material\\Casing\\"))
		{
			materialObj.setValue(HBIProperties.hbiBuyerGroupKey, "hbiCasingRCM");
		}
		
		//Validating the 'Major Category', 'Minor Category' Type, format the given 'Type' as needed (which is using as a unique parameter to get pre-configured value from properties file)
		if(FormatHelper.hasContent(majorMinorCategoryType))
		{
			String formattedMaterialFlexTypes = majorMinorCategoryType.replaceAll("\\\\", "").replaceAll("-", "").replaceAll(" ", "");
			majorCategoryAttributePropertyEntryKey = majorCategoryAttributePropertyEntryKey.concat(formattedMaterialFlexTypes);
			minorCategoryAttributePropertyEntryKey = minorCategoryAttributePropertyEntryKey.concat(formattedMaterialFlexTypes);
			String majorCategoryAttributeValue = LCSProperties.get(majorCategoryAttributePropertyEntryKey);
			String minorCategoryAttributeValue = LCSProperties.get(minorCategoryAttributePropertyEntryKey);
			materialObj.setValue(HBIProperties.hbiMajorCategoryKey, ""+majorCategoryAttributeValue);
			materialObj.setValue(HBIProperties.hbiMinorCategoryKey, ""+minorCategoryAttributeValue);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.populateAttributeDefaultValuesForMaterialObject(materialObj, materialFlexTypePath, majorMinorCategoryType) ###");
		return materialObj;
	}

	/**
	 * This function is using to update the given Material object to populate the given attribute data (attribute data provided from ESKO server) and persist the updated material object
	 * @param materialObj - LCSMaterial
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @return materialDataLoadComments - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private String validateAndUpdateMaterialObject(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj, String materialName) throws WTException, WTPropertyVetoException 
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.validateAndUpdateMaterialObject(materialObj, eskoMaterialDataMapObj) ###");
		String brandUsageAttributeKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.Division", "flexAtthbiBrandValue");
		String materialDataLoadComments = "";
		String flexTypeAttributeKey = "";
		Object flexTypeAttributeValue = null;
		boolean isHosieryOrSocks = false;
		
		//Iterating on each Attribute-Key (from the given Map), validate and identify the actual FlexTypeAttribute-Key, get FlexTypeAttribute-Value, populate latest changes on Material
		for(String strFlexTypeAttributeKey : eskoMaterialDataMapObj.keySet()) 
		{
			if(strFlexTypeAttributeKey.startsWith(HBIProperties.flexTypeAttributeKeyAppender)) 
			{
				flexTypeAttributeKey = strFlexTypeAttributeKey.replaceFirst(HBIProperties.flexTypeAttributeKeyAppender, "").trim();
				flexTypeAttributeValue = eskoMaterialDataMapObj.get(strFlexTypeAttributeKey);

				//This block of code is to change 'Workflow Process' flag from 'Required' to 'Not Required' based on the values of the 'Division/Brand Usage' Attribute flowing from ESKO
				if(brandUsageAttributeKey.equalsIgnoreCase(strFlexTypeAttributeKey) && ("hbiSocks".equalsIgnoreCase((String)flexTypeAttributeValue) || "hbiHosiery".equalsIgnoreCase((String)flexTypeAttributeValue)))
				{
					materialObj.setValue(HBIProperties.hbiWorkflowProcessKey, "notRequired");
					isHosieryOrSocks = true;
				}
				System.out.println("flexTypeAttribute>>>>>>>>>>"+flexTypeAttributeKey+"  " + flexTypeAttributeValue);
				//Updating the given Material object/instance to populate the latest data (values for the given set of Attributes, data provided from ESKO-WebCenter server)
				try {
					materialObj.setValue(flexTypeAttributeKey, flexTypeAttributeValue);
				} catch (NumberFormatException e) {
					
				 String flexTypeAttributeValueCheck = (String) flexTypeAttributeValue;
				 System.out.println(" before if flexTypeAttribute>>>>>>>>>>>>>>>>>>>>"+flexTypeAttributeValueCheck);
				 flexTypeAttributeValueCheck = flexTypeAttributeValueCheck.substring(0, flexTypeAttributeValueCheck.length()-1);
				 System.out.println(" after change flexTypeAttribute>>>>>>>>>>>>>>>>>>>>"+flexTypeAttributeValueCheck);
				materialObj.setValue(flexTypeAttributeKey, flexTypeAttributeValueCheck);
				
				}

				
			}
		}
		
		String brandUsage=(String) eskoMaterialDataMapObj.get("Brand Usage");
		String division=(String) eskoMaterialDataMapObj.get("Division");

		
        brandUsage=  LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.BrandUsage."+brandUsage);
		division=  LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.hbistylegroupSL."+division);

		if((materialObj.getFlexType().getFullName()).contains("Garment Label")){
	    	String hbiLabelCountry=(String)materialObj.getValue("hbiLabelCountry");

	    	
	    	if(FormatHelper.hasContent(hbiLabelCountry)){
	    	String countryToFindCode=materialObj.getFlexType().getAttribute("hbiLabelCountry").getAttValueList().getValue(hbiLabelCountry, null);
            if(FormatHelper.hasContent(countryToFindCode) && !"Not Specified / Sourced".equals(countryToFindCode)&&countryToFindCode.contains("("))
            {
	    	int index1= countryToFindCode.indexOf("(");
	        int index2= countryToFindCode.indexOf(")");
	        countryToFindCode=countryToFindCode.substring(index1+1,index2);
            }

    		LCSCountry co=HBIMaterialDataExtractor.getCountryByCriteria(countryToFindCode.toUpperCase(), "Country");

        	materialObj.setValue("hbiCountryCode",  co);

	    	}
	    	
            materialObj.setValue("hbiBrandValue",brandUsage);
            materialObj.setValue("hbistylegroupSL",division);

            


		materialObj.setValue("hbiGarmentSize", eskoMaterialDataMapObj.get("hbiGarmentSize"));
		}
		
		if((materialObj.getFlexType().getFullName()).contains("Accessories")){
			materialObj.setValue("hbiMaterialSubType", "attribution");
		     materialObj.setValue("hbiBrandValue",brandUsage);

			
	
		}
    	//materialObj.setPartPrimaryImageURL((String)eskoMaterialDataMapObj.get("imageUrl"));
		materialObj.setPrimaryImageURL((String)eskoMaterialDataMapObj.get("imageUrl"));





		//Persist the Material object/instance to get latest changes (latest updates to the Material object/instance based on the given ESKO-WebCenter Project and Documents data)
		materialDataLoadComments = updateAndPersistMaterialObject(materialObj, eskoMaterialDataMapObj, isHosieryOrSocks);
		
		// LCSLog.debug("### END HBIMaterialDataLoader.validateAndUpdateMaterialObject(materialObj, eskoMaterialDataMapObj) ###");
		return materialDataLoadComments;
	}
	
	/**
	 * This function is using to validate 'Buy' and 'Devision' attribute data, based on the validation status re-initializing the 'WorkFlow Process' and updating the given material object
	 * @param materialObj - LCSMaterial
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @param isHosieryOrSocks - boolean
	 * @return materialDataLoadComments - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private String updateAndPersistMaterialObject(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj, boolean isHosieryOrSocks)
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.updateAndPersistMaterialObject(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj, isHosieryOrSocks) ###");
		String imageBaseLocation = LCSProperties.get("com.lcs.wc.content.imageURL",	"/Windchill/images");
		String buyFlagAttributeKey = HBIProperties.flexTypeAttributeKeyAppender.concat(HBIProperties.hbiBuyOrNotBuyKey);
		String materialDataLoadComments = "";
		
		//Validate the eskoDataMap (map contains key-value of the ESKO-FlexPLM mapping attributes data) for specific business logic (logic around 'Buy' Flag and ' WorkFlow Process'
		if(eskoMaterialDataMapObj.keySet().contains(buyFlagAttributeKey) && !isHosieryOrSocks)
		{
			String buyFlagStatus = (String) eskoMaterialDataMapObj.get(buyFlagAttributeKey);
			if("true".equalsIgnoreCase(buyFlagStatus))
			{
				materialObj.setValue(HBIProperties.hbiWorkflowProcessKey, "required");
			}
			else if("false".equalsIgnoreCase(buyFlagStatus))
			{
				materialObj.setValue(HBIProperties.hbiWorkflowProcessKey, "notRequired");
			}
		}
		
		//Get ESKO-WebCenter 'Project-Document' name (which is using as Material thumb nail in FlexPLM), validate the given name, format the name as needed and update on object
		//String eskoDocumentName = (String) eskoMaterialDataMapObj.get("eskoDocName");
		/*if(FormatHelper.hasContent(eskoDocumentName)) 
		{
			eskoDocumentName = imageBaseLocation.trim().concat("/").concat(eskoDocumentName);
			materialObj.setPartPrimaryImageURL(eskoDocumentName);
		}*/

		try {
			LCSMaterialLogic.deriveFlexTypeValues(materialObj);
			LCSMaterialHelper.service.saveMaterial(materialObj);
			deleteLogEntry(materialObj);

		} catch (WTException e) {
try {
	ESKOLogEntryForFailures.logTransaction(new Date(), materialObj.getName(), e.getMessage(),false);
} catch (WTException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}

		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.updateAndPersistMaterialObject(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj, isHosieryOrSocks) ###");
		return materialDataLoadComments;
	}
	
	private void deleteLogEntry(LCSMaterial materialObj) {
	    //FlexType flextype = (FlexType) LoadCommon.getCachedFlexTypeFromPath ("Log Entry\\ESKO-PLM FAILURE LOG ENTRY");
		FlexType flextype = null;
		try {
			flextype = (FlexType) FlexTypeCache.getFlexTypeFromPath("Log Entry\\ESKO-PLM FAILURE LOG ENTRY");
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// TODO Auto-generated method stub
		try {
			LCSLogEntry oldLog=ESKOLogEntryForFailures.checkForLogEntryExistance((String)materialObj.getValue("name"), flextype);
			LCSLogEntryLogic logic=new LCSLogEntryLogic();
			if(oldLog!=null){
			logic.delete(oldLog);
			LCSLog.debug("Deleted Log Entry for"+(String)materialObj.getValue("name"));
			}
			else{
			LCSLog.debug("No  Log Entry exists for"+(String)materialObj.getValue("name"));
	
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This function is using to get Supplier Object based on the given 'Supplier Name' and 'Supplier FlexType Path' (that is Supplier\Supplier or Supplier\Factory) to return from header
	 * @param supplierName - String
	 * @return supplierObj - LCSSupplier
	 * @throws WTException
	 */
	public LCSSupplier findSupplierByNameType(String supplierName) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.findSupplierByNameType(supplierName) ###");
		FlexType supplierFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Supplier\\Supplier");
		FlexType factoryFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Supplier\\Factory");
		
		//Get Supplier Object for the given 'Supplier Name' and 'Supplier FlexType Path', validate the supplier object, based on the validation status get Supplier of type Factory 
		LCSSupplier supplierObj = new LCSSupplierQuery().findSupplierByNameType(supplierName, supplierFlexTypeObj);
		if(!(supplierObj != null && !supplierObj.isPlaceholder()))
		{
			supplierObj = new LCSSupplierQuery().findSupplierByNameType(supplierName, factoryFlexTypeObj);
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.findSupplierByNameType(supplierName) ###");
		return supplierObj;
	}
	
	/**
	 * This function is using to get Material object based on the given input parameter (Material Name, Attribute Code, Color Code, Size Code and Material Type Path) using for data update
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @param materialName - String
	 * @param materialFlexTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 */
	public LCSMaterial findMaterialByMaterialSKUAndMaterialType(Map<String, Object> eskoMaterialDataMapObj, String materialName, String materialFlexTypePath) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.findMaterialByMaterialSKUAndMaterialType(Map<String, Object> eskoMaterialDataMapObj, materialName, materialFlexTypePath)");
		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePath);
		LCSMaterial materialObj = null;
		
		//Get Color Code, Attribute Code and Size Code from the given Map then get corresponding attribute database column which are needed to use as an where clause in the query
		String colorCode = (String)eskoMaterialDataMapObj.get(HBIProperties.hbiColorCodeKey);
		String attributeCode = (String)eskoMaterialDataMapObj.get(HBIProperties.hbiAttrCodeKey);
		String sizeCode = (String)eskoMaterialDataMapObj.get(HBIProperties.hbiSizeCodeKey);
		String colorCodeDBColumn = materialFlexTypeObj.getAttribute(HBIProperties.hbiColorCodeKey).getColumnDescriptorName();
		String attributeCodeDBColumn = materialFlexTypeObj.getAttribute(HBIProperties.hbiAttrCodeKey).getColumnDescriptorName();
		String sizeCodeDBColumn = materialFlexTypeObj.getAttribute(HBIProperties.hbiSizeCodeKey).getColumnDescriptorName();
		String materialNameDBColumn = materialFlexTypeObj.getAttribute("name").getColumnDescriptorName();
		String typeIdPath = materialFlexTypeObj.getTypeIdPath();
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object data and unique id's)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), "1");
	    statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "flexTypeIdPath"), "?", "="), typeIdPath);
    	statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialNameDBColumn), materialName, Criteria.EQUALS));
	    
	    //Validating the given 'Color Code', 'Attribute Code' and 'Size Code' and appending to the Criteria to get the existing Material which is using to update from ESKO-Interface
	    if(FormatHelper.hasContent(colorCode))
	    {
	    	statement.appendAndIfNeeded();
	    	statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));
	    }
	    if(FormatHelper.hasContent(attributeCode))
	    {
	    	statement.appendAndIfNeeded();
	    	statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), attributeCode, Criteria.EQUALS));
	    }
	    if(FormatHelper.hasContent(sizeCode))
	    {
	    	statement.appendAndIfNeeded();
	    	statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), sizeCode, Criteria.EQUALS));
	    }
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	    SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.BRANCHIDITERATIONINFO"));
	  	}
		// LCSLog.debug("### END HBIMaterialDataLoader.findMaterialByMaterialSKUAndMaterialType(Map<String, Object> eskoMaterialDataMapObj, materialName, materialFlexTypePath)");
		return materialObj;
	}
	
	/**
	 * This function is using to validate the 'Buy' flag change set status, based on the status fetch all the suppliers associated with the material to set new LifeCycle Template object
	 * @param materialObj - LCSMaterial
	 * @param eskoMaterialDataMapObj - Map<String, Object>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void validateAndSetLifeCycleTemplateOnMaterialSupplier(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.validateAndSetLifeCycleTemplateOnMaterialSupplier(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj)");
		String buyFlagAttributeKey = HBIProperties.flexTypeAttributeKeyAppender.concat(HBIProperties.hbiBuyOrNotBuyKey);
		String existingMaterialBuyFlag = String.valueOf(materialObj.getValue(HBIProperties.hbiBuyOrNotBuyKey));
		LCSMaterialSupplier materialSupplierObj = null;
		boolean reInitiateWorkflowProcess = false;
		
		//Validate the eskoDataMap (map contains key-value of the ESKO-FlexPLM mapping attributes data) for specific business logic (logic around 'Buy' Flag) validate Buy flag change set
		if(eskoMaterialDataMapObj.keySet().contains(buyFlagAttributeKey))
		{
			String buyFlagStatus = String.valueOf(eskoMaterialDataMapObj.get(buyFlagAttributeKey));
			if("true".equalsIgnoreCase(buyFlagStatus) && !buyFlagStatus.equalsIgnoreCase(existingMaterialBuyFlag))
			{
				reInitiateWorkflowProcess = true;
			}
		}
		
		//Get all 'Material-Supplier' object from the given Material, iterate through the 'Material-Supplier' collection, update each 'Material-Supplier' to set new 'LifeCycle Template'
		SearchResults results = LCSMaterialSupplierQuery.findMaterialSuppliers(materialObj);
		if(reInitiateWorkflowProcess && results != null && results.getResultsFound() > 0)
		{
			Collection<FlexObject> materialSupplierCollection = results.getResults();
			for(FlexObject flexObj : materialSupplierCollection)
			{
				//Validating the 'Material-Supplier' object and invoking an existing API's to change the type from an existing to the new type (new type is provided in data file)
				materialSupplierObj = (LCSMaterialSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:"+flexObj.getString("LCSMaterialSupplier.BRANCHIDITERATIONINFO"));
				if(materialSupplierObj != null && !materialSupplierObj.isPlaceholder())
				{
					validateAndSetLifeCycleTemplateOnMaterialSupplier(materialObj, materialSupplierObj);
				}
			}	
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.validateAndSetLifeCycleTemplateOnMaterialSupplier(LCSMaterial materialObj, Map<String, Object> eskoMaterialDataMapObj)");
	}
	
	/**
	 * This function is using to get the LifeCycleTemplate for the given custom template (HBI Material Supplier Development LC) and re-assign this template to Material-Supplier work flow
	 * @param materialObj - LCSMaterial
	 * @param materialSupplierObj - LCSMaterialSupplier
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private void validateAndSetLifeCycleTemplateOnMaterialSupplier(LCSMaterial materialObj, LCSMaterialSupplier materialSupplierObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialDataLoader.validateAndSetLifeCycleTemplateOnMaterialSupplier(LCSMaterial materialObj, LCSMaterialSupplier materialSupplierObj)");
		LCSSupplier supplierObj = (LCSSupplier) VersionHelper.latestIterationOf(materialSupplierObj.getSupplierMaster());
		String placeholderSupplierName = "Color Version";
		String supplierIdentity = (String) supplierObj.getIdentity();
		String buyerGroupValue = (String) materialObj.getValue(HBIProperties.hbiBuyerGroupKey);
		String materialName = ""+materialObj.getValue("name").toString();
		String placeholderMatSupplierName = String.valueOf(materialName) + " - (TBD)";
		String matSupplierIdentity = materialSupplierObj.getIdentity();
		
		//Initializing WTContainer and LifeCycleTemplateReference object based on the custom LifeCycle template name (for example template name is 'HBI Material Supplier Development LC')
		WTContainerRef hbiWTContainerRef = materialObj.getContainerReference();
		//LifeCycleTemplate lifeCyleTemplateObj = LifeCycleHelper.service.getLifeCycleTemplate("HBI Material Supplier Development LC", hbiWTContainerRef);
		LifeCycleTemplateReference lifeCyleTemplateRefObj = LifeCycleHelper.service.getLifeCycleTemplateReference("HBI Material Supplier Development LC", hbiWTContainerRef);
		
		//Validating the Supplier and Material-Supplier data as per the business rules, based on the validation status invoking out of the box functions to reassign work flow template
		if(FormatHelper.hasContent(supplierIdentity) && !supplierIdentity.equals(placeholderSupplierName) && FormatHelper.hasContent(matSupplierIdentity) && !matSupplierIdentity.equals(placeholderMatSupplierName))
		{
			if(FormatHelper.hasContent(buyerGroupValue) && lifeCyleTemplateRefObj != null)
			{
				LifeCycleHelper.service.reassign((LifeCycleManaged)materialSupplierObj, lifeCyleTemplateRefObj);
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoader.validateAndSetLifeCycleTemplateOnMaterialSupplier(LCSMaterial materialObj, LCSMaterialSupplier materialSupplierObj)");
	}
}
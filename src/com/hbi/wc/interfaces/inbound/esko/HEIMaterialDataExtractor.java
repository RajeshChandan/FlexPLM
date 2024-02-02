package com.hbi.wc.interfaces.inbound.esko;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HEIMaterialDataExtractor.java
 *
 * This class contains generic functions which are using to get web center project data based on the mapped attributes (ESKO-FlexPLM attributes mapping), get approval status from project,
 * get vendor name to load material-supplier data in PLM, validate vendor change status in web center, formatting major category and minor category attributes data, formatting object path 
 * @author Abdul.Patel@Hanes.com
 * @since February-10-2017
 */
public class HEIMaterialDataExtractor
{
	private String packagingSupplierName = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HEIMaterialDataExtractor.packagingSupplierName", "Supplier");
	private String casingSupplierName = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HEIMaterialDataExtractor.casingSupplierName", "CaseSuppliers");
	private String insertTypeAttributeName = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HEIMaterialDataExtractor.insertTypeAttributeName", "InsertType");
	
    /**
     * This function is using to validate the 'Vendor Approval' Status for the given WebCenter project, if Approved invoke internal functions to load Material and Material-Supplier to PLM
     * @param elementObj - Element
     * @param partNumber - String
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return materialSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public String validateAndProcessESKOProjectDataExtractor( String partNumber, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HEIMaterialDataExtractor.validateAndProcessESKOProjectDataExtractor(elementObj, partNumber, eskoMaterialDataMapObj) ###");
    	String materialSyncStatus = "";
    	
    	//Invoking internal function to get project approval status (vendor approved) for the given project, which is then used to sync/integrate material data from ESKO to FlexPLM
    	boolean projectApprovalStatus = getESKOProjectVendorApprovalStatus(eskoMaterialDataMapObj);
    	if(projectApprovalStatus) 
    	{
    		//Calling a function which is using to validate the given 'Part Number' size and split into 'Material Name', 'Color Code', 'Attribute Code' and 'Size Code' attribute value
        	eskoMaterialDataMapObj = new HBIMaterialDataExtractor().validateAndPopulateFlexPLMMaterialSKUAttributeData(partNumber, eskoMaterialDataMapObj);
			if(eskoMaterialDataMapObj!=null){
        	partNumber = (String)eskoMaterialDataMapObj.get(HBIMaterialDataExtractor.eskoPartNumberKey); 
        	
        	//Calling a method from HBIMaterialDataLoader which is used to Process Material(perform create/update event) in FlexPLM based on the given material data map from ESKO
        	materialSyncStatus = new HBIMaterialDataLoader().validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj);
        	
        	//Calling a function which is using validate Material object and Supplier Name from the given Project dataMap and invoke internal function to create Material-Supplier
        	validateAndProcessMaterialSupplierDataLoadRequest(eskoMaterialDataMapObj, partNumber);
			}
    	}
    	
    	// LCSLog.debug("### END HEIMaterialDataExtractor.validateAndProcessESKOProjectDataExtractor(elementObj, partNumber, eskoMaterialDataMapObj) ###");
        return materialSyncStatus;
    }
    
    /**
     * This function is using to get 'Material  FlexType Path' from the given dataMap, initialize Material object, get Supplier Name from the dataMap, validate Material, Supplier to link
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @param partNumber - String
     * @return materialSupplierSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public String validateAndProcessMaterialSupplierDataLoadRequest(Map<String, Object> eskoMaterialDataMapObj, String partNumber) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HEIMaterialDataExtractor.validateAndProcessMaterialSupplierDataLoadRequest(eskoMaterialDataMapObj, partNumber) ###");
    	LCSMaterial placeHolderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
    	String materialSupplierSyncStatus = "";
    	
    	//Get Material Type/Id Path from the given data map, which is using to get LCSMaterial instance(get Material for the given Material Name and Material FlexType Id Path)
    	String materialFlexTypePath = (String) eskoMaterialDataMapObj.get(HBIProperties.materialFlexTypePathKey);
    	if(FormatHelper.hasContent(materialFlexTypePath))
    	{
    		//Calling a function which is using to get Material object from FlexPLM based on the given parameters (Material Name, Type Path, Attribute Code, Color Code and Size Code)
			LCSMaterial materialObj = new HBIMaterialDataLoader().findMaterialByMaterialSKUAndMaterialType(eskoMaterialDataMapObj, partNumber, materialFlexTypePath);
    	
			//Calling a function which is using to get Supplier Name (Supplier Name that exists in PLM) for the given WebCenter Project Data, using this object to link with Material 
			String supplierName = getESKOProjectApprovedVendorName(eskoMaterialDataMapObj);
			
			//Validating Material and Supplier data, calling functions to validate and establish 'Material-Supplier' link in FlexPLM for the given 'Material' and 'Supplier' data
			if(materialObj != null && materialObj != placeHolderMaterialObj && FormatHelper.hasContent(supplierName))
			{
				materialSupplierSyncStatus = new HBIMaterialDataLoader().validateAndProcessSupplierDataLoadRequest(materialObj, supplierName, eskoMaterialDataMapObj);
			}
    	}
    	
    	// LCSLog.debug("### END HEIMaterialDataExtractor.validateAndProcessMaterialSupplierDataLoadRequest(eskoMaterialDataMapObj, partNumber) ###");
    	return materialSupplierSyncStatus;
    }
    
    /**
     * This function is using to validate and return vendor approval status for the given web center project (validating the given data map keys, validate data for specific keys & return)
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return projectApprovalStatus - boolean
     * @throws WTException
     */
    public boolean getESKOProjectVendorApprovalStatus(Map<String, Object> eskoMaterialDataMapObj) throws WTException 
    {
    	// LCSLog.debug("### START HEIMaterialDataExtractor.getESKOProjectVendorApprovalStatus(Map<String, Object> eskoMaterialDataMapObj) ###");
    	boolean projectApprovalStatus = false;
    	String supplierName = "";
    	
    	//Get all keys from ESKO data map, validate the key set to check does it contains 'Packaging' or 'Casing' 'Supplier Name' key in the container using to data validation.
    	Set<String> eskoMaterialDataMapKeys = eskoMaterialDataMapObj.keySet();
    	if(eskoMaterialDataMapKeys.contains(packagingSupplierName))
    	{
    		supplierName = (String) eskoMaterialDataMapObj.get(packagingSupplierName);
    	}
    	else if(eskoMaterialDataMapKeys.contains(casingSupplierName))
    	{
    		supplierName = (String) eskoMaterialDataMapObj.get(casingSupplierName);
    	}
    	
    	//Validating the 'Supplier Name' web center value, based on the validation status re-initializing projectApprovalStatus flag which is using to load the Part to FlexPLM.
    	if(FormatHelper.hasContent(supplierName))
    	{
    		projectApprovalStatus = true;
    	}
    	
    	// LCSLog.debug("### END HEIMaterialDataExtractor.getESKOProjectVendorApprovalStatus(Map<String, Object> eskoMaterialDataMapObj) ###");
    	return projectApprovalStatus;
    }
    
    /**
     * This function is using to get approved vendor name from the given web-center project data, validate the vendor name and get the corresponding FlexPLM vendor/supplier name to return 
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return supplierName - String
     * @throws WTException
     */
    public String getESKOProjectApprovedVendorName(Map<String, Object> eskoMaterialDataMapObj) throws WTException
    {
    	// LCSLog.debug("### START HEIMaterialDataExtractor.getESKOProjectApprovedVendorName(Map<String, Object> eskoMaterialDataMapObj) ###");
    	String supplierName = "";
    	
    	//Get all keys from ESKO data map, validate the key set to check does it contains 'Packaging' or 'Casing' 'Supplier Name' key in the container using to data validation.
    	Set<String> eskoMaterialDataMapKeys = eskoMaterialDataMapObj.keySet();
    	if(eskoMaterialDataMapKeys.contains(packagingSupplierName))
    	{
    		supplierName = (String) eskoMaterialDataMapObj.get(packagingSupplierName);
    	}
    	else if(eskoMaterialDataMapKeys.contains(casingSupplierName))
    	{
    		supplierName = (String) eskoMaterialDataMapObj.get(casingSupplierName);
    	}
    	
    	//Validating the 'Supplier Name' web center value, based on the validation status get mapped vendor/supplier name for FlexPLM, which is using to associate/link with Part.
    	if(FormatHelper.hasContent(supplierName))
    	{
    		supplierName = new HBIMaterialDataExtractor().getMappingFlexPLMSupplierName(supplierName);
    	}
    	
    	// LCSLog.debug("### END HEIMaterialDataExtractor.getESKOProjectApprovedVendorName(Map<String, Object> eskoMaterialDataMapObj) ###");
    	return supplierName;
    }
    
    /**
     * This function is using to get the 'Project Type' (for example Project of type 'Insert Boards') for the given ESKO Project data map and returning 'Project Type' from function header
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return eskoProjectType - String
     * @throws WTException
     */
    public String getESKOProjectType(Map<String, Object> eskoMaterialDataMapObj) throws WTException
    {
    	// LCSLog.debug("### START HEIMaterialDataExtractor.getESKOProjectType(Map<String, Object> eskoMaterialDataMapObj) ###");
    	String eskoProjectType = "";
    	
    	//Get all keys from ESKO data map, validate the key set to check does it contains 'Insert Type' attribute and its value in the container which is using to derive the type.
    	Set<String> eskoMaterialDataMapKeys = eskoMaterialDataMapObj.keySet();
    	if(eskoMaterialDataMapKeys.contains(insertTypeAttributeName))
    	{
    		eskoProjectType = "Insert_Boards";
    	}
    	
    	// LCSLog.debug("### END HEIMaterialDataExtractor.getESKOProjectType(Map<String, Object> eskoMaterialDataMapObj) ###");
    	return eskoProjectType;
    }
    
    /**
     * This function is specific to Insert Boards' project, which is using to validate the given dataMap for 'Sourced Production Only?' flag status, update flag as per the business process
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return eskoMaterialDataMapObj - Map<String, Object>
     * @throws WTException
     */
    public Map<String, Object> updateMaterialDataMapForInsertBoards(Map<String, Object> eskoMaterialDataMapObj) throws WTException
    {
    	// LCSLog.debug("### START HEIMaterialDataExtractor.updateMaterialDataMapForInsertBoards(Map<String, Object> eskoMaterialDataMapObj) ###");
    	String sourcedToProductionAttKey = HBIProperties.flexTypeAttributeKeyAppender.concat(HBIProperties.hbiBuyOrNotBuyKey);
    	String sourcedToProductionValue = "";
    	
    	//Get all keys from ESKO data map, validate the key set to check does it contains 'Sourced Production Only?' attribute key in the container using for data validation.
    	Set<String> eskoMaterialDataMapKeys = eskoMaterialDataMapObj.keySet();
    	if(eskoMaterialDataMapKeys.contains(sourcedToProductionAttKey))
    	{
    		sourcedToProductionValue = (String) eskoMaterialDataMapObj.get(sourcedToProductionAttKey);
    	}
    	
    	//Validating 'Sourced Production Only?' status, based on the validation updating the given dataMap to re-populate 'Sourced Production Only?' flag to 'true'.
    	if(!FormatHelper.hasContent(sourcedToProductionValue))
    	{
    		sourcedToProductionValue = "true";
    		eskoMaterialDataMapObj.put(sourcedToProductionAttKey, sourcedToProductionValue);
    	}
    	
    	// LCSLog.debug("### END HEIMaterialDataExtractor.updateMaterialDataMapForInsertBoards(Map<String, Object> eskoMaterialDataMapObj) ###");
    	return eskoMaterialDataMapObj;
    }
}
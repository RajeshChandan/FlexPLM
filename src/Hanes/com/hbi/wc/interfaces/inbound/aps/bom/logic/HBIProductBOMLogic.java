package com.hbi.wc.interfaces.inbound.aps.bom.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIAPSProductBOMQuery;
import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIFlexBOMLinkQuery;
import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIFlexBOMPartQuery;
import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIMasterMaterialQuery;
import com.lcs.wc.flexbom.FlexBOMHelper;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.FlexBOMPartMaster;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.supplier.LCSSupplierQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialBOMLogic.java
 *
 * This class contains specific and generic functions which are using to create FlexBOMPart (based on the given parameters like Owner reference, in this case we are using Product as owner
 * as we are creating Product BOM) and FlexBOMLink (creating for the given Owner reference, BOM Section Name, referencing Material object and other attributes using to set default values)
 * @author Abdul.Patel@Hanes.com
 * @since November-16-2017
 */
public class HBIProductBOMLogic
{
	private static String bomSourceTypeKey = LCSProperties.get("com.hbi.wc.flexbom.HBIMaterialBOMLogic.bomSourceTypeKey", "hbiBOMSourceType");
	private static String bomSectionKey = LCSProperties.get("com.hbi.wc.flexbom.HBIMaterialBOMLogic.bomSectionKey", "section");
	private static String garmentCutSectionName = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIProductBOMLogic.garmentCutSectionName", "garmentCut");
	private static String garmentSewSectionName = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIProductBOMLogic.garmentSewSectionName", "garmentSew");
	private static String initialLoadBOMTypePath = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIProductBOMLogic.initialLoadBOMTypePath", "BOM\\Materials\\HBI\\Initial Load");
	private static String billOfMaterialNameAppender = " - Initial Load";
	
	/**
	 * This function is using to validate and create FlexBOMPart, validate the given resultSet and invoke internal function using in master material, garmentCut and garmentSew bom sections
	 * @param productObj - LCSProduct
	 * @param resultSetObj - ResultSet
	 * @param billOfMaterialName - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public void validateAndSyncProductBOMData(LCSProduct productObj, ResultSet resultSetObj, String billOfMaterialName) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIProductBOMLogic.validateAndSyncProductBOMData(LCSProduct productObj, ResultSet resultSetObj, String billOfMaterialName) ###");
		String flexBOMPartName = billOfMaterialName.concat(billOfMaterialNameAppender);
		FlexBOMPart flexBOMPartObj = new HBIFlexBOMPartQuery().getFlexBOMPartForOwner(productObj, flexBOMPartName);
		String flexBOMPartEvent = "";
		
		//Validate the given ResultSet instance, based on the resultSet validation invoking internal function to create FlexBOMPart for the given data(Product object, BillOfMaterial Name) 
		if(flexBOMPartObj != null && resultSetObj != null)
		{
			//update event
		}
		else if(resultSetObj != null)
		{
			flexBOMPartObj = createFlexBOMPartForOwner(flexBOMPartName, initialLoadBOMTypePath, productObj);
			flexBOMPartEvent = "CREATE";
		}
		else
		{
			//add error handing logic here...
		}
		
		//Calling a function which is using to validate FlexBOMPart, prepare garmentCutBOM and garmentSewBOM components set, invoke internal functions to add components to cut and sew bom
		validateAndSyncProductBOMData(flexBOMPartObj, resultSetObj, billOfMaterialName);
		
		//Validate FlexBOMPart object and FlexBOMPart event type (CREATE/UPDATE), based on the validation invoke internal function using to add FlexBOMPart as a component to Specification
		if(flexBOMPartObj != null && "CREATE".equals(flexBOMPartEvent))
		{
			//TO DO: here we can call a function which will add FlexBOMPart as a component to the product primary specification (using business logic to derive Product Specification)
		}
		
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndSyncProductBOMData(LCSProduct productObj, ResultSet resultSetObj, String billOfMaterialName) ###");
	}
	
	/**
	 * This function is using to validate FlexBOMPart, prepare garmentCutBOM and garmentSewBOM components set, invoke internal functions to add components into a garmentCut and garmentSew
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param resultSetObj - ResultSet
	 * @param billOfMaterialName - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public void validateAndSyncProductBOMData(FlexBOMPart flexBOMPartObj, ResultSet resultSetObj, String billOfMaterialName) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIProductBOMLogic.validateAndSyncProductBOMData(FlexBOMPart flexBOMPartObj, ResultSet resultSetObj, String billOfMaterialName) ###");
		
		//Validate FlexBOMPart, iterate through each rows of the resultSet to get materialName, colorCode, attributeCode, sizeCode and activityCode, using to create/update FlexBOMLinks
		if(flexBOMPartObj != null)
		{
			//
			Map<String, Set<String>> masterMaterialData = validateAndReturnMaterialSearchCriteria(resultSetObj);
			Set<String> garmentCutMaterialSet = masterMaterialData.get("garmentCutMaterialSet");
			Set<String> garmentSewMaterialSet = masterMaterialData.get("garmentSewMaterialSet");
					
			//
			int sortingNumber = 0;
			garmentCutMaterialSet = new HBIAPSProductBOMQuery().getGarmentCutBOMMaterialSet(garmentCutMaterialSet, billOfMaterialName);
				
			sortingNumber = validateAndSyncFlexBOMLinkData(flexBOMPartObj, garmentCutMaterialSet, garmentCutSectionName, sortingNumber);
			sortingNumber = validateAndSyncFlexBOMLinkData(flexBOMPartObj, garmentSewMaterialSet, garmentSewSectionName, sortingNumber);
		}
		
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndSyncProductBOMData(FlexBOMPart flexBOMPartObj, ResultSet resultSetObj, String billOfMaterialName) ###");
	}
	
	/**
	 * This function is using to iterate through the resultSet data, validate activity code to differentiate Cut BOM and Sew BOM, based on the BOM type adding components to a map to return
	 * @param resultSetObj - ResultSet
	 * @return masterMaterialData - Map<String, Set<String>>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public Map<String, Set<String>> validateAndReturnMaterialSearchCriteria(ResultSet resultSetObj) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndReturnMaterialSearchCriteria(ResultSet resultSetObj) ###");
		Map<String, Set<String>> masterMaterialData = new HashMap<String, Set<String>>();
		Set<String> garmentCutMaterialSet = new HashSet<String>();
		Set<String> garmentSewMaterialSet = new HashSet<String>();
		String materialName = "";
		String sizeCode = "";
		String activityCode = "";
		String uniqueMasterMaterialData = "";
		
		while(resultSetObj.next())
		{
			//This block of code is specific to KITC (KIT CUT) BOM Line Items from APS BILL_OF_MTRLS table
			activityCode = resultSetObj.getString("ACTIVITY_CD");
			if("KITC".equals(activityCode))
			{
				materialName =  resultSetObj.getString("COMP_STYLE_CD");
				garmentCutMaterialSet.add(materialName);
			}
			//This block of code is to handle Sew BOM and Make BOM Line Items from APS BILL_OF_MTRLS table
			else
			{
				materialName =  resultSetObj.getString("COMP_STYLE_CD");
				sizeCode =  resultSetObj.getString("COMP_SIZE_CD");
				uniqueMasterMaterialData = materialName.concat("_").concat(sizeCode);
				garmentSewMaterialSet.add(uniqueMasterMaterialData);
			}
		}
		
		masterMaterialData.put("garmentCutMaterialSet", garmentCutMaterialSet);
		masterMaterialData.put("garmentSewMaterialSet", garmentSewMaterialSet);
		
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndReturnMaterialSearchCriteria(ResultSet resultSetObj) ###");
		return masterMaterialData;
	}
	
	/**
	 * This function is using to iterate through the given components, validate the component status in PLM, based on the validation status invoke internal functions to create FlexBOMPart
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param flexBOMLinkComponentsSet - Set<String>
	 * @param bomSectionName - String
	 * @param sortingNumber - int
	 * @return  sortingNumber - int
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public int validateAndSyncFlexBOMLinkData(FlexBOMPart flexBOMPartObj, Set<String> flexBOMLinkComponentsSet, String bomSectionName, int sortingNumber) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductBOMLogic.validateAndSyncFlexBOMLinkData(FlexBOMPart flexBOMPartObj, Set<String> flexBOMLinkComponentsSet, String bomSectionName) ###");
		String materialName = "";
		String colorCode = "000";
		String attributeCode = "------";
		String sizeCode = "";
		String materialData[] = {};
		
		//
		for(String materialCode : flexBOMLinkComponentsSet)
		{
			materialData = materialCode.split("_");
			materialName = materialData[0];
			sizeCode = materialData[1];
			sortingNumber = sortingNumber + 1;
			validateAndSyncProductBOMData(flexBOMPartObj, materialName, colorCode, attributeCode, sizeCode, bomSectionName, sortingNumber);
		}
		
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndSyncFlexBOMLinkData(FlexBOMPart flexBOMPartObj, Set<String> flexBOMLinkComponentsSet, String bomSectionName) ###");
		return sortingNumber;
	}
	
	/**
	 * This function is using to validate bill of material component data within the given FlexBOMPart, based on the validation status invoke internal functions to create/update FlexLinks
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param materialName - String
	 * @param colorCode - String
	 * @param attributeCode - String
	 * @param sizeCode - String
	 * @param bomSectionName - String
	 * @param sortingNumber - int
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateAndSyncProductBOMData(FlexBOMPart flexBOMPartObj, String materialName, String colorCode, String attributeCode, String sizeCode, String bomSectionName, int sortingNumber) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndReturnMaterialSearchCriteria(flexBOMPartObj, materialName, colorCode, attrCode, sizeCode, bomSection, sortingNumber) ###");
		
		//
		LCSMaterial materialObj = new HBIMasterMaterialQuery().getMaterialObjectForCriteria(materialName, colorCode, attributeCode, sizeCode);
		if(materialObj == null)
		{
			materialObj = new HBIMasterMaterialLogic().createMasterMaterial(materialName, colorCode, attributeCode, sizeCode);
		}
		
		//
		FlexBOMLink flexBOMLinkObj = new HBIFlexBOMLinkQuery().getFlexBOMLinkForOwner(flexBOMPartObj, materialObj);
		if(flexBOMLinkObj == null)
		{
			flexBOMLinkObj = createFlexBOMLinkForOwner(flexBOMPartObj, materialObj, bomSectionName, sortingNumber);
		}
		
		// LCSLog.debug("### END HBIProductBOMLogic.validateAndReturnMaterialSearchCriteria(flexBOMPartObj, materialName, colorCode, attrCode, sizeCode, bomSection, sortingNumber) ###");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////		Following functions are using to create FlexBOMPart and FlexBOMLink data	///////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This function is using to create FlexBOMPart for the given BOMPart Name, BOMPart Type and BOMPart Owner (Material Object, as we are creating Product BOM) and set all default values
	 * @param flexBOMPartName - String
	 * @param flexBOMPartTypePath - String
	 * @param productObj - LCSProduct
	 * @return flexBOMPartObj - FlexBOMPart
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public FlexBOMPart createFlexBOMPartForOwner(String flexBOMPartName, String flexBOMPartTypePath, LCSProduct productObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductBOMLogic.createFlexBOMPartForOwner(String flexBOMPartName, LCSProduct productObj) ###");
		String bomTypeID = FormatHelper.getObjectId(FlexTypeCache.getFlexTypeFromPath(flexBOMPartTypePath));
		
		//Initializing FlexBOMPart using Client Model(which internally calls OOTB functions) with the existing Product object (Creating Product BOM) and BOM FlexType Path (Full Path)
		FlexBOMPartClientModel bomPartClientModelObj = new FlexBOMPartClientModel();
		bomPartClientModelObj.initiateBOMPart(FormatHelper.getObjectId(productObj), bomTypeID, "MAIN", null, false);
		FlexBOMPart flexBOMPartObj = bomPartClientModelObj.getBusinessObject(); 
		
		//Updating the attribute values on BOMPart object(Like BOMPart Name, BOM Type and other attributes which are having default values in configuration) Persisting FlexBOMPart object
		flexBOMPartObj.setValue("name", flexBOMPartName);
		flexBOMPartObj.setValue(bomSourceTypeKey, "createdFromLoader");
		flexBOMPartObj.setValue("subassemblyInsertionMode", "ALWAYS_LINK");
		FlexBOMHelper.service.saveBOMPart(flexBOMPartObj);
		
		// LCSLog.debug("### END HBIProductBOMLogic.createFlexBOMPartForOwner(String flexBOMPartName, LCSProduct productObj) ###");
		return flexBOMPartObj;
	}
	
	/**
	 * This function is using to create FlexBOMLink for the given FlexBOMPart (Owner object, using to set owner references), BOM Section Name, FlexBOMLink Sorting Number and Material data
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param materialObj - LCSMaterial
	 * @param bomSectionName - String
	 * @param sortingNumber - int
	 * @return flexBOMLinkObj - FlexBOMLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public FlexBOMLink createFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj, String bomSectionName, int sortingNumber) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductBOMLogic.createFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj, String bomSectionName, int sortingNumber) ###");
		FlexBOMLink flexBOMLinkObj = FlexBOMLink.newFlexBOMLink();
		flexBOMLinkObj.setSortingNumber(sortingNumber);
		flexBOMLinkObj.setBranchId(sortingNumber);
		
		//Updating Sequence, InDate and OutDate on FlexBOMLink(which is using as an additional parameter to identify the type of FlexBOMLink event as per the OOTB FlexBOMLink data)
		flexBOMLinkObj.setInDate(new java.sql.Timestamp(new Date().getTime()));
		flexBOMLinkObj.setOutDate(null);
		flexBOMLinkObj.setSequence(0);
		
		//Updating FlexBOMLink Type (FlexTypePath of the Link which is derived from FlexBOMPart), Object Status(WIP) and Drop Status of the FlexBOMLink as per the OOTB FlexBOMLink data
		flexBOMLinkObj.setFlexType(flexBOMPartObj.getFlexType());
		flexBOMLinkObj.setWip(false);
		flexBOMLinkObj.setDropped(false);
		
				

		//Updating Parent and Child Relationship details(Like Material, Supplier information of FlexBOMLink, Owner of the FlexBOMLink that is FlexBOMPart references) and Persisting Link
		//flexBOMLinkObj.setParent((WTPartMaster) flexBOMPartObj.getMaster());
		flexBOMLinkObj.setParent((FlexBOMPartMaster) flexBOMPartObj.getMaster());
		flexBOMLinkObj.setParentRev(flexBOMPartObj.getVersionIdentifier().getValue());
		flexBOMLinkObj.setParentReference(flexBOMPartObj.getMasterReference());
		//flexBOMLinkObj.setChild((WTPartMaster) materialObj.getMaster());
		flexBOMLinkObj.setChild((LCSMaterialMaster) materialObj.getMaster());
		flexBOMLinkObj.setSupplier(LCSSupplierQuery.PLACEHOLDER);
		flexBOMLinkObj.setValue(bomSectionKey, bomSectionName);
		flexBOMLinkObj.calculateDimensionId();
		LCSFlexBOMLogic.persist(flexBOMLinkObj);
		
		// LCSLog.debug("### END HBIProductBOMLogic.createFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj, String bomSectionName, int sortingNumber) ###");
		return flexBOMLinkObj;
	}
}
package com.hbi.wc.flexbom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIConditionedWidthDataUtil.java
 *
 * This class contains generic functions which are using to validate FlexBOMPart and FlexBOMLink(type path validation), associated material data validation(type path validation), material
 * data validation(not a placeholder material), fetching 'Finish Dimensions' MOA attribute data (conditioned width(in)), formating 'Conditioned Width(in)' to display in value in edit/view 
 * @author Abdul.Patel@Hanes.com
 * @since January-9-2017
 */
public class HBIConditionedWidthDataUtil
{
	public static String bomFlexTypePath = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.bomFlexTypePath", "BOM\\Materials\\HBI\\Garment Cut,BOM\\Materials\\HBI\\Garment Sourced");
	public static String materialFlexTypePath = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.materialFlexTypePath", "Material\\Fabric\\Fabric Buy,Material\\Fabric\\Finished");
	public static String finishDimensionsMOAAttKey = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.finishDimensionsMOAAttKey", "hbiFinishAttributeMOA");
	public static String bomConditionedWidthKey = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.bomConditionedWidthKey", "hbiConditionedWidth");
	public static String bomConditionedWidthDisplay = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.bomConditionedWidthDisplay", "hbiConditionedWidthDisplay");
	public static String bomCondWidthKey = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.bomCondWidthKey", "hbifabCondWidth");
	public static String bomCondWidthDisplay = LCSProperties.get("com.hbi.wc.flexbom.HBIConditionedWidthDataUtil.bomCondWidthKey", "hbifabCondWidthDisplay");
	
	/**
	 * This function is calling from EditBOMAlt.jsp to validate the given FlexBOMLink(is Garment Cut or Garment Sourced Type),validate the associated Material & cascade 'Conditioned Width' 
	 * @param dataRow - Map<String, String>
	 * @return conditionedWidth - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static String validateAndReturnConditionedWidth(Map<String, String> dataRow) throws WTException, WTPropertyVetoException
	{
		LCSLog.debug("### START HBIConditionedWidthDataUtil.validateAndReturnConditionedWidth(Map<String, String> dataRow) ###");
		//WTPartMaster placeholderMaterialMasterObj = LCSMaterialQuery.PLACEHOLDER;
		LCSMaterialMaster placeholderMaterialMasterObj = LCSMaterialQuery.PLACEHOLDER;
		LCSMaterial placeholderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(placeholderMaterialMasterObj);
		String placeholderMaterialMasterId = FormatHelper.getNumericObjectIdFromObject(placeholderMaterialMasterObj);
		String conditionedWidth = "";
		String bomTypePath = "";
		LCSMaterial materialObj = null;
		
		//Get FlexBOMLink IA2A2 from the given dataRow, Initialize FlexBOMLink object which is using to get the FlexTypePath because we need this functionality for specific BOM types
		String flexBOMLinkIDA2A2 = (String)dataRow.get("linkId");
		FlexBOMLink flexBOMLinkObj = (FlexBOMLink) LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:"+flexBOMLinkIDA2A2);
		bomTypePath = flexBOMLinkObj.getFlexType().getFullName(true);
		
		//Get Master Material ID2A2 from the given dataRow, Initialize WTPartMaster object which is the owner of Material, down cast PartMaster to Material, using to get Cond Width (in)
		String materialMasterIDA2A2 = (String)dataRow.get("materialMasterId");
		if(FormatHelper.hasContent(materialMasterIDA2A2) && !materialMasterIDA2A2.equals(placeholderMaterialMasterId))
		{
			//WTPartMaster masterMaterialObj = (WTPartMaster) LCSQuery.findObjectById("OR:wt.part.WTPartMaster:"+materialMasterIDA2A2);
			LCSMaterialMaster masterMaterialObj = (LCSMaterialMaster) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialMaster:"+materialMasterIDA2A2);
			
			materialObj = (LCSMaterial) VersionHelper.latestIterationOf(masterMaterialObj);
		}
		else
		{
			materialObj = new HBIConditionedWidthDataUtil().getVariationLinkMaterialObject(flexBOMLinkObj);
		}
		
		//Get a list of BOM FlexTypePath(which are pre-configured data as per the requirement), compare the given BOM FlexTypePath with the pre-configured data and invoke other function 
		List<String> bomFlexTypePathList = new HBIConditionedWidthDataUtil().getFlexTypePathFromPropertiesFile(bomFlexTypePath);
		if(materialObj != null && materialObj != placeholderMaterialObj && FormatHelper.hasContent(bomTypePath) && bomFlexTypePathList.contains(bomTypePath))
		{
			//Calling a function which is using to validate the given Material (Fabric Buy or Fabric Finished), get 'Conditioned Width(in)' attribute data from 'Finish Dimensions' MOA
			conditionedWidth = new HBIConditionedWidthDataUtil().validateAndReturnFinishDimensionsWidth(materialObj);
		}
		
		LCSLog.debug("### END HBIConditionedWidthDataUtil.validateAndReturnConditionedWidth(Map<String, String> dataRow) ###");
		System.out.println(" <<<<<<< conditionedWidth conditionedWidth conditionedWidth >>>>>>>>>  " +conditionedWidth);
		return conditionedWidth;
	}
	
	/**
	 * This function is using to validate the given Material object (validate the Material Type Path), get 'Finish Dimensions' MOA from Material, Iterate through MOA line item to get width
	 * @param materialObj - LCSMaterial
	 * @return conditionedWidth - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private String validateAndReturnFinishDimensionsWidth(LCSMaterial materialObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.validateAndReturnFinishDimensionsWidth(LCSMaterial materialObj) ###");
		LCSMOATable finishDimensionsMOAObj = null;
		String conditionedWidth = "";
		double dblConditionedWidth = 0.0;
		int rowCount = 0;
		
		//Get a list of Material FlexTypePath(which are pre-configured data as per the requirement), compare the given Material FlexTypePath with the pre-configured data, get MOA
		String materialTypePath = materialObj.getFlexType().getFullName(true);
		List<String> materialFlexTypePathList = getFlexTypePathFromPropertiesFile(materialFlexTypePath);
		if(FormatHelper.hasContent(materialTypePath) && materialFlexTypePathList.contains(materialTypePath))
		{
			finishDimensionsMOAObj = (LCSMOATable) materialObj.getValue(finishDimensionsMOAAttKey);
		}
		
		//Validate the 'Finish Dimensions' MOA (initialized from Material), initialize MOA rows, iterate through each row to fetch 'Conditioned Width(in)', sort and return Cond Width(in)
		if(finishDimensionsMOAObj != null && finishDimensionsMOAObj.getRows() != null && finishDimensionsMOAObj.getRows().size() > 0)
		{
			double tempConditionedWidth[] =  new double[finishDimensionsMOAObj.getRows().size()];
			Collection<FlexObject> finishDimensionsMOACollection = finishDimensionsMOAObj.getRows();
			for(FlexObject flexObj : finishDimensionsMOACollection)
			{
				dblConditionedWidth = flexObj.getDouble("HBIFABCONDWIDTH");
				tempConditionedWidth[rowCount] = dblConditionedWidth;
				rowCount = rowCount + 1; 
			}
			Arrays.sort(tempConditionedWidth);
			conditionedWidth = (Arrays.toString(tempConditionedWidth)).replace("[", "").replace("]", "");
		}
		
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.validateAndReturnFinishDimensionsWidth(LCSMaterial materialObj) ###");
		return conditionedWidth;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////	Below defined functions are using in ViewBOM.jsp (customizing the data-set for dynamic data population from Material MOA	//////////////////////////// 
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function is calling from ViewBOM.jsp to validate the given FlexBOMPart(is Garment Cut or Garment Sourced Type),validate the associated Material and cascade 'Conditioned Width'
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param bomDataCollection - Collection<FlexObject>
	 * @return bomData - Collection<FlexObject>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection<FlexObject> validateAndUpdateBOMDataCollection(FlexBOMPart flexBOMPartObj, Collection<FlexObject> bomData) throws WTException, WTPropertyVetoException
	{
		LCSLog.debug("### START HBIConditionedWidthDataUtil.validateAndUpdateBOMDataCollection(FlexBOMPart flexBOMPartObj, Collection<FlexObject> bomData) ###");
		LCSMaterial placeholderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
		String placeholderMaterialBranchId = FormatHelper.getNumericVersionIdFromObject(placeholderMaterialObj);
		String materialBranchId = "";
		LCSMaterial materialObj = null;
		String conditionedWidth = "";
		
		//Get a list of BOM FlexTypePath(which are pre-configured data as per the requirement), compare the given BOM FlexTypePath with the pre-configured data to update bomData set
		String bomTypePath = flexBOMPartObj.getFlexType().getFullName(true);
		List<String> bomFlexTypePathList = new HBIConditionedWidthDataUtil().getFlexTypePathFromPropertiesFile(bomFlexTypePath);
		if(FormatHelper.hasContent(bomTypePath) && bomFlexTypePathList.contains(bomTypePath))
		{
			Collection<FlexObject> bomDataCollection = new ArrayList<FlexObject>();
			for(FlexObject flexObj : bomData)
			{
				//Get Material BranchId from the given bomData, validate the associated material with the Placeholder material, initialize material object to get MOA 'Conditioned Width' 
				materialBranchId = flexObj.getString("MATERIALBRANCHID");
				if(FormatHelper.hasContent(materialBranchId) && !materialBranchId.equals(placeholderMaterialBranchId))
				{
					materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+materialBranchId);
					materialObj = (LCSMaterial) VersionHelper.latestIterationOf(materialObj);
					
					conditionedWidth = new HBIConditionedWidthDataUtil().validateAndReturnFinishDimensionsWidth(materialObj);
					flexObj.put("HBICONDITIONEDWIDTH", conditionedWidth);
					bomDataCollection.add(flexObj);
				}
			}
			bomData = bomDataCollection;
		}
		
		LCSLog.debug("### END HBIConditionedWidthDataUtil.validateAndUpdateBOMDataCollection(FlexBOMPart flexBOMPartObj, Collection<FlexObject> bomData) ###");
		return bomData;
	}
	
	/**
	 * This function is calling from ViewBOM.jsp to validate the given FlexBOMPart(is Garment Cut or Garment Sourced Type),cascade user selected Conditioned Width data to the ViewBOM Page 
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param bomDataCollection - Collection<FlexObject>
	 * @return bomData - Collection<FlexObject>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection<FlexObject> validateAndUpdateViewBOMDataCollection(FlexBOMPart flexBOMPartObj, Collection<FlexObject> bomData) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.validateAndUpdateViewBOMDataCollection(FlexBOMPart flexBOMPartObj, Collection<FlexObject> bomData) ###");
		double conditionedWidth = 0.0;
		
		//Get a list of BOM FlexTypePath(which are pre-configured data as per the requirement), compare the given BOM FlexTypePath with the pre-configured data to update bomData set
		String bomTypePath = flexBOMPartObj.getFlexType().getFullName(true);
		List<String> bomFlexTypePathList = new HBIConditionedWidthDataUtil().getFlexTypePathFromPropertiesFile(bomFlexTypePath);
		if(FormatHelper.hasContent(bomTypePath) && bomFlexTypePathList.contains(bomTypePath))
		{
			Collection<FlexObject> bomDataCollection = new ArrayList<FlexObject>();
			for(FlexObject flexObj : bomData)
			{
				//Get user selected 'Conditioned Width' value from drop down/single list, cascade/populate the user selected 'Conditioned Width' to the Text Area attribute in ViewBOM  
				conditionedWidth = flexObj.getDouble("HBIFABCONDWIDTH");
				flexObj.put("HBICONDITIONEDWIDTH", conditionedWidth);
				bomDataCollection.add(flexObj);
			}
			bomData = bomDataCollection;
		}
		
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.validateAndUpdateViewBOMDataCollection(FlexBOMPart flexBOMPartObj, Collection<FlexObject> bomData) ###");
		return bomData;
	}
	
	/**
	 * This function is using to get the Object Type Path List<String> from the given properties entry (using property entry as key) and returning a List<String> contains configured Paths 
	 * @param propertyEntryValue - String
	 * @return attributesList - List<String>
	 * @throws WTException
	 */
	public List<String> getFlexTypePathFromPropertiesFile(String propertyEntryValue) throws WTException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.getFlexTypePathFromPropertiesFile(String propertyEntryValue) ###");
		List<String> flexTypePathList = new ArrayList<String>();
		
		//Validating the given 'Property Entry key' and converting the string into StringTokenizer using comma (,) as delimiter, using StringTokenizer object to iterate and get keys
		if (FormatHelper.hasContent(propertyEntryValue))
		{
			StringTokenizer strTokenFlexTypePathObj = new StringTokenizer(propertyEntryValue, ",");
			
			//Iterating through StringTokenizer object to get each FlexTypePath and adding new each flexTypePath to the List<String>, which is using to return from the function header
			while (strTokenFlexTypePathObj.hasMoreTokens())
			{
				String flexTypePath = strTokenFlexTypePathObj.nextToken().trim();
				flexTypePathList.add(flexTypePath);
			}
		}
		
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.getFlexTypePathFromPropertiesFile(String propertyEntryValue) ###");
		return flexTypePathList;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////	Below defined functions are calling from SSP entries (registered SSP on FlexBOMLink PRE_PERSIST Event to flip Cond Width(in)	//////////////////////// 
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function is using as a plug-in function which is registered on FlexBOMLink PRE_PERSIST EVENT to validate the 'Cond Width(in)' data with the given collection and set the default
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndFlipConditionedWidth(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		LCSLog.debug("### START HBIConditionedWidthDataUtil.validateAndFlipConditionedWidth(WTObject wtObj) ###");
		LCSMaterial materialObj = null;
		double condWidthDblValue = 0.0;
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type FlexBOMLink Object
		if(!(wtObj instanceof FlexBOMLink))
		{
			LCSLog.debug("Returning without performing any action as the incoming object is not an instance of FlexBOMLink, this plug-in is on FlexBOMLink object");
			return;
		}
		
		//Get a list of BOM FlexTypePath(which are pre-configured data as per the requirement), compare the given BOM FlexTypePath with the pre-configured data to update FlexBOMLink
		FlexBOMLink flexBOMLinkObj = (FlexBOMLink) wtObj;
		String bomTypePath = flexBOMLinkObj.getFlexType().getFullName(true);
		List<String> bomFlexTypePathList = new HBIConditionedWidthDataUtil().getFlexTypePathFromPropertiesFile(bomFlexTypePath);
		if(FormatHelper.hasContent(bomTypePath) && bomFlexTypePathList.contains(bomTypePath))
		{
			materialObj = (LCSMaterial) VersionHelper.latestIterationOf(flexBOMLinkObj.getChild());
			condWidthDblValue = (Double) flexBOMLinkObj.getValue(bomCondWidthKey);
		}
		
		//Initializing 'Placeholder Material', validating the associated/linked material to check the for Placeholder status and calling internal function to flip 'Cond Width (in) value
		LCSMaterial placeholderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
		if(condWidthDblValue != 0.0 && materialObj != null && !PersistenceHelper.isEquivalent(materialObj, placeholderMaterialObj))
		{
			//Calling a function to get 'Conditioned Width (in)' flip status, based on the status updating the given FlexBOMLink to chnage 'Cond Width (in)' value to default value (0.0)
			boolean flipCondWidth = new HBIConditionedWidthDataUtil().getConditionedWidthFlipStatus(flexBOMLinkObj, materialObj, condWidthDblValue);
			if(flipCondWidth)
				flexBOMLinkObj.setValue(bomCondWidthKey, 0.0);
		}
		
		LCSLog.debug("### END HBIConditionedWidthDataUtil.validateAndFlipConditionedWidth(WTObject wtObj) ###");
	}
	
	/**
	 * This function is using to validate the BOM 'Cond Width (in)' by comparing Material MOA(Finished Dimensions) 'Cond Width (in)' and return the validation status using to flip Width
	 * @param flexBOMLinkObj - FlexBOMLink
	 * @param materialObj - LCSMaterial
	 * @param condWidthDblValue - double
	 * @return true/false - boolean
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private boolean getConditionedWidthFlipStatus(FlexBOMLink flexBOMLinkObj, LCSMaterial materialObj, double condWidthDblValue) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.getConditionedWidthFlipStatus(FlexBOMLink flexBOMLinkObj, LCSMaterial materialObj) ###");
		LCSMOATable finishDimensionsMOAObj = null;
		double dblConditionedWidth = 0.0;
		
		//Get a list of Material FlexTypePath(which are pre-configured data as per the requirement), compare the given Material FlexTypePath with the pre-configured data to get MOA
		String materialTypePath = materialObj.getFlexType().getFullName(true);
		List<String> materialFlexTypePathList = getFlexTypePathFromPropertiesFile(materialFlexTypePath);
		if(FormatHelper.hasContent(materialTypePath) && materialFlexTypePathList.contains(materialTypePath))
		{
			finishDimensionsMOAObj = (LCSMOATable) materialObj.getValue(finishDimensionsMOAAttKey);
		}
				
		//Validate the 'Finish Dimensions' MOA (initialized from Material), initialize MOA rows, iterate through each row to fetch 'Conditioned Width(in)', sort and return Cond Width(in)
		if(finishDimensionsMOAObj != null && finishDimensionsMOAObj.getRows() != null && finishDimensionsMOAObj.getRows().size() > 0)
		{
			Collection<FlexObject> finishDimensionsMOACollection = finishDimensionsMOAObj.getRows();
			for(FlexObject flexObj : finishDimensionsMOACollection)
			{
				//validate FlexBOMLink 'Cond Width (in)' with Material MOA (Finished Dimensions) 'Cond Width (in)', return false if the values are matching to skip the looping 
				dblConditionedWidth = flexObj.getDouble("HBIFABCONDWIDTH");
				if(condWidthDblValue == dblConditionedWidth)
					return false;
			}
		}
		
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.getConditionedWidthFlipStatus(FlexBOMLink flexBOMLinkObj, LCSMaterial materialObj) ###");
		return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////	Below defined functions are using in EditBOMAlt.jsp file - Initializing FlexType Path Id's String on Page Load Event and using this String in Java-script	////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function is calling from EditBOMAlt.jsp to load the pre-defined BOM FlexType Path OID on load of Edit Loader and using the same OID's in Java-script to execute the custom code
	 * @param bomFlexTypePath - String
	 * @return bomFlexTypePathOID - String
	 * @throws WTException
	 */
	public static String getBOMFlexTypePathOID(String bomFlexTypePath) throws WTException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.getBOMFlexTypePathOID(String bomFlexTypePath) ###");
		String bomFlexTypePathOID = "";
		FlexType flexTypeObj = null;
		String bomFlexTypeObjectId = "";
		
		//Get FlexTypePath List from the given flexTypePath string (which contains one or more than one FlexTypePath), iterate through flexTypePath List and initialize FlexTypePath OID
		List<String> bomFlexTypePathList = new HBIConditionedWidthDataUtil().getFlexTypePathFromPropertiesFile(bomFlexTypePath);
		for(String strBOMFlexTypePath : bomFlexTypePathList)
		{
			flexTypeObj = FlexTypeCache.getFlexTypeFromPath(strBOMFlexTypePath);
			bomFlexTypeObjectId = FormatHelper.getObjectId(flexTypeObj);
			bomFlexTypePathOID = bomFlexTypePathOID.concat(bomFlexTypeObjectId).concat(",");
		}
		
		//Validating a formatted string, contains concatenated FlexType Path OID's, validating and eliminating the additional delimiter from the formatted string (concatenated OID's)
		if(FormatHelper.hasContent(bomFlexTypePathOID) && bomFlexTypePathOID.contains(","))
		{
			bomFlexTypePathOID = bomFlexTypePathOID.substring(0, bomFlexTypePathOID.length()-1);
		}
		
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.getBOMFlexTypePathOID(String bomFlexTypePath) ###");
		return bomFlexTypePathOID;
	}
	
	/**
	 * This function is calling from EditBOMAlt.jsp to load the pre-defined BOM Type OID, BOM attribute column name on page load, using this in data Java-script to populate Cond Width(in) 
	 * @param bomFlexTypePath - String
	 * @return condWidthColumnIdentifier - String
	 * @throws WTException
	 */
	public static String getCondWidthColumnIdentifier(String bomFlexTypePath) throws WTException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.getCondWidthColumnIdentifier(String bomFlexTypePath) ###");
		String condWidthColumnIdentifier = "";
		FlexType flexTypeObj = null;
		String bomFlexTypeObjectId = "";
		String condWidthColumnName = "";
		
		//Get FlexTypePath List from the given flexTypePath string (which contains one or more than one FlexTypePath), iterate through flexTypePath List and initialize FlexTypePath OID
		List<String> bomFlexTypePathList = new HBIConditionedWidthDataUtil().getFlexTypePathFromPropertiesFile(bomFlexTypePath);
		for(String strBOMFlexTypePath : bomFlexTypePathList)
		{
			flexTypeObj = FlexTypeCache.getFlexTypeFromPath(strBOMFlexTypePath);
			//Changes done by Wipro Upgrade Team
			//condWidthColumnName = flexTypeObj.getAttribute(bomCondWidthKey).getVariableName();
			condWidthColumnName = flexTypeObj.getAttribute(bomCondWidthKey).getAttributeName();
			condWidthColumnName = "#FLEXBOMLINK_".concat(condWidthColumnName).concat("Input");
			bomFlexTypeObjectId = FormatHelper.getObjectId(flexTypeObj);
			condWidthColumnIdentifier = condWidthColumnIdentifier.concat(bomFlexTypeObjectId).concat("~").concat(condWidthColumnName).concat(",");
		}
		
		//Validating a formatted string, contains concatenated FlexType Path OID's along with column name, validating and eliminating the additional delimiter from the formatted string
		if(FormatHelper.hasContent(condWidthColumnIdentifier) && condWidthColumnIdentifier.contains(","))
		{
			condWidthColumnIdentifier = condWidthColumnIdentifier.substring(0, condWidthColumnIdentifier.length()-1);
		}
				
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.getCondWidthColumnIdentifier(String bomFlexTypePath) ###");
		return condWidthColumnIdentifier;
	}
	
	/**
	 * This function is using to validate the given FlexBOMLink (to check is primary row or variations row), get primary FlexBOMLink from variation link then get associated material object
	 * @param flexBOMLinkObj - FlexBOMLink
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 */
	public LCSMaterial getVariationLinkMaterialObject(FlexBOMLink flexBOMLinkObj) throws WTException
	{
		// LCSLog.debug("### START HBIConditionedWidthDataUtil.getVariationLinkMaterialObject(FlexBOMLink flexBOMLinkObj) ###");
		LCSMaterial materialObj = null;
		
		//Get Dimension Name from the given FlexBOMLink object, which is using to identify link type (like Top Level Row/Primary Row, variations row) to get the associated material
		String dimensionName = flexBOMLinkObj.getDimensionName();
		System.out.println(" <<<<<<< dimensionName from Variation  Link >>>>>>>>>  " +dimensionName);
		if(FormatHelper.hasContent(dimensionName))
		{
			FlexBOMLink primaryFlexBOMLinkObj = LCSFlexBOMQuery.findTopLevelBranch(flexBOMLinkObj);
			
			//Get Top Level Branch (this is also called as Primary FlexBOMLink), validate the FlexBOMLink, get associated material object, using to return from the function header
			if(primaryFlexBOMLinkObj != null)
			{
				//Changed for 12 upgrade(afsyed) - start
				//WTPartMaster materialMasterObj = primaryFlexBOMLinkObj.getChild();
				materialObj = (LCSMaterial) VersionHelper.latestIterationOf(primaryFlexBOMLinkObj.getChild());
				//Changed for 12 upgrade(afsyed) - End
				System.out.println(" <<<<<<< materialObj from Variation  Link >>>>>>>>>  " +materialObj.getIdentity());
			}
		}
		
		// LCSLog.debug("### END HBIConditionedWidthDataUtil.getVariationLinkMaterialObject(FlexBOMLink flexBOMLinkObj) ###");
		return materialObj;
	}
}
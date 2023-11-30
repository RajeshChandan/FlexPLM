package com.hbi.wc.flexbom;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.material.LCSMaterialMaster;


/**
 * HBIMaterialBOMQuery.java
 *
 * This class contains specific and generic functions which are using to fetch 'Material object' based on the given Material Name, Attribute code, Color Code and Size Code. Fetch BOMPart
 * based on the given Owner object (Material), fetch FlexBOMLink based on the given Owner object (FlexBOMPart) and referencing Material object and various FlexObject fetching custom logic
 * @author Abdul.Patel@Hanes.com
 * @since August-07-2017
 */
public class HBIMaterialBOMQuery
{
	private static String hbiMaterialSKUKey = LCSProperties.get("com.hbi.wc.flexbom.HBIMaterialBOMQuery.hbiMaterialSKUKey", "hbi17DigitSKU");

	/**
	 * This function is using to get Material object for the given criteria (like Material Name, Attribute Code, Color Code, Size Code) and return LCSMaterial object from function header
	 * @param materialName - String
	 * @param colorCode - String
	 * @param attributeCode - String
	 * @param sizeCode - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialBOMQuery.getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode) ###");
		String hbiMaterialSKUDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute(hbiMaterialSKUKey).getVariableName();
		String hbiMaterialSKU = materialName.concat(" ").concat(colorCode).concat(" ").concat(attributeCode).concat(" ").concat(sizeCode);
		LCSMaterial materialObj = null;
		
		//This block of code is explicitly written for ATT86 index, we have created index on ATT86 using UPPER char, this is done to manage material SKU uniqueness check performance issue
		String queryTableName = "UPPER(".concat("LCSMaterial");
		hbiMaterialSKUDBColumn = hbiMaterialSKUDBColumn.concat(")");
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object and unique parameters)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), new Long(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "c/i", Criteria.EQUALS));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(queryTableName, hbiMaterialSKUDBColumn), hbiMaterialSKU, Criteria.EQUALS));
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.BRANCHIDITERATIONINFO"));
	  		materialObj = (LCSMaterial) VersionHelper.latestIterationOf(materialObj);
	  	}
	    
		// LCSLog.debug("### END HBIMaterialBOMQuery.getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to get FlexBOMPart based on the given Material, using the FlexBOMPart status to decide the create or update event with the given set of data for attributes
	 * @param materialObj - LCSMaterial
	 * @return flexBOMPartObj - FlexBOMPart
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public FlexBOMPart getFlexBOMPartForOwner(LCSMaterial materialObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialBOMQuery.getFlexBOMPartForOwner(WTPartMaster materialMasterObj) ###");
		
		FlexBOMPart flexBOMPartObj = null;
	//	LCSMaterialMaster materialMasterObj = (LCSMaterialMaster) materialObj.getMaster();
		String ownerMasterRefID = FormatHelper.getNumericObjectIdFromObject(materialObj.getMaster());
	//	WTPartMaster materialMasterObj = (WTPartMaster) materialObj.getMaster();
	//	String ownerMasterRefID = FormatHelper.getNumericObjectIdFromObject(materialMasterObj);
		
		//Initializing the PreparedQueryStatement, which is using to get FlexBOMPart object based on the given set of parameter(like Material Master Unique Identifier, IterationInfo)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "iterationInfo.branchId"));
	    statement.appendFromTable(FlexBOMPart.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "iterationInfo.latest"), "?", "="), new Long(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "checkoutInfo.state"), "c/o", Criteria.NOT_EQUAL_TO));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "ownerMasterReference.key.id"), "?", "="), new Long(ownerMasterRefID));
		
	    //Get SearchResults instance from the given PreparedQueryStatement object, which is using to form FlexBOMPart object and return the FlexBOMPart object from the function header
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		flexBOMPartObj = (FlexBOMPart) LCSQuery.findObjectById("VR:com.lcs.wc.flexbom.FlexBOMPart:"+flexObj.getString("FlexBOMPart.BRANCHIDITERATIONINFO"));
	  	}
	  	
		// LCSLog.debug("### END HBIMaterialBOMQuery.getFlexBOMPartForOwner(WTPartMaster materialMasterObj) ###");
		return flexBOMPartObj;
	}
	
	/**
	 * This function is using to get FlexBOMLink from the given FlexBOMPart (Owner object) and LCSMaterial which is using to decide the create or update event with the given set of data
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param materialObj - LCSMaterial
	 * @return flexBOMLinkObj - FlexBOMLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public FlexBOMLink getFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialBOMQuery.getFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj) ###");
		FlexBOMLink flexBOMLinkObj = null;
		//WTPartMaster materialMasterObj = (WTPartMaster) materialObj.getMaster();
		LCSMaterialMaster materialMasterObj = (LCSMaterialMaster) materialObj.getMaster();
		String materialMasterRefID = FormatHelper.getNumericObjectIdFromObject(materialMasterObj);
	//	WTPartMaster bomPartMasterObj = (WTPartMaster) flexBOMPartObj.getMaster();
	//	String flexBOMPartMasterRefID = FormatHelper.getNumericObjectIdFromObject(bomPartMasterObj);
		String flexBOMPartMasterRefID = FormatHelper.getNumericObjectIdFromObject(flexBOMPartObj.getMaster());
		
		//Initializing the PreparedQueryStatement, which is using to get FlexBOMLink object based on the given set of parameter(like Material Master Unique Identifier, BOMPart Unique ID)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(FlexBOMLink.class, "thePersistInfo.theObjectIdentifier.id"));
	    statement.appendFromTable(FlexBOMLink.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, "outDate"), "", "IS NULL"));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, "dropped"), "0", "="));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, "parentReference.key.id"), "?", "="), new Long(flexBOMPartMasterRefID));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, "childReference.key.id"), "?", "="), new Long(materialMasterRefID));
		
	    //Get SearchResults instance from the given PreparedQueryStatement object, which is using to form FlexBOMLink object and return the FlexBOMLink object from the function header
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		flexBOMLinkObj = (FlexBOMLink) LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:"+flexObj.getString("FlexBOMLink.IDA2A2"));
	  	}
	  	
		// LCSLog.debug("### END HBIMaterialBOMQuery.getFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj) ###");
		return flexBOMLinkObj;
	}
}
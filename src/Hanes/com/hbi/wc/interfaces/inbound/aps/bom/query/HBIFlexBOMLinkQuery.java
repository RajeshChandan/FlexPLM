package com.hbi.wc.interfaces.inbound.aps.bom.query;

import java.sql.ResultSet;
import java.sql.SQLException;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartMaster;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIFlexBOMLinkQuery.java
 * 
 * This class contains specific and generic functions which are using to query FlexBOMLink data using FlexBOMPart and LCSMaterial object, using material as child object within the BOMLink,
 * get an existing sorting number within the BOMPart, max sorting number or max branch ID, these functions will be using in FlexBOMLink create and update event to populate FlexBOMLink data
 * @author Abdul.Patel@Hanes.com
 * @since November-20-2017
 */
public class HBIFlexBOMLinkQuery
{
	private static String bomSectionKey = LCSProperties.get("com.hbi.wc.flexbom.HBIMaterialBOMLogic.bomSectionKey", "section");
	
	/**
	 * This function is using to get FlexBOMLink from the given FlexBOMPart(Owner object) and LCSMaterial, which is using to decide the event type (create/update) within the given BOMPart
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param materialObj - LCSMaterial
	 * @return flexBOMLinkObj - FlexBOMLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public FlexBOMLink getFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIFlexBOMLinkQuery.getFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj) ###");
		FlexBOMLink flexBOMLinkObj = null;
		//WTPartMaster materialMasterObj = (WTPartMaster) materialObj.getMaster();
		LCSMaterialMaster materialMasterObj = (LCSMaterialMaster) materialObj.getMaster();
		String materialMasterRefID = FormatHelper.getNumericObjectIdFromObject(materialMasterObj);
		//WTPartMaster bomPartMasterObj = (WTPartMaster) flexBOMPartObj.getMaster();
		FlexBOMPartMaster bomPartMasterObj = (FlexBOMPartMaster) flexBOMPartObj.getMaster();
		String flexBOMPartMasterRefID = FormatHelper.getNumericObjectIdFromObject(bomPartMasterObj);
		
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
	  	
		// LCSLog.debug("### END HBIFlexBOMLinkQuery.getFlexBOMLinkForOwner(FlexBOMPart flexBOMPartObj, LCSMaterial materialObj) ###");
		return flexBOMLinkObj;
	}
	
	/**
	 * This function is using to get/return a next available sorting number/ID of a FlexBOMLink based on the given FlexBOMPart(owner of FlexBOMLink) and section name of the FlexBOMLink
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param bomSection - String
	 * @return sortingNumber - int
	 * @throws WTException
	 */
	public int getMaximumSortingNumberForFlexBOMPartAndSection(FlexBOMPart flexBOMPartObj, String bomSection) throws WTException
	{
		// LCSLog.debug("### START HBIFlexBOMLinkQuery.getMaximumSortingNumberForFlexBOMPartAndSection(FlexBOMPart flexBOMPartObj, String bomSection) ###");
		int sortingNumber= 0;
		
		try
		{
			//WTPartMaster ownerMasterObj = (WTPartMaster)flexBOMPartObj.getMaster();
			FlexBOMPartMaster  ownerMasterObj = (FlexBOMPartMaster )flexBOMPartObj.getMaster();
			String bomPartMasterID = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(ownerMasterObj));
			String sectionNameDBColumn = flexBOMPartObj.getFlexType().getAttribute(bomSectionKey).getColumnDescriptorName();
			
			//Preparing SQL Query to get Maximum Sorting Number for the given FlexBOMPart(Owner of all FlexBOMLinks) and for the given BOM Section Name 
			String sortingNumberQuery = "SELECT MAX(SORTINGNUMBER) FROM FLEXBOMLINK WHERE FLEXBOMLINK.OUTDATE IS NULL AND IDA3A5 = " + bomPartMasterID + " AND "+sectionNameDBColumn+" LIKE '"+bomSection +"'"+ " GROUP BY "+sectionNameDBColumn;
			LCSLog.debug("HBIFlexBOMLinkQuery.getMaximumSortingNumberForFlexBOMPartAndSection() : sortingNumberQuery = " + sortingNumberQuery);
			 
			//Initializing Query object(which internally take care of Database Connection) and ResultSet to execute the SQL Query to get Maximum Sorting Number
			Query query = new Query();    
			query.prepareForQuery();
			ResultSet results = query.runQuery(sortingNumberQuery);  
			if(results != null && results.next())
			{
				sortingNumber = results.getInt(1);
			}
			
			LCSLog.debug("HBIFlexBOMLinkQuery.getMaximumSortingNumberForFlexBOMPartAndSection() : sortingNumber = " + sortingNumber);
			results.close();
			query.cleanUpQuery();
		}
		catch (SQLException sqlExp)
		{
			LCSLog.debug(" SQLException in HBIFlexBOMLinkQuery.getMaximumSortingNumberForFlexBOMPartAndSection() is :: "+ sqlExp);
		}
		
		// LCSLog.debug("### END HBIFlexBOMLinkQuery.getMaximumSortingNumberForFlexBOMPartAndSection(FlexBOMPart flexBOMPartObj, String bomSection) ###");
		return sortingNumber;
	}
}
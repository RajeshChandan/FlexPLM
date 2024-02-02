package com.hbi.wc.interfaces.inbound.aps.bom.query;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;

/**
 * HBIFlexBOMPartQuery.java
 * 
 * This class contains specific and generic functions which are using to query FlexBOMPart, using product object and bill of material name as an input parameter, these functions are using 
 * in FlexBOMPart create and update event to add or update FlexBOMLink within a FlexBOMPart, adding new BOMLink, deleting an existing FlexBOMLink and updating FlexBOMLink for material data
 * @author Abdul.Patel@Hanes.com
 * @since November-21-2017
 */
public class HBIFlexBOMPartQuery
{
	/**
	 * This function is using to get FlexBOMPart object based on the given Product, using FlexBOMPart status to decide the create or update event with the given FlexBOMPart attributes set
	 * @param productObj - LCSProduct
	 * @param billOfMaterialName - String
	 * @return flexBOMPartObj - FlexBOMPart
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public FlexBOMPart getFlexBOMPartForOwner(LCSProduct productObj, String billOfMaterialName) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIFlexBOMPartQuery.getFlexBOMPartForOwner(LCSProduct productObj, String billOfMaterialName) ###");
		String bomPartNameDBColumn = FlexTypeCache.getFlexTypeFromPath("BOM").getAttribute("name").getColumnDescriptorName();
		FlexBOMPart flexBOMPartObj = null;
		LCSPartMaster productMasterObj =  productObj.getMaster();
		String ownerMasterRefID = FormatHelper.getNumericObjectIdFromObject(productMasterObj);
		
		//Initializing the PreparedQueryStatement, which is using to get FlexBOMPart object based on the given set of parameter(like Product Master Unique Identifier, IterationInfo)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "iterationInfo.branchId"));
	    statement.appendFromTable(FlexBOMPart.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "iterationInfo.latest"), "?", "="), new Long(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "checkoutInfo.state"), "c/o", Criteria.NOT_EQUAL_TO));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "ownerMasterReference.key.id"), "?", "="), new Long(ownerMasterRefID));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, bomPartNameDBColumn), billOfMaterialName, Criteria.EQUALS));
	    
	    //Get SearchResults instance from the given PreparedQueryStatement object, which is using to form FlexBOMPart object and return the FlexBOMPart object from the function header
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		flexBOMPartObj = (FlexBOMPart) LCSQuery.findObjectById("VR:com.lcs.wc.flexbom.FlexBOMPart:"+flexObj.getString("FlexBOMPart.BRANCHIDITERATIONINFO"));
	  	}
	  	
		// LCSLog.debug("### END HBIFlexBOMPartQuery.getFlexBOMPartForOwner(LCSProduct productObj, String billOfMaterialName) ###");
		return flexBOMPartObj;
	}
}
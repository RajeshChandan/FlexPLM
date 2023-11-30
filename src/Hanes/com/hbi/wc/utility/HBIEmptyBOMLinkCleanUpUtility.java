package com.hbi.wc.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.lcs.wc.flexbom.BOMOwner;
import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFindFlexBOMHelper;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.ReferencedTypeKeys;
import com.lcs.wc.report.ColumnList;
import com.lcs.wc.report.ReportQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * @author U55219
 * Plugin to remove empty bom links in PLM Application
 * Takes input parameters BOM type and VIEW name .
 * Triggers when updated Business Object/Size Class
 */
public class HBIEmptyBOMLinkCleanUpUtility {

	
	/* Default executable function of the class HBIEmptyBOMLinkCleanUpUtility */
	public static StringBuffer nonProductBOMs = new StringBuffer();
	public static StringBuffer modifiedBOMs = new StringBuffer();

	private static String VIEW_NAME = LCSProperties.get(
			"com.hbi.wc.utility.HBIEmptyBOMLinkCleanUpUtility.viewName");
	private static String BOM_TYPE = LCSProperties.get(
			"com.hbi.wc.utility.HBIEmptyBOMLinkCleanUpUtility.bomType");
	private static String SECTIONS_TO_IGNORE = LCSProperties.get(
			"com.hbi.wc.utility.HBIEmptyBOMLinkCleanUpUtility.sectionsToIgnore","hbilabel");
	
	

	/**
	 * Method gets all BOM parts for the given BOM type and process.
	 * 
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void removeBomEmptylinks(WTObject obj) throws WTException,
			WTPropertyVetoException  {

		if (obj instanceof LCSLifecycleManaged) {
			LCSLifecycleManaged businessObject = (LCSLifecycleManaged) obj;

			long start = System.currentTimeMillis();

			
			
			
			// String bomtype=(String) businessObject.getValue("bomType");
			LCSLog.debug("---------------BOM_TYPE VALUE------------------------------------------"
					+ BOM_TYPE);
			if (FormatHelper.hasContent(BOM_TYPE)) {
				Collection columns=getColumnsFromViewName();
				LCSLog.debug("### START HBIEmptyBOMLinkCleanUpUtility.getAllFlexBOMPartFromType() ###");
				FlexBOMPart bomPartObject = null;
				
				Collection<FlexObject> flexBOMPartCollection = getFlexBOMColletionFromType(BOM_TYPE);
				LCSLog.debug("flexBOMPartCollection-------------->"
						+ flexBOMPartCollection.size());
				for (FlexObject flexObject : flexBOMPartCollection) {
					bomPartObject = (FlexBOMPart) LCSQuery
							.findObjectById("VR:com.lcs.wc.flexbom.FlexBOMPart:"
									+ flexObject
											.getString("FlexBOMPart.BRANCHIDITERATIONINFO"));

					if (!VersionHelper.isWorkingCopy(bomPartObject)) {

						BOMOwner wtPartMaster = null;
						wtPartMaster = bomPartObject.getOwnerMaster();

						// LCSPart lcsPart =
						// (LCSPart)VersionHelper.latestIterationOf(wtPartMaster);

						if (VersionHelper.latestIterationOf(wtPartMaster) instanceof LCSProduct) {
							LCSProduct product = (LCSProduct) VersionHelper
									.latestIterationOf(wtPartMaster);
							product = SeasonProductLocator.getProductARev(product);
							
							getBOMLinkDataFromFlexBOMPartObject(bomPartObject,
									wtPartMaster,columns);
							 
						} 

						// This methos is used to get BOMLink with all variation by
						// passing
						// FlexBOMPart object.

					}

				}// break;

				LCSLog.debug("### END HBIEmptyBOMLinkCleanUpUtility.getAllFlexBOMPartFromType() ###");

				LCSLog.debug("BOMS Modified ----------->\n"
						+ modifiedBOMs.toString());
				
				long end = System.currentTimeMillis();

				NumberFormat formatter = new DecimalFormat("#0.00000");
				LCSLog.debug("Execution time for the type : " +BOM_TYPE+" is :-->"+ formatter.format((end - start) / 1000d) + " seconds");
				

			}
			 
		}

	}

	

	/**
	 * Method to get all BOM part objects for the given type.
	 * @param flexBOMPartType
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection<FlexObject> getFlexBOMColletionFromType(
			String flexBOMPartType) throws WTException, WTPropertyVetoException {
		Collection<FlexObject> flexBOMPartCollection = null;
		FlexType bomFlexTypeObj = FlexTypeCache
				.getFlexTypeFromPath(flexBOMPartType);
		String id = bomFlexTypeObj.getTypeIdPath();

		PreparedQueryStatement pqs = new PreparedQueryStatement();
		pqs.appendFromTable(FlexBOMPart.class);
		pqs.setDistinct(true);
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
				"iterationInfo.branchId"));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class,
				"iterationInfo.latest"), "1", Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class,
				"checkoutInfo.state"), "wrk", "<>"));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class,
				"flexTypeIdPath"), id, Criteria.EQUALS));
		
		SearchResults results = LCSQuery.runDirectQuery(pqs);
		if (results != null && results.getResultsFound() > 0) {
			flexBOMPartCollection = results.getResults();
		}
		// LCSLog.debug("STMT is ------------>"+pqs);
		// LCSLog.debug("flexBOMPartCollection--------------->"+flexBOMPartCollection.size());
		return flexBOMPartCollection;
	}

	/**
	 * method to identify the empty bomlinks for the input parameter BOM Part.
	 * @param flexBOMPartObj
	 * @param prod
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void getBOMLinkDataFromFlexBOMPartObject(
			FlexBOMPart flexBOMPartObj, BOMOwner prod,Collection columns) throws WTException,
			WTPropertyVetoException {

		FlexType materialType = flexBOMPartObj.getFlexType()
				.getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);

		
		flexBOMPartObj = (FlexBOMPart) VersionHelper
				.latestIterationOf(flexBOMPartObj);

		Collection<FlexObject> bomData = new ArrayList();
		try {
			bomData = LCSFindFlexBOMHelper.findBOM(flexBOMPartObj, "", "", "",
					"", "", "SINGLE", "", new Boolean(true), materialType,
					columns);
			
			// System.out.println("Bomlink  for the -----------------------------"+bomData);
			// bomData = SortHelper.sortFlexObjectsByNumber(bomData,
			// "sortingNumber");
		} catch (Throwable t) {
			t.printStackTrace();
		}

		for (FlexObject flexObject : bomData) {
		
			
			if (flexObject.getString("FLEXBOMLINKID") == null) {

				continue;
			}
			FlexBOMLink bomLinkObj = (FlexBOMLink) LCSQuery
					.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:"
							+ flexObject.getString("FLEXBOMLINKID"));
			
			
			
			if (bomLinkObj != null  && (! SECTIONS_TO_IGNORE.contains(flexObject.getString("section")))) {

				

				boolean isEmpty = isBomLinkEmpty(flexObject);
				

				if (isEmpty) {

					
				
					
					LCSLog.debug("LOGGER: "+flexBOMPartObj.getName()+"\t"+bomLinkObj.getSortingNumber()+"\t"+ "ProdName:"
							+ prod +"\t"+ " - Link ID:"
							+ FormatHelper.getObjectId(bomLinkObj) );
					
					
					
					bomLinkObj.setDropped(true);

					LCSLogic.persist(bomLinkObj, true);
					
					modifiedBOMs.append(flexBOMPartObj.getName()
							+ " - Link ID:"
							+ FormatHelper.getObjectId(bomLinkObj) + "("
							+ prod + ")" + '\n');

				}

			}
		}
	}

	/**
	 * Checks for any value presence in the given row.
	 * @param bomLinkFOB
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static boolean isBomLinkEmpty(FlexObject bomLinkFOB)
			throws WTException, WTPropertyVetoException {
		// TODO Auto-generated method stub

		Collection columns=getColumnsFromViewName();
		System.out.println("");
		Iterator<String> viewAttCol = columns.iterator();

		while (viewAttCol.hasNext()) {

			String attString = viewAttCol.next();
			 if(!"section".equals(attString)){
			String value = null;
			
				
				value = bomLinkFOB.getString(attString);

			
			if("supplierName".equals(attString) ){
				
				if(!"placeholder".equals(value)){
					return false;
				}
			}else{
				
				if (FormatHelper.hasContent(value)) {

					return false;
				}
			
				
				
			}

			
			 }
		}

		return true;
	}

	/**
	 * Gets view Object from the given name and returns all the columns
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection getColumnsFromViewName() throws WTException,
			WTPropertyVetoException {

		Collection finalColumns = new ArrayList();
		PreparedQueryStatement pqs = new PreparedQueryStatement();
		pqs.appendFromTable(ColumnList.class);
		pqs.setDistinct(true);
		pqs.appendSelectColumn(new QueryColumn("ColumnList", "IDA2A2"));

		pqs.appendCriteria(new Criteria(new QueryColumn("ColumnList",
				"RELEVANTACTIVITY"), "EDIT_BOM", Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn("ColumnList",
				"DISPLAYNAME"), VIEW_NAME, Criteria.EQUALS));

		Iterator i = ReportQuery.runDirectQuery(pqs).getResults().iterator();
		ColumnList viewObj = null;
		while (i.hasNext()) {

			FlexObject viewFOB = (FlexObject) i.next();
			String viewID = viewFOB.getString("COLUMNLIST.IDA2A2");

			viewObj = (ColumnList) LCSQuery
					.findObjectById("OR:com.lcs.wc.report.ColumnList:" + viewID);
			if (viewObj != null) {
				Collection<String> columns = viewObj.getAttributes();

				for (String key : columns) {

					if (!key.equals("Colorways")) {
					if (key.indexOf(".") > 0) {
						finalColumns.add(key.substring(key.indexOf(".") + 1,
								key.length()));

					} else {

						finalColumns.add(key);
					}
					}

				}
			} else {

				LCSLog.debug("--------------VIEW IS NOT PRESENT WITH THE GIVEN NAME :-->"
						+ VIEW_NAME);
			}
			
			finalColumns.add("partName");
			finalColumns.add("section");

		}
		LCSLog.debug("finalColumns-------------->"+finalColumns);
		return finalColumns;
	}

}

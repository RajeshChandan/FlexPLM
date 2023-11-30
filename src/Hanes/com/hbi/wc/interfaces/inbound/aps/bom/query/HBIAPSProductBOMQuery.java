package com.hbi.wc.interfaces.inbound.aps.bom.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.hbi.wc.interfaces.inbound.aps.bom.db.HBIConnectionManager;
import com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIProductBOMLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;

/**
 * HBIAPSProductBOMQuery.java
 * 
 * This class contains specific and generic functions which are using to get connection instance, get style status from APS STYLE, get bill of material id from MFG_PATH table, get bill of 
 * material data from BILL_OF_MTRLS table, get style information from SKU table, format APS data to downcast as the PLM mappable data, release connection to the pool, db connection clean-up
 * @author Abdul.Patel@Hanes.com
 * @since November-23-2017
 */
public class HBIAPSProductBOMQuery
{
	private static String activeStylesQueryString = " AND END_DATE_IND='N' AND EFFECT_END_DATE > SYSDATE";
	private static String manufacturingStyleStatusColumns = "ITEM_TYPE_CD, MATL_TYPE_CD";
	private static String mfgStyleBillOfMaterialColumns = "PARENT_STYLE, PARENT_COLOR, PARENT_ATTRIBUTE, PARENT_SIZE, COMP_STYLE_CD, COMP_COLOR_CD, COMP_ATTRIBUTE_CD, COMP_SIZE_CD, ACTIVITY_CD";
	
	/**
	 * This function is using to get connection object from connection pool, preparing queryString to get manufacturing style status from APS, validate and return manufacturing style status
	 * @param styleCode - String
	 * @return manufacturingStyleExistsInAPS - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public boolean getManufacturingStyleStatus(String manufacturingStyleCode) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMQuery.getManufacturingStyleStatus(String manufacturingStyleCode) ###");
		boolean manufacturingStyleExistsInAPS = false;
		Connection connectionObj = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		
		try
		{
			connectionObj = HBIConnectionManager.getConnection();
			statementObj = connectionObj.createStatement();
			String queryString = "SELECT "+manufacturingStyleStatusColumns+" FROM STYLE WHERE STYLE_CD='"+manufacturingStyleCode+"'"+activeStylesQueryString;
			LCSLog.debug("Query to check Manufacturing Style status = "+ queryString);
			
			//Execute queryString to fetch manufacturing style status from APS STYLE table, validate ResultSet, based on ResultSet validation re-initialize manufacturing style APS status
			resultSetObj = statementObj.executeQuery(queryString);
			if(resultSetObj != null && resultSetObj.next())
			{
				manufacturingStyleExistsInAPS = true;
			}
		}
		finally
		{
			if(resultSetObj != null)
			{
				resultSetObj.close();
				resultSetObj = null;
			}
			if(statementObj != null)
			{
				statementObj.close();
				statementObj = null;
			}
			
			HBIConnectionManager.releaseConnection(connectionObj);
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getManufacturingStyleStatus(String manufacturingStyleCode) ###");
		return manufacturingStyleExistsInAPS;
	}
	
	/**
	 * This function is using to get a collection of bill of material id from MFG_PATH table for the given 'Manufacturing Style' and returning a bill of material id collection to a caller
	 * @param manufacturingStyleCode - String
	 * @return billOfMaterialIdColl - Collection<String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public Collection<String> getBillOfMaterialIdCollection(String manufacturingStyleCode) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMQuery.getBillOfMaterialIdCollection(String manufacturingStyleCode) ###");
		Collection<String> billOfMaterialIdColl = new ArrayList<String>();
		Connection connectionObj = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		
		try
		{
			connectionObj = HBIConnectionManager.getConnection();
			statementObj = connectionObj.createStatement();
			String queryString = "SELECT DISTINCT(BILL_OF_MTRLS_ID) FROM MFG_PATH WHERE STYLE_CD='"+manufacturingStyleCode+"'"+activeStylesQueryString;
			LCSLog.debug("Query to get Distinct bill of material ID's = "+ queryString);
			
			//Execute queryString to fetch bill of material id from APS MFG_PATH table, validate ResultSet, based on ResultSet validation create a bill of material id collection
			resultSetObj = statementObj.executeQuery(queryString);
			billOfMaterialIdColl = getBillOfMaterialIdCollection(resultSetObj);
		}
		finally
		{
			if(resultSetObj != null)
			{
				resultSetObj.close();
				resultSetObj = null;
			}
			if(statementObj != null)
			{
				statementObj.close();
				statementObj = null;
			}
			
			HBIConnectionManager.releaseConnection(connectionObj);
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getBillOfMaterialIdCollection(String manufacturingStyleCode) ###");
		return billOfMaterialIdColl;
	}
	
	/**
	 * This function is using to iterate through each rows of the given resultSet object, get bill of material id, validate and add bill of material id to collection to return from header
	 * @param resultSetObj - ResultSet
	 * @return billOfMaterialIdColl - Collection<String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	private Collection<String> getBillOfMaterialIdCollection(ResultSet resultSetObj) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMQuery.getBillOfMaterialIdCollection(ResultSet resultSetObj) ###");
		Collection<String> billOfMaterialIdColl = new ArrayList<String>();
		String billOfMaterialName = "";
		
		//Validate ResultSet object, iterate through each rows to get bill of material id, validate and add to a collection which is using to return from function header to calling method
		if(resultSetObj != null)
		{
			while(resultSetObj.next())
			{
				billOfMaterialName = resultSetObj.getString("BILL_OF_MTRLS_ID");
				if(FormatHelper.hasContent(billOfMaterialName))
				{
					billOfMaterialName = billOfMaterialName.trim();
					billOfMaterialIdColl.add(billOfMaterialName);
				}
			}
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getBillOfMaterialIdCollection(ResultSet resultSetObj) ###");
		return billOfMaterialIdColl;
	}
	
	/**
	 * This function is using to get bill of materials data from APS BILL_OF_MTRLS table, using manufacturing style code and billOfMaterialId as where clause parameters to return resultSet
	 * @param parentStyle - String
	 * @param billOfMaterialId - String
	 * @return resultSetObj - ResultSet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public void getManufacturingStyleBillOfMaterials(LCSProduct productObj, String parentStyle, String billOfMaterialId) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMQuery.getManufacturingStyleBillOfMaterials(String parentStyle, String billOfMaterialId) ###");
		Connection connectionObj = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		
		try
		{
			connectionObj = HBIConnectionManager.getConnection();
			statementObj = connectionObj.createStatement();
			String mfgPathQuery = "SELECT COLOR_CD, ATTRIBUTE_CD, SIZE_CD FROM MFG_PATH WHERE STYLE_CD='"+parentStyle+"'"+" AND BILL_OF_MTRLS_ID='"+billOfMaterialId+"'"+activeStylesQueryString;
			String bomQuery = "SELECT "+mfgStyleBillOfMaterialColumns+" FROM BILL_OF_MTRLS WHERE PARENT_STYLE='"+parentStyle+"'"+" AND BILL_OF_MTRLS_ID='"+billOfMaterialId+"'";
			String queryString = bomQuery+" AND (PARENT_COLOR, PARENT_ATTRIBUTE, PARENT_SIZE) IN("+mfgPathQuery+")";
			LCSLog.debug("Query to get bill of material data = "+ queryString);
			
			//Calling a function which is using to validate the given data, based on the validation status creating/updating FlexBOMPart and FlexBOMLink to populate latest data from APS
			resultSetObj = statementObj.executeQuery(queryString);
			new HBIProductBOMLogic().validateAndSyncProductBOMData(productObj, resultSetObj, billOfMaterialId);
		}
		finally
		{
			if(resultSetObj != null)
			{
				resultSetObj.close();
				resultSetObj = null;
			}
			if(statementObj != null)
			{
				statementObj.close();
				statementObj = null;
			}
			
			HBIConnectionManager.releaseConnection(connectionObj);
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getManufacturingStyleBillOfMaterials(String parentStyle, String billOfMaterialId) ###");
	}
	
	/**
	 * This function is using to get garment cut section bill of materials data from APS BILL_OF_MTRLS table using manufacturing style code and billOfMaterialId as where clause parameters
	 * @param garmentCutParentStyleSet - Set<String>
	 * @param billOfMaterialId - String
	 * @return garmentCutParentStyleSet - Set<String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public Set<String> getGarmentCutBOMMaterialSet(Set<String> garmentCutParentStyleSet, String billOfMaterialId) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getGarmentCutBOMMaterialSet(Set<String> garmentCutParentStyleSet, String billOfMaterialId) ###");
		Set<String> garmentCutMaterialSet = new HashSet<String>();
		Connection connectionObj = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		String queryString = "";
		String mfgPathQuery = "";
		String bomQuery = "";
		
		try
		{
			connectionObj = HBIConnectionManager.getConnection();
			statementObj = connectionObj.createStatement();
			for(String parentStyle : garmentCutParentStyleSet)
			{
				//Calling a function to get a collection of bill of material id from MFG_PATH table for the given 'Manufacturing Style' and returning a bill of material id collection
				Collection<String> billOfMaterialIdColl = getBillOfMaterialIdCollection(parentStyle);
				for(String billOfMaterialName : billOfMaterialIdColl)
				{
					mfgPathQuery = "SELECT COLOR_CD, ATTRIBUTE_CD, SIZE_CD FROM MFG_PATH WHERE STYLE_CD='"+parentStyle+"'"+" AND BILL_OF_MTRLS_ID='"+billOfMaterialName+"'"+activeStylesQueryString;
					bomQuery = "SELECT "+mfgStyleBillOfMaterialColumns+" FROM BILL_OF_MTRLS WHERE PARENT_STYLE='"+parentStyle+"'"+" AND BILL_OF_MTRLS_ID='"+billOfMaterialName+"'";
					queryString = bomQuery+" AND (PARENT_COLOR, PARENT_ATTRIBUTE, PARENT_SIZE) IN("+mfgPathQuery+")";
					resultSetObj = statementObj.executeQuery(queryString);
					garmentCutMaterialSet = getGarmentCutBOMMaterialSet(resultSetObj, garmentCutMaterialSet, parentStyle);
				}
			}
		}
		finally
		{
			if(resultSetObj != null)
			{
				resultSetObj.close();
				resultSetObj = null;
			}
			if(statementObj != null)
			{
				statementObj.close();
				statementObj = null;
			}
			
			HBIConnectionManager.releaseConnection(connectionObj);
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getGarmentCutBOMMaterialSet(Set<String> garmentCutParentStyleSet, String billOfMaterialId) ###");
		return garmentCutMaterialSet;
	}
	
	/**
	 * This function is using to iterate through result set data get component style code and component size code, which are needed to create master material and garment cut sections data
	 * @param resultSetObj - ResultSet
	 * @param garmentCutMaterialSet - Set<String>
	 * @param parentStyle - String
	 * @return garmentCutMaterialSet - Set<String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	private Set<String> getGarmentCutBOMMaterialSet(ResultSet resultSetObj, Set<String> garmentCutMaterialSet, String parentStyle) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMQuery.getGarmentCutBOMMaterialSet(ResultSet resultSetObj, Set<String> garmentCutMaterialSet) ###");
		String materialName = "";
		String sizeCode = "";
		String uniqueMasterMaterialData = "";
		
		//Validate ResultSet object, iterate through each rows to get component style code and component size code, which is needed to create master material and garment cut BOM data 
		if(resultSetObj != null)
		{
			while(resultSetObj.next())
			{
				materialName = resultSetObj.getString("COMP_STYLE_CD");
				sizeCode = resultSetObj.getString("COMP_SIZE_CD");
				uniqueMasterMaterialData = materialName.concat("_").concat(sizeCode);
				garmentCutMaterialSet.add(uniqueMasterMaterialData);
			}
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMQuery.getGarmentCutBOMMaterialSet(ResultSet resultSetObj, Set<String> garmentCutMaterialSet) ###");
		return garmentCutMaterialSet;
	}
}
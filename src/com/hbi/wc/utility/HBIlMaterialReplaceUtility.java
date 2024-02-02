package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Vector;

import com.lcs.wc.material.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hbi.wc.sample.HBISampleHelper;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSColorLogic;
import com.lcs.wc.color.LCSColorQuery;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteLogic;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.whereused.FAWhereUsedQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPartMaster;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

/**
 * HBIlMaterialReplaceUtility.java
 *
 * This class contains generic functions to fetch Supplier references using where used function, iterate through each references to update color reference from one color to another for all
 * references (like material-color, sample, colorway and FlexBOMLink), using this utility we can replace multiple Suppliers in a single execution and this utility will handle of existing data
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since Oct-06-2017
 */
public class HBIlMaterialReplaceUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
    private static RemoteMethodServer remoteMethodServer; 
    private static String floderPhysicalLocation = "";
	private static String supplierDataFileName = "SupplierReplaceDataFile.xls";
	
	static
	{
		try
		{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String home = wtprops.getProperty("wt.home");
	        floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration";
	        if(!(new File(floderPhysicalLocation).exists()))
	        {
	        	new File(floderPhysicalLocation).mkdir();
	        }
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in static block of the class HBIlMaterialReplaceUtility is : "+ exp);
		}
	}
    
    /** Default executable function of the class HBIlMaterialReplaceUtility */
    public static void main(String[] args) 
    {
        LCSLog.debug("### START HBIlMaterialReplaceUtility.main() ###");
        
        try 
        {
            MethodContext mcontext = new MethodContext((String) null, (Object) null);
            SessionContext sessioncontext = SessionContext.newContext();

            remoteMethodServer = RemoteMethodServer.getDefault();
            remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
            remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
			
			validateAndUpdateForSupplierReplace(supplierDataFileName);
            System.exit(0);
        }
        catch(Exception exp) 
        {
        	exp.printStackTrace();
            System.exit(1);
        }

        LCSLog.debug("### END HBIlMaterialReplaceUtility.main() ###");
    }
    
    /**
     * This function is using as a plug-in function which is registered on LCSLifecycleManaged PRE_PERSIST EVENT to validate the business object type, invoke color reference change utility
     * @param wtObj - WTObject
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws SQLException
     */
    public static void updateSupplierReferencesForReplace(WTObject wtObj) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	LCSLog.debug("### START HBIlMaterialReplaceUtility.updateColorReferencesForReplace(WTObject wtObj) ###");
    	
    	if(wtObj instanceof LCSLifecycleManaged)
		{
    		LCSLifecycleManaged businessObject = (LCSLifecycleManaged) wtObj;
    		
    		//Validating the given business object type path, based on the type path validations invoking color replace utility which will update color references to set new color object 
    		if("Business Object\\Reports".equals(businessObject.getFlexType().getFullName(true)))
    		{
    			if("Test Color".equalsIgnoreCase((String) businessObject.getName()))
    			{
    				validateAndUpdateForSupplierReplace(supplierDataFileName);
    			}
    		}
		}
    
    	LCSLog.debug("### END HBIlMaterialReplaceUtility.updateColorReferencesForReplace(WTObject wtObj) ###");
    }
    
    /**
     * This function is using to read color data file from the source directory, get first work sheet from the given XLS and invoking internal function to read given data and format color
     * @param colorDataFileName - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SQLException
     */
    public static void validateAndUpdateForSupplierReplace(String colorDataFileName) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	// LCSLog.debug("### START HBIlMaterialReplaceUtility.validateAndUpdateForSupplierReplace(String colorDataFileName) ###");
		FileInputStream fileInputStreamObj = null;
		
		try
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+colorDataFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);

			new HBIlMaterialReplaceUtility().validateAndUpdateForSupplierReplace(worksheet);
		}
		finally
    	{
    		if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
    	}
    	
    	// LCSLog.debug("### END HBIlMaterialReplaceUtility.validateAndUpdateForSupplierReplace(String colorDataFileName) ###");
    }
    
    /**
     * This function is using to read all rows from the given data file to get Source Color unique identifier and 'Target Color' unique identifier to initialize and replace all references
     * @param worksheet - HSSFSheet
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SQLException
     */
    @SuppressWarnings("deprecation")
    public void validateAndUpdateForSupplierReplace(HSSFSheet worksheet) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	// LCSLog.debug("### START HBIlMaterialReplaceUtility.validateAndUpdateForSupplierReplace(HSSFSheet worksheet) ###");
    	HSSFRow row = null;
		int sourceMaterialID = 0;
		int targetMaterialID = 0;
		LCSMaterial sourceMaterialObj = null;
		LCSMaterial targetMaterialObj = null;
		String sourceColorServiceName = "";
		int targetColorServiceNo = 0;
		String targetColorServiceName = "";
		
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
			
	    	sourceMaterialID = (int) row.getCell((short) 0).getNumericCellValue();
			targetMaterialID = (int) row.getCell((short) 1).getNumericCellValue();
			
			System.out.println("  <,sourceMaterialID  >>>" +sourceMaterialID);
			System.out.println("  <,targetMaterialID  >>>" +targetMaterialID);

			sourceMaterialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+sourceMaterialID);
			System.out.println(" sourceMaterialObj Name " +sourceMaterialObj.getName());
			targetMaterialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+targetMaterialID);
			System.out.println(" targetMaterialObj Name " +targetMaterialObj.getName());
			
			validateAndUpdateSupplierReferences(sourceMaterialObj, targetMaterialObj);
		}
	}
	
	public void validateAndUpdateSupplierReferences(LCSMaterial sourceMaterialObj, LCSMaterial targetMaterialObj) throws WTException, WTPropertyVetoException,SQLException
    {
		LCSMaterialMaster sourceMaterialMaster =(LCSMaterialMaster) sourceMaterialObj.getMaster();
		LCSMaterialMaster targetMaterialMaster =(LCSMaterialMaster) targetMaterialObj.getMaster();
		
		int sourceSupplierID = 3493715;
		LCSSupplier supplierObj = (LCSSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:"+sourceSupplierID);
		System.out.println("Supplier Name Name " +supplierObj.getName());
		LCSSupplierMaster supplierMaster = (LCSSupplierMaster)supplierObj.getMaster();
		
		LCSMaterialSupplier targetMatSupObj = new LCSMaterialSupplierQuery().findMaterialSupplier(targetMaterialMaster,supplierMaster); 
		
		updateMaterialColorForMaterialReplace(sourceMaterialMaster,targetMaterialMaster,supplierMaster,targetMatSupObj);
		
    }

	public void updateMaterialColorForMaterialReplace(LCSMaterialMaster sourceMaterialMaster,LCSMaterialMaster targetMaterialMaster,LCSSupplierMaster supplierMaster,LCSMaterialSupplier targetMatSupObj) throws WTException, WTPropertyVetoException,SQLException
	{
		Vector<FlexObject> sourceListOfObjects = null;
		LCSMaterialColor sourceMatColorObj =null;
		LCSMaterialColorLogic materialColorLogicObj = new LCSMaterialColorLogic();
		LCSMaterialColorQuery materialColorQueryObj = new LCSMaterialColorQuery();
		LCSMaterialSupplierMaster  targetMasterMaterialSupplierObj = (LCSMaterialSupplierMaster)targetMatSupObj.getMaster();
		
		SearchResults  sourceSearchResultsObj = materialColorQueryObj.findMaterialColorData(sourceMaterialMaster,supplierMaster);
		if(sourceSearchResultsObj != null && sourceSearchResultsObj.getResultsFound() > 0)
		{	
			sourceListOfObjects = sourceSearchResultsObj.getResults();
			System.out.println(">>>> Size of Material Colors >>> ="+sourceListOfObjects.size());
			for (FlexObject flexObj : sourceListOfObjects)
			{
				sourceMatColorObj = (LCSMaterialColor)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:"+ flexObj.getString("LCSMaterialColor.IDA2A2"));
				sourceMatColorObj.setMaterialMaster(targetMaterialMaster);
				sourceMatColorObj.setSupplierMaster(supplierMaster);
				sourceMatColorObj.setMaterialSupplierMaster(targetMasterMaterialSupplierObj);
				materialColorLogicObj.saveMaterialColor(sourceMatColorObj);
			}
		}		
	
	}

}	

	
	
	
	
    


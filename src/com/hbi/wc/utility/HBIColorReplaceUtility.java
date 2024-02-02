package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialColorQuery;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.whereused.FAWhereUsedQuery;

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
 * HBIColorReplaceUtility.java
 *
 * This class contains generic functions to fetch color references using where used function, iterate through each references to update color reference from one color to another for all
 * references (like material-color, sample, colorway and FlexBOMLink), using this utility we can replace multiple colors in a single execution and this utility will handle of existing data
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-04-2017
 */
public class HBIColorReplaceUtility implements Serializable, RemoteAccess
{
	private static final Logger logger = LogR.getLogger("com.hbi.wc.utility.HBIColorReplaceUtility");
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
    private static RemoteMethodServer remoteMethodServer; 
    private static String floderPhysicalLocation = "";
	private static String colorDataFileName = "ColorReplaceDataFile.xls";
	
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
			logger.debug("Exception in static block of the class HBIColorReplaceUtility is : "+ exp);
		}
	}
    
    /** Default executable function of the class HBIColorReplaceUtility */
    public static void main(String[] args) 
    {
        logger.debug("### START HBIColorReplaceUtility.main() ###");
        
       try 
        {
          //  MethodContext mcontext = new MethodContext((String) null, (Object) null);
           // SessionContext sessioncontext = SessionContext.newContext();

            remoteMethodServer = RemoteMethodServer.getDefault();
            remoteMethodServer.setUserName(args[0]);
            remoteMethodServer.setPassword(args[1]);
       
  
			//GatewayAuthenticator authenticator = new GatewayAuthenticator();
			//authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			//remoteMethodServer.setAuthenticator(authenticator);
			
			
            Class<?>[]argTypes={String.class};
            Object[]args1={colorDataFileName};
            remoteMethodServer.invoke("validateAndUpdateForColorReplace",HBIColorReplaceUtility.class.getName(),null,argTypes,args1);
			
           
        }
        catch(Exception exp) 
        {
        	exp.printStackTrace();
            System.exit(1);
        }

        logger.debug("### END HBIColorReplaceUtility.main() ###");
    }
    
    /**
     * This function is using as a plug-in function which is registered on LCSLifecycleManaged PRE_PERSIST EVENT to validate the business object type, invoke color reference change utility
     * @param wtObj - WTObject
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws SQLException
     */
    public static void updateColorReferencesForReplace(WTObject wtObj) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	logger.debug("### START HBIColorReplaceUtility.updateColorReferencesForReplace(WTObject wtObj) ###");
    	
    	if(wtObj instanceof LCSLifecycleManaged)
		{
    		LCSLifecycleManaged businessObject = (LCSLifecycleManaged) wtObj;
    		
    		//Validating the given business object type path, based on the type path validations invoking color replace utility which will update color references to set new color object 
    		if("Business Object\\Reports".equals(businessObject.getFlexType().getFullName(true)))
    		{
    			if("Test Color".equalsIgnoreCase((String) businessObject.getName()))
    			{
    				validateAndUpdateForColorReplace(colorDataFileName);
    			}
    		}
		}
    
    	logger.debug("### END HBIColorReplaceUtility.updateColorReferencesForReplace(WTObject wtObj) ###");
    }
    
    /**
     * This function is using to read color data file from the source directory, get first work sheet from the given XLS and invoking internal function to read given data and format color
     * @param colorDataFileName - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SQLException
     */
    public static void validateAndUpdateForColorReplace(String colorDataFileName) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.validateAndUpdateForColorReplace(String colorDataFileName) ###");
		FileInputStream fileInputStreamObj = null;
		
		try
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+colorDataFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);

			new HBIColorReplaceUtility().validateAndUpdateForColorReplace(worksheet);
		}
		finally
    	{
    		if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.validateAndUpdateForColorReplace(String colorDataFileName) ###");
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
    public void validateAndUpdateForColorReplace(HSSFSheet worksheet) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.validateAndUpdateForColorReplace(HSSFSheet worksheet) ###");
    	HSSFRow row = null;
		int sourceColorSequenceNo = 0;
		int targetColorSequenceNo = 0;
		LCSColor sourceColorObj = null;
		LCSColor targetColorObj = null;
		String sourceColorServiceName = "";
		int targetColorServiceNo = 0;
		String targetColorServiceName = "";
		
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
			
	    	sourceColorSequenceNo = (int) row.getCell((short) 0).getNumericCellValue();
			targetColorSequenceNo = (int) row.getCell((short) 1).getNumericCellValue();
			
			if(row.getCell(2) != null)
			{
				sourceColorServiceName = row.getCell(2).getStringCellValue();
			}	
				
			if(row.getCell(3) != null)
			{
				targetColorServiceNo = (int) row.getCell((short) 3).getNumericCellValue();
			}
				
			if(row.getCell(4) != null)
			{
				targetColorServiceName = row.getCell(4).getStringCellValue();
			}
			
			//Calling internal function to get color object for the given unique parameters, using these two colors (source color and target color) to invoke color replace function 
			sourceColorObj = new HBIColorReplaceUtility().getColorObjectForCriteria(sourceColorSequenceNo, 0, sourceColorServiceName);
	    	targetColorObj = new HBIColorReplaceUtility().getColorObjectForCriteria(targetColorSequenceNo, targetColorServiceNo, targetColorServiceName);
	    	
	    	validateAndUpdateColorReferences(sourceColorObj, targetColorObj);
		}

    	// LCSLog.debug("### END HBIColorReplaceUtility.validateAndUpdateForColorReplace(HSSFSheet worksheet) ###");
    }
    
    /**
     * This function is using to initialize color object from the given object unique parameters (color sequence number), get references for given color object, invoke functions to replace
     * @param sourceColorSequenceNo - int
     * @param colorServiceNo - int
     * @param colorServiceName - String
     * @param targetColorSequenceNo - int
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws SQLException
     */
    public void validateAndUpdateColorReferences(LCSColor sourceColorObj, LCSColor targetColorObj) throws WTException, WTPropertyVetoException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.validateAndUpdateColorReferences(LCSColor sourceColorObj, LCSColor targetColorObj) ###");
    	HBIColorReplaceUtility replaceUtilityObj = new HBIColorReplaceUtility();
    	String className = "";
    	Collection<FlexObject> colorReferencesColl = null;
    	
    	//Validating Source Color Object and Target Color Object, based on validation status calling out of the box function to get all references for the given color object.
    	if(sourceColorObj != null && targetColorObj != null && sourceColorObj != targetColorObj)
    	{
    		colorReferencesColl = (Collection<FlexObject>) (new FAWhereUsedQuery()).checkForObjectReferences(sourceColorObj);
    	}
    	
    	//Validate references collection, based on validation status iterate through each FlexObject, get object class name and invoke internal functions for object specific logic
    	if(colorReferencesColl != null && colorReferencesColl.size() > 0)
		{
			for(FlexObject flexObj : colorReferencesColl)
			{
				className = flexObj.getString("CLASS");
				replaceUtilityObj.updateFlexObjectForColorReference(sourceColorObj, targetColorObj, className, flexObj);
			}
		}
    	
    	//Calling function to update palette object to add color to palette and remove color from palette for the given color objects (get palette object from the given color)
    	validateAndUpdatePaletteForColorReplace(sourceColorObj, targetColorObj, "ADD");
    	validateAndUpdatePaletteForColorReplace(sourceColorObj, targetColorObj, "REMOVE");
    	
    	//Calling a function to update MaterialColor object to replace color (from linked color to target color), this should take care of replacing color reference from all samples
    	updateMaterialColorForColorReplace(sourceColorObj, targetColorObj);
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.validateAndUpdateColorReferences(LCSColor sourceColorObj, LCSColor targetColorObj) ###");
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////	Following functions are related to reference objects (where color object is using as object reference) to replace color		///////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is using to validate the given class name and invoke object specific functions to validate and replace color reference from source color object to target color object
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @param className - String
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void updateFlexObjectForColorReference(LCSColor sourceColorObj, LCSColor targetColorObj, String className, FlexObject flexObj) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateFlexObjectForColorReference(LCSColor sourceColorObj, LCSColor targetColorObj, String className, flexObj) ###");
    	
    	//Validate the reference object className, based on the type of the object invoke internal function which is developed to clear existing reference and set new reference
    	if("com.lcs.wc.color.LCSColor".equalsIgnoreCase(className))
		{
			LCSColor refColorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+flexObj.getString("LCSCOLOR.IDA2A2"));
			if(refColorObj != null)
			{
				updateReferenceColorForColorReplace(refColorObj, targetColorObj);
			}
		}
    	else if("com.lcs.wc.product.LCSSKU".equalsIgnoreCase(className))
		{
    		LCSSKU refSKUObj = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+flexObj.getString("LCSSKU.BRANCHIDITERATIONINFO"));
    		if(refSKUObj != null)
			{
    			refSKUObj = (LCSSKU) VersionHelper.latestIterationOf(refSKUObj);
    			updateReferenceSKUForColorReplace(refSKUObj, targetColorObj);
			}
		}
    	else if("com.lcs.wc.moa.LCSMOAObject".equalsIgnoreCase(className))
		{
    		LCSMOAObject refMOAObject = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+flexObj.getString("LCSMOAOBJECT.IDA2A2"));
    		if(refMOAObject != null)
			{
    			updateReferenceMOAObjectForColorReplace(refMOAObject, targetColorObj);
			}
		}
    	else if("com.lcs.wc.material.LCSMaterial".equalsIgnoreCase(className))
    	{
    		LCSMaterial refMaterialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
    		refMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(refMaterialObj);
    		if(refMaterialObj != null && !(PersistenceHelper.isEquivalent(refMaterialObj.getMaster(), LCSMaterialQuery.PLACEHOLDER)))
    		{
    			updateReferenceMaterialForColorReplace(refMaterialObj, sourceColorObj, targetColorObj);
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateFlexObjectForColorReference(LCSColor sourceColorObj, LCSColor targetColorObj, String className, flexObj) ###");
    }
    
    /**
     * This function is using to validate the given reference object (type path) and update to change attribute object reference from existing color reference to the given color reference
     * @param refColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void updateReferenceColorForColorReplace(LCSColor refColorObj, LCSColor targetColorObj) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateReferenceColorForColorReplace(LCSColor refColorObj, LCSColor targetColorObj) ###");
    	
    	//Validating the reference color type and updating to replace color reference only for Prints and Patterns type as 'Ground Color' attribute created in a specific type
    	if("Color\\Prints and Patterns".equals(refColorObj.getFlexType().getFullName(true)))
    	{
    		refColorObj.setValue("hbiGroundColor", targetColorObj);
    		LCSColorLogic.persist(refColorObj, false);
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateReferenceColorForColorReplace(LCSColor refColorObj, LCSColor targetColorObj) ###");
    }
    
    /**
     * This function is using to update reference colorway object to change 'Color' reference from existing object to the given color object and persisting color object to reflect changes
     * @param refSKUObj - LCSSKU
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void updateReferenceSKUForColorReplace(LCSSKU refSKUObj, LCSColor targetColorObj) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateReferenceSKUForColorReplace(LCSSKU refSKUObj, LCSColor targetColorObj) ###");
    	
    	//Updating reference colorway object to change 'Color' reference from existing object to the given color object and persisting color object to reflect references changes  
    	refSKUObj.setValue("color", targetColorObj);
		LCSProductHelper.service.saveSKU(refSKUObj);
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateReferenceSKUForColorReplace(LCSSKU refSKUObj, LCSColor targetColorObj) ###");
    }
    
    /**
     * This function is using to get owner object from the given MOAObject, validate owner and downcast to specific object, update MOA to replace existing references with the given object
     * @param refMOAObject - LCSMOAObject
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void updateReferenceMOAObjectForColorReplace(LCSMOAObject refMOAObject, LCSColor targetColorObj) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateReferenceMOAObjectForColorReplace(LCSMOAObject refMOAObject, LCSColor targetColorObj) ###");
    	WTObject ownerMaster = refMOAObject.getOwner();
    	
    	//Validate the owner master and downcast to specific object, from specific object (color object) get type path, validate owner object type path with the pre-defined path
    	if(ownerMaster instanceof LCSColor)
    	{
    		LCSColor ownerColorObj = (LCSColor) ownerMaster;
    		
    		//For Complex colors, Pitch Sheet is the MOA which has two columns ('color' object reference to color library and 'comments' of text area), update MOA for new reference 
    		String colorFlexTypePath = ownerColorObj.getFlexType().getFullName(true);
    		if(!"Color\\Colorway".equals(colorFlexTypePath) && !"Color\\Solid".equals(colorFlexTypePath))
    		{
    			refMOAObject.getFlexType().getAttribute("color").setValue(refMOAObject, targetColorObj);
    			LCSMOAObjectLogic.persist(refMOAObject);
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateReferenceMOAObjectForColorReplace(LCSMOAObject refMOAObject, LCSColor targetColorObj) ###");
    }
    
    /**
     * This function is using to validate the given material type path and update the material to change attributes object reference from existing color reference to given color reference
     * @param refMaterialObj - LCSMaterial
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void updateReferenceMaterialForColorReplace(LCSMaterial refMaterialObj, LCSColor sourceColorObj, LCSColor targetColorObj) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateReferenceMaterialForColorReplace(LCSMaterial refMaterialObj, LCSColor targetColorObj) ###");
    	String materialFlexTypePath = refMaterialObj.getFlexType().getFullName(true);
    	
    	//validate type path of the given reference material object, based on the type of the material (like Accessories, Elastics) execute specific code for color reference change
    	if("Material\\Accessories".equals(materialFlexTypePath))
    	{
    		refMaterialObj.setValue("hbiThreadMatchColor", targetColorObj);
    		LCSMaterialHelper.service.saveMaterial(refMaterialObj);
    	}
    	else if("Material\\Dye Formula".equals(materialFlexTypePath))
    	{
    		refMaterialObj.setValue("hbiColorStandardRef", targetColorObj);
    		LCSMaterialHelper.service.saveMaterial(refMaterialObj);
    	}
    	else if("Material\\Elastics".equals(materialFlexTypePath))
    	{
    		refMaterialObj.setValue("hbiThreadMatchColor", targetColorObj);
    		LCSMaterialHelper.service.saveMaterial(refMaterialObj);
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateReferenceMaterialForColorReplace(LCSMaterial refMaterialObj, LCSColor targetColorObj) ###");
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////		Following functions are related Palette object - adding color to palette or removing color from palette object		///////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is using to iterate through each palette, validate the given actionType(add color to palette or remove color from palette), call existing functions execute actionType 
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @param actionType - String
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    @SuppressWarnings("unchecked")
    public void validateAndUpdatePaletteForColorReplace_NOT_USED(LCSColor sourceColorObj, LCSColor targetColorObj, String actionType) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.validateAndUpdatePaletteForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj, String actionType) ###");
    	Collection<LCSPalette> paletteCollection = new LCSColorQuery().findPalettesForColor(sourceColorObj);
    	LCSPaletteLogic paletteLogic = new LCSPaletteLogic();
    	
    	//Iterating through palette collection, validating the given actionType (ADD or REMOVE color within palette context) and call out of the box function to perform actions
    	for(LCSPalette paletteObj : paletteCollection)
    	{
    		if("ADD".equalsIgnoreCase(actionType) && !paletteLogic.hasSubpalette(paletteObj))
    		{
    			paletteLogic.addColorToPalette(targetColorObj, paletteObj);
    		}
    		else if("REMOVE".equalsIgnoreCase(actionType))
    		{
    			paletteLogic.removeColorFromPalette(sourceColorObj, paletteObj);
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.validateAndUpdatePaletteForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj, String actionType) ###");
    }
    
    /**
     * This function is using to iterate through each palette, validate the given actionType(add color to palette or remove color from palette) for the given source color and target color 
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @param actionType - String
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    @SuppressWarnings("unchecked")
    public void validateAndUpdatePaletteForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj, String actionType) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.validateAndUpdatePaletteForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj, String actionType) ###");
    	Collection<LCSPalette> paletteCollection = new LCSColorQuery().findPalettesForColor(sourceColorObj);
    	Map<String, LCSPalette> paletteDataColl = getFilteredPaletteDataCollection(paletteCollection);
    	LCSPaletteLogic paletteLogic = new LCSPaletteLogic();
    	LCSPalette parentPaletteObj = null;
    	
    	//Iterating through palette collection, validating the given actionType (ADD or REMOVE color within palette context) and call out of the box functions to perform actions
    	for(LCSPalette paletteObj : paletteDataColl.values())
    	{
    		parentPaletteObj = paletteObj.getParentPalette();
    		
    		//adding color from sub palette will not take care of adding color from master/parent palette hence adding color to sub palette as well as in master/parent palette
    		if("ADD".equalsIgnoreCase(actionType))
    		{
    			paletteLogic.addColorToPalette(targetColorObj, paletteObj);
    			if(parentPaletteObj != null)
    			{
    				paletteLogic.addColorToPalette(targetColorObj, parentPaletteObj);
    			}
    		}
    		//removing color from sub palette will not take care of removing color from master/parent palette hence removing color from sub palette as well as from parent palette
    		else if("REMOVE".equalsIgnoreCase(actionType))
    		{
    			paletteLogic.removeColorFromPalette(sourceColorObj, paletteObj);
    			if(parentPaletteObj != null)
    			{
    				paletteLogic.removeColorFromPalette(sourceColorObj, parentPaletteObj);
    			}
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.validateAndUpdatePaletteForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj, String actionType) ###");
    }
    
    /**
     * This function is using to iterate through palette collection, initialize object identifier from each palette object, update palette data collection with the object unique parameter 
     * @param paletteCollection - Collection<LCSPalette>
     * @return paletteDataColl - Map<String, LCSPalette>
     * @throws WTException
     */
    private Map<String, LCSPalette> getFilteredPaletteDataCollection(Collection<LCSPalette> paletteCollection) throws WTException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.getFilteredPaletteDataCollection(Collection<LCSPalette> paletteCollection) ###");
    	Map<String, LCSPalette> paletteDataColl = new HashMap<String, LCSPalette>();
    	String paletteIDA2A2 = "";
    	String parentPaletteIDA2A2 = "";
    	LCSPalette parentPaletteObj = null;
    	
    	if(paletteCollection != null && paletteCollection.size() > 0)
    	{
    		//iterating through palette collection to get each palette object, initialize object identifier (IDA2A2) from each palette object, add palette IDA2A2 to palette data map
    		for(LCSPalette paletteObj : paletteCollection)
    		{
    			paletteIDA2A2 = FormatHelper.getNumericObjectIdFromObject(paletteObj);
    			paletteDataColl.put(paletteIDA2A2, paletteObj);
    		}
    		
    		//iterating through palette collection to get palette object, get parent palette object using getParentPalette, get IDA2A2 from parent palette using to update palette map
    		for(LCSPalette paletteObj : paletteCollection)
    		{
    			parentPaletteObj = paletteObj.getParentPalette();
    			if(parentPaletteObj != null)
    			{
    				parentPaletteIDA2A2 = FormatHelper.getNumericObjectIdFromObject(parentPaletteObj);
    				paletteDataColl.remove(parentPaletteIDA2A2);
    			}
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.getFilteredPaletteDataCollection(Collection<LCSPalette> paletteCollection) ###");
    	return paletteDataColl;
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////			Following functions are using to update MaterialColor object using Query API for color replace			///////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is using to get all material-colors from the given source color object, iterate through each material-color to validate the associated objects and replace color object
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void updateMaterialColorForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj) throws WTException, WTPropertyVetoException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateMaterialColorForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj) ###");
    	LCSMaterialColor materialColorObj = null;
    	LCSMaterialMaster materialMasterObj = null;
    	LCSColor linkedColorObj = null;
    	
    	//Get all Material-Colors from the given color object, iterate through each material-color, using this material-color object to validate associated samples and sample requests
    	SearchResults results = new LCSColorQuery().findMaterialsColorsForColor(sourceColorObj);
    	if(results != null && results.getResultsFound() > 0)
    	{
    		Collection<FlexObject> materialColorColl = results.getResults();
    		for(FlexObject flexObj : materialColorColl)
    		{
    			materialColorObj = (LCSMaterialColor) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:"+flexObj.getString("LCSMATERIALCOLOR.IDA2A2"));
    			materialMasterObj = materialColorObj.getMaterialMaster();
    			linkedColorObj = materialColorObj.getColor();
    			
    			//Calling a function which is using to initialize the query parameters (like material master id, color id), prepare query to update existing material-color for replace
    			updateMaterialColorForColorReplace(materialMasterObj, linkedColorObj, targetColorObj);
    			
    			//Calling a function which is using to get all samples for the given material-color, iterate through each sample, get linked color to validate and change request name
    			updateSampleForColorReplace(materialColorObj, sourceColorObj, targetColorObj);
    			
    			//Calling a function which is using to initialize material from material-color, get all FlexBOMLinks using whereUsed material, update each FlexBOMLink for color replace 
    			updateFlexBOMLinkForColorReplace(materialColorObj, sourceColorObj, targetColorObj);
    		}
    	}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateMaterialColorForColorReplace(LCSColor sourceColorObj, LCSColor targetColorObj) ###");
    }
    
    /**
     * This function is using to prepare script and execute using Query(auto connection handler) for updating MaterialColor record to replace linked color object to the given color object
     * @param materialMasterObj - WTPartMaster
     * @param linkedColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws SQLException
     */
    private void updateMaterialColorForColorReplace(LCSMaterialMaster materialMasterObj, LCSColor linkedColorObj, LCSColor targetColorObj) throws WTException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateMaterialColorForColorReplace(WTPartMaster materialMasterObj, LCSColor linkedColorObj, LCSColor targetColorObj) ###");
    	String materialMasterRefID = FormatHelper.getNumericObjectIdFromObject(materialMasterObj);
    	String linkedColorRefID = FormatHelper.getNumericObjectIdFromObject(linkedColorObj);
    	String targetColorRefID = FormatHelper.getNumericObjectIdFromObject(targetColorObj);
    	
    	//Preparing SQL Query for updating MaterialColor record to replace linked color object from existing color to the given color and committing the updated material-color object
    	String colorReplaceQuery = "UPDATE LCSMATERIALCOLOR SET IDA3B10="+targetColorRefID+" WHERE IDA3A10="+materialMasterRefID+" AND IDA3B10="+linkedColorRefID;
    	
    	//Initializing Query object(which internally take care of Database Connection and managing connection pool), using query instance to update material-color for color replace
    	Query query = new Query();    
		query.prepareForQuery();
    	query.runUpdate(colorReplaceQuery);
    	query.commit();
    	query.cleanUpQuery();
		
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateMaterialColorForColorReplace(WTPartMaster materialMasterObj, LCSColor linkedColorObj, LCSColor targetColorObj) ###");
    }
    
    /**
     * This function is using to get all samples for the given material-color, iterate through each sample object to initialize linked color object, invoke function for sample name change
     * @param materialColorObj - LCSMaterialColor
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    @SuppressWarnings("unchecked")
	public void updateSampleForColorReplace(LCSMaterialColor materialColorObj, LCSColor sourceColorObj, LCSColor targetColorObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIColorReplaceUtility.updateSampleForColorReplace(LCSMaterialColor materialColorObj, LCSColor linkedColorObj, LCSColor targetColorObj) ###");
		LCSSample sampleObj = null;
		LCSColor linkedColorObj = null;
		
		//Get all samples (sample collection) for the given Material-Color, iterate through each sample object to initialize linked color object using for sample request name change
		SearchResults results = LCSMaterialColorQuery.findSamples(materialColorObj);
		if(results != null && results.getResultsFound() > 0)
		{
			Collection<FlexObject> sampleCollection = results.getResults();
			for(FlexObject flexObj : sampleCollection)
			{
				sampleObj = (LCSSample) LCSQuery.findObjectById("OR:com.lcs.wc.sample.LCSSample:"+flexObj.getString("LCSSAMPLE.IDA2A2"));
				linkedColorObj = ((LCSMaterialColor)sampleObj.getColor()).getColor();
				
				//Validate two color object (checking two color objects are same or not, if both the color objects are same invoking internal function for sample request name change
				if(linkedColorObj != null && PersistenceHelper.isEquivalent(linkedColorObj, targetColorObj))
				{
					HBISampleHelper.updateSampleRequestName(sampleObj);
				}
			}
		}

		// LCSLog.debug("### END HBIColorReplaceUtility.updateSampleForColorReplace(LCSMaterialColor materialColorObj, LCSColor linkedColorObj, LCSColor targetColorObj) ###");
	}
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////		Following functions are related to FlexBOMLink - find color reference FlexBOMLink and update for color replace		///////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is using to initialize material from the given material-color, get all FlexBOMLinks using whereUsed material object, iterate through each FlexBOMLink for color replace
     * @param materialColorObj - LCSMaterialColor
     * @param sourceColorObj - LCSColor
     * @param targetColorObj - LCSColor
     * @throws WTException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void updateFlexBOMLinkForColorReplace(LCSMaterialColor materialColorObj, LCSColor sourceColorObj, LCSColor targetColorObj) throws WTException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateFlexBOMLinkForColorReplace(LCSMaterialColor materialColorObj, LCSColor linkedColorObj, LCSColor targetColorObj) ###");
    	LCSMaterial materialObj = (LCSMaterial) VersionHelper.latestIterationOf(materialColorObj.getMaterialMaster());
    	int sourceColorIDA2A2 = Integer.parseInt(FormatHelper.getNumericObjectIdFromObject(sourceColorObj));
    	int targetColorIDA2A2 = Integer.parseInt(FormatHelper.getNumericObjectIdFromObject(targetColorObj));
    	int linkedColorIDA2A2 = 0;
    	int flexBOMLinkIDA2A2 = 0;
    	
    	//Get all FlexBOMLinks for the given material (whereUsed data), iterate through each FlexBOMLink to initialize the required parameters using to update FlexBOMLink for replace

    	Collection<FlexObject> flexBOMLinkColl = LCSFlexBOMQuery.findWhereUsedData(materialObj);
    	for(FlexObject flexObj : flexBOMLinkColl)
    	{
    		flexBOMLinkIDA2A2 = flexObj.getInt("FLEXBOMLINK.IDA2A2");
    		linkedColorIDA2A2 = flexObj.getInt("FLEXBOMLINK.IDA3D5");

    		//Calling a function which is using to validate and update FlexBOMLink object for color replace from linked color object to the given color object, commit the changes
    		updateFlexBOMLinkForColorReplace(sourceColorIDA2A2, targetColorIDA2A2, flexBOMLinkIDA2A2, linkedColorIDA2A2);
    	}
    	
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateFlexBOMLinkForColorReplace(LCSMaterialColor materialColorObj, LCSColor linkedColorObj, LCSColor targetColorObj) ###");
    }
    
    /**
     * This function is using to prepare script and execute using Query(auto connection handler) for updating FlexBOMLink record to replace from linked color object to a given color object
     * @param sourceColorIDA2A2 - int
     * @param targetColorIDA2A2 - int
     * @param flexBOMLinkIDA2A2 - int
     * @param linkedColorIDA2A2 - int
     * @throws WTException
     * @throws SQLException
     */
    private void updateFlexBOMLinkForColorReplace(int sourceColorIDA2A2, int targetColorIDA2A2, int flexBOMLinkIDA2A2, int linkedColorIDA2A2) throws WTException, SQLException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.updateFlexBOMLinkForColorReplace(sourceColorIDA2A2, targetColorIDA2A2, flexBOMLinkIDA2A2, linkedColorIDA2A2) ###");
    	
    	//Compare two color objects (source color object and whereUsed of the source color object within FlexBOMLink, based on validation updating FlexBOMLink for color replace
    	if(sourceColorIDA2A2 == linkedColorIDA2A2)
		{
    		//Preparing SQL Query for updating FlexBOMLink record to replace linked color object from existing color to the given color and committing the updated FlexBOMLink object
        	String colorReplaceQuery = "UPDATE FLEXBOMLINK SET IDA3D5="+targetColorIDA2A2+" WHERE IDA3D5="+sourceColorIDA2A2+" AND IDA2A2="+flexBOMLinkIDA2A2;
        	
        	//Initializing Query object(which internally take care of Database Connection and managing connection pool), using query instance to update FlexBOMLink for color replace
        	Query query = new Query();    
    		query.prepareForQuery();
        	query.runUpdate(colorReplaceQuery);
        	query.commit();
        	query.cleanUpQuery();
		}
    	
    	// LCSLog.debug("### END HBIColorReplaceUtility.updateFlexBOMLinkForColorReplace(sourceColorIDA2A2, targetColorIDA2A2, flexBOMLinkIDA2A2, linkedColorIDA2A2) ###");
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////		Following functions are related to Color Object Query using PreparedQueryStatement for the given unique parameters		///////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This function is using to get color object using PreparedQueryStatement for the given criteria's ('Color Sequence', 'Color Service No' and Color Service Name) and return from header
     * @param colorSequnceNo - int
     * @param colorServiceNo - int
     * @param colorServiceName - String
     * @return colorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public LCSColor getColorObjectForCriteria(int colorSequenceNo, int colorServiceNo, String colorServiceName) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.getColorObjectForCriteria(int colorSequenceNo, int colorServiceNo, String colorServiceName) ###");
    	FlexType colorFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Color");
    	String colorSequenceDBColumn = colorFlexTypeObj.getAttribute("hbiColorSequence").getColumnDescriptorName();
		String colorServiceNoDBColumn = colorFlexTypeObj.getAttribute("hbiColorServiceRef").getColumnDescriptorName();
		String colorServiceNameDBColumn = colorFlexTypeObj.getAttribute("hbiColorServiceName").getColumnDescriptorName();
		LCSColor colorObj = null;
		
    	//Initializing the PreparedQueryStatement, which is using to get LCSColor object based on the given set of parameters(like FlexTypePath of the object data and unique parameters)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(LCSColor.class);
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, colorSequenceDBColumn), "?", "="), new Long(colorSequenceNo));
    	
    	//'Color Service No' validate the given color service number, based on the validation status (only if the given data is valid) append 'Color Service No' as required criteria 
    	if(colorServiceNo != 0)
    	{
    		statement.appendAndIfNeeded();
    		statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, colorServiceNoDBColumn), "?", "="), new Long(colorServiceNo));
    	}
    	
    	//'Color Service Name' validate the given color service name, based on the validation status (only if the given data is valid) append 'Color Service Name' as required criteria
		if(FormatHelper.hasContent(colorServiceName))
		{
			statement.appendAndIfNeeded();
			statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, colorServiceNameDBColumn), colorServiceName, Criteria.EQUALS));
		}	

    	//Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSColor instance/object and returning the Collection of LCSColor objects
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
        	FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
        	colorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+flexObj.getString("LCSColor.IDA2A2"));
        }
        
        // LCSLog.debug("### END HBIColorReplaceUtility.getColorObjectForCriteria(int colorSequenceNo, int colorServiceNo, String colorServiceName) ###");
    	return colorObj;
    }
}
package com.hbi.wc.interfaces.inbound.aps.bom.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIAPSProductBOMQuery;
import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIGarmentProductQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIAPSProductBOMProcessor.java
 *
 * This class contains specific and generic functions which are using to read the given data like 'Garment Product Name' and 'Manufacturing Style Code' from a given data file, validate the 
 * 'Manufacturing Style' status in APS, invoking internal functions to sync Product BOM(creating new FlexBOMPart, creating new FlexBOMLink, updating an existing FlexBOMPart and FlexBOMLink
 * @author Abdul.Patel@Hanes.com
 * @since November-24-2017
 */
public class HBIAPSProductBOMProcessor implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
	private static RemoteMethodServer remoteMethodServer;
	private static String floderPhysicalLocation = "";
	private static String productBOMDataFileName = "APSFlexPLMBOMLoadDataFile.xls";
	private static String productBOMDataLoadStatus = "APSFlexPLMBOMLoadDataFile_Sys.xls";
	
	private static String manufacturingStyleDelim = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.processor.HBIAPSProductBOMProcessor.manufacturingStyleDelim", ",");
	
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
			LCSLog.debug("Exception in static block of the class HBIAPSProductBOMProcessor is : "+ exp);
		}
	}
	
	/* Default executable function of the class HBIAPSProductBOMProcessor */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIAPSProductBOMProcessor.main() ###");
		
		try
		{
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer = RemoteMethodServer.getDefault();
	        remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
	        //validateAndSyncProductBOMData(productBOMDataFileName);
	        
	        //This block of code is using to initialize RemoteMethodServer call parameters (RemoteMethodServer call argument types and argument values) and invoking RemoteMethodServer
			Class<?> argTypes[] = {String.class};
			String argValues[] = {productBOMDataFileName};
			remoteMethodServer.invoke("validateAndSyncProductBOMData", "com.hbi.wc.interfaces.inbound.aps.bom.processor.HBIAPSProductBOMProcessor", null, argTypes, argValues);
	        System.exit(0);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIAPSProductBOMProcessor.main() ###");
	}
	
	/**
	 * This function is using to read data file from a pre-defined location, get first sheet from the given excel workbook and passing excel sheet to an internal function to retrieve data
	 * @param productBOMDataFileName - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void validateAndSyncProductBOMData(String productBOMDataFileName) throws WTException, WTPropertyVetoException, SQLException, IOException
	{
		// LCSLog.debug("### START HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(String productBOMDataFileName) ###");
		FileInputStream fileInputStreamObj = null;
		FileOutputStream fileOutputStreamObj = null;
		
		try
		{
			//Reading data file from a pre-defined location, get first sheet/tab from the given excel workbook, passing excel sheet to an internal function to read each columns data
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+productBOMDataFileName);
			fileOutputStreamObj = new FileOutputStream(floderPhysicalLocation+File.separator+productBOMDataLoadStatus);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			worksheet = new HBIAPSProductBOMProcessor().validateAndSyncProductBOMData(worksheet);
			workbook.write(fileOutputStreamObj);
		}
		catch (IOException ioExp)
		{
			LCSLog.debug("IOException in HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(String productBOMDataFileName) is "+ioExp);
			ioExp.printStackTrace();
		}
		finally
    	{
    		if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
    	}
		
		// LCSLog.debug("### END HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(String productBOMDataFileName) ###");
	}
	
	/**
	 * This function is using to iterate through each rows of the given excel sheet, initialize garmentProductName and manufacturingStyle variables, invoke internal functions for BOM Load
	 * @param worksheet - HSSFSheet
	 * @return worksheet - HSSFSheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public HSSFSheet validateAndSyncProductBOMData(HSSFSheet worksheet) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(HSSFSheet worksheet) ###");
		HSSFRow row = null;
		String garmentProductName = "";
		String manufacturingStyle = "";
		LCSProduct productObj = null;
		HBIGarmentProductQuery productQueryObj = new HBIGarmentProductQuery();
		
		//Iterating through each rows of the given excel, initialize garmentProductName and Manufacturing Style Code, invoke internal function to validate Style code, load BOM data to PLM 
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
		
			garmentProductName = row.getCell((short) 0).getStringCellValue();
			manufacturingStyle = row.getCell((short) 1).getStringCellValue();
			
			if(FormatHelper.hasContent(garmentProductName) && FormatHelper.hasContent(manufacturingStyle))
			{
				//Calling a function to get a garment product object for the given 'Product Name', using this product object as owner to create FlexBOMPart for each manufacturing style
				productObj = productQueryObj.getGarmentProductByName(garmentProductName);
				
				//Calling a function to validate the given product and manufacturing style and each manufacturing style status in APS STYLE table and invoking internal functions
				validateAndSyncProductBOMData(productObj, manufacturingStyle, row);
			}
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(HSSFSheet worksheet) ###");
		return worksheet;
	}
	
	/**
	 * This function is using to validate the given product object and populating error logs/comments within the given data file, based on the validation status invoking internal functions
	 * @param productObj - LCSProduct
	 * @param manufacturingStyle - String
	 * @param row - HSSFRow
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public void validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyle, HSSFRow row) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyle, HSSFRow row) ###");
		String manufacturingStyles[] = {manufacturingStyle};
		
		//Error handling mechanism - 'BOM Load Status in PLM' column value should be 'Load Fail' and 'BOM Load Error Report' column value should be Garment Product Does Not Exists in PLM
		if(productObj == null)
		{
			HSSFCell garmentProductStatusCell = row.createCell((short) 2);
			garmentProductStatusCell.setCellValue("Garment Product listed in column A does not exists in PLM");
			return;
		}
		
		//Validate the 'Manufacturing Style' and split the 'Manufacturing Style' using manufacturingStyleDelimiter (,), if the given 'Manufacturing Style' contains manufacturingStyleDelim
		if(manufacturingStyle.contains(manufacturingStyleDelim))
		{
			manufacturingStyles = manufacturingStyle.split(manufacturingStyleDelim);
		}
		
		//Calling a function to validate the given manufacturing styles, validate each manufacturing style status in APS STYLE table, based on validation status invoke internal functions
		validateAndSyncProductBOMData(productObj, manufacturingStyles, row);
		
		// LCSLog.debug("### END HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyle, HSSFRow row) ###");
	}
	
	/**
	 * This function is using to validate the given manufacturing style and each manufacturing style status in APS STYLE table, based on validation status invoking internal load functions
	 * @param productObj - LCSProduct
	 * @param manufacturingStyles - String
	 * @param row - HSSFRow
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public void validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyles[], HSSFRow row) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyles[], HSSFRow row) ###");
		boolean manufacturingStyleExistsInAPS = false;
		HBIAPSProductBOMQuery apsProductBOMQuery = new HBIAPSProductBOMQuery();
		String manufacturingStyleValidationStatus = "";
		String apsBOMValidationStatus = "";
		
		//Iterating through each 'Manufacturing Style', get manufacturing style code status from APS STYLE table, based on the manufacturing style status, invoke internal functions  
		for(String manufacturingStyleCode : manufacturingStyles)
		{
			manufacturingStyleCode = manufacturingStyleCode.trim();
			manufacturingStyleExistsInAPS = apsProductBOMQuery.getManufacturingStyleStatus(manufacturingStyleCode);
			if(manufacturingStyleExistsInAPS)
			{
				apsBOMValidationStatus = validateAndSyncProductBOMData(productObj, manufacturingStyleCode, apsProductBOMQuery, apsBOMValidationStatus);
			}
			else
			{
				manufacturingStyleValidationStatus = manufacturingStyleValidationStatus.concat(", ").concat("Manufacturing Style = "+ manufacturingStyleCode +" does not exists in APS");
			}
		}
		
		//Validating the 'manufacturing style status from APS', based on the validation status creating a cell within the given data file to add 'Manufacturing Style Validation Status'
		if(FormatHelper.hasContent(manufacturingStyleValidationStatus))
		{
			manufacturingStyleValidationStatus = manufacturingStyleValidationStatus.substring(1, manufacturingStyleValidationStatus.length()).trim();
			HSSFCell manufacturingStyleStatusCell = row.createCell((short) 3);
			manufacturingStyleStatusCell.setCellValue(manufacturingStyleValidationStatus);
		}
		
		//Validating the 'bill of materials status from APS', based on the validation status creating a cell within the given data file to add 'Bill of Materials Validation Status'
		if(FormatHelper.hasContent(apsBOMValidationStatus))
		{
			apsBOMValidationStatus = apsBOMValidationStatus.substring(1, apsBOMValidationStatus.length()).trim();
			HSSFCell apsBOMValidationStatusCell = row.createCell((short) 4);
			apsBOMValidationStatusCell.setCellValue(apsBOMValidationStatus);
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyles[], HSSFRow row) ###");
	}
	
	/**
	 * This function is using get a collection of bill of material id from MFG_PATH table, iterate through bill of material id collection, get bill of material data from APS BILL_OF_MTRLS
	 * @param productObj - LCSProduct
	 * @param manufacturingStyle - String
	 * @param apsProductBOMQuery - HBIAPSProductBOMQuery
	 * @param apsBOMValidationStatus - String
	 * @return apsBOMValidationStatus - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public String validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyle, HBIAPSProductBOMQuery apsProductBOMQuery, String apsBOMValidationStatus) throws WTException, WTPropertyVetoException, SQLException
	{
		// LCSLog.debug("### START HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyle, apsProductBOMQueryObj) ###");
		
		//Calling a function to get a collection of bill of material id from MFG_PATH table for the given 'Manufacturing Style' and returning a bill of material id collection
		Collection<String> billOfMaterialIdColl = apsProductBOMQuery.getBillOfMaterialIdCollection(manufacturingStyle);
		
		//Error handling mechanism - 'BOM Load Error Report' column value should be 'Manufacturing Style Code' does not have any active bill of material data in APS MFG_PATH, skip loading
		if(billOfMaterialIdColl.size() == 0)
		{
			apsBOMValidationStatus = apsBOMValidationStatus.concat(", ").concat("No Active BOM in MFG_PATH Where STYLE_CD = "+manufacturingStyle);
			return apsBOMValidationStatus;
		}
		
		//Iterating through bill of materialId collection, get bill of material data for the given 'Manufacturing Style' and 'Bill of Material ID', validate and load bill of materials
		for(String billOfMaterialName : billOfMaterialIdColl)
		{
			//Calling a function to get bill of materials data from APS BILL_OF_MTRLS table for the given 'Manufacturing Style' and 'Bill of Material ID', using this data for PLM Loading 
			apsProductBOMQuery.getManufacturingStyleBillOfMaterials(productObj, manufacturingStyle, billOfMaterialName);
		}
		
		// LCSLog.debug("### END HBIAPSProductBOMProcessor.validateAndSyncProductBOMData(LCSProduct productObj, String manufacturingStyle, apsProductBOMQueryObj) ###");
		return apsBOMValidationStatus;
	}
}
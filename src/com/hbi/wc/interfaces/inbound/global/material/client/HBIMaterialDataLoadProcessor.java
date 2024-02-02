package com.hbi.wc.interfaces.inbound.global.material.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialDataLoadProcessor.java
 *
 * This class contains stand alone function as well as soap client invocation points to read each material data file for each global material system (HAA, HEI 
 * and Champion Europe) and get into native format, material data sync processor to load all data files for each global systems and invoke system-cleanup util
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-14-2018
 */
public class HBIMaterialDataLoadProcessor implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.interface.inbound.gobla.material.client.CLIENT_ADMIN_USER_ID", "Administrator");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.interface.inbound.gobla.material.client.CLIENT_ADMIN_PASSWORD", "QAadmin");
    
	public static String materialDataFileLocation_HAA = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataLoadProcessor.haaMaterialDataFileLocation", "\\\\WSFLEXAPPPRD1V\\MaterialSyncData\\HAA\\");
	public static String materialDataFileLocation_HEI = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataLoadProcessor.heiMaterialDataFileLocation", "\\\\WSFLEXAPPPRD1V\\MaterialSyncData\\HEI\\");
	public static String materialDataFileLocation_CHEU = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataLoadProcessor.cheuMaterialDataFileLocation", "\\\\WSFLEXAPPPRD1V\\MaterialSyncData\\CHEU\\");
	
	private static String materialSourceType = "";
	
	/**
	 * Default executable method of the class HBIMaterialDataLoadProcessor, this function begin the execution to complete the global raw material load process.
	 * @param args - String[]
	 */
	public static void main(String[] args)
	{
		LCSLog.debug("### START HBIMaterialDataLoadProcessor.main() ###");
		
		try
		{
			RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
	        MethodContext mcontext = new MethodContext((String) null, (Object) null);
	        SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
			//calling a function which will invoke material data sync processor to load all data files for each global systems and invoke system-cleanup util's
			invokeMaterialDataSyncProcessor();
			System.exit(0);
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in HBIMaterialDataLoadProcessor.main() = "+ exp);
			exp.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialDataLoadProcessor.main() ###");
	}
	
	/**
	 * This function is the main function of HBIMaterialDataLoadProcessor, which will trigger a material data loading from each global material systems shared
	 * location to FlexPLM, this function also takes care of moving successfully loaded data files to a backup folder and sending email notification to user's
	 */
	public static void invokeMaterialDataSyncProcessor()
	{
		LCSLog.debug("### START HBIMaterialDataLoadProcessor.invokeMaterialDataSyncProcessor() ###");
		
		try
		{
			//Calling a function to validate and load HAA raw material data from excel data file to FlexPLM application through IIB MQ using SOAP Client message
			processHAADataLoadRequest(materialDataFileLocation_HAA);
			
			//Calling a function to validate and load HEI raw material data from excel data file to FlexPLM application through IIB MQ using SOAP Client message
			processHEIDataLoadRequest(materialDataFileLocation_HEI);
			
			//Calling function to validate and load CHEU raw material data from excel data file to FlexPLM application through IIB MQ using SOAP Client message
			processCHEUDataLoadRequest(materialDataFileLocation_CHEU);
			
			//Calling a function which is using as an invocation point to move the successfully processed data files from current location to a backup location
			HBIMaterialDataFileMoveUtility.startProcessedDataFileBackup();
			
			System.exit(0);
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in HBIMaterialDataLoadProcessor.invokeMaterialDataSyncProcessor() = "+ exp);
			exp.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialDataLoadProcessor.invokeMaterialDataSyncProcessor() ###");
	}
	
	/**
     * This function is using to validate and load HAA raw material data from excel data file to FlexPLM application through IIB MQ using SOAP Client message
     * @param haaMaterialDataFileLocation - String
     * @throws SOAPException
     * @throws IOException
     */
	public static void processHAADataLoadRequest(String haaMaterialDataFileLocation) throws SOAPException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.processHAADataLoadRequest(String haaMaterialDataFileLocation) ###");
		materialSourceType = "HAA";
		
		//Create File object using material data file location (shared location for each global material system), get all material data files from shared path  
		File materialDataFileLocation = new File(haaMaterialDataFileLocation);
		File materialDataFiles[] = materialDataFileLocation.listFiles();
		
		//Iterating through each material data files, validate the file type and invoke internal functions using to read each columns/attributes data values. 
		for(File materialDataFile : materialDataFiles)
		{
			if(materialDataFile.isFile())
			{
				Map<Integer, String> materialDataFileHeadersMap = new HBIMaterialDataFileUtil().getMaterialDataFileHeadersMap(materialDataFile);
				
				//This function is using to process Material Data Files from shared location and to write status on material data files provided by Material Authoring Systems.
				new HBIMaterialDataLoadProcessor().processMaterialDataLoadRequest(materialDataFileHeadersMap, materialDataFile);
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadProcessor.processHAADataLoadRequest(String haaMaterialDataFileLocation) ###");
	}
	
	/**
     * This function is using to validate and load HEI raw material data from excel data file to FlexPLM application through IIB MQ using SOAP Client message
     * @param haaMaterialDataFileLocation - String
     * @throws SOAPException
     * @throws IOException
     */
	public static void processHEIDataLoadRequest(String heiMaterialDataFileLocation) throws SOAPException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.processHEIDataLoadRequest(String heiMaterialDataFileLocation) ###");
		materialSourceType = "HEI";
		
		//Create File object using material data file location (shared location for each global material system), get all material data files from shared path
		File materialDataFileLocation = new File(heiMaterialDataFileLocation);
		File materialDataFiles[] = materialDataFileLocation.listFiles();
		
		//Iterating through each material data files, validate the file type and invoke internal functions using to read each columns/attributes data values.
		for(File materialDataFile : materialDataFiles)
		{
			if(materialDataFile.isFile())
			{
				Map<Integer, String> materialDataFileHeadersMap = new HBIMaterialDataFileUtil().getMaterialDataFileHeadersMap(materialDataFile);
				
				//This function is using to process Material Data Files from shared location and to write status on material data files provided by Material Authoring Systems.
				new HBIMaterialDataLoadProcessor().processMaterialDataLoadRequest(materialDataFileHeadersMap, materialDataFile);
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadProcessor.processHEIDataLoadRequest(String heiMaterialDataFileLocation) ###");
	}
	
	/**
     * * This function is using to validate and load CHEU raw material data from excel data file to FlexPLM application through IIBMQ using soap client message
     * @param haaMaterialDataFileLocation - String
     * @throws SOAPException
     * @throws IOException
     */
	public static void processCHEUDataLoadRequest(String chEUMaterialDataFileLocation) throws SOAPException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.processCHEUDataLoadRequest(String chEUMaterialDataFileLocation) ###");
		materialSourceType = "Champion Europe";
		
		//Create File object using material data file location (shared location for each global material system), get all material data files from shared path
		File materialDataFileLocation = new File(chEUMaterialDataFileLocation);
		File materialDataFiles[] = materialDataFileLocation.listFiles();
		
		//Iterating through each material data files, validate the file type and invoke internal functions using to read each columns/attributes data values.
		for(File materialDataFile : materialDataFiles)
		{
			if(materialDataFile.isFile())
			{
				Map<Integer, String> materialDataFileHeadersMap = new HBIMaterialDataFileUtil().getMaterialDataFileHeadersMap(materialDataFile);
				
				//This function is using to process Material Data Files from shared location and to write status on material data files provided by Material Authoring Systems.
				new HBIMaterialDataLoadProcessor().processMaterialDataLoadRequest(materialDataFileHeadersMap, materialDataFile);
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadProcessor.processCHEUDataLoadRequest(String chEUMaterialDataFileLocation) ###");
	}
	
	/**
     * This function is using to process Material data files from shared location, to write status on material data file provided by material authoring systems.
     * @param materialDataFileHeadersMap - Map<Integer, String>
     * @param materialDataFileObj - File
     * @throws SOAPException
     * @throws IOException
     */
	public void processMaterialDataLoadRequest(Map<Integer, String> materialDataFileHeadersMap, File materialDataFileObj) throws SOAPException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.processMaterialDataLoadRequest(materialDataFileHeadersMap, File materialDataFileObj) ###");
		FileInputStream fileInputStreamObj = null;
		FileOutputStream fileOutputStreamObj = null;
		//String sysModifiedDataFileName = materialDataFileObj.getPath().concat("_Sys.xls");
		
		try
		{
			//fileOutputStreamObj = new FileOutputStream(sysModifiedDataFileName);
			fileInputStreamObj = new FileInputStream(materialDataFileObj);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			//This function is using to iterate through each row from the Material Data Files from shared location and  convert each to native format.
			processMaterialDataLoadRequest(materialDataFileHeadersMap, worksheet);
			
			fileOutputStreamObj = new FileOutputStream(materialDataFileObj);
			workbook.write(fileOutputStreamObj);
		}
		finally
		{
			if(fileOutputStreamObj != null)
    		{
    			fileOutputStreamObj.flush();
    			fileOutputStreamObj.close();
    			fileOutputStreamObj = null;
    		}
			
			if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadProcessor.processMaterialDataLoadRequest(materialDataFileHeadersMap, File materialDataFileObj) ###");
	}
	
	/**
     * This function is using to iterate through each row from the material data file reading from a shared location and convert each fields into native format
     * @param materialDataFileHeadersMap - Map<Integer, String>
     * @param worksheet - HSSFSheet
     * @throws SOAPException
     * @throws IOException
     */
	public void processMaterialDataLoadRequest(Map<Integer, String> materialDataFileHeadersMap, HSSFSheet worksheet) throws SOAPException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.processMaterialDataLoadRequest(materialDataFileHeadersMap, HSSFSheet worksheet) ###");
		HBIMaterialDataFileUtil materialDataFileUtil = new HBIMaterialDataFileUtil();
		HBIMaterialSOAPMessageUtil materialSOAPMessageUtil = new HBIMaterialSOAPMessageUtil();
		HSSFRow row = null;
		Map<String, String> materialDataFileDataMap = null;
		String soapMessage = "";
		
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
		
			materialDataFileDataMap = materialDataFileUtil.getMaterialDataFileDataMap(row, materialDataFileHeadersMap, materialSourceType);
			soapMessage = materialSOAPMessageUtil.getRawMaterialSOAPMessage(materialDataFileDataMap);
			
			//Calling SOAP Client by passing SOAP message and getting response and validating the status.
			try
			{
				String serviceResponse = HBIMaterialDataLoadSOAPClient.validateAndLoadGlobalRawMaterials(soapMessage);
				boolean continueMaterialLoad = getMaterialLoadFeedbackStatus(row, serviceResponse);
				if(!continueMaterialLoad)
					break;
			}
			catch (Exception exp) {
				// TODO: handle exception
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataLoadProcessor.processMaterialDataLoadRequest(materialDataFileHeadersMap, HSSFSheet worksheet) ###");
	}
	
	/**
     * This function is using to writing the material data load feedback status on material data file provided by material authoring systems (HAA,HEi and CHEU)
     * @param row - HSSFRow
	 * @param serviceResponse - String
     * @throws IOException
     */
	private boolean getMaterialLoadFeedbackStatus(HSSFRow row, String serviceResponse) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.getMaterialLoadFeedbackStatus(HSSFRow row, String serviceResponse) ###");
		boolean continueMaterialLoad = false;
		
		if("Material Loaded Successfully".equalsIgnoreCase(serviceResponse))
		{
			continueMaterialLoad = true;
		}
		else if(!FormatHelper.hasContent(serviceResponse))
		{
			serviceResponse = "Material Load Failed in PLM, for detailed Exceptions refer FlexPLM logs folder";
		}
		
		//Creating new cell at end of the each row leaving one cloumns as blank before wrting the status.
		HSSFCell loadStatusCell = row.createCell(HBIMaterialDataFileUtil.materialLoadStatusColumnIndex);
		loadStatusCell.setCellValue(serviceResponse);
		
		// LCSLog.debug("### END HBIMaterialDataLoadProcessor.getMaterialLoadFeedbackStatus(HSSFRow row, String serviceResponse) ###");
		return continueMaterialLoad;
	}
}
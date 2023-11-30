package com.hbi.wc.interfaces.inbound.global.material.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import wt.util.WTException;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialDataFileMoveUtility.java
 *
 * This class contains stand alone function as well as generic function, using read all the successfully processed files from current location to a file backup
 * location, under backup folder we have created a system specific folders such as HAA, HEI and CHEU, these files are using as backup files for future reference
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-10-2018
 */
public class HBIMaterialDataFileMoveUtility
{
	private static String backupDataFileLocation_HAA = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataFileMoveUtility.backupDataFileLocation_HAA", "\\\\WSFLEXAPPPRD1V\\MaterialSyncData\\Backup_DataFiles\\HAA\\");
	private static String backupDataFileLocation_HEI = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataFileMoveUtility.backupDataFileLocation_HEI", "\\\\WSFLEXAPPPRD1V\\MaterialSyncData\\Backup_DataFiles\\HEI\\");
	private static String backupDataFileLocation_CHEU = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataFileMoveUtility.backupDataFileLocation_CHEU", "\\\\WSFLEXAPPPRD1V\\MaterialSyncData\\Backup_DataFiles\\CHEU\\");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyhhmm");
	
	/**
	 * Default executable method of the class HBIMaterialDataFileMoveUtility, this function begins the execution to complete successfully loaded data file move 
	 * @param args - String[]
	 */
	public static void main(String[] args)
	{
		LCSLog.debug("### START HBIMaterialDataFileMoveUtility.main() ###");
		
		try
		{
			//Calling function which will take care of moving the successfully processed data files from current location to a backup location for each systems
			startProcessedDataFileBackup();
			
			System.exit(0);
		}
		catch(Exception exp)
		{
			LCSLog.debug("Exception in HBIMaterialDataFileMoveUtility.main() = "+ exp);
			exp.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialDataFileMoveUtility.main() ###");
	}
	
	/**
	 * This function is using as an invocation point to move the successfully processed data files from current location to a backup location for each systems
	 * @throws IOException
	 */
	public static void startProcessedDataFileBackup() throws WTException, IOException
	{
		LCSLog.debug("### START HBIMaterialDataFileMoveUtility.startProcessedDataFileBackup() ###");
		HBIMaterialDataFileMoveUtility fileMoveUtilityObj = new HBIMaterialDataFileMoveUtility();
		
		//This function is to move the HAA data files - move all the processed data files from current location to the backup folder which is specific to HAA
		fileMoveUtilityObj.moveProcessedFilesToBackupFolder(HBIMaterialDataLoadProcessor.materialDataFileLocation_HAA, backupDataFileLocation_HAA, "HAA");
		
		//This function is to move the HEI data files - move all the processed data files from current location to the backup folder which is specific to HEI
		fileMoveUtilityObj.moveProcessedFilesToBackupFolder(HBIMaterialDataLoadProcessor.materialDataFileLocation_HEI, backupDataFileLocation_HEI, "HEI");
		
		//This function is to move the CHEU data files - move all the processed data files from current location to the backup folder which is specific to CHEU
		fileMoveUtilityObj.moveProcessedFilesToBackupFolder(HBIMaterialDataLoadProcessor.materialDataFileLocation_CHEU, backupDataFileLocation_CHEU, "CHEU");
		
		LCSLog.debug("### END HBIMaterialDataFileMoveUtility.startProcessedDataFileBackup() ###");
	}
	
	/**
	 * This function is using to move the successfully processed material data files from current location to a backup location created for each global systems 
	 * @param dataFileSourceLocation - String
	 * @param dataFileTargetLocation - String
	 * @throws IOException
	 */
	public void moveProcessedFilesToBackupFolder(String dataFileSourceLocation, String dataFileTargetLocation, String dataSourceName) throws WTException, IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileMoveUtility.moveProcessedFilesToBackupFolder(dataFileSourceLocation, dataFileTargetLocation) ###");
		File materialDataFiles[] = new File(dataFileSourceLocation).listFiles();
		String dataFileLoadStatus = "";
		int materialLoadStatusColumnIndex = 0;
		
		for(File materialDataFile : materialDataFiles)
		{
			if(materialDataFile.isFile())
			{
				materialLoadStatusColumnIndex = getMaterialLoadStatusColumnIndex(materialDataFile); 
				dataFileLoadStatus = getMaterialDataFileLoadStatus(materialDataFile, materialLoadStatusColumnIndex);
				
				if(FormatHelper.hasContent(dataFileLoadStatus) && "success".equalsIgnoreCase(dataFileLoadStatus))
				{
					//calling a function which is using to move materialDataFile from source location to dataFiles backup location, for maintaining data files
					moveDataFileToBackupFolder(materialDataFile, dataSourceName);
				}
				else if(FormatHelper.hasContent(dataFileLoadStatus) && "failed".equalsIgnoreCase(dataFileLoadStatus))
				{
					//calling a function which is using to send email notification to the respective team/member to take care of failure records in data-file
					new HBIMaterialDataLoadEmailNotification().sendLoadFailureEmailNotification(materialDataFile, dataSourceName);
				}
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataFileMoveUtility.moveProcessedFilesToBackupFolder(dataFileSourceLocation, dataFileTargetLocation) ###");
	}
	
	/**
	 * This function is using validate 'Material Load Status' from the given data file, based on the status, returning a boolean flag to the calling function
	 * @param materialDataFile - File
	 * @param materialLoadStatusColumnIndex - int
	 * @return dataFileLoadStatus - String
	 * @throws IOException
	 */
	public String getMaterialDataFileLoadStatus(File materialDataFile, int materialLoadStatusColumnIndex) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileMoveUtility.getMaterialDataFileLoadStatus(File materialDataFile, materialLoadStatusColumnIndex) ###");
		FileInputStream fileInputStreamObj = null;
		String dataFileLoadStatus = "";
		
		try
		{
			fileInputStreamObj = new FileInputStream(materialDataFile);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			//calling a function which is using to validate 'Material Load Status' from the given data file, based on the status validation return load status
			dataFileLoadStatus = getMaterialDataFileLoadStatus(worksheet, materialLoadStatusColumnIndex);
		}
		finally
		{
			if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
		}
		
		// LCSLog.debug("### END HBIMaterialDataFileMoveUtility.getMaterialDataFileLoadStatus(File materialDataFile, materialLoadStatusColumnIndex) ###");
		return dataFileLoadStatus;
	}
	
	/**
	 * This function is using to validate 'Material Load Status' from the given data file, based on the 'Material Load Status validation return status to call
	 * @param worksheet - HSSFSheet
	 * @param materialLoadStatusColumnIndex - int
	 * @return dataFileLoadStatus - String
	 * @throws IOException
	 */
	private String getMaterialDataFileLoadStatus(HSSFSheet worksheet, int materialLoadStatusColumnIndex) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileMoveUtility.getMaterialDataFileLoadStatus(HSSFSheet worksheet, materialLoadStatusColumnIndex) ###");
		String dataFileLoadStatus = "";
		String materialLoadStatus = "";
		HSSFRow row = null;
		
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
	
			//validate 'Material Load Status' from the given data file, based on the 'Material Load Status' validation, return a formatted Data Load status 
			if(row.getCell(materialLoadStatusColumnIndex) != null)
				materialLoadStatus = row.getCell(materialLoadStatusColumnIndex).getStringCellValue();
			
			if(FormatHelper.hasContent(materialLoadStatus) && "Material Loaded Successfully".equalsIgnoreCase(materialLoadStatus))
			{
				dataFileLoadStatus = "success";
			}
			else if(FormatHelper.hasContent(materialLoadStatus) && !"Material Loaded Successfully".equalsIgnoreCase(materialLoadStatus))
			{
				dataFileLoadStatus = "failed";
			}
		}
		
		// LCSLog.debug("### END HBIMaterialDataFileMoveUtility.getMaterialDataFileLoadStatus(HSSFSheet worksheet, materialLoadStatusColumnIndex) ###");
		return dataFileLoadStatus;
	}
	
	/**
	 * This function is using to get column index for a column called "Material Data Load Status in PLM", which is populated based on the PLM data load status
	 * @param materialDataFileObj - File
	 * @return materialLoadStatusColumnIndex - int
	 * @throws IOException
	 */
	public int getMaterialLoadStatusColumnIndex(File materialDataFileObj) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileMoveUtility.getMaterialLoadStatusColumnIndex(File materialDataFileObj) ###");
		int materialLoadStatusColumnIndex = 0;
		FileInputStream fileInputStreamObj = null;
		
		try
		{
			fileInputStreamObj = new FileInputStream(materialDataFileObj);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			HSSFRow row = worksheet.getRow(0);
			materialLoadStatusColumnIndex = row.getPhysicalNumberOfCells() + 1;
		}
		finally
		{
			if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
		}
		
		// LCSLog.debug("### END HBIMaterialDataFileMoveUtility.getMaterialLoadStatusColumnIndex(File materialDataFileObj) ###");
		return materialLoadStatusColumnIndex;
	}
	
	/**
	 * This function is using to move date file from 'source location' or 'user copied location' to the respective backup folder along with the formatted file 
	 * @param materialDataFileObj - File
	 * @param dataSourceName - String
	 * @throws IOException
	 */
	public void moveDataFileToBackupFolder(File materialDataFileObj, String dataSourceName) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileMoveUtility.moveDataFileToBackupFolder(File materialDataFileObj, String dataSourceName) ###");
		String backupFolderPath = "";
		
		//Validating the dataSourceName, based on the data-source name initializing backup folder path, this is using along with the formatted data-file name
		if("HAA".equalsIgnoreCase(dataSourceName))
		{
			backupFolderPath = backupDataFileLocation_HAA;
		}
		else if("HEI".equalsIgnoreCase(dataSourceName))
		{
			backupFolderPath = backupDataFileLocation_HEI;
		}
		else if("CHEU".equalsIgnoreCase(dataSourceName))
		{
			backupFolderPath = backupDataFileLocation_CHEU;
		}
		
		//Get Data-File name, format the data-file name to append date and time to make data-file name as unique within the context and move file to target
		String currentDateandTime = dateFormat.format(new Date());
		String materialDataFileName = materialDataFileObj.getName();
		String fileExtenstion = materialDataFileName.substring(materialDataFileName.lastIndexOf("."), materialDataFileName.length());
		materialDataFileName = materialDataFileName.substring(0, materialDataFileName.lastIndexOf("."));
		materialDataFileName = materialDataFileName.concat("_").concat(currentDateandTime).concat(fileExtenstion);
		
		File targetFileName = new File(backupFolderPath+File.separator+materialDataFileName);
		materialDataFileObj.renameTo(targetFileName);
		
		// LCSLog.debug("### END HBIMaterialDataFileMoveUtility.moveDataFileToBackupFolder(File materialDataFileObj, String dataSourceName) ###");
	}
}
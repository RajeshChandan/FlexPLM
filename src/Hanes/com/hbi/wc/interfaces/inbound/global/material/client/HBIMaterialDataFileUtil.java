package com.hbi.wc.interfaces.inbound.global.material.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

import wt.util.WTException;

/**
 * HBIMaterialDataFileUtil.java
 *
 * This class is used to get a collection of column headers from the given data file, using these column headers to prepare SOAP message, sending SOAP message
 * to MQ, prepare data file map using the given row and excel data file column header, the final data file map contains data as key, value (attKey, attValues)
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-11-2018
 */
public class HBIMaterialDataFileUtil
{
	public static int materialLoadStatusColumnIndex = 0;
	private static String hbiMaterialSourceTypeKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialDataFileUtil.hbiMaterialSourceTypeKey", "hbiMaterialSourceType");
	
	/**
	 * This function is using to get collection of header from the given data file, using these headers to prepare SOAP Message and sending SOAP message to MQ
	 * @param materialDataFileObj - File
	 * @return materialDataFileHeadersMap - Map<Integer, String>
	 * @throws WTException
	 * @throws IOException
	 */
	public Map<Integer, String> getMaterialDataFileHeadersMap(File materialDataFileObj) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileUtil.getMaterialDataFileHeadersMap(File materialDataFileObj) ###");
		Map<Integer, String> materialDataFileHeadersMap = new HashMap<Integer, String>();
		FileInputStream fileInputStreamObj = null;
		
		try
		{
			fileInputStreamObj = new FileInputStream(materialDataFileObj);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			HSSFRow row = worksheet.getRow(0);
			materialLoadStatusColumnIndex = row.getPhysicalNumberOfCells() + 1;
			
			for(int i = 0; i<row.getPhysicalNumberOfCells(); i++)
			{
				materialDataFileHeadersMap.put(new Integer(i), row.getCell(i).getStringCellValue());
			}
		}
		finally
		{
			if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
		}
		
		// LCSLog.debug("### END HBIMaterialDataFileUtil.getMaterialDataFileHeadersMap(File materialDataFileObj) ###");
		return materialDataFileHeadersMap;
	}
	
	/**
	 * This function is using to prepare data file map using the given row and excel data file header info, the final data file map data file map contains data
	 * @param row - HSSFRow
	 * @param materialDataFileHeadersMap - Map<Integer, String>
	 * @return materialDataFileDataMap - Map<String, String>
	 * @throws WTException
	 * @throws IOException
	 */
	public Map<String, String> getMaterialDataFileDataMap(HSSFRow row, Map<Integer, String> materialDataFileHeadersMap, String materialSourceType) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileUtil.getMaterialDataFileDataMap(HSSFRow row, Map<Integer, String> materialDataFileHeadersMap) ###");
		Map<String, String> materialDataFileDataMap = new HashMap<String, String>();
		String flexTypeAttributeKey = "";
		String flexTypeAttributeValue = "";
		HSSFCell cell = null;
		
		//Iterating through headerMap to get each column header and column value, prepare map contains column header as key and column value as value to a map
		for(Integer keyIndex : materialDataFileHeadersMap.keySet())
		{
			flexTypeAttributeKey = materialDataFileHeadersMap.get(new Integer(keyIndex));
			cell = row.getCell(keyIndex);
			
			//Calling a function which is using to get cell value (attribute value) from the given data file row and data file row cell along with the Index
			flexTypeAttributeValue = getMaterialDataFileCellValue(row, cell, keyIndex);
			materialDataFileDataMap.put(flexTypeAttributeKey, flexTypeAttributeValue);
		}
		
		//validate dataFileMap contains key-value, check for 'materialSourceType' key existence in dataFile, based on the validation update dataFileMap source
		if(materialDataFileDataMap.containsKey(hbiMaterialSourceTypeKey) && !FormatHelper.hasContent(materialDataFileDataMap.get(hbiMaterialSourceTypeKey)))
		{
			materialDataFileDataMap.put(hbiMaterialSourceTypeKey, materialSourceType);
		}
		else if(!materialDataFileDataMap.containsKey(hbiMaterialSourceTypeKey))
		{
			materialDataFileDataMap.put(hbiMaterialSourceTypeKey, materialSourceType);
		}
		
		// LCSLog.debug("### END HBIMaterialDataFileUtil.getMaterialDataFileDataMap(HSSFRow row, Map<Integer, String> materialDataFileHeadersMap) ###");
		return materialDataFileDataMap;
	}
	
	/**
	 * This function is using to get cell value for the given cell index, this cell value will be mapping to specific attribute in the caller prior to SOAP MSG
	 * @param row - HSSFRow
	 * @param cell - HSSFCell
	 * @param keyIndex - Integer
	 * @return flexTypeAttributeValue - String
	 * @throws IOException
	 */
	private String getMaterialDataFileCellValue(HSSFRow row, HSSFCell cell, Integer keyIndex) throws IOException
	{
		// LCSLog.debug("### START HBIMaterialDataFileUtil.getMaterialDataFileCellValue(HSSFRow row, HSSFCell cell, Integer keyIndex) ###");
		String flexTypeAttributeValue = "";
		//Changed for 12 upgrade(afsyed) - start
		if(cell != null && (cell.getCellType() == CellType.STRING))
		{
			flexTypeAttributeValue = row.getCell(keyIndex).getStringCellValue();
		}
		else if(cell != null && (cell.getCellType() == CellType.NUMERIC))
		{
			flexTypeAttributeValue = String.valueOf(row.getCell(keyIndex).getNumericCellValue());
		}
		else if(cell != null && (cell.getCellType() == CellType.BOOLEAN))
		{
			flexTypeAttributeValue = String.valueOf(row.getCell(keyIndex).getBooleanCellValue());
		}
		else
		{
			flexTypeAttributeValue = "";
		}
		//Changed for 12 upgrade(afsyed) - end
		// LCSLog.debug("### END HBIMaterialDataFileUtil.getMaterialDataFileCellValue(HSSFRow row, HSSFCell cell, Integer keyIndex) ###");
		return flexTypeAttributeValue;
	}
}
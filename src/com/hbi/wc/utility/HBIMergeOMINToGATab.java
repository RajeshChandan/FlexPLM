package com.hbi.wc.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class HBIMergeOMINToGATab
{
	private static String inputFileName = "SAP_CONVERTED_OMNI.xls";
	private static String inputFileName1 = "SAP_CONVERTED_GEN_ATT_MERGED.xls";
	private static String outputFileName = "SAP_CONVERTED_GEN_ATT_MERGED_Sys.xls";
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Data Copy from one Excel to Another STARTED !!!!!!!!!!!!!!!!!!!!! ");
		processExcelDataCopy(inputFileName, outputFileName);
		System.out.println("Data Copy from one Excel to Another COMPLETED !!!!!!!!!!!!!!!!!!!!! ");
	}
	
	public static void processExcelDataCopy(String inputFileName, String outputFileName) throws IOException
	{
		FileInputStream fileInputStreamObj = null;
		FileInputStream fileInputStreamObj1 = null;
		FileOutputStream fileOutputStreamObj = null;
		
		try
		{
			fileInputStreamObj = new FileInputStream("D:\\ptc\\data\\".concat(inputFileName));
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			Map<String, Integer> generalAttributesMap = populateGeneralAttributesMap(worksheet);
			
			fileInputStreamObj1 = new FileInputStream("D:\\ptc\\data\\".concat(inputFileName1));
			HSSFWorkbook workbook1 = new HSSFWorkbook(fileInputStreamObj1);
			HSSFSheet worksheet1 = workbook1.getSheetAt(0);
			
			worksheet1 = populateGeneralAttributesToExcel(generalAttributesMap, worksheet, worksheet1);
			
			fileOutputStreamObj = new FileOutputStream("D:\\ptc\\data\\".concat(outputFileName));
			workbook1.write(fileOutputStreamObj);
		}
		finally
		{
			if(fileInputStreamObj != null)
			{
				fileInputStreamObj.close();
			}
			if(fileInputStreamObj1 != null)
			{
				fileInputStreamObj1.close();
			}
			if(fileOutputStreamObj != null)
			{
				fileOutputStreamObj.close();
			}
		}
	}
	
	public static HSSFSheet populateGeneralAttributesToExcel(Map<String, Integer> generalAttributesMap, HSSFSheet worksheet, HSSFSheet worksheet1) throws IOException
	{
		HSSFRow row = null;
		String sapCode = "";
		
		//Iterating through each rows of the given excel file, initialize 'Attribution Code', 'Description', 'APS Pack Quantity' and 'Selling Style Number' 
		for(int i=1; i<=100000; i++)
		{
			row = worksheet1.getRow(i);
			if(row == null)
				break;
						
			sapCode = row.getCell(0).getStringCellValue();
			if(generalAttributesMap.containsKey(sapCode))
			{
				populateGeneralAttributesToExcel(sapCode, row, worksheet, generalAttributesMap.get(sapCode));
			}
		}
		
		return worksheet1;
	}
	
	public static HSSFRow populateGeneralAttributesToExcel(String sapCode, HSSFRow row, HSSFSheet worksheet, int dataFileRowIndex) throws IOException
	{
		HSSFRow dataRow = worksheet.getRow(dataFileRowIndex);
		String sellingStyleNo = "";
		
		String str1 = dataRow.getCell(1).getStringCellValue();
		String str2 = dataRow.getCell(2).getStringCellValue();
		//
		row.createCell(73).setCellValue(str1);
		row.createCell(74).setCellValue(str2);
		
		return row;
	}
	
	public static Map<String, Integer> populateGeneralAttributesMap(HSSFSheet worksheet) throws IOException
	{
		Map<String, Integer> generalAttributesMap = new HashMap<String, Integer>();
		HSSFRow row = null;
		String sapCode = "";
		
		//Iterating through each rows of the given excel file, initialize 'Attribution Code', 'Description', 'APS Pack Quantity' and 'Selling Style Number' 
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
					
			sapCode = row.getCell(0).getStringCellValue();
			generalAttributesMap.put(sapCode, i);
		}
		
		return generalAttributesMap;
	}
}
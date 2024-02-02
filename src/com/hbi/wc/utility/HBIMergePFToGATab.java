package com.hbi.wc.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class HBIMergePFToGATab
{
	private static String inputFileName = "SAP_CONVERTED_PF.xls";
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
		String str3 = dataRow.getCell(3).getStringCellValue();
		String str4 = dataRow.getCell(4).getStringCellValue();
		String str5 = dataRow.getCell(5).getStringCellValue();
		String str6 = dataRow.getCell(6).getStringCellValue();
		String str7 = dataRow.getCell(7).getStringCellValue();
		String str8 = dataRow.getCell(8).getStringCellValue();
		String str9 = dataRow.getCell(9).getStringCellValue();
		String str10 = dataRow.getCell(10).getStringCellValue();
		String str11 = dataRow.getCell(11).getStringCellValue();
		String str12 = dataRow.getCell(12).getStringCellValue();
		String str13 = dataRow.getCell(13).getStringCellValue();
		String str14 = dataRow.getCell(14).getStringCellValue();
		String str15 = dataRow.getCell(15).getStringCellValue();
		String str16 = dataRow.getCell(16).getStringCellValue();
		String str17 = dataRow.getCell(17).getStringCellValue();
		String str18 = dataRow.getCell(18).getStringCellValue();
		
	
		//
		row.createCell(55).setCellValue(str1);
		row.createCell(56).setCellValue(str2);
		row.createCell(57).setCellValue(str3);
		row.createCell(58).setCellValue(str4);
		row.createCell(59).setCellValue(str5);
		row.createCell(60).setCellValue(str6);
		row.createCell(61).setCellValue(str7);
		row.createCell(62).setCellValue(str8);
		row.createCell(63).setCellValue(str9);
		row.createCell(64).setCellValue(str10);
		row.createCell(65).setCellValue(str11);
		row.createCell(66).setCellValue(str12);
		row.createCell(67).setCellValue(str13);
		row.createCell(68).setCellValue(str14);
		row.createCell(69).setCellValue(str15);
		row.createCell(70).setCellValue(str16);
		row.createCell(71).setCellValue(str17);
		row.createCell(72).setCellValue(str18);
		
	
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
package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HBICreateNewColumnFromGAtoSizeCat 
{
	public static void main(String[] args)
	{
		try 
		{
			FileInputStream file = new FileInputStream(new File("D:\\SAP_CONVERTED_GEN_ATT.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			System.out.println("Started.........");
			ArrayList<String> al = null;
			HashMap<String, ArrayList<String>> keyValMap = new HashMap<String, ArrayList<String>>();
			while (rowIterator.hasNext()) 
			{
				Row row = rowIterator.next();
				al = new ArrayList<String>();
				al.add(row.getCell(1).toString());
				al.add(row.getCell(2).toString());
				al.add(row.getCell(3).toString());
				al.add(row.getCell(4).toString());
				keyValMap.put(row.getCell(0).toString() , al);
			}
			file.close();
			
			FileInputStream file_1 = new FileInputStream(new File("D:\\SP_SizeCat.xlsx"));
			XSSFWorkbook workbook_1 = new XSSFWorkbook(file_1);
			XSSFSheet sheet_1 = workbook_1.getSheetAt(0);
			// Iterate through each rows one by one
			Iterator<Row> rowIterator_1 = sheet_1.iterator();
			while (rowIterator_1.hasNext()) 
			{
				Row row_1 = rowIterator_1.next();
				if (keyValMap.containsKey(row_1.getCell(0).toString()))
					{
						ArrayList<String> val = keyValMap.get(row_1.getCell(0).toString());
						row_1.getCell(3).setCellValue(val.get(0));
						row_1.getCell(4).setCellValue(val.get(1));
						row_1.getCell(5).setCellValue(val.get(2));
						row_1.getCell(6).setCellValue(val.get(3));
					}
				}
				file_1.close();

				FileOutputStream outputStream_1 = new FileOutputStream(new File("D:\\SP_SizeCat_gen.xlsx"));
				workbook_1.write(outputStream_1);
				outputStream_1.close();
			
			System.out.println("Completed.........");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


package com.lowes.massimport.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellType;
import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.util.MassImport;

public class ExcelUtil {

	public static void processHeaderData(Row row, MassImportHeader header) {
		int rowNum = row.getRowNum();
		/** First two rows does not have any value **/
		if (rowNum == 0) {
			header.setMassImportTitle(getMassImportTitle(row));
		} else if (rowNum == 2) {
			updateColumnIndex(row, header);
		} else if (rowNum == 3) {
			header.setColumns(getColumnNames(row));
		}

	}

	private static String getMassImportTitle(Row row) {
		String massImportVersion = "";
		for (Cell cell : row) {
			if (cell.getCellType() == CellType.BLANK) {
				break;
			}
			return cell.getStringCellValue().trim();
		}
		return massImportVersion;
	}

	private static List<String> getColumnNames(Row row) {
		List<String> columnNames = new ArrayList<String>();
		for (Cell cell : row) {
			if (cell.getCellType() == CellType.BLANK) {
				break;
			}
			String columnName = cell.getStringCellValue().trim();
			columnNames.add(columnName);
		}
		return columnNames;
	}

	private static void updateColumnIndex(Row row, MassImportHeader header) {
		Sheet sheet = row.getSheet();
		int numOfMergeCell = sheet.getNumMergedRegions();
		int mergeCount = 0;
		for (Cell cell : row) {
			if (cell.getCellType() == CellType.BLANK) {
				if (mergeCount == numOfMergeCell) {
					break;
				}
				continue;
			}
			CellRangeAddress cellRangeAddress = sheet.getMergedRegion(mergeCount++);
			int lastColumnIndex = cellRangeAddress.getLastColumn();
			String columnName = cell.getStringCellValue();
			if (MassImport.PRODUCT_COLUMN.equals(columnName)) {
				header.setProductColumnIndex(lastColumnIndex);
			} else if (MassImport.SOURCING_COLUMN.equals(columnName)) {
				header.setSourceColumnIndex(lastColumnIndex);
			} else if (MassImport.PACKAGING_COLUMN.equals(columnName)) {
				header.setPackageColumnIndex(lastColumnIndex);
			} else if (MassImport.COSTSHEET_COLUMN.equals(columnName)) {
				header.setCostSheetColumnIndex(lastColumnIndex);
			}
		}
	}

}

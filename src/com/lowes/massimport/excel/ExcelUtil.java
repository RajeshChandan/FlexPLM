package com.lowes.massimport.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.util.MassImport;

public class ExcelUtil {

	public static void processHeaderData(Row row, MassImportHeader header) {
		int rowNum = row.getRowNum();
		// GPBT-2150 starts
		String sheetName = row.getSheet().getSheetName();
		header.setMassImportSheetName(sheetName);
		// GPBT-2150 ends
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

	// Modified for GPBT-2150, as old logic was unable to give proper indexes for costsheet tab
	private static void updateColumnIndex(Row row, MassImportHeader header) {
		Sheet sheet = row.getSheet();
		List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
		for(CellRangeAddress mergedRegion : mergedRegions) {
			if (mergedRegion.getFirstRow() <= row.getRowNum() && mergedRegion.getLastRow() >= row.getRowNum()) { // range is in ROW 2
				int firstCol = mergedRegion.getFirstColumn();
                int lastColumnIndex = mergedRegion.getLastColumn();

                // Get the value of the top-left cell of the merged region
                Row regionFirstRow = sheet.getRow(mergedRegion.getFirstRow());
                Cell cell = regionFirstRow.getCell(firstCol);
                String columnName = getCellValue(cell);
                
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
	
	// Added for GPBT-2150
	public static boolean checkIfRowIsEmpty(Row row) {
	    if (row == null) {
	        return true;
	    }
	    if (row.getLastCellNum() <= 0) {
	        return true;
	    }
	    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
	        Cell cell = row.getCell(cellNum);
	        if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
	            return false;
	        }
	    }
	    return true;
	}

    // Added for GPBT-2150
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}

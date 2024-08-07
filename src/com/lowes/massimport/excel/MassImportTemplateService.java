package com.lowes.massimport.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.util.DeleteFileHelper;
import com.lowes.massimport.document.DocumentService;
import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.excel.pojo.MassImportTemplate;
import com.lowes.massimport.util.MassImport;

import wt.log4j.LogR;
import wt.util.WTException;

public class MassImportTemplateService {

	private DocumentService documentService = DocumentService.getDocumentService();
	private Map<Long, MassImportTemplate> massImportTemplateMap = new HashMap<>();
	private static final Logger LOGGER = LogR.getLogger(MassImportTemplateService.class.getName());
	private static MassImportTemplateService service = null;

	public static MassImportTemplateService getInstance() {
		if (service == null) {
			service = new MassImportTemplateService();
		}
		return service;
	}

	public MassImportTemplate getMassImportTemplate() throws WTException {
		MassImportTemplate massImportTemplate = null;
		LCSDocument document = queryMassImportTemplate();
		if (document == null) {
			return massImportTemplate;
		}
		long key = document.getModifyTimestamp().getTime();
		if (massImportTemplateMap.containsKey(key)) {
			massImportTemplate = massImportTemplateMap.get(key);
		} else {
			/** Refreshing the cache **/
			if (massImportTemplateMap.size() > 0) {
				massImportTemplateMap.clear();
			}
			/** Reading the Mass Import Template file **/
			MassImportTemplate template = readMassImportTemplate(document);
			if (template != null && template.getMassImportHeaderMap() != null) {
				String errorMessage = template.getMessage();
				if (StringUtils.isEmpty(errorMessage)) {
					massImportTemplateMap.put(key, template);
					massImportTemplate = template;
				} else {
					LOGGER.error("Error while reading the Mass Import Template: " + errorMessage);
					throw new WTException(errorMessage);
				}
			}

		}

		return massImportTemplate;
	}

	private LCSDocument queryMassImportTemplate() {
		LCSDocument document = null;
		try {
			document = MassImport.getMassImportTemplate();
		} catch (WTException e) {
			LOGGER.error("Error while querying the Mass Import Template. " + e.getMessage());
			e.printStackTrace();
		}
		return document;
	}

	private MassImportTemplate readMassImportTemplate(LCSDocument document) {

		FileInputStream massImportFile = null;
		String primaryFilePath = "";
		// int rowNum = 0; can't use local varibale directly in java stream, so wrapped it in below line GPBT-2150 
		var sheetRowNum = new Object(){ int curRowNum = 0; };
		Set<XSSFSheet> itemSheet = new HashSet<XSSFSheet>(); // GPBT-2150 changes
		Set<MassImportHeader> itemSheetHeader = new HashSet<MassImportHeader>(); // GPBT-2150 changes
		MassImportTemplate massImportTemplate = new MassImportTemplate();
		massImportTemplate.setUpdatedTime(document.getModifyTimestamp().getTime());
		StringBuilder errorMessage = new StringBuilder();
		XSSFWorkbook workbook = null;
		try {
			primaryFilePath = documentService.downloadAndGetPrimaryFilePath(document);
			massImportFile = new FileInputStream(new File(primaryFilePath));
			workbook = new XSSFWorkbook(massImportFile); // GPBT-2150 changes
			
			itemSheet.add(workbook.getSheet(MassImport.WORKBOOK_NAME));
			itemSheet.add(workbook.getSheet(MassImport.COSTSHEET_WORKBOOK_NAME));
			
			
			
			// GPBT-2150 starts
			itemSheet.stream()
			.peek(e -> LOGGER.info("Processing template sheet for header information>>>>>>>>" + e.getSheetName()))
			.forEach(sheet -> {
				MassImportHeader header = new MassImportHeader();
				for (Row row : sheet) {
					sheetRowNum.curRowNum = row.getRowNum();
					if (sheetRowNum.curRowNum < MassImport.HEADER_ROWS) {
						ExcelUtil.processHeaderData(row, header);
					} else {
						/** Only reading the columns **/
						break;
					}
				}
				LOGGER.info("Template sheet "+ header.getMassImportSheetName() +" header data>>>>>>>>" + header);
				itemSheetHeader.add(header);
			});
			
			
			
			String validationMessage = validateColumns(itemSheetHeader);
			LOGGER.debug("validationMessage after validateColumns>>>>>>>>>>>" + validationMessage);
			if (!StringUtils.isEmpty(validationMessage)) {
				massImportTemplate.setMessage(validationMessage);
			}
			
			itemSheetHeader.stream().forEach(header -> massImportTemplate.setMassImportHeader(header.getMassImportSheetName(), header));
			
			LOGGER.debug("All sheets processed for template...........");
			// GPBT-2150 ends
		} catch (FileNotFoundException e) {
			String error = getGenericErrorMessage(e.getMessage(), sheetRowNum.curRowNum + 1);
			errorMessage.append(error);
			LOGGER.error(error);
			e.printStackTrace();
		} catch (IOException e) {
			String error = getGenericErrorMessage(e.getMessage(), sheetRowNum.curRowNum + 1);
			errorMessage.append(error);
			LOGGER.error(error);
			e.printStackTrace();
		} catch (Exception e) {
			String error = getGenericErrorMessage(e.getMessage(), sheetRowNum.curRowNum + 1);
			errorMessage.append(error);
			LOGGER.error(error);
			e.printStackTrace();
		} finally {
			if(workbook != null) { // GPBT-2150, closed workbook in finally
				try {
					workbook.close();
				} catch(IOException e) {
					String error = getGenericErrorMessage(e.getMessage(), sheetRowNum.curRowNum + 1);
					errorMessage.append(error);
					LOGGER.error(error);
					e.printStackTrace();
				}
			}
			if (massImportFile != null) {
				try {
					massImportFile.close();
				} catch (IOException e) {
					String error = getGenericErrorMessage(e.getMessage(), sheetRowNum.curRowNum + 1);
					errorMessage.append(error);
					LOGGER.error(error);
					e.printStackTrace();
				}
				DeleteFileHelper.deleteFile(primaryFilePath);
			}
			if (errorMessage.length() > 0) {
				massImportTemplate.setMessage(errorMessage.toString());
			}

		}

		return massImportTemplate;

	}

	// GPBT-2150, added condition for CS tab validation handling
	private String validateColumns(Set<MassImportHeader> headers) {
		StringBuilder builder = new StringBuilder();
		headers.stream()
		.peek(header -> LOGGER.debug("Going to validate header for " + header.getMassImportSheetName() + " tab."))
		.forEach(header -> {
			if (header.getColumns().size() == 0 ) {
				builder.append("Mass Import Template does not have any columns in " + header.getMassImportSheetName() + "tab. ");
			} else if (header.getProductColumnIndex() == 0) {
				builder.append("Mass Import Template " + header.getMassImportSheetName() + " tab does not have any Product attributes. ");
			} else if (header.getMassImportSheetName().equals(MassImport.WORKBOOK_NAME) && (header.getSourceColumnIndex() == 0 || header.getPackageColumnIndex() == 0)) {
				builder.append("Mass Import Template Item tab does not have any Source/Packaging attributes. ");
			} else if (header.getMassImportSheetName().equals(MassImport.COSTSHEET_WORKBOOK_NAME) && header.getCostSheetColumnIndex() == 0) {
				builder.append("Mass Import Template CostSheet tab does not have any costsheet attributes.");
			}
		});
		return builder.toString();
	}

	private String getGenericErrorMessage(String message, int rowNum) {
		return "Row Num: " + rowNum + " : Error while proeccesing Mass Import Template. Error Message [" + message
				+ "]";
	}

}

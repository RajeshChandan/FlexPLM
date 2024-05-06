package com.lowes.massimport.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
			if (template != null && template.getMassImportHeader() != null) {
				String errorMessage = template.getMessage();
				if (StringUtils.isEmpty(errorMessage)) {
					massImportTemplateMap.put(key, template);
					massImportTemplate = template;
				} else {
					LOGGER.error("Error while reading the Mass Import Template: " + errorMessage);
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
		int rowNum = 0;
		MassImportTemplate massImportTemplate = new MassImportTemplate();
		massImportTemplate.setUpdatedTime(document.getModifyTimestamp().getTime());
		StringBuilder errorMessage = new StringBuilder();
		try {
			primaryFilePath = documentService.downloadAndGetPrimaryFilePath(document);
			massImportFile = new FileInputStream(new File(primaryFilePath));
			XSSFWorkbook workbook = new XSSFWorkbook(massImportFile);
			XSSFSheet itemSheet = workbook.getSheet(MassImport.WORKBOOK_NAME);
			MassImportHeader header = new MassImportHeader();

			for (Row row : itemSheet) {
				rowNum = row.getRowNum();
				if (rowNum < MassImport.HEADER_ROWS) {
					ExcelUtil.processHeaderData(row, header);
				} else {
					/** Only reading the columns **/
					break;
				}

			}
			String validationMessage = validateColumns(header);
			if (!StringUtils.isEmpty(validationMessage)) {
				massImportTemplate.setMessage(validationMessage);
			}
			massImportTemplate.setMassImportHeader(header);

		} catch (FileNotFoundException e) {
			String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
			errorMessage.append(error);
			LOGGER.error(error);
			e.printStackTrace();
		} catch (IOException e) {
			String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
			errorMessage.append(error);
			LOGGER.error(error);
			e.printStackTrace();
		} catch (Exception e) {
			String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
			errorMessage.append(error);
			LOGGER.error(error);
			e.printStackTrace();
		} finally {
			if (massImportFile != null) {
				try {
					massImportFile.close();
				} catch (IOException e) {
					String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
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

	private String validateColumns(MassImportHeader header) {
		StringBuilder builder = new StringBuilder();
		if (header.getColumns().size() == 0) {
			builder.append("Mass Import Template does not have any columns. ");
		} else if (header.getProductColumnIndex() == 0) {
			builder.append("Mass Import Template does not have any Product attributes. ");
		} else if (header.getSourceColumnIndex() == 0 || header.getPackageColumnIndex() == 0) {
			builder.append("Mass Import Template does not have any Source/Packaging attributes. ");
		} else if (header.getCostSheetColumnIndex() == 0) {
			builder.append("Mass Import Template does not have any Cost Sheet attributes.");
		}
		return builder.toString();

	}

	private String getGenericErrorMessage(String message, int rowNum) {
		return "Row Num: " + rowNum + " : Error while proeccesing Mass Import Template. Error Message [" + message
				+ "]";
	}

}

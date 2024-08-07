package com.lowes.massimport.excel;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.DeleteFileHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lowes.massimport.document.DocumentService;
import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.excel.pojo.MassImportItem;
import com.lowes.massimport.excel.pojo.MassImportTemplate;
import com.lowes.massimport.service.ProductService;
import com.lowes.massimport.util.MassImport;
import com.lowes.type.metadata.pojo.AttributesMetaData;
import com.lowes.type.metadata.pojo.EnumEntry;
import com.lowes.type.metadata.pojo.EnumMetaData;
import com.lowes.type.metadata.pojo.TypeAttributesMetaData;
import com.lowes.type.metadata.service.PLMTypeAttributesMetadataService;

import wt.access.NotAuthorizedException;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainerException;
import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.session.SessionServerHelper;
import wt.util.WTException;

/****
 * Validates the Mass Import input file and parse the input records
 *
 * @author Samikkannu Manickam (samikkannu.manickam@lowes.com)
 *
 */
public class ExcelValidationService {

	private static final Logger LOGGER = LogR.getLogger(ExcelValidationService.class.getName());
	private static final int MAX_ROW_CAPACITY = 2005;
	private static final String PRODUCT_ATTS_TO_MATCH = LCSProperties.get("com.lowes.massimport.productAttributesToMatch", "Product Group");
	private static final List<String> PRODUCT_ATTS_LIST = Arrays.asList(PRODUCT_ATTS_TO_MATCH.split(","));
	private DocumentService documentService = DocumentService.getDocumentService();
	private static final ProductService productService = ProductService.getProductService();
	private PLMTypeAttributesMetadataService typeService = PLMTypeAttributesMetadataService.getTypeSeriveInstance();
	private TypeAttributesMetaData productTypeAttributes = null;
	private TypeAttributesMetaData sourceTypeAttributes = null;
	private TypeAttributesMetaData costSheetTypeAttributes = null;
	private XSSFFormulaEvaluator evaluator;
	

	/***
	 *
	 * @param document
	 * @param errorMessages
	 * @param massImportItems
	 */
	public void readAndValidateImportFile(LCSDocument document, List<String> errorMessages,
			List<MassImportItem> massImportItems) {
		FileInputStream massImportFile = null;
		String primaryFilePath = "";
		int rowNum = 0;
		XSSFWorkbook workbook = null;
		String sheetNameBeingProcessed = "";
		try {
			/** Reading the Mass Import Template Document **/
			MassImportTemplate massImportTemplate = MassImportTemplateService.getInstance().getMassImportTemplate();
			if (massImportTemplate == null) {
				String error = "Error while reading the Mass Import Template file. Please check with System Administrtator.";
				LOGGER.error(error);
				errorMessages.add(error);
				return;
			}
			LOGGER.info("After getMassImportTemplate.....massImportTemplate IS NOT NULL");
			primaryFilePath = documentService.downloadAndGetPrimaryFilePath(document);
			massImportFile = new FileInputStream(new File(primaryFilePath));
			workbook = new XSSFWorkbook(massImportFile);
			evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			
			
			Comparator<XSSFSheet> comp = Comparator.comparingInt(workbook::getSheetIndex); // making sure sheets are processed using their position in workbook
			Set<XSSFSheet> sheetsToImport = new TreeSet<XSSFSheet>(comp);
			sheetsToImport.add(workbook.getSheet(MassImport.COSTSHEET_WORKBOOK_NAME));
			sheetsToImport.add(workbook.getSheet(MassImport.WORKBOOK_NAME));
			
			LCSSeason season = (LCSSeason) document.getValue(MassImport.MASSIMPORT_DOC_SEASON_ATTRIBUTE);
			LCSSupplier supplier = (LCSSupplier) document.getValue(MassImport.MASSIMPORT_DOC_VENDOR_ATTRIBUTE);
			LCSProduct rfpProductRef = (LCSProduct) document.getValue(MassImport.MASSIMPORT_DOC_RFP_INTERNAL_ATTR);
			if (season == null || supplier == null || rfpProductRef == null) {
				String message = formErrorMessage(season, supplier, rfpProductRef);
				LOGGER.error(message);
				errorMessages.add(message);
				return;
			}
			
			MassImportHeader header = null;
			
			initializeAttributesConstraints();	
			for (XSSFSheet sheet : sheetsToImport) { // moved nested loop into a method.....GPBT-2150
				header = new MassImportHeader();
				header.setSeason(season);
				header.setRfpProductRef(rfpProductRef);
				header.setSupplier(supplier);
				header.setMassImportSheetName(sheet.getSheetName());
				rowNum = processCurSheet(sheet, sheet.getSheetName(), massImportTemplate, header, season,
						rfpProductRef, supplier, massImportItems, errorMessages);
			}
		} catch (PropertyVetoException | WTException | IOException | NullPointerException e) {
			/*
			 * For any exception, processCurSheet will always return the last passed rowNum
			 * GPBT-2150
			 */
			String error = FormatHelper.hasContent(sheetNameBeingProcessed)
					? getGenericErrorMessage(e.getMessage(), rowNum + 2, sheetNameBeingProcessed)
					: getGenericErrorMessage(e.getMessage(), -1);
			errorMessages.add(error);
			LOGGER.error(error);
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					String error = getGenericErrorMessage(e.getMessage(), -1);
					errorMessages.add(error);
					LOGGER.error(error);
					e.printStackTrace();
				}
			}

			if (massImportFile != null) {
				try {
					massImportFile.close();
				} catch (IOException e) {
					String error = getGenericErrorMessage(e.getMessage(), -1);
					errorMessages.add(error);
					LOGGER.error(error);
					e.printStackTrace();
				}
				DeleteFileHelper.deleteFile(primaryFilePath);
			}

		}
	}

	private int processCurSheet(XSSFSheet curSheet, String sheetNameBeingProcessed,
			MassImportTemplate massImportTemplate, MassImportHeader header, LCSSeason season, LCSProduct rfpProductRef,
			LCSSupplier supplier, List<MassImportItem> massImportItems, List<String> errorMessages) throws WTException {
		int rowNum = -1;
		boolean isHeaderValidated = false;
		for (Row row : curSheet) {
			rowNum = row.getRowNum();
			MassImportItem massImportItem = new MassImportItem();
			massImportItem.setSheetName(sheetNameBeingProcessed);
			massImportItem.setSheetPosition(curSheet.getWorkbook().getSheetIndex(sheetNameBeingProcessed));
			/** Check if number of rows reaches maximum limit **/
			if (MAX_ROW_CAPACITY < rowNum) {
				errorMessages.add("Mass Import file can't be processed since it has more than 2000 rows of records in "
						+ sheetNameBeingProcessed + " tab (Current rowNo: " + (rowNum + 1) + ")."
						+ " Mass Import maximum row limit is 2000."
						+ " Please update the Mass Import file with less than 2000 rows of records "
						+ " in all data sheets and upload again.");
				return rowNum;
			}
			StringBuilder error = new StringBuilder();
			massImportItem.setRowNum(rowNum);
			/** First four rows have vendor info and column info */
			if (rowNum < MassImport.HEADER_ROWS) {
				ExcelUtil.processHeaderData(row, header);
				// LOGGER.debug("header row processed for " + sheetNameBeingProcessed + " TAB, row::::::::" + (rowNum + 1));
			} else {
				if (!isHeaderValidated) {
					isHeaderValidated = true;
					validateHeaders(header, error, massImportTemplate, sheetNameBeingProcessed);
					if (error.length() != 0) {
						errorMessages.add(error.toString());
						return rowNum;
					}
				}
				//LOGGER.debug("start processing non-header data for " + sheetNameBeingProcessed + " TAB, row::::::::" + (rowNum + 1));
				massImportItem.setMassImportHeader(header);
				error.append("Sheet:").append(sheetNameBeingProcessed).append(" :Row Num: ").append(rowNum + 1)
						.append(" :");
				if(ExcelUtil.checkIfRowIsEmpty(row)) continue;
				/*processRowData(row, sheetNameBeingProcessed.equals(MassImport.WORKBOOK_NAME) ? header.getColumns()
						: header.getCsColumns(), massImportItem, error);*/
				processRowData(row, header.getColumns(), massImportItem, error);
				/** Validate the existing item **/
				validateExistingItem(massImportItem, season, rfpProductRef, supplier, error);


				/**
				 * GPBT-2150 for new products check product group, if not matching with RFP
				 * product, don't process and add to error
				 *
				 * For existing products product group check not required - can it be blank?
				 */
				applyValidation(massImportItem, rfpProductRef, sheetNameBeingProcessed, error);
				
				/** Max Row Id length is 4 [ROW NUM 2000 :] **/
				/** default error:Sheet:Item Cost Details :Row Num: 2000 :**/ // character count is 40
				if (error.length() > 40) {
					LOGGER.debug("Inside processCurSheet, error is there in " + sheetNameBeingProcessed + " TAB, row::::::::"
							+ (rowNum + 1) + "<<<<<errorMessage>>>>>" + error);
					errorMessages.add(error.substring(0, error.length() - 1).toString());
					continue;
				}
				massImportItems.add(massImportItem);
				
			}
		}
		return rowNum;
	}

	
	// GPBT-2150 added PG check logic, can be used to validate more attributes in future
	private void applyValidation(MassImportItem massImportItem, LCSProduct rfpProductRef, String sheetNameBeingProcessed, StringBuilder error) 
			throws NotAuthorizedException, WTContainerException, WTException {
		String rfpData = "", excelData = "", existingItemData = "";
		LCSProduct existingItem = massImportItem.getExistingItem() != null ? massImportItem.getExistingItem() :
			productService.getExistingProduct(massImportItem.getProductDescription(), massImportItem.getModelNumer() 
					, rfpProductRef, massImportItem.getMassImportHeader().getSeason());
		
		for(String att : PRODUCT_ATTS_LIST) {
			TypeAttributesMetaData prodMetaData = typeService.getPLMTypelMetaData(MassImport.LCSPRODUCT_TYPE);
			Map<String, AttributesMetaData> prodAttributesDataMap = prodMetaData.getAttributeMetaDataMap();
			AttributesMetaData attMetaData = prodAttributesDataMap.get(att);
			String attInternalName = attMetaData.getAttributeInternalName();
			String attDisplayName = attMetaData.getAttributeDisplayName();
			List<EnumMetaData> enumMetaDataList = attMetaData.getEnumValueList();
			
			rfpData = rfpProductRef.getValue(attInternalName) != null ? 
					(String)rfpProductRef.getValue(attInternalName) : "";
			excelData = massImportItem.getStringProductAttributes().get(attInternalName) != null ? 
					massImportItem.getStringProductAttributes().get(attInternalName) : "";
			
			existingItemData = existingItem != null && existingItem.getValue(attInternalName) != null ? 
					(String)existingItem.getValue(attInternalName) : "";
			
			
			switch (sheetNameBeingProcessed) {
				case MassImport.COSTSHEET_WORKBOOK_NAME :
					switch (attDisplayName) {
						case MassImport.PRODUCT_GROUP_DISPLAY_ATTR:
							if(existingItem != null && !existingItemData.equals(excelData)) {
								addErrorMessage(error, attDisplayName + " is not matching with existing item's " + attDisplayName + " for this row." + " Excel Value:"
										+ getEnumLabel(enumMetaDataList, excelData) 
										+ ", ExistingItem " + attDisplayName + " Value:" 
										+ getEnumLabel(enumMetaDataList, existingItemData)
										+ " \n");
							}
						break;
					}
					break;
				case MassImport.WORKBOOK_NAME :
					switch (attDisplayName) {
						case MassImport.PRODUCT_GROUP_DISPLAY_ATTR:
							if(existingItem == null && ! rfpData.equals(excelData)) {
								addErrorMessage(error, attDisplayName + " is not matching with RFP " + attDisplayName + " for this row." + " Excel Value:"
										+ getEnumLabel(enumMetaDataList, excelData) 
										+ ", RFP " + attDisplayName + " Value:" 
										+ getEnumLabel(enumMetaDataList, rfpData)
										+ " \n");
							}
						break;
					}
					break;
			}
		}
		
	}

	// GPBT-2150 modification done
	private void validateHeaders(MassImportHeader header, StringBuilder error, MassImportTemplate template,
			String sheetName) throws WTException {
		/** Checking the columns */
		//List<String> importFileColumns = sheetName.equals(MassImport.WORKBOOK_NAME) ? header.getColumns() : header.getCsColumns();
		List<String> importFileColumns = header.getColumns();
		if (importFileColumns.size() == 0) {
			String errorMessage = "Invalid input file. Input file does not have any columns. \n";
			addErrorMessage(error, errorMessage);
		}
		MassImportHeader templateHeader = template.getMassImportHeader(sheetName);
		String templateTitle = templateHeader.getMassImportTitle();
		String errorMessage = "You are not using the latest version of Mass Import Item Template. Please download the latest Mass Import Item Template from My Profile --> PLM Templates \n";
		if (!templateTitle.equalsIgnoreCase(header.getMassImportTitle())) {
			addErrorMessage(error, errorMessage);
			return;
		}
		boolean isOutdatedFile = false;
		// List<String> templateColumns = sheetName.equals(MassImport.WORKBOOK_NAME) ? templateHeader.getColumns()	: templateHeader.getCsColumns();
		List<String> templateColumns = templateHeader.getColumns();

		if (importFileColumns.size() != templateColumns.size()) {
			isOutdatedFile = true;
		}
		int i = 0;
		while (i < templateColumns.size() && !isOutdatedFile) {
			String templateColumn = templateColumns.get(i);
			String importFileColumnName = importFileColumns.get(i);
			if (!templateColumn.equalsIgnoreCase(importFileColumnName)) {
				isOutdatedFile = true;
			}
			i++;
		}
		if (isOutdatedFile) {
			addErrorMessage(error, errorMessage);
		}
	}

	// GPBT-2150 modified method logic for CS changes
	private void processRowData(Row row, List<String> columnNames, MassImportItem massImportItem, StringBuilder error)
			throws WTException {
		String curSheetName = massImportItem.getSheetName(); // GPBT- 2150
		
		for (Cell cell : row) {
			int columnIndex = cell.getColumnIndex();
			/**
			 * Calculate cell value if formula is present.
			 */
			cell = evaluator.evaluateInCell(cell);
			/**
			 * There is no data after processing the packaging and costSheet attributes for
			 * each sheets respectively GPBT- 2150
			 **/
			if (curSheetName.equals(MassImport.WORKBOOK_NAME)
					&& massImportItem.getMassImportHeader().getPackageColumnIndex() < columnIndex)
				break;
			if (curSheetName.equals(MassImport.COSTSHEET_WORKBOOK_NAME)
					&& massImportItem.getMassImportHeader().getCostSheetColumnIndex() < columnIndex)
				break;

			String columnName = columnNames.get(columnIndex);
			LOGGER.log(Level.DEBUG, "{} tab evaluated Cell value {} <<>> {}", curSheetName, columnName, cell);
			int productColumnIndex = massImportItem.getMassImportHeader().getProductColumnIndex();
			if (columnIndex <= productColumnIndex) {
				if (MassImport.PRODUCT_DESCRIPTION_DISPLAY_ATTR.equals(columnName)) {
					String productDesc = getStringValue(columnName, cell, error);
					if (FormatHelper.hasContent(productDesc)) { // GPBT-2150
						massImportItem.setProductDescription(productDesc);
					} else {
						addErrorMessage(error, "Product Description on row : " + (row.getRowNum() + 1) + " is empty. \n");
					}
				} else if (MassImport.PRODUCT_MODELNUMBER_DISPLAY_ATTR.equals(columnName)) {
					String modelNumber = getStringValue(columnName, cell, error);
					massImportItem.setModelNumer(modelNumber);
				} else if (MassImport.PRODUCT_ENTERPRISENUMBER_DISPLAY_ATTR.equals(columnName)) {
					String enterpriseNumber = getStringValue(columnName, cell, error);
					massImportItem.setEnterpriseItemNumber(enterpriseNumber);
				} else {
					processProductAttributes(cell, columnName, massImportItem, error);
				}

			} else if (curSheetName.equals(MassImport.WORKBOOK_NAME) && (columnIndex <= massImportItem.getMassImportHeader().getSourceColumnIndex()
					|| columnIndex <= massImportItem.getMassImportHeader().getPackageColumnIndex())) {
				processSourceAttributes(cell, columnName, massImportItem, error);

			} else if (curSheetName.equals(MassImport.COSTSHEET_WORKBOOK_NAME) && columnIndex <= massImportItem.getMassImportHeader().getCostSheetColumnIndex()) {
				processCostSheetAttributes(cell, columnName, massImportItem, error);
			} else {
				// DO Nothing: As of now Mass Import deals with Product, Source and costSheet
			}
		}
	}

	private void processProductAttributes(Cell cell, String columnName, MassImportItem massImportItem,
			StringBuilder builder) throws WTException {
		Map<String, AttributesMetaData> productAttributeMap = productTypeAttributes.getAttributeMetaDataMap();
		if (productAttributeMap.containsKey(columnName.trim())) {
			AttributesMetaData attributeMetaData = productAttributeMap.get(columnName);
			String internalName = attributeMetaData.getAttributeInternalName();
			String flexDataType = attributeMetaData.getFlexDataType();
			List<EnumMetaData> enumMetaDataList = attributeMetaData.getEnumValueList();
			if (MassImport.FLEX_STRING_TYPE.contains(flexDataType)) {
				addStringValue(internalName, columnName, cell, enumMetaDataList,
						massImportItem.getStringProductAttributes(), builder, flexDataType);
			} else if (MassImport.FLEX_BOOLEAN_TYPE.equals(flexDataType)) {
				addBooleanValue(internalName, columnName, cell, massImportItem.getBooleanProductAttributes(), builder);
			} else if (MassImport.FLEX_FLOAT_TYPE.equals(flexDataType)
					|| MassImport.FLEX_CURRENCY_TYPE.equals(flexDataType)) {
				double value = getDoubleValue(columnName, cell, builder);
				massImportItem.getFloatProductAttributes().put(internalName, value);
			} else if (MassImport.FLEX_INTEGER_TYPE.equals(flexDataType)) {
				long value = getNumberValue(columnName, cell, builder);
				massImportItem.getNumberProductAttributes().put(internalName, value);
			} else if (MassImport.FLEX_DATE_TYPE.equals(flexDataType)) {
				Date dateValue = getDateValue(columnName, cell, builder);
				massImportItem.getDateProductAttributes().put(internalName, dateValue);
			} else if (MassImport.FLEX_OBJECT_REF_TYPE.equals(flexDataType)) {
				if (cell.getCellType() == CellType.BLANK) {
					massImportItem.getObjectRefProductAttributes().put(internalName, null);
				} else {
					String name = getStringValue(columnName, cell, builder);
					if (MassImport.FLEX_OBJECT_REF_LIFECYCLEMANAGED.equals(attributeMetaData.getObjectRefClass())) {
						LCSLifecycleManaged businessObj = MassImport.getBusinessObjectMap(name);
						if (businessObj == null) {
							String errorMessage = columnName + " [" + name + "] does not exist in the system. \n";
							addErrorMessage(builder, errorMessage);
						}
						addAssortmentCode(massImportItem.getStringProductAttributes(), columnName, businessObj);
						massImportItem.getObjectRefProductAttributes().put(internalName, businessObj);
					}
				}
			}

		} else {
			String error = "Invalid product attribute [" + columnName + "] \n";
			addErrorMessage(builder, error);
		}
	}

	private void processSourceAttributes(Cell cell, String columnName, MassImportItem massImportItem,
			StringBuilder builder) throws WTException {
		Map<String, AttributesMetaData> sourceAttributeMap = sourceTypeAttributes.getAttributeMetaDataMap();
		if (sourceAttributeMap.containsKey(columnName.trim())) {
			AttributesMetaData attributeMetaData = sourceAttributeMap.get(columnName);
			String internalName = attributeMetaData.getAttributeInternalName();
			String flexDataType = attributeMetaData.getFlexDataType();
			List<EnumMetaData> enumMetaDataList = attributeMetaData.getEnumValueList();
			if (MassImport.FLEX_STRING_TYPE.contains(flexDataType)) {
				addStringValue(internalName, columnName, cell, enumMetaDataList,
						massImportItem.getStringSourceAttributes(), builder, flexDataType);
			} else if (MassImport.FLEX_BOOLEAN_TYPE.equals(flexDataType)) {
				addBooleanValue(internalName, columnName, cell, massImportItem.getBooleanSourceAttributes(), builder);
			} else if (MassImport.FLEX_FLOAT_TYPE.equals(flexDataType)
					|| MassImport.FLEX_CURRENCY_TYPE.equals(flexDataType)) {
				double value = getDoubleValue(columnName, cell, builder);
				massImportItem.getFloatSourcetAttributes().put(internalName, value);
			} else if (MassImport.FLEX_INTEGER_TYPE.equals(flexDataType)) {
				long value = getNumberValue(columnName, cell, builder);
				massImportItem.getNumberSourceAttributes().put(internalName, value);
			} else if (MassImport.FLEX_DATE_TYPE.equals(flexDataType)) {
				Date dateValue = getDateValue(columnName, cell, builder);
				massImportItem.getDateSourceAttributes().put(internalName, dateValue);
			} else if (MassImport.FLEX_OBJECT_REF_TYPE.equals(flexDataType)) {
				if (cell.getCellType() == CellType.BLANK) {
					massImportItem.getObjectRefSourceAttributes().put(internalName, null);
				} else {
					String countryName = getStringValue(columnName, cell, builder);
					if (MassImport.FLEX_OBJECT_REF_COUNTRY.equals(attributeMetaData.getObjectRefClass())
							&& !StringUtils.isEmpty(countryName)) {
						LCSCountry country = MassImport.getCountry(countryName);
						if (country == null) {
							String errorMessage = columnName + " [" + countryName + "] does not exist in the system. \n";
							addErrorMessage(builder, errorMessage);
						}
						massImportItem.getObjectRefSourceAttributes().put(internalName, country);
					}
				}
			}

		} else {
			String error = "Invalid source attribute [" + columnName + "] \n";
			addErrorMessage(builder, error);
		}
	}

	// GPBT-2150 modification
	private void processCostSheetAttributes(Cell cell, String columnName, MassImportItem massImportItem,
			StringBuilder builder) throws WTException {
		Map<String, AttributesMetaData> costSheetAttributeMap = costSheetTypeAttributes.getAttributeMetaDataMap();
		if (costSheetAttributeMap.containsKey(columnName.trim())) {
			AttributesMetaData attributeMetaData = costSheetAttributeMap.get(columnName);
			String internalName = attributeMetaData.getAttributeInternalName();
			String flexDataType = attributeMetaData.getFlexDataType();
			List<EnumMetaData> enumMetaDataList = attributeMetaData.getEnumValueList();
			if (MassImport.FLEX_STRING_TYPE.contains(flexDataType)) {
				addStringValue(internalName, columnName, cell, enumMetaDataList,
						massImportItem.getStringCostSheetAttributes(), builder, flexDataType);
			} else if (MassImport.FLEX_BOOLEAN_TYPE.equals(flexDataType)) {
				addBooleanValue(internalName, columnName, cell, massImportItem.getBooleanCostSheetAttributes(),
						builder);
			} else if (MassImport.FLEX_FLOAT_TYPE.equals(flexDataType)
					|| MassImport.FLEX_CURRENCY_TYPE.equals(flexDataType)) {
				double value = getDoubleValue(columnName, cell, builder);
				massImportItem.getFloatCostSheetAttributes().put(internalName, value);
			} else if (MassImport.FLEX_INTEGER_TYPE.equals(flexDataType)) {
				long value = getNumberValue(columnName, cell, builder);
				massImportItem.getNumberCostSheetAttributes().put(internalName, value);
			} else if (MassImport.FLEX_DATE_TYPE.equals(flexDataType)) {
				Date dateValue = getDateValue(columnName, cell, builder);
				massImportItem.getDateCostSheetAttributes().put(internalName, dateValue);
			} else {
				String countryName = getStringValue(columnName, cell, builder);
				if(MassImport.FLEX_OBJECT_REF_COUNTRY.equals(attributeMetaData.getObjectRefClass())) {
					if(FormatHelper.hasContent(countryName)) {
						LCSCountry country = MassImport.getCountry(countryName);
						if (country == null) {
							String errorMessage = columnName + " [" + countryName + "] does not exist in the system. \n";
							addErrorMessage(builder, errorMessage);
						}
						massImportItem.getObjectRefCostsheetAttributes().put(internalName, country);
					} else {
						String errorMessage = "Country name is blank. \n";
						addErrorMessage(builder, errorMessage);
					}
				}
			}

		} else {
			String error = "Invalid cost sheet attribute [" + columnName + "] \n";
			addErrorMessage(builder, error);
		}
	}

	private String getEnumValue(List<EnumMetaData> enumMetaDataList, String cellValue) {
		for (EnumMetaData enumMetaData : enumMetaDataList) {
			List<EnumEntry> enumEntries = enumMetaData.getEnumEntries();
			for (EnumEntry enumEntry : enumEntries) {
				if (enumEntry.getSelectionLabel().equals(cellValue)) {
					return enumEntry.getSelectionValue();
				}
			}
		}
		return null;
	}
	
	// GPBT-2150 added
	private String getEnumLabel(List<EnumMetaData> enumMetaDataList, String internalValue) {
		for (EnumMetaData enumMetaData : enumMetaDataList) {
			List<EnumEntry> enumEntries = enumMetaData.getEnumEntries();
			for (EnumEntry enumEntry : enumEntries) {
				if (enumEntry.getSelectionValue().equals(internalValue)) {
					return enumEntry.getSelectionLabel();
				}
			}
		}
		return null;
	}

	private void addStringValue(String attributeInternalName, String displayName, Cell cell,
			List<EnumMetaData> enumMetaDataList, Map<String, String> stringAttrubuteMap, StringBuilder errorMessage,
			String flexDataType) {
		/** Check the cell value if it is blank then set the NULL value **/
		if (cell.getCellType() == CellType.BLANK) {
			stringAttrubuteMap.put(attributeInternalName, null);
			return;
		}
		String cellValue = getStringValue(displayName, cell, errorMessage);
		if (!StringUtils.isEmpty(cellValue)) {
			if (enumMetaDataList.size() > 0) {
				String selectionValue = getEnumValue(enumMetaDataList, cellValue);
				if (selectionValue != null) {
					if (MassImport.FLEX_MULTIENTRY_TYPE.equals(flexDataType)) {
						selectionValue += MassImport.CHOICE_SEPARATOR;
					}
					stringAttrubuteMap.put(attributeInternalName, selectionValue);
				} else {
					String error = "Invalid value [" + cellValue + "] for the attribute '" + displayName + "' \n";
					addErrorMessage(errorMessage, error);
				}
			} else {
				stringAttrubuteMap.put(attributeInternalName, cellValue);
			}
		}
	}

	private void addBooleanValue(String attributeInternalName, String displayName, Cell cell,
			Map<String, Boolean> booleanAttrubuteMap, StringBuilder errorMessage) {
		CellType cellType = cell.getCellType();
		/** Check the cell value if it is blank then set the null value **/
		if (cellType == CellType.BLANK) {
			booleanAttrubuteMap.put(attributeInternalName, false);
			return;
		}
		if (cellType == CellType.BOOLEAN) {
			boolean booleanValue = cell.getBooleanCellValue();
			booleanAttrubuteMap.put(attributeInternalName, booleanValue);
		} else if (CellType.STRING == cellType) {
			String cellValue = cell.getStringCellValue().trim();
			boolean booleanValue = "Yes".equalsIgnoreCase(cellValue) ? true : false;
			booleanAttrubuteMap.put(attributeInternalName, booleanValue);
		} else {
			String error = "Invalid boolean value for the attribute '" + displayName + "' \n";
			addErrorMessage(errorMessage, error);
		}
	}

	private void addErrorMessage(StringBuilder builder, String errorMessage) {
		if (builder.length() == 0) {
			builder.append(errorMessage);
		} else {
			builder.append(" ");
			builder.append(errorMessage).append(",");
		}
	}

	public TypeAttributesMetaData getTypeAttributes(String objectType)
			throws NotAuthorizedException, WTContainerException, WTException {
		if (MassImport.LCSPRODUCT_TYPE.equals(objectType)) {
			if (productTypeAttributes == null) {
				productTypeAttributes = typeService.getPLMTypelMetaData(objectType);
			}
			return productTypeAttributes;
		} else if (MassImport.LCSSOUCINGCONFIG_TYPE.equals(objectType)) {
			if (sourceTypeAttributes == null) {
				sourceTypeAttributes = typeService.getPLMTypelMetaData(objectType);
			}
			return sourceTypeAttributes;
		} else if (MassImport.LCSCOSTSHEET_TYPE.equals(objectType)) {
			if (costSheetTypeAttributes == null) {
				costSheetTypeAttributes = typeService.getPLMTypelMetaData(objectType);
			}
			return costSheetTypeAttributes;
		} else {
			return null;
		}

	}

	private void initializeAttributesConstraints() throws NotAuthorizedException, WTContainerException, WTException {
		if (productTypeAttributes == null) {
			productTypeAttributes = MassImport.getInstance().getTypeAttributes(MassImport.LCSPRODUCT_TYPE);
		}
		if (sourceTypeAttributes == null) {
			sourceTypeAttributes = MassImport.getInstance().getTypeAttributes(MassImport.LCSSOUCINGCONFIG_TYPE);
		}
		if (costSheetTypeAttributes == null) {
			costSheetTypeAttributes = MassImport.getInstance().getTypeAttributes(MassImport.LCSCOSTSHEET_TYPE);
		}

	}

	/****
	 * This method is not used right now but it will used in future to validate the
	 * user
	 *
	 * @param principal
	 * @param vendorName
	 * @return isValidUser
	 * @throws ObjectNoLongerExistsException
	 * @throws WTException
	 */
	@SuppressWarnings("unused")
	private boolean isValidUser(WTPrincipal principal, String vendorName)
			throws ObjectNoLongerExistsException, WTException {
		Map<ObjectIdentifier, ObjectIdentifier> userGroupsMap = OrganizationServicesHelper.manager
				.parentGroupMap(principal);
		if (userGroupsMap == null || userGroupsMap.size() == 0) {
			LOGGER.error("Invalid user access. User is not allowed to perform the MassImport");
			return false;
		}
		Set<String> authorizedGroups = MassImport.getAuthorizedUsers();
		boolean accessEnabled = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			for (Map.Entry<ObjectIdentifier, ObjectIdentifier> set : userGroupsMap.entrySet()) {
				Persistable persisObj = PersistenceHelper.manager.refresh(set.getKey());
				if (persisObj instanceof WTGroup) {
					WTGroup grp = (WTGroup) persisObj;
					String groupName = grp.getName();
					LOGGER.info("Group Name: " + groupName);
					if (vendorName.equalsIgnoreCase(groupName) || authorizedGroups.contains(groupName)) {
						return true;
					}
				}
			}

		} finally {
			SessionServerHelper.manager.setAccessEnforced(accessEnabled);
		}
		return false;
	}

	// GPBT-2150 changes handled scenario where we don't need rownum in error
	// message
	private String getGenericErrorMessage(String message, int rowNum) {
		if (rowNum > 0) {
			return "Row Num: " + rowNum + " : Error while proeccesing mass import file. Error Message [" + message
					+ "]";
		} else {
			return "Error while proeccesing mass import file. Error Message [" + message + "]";
		}

	}

	// GPBT-2150 - no need to add rownum condition as this is more likely be called
	// when error is in a data sheet
	private String getGenericErrorMessage(String message, int rowNum, String sheetName) {
		return sheetName + " tab, Row Num: " + rowNum + " : Error while proeccesing mass import file. Error Message ["
				+ message + "]";
	}

	private String formErrorMessage(LCSSeason season, LCSSupplier supplier, LCSProduct product) {
		StringBuilder builder = new StringBuilder();
		if (season == null) {
			builder.append("Mass Import document is not associated with any season. ");
		}
		if (supplier == null) {
			builder.append("Mass Import document does not have supplier reference. ");
		}
		if (product == null) {
			builder.append("Mass Import document does not have RFP Product Ref.");
		}
		return builder.toString();
	}

	private String getStringValue(String displayName, Cell cell, StringBuilder errorMessage) {
		String value = "";
		if (CellType.BLANK == cell.getCellType()) {
			return value;
		} else if (CellType.STRING == cell.getCellType()) {
			value = cell.getStringCellValue();
		} else if (CellType.NUMERIC == cell.getCellType()) {
			value += (long) cell.getNumericCellValue();
		} else {
			String error = "Invalid value for the attribute '" + displayName + "' value should be in Text format \n";
			addErrorMessage(errorMessage, error);
		}
		return value.trim();
	}

	private double getDoubleValue(String displayName, Cell cell, StringBuilder errorMessage) {
		double value = 0;
		if (CellType.BLANK == cell.getCellType()) {
			return value;
		} else if (CellType.NUMERIC == cell.getCellType()) {
			value = cell.getNumericCellValue();
		} else if (CellType.STRING == cell.getCellType()) {
			try {
				value = Double.parseDouble(cell.getStringCellValue());
			} catch (NumberFormatException e) {
				LOGGER.error(e);
				String error = "Invalid value[" + cell.getStringCellValue() + "] for the attribute '" + displayName
						+ "' value should be in Number format \n";
				addErrorMessage(errorMessage, error);
			}
		} else {
			String error = "Invalid value for the attribute '" + displayName + "' value should be in Number format \n";
			addErrorMessage(errorMessage, error);
		}
		return value;
	}

	private long getNumberValue(String displayName, Cell cell, StringBuilder errorMessage) {
		long value = 0;
		if (CellType.BLANK == cell.getCellType()) {
			return value;
		} else if (CellType.NUMERIC == cell.getCellType()) {
			value = (long) cell.getNumericCellValue();
		} else if (CellType.STRING == cell.getCellType()) {
			try {
				value = Long.parseLong(cell.getStringCellValue());
			} catch (NumberFormatException e) {
				LOGGER.error(e);
				String error = "Invalid value[" + cell.getStringCellValue() + "] for the attribute '" + displayName
						+ "' value should be in Number format \n";
				addErrorMessage(errorMessage, error);
			}
		} else {
			String error = "Invalid value for the attribute '" + displayName + "' value should be in Number format \n";
			addErrorMessage(errorMessage, error);
		}
		return value;
	}

	private Date getDateValue(String displayName, Cell cell, StringBuilder errorMessage) {
		Date value = null;
		if (CellType.BLANK == cell.getCellType()) {
			return value;
		} else if (CellType.NUMERIC == cell.getCellType()) {
			value = cell.getDateCellValue();
		} else {
			String error = "Invalid date format for the attribute '" + displayName
					+ "'. Date should be in [MM/DD/YYYY] format \n";
			addErrorMessage(errorMessage, error);
		}
		return value;
	}

	private void addAssortmentCode(Map<String, String> stringAttrubuteMap, String columnName,
			LCSLifecycleManaged businessObj) throws WTException {
		if (businessObj != null && MassImport.PRODUCT_ASSORTMENT_DISPLAYNAME.equalsIgnoreCase(columnName)) {
			String value = (String) businessObj.getValue(MassImport.BUSINESSOBJ_ASSORTMENT_CODE_INTERNAL_NAME);
			stringAttrubuteMap.put(MassImport.PRODUCT_ASSORTMENT_NUMBER_INTERNAL_NAME, value);
		}

	}

	private void validateExistingItem(MassImportItem massImportItem, LCSSeason season, LCSProduct rfpProduct,
			LCSSupplier importSupplier, StringBuilder error) throws WTException {
		String enterpriseNumber = massImportItem.getEnterpriseItemNumber();
		String modelNumber = massImportItem.getModelNumer();
		String productDesc = massImportItem.getProductDescription();
		if (StringUtils.isEmpty(enterpriseNumber)) {
			/** If there is no enterprise # then no need to do validation */
			return;
		}
		/** Checking the existing Item **/
        LCSProduct existingItem = MassImport.queryProduct(productDesc, modelNumber,
                rfpProduct, true, enterpriseNumber, season);
		String message = "";
		String existingItemMessage = "Existing Item with Product Description [" + productDesc + "] , Model Number ["
				+ modelNumber + "] , Enterprise Item # [" + enterpriseNumber + "]";
		if (existingItem == null) {
			message = existingItemMessage + " is not associated with season.";
			LOGGER.error(message);
			error.append(message);
			return;
		}
		Collection<?> sourceConfigs = LCSSourcingConfigQuery.getSourcingConfigForProductSeason(existingItem, season);
		Iterator<?> itr = sourceConfigs.iterator();
		boolean isSupplierExist = false;
		LCSSourcingConfig itemSourcingConfig = null;
		while (itr.hasNext()) {
			LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) itr.next();
			LCSSupplier supplier = (LCSSupplier) sourcingConfig.getValue(MassImport.VENDOR);
			if (supplier != null) {
				if (importSupplier.getSupplierName().equals(supplier.getSupplierName())) {
					isSupplierExist = true;
					itemSourcingConfig = sourcingConfig;
					break;
				}
			}
		}
		if (!isSupplierExist) {
			message = "Supplier [" + importSupplier.getSupplierName() + "] is not associated with "
					+ existingItemMessage;
			error.append(message);
			LOGGER.error(message);
		} else {
			massImportItem.setExistingItem(existingItem);
			massImportItem.setExistingItemSourcingConfig(itemSourcingConfig);
		}
	}
}

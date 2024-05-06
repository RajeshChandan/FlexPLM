package com.lowes.massimport.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.DeleteFileHelper;
import com.lowes.massimport.document.DocumentService;
import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.excel.pojo.MassImportItem;
import com.lowes.massimport.excel.pojo.MassImportTemplate;
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
	private DocumentService documentService = DocumentService.getDocumentService();
	private PLMTypeAttributesMetadataService typeService = PLMTypeAttributesMetadataService.getTypeSeriveInstance();
	private TypeAttributesMetaData productTypeAttributes = null;
	private TypeAttributesMetaData sourceTypeAttributes = null;
	private TypeAttributesMetaData costSheetTypeAttributes = null;

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
		try {
			/** Reading the Mass Import Template Document **/
			MassImportTemplate massImportTemplate = MassImportTemplateService.getInstance().getMassImportTemplate();
			if (massImportTemplate == null) {
				String error = "Error while reading the Mass Import Template file. Please check with System Administrtator.";
				LOGGER.error(error);
				errorMessages.add(error);
				return;
			}
			primaryFilePath = documentService.downloadAndGetPrimaryFilePath(document);
			massImportFile = new FileInputStream(new File(primaryFilePath));
			XSSFWorkbook workbook = new XSSFWorkbook(massImportFile);
			XSSFSheet itemSheet = workbook.getSheet(MassImport.WORKBOOK_NAME);
			LCSSeason season = (LCSSeason) document.getValue(MassImport.MASSIMPORT_DOC_SEASON_ATTRIBUTE);
			LCSSupplier supplier = (LCSSupplier) document.getValue(MassImport.MASSIMPORT_DOC_VENDOR_ATTRIBUTE);
			LCSProduct rfpProductRef = (LCSProduct) document.getValue(MassImport.MASSIMPORT_DOC_RFP_INTERNAL_ATTR);
			if (season == null || supplier == null || rfpProductRef == null) {
				String message = formErrorMessage(season, supplier, rfpProductRef);
				LOGGER.error(message);
				errorMessages.add(message);
				return;
			}
			MassImportHeader header = new MassImportHeader();
			header.setSeason(season);
			header.setRfpProductRef(rfpProductRef);
			header.setSupplier(supplier);
			initializeAttributesConstraints();
			boolean isHeaderValidated = false;
			for (Row row : itemSheet) {
				MassImportItem massImportItem = new MassImportItem();
				rowNum = row.getRowNum();
				/** Check if number of rows reaches maximum limit **/
				if (MAX_ROW_CAPACITY < rowNum) {
					errorMessages.add(
							"Mass Import file can't be processed. Since it has more than 2000 rows of records. Mass Import maximum row limit is 2000. Please update the Mass Import file with less than 2000 rows of records and upload again.");
					return;
				}
				StringBuilder error = new StringBuilder();
				massImportItem.setRowNum(rowNum);
				/** First four rows have vendor info and column info */
				if (rowNum < MassImport.HEADER_ROWS) {
					ExcelUtil.processHeaderData(row, header);
				} else {
					if (!isHeaderValidated) {
						isHeaderValidated = true;
						validateHeaders(header, error, massImportTemplate);
						if (error.length() != 0) {
							errorMessages.add(error.toString());
							return;
						}
					}
					massImportItem.setMassImportHeader(header);
					error.append("Row Num: ").append(rowNum + 1).append(" :");
					processRowData(row, header.getColumns(), massImportItem, error);
					/** Validate the existing item**/
					validateExistingItem(massImportItem, season, rfpProductRef, supplier, error);
					/** Max Row Id length is 15 [ROW NUM 2000 :] **/
					if (error.length() > 15) {
						errorMessages.add(error.substring(0, error.length()-1).toString());
						continue;
					}
					if (!StringUtils.isEmpty(massImportItem.getProductDescription())) {
						massImportItems.add(massImportItem);
					}

				}

			}
		} catch (FileNotFoundException e) {
			String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
			errorMessages.add(error);
			LOGGER.error(error);
			e.printStackTrace();
		} catch (IOException e) {
			String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
			errorMessages.add(error);
			LOGGER.error(error);
			e.printStackTrace();
		} catch (Exception e) {
			String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
			errorMessages.add(error);
			LOGGER.error(error);
			e.printStackTrace();
		} finally {
			if (massImportFile != null) {
				try {
					massImportFile.close();
				} catch (IOException e) {
					String error = getGenericErrorMessage(e.getMessage(), rowNum + 1);
					errorMessages.add(error);
					LOGGER.error(error);
					e.printStackTrace();
				}
				DeleteFileHelper.deleteFile(primaryFilePath);
			}

		}
	}

	private void validateHeaders(MassImportHeader header, StringBuilder error, MassImportTemplate template)
			throws WTException {
		/** Checking the columns */
		List<String> importFileColumns = header.getColumns();
		if (importFileColumns.size() == 0) {
			String errorMessage = "Invalid input file. Input file does not have any columns.";
			addErrorMessage(error, errorMessage);
		}
		MassImportHeader templateHeader = template.getMassImportHeader();
		String templateTitle = templateHeader.getMassImportTitle();
		String errorMessage = "You are not using the latest version of Mass Import Item Template. Please download the latest Mass Import Item Template from My Profile --> PLM Templates";
		if (!templateTitle.equalsIgnoreCase(header.getMassImportTitle())) {
			addErrorMessage(error, errorMessage);
			return;
		}
		boolean isOutdatedFile = false;
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

	private void processRowData(Row row, List<String> columnNames, MassImportItem massImportItem, StringBuilder error)
			throws WTException {
		for (Cell cell : row) {
			int columnIndex = cell.getColumnIndex();
			/** There is no data after processing the costSheet attributes **/
			if (massImportItem.getMassImportHeader().getCostSheetColumnIndex() < columnIndex) {
				break;
			}

			String columnName = columnNames.get(columnIndex);
			if (columnIndex <= massImportItem.getMassImportHeader().getProductColumnIndex()) {
				if (MassImport.PRODUCT_DESCRIPTION_DISPLAY_ATTR.equals(columnName)) {
					String productDesc = getStringValue(columnName, cell, error);
					massImportItem.setProductDescription(productDesc);
				} else if (MassImport.PRODUCT_MODELNUMBER_DISPLAY_ATTR.equals(columnName)) {
					String modelNumber = getStringValue(columnName, cell, error);
					massImportItem.setModelNumer(modelNumber);
				} else if(MassImport.PRODUCT_ENTERPRISENUMBER_DISPLAY_ATTR.equals(columnName)) {
					String enterpriseNumber = getStringValue(columnName, cell, error);
					massImportItem.setEnterpriseItemNumber(enterpriseNumber);
				} else {
					processProductAttributes(cell, columnName, massImportItem, error);
				}

			} else if (columnIndex <= massImportItem.getMassImportHeader().getSourceColumnIndex()
					|| columnIndex <= massImportItem.getMassImportHeader().getPackageColumnIndex()) {
				processSourceAttributes(cell, columnName, massImportItem, error);

			} else if (columnIndex <= massImportItem.getMassImportHeader().getCostSheetColumnIndex()) {
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
							String errorMessage = columnName + " [" + name + "] does not exist in the system. ";
							addErrorMessage(builder, errorMessage);
						}
						addAssortmentCode(massImportItem.getStringProductAttributes(), columnName, businessObj);
						massImportItem.getObjectRefProductAttributes().put(internalName, businessObj);
					}
				}
			}

		} else {
			String error = "Invalid product attribute [" + columnName + "] ";
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
					if (MassImport.FLEX_OBJECT_REF_COUNTRY.equals(attributeMetaData.getObjectRefClass()) && !StringUtils.isEmpty(countryName)) {
						LCSCountry country = MassImport.getCountry(countryName);
						if (country == null) {
							String errorMessage = columnName + " [" + countryName + "] does not exist in the system. ";
							addErrorMessage(builder, errorMessage);
						}
						massImportItem.getObjectRefSourceAttributes().put(internalName, country);
					}
				}
			}

		} else {
			String error = "Invalid source attribute [" + columnName + "] ";
			addErrorMessage(builder, error);
		}
	}

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
				if (MassImport.FLEX_OBJECT_REF_COUNTRY.equals(attributeMetaData.getObjectRefClass()) && !StringUtils.isEmpty(countryName)) {
					LCSCountry country = MassImport.getCountry(countryName);
					if (country == null) {
						String errorMessage = columnName + " [" + countryName + "] does not exist in the system. ";
						addErrorMessage(builder, errorMessage);
					}
					massImportItem.getObjectRefCostsheetAttributes().put(internalName, country);
				}
			}

		} else {
			String error = "Invalid cost sheet attribute [" + columnName + "] ";
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
					String error = "Invalid value [" + cellValue + "] for the attribute '" + displayName + "'";
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
			String error = "Invalid boolean value for the attribute '" + displayName + "'";
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

	private String getGenericErrorMessage(String message, int rowNum) {
		return "Row Num: " + rowNum + " : Error while proeccesing mass import file. Error Message [" + message + "]";
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
			String error = "Invalid value for the attribute '" + displayName + "' value should be in Text format";
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
						+ "' value should be in Number format";
				addErrorMessage(errorMessage, error);
			}
		} else {
			String error = "Invalid value for the attribute '" + displayName + "' value should be in Number format";
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
						+ "' value should be in Number format";
				addErrorMessage(errorMessage, error);
			}
		} else {
			String error = "Invalid value for the attribute '" + displayName + "' value should be in Number format";
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
					+ "'. Date should be in [MM/DD/YYYY] format";
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

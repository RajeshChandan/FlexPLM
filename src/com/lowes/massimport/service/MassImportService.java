package com.lowes.massimport.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentClientModel;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lowes.massimport.document.DocumentService;
import com.lowes.massimport.excel.ExcelValidationService;
import com.lowes.massimport.excel.pojo.MassImportItem;
import com.lowes.massimport.util.MassImport;

import wt.log4j.LogR;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/***
 * Service class to process, create and update the Mass Import item.
 * 
 * @author Samikkannu Manickam (Samikkannu.manickam@lowes.com)
 *
 */
public class MassImportService {

	private static final Logger LOGGER = LogR.getLogger(MassImportService.class.getName());
	private static final ProductService productService = ProductService.getProductService();
	private static final SourcingConfigService sourceService = SourcingConfigService.getSourcingSerivice();
	private static final CostSheetService costSheetService = CostSheetService.getCostSheetSerivice();
	private static final DocumentService documentService = DocumentService.getDocumentService();
	private static final String SUCCESS = "success";
	private static final String FAILED = "failed";

	/***
	 * 
	 * @param document
	 * @return ImportStatus
	 */
	//GPBT-2150 changes
	public String loadItems(LCSDocument document) {
		LOGGER.info("Start processing the Mass Import of new template!");
		// GPBT-2150 addition starts
		var status = new Object(){ String status = SUCCESS; };
		var transactionRollbackStatus = new Object(){ boolean rolledBack = false; };
		// GPBT-2150 addition ends
		long startTime = System.currentTimeMillis();
		ExcelValidationService excelValidationService = new ExcelValidationService();
		List<String> errorMessages = new ArrayList<String>();
		List<MassImportItem> massImportItems = new ArrayList<>();
		excelValidationService.readAndValidateImportFile(document, errorMessages, massImportItems);
		if (errorMessages.size() > 0) {
			/** write the error message into secondary content ### */
			documentService.addSecondaryContent(document, errorMessages);
			return FAILED;
		}
		
		// GPBT-2150, sorting massImportItem based upon sheet name and row numbers and persisting.....starts
		LOGGER.info("###########....procceding with create/update of prod,src,CS");
		/*
		Comparator<MassImportItem> comparator = Comparator.comparing(MassImportItem :: getSheetName)
				// .reversed()
				.thenComparingInt(MassImportItem :: getRowNum);
		*/
		Comparator<MassImportItem> comparator = Comparator.comparingInt(MassImportItem :: getSheetPosition)
				.thenComparingInt(MassImportItem :: getRowNum);
		
		massImportItems
		.stream()
		.sorted(comparator)
		.peek(item -> LOGGER.info("Current Massimport item getting persisted>>>>>>>>>>>" + item))
		.forEach(massImportItem -> {
			int rowNum = massImportItem.getRowNum() + 1;
			Transaction trx = null;
			try {
				trx = new Transaction();
				trx.start();
				transactionRollbackStatus.rolledBack = false;
				
				if(massImportItem.getSheetName().equals(MassImport.WORKBOOK_NAME)) { // GPBT-2150, create/update if current sheet is ITEM 
					persistExcelItemSheetData(massImportItem);
				} else {
					persistExcelCostSheetData(massImportItem);
				}
			} catch (WTPropertyVetoException e) {
				LOGGER.error("Error creating prod/src/CS>>>" + e.getLocalizedMessage());
				errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
				status.status = FAILED;
				trx.rollback();
				transactionRollbackStatus.rolledBack = true;
				e.printStackTrace();
			} catch (WTException e) {
				LOGGER.error("Error creating prod/src/CS>>>" + e.getLocalizedMessage());
				errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
				status.status = FAILED;
				trx.rollback();
				transactionRollbackStatus.rolledBack = true;
				e.printStackTrace();
			} catch (Exception e) {
				LOGGER.error("Error creating prod/src/CS>>>" + e.getLocalizedMessage());
				errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
				status.status = FAILED;
				trx.rollback();
				transactionRollbackStatus.rolledBack = true;
				e.printStackTrace();
			} finally {
				 try {
					 if(! transactionRollbackStatus.rolledBack) trx.commit();
				} catch (PersistenceException e) {
					status.status = FAILED;
					errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
					e.printStackTrace();
				}
			}
		});
		
		LOGGER.info("###########....after create/update of prod,src,CS errorMessages>>>>>>>>>>>>" + errorMessages);
		if (errorMessages.size() > 0) {
			/** write the error message into secondary content ### */
			documentService.addSecondaryContent(document, errorMessages);
			status.status = FAILED;
		} else {
			try {
				/** If there is no error then check for secondary content and delete it **/
				documentService.deleteSecondaryContent(document);
				updateAccess(document, status.status);
			} catch (WTPropertyVetoException | WTException e) {
				LOGGER.error("Error while deleting the secondary content");
				e.printStackTrace();
			}
		}
		
		long endTime = System.currentTimeMillis();
		long timeTaken = (endTime - startTime) / 1000;
		LOGGER.info("Time taken to load " + massImportItems.size() + " rows is: " + timeTaken + " seconds");

		return status.status;

	}

	private String getErrorMeasge(int rowNum, String erroMeasage) {
		String error = "Row Num: " + rowNum + " Mass import is failed. Please check the data and load it again. Error: "
				+ erroMeasage;
		return error;
	}

	private void updateAccess(LCSDocument document, String status) throws WTPropertyVetoException, WTException {
		if (SUCCESS.equalsIgnoreCase(status)) {
			LCSDocumentClientModel documentClient = new LCSDocumentClientModel();
			String objectId = FormatHelper.getObjectId(document);
			documentClient.load(objectId);
			documentClient.setValue(MassImport.MASSIMPORT_DOC_COMPLETED_ATTR,
					MassImport.MASSIMPORT_DOC_COMPLETED_ATTR_VALUE);
			documentClient.save();
		}
	}
	
	// Added as part of GPBT-2150
	private void persistExcelCostSheetData(MassImportItem massImportItem) 
			throws WTException, WTPropertyVetoException {
		LCSProduct product = null;
		LCSSourcingConfig sourcingConfig = null;
		int rowNum = massImportItem.getRowNum() + 1;
		LCSSeason season = massImportItem.getMassImportHeader().getSeason();
		
		
		LCSCountry curCSCountry = (LCSCountry) massImportItem.getObjectRefCostsheetAttributes().get(MassImport.CS_COUNTRY_OF_ORIGIN_ATTR);
		LCSProduct rfpProduct = massImportItem.getMassImportHeader().getRfpProductRef();
		if(massImportItem.getExistingItem() == null) { // if Item was new and got created while Item sheet was being processed
			product = MassImport.queryProduct(massImportItem.getProductDescription(), massImportItem.getModelNumer(), rfpProduct, false, null, season);
			String rfpSupplierName = massImportItem.getMassImportHeader().getSupplier() != null 
					? massImportItem.getMassImportHeader().getSupplier().getName() : ""; 
			Vector<FlexObject> srcConfigs = LCSSourcingConfigQuery.getSourcingConfigDataForProductSeason(product.getMaster(), season.getMaster()).getResults();
			for(FlexObject obj : srcConfigs) {
				LCSSourcingConfig srcconfig = (LCSSourcingConfig) LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:" + obj.getString(MassImport.LCSSOURCINGCONFIGBRANCHIDITERATIONINFO));
				LCSSupplier supplier = (LCSSupplier) srcconfig.getValue(MassImport.VENDOR);
				if(supplier != null && supplier.getName().equals(rfpSupplierName)) {
					sourcingConfig = srcconfig;
					break;
				}
			}
		} else {
			product = massImportItem.getExistingItem();
			sourcingConfig = massImportItem.getExistingItemSourcingConfig();
		}
		// if at this point product or src is null, skip and addToError
		if(product == null || sourcingConfig == null) {
			String message = "Row Num: " + rowNum + " Cost Sheet create/update is failed as no associated item/vendor is found \n";
			//errorMessages.add(message);
			//LOGGER.error(message);
			throw new WTException(message);
		}				
		int costSheetCount = costSheetService.createAndUpdateCostSheet(sourcingConfig,
				massImportItem, product, season, curCSCountry);
		LOGGER.info("Row # " + rowNum + " no of costSheet created/updated : " + costSheetCount);
		
	}
	
	// Added as part of GPBT-2150
	private void persistExcelItemSheetData(MassImportItem massImportItem) 
			throws WTPropertyVetoException, WTException  {
		int rowNum = massImportItem.getRowNum() + 1;
		LCSProduct product = productService.createAndUpdateProduct(massImportItem);
		LOGGER.info("Row # " + rowNum + " Product: " + product);
		if (product == null) {
			String message = "Row Num: " + rowNum + " Product create/update is failed";
			//errorMessages.add(message);
			//LOGGER.error(message);
			throw new WTException(message);
		}
		LCSSourcingConfig sourcingConfig = sourceService.createAndUpdateSourcingConfig(product, massImportItem);
		LOGGER.info("Row # " + rowNum + " sourcingConfig: " + sourcingConfig);
		if (sourcingConfig == null) {
			String message = "Row Num: " + rowNum + " Sourcingconfig create/update is failed";
			//errorMessages.add(message);
			//LOGGER.error(message);
			throw new WTException(message);
		}
	}

}

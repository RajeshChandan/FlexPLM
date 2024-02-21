package com.lowes.massimport.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lowes.massimport.document.DocumentService;
import com.lowes.massimport.excel.ExcelValidationService;
import com.lowes.massimport.excel.pojo.MassImportItem;

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
	public String loadItems(LCSDocument document) {
		String message = "";
		String status = SUCCESS;
		LOGGER.info("Start processing the Mass Import!");
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

		for (MassImportItem massImportItem : massImportItems) {
			int rowNum = massImportItem.getRowNum() + 1;
			Transaction trx = new Transaction();
			try {
				trx.start();
				LCSSeason season = massImportItem.getMassImportHeader().getSeason();
				LCSProduct product = productService.createAndUpdateProduct(massImportItem);
				LOGGER.info("Row # " + rowNum + " Product: " + product);
				if (product == null) {
					String measge = "Row Num: " + rowNum + " Product create/update is failed";
					errorMessages.add(measge);
					LOGGER.error(message);
					continue;
				}
				LCSSourcingConfig sourcingConfig = sourceService.createAndUpdateSourcingConfig(product, massImportItem);
				LOGGER.info("Row # " + rowNum + " sourcingConfig: " + sourcingConfig);
				if (sourcingConfig == null) {
					String measge = "Row Num: " + rowNum + " Sourcingconfig create/update is failed";
					errorMessages.add(measge);
					LOGGER.error(message);
					continue;
				}
				LCSProductCostSheet costSheet = costSheetService.createAndUpdateCostSheet(sourcingConfig,
						massImportItem, product, season);
				LOGGER.info("Row # " + rowNum + " costSheet: " + costSheet);

			} catch (WTException e) {
				status = FAILED;
				trx.rollback();
				errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				status = FAILED;
				trx.rollback();
				errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
				e.printStackTrace();
			} finally {
				try {
					trx.commit();
				} catch (PersistenceException e) {
					status = FAILED;
					errorMessages.add(getErrorMeasge(rowNum, e.getMessage()));
					e.printStackTrace();
				}
			}

		}
		if (errorMessages.size() > 0) {
			/** write the error message into secondary content ### */
			documentService.addSecondaryContent(document, errorMessages);
			status = FAILED;
		} else {
			/** If there is no error then check for secondary content and delete it **/
			try {
				documentService.deleteSecondaryContent(document);
			} catch (WTPropertyVetoException | WTException e) {
				LOGGER.error("Error while deleing the secondary content");
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		long timeTaken = (endTime-startTime)/1000;
		LOGGER.info("Time taken to load "+massImportItems.size()+" rows is: "+timeTaken+" seconds");

		return status;

	}

	private String getErrorMeasge(int rowNum, String erroMeasage) {
		String error = "Row Num: " + rowNum + " Mass import is failed. Please check the data and load it again. Error: "
				+ erroMeasage;
		return error;
	}

}

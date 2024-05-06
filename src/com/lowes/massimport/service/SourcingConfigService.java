package com.lowes.massimport.service;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigClientModel;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.excel.pojo.MassImportItem;
import com.lowes.massimport.util.MassImport;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/***
 * SourcingConfig Service to create and update the primary SourcingConfig during the
 * massimport.
 * @author Samikkannu Manickam (Samikkannu.manickam@lowes.com)
 *
 */
public class SourcingConfigService {

	private static final Logger LOGGER = LogR.getLogger(SourcingConfigService.class.getName());
	private static SourcingConfigService sourcingConfigInstance = null;

	private SourcingConfigService() {
	}

	public static SourcingConfigService getSourcingSerivice() {
		if (sourcingConfigInstance == null) {
			sourcingConfigInstance = new SourcingConfigService();
		}
		return sourcingConfigInstance;
	}
	/****
	 * 
	 * @param product
	 * @param massImportItem
	 * @return LCSSourcingConfig
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSSourcingConfig createAndUpdateSourcingConfig(LCSProduct product, MassImportItem massImportItem)
			throws WTException, WTPropertyVetoException {
		int rowNum = massImportItem.getRowNum() + 1;
		MassImportHeader massImportHeader = massImportItem.getMassImportHeader();
		LCSSourcingConfig existingItemSourcingConfig = massImportItem.getExistingItemSourcingConfig();
		LCSSupplier supplier = massImportHeader.getSupplier();
		LCSSourcingConfig sourcingConfig = null;
		/**
		 * Checking if this is existing Item's SourcingConfig. If this is not a existing
		 * item's sourcingconfig then use the Primary sourcingconfig
		 */
		if (existingItemSourcingConfig != null) {
			sourcingConfig = existingItemSourcingConfig;
			LOGGER.info("Existing Item's SourcingConfig: " + sourcingConfig);
		} else {
			sourcingConfig = LCSSourcingConfigQuery.getPrimarySourceForProduct(product);
			LOGGER.info("Primary SourcingConfig: " + sourcingConfig);
		}

		LCSSourcingConfigClientModel clientModel = new LCSSourcingConfigClientModel();
		if (sourcingConfig != null) {
			LOGGER.info("Row # " + rowNum + " Primary SourcingConfig is already available and updating it. ");
			String configName = clientModel.getSourcingConfigName();
			String objectId = FormatHelper.getObjectId(sourcingConfig);
			clientModel.load(objectId);
			if (!supplier.getName().equals(configName)) {
				clientModel.setSourcingConfigName(supplier.getName());
				clientModel.setValue("name", supplier.getName());

			}
		}
		clientModel.setValue(MassImport.VENDOR, supplier);
		clientModel.setValue(MassImport.SUPPLIER_RELEASE_TO_VENDOR_INTERNAL_ATTR,
				MassImport.SUPPLIER_RELEASE_TO_VENDOR_VALUE);
		setSourceAttributes(clientModel, massImportItem);
		clientModel.save();
		sourcingConfig = clientModel.getBusinessObject();
		LOGGER.info("SourcingConfig is saved/updated successfully!");
		return sourcingConfig;

	}
	/***
	 * 
	 * @param sourceClientModel
	 * @param massImportItem
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private void setSourceAttributes(LCSSourcingConfigClientModel sourceClientModel, MassImportItem massImportItem)
			throws WTPropertyVetoException, WTException {
		/** Setting String attributes */
		for (Map.Entry<String, String> entry : massImportItem.getStringSourceAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sourceClientModel.setValue(key, value);
		}

		/** Setting Boolean attributes */
		for (Map.Entry<String, Boolean> entry : massImportItem.getBooleanSourceAttributes().entrySet()) {
			String key = entry.getKey();
			Boolean value = entry.getValue();
			sourceClientModel.setValue(key, value);
		}

		/** Setting Object Ref attributes */
		for (Map.Entry<String, Object> entry : massImportItem.getObjectRefSourceAttributes().entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof LCSCountry) {
				LCSCountry obj = (LCSCountry) value;
				sourceClientModel.setValue(key, obj);
			}
		}

		/** Setting Float and currency attributes */
		for (Map.Entry<String, Double> entry : massImportItem.getFloatSourcetAttributes().entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			sourceClientModel.setValue(key, value);
		}

		/** Setting number attributes */
		for (Map.Entry<String, Long> entry : massImportItem.getNumberSourceAttributes().entrySet()) {
			String key = entry.getKey();
			Long value = entry.getValue();
			sourceClientModel.setValue(key, value);
		}

		/** Setting date attributes */
		for (Map.Entry<String, Date> entry : massImportItem.getDateSourceAttributes().entrySet()) {
			String key = entry.getKey();
			Date value = entry.getValue();
			sourceClientModel.setValue(key, value);
		}

	}

}

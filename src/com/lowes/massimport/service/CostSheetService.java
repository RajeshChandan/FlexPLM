package com.lowes.massimport.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSCostSheetClientModel;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.FormatHelper;
import com.lowes.massimport.excel.pojo.MassImportItem;
import com.lowes.massimport.util.MassImport;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/***
 * CostSheet Service to create and update the primary costsheet during the
 * massimport.
 * 
 * @author Samikkannu Manickam (Samikkannu.manickam@lowes.com)
 *
 */
public class CostSheetService {

	private static final Logger LOGGER = LogR.getLogger(CostSheetService.class.getName());
	private static CostSheetService costSheetInstance = null;

	private CostSheetService() {
	}

	public static CostSheetService getCostSheetSerivice() {
		if (costSheetInstance == null) {
			costSheetInstance = new CostSheetService();
		}
		return costSheetInstance;
	}

	/***
	 * 
	 * @param sourcingConfig
	 * @param massImportItem
	 * @param product
	 * @param season
	 * @return LCSProductCostSheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSProductCostSheet createAndUpdateCostSheet(LCSSourcingConfig sourcingConfig, MassImportItem massImportItem,
			LCSProduct product, LCSSeason season) throws WTException, WTPropertyVetoException {
		int rowNum = massImportItem.getRowNum() + 1;
		String supplierName = massImportItem.getMassImportHeader().getSupplier().getName();
		LCSProductCostSheet productCosheet = getPrimaryCostSheet(sourcingConfig);
		LCSCostSheetClientModel clientModelCostSheet = new LCSCostSheetClientModel();
		String name = "Primary - " + massImportItem.getModelNumer() + " - "
				+ supplierName;
		LOGGER.info("Costsheet : " + productCosheet);
		if (productCosheet == null) {
			LOGGER.info("Row # " + rowNum + " Primary Cost Sheet does not exist and creating a new one. ");
			FlexType costSheetFlexType = FlexTypeCache.getFlexTypeFromPath(MassImport.COSTSHEET_FLEX_TYPE);
			String flexTypeId = FormatHelper.getObjectId(costSheetFlexType);
			LOGGER.info("Cost Sheet Flex type id: " + flexTypeId);
			clientModelCostSheet.setTypeId(flexTypeId);
			clientModelCostSheet.setValue("name", name);
			/* Cost Sheet primary attributes */
			clientModelCostSheet.setCostSheetType(MassImport.COSTSHEET_TYPE);
			clientModelCostSheet.setPrimaryCostSheet(true);
			clientModelCostSheet.setWhatIf(false);
			clientModelCostSheet.setSourcingConfigId(FormatHelper.getObjectId(sourcingConfig));
			clientModelCostSheet.setProductId(FormatHelper.getObjectId(product));
			LCSSeasonMaster lcsSeasonMaster = season.getMaster();
			clientModelCostSheet.setSeasonId(FormatHelper.getObjectId(lcsSeasonMaster));
			clientModelCostSheet.setSkuMaster(product.getPlaceholderMaster());
			setCostSheetAttributes(clientModelCostSheet, massImportItem);
			clientModelCostSheet.save();
			productCosheet = (LCSProductCostSheet) clientModelCostSheet.getBusinessObject();
			LOGGER.info("Primary Cost Sheet is saved successfully ");

		} else {
			LOGGER.info("Row # " + rowNum + " Primary Cost Sheet is already available and updating it. ");
			String objectId = FormatHelper.getObjectId(productCosheet);
			clientModelCostSheet.load(objectId);
			clientModelCostSheet.setValue("name", name);
			setCostSheetAttributes(clientModelCostSheet, massImportItem);
			clientModelCostSheet.save();
			productCosheet = (LCSProductCostSheet) clientModelCostSheet.getBusinessObject();
			LOGGER.info("Primary Cost Sheet is updated successfully ");

		}
		return productCosheet;
	}

	/***
	 * 
	 * @param costSheetClientModel
	 * @param massImportItem
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private void setCostSheetAttributes(LCSCostSheetClientModel costSheetClientModel, MassImportItem massImportItem)
			throws WTPropertyVetoException, WTException {
		/** Setting String attributes */
		for (Map.Entry<String, String> entry : massImportItem.getStringCostSheetAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			costSheetClientModel.setValue(key, value);
		}

		/** Setting Boolean attributes */
		for (Map.Entry<String, Boolean> entry : massImportItem.getBooleanCostSheetAttributes().entrySet()) {
			String key = entry.getKey();
			Boolean value = entry.getValue();
			costSheetClientModel.setValue(key, value);
		}

		/** Setting Object Ref attributes */
		for (Map.Entry<String, Object> entry : massImportItem.getObjectRefCostsheetAttributes().entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof LCSCountry) {
				LCSCountry obj = (LCSCountry) value;
				costSheetClientModel.setValue(key, obj);
			}
		}

		/** Setting Float and currency attributes */
		for (Map.Entry<String, Double> entry : massImportItem.getFloatCostSheetAttributes().entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			costSheetClientModel.setValue(key, value);
		}

		/** Setting number attributes */
		for (Map.Entry<String, Long> entry : massImportItem.getNumberCostSheetAttributes().entrySet()) {
			String key = entry.getKey();
			Long value = entry.getValue();
			costSheetClientModel.setValue(key, value);
		}

		/** Setting date attributes */
		for (Map.Entry<String, Date> entry : massImportItem.getDateCostSheetAttributes().entrySet()) {
			String key = entry.getKey();
			Date value = entry.getValue();
			costSheetClientModel.setValue(key, value);
		}

	}

	/***
	 * 
	 * @param sourcingConfig
	 * @return LCSProductCostSheet
	 * @throws WTException
	 */
	private LCSProductCostSheet getPrimaryCostSheet(LCSSourcingConfig sourcingConfig) throws WTException {
		LCSProductCostSheet productCostSheet = null;
		Collection<?> costSheets = LCSCostSheetQuery.getAllCostSheetsForSourcingConfig(sourcingConfig);
		Iterator<?> itr = costSheets.iterator();
		while (itr.hasNext()) {
			LCSProductCostSheet costSheet = (LCSProductCostSheet) itr.next();
			if (costSheet.isPrimaryCostSheet()) {
				productCostSheet = costSheet;
				break;
			}
		}
		return productCostSheet;
	}

}

package com.burberry.wc.integration.productcostingapi.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productcostingapi.bean.CostSheet;
import com.burberry.wc.integration.productcostingapi.bean.Source;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle Transformation activity. Class contain several
 * method to handle Extraction activity i.e. Extracting Data from different
 * objects and putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductCostingAPIDataTransform {

	/**
	 * BurberryProductCostingAPIDataTransform.
	 */
	private BurberryProductCostingAPIDataTransform() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIDataTransform.class);

	/**
	 * @param productObj
	 * @param collProdToSourceIds
	 * @param collProdToCostSheetIds
	 * @param collProdToSKUIds
	 * @param seasons
	 * @param deltaCriteria
	 * @param mapTrackedCostSheet
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static List<Source> getListProductCostingSourceBean(
			LCSProduct productObj, Collection<String> collProdToSourceIds,
			Collection<String> collProdToSKUIds,
			Collection<String> collProdToCostSheetIds, Collection seasons,
			boolean deltaCriteria,
			Map<String, Collection<HashMap>> mapTrackedCostSheet)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getListProductCostingSourceBean() ";
		long sourceStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sourceStartTime: ");

		Collection<LCSSourcingConfig> sources = LCSSourcingConfigQuery
				.getSourcingConfigsForProduct(productObj);

		logger.debug(methodName + "Collection Of Sources: " + sources);
		ArrayList<Source> listSource = new ArrayList<Source>();

		// Checking through each sourcing configuration under project
		for (LCSSourcingConfig source : sources) {
			// checking if source criteria is given in URL and validating if
			// source object satisfies the URL criteria
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(source.getBranchIdentifier()),
					collProdToSourceIds);
			// Check if exists
			if (idExists) {
				source = (LCSSourcingConfig) VersionHelper
						.latestIterationOf(source);
				logger.debug(methodName + "Extracting data from Source: "
						+ source.getName());

				// Extraction of Source Object data
				Source sourceBean = BurberryProductCostingAPIJsonDataUtil
						.getSourceBean(source);
				logger.debug(methodName + "Source Bean: " + sourceBean);

				sourceBean.setCostSheet(getListCostSheets(
						collProdToCostSheetIds, source, productObj, seasons,
						collProdToSKUIds, mapTrackedCostSheet));
				if (sourceBean.getCostSheet() != null
						&& !sourceBean.getCostSheet().isEmpty()) {
					logger.debug(methodName + "Source Bean: " + sourceBean);

					logger.debug("source cost sheet list "
							+ sourceBean.getCostSheet());
					listSource.add(sourceBean);
				}
			}
		}
		logger.debug(methodName + "List of source beans " + listSource);
		long sourceEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sourceEndTime: ");
		logger.debug(methodName
				+ "Source Transform  Total Execution Time (ms): "
				+ (sourceEndTime - sourceStartTime));
		return listSource;
	}

	/**
	 * @param collProdToCostSheetIds
	 * @param source
	 * @param productObj
	 * @param seasons
	 * @param collProdToSKUIds
	 * @param mapTrackedCostSheet
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	private static List<CostSheet> getListCostSheets(
			Collection<String> collProdToCostSheetIds,
			LCSSourcingConfig source, LCSProduct productObj,
			Collection<LCSSeason> seasons, Collection<String> collProdToSKUIds,
			Map<String, Collection<HashMap>> mapTrackedCostSheet)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		String methodName = "getListCostSheets() ";
		long costSheetStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"costSheetStartTime: ");
		List<CostSheet> lstCostSheet = new ArrayList<CostSheet>();
		for (LCSSeason season : seasons) {
			// get cost sheet objects associated to source, season,product
			Collection costsheetObjs = LCSCostSheetQuery
					.getCostSheetsForProduct(new HashMap(), productObj, source,
							season, null, false, true, false, null, false,
							null, null);
			logger.debug(methodName + " costsheetObjs " + costsheetObjs.size());
			// Get cost sheet objects from the FlexObjects above
			Collection<LCSCostSheet> costsheets = LCSQuery
					.getObjectsFromResults(costsheetObjs,
							"VR:com.lcs.wc.sourcing.LCSProductCostSheet:",
							"LCSCOSTSHEET.BRANCHIDITERATIONINFO");
			for (LCSCostSheet costsheet : costsheets) {
				boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
						String.valueOf(costsheet.getBranchIdentifier()),
						collProdToCostSheetIds);
				// Check if exists
				if (idExists) {
					logger.debug(methodName + "costsheet added "
							+ costsheet.getName());
					lstCostSheet.add(BurberryProductCostingAPIJsonDataUtil
							.getCostSheetBean((LCSProductCostSheet) costsheet,
									collProdToSKUIds));
				}
			}
		}
		logger.debug(methodName + "list size " + lstCostSheet.size());

		// CR R26: Handle Remove Cost Sheet Customisation : start

		// Extraction of Removed Cost Sheet Bean Object data using Product and
		// Source
		// Object
		List<CostSheet> removedCostSheets = getRemovedCostSheets(productObj,
				source, mapTrackedCostSheet);
		lstCostSheet.addAll(removedCostSheets);

		// CR R26: Handle Remove Cost Sheet Customisation : End
		long costSheetEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"costSheetEndTime: ");
		logger.debug(methodName
				+ "cost sheet Transform  Total Execution Time (ms): "
				+ (costSheetEndTime - costSheetStartTime));
		return lstCostSheet;
	}

	// CR R26: Handle Remove Cost Sheet Customisation : start

	
	/**
	 * @param productObj
	 * @param source
	 * @param mapTrackedCostSheet
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static List<CostSheet> getRemovedCostSheets(LCSProduct productObj,
			LCSSourcingConfig source,
			Map<String, Collection<HashMap>> mapTrackedCostSheet)
			throws WTException, IllegalAccessException,
			InvocationTargetException {

		String methodName = "getRemovedCostSheets() ";
		List<CostSheet> removedCostSheets = new ArrayList<CostSheet>();

		// Check tracked map contains product id
		if (mapTrackedCostSheet.containsKey(String.valueOf(productObj
				.getBranchIdentifier()))) {
			// Get the collection for this Product
			Collection<HashMap> colMap = mapTrackedCostSheet.get(String
					.valueOf(productObj.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Source Id
				String sourceId = String.valueOf(hm.get("OWNER_ID"));

				// Check Source Id
				if (FormatHelper.hasContent(sourceId)
						&& sourceId.equalsIgnoreCase(String.valueOf(source
								.getBranchIdentifier()))) {
					// Get the removed moa id
					String removedCSRow = (String) hm.get("MOA_OBJECT_ID");
					// Initialisation
					CostSheet removedCsBean = new CostSheet();

					BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(
							removedCsBean, BurProductCostingConstant.CSUNIQID,
							removedCSRow);

					logger.debug(methodName + "Removed Cost Sheet Bean: "
							+ removedCsBean);
					// Add to list
					removedCostSheets.add(removedCsBean);

				}
			}
		}

		logger.debug(methodName + "Removed Yarn Detail Bean Size: "
				+ removedCostSheets.size());
		return removedCostSheets;
	}
	// CR R26: Handle Remove Cost Sheet Customisation : End

}

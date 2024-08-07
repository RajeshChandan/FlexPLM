package com.sportmaster.wc.sourcing;

import java.util.List;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * SMCostSheetPlugin.java
 * Currency Conversion in Cost Sheet.
 * 
 * @author 'true' Ajay Kalkoti
 * @version  'true' 1.0
 * 
 */
public class SMCostSheetPlugin {

	// Creating LCSCostSheet static instance.
	private static LCSCostSheet lcsCostSheet;
	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMCostSheetPlugin.class);
	/**
	 * Refresh Exchange Rate.
	 */
	public static final String REFRESH_EXCHANGERATE = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smRefreshExchangeRate");
	/**
	 * Status.
	 */
	public static final String STATUS = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.vrdCSStatus");
	/**
	 * Season.
	 */
	public static final String BO_SEASON = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smFxSeason");
	/**
	 * Contract Currency.
	 */
	public static final String CONTRACT_CURRENCY = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smCsContractCurrency");
	/**
	 * Exchange Rate.
	 */
	public static final String BO_EXCHANGE_RATE = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smFxExchangeRate");
	/**
	 * Currency.
	 */
	public static final String BO_CURRENCY = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smFxCurrency");
	/**
	 * Exchange Rate.
	 */
	public static final String EXCHANGE_RATE = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smCsExchangeRate");
	/**
	 * Flex Path.
	 */
	public static final String FLEX_PATH = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.flexPath");
	/**
	 * Cost Sheet Status APPROVED.
	 */
	public static final String APPROVED = "smApproved";
	/**
	 * Cost Sheet Status Cancelled.
	 */
	public static final String CANCELLED = "smCancelled";

	/**
	 * LCS LIFE CYCLE MANAGED.
	 */
	public static final String LCSLIFECYCLEMANAGED = "LCSLifecycleManaged";

	/**
	 * Constructor.
	 */
	protected SMCostSheetPlugin() {

	}

	/**
	 * Main Contract Currency Convertor method.
	 * 
	 * @param wtObj - WTObject
	 */

	public static void currencyConverter(WTObject wtObj)

	{
		try {
			lcsCostSheet = (LCSCostSheet) wtObj;
			
			
			final String csStatus = (String) lcsCostSheet.getValue(STATUS);
			final Boolean isRefreshExchangeRateChecked = lcsCostSheet
					.getValue(REFRESH_EXCHANGERATE) != null ? (Boolean) lcsCostSheet
					.getValue(REFRESH_EXCHANGERATE) : false;
					
					
			// Check wether status is not approved and cancelled and refresh
			// rate is checked
			if (FormatHelper.hasContent(csStatus)
					&& !(((String)lcsCostSheet.getValue(STATUS)).equalsIgnoreCase(APPROVED))
					&& !(((String)lcsCostSheet.getValue(STATUS)).equalsIgnoreCase(CANCELLED))
					&& isRefreshExchangeRateChecked) {
				if (!FormatHelper.hasContent((String) lcsCostSheet
						.getValue(CONTRACT_CURRENCY))) {
					LOGGER.error("Contract Currency is null");
					throw new WTException();
				}
				final FlexType boType = lookupCurrencyConvTable();
				saveExchangeRate(processExchangeRateResults(
						processExchangeRate(boType), boType));

			}
			lcsCostSheet.setValue(REFRESH_EXCHANGERATE, false);

		} catch (WTPropertyVetoException ex1) {
			LOGGER.error("Exception in Cost Sheet:" + ex1.getMessage());
		} catch (WTException ex2) {
			LOGGER.error("Exception in Cost Sheet:" + ex2.getMessage());
		}
	}

	/**
	 * Looks up Flex Type for Currency Conversion in Lookup table.
	 * 
	 * @return FlexType
	 * @throws WTException
	 */
	public static FlexType lookupCurrencyConvTable() throws WTException {

		return FlexTypeCache.getFlexTypeFromPath(FLEX_PATH);
	}

	/**
	 * Query for the Exhange Rate from the database.
	 * @param boType  - FlexType
	 * @return  SearchResults
	 * @throws WTException - WTException
	 */

	public static SearchResults processExchangeRate(FlexType boType)
			throws WTException {
		String classNAME = "LCSLIFECYCLEMANAGED";
		String currency = (String) lcsCostSheet.getValue(CONTRACT_CURRENCY);
		final LCSSeason season = (LCSSeason) VersionHelper
				.latestIterationOf(lcsCostSheet.getSeasonMaster());

		final Long seasonBranchId = season.getBranchIdentifier();

		final PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendSelectColumn(LCSLIFECYCLEMANAGED,
				boType.getAttribute(BO_EXCHANGE_RATE).getColumnName());
		statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
				BO_CURRENCY).getColumnName(), currency, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
				BO_SEASON).getColumnName(), seasonBranchId.toString(),
				Criteria.EQUALS));
		return LCSQuery.runDirectQuery(statement);

	}

	/**
	 * Process the exchange rate results.
	 * 
	 * @param results - SearchResults
	 * @param boType - FlexType
	 * @return String
	 * @throws WTException - WTException
	 */
	public static String processExchangeRateResults(SearchResults results,
			FlexType boType) throws WTException {
		final List<?> dataCollection = results.getResults();
		if (dataCollection.size() > 0) {
			for (Object obj1 : dataCollection) {
				final FlexObject fo = (FlexObject) obj1;
				return fo
						.getString("LCSLIFECYCLEMANAGED."
								+ boType.getAttribute(BO_EXCHANGE_RATE)
										.getColumnName());
			}
		}

		return null;
	}

	/**
	 * Set the exchange Rate to LCS Cost Sheet .
	 * 
	 * @param exchangRate - String
	 * @throws WTException - WTException
	 */
	public static void saveExchangeRate(String exchangRate) throws WTException {

		if (FormatHelper.hasContent(exchangRate)) {
			lcsCostSheet.setValue(EXCHANGE_RATE, exchangRate);
		}

	}

}
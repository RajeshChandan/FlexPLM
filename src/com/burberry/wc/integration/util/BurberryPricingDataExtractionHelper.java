package com.burberry.wc.integration.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import wt.access.NotAuthorizedException;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.burberry.wc.integration.bean.*;
import com.burberry.wc.integration.bean.Pricing.PricingStyle;
import com.burberry.wc.integration.exception.*;
import com.ibm.icu.text.SimpleDateFormat;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.foundation.LCSRevisableEntity;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.*;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A utility class to handle Product retrieving activity. Class contain several
 * method to extract specific data from the collection object fetched from
 * Database. Class has several method to extract data specific to Flex type i.e.
 * Season, Colorway. Class receive a collection of Flex object returned by DB
 * operation and apply several manipulation to filter exact info and put them in
 * different relevant Map and List. Resulted Map/list are then used to populate
 * Bean specific data object. As response this class generate a full fledged
 * Product bean as per schema which later processed as REST response.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryPricingDataExtractionHelper implements Serializable,
		RemoteAccess {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPricingDataExtractionHelper.class);

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2037004007719079486L;

	private static final String COSTSHEETS = "costsheets";

	/**
	 * BurberryPricingDataExtractionHelper.
	 */
	private BurberryPricingDataExtractionHelper() {

	}

	/**
	 * Return root bean object after retrieving and manipulating data.
	 * 
	 * @param sDate
	 *            start date
	 * @param eDate
	 *            end date
	 * @param seasons
	 *            number of season
	 * @return Pricing
	 * @throws WTException
	 * @throws ParseException
	 * @throws BurException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */

	public static Object getPricing(final String sDate, final String eDate,
			final String seasons) throws NoSuchFieldException,
			SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		String methodName = "getPricing() ";
		boolean previousEnforcement = true;
		BurberryLogFileGenerator.configureLog();
		final Pricing productBean = ObjectFactory.createPricing();
		Map<Status, Object> responseMap = new HashMap<Status, Object>();
		try {

			// Momentarily suspend access control enforcement.

			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);
			Map<String, Date> dateMap = MapUtil.getDates(sDate, eDate);

			if (!dateMap.isEmpty()) {
				Date startdate = dateMap.get("startdate");
				Date enddate = dateMap.get("enddate");
				logger.debug(methodName + "startdate: " + startdate
						+ ", enddate: " + enddate + ", seasons: " + seasons);
				final Iterator<?> proItr = getProductcollection(startdate,
						enddate, seasons).iterator();

				// iterate product collection
				while (proItr.hasNext()) {

					final LCSProduct product = getProductObject((FlexObject) proItr
							.next());
					logger.info(methodName + "LCSProduct: " + product.getName());

					// get sku collection for each Product
					final Iterator<?> skuItr = getSkusListCollectionIterator(product);
					if (skuItr != null) {
						updatePlaceholder(productBean, product, skuItr,
								startdate, enddate, seasons);
					}
				}
			}
			
			logger.info(methodName + " Returning Product Bean "+productBean);
			responseMap.put(Status.OK, productBean);

		} catch (final NotAuthorizedException e) {
			responseMap.put(Status.UNAUTHORIZED,
					BurberryProductDataExtractionHelper.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRICING, e.getMessage(),
							Status.UNAUTHORIZED));
		} catch (final ParseException e) {
			responseMap.put(Status.INTERNAL_SERVER_ERROR,
					BurberryProductDataExtractionHelper.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRICING,
							BurConstant.STR_ERROR_MSG,
							Status.INTERNAL_SERVER_ERROR));
		} catch (final WTException e) {
			responseMap.put(Status.INTERNAL_SERVER_ERROR,
					BurberryProductDataExtractionHelper.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRICING, e.getMessage(),
							Status.INTERNAL_SERVER_ERROR));
		} catch (final InvalidInputException e) {
			responseMap.put(Status.BAD_REQUEST,
					BurberryProductDataExtractionHelper.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRICING, e.getMessage(),
							Status.BAD_REQUEST));
		} catch (final NoRecordFoundException e) {
			responseMap.put(Status.OK, BurberryProductDataExtractionHelper
					.getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRICING,
							e.getMessage(), Status.OK));
		} catch (BurException e) {
			responseMap.put(Status.BAD_REQUEST,
					BurberryProductDataExtractionHelper.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRICING, e.getMessage(),
							Status.BAD_REQUEST));
		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}

		/*
		 * commenting below section as No Record Found can not be parsed as
		 * error for Burberry Data Service. However keeping below logic for
		 * future change if required
		 */
		// throw exception if matches no record fetched.
		// sendNoRecordFoundException(productBean);

		return responseMap;

	}

	/**
	 * Get iterator of collection
	 * 
	 * @param product
	 *            LCSProduct
	 * @return Iterator
	 * @throws WTException
	 */
	private static Iterator<?> getSkusListCollectionIterator(
			final LCSProduct product) throws WTException {
		Iterator<?> prodItr = null;

		prodItr = product != null ? LCSSKUQuery.findSKUs(product).iterator()
				: null;

		return prodItr;
	}

	/**
	 * @param productBean
	 * @param listOfSkuMaps
	 * @param listOfSkuseasonMaps
	 * @param product
	 * @param skuItr
	 * @throws WTException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ParseException
	 */
	private static void updatePlaceholder(final Pricing productBean,
			final LCSProduct product, final Iterator<?> skuItr, Date startDate,
			Date endDate, final String seasons) throws WTException,
			NoSuchFieldException, SecurityException, NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException {

		String methodName = "updatePlaceHolders() ";
		final List<Map<String, Object>> listOfSkuMaps = new ArrayList<Map<String, Object>>();
		final List<Map<String, Object>> listOfSkuseasonMaps = new ArrayList<Map<String, Object>>();
		Boolean approved;
		PricingStyle styleBean;
		String productmodifyStamp = BurberryDataUtil.getLastModify(product);
		final SimpleDateFormat sf = new SimpleDateFormat(BurConstant.dateFormat);
		Date productModify = sf.parse(productmodifyStamp);

		// True - if product is not modified between given time stamps
		if (productModify.compareTo(startDate) < 0
				|| productModify.compareTo(endDate) > 0) {
			logger.debug(methodName
					+ "product is not modified between given time stamps");
			while (skuItr.hasNext()) {
				approved = false;
				LCSSKU sku = null;
				sku = getLCSSkuObject((LCSSKU) skuItr.next());
				logger.debug(methodName + "LCSSKU: " + sku.getName());
				String skuLastModify = BurberryDataUtil.getLastModify(sku);

				Date skuModify = sf.parse(skuLastModify);
				final Iterator<?> seasonSkuItr = getseasonSkuListiterator(sku);

				// True - if colorway is modified between given time stamps
				if (skuModify.compareTo(startDate) >= 0
						&& skuModify.compareTo(endDate) <= 0) {

					if (seasonSkuItr != null) {
						while (seasonSkuItr.hasNext()) {
							final LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) seasonSkuItr
									.next();
							LCSSeason season = (LCSSeason) VersionHelper
									.latestIterationOf(skuSeason
											.getSeasonMaster());
							final String status = (String) skuSeason
									.getValue(BurConstant.STR_VALIDATION_STATUS);

							logger.debug(methodName + "Validation status: "
									+ status);

							String colourwaySeason = (String) skuSeason
									.getValue(BurConstant.BUR_SEASON);
							logger.debug(methodName + "colourwaySeason: "
									+ colourwaySeason);
							// get display value for colourway season set in Sku
							FlexTypeAttribute flexTypeAtt = skuSeason
									.getFlexType().getAttribute(
											BurConstant.BUR_SEASON);
							AttributeValueList attValList = flexTypeAtt
									.getAttValueList();
							String colourwaySeasonDisplayValue = (attValList
									.getValue(colourwaySeason,
											java.util.Locale.getDefault()));

							// Added after Phase - 1 release
							// True - if colourway season is set
							if (colourwaySeasonDisplayValue != null) {
								logger.debug(methodName
										+ "colourwaySeasonDisplayValue: "
										+ colourwaySeasonDisplayValue);
								logger.debug(methodName + "seasons: " + seasons);
								// check if season parameter matches with
								// colourway season
								if ((seasons == null)
										|| (!(FormatHelper.hasContent(seasons)))) {
									logger.debug(methodName
											+ "Season parameter is null hence adding ALL Approved Colourway Seasons ");
									// True - if status is approved for sampling
									if (isValidStat(skuSeason, status)) {

										// Added for Jira BURBERRY-1142
										List<Map<String, Object>> listOfskucostMaps = new ArrayList<Map<String, Object>>();
										approved = true;
										Map<String, Object> seasonMap = getSeasonMap(
												skuSeason, product, sku);
										seasonMap
												.put(BurConstant.SEASON_NAM,
														season.getValue(BurConstant.SEASON_NAM));
										listOfskucostMaps
												.addAll(getskucostMaps(
														skuSeason, product,
														sku, season));
										seasonMap.put(COSTSHEETS,
												listOfskucostMaps);

										// Added for Jira BURBERRY-1142
										listOfSkuseasonMaps.add(seasonMap);

									}
								} else if (seasons
										.contains(colourwaySeasonDisplayValue)) {
									logger.debug(methodName
											+ "Colourway Season is in season parameter: ");
									// True - if status is approved for sampling
									if (isValidStat(skuSeason, status)) {

										// Added for Jira BURBERRY-1142
										List<Map<String, Object>> listOfskucostMaps = new ArrayList<Map<String, Object>>();
										approved = true;
										Map<String, Object> seasonMap = getSeasonMap(
												skuSeason, product, sku);
										seasonMap
												.put(BurConstant.SEASON_NAM,
														season.getValue(BurConstant.SEASON_NAM));
										listOfskucostMaps
												.addAll(getskucostMaps(
														skuSeason, product,
														sku, season));
										seasonMap.put(COSTSHEETS,
												listOfskucostMaps);

										// Added for Jira BURBERRY-1142
										listOfSkuseasonMaps.add(seasonMap);
									}
								}
							}
						}
					}
				} else {
					if (seasonSkuItr != null) {
						while (seasonSkuItr.hasNext()) {
							final LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) seasonSkuItr
									.next();
							LCSSeason season = (LCSSeason) VersionHelper
									.latestIterationOf(skuSeason
											.getSeasonMaster());
							logger.debug(methodName + "LCSSKUSeasonLink: "
									+ skuSeason.getIdentity());

							String skuseasonLastModify = BurberryDataUtil
									.getLastModify(skuSeason);
							logger.debug(methodName + "skuseason modify date "
									+ skuseasonLastModify);

							Date skuseasonmodifydate = sf
									.parse(skuseasonLastModify);
							final String status = (String) skuSeason
									.getValue(BurConstant.STR_VALIDATION_STATUS);
							logger.debug(methodName + "Validation status: "
									+ status);

							String colourwaySeason = (String) skuSeason
									.getValue(BurConstant.BUR_SEASON);
							logger.debug(methodName + "colourwaySeason: "
									+ colourwaySeason);

							// get display value for colourway season set in Sku
							FlexTypeAttribute flexTypeAtt = skuSeason
									.getFlexType().getAttribute(
											BurConstant.BUR_SEASON);
							AttributeValueList attValList = flexTypeAtt
									.getAttValueList();
							String colourwaySeasonDisplayValue = (attValList
									.getValue(colourwaySeason,
											java.util.Locale.getDefault()));

							// True - if colourway season is set
							if (colourwaySeasonDisplayValue != null) {
								logger.debug(methodName
										+ "colourwaySeasonDisplayValue: "
										+ colourwaySeasonDisplayValue);
								logger.debug(methodName + "seasons: " + seasons);
								// check if season parameter matches with
								// colourway season
								if ((seasons == null)
										|| (!(FormatHelper.hasContent(seasons)))) {
									logger.debug(methodName
											+ "Season parameter is null hence adding ALL Approved Colourway Seasons ");
									// True - if status is approved for sampling
									if (isValidStat(skuSeason, status)
											&& skuseasonmodifydate
													.compareTo(startDate) >= 0
											&& skuseasonmodifydate
													.compareTo(endDate) <= 0) {
										List<Map<String, Object>> listOfskucostMaps = new ArrayList<Map<String, Object>>();
										approved = true;
										Map<String, Object> seasonMap = getSeasonMap(
												skuSeason, product, sku);
										seasonMap
												.put(BurConstant.SEASON_NAM,
														season.getValue(BurConstant.SEASON_NAM));
										listOfskucostMaps
												.addAll(getskucostMaps(
														skuSeason, product,
														sku, season));
										seasonMap.put(COSTSHEETS,
												listOfskucostMaps);
										listOfSkuseasonMaps.add(seasonMap);
									}
								} else if (seasons
										.contains(colourwaySeasonDisplayValue)) {
									logger.debug(methodName
											+ "Colourway Season is in season parameter: ");
									// True - if status is approved for sampling
									if (isValidStat(skuSeason, status)
											&& skuseasonmodifydate
													.compareTo(startDate) >= 0
											&& skuseasonmodifydate
													.compareTo(endDate) <= 0) {
										List<Map<String, Object>> listOfskucostMaps = new ArrayList<Map<String, Object>>();
										approved = true;
										Map<String, Object> seasonMap = getSeasonMap(
												skuSeason, product, sku);
										seasonMap
												.put(BurConstant.SEASON_NAM,
														season.getValue(BurConstant.SEASON_NAM));
										listOfskucostMaps
												.addAll(getskucostMaps(
														skuSeason, product,
														sku, season));
										seasonMap.put(COSTSHEETS,
												listOfskucostMaps);
										listOfSkuseasonMaps.add(seasonMap);
									}
								}
							}
						}
					}
				}

				// add sku to map if approved flag is true
				if (approved) {
					listOfSkuMaps.add(getSkuMap(sku));
				}
			}

		} else {
			logger.debug(methodName
					+ "product is  modified between given time stamps");
			while (skuItr.hasNext()) {
				approved = false;

				LCSSKU sku = null;

				sku = getLCSSkuObject((LCSSKU) skuItr.next());

				final Iterator<?> seasonSkuItr = getseasonSkuListiterator(sku);
				if (seasonSkuItr != null) {
					while (seasonSkuItr.hasNext()) {
						final LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) seasonSkuItr
								.next();
						LCSSeason season = (LCSSeason) VersionHelper
								.latestIterationOf(skuSeason.getSeasonMaster());
						final String status = (String) skuSeason
								.getValue(BurConstant.STR_VALIDATION_STATUS);

						logger.debug(methodName + "Validation status: "
								+ status);

						String colourwaySeason = (String) skuSeason
								.getValue(BurConstant.BUR_SEASON);
						logger.debug(methodName + "colourwaySeason: "
								+ colourwaySeason);

						// get display value for colourway season set in Sku
						FlexTypeAttribute flexTypeAtt = skuSeason.getFlexType()
								.getAttribute(BurConstant.BUR_SEASON);
						AttributeValueList attValList = flexTypeAtt
								.getAttValueList();
						String colourwaySeasonDisplayValue = (attValList
								.getValue(colourwaySeason,
										java.util.Locale.getDefault()));

						// True - if colourway season is set
						if (colourwaySeasonDisplayValue != null) {
							logger.debug(methodName
									+ "colourwaySeasonDisplayValue: "
									+ colourwaySeasonDisplayValue);
							logger.debug(methodName + "seasons: " + seasons);
							// check if season parameter matches with colourway
							// season
							if ((seasons == null)
									|| (!(FormatHelper.hasContent(seasons)))) {
								logger.debug(methodName
										+ "Season parameter is null hence adding ALL Approved Colourway Seasons ");
								// True - if status is approved for sampling
								if (isValidStat(skuSeason, status)) {
									List<Map<String, Object>> listOfskucostMaps = new ArrayList<Map<String, Object>>();
									approved = true;
									Map<String, Object> seasonMap = getSeasonMap(
											skuSeason, product, sku);
									seasonMap
											.put(BurConstant.SEASON_NAM,
													season.getValue(BurConstant.SEASON_NAM));
									listOfskucostMaps.addAll(getskucostMaps(
											skuSeason, product, sku, season));
									seasonMap
											.put(COSTSHEETS, listOfskucostMaps);
									listOfSkuseasonMaps.add(seasonMap);
								}
							} else if (seasons
									.contains(colourwaySeasonDisplayValue)) {
								logger.debug(methodName
										+ "Colourway Season is in season parameter: ");
								// True - if status is approved for sampling
								if (isValidStat(skuSeason, status)) {
									List<Map<String, Object>> listOfskucostMaps = new ArrayList<Map<String, Object>>();
									approved = true;
									Map<String, Object> seasonMap = getSeasonMap(
											skuSeason, product, sku);
									seasonMap
											.put(BurConstant.SEASON_NAM,
													season.getValue(BurConstant.SEASON_NAM));
									listOfskucostMaps.addAll(getskucostMaps(
											skuSeason, product, sku, season));
									seasonMap
											.put(COSTSHEETS, listOfskucostMaps);
									listOfSkuseasonMaps.add(seasonMap);
								}
							}
						}
					}
				}
				// add sku to map if approved flag is true
				if (approved) {
					listOfSkuMaps.add(getSkuMap(sku));
				}
			}
		}
		logger.info(methodName + "listOfSkuseasonMaps: " + listOfSkuseasonMaps);
		logger.info(methodName + "listOfSkuMaps: " + listOfSkuMaps);

		// update product bean if sku or skuseason map is populated
		if (!listOfSkuseasonMaps.isEmpty()) {
			styleBean = BeanUtil.putPricingData(getProducMap(product),
					listOfSkuMaps, listOfSkuseasonMaps);
			productBean.getStyle().add(styleBean);

		}
	}

	// Added to get collection of costsheets for Jira BURBERRY-1142
	private static Collection<? extends Map<String, Object>> getskucostMaps(
			LCSSKUSeasonLink skuSeason, LCSProduct product, LCSSKU sku,
			LCSSeason season) throws WTException {

		String methodName = "getskucostMaps() ";
		List<Map<String, Object>> listcosts = new ArrayList<Map<String, Object>>();
		final Iterator<?> costsheetItr = getCostsheets(product, skuSeason)
				.iterator();
		while (costsheetItr.hasNext()) {
			Map<String, Object> costMap = new HashMap<String, Object>();
			LCSCostSheet skucostsheet = (LCSCostSheet) costsheetItr.next();
			skucostsheet = (LCSCostSheet) VersionHelper
					.latestIterationOf(skucostsheet);
			logger.debug(methodName + "skucostsheet: " + skucostsheet.getName());
			final StringTokenizer attKeys = new StringTokenizer(
					BurConstant.PRICING_COSTSHEET_KEY, BurConstant.STRING_COMMA);
			if (isValid(skucostsheet, sku)) {
				costMap.put(BurConstant.NAME, sku.getName());
				costMap.put(BurConstant.SEASON_NAM,
						season.getValue(BurConstant.SEASON_NAM));
				while (attKeys.hasMoreTokens()) {
					final String attKey = attKeys.nextToken();
					logger.debug("Extracting data from costsheet "
							+ skucostsheet.getName());
					logger.debug(methodName + "updating costMap for: " + attKey);
					costMap.put(attKey, BurberryDataUtil.getData(skucostsheet,
							attKey, null));
				}
				listcosts.add(costMap);
			} else {
				logger.debug(methodName
						+ " colourway is not in the colourways list of costsheet ");
			}
		}
		logger.info(methodName + " listcosts " + listcosts);
		return listcosts;
	}

	/**
	 * @param skuSeason
	 * @param status
	 * @return
	 */
	public static boolean isValidStat(final LCSSKUSeasonLink skuSeason,
			final String status) {

		return (FormatHelper.hasContent(status))
				&& (BurConstant.STR_PASSED_KEY.equalsIgnoreCase(status))
				&& skuSeason.isEffectLatest() && !skuSeason.isSeasonRemoved();
	}

	/**
	 * Get collection object.
	 * 
	 * @param sku
	 *            LCSSKU object
	 * @return Collection
	 * @throws WTException
	 */
	private static Iterator<?> getseasonSkuListiterator(final LCSSKU sku)
			throws WTException {

		final Collection<?> seasonSkuList = new LCSSeasonQuery()
				.findSeasonProductLinks(((LCSPartMaster) sku.getMaster()));
		return seasonSkuList != null ? seasonSkuList.iterator() : null;
	}

	/**
	 * Get LCSSKU object.
	 * 
	 * @param sku
	 *            LCSSKU object
	 * @return LCSSKU
	 * @throws WTException
	 */
	private static LCSSKU getLCSSkuObject(final LCSSKU sku) throws WTException {

		return (LCSSKU) VersionHelper.latestIterationOf(sku);
	}

	/**
	 * Get LCSProduct object.
	 * 
	 * @param prod
	 *            FlexObject
	 * @return LCSProduct
	 * @throws WTException
	 */
	private static LCSProduct getProductObject(final FlexObject prod)
			throws WTException {

		LCSProduct product = null;

		product = (LCSProduct) LCSQuery
				.findObjectById(BurConstant.LCSPRODUCT_ROOT_ID
						+ prod.getString(BurConstant.LCSPRODUCT_IDA2A2));
		product = (LCSProduct) VersionHelper.latestIterationOf(product);

		return product;
	}

	/**
	 * Get Collection object.
	 * 
	 * @param startdate
	 *            start date
	 * @param enddate
	 *            end date
	 * @param seasons
	 *            n. of season
	 * @return Collection
	 * @throws ParseException
	 * @throws WTException
	 * @throws BurException
	 */
	private static Collection<?> getProductcollection(final Date startdate,
			final Date enddate, final String seasons) throws ParseException,
			WTException, BurException {

		logger.debug("Getting  data from  DB.");
		final Collection<?> list = BurberryDBHelper.getProducts(startdate,
				enddate, seasons);

		/*
		 * commenting below section as No Record Found can not be parsed as
		 * error for Burberry Data Service. However keeping below logic for
		 * future change if required
		 */
		// throw exception if matches no record fetched.
		/*
		 * if (list == null || list.size() <= 0) { throw new
		 * NoRecordFoundException(STR_NO_MATCHING_RECORD_FOUND); }
		 */

		// return
		return list;

	}

	/**
	 * Return updated Map.
	 * 
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param prod
	 *            LCSProduct
	 * @param sku
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	private static Map<String, Object> getSeasonMap(
			final LCSSKUSeasonLink skuSeason, final LCSProduct prod,
			final LCSSKU sku) throws WTException {

		Map<String, Object> seasonMap = new HashMap<String, Object>();
		logger.debug("Extracting data from colorway season "
				+ skuSeason.getIdentity());
		seasonMap.put(BurConstant.NAME, sku.getValue("skuName"));

		seasonMap = updateMapForCondition1(seasonMap, skuSeason);
		seasonMap = updateMapForCondition2(seasonMap, skuSeason, prod);
		// seasonMap = updateMapForCondition3(seasonMap, skuSeason, prod);
		seasonMap = updateMapForCondition4(seasonMap, skuSeason);
		seasonMap = updateMapForCondition5(seasonMap, skuSeason, prod);
		seasonMap = updateMapForSourcingConfiguration(seasonMap, skuSeason,
				prod);
		return seasonMap;
	}

	/**
	 * Update map with new data for give condition.
	 * 
	 * @param seasonMap2
	 *            Map
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param prod
	 *            LCSProduct
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	private static Map<String, Object> updateMapForCondition5(
			final Map<String, Object> seasonMap2,
			final LCSSKUSeasonLink skuSeason, final LCSProduct prod)
			throws WTException {

		String methodName = "updateMapForCondition5() ";
		final Map<String, Object> seasonMap = seasonMap2;

		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRICING_PRODUCTSEASON_KEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			final String attKey = attKeys.nextToken();

			final LCSProductSeasonLink productseason = getLCSProductSeasonLinkObject(
					skuSeason, prod);
			logger.debug(methodName + "LCSProduct: " + prod
					+ "LCSSKUSeasonLink: " + skuSeason
					+ "LCSProductSeasonLink: " + productseason);
			logger.debug(methodName + "updating seasonMap for productseason: "
					+ attKey);

			// True - if the attribute is one of the 'Adjusted Retail
			// EUR/USD/HKD'
			if (isValid(attKey)) {
				// method to extract Adjusted Retail Price attributes value
				// and update Season Map
				updateAdjustedRetailPriceInformation(productseason, attKey,
						seasonMap);

			} else {
				seasonMap.put(attKey, BurberryDataUtil.getData(
						(WTObject) productseason, attKey, null));
			}
		}
		// return updated map
		return seasonMap;
	}

	/**
	 * @param skuSeason
	 * @param prod
	 * @return
	 * @throws WTException
	 */
	private static LCSProductSeasonLink getLCSProductSeasonLinkObject(
			final LCSSKUSeasonLink skuSeason, final LCSProduct prod)
			throws WTException {

		final LCSSeason season = (LCSSeason) VersionHelper
				.latestIterationOf(skuSeason.getSeasonMaster());
		return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(
				prod, season);

	}

	/**
	 * @param attKey
	 * @return
	 */
	private static boolean isValid(final String attKey) {

		return BurConstant.BUR_ADJUSTED_EUR_RETAIL.equalsIgnoreCase(attKey)
				|| BurConstant.BUR_ADJUSTED_HKD_RETAIL.equalsIgnoreCase(attKey)
				|| BurConstant.BUR_ADJUSTED_USD_RETAIL.equalsIgnoreCase(attKey);
	}

	/**
	 * @param productseason
	 * @param attKey
	 * @param seasonMap
	 */
	private static void updateAdjustedRetailPriceInformation(
			final LCSProductSeasonLink productseason, final String attKey,
			Map<String, Object> seasonMap) {

		String methodName = "updateAdjustedRetailPriceInformation() ";

		try {
			// True if Adjusted Retail EUR/HKD/USD is set
			if (productseason.getValue(attKey) != null) {
				logger.debug(methodName
						+ "Adjusted Retail Price EUR/HKD/USD is set at Product Season");
				// get Adjusted Retail Price Info from Valid Price Band
				// and update Season map
				getLCSRevisableEntity(productseason, attKey, seasonMap);

			} else {
				logger.debug(methodName
						+ "Adjusted Retail Price attribute is not set at Product Season");

				// get Initial Price Band from Product Season as Adjusted Retail
				// Price is not set by user
				LCSRevisableEntity initialPriceBand = (LCSRevisableEntity) productseason
						.getValue(BurConstant.BUR_PRODUCTSEASONINITIALPRICEBAND);

				// True - if Initial Price Band is set at Product Season
				if (initialPriceBand != null) {

					logger.debug(methodName
							+ "Getting Actual Retail Price attribute from Initial Price Band");
					logger.debug(methodName + "LCSRevisableEntity: "
							+ initialPriceBand.getIdentity());

					String priceBandActualRetailkey = "";
					// getting appropriate attribute key from Intial Price Band
					// based on EUR/HKD/USD
					if (BurConstant.BUR_ADJUSTED_EUR_RETAIL
							.equalsIgnoreCase(attKey)) {
						priceBandActualRetailkey = BurConstant.BUR_PRICEBANDACTUALEURRETAIL;
					} else if (BurConstant.BUR_ADJUSTED_HKD_RETAIL
							.equalsIgnoreCase(attKey)) {
						priceBandActualRetailkey = BurConstant.BUR_PRICEBANDACTUALHKDRETAIL;
					} else if (BurConstant.BUR_ADJUSTED_USD_RETAIL
							.equalsIgnoreCase(attKey)) {
						priceBandActualRetailkey = BurConstant.BUR_PRICEBANDACTUALUSDRETAIL;
					}
					logger.debug(methodName + "priceBandActualRetailkey: "
							+ priceBandActualRetailkey);
					seasonMap.put(attKey, BurberryDataUtil.getData(
							(WTObject) initialPriceBand,
							priceBandActualRetailkey, null));
				} else {
					logger.debug(methodName
							+ "Initial Price Band is not set at Product Season");
				}
			}
		} catch (WTException e) {
			logger.error(methodName + "WtException: " + e);
		}
	}

	/**
	 * @param productseason
	 * @param attKey
	 * @param seasonMap
	 * @throws WTException
	 */
	private static void getLCSRevisableEntity(
			final LCSProductSeasonLink productseason, final String attKey,
			Map<String, Object> seasonMap) throws WTException {

		String methodName = "getLCSRevisableEntity() ";

		// get valid Price Band, set at Product Season as Adjusted Retail Price
		LCSRevisableEntity validPriceBandObj = ((LCSRevisableEntity) productseason
				.getValue(attKey));
		logger.debug(methodName
				+ "Getting Valid Retail Price attribute from Product Season");
		logger.debug(methodName + "LCSRevisableEntity: " + validPriceBandObj);

		String validPriceBandAttributekey = "";
		// getting appropriate attribute key from Valid Price Band based on
		// EUR/HKD/USD
		if (BurConstant.BUR_ADJUSTED_EUR_RETAIL.equalsIgnoreCase(attKey)) {
			validPriceBandAttributekey = BurConstant.BUR_VALIDPRICEBANDEUR;
		} else if (BurConstant.BUR_ADJUSTED_HKD_RETAIL.equalsIgnoreCase(attKey)) {
			validPriceBandAttributekey = BurConstant.BUR_VALIDPRICEBANDHKD;
		} else if (BurConstant.BUR_ADJUSTED_USD_RETAIL.equalsIgnoreCase(attKey)) {
			validPriceBandAttributekey = BurConstant.BUR_VALIDPRICEBANDUSD;
		}
		logger.debug(methodName + "validPriceBandAttributekey: "
				+ validPriceBandAttributekey);
		//
		seasonMap
				.put(attKey, BurberryDataUtil.getData(
						(WTObject) validPriceBandObj,
						validPriceBandAttributekey, null));

	}

	/**
	 * Update map with new data for give condition.
	 * 
	 * @param seasonMap2
	 *            Map
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	private static Map<String, Object> updateMapForCondition4(
			final Map<String, Object> seasonMap2,
			final LCSSKUSeasonLink skuSeason) throws WTException {

		String methodName = "updateMapForCondition4() ";
		final Map<String, Object> seasonMap = seasonMap2;
		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRICING_SEASON_KEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			final String attKey = attKeys.nextToken();
			final LCSSeason season = (LCSSeason) VersionHelper
					.latestIterationOf(skuSeason.getSeasonMaster());
			logger.debug(methodName + "updating seasonMap for season: "
					+ attKey);
			seasonMap.put(attKey,
					BurberryDataUtil.getData(season, attKey, null));
		}

		return seasonMap;
	}

	/**
	 * Validate state.
	 * 
	 * @param skucostsheet
	 *            LCSCostSheet
	 * @return
	 * @throws WTException
	 */
	private static boolean isValid(final LCSCostSheet skucostsheet, LCSSKU sku)
			throws WTException {
		Boolean isValid = false;
		boolean valid = false;
		String methodName = "isValid()";
		if (skucostsheet != null
				&& (skucostsheet instanceof LCSProductCostSheet)) {
			final Object state = skucostsheet
					.getValue(BurConstant.BUR_APPROVED_COLOURWAY_COST);
			isValid = state != null ? (Boolean) state : false;

		}
		// updated to check if costsheet has colourway being extracted in the
		// colourways list on costsheet ,Jira BURBERRY-1142
		if (isValid) {
			Collection<FlexObject> colorlist = LCSCostSheetQuery
					.getColorLinks((LCSCostSheetMaster) skucostsheet
							.getMaster());
			for (FlexObject fobj : colorlist) {
				logger.debug("flexobject " + fobj);
				LCSSKU costsheetsku = (LCSSKU) LCSQuery
						.findObjectById("VR:com.lcs.wc.product.LCSSKU:"
								+ fobj.getData("LCSSKU.BRANCHIDITERATIONINFO"));
				costsheetsku = (LCSSKU) VersionHelper
						.latestIterationOf(costsheetsku.getMaster());
				String skuMaster = FormatHelper
						.getNumericObjectIdFromObject(sku.getMaster());
				if (fobj.getData("LCSSKU.IDA3MASTERREFERENCE")
						.equals(skuMaster)) {
					valid = true;
					logger.debug(methodName + " " + skucostsheet.getName()
							+ " is valid for extraction " + valid);
					break;
				}
			}
		}

		return valid;

	}

	/**
	 * Return cost sheet.
	 * 
	 * @param prod
	 *            LCSProduct
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @return Collection
	 * @throws WTException
	 */
	private static Collection<?> getCostsheets(final LCSProduct prod,
			final LCSSKUSeasonLink skuSeason) throws WTException {

		Collection<?> costsheets = new ArrayList<>();
		final LCSSourcingConfig source = MapUtil.getSource(prod,skuSeason);
		// Fix for BURBERRY-1042
		if (source != null) {
			costsheets = (new LCSCostSheetQuery())
					.getCostSheetsForSourceToSeason(
							skuSeason.getSeasonMaster(), source.getMaster(),
							null, false, false);
		}
		return costsheets;
		// return LCSCostSheetQuery.getAllCostSheetsForSourcingConfig(source);
	}

	/**
	 * Update map with new data for give condition.
	 * 
	 * @param seasonMap2
	 *            Map
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param prod
	 *            LCSProduct
	 * @return Map
	 * @throws WTException
	 */
	private static Map<String, Object> updateMapForCondition2(
			final Map<String, Object> seasonMap2,
			final LCSSKUSeasonLink skuSeason, final LCSProduct prod)
			throws WTException {

		String methodName = "updateMapForCondition2() ";
		final Map<String, Object> seasonMap = seasonMap2;

		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRICING_SOURCE_KEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			final String attKey = attKeys.nextToken();

			final LCSSupplier vendor = getVendor(skuSeason, prod);
			if (vendor != null) {
				logger.debug(methodName + "vendor: " + vendor.getName());

				// add vendor name
				if (BurConstant.SUPPLIER_NAME.equalsIgnoreCase(attKey)) {
					logger.debug(methodName
							+ "updating seasonMap for supplier: " + attKey);
					seasonMap.put(attKey, vendor.getName());
				} else {
					logger.debug(methodName
							+ "updating seasonMap for supplier: " + attKey);
					seasonMap.put(attKey, BurberryDataUtil.getData(
							(WTObject) vendor, attKey, null));
				}
			}
		}
		// return updated map
		return seasonMap;
	}

	/**
	 * Return vendor object.
	 * 
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param prod
	 *            LCSProduct
	 * @return LCSSupplier
	 * @throws WTException
	 */
	private static LCSSupplier getVendor(final LCSSKUSeasonLink skuSeason,
			final LCSProduct prod) throws WTException {

		LCSSourcingConfig source = MapUtil.getSource(prod, skuSeason);
		return (source != null) ? (LCSSupplier) source
				.getValue(BurConstant.VENDOR) : null;
	}

	/**
	 * Update map with new data for give condition.
	 * 
	 * @param seasonMap2
	 *            Map
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @return Map
	 * @throws WTException
	 */
	private static Map<String, Object> updateMapForCondition1(
			final Map<String, Object> seasonMap2,
			final LCSSKUSeasonLink skuSeason) throws WTException {

		String methodName = "updateMapForCondition1() ";
		final Map<String, Object> seasonMap = seasonMap2;

		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRICING_SKUSEASON_KEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			final String attKey = attKeys.nextToken();

			if (BurConstant.MODIFY.equalsIgnoreCase(attKey)) {
				logger.debug(methodName
						+ "updating seasonMap for colourway season: " + attKey);
				seasonMap
						.put(attKey, BurberryDataUtil.getLastModify(skuSeason));
			} else {
				logger.debug(methodName
						+ "updating seasonMap for colourway season: " + attKey);
				seasonMap.put(attKey, BurberryDataUtil.getData(
						(WTObject) skuSeason, attKey, null));
			}

		}

		return seasonMap;
	}

	/**
	 * Return a map of SKU data.
	 * 
	 * @param sku
	 *            LCSSKU
	 * @return Map
	 * @throws WTException
	 */
	private static Map<String, Object> getSkuMap(final LCSSKU sku)
			throws WTException {

		String methodName = "getSkuMap() ";
		final Map<String, Object> skuMap = new HashMap<String, Object>();
		String attKey = "";
		logger.debug("Extracting data from colorway " + sku.getName());
		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRICING_SKU_KEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			attKey = attKeys.nextToken();
			if (BurConstant.MODIFY.equalsIgnoreCase(attKey)) {
				logger.debug(methodName + "updating skuMap for: " + attKey);
				skuMap.put(attKey, BurberryDataUtil.getLastModify(sku));
			} else if (BurConstant.NAME.equalsIgnoreCase(attKey)) {
				skuMap.put(attKey, sku.getValue("skuName"));
			} else {
				logger.debug(methodName + "updating skuMap for: " + attKey);
				skuMap.put(attKey,
						BurberryDataUtil.getData((WTObject) sku, attKey, null));
			}

		}

		return skuMap;
	}

	/**
	 * Return map of Product data.
	 * 
	 * @param product
	 *            LCSProduct
	 * @return Map
	 * @throws WTException
	 */
	private static Map<String, Object> getProducMap(final LCSProduct product)
			throws WTException {

		String methodName = "getProducMap() ";
		final Map<String, Object> productMap = new HashMap<String, Object>();
		String attKey = "";
		logger.debug("Extracting data from product " + product.getName());
		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRICING_PRODUCT_KEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			attKey = attKeys.nextToken();
			logger.debug(methodName + "updating productMap for: " + attKey);

			if (BurConstant.MODIFY.equalsIgnoreCase(attKey)) {
				productMap.put(attKey, BurberryDataUtil.getLastModify(product));
			} else if (BurConstant.NAME.equalsIgnoreCase(attKey)) {
				productMap.put(attKey, product.getName());
			} else if (BurConstant.BUR_OPERATIONAL_CATEGORY_APP
					.equalsIgnoreCase(attKey)) {

				productMap.put(attKey,
						MapUtil.getOperationalValue(product, attKey));
			} else if (BurConstant.BUR_BRAND.equalsIgnoreCase(attKey)) {
				productMap.put(attKey, MapUtil.getBrandValue(product, attKey));
			} else {
				productMap.put(attKey, BurberryDataUtil.getData(
						(WTObject) product, attKey, null));
			}
		}

		return productMap;
	}

	private static Map<String, Object> updateMapForSourcingConfiguration(
			final Map<String, Object> seasonMap2,
			final LCSSKUSeasonLink skuSeason, final LCSProduct prod) {

		String methodName = "updateMapForSourcingConfiguration() ";
		String pricingSourcingKey = "vrdCountryOfOrigin";
		Map<String, Object> seasonMap = seasonMap2;

		try {

			final StringTokenizer attKeys = new StringTokenizer(
					pricingSourcingKey, BurConstant.STRING_COMMA);
			logger.debug(methodName + "BurConstant.STRING_COMMA: "
					+ BurConstant.STRING_COMMA);
			logger.debug(methodName + "attKeys: " + attKeys);

			while (attKeys.hasMoreTokens()) {
				final String attKey = attKeys.nextToken();
				logger.debug(methodName + "attKey: " + attKey);

				// get sourcing config
				final LCSSourcingConfig sourcingConfig = MapUtil.getSource(prod, skuSeason);

				// if sourcing configuration exists
				if (sourcingConfig != null) {
					logger.debug(methodName + "Product Sourcing Config: "
							+ sourcingConfig.getName());
					logger.debug(methodName
							+ "Country of origin attribute value: "
							+ sourcingConfig.getValue(attKey));
					//
					LCSCountry country = null;

					// get country of origin from sourcing config
					country = (LCSCountry) (sourcingConfig.getValue(attKey));
					// if country of origin exists, get ISO Code attribute value
					if (country != null) {
						logger.debug(methodName + "country: "
								+ country.getName());

						// add ISO Code value to season map
						logger.debug(methodName
								+ "updating seasonMap for country: " + attKey);
						seasonMap.put(BurConstant.BUR_ISO, BurberryDataUtil
								.getData((WTObject) country,
										BurConstant.BUR_ISO, null));
					}
				}
			}

		} catch (WTException e) {
			logger.error(methodName + "WTEXCEPTION: " + e);
		}

		logger.debug(methodName + "updated seasonMap: " + seasonMap);
		// return
		return seasonMap;
	}

}

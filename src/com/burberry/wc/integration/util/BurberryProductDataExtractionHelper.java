package com.burberry.wc.integration.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import com.burberry.wc.integration.bean.Products.Style;
import com.burberry.wc.integration.exception.*;
import com.ibm.icu.text.SimpleDateFormat;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.*;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeasonQuery;
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
 * 
 *
 *
 * 
 *
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public final class BurberryProductDataExtractionHelper implements Serializable,
		RemoteAccess {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1584490796589048213L;
	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductDataExtractionHelper.class);
	/**
	 * STRING_BLANK_SPACE.
	 */
	private static final String STRING_BLANK_SPACE = " ";

	/**
	 * STRING_OBRAKET.
	 */
	private static final String STRING_OBRAKET = ")";

	/**
	 * STRING_CBRAKET.
	 */
	private static final String STRING_CBRAKET = " (";

	/**
	 * constructor.
	 */
	private BurberryProductDataExtractionHelper() {

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
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static Object getProducts(final String sDate, final String eDate,
			final String seasons) throws NoSuchFieldException,
			SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		String methodName = "getProducts() ";
		boolean previousEnforcement = true;
		BurberryLogFileGenerator.configureLog();
		final Products productBean = ObjectFactory.createProducts();
		Map<Status, Object> responseMap = new HashMap<Status, Object>();
		try {
			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);
			Map<String, Date> dateMap = MapUtil.getDates(sDate, eDate);
			// Initialisation of response map to be be sent back for request
			if (!dateMap.isEmpty()) {
				Date startdate = dateMap.get("startdate");
				Date enddate = dateMap.get("enddate");
				logger.debug(methodName + "startdate: " + startdate
						+ ", enddate: " + enddate + ", seasons: " + seasons);
				// get all Products modified based on the parameters
				final Iterator<?> proItr = getProductcollection(startdate,
						enddate, seasons).iterator();
				// iterate product collection
				while (proItr.hasNext()) {

					final List<Map<String, Object>> skuseasonMaps = new ArrayList<Map<String, Object>>();
					final List<Map<String, Object>> skuMaps = new ArrayList<Map<String, Object>>();

					final LCSProduct product = getProductObject((FlexObject) proItr
							.next());
					logger.info(methodName + "LCSProduct: " + product.getName());
					// get sku collection for each Product
					final Iterator<?> skuItr = getSkuCollectionIterator(product);

					if (skuItr != null) {
						updatePlaceHolders(productBean, skuseasonMaps, skuMaps,
								product, skuItr, startdate, enddate, seasons);
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
		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}

		// }catch

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
	 * @param strErrorMsg
	 * @param message
	 * @param status
	 * @return
	 */
	public static Object getErrorResponseBean(String strErrorMsg,
			String message, Status status) {
		final ErrorMessage errorMessage = new ErrorMessage(strErrorMsg,
				status.getStatusCode(), message);
		return errorMessage;
	}

	/**
	 * Update beans
	 * 
	 * @param productBean
	 *            Products
	 * @param skuseasonMaps
	 *            List
	 * @param skuMaps
	 *            List
	 * @param product
	 *            LCSProduct
	 * @param skuItr
	 *            Iterator
	 * @throws WTException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	private static void updatePlaceHolders(final Products productBean,
			final List<Map<String, Object>> skuseasonMaps,
			final List<Map<String, Object>> skuMaps, final LCSProduct product,
			final Iterator<?> skuItr, Date startDate, Date endDate,
			final String seasons) throws WTException, NoSuchFieldException,
			SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException {

		String methodName = "updatePlaceHolders() ";
		boolean approved;
		final SimpleDateFormat sf = new SimpleDateFormat(BurConstant.dateFormat);
		sf.setLenient(false);
		String status = "";
		String productmodifyStamp = BurberryDataUtil.getLastModify(product);
		Date productModify = sf.parse(productmodifyStamp);

		// True - if product is not modified between given time stamps
		if (productModify.compareTo(startDate) < 0
				|| productModify.compareTo(endDate) > 0) {
			logger.debug(methodName
					+ "product is not modified between given time stamps");
			while (skuItr.hasNext()) {
				approved = false;
				final LCSSKU sku = (LCSSKU) VersionHelper
						.latestIterationOf((LCSSKU) skuItr.next());
				logger.debug(methodName + "LCSSKU: " + sku.getName());

				String skuLastModify = BurberryDataUtil.getLastModify(sku);

				Date skuModify = sf.parse(skuLastModify);

				// True - if colorway is modified between given time stamps
				if ((skuModify.after(startDate) && (skuModify.before(endDate)))) {
					logger.debug(methodName
							+ "colorway is  modified between given time stamps");

					final Iterator<?> seasonSkuItr = new LCSSeasonQuery()
							.findSeasonProductLinks(
									(LCSPartMaster) sku.getMaster()).iterator();
					while (seasonSkuItr.hasNext()) {
						final LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) seasonSkuItr
								.next();
						status = (String) skuSeason
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
								if ((FormatHelper.hasContent(status))
										&& (BurConstant.STR_PASSED_KEY
												.equalsIgnoreCase(status))
										&& skuSeason.isEffectLatest()) {
									approved = true;
									skuseasonMaps.add(MapUtil.getSeasonMap(
											skuSeason, product, sku));
								}
							} else if (seasons
									.contains(colourwaySeasonDisplayValue)) {
								logger.debug(methodName
										+ "Colourway Season is in season parameter: ");
								// True - if status is approved for sampling
								if ((FormatHelper.hasContent(status))
										&& (BurConstant.STR_PASSED_KEY
												.equalsIgnoreCase(status))
										&& skuSeason.isEffectLatest()) {
									approved = true;
									skuseasonMaps.add(MapUtil.getSeasonMap(
											skuSeason, product, sku));
								}
							}
						}
					}

				} else {
					final Iterator<?> seasonSkuItr = new LCSSeasonQuery()
							.findSeasonProductLinks(
									(LCSPartMaster) sku.getMaster()).iterator();
					while (seasonSkuItr.hasNext()) {
						final LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) seasonSkuItr
								.next();
						logger.debug(methodName + "LCSSKUSeasonLink: "
								+ skuSeason.getIdentity());

						String skuseasonLastModify = BurberryDataUtil
								.getLastModify(skuSeason);
						logger.debug(methodName + "skuseason modify date "
								+ skuseasonLastModify);

						Date skuseasonmodifydate = sf
								.parse(skuseasonLastModify);
						status = (String) skuSeason
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
								if ((FormatHelper.hasContent(status))
										&& (BurConstant.STR_PASSED_KEY
												.equalsIgnoreCase(status))
										&& skuSeason.isEffectLatest()
										&& skuseasonmodifydate
												.compareTo(startDate) >= 0
										&& skuseasonmodifydate
												.compareTo(endDate) <= 0) {
									logger.debug(methodName
											+ "colorway season is  modified between given time stamps");
									approved = true;
									skuseasonMaps.add(MapUtil.getSeasonMap(
											skuSeason, product, sku));
								}
							} else if (seasons
									.contains(colourwaySeasonDisplayValue)) {
								logger.debug(methodName
										+ "Colourway Season is in season parameter: ");
								// True - if status is approved for sampling
								if ((FormatHelper.hasContent(status))
										&& (BurConstant.STR_PASSED_KEY
												.equalsIgnoreCase(status))
										&& skuSeason.isEffectLatest()
										&& skuseasonmodifydate
												.compareTo(startDate) >= 0
										&& skuseasonmodifydate
												.compareTo(endDate) <= 0) {
									logger.debug(methodName
											+ "colorway season is  modified between given time stamps");
									approved = true;
									skuseasonMaps.add(MapUtil.getSeasonMap(
											skuSeason, product, sku));
								}
							}
						}
					}
				}
				// add sku to map if approved flag is true
				if (approved) {
					skuMaps.add(getSkuMap(sku));
				}
			}

		} else {
			logger.debug(methodName
					+ "product is  modified between given time stamps");
			while (skuItr.hasNext()) {
				approved = false;
				final LCSSKU sku = (LCSSKU) VersionHelper
						.latestIterationOf((LCSSKU) skuItr.next());
				logger.debug(methodName + "sku last modified "
						+ BurberryDataUtil.getLastModify(sku));

				final Iterator<?> seasonSkuItr = new LCSSeasonQuery()
						.findSeasonProductLinks((LCSPartMaster) sku.getMaster())
						.iterator();
				while (seasonSkuItr.hasNext()) {
					final LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) seasonSkuItr
							.next();
					status = (String) skuSeason
							.getValue(BurConstant.STR_VALIDATION_STATUS);
					logger.debug(methodName + "Validation status: " + status);

					String colourwaySeason = (String) skuSeason
							.getValue(BurConstant.BUR_SEASON);
					logger.debug(methodName + "colourwaySeason: "
							+ colourwaySeason);

					// get display value for colourway season set in Sku
					FlexTypeAttribute flexTypeAtt = skuSeason.getFlexType()
							.getAttribute(BurConstant.BUR_SEASON);
					AttributeValueList attValList = flexTypeAtt
							.getAttValueList();
					String colourwaySeasonDisplayValue = (attValList.getValue(
							colourwaySeason, java.util.Locale.getDefault()));

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
							if ((FormatHelper.hasContent(status))
									&& (BurConstant.STR_PASSED_KEY
											.equalsIgnoreCase(status))
									&& skuSeason.isEffectLatest()) {
								logger.debug(methodName + "skuseason "
										+ skuSeason);
								approved = true;
								skuseasonMaps.add(MapUtil.getSeasonMap(
										skuSeason, product, sku));
							}
						} else if (seasons
								.contains(colourwaySeasonDisplayValue)) {
							logger.debug(methodName
									+ "Colourway Season is in season parameter: ");
							// True - if status is approved for sampling
							if ((FormatHelper.hasContent(status))
									&& (BurConstant.STR_PASSED_KEY
											.equalsIgnoreCase(status))
									&& skuSeason.isEffectLatest()) {
								logger.debug(methodName + "skuseason "
										+ skuSeason);
								approved = true;
								skuseasonMaps.add(MapUtil.getSeasonMap(
										skuSeason, product, sku));
							}
						}
					}
				}
				// add sku to map if approved flag is true
				if (approved) {
					skuMaps.add(getSkuMap(sku));
				}
			}
		}

		logger.info(methodName + "skuseasonMaps: " + skuseasonMaps);
		logger.info(methodName + "skuMaps: " + skuMaps);

		// update product bean if sku or skuseason map is populated
		if (!skuseasonMaps.isEmpty() && !skuMaps.isEmpty()) {
			logger.debug(methodName
					+ "calling putProductData to set values from Map to XML bean");
			final Style styleBean = BeanUtil.putProductData(
					getProducMap(product), skuMaps, skuseasonMaps);

			productBean.getStyle().add(styleBean);

		}
	}

	/**
	 * Get iterator of collection
	 * 
	 * @param product
	 *            LCSProduct
	 * @return Iterator
	 * @throws WTException
	 */
	private static Iterator<?> getSkuCollectionIterator(final LCSProduct product)
			throws WTException {
		Iterator<?> prodItr = null;

		prodItr = product != null ? LCSSKUQuery.findSKUs(product).iterator()
				: null;

		return prodItr;
	}

	/**
	 * SendNoRecordFoundException.
	 * 
	 * @param productBean
	 *            Products
	 * @throws BurException
	 */
	private static void sendNoRecordFoundException(final Products productBean)
			throws BurException {

		// throw exception if matches no record fetched.
		if (productBean == null || productBean.getStyle() == null
				|| productBean.getStyle().size() <= 0) {
			throw new NoRecordFoundException(
					BurConstant.STR_NO_MATCHING_RECORD_FOUND);
		}

	}

	/**
	 * GetProductObject.
	 * 
	 * @param prod
	 *            FlexObject
	 * @return LCSProduct
	 * @throws WTException
	 */
	private static LCSProduct getProductObject(final FlexObject prod)
			throws WTException {
		logger.debug("Getting product info.");
		LCSProduct product = null;

		product = (LCSProduct) LCSQuery
				.findObjectById(BurConstant.LCSPRODUCT_ROOT_ID
						+ prod.getString(BurConstant.LCSPRODUCT_IDA2A2));
		product = ((LCSProduct) VersionHelper.latestIterationOf(product));
		logger.debug("product is " + product.getName());
		return product;
	}

	/**
	 * getProductcollection.
	 * 
	 * @param sDate
	 *            start date
	 * @param eDate
	 *            end date
	 * @param seasons
	 *            no. of season
	 * @return Collection
	 * @throws ParseException
	 * @throws WTException
	 * @throws InvalidInputException
	 * @throws NoRecordFoundException
	 */
	private static Collection<?> getProductcollection(final Date startdate,
			final Date enddate, final String seasons) throws ParseException,
			WTException, InvalidInputException, NoRecordFoundException {

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
		 * NoRecordFoundException( BurConstant.STR_NO_MATCHING_RECORD_FOUND); }
		 */

		// return
		return list;

	}

	/**
	 * Update map with new data.
	 * 
	 * @param sku
	 *            LCSSKU
	 * @return Map
	 * @throws WTException
	 */
	private static Map<String, Object> getSkuMap(final LCSSKU sku)
			throws WTException {

		Map<String, Object> skuMap = new HashMap<String, Object>();

		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRODCUT_SKU_KEY, BurConstant.STRING_REG_PATTERN);
		while (attKeys.hasMoreTokens()) {

			final String rootKey = attKeys.nextToken();
			final StringTokenizer typeKeys = new StringTokenizer(rootKey,
					BurConstant.STRING_COLON);
			final String typeKey = typeKeys.nextToken();
			final String attKey = typeKeys.nextToken();

			final WTObject obj = getWTObjectforSkuMap(typeKey, sku,
					getFlexType(sku, typeKey));

			skuMap = updatSkuMap(skuMap, attKey, sku, obj);

		}
		return skuMap;
	}

	/**
	 * Update give map with new data.
	 * 
	 * @param skuMap2
	 *            Map
	 * @param attKey
	 *            String
	 * @param sku
	 *            LCSSKU
	 * @param obj
	 *            WTObject
	 * @return Map
	 * @throws WTException
	 */
	private static Map<String, Object> updatSkuMap(
			final Map<String, Object> skuMap2, final String attKey,
			final LCSSKU sku, final WTObject obj) throws WTException {
		logger.debug("Extracting data from colorway " + sku.getName());
		final Map<String, Object> skuMap = skuMap2;
		final StringTokenizer typeKeys = new StringTokenizer(attKey,
				BurConstant.STRING_COMMA);
		if (obj != null) {
			while (typeKeys.hasMoreTokens()) {
				final String key = typeKeys.nextToken();
				if (BurConstant.MODIFY.equalsIgnoreCase(key)) {
					skuMap.put(key, BurberryDataUtil.getLastModify(obj));
				} else if (BurConstant.NAME.equalsIgnoreCase(key)) {

					skuMap.put(key, sku.getValue("skuName"));
				} else if (BurConstant.BUR_GENDER.equals(key)) {
					if (sku.getFlexType().attributeExist(key)) {
						FlexTypeAttribute att = sku.getFlexType().getAttribute(
								key);
						String attType = att.getAttVariableType();
						Object attValue = sku.getLogicalValue(key);
						skuMap.put(key, BurberryDataUtil.getValueForObjectType(
								attType, att, attValue, sku, key));
					}
				}// Commented as no need to check for colourType with latest
					// config
				/*
				 * else if (BurConstant.BUR_IP.equals(key)) { skuMap.put(key,
				 * checkColourType(obj, key)); }
				 */else {
					skuMap.put(key, BurberryDataUtil.getData(obj, key, null));
				}
			}
		}
		return skuMap;
	}

	// Commented as no need to check for colourType with latest config
	/*
	 * private static Object checkColourType(WTObject obj, String key) throws
	 * WTException { String value = null; if
	 * (BurConstant.COLOURSTOCHECK.contains(((LCSColor) obj).getFlexType()
	 * .getFullName())) { value = BurberryDataUtil.getData(obj, key, null); }
	 * return value; }
	 */

	/**
	 * Return FLEX type.
	 * 
	 * @param sku
	 *            LCSSKU
	 * @param typeKey
	 *            String
	 * @return FlexType
	 * @throws WTException
	 */
	private static FlexType getFlexType(final LCSSKU sku, final String typeKey)
			throws WTException {
		FlexType fType = null;
		if (BurConstant.COLOUR.equalsIgnoreCase(typeKey)) {
			LCSColor color = (LCSColor) sku.getValue(BurConstant.COLOR);
			fType = (color != null) ? color.getFlexType() : null;

		}
		return fType;
	}

	/**
	 * Return WTObject type.
	 * 
	 * @param typeKey
	 *            String
	 * @param sku
	 *            LCSSKU
	 * @param fType
	 *            FlexType
	 * @return WTObject
	 * @throws WTException
	 */
	private static WTObject getWTObjectforSkuMap(final String typeKey,
			final LCSSKU sku, final FlexType fType) throws WTException {
		WTObject obj = null;

		if (BurConstant.COLOURWAY.equalsIgnoreCase(typeKey)) {
			obj = sku;
		}
		if (BurConstant.COLOUR.equalsIgnoreCase(typeKey)) {
			obj = (WTObject) sku.getValue(BurConstant.COLOR);
		}
		return obj;
	}

	/**
	 * Return a map containing product data.
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
		String attKey;
		logger.debug(methodName + "Extracting data from product "
				+ product.getName());
		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRODCUT_PRODUCTKEY, BurConstant.STRING_COMMA);
		while (attKeys.hasMoreTokens()) {
			attKey = attKeys.nextToken();
			logger.debug(methodName + "updating productMap for: " + attKey);

			if (BurConstant.MODIFY.equalsIgnoreCase(attKey)) {
				productMap.put(attKey, BurberryDataUtil.getLastModify(product));
			} else if (BurConstant.NAME.equalsIgnoreCase(attKey)) {
				productMap.put(attKey, product.getName());
			} else if (BurConstant.BUR_MAIN_RM.equalsIgnoreCase(attKey)) {
				final LCSMaterial primMat = (LCSMaterial) product
						.getValue(attKey);
				if (primMat != null) {
					productMap.put(attKey, getFiberContent(primMat));
				}
			} else if (BurConstant.BUR_OPERATIONAL_CATEGORY_APP
					.equalsIgnoreCase(attKey)) {
				//
				productMap.put(attKey,
						MapUtil.getOperationalValue(product, attKey));

			} else if (BurConstant.BUR_BRAND.equalsIgnoreCase(attKey)) {
				//
				productMap.put(attKey, MapUtil.getBrandValue(product, attKey));
			} else {
				productMap.put(attKey,
						BurberryDataUtil.getData(product, attKey, null));
			}
		}
		return productMap;
	}

	private static Object getFiberContent(LCSMaterial primMat)
			throws WTException {
		final FlexType matType = primMat.getFlexType();
		logger.debug("Main RM Type  " + matType.getFullName(true));
		String value = null;
		if (BurConstant.MATERIALSTOCHECK.contains(matType.getFullName(true))) {
			value = (String) primMat.getValue(BurConstant.BUR_CONTENT)
					+ STRING_BLANK_SPACE
					+ BurberryDataUtil.getData((WTObject) primMat,
							BurConstant.BUR_COMMON_NAME, null)
					+ STRING_CBRAKET
					+ BurberryDataUtil.getData((WTObject) primMat,
							BurConstant.BUR_LATIN_NAME, null) + STRING_OBRAKET;
		} else {
			value = (String) BurberryDataUtil.getData((WTObject) primMat,
					BurConstant.VRD_FIBER_CONTENT, null);

		}
		return value;
	}

}

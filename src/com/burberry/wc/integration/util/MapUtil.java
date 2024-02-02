/**
 * 
 */
package com.burberry.wc.integration.util;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.util.WTException;

import com.burberry.wc.integration.exception.InvalidInputException;
import com.ibm.icu.text.SimpleDateFormat;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.*;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.part.LCSPartMaster;

/**
 *
 * Utility class to handle data operation on map.
 * 
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public final class MapUtil {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPricingDataExtractionHelper.class);

	/**
	 * constructor.
	 */
	private MapUtil() {

	}

	/**
	 * Update map with new data.
	 * 
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param prod
	 *            LCSProduct
	 * @param sku
	 *            LCSSKU
	 * @return
	 * @throws WTException
	 */
	static Map<String, Object> getSeasonMap(final LCSSKUSeasonLink skuSeason,
			final LCSProduct prod, final LCSSKU sku) throws WTException {

		String methodName = "getSeasonMap() ";
		Map<String, Object> seasonMap = new HashMap<String, Object>();
		logger.debug(methodName + "prod: " + prod + " || sku: " + sku);

		seasonMap.put(BurConstant.NAME, sku.getValue("skuName"));

		final StringTokenizer attKeys = new StringTokenizer(
				BurConstant.PRODUCT_SKUSEASON_KEY,
				BurConstant.STRING_REG_PATTERN);
		while (attKeys.hasMoreTokens()) {
			final String rootKey = attKeys.nextToken();
			final StringTokenizer typeKeys = new StringTokenizer(rootKey,
					BurConstant.STRING_COLON);
			final String typeKey = typeKeys.nextToken();
			final String attKey = typeKeys.nextToken();

			final WTObject obj = getWTObject(prod, skuSeason, typeKey);
			logger.debug(methodName + "WTObject: " + obj);

			seasonMap = updateSeasonMap(seasonMap, skuSeason, obj, attKey);
		}
		logger.debug(methodName + "seasonMap: " + seasonMap);
		// return
		return seasonMap;
	}

	/**
	 * Update map with new data.
	 * 
	 * @param seasonMap2
	 *            Map
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param obj
	 *            WTObject
	 * @param attKey
	 *            String
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	private static Map<String, Object> updateSeasonMap(
			final Map<String, Object> seasonMap2,
			final LCSSKUSeasonLink skuSeason, final WTObject obj,
			final String attKey) throws WTException {

		final Map<String, Object> seasonMap = seasonMap2;
		final StringTokenizer typeKeys = new StringTokenizer(attKey,
				BurConstant.STRING_COMMA);
		while (typeKeys.hasMoreTokens()) {
			final String key = typeKeys.nextToken();
			if (BurConstant.MODIFY.equalsIgnoreCase(key)) {
				seasonMap.put(key, BurberryDataUtil.getLastModify(skuSeason));
			} else if (obj != null
					&& BurConstant.SUPPLIER_NAME.equalsIgnoreCase(key)) {
				seasonMap.put(key, ((LCSSupplier) obj).getName());

			} else if (obj != null) {
				final String value = BurberryDataUtil.getData(obj, key, null);
				if (value != null) {
					seasonMap.put(key, value);
				}
			}
		}
		logger.debug("updateSeasonMap() seasonMap: " + seasonMap);
		// return
		return seasonMap;
	}

	/**
	 * Return WTObject.
	 * 
	 * @param prod
	 *            LCSProduct
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param typeKey
	 *            String
	 * @return
	 * @throws WTException
	 */
	private static WTObject getWTObject(final LCSProduct prod,
			final LCSSKUSeasonLink skuSeason, final String typeKey)
					throws WTException {

		String methodName = "getWTObject() ";
		WTObject obj=null;
		logger.debug(methodName + "typeKey: " + typeKey);
		logger.debug(methodName + "LCSProduct: " + prod);
		logger.debug(methodName + "LCSSKUSeasonLink: " + skuSeason);

		if (BurConstant.SEASON.equalsIgnoreCase(typeKey)) {
			obj= VersionHelper.latestIterationOf(skuSeason.getSeasonMaster());
		}
		if (BurConstant.SKUSEASON.equalsIgnoreCase(typeKey)) {
			obj= skuSeason;
		}
		if (BurConstant.SOURCE.equalsIgnoreCase(typeKey)) {
			obj= getSource(prod, skuSeason);
		}
		if(BurConstant.COUNTRY.equalsIgnoreCase(typeKey)){
			obj=getCountry(prod,skuSeason);
		}
		if (BurConstant.VENDOR.equalsIgnoreCase(typeKey)) {

			// get primary source for product season
			LCSSourcingConfig source = (LCSSourcingConfig) getSource(prod, skuSeason);
			// get finished goods vendor if primary source is defined
			if (source != null) {
				logger.debug(methodName + "source: " + source.getIdentity());
				obj= getVendor(source);
			} else { 
				logger.debug(methodName + "Primary source is null or not defined: ");
			}
		}
		if (BurConstant.FLEXBOM.equalsIgnoreCase(typeKey)
				|| BurConstant.MATERIAL.equalsIgnoreCase(typeKey)) {
			obj= getMaterial(getSource(prod, skuSeason), skuSeason, prod,
					typeKey);
		}
		logger.debug(methodName + "obj: " + obj);
		// return obj
		return obj;
	}

	private static LCSCountry getCountry(LCSProduct prod,
			LCSSKUSeasonLink skuSeason) throws WTException {

		String methodName = "getCountry() ";
		LCSSourcingConfig source = getSource(prod,skuSeason);
		LCSCountry country = null;
		// True - if primary source is defined
		if(source!=null){
			logger.debug(methodName + "source: " + source.getIdentity());
			// get planned fg coo from primary source
			country=(LCSCountry) ((source.getValue(BurConstant.VRD_COUNTRY_OF_ORIGIN)!=null)?(source.getValue(BurConstant.VRD_COUNTRY_OF_ORIGIN)):null);
		}
		return country;
	}

	/**
	 * Return LCSSourcingConfig object.
	 * 
	 * @param prod
	 *            LCSProduct
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @return
	 * @throws WTException
	 */
	public static LCSSourcingConfig getSource(final LCSProduct prod,
			final LCSSKUSeasonLink skuSeason) throws WTException {

		String methodName = "getSource() ";
		LCSSourcingConfig source = null;

		// get primary source
		source = LCSSourcingConfigQuery.getPrimarySourceForProduct(prod);
		if(!LCSSourcingConfigQuery.sourceToSeasonExists(source.getMaster(), skuSeason.getSeasonMaster())){
			source=null;
		}
		logger.debug(methodName + "source: " + source);
		// return source
		return source;
	}

	/**
	 * Get Primary Source for season.
	 * @param prod
	 * @param skuSeason
	 * @return
	 * @throws WTException
	 */
	public static LCSSourcingConfig getPrimarySourceForSeason(final LCSProduct prod,
			final LCSSKUSeasonLink skuSeason) throws WTException {

		String methodName = "getPrimarySourceForSeason() ";
		LCSSourcingConfig source = null;

		LCSSeason seasonFromLink = SeasonProductLocator.getSeasonRev(skuSeason);

		logger.debug("Season extracted  ==========>>>   "+seasonFromLink);

		// get primary source for season
		LCSSourceToSeasonLink stsl =  LCSSourcingConfigQuery.getPrimarySourceToSeasonLink(prod.getMaster(), seasonFromLink.getMaster());

		// if primary source for season exists.
		if(null != stsl) {
			source = (LCSSourcingConfig) VersionHelper.latestIterationOf(stsl.getSourcingConfigMaster());

			logger.debug("Primary Sourcing Config for Season  ==============>>>  "+source);
			
			//source = LCSSourcingConfigQuery.getPrimarySourceForProduct(prod);
			//if(!LCSSourcingConfigQuery.sourceToSeasonExists(source.getMaster(), skuSeason.getSeasonMaster())){
			//	source=null;
			//}
		}
		logger.debug(methodName + "source: " + source);
		// return source
		return source;
	}

	/**
	 * Manipulate and return WTObject of type Material.
	 * 
	 * @param source
	 *            LCSSourcingConfig
	 * @param skuSeason
	 *            LCSSKUSeasonLink
	 * @param prod
	 *            LCSProduct
	 * @param key
	 *            String
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public static WTObject getMaterial(final LCSSourcingConfig source,
			final LCSSKUSeasonLink skuSeason, final LCSProduct prod,
			final String key) throws WTException {

		WTObject obj = null;
		final SearchResults sr = FlexSpecQuery.findExistingSpecs(prod,
				(LCSSeason) VersionHelper.latestIterationOf(skuSeason
						.getSeasonMaster()), source);
		final Collection<FlexObject> spec = sr.getResults();
		final Iterator<FlexObject> specItr = spec.iterator();
		while (specItr.hasNext()) {
			final FlexObject object = specItr.next();
			final FlexSpecification specification = (FlexSpecification) LCSQuery
					.findObjectById(BurConstant.LCS_FLEXSPECIFICATION_ROOT_ID
							+ object.getData(BurConstant.LCS_FLEXSPECIFICATION_IDA2A2));
			final FlexSpecToSeasonLink spclink = FlexSpecQuery
					.findSpecToSeasonLink(specification.getMaster(),
							skuSeason.getSeasonMaster());
			if (spclink.isPrimarySpec()) {
				final FlexSpecToComponentLink bomLink = FlexSpecQuery
						.getPrimaryComponentLink(specification, BurConstant.BOM);
				if (bomLink != null) {
					final FlexBOMPart bom = (FlexBOMPart) VersionHelper
							.latestIterationOf(bomLink.getComponent());
					if (BurConstant.FLEXBOM.equalsIgnoreCase(key)) {
						final Collection<FlexObject> flexLinks = LCSFlexBOMQuery
								.findFlexBOMData(bom, BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY, null, false,
										true, BurConstant.ALL_DIMENSIONS,
										BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY,
										BurConstant.STRING_EMPTY, null)
								.getResults();

						final Iterator<FlexObject> flexLink = flexLinks
								.iterator();
						while (flexLink.hasNext()) {
							final FlexObject flexObj = flexLink.next();
							final FlexBOMLink link = (FlexBOMLink) LCSQuery
									.findObjectById(BurConstant.LCS_FLEXBOMLINK_ROOT_ID
											+ flexObj
											.getData(BurConstant.LCS_FLEXBOMLINK_IDA2A2));
							if (!link.isDropped()) {
								final String section = (String) link
										.getValue(BurConstant.SECTION);
								final LCSMaterial mat = (LCSMaterial) VersionHelper
										.latestIterationOf((Persistable) (LCSQuery.findObjectById(BurConstant.LCS_LCSMATERIALMASTER_ROOT_ID
												+ flexObj
												.getData(BurConstant.LCS_FLEXBOMLINK_IDA3B5))));
								if ("burSwingTicketInserts".equalsIgnoreCase(mat.getFlexType().getTypeName())) {
									if (BurConstant.BUR_PACKAGING_LABELING
											.equalsIgnoreCase(section)) {
										obj = mat;
									}
								}
							}
						}
					} else {
						obj = bom;
					}
				}
			}
		}
		return obj;
	}

	/**
	 * Return vendor object.
	 * 
	 * @param source
	 *            LCSSourcingConfig
	 * @return
	 * @throws WTException
	 */
	private static LCSSupplier getVendor(final LCSSourcingConfig source) {

		String methodName = "getVendor() ";
		LCSSupplier lcsSupplier = null;

		try {

			// check if supplier is selected or not
			if ( source.getValue(BurConstant.VENDOR) != null ) {
				logger.debug(methodName + "primary source: " + source.getValue(BurConstant.VENDOR));
				lcsSupplier = (LCSSupplier) source.getValue(BurConstant.VENDOR);
				logger.debug(methodName + "LCSSupplier: " + lcsSupplier.getIdentity());
			}
		} catch (WTException e) {
			logger.debug(methodName + "WtException: " + e);
		}
		// return supplier
		return lcsSupplier;
	}

	/**
	 * 
	 * @param product
	 * @param attKey
	 * @return
	 * @throws WTException
	 */
	public static String getOperationalValue(LCSProduct product, String attKey)
			throws WTException {
		String value = null;
		//
		if ( (product.getFlexType().getFullName().contains(BurConstant.ApparelMens)) || 
				(product.getFlexType().getFullName().contains(BurConstant.ApparelWomens)) ) {
			value = BurberryDataUtil.getData(product, attKey, null);
		} else {
			value = BurberryDataUtil.getData(product,BurConstant.BUR_OPERATIONAL_CATEGORY,null);
		}
		return value;
	}

	/**
	 * 
	 * @param product
	 * @param attKey
	 * @return
	 * @throws WTException
	 */
	public static String getBrandValue(LCSProduct product, String attKey)
			throws WTException {
		String value = null;
		//
		if ( (product.getFlexType().getFullName().contains(BurConstant.ApparelMens)) || 
				(product.getFlexType().getFullName().contains(BurConstant.ApparelWomens)) ) {
			value = BurberryDataUtil.getData(product, attKey, null);
		} else {
			value = BurberryDataUtil.getData(product,BurConstant.BUR_VRDBRAND,null);
		}
		return value;
	}

	/**
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 * @throws InvalidInputException
	 * @throws ParseException
	 */
	public static Map<String, Date> getDates(String sDate, String eDate) throws InvalidInputException, ParseException {

		String methodName = "getDates() ";
		Map<String,Date> dates=new HashMap<String,Date>();
		final SimpleDateFormat sf = new SimpleDateFormat(BurConstant.dateFormat);
		sf.setLenient(false);
		Date startdate;
		Date enddate;

		if (sDate == null || sDate.trim().isEmpty()) {
			throw new InvalidInputException(BurConstant.STR_INVALID_START_DATE);
		} else {
			startdate = sf.parse(sDate);
			dates.put("startdate", startdate);
			logger.debug(methodName + "startDate " + startdate);
		}
		if (sDate.length() != 19 ) {
			throw new InvalidInputException(BurConstant.STR_ERROR_MSG);
		}
		if (eDate == null || eDate.trim().isEmpty()) {
			enddate = new Date();
			dates.put("enddate", enddate);
			logger.debug(methodName + "enddate " + enddate);
		} else if(eDate.length()!=19){
			throw new InvalidInputException(BurConstant.STR_ERROR_MSG);
		}
		else{
			enddate=sf.parse(eDate);
			dates.put("enddate", enddate);
			logger.debug(methodName + "enddate " + enddate);
		}
		logger.debug(methodName + "dates " + dates);
		return dates;
	}
}

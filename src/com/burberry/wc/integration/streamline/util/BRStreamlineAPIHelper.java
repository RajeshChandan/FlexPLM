package com.burberry.wc.integration.streamline.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.sourcingconfig.BRCreateSourcingCofig;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleLogic;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.VersionHelper;

import wt.org.WTUser;
import wt.util.WTException;

public class BRStreamlineAPIHelper {

	private static final Logger LOGGER = Logger.getLogger(BRStreamlineAPIHelper.class);

	/**
	 * Code to get flex object
	 *
	 * @param tableName criteria objType
	 * 
	 * @return String
	 */
	public static String getObject(String tableName, Map<String, String> criteria, FlexType objType) {
		String key;
		String object = null;
		LOGGER.debug("*****BRStreamlineAPIHelper*********");
		try {
			PreparedQueryStatement pqs = new PreparedQueryStatement();
			pqs.setDistinct(true);
			pqs.appendFromTable(tableName);
			if (!"LCSCOLOR".equalsIgnoreCase(tableName) && !"LCSConstructionDetail".equalsIgnoreCase(tableName)
					&& !"LCSLifecycleManaged".equalsIgnoreCase(tableName)
					&& !"LCSPalette".equalsIgnoreCase(tableName)) {
				pqs.appendSelectColumn(tableName, "BRANCHIDITERATIONINFO");
				pqs.appendAndIfNeeded();
				pqs.appendCriteria(new Criteria(tableName, "LATESTITERATIONINFO", "1", "=")); // adding criteria.
			} else {
				pqs.appendSelectColumn(tableName, "IDA2A2");
			}
			Iterator<String> iter = criteria.keySet().iterator();
			while (iter.hasNext()) {
				key = (String) iter.next();
				pqs.appendAndIfNeeded();
				pqs.appendCriteria(new Criteria(tableName, objType.getAttribute(key).getColumnName(),
						(String) criteria.get(key), Criteria.EQUALS));
			}
			if ("LCSConstructionDetail".equalsIgnoreCase(tableName)
					|| "LCSLifecycleManaged".equalsIgnoreCase(tableName)) {
				pqs.appendAndIfNeeded();
				pqs.appendCriteria(new Criteria(tableName, "flextypeidpath", objType.getIdPath(), Criteria.EQUALS));
			}
			// LOGGER.info(pqs);
			// LOGGER.info(criteria);
			LOGGER.debug("*****BRStreamlineAPIHelper*******pqs-------->>" + pqs);
			SearchResults objCol = LCSQuery.runDirectQuery(pqs);
			@SuppressWarnings("unchecked")
			Collection<FlexObject> objectCol = objCol.getResults();
			Iterator<FlexObject> objIter = objectCol.iterator();
			FlexObject flexObj = null;
			while (objIter.hasNext()) {
				flexObj = (FlexObject) objIter.next();
				if (!"LCSCOLOR".equalsIgnoreCase(tableName) && !"LCSConstructionDetail".equalsIgnoreCase(tableName)
						&& !"LCSLifecycleManaged".equalsIgnoreCase(tableName)
						&& !"LCSPalette".equalsIgnoreCase(tableName)) {
					object = flexObj.getData(tableName + ".BRANCHIDITERATIONINFO");
					LOGGER.debug("*****BRStreamlineAPIHelper*********object-------->>" + object);
				} else {
					object = flexObj.getData(tableName + ".IDA2A2");
					LOGGER.debug("*****BRStreamlineAPIHelper*********object-------->>" + object);
				}

			}
		} catch (WTException e) {
			LOGGER.error("Error: " + e.getLocalizedMessage(), e);
		}
		return object;
	}

	/**
	 * Code to get Product object
	 *
	 * @param styleId flexTypestr
	 * 
	 * 
	 * @return LCSProduct
	 */
	public static LCSProduct getProductFromID(int styleId, String flexTypestr) throws WTException {
		// TODO Auto-generated method stub
		LCSProduct product = null;
		FlexType productFlexType;
		Map<String, String> criteriaMap = new HashMap<String, String>();
		// productFlexType =
		// BRLoadUtil.getFlexType((String)dataMap.get(BRMigrationConstants.PRODUCT_TYPE),
		// "Product");
		String productSearchAtt = null;
		productSearchAtt = BRStreamlineConstants.STYLE_NUM;
		LOGGER.debug("*****BRStreamlineAPIHelper*********productSearchAtt----->>>" + productSearchAtt);
		criteriaMap.put(productSearchAtt, String.valueOf(styleId));
		productFlexType = FlexTypeCache.getFlexTypeFromPath(flexTypestr);
		LOGGER.debug("*****BRStreamlineAPIHelper*********productFlexType----->>>" + productFlexType);
		String productBranchId = getObject("LCSPRODUCT", criteriaMap, productFlexType);
		if (productBranchId != null) {
			product = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + productBranchId);
			product = (LCSProduct) VersionHelper.getVersion(product, "A");
			product = (LCSProduct) VersionHelper.latestIterationOf(product);
		}
		LOGGER.debug("*****BRStreamlineAPIHelper*********Product----->>>" + product);
		LOGGER.debug("*****BRStreamlineAPIHelper*********Product version----->>>" + product.getIterationDisplayIdentifier());
		return product;
	}

	/**
	 * Code to get season object
	 *
	 * @param seasonId
	 * 
	 * 
	 * @return LCSSeason
	 */
	public static LCSSeason getSeasonFromID(int seasonId) {
		// TODO Auto-generated method stub
		LCSSeason season = null;
		try {
			season = (LCSSeason) LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSeason:" + seasonId);
		} catch (WTException e) {
			LOGGER.debug("Error: " + e.getLocalizedMessage());
		}
		season.getBranchIdentifier();
		LOGGER.debug("*****BRStreamlineAPIHelper*********season----->>>" + season);
		return season;
	}

	public static LCSSourcingConfig getLatestSrcConfig(LCSProduct product, final LCSSeason lcsSeason) {

		LCSPartMaster productMaster = (LCSPartMaster) product.getMaster();
		LCSSeasonMaster seasonMaster = product.getSeasonMaster();
		LCSSourcingConfig sc;
		new BRCreateSourcingCofig();
		new LCSSourcingConfigLogic();
		LCSSourcingConfig srcConfig = null;

		try {
			sc = LCSSourcingConfigQuery.getPrimarySource(productMaster, seasonMaster);

			LOGGER.debug("*****BRStreamlineAPIHelper******Sourcing Config : " + sc.getName());

			srcConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sc);
			LOGGER.debug("SRC OBJECT  >>>>>>>>>   " + srcConfig + "   NAME :::::::::   " + srcConfig.getName()
					+ "  is iteration latest  ---->    " + VersionHelper.isLatestVersion(srcConfig)
					+ "  is CHECKED OUT  ******  " + VersionHelper.isCheckedOut(srcConfig));

			LOGGER.debug("SRC CONFIG VERSION  ***************************>>>    "
					+ srcConfig.getIterationDisplayIdentifier());
			LOGGER.debug("*****BRStreamlineAPIHelper******Sourcing Config Name: " + srcConfig.getName());
			LOGGER.debug("*****BRStreamlineAPIHelper******Sourcing Config SourcingConfigName : " + srcConfig.getSourcingConfigName());
			LOGGER.debug("*****BRStreamlineAPIHelper******Sourcing Config Name: " + srcConfig.getValue("name"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return srcConfig;
	}

	/**
	 * Code to get Primary Specification object
	 *
	 * @param product season srcConfig
	 * 
	 * @return LCSSeason
	 */
	public static FlexSpecification getSpecification(LCSProduct product, LCSSeason season, LCSSourcingConfig srcConfig)
			throws WTException {
		// TODO Auto-generated method stub
		FlexSpecification specification = null;
		final SearchResults sr = FlexSpecQuery.findExistingSpecs(product, season, srcConfig);
		final Collection<FlexObject> spec = sr.getResults();
		final Iterator<FlexObject> specItr = spec.iterator();
		while (specItr.hasNext()) {
			final FlexObject object = specItr.next();
			specification = (FlexSpecification) LCSQuery
					.findObjectById(BRStreamlineConstants.LCS_FLEXSPECIFICATION_ROOT_ID
							+ object.getData(BRStreamlineConstants.LCS_FLEXSPECIFICATION_IDA2A2));
			final FlexSpecToSeasonLink spclink = FlexSpecQuery.findSpecToSeasonLink(specification.getMaster(),
					season.getMaster());
			if (spclink.isPrimarySpec()) {
				LOGGER.debug("*****BRStreamlineAPIHelper******specification is->>>>" + specification);

				break;

			}
		}
		LOGGER.debug("*****BRStreamlineAPIHelper******specification is->>>>" + specification);
		return specification;
	}

	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static WTUser getUserFromID(int userId) {
		WTUser user = null;
		try {
			user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + userId);
			LOGGER.debug("*****BRStreamlineAPIHelper******"+user.getName());
		} catch (WTException e) {
			LOGGER.debug("Error: " + e.getLocalizedMessage());
		}
		return user;
	}

	/**
	 * 
	 * @param vendorId
	 * @return
	 */
	public static LCSSupplier getVendorFromID(int vendorId) {
		LCSSupplier vendor = null;
		try {
			vendor = (LCSSupplier) LCSQuery.findObjectById("OR:com.lcs.wc.supplier.LCSSupplier:" + vendorId);
			LOGGER.debug(vendor.getName() + " " + vendor.getSupplierName());
		} catch (WTException e) {
			LOGGER.debug("Error: " + e.getLocalizedMessage());
		}
		return vendor;
	}

	/**
	 * 
	 * @param countryId
	 * @return
	 */
	public static LCSCountry getCountryFromID(int countryId) {
		LCSCountry country = null;
		try {
			country = (LCSCountry) LCSQuery.findObjectById("OR:com.lcs.wc.country.LCSCountry:" + countryId);
			LOGGER.debug(country.getName() + "---------- " + country.getDisplayIdentifier());
		} catch (WTException e) {
			LOGGER.debug("Error: " + e.getLocalizedMessage());
		}
		return country;
	}

	/**
	 * Code to create the Fit Sample
	 *
	 * @param product sample
	 * 
	 * 
	 * @return LCSSample
	 */
	public static LCSSample createFitSamples(LCSSample sample, LCSProduct product, FlexSpecification specification,
			LCSSourcingConfig srcConfig) throws WTException {
		// TODO Auto-generated method stub
		LCSSampleLogic sampleLogic = new LCSSampleLogic();
		
		LCSMeasurements measurements = LCSMeasurementsQuery.findMeasurements(product);
		if (measurements != null) {
			LOGGER.debug("*****BRStreamlineAPIHelper******measurements is->>>>" + measurements);
			measurements = (LCSMeasurements) VersionHelper.latestIterationOf(measurements);
			String measurementsId = "OR:" + measurements;
			LOGGER.debug("*****BRStreamlineAPIHelper******measurementsId is->>>>" + measurementsId);

			// String size=
			// BRStreamlineAPIHelper.getOneProductSizeCategoryName(product,season);
			String size = measurements.getSampleSize();
			if (size !=null) {
				LOGGER.debug("*****BRStreamlineAPIHelper******size is->>>>" + size);
				sample = sampleLogic.createFitSample(sample, measurementsId, size);
			}
			
		}
		return sample;
	}

	/*public static String ValidateJson(JSONObject json, String property) {

		Properties prop = new Properties();
		StringTokenizer st;
		String key = null;
		String keyString = null;
		String response = null;
		InputStream input = null;
		Boolean flag = true;
		try {
			input = new FileInputStream(System.getProperty("wt.home") + File.separator + "codebase" + File.separator
					+ "com" + File.separator + "burberry" + File.separator + "wc" + File.separator + "integration"
					+ File.separator + "streamline" + File.separator + "util" + File.separator
					+ "Streamline.properties");
			// load a properties file
			prop.load(input);
			keyString = prop.getProperty(property);

			st = new StringTokenizer(keyString, "|~*~|");
			while (st.hasMoreTokens()) {
				key = st.nextToken();
				if (json.opt(key) == null || json.isNull(key)) {
					flag = false;
					response = key;
					break;

				}
			}

		} catch (Exception e) {
			LOGGER.debug("Error: " + e.getLocalizedMessage());
		}

		return response;

	}*/
	public static void ValidateJson(JSONObject json, String property) throws IOException, BRJSONValidationException {

		Properties prop = new Properties();
		StringTokenizer st;
		String key = null;
		String keyString = null;
		String response = null;
		InputStream input = null;
		Boolean flag = true;
		
			/*input = new FileInputStream(System.getProperty("wt.home") + File.separator + "codebase" + File.separator
					+ "com" + File.separator + "burberry" + File.separator + "wc" + File.separator + "integration"
					+ File.separator + "streamline" + File.separator + "util" + File.separator
					+ "Streamline.properties");*/
			
			input = new FileInputStream(System.getProperty("wt.home") + File.separator + "codebase" + File.separator + "Streamline.properties");
			// load a properties file
			prop.load(input);
			keyString = prop.getProperty(property);

			st = new StringTokenizer(keyString, "|~*~|");
			while (st.hasMoreTokens()) {
				key = st.nextToken();
				if (json.opt(key) == null || json.isNull(key)) {
					flag = false;
					response = key+" is a required field in the input json";
					LOGGER.debug("ValidateJson::::Error: " + key+ " is a required field in the input json");
					//break;
					throw new BRJSONValidationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
							.entity(response).build());
				}
			}

		
		//return response;

	}
}

package com.burberry.wc.integration.productapi.extraction;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.*;
import java.util.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.burberry.wc.integration.exception.*;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.productapi.bean.*;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productapi.transform.BurberryProductAPIDataTransformHelper;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductAPIDataExtractionHelper implements RemoteAccess {

	/**
	 * BurberryProductAPIDataExtractionHelper.
	 */
	private BurberryProductAPIDataExtractionHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger.getLogger(BurberryProductAPIDataExtractionHelper.class);

	/**
	 * apiType.
	 */
	private static final String apiType = "Product";

	/**
	 * This is starting point of Product API. Method contains trigger mechanism,
	 * extraction and transformation
	 * 
	 * @param queryParams Params Query
	 * @return Object Product API Bean
	 * @throws WTException               Exception
	 * @throws BurException              Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws ParseException            Exception
	 * @throws IOException               Exception
	 * @throws NoSuchMethodException     NoSuchMethodException
	 * @throws PropertyVetoException
	 */
	public static Object getProductAPIData(MultivaluedMap<String, String> queryParams)
			throws BurException, IllegalAccessException, InvocationTargetException, ParseException, IOException,
			NoSuchMethodException, WTException, PropertyVetoException {
		boolean previousEnforcement = true;

		BurberryLogFileGenerator.configureProductAPILog();

		String methodName = "getProductAPIData()";
		// Method Start Time
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Product API Data Extraction Start Time: ");

		// Initialisation of response map to be be sent back for request
		Map<Status, Object> responseMap = new HashMap<Status, Object>();
		try {
			// Momentarily suspend access control enforcement.

			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr.getName());
			previousEnforcement = SessionServerHelper.manager.setAccessEnforced(false);
			// Initialisation of a new Product Bean Object
			ProductAPI productsBean = ObjectFactory.createProductAPI();

			// Initialisation product api valid objects
			Map<String, String> mapValidObjects = BurberryAPIUtil
					.initializeValidObjects(BurConstant.STR_PRODUCTAPI_VALID_OBJECTS);
			logger.debug(methodName + "mapValidObjects: " + mapValidObjects);

			// Step 1: Verify Passed Parameters
			BurberryAPIUtil.verifyPassedParameters(queryParams, mapValidObjects);

			// JIRA - BURBERRY-1363: START
			// Step 2: Based on the valid objects get query criteria
			logger.info("query Params  >>>>>>>>>>>>>>   "+queryParams);
			logger.info("mapValidObjects  >>>>>>>>>>>>>   "+mapValidObjects);
			List<Map> criteria = (List<Map>) BurberryAPIUtil.getCriteriaCollection(apiType, queryParams,
					mapValidObjects);
			logger.debug(methodName + "Product Criteria: " + criteria);

			// Flag to check for Delta criteria
			boolean deltaCriteria = queryParams.toString().contains("Delta");
			// Step 3: Pass the criteria map and get all the unique products branch ids

			List<String> listofObjects = BurberryAPIUtil.getUniqueObjectIds(criteria,
					"LCSPRODUCT.BRANCHIDITERATIONINFO");
			// JIRA - BURBERRY-1363: END

			List<Style> lstStyle = new ArrayList<Style>();
			// Step 4: Pass the product collections and get transformed bean
			// data
			if (listofObjects != null && !listofObjects.isEmpty()) {
				lstStyle = getTransformedStyleData(listofObjects, criteria, deltaCriteria);
			}
			logger.debug(methodName + "Bean Style List: " + lstStyle);
			logger.debug(methodName + "Number of Style: " + lstStyle.size());

			// Set all the bean data into products bean
			productsBean.setStyle(lstStyle);

			BurberryAPIBeanUtil.sendNoRecordFoundException(productsBean.getStyle());

			responseMap.put(Status.OK, productsBean);

			// Method End Time
			long lEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Product API Data Extraction End Time: ");
			logger.info(methodName + "Product API  Total Execution Time (ms): " + (lEndTime - lStartTime));

		} catch (final WTException e) {
			responseMap.put(Status.INTERNAL_SERVER_ERROR,
					BurberryAPIBeanUtil.getErrorResponseBean(e.getMessage(), Status.INTERNAL_SERVER_ERROR, queryParams,
							lStartTime, BurPaletteMaterialConstant.PRODUCT_API_LOG_ENTRY_FLEXTYPE, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
		} catch (final ParseException e) {
			responseMap.put(Status.INTERNAL_SERVER_ERROR,
					BurberryAPIBeanUtil.getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE,
							Status.INTERNAL_SERVER_ERROR, queryParams, lStartTime,
							BurPaletteMaterialConstant.PRODUCT_API_LOG_ENTRY_FLEXTYPE, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
		} catch (final NoRecordFoundException e) {
			responseMap.put(Status.OK, BurberryAPIBeanUtil.getErrorResponseBean(e.getMessage(), Status.OK, queryParams,
					lStartTime, BurPaletteMaterialConstant.PRODUCT_API_LOG_ENTRY_FLEXTYPE, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
		} catch (final BurException e) {
			responseMap.put(Status.BAD_REQUEST,
					BurberryAPIBeanUtil.getErrorResponseBean(e.getMessage(), Status.BAD_REQUEST, queryParams,
							lStartTime, BurPaletteMaterialConstant.PRODUCT_API_LOG_ENTRY_FLEXTYPE, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);

		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}
		return responseMap;
	}

	/**
	 * This method will loop through each product and get beans.
	 * 
	 * @param listofObjects Collection of Product
	 * @param criteria      Collection Criteria
	 * @param deltaCriteria DeltaCriteria
	 * @return Beans Beans
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws WTException               Exception
	 * @throws IOException               Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws PropertyVetoException
	 */
	private static List<Style> getTransformedStyleData(List<String> listofObjects, Collection<Map> criteria,
			boolean deltaCriteria) throws IllegalAccessException, InvocationTargetException, WTException, IOException,
			NoSuchMethodException, PropertyVetoException {
		ArrayList<Style> styleList = new ArrayList<Style>();
		String methodName = "getTransformedStyleData() ";
		// Track Start time
		long transStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Product API - Transformation Start Time: ");

		// Check if collection of products is not empty
		logger.debug(methodName + "Product Count: " + listofObjects.size());

		Map<String, List> objectMap = BurberryAPIUtil.mapFilter(criteria);
		// Filter the map collection criteria and separate the below flex
		// object
		Collection<String> colProdToSKUIds = objectMap.get("LCSSKU.BRANCHIDITERATIONINFO");
		logger.debug(methodName + "Product to SKUs: " + colProdToSKUIds);

		Collection<String> colProdToSeasonLinkIds = objectMap.get("LCSPRODUCTSEASONLINK.IDA2A2");
		logger.debug(methodName + "Product to Product-Season Links: " + colProdToSeasonLinkIds);

		Collection<String> colProdToSKUSeasonLinkIds = objectMap.get("LCSSKUSEASONLINK.IDA2A2");
		logger.debug(methodName + "Product to SKU-Season Links: " + colProdToSKUSeasonLinkIds);

		Collection<String> colProdToSourceIds = objectMap.get("LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO");
		logger.debug(methodName + "Product to Source: " + colProdToSourceIds);
		
		//Source to Season -------------------------------
		//Collection<String> colSourceToSeasonIds = objectMap.get("LCSSOURCETOSEASONLINK.IDA2A2");
		//logger.debug(methodName + "Source to Season: " + colSourceToSeasonIds);
		//---------------------------------------------------

		Collection<String> colProdToSeasonIds = objectMap.get("LCSSEASON.BRANCHIDITERATIONINFO");
		logger.debug(methodName + "Product to Season: " + colProdToSeasonIds);

		// Tracked Risk Management Data
		Map<String, Collection<HashMap>> mapTrackedRiskManagement = BurberryAPIUtil.getRemovedMOARowsDeleted(criteria,
				BurProductConstant.LCSPRODUCT_BRANCHIDITERATIONINFO,
				BurPaletteMaterialConstant.BO_TRACK_RISK_MANAGEMENT_NAME,
				BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_OWNER_ID,
				BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_MOA_OBJECT_ID);

		// Tracked Image Page Data
		Map<String, Collection<HashMap>> mapTrackedImagePage = BurberryAPIUtil.getRemovedImagePageData(criteria,
				BurProductConstant.LCSPRODUCT_BRANCHIDITERATIONINFO,
				BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME, BurProductConstant.MOA_TRACK_PRODUCT_ID,
				BurProductConstant.MOA_TRACK_SOURCING_ID, BurProductConstant.MOA_TRACK_SPECIFICATION_ID,
				BurProductConstant.MOA_TRACK_IMAGE_PAGE_NAME);

		// Tracked Image From Image Page Data
		Map<String, Collection<HashMap>> mapTrackedImageFromImagePage = BurberryAPIUtil
				.getRemovedImageFromImagePageData(criteria, BurProductConstant.LCSPRODUCT_BRANCHIDITERATIONINFO,
						BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME, BurProductConstant.MOA_TRACK_PRODUCT_ID,
						BurProductConstant.MOA_TRACK_SOURCING_ID, BurProductConstant.MOA_TRACK_SPECIFICATION_ID,
						BurProductConstant.MOA_TRACK_IMAGE_PAGE_ID, BurProductConstant.MOA_TRACK_IMAGE_FILE_UNIQUE_ID);

		// CR R26: Handle Remove Object Customisation: START
		// Tracked Product Specification Data
		Map<String, Collection<HashMap>> mapTrackedProdSpec = BurberryAPIUtil.getSpecDeletionsMapData(criteria,
				BurProductConstant.LCSPRODUCT_BRANCHIDITERATIONINFO, BurProductConstant.BO_TRACK_SPECIFICATION_NAME,
				BurProductConstant.MOA_TRACK_PRODUCT_ID, BurProductConstant.MOA_TRACK_SOURCING_ID,
				BurProductConstant.MOA_TRACK_SEASON_ID, BurProductConstant.MOA_TRACK_SPECIFICATION_SPEC_NAME);
		logger.debug(methodName + " Removed Specification Map : " + mapTrackedProdSpec);
		// CR R26: Handle Remove Object Customisation: END

		Map<String, Collection<HashMap>> mapTrackedDocuments = BurberryAPIUtil.getRemovedMOARowsDeleted(criteria,
				BurProductConstant.LCSPRODUCT_BRANCHIDITERATIONINFO,
				BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME, BurProductConstant.MOA_TRACK_SPECIFICATION_ID,
				BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID);

		logger.info(methodName + " Removed Spec Documents Map : " + mapTrackedDocuments);
		// Loop through Product Collection
		for (String prod : listofObjects) {
			LCSProduct productObj = null;
			productObj = getProductObject(prod);
			logger.info(methodName + "Product Object: " + productObj.getName());
			styleList.add(BurberryProductAPIDataTransformHelper.getStyleBean(productObj, colProdToSeasonIds,
					colProdToSeasonLinkIds, colProdToSKUIds, colProdToSKUSeasonLinkIds, colProdToSourceIds,
					deltaCriteria, mapTrackedRiskManagement, mapTrackedImagePage, mapTrackedImageFromImagePage,
					mapTrackedProdSpec, mapTrackedDocuments));
			logger.debug(methodName + "Style List Object : " + styleList);
		}

		// Track execution time
		long transEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Product API - Transformation End Time: ");
		logger.info(methodName + "Product API - Transformation Total Execution Time (ms): "
				+ (transEndTime - transStartTime));

		return styleList;
	}

	/**
	 * getProductObject.
	 * 
	 * @param prod Flex Object
	 * @return LCSProduct
	 * @throws WTException Exception
	 */
	private static LCSProduct getProductObject(final String prod) throws WTException {

		String methodName = "getProductObject() ";
		// Initialisation of product object
		LCSProduct product = null;

		product = (LCSProduct) LCSQuery.findObjectById(BurProductConstant.LCSPRODUCT_ROOT_ID + prod);
		product = ((LCSProduct) VersionHelper.latestIterationOf(product));
		logger.debug(methodName + "Product Object: " + product);
		return product;
	}

}

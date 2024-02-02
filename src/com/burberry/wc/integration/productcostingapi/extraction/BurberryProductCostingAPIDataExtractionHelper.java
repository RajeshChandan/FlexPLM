package com.burberry.wc.integration.productcostingapi.extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.NoRecordFoundException;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productcostingapi.bean.ProductCostingAPI;
import com.burberry.wc.integration.productcostingapi.bean.Style;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.productcostingapi.transform.BurberryProductCostingAPIDataTransformHelper;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.util.VersionHelper;

import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductCostingAPIDataExtractionHelper implements
		RemoteAccess {

	/**
	 * BurberryProductCostingAPIDataExtractionHelper.
	 */
	private BurberryProductCostingAPIDataExtractionHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIDataExtractionHelper.class);

	/**
	 * apiType.
	 */
	private static final String apiType = "Product Costing";

	/**
	 * This is starting point of Product Costing API. Method contains trigger
	 * mechanism, extraction and transformation /**
	 * 
	 * @param queryParams
	 *            Parameters
	 * @return Object
	 * @throws BurException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Object getProductCostingAPIData(
			MultivaluedMap<String, String> queryParams) throws BurException,
			IllegalAccessException, InvocationTargetException, ParseException,
			IOException, NoSuchMethodException, WTPropertyVetoException,
			WTException {

		// Configure Logger Properties
		BurberryLogFileGenerator.configureProductCostingAPILog();
		// Enforcement initialisation
		boolean previousEnforcement = true;
		// Method Name
		String methodName = "getProductCostingAPIData() ";

		// Method Start Time
		long prodCostingStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Costing API Data Extraction Start Time: ");

		// Initialisation of response map to be be sent back for request
		Map<Status, Object> responseProductCostingMap = new HashMap<Status, Object>();

		ProductCostingAPI costing = new ProductCostingAPI();
		try {
			// Set Authenticated Principal
			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);

			// Step 1: Get Valid Objects Map
			Map<String, String> mapValidProductCostingObjects = BurberryAPIUtil
					.initializeValidObjects(BurProductCostingConstant.STR_PRODUCT_COSTING_API_VALID_OBJECTS);
			logger.debug(methodName + "Valid Product Costing Objects: "
					+ mapValidProductCostingObjects);

			// Step 2: Check Passed Parameters
			BurberryAPIUtil.verifyPassedParameters(queryParams,
					mapValidProductCostingObjects);
			logger.debug(methodName + "Product Costing Query Params: "
					+ queryParams);

			// Flag to check for Delta criteria
			boolean deltaCriteria = queryParams.toString().contains("Delta");
			logger.debug(methodName + "Delta Criteria: " + deltaCriteria);
			
			//JIRA - BURBERRY-1363: START
			// Step 3: Based on the valid objects get query criteria
			List<Map> criteria = (List<Map>)BurberryAPIUtil.getCriteriaCollection(
					apiType, queryParams, mapValidProductCostingObjects);
			logger.debug(methodName + "Product Costing Criteria: " + criteria);

			List<String> listofObjects = BurberryAPIUtil.getUniqueObjectIds(criteria,"LCSPRODUCT.BRANCHIDITERATIONINFO");
			
			List<Style> productCostingAPIBean = new ArrayList<Style>();
			// Step 5: Pass the Product collections and get transformed
			// bean data
			if (listofObjects != null && !listofObjects.isEmpty()) {
				productCostingAPIBean = getProductCostingTransformedData(
						listofObjects, criteria, deltaCriteria);
				logger.debug(methodName + "Bean Product Costing List: "
						+ productCostingAPIBean);
			}

			// Step 7: Throw exception if matches no record fetched.

			costing.setStyle(productCostingAPIBean);
			// Step 8: Setting Response Map
			responseProductCostingMap.put(Status.OK, costing);

			// Method End Time
			long prodCostingEndTime = BurberryAPIUtil.printCurrentTime(
					methodName,
					"Product Costing API Data Extraction End Time: ");
			logger.info(methodName
					+ "Product Costing API Total Execution Time (ms): "
					+ (prodCostingEndTime - prodCostingStartTime));

		} catch (final WTException e) {
			responseProductCostingMap
					.put(Status.INTERNAL_SERVER_ERROR,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.INTERNAL_SERVER_ERROR,
									queryParams,
									prodCostingStartTime,
									BurProductCostingConstant.PRODUCT_COSTING_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(
					BurProductCostingConstant.STR_PRODUCT_COSTING_API_ERROR_MSG,
					e);
		} catch (final ParseException e) {
			responseProductCostingMap
					.put(Status.INTERNAL_SERVER_ERROR,
							BurberryAPIBeanUtil
									.getErrorResponseBean(
											BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE,
											Status.INTERNAL_SERVER_ERROR,
											queryParams,
											prodCostingStartTime,
											BurProductCostingConstant.PRODUCT_COSTING_API_LOG_ENTRY_FLEXTYPE,
											apiType));
			logger.error(
					BurProductCostingConstant.STR_PRODUCT_COSTING_API_ERROR_MSG,
					e);
		} catch (final NoRecordFoundException e) {
			responseProductCostingMap
					.put(Status.OK,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.OK,
									queryParams,
									prodCostingStartTime,
									BurProductCostingConstant.PRODUCT_COSTING_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(
					BurProductCostingConstant.STR_PRODUCT_COSTING_API_ERROR_MSG,
					e);
		} catch (final BurException e) {
			responseProductCostingMap
					.put(Status.BAD_REQUEST,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.BAD_REQUEST,
									queryParams,
									prodCostingStartTime,
									BurProductCostingConstant.PRODUCT_COSTING_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(
					BurProductCostingConstant.STR_PRODUCT_COSTING_API_ERROR_MSG,
					e);
		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}
		// Return Final Map
		return responseProductCostingMap;
	}

	private static List<Style> getProductCostingTransformedData(
			List<String> listofObjects, Collection<Map> criteria,
			boolean deltaCriteria) throws IllegalAccessException,
			InvocationTargetException, WTException, IOException,
			NoSuchMethodException {

		ArrayList<Style> styleList = new ArrayList<Style>();
		String methodName = "getProductCostingTransformedData() ";

		// Track Start time
		long transProdCostingStartTime = BurberryAPIUtil
				.printCurrentTime(methodName,
						"Product Costing API - Transformation Start Time: ");
		
	
			logger.info(methodName + "Product Count: " + listofObjects.size());
	
			Map<String, List> mapFilterObject = BurberryAPIUtil.mapFilter(criteria);
	
			// Filter the map collection criteria and separate the below flex
			// object
			Collection<String> collProdToSKUIds = mapFilterObject
					.get("LCSSKU.BRANCHIDITERATIONINFO");
			logger.debug(methodName + "Collection of Product to SKUs: "
					+ collProdToSKUIds);
	
			Collection<String> collProdToSourceIds = mapFilterObject
					.get("LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO");
			logger.debug(methodName + "Collection of Product to Source: "
					+ collProdToSourceIds);
	
			Collection<String> collProdToCostSheetIds = mapFilterObject
					.get("LCSPRODUCTCOSTSHEET.BRANCHIDITERATIONINFO");
			logger.debug(methodName + "Collection of Product to Cost Sheet: "
					+ collProdToCostSheetIds);
			
			Map<String, Collection<HashMap>> mapTrackedCostSheet = BurberryAPIUtil
					.getRemovedMOARowsDeleted(
							criteria,
							"LCSPRODUCT.BRANCHIDITERATIONINFO",
							BurProductCostingConstant.BO_TRACK_COST_SHEET_NAME,
							BurProductCostingConstant.MOA_TRACK_COST_SHEET_SOURCING_ID,
							BurProductCostingConstant.MOA_TRACK_COST_SHEET_ID);
	
			ProductHeaderQuery phq = new ProductHeaderQuery();
			// Loop through Product Collection
			for (String prod : listofObjects) {
				LCSProduct productObj = null;
				productObj = getProductCostingObject(prod);
				Collection seasons = phq.findSeasonsForProduct(productObj);
				if (seasons != null && !seasons.isEmpty()) {
					Style styleBean=BurberryProductCostingAPIDataTransformHelper
							.getProductStyleBean(productObj, collProdToSKUIds,
									collProdToSourceIds, collProdToCostSheetIds,
									deltaCriteria, seasons,mapTrackedCostSheet);
					if(styleBean.getSource()!=null && !styleBean.getSource().isEmpty()){
						logger.info(methodName + "Product Object: " + productObj.getName());
						styleList.add(styleBean);
						logger.debug(methodName + "Style List Object : " + styleList);
					}
				}
			}
		
		// Track execution time
		long transProdCostingEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Costing API - Transformation End Time: ");
		logger.info(methodName
				+ "Product Costing API - Transformation Total Execution Time (ms): "
				+ (transProdCostingEndTime - transProdCostingStartTime));
		return styleList;
	}

	/**
	 * getProductObject.
	 * 
	 * @param prod
	 *            Flex Object
	 * @return LCSProduct
	 * @throws WTException
	 *             Exception
	 */
	private static LCSProduct getProductCostingObject(final String prod)
			throws WTException {

		String methodName = "getProductCostingObject() ";
		// Initialisation of product object
		LCSProduct productObj = null;
		productObj = (LCSProduct) LCSQuery
				.findObjectById(BurProductConstant.LCSPRODUCT_ROOT_ID
						+ prod);
		productObj = ((LCSProduct) VersionHelper.latestIterationOf(productObj));
		logger.debug(methodName + "Product Object: " + productObj);
		return productObj;
	}
	

}

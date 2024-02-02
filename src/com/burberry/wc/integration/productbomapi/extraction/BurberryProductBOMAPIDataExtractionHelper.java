package com.burberry.wc.integration.productbomapi.extraction;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productbomapi.bean.ProductBOMAPI;
import com.burberry.wc.integration.productbomapi.bean.Style;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.productbomapi.transform.BurberryProductBOMAPIDataTransformHelper;
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
public final class BurberryProductBOMAPIDataExtractionHelper implements RemoteAccess {

	/**
	 * BurberryProductAPIDataExtractionHelper.
	 */
	private BurberryProductBOMAPIDataExtractionHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIDataExtractionHelper.class);

	/**
	 * apiType.
	 */
	private static final String apiType = "Product BOM";

	/**
	 * This is starting point of Product BOM API. Method contains trigger
	 * mechanism, extraction and transformation
	 * 
	 * @param queryParams
	 *            Params Query
	 * @return Object Product API Bean
	 * @throws WTException
	 *             Exception
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
	 * @throws PropertyVetoException 
	 */
	public static Object getProductBOMAPIData(
			MultivaluedMap<String, String> queryParams)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException, WTException,
			PropertyVetoException {

		boolean previousEnforcement = true;
		BurberryLogFileGenerator.configureProductBOMAPILog();
		String methodName = "getProductBOMAPIData() ";
		// Initialisation of response map to be be sent back for request
		Map<Status, Object> responseMap = new HashMap<Status, Object>();
		// Method Start Time
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product BOM API Data Extraction Start Time: ");
		try {
			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);
			ProductBOMAPI productBean = new ProductBOMAPI();
			

			// Step 1: Get Valid Objects Map
			Map<String, String> mapValidObjects = BurberryAPIUtil
					.initializeValidObjects(BurProductBOMConstant.STR_PRODUCT_BOM_API_VALID_OBJECTS);
			logger.debug(methodName + "mapValidObjects: " + mapValidObjects);

			// Step 2: Check Passed Parameters
			BurberryAPIUtil
					.verifyPassedParameters(queryParams, mapValidObjects);

			//JIRA - BURBERRY-1363: START
			// Step 3: Based on the valid objects get query criteria
			List<Map> criteria = (List<Map>)BurberryAPIUtil.getCriteriaCollection(
					apiType, queryParams, mapValidObjects);
			logger.debug(methodName + "Product BOM Criteria: " + criteria);

			// Flag to check for Delta criteria
			boolean deltaCriteria = queryParams.toString().contains("Delta");
			//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : Start
			Map bomDeltaDateMap = new HashMap();
			//Check Delta
			if(deltaCriteria){
				//Get Delta Dates Map
				bomDeltaDateMap = getDeltaDateMap(queryParams);
			}
			//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : End
						
			// Step 4: Pass the criteria map and get all the product branch ids
			List<String> listofObjects = BurberryAPIUtil.getUniqueObjectIds(criteria,"LCSPRODUCT.BRANCHIDITERATIONINFO");

			logger.debug(methodName + "Collection of Products: " + listofObjects);
			
			//JIRA - BURBERRY-1363: END
			//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : Start
			
			List<Style> lstStyle=new ArrayList<Style>();
			if (listofObjects != null && !listofObjects.isEmpty()) {
				lstStyle = getTransformedStyleData(listofObjects, criteria,
						deltaCriteria,bomDeltaDateMap);
			}
			//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : End
			
			productBean.setStyle(lstStyle);
			BurberryAPIBeanUtil.sendNoRecordFoundException(productBean.getStyle());
			long lEndTime = BurberryAPIUtil.printCurrentTime(methodName,
					"Product BOM API Data Extraction End Time ");
			logger.info(methodName
					+ "Product BOM API  Total Execution Time (ms): "
					+ (lEndTime - lStartTime));

			responseMap.put(Status.OK, productBean);

		} catch (final WTException e) {
			responseMap.put(Status.INTERNAL_SERVER_ERROR, BurberryAPIBeanUtil
					.getErrorResponseBean(e.getMessage(),
							Status.INTERNAL_SERVER_ERROR, queryParams,
							lStartTime, BurProductBOMConstant.PRODUCTBOMLOGENTRY, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
			// return responseMap;
		} catch (final ParseException e) {
			responseMap.put(Status.INTERNAL_SERVER_ERROR, BurberryAPIBeanUtil
					.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE,
							Status.INTERNAL_SERVER_ERROR, queryParams,
							lStartTime, BurProductBOMConstant.PRODUCTBOMLOGENTRY, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
			// return responseMap;
		} /*catch (final NoRecordFoundException e) {
			responseMap.put(Status.OK, BurberryAPIBeanUtil
					.getErrorResponseBean(e.getMessage(), Status.OK,
							queryParams, lStartTime, BurProductBOMConstant.PRODUCTBOMLOGENTRY, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
			// return responseMap;
		}*/ catch (final BurException e) {
			responseMap.put(Status.BAD_REQUEST, BurberryAPIBeanUtil
					.getErrorResponseBean(e.getMessage(), Status.BAD_REQUEST,
							queryParams, lStartTime, BurProductBOMConstant.PRODUCTBOMLOGENTRY, apiType));
			logger.error(BurConstant.STR_ERROR_MSG_PRODUCT_API, e);
			// return responseMap;
		}finally{
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);
			
		}
		return responseMap;
	}
	
	//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : Start
	/**
	 * Method to get delta date map
	 * 
	 * @param queryParams
	 *            MultivaluedMap<String, String>
	 * @return Map
	 * @throws BurException
	 * @throws ParseException
	 */
	private static Map getDeltaDateMap(
			MultivaluedMap<String, String> queryParams) throws ParseException,
			BurException {
		// Method Name
		String methodName = "getDeltaDateMap() ";
		// Initialisation of date delta map
		HashMap<String, String> mapBOMDeltaDate = new HashMap<String, String>();
		// Loop through each request parameters
		for (Entry<String, List<String>> mapBOMEntry : queryParams.entrySet()) {
			String strParamKey = mapBOMEntry.getKey();
			List<String> lValue = queryParams.get(strParamKey);
			// Initialisation of iterator
			Iterator itrValues = lValue.iterator();
			// Loop through parameter values
			while (itrValues.hasNext()) {
				String strAttValue = (String) itrValues.next();
				String[] objAtt = strParamKey.split("~~");
				String strAttDisplayName = objAtt[1].trim();
				// Passed Object is Delta
				mapBOMDeltaDate.put(strAttDisplayName, strAttValue);
			}
		}
		logger.debug(methodName + "mapBOMDelataDate: " + mapBOMDeltaDate);
		// Get the Delta Map with Start and End Dates
		Map<String, Date> bomDeltaDateMap = BurberryAPIUtil
				.getDeltaDateMap(mapBOMDeltaDate);
		logger.debug(methodName + "BOM Delta Dates Map: " + bomDeltaDateMap);
		return bomDeltaDateMap;
	}
	//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : End
	
	/**
	 * This method will loop through each product and get beans.
	 * 
	 * @param listofObjects
	 *            Collection of Product
	 * @param criteria
	 * @param deltaCriteria
	 * @param bomDeltaDateMap 
	 * @return Beans Beans
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 * @throws PropertyVetoException 
	 */
	private static List<Style> getTransformedStyleData(
			List<String> listofObjects, Collection<Map> criteria,
			boolean deltaCriteria, Map bomDeltaDateMap) throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		ArrayList<Style> bomStyleList = new ArrayList<Style>();
		String methodName = "getTransformedStyleData() ";

		// Track Start time
		long bomTransStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product BOM API - Transformation Start Time: ");
		// Check if collection of products is not empty
		logger.debug(methodName + "BOM API Product Count: " + listofObjects.size());

		Map<String,List> objectMap= BurberryAPIUtil.mapFilter(
				criteria);
		// Filter the map collection criteria and separate the below flex
		// object
		Collection<String> colProdBOMToSKUIds = objectMap.get("LCSSKU.BRANCHIDITERATIONINFO");
		logger.debug(methodName + "BOM API Product to SKUs: " + colProdBOMToSKUIds);

		Collection<String> colProdBOMToSeasonLinkIds = objectMap.get("LCSPRODUCTSEASONLINK.IDA2A2");
		logger.debug(methodName + "BOM API Product to Product-Season Links: "
				+ colProdBOMToSeasonLinkIds);

		Collection<String> colProdBOMToSKUSeasonLinkIds = objectMap.get("LCSSKUSEASONLINK.IDA2A2");
		logger.debug(methodName + "BOM API Product to SKU-Season Links: "
				+ colProdBOMToSKUSeasonLinkIds);

		Collection<String> colProdBOMToSeasonIds =objectMap.get("LCSSEASON.BRANCHIDITERATIONINFO");
		logger.debug(methodName + "BOM API Product to Season: "
				+ colProdBOMToSeasonIds);

		Collection<String> colProdToBOMIds = objectMap.get("FLEXBOMPART.BRANCHIDITERATIONINFO");

		logger.debug(methodName + " BOM Part to product " + colProdToBOMIds);

		Collection<String> colProdToBOMLinkIds = objectMap.get("FLEXBOMLINK.IDA2A2");

		Collection<String> colProdToSourceIds = objectMap.get("LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO");
		
		//CR R26: Handle Remove Object Customisation: START
		// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output -Start
		// Tracked BOM Data
		Map<String, Collection<HashMap>> mapTrackedBOM = BurberryAPIUtil
				.getBOMDeletionsMapData(
						criteria,
						"LCSPRODUCT.BRANCHIDITERATIONINFO",
						BurProductBOMConstant.BO_TRACK_BOM_NAME,
						BurProductConstant.MOA_TRACK_PRODUCT_ID,
						BurProductConstant.MOA_TRACK_SOURCING_ID,
						BurProductConstant.MOA_TRACK_SPECIFICATION_ID,
						BurProductBOMConstant.MOA_TRACK_BOM_PART_ID,
						BurProductBOMConstant.MOA_TRACK_BOM_NAME);
		// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - End
		//CR R26: Handle Remove Object Customisation: END

		logger.debug(methodName + " BOM LINK to product "
				+ colProdToBOMLinkIds);

		// Loop through Product Collection
		for (String prod : listofObjects) {
			LCSProduct productObj = null;
			productObj = getProductObject(prod);
			logger.info(methodName + "Product Object: " + productObj.getName());
			Style styleBean=BurberryProductBOMAPIDataTransformHelper
					.getStyleBean(productObj, colProdBOMToSeasonIds,
							colProdBOMToSeasonLinkIds, colProdBOMToSKUIds,
							colProdBOMToSKUSeasonLinkIds, colProdToBOMIds,
							colProdToBOMLinkIds, colProdToSourceIds,mapTrackedBOM,
							deltaCriteria,bomDeltaDateMap);
			if(styleBean.getProductSeason()!=null && !styleBean.getProductSeason().isEmpty()){
				bomStyleList.add(styleBean);
			}
			logger.debug(methodName + "Style List Object : " + bomStyleList);
		}

		
		// Track execution time
		long bomTransEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product BOM API - Transformation End Time: ");
		logger.info(methodName
				+ "Product BOM API - Transformation Total Execution Time (ms): "
				+ (bomTransEndTime - bomTransStartTime));
		return bomStyleList;
	}

	/**
	 * getProductObject.
	 * 
	 * @param prod
	 *            String
	 * @return LCSProduct
	 * @throws WTException
	 *             Exception
	 */
	private static LCSProduct getProductObject(final String prod)
			throws WTException {

		String methodName = "getProductObject() ";
		// Initialisation of product object
		LCSProduct product = null;

		product = (LCSProduct) LCSQuery
				.findObjectById(BurProductConstant.LCSPRODUCT_ROOT_ID
						+ prod);
		product = ((LCSProduct) VersionHelper.latestIterationOf(product));
		logger.debug(methodName + "Product Object: " + product);
		return product;
	}
}

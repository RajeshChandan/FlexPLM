package com.burberry.wc.integration.sampleapi.extraction;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.NoRecordFoundException;
import com.burberry.wc.integration.sampleapi.bean.*;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.sampleapi.transform.BurberrySampleAPITransformHelper;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.sample.SampleOwner;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberrySampleAPIDataExtractionHelper implements
		RemoteAccess {

	/**
	 * BurberrySampleAPIDataExtractionHelper.
	 */
	private BurberrySampleAPIDataExtractionHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIDataExtractionHelper.class);

	/**
	 * API Type.
	 */
	private static final String apiType = "Sample";

	/**
	 * This is starting point of Sample API. Method contains trigger mechanism,
	 * extraction and transformation /**
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
	 * @throws PropertyVetoException 
	 */
	public static Object getSampleAPIData(
			MultivaluedMap<String, String> queryParams) throws BurException,
			IllegalAccessException, InvocationTargetException, ParseException,
			IOException, NoSuchMethodException, WTException, PropertyVetoException {

		// Configure Logger Properties
		BurberryLogFileGenerator.configureSampleAPILog();
		// Enforcement initialisation
		boolean previousEnforcement = true;
		// Method Name
		String methodName = "getSampleAPIData() ";

		// Method Start Time
		long sampleStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Sample API Data Extraction Start Time: ");

		// Initialisation of response map to be be sent back for request
		Map<Status, Object> responseSampleMap = new HashMap<Status, Object>();

		try {
			// Set Authenticated Principal
			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);

			// Step 1: Get Valid Objects Map
			Map<String, String> mapValidSampleObjects = BurberryAPIUtil
					.initializeValidObjects(BurSampleConstant.STR_SAMPLE_API_VALID_OBJECTS);
			logger.debug(methodName + "Valid Sample Objects: "
					+ mapValidSampleObjects);

			// Step 2: Check Passed Parameters
			BurberryAPIUtil.verifyPassedParameters(queryParams,
					mapValidSampleObjects);
			logger.debug(methodName + "Sample Query Params: " + queryParams);

			// Flag to check for Delta criteria
			boolean deltaCriteria = queryParams.toString().contains("Delta");
			logger.debug(methodName + "Delta Criteria: " + deltaCriteria);
			
			// JIRA - BURBERRY-1363: START
			// Step 3: Based on the valid objects get query criteria
			List<Map> criteria = (List<Map>) BurberryAPIUtil.getCriteriaCollection(
					apiType, queryParams, mapValidSampleObjects);
			logger.debug(methodName + "Sample Criteria: " + criteria);

			// Step 4: Pass the criteria map and get all the sample ids
		
			List<String> listofObjects = BurberryAPIUtil.getUniqueObjectIds(criteria,"LCSSAMPLEREQUEST.IDA2A2");

			// JIRA - BURBERRY-1363: END
			
			// Step 5: Pass the Sample collections and get transformed
			// bean data
			SampleRequest sampleRequestAPIBean = getSampleTransformedData(
					listofObjects, criteria, deltaCriteria);
			logger.debug(methodName + "Bean Sample Request List: "
					+ sampleRequestAPIBean);

			// Step 6:  Get the combined collection of sample requests and check for empty
			List colAllSampleRequests = new ArrayList();
			if (sampleRequestAPIBean.getProductSampleRequest() != null) {
				colAllSampleRequests.addAll(sampleRequestAPIBean
						.getProductSampleRequest());
			} else if (sampleRequestAPIBean.getMaterialSampleRequest() != null) {
				colAllSampleRequests.addAll(sampleRequestAPIBean
						.getMaterialSampleRequest());
			}

			// Step 7: Throw exception if matches no record fetched.
			BurberryAPIBeanUtil
					.sendNoRecordFoundException(colAllSampleRequests);

			// Step 8: Setting Response Map
			responseSampleMap.put(Status.OK, sampleRequestAPIBean);

			// Method End Time
			long sampleEndTime = BurberryAPIUtil.printCurrentTime(methodName,
					"Sample API Data Extraction End Time: ");
			logger.info(methodName + "Sample API  Total Execution Time (ms): "
					+ (sampleEndTime - sampleStartTime));

		} catch (final WTException e) {
			responseSampleMap.put(Status.INTERNAL_SERVER_ERROR,
					BurberryAPIBeanUtil.getErrorResponseBean(e.getMessage(),
							Status.INTERNAL_SERVER_ERROR, queryParams,
							sampleStartTime,
							BurSampleConstant.SAMPLE_API_LOG_ENTRY_FLEXTYPE,
							apiType));
			logger.error(BurSampleConstant.STR_SAMPLE_API_ERROR_MSG, e);
		} catch (final ParseException e) {
			responseSampleMap.put(Status.INTERNAL_SERVER_ERROR,
					BurberryAPIBeanUtil.getErrorResponseBean(
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE,
							Status.INTERNAL_SERVER_ERROR, queryParams,
							sampleStartTime,
							BurSampleConstant.SAMPLE_API_LOG_ENTRY_FLEXTYPE,
							apiType));
			logger.error(BurSampleConstant.STR_SAMPLE_API_ERROR_MSG, e);
		} catch (final NoRecordFoundException e) {
			responseSampleMap.put(Status.OK, BurberryAPIBeanUtil
					.getErrorResponseBean(e.getMessage(), Status.OK,
							queryParams, sampleStartTime,
							BurSampleConstant.SAMPLE_API_LOG_ENTRY_FLEXTYPE,
							apiType));
			logger.error(BurSampleConstant.STR_SAMPLE_API_ERROR_MSG, e);
		} catch (final BurException e) {
			responseSampleMap.put(Status.BAD_REQUEST, BurberryAPIBeanUtil
					.getErrorResponseBean(e.getMessage(), Status.BAD_REQUEST,
							queryParams, sampleStartTime,
							BurSampleConstant.SAMPLE_API_LOG_ENTRY_FLEXTYPE,
							apiType));
			logger.error(BurSampleConstant.STR_SAMPLE_API_ERROR_MSG, e);
		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}
		// Return Final Map
		return responseSampleMap;
	}

	/**
	 * This method is used to transform sample data.
	 * 
	 * @param listofObjects
	 *            Collection<FlexObject>
	 * @param criteria
	 *            Collection<Map>
	 * @return SampleRequest
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws PropertyVetoException 
	 */
	private static SampleRequest getSampleTransformedData(
			List<String> listofObjects,
			Collection<Map> criteria, boolean deltaCriteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		// Method Name
		String methodName = "getSampleTransformedData() ";

		// Track Start time
		long transStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Sample API - Transformation Start Time: ");

		// Initialisation
		SampleRequest sampleRequestBean = new SampleRequest();

		// Check the collection of sample Request objects
		if (listofObjects != null && !listofObjects.isEmpty()) {
			logger.debug(methodName + "Sample Request Count: "
					+ listofObjects.size());
			// Initialisation of map filter objects
			Map<String, List> objectMap = BurberryAPIUtil.mapFilter(criteria);
			// Filter product season link collection
			Collection<String> colProdToSeasonLink = objectMap
					.get("LCSPRODUCTSEASONLINK.IDA2A2");
			logger.debug(methodName
					+ "Map Filtered Product-Season Links Objects: "
					+ colProdToSeasonLink);
			// Filter sample collection
			Collection<String> colSamples = objectMap.get("LCSSAMPLE.IDA2A2");
			logger.debug(methodName + "Map Filtered Sample Objects: "
					+ colSamples);

			// Initialisation
			List<ProductSampleRequest> colProductSampleRequest = new ArrayList<ProductSampleRequest>();
			List<MaterialSampleRequest> colMaterialSampleRequest = new ArrayList<MaterialSampleRequest>();

			// Loop through Sample Request Collection
			for (String sampleReq : listofObjects) {
				LCSSampleRequest sampleReqObject = null;
				// Get Sample Request object
				sampleReqObject = getSampleRequestObject(sampleReq);
				logger.info(methodName + "Sample Request Name: "
						+ sampleReqObject.getName());
				SampleOwner ownerMaster = sampleReqObject.getOwnerMaster();
				logger.debug(methodName + "Sample Owner: " + ownerMaster);

				// Check if the owner is Product
				if (ownerMaster instanceof LCSPartMaster) {
					// Get Product Object
					LCSProduct productObj = (LCSProduct) VersionHelper
							.latestIterationOf(ownerMaster);
					logger.debug(methodName + "Product Owner: "
							+ productObj.getName());
					// Get List of Product Sample Request Bean Data
					colProductSampleRequest
							.add(BurberrySampleAPITransformHelper
									.getProductSampleRequestBean(
											sampleReqObject, productObj,
											colProdToSeasonLink, colSamples,
											deltaCriteria));

				}
				// Check if the owner is Material
				else if (ownerMaster instanceof LCSMaterialMaster) {
					// Get Material Object
					LCSMaterial materialObject = (LCSMaterial) VersionHelper
							.getVersion(ownerMaster, "A");
					logger.debug(methodName + "Material Owner: "
							+ materialObject.getName());
					// Get List of Material Sample Request Bean Data
					colMaterialSampleRequest
							.add(BurberrySampleAPITransformHelper
									.getMaterialSampleRequestBean(
											sampleReqObject, materialObject,
											colSamples));
				}
			}
			logger.debug(methodName + "Product Sample List Object: "
					+ colProductSampleRequest);
			logger.debug(methodName + "Product Sample List Size: "
					+ colProductSampleRequest.size());
			// Check if Product Sample Request Size
			if (colProductSampleRequest.size() > 0) {
				sampleRequestBean
						.setProductSampleRequest(colProductSampleRequest);
			}

			logger.debug(methodName + "Material Sample List Object: "
					+ colMaterialSampleRequest);
			logger.debug(methodName + "Material Sample List Size: "
					+ colMaterialSampleRequest.size());
			// Check if Material Sample Request Size
			if (colMaterialSampleRequest.size() > 0) {
				sampleRequestBean
						.setMaterialSampleRequest(colMaterialSampleRequest);
			}
		}
		// Track execution time
		long transEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Sample API -  Transformation End Time: ");
		logger.info(methodName
				+ "Sample API - Transformation Total Execution Time (ms): "
				+ (transEndTime - transStartTime));
		// Return Statement
		return sampleRequestBean;
	}

	/**
	 * This method is used to get sample objects from flex object.
	 * 
	 * @param sampleReq
	 *            Flex Object
	 * @return LCSSampleRequest
	 * @throws WTException
	 *             Exception
	 */
	private static LCSSampleRequest getSampleRequestObject(String sampleReq)
			throws WTException {
		String methodName = "getSampleRequestObject() ";
		// Initialisation of sample request object
		LCSSampleRequest sampleRequest = null;
		// Get Sample Request Object
		sampleRequest = (LCSSampleRequest) LCSQuery
				.findObjectById(BurSampleConstant.LCSSAMPLEREQUEST_ROOT_ID
						+ sampleReq);
		logger.debug(methodName + "Sample Request Object: " + sampleRequest);
		// Return Statement
		return sampleRequest;
	}

}

package com.burberry.wc.integration.palettematerialapi.extraction;

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
import com.burberry.wc.integration.exception.*;
import com.burberry.wc.integration.palettematerialapi.bean.Material;
import com.burberry.wc.integration.palettematerialapi.bean.PaletteMaterialAPI;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.palettematerialapi.transform.BurberryPaletteMaterialAPIDataTransformHelper;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.util.FormatHelper;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIDataExtractionHelper implements
		RemoteAccess {

	/**
	 * BurberryMaterialAPIDataExtractionHelper.
	 */
	private BurberryPaletteMaterialAPIDataExtractionHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPIDataExtractionHelper.class);

	/**
	 * apiType.
	 */
	private static final String apiType = "Palette Material";

	/**
	 * This is starting point of Palette Material API. Method contains trigger
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
	 *             WTException
	 * @throws PropertyVetoException 
	 */
	public static Object getPaletteMaterialAPIData(
			MultivaluedMap<String, String> queryParams) throws BurException,
			IllegalAccessException, InvocationTargetException, ParseException,
			IOException, NoSuchMethodException, WTException, PropertyVetoException {

		// Configure Logger Properties
		BurberryLogFileGenerator.configurePaletteMaterialAPILog();
		boolean previousEnforcement = true;
		String methodName = "getPaletteMaterialAPIData() ";

		// Method Start Time
		long palMatStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Palette Material API Data Extraction Start Time: ");

		// Initialisation of response map to be be sent back for request
		Map<Status, Object> responseMap = new HashMap<Status, Object>();

		try {
			// Set Authenticated Principal
			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);

			// Initialisation of a new Material Bean Object
			PaletteMaterialAPI paletteMaterialBean = new PaletteMaterialAPI();

			// Step 1: Get Valid Objects Map
			Map<String, String> mapValidObjects = BurberryAPIUtil
					.initializeValidObjects(BurPaletteMaterialConstant.STR_PALETTE_MATERIAL_VALID_OBJECTS);
			logger.debug(methodName + "mapValidObjects: " + mapValidObjects);

			// Step 2: Check Passed Parameters
			BurberryAPIUtil
					.verifyPassedParameters(queryParams, mapValidObjects);
			logger.debug(methodName + "queryParams: " + queryParams);
			
			// JIRA - BURBERRY-1363: START
			// Step 3: Based on the valid objects get query criteria
			List<Map> criteria = (List<Map>)BurberryAPIUtil.getCriteriaCollection(
					apiType, queryParams, mapValidObjects);
			logger.debug(methodName + "Palette Material Criteria: " + criteria);
			
			List<String> listofObjects = BurberryAPIUtil.getUniqueObjectIds(criteria,"LCSMATERIAL.BRANCHIDITERATIONINFO");
			List<Material> lstMaterial = new ArrayList<Material>();
			// Step 5: Pass the Material collections and get transformed bean
			// data
			if (listofObjects != null && !listofObjects.isEmpty()) {
				lstMaterial = getTransformedMaterialData(listofObjects,
					criteria);
			}
			logger.debug(methodName + "Bean Material List: " + lstMaterial);
			logger.info(methodName + "Number of Materials: "
					+ lstMaterial.size());

			// Step 6: Set the list of all the bean data
			paletteMaterialBean.setMaterial(lstMaterial);

			// Step 7: Throw exception if matches no record fetched.
			BurberryAPIBeanUtil.sendNoRecordFoundException(paletteMaterialBean
					.getMaterial());

			// Step 8: Setting Response Map
			responseMap.put(Status.OK, paletteMaterialBean);

			// Method End Time
			long palMatEndTime = BurberryAPIUtil.printCurrentTime(methodName,
					"Palette Material API Data Extraction End Time: ");
			logger.info(methodName
					+ "Palette Material API  Total Execution Time (ms): "
					+ (palMatEndTime - palMatStartTime));

		} catch (final WTException e) {
			responseMap
					.put(Status.INTERNAL_SERVER_ERROR,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.INTERNAL_SERVER_ERROR,
									queryParams,
									palMatStartTime,
									BurPaletteMaterialConstant.PALETTE_MATERIAL_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(
					BurPaletteMaterialConstant.STR_ERROR_MSG_PALATTE_MATERIAL_API,
					e);
		} catch (final ParseException e) {
			responseMap
					.put(Status.INTERNAL_SERVER_ERROR,
							BurberryAPIBeanUtil
									.getErrorResponseBean(
											BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE,
											Status.INTERNAL_SERVER_ERROR,
											queryParams,
											palMatStartTime,
											BurPaletteMaterialConstant.PALETTE_MATERIAL_API_LOG_ENTRY_FLEXTYPE,
											apiType));
			logger.error(
					BurPaletteMaterialConstant.STR_ERROR_MSG_PALATTE_MATERIAL_API,
					e);
		} catch (final NoRecordFoundException e) {
			responseMap
					.put(Status.OK,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.OK,
									queryParams,
									palMatStartTime,
									BurPaletteMaterialConstant.PALETTE_MATERIAL_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(
					BurPaletteMaterialConstant.STR_ERROR_MSG_PALATTE_MATERIAL_API,
					e);
		} catch (final BurException e) {
			responseMap
					.put(Status.BAD_REQUEST,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.BAD_REQUEST,
									queryParams,
									palMatStartTime,
									BurPaletteMaterialConstant.PALETTE_MATERIAL_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(
					BurPaletteMaterialConstant.STR_ERROR_MSG_PALATTE_MATERIAL_API,
					e);
		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}
		return responseMap;
	}

	/**
	 * Method to Transform Material data.
	 * 
	 * @param listofObjects
	 *            Collection of Materials
	 * @param criteria
	 *            Map
	 * @return List Material Bean
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws PropertyVetoException 
	 */
	private static List<Material> getTransformedMaterialData(
			List<String> listofObjects, Collection<Map> criteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		String methodName = "getTransformedMaterialData() ";
		// Track Start time
		long transStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Palette Material API - Transformation Start Time: ");
		
		List<Material> materialList = new ArrayList<Material>();
		// Check if collection of products is not empty
		
		// Initialisation
		

		Map<String, List> objectMap = BurberryAPIUtil.mapFilter(criteria);
		// Filter material supplier collection
		Collection<String> colMaterialSupplierLinks = objectMap
				.get(BurPaletteMaterialConstant.MATERIAL_SUPPLIER_BRANCHID);
		logger.debug(methodName + "Material-Supplier Filter Map: "
				+ colMaterialSupplierLinks);

		// Filter material colour collection
		Collection<String> colMaterialColourLinks = objectMap
				.get(BurPaletteMaterialConstant.MATERIAL_COLOR_ID);
		logger.debug(methodName + "Material-Colour Filter Map: "
				+ colMaterialColourLinks);

		// Filter palette collection
		Collection<String> colPalettes = objectMap
				.get(BurPaletteMaterialConstant.PALETTE_ID);
		logger.debug(methodName + "Palette Filter Map: " + colPalettes);

		// CR R26: Handle Remove Object Customisation: Start

		// Tracked Palette Material Colour Data
		Map<String, Collection<HashMap>> mapTrackedPalette = BurberryAPIUtil
				.getPaletteDeletionsMapData(
						criteria,
						BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME);

		// Tracked Risk Management Data
		Map<String, Collection<HashMap>> mapTrackedRiskManagement = BurberryAPIUtil
				.getRemovedMOARowsDeleted(
						criteria,
						BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO,
						BurPaletteMaterialConstant.BO_TRACK_RISK_MANAGEMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_OWNER_ID,
						BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_MOA_OBJECT_ID);

		// Tracked Yarn Details Data
		Map<String, Collection<HashMap>> mapTrackedYarnDetails = BurberryAPIUtil
				.getRemovedMOARowsDeleted(
						criteria,
						BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO,
						BurPaletteMaterialConstant.BO_TRACK_YARN_DETAILS_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_YARN_DETAILS_OWNER_ID,
						BurPaletteMaterialConstant.MOA_TRACK_YARN_DETAILS_MOA_OBJECT_ID);

		// Tracked Material Document Data
		Map<String, Collection<HashMap>> mapTrackedMaterialDocument = BurberryAPIUtil
				.getRemovedMOARowsDeleted(
						criteria,
						BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_DOCUMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_ID,
						BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID);

		// Tracked Material Price Management Data
		Map<String, Collection<HashMap>> mapTrackedMaterialPriceMgmt = BurberryAPIUtil
				.getRemovedMOARowsDeleted(
						criteria,
						BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_ID,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_MOA_OBJECT_ID);

		// Tracked Material Price Entry Data
		Map<String, Collection<HashMap>> mapTrackedMaterialPriceEntry = BurberryAPIUtil
				.getRemovedMOARowsDeleted(
						criteria,
						BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICING_ENTRY_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_SUPPLIER_ID,
						BurPaletteMaterialConstant.MOA_TRACK_OBJECT_ID);

		// BURBERRY-1485 RD 74: Material Supplier Documents - Start
		// Tracked Material Supplier Document Data
		Map<String, Collection<HashMap>> mapTrackedMaterialSupplierDocument = BurberryAPIUtil
				.getRemovedMOARowsDeleted(
						criteria,
						BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_SUPPLIER_ID,
						BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID);
		// BURBERRY-1485 RD 74: Material Supplier Documents - End

		// CR R26: Handle Remove Object Customisation: End

		// Check if collection of materials is not empty
		
		logger.debug(methodName + "Material Count: " + listofObjects.size());
		// Loop through Material Collection
		for (String mat : listofObjects) {
			LCSMaterial materialObject = null;
			// Get material object
			materialObject = getMaterialObject(mat);
			if (!FormatHelper.areWTObjectsEqual(materialObject.getMaster(),
					LCSMaterialQuery.PLACEHOLDER)) {
				logger.info(methodName + "Material Object: " + materialObject.getName());
				// Get List of Material Bean Data
				materialList.add(BurberryPaletteMaterialAPIDataTransformHelper
						.getMaterialBean(materialObject,
								colMaterialSupplierLinks,
								colMaterialColourLinks, colPalettes,
								mapTrackedPalette, mapTrackedRiskManagement,
								mapTrackedYarnDetails,
								mapTrackedMaterialPriceMgmt,
								mapTrackedMaterialPriceEntry,
								mapTrackedMaterialDocument,
								mapTrackedMaterialSupplierDocument));
				logger.debug(methodName + "Material List Object : "
						+ materialList);
			}
		}
		
	
		// Track execution time
		long transEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Palette Material API - Transformation End Time: ");
		logger.info(methodName
				+ "Palette Material API - Transformation Total Execution Time (ms): "
				+ (transEndTime - transStartTime));
		// Return Statement
		return materialList;
	}

	/**
	 * Method to get Material Object.
	 * 
	 * @param mat
	 *            FlexObject
	 * @return Material Object
	 * @throws WTException
	 *             Exception
	 */
	private static LCSMaterial getMaterialObject(String mat)
			throws WTException {
		String methodName = "getMaterialObject() ";
		// Initialisation of material object
		LCSMaterial material = null;
		// Get Material Object
		material = (LCSMaterial) LCSQuery
				.findObjectById(BurPaletteMaterialConstant.LCSMATERIAL_ROOT_ID
						+ mat);
		material = ((LCSMaterial) VersionHelper.latestIterationOf(material));
		logger.debug(methodName + "Material Object: " + material);
		// Return Statement
		return material;
	}

}

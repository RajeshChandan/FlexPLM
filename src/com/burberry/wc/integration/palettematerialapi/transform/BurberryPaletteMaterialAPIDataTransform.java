package com.burberry.wc.integration.palettematerialapi.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.bean.*;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteMaterialColorLink;
import com.lcs.wc.color.LCSPaletteMaterialLink;
import com.lcs.wc.color.LCSPaletteQuery;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.*;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A class to handle Transformation activity. Class contain several method to
 * handle transformation activity i.e. Transforming Data from different objects
 * and putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIDataTransform {

	/**
	 * Default Constructor.
	 */
	private BurberryPaletteMaterialAPIDataTransform() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger.getLogger(BurberryPaletteMaterialAPIDataTransform.class);

	/**
	 * STR_MOA_OBJECT_ID.
	 */
	private static final String STR_MOA_OBJECT_ID = "MOA_OBJECT_ID";

	/**
	 * STR_MOA_OWNER_ID.
	 */
	private static final String STR_MOA_OWNER_ID = "OWNER_ID";

	/**
	 * Method to list of palette bean data.
	 * 
	 * @param materialObject         Material Object
	 * @param colPalettes            colPalettes
	 * @param mapTrackedPalette
	 * @param associatedPaletteNames
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 */
	public static List<Palette> getListPaletteBean(LCSMaterial materialObject, Collection<String> colPalettes,
			Map<String, Collection<HashMap>> mapTrackedPalette)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		String methodName = "getListPaletteBean() ";
		// Track execution time
		long palStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Palette Transform Start Time: ");

		// Initialisation
		List<Palette> lstPalette = new ArrayList<Palette>();

		// Get the collection of Palette using material object
		Collection<LCSPalette> colPalette = new LCSMaterialQuery().findPalettesForMaterial(materialObject);

		QueryResult result = PersistenceHelper.manager.navigate(materialObject.getMaster(),
				LCSPaletteMaterialLink.PALETTE_ROLE, LCSPaletteMaterialLink.class, false);
		Collection<LCSPaletteMaterialLink> paletteMaterialLinks = new ArrayList<LCSPaletteMaterialLink>();
		while (result.hasMoreElements()) {
			paletteMaterialLinks.add((LCSPaletteMaterialLink) result.nextElement());
		}
		System.out.println(methodName + "Palette Collection: " + colPalette);

		// CR R26: Handle Remove Object Customisation:
		List<String> associatedPaletteNames = new ArrayList<String>();
		List<String> associatedSupplierNames = new ArrayList<String>();

		// Loop through palette collection
		for (LCSPalette palette : colPalette) {
			logger.debug(methodName + "Palette: " + palette.getName());
			// checking if palette exists
			boolean paletteExists = BurberryAPIDBUtil
					.checkIfObjectExists(FormatHelper.getNumericObjectIdFromObject(palette), colPalettes);

			logger.debug(methodName + "Palette Exists: " + paletteExists);
			// If palette exists
			if (paletteExists) {
				// CR R26: Handle Remove Object Customisation:
				associatedPaletteNames.add(palette.getName());
				// Extract Palette object data
				Palette paletteBean = BurberryPaletteMaterialAPIJsonDataUtil.getPaletteBean(palette);
				List<PaletteSupplier> paletteSuppliers = new ArrayList<PaletteSupplier>();
				for (LCSPaletteMaterialLink paletteLink : paletteMaterialLinks) {
					if (FormatHelper.areWTObjectsEqual(paletteLink.getPalette(), palette)) {
						PaletteSupplier palSupplierBean = new PaletteSupplier();
						LCSSupplier supplierObj = (LCSSupplier) VersionHelper.latestIterationOf(paletteLink.getSupplierMaster());
						if (!supplierObj.isPlaceholder()) {
							associatedSupplierNames.add(supplierObj.getName());
							palSupplierBean = BurberryPaletteMaterialAPIJsonDataUtil
									.getPaletteSupplierBean(supplierObj);
							paletteSuppliers.add(palSupplierBean);
						}
					}
				}

				List<PaletteSupplier> lstRemovedPaletteSuppliers = getRemovedListPaletteSupplierBean(materialObject,
						associatedSupplierNames, mapTrackedPalette);

				paletteSuppliers.addAll(lstRemovedPaletteSuppliers);
				paletteBean.setPaletteSupplier(paletteSuppliers);

				logger.debug(methodName + "Palette bean: " + paletteBean);
				// Set Palette Bean data
				lstPalette.add(paletteBean);
			}
		}

		// CR R26: Handle Remove Object Customisation: Start
		List<Palette> lstRemovedPalette = getRemovedListPaletteBean(materialObject, associatedPaletteNames,
				mapTrackedPalette);
		logger.debug(methodName + "List of Removed Palette Names: " + lstRemovedPalette);
		lstPalette.addAll(lstRemovedPalette);
		// CR R26: Handle Remove Object Customisation: End

		// Track execution time
		long palEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Palette Transform End Time: ");
		logger.debug(methodName + "Palette Transform  Total Execution Time (ms): " + (palEndTime - palStartTime));

		// Return Statement
		return lstPalette;
	}

	/**
	 * @param materialObject
	 * @param associatedSupplierIds
	 * @param mapTrackedPalette
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private static List<PaletteSupplier> getRemovedListPaletteSupplierBean(LCSMaterial materialObject,
			List<String> associatedSupplierIds, Map<String, Collection<HashMap>> mapTrackedPalette)
			throws IllegalAccessException, InvocationTargetException {

		String methodName = "getRemovedListPaletteSupplierBean() ";
		// Track execution time
		long remPalStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Palette Info Transform Start Time: ");
		List<PaletteSupplier> removedPaletteSuppliers = new ArrayList<PaletteSupplier>();

		// Initialisation
		List<String> removedSupplierList = new ArrayList<String>();

		// Check tracked map contains material id
		if (mapTrackedPalette.containsKey(String.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedPalette.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed palette name
				String strRemovedSupplierName = (String) hm.get("SUPPLIER_NAME");
				// Add to list
				removedSupplierList.add(strRemovedSupplierName);
			}
		}

		// Remove Duplicate Palette Names
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedSupplierList);
		removedSupplierList.clear();
		removedSupplierList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil.compareAndRemoveSameObjects(associatedSupplierIds,
				removedSupplierList);
		System.out.println(methodName + "List Removed PAlette Supplier Names: " + lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedPaletteSupName : lstComparedAndRemovedObjects) {
			// Initialisation
			PaletteSupplier removedPaletteSupplierBean = new PaletteSupplier();
			BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(removedPaletteSupplierBean, "suppName",
					strRemovedPaletteSupName);
			removedPaletteSupplierBean.setSuppName(strRemovedPaletteSupName);
			logger.debug(methodName + "RemovedPaletteBean: " + removedPaletteSupplierBean);
			// Add to the list
			removedPaletteSuppliers.add(removedPaletteSupplierBean);
		}

		logger.debug(methodName + "Removed Palette Names Size: " + removedPaletteSuppliers.size());

		// Track execution time
		long remPalEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Palette Material Transform End Time: ");
		logger.debug(methodName + "Remove Palette Material Transform  Total Execution Time (ms): "
				+ (remPalEndTime - remPalStartTime));
		return removedPaletteSuppliers;
	}

	// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
	/**
	 * Method to get removed material documents
	 * 
	 * @param mapTrackedMaterialDocument
	 * @param materialObjectID
	 * @param associatedDocumentIds
	 * @return
	 */
	public static List<String> getRemovedMaterialDocumentIds(
			Map<String, Collection<HashMap>> mapTrackedMaterialDocument, String materialObjectID,
			List<String> associatedDocumentIds) {
		// Method Name
		String methodName = "getRemovedMaterialDocumentIds() ";
		// Initialisation
		List<String> removedDocumentIdList = new ArrayList<String>();
		logger.info("check " + materialObjectID);
		// Check tracked map contains material id
		if (mapTrackedMaterialDocument.containsKey(String.valueOf(materialObjectID))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedMaterialDocument.get(String.valueOf(materialObjectID));
			logger.info("Material Documents Map >> " + colMap);
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed document id
				String removedDocumentId = (String) hm.get(STR_MOA_OBJECT_ID);
				// Get Material Supplier Id
				String strMaterialId = String.valueOf(hm.get(STR_MOA_OWNER_ID));
				// Check Material Id
				if (FormatHelper.hasContent(strMaterialId) && materialObjectID.equalsIgnoreCase(strMaterialId)) {
					// Add to list
					removedDocumentIdList.add(removedDocumentId);
				}
			}
		}
		// Remove Duplicate Document Id
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedDocumentIdList);
		removedDocumentIdList.clear();
		removedDocumentIdList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil.compareAndRemoveSameObjects(associatedDocumentIds,
				removedDocumentIdList);
		logger.info(methodName + "List Removed Docuement Ids: " + lstComparedAndRemovedObjects);

		return lstComparedAndRemovedObjects;
	}

	/**
	 * Method to get removed material supplier documents
	 * 
	 * @param mapTrackedMaterialSupplierDocument
	 * @param materialObjectID
	 * @param associatedDocumentIds
	 * @return
	 */
	public static List<String> getRemovedMaterialSupplierDocumentIds(
			Map<String, Collection<HashMap>> mapTrackedMaterialSupplierDocument, String materialObjectID,
			String materialSuppObjectID, List<String> associatedDocumentIds) {

		String methodName = "getRemovedMaterialSupplierDocumentIds() ";
		// Initialisation
		List<String> removedDocumentIdList = new ArrayList<String>();
		// Check tracked map contains material id
		if (mapTrackedMaterialSupplierDocument.containsKey(String.valueOf(materialObjectID))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedMaterialSupplierDocument.get(String.valueOf(materialObjectID));
			logger.debug("Material Supplier Documents Map >> " + colMap);
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed document id
				String removedDocumentId = (String) hm.get(STR_MOA_OBJECT_ID);
				// Get Material Supplier Id
				String strMaterialSupplierId = String.valueOf(hm.get(STR_MOA_OWNER_ID));
				// Check Material Supplier Id
				if (FormatHelper.hasContent(strMaterialSupplierId)
						&& materialSuppObjectID.equalsIgnoreCase(strMaterialSupplierId)) {
					// Add to list
					removedDocumentIdList.add(removedDocumentId);
				}
			}
		}
		// Remove Duplicate Document Id
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedDocumentIdList);
		removedDocumentIdList.clear();
		removedDocumentIdList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil.compareAndRemoveSameObjects(associatedDocumentIds,
				removedDocumentIdList);
		logger.debug(methodName + "List Removed Docuement Ids: " + lstComparedAndRemovedObjects);

		return lstComparedAndRemovedObjects;
	}
	// BURBERRY-1485 New Attributes Additions post Sprint 8: End

	/**
	 * Method to get Yarn Details Bean Data.
	 * 
	 * @param materialObject        LCSMaterial
	 * @param mapTrackedYarnDetails
	 * @param endModifyDate
	 * @param startModifyDate
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 */
	public static List<YarnDetail> getListYarnDetailsBean(LCSMaterial materialObject,
			Map<String, Collection<HashMap>> mapTrackedYarnDetails)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		String methodName = "getYarnDetailsBean() ";
		// Track execution time
		long yarnStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Yarn Detail Transform Start Time: ");

		// Initialisation
		ArrayList<YarnDetail> listYarnDetail = new ArrayList<YarnDetail>();

		// Get Material Flex Type for Yarn Detail
		String strFlexTypePath = BurPaletteMaterialConstant.MATERIAL_YARN_DETAILS;
		logger.debug(methodName + "String FlexType Path: " + strFlexTypePath);

		// Split the string with comma and get flextype path
		String slFlexTypePath[] = strFlexTypePath.split(",");
		logger.debug(methodName + "Material Flex Type Path: " + materialObject.getFlexType().getFullName(true));
		// Loop through each flex type path
		for (String strPath : slFlexTypePath) {
			logger.debug(methodName + "strPath: " + strPath);
			// Check if the flex type path matches with material flex type path
			if (materialObject.getFlexType().getFullName(true).contains(strPath)) {
				// Getting risk management MOA objects from Material
				Collection<LCSMOAObject> yarnDetails = LCSMOAObjectQuery.findMOACollection(materialObject,
						FlexTypeCache.getFlexType(materialObject)
								.getAttribute(BurPaletteMaterialConstant.MATERIAL_MOA_YARN_DETAIL_ATT));
				logger.debug(methodName + "YarnDetails: " + yarnDetails);
				// Loop through each MOA object
				for (LCSMOAObject yarnDtl : yarnDetails) {
					logger.debug(methodName + "Yarn Detail Object: " + yarnDtl);
					// Extract details from each Yarn Detail Object
					YarnDetail yarnDetailBean = BurberryPaletteMaterialAPIJsonDataUtil.getYarnDetailBean(yarnDtl);
					logger.debug(methodName + "Yarn Detail Bean: " + yarnDetailBean);
					listYarnDetail.add(yarnDetailBean);
				}

				// CR R26: Handle Remove Object Customisation: Start
				List<YarnDetail> lstRemovedYarnDetail = getRemovedListYarnDetail(materialObject, mapTrackedYarnDetails);
				logger.debug(methodName + "List of Removed Yarn Detail: " + lstRemovedYarnDetail);
				listYarnDetail.addAll(lstRemovedYarnDetail);
				// CR R26: Handle Remove Object Customisation: End

				break;
			}
		}
		logger.debug(methodName + "List of Yarn Detail Beans: " + listYarnDetail);
		// Track execution time
		long yarnEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Yarn Detail Transform End Time: ");
		logger.debug(methodName + "Yarn Detail Transform  Total Execution Time (ms): " + (yarnEndTime - yarnStartTime));
		// Return Statement
		return listYarnDetail;
	}

	/**
	 * Method to get list of Risk Management Bean Data.
	 * 
	 * @param materialObject           material object
	 * @param mapTrackedRiskManagement
	 * @param endModifyDate
	 * @param startModifyDate
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 */
	public static List<RiskManagement> getListRiskManagementBean(LCSMaterial materialObject,
			Map<String, Collection<HashMap>> mapTrackedRiskManagement)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		String methodName = "getListRiskMgmtBean() ";
		// Track execution time
		long rmStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Risk Management Transform Start Time: ");

		// Initialisation
		List<RiskManagement> listRiskManagement = new ArrayList<RiskManagement>();

		// Get Material Flex Type for Risk Management
		String strFlexTypePath = BurPaletteMaterialConstant.MATERIAL_RISK_MANAGEMENT_FLEXTYPE;
		logger.debug(methodName + "String FlexType Path: " + strFlexTypePath);
		String slFlexTypePath[] = strFlexTypePath.split(",");
		logger.debug(methodName + "Material Flex Type Path: " + materialObject.getFlexType().getFullName(true));
		// Loop through each flex type path
		for (String strPath : slFlexTypePath) {
			logger.debug(methodName + "strPath: " + strPath);
			// Check if the flex type path matches with material flex type path
			if (materialObject.getFlexType().getFullName(true).contains(strPath)) {
				// Getting risk management MOA objects from Material
				Collection<LCSMOAObject> riskManagements = LCSMOAObjectQuery.findMOACollection(materialObject,
						FlexTypeCache.getFlexType(materialObject).getAttribute(BurConstant.RM_ALL_KEY));
				logger.debug(methodName + "RiskManagement: " + riskManagements);
				// Loop through each MOA object
				for (LCSMOAObject riskmgt : riskManagements) {
					logger.debug(methodName + "Risk Management Object: " + riskmgt);
					// Extract details from each Risk Management Object
					RiskManagement riskManagementBean = BurberryPaletteMaterialAPIJsonDataUtil
							.getRiskManagementBean(riskmgt);
					logger.debug(methodName + "Risk Management Bean: " + riskManagementBean);
					listRiskManagement.add(riskManagementBean);
				}

				// CR R26: Handle Remove Object Customisation: Start
				List<RiskManagement> lstRemovedRiskManagement = getRemovedListRiskManagement(materialObject,
						mapTrackedRiskManagement);
				logger.debug(methodName + "List of Removed Risk Management: " + lstRemovedRiskManagement);
				listRiskManagement.addAll(lstRemovedRiskManagement);
				// CR R26: Handle Remove Object Customisation: End

				break;
			}
		}
		// Track execution time
		logger.debug(methodName + "List of Risk Management Beans: " + listRiskManagement);
		long rmEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Risk Management Transform End Time: ");
		logger.debug(methodName + "Risk Management Transform  Total Execution Time (ms): " + (rmEndTime - rmStartTime));
		// Return Statement
		return listRiskManagement;
	}

	/**
	 * Method to get Mat Price Management Bean.
	 * 
	 * @param materialObject
	 * 
	 * @param matSupplier                 matSupplier
	 * @param mapTrackedMaterialPriceMgmt
	 * @param endModifyDate
	 * @param startModifyDate
	 * @param mapDeltaDateTime
	 * @return List<MatPriceManagement>
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 */
	public static List<MatPriceManagement> getListMatPriceManagementBean(LCSMaterial materialObject,
			LCSMaterialSupplier matSupplier, Map<String, Collection<HashMap>> mapTrackedMaterialPriceMgmt)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		String methodName = "getListMatPriceManagementBean() ";
		// Track execution time
		long matPriceMgmtStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Price Management Transform Start Time: ");

		// Initialisation
		List<MatPriceManagement> listMatPriceMgmt = new ArrayList<MatPriceManagement>();

		// Commented: CR 73: Enabled Material Price Management for All Material Type:
		// Start
		// Get Material Flex Type for Material Size
		// String strFlexTypePath =
		// BurPaletteMaterialConstant.MATERIAL_PRICE_MANAGEMENT_FLEXTYPE;
		// logger.debug(methodName + "String FlexType Path: " +
		// strFlexTypePath);
		// String slFlexTypePath[] = strFlexTypePath.split(",");
		// logger.debug(methodName + "Material Flex Type Path: "
		// + matSupplier.getFlexType().getFullName(true));
		// Loop through each flex type path
		// for (String strPath : slFlexTypePath) {
		// logger.debug(methodName + "strPath: " + strPath);
		// Check if the flex type path matches with material flex type path
		// if (matSupplier.getFlexType().getFullName(true).contains(strPath)) {
		// Commented: CR 73: Enabled Material Price Management for All Material Type:
		// End
		// Getting Material Price MOA objects from Material
		Collection<LCSMOAObject> colMatPriceMgmt = LCSMOAObjectQuery.findMOACollection(matSupplier,
				FlexTypeCache.getFlexType(matSupplier)
						.getAttribute(BurPaletteMaterialConstant.MATERIAL_MOA_MATERIAL_PRICE_MGMT_ATT));
		logger.debug(methodName + "Material Price Mgmt: " + colMatPriceMgmt);
		// Loop through each MOA object
		for (LCSMOAObject matPriceMgmt : colMatPriceMgmt) {
			logger.debug(methodName + "Material Price Mgmt row: " + matPriceMgmt);
			// Extract details from each Material Size Row
			MatPriceManagement matPriceMgmtBean = BurberryPaletteMaterialAPIJsonDataUtil
					.getMaterialPriceManagementBean(matPriceMgmt);
			logger.debug(methodName + "Material Price Mgmt Bean: " + matPriceMgmtBean);
			listMatPriceMgmt.add(matPriceMgmtBean);
		}

		// CR R26: Handle Remove Object Customisation: Start
		List<MatPriceManagement> lstRemovedMatPriceManagement = getRemovedListMatPriceManagement(materialObject,
				matSupplier, mapTrackedMaterialPriceMgmt);
		logger.debug(methodName + "List of Removed Risk Management: " + lstRemovedMatPriceManagement);
		listMatPriceMgmt.addAll(lstRemovedMatPriceManagement);
		// CR R26: Handle Remove Object Customisation: End

		// break;
		// }
		// }

		// Track execution time
		logger.debug(methodName + "List of Material Price Management Beans: " + listMatPriceMgmt);
		long matPriceMgmtEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Price Management  End Time: ");
		logger.debug(methodName + "Material Price Management Total Execution Time (ms): "
				+ (matPriceMgmtEndTime - matPriceMgmtStartTime));
		// Return Statement
		return listMatPriceMgmt;
	}

	/**
	 * Method to get Palette Info Bean.
	 * 
	 * @param materialObject    LCSMaterial
	 * @param matColor          LCSMaterialColor
	 * @param colPalettes       Collection
	 * @param mapTrackedPalette Map
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 */
	public static List<PaletteInfo> getListPaletteInfoBean(LCSMaterial materialObject, LCSMaterialColor matColor,
			Collection<String> colPalettes, Map<String, Collection<HashMap>> mapTrackedPalette)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {

		// Method Name
		String methodName = "getPaletteInfoBean() ";
		// Track execution time
		long palStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Palette Info Transform Start Time: ");

		List<PaletteInfo> lstPaletteInfoBean = new ArrayList<PaletteInfo>();

		// Get the collection of Palettes using Material Objects
		Collection<LCSPalette> paletteCollection = new LCSMaterialQuery().findPalettesForMaterial(materialObject);
		logger.debug(methodName + "Collection of Palette: " + paletteCollection);

		// CR R26
		List<String> associatedPaletteMaterialColors = new ArrayList<String>();

		// Loop through palette collection
		for (LCSPalette palette : paletteCollection) {
			logger.debug(methodName + "Palette: " + palette.getName());
			// checking if palette exists
			boolean paletteExists = BurberryAPIDBUtil
					.checkIfObjectExists(FormatHelper.getNumericObjectIdFromObject(palette), colPalettes);
			logger.debug(methodName + "Palette Info Exists: " + paletteExists);

			// If palette exists
			if (paletteExists) {
				LCSMaterialSupplierMaster materialSupplierMaster = (LCSMaterialSupplierMaster) matColor
						.getMaterialSupplierMaster();
				logger.debug(methodName + "Material Colour Supplier Master: " + materialSupplierMaster);

				// Get all the Palette Material Colour Links
				Collection<LCSPaletteMaterialColorLink> colPaletteMatColourLinks = BurberryAPIBeanUtil
						.getPaleteMaterialColourLink(palette, materialSupplierMaster);
				logger.debug(methodName + "PaletteMatColourLinks Collection: " + colPaletteMatColourLinks);
				// Loop through Palette Material Collection
				for (LCSPaletteMaterialColorLink palMatColourLink : colPaletteMatColourLinks) {
					logger.debug(methodName + "Palette Material Colour Link: " + palMatColourLink);

					// Get Palette from Palette Material Link
					LCSPalette linkedPalette = (LCSPalette) palMatColourLink.getPalette();
					logger.debug(methodName + "Linked Palette: " + linkedPalette);

					// Get Material Colour from Palette Material Link
					LCSMaterialColor linkedMaterialColour = (LCSMaterialColor) palMatColourLink.getMaterialColor();
					logger.debug(methodName + "Linked Material Colour: " + linkedMaterialColour);

					// Get Supplier Master from Palette Material Link
					LCSMaterialSupplierMaster linkedMaterialSupplierMaster = (LCSMaterialSupplierMaster) linkedMaterialColour
							.getMaterialSupplierMaster();
					logger.debug(methodName + "Linked Material Supplier Master: " + linkedMaterialSupplierMaster);

					// Check and Set PaletteInfo Bean
					if (FormatHelper.areWTObjectsEqual(linkedPalette, palette)
							&& FormatHelper.areWTObjectsEqual(linkedMaterialColour, matColor)
							&& FormatHelper.areWTObjectsEqual(linkedMaterialSupplierMaster, materialSupplierMaster)) {

						// CR R26
						associatedPaletteMaterialColors.add(palette.getName());

						// Extract Palette INFO Bean
						PaletteInfo paletteInfoBean = BurberryPaletteMaterialAPIJsonDataUtil
								.getPaletteInfoBean(palMatColourLink);
						logger.debug(methodName + "paletteInfoBean: " + paletteInfoBean);
						lstPaletteInfoBean.add(paletteInfoBean);
					}
				}
			}
		}

		// CR R26: Handle Remove Palette Material Colour Customisation:
		// Start
		List<PaletteInfo> colRemovedPaletteInfo = getRemovedListPaletteInfoBean(materialObject, matColor,
				associatedPaletteMaterialColors, mapTrackedPalette);
		logger.debug(methodName + "List of Removed Palette Info Bean: " + colRemovedPaletteInfo);
		lstPaletteInfoBean.addAll(colRemovedPaletteInfo);
		// CR R26: Handle Remove Palette Material Colour Customisation:
		// End

		logger.debug("Palette Info Bean List: " + lstPaletteInfoBean);

		// Track execution time
		long palEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Palette Info Transform End Time: ");
		logger.debug(methodName + "Palette Info Transform  Total Execution Time (ms): " + (palEndTime - palStartTime));
		// Return Statement
		return lstPaletteInfoBean;
	}

	/**
	 * Method to get List of Removed Palette Bean.
	 * 
	 * @param materialObject         LCSMaterial
	 * @param associatedPaletteNames
	 * @param mapTrackedPalette
	 * @param mapDeltaDateTime       Map
	 * @return List<Palette>
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws WTException               Exception
	 */
	// CR R26: Handle Remove Palette Material Colour Customisation : Start

	public static List<Palette> getRemovedListPaletteBean(LCSMaterial materialObject,
			List<String> associatedPaletteNames, Map<String, Collection<HashMap>> mapTrackedPalette)
			throws IllegalAccessException, InvocationTargetException, WTException {

		String methodName = "getListRemovedPaletteBean() ";
		// Track execution time
		long remPalStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Palette Info Transform Start Time: ");

		// Initialisation
		List<Palette> lstRemovedPaletteBean = new ArrayList<Palette>();
		List<String> removedPaletteNameList = new ArrayList<String>();
		HashMap<String, String> paletteSupplierMap = new HashMap<String, String>();

		// Check tracked map contains material id
		if (mapTrackedPalette.containsKey(String.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedPalette.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed palette name
				String strRemovedPaletteName = (String) hm.get("PALETTE_NAME");
				String strSupplierName = (String) hm.get("SUPPLIER_NAME");
				if (FormatHelper.hasContent(strSupplierName)) {
					if (FormatHelper.hasContent(paletteSupplierMap.get(strRemovedPaletteName))) {
						strSupplierName = strSupplierName + "|~*~|" + paletteSupplierMap.get(strRemovedPaletteName);
					}
					paletteSupplierMap.put(strRemovedPaletteName, strSupplierName);
				}
				// Add to list
				removedPaletteNameList.add(strRemovedPaletteName);
			}
		}

		// Remove Duplicate Palette Names
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedPaletteNameList);
		removedPaletteNameList.clear();
		removedPaletteNameList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil.compareAndRemoveSameObjects(associatedPaletteNames,
				removedPaletteNameList);
		logger.debug(methodName + "List Removed Palette Names: " + lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedPaletteName : lstComparedAndRemovedObjects) {

			// Initialisation
			Palette removedPaletteBean = new Palette();
			List<PaletteSupplier> lstRemovedPaletteSupBean = new ArrayList<PaletteSupplier>();

			BurberryPaletteMaterialAPIJsonDataUtil.getRemovedPaletteBean(strRemovedPaletteName, removedPaletteBean);
			if (FormatHelper.hasContent(paletteSupplierMap.get(strRemovedPaletteName))) {
				StringTokenizer supplierNames = new StringTokenizer(paletteSupplierMap.get(strRemovedPaletteName),
						"|~*~|");
				while (supplierNames.hasMoreElements()) {
					String removedSupName = supplierNames.nextToken();
					if(FormatHelper.hasContent(removedSupName)) {
						PaletteSupplier removedPaletteSupplierBean = new PaletteSupplier();
						BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(removedPaletteSupplierBean, "suppName",
								removedSupName);
						lstRemovedPaletteSupBean.add(removedPaletteSupplierBean);
					}

				}
			}
			removedPaletteBean.setPaletteSupplier(lstRemovedPaletteSupBean);
			logger.debug(methodName + "RemovedPaletteBean: " + removedPaletteBean);
			// Add to the list
			lstRemovedPaletteBean.add(removedPaletteBean);
		}

		logger.debug(methodName + "Removed Palette Names Size: " + lstRemovedPaletteBean.size());

		// Track execution time
		long remPalEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Palette Material Transform End Time: ");
		logger.debug(methodName + "Remove Palette Material Transform  Total Execution Time (ms): "
				+ (remPalEndTime - remPalStartTime));
		return lstRemovedPaletteBean;
	}

	// CR R26: Handle Remove Palette Material Colour Customisation : End

	/**
	 * Method to get Removed Palette Material Color Bean
	 * 
	 * @param materialObject       Material
	 * @param matColor             Material Color
	 * @param associatedPaletteIds
	 * @param mapTrackedPalette
	 * @param endDate
	 * @param startDate
	 * @return PaletteInfo Bean
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws WTException
	 */
	// CR R26: Handle Remove Palette Material Colour Customisation : Start
	public static List<PaletteInfo> getRemovedListPaletteInfoBean(LCSMaterial materialObject, LCSMaterialColor matColor,
			List<String> associatedPaletteNames, Map<String, Collection<HashMap>> mapTrackedPalette)
			throws IllegalAccessException, InvocationTargetException, WTException {

		String methodName = "getRemovedPaletteInfoBean() ";
		// Track execution time
		long remPalInfoStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Palette Info Transform Start Time: ");

		// Initialisation
		List<PaletteInfo> lstRemovedPaletteInfoBean = new ArrayList<PaletteInfo>();
		List<String> removedPaletteNameList = new ArrayList<String>();
		// Check Material Object
		if (mapTrackedPalette.containsKey(String.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection
			Collection<HashMap> colMap = mapTrackedPalette.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Material Colour Id
				String strMaterialColourId = String.valueOf(hm.get("MATERIAL_COLOUR_ID"));

				// Check Material Colour Id
				if (FormatHelper.getNumericObjectIdFromObject(matColor).equalsIgnoreCase(strMaterialColourId)) {
					// Get the Palette Name
					String strRemovedPaletteName = (String) hm.get("PALETTE_NAME");
					// Add to list
					removedPaletteNameList.add(strRemovedPaletteName);
				}
			}
		}
		// Remove Duplicate Palette Names
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedPaletteNameList);
		removedPaletteNameList.clear();
		removedPaletteNameList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil.compareAndRemoveSameObjects(associatedPaletteNames,
				removedPaletteNameList);
		logger.debug(methodName + "List Removed Palette Names: " + lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedPaletteName : lstComparedAndRemovedObjects) {
			// Initialisation
			PaletteInfo removedPaletteBean = new PaletteInfo();
			BurberryPaletteMaterialAPIJsonDataUtil.getRemovedPaletteBean(strRemovedPaletteName, removedPaletteBean);
			logger.debug(methodName + "RemovedPaletteBean: " + removedPaletteBean);
			// Add to the list
			lstRemovedPaletteInfoBean.add(removedPaletteBean);
		}

		logger.debug("Removed Palette Info Bean List: " + lstRemovedPaletteInfoBean);

		// Track execution time
		long remPalInfoEnd = BurberryAPIUtil.printCurrentTime(methodName, "Remove Palette Info Transform End Time: ");
		logger.debug(methodName + "Remove Palette Info Transform  Total Execution Time (ms): "
				+ (remPalInfoEnd - remPalInfoStart));

		return lstRemovedPaletteInfoBean;
	}

	// CR R26: Handle Remove Palette Material Colour Customisation : End

	/**
	 * Method to get removed risk management moa records
	 * 
	 * @param materialObject           LCSMaterial
	 * @param mapTrackedRiskManagement
	 * @param startModifyDate          Date
	 * @param endModifyDate            Date
	 * @return List<RiskManagement>
	 * @throws WTException               Exception
	 * @throws InvocationTargetException Exception
	 * @throws IllegalAccessException    Exception
	 */
	// CR R26: Handle Remove Object Customisation : Start
	private static List<RiskManagement> getRemovedListRiskManagement(LCSMaterial materialObject,
			Map<String, Collection<HashMap>> mapTrackedRiskManagement)
			throws WTException, IllegalAccessException, InvocationTargetException {

		// Method Name
		String methodName = "getRemovedListRiskManagement() ";
		// Track execution time
		long remRiskMgmtStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Risk Management Transform Start Time: ");

		// Initialisation
		List<RiskManagement> lstRemovedRiskMgmtBean = new ArrayList<RiskManagement>();

		// Check tracked map contains material id
		if (mapTrackedRiskManagement.containsKey(String.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedRiskManagement
					.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed moa id
				String removedMOARow = (String) hm.get(STR_MOA_OBJECT_ID);
				// Initialisation
				RiskManagement removedRiskMgmtBean = new RiskManagement();
				BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(removedRiskMgmtBean,
						BurPaletteMaterialConstant.RISK_MANAGEMENT_JSON_UNIQUE_ID, removedMOARow);
				logger.debug(methodName + "Removed MOA Bean: " + removedRiskMgmtBean);
				// Add to list
				lstRemovedRiskMgmtBean.add(removedRiskMgmtBean);
			}
		}

		logger.debug(methodName + "Removed Risk Management Bean Size: " + lstRemovedRiskMgmtBean.size());

		// Track execution time
		long remRiskMgmtEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Risk Management Transform End Time: ");
		logger.debug(methodName + "Remove Risk Management Transform Total Execution Time (ms): "
				+ (remRiskMgmtEnd - remRiskMgmtStart));
		// Return
		return lstRemovedRiskMgmtBean;
	}

	// CR R26: Handle Remove Object Customisation : End

	/**
	 * Method to get removed yarn details moa records
	 * 
	 * @param materialObject        LCSMaterial
	 * @param mapTrackedYarnDetails
	 * @param startModifyDate       Date
	 * @param endModifyDate         Date
	 * @return List<YarnDetail>
	 * @throws WTException               Exception
	 * @throws InvocationTargetException Exception
	 * @throws IllegalAccessException    Exception
	 */
	// CR R26: Handle Remove Object Customisation : Start
	private static List<YarnDetail> getRemovedListYarnDetail(LCSMaterial materialObject,
			Map<String, Collection<HashMap>> mapTrackedYarnDetails)
			throws WTException, IllegalAccessException, InvocationTargetException {

		String methodName = "getRemovedListYarnDetail() ";
		// Track execution time
		long remYarnDetailStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Yarn Detail Transform Start Time: ");

		// Initialisation
		List<YarnDetail> lstRemovedYarnDetailBean = new ArrayList<YarnDetail>();

		// Check tracked map contains material id
		if (mapTrackedYarnDetails.containsKey(String.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedYarnDetails
					.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed moa id
				String removedMOARow = (String) hm.get(STR_MOA_OBJECT_ID);
				// Initialisation
				YarnDetail removedYarnDetailBean = new YarnDetail();
				BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(removedYarnDetailBean,
						BurPaletteMaterialConstant.YARN_DETAIL_JSON_UNIQUE_ID, removedMOARow);
				logger.debug(methodName + "Removed Yarn Detail MOA Bean: " + removedYarnDetailBean);
				// Add to list
				lstRemovedYarnDetailBean.add(removedYarnDetailBean);
			}
		}

		logger.debug(methodName + "Removed Yarn Detail Bean Size: " + lstRemovedYarnDetailBean.size());

		// Track execution time
		long remYarnDetailEnd = BurberryAPIUtil.printCurrentTime(methodName, "Remove Yarn Detail Transform End Time: ");
		logger.debug(methodName + "Remove Yarn Detail Transform Total Execution Time (ms): "
				+ (remYarnDetailEnd - remYarnDetailStart));

		return lstRemovedYarnDetailBean;
	}

	// CR R26: Handle Remove Object Customisation : End

	/**
	 * Method to get removed material price management moa records
	 * 
	 * @param materialObject
	 * 
	 * @param materialSupplierObject      LCSMaterialSupplier
	 * @param mapTrackedMaterialPriceMgmt
	 * @param startModifyDate             Date
	 * @param endModifyDate               Date
	 * @return List<MatPriceManagement>
	 * @throws WTException               Exception
	 * @throws InvocationTargetException Exception
	 * @throws IllegalAccessException    Exception
	 */
	// CR R26: Handle Remove Object Customisation : Start
	private static List<MatPriceManagement> getRemovedListMatPriceManagement(LCSMaterial materialObject,
			LCSMaterialSupplier materialSupplierObject, Map<String, Collection<HashMap>> mapTrackedMaterialPriceMgmt)
			throws WTException, IllegalAccessException, InvocationTargetException {

		// Method Name
		String methodName = "getRemovedListMatPriceManagement() ";
		// Track execution time
		long remMatPriceMgmtStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Mat Price Management Transform Start Time: ");

		// Initialisation
		List<MatPriceManagement> lstRemovedMatPriceMgmtBean = new ArrayList<MatPriceManagement>();

		// Check tracked map contains material id
		if (mapTrackedMaterialPriceMgmt.containsKey(String.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedMaterialPriceMgmt
					.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Material Id
				String materialSupplierId = String.valueOf(hm.get("OWNER_ID"));

				// Check Material Id
				if (String.valueOf(materialSupplierObject.getBranchIdentifier()).equalsIgnoreCase(materialSupplierId)) {
					// Get the removed moa id
					String removedMOARow = (String) hm.get(STR_MOA_OBJECT_ID);
					// Initialisation
					MatPriceManagement removedMatPriceManagementBean = new MatPriceManagement();
					BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(removedMatPriceManagementBean,
							BurPaletteMaterialConstant.MATERIAL_PRICE_MANAGEMENT_JSON_UNIQUE_ID, removedMOARow);
					logger.debug(methodName + "Removed Material Price Mgmt MOA Bean: " + removedMatPriceManagementBean);
					// Add to list
					lstRemovedMatPriceMgmtBean.add(removedMatPriceManagementBean);

				}
			}
		}

		logger.debug(methodName + "Removed Yarn Detail Bean Size: " + lstRemovedMatPriceMgmtBean.size());

		// Track execution time
		long remMatPriceMgmtEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Mat Price Management Transform End Time: ");
		logger.debug(methodName + "Remove Mat Price Management Transform Total Execution Time (ms): "
				+ (remMatPriceMgmtEnd - remMatPriceMgmtStart));

		// Return
		return lstRemovedMatPriceMgmtBean;

	}
	// CR R26: Handle Remove Object Customisation : End
}

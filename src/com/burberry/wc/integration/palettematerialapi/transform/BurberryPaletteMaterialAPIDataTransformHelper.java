package com.burberry.wc.integration.palettematerialapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.bean.*;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.color.*;
import com.lcs.wc.material.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.palettematerialapi.transform.BurburreyPaletteMaterialAPIDataTransformDocs;

/**
 * A helper class to handle Transformation activity. Class contain several
 * method to handle transformation activity i.e. Transforming Data from
 * different objects and putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIDataTransformHelper {

	/**
	 * Default Constructor.
	 */
	private BurberryPaletteMaterialAPIDataTransformHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPIDataTransformHelper.class);

	/**
	 * Method to transform Material Object into Json.
	 * 
	 * @param materialObject
	 *            LCSMaterial
	 * @param colMaterialSupplierLinks
	 *            Collection
	 * @param colMaterialColourLinks
	 *            Collection
	 * @param colPalettes
	 *            Collection
	 * @param mapTrackedPalette
	 *            Map
	 * @param mapTrackedRiskManagement
	 *            Map
	 * @param mapTrackedYarnDetails
	 *            Map
	 * @param mapTrackedMaterialPriceMgmt
	 *            Map
	 * @param mapTrackedMaterialPriceEntry
	 *            Map
	 * @param mapTrackedMaterialDocument
	 *            Map
	 * @param mapTrackedMaterialSupplierDocument
	 * @return Material Bean
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws PropertyVetoException 
	 */
	public static Material getMaterialBean(
			LCSMaterial materialObject,
			Collection<String> colMaterialSupplierLinks,
			Collection<String> colMaterialColourLinks,
			Collection<String> colPalettes,
			Map<String, Collection<HashMap>> mapTrackedPalette,
			Map<String, Collection<HashMap>> mapTrackedRiskManagement,
			Map<String, Collection<HashMap>> mapTrackedYarnDetails,
			Map<String, Collection<HashMap>> mapTrackedMaterialPriceMgmt,
			Map<String, Collection<HashMap>> mapTrackedMaterialPriceEntry,
			Map<String, Collection<HashMap>> mapTrackedMaterialDocument,
			Map<String, Collection<HashMap>> mapTrackedMaterialSupplierDocument)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException,
			PropertyVetoException {
		String methodName = "getMaterialBean() ";
		// Track execution time
		long materialStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Transform Start Time: ");

		logger.debug(methodName + "Material Name: " + materialObject.getName());

		// Get Material Bean Data using Material Object
		Material materialBean = BurberryPaletteMaterialAPIJsonDataUtil
				.getMaterialBean(materialObject);
		logger.debug(methodName + "Material Beans Before: " + materialBean);

		// Extracting Material Supplier list data using Material Object
		// CR R26: Handle Remove Palette Material Colour Customisation
		List<MaterialSupplier> lstMatSupplier = getListMaterialSupplierBean(
				materialObject, colMaterialColourLinks,
				colMaterialSupplierLinks, colPalettes, mapTrackedPalette,
				mapTrackedMaterialPriceEntry, mapTrackedMaterialPriceMgmt,
				mapTrackedMaterialSupplierDocument);
		logger.debug(methodName + "List of Material-Supplier Beans: "
				+ lstMatSupplier);
		materialBean.setMaterialSupplier(lstMatSupplier);

		// Extraction of Palette Bean Object data using Material
		// CR R26: Handle Remove Palette Material Colour Customisation
		List<Palette> lstPalette = BurberryPaletteMaterialAPIDataTransform
				.getListPaletteBean(materialObject, colPalettes,
						mapTrackedPalette);
		logger.debug(methodName + "List of Associated Palette Beans: "
				+ lstPalette);

		// Set Combined List of Palette Bean on Material Bean
		materialBean.setPalette(lstPalette);

		// Extracting Document Data using Material Object
		List<Document> lstDocument = BurburreyPaletteMaterialAPIDataTransformDocs
				.getListDocumentsBean(materialObject,
						mapTrackedMaterialDocument);
		logger.debug(methodName + "List of Document Beans: " + lstDocument);
		materialBean.setDocuments(lstDocument);

		// Extracting Yarn Detail using Material Object
		List<YarnDetail> lstYarnDetails = BurberryPaletteMaterialAPIDataTransform
				.getListYarnDetailsBean(materialObject, mapTrackedYarnDetails);
		logger.debug(methodName + "List of Yarn Details Beans: "
				+ lstYarnDetails);
		materialBean.setYarnDetails(lstYarnDetails);

		// Extracting RiskManagement using Material Object
		List<RiskManagement> lstRiskManagement = BurberryPaletteMaterialAPIDataTransform
				.getListRiskManagementBean(materialObject,
						mapTrackedRiskManagement);
		logger.debug(methodName + "List of Risk Management Beans: "
				+ lstRiskManagement);
		materialBean.setRiskManagement(lstRiskManagement);
		// CR R26: Handle Remove Object Customisation: End

		// Final Material Bean Object
		logger.debug(methodName + "Material Beans Final: " + materialBean);
		// Track execution time
		long materialEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Transform End Time: ");
		logger.debug(methodName
				+ "Material Transform  Total Execution Time (ms): "
				+ (materialEndTime - materialStartTime));
		// Return Statement
		return materialBean;

	}

	/**
	 * Method to get MaterialColour bean data.
	 * 
	 * @param materialObject
	 *            material Object
	 * @param colMaterialColourLinks
	 * @param endModifyDate
	 * @param startModifyDate
	 * @param mapTrackedPalette
	 * @param mapDeltaDateTime
	 * @return List
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
	private static List<MaterialColour> getListMaterialColourBean(
			LCSMaterial materialObject,
			LCSMaterialSupplier materialSupplierObject,
			Collection<String> colMaterialColourLinks,
			Collection<String> colPalettes,
			Map<String, Collection<HashMap>> mapTrackedPalette)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {
		String methodName = "getListMaterialColourBean() ";
		// Track execution time
		long matColStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Colour Transform Start Time: ");

		// Initialisation
		List<MaterialColour> lstMaterialColour = new ArrayList<MaterialColour>();

		// Get material colour from material object
		Collection<LCSMaterialColor> materialColors = LCSQuery
				.getObjectsFromResults(new LCSMaterialColorQuery()
						.findMaterialColorData(materialSupplierObject),
						BurPaletteMaterialConstant.LCS_MATERIAL_COLOR_PREFIX,
						BurPaletteMaterialConstant.MATERIAL_COLOR_ID);
		logger.debug(methodName + "Collection of Material-Colour: "
				+ materialColors);

		// Loop through each material colour
		for (LCSMaterialColor matColor : materialColors) {
			logger.debug(methodName + "Material Colour: " + matColor.getName());
			// checking if material colour criteria
			boolean matColourExists = BurberryAPIDBUtil.checkIfObjectExists(
					FormatHelper.getNumericObjectIdFromObject(matColor),
					colMaterialColourLinks);
			logger.debug(methodName + "Material Colour Exists: "
					+ matColourExists);
			// If material supplier exists
			if (matColourExists) {
				// Extraction of material colour Object data
				MaterialColour materialColourBean = BurberryPaletteMaterialAPIJsonDataUtil
						.getMaterialColourBean(matColor);
				logger.debug(methodName + "Material Colour Bean: "
						+ materialColourBean);

				// Get Colour Data from Material Colour
				LCSColor colour = matColor.getColor();
				logger.debug(methodName + "Colour: " + colour.getColorName());
				Colour colourBean = BurberryPaletteMaterialAPIJsonDataUtil
						.getColourBean(colour);
				logger.debug(methodName + "Colour Bean: " + colourBean);
				// Set Colour Bean to Material Colour Bean
				materialColourBean.setColour(colourBean);
				logger.debug(methodName + "material Colour Bean: "
						+ materialColourBean);

				// CR R26: Handle Remove Palette Material Colour Customisation
				// Get Palette Info Bean data using Material-Color
				List<PaletteInfo> lstPaletteInfoBean = BurberryPaletteMaterialAPIDataTransform
						.getListPaletteInfoBean(materialObject, matColor,
								colPalettes, mapTrackedPalette);
				logger.debug(methodName + "List Palette Info Bean: "
						+ lstPaletteInfoBean);

				// Set List of Palette Bean on Material Bean
				materialColourBean.setPaletteInfo(lstPaletteInfoBean);

				// Add bean to list
				lstMaterialColour.add(materialColourBean);
			}
		}
		// Track execution time
		long matColEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Colour Transform End Time: ");
		logger.debug(methodName
				+ "Material Colour Transform  Total Execution Time (ms): "
				+ (matColEndTime - matColStartTime));
		// Return Statement
		return lstMaterialColour;
	}

	/**
	 * Method to get list of material supplier bean data.
	 * 
	 * @param materialObject
	 *            LCSMaterial
	 * @param colMaterialColourLinks
	 *            Collection
	 * @param colMaterialSupplierLinks
	 *            Collection
	 * @param colPalettes
	 *            Collection
	 * @param mapTrackedPalette
	 *            Map
	 * @param mapTrackedMaterialPriceEntry
	 *            Map
	 * @param mapTrackedMaterialPriceMgmt
	 *            Map
	 * @param mapTrackedMaterialSupplierDocument
	 * @return List
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
	private static List<MaterialSupplier> getListMaterialSupplierBean(
			LCSMaterial materialObject,
			Collection<String> colMaterialColourLinks,
			Collection<String> colMaterialSupplierLinks,
			Collection<String> colPalettes,
			Map<String, Collection<HashMap>> mapTrackedPalette,
			Map<String, Collection<HashMap>> mapTrackedMaterialPriceEntry,
			Map<String, Collection<HashMap>> mapTrackedMaterialPriceMgmt,
			Map<String, Collection<HashMap>> mapTrackedMaterialSupplierDocument)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {

		String methodName = "getListMaterialSupplierBean() ";
		// Track execution time
		long matSupStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Supplier Transform Start Time: ");

		// Initialisation
		List<MaterialSupplier> lstMaterialSupplier = new ArrayList<MaterialSupplier>();

		// Get all the Material Supplier from Material Object
		Collection<LCSMaterialSupplier> materialSuppliers = LCSQuery
				.getObjectsFromResults(
						new LCSMaterialSupplierQuery()
								.findMaterialSuppliers(materialObject),
						BurPaletteMaterialConstant.LCS_MATERIAL_SUPPLIER_PREFIX,
						BurPaletteMaterialConstant.MATERIAL_SUPPLIER_BRANCHID);
		logger.debug(methodName + "Collection Material-Supplier: "
				+ materialSuppliers);

		// Loop through each Material Supplier
		for (LCSMaterialSupplier matSupplier : materialSuppliers) {
			logger.debug(methodName + "Material-Supplier: " + matSupplier);

			// Checking if material supplier criteria is given in URL
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(matSupplier.getBranchIdentifier()),
					colMaterialSupplierLinks);
			logger.debug(methodName + "Material Supplier Exists: " + idExists);
			// If Material Supplier exists
			if (idExists) {
				matSupplier = (LCSMaterialSupplier) VersionHelper
						.latestIterationOf(matSupplier);
				logger.debug(methodName + " Material Supplier: "
						+ matSupplier.getName());

				// Extraction of Material-Supplier Bean Object data using
				// Material-Supplier Flex Object
				MaterialSupplier materialSupplierBean = BurberryPaletteMaterialAPIJsonDataUtil
						.getMaterialSupplierBean(matSupplier);
				logger.debug(methodName + "Material Supplier Bean: "
						+ materialSupplierBean);

				// Get Supplier Object
				LCSSupplier supplierObject = (LCSSupplier) VersionHelper
						.latestIterationOf(matSupplier.getSupplierMaster());
				logger.debug(methodName + "Supplier: "
						+ supplierObject.getName());
				// Extraction of Supplier Bean Object data using
				// Material-Supplier Flex Object
				Supplier supplierBean = BurberryPaletteMaterialAPIJsonDataUtil
						.getSupplierBean(supplierObject);
				logger.debug(methodName + "Supplier Bean: " + supplierBean);
				// Set Supplier Bean Data on MaterialSupplierBean
				materialSupplierBean.setSupplier(supplierBean);

				// Extraction of Material-Colour Bean Object data using
				// Material-Supplier Flex Object
				List<MaterialColour> lstMaterialColour = getListMaterialColourBean(
						materialObject, matSupplier, colMaterialColourLinks,
						colPalettes, mapTrackedPalette);
				logger.debug(methodName + "List of Material-Colour Beans: "
						+ lstMaterialColour);
				// Set Material Colour Bean on MaterialSupplierBean
				materialSupplierBean.setMaterialColour(lstMaterialColour);

				// Extraction of Material Pricing Entry Bean Object data using
				// Material-Supplier Flex Object
				List<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry> lstMaterialPricingEntry = getListMaterialPricingEntryBean(
						materialObject, matSupplier,
						mapTrackedMaterialPriceEntry);
				logger.debug(methodName + "List of Material Pricing Beans: "
						+ lstMaterialPricingEntry);
				// Set List of Material Pricing Bean on MaterialSupplierBean
				materialSupplierBean
						.setMaterialPricingEntry(lstMaterialPricingEntry);

				// Extracting Material Price Management using Material Supplier
				// Object
				List<MatPriceManagement> lstMatPriceMgmt = BurberryPaletteMaterialAPIDataTransform
						.getListMatPriceManagementBean(materialObject,
								matSupplier, mapTrackedMaterialPriceMgmt);
				logger.debug(methodName
						+ "List of Material Price Management Beans: "
						+ lstMatPriceMgmt);
				materialSupplierBean.setMatPriceManagement(lstMatPriceMgmt);

				// BURBERRY-1485 RD 74: Material Supplier Documents - Start
				// Extracting Document Data using Material Object
				List<MaterialSupplierDocument> lstMaterialSupplierDocuments = BurburreyPaletteMaterialAPIDataTransformDocs
						.getListMaterialSupplierDocumentsBean(materialObject,matSupplier,
								mapTrackedMaterialSupplierDocument);
				logger.debug(methodName
						+ "List of Material Supplier Document Beans: "
						+ lstMaterialSupplierDocuments);
				materialSupplierBean
						.setMaterialSupplierDocuments(lstMaterialSupplierDocuments);
				// BURBERRY-1485 RD 74: Material Supplier Documents - End
				// Finally Set Material Supplier Bean data
				lstMaterialSupplier.add(materialSupplierBean);

			}
		}
		// Track execution time
		long matSupEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Supplier Transform End Time: ");
		logger.debug(methodName
				+ "Material Supplier Transform  Total Execution Time (ms): "
				+ (matSupEndTime - matSupStartTime));
		// Return Statement
		return lstMaterialSupplier;
	}

	/**
	 * Method to get Material Pricing Entries Data.
	 * 
	 * @param materialObject
	 * 
	 * @param matSupplier
	 *            LCSMaterialSupplier
	 * @param mapTrackedMaterialPriceEntry
	 * @param endModifyDate
	 * @param startModifyDate
	 * @return List
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 */
	private static List<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry> getListMaterialPricingEntryBean(
			LCSMaterial materialObject, LCSMaterialSupplier matSupplier,
			Map<String, Collection<HashMap>> mapTrackedMaterialPriceEntry)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getListMaterialPricingEntry() ";

		List<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry> lstMaterialPricingEntry = new ArrayList<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry>();

		// Get Material Pricing Entry Bean Data
		Collection<com.lcs.wc.material.MaterialPricingEntry> matPricingEntries = LCSQuery
				.getObjectsFromResults(
						new MaterialPricingEntryQuery()
								.findIndependentMaterialPricingEntryCollection(matSupplier),
						BurPaletteMaterialConstant.MATERIAL_PRICING_ENTRY_PREFIX,
						BurPaletteMaterialConstant.MATERIAL_PRICING_ENTRY_ID);

		logger.debug(methodName + "Collection Material-Pricing-Entries: "
				+ matPricingEntries);

		// Loop through each material supplier
		for (com.lcs.wc.material.MaterialPricingEntry matPricingEntry : matPricingEntries) {
			logger.debug(methodName + "Material Pricing Entry: "
					+ matPricingEntry.getEffectivityContext());
			// Extract supplier object data
			com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry matPricingEntryBean = BurberryPaletteMaterialAPIJsonDataUtil
					.getMaterialPricingEntryBean(matPricingEntry);
			logger.debug(methodName + "MaterialPricingEntry Bean: "
					+ matPricingEntryBean);
			lstMaterialPricingEntry.add(matPricingEntryBean);
		}

		// CR R26: Handle Remove Object Customisation: Start
		List<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry> lstRemovedMaterialPricingEntries = getRemovedListMaterialPricingEntry(
				materialObject, matSupplier, mapTrackedMaterialPriceEntry);
		logger.debug(methodName + "List of Removed Material Pricing Entry: "
				+ lstRemovedMaterialPricingEntries);

		lstMaterialPricingEntry.addAll(lstRemovedMaterialPricingEntries);
		// CR R26: Handle Remove Object Customisation: End

		// Return Statment
		return lstMaterialPricingEntry;
	}

	// CR R26: Handle Remove Object Customisation: Start

	/**
	 * Method to get Removed Material Price Entry
	 * 
	 * @param materialObject
	 *            LCSMaterial
	 * @param matSupplier
	 *            LCSMaterialSupplier
	 * @param mapTrackedMaterialPriceEntry
	 *            Map
	 * @return List
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 */
	private static List<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry> getRemovedListMaterialPricingEntry(
			LCSMaterial materialObject, LCSMaterialSupplier matSupplier,
			Map<String, Collection<HashMap>> mapTrackedMaterialPriceEntry)
			throws WTException, IllegalAccessException,
			InvocationTargetException {

		String methodName = "getRemovedListMaterialPricingEntry() ";
		// Track execution time
		long remMaterialPricingEntryStart = BurberryAPIUtil.printCurrentTime(
				methodName,
				"Remove Material Pricing Entry Transform Start Time: ");

		// Initialisation
		List<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry> lstRemovedMatPriceEntriesBean = new ArrayList<com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry>();

		// Check tracked map contains material id
		if (mapTrackedMaterialPriceEntry.containsKey(String
				.valueOf(materialObject.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedMaterialPriceEntry
					.get(String.valueOf(materialObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Material Supplier Id
				String materialSupplierId = String.valueOf(hm.get("OWNER_ID"));
				// Check Material Supplier Id
				if (String.valueOf(matSupplier.getBranchIdentifier())
						.equalsIgnoreCase(materialSupplierId)) {
					// Get the removed palette name
					String removedMOARow = (String) hm.get("MOA_OBJECT_ID");
					// Initialisation
					com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry removedMatPriceEntryBean = new com.burberry.wc.integration.palettematerialapi.bean.MaterialPricingEntry();
					BurberryPaletteMaterialAPIJsonDataUtil
							.getRemovedMOABean(
									removedMatPriceEntryBean,
									BurPaletteMaterialConstant.MATERIAL_PRICE_ENTRY_JSON_UNIQUE_ID,
									removedMOARow);
					logger.debug(methodName
							+ "Removed Material Price Entry MOA Bean: "
							+ removedMatPriceEntryBean);
					// Add to list
					lstRemovedMatPriceEntriesBean.add(removedMatPriceEntryBean);

				}
			}
		}
		// Track execution time
		long remMaterialPriceEntryEnd = BurberryAPIUtil.printCurrentTime(
				methodName,
				"Remove Material Pricing Entry Transform End Time: ");
		logger.debug(methodName
				+ "Remove Material Pricing Entry Transform Total Execution Time (ms): "
				+ (remMaterialPriceEntryEnd - remMaterialPricingEntryStart));

		return lstRemovedMatPriceEntriesBean;
	}
}

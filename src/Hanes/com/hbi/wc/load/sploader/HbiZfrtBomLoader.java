package com.hbi.wc.load.sploader;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import wt.fc.WTObject;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HbiZfrtBomLoader {
	private static final String ZFRT = "hbiZFRT";
	private static final String REQUIRED_ZFRT_COLUMNS = "DROPPED,ID,branchId,sortingNumber,dimensionName,size1,colorDimensionId,section,partName,"
			+ "hbiErpComponentStyle,hbiErpComponentPutUp,hbiErpComponentColor,hbiErpComponentSize,quantity";

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param rows
	 * @param zfrtBomName
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexBOMPart createZFRTBom(String sap_key, LCSProduct sp, String putup,
			Map<String, Collection<FlexObject>> rows, String zfrtBomName)
			throws IOException, WTException, WTPropertyVetoException {

		FlexBOMPart zfrtBom = null;

		zfrtBom = HBISPBomUtil.initiateBOM(sp, HBISPBomUtil.SALES_BOMTYPE, zfrtBomName);

		zfrtBom = updateBomData(sap_key, sp, putup, zfrtBom, rows);

		return zfrtBom;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param bom
	 * @param rows
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexBOMPart updateBomData(String sap_key, LCSProduct sp, String putup, FlexBOMPart bom,
			Map<String, Collection<FlexObject>> rows) throws WTException, WTPropertyVetoException {
		FlexBOMPart zfrtBom = bom;
		if (zfrtBom != null) {
			long start_pkgProc = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");

			// Process Pack Case BOM Data
			Collection<FlexObject> processedBomData = processBomData(sp, zfrtBom, rows);

			long end_pkgProc = System.currentTimeMillis();
			HBISPBomUtil.debug(sap_key + ", A pkgCase Bom processing time ::" + " ["
					+ formatter.format((end_pkgProc - start_pkgProc) / 1000d) + "]");

			// Covert data into a string with required delimiters
			String dataString = HBISPBomUtil.covertInToMergeString(processedBomData, REQUIRED_ZFRT_COLUMNS);

			FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
			// Get latest version
			zfrtBom = (FlexBOMPart) VersionHelper.latestIterationOf(zfrtBom);
			// checkout
			zfrtBom = (FlexBOMPart) VersionHelper.checkout(zfrtBom);

			HBISPBomUtil.debug("************* Checked out BOM");

			// get Working Coy
			zfrtBom = (FlexBOMPart) VersionHelper.getWorkingCopy(zfrtBom);

			HBISPBomUtil.debug("************* Got Working copy of BOM");

			// get ObjectId of BOm
			String bomId = FormatHelper.getObjectId(zfrtBom);

			// Load Bom
			bomPartClientModel.load(bomId);

			HBISPBomUtil.debug("************* Loaded BOM");

			// Update BOM with MergedBomLineItems Data String
			bomPartClientModel.update(dataString);
			HBISPBomUtil.debug("************* Updated BOM with Merged Data");

			// save the bom
			bomPartClientModel.save();
			HBISPBomUtil.debug("************* BOM Saved");
			bomPartClientModel.checkIn();
			zfrtBom = bomPartClientModel.getBusinessObject();
			HBISPBomUtil.debug("########## Completed Updating zfrtBom Line Items in FlexPLM  ###########");
		}
		return zfrtBom;

	}

	/**
	 * @param sp
	 * @param packCaseBom
	 * @param rows
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> processBomData(LCSProduct sp, FlexBOMPart zfrtBom,
			Map<String, Collection<FlexObject>> rows) throws WTException {
		Collection<FlexObject> processedRows = new ArrayList();
		LCSFlexBOMLogic bomLogic = new LCSFlexBOMLogic();
		int maxBranchId = bomLogic.getMaxBranchId(zfrtBom);
		int maxSortingNumber = 0;

		Iterator itr = rows.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Collection<FlexObject> zfrtRows = rows.get(key);
			if (zfrtRows != null && !zfrtRows.isEmpty()) {
				HBISPBomUtil.debug("Total processed zfrtRows found ::" + zfrtRows.size());
				zfrtRows = getProcessedRow(sp, zfrtBom, zfrtRows, maxBranchId, maxSortingNumber);
				processedRows.addAll(zfrtRows);
				maxBranchId++;
				maxSortingNumber++;

			}
		}

		return processedRows;

	}

	/**
	 * @param ipRows
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private static void validateIPRowSizes(Collection<FlexObject> ipRows) throws WTException {
		Iterator itr = ipRows.iterator();
		while (itr.hasNext()) {
			FlexObject ipRow = (FlexObject) itr.next();
			String size1Val = ipRow.getData("size1");
			if (!FormatHelper.hasContent(size1Val)) {
				throw new WTException("!!!! Found No size1 value on a IP Primary Row");
			}
		}

	}

	/**
	 * @param sp
	 * @param zfrtBom
	 * @param primRows
	 * @param maxBranchId
	 * @param maxSortingNumber
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> getProcessedRow(LCSProduct sp, FlexBOMPart zfrtBom,
			Collection<FlexObject> primRows, int maxBranchId, int maxSortingNumber) throws WTException {
		Collection<FlexObject> processedRows = new ArrayList();
		Iterator itr = primRows.iterator();
		while (itr.hasNext()) {
			FlexObject row = (FlexObject) itr.next();
			String size1Val = (String) row.getObject("size1");
			String skuMasterId = (String) row.getObject("colorDimensionId");
			if (FormatHelper.hasContent(size1Val) && FormatHelper.hasContent(skuMasterId)) {
				row.put("dimensionName", ":SKU:SIZE1");
				String dimId = getDimId(maxBranchId, zfrtBom, skuMasterId, size1Val);
				row.put("ID", dimId);
				row.put("branchId", maxBranchId);
				row.put("sortingNumber", maxSortingNumber);
				processedRows.add(row);
			} else if (!FormatHelper.hasContent(size1Val) && !FormatHelper.hasContent(skuMasterId)) {
				String id = getId(maxBranchId, zfrtBom);
				row.put("ID", id);
				row.put("branchId", maxBranchId);
				row.put("sortingNumber", maxSortingNumber);
				row.put("dimensionName", "");
				processedRows.add(row);
			} else {
				throw new WTException("Dimension details missing for SP [" + sp.getName() + "]");
			}
		}
		return processedRows;
	}

	/**
	 * @param maxBranchId
	 * @param packBom
	 * @param size1Val
	 * @return ex:
	 *         -PARENT:wt.part.WTPartMaster:113557855-REV:A-BRANCH:1-SKU:wt.part.WTPartMaster:113073249-SIZE1:XL
	 */
	private static String getDimId(int maxBranchId, FlexBOMPart zfrtBom, String colorMasterid, String size1Val) {

		String bomMasterID = FormatHelper.getNumericObjectIdFromObject((WTObject) zfrtBom.getMaster());
		String dimId = "-PARENT:com.lcs.wc.part.LCSPartMaster:" + bomMasterID + "-REV:A-BRANCH:" + maxBranchId
				+ "-SKU:com.lcs.wc.part.LCSPartMaster:" + colorMasterid + "-SIZE1:" + size1Val;
		return dimId;
	}

	/**
	 * @param bomLinks
	 * @return
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Collection<FlexObject>> groupByKey(Collection<FlexObject> bomLinks) {
		Map<String, Collection<FlexObject>> boms = new HashMap();
		Iterator loop = bomLinks.iterator();
		FlexObject bomLink = null;

		while (loop.hasNext()) {
			bomLink = (FlexObject) loop.next();
			String key = FormatHelper.format("" + bomLink.getString("KEY"));
			Collection<FlexObject> bomRows = (Collection<FlexObject>) boms.get(key);
			if (bomRows == null) {
				bomRows = new ArrayList();
			}

			bomRows.add(bomLink);
			boms.put(key, bomRows);

		}

		return boms;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param psdSize1Values
	 * @param skucolorMap
	 * @param bomRows
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	private static Collection<FlexObject> getZFRTBomData(String sap_key, LCSProduct sp, String putup,
			Collection<String> psdSize1Values, HashMap<String, String> skucolorMap, Collection<Row> bomRows)
			throws WTException, IOException {

		Collection<FlexObject> zfrtRows = new ArrayList<FlexObject>();

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = bomRows.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			FlexObject link = null;
			// For ZFRT
			if (sp.getValue("hbiErpMaterialType").equals(ZFRT)) {
				link = convertInToZFRTBomFlexObjects(sap_key, sp, psdSize1Values, skucolorMap, row);
			} else {
				
				throw new WTException("sap_key :: " + sap_key + " is not a ZFRT Product in FlexPLM ");
			}
			if (link != null) {
				zfrtRows.add(link);
			}

		}
		return zfrtRows;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param psdSize1Values
	 * @param skucolorMap
	 * @param row
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	private static FlexObject convertInToZFRTBomFlexObjects(String sap_key, LCSProduct sp,
			Collection<String> psdSize1Values, HashMap<String, String> skucolorMap, Row row)
			throws WTException, IOException {

		FlexObject linkObj = new FlexObject();

		// COMMENTS (B)
		String comments = HBISPBomUtil.getCellValue(row, 1);
		linkObj.put("partName", comments);

		// PARENT_PUTUP (C)
		String pARENT_PUTUP = HBISPBomUtil.getCellValue(row, 2);
		linkObj.put("parentPutup", pARENT_PUTUP);

		// PARENT_COLOR (D)
		String pARENT_COLOR = HBISPBomUtil.getCellValue(row, 3);
		if (skucolorMap.containsKey(pARENT_COLOR)) {
			String colorMasterId = skucolorMap.get(pARENT_COLOR);
			linkObj.put("colorDimensionId", colorMasterId);
		} else {
			throw new WTException(
					" No colorway found with colorcode [" + pARENT_COLOR + "], on Product [" + sp.getName() + "]");
		}
		// PARENT_SIZE (E)
		String pARENT_SIZE = HBISPBomUtil.getCellValue(row, 4);
		if (psdSize1Values.contains(pARENT_SIZE)) {
			linkObj.put("size1", pARENT_SIZE);
		} else {
			throw new WTException(" No SIZE1 found with [" + pARENT_SIZE + "], on Product [" + sp.getName() + "]");
		}

		// COMPONENT_STYLE (F) (Respective product in pLM has to be fetched and
		// loaded
		String compStyle_SapKey = HBISPBomUtil.getCellValue(row, 5);
		LCSProduct compStyle = HBISPBomUtil.getProductFromSapKey("Z_COMPONENT_STYLE", compStyle_SapKey, row);
		long compBranchId = 0;
		if (compStyle != null) {
			compBranchId = compStyle.getBranchIdentifier();
			linkObj.put("hbiErpComponentStyle", compBranchId);

		} else {
			throw new WTException(
					"COMPONENT_STYLE :: [" + HBISPBomUtil.getCellValue(row, 5) + "], is not a product in FlexPLM");

		}

		// COMPONENT_PUTUP (G)
		String cOMPONENT_PUTUP = HBISPBomUtil.getCellValue(row, 6);
		linkObj.put("hbiErpComponentPutUp", HBISPBomUtil.getCellValue(row, 6));

		// COMPONENT_COLOR (H)
		linkObj.put("hbiErpComponentColor", HBISPBomUtil.getCellValue(row, 7));

		// PLM_SIZE_LITERAL (I)
		String plmSize = HBISPBomUtil.getCellValue(row, 8);

		if (FormatHelper.hasContent(plmSize)) {
			linkObj.put("hbiErpComponentSize", plmSize);
		} else {
			throw new WTException(
					"Not found PLM size literal for [COMPONENT_STYLE :: " + compStyle_SapKey + "],   [aps_size :: ]");
		}
		// USAGE (J)
		String uSAGE = HBISPBomUtil.getCellValue(row, 9);
		linkObj.put("quantity", uSAGE);

		linkObj.put("section", "components");
		linkObj.put("DROPPED", "false");

		// Below key is used to differentiate rows in ZFRT BOM
		String key = compBranchId + cOMPONENT_PUTUP + uSAGE;
		linkObj.put("KEY", key);
		return linkObj;

	}

	/**
	 * @param secRow
	 * @param maxBranchId
	 * @param mergedBom
	 * @return
	 */
	public static String getId(int maxBranchId, FlexBOMPart packBom) {

		String bomMasterID = FormatHelper.getNumericObjectIdFromObject((WTObject) packBom.getMaster());
		String topRowID = "-PARENT:com.lcs.wc.part.LCSPartMaster:" + bomMasterID + "-REV:A-BRANCH:" + maxBranchId;
		return topRowID;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param refSpec
	 * @param zfrtBomName
	 * @param bomRows
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static FlexBOMPart loadZFRTBom(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			FlexSpecification refSpec, String zfrtBomName, Collection<Row> bomRows)
			throws IOException, WTException, WTPropertyVetoException {
		HBISPBomUtil.debug("########## Started BOM Loading zfrtBomName :: [" + zfrtBomName + "] #######");
		FlexBOMPart zfrtBom = null;
		Transaction tr = null;
		try {
			Map<String, Collection<FlexObject>> uomRows = getZFRTBomRows(sap_key, sp, putup, bomRows);

			if (!uomRows.isEmpty()) {

				tr = new Transaction();
				tr.start();

				zfrtBom = createZFRTBom(sap_key, sp, putup, uomRows, zfrtBomName);

				HBISPBomUtil.createSpecBomLink(sp, zfrtBom, season, refSpec);

				tr.commit();
				tr = null;
			} else {
				HBISPBomUtil.debug("No zfrtBom Bom data found in extract for [ sap_key :: " + sap_key + "],"
						+ "[ putup :: " + putup + "]");
			}
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! zfrtBom Bom :: [" + zfrtBomName
						+ "] Create/update transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}
		}

		HBISPBomUtil.debug("########## Completed BOM Loading for [ sap_key :: " + sap_key + "]," + "[ putup :: " + putup
				+ "], SP :: [" + sp.getName() + "], PackCaseBom :: [" + zfrtBomName + "] #######");
		return zfrtBom;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param bomRows
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Collection<FlexObject>> getZFRTBomRows(String sap_key, LCSProduct sp, String putup,
			Collection<Row> bomRows) throws WTException, IOException {
		@SuppressWarnings("rawtypes")
		Map<String, Collection<FlexObject>> rows = new HashMap();
		// Get respective BOMs for the SAP_Key that have to be loaded.
		Collection<String> psdSize1Values = HBISPBomUtil.getPSDSize1Values(sp);
		HBISPBomUtil.debug("psdSize1Values :: " + psdSize1Values);

		// Get all skus for the Produt
		Collection<LCSSKU> allSKUs = new ProductHeaderQuery().findSKUs(sp, null, null, true, false);
		HBISPBomUtil.debug(":::::::::::::::allSKUs::::::::::::" + allSKUs.size());
		// Get sku -color code map
		HashMap<String, String> skucolorMap = getSKUColorCodeMap(allSKUs);
		// Check if product having a PSD

		if (psdSize1Values.size() >= 1 && !allSKUs.isEmpty()) {

			Collection<FlexObject> zfrtBomData = getZFRTBomData(sap_key, sp, putup, psdSize1Values, skucolorMap,
					bomRows);

			HBISPBomUtil.debug("Total zfrtBomData fetched" + zfrtBomData.size());
			Map<String, Collection<FlexObject>> groupedRows = groupByKey(zfrtBomData);
			HBISPBomUtil.debug("Total SAP UOM groups fecthed" + groupedRows.keySet().toString());

			rows = getValidRows(sap_key, putup, sp, groupedRows, psdSize1Values);
			HBISPBomUtil.debug("Total processed Valid SAP UOM groups fecthed" + rows.size());

		} else {
			throw new WTException("!!!! No PSD or colorways found in FlexPLM for [sap_key :: " + sap_key + "], [SP :: "
					+ sp.getName() + "]");
		}
		return rows;
	}

	/**
	 * @param sap_key
	 * @param putup
	 * @param sp
	 * @param validPrimRows
	 * @param psdSize1Values
	 * @return
	 * @throws WTException
	 *
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<String, Collection<FlexObject>> getValidRows(String sap_key, String putup, LCSProduct sp,
			Map<String, Collection<FlexObject>> validPrimRows, Collection<String> psdSize1Values) throws WTException {
		Map<String, Collection<FlexObject>> validRows = new HashMap();

		Iterator itr = validPrimRows.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Collection<FlexObject> varRows = validPrimRows.get(key);
			FlexObject topRow = new FlexObject();
			Collection<FlexObject> allRows = new ArrayList();
			allRows.addAll(varRows);
			FlexObject varRow = varRows.iterator().next();

			//topRow.put("partName", varRow.get("partName"));
			topRow.put("hbiErpComponentStyle", varRow.get("hbiErpComponentStyle"));
			topRow.put("hbiErpComponentPutUp", varRow.get("hbiErpComponentPutUp"));
			topRow.put("quantity", varRow.get("quantity"));
			topRow.put("size1", "");
			topRow.put("colorDimensionId", "");
			topRow.put("section", "components");
			topRow.put("DROPPED", "false");

			allRows.add(topRow);
			validRows.put(key, allRows);

		}
		return validRows;
	}

	/**
	 * @param allSKUs
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashMap<String, String> getSKUColorCodeMap(Collection allSKUs) throws WTException {
		HashMap<String, String> map = new HashMap();
		Iterator itr = allSKUs.iterator();
		while (itr.hasNext()) {
			LCSSKU sku = (LCSSKU) itr.next();
			LCSColor color = (LCSColor) sku.getValue("color");
			if (color != null && FormatHelper.hasContent((String) color.getValue("hbiColorwayCodeNew"))) {
				map.put((String) color.getValue("hbiColorwayCodeNew"),
						FormatHelper.getNumericObjectIdFromObject((WTObject) sku.getMaster()));
			} else {
				HBISPBomUtil.debug("Found the colorway on a product which not having APSColorCode");
			}
		}
		HBISPBomUtil.debug("Found the following colorways on a product With colorcodes "+ map.keySet().toString());
		return map;
	}
}

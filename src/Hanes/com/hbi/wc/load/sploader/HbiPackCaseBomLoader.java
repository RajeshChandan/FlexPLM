package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import wt.fc.WTObject;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HbiPackCaseBomLoader {
	private static final String ZPPK = "hbiZPPK";
	private static final String REQUIRED_PACKCASEBOM_COLUMNS = "DROPPED,ID,branchId,sortingNumber,dimensionName,size1,section,partName,hbiPrimarySecondary,"
			+ "hbiPackagingWeightLbs,hbiFPLength,hbiFPWidth,"
			+ "hbiFPHeight,materialDescription,childId,hbiPksCases,hbiPkgsOrInner";
	private static final String SP_PACKCASE_BOM = HBISPBomUtil.SP_PACKCASE_BOM_FILENAME;
	private static final String VALID_PLM_PRIM_KEYS[] = { "hbiEA", "hbiCV1", "hbiCV2", "hbiCV3", "hbiCV4", "hbiCV5",
			"hbiCV6", "hbiCV7", "hbiCV8", "hbiCV9", "hbiIP1", "hbiIP2", "hbiIP3", "hbiIP4", "hbiIP5" };
	private static final String VALID_PLM_CV_PRIM[] = { "hbiCV1", "hbiCV2", "hbiCV3", "hbiCV4", "hbiCV5", "hbiCV6",
			"hbiCV7", "hbiCV8", "hbiCV9" };
	private static final String VALID_PLM_IP_PRIM[] = { "hbiIP1", "hbiIP2", "hbiIP3", "hbiIP4", "hbiIP5" };
	private static final String VALID_SAP_CV_UOMS[] = { "CV1", "CV2", "CV3", "CV4", "CV5", "CV6", "CV7", "CV8", "CV9" };
	private static final String VALID_SAP_UOMS[] = { "EA", "CV", "IP" };
	private static final List<String> VALID_PLM_CV_LIST = Arrays.asList(VALID_PLM_CV_PRIM);
	private static final List<String> VALID_PLM_IP_LIST = Arrays.asList(VALID_PLM_IP_PRIM);
	private static final List<String> VALID_SAP_UOM_LIST = Arrays.asList(VALID_SAP_UOMS);
	private static final List<String> VALID_SAP_CV_UOMS_LIST = Arrays.asList(VALID_SAP_CV_UOMS);
	private static final List<String> VALID_PLM_PRIM_KEYS_LIST = Arrays.asList(VALID_PLM_PRIM_KEYS);
	private static final String SP_SIZES = HBISPBomUtil.SP_SIZES;

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param pkgCaseName
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexBOMPart createPackCaseBom(String sap_key, LCSProduct sp, String putup,
			Map<String, Collection<FlexObject>> rows, String pkgCaseName)
			throws IOException, WTException, WTPropertyVetoException {

		FlexBOMPart packCaseBom = null;
		packCaseBom = HBISPBomUtil.initiateBOM(sp, HBISPBomUtil.PACKCASE_BOMTYPE, pkgCaseName);

		packCaseBom = updateBomData(sap_key, sp, putup, packCaseBom, rows);

		return packCaseBom;
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
		FlexBOMPart packCaseBom = bom;
		if (packCaseBom != null) {
			long start_pkgProc = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
				
			// Process Pack Case BOM Data
			Collection<FlexObject> processedBomData = processBomData(sp, packCaseBom, rows);
			
			long end_pkgProc = System.currentTimeMillis();
			HBISPBomUtil.debug(sap_key +", A pkgCase Bom processing time ::"
					+ " ["+formatter.format((end_pkgProc - start_pkgProc) / 1000d)+"]");
			
			// Covert data into a string with required delimiters
			String dataString = HBISPBomUtil.covertInToMergeString(processedBomData, REQUIRED_PACKCASEBOM_COLUMNS);

			FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
			// Get latest version
			packCaseBom = (FlexBOMPart) VersionHelper.latestIterationOf(packCaseBom);
			// checkout
			packCaseBom = (FlexBOMPart) VersionHelper.checkout(packCaseBom);

			HBISPBomUtil.debug("************* Checked out BOM");

			// get Working Copy
			packCaseBom = (FlexBOMPart) VersionHelper.getWorkingCopy(packCaseBom);

			HBISPBomUtil.debug("************* Got Working copy of BOM");

			// get ObjectId of BOm
			String bomId = FormatHelper.getObjectId(packCaseBom);

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
			packCaseBom = bomPartClientModel.getBusinessObject();
			HBISPBomUtil.debug("########## Completed Updating PackCaseBom Line Items in FlexPLM  ###########");
		}
		return packCaseBom;

	}

	/**
	 * @param sp
	 * @param packCaseBom
	 * @param rows
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> processBomData(LCSProduct sp, FlexBOMPart packCaseBom,
			Map<String, Collection<FlexObject>> rows) throws WTException {
		Collection<FlexObject> processedRows = new ArrayList();
		LCSFlexBOMLogic bomLogic = new LCSFlexBOMLogic();
		int maxBranchId = bomLogic.getMaxBranchId(packCaseBom);
		int maxSortingNumber = 0;

		Collection<FlexObject> eaRows = rows.get("EA");
		if (eaRows != null && !eaRows.isEmpty()) {
			HBISPBomUtil.debug("Total processed eaRows found ::" + eaRows.size());
			eaRows = getProcessedRow(sp, packCaseBom, eaRows, maxBranchId, maxSortingNumber);
			processedRows.addAll(eaRows);
			maxBranchId++;
			maxSortingNumber++;

		}

		// hbiCV1,hbiCV2,hbiCv3,hbiCv4,hbiCV5,hbiCv6,hbiCV7,hbiCV8,hbiCV9
		Collection<FlexObject> cvRows = rows.get("CV");
		if (cvRows != null && !cvRows.isEmpty()) {
			HBISPBomUtil.debug("Total processed cvRows found ::" + cvRows.size());

			cvRows = getProcessedRow(sp, packCaseBom, cvRows, maxBranchId, maxSortingNumber);
			processedRows.addAll(cvRows);
			maxBranchId++;
			maxSortingNumber++;

		}

		// hbiIP1,hbiIP2,hbiIP3,hbiIP4,hbiIP5
		Collection<FlexObject> ipRows = rows.get("IP");
		if (ipRows != null && !ipRows.isEmpty()) {
			HBISPBomUtil.debug("Total processed ipRows found ::" + ipRows.size());
			ipRows = getProcessedRow(sp, packCaseBom, ipRows, maxBranchId, maxSortingNumber);
			processedRows.addAll(ipRows);
			maxBranchId++;
			maxSortingNumber++;

		}

		return processedRows;

	}

	/**
	 * @param groupedRows
	 * @return
	 * @throws WTException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Collection<FlexObject>> groupByValidPrimKeys(
			Map<String, Collection<FlexObject>> groupedRows) throws WTException {

		Map<String, Collection<FlexObject>> boms = new HashMap();
		Collection<FlexObject> eaRows = new ArrayList();
		Collection<FlexObject> cvRows = new ArrayList();
		Collection<FlexObject> ipRows = new ArrayList();
		{	// START EA ROWS
			if (groupedRows.containsKey("hbiEA")) {
				eaRows = groupedRows.get("hbiEA");
				boms.put("EA", eaRows);
			}
			// START EA ROWS
		}

		{// START CV ROWS
			for (int i = 0; i <= VALID_PLM_CV_PRIM.length; i++) {
				if (groupedRows.containsKey("hbiCV" + i)) {
					cvRows.addAll(groupedRows.get("hbiCV" + i));
				}
			}
			if (!cvRows.isEmpty()) {
				boms.put("CV", cvRows);
			}
			// END CV ROWS
		}

		{ // Start IP ROWS
			for (int i = 0; i <= VALID_PLM_IP_PRIM.length; i++) {
				if (groupedRows.containsKey("hbiIP" + i)) {
					ipRows.addAll(groupedRows.get("hbiIP" + i));

				}
			}
			// If there is only one IP1 UOM row and It's innerPkgVal <=1, then
			// no need to load
			if (!ipRows.isEmpty()) {
				HBISPBomUtil.debug("Top IP UOM Rows fetched :: " + ipRows.size());
				boms.put("IP", ipRows);
               // commented this logic as per user story 88121
				/*if (ipRows.size() == 1) {
					FlexObject ipRow = ipRows.iterator().next();
					String innerPkgVal = ipRow.getData("hbiPkgsOrInner");
					HBISPBomUtil.debug("IP UOM innerPkgVal value is [" + innerPkgVal + "]");
					if (FormatHelper.hasContent(innerPkgVal) && !innerPkgVal.equals("0") && !innerPkgVal.equals("1")) {
						boms.put("IP", ipRows);
					}
				} else{
					boms.put("IP", ipRows);
				}*/

			}
			// END IP ROWS
		}

		return boms;

	}

	/**
	 * @param ipRows
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private static void validateIPRowSizes(Collection<FlexObject> ipRows) throws WTException {
		Iterator itr = ipRows.iterator();
		while(itr.hasNext()){
			FlexObject ipRow = (FlexObject) itr.next();
			String size1Val = ipRow.getData("size1");
			if(!FormatHelper.hasContent(size1Val)){
				throw new WTException("!!!! Found No size1 value on a IP Primary Row");
			}
		}
		
	}

	/**
	 * @param row
	 * @return
	 */
	private static String getCVKey(FlexObject row) {
		String key = row.getData("hbiPrimarySecondary") + row.getData("hbiPksCases")
				+ row.getData("hbiFPLength") + row.getData("hbiFPHeight")
				+ row.getData("hbiFPWidth") + row.getData("materialDescription");
		return key;
	}

	/**
	 * @param row
	 * @return
	 */
	private static String getIPKey(FlexObject row) {
		String key = row.getData("hbiPrimarySecondary") + row.getData("hbiPkgsOrInner")
				+ row.getData("hbiFPLength") + row.getData("hbiFPHeight")
				+ row.getData("hbiFPWidth") + row.getData("materialDescription");
		return key;
	}

	/**
	 * @param row
	 * @return
	 */
	private static String getEAKey(FlexObject row) {
		String key = row.getData("hbiPrimarySecondary") + row.getData("hbiPackagingWeightLbs")
				+ row.getData("hbiFPLength") + row.getData("hbiFPWidth")
				+ row.getData("hbiFPHeight");
		return key;
	}

	/**
	 * @param sp
	 * @param packCaseBom
	 * @param primRows
	 * @param maxBranchId
	 * @param maxSortingNumber
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> getProcessedRow(LCSProduct sp, FlexBOMPart packCaseBom,
			Collection<FlexObject> primRows, int maxBranchId, int maxSortingNumber) {
		Collection<FlexObject> processedRows = new ArrayList();
		Iterator itr = primRows.iterator();
		while (itr.hasNext()) {
			FlexObject row = (FlexObject) itr.next();
			String size1Val = (String) row.getObject("size1");
			if (FormatHelper.hasContent(size1Val)) {
				row.put("dimensionName", ":SIZE1");
				String dimId = getDimId(maxBranchId, packCaseBom, size1Val);
				row.put("ID", dimId);
				row.put("branchId", maxBranchId);
				row.put("sortingNumber", maxSortingNumber);
				processedRows.add(row);
			} else {
				String id = getId(maxBranchId, packCaseBom);
				row.put("ID", id);
				row.put("branchId", maxBranchId);
				row.put("sortingNumber", maxSortingNumber);
				row.put("dimensionName", "");
				processedRows.add(row);
			}
		}
		return processedRows;
	}

	/**
	 * @param maxBranchId
	 * @param packBom
	 * @param size1Val
	 * @return
	 */
	private static String getDimId(int maxBranchId, FlexBOMPart packBom, String size1Val) {

		String bomMasterID = FormatHelper.getNumericObjectIdFromObject((WTObject) packBom.getMaster());
		String dimId = "-PARENT:com.lcs.wc.part.LCSPartMaster:" + bomMasterID + "-REV:A-BRANCH:" + maxBranchId + "-SIZE1:"
				+ size1Val;
		return dimId;
	}

	/**
	 * @param bomLinks
	 * @return
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Collection<FlexObject>> groupByPrimKey(Collection<FlexObject> bomLinks) {
		Map<String, Collection<FlexObject>> boms = new HashMap();
		Iterator loop = bomLinks.iterator();
		FlexObject bomLink = null;

		while (loop.hasNext()) {
			bomLink = (FlexObject) loop.next();
			String primKey = FormatHelper.format("" + bomLink.getString("hbiPrimarySecondary"));
			Collection<FlexObject> bomRows = (Collection<FlexObject>) boms.get(primKey);
			if (bomRows == null) {
				bomRows = new ArrayList();
			}

			bomRows.add(bomLink);
			boms.put(primKey, bomRows);

		}

		return boms;
	}

	/**
	 * @param sp
	 * @param putup
	 * @param psdSize1Values
	 * @param bomRows 
	 * @param prod
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> getPackCaseBomData(String sap_key, LCSProduct sp, String putup,
			Collection<String> psdSize1Values, Collection<Row> bomRows) throws IOException, WTException {
		long start_getpkgData = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.00000");
		
		Collection<FlexObject> pcBomRows = new ArrayList();
		String erp_Material_Type = (String) sp.getValue("hbiErpMaterialType");
		HBISPBomUtil.debug("In PLM ERP_Material_Type key :: " + erp_Material_Type);
		
		if(bomRows == null){
		// get Valid UOM Bom rows
			pcBomRows = getPkgCaseBomData(sap_key, sp, putup, psdSize1Values, SP_PACKCASE_BOM,
				erp_Material_Type);
			long end_getpkgData = System.currentTimeMillis();
			HBISPBomUtil.debug(sap_key +", A pkgCase Bom Reading From Excels sheets time ::"
					+ " ["+formatter.format((end_getpkgData - start_getpkgData) / 1000d)+"]");
		}else{
			// get Valid UOM Bom rows
			pcBomRows = getPkgCaseBomData(sap_key, sp, putup, psdSize1Values, SP_PACKCASE_BOM,
					erp_Material_Type,bomRows);
			long end_getpkgData = System.currentTimeMillis();
			HBISPBomUtil.debug("sap_key :: "+ sap_key + ",putup :: "+putup+" of a pkgCase Bom processing time::" + " ["
					+ formatter.format((end_getpkgData - start_getpkgData) / 1000d) + "]");
		}
		
		
		
		return pcBomRows;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param psdSize1Values
	 * @param spPackcaseBom
	 * @param erp_Material_Type
	 * @param bomRows
	 * @return
	 * @throws IOException 
	 * @throws WTException 
	 */
	private static Collection<FlexObject> getPkgCaseBomData(String sap_key, LCSProduct sp, String putup,
			Collection<String> psdSize1Values, String spPackcaseBom, String erp_Material_Type,
			Collection<Row> bomRows) throws WTException, IOException {

		Collection<FlexObject> uomRows = new ArrayList<FlexObject>();

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = bomRows.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			FlexObject link = null;
			// For ZPPK only CV UOM rows required
			if (!erp_Material_Type.equals(ZPPK)) {
				link = convertInToPkgCaseBomFlexObjects(sap_key, sp, psdSize1Values, row);
			} else if (VALID_SAP_CV_UOMS_LIST.contains(HBISPBomUtil.getCellValue(row, 2))) {
				link = convertInToPkgCaseBomFlexObjects(sap_key, sp, psdSize1Values, row);
			}
			if (link != null) {
				uomRows.add(link);
			}

		}
		return uomRows;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param psdSize1Values
	 * @param sp_UNITS_OF_MEASURE_EA
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private static Collection<FlexObject> getUOM_EAData(String sap_key, LCSProduct sp, String putup,
			Collection<String> psdSize1Values, String sp_UNITS_OF_MEASURE_EA) throws IOException, WTException {

		Collection<FlexObject> uomEaRows = new ArrayList();
		for (int i = 1; i <= HBISPBomUtil.SP_UOM_EA_FILES_SPLIT_COUNT; i++) {

			if (uomEaRows.isEmpty()) {
				FileInputStream file = new FileInputStream(new File(sp_UNITS_OF_MEASURE_EA + "_" + i + ".xlsx"));

				// Create Workbook instance holding reference to .xlsx file
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				// Get first/desired sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);
				// Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();
				boolean flag = false;
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					String cell0 = HBISPBomUtil.getCellValue(row, 0);
					String cell1 = HBISPBomUtil.getCellValue(row, 1);
					if (cell0.equals(sap_key) && cell1.equals(putup)) {
						FlexObject link = convertInToUOM_EADataFlexObjects(sap_key, sp, psdSize1Values, row);
						if (link != null) {
							uomEaRows.add(link);
						}
						flag = true;
					} else if(flag){
						break;
					}
				}
				file.close();
			} else {
				break;
			}

		}
		return uomEaRows;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param psdSize1Values
	 * @param row
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	private static FlexObject convertInToUOM_EADataFlexObjects(String sap_key, LCSProduct sp,
			Collection<String> psdSize1Values, Row row) throws WTException, IOException {

		FlexObject linkObj = new FlexObject();
		// MEINH (D)
		String primKey = getPrimKey(HBISPBomUtil.getCellValue(row, 2));
		linkObj.put("hbiPrimarySecondary", primKey);
		if (isValidPrim(sap_key, primKey)) {
			// UMREZ (E)
			String umrez = HBISPBomUtil.getCellValue(row, 3);
			if (umrez.equals("1.0")) {
				linkObj.put("hbiPkgsOrInner", "1");
			}
			// J_3ASIZE (R)
			String j_3ASIZE = HBISPBomUtil.getCellValue(row, 4);
			// String plmSize1 = getPlmSize1Value(sap_key, sp, j_3ASIZE,
			// psdSize1Values);
			String plmSize1 = getPlmSizeLiteral(sp, sap_key, j_3ASIZE, psdSize1Values);
			if (FormatHelper.hasContent(plmSize1)) {
				linkObj.put("size1", plmSize1);
			} else {
				throw new WTException("In UOM_EA extract, not found PLM size literal for [sap_key :: " + sap_key
						+ "],   [j_3ASIZE :: " + j_3ASIZE + "]");
			}

			// NTGEW (U)
			linkObj.put("hbiPackagingWeightLbs", HBISPBomUtil.getCellValue(row, 5));
			// ZLAENG (X)
			linkObj.put("hbiFPLength", HBISPBomUtil.getCellValue(row, 6));
			// ZBREIT (Y)
			linkObj.put("hbiFPWidth", HBISPBomUtil.getCellValue(row, 7));
			// ZHOEHE (Z)
			linkObj.put("hbiFPHeight", HBISPBomUtil.getCellValue(row, 8));

			linkObj.put("section", "casing");
			linkObj.put("DROPPED", "false");

		} else {
			linkObj = null;
		}
		return linkObj;

	}

	/**
	 * @param sp
	 * @param sap_key
	 * @param j_3asize
	 * @param psdSize1Values
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	private static String getPlmSizeLiteral(LCSProduct sp, String sap_key, String j_3asize,
			Collection<String> psdSize1Values) throws IOException, WTException {
		String plm_Size_Literal = "";
		FileInputStream file = new FileInputStream(new File(HBISPBomUtil.SP_SIZES));
		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			if (HBISPBomUtil.getCellValue(row, 0).equals(sap_key)
					&& HBISPBomUtil.getCellValue(row, 1).equals(j_3asize)) {
				plm_Size_Literal = HBISPBomUtil.getCellValue(row, 3);
				if (FormatHelper.hasContent(plm_Size_Literal) && psdSize1Values.contains(plm_Size_Literal)) {
					file.close();
					return plm_Size_Literal;
				} else {
					file.close();
					throw new WTException("SAP_KEY :: ["+sap_key+" ], J_3ASIZE :: [" + j_3asize + "], [ PLM Size Literal :: " + plm_Size_Literal + "],"
							+ " not existing on the PSD of Product [" + sp.getName() + "]");

				}
			}
		}
		file.close();
		return plm_Size_Literal;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param psdSize1Values
	 * @param sp_UNITS_OF_MEASURE
	 * @param erp_Material_Type
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	private static Collection<FlexObject> getPkgCaseBomData(String sap_key, LCSProduct sp, String putup,
			Collection<String> psdSize1Values, String sp_PACKCASE_BOM_IN, String erp_Material_Type)
			throws IOException, WTException {
		Collection<FlexObject> uomRows = new ArrayList<FlexObject>();
		for (int i = 1; i <= HBISPBomUtil.SP_PKGCase_FILES_SPLIT_COUNT; i++) {
			if (uomRows.isEmpty()) {
				FileInputStream file = new FileInputStream(new File(sp_PACKCASE_BOM_IN + "_" + i + ".xlsx"));

				// Create Workbook instance holding reference to .xlsx file
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				// Get first/desired sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);
				// Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();
				boolean flag = false;
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					if (HBISPBomUtil.getCellValue(row, 0).equals(sap_key)
							&& HBISPBomUtil.getCellValue(row, 1).equals(putup)) {
						FlexObject link = null;
						
						// For ZPPK only CV UOM rows required
						if (!erp_Material_Type.equals(ZPPK)) {
							link = convertInToPkgCaseBomFlexObjects(sap_key, sp, psdSize1Values, row);
						} else if (VALID_SAP_CV_UOMS_LIST.contains(HBISPBomUtil.getCellValue(row, 2))) {
							link = convertInToPkgCaseBomFlexObjects(sap_key, sp, psdSize1Values, row);
						}
						if (link != null) {
							uomRows.add(link);
						}
						flag = true;
					} else if(flag){
						
						break;
					}
				}
				file.close();
			} else {
				break;
			}
		}

		return uomRows;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param psdSize1Values
	 * @param row
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	private static FlexObject convertInToPkgCaseBomFlexObjects(String sap_key, LCSProduct sp,
			Collection<String> psdSize1Values, Row row) throws WTException, IOException {

		FlexObject linkObj = new FlexObject();

		// MEINH (C)
		String primKey = getPrimKey(HBISPBomUtil.getCellValue(row, 2));
		linkObj.put("hbiPrimarySecondary", primKey);
		if (isValidPrim(sap_key, primKey)) {
			// UMREZ (D)
			String umrez = HBISPBomUtil.getCellValue(row, 3);
			// For EA UOM rows hbiPkgsOrInner is always 1 in FlexPLM
			if ("hbiEA".equals(primKey)) {
				linkObj.put("hbiPkgsOrInner", "1");
			} else if (umrez.endsWith(".0")) {
				umrez = umrez.substring(0, umrez.length() - 2);
			}
			// For UOM CV's umrez should be set on hbiPksCases
			if (primKey.startsWith("hbiCV")) {

				linkObj.put("hbiPksCases", umrez);
				// For UOM IP's umrez should be set on hbiPkgsOrInner

			} else if (primKey.startsWith("hbiIP")) {
				linkObj.put("hbiPkgsOrInner", umrez);
			}

			// J_3ASIZE (E)
			String j_3ASIZE = HBISPBomUtil.getCellValue(row, 4);

			// From the SAP Extract, only UOM EA Rows having NTGEW rows will be
			// loaded into PLM.
			if (primKey.equals("hbiEA")) {
				// NTGEW (F)
				linkObj.put("hbiPackagingWeightLbs", HBISPBomUtil.getCellValue(row, 5));
			}
			// For only UOM IP's and EA dimensions required to load from extract to PLM
			if (primKey.startsWith("hbiIP") || primKey.startsWith("hbiEA") ) {
				// ZLAENG (G)
				linkObj.put("hbiFPLength", HBISPBomUtil.getCellValue(row, 6));
				// ZBREIT (H)
				linkObj.put("hbiFPWidth", HBISPBomUtil.getCellValue(row, 7));
				// ZHOEHE (I)
				linkObj.put("hbiFPHeight", HBISPBomUtil.getCellValue(row, 8));
			}
			// For UOM CV's there won't be dimensions required to load from
			// extract to PLM
			else if (primKey.startsWith("hbiCV")) {
				linkObj.put("hbiFPLength", "");
				linkObj.put("hbiFPHeight", "");
				linkObj.put("hbiFPWidth", "");
			}
			// CARTON_ID (Material not exist on EA Rows)
			if (!primKey.equals("hbiEA")) {
				// CARTON_ID (J)
				String carton_Id = HBISPBomUtil.getCellValue(row, 9);
				linkObj.put("materialDescription", carton_Id);

				//Commented below line to improve performance, it is moved while creating data string
				//String childId = HBISPBomUtil.getChildId(carton_Id);
				
				linkObj.put("childId", "");
			}

			// PLM_SIZE_LITERAL (K)
			// String plmSize1 = getPlmSize1Value(sap_key, sp,
			// j_3ASIZE,psdSize1Values);
			// String plmSize1 = getPlmSizeLiteral(sp, sap_key, j_3ASIZE,
			// psdSize1Values);
			String plmSize1 = HBISPBomUtil.getCellValue(row, 10);
			
			if (FormatHelper.hasContent(j_3ASIZE)) {
			linkObj.put("size1", plmSize1);
			}else{
				linkObj.put("size1", j_3ASIZE);
			}
			/*if (FormatHelper.hasContent(plmSize1)) {
				linkObj.put("size1", plmSize1);
				// If only one IP Rows exists
			} else if (VALID_PLM_IP_LIST.contains(primKey)) {
				linkObj.put("size1", plmSize1);
			} else {
				throw new WTException("In PackCase Bom extract, not found PLM size literal for [sap_key :: " + sap_key
						+ "],   [j_3ASIZE :: " + j_3ASIZE + "]");
			}
*/
			linkObj.put("section", "casing");
			linkObj.put("DROPPED", "false");

		} else {
			linkObj = null;
		}
		return linkObj;

	}

	/**
	 * @param primKey
	 * @param primKey2
	 * @return
	 * @throws WTException
	 */
	private static boolean isValidPrim(String sap_key, String primKey) throws WTException {

		if (FormatHelper.hasContent(primKey)) {
			if (VALID_PLM_PRIM_KEYS_LIST.contains(primKey)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new WTException("Found a Row with out any UOM key on [sap_key :: " + sap_key + "]");
		}

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param sap_Grid_Size
	 * @param psdSize1Values
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static String getPlmSize1Value(String sap_key, LCSProduct sp, String sap_Grid_Size,
			Collection<String> psdSize1Values) throws WTException, IOException {
		String plm_Size_Literal = "";
		if (FormatHelper.hasContent(sap_Grid_Size)) {
			LCSLifecycleManaged sizeCategoryBO = (LCSLifecycleManaged) sp.getValue("hbiSellingSizeCategory");
			String sizeCatIda2a2 = FormatHelper.getNumericObjectIdFromObject(sizeCategoryBO);
			String aps_sizecode = getApsSizeCodeFromExtract(sap_key, sap_Grid_Size);
			plm_Size_Literal = getPLMSizeLiteralFromSapgrid(sizeCatIda2a2, sap_Grid_Size, aps_sizecode);

			if (psdSize1Values.contains(plm_Size_Literal)) {
				return plm_Size_Literal;
			} else {
				throw new WTException("J_3ASIZE :: [" + sap_Grid_Size + "], [ PLM Size Literal :: " + plm_Size_Literal + "],"
						+ " not existing on the PSD of Product [" + sp.getName() + "]");
			}
		} else {
			plm_Size_Literal = "";
		}
		return plm_Size_Literal;
	}

	/**
	 * @param sap_key
	 * @param sap_Grid_Size
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String getApsSizeCodeFromExtract(String sap_key, String sap_Grid_Size)
			throws IOException, WTException {
		String apsCode = "";
		Collection<String> codes = new HashSet();
		FileInputStream file = new FileInputStream(new File(SP_SIZES));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String cell0 = HBISPBomUtil.getCellValue(row, 0);
			String cell1 = HBISPBomUtil.getCellValue(row, 1);
			if (cell0.equals(sap_key) && cell1.equals(sap_Grid_Size)) {
				apsCode = HBISPBomUtil.getCellValue(row, 2);
				if (FormatHelper.hasContent(apsCode)) {
					codes.add(apsCode);
				} else {
					throw new WTException("Not found any unique Aps code for [sap_key :: " + sap_key
							+ "], [J_3AKORD2 :: " + sap_Grid_Size + "]");
				}
			}

		}
		if (codes.size() == 1) {
			apsCode = codes.iterator().next();
		} else if (codes.size() == 0) {
			apsCode = "";
		} else {
			throw new WTException("Not found any unique Aps code for [sap_key :: " + sap_key + "], [J_3AKORD2 :: "
					+ sap_Grid_Size + "]");
		}
		return apsCode;

	}

	/**
	 * @param sizeCatida2a2
	 * @param sap_Grid_Size
	 * @param aps_sizecode
	 * @return
	 * @throws WTException
	 */
	private static String getPLMSizeLiteralFromSapgrid(String sizeCatida2a2, String sap_Grid_Size, String aps_sizecode)
			throws WTException {
		LCSLifecycleManaged businessObject = null;
		String plmSizeLiteral = "";
		String businessObjectTypePath = "Business Object\\Automation Support Tables\\Size Xref";
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		String hbiAPSSizeCategory_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCategory").getColumnDescriptorName();//getVariableName();
		String hbiSAPGridSize_DBColumn = boFlexTypeObj.getAttribute("hbiSAPGridSize").getColumnDescriptorName();//getVariableName();
		String hbiAPSSizeCode_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCode").getColumnDescriptorName();//.getVariableName();

		String typeIdPath = String.valueOf(boFlexTypeObj.getTypeIdPath());

		// Initializing the PreparedQueryStatement,
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiAPSSizeCategory_DBColumn),
				sizeCatida2a2, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiSAPGridSize_DBColumn),
				sap_Grid_Size, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiAPSSizeCode_DBColumn),
				aps_sizecode, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"), "?", "="),
				typeIdPath);

		// Get SearchResults instance from the given PreparedQueryStatement
		HBISPBomUtil.debug("Stmt ::" + statement.toString());
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if (results != null && results.getResultsFound() == 1) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById(
					"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLifecycleManaged.IDA2A2"));
			plmSizeLiteral = (String) businessObject.getValue("hbiPLMSizeLiteral");
		} else {
			throw new WTException(
					"PLMSizeLiteral not found in FlexPlM for SAP Size Grid Value :: [" + sap_Grid_Size + "]");
		}
		return plmSizeLiteral;

	}

	/**
	 * @param key
	 * @return
	 */
	private static String getPrimKey(String key) {

		return "hbi" + key;
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
	 * @param pkgCaseName
	 * @param bomRows 
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static FlexBOMPart loadPackCaseBom(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			FlexSpecification refSpec, String pkgCaseName, Collection<Row> bomRows) throws IOException, WTException, WTPropertyVetoException {
		HBISPBomUtil.debug("########## Started BOM Loading PackCaseBom :: ["+pkgCaseName+"] #######");
		FlexBOMPart packCaseBom = null;
		Transaction tr = null;
		try {
				Map<String, Collection<FlexObject>> uomRows = getPkgCaseBomRows(sap_key, sp, putup,bomRows);

					if (!uomRows.isEmpty()) {

				tr = new Transaction();
				tr.start();

				packCaseBom = createPackCaseBom(sap_key, sp, putup, uomRows, pkgCaseName);

				HBISPBomUtil.createSpecBomLink(sp, packCaseBom, season, refSpec);

				tr.commit();
				tr = null;
			} else {
				HBISPBomUtil.debug("No Pack Case Bom data found in extract for [ sap_key :: " + sap_key + "],"
						+ "[ putup :: " + putup + "]");
			}
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! Pack Case Bom :: ["+pkgCaseName+"] Create/update transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}
		}

		HBISPBomUtil.debug("########## Completed BOM Loading for [ sap_key :: " + sap_key + "],"
						+ "[ putup :: " + putup + "], SP :: ["+sp.getName()+"], PackCaseBom :: ["+pkgCaseName+"] #######");
		return packCaseBom;

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
	private static Map<String, Collection<FlexObject>> getPkgCaseBomRows(String sap_key, LCSProduct sp, String putup, Collection<Row> bomRows)
			throws WTException, IOException {
		@SuppressWarnings("rawtypes")
		Map<String, Collection<FlexObject>> rows = new HashMap();
		// Get respective BOMs for the SAP_Key that have to be loaded.
		Collection<String> psdSize1Values = HBISPBomUtil.getPSDSize1Values(sp);
		HBISPBomUtil.debug("psdSize1Values :: " + psdSize1Values);
		// Check if product having a PSD
		if (psdSize1Values.size() >= 1) {
			
			Collection<FlexObject> packCaseBomData = getPackCaseBomData(sap_key, sp, putup, psdSize1Values,bomRows);
		
			HBISPBomUtil.debug("Total packCaseBomData fetched" + packCaseBomData.size());
			Map<String, Collection<FlexObject>> groupedRows = groupByPrimKey(packCaseBomData);
			HBISPBomUtil.debug("Total SAP UOM groups fecthed" + groupedRows.keySet().toString());

			Map<String, Collection<FlexObject>> validgroupedRows = groupByValidPrimKeys(groupedRows);
			HBISPBomUtil.debug("Total Valid SAP UOM groups filtered is"+validgroupedRows.keySet().toString());

			rows = getValidRows(sap_key, putup, sp, validgroupedRows, psdSize1Values);
			HBISPBomUtil.debug("Total processed Valid SAP UOM groups fecthed" + rows.size());

		} else {
			throw new WTException(
					"!!!! No PSD found in FlexPLM for [sap_key :: " + sap_key + "], [SP :: " + sp.getName() + "]");
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

		Collection<String> primList = VALID_SAP_UOM_LIST;
		Iterator itr = primList.iterator();
		// Iterate for all EA,CV,IP Rows
		while (itr.hasNext()) {
			String primKey = (String) itr.next();

			Collection<FlexObject> primRows = validPrimRows.get(primKey);
			if (primRows != null && !primRows.isEmpty()) {
				FlexObject topRow = null;
				Collection<FlexObject> allRows = new ArrayList();
				// If there is only one row for a UOM and not having size will be a Top Row
				if (primRows.size() == 1 && !isRowHavingSize(primRows.iterator().next())) {
					topRow = primRows.iterator().next();
				} else {
					topRow = getTopRow(sap_key, putup, primRows, psdSize1Values, primKey);
					if (topRow.getObject("size1") == null) {
						Collection<FlexObject> sVarRows = getSize1VarRows(sap_key, putup, primRows, psdSize1Values);
						if (!sVarRows.isEmpty()) {
							allRows.addAll(sVarRows);
						}
					} else {
						topRow.put("size1", "");
					}
				}
				topRow.put("partName", getPartName(primKey));
				
				//EA UOMs Top rows in FlexPLM always should have hbiPrimarySecondary value hbiEA
				if ("EA".equals(primKey)) {
				topRow.put("hbiPrimarySecondary", "hbiEA");
				}
				
				topRow.put("section", "casing");
				topRow.put("DROPPED", "false");
				topRow.put("size1", "");
				allRows.add(topRow);
				validRows.put(primKey, allRows);
			}
		}
		return validRows;
	}

	/**
	 * @param flexObject
	 * @return
	 */
	private static boolean isRowHavingSize(FlexObject flexObject) {
		String size1 = flexObject.getData("size1");
		if (FormatHelper.hasContent(size1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param sap_key
	 * @param putup
	 * @param rows
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> getSize1VarRows(String sap_key, String putup, Collection<FlexObject> rows,
			Collection<String> psdSize1Values) throws WTException {
		Collection<FlexObject> size1VarRows = new ArrayList();
		Iterator itr = rows.iterator();
		Collection sizes = new ArrayList();
		while (itr.hasNext()) {
			FlexObject row = (FlexObject) itr.next();
			String size1Val = row.getData("size1");
			if (FormatHelper.hasContent(size1Val)) {
				if (psdSize1Values.contains(size1Val)) {
					if (!sizes.contains(size1Val)) {
						sizes.add(size1Val);
						size1VarRows.add(row);
					} else {
						throw new WTException("More than one row found for same J_3ASIZE on the similar UOM [sap_key :: "
								+ sap_key + "], [PUTUP :: " + putup + "],[PrimarySecondary :: "+ row.getData("hbiPrimarySecondary")+"], [PLM_SIZE_LITERAL :: " + size1Val + "]");
					}
				}else{
					throw new WTException("[sap_key :: "
							+ sap_key + "], [PUTUP :: " + putup + "],[PrimarySecondary :: "+ row.getData("hbiPrimarySecondary")+"], [PLM_SIZE_LITERAL :: " + size1Val + "], not existing on PSD sizes ["+psdSize1Values+"]");
				}
			} else {
				throw new WTException("At least one row found with no J_3ASIZE value, for [sap_key :: " + sap_key + "], [PUTUP :: " + putup + "],[PrimarySecondary :: "+ row.getData("hbiPrimarySecondary")+"], [PLM_SIZE_LITERAL :: " + size1Val + "]");

			}
		}
		return size1VarRows;
	}

	/**
	 * @param sap_key
	 * @param putup
	 * @param rows
	 * @param psdSize1Values
	 * @param primKey
	 * @return
	 * @throws WTException
	 */
	private static FlexObject getTopRow(String sap_key, String putup, Collection<FlexObject> rows,
			Collection<String> psdSize1Values, String primKey) throws WTException {
		FlexObject topRow = null;

		if (isBomHavingAllProdSizes(sap_key, putup, rows, psdSize1Values)) {
			topRow = findTopRowFromVarRows(rows);
		} else {
			topRow = new FlexObject();
		}
		return topRow;
	}

	/**
	 * @param rows
	 * @return
	 * @throws WTException
	 *             Identifying the top row, if rows having variation rows.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static FlexObject findTopRowFromVarRows(Collection<FlexObject> rows) throws WTException {
		FlexObject topRow = null;
		Map<String, FlexObject> map = new HashMap();
		if (rows.size() == 1) {
			topRow = rows.iterator().next();
		} else if (rows.size() > 1) {
			Iterator itr = rows.iterator();
			while (itr.hasNext()) {
				FlexObject row = (FlexObject) itr.next();
				String prim = row.getData("hbiPrimarySecondary");
				if ("hbiEA".equals(prim)) {
					String key = getEAKey(row);
					map.put(key, row);
				} else if (VALID_PLM_CV_LIST.contains(prim)) {
					String key = getCVKey(row);
					map.put(key, row);
				} else if (VALID_PLM_IP_LIST.contains(prim)) {
					String key = getIPKey(row);
					map.put(key, row);
				}
			}
			// If all size variations are having same primary key.
			if (map.size() == 1) {
				return rows.iterator().next();
			} else {
				topRow = new FlexObject();
			}
		}
		return topRow;
	}

	/**
	 * @param primKey
	 * @return
	 */
	private static String getPartName(String primKey) {
		if ("EA".equals(primKey)) {
			return "SELLING UNIT (EA)";
		} else if ("CV".equals(primKey)) {
			return "CASE CARTONS";
		} else if ("IP".equals(primKey)) {
			return "INNER PACKS (IP)";
		}
		return primKey;

	}

	/**
	 * @param sap_key
	 * @param putup
	 * @param eaRows
	 * @param psdSize1Values
	 * @return
	 * @throws WTException
	 */
	private static boolean isBomHavingAllProdSizes(String sap_key, String putup, Collection<FlexObject> rows,
			Collection<String> psdSize1Values) throws WTException {
		Collection<String> sizeVars = getAllUniQueSizes(sap_key, putup, rows);
		if (psdSize1Values.size() == sizeVars.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param sap_key
	 * @param putup
	 * @param rows
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Collection<String> getAllUniQueSizes(String sap_key, String putup, Collection<FlexObject> rows)
			throws WTException {
		Collection<String> uniqueSizes = new HashSet();
		Iterator itr = rows.iterator();
		while (itr.hasNext()) {
			FlexObject row = (FlexObject) itr.next();
			String sizeValue = row.getData("size1");
			if (FormatHelper.hasContent(sizeValue)) {
				uniqueSizes.add(sizeValue);
			} else {
				throw new WTException(
						"Found row with no J_3ASIZE/PLM_SIZE_LITERAL values in the extract, for [ SAP_KEY :: "
								+ sap_key + " ], [Putup ::" + putup + "], [ MEINH :: "+row.getData("hbiPrimarySecondary")+"]");
			}
		}
		return uniqueSizes;
	}
	
}

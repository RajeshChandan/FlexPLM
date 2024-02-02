package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import wt.fc.WTObject;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HbiSalesBomLoader {
	private static final String SALES_BOMTYPE = HBISPBomUtil.SALES_BOMTYPE;
	private static final String SP_SALES_BOM_EXPORT = HBISPBomUtil.SP_SALES_BOM_EXPORT;
	private static final String SP_SALES_ALT_BOM_EXPORT = HBISPBomUtil.SP_SALES_ALT_BOM_EXPORT;
	private static final String ID = "ID";
	private static final String REQUIRED_BOM_COLUMNS = "ID,branchId,sortingNumber,partName,section,"
			+ "hbiErpComponentStyle,hbiErpComponentPutUp,hbiErpComponentColor,hbiErpComponentSize,quantity";
	public static String materialFlexTypePathCC = LCSProperties.get("com.hbi.wc.cartonID", "Material\\Casing");
	
	/**
	 * @param rows
	 * @param sp
	 * @param salesBom
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> convertInToFlexObjects(Collection<Row> rows, LCSProduct sp,
			FlexBOMPart salesBom) throws WTException, IOException {

		Collection<FlexObject> bomLinks = new ArrayList();
		LCSFlexBOMLogic bomLogic = new LCSFlexBOMLogic();
		int maxBranchId = bomLogic.getMaxBranchId(salesBom);
		int maxSortingNumber = 0;

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = rows.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			FlexObject linkObj = new FlexObject();

			maxBranchId = maxBranchId + 1;
			maxSortingNumber = maxSortingNumber + 1;

			// COMMENTS (B)
			linkObj.put("partName", HBISPBomUtil.getCellValue(row, 1));

			// COMPONENT_STYLE (D) (Respective product in pLM has to be fetched and loaded
			String compStyle_SapKey = HBISPBomUtil.getCellValue(row, 3);
			LCSProduct compStyle = HBISPBomUtil.getProductFromSapKey("COMPONENT_STYLE", compStyle_SapKey, row);
			if (compStyle != null) {
				linkObj.put("hbiErpComponentStyle", compStyle.getBranchIdentifier());
			} else {
				throw new WTException(
						"COMPONENT_STYLE :: [" + HBISPBomUtil.getCellValue(row, 3) + "], is not a product in FlexPLM");

			}
			/*Collection<LCSProduct> compStyles = HBISPBomUtil.getProductsFromSapKey(compStyle_SapKey);
			LCSProduct compStyle = null;
			if (!compStyles.isEmpty()) {
				compStyle = compStyles.iterator().next();
				if (compStyle != null) {
					linkObj.put("hbiErpComponentStyle", compStyle.getBranchIdentifier());
				} else {
					throw new WTException("COMPONENT_STYLE :: [" + HBISPBomUtil.getCellValue(row, 3)
							+ "], is not a product in FlexPLM");

				}
			} else {
				throw new WTException(
						"COMPONENT_STYLE :: [" + HBISPBomUtil.getCellValue(row, 3) + "], is not a product in FlexPLM");

			}*/
			
			// COMPONENT_PUTUP (E)
			linkObj.put("hbiErpComponentPutUp", HBISPBomUtil.getCellValue(row, 4));

			// COMPONENT_COLOR (F)
			linkObj.put("hbiErpComponentColor", HBISPBomUtil.getCellValue(row, 5));

			// COMPONENT_SIZE (G)- This is not required to load in PLM
			String aps_size = HBISPBomUtil.getCellValue(row, 6);
			// String plmSize = HBISPBomUtil.getPlmSize1ApsValue(compStyle,
			// sap_Grid_Size);
			//String plmSize = getPlmSizeLiteral(compStyle_SapKey, aps_size,sp);
		
			// USAGE (H)
			linkObj.put("quantity", HBISPBomUtil.getCellValue(row, 7));
			
			//PLM_SIZE_LITERAL (I)
			String plmSize = HBISPBomUtil.getCellValue(row, 8);;

			if (FormatHelper.hasContent(plmSize)) {
				linkObj.put("hbiErpComponentSize", plmSize);
			} else {
				throw new WTException("Not found PLM size literal for [COMPONENT_STYLE :: " + compStyle_SapKey
						+ "],   [aps_size :: " + aps_size + "]");
			}
			linkObj.put("section", "components");
			linkObj.put("branchId", maxBranchId);
			linkObj.put("sortingNumber", maxSortingNumber);
			linkObj.put(ID, updateID(maxBranchId, salesBom));
			linkObj.put("DROPPED", "false");
			bomLinks.add(linkObj);
			//HBISPBomUtil.debug("FlexObject :: "+linkObj);

		}
		return bomLinks;

	}

	/**
	 * @param secRow
	 * @param maxBranchId
	 * @param mergedBom
	 * @return
	 */
	public static String updateID(int maxBranchId, FlexBOMPart mergedBom) {

		String bomMasterID = FormatHelper.getNumericObjectIdFromObject((WTObject) mergedBom.getMaster());
		String topRowID = "-PARENT:com.lcs.wc.part.LCSPartMaster:" + bomMasterID + "-REV:A-BRANCH:" + maxBranchId;
		return topRowID;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param salesBomName
	 * @param altSaleBomName
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	public static FlexBOMPart loadSalesBom(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			FlexSpecification refSpec, String salesBomName, String altSaleBomName, String cartonId)
			throws WTException, WTPropertyVetoException, IOException {
		FlexBOMPart salesBom = null;
		FlexBOMPart altBom = null;

		Transaction tr = null;
		try {
			Collection<Row> rows = getProductBomData(sap_key, SP_SALES_BOM_EXPORT, putup);
			if (!rows.isEmpty()) {

				tr = new Transaction();
				tr.start();

				salesBom = creatSalesBOM(sap_key, sp, putup, rows, salesBomName, cartonId, "");
				HBISPBomUtil.createSpecBomLink(sp, salesBom, season, refSpec);

				// Create Alternate Sales Bom If exist
				Collection<Row> alt_rows = getProductBomData(sap_key, SP_SALES_ALT_BOM_EXPORT, putup);
				if (!alt_rows.isEmpty()) {
					altBom = creatSalesBOM(sap_key, sp, putup, alt_rows, altSaleBomName,"","");
					HBISPBomUtil.createSpecBomLink(sp, altBom, season, refSpec);
				} else {
					HBISPBomUtil.debug("No alterante sale Bom found for refSpec [" + refSpec.getName() + "]");
				}

				tr.commit();
				tr = null;
			} else {
				HBISPBomUtil.debug("Skipping !!!! No sales or Alternate Bom data found in extract for [ sap_key :: " + sap_key + "],"
						+ "[ putup :: " + putup + "]");
			}
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! Sales Bom Create transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}
		}
		return salesBom;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param rows
	 * @param salesBomName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	private static FlexBOMPart creatSalesBOM(String sap_key, LCSProduct sp, String putup, Collection<Row> rows,
			String salesBomName, String cartonId,String cmdbomType) throws WTException, WTPropertyVetoException, IOException {
		FlexBOMPart salesBom = null;

		salesBom = initiateSalesBOM(sp, SALES_BOMTYPE, salesBomName,cartonId,cmdbomType);
		salesBom = updateBomData(sap_key, sp, putup, salesBom, rows);

		return salesBom;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param bom
	 * @param rows
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexBOMPart updateBomData(String sap_key, LCSProduct sp, String putup, FlexBOMPart bom,
			Collection<Row> rows) throws IOException, WTException, WTPropertyVetoException {

		FlexBOMPart salesBom = bom;
		if (salesBom != null) {

			// Convert the BOM rows into FlexObjects
			Collection<FlexObject> bomLinks = convertInToFlexObjects(rows, sp, salesBom);

			// Covert data into a string with required delimiters
			String dataString = HBISPBomUtil.covertInToMergeString(bomLinks, REQUIRED_BOM_COLUMNS);

			FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
			// Get latest version
			salesBom = (FlexBOMPart) VersionHelper.latestIterationOf(salesBom);
			// checkout
			salesBom = (FlexBOMPart) VersionHelper.checkout(salesBom);

			HBISPBomUtil.debug("************* Checked out BOM");

			// get Working Copy
			salesBom = (FlexBOMPart) VersionHelper.getWorkingCopy(salesBom);

			HBISPBomUtil.debug("************* Got Working copy of BOM");

			// get ObjectId of Bom
			String bomId = FormatHelper.getObjectId(salesBom);

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
			salesBom = bomPartClientModel.getBusinessObject();
			HBISPBomUtil.debug("########## Completed Loading SalesBom  ###########");
		} else {
			HBISPBomUtil.debug("BOM is Null");
		}
		return salesBom;
	}

	/**
	 * @param sap_key
	 * @param file_extract
	 * @param putup
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Collection<Row> getProductBomData(String sap_key, String file_extract, String putup)
			throws IOException {
		long start_getSalesData = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.00000");
		
		Collection<Row> rows = new ArrayList();
		FileInputStream file = new FileInputStream(new File(file_extract));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		boolean flag = false;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// SAP_KEY (cell(0)) AND PARENT_PUTUP cell(2)
			String parent_putup = HBISPBomUtil.getCellValue(row, 2);
			if (parent_putup.length() == putup.length() - 1) {
				parent_putup = "0" + parent_putup;
			}
			if (sap_key.equals(HBISPBomUtil.getCellValue(row, 0)) && putup.equals(parent_putup)) {
				rows.add(row);
				flag = true;
			} else if(flag){
				break;
			}
		}
		file.close();
		
		long end_getSalesData = System.currentTimeMillis();
		HBISPBomUtil.debug(sap_key +", A Sales Bom Reading From Excels sheets time ::"
				+ " ["+formatter.format((end_getSalesData - start_getSalesData) / 1000d)+"]");
		
		
		return rows;

	}

	/**
	 * @param sp
	 * @param sap_key
	 * @param aps_SIZE
	 * @param sp 
	 * @param psdSize1Values
	 * @return
	 * @throws IOException
	 * @throws WTException
	 */
	private static String getPlmSizeLiteral(String sap_key, String aps_SIZE, LCSProduct sp) throws IOException, WTException {
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
					&& HBISPBomUtil.getCellValue(row, 2).equals(aps_SIZE)) {
				plm_Size_Literal = HBISPBomUtil.getCellValue(row, 3);
				if(FormatHelper.hasContent(plm_Size_Literal)){
					file.close();
					return plm_Size_Literal;
				}else{
					file.close();
					throw new WTException("SAP_KEY :: ["+sap_key+" ], APS_SIZE :: [" + aps_SIZE + "], [ plmSize1 :: " + plm_Size_Literal + "],"
							+ " not existing on the PSD of Product [" + sp.getName() + "]");
				}
			}
		}
		
		return plm_Size_Literal;
	}


	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param refSpec
	 * @param salesBomName
	 * @param bomRows
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	public static FlexBOMPart loadSalesBom(String sap_key, LCSProduct sp, String putup, LCSSeason season,
		FlexSpecification refSpec, String salesBomName, Collection<Row> bomRows, String cartonId,String cmdbomType) throws WTException, WTPropertyVetoException, IOException {
		FlexBOMPart salesBom = null;
		Transaction tr = null;
		try {
			Collection<Row> rows = bomRows;
			if (!rows.isEmpty()) {

				tr = new Transaction();
				tr.start();

				salesBom = creatSalesBOM(sap_key, sp, putup, rows, salesBomName, cartonId,cmdbomType);
				HBISPBomUtil.createSpecBomLink(sp, salesBom, season, refSpec);

				tr.commit();
				tr = null;
			} else {
				HBISPBomUtil.debug("Skipping !!!! No sales Bom data found in extract for [ sap_key :: " + sap_key + "],"
						+ "[ putup :: " + putup + "]");
			}
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! Sales Bom Create transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}
		}
		return salesBom;
	}
	
	/**
	 * @param sp
	 * @param salesBomtype
	 * @return
	 * @throws WTException
	 */
	public static FlexBOMPart initiateSalesBOM(LCSProduct sp, String salesBomtype, String bomName, String cartonId,String cmdbomType) throws WTException {
		FlexType bomType = FlexTypeCache.getFlexTypeFromPath(salesBomtype);
		FlexBOMPart newBom = null;
		FlexBOMPart oldBom = getExistingBom(sp, bomName);
		if (oldBom == null) {
			newBom = (new LCSFlexBOMLogic()).initiateBOMPart(sp, bomType, "MAIN");
			System.out.println("************* initiated BOM");

			String bomId = FormatHelper.getObjectId(newBom);

			FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
			// Load Bom
			try {
				
				LCSMaterial material = getMaterial(cartonId);
				bomName = bomName.substring(0, bomName.lastIndexOf("_"));
				bomPartClientModel.load(bomId);
				bomPartClientModel.setValue("hbiDesc", bomName);
				bomPartClientModel.setValue("hbiBOMType", cmdbomType);
				bomPartClientModel.setValue("hbiErpCartonID", material); //78861374
				bomPartClientModel.setValue("number", "");
				bomPartClientModel.save();
				bomPartClientModel.checkIn();
				newBom = bomPartClientModel.getBusinessObject();

			} catch (WTPropertyVetoException e) {
				String error = "WTPropertyVetoException Caught in initiateMergeBom method";
				System.out.println(error + e.getLocalizedMessage());
				throw new WTException(e);
			}
		} else {
			throw new WTException("BOM :: [" + bomName + "], already existing on Product [" + sp.getName() + "]");
		}
		return newBom;

	}
	

	private static LCSMaterial getMaterial(String cartonId) {
		// TODO Auto-generated method stub
		LCSMaterial material =null;
		 try {
			 LCSLog.debug("getMaterial cartonId:: "+cartonId);
			if(FormatHelper.hasContent(cartonId)){
				FlexType matCCFlexType = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePathCC);
				LCSLog.debug("matCCFlexType getIDPath "+matCCFlexType.getTypeIdPath());
				
				PreparedQueryStatement stmt = new PreparedQueryStatement();
		        stmt.appendFromTable("LCSMaterial");
		        stmt.appendSelectColumn("LCSMaterial", "BRANCHIDITERATIONINFO");
		        stmt.appendOpenParen();
				stmt.appendCriteria(new Criteria("LCSMaterial", "flextypeidpath", "%"+matCCFlexType.getTypeIdPath()+"%", Criteria.LIKE));
				stmt.appendAnd();
		        stmt.appendCriteria(new Criteria("LCSMaterial", "latestIterationInfo", "1", Criteria.EQUALS));
		        stmt.appendAnd();
		        stmt.appendCriteria(new Criteria("LCSMaterial", "ptc_str_1typeInfoLCSMaterial", cartonId, Criteria.EQUALS));
		        stmt.appendClosedParen();
		        Collection<FlexObject> output  = new ArrayList();
		        output = LCSQuery.runDirectQuery(stmt).getResults();
		        LCSLog.debug("size::[ " + output.size()+" ]");
								LCSLog.debug("<----------------------------stmt----------------->"+stmt);

		        if (output.size() == 1) {
		             FlexObject obj = (FlexObject) output.iterator().next();
		             material = (LCSMaterial) LCSQuery
		                     .findObjectById("VR:com.lcs.wc.material.LCSMaterial:" + obj.getData("LCSMaterial.BRANCHIDITERATIONINFO"));
		        }else{
		        	LCSLog.debug("******No material found with name ["+cartonId+"]");
		        }
			  }
			} catch (WTException e) {
				
				e.printStackTrace();
			}	

		return material;
		//(LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:78861374");
	}

	/**
	 * @param linkedGP
	 * @param bomName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static FlexBOMPart getExistingBom(LCSProduct sp, String bomName) throws WTException {
		FlexBOMPart bom = null;
		FlexSpecification flexSpec = null;

		Collection<FlexBOMPart> bomParts = new ArrayList();
		if (sp != null && FormatHelper.hasContent(bomName)) {
			try {
				bomParts = (new LCSFlexBOMQuery()).findBOMPartsForOwner(sp, "A", "MAIN", (FlexSpecification) flexSpec,
						bomName);
				if (bomParts.size() == 1) {
					bom = (FlexBOMPart) bomParts.iterator().next();
					if (VersionHelper.isCheckedOut(bom)) {
						System.out.println("Found check out bom on SP, so checking in [bomName :: " + bomName
								+ "], [ SP :: " + sp.getName() + " ]");
						bom = (FlexBOMPart) VersionHelper.checkin(bom);
					}

				} else if (bomParts.size() > 1) {
					String error = "Error,Found Product with two Boms same name [ SP :: " + sp + " ], [ bomName :: "
							+ bomName + " ]";
					System.out.println("!!!!  " + error);
					throw new WTException(error);
				}

			} catch (WTException e) {
				String error = "Exception occured while searching Boms with [ bomName :: " + bomName + " ][ SP :: "
						+ sp.getName() + " ]";
				System.out.println("!!!!  " + error);
				throw new WTException(error);
			}

		}
		return bom;
	}
}

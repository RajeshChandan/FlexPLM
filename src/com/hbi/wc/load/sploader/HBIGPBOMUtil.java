package com.hbi.wc.load.sploader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecHelper;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.specification.FlexSpecificationClientModel;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.VersionHelper;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBIGPBOMUtil {
	private static final String PLACEHOLDER_VALUE = "1";
	private static final String GP_PRODUCT = "Product\\BASIC CUT & SEW - GARMENT";

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param srcCfg
	 * @param specName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws NumberFormatException
	 */
	@SuppressWarnings("rawtypes")
	public static FlexSpecification createProdSpec(String sap_key, LCSProduct sp, LCSSeason season,
			LCSSourcingConfig srcCfg, String specName)
			throws WTException, NumberFormatException, WTPropertyVetoException {
		FlexSpecificationClientModel flexSpecModel = new FlexSpecificationClientModel();
		boolean createSpec = true;
		FlexType specFlexType = FlexTypeCache.getFlexTypeFromPath("Specification\\Basic Cut and Sew - Garment");
		flexSpecModel.setFlexType(specFlexType);

		FlexSpecification spec = null;
		SearchResults sr = FlexSpecQuery.findExistingSpecs(sp, season, srcCfg);

		if (sr.getResults().size() > 0) {
			Iterator srItr = sr.getResults().iterator();
			while (srItr.hasNext()) {
				FlexObject specFo = (FlexObject) srItr.next();

				String name = specFo.getData("FLEXSPECIFICATION.PTC_STR_1TYPEINFOFLEXSPECIFI");
				if (name.contains(specName)) {
					createSpec = false;
					break;
				}
			}

		}
		if (createSpec) {
			Collection<String> seasonIds = new ArrayList<String>();
			/** Adding seasons to it. */
			seasonIds.add(season.toString());

			//WTPartMaster prodMaster = (WTPartMaster) sp.getMaster();
			LCSPartMaster prodMaster = sp.getMaster();
			

			/** Setting the specification name to product name. */
			flexSpecModel.setValue("specName", specName);
			/** Creating an array list for source. */
			Collection<LCSSourcingConfig> sourceIds = new ArrayList<LCSSourcingConfig>();
			/** Adding source to it. */
			srcCfg = (LCSSourcingConfig) VersionHelper.latestIterationOf(srcCfg.getMaster());

			// srcCfg=(LCSSourcingConfig)VersionHelper.checkout(srcCfg);
			sourceIds.add(srcCfg);

			Collection<String> componentIds = new ArrayList<String>();
			/** Creating a HashMap. */
			Map<String, String> addtionalParams = new HashMap<String, String>();
			flexSpecModel.setSpecOwnerReference(ObjectReference.newObjectReference(prodMaster));
			flexSpecModel.setSpecOwner(srcCfg.getProductMaster());
			/**
			 * Saving the spec by passing
			 * flexSpecModel,sourceIds,seasonIds,componentIds,addtionalParams
			 */

			FlexSpecHelper.service.saveSpec(flexSpecModel, sourceIds, seasonIds, componentIds, addtionalParams);
			spec = flexSpecModel.getBusinessObject();
		}
		return spec;

	}

	/**
	 * @param carton_Id
	 * @return
	 * @throws WTException
	 */
	public static String getChildId(String carton_Id) throws WTException {
		String childId = "";
		if (FormatHelper.hasContent(carton_Id)) {

			LCSMaterialQuery materialQueryObject = new LCSMaterialQuery();

			// Bug in fetching the material has to fix
			LCSMaterial mat = materialQueryObject.findMaterialByNameType(carton_Id, null);
			if (mat != null) {
				String matMasterid = FormatHelper.getNumericObjectIdFromObject((WTObject) mat.getMaster());
				String matSupplierMasterid=getMaterialSupplierMasterId(mat);
				
				childId = getMatSupMaster(matMasterid, PLACEHOLDER_VALUE,matSupplierMasterid);
				
			} else {
				throw new WTException("Material not found in flexPLM for [carton_Id ::" + carton_Id + "]");
			}
		}
		return childId;

	}

	private static String getMaterialSupplierMasterId(LCSMaterial mat) {
		// TODO Auto-generated method stub
		String matSupplierMasterid=null;
		try {
			SearchResults supplierresults = LCSMaterialSupplierQuery.findMaterialSuppliers(mat);
		   Collection coll=supplierresults.getResults();
		   Iterator itr=coll.iterator();
		   while(itr.hasNext()){
			   FlexObject obj=(FlexObject)itr.next();
			   String supp=obj.getData("LCSSUPPLIERMASTER.SUPPLIERNAME");
			   if("Color Version".equals(supp)){
				   matSupplierMasterid= obj.getData("LCSSUPPLIERMASTER.IDA2A2");
				   break;
			   }
		   }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matSupplierMasterid;
	}

	/**
	 * @param matMasterId
	 * @param placeHolder
	 * @param matSupplierMasterid 
	 * @return
	 * @throws WTException
	 */
	private static String getMatSupMaster(String matMasterId, String placeHolder, String matSupplierMasterid) throws WTException {
		Query query = new Query();
		String matSupId = "";
		ResultSet results=null;
		try {
			query.prepareForQuery();
			if(FormatHelper.hasContent(matSupplierMasterid)){
				results = query.runQuery("SELECT ida2a2 FROM LCSMaterialSupplierMaster WHERE idA3A6 = "
						+ matMasterId + " AND idA3B6 = " + matSupplierMasterid);
			}
			else{
			 results = query.runQuery("SELECT ida2a2 FROM LCSMaterialSupplierMaster WHERE idA3A6 = "
					+ matMasterId + " AND PLACEHOLDER = " + placeHolder);
			}
			results.next();
			matSupId = results.getString(1);
			HBISPBomUtil.debug("MatSupId :: " + matSupId);
			results.close();
			query.cleanUpQuery();

		} catch (SQLException e) {
			throw new WTException("SQL Exception occured in LCSMaterialSupplierMaster method");
		}

		return matSupId;

	}

	/**
	 * @param hbiErpAttributionCode
	 * @param hbiSellingStyleNumber
	 * @param hbiDescription
	 * @param hbiAPSPackQuantity
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public static LCSProduct getProductByStyleNo(String SAP_KEY) throws WTException {
		FlexType prdType = FlexTypeCache.getFlexTypeFromPath(GP_PRODUCT);
		String sapKey_DB_Col = prdType.getAttribute("hbiProdNumber").getColumnName();//.getColumnDescriptorName();//.getVariableName();
//SELECT product.ida2a2 FROM prodarev product WHERE 
//( product.typeInfoLCSProduct.ptc_str_3 = 'P07900' AND product.flexTypeIdPath = '\16062\216858948' );
		LCSProduct product = null;
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("prodarev", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria("product", sapKey_DB_Col, SAP_KEY, Criteria.EQUALS));
		stmt.appendAnd();

		stmt.appendCriteria(new Criteria("product", "flexTypeIdPath", prdType.getTypeIdPath(), Criteria.EQUALS));
		stmt.appendClosedParen();

		Collection<FlexObject> output = new ArrayList();
		System.out.println(">>>>>>>>>>>>>>>>>query stmt "+stmt);
		output = LCSQuery.runDirectQuery(stmt).getResults();
		
		if (output.size() == 1) {
			FlexObject obj = (FlexObject) output.iterator().next();
			product = (LCSProduct) LCSQuery
					.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));

		}
		return product;

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
		FlexBOMPart existing=null;

		Collection<FlexBOMPart> bomParts = new ArrayList();
		if (sp != null && FormatHelper.hasContent(bomName)) {
			try {
				bomParts = (new LCSFlexBOMQuery()).findBOMPartsForOwner(sp, "A", "MAIN", (FlexSpecification) flexSpec);
                Iterator itr=bomParts.iterator();
				System.out.println("bomParts available:::::::::::::::"+bomParts);

                while (itr.hasNext()) {
					bom = (FlexBOMPart) itr.next();
					String name =bom.getName();
					System.out.println("Retrived BOM::::::::::::::::"+name);

					//name = name.replaceAll("\\s","");
					System.out.println("Retrived BOM 1::::::::::::::::"+name.replaceAll("\\s",""));
					System.out.println("Retrived bomName::::::::::::::::"+bomName);

					
					if(name.contains(bomName)) {
						System.out.println("-------------NEW BOM && OLD BOM MATCHED-----------"+name);

						if (VersionHelper.isCheckedOut(bom)) {
							System.out.println("Found check out bom on SP, so checking in [bomName :: " + bomName
									+ "], [ SP :: " + sp.getName() + " ]");
							bom = (FlexBOMPart) VersionHelper.checkin(bom);
						}
						
						existing=bom;
					}
					else {
						System.out.println("-------------NEW BOM && OLD BOM Not MATCHED-----------"+name);

						
					}
					

					
                }
				/*if (bomParts.size() == 1) {
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
				}*/

			} catch (WTException e) {
				String error = "Exception occured while searching Boms with [ bomName :: " + bomName + " ][ SP :: "
						+ sp.getName() + " ]";
				System.out.println("!!!!  " + error);
				throw new WTException(error);
			}

		}
		return existing;
	}
}

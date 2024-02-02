package com.burberry.wc.integration.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTProperties;

import com.burberry.wc.integration.productbomapi.bean.*;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.material.*;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle JSON data transform activity for Product BOM API.
 * Class contain several method to handle transform of object data putting it to
 * the beans.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPIUtil {

	/**
	 * Default Constructor.
	 */
	private BurberryProductBOMAPIUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIUtil.class);

	/**
	 * DIMID_COL.
	 */
	public static final String DIMID_COL = "FLEXBOMLINK.DIMENSIONID";

	/**
	 * DIM_COL.
	 */
	public static final String DIM_COL = "FLEXBOMLINK.DIMENSIONNAME";

	/**
	 * @param link
	 * @return
	 * @throws WTException
	 */
	public static boolean validateValuesonLink(FlexBOMLink link)
			throws WTException {
		String methodName = "validateValuesonLink";
		FlexType type = link.getFlexType();
		Collection<FlexTypeAttribute> flexTypeAttributesCol = type
				.getAllAttributes(FlexBOMFlexTypeScopeDefinition.LINK_SCOPE,
						null);
		for (FlexTypeAttribute att : flexTypeAttributesCol) {
			String value = String.valueOf(link.getValue(att.getAttKey()));
			if (!"section".equalsIgnoreCase(att.getAttKey())
					&& FormatHelper.hasContent(value)) {
				logger.debug(methodName + "key---> " + att.getAttKey()
						+ " value " + value);
				return true;
			}
		}
		return false;

	}

	/**
	 * @param childLink
	 * @param link 
	 * @param sku
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	public static ColourVariation getColourVariation(FlexBOMLink link,
			FlexBOMLink childLink, LCSSKU sku) throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		ColourVariation clrVariation = new ColourVariation();

		String methodName = "getColourVariation";

		// CR # JIRA # - Material Colour Size Variation: Start
		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_COLOUR_VARIATION);

		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_COLOUR_VARIATION);

		// getting colour variation data
		BurberryAPIBeanUtil.getObjectData(
				BurProductBOMConstant.COLOUR_VARIATION_IGNORE, clrVariation,
				childLink, BurProductBOMConstant.COLOUR_VARIATION_ATT,
			jsonMapping, sysJsonMapping);
		for (Map.Entry<String, String> mapEntry : jsonMapping.entrySet()) {
			String attKey = mapEntry.getKey();
			String jsonKey = mapEntry.getValue();
			if (!FormatHelper.hasContent(BeanUtils.getProperty(clrVariation,
					jsonKey))) {
				BeanUtils.setProperty(clrVariation, jsonKey,
						link.getValue(attKey));
			}

		}
		// CR # JIRA # - Material Colour Size Variation: End
				
		// RD-39 - BURBERRY-1399: Start
		//if (!FormatHelper.areWTObjectsEqual(link, childLink)) {
			if (sysJsonMapping
					.containsKey(BurProductBOMConstant.STR_BOM_BRANCH_ID)) {
				BeanUtils.setProperty(clrVariation,sysJsonMapping
										.get(BurProductBOMConstant.STR_BOM_BRANCH_ID),
								childLink.getBranchId()+ "_"+ String.valueOf(sku
												.getValue(BurProductBOMConstant.STR_SAP_MAT_NUM)));
			}
		//}
		// RD-39 - BURBERRY-1399: End
				
		LCSColor color = childLink.getColor();
		
		if(color==null){
			color=link.getColor();
		}
		// Setting colourway variation data
		clrVariation.setColourwayName((String) sku
				.getValue(BurConstant.SKUNAME));
		clrVariation.setMaterialColour(getMaterialColourBean(color));
		logger.debug(methodName + "Returing colourway variation bean "
				+ clrVariation);

		//BURBERRY-1495: RD-76 Free-typed colour data in BOM not appearing in Rating report: Start
		if(color!=null 
				&& jsonMapping.containsKey(BurProductBOMConstant.JSON_CV_COLOR_DESC)){
			BeanUtils.setProperty(clrVariation, jsonMapping
					.get(BurProductBOMConstant.JSON_CV_COLOR_DESC),
					color.getName());
		}
		//BURBERRY-1495: RD-76 Free-typed colour data in BOM not appearing in Rating report: End
		
		// Returning colourway variation bean
		return clrVariation;
	}

	/**
	 * @param color
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	private static MaterialColour getMaterialColourBean(LCSColor color)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		MaterialColour matColorBean = new MaterialColour();
		String methodName = "getMaterialColourBean()";
		if (color != null) {
			String codebase = WTProperties.getServerProperties().getProperty(
					"wt.server.codebase");
			codebase = codebase.substring(0, codebase.lastIndexOf('/'));
			logger.debug(methodName + "Extracting data from Material Color"
					+ color.getName());
			Map<String, String> jsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.JSON_MATCOLOR);
			Map<String, String> sysJsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_MATCOLOR);
			// Getting material colour data
			BurberryAPIBeanUtil.getObjectData(
					BurProductBOMConstant.MATCOLOR_IGNORE, matColorBean, color,
					BurProductBOMConstant.MATCOLOR_ATT, jsonMapping,
					sysJsonMapping);
			for (Map.Entry<String, String> mapEntry : jsonMapping.entrySet()) {
				String strAttKey = mapEntry.getKey();
				String strJsonKey = mapEntry.getValue();
				logger.debug(methodName + " color strAttKey " + strAttKey
						+ " color strJsonKey " + strJsonKey);
				// setting colour thumbnail
				if (BurProductBOMConstant.THUMBNAIL.equalsIgnoreCase(strAttKey)
						&& FormatHelper.hasContent(color.getThumbnail())) {
					BeanUtils.setProperty(matColorBean, strJsonKey, codebase
							+ color.getThumbnail());
				}
			}
			// Validation Required attributes
			// BurberryAPIBeanUtil.validateRequiredAttributes(matColorBean,
			// BurProductBOMConstant.MATCOLOR_REQ);
		}
		logger.debug(methodName + "Returing Material Colour bean "
				+ matColorBean);
		// Returning material colour bean
		return matColorBean;
	}

	/**
	 * @param link
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static MaterialMaster getMaterialMasterBean(FlexBOMLink link)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getMaterialMasterBean()";
		MaterialMaster materialMasterBean = new MaterialMaster();
		LCSMaterial materialObj = (LCSMaterial) VersionHelper
				.latestIterationOf(link.getChild());
		if (materialObj != null) {
			logger.debug(methodName + "Extracting data from Material "
					+ materialObj.getName());
			Map<String, String> jsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.JSON_MAT);
			Map<String, String> sysJsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_MAT);
			// Getting material data
			BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.MAT_IGNORE,
					materialMasterBean, materialObj,
					BurProductBOMConstant.MAT_ATT, jsonMapping, sysJsonMapping);
			materialMasterBean.setMaterialSupplier(getMaterialSupplierBean(
					link, materialObj));
			// CR RD-022 JIRA 1368: Start
			// Get Material Flex Type name
			if (sysJsonMapping.containsKey(BurConstant.MATERIALTYPE)) {
				BeanUtils.setProperty(materialMasterBean,
						sysJsonMapping.get(BurConstant.MATERIALTYPE),
						(materialObj.getFlexType().getFullNameDisplay()));
			}
			// CR RD-022 JIRA 1368: End
			// Validation Required attributes
			// BurberryAPIBeanUtil.validateRequiredAttributes(materialMasterBean,
			// BurProductBOMConstant.MAT_REQ);
		}

		logger.debug(methodName + "Returing material Master bean "
				+ materialMasterBean);
		// Returning material bean
		return materialMasterBean;
	}

	/**
	 * @param vmLink
	 * @param materialObj
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	private static MaterialSupplier getMaterialSupplierBean(FlexBOMLink vmLink,
			LCSMaterial materialObj) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {
		String methodName = "getMaterialMasterBean()";
		MaterialSupplier materialSupplierBean = new MaterialSupplier();
		LCSSupplier sup = (LCSSupplier) VersionHelper.latestIterationOf(vmLink
				.getSupplier());
		LCSMaterialSupplier materialSupObj = null;
		// Check if supplier object is not null
		if (sup != null) {
			materialSupObj = (LCSMaterialSupplier) LCSMaterialSupplierQuery
					.findMaterialSupplier(materialObj.getMaster(),
							(LCSSupplierMaster) sup.getMaster());
			logger.debug(methodName
					+ " material supplier from API supplier from FlexBOMLink "
					+ materialSupObj);
		}
		if (materialSupObj != null) {
			logger.debug(methodName + "Extracting data from Material " + vmLink);
			materialSupObj = (LCSMaterialSupplier) VersionHelper
					.latestIterationOf(materialSupObj);
			Map<String, String> jsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.JSON_MATSUP);

			Map<String, String> sysJsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_MATSUP);
			// Getting material supplier data
			BurberryAPIBeanUtil.getObjectData(
					BurProductBOMConstant.MATSUP_IGNORE, materialSupplierBean,
					materialSupObj, BurProductBOMConstant.MATSUP_ATT,
					jsonMapping, sysJsonMapping);
			// Validation Required attributes
			// BurberryAPIBeanUtil.validateRequiredAttributes(
			// materialSupplierBean, BurProductBOMConstant.MATSUP_REQ);
			
			//BURBERRY-1485 New Attributes Additions post Sprint 8: Start
			SupplierMaster supMasterBean = getSupplierMasterBean(sup);
			logger.debug(methodName + "Supplier Master Bean: "+supMasterBean);
			materialSupplierBean.setSupplierMaster(supMasterBean);
			//BURBERRY-1485 New Attributes Additions post Sprint 8: End
		}

		logger.debug(methodName + "Returing material Supplier bean "
				+ materialSupplierBean);
		// Returning material supplier bean
		return materialSupplierBean;
	}

	// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
	/**
	 * Method to get Supplier Master Bean Data using Supplier Object
	 * 
	 * @param supplierObject
	 *            LCSSupplier
	 * @return SupplierMaster
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
	private static SupplierMaster getSupplierMasterBean(
			LCSSupplier supplierObject) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {
		String methodName = "getSupplierMasterBean";
		logger.debug(methodName + "Supplier Name: " + supplierObject.getName());
		SupplierMaster supplierMasterBean = new SupplierMaster();
		// Get all the json Mapping for Supplier Master
		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_SUPPLIER_MASTER);
		// Get all the system mapping for Supplier Master
		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_SUPPLIER_MASTER);
		// Getting supplier data
		BurberryAPIBeanUtil.getObjectData(
				BurProductBOMConstant.SUPPLIER_MASTER_IGNORE,
				supplierMasterBean, supplierObject,
				BurProductBOMConstant.SUPPLIER_MASTER_ATT, jsonMapping,
				sysJsonMapping);
		// Return
		return supplierMasterBean;
	}
	// BURBERRY-1485 New Attributes Additions post Sprint 8: End

	/**
	 * @param bomPart
	 * @param bomType
	 * @return
	 * @throws WTException
	 */
	public static Map getChildMap(FlexBOMPart bomPart, FlexType bomType)
			throws WTException {

		String methodName = "getChildMap";

		if (bomPart == null) {
			return new HashMap();
		}
		Map childMap = new HashMap();
		Collection topLevelBranches = new ArrayList();

		Collection<FlexObject> dataSet = LCSFlexBOMQuery.findFlexBOMData(bomPart, null,
				null, null, null, null, LCSFlexBOMQuery.EFFECTIVE_ONLY, null,
				true, true, LCSFlexBOMQuery.ALL_DIMENSIONS, null, null, null,
				bomType).getResults();

		Iterator dataIter = dataSet.iterator();
		// Get all top level branches
		while (dataIter.hasNext()) {
			FlexObject branch = (FlexObject) dataIter.next();
			if (!FormatHelper.hasContent(branch.getString(DIM_COL))) {
				topLevelBranches.add(branch);
			}
		}

		Iterator topLevelIter = topLevelBranches.iterator();
		// Parse through each top level branch and add list of childs
		// corresponding to it
		while (topLevelIter.hasNext()) {
			FlexObject tlbranch = (FlexObject) topLevelIter.next();
						
			//BURBERRY-1404 : Defect Fix1: Start
			Collection<FlexObject> overrides = new ArrayList<FlexObject>();
			// Top level dimension id
			String tlDimId = tlbranch.getString(DIMID_COL);
			// Top level branch id
			String tlBranch = getBranchFromDimId(tlDimId);
			// Loop through data set
			for (FlexObject branch : dataSet) {
				// get the flex object
				FlexObject obj = (FlexObject) branch;
				// Check dimension name
				if (FormatHelper.hasContent(obj.getString(DIM_COL))) {
					boolean validChildLink = checkValidChildLink(tlBranch, obj);
					logger.debug(methodName + "validChildLink: "
							+ validChildLink);
					if (validChildLink) {
						overrides.add(obj);
					}
				}
			}
			// Add top branch and its childs links
			childMap.put(tlbranch, overrides);
	        //BURBERRY-1404 : Defect Fix1: End
		}
		logger.debug(methodName + "Returing ChildMap bean " + childMap);
		return childMap;
	}
	
	//BURBERRY-1404 : Defect Fix1: Start
	
	/**
	 * Method to check valid child object
	 * 
	 * @param tlBranch
	 *            String
	 * @param obj
	 *            FlexObject
	 * @return boolean
	 */
	private static boolean checkValidChildLink(String tlBranch, FlexObject obj) {
		//Method Name
		String methodName = "checkValidChildLink() ";
		boolean validObj = false;
		// Get dimension id
		String orDimId = obj.getString(DIMID_COL);
		logger.debug(methodName + "orDimId: " + orDimId);
		// orDimId = orDimId.substring(0, orDimId.indexOf("-"))
		if (FormatHelper.hasContent(orDimId)) {
			// Get branch id
			String orBranch = getBranchFromDimId(orDimId);
			logger.debug(methodName + "orBranch: " + orBranch);
			if (tlBranch.equals(orBranch)) {
				validObj = true;
			}
		}
		return validObj;
	}
	
	/**
	 * Method to get the branchid from Dimension Id
	 * 
	 * @param dimId
	 *            String
	 * @return String
	 */
	public static String getBranchFromDimId(String dimId) {
		// tlDimId:
		// -PARENT:com.lcs.wc.flexbom.FlexBOMPartMaster:165797-REV:A-BRANCH:1
		// orDimId:
		// -PARENT:com.lcs.wc.flexbom.FlexBOMPartMaster:165797-REV:A-BRANCH:15-SKU:com.lcs.wc.part.LCSPartMaster:165714
		String strBranchColon = "BRANCH:";
		String strHyphen="-";
		if (dimId.indexOf(strBranchColon) < 0){
			return "";
		}

		String stub = dimId.substring(dimId.indexOf(strBranchColon) + strBranchColon.length());
		if (stub.indexOf(strHyphen) > -1) {
			stub = stub.substring(0, stub.indexOf(strHyphen));
		}
		return stub.trim();
	}
	// BURBERRY-1404 : Defect Fix1: End

}

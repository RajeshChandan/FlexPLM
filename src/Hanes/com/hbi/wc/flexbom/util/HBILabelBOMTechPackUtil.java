package com.hbi.wc.flexbom.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFindFlexBOMHelper;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBILabelBOMTechPackUtil.java
 * 
 * This class contains functions for extracting data from Sales BOM sections
 * ,those are 'Components' and 'Packing BOM Instructions' for SP Automation to
 * SAP system, data extracting is specific to the SAP system, And constructing
 * SOAP message for maintenance BOM which will be classified by IIB system, and
 * will be passed to the SAP system.
 * 
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since Nov-30-2018
 */
public class HBILabelBOMTechPackUtil {
	private static Logger log = Logger.getLogger(HBILabelBOMTechPackUtil.class);
	private static String LABEL_BOM_TYPE = LCSProperties.get("hbi.gp.labelbom.type", "BOM\\Materials\\HBI\\Label");
	private static String BASIC_CUT_AND_SEW_GARMENT = LCSProperties.get("hbi.gp.product.type",
			"Product\\BASIC CUT & SEW - GARMENT");
	private static String applicationKey = LCSProperties.get("com.hbi.wc.flexbom.gen.HBILabelPDFContent.applicationKey",
			"hbiApplication");
	private static String garmentSizeKey = LCSProperties.get("com.hbi.wc.flexbom.gen.HBILabelPDFContent.garmentSizeKey",
			"hbiGarmentSize");
	private static final String GP_ROUTING_BOM = LCSProperties.get("hbi.gp.routingbom.type");

	/**
	 * This function is using to validate and return a collection of FlexObject,
	 * validation rules are defined as per the business requirement for Labels
	 * 
	 * @param labelBOMData
	 *            - Collection<FlexObject>
	 * @param countryMap
	 *            - Map<String, String>
	 * @param sizeMap
	 *            - Map<String, String>
	 * @return labelBOMDataColl - Collection<FlexObject>
	 * @throws WTException
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<FlexObject> processFlexObjectForGarLabelBOM(Collection<FlexObject> labelBOMdata, Map params)
			throws WTException {
		log.debug(
				"### START HBILabelBOMTechPackUtil.getLabelBOMProductLevelData(Collection<FlexObject> labelBOMData, countryMap, sizeMap) ###");
		LCSMaterial placeholderMaterialObj = (LCSMaterial) VersionHelper
				.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
		Collection<FlexObject> labelBOMDataColl = new ArrayList<FlexObject>();
		FlexBOMLink flexBOMLinkObj = null;
		LCSMaterial materialObj = null;
		boolean bomLineItemValidationStatus = false;

		LCSPartMaster prodMasterObj = (LCSPartMaster) LCSQuery.findObjectById((String) params.get("PRODUCT_MASTER_ID"));
		LCSProduct garmentProductObj = (LCSProduct) VersionHelper.latestIterationOf(prodMasterObj);
		garmentProductObj = SeasonProductLocator.getProductARev(garmentProductObj);
		log.debug("--------------- garmentProductObj-----------" + garmentProductObj.getName());
		Map countryMap = new HashMap();
		WTObject specObj = (WTObject) LCSProductQuery.findObjectById((String) params.get("SPEC_ID"));
		if (specObj != null) {
			FlexSpecification spec = (FlexSpecification) specObj;
			countryMap = new HBILabelBOMTechPackUtil().getCountryCode(spec);
		}

		// Map countryMap = new
		// HBILabelBOMTechPackUtil().getCountryCode(garmentProductObj);
		Map sizeMap = new HBILabelBOMTechPackUtil().getSizes(garmentProductObj);

		for (FlexObject flexObj : labelBOMdata) {
			if (FormatHelper.hasContent(flexObj.getString("FLEXBOMLINK.MASTERBRANCHID"))) {
				if (!countryMap.isEmpty() ) {
					flexBOMLinkObj = (FlexBOMLink) LCSQuery.findObjectById(
							"OR:com.lcs.wc.flexbom.FlexBOMLink:" + flexObj.getString("FlexBOMLink.IDA2A2"));
					materialObj = (LCSMaterial) VersionHelper
							.latestIterationOf((wt.vc.Mastered) flexBOMLinkObj.getChild());

					if (materialObj != null && materialObj != placeholderMaterialObj) {
						log.debug("---------------UTIL materialObj-----------" + materialObj.getName());
						bomLineItemValidationStatus = getApplicationValidationStatus(materialObj, countryMap, sizeMap);
						if (bomLineItemValidationStatus) {
							flexObj = addLabelMaterialToFlexObj(materialObj, flexObj);
							labelBOMDataColl.add(flexObj);
						}
					}
				}
			} else {
				labelBOMDataColl.add(flexObj);
			}
		}
		log.debug(
				"### END HBILabelBOMTechPackUtil.getLabelBOMProductLevelData(Collection<FlexObject> labelBOMData, countryMap, sizeMap) ###");
		return labelBOMDataColl;
	}

	/**
	 * This function is using to validate the material application status,
	 * material country, garment size value, validate data return validation
	 * status
	 * 
	 * @param materialObj
	 *            - LCSMaterial
	 * @param countryMap
	 *            - Map<String, String>
	 * @param sizeMap
	 *            - Map<String, String>
	 * @return true/false - boolean
	 * @throws WTException
	 */
	public boolean getApplicationValidationStatus(LCSMaterial materialObj, Map<String, String> countryMap,
			Map<String, String> sizeMap) throws WTException {
		log.debug(
				"### START HBILabelBOMTechPackUtil.getApplicationValidationStatus(LCSMaterial materialObj, countryMap, sizeMap) ###");
		boolean bomLineItemValidationStatus = false;
		String applicationValue = "" + (String) materialObj.getValue(applicationKey);
		String countryValue = "" + getCountryFromMaterial(materialObj);
		String garmentsizeValue = "" + (String) materialObj.getValue(garmentSizeKey);
		
		if ("sewin".equalsIgnoreCase(applicationValue) || "heatSeal".equalsIgnoreCase(applicationValue)
				|| "heatTransfer".equalsIgnoreCase(applicationValue)) {
			if (getHeatTransferValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap)) {
				bomLineItemValidationStatus = true;
			}
		} else if ("padPrint".equalsIgnoreCase(applicationValue)) {
			if (getPadPrintValidationStatus(countryValue, countryMap)) {
				bomLineItemValidationStatus = true;
			}
		} else if ("joker".equalsIgnoreCase(applicationValue)) {
			if (getJokerValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap)) {
				bomLineItemValidationStatus = true;
			}
		}
		
		log.debug(
				"### END HBILabelBOMTechPackUtil.getApplicationValidationStatus(LCSMaterial materialObj, countryMap, sizeMap) ###");
		return bomLineItemValidationStatus;
	}

	/**
	 * This function is using to validate material level country value,
	 * garmentSize value, based on validations, returning boolean (true/false)
	 * status
	 * 
	 * @param countryValue
	 *            - String
	 * @param garmentsizeValue
	 *            - String
	 * @param countryMap
	 *            - Map<String, String>
	 * @param sizeMap
	 *            - Map<String, String>
	 * @return heatTransferValidationStatus - boolean
	 * @throws WTException
	 */
	private boolean getHeatTransferValidationStatus(String countryValue, String garmentsizeValue,
			Map<String, String> countryMap, Map<String, String> sizeMap) throws WTException {
		log.debug(
				"### START HBILabelBOMTechPackUtil.getHeatTransferValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap) ###");
		boolean heatTransferValidationStatus = false;
		if(!sizeMap.isEmpty()) {
			if (countryMap.containsKey(countryValue) && sizeMap.containsKey(garmentsizeValue)) {
				heatTransferValidationStatus = true;
			}
		}
		

		log.debug(
				"### END HBILabelBOMTechPackUtil.getHeatTransferValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap) ###");
		return heatTransferValidationStatus;
	}

	/**
	 * This function is using to validate material level country value,
	 * garmentSize value, based on validations, returning boolean (true/false)
	 * status
	 * 
	 * @param countryValue
	 *            - String
	 * @param garmentsizeValue
	 *            - String
	 * @param countryMap
	 *            - Map<String, String>
	 * @param sizeMap
	 *            - Map<String, String>
	 * @return heatTransferValidationStatus - boolean
	 * @throws WTException
	 */
	private boolean getPadPrintValidationStatus(String countryValue,
			Map<String, String> countryMap) throws WTException {
		log.debug(
				"### START HBILabelBOMTechPackUtil.getHeatTransferValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap) ###");
		boolean heatTransferValidationStatus = false;
	
		if (countryMap.containsKey(countryValue)) {
			heatTransferValidationStatus = true;
		}
		log.debug(
				"### END HBILabelBOMTechPackUtil.getHeatTransferValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap) ###");
		return heatTransferValidationStatus;
	}

	/**
	 * @param countryValue
	 * @param garmentsizeValue
	 * @param countryMap
	 * @param sizeMap
	 * @return
	 * @throws WTException
	 */
	private boolean getJokerValidationStatus(String countryValue, String garmentsizeValue,
			Map<String, String> countryMap, Map<String, String> sizeMap) throws WTException {
		log.debug(
				"### START HBILabelBOMTechPackUtil.getJokerValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap) ###");
		boolean jokerValidationStatus = false;
		if(!sizeMap.isEmpty()) {
			if (countryMap.containsKey(countryValue) && sizeMap.containsKey(garmentsizeValue)) {
				jokerValidationStatus = true;
			} else if (!FormatHelper.hasContent(countryValue) && !FormatHelper.hasContent(garmentsizeValue)) {
				jokerValidationStatus = true;
			}
		}

		log.debug(
				"### END HBILabelBOMTechPackUtil.getJokerValidationStatus(countryValue, garmentsizeValue, countryMap, sizeMap) ###");
		return jokerValidationStatus;
	}

	/**
	 * This function is for establishing Seasonal Product object based on ID
	 * received from the IIB Queue.
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public String getCountryFromMaterial(LCSMaterial materialObj) throws WTException {
		String countryValue = (String) materialObj.getValue("hbiLabelCountry");
		if (FormatHelper.hasContent(countryValue)) {
			countryValue = materialObj.getFlexType().getAttribute("hbiLabelCountry").getAttValueList()
					.getValue(countryValue, null);
			countryValue = countryValue.substring(countryValue.length() - 3);
			countryValue = countryValue.substring(0, 2);
			log.debug(" ----------country-------------" + countryValue);
		}
		return countryValue;
	}

	/**
	 * This function is using to print only Label BOM for Garment Product.
	 * 
	 * @param bomPart
	 *            - FlexBOMPart
	 * @return bomTachPackStatus - boolean
	 * @throws WTException
	 */
	public boolean generateBOMTechpackStatus(LCSProduct garmentProdObj, FlexBOMPart bomPart) throws WTException {
		boolean bomTachPackStatus = false;

		String bomPartFlexTypePath = bomPart.getFlexType().getFullName(true);
		String productFlexTypePath = garmentProdObj.getFlexType().getFullName(true);
		log.debug(" <<<<<< validation Label BOM >>>>>>>" + LABEL_BOM_TYPE.equalsIgnoreCase(bomPartFlexTypePath));
		if (BASIC_CUT_AND_SEW_GARMENT.equalsIgnoreCase(productFlexTypePath)
				&& LABEL_BOM_TYPE.equalsIgnoreCase(bomPartFlexTypePath)) {
			log.debug(" <<<<<< INSIDE VALIDATION >>>>>>>" + bomPart.getName());
			bomTachPackStatus = true;
		}

		return bomTachPackStatus;
	}

	/**
	 * This function is for establishing Seasonal Product object based on ID
	 * recevied from the IIB Queue.
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public Map<String, Object> getCareCodeFromSourceToSea(@SuppressWarnings("rawtypes") Map params,
			FlexSpecification specObj) throws WTException {
		Map<String, Object> careCodeDataMap = new HashMap<String, Object>();
		LCSSeason seasonObj = null;
		LCSSourceToSeasonLink sourceToseasonObj = null;
		LCSLifecycleManaged careCodeObj = null;
		String careCode = "";
		String careCodeDescription = "";
		LCSSeasonMaster seasonMasterObj = null;

		LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper
				.latestIterationOf((LCSSourcingConfigMaster) specObj.getSpecSource());
		log.debug("----------sourcingConfig -------------------" + sourcingConfig.getIdentity());

		if (FormatHelper.hasContent((String) params.get("SEASONMASTER_ID"))) {
			seasonMasterObj = (LCSSeasonMaster) LCSQuery.findObjectById((String) params.get("SEASONMASTER_ID"));
			if (seasonMasterObj != null) {
				seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonMasterObj);
				log.debug("----------seasonObj -------------------" + seasonObj.getIdentity());

				sourceToseasonObj = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourcingConfig, seasonObj);
				if (sourceToseasonObj != null) {
					careCodeObj = (LCSLifecycleManaged) sourceToseasonObj.getValue("hbiCareCode");
					if (careCodeObj != null) {
						careCode = (String) careCodeObj.getValue("name");
						if (!FormatHelper.hasContent(careCode)) {
							careCode = "";
						}
						careCodeDescription = (String) careCodeObj.getValue("hbiCareInstructions");
						if (!FormatHelper.hasContent(careCodeDescription)) {
							careCodeDescription = "";
						}
					}
				}
			}
		}
		careCodeDataMap.put("name", careCode);
		careCodeDataMap.put("hbiObjectDescription", careCodeDescription);

		return careCodeDataMap;
	}

	/**
	 * This function is for establishing Seasonal Product object based on ID
	 * recevied from the IIB Queue.
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public Map getCountryCode(FlexSpecification spec) {
		Map countries = new HashMap();
		Collection productBOMs = new ArrayList();
		boolean USE_MULTILEVEL = LCSProperties.getBoolean("jsp.flexbom.ViewBOM.useMultilevel");
		try {
			productBOMs = (ArrayList<FlexBOMPart>) FlexSpecQuery.getSpecComponents(spec, "BOM");
			// productBOMs = new LCSFlexBOMQuery().findBOMPartsForOwner(spec,
			// "A", "MAIN", null);
			Iterator itr = productBOMs.iterator();
			while (itr.hasNext()) {
				FlexBOMPart bompart = (FlexBOMPart) itr.next();
				Collection bomData = new ArrayList();
				FlexType materialType = bompart.getFlexType().getReferencedFlexType("MATERIAL_TYPE_ID");
				String timestamp = FormatHelper
						.formatWithDateTimeFormat(wt.fc.PersistenceHelper.getModifyStamp(bompart));

				String parttype = bompart.getFlexType().getFullName(true);
				if (GP_ROUTING_BOM.equals(parttype)) {
					Collection flexBOMLinksFromDBnew = LCSFindFlexBOMHelper.findBOM(bompart, null, null, null, null,
							null, LCSFlexBOMQuery.ALL_SKUS, timestamp, new Boolean(USE_MULTILEVEL), materialType, null);
					Iterator itr1 = flexBOMLinksFromDBnew.iterator();
					while (itr1.hasNext()) {
						FlexObject fob = (FlexObject) itr1.next();
						FlexBOMLink parentbomlink = (FlexBOMLink) LCSQuery
								.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + fob.getData("FLEXBOMLINKID"));
						LCSSupplier supp = (LCSSupplier) parentbomlink.getValue("hbiRoutingSew");
						if (supp != null) {
							LCSCountry cntry = (LCSCountry) supp.getValue("hbiCountry");
							if (cntry != null) {
								countries.put(cntry.getName(), cntry.getName());
							}
						}
					}
				}
			}
		} catch (Exception exe) {
			log.debug(" Label BOM exception:: " + exe);
		}
		log.debug("------countries---------------" + countries);
		return countries;
	}

	/**
	 * This function is for establishing Seasonal Product object based on ID
	 * recevied from the IIB Queue.
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getSizes(LCSProduct product) throws WTException {
		Map sizes = new HashMap();
		LCSMOATable moatable = (LCSMOATable) product.getValue("hbiGarmentSizeTable");
		Collection moaColl = (Collection) moatable.getRows();
		Iterator itr = moaColl.iterator();
		while (itr.hasNext()) {
			FlexObject flexObj = (FlexObject) itr.next();
			LCSMOAObject moa = (LCSMOAObject) LCSQuery
					.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + flexObj.getData("OID"));
			sizes.put(moa.getValue("hbiGarmentSize"), moa.getValue("hbiGarmentSize"));
		}
		log.debug("sizes------------------------------" + sizes);

		return sizes;
	}

	public FlexObject addLabelMaterialToFlexObj(LCSMaterial materialObj, FlexObject flexObject) throws WTException {
		String countryCode = "";
		String application = (String) materialObj.getValue("hbiApplication");
		log.debug("---------------application-----------" + application);

		String labelType = (String) materialObj.getValue("hbiMatLabelType");
		log.debug("---------------labelType-----------" + labelType);

		String garmentSize = (String) materialObj.getValue("hbiGarmentSize");
		log.debug("---------------hbiGarmentSize-----------" + garmentSize);

		String padPrintInkColors = (String) materialObj.getValue("hbiPadPrintInkColors");
		log.debug("---------------padPrintInkColors-----------" + padPrintInkColors);

		LCSCountry countryObj = (LCSCountry) materialObj.getValue("hbiCountryCode");
		if (countryObj != null)
			countryCode = (String) countryObj.getName();
		log.debug("---------------countryCode-----------" + countryCode);

		flexObject.put("LCSMATERIAL.ATT48", application);
		flexObject.put("LCSMATERIAL.ATT55", labelType);
		flexObject.put("LCSMATERIAL.ATT49", garmentSize);
		flexObject.put("LCSMATERIAL.ATT56", padPrintInkColors);
		flexObject.put("LCSMATERIAL.NUM14", countryCode);
		return flexObject;
	}

}
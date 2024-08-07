package com.sportmaster.wc.interfaces.webservices.outbound.material.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.helper.SMOutboundIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMMaterialHleper.java This class is using to call the methods defined in
 * helper class. for Integration.
 * 
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialHleper {
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialHleper.class);

	/**
	 * Setting Fields value.
	 * 
	 * @param obj - Object
	 * @param bean - SMMaterialBean
	 */
	public void setMaterialFieldVlaues(Object obj, SMMaterialBean bean) {
		
		String type;
		String attrr;
		String value;
		
		bean.setLastUpdated(bean.getMaterial().getModifyTimestamp());
		bean.setLastUpdatedBy(bean.getMaterial().getModifierFullName());
		if (FormatHelper.hasContent(bean.getMaterial().getModifierEMail())) {
			bean.setLastUpdatedBy(bean.getMaterial().getModifierEMail());

		}
		bean.setLifecycleState(bean.getMaterial().getLifeCycleState().getDisplay());
		bean.setCreatedBy(bean.getMaterial().getCreatorFullName());
		if (FormatHelper.hasContent(bean.getMaterial().getCreatorEMail())) {
			bean.setCreatedBy(bean.getMaterial().getCreatorEMail());

		}

		bean.setCreatedOn(bean.getMaterial().getCreateTimestamp());
		LOGGER.debug("Setting Material fields..");
		// calling set method to set fields in request object
		for (int i = 0; i < bean.getAttrKeyList().size(); i++) {
			attrr = (String) bean.getAttrKeyList().get(i);
			try {
				value = String.valueOf(bean.getMaterial().getValue(attrr));
				// getting value from material
				value = String.valueOf(SMOutboundIntegrationHelper.getAttributeValues(bean.getMaterial(), null, attrr,
						String.valueOf(value)));
				// setting field values
				bean.getPluginHelper().set(obj, (String) bean.getAttrMappedElementList().get(i), value);
				
			} catch (WTException wtExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
			} catch (WTPropertyVetoException wtProEx) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtProEx);
			}
		}
		if (validateData(obj, "Material : " + bean.getMaterial().getName(), bean,
				bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_REQUIRED_MATERIAL_ELEMENTS"))) {
			LOGGER.debug("some required fields are missing, skipping material from sending  : "
					+ bean.getMaterial().getName());
			return;
		}
		// setting values for type zipper or knit
		if (bean.isKintZipper() || bean.isTrimsOtherType()) {
			type = bean.getPluginHelper()
					.getLogEntryType(bean.getMaterial().getFlexType().getFullNameDisplay(), false, bean.getConstant())
					.toUpperCase();
			LOGGER.debug("Setting field values for : " + type);
			setFields(obj, bean.getPluginHelper().getField(bean.getConstant(), type + "_ATTRIBUTES"),
					bean.getPluginHelper().getField(bean.getConstant(), type + "_MAPPED_ELEMENTS"), false, true, bean);
		}
		// setting values for created on , created by, updated on , update by
		setOtherDetails(obj, bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_OTHER_FIELDS"),
				bean);
	}

	/**
	 * Setting others Fields value
	 * 
	 * @param obj
	 *            --Object
	 * @param mappedEleemts
	 *            --String
	 * @param bean
	 *            --SMMaterialBean
	 */
	public void setOtherDetails(Object obj, String mappedEleemts,
			SMMaterialBean bean) {

		String eleement;
		String methodNmae;
		Object eleementValue;
		List<String> elemets = FormatHelper.commaSeparatedListToList(mappedEleemts);
		
		for (int i = 0; i < elemets.size(); i++) {
			eleement = elemets.get(i);
			methodNmae = "get" + eleement.substring(0, 1).toUpperCase() + eleement.substring(1);
			eleementValue = bean.getPluginHelper().getValueByInvokingMethod(methodNmae, bean, null);
			// setting other fields to request object
			bean.getPluginHelper().set(obj, eleement, eleementValue);

		}
	}

	/**
	 * Setting Material Supplier details.
	 * @param obj --Object
	 * @param bean--SMMaterialBean
	 */
	public void setMaterialSupplierFileds(Object obj, SMMaterialBean bean) {
		
		LCSSupplier spplr;
		Object matSuplrBean;
		LCSMOATable moa = null;
		LCSMaterialSupplier mtrlSplr;
		LCSMaterialSupplierMaster matSupplrMstr;
		List<Object> matSplrList = new ArrayList<>();
		LOGGER.debug("Setting Material-Supplier/Supplier fields");
		
		try {
			SearchResults result = LCSMaterialSupplierQuery.findMaterialSuppliers(bean.getMaterial());
		
			@SuppressWarnings("unchecked")
			Collection<FlexObject> coll = result.getResults();
			
			for (FlexObject fo : coll) {
				matSupplrMstr = (LCSMaterialSupplierMaster) LCSMaterialSupplierQuery
						.findObjectById("com.lcs.wc.material.LCSMaterialSupplierMaster:"
								+ fo.getString("LCSMATERIALSUPPLIERMASTER.IDA2A2"));
				// getting material supplier from material
				mtrlSplr = (LCSMaterialSupplier) VersionHelper.latestIterationOf(matSupplrMstr);
				bean.setMaterialSupplier(mtrlSplr);
				bean.setLastUpdated(mtrlSplr.getModifyTimestamp());
				bean.setLastUpdatedBy(mtrlSplr.getModifierFullName());

				if (FormatHelper.hasContent(mtrlSplr.getModifierEMail())) {
					bean.setLastUpdatedBy((mtrlSplr.getModifierEMail()));

				}

				// getting supplier from material suupler
				spplr = (LCSSupplier) VersionHelper.latestIterationOf(mtrlSplr.getSupplierMaster());
				bean.setSupplier(spplr);
				matSuplrBean = bean.getPluginHelper().getObject(SMOutboundWebServiceConstants.COMMON_BEAN_PACKAGE,
						bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_MAT_SUP_BEAN_CLASS"));
				LOGGER.debug("Setting fields for material Supplier " + mtrlSplr.getName());
				// setting material supplier fields
				setFields(matSuplrBean,
						bean.getPluginHelper().getField(bean.getConstant(),
								bean.getType() + "_MATERIAL_SUPPLIER_ATTRIBUTES"),
						bean.getPluginHelper().getField(bean.getConstant(),
								bean.getType() + "_MATERIAL_SUPPLER_MAPPED_ELEMENTS"),
						false, false, bean);
				LOGGER.debug("Setting fields for supplier " + spplr.getName());
				// setting supplier fields
				setFields(matSuplrBean,
						bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_SUPPLIER_ATTRIBUTES"),
						bean.getPluginHelper().getField(bean.getConstant(),
								bean.getType() + "_SUPPLER_MAPPED_ELEMENTS"),
						true, false, bean);
				if (validateData(matSuplrBean, "Material Supplier : " + mtrlSplr.getName(), bean, bean.getPluginHelper()
						.getField(bean.getConstant(), bean.getType() + "_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS"))) {
					LOGGER.debug("some required fields are missing, skipping material supplier from sending  : "
							+ mtrlSplr.getName());
					continue;
				}
				moa = (LCSMOATable) mtrlSplr.getValue(SMOutboundWebServiceConstants.MATERIAL_MOA_KEY);
				// setting pricing datas from moa
				pricing(matSuplrBean, bean, moa);
				// setting values for created on , created by, updated on ,
				// update by
				setOtherDetails(matSuplrBean, bean.getPluginHelper().getField(bean.getConstant(),
						bean.getType() + "_MATERIAL_SUPPLER_OTHER_FIELDS"), bean);
				matSplrList.add(matSuplrBean);
			}
			bean.getPluginHelper().set(obj,
					bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_MAT_SUP_BEAN_ELEMENT"),
					matSplrList);
		} catch (WTException etEx) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, etEx);
		}
	}

	/**
	 * validate data to skip setting data to request object.
	 * 
	 * @param obj - Object
	 * @param mappedEleemts - String
	 * @return boolean
	 */
	public boolean validateData(Object obj,String objName,SMMaterialBean bean, String mappedEleemts) {
		
		String eleement;
		String attrVlaue;
		List<String> reqElemets = FormatHelper.commaSeparatedListToList(mappedEleemts);
		
		// iterating required fields
		for (int i = 0; i < reqElemets.size(); i++) {
			eleement = reqElemets.get(i);
			// getting attribute values
			attrVlaue = String.valueOf(bean.getPluginHelper().getField(obj, eleement));
			// checking field values
			if (!FormatHelper.hasContent(attrVlaue)) {
				LOGGER.debug("attribute value is missing  : " + eleement);
				bean.setSmErrorReason(eleement + " - attribute value is missing for " + objName);
				return true;
			}
		}
		return false;
	}

	/**
	 * setting filed values.
	 * 
	 * @param obj - Object
	 * @param attributes - String
	 * @param MappedEleemts - String
	 * @param supplier - Boolean
	 * @param material - material
	 * @param bean - SMMaterialBean
	 */
	public void setFields(Object obj, String attributes, String mappedEleemts, Boolean supplier, boolean material,
			SMMaterialBean bean) {
		
		String eleement;
		String attribute;
		Object attrVlaue = null;
		List<String> attrList = FormatHelper.commaSeparatedListToList(attributes);
		List<String> elemets = FormatHelper.commaSeparatedListToList(mappedEleemts);
		
		for (int i = 0; i < elemets.size(); i++) {
			eleement = elemets.get(i);
			attribute = attrList.get(i);
			try {
				// setting datas for supplier
				if (supplier) {
					attrVlaue = String.valueOf(bean.getSupplier().getValue(attribute));
					attrVlaue = SMOutboundIntegrationHelper.getAttributeValues(bean.getSupplier(), null, attribute,
							String.valueOf(attrVlaue));
					// setting datas from material fields
				} else if (material) {
					attrVlaue = String.valueOf(bean.getMaterial().getValue(attribute));
					attrVlaue = SMOutboundIntegrationHelper.getAttributeValues(bean.getMaterial(), null, attribute,
							String.valueOf(attrVlaue));
					// setting data for material supplier
				} else {
					attrVlaue = String.valueOf(bean.getMaterialSupplier().getValue(attribute));
					attrVlaue = SMOutboundIntegrationHelper.getAttributeValues(bean.getMaterialSupplier(), null,
							attribute, String.valueOf(attrVlaue));
				}
				bean.getPluginHelper().set(obj, eleement, attrVlaue);
				
			} catch (WTException wtEx) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL,  wtEx);
			} catch (WTPropertyVetoException wtProEx) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtProEx);
			}
		}
	}

	/**
	 * setting pricing data.
	 * 
	 * @param obj - Object
	 * @param bean - SMMaterialBean
	 * @param moa - LCSMOATable
	 */
	public void pricing(Object obj, SMMaterialBean bean, LCSMOATable moa) {
		
		Object pricing;
		String attribute;
		Collection<?> rows;
		FlexObject row = null;
		Object attrVlaue = null;
		FlexTypeAttribute attributeMoa;
		List<Object> pricinbgList = new ArrayList<>();

		List<String> attrList = FormatHelper.commaSeparatedListToList(
				bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_PRICING_ATTRIBUTES"));
		List<String> elemets = FormatHelper.commaSeparatedListToList(
				bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_PRICING_MAPPED_ELEMENTS"));
		try {
			
			// getting rows from MOA list
			rows = moa.getRows();
			for (Object fo : rows) {
				row = (FlexObject) fo;
				pricing = bean.getPluginHelper().getObject(SMOutboundWebServiceConstants.COMMON_BEAN_PACKAGE,
						bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_PRICING_BEAN_CLASS"));
				// iterating rows
				for (int i = 0; i < attrList.size(); i++) {
					attribute = attrList.get(i);
					// getting row data
					attrVlaue = row.getString(attrList.get(i).toUpperCase());
					attributeMoa = FlexTypeCache.getFlexTypeFromPath(
							bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_MAT_MOA_TYPE"))
							.getAttribute(attribute);

					attrVlaue = SMOutboundIntegrationHelper.getAttributeValues(null, attributeMoa, attribute,
							String.valueOf(attrVlaue));
					// setting row data in pricing tag
					bean.getPluginHelper().set(pricing, elemets.get(i), attrVlaue);
				}
				pricinbgList.add(pricing);
			}
			bean.getPluginHelper().set(obj,
					bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_PRICING_BEAN_ELEMENT"),
					pricinbgList);
		} catch (WTException wtEx) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtEx);
		} catch (WTPropertyVetoException wtPrEx) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtPrEx);
		}

	}

	/**
	 * Post service load.
	 * 
	 * @param bean
	 *            ---SMMaterialBean
	 * @param response
	 *            ---Object
	 */
	public void postServiceLoad(SMMaterialBean bean, Object response) {
		
		LOGGER.debug("Running Post Request Load");
		// checking response object
		processMaterialbyResponse(bean, response);
		
		if (bean.isServiceError()) {
			bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.CREATE_PENDING);
			if (bean.isForScheduleQueue()) {
				bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.UPDATE_PENDING);
			}
		}
		
		try {

			// querying log entry data
			Map<String, FlexObject> flexLogEntryData = bean.getUtill().getMaterialLogENtryData(
					bean.getPluginHelper().getLogEntryType(bean.getObjectTye(), true, bean.getConstant()),
					SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_ATTRIBUTES, bean.getSmObjectID(),
					SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_OBJECTID_ATTR);
			// setting key colujm map
			bean.setAttKeyColumnMap(bean.getUtill().getKeyColumnMap());
			// calling method to process log entry data
			LOGGER.debug("Processing Log Entry Object");
			bean.getLogEntryProcessor().processMaterialLogentry(flexLogEntryData, bean);
			
		} catch (WTException wtExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
		}
	}

	/**
	 * processing mdm id for material according to response.
	 * 
	 * @param bean - SMMaterialBean
	 * @param response- Object
	 */
	public void processMaterialbyResponse(SMMaterialBean bean, Object response) {
		
		String status;
		String mdmId = null;
		
		LOGGER.debug("Running Post Request Load for Reponse validation");
		
		// checking response object
		if (response == null) {
			LOGGER.debug("Response object is null");
			return;
		}
		
		// getting status value
		status = bean.getPluginHelper().getField(response, "integrationStatus");
		
		LOGGER.debug("status value received in response : " + status);
		LOGGER.debug("ErrorMessage value received in response : "
				+ bean.getPluginHelper().getField(response, "errorMessage"));
		
		if ("false".equalsIgnoreCase(status)) {
			LOGGER.debug("status is false, Not saving MDMID on Material");
			bean.setTotalFailCount(1);
			bean.setSmErrorReason(bean.getPluginHelper().getField(response, "errorMessage"));
			return;
		}
		// getting mdm id
		mdmId = bean.getPluginHelper().getField(response, "mdmId");
		
		LOGGER.debug("MDM ID value received in response : " + mdmId);
		
		if (FormatHelper.hasContent(mdmId) && !bean.isForScheduleQueue()
				&& !mdmId.equals(SMOutboundWebServiceConstants.FAKE_MDM_ID)) {
			
			LOGGER.debug("Updating MDMID : " + mdmId + " on Material");
			// updating material
			bean.getUtill().updateMaterial(bean.getMaterial(), mdmId);
			bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.CREATE_PROCESSED);
			// setting mdm id in bean
			bean.setSmMDMDIV(mdmId);
			
		} else if (bean.isForScheduleQueue()) {
			// setting success count
			bean.setTotalProcessedCount(1);
			bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.UPDATE_PROCESSED);
		}

	}
}

package com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.BusinessSupplierInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.FactoryInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.MaterialSupplierInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.outbound.helper.SMOutboundIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.client.SMSupplierOutboundDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.helper.SMSupplierHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;

/**
 * 
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierDataProcessing {
	
	private static final String ERROR_OCCURED_STR = "ERROR OCCURED :-";
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierDataProcessing.class);
	/**
	 * constructor.
	 */
	protected SMSupplierDataProcessing(){
		//protected constructor
	}
	
	/**
	 * Set Data to Factory Bean.
	 * @param supplierObj - LCSSupplier
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void setDataToFactoryRequestBean(LCSSupplier supplierObj, FactoryInformationUpdatesRequest factoryRequest)
			throws WTException, WTPropertyVetoException {
		String factoryAttributes = SMOutboundWebServiceConstants.OUTBOUND_FACTORY_ATTRIBUTES;
		try {
			setFactoryAttributes(supplierObj, factoryRequest);
		} catch (DatatypeConfigurationException e1) {
			LOGGER.error(ERROR_OCCURED_STR, e1);
		} catch (ParseException parseExcpt) {
			LOGGER.error(parseExcpt.getLocalizedMessage(), parseExcpt);
		}
		setAttributeValueToSupplierBean(supplierObj, factoryAttributes, SMOutboundWebServiceConstants.FACTORY_BEAN_ATTRIBUTES, factoryRequest);
	}

	/**
	 * Set data to Material Supplier.
	 * @param supplierObj - LCSSupplier
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void setDataToMaterialSupplierRequestBean(
			LCSSupplier supplierObj, MaterialSupplierInformationUpdatesRequest materialSupplierRequest) throws WTException,
			WTPropertyVetoException {
		String materialSupplierAttributes = SMOutboundWebServiceConstants.OUTBOUND_MATERIAL_SUPPLIER_ATTRIBUTES;
		try {
			setMaterialSupplierAttributes(supplierObj, materialSupplierRequest);
		} catch (DatatypeConfigurationException e2) {
			LOGGER.error(ERROR_OCCURED_STR, e2);
		} catch (ParseException pExpt) {
			LOGGER.error(ERROR_OCCURED_STR, pExpt);
		}
		setAttributeValueToSupplierBean(supplierObj, materialSupplierAttributes, SMOutboundWebServiceConstants.MATERIAL_SUPPLIER_BEAN_ATTRIBUTES, materialSupplierRequest);
	}

	/**
	 * Set data to business Supplier bean.
	 * @param supplierObj - LCSSupplier
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void setDataToBusinessSupplierRequestBean(
			LCSSupplier supplierObj, BusinessSupplierInformationUpdatesRequest businessSupplierRequest) throws WTException,
			WTPropertyVetoException {
		String businessSupplierAttributes = SMOutboundWebServiceConstants.OUTBOUND_BUSINESS_SUPPLIER_ATTRIBUTES;
		try {
			setBusinessSupplierAttributes(supplierObj, businessSupplierRequest);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error(ERROR_OCCURED_STR, e);
		} catch (ParseException pExp) {
			LOGGER.error(ERROR_OCCURED_STR, pExp);
		}
		setAttributeValueToSupplierBean(supplierObj, businessSupplierAttributes, SMOutboundWebServiceConstants.BUSINESS_SUPPLIER_BEAN_ATTRIBUTES, businessSupplierRequest);
	}
	
	/**
	 * Set Factory attributes.
	 * @param supplierObj - LCSSupplier
	 * @param factoryRequest - FactoryInformationUpdatesRequest
	 * @throws DatatypeConfigurationException - DatatypeConfigurationException
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTException
	 * @throws ParseException - ParseException
	 */
	private static void setFactoryAttributes(LCSSupplier supplierObj,
			FactoryInformationUpdatesRequest factoryRequest) throws DatatypeConfigurationException, WTException, WTPropertyVetoException, ParseException {
		factoryRequest.setRequestId(SMSupplierOutboundDataRequestClient.getSupplierRequestID());
		factoryRequest.setObjectType(supplierObj.getFlexType().getFullName(true));
		factoryRequest.setLifeCycleState(supplierObj.getLifeCycleState().toString());
		if(SMSupplierUtil.getSupplierContactInformationFromMOA(supplierObj) != null){
			factoryRequest.setSupplierContact(SMSupplierUtil.getSupplierContactInformationFromMOA(supplierObj));
		}
		factoryRequest.getSupplierMdmId().addAll(SMSupplierUtil.setBusinessSupplierMDMIDForFactory().getMdmId());
		factoryRequest.setCountry(SMSupplierUtil.getCountryMDMIDFromSupplier(supplierObj));
		factoryRequest.setCreatedBy(getSupplierCreator(supplierObj));
		factoryRequest.setAgent(FormatHelper.parseBoolean(String.valueOf(supplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_AGENT_KEY))));
		factoryRequest.setCreatedOn(SMSupplierHelper.getXMLGregorianCalendarFormat(supplierObj.getCreateTimestamp()));
		factoryRequest.setLastUpdatedBy(getSupplierModifier(supplierObj));
		factoryRequest.setLastUpdated(SMSupplierHelper.getXMLGregorianCalendarFormat(supplierObj.getModifyTimestamp()));
		// Phase 14 - EMP-449 - Start
		String supplierPrefix = SMSupplierUtil.getSupplierPrefix(supplierObj);
		if(FormatHelper.hasContent(supplierPrefix)){
			factoryRequest.setSmSupplierPrefix(supplierPrefix);
		}
		// Phase 14 - EMP-449 - End
	}

	/**
	 * Set Material Attributes.
	 * @param supplierObj - LCSSupplier
	 * @param materialSupplierRequest - MaterialSupplierInformationUpdatesRequest
	 * @throws DatatypeConfigurationException - DatatypeConfigurationException
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws ParseException 
	 */
	private static void setMaterialSupplierAttributes(LCSSupplier supplierObj,
			MaterialSupplierInformationUpdatesRequest materialSupplierRequest) throws DatatypeConfigurationException, WTException, WTPropertyVetoException, ParseException {
		materialSupplierRequest.setRequestId(SMSupplierOutboundDataRequestClient.getSupplierRequestID());
		materialSupplierRequest.setObjectType(supplierObj.getFlexType().getFullName(true));
		materialSupplierRequest.setLifeCycleState(supplierObj.getLifeCycleState().toString());
		if(SMSupplierUtil.getSupplierContactInformationFromMOA(supplierObj) != null){
			materialSupplierRequest.setSupplierContact(SMSupplierUtil.getSupplierContactInformationFromMOA(supplierObj));
		}
		if(SMSupplierUtil.getMDMIDFromMOA(supplierObj) != null){
			materialSupplierRequest.setFactoryMdmId(SMSupplierUtil.getMDMIDFromMOA(supplierObj));
		}
		materialSupplierRequest.setCountry(SMSupplierUtil.getCountryMDMIDFromSupplier(supplierObj));
		materialSupplierRequest.setCreatedBy(getSupplierCreator(supplierObj));
		materialSupplierRequest.setAgent((FormatHelper.parseBoolean(String.valueOf(supplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_AGENT_KEY)))));
		materialSupplierRequest.setCreatedOn(SMSupplierHelper.getXMLGregorianCalendarFormat(supplierObj.getCreateTimestamp()));
		materialSupplierRequest.setLastUpdatedBy(getSupplierModifier(supplierObj));
		materialSupplierRequest.setLastUpdated(SMSupplierHelper.getXMLGregorianCalendarFormat(supplierObj.getModifyTimestamp()));
		// Phase 14 - EMP-449 - Start
		String supplierPrefix = SMSupplierUtil.getSupplierPrefix(supplierObj);
		if(FormatHelper.hasContent(supplierPrefix)){
			materialSupplierRequest.setSmSupplierPrefix(supplierPrefix);
		}
		// Phase 14 - EMP-449 - End
	}

	/**
	 * Set Business Supplier Attributes.
	 * @param supplierObj - LCSSupplier
	 * @param businessSupplierRequest - BusinessSupplierInformationUpdatesRequest
	 * @throws DatatypeConfigurationException - DatatypeConfigurationException
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws ParseException 
	 */
	private static void setBusinessSupplierAttributes(LCSSupplier supplierObj,
			BusinessSupplierInformationUpdatesRequest businessSupplierRequest) throws DatatypeConfigurationException, WTException, WTPropertyVetoException, ParseException {
		businessSupplierRequest.setRequestId(SMSupplierOutboundDataRequestClient.getSupplierRequestID());
		businessSupplierRequest.setObjectType(supplierObj.getFlexType().getFullNameDisplay(true));
		businessSupplierRequest.setLifeCycleState(supplierObj.getLifeCycleState().toString());
		if(SMSupplierUtil.getSupplierContactInformationFromMOA(supplierObj) != null){
			businessSupplierRequest.getSupplierContact().add(SMSupplierUtil.getSupplierContactInformationFromMOA(supplierObj));
		}
		if(SMSupplierUtil.getMDMIDFromMOA(supplierObj) != null){
			businessSupplierRequest.setFactoryMdmId(SMSupplierUtil.getMDMIDFromMOA(supplierObj));
		}
		businessSupplierRequest.setCountry(SMSupplierUtil.getCountryMDMIDFromSupplier(supplierObj));
		businessSupplierRequest.setCreatedBy(getSupplierCreator(supplierObj));
		businessSupplierRequest.setAgent((FormatHelper.parseBoolean(String.valueOf(supplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_AGENT_KEY)))));
		businessSupplierRequest.setCreatedOn(SMSupplierHelper.getXMLGregorianCalendarFormat(supplierObj.getCreateTimestamp()));
		businessSupplierRequest.setLastUpdatedBy(getSupplierModifier(supplierObj));
		businessSupplierRequest.setLastUpdated(SMSupplierHelper.getXMLGregorianCalendarFormat(supplierObj.getModifyTimestamp()));
		// Phase 14 - EMP-449 - Start
		String supplierPrefix = SMSupplierUtil.getSupplierPrefix(supplierObj);
		if(FormatHelper.hasContent(supplierPrefix)){
			businessSupplierRequest.setSmSupplierPrefix(supplierPrefix);
		}
		// Phase 14 - EMP-449 - End
	}

	/**
	 * Set value to Supplier Bean.
	 * @param supplierObj - LCSSupplier
	 * @param suppAttributes - String
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void setAttributeValueToSupplierBean(LCSSupplier supplierObj,
			String suppAttributes, String beanEntries, Object obj) throws WTException,
			WTPropertyVetoException {
		LOGGER.debug("Inside setAttributeValueToBusinessSupplierBean  !!!!!!!!!!!!!");
		List<?> supplierAttributePLMKey=(List<?>) FormatHelper.commaSeparatedListToCollection(suppAttributes);
		LOGGER.debug("PLM Key List Size  >>>>>>>>>>>>>>>>>>>  "+supplierAttributePLMKey.size());
		List<?> supplierAttributeBeanKey=(List<?>) FormatHelper.commaSeparatedListToCollection(beanEntries);
		LOGGER.debug("Bean Entries List Size  >>>>>>>>>>>>>>>>>>>>>>  "+supplierAttributeBeanKey.size());
		for(int i=0;i<supplierAttributePLMKey.size();i++){
			String supplierPLMAttr=(String) supplierAttributePLMKey.get(i);
			String supplierBeanAttr=(String) supplierAttributeBeanKey.get(i);
			LOGGER.debug("attr -- bean key >>"+supplierPLMAttr+"----"+supplierBeanAttr);
			try {
				String suppPLMValue=String.valueOf( supplierObj.getValue(supplierPLMAttr));
				suppPLMValue=String.valueOf(SMOutboundIntegrationHelper.getAttributeValues(supplierObj,null, supplierPLMAttr, suppPLMValue));
				LOGGER.debug("attr -- bean key >>"+supplierPLMAttr+"----"+supplierBeanAttr+"-----"+suppPLMValue);
				SMSupplierHelper.setSupplierBean(obj, supplierBeanAttr, suppPLMValue);
			} catch (WTException e) {
				LOGGER.error(ERROR_OCCURED_STR, e);
			}
		}
	}
	
	/**
	 * Set Factory Data.
	 * @param entry - Map<String, LCSSupplier>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws IOException - IOException
	 */
	public static void setFactoryData(Map.Entry<String, LCSSupplier> entry)
			throws WTException, WTPropertyVetoException, IOException {
		Map<String, String> mapToCreateLogEntry = new HashMap<>();
		mapToCreateLogEntry.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(entry.getValue()), SMOutboundWebServiceConstants.SUPPLIER_QUEUE_REQUEST);
		FactoryInformationUpdatesRequest factoryBean = new FactoryInformationUpdatesRequest();
		SMSupplierOutboundDataRequestClient.setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());
		SMSupplierDataProcessing.setDataToFactoryRequestBean(entry.getValue(), factoryBean);
		//Creating Log Entry.....Need to change
		SMSupplierLogEntryProcessor.processDataFromProcessQueue(mapToCreateLogEntry);
	}

	/**
	 * Set Material Data.
	 * @param entry - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws IOException - IOException
	 */
	public static void setMaterialSupplierData(
			Map.Entry<String, LCSSupplier> entry) throws WTException,
			WTPropertyVetoException, IOException {
		Map<String, String> mapToCreateLogEntry = new HashMap<>();
		mapToCreateLogEntry.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(entry.getValue()), SMOutboundWebServiceConstants.SUPPLIER_QUEUE_REQUEST);
		MaterialSupplierInformationUpdatesRequest materialSupplierBean = new MaterialSupplierInformationUpdatesRequest();
		SMSupplierOutboundDataRequestClient.setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());
		SMSupplierDataProcessing.setDataToMaterialSupplierRequestBean(entry.getValue(), materialSupplierBean);
		//Creating Log Entry.....Need to change
		SMSupplierLogEntryProcessor.processDataFromProcessQueue(mapToCreateLogEntry);
	}

	/**
	 * Set Business Supplier data.
	 * @param entry - Map<String, LCSSupplier>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws IOException - IOException
	 */
	public static void setBusinessSupplierData(
			Map.Entry<String, LCSSupplier> entry) throws WTException,
			WTPropertyVetoException, IOException {
		Map<String, String> mapToCreateLogEntry = new HashMap<>();
		mapToCreateLogEntry.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(entry.getValue()), SMOutboundWebServiceConstants.SUPPLIER_QUEUE_REQUEST);
		BusinessSupplierInformationUpdatesRequest businessSupplierBean = new BusinessSupplierInformationUpdatesRequest();
		SMSupplierOutboundDataRequestClient.setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());
		SMSupplierDataProcessing.setDataToBusinessSupplierRequestBean(entry.getValue(), businessSupplierBean);
		//Creating Log Entry.....Need to change
		SMSupplierLogEntryProcessor.processDataFromProcessQueue(mapToCreateLogEntry);
	}
	
	/**
	 * Returns creator email/name.
	 * @param supplier - LCSSupplier
	 * @return String
	 */
	public static String getSupplierCreator(LCSSupplier supplier){
		if(FormatHelper.hasContent(supplier.getCreatorEMail())){
			return supplier.getCreatorEMail();
		}
		else{
			return supplier.getCreatorFullName();
		}
	}
	
	/**
	 * Returns modifier email/name.
	 * @param supplier - LCSSupplier
	 * @return String
	 */
	public static String getSupplierModifier(LCSSupplier supplier){
		if(FormatHelper.hasContent(supplier.getModifierEMail())){
			return supplier.getModifierEMail();
		}
		else{
			return supplier.getModifierFullName();
		}
	}

}

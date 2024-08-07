package com.sportmaster.wc.interfaces.webservices.outbound.material.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.supplier.LCSSupplier;
import com.sportmaster.wc.interfaces.webservices.outbound.helper.SMOutBoundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialHleper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialPluginHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialXMLHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.plugin.SMMaterialPlugins;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
/**
 * SMMaterialBean.java
 * This class has setter and getter method for attribute.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialBean {

	/**
	 * the lvCreateObjCount.
	 */
	private int totalObjCount;
	/**
	 * the lvFailCount.
	 */
	private int totalFailCount;
	/**
	 * the lvFailCount.
	 */
	private int totalProcessedCount;
	
    private String objectTye;
	
	private String smMDMDIV;
	
	private int smRequestId;
	
	private String smObjectID;
	
	private String smErrorReason="";
	
	private String smObjectName;
	
	private String smIntegrationStatus;
	
	private LCSMaterial material;
	
	private Map<String, String> attKeyColumnMap;
	
	private List<?> attrKeyList;
	
	private List<?> attrMappedElementList;
	
	private LCSMaterialSupplier materialSupplier;
	
	private LCSSupplier supplier;
	
	private boolean forScheduleQueue;
	
	private boolean serviceError;
	
	private String type;
	
	private String lifecycleState;
	
	private String createdBy;
	
	private XMLGregorianCalendar createdOn;
	
	private XMLGregorianCalendar lastUpdated;
	
	private String lastUpdatedBy;
	
	private boolean kintZipper;
	
	private boolean trimsOtherType;
	
	private SMOutboundWebServiceConstants constant;
	
	private boolean commonMaterialType;
	
	private SMMaterialPluginHelper pluginHelper;
	
	private SMMaterialXMLHelper xmlHelper;
	
	private SMMaterialHleper helper;
	
	private SMMaterialUtill utill;
	
	private SMLogEntryProcessor logEntryProcessor;
	
	
	/**
	 * @return the trimsOtherType
	 */
	public boolean isTrimsOtherType() {
		return trimsOtherType;
	}
	/**
	 * @param trimsOtherType the trimsOtherType to set
	 */
	public void setTrimsOtherType(boolean trimsOtherType) {
		this.trimsOtherType = trimsOtherType;
	}
	/**
	 * @return the objectTye
	 */
	public String getObjectTye() {
		return objectTye;
	}
	/**
	 * @param objectTye the objectTye to set
	 */
	public void setObjectTye(String objectTye) {
		if("Trims\\Zipper".equalsIgnoreCase(objectTye)){
			setKintZipper(true);
		}else if("Fabric\\Knit".equalsIgnoreCase(objectTye)){
			setKintZipper(true);
		}else if("Trims\\Flat Knit Rib".equalsIgnoreCase(objectTye)){
			trimsOtherType=true;
		}else if("Trims\\Hook & Loop".equalsIgnoreCase(objectTye)){
			trimsOtherType=true;
		}else{
			setKintZipper(false);
		}
		
		//added for 3.9.0.0 Build
		if(!SMMaterialPlugins.checkEligibility(objectTye)) {
			commonMaterialType = true;
		}
		
		this.objectTye = objectTye;
		
		if ("Shipping & Packing".equalsIgnoreCase(objectTye)) {
			
			this.objectTye = objectTye.replace("&", "AND");
			
		}
	}
	/**
	 * @return the smMDMDIV
	 */
	public String getSmMDMDIV() {
		return smMDMDIV;
	}
	/**
	 * @param smMDMDIV the smMDMDIV to set
	 */
	public void setSmMDMDIV(String smMDMDIV) {
		this.smMDMDIV = smMDMDIV;
	}
	/**
	 * @return the smRequestId
	 */
	public int getSmRequestId() {
		return smRequestId;
	}
	/**
	 * @param smRequestId the smRequestId to set
	 */
	public void setSmRequestId(int smRequestId) {
		this.smRequestId = smRequestId;
	}
	/**
	 * @return the smObjectID
	 */
	public String getSmObjectID() {
		return smObjectID;
	}
	/**
	 * @param smObjectID the smObjectID to set
	 */
	public void setSmObjectID(String smObjectID) {
		this.smObjectID = smObjectID;
	}
	/**
	 * @return the smErrorReason
	 */
	public String getSmErrorReason() {
		return smErrorReason;
	}
	/**
	 * @param smErrorReason the smErrorReason to set
	 */
	public void setSmErrorReason(String smErrorReason) {
		
		this.smErrorReason = this.smErrorReason + System.lineSeparator() + smErrorReason;
	}
	/**
	 * @return the smObjectName
	 */
	public String getSmObjectName() {
		return smObjectName;
	}
	/**
	 * @param smObjectName the smObjectName to set
	 */
	public void setSmObjectName(String smObjectName) {
		this.smObjectName = smObjectName;
	}
	/**
	 * @return the smIntegrationStatus
	 */
	public String getSmIntegrationStatus() {
		return smIntegrationStatus;
	}
	/**
	 * @param smIntegrationStatus the smIntegrationStatus to set
	 */
	public void setSmIntegrationStatus(String smIntegrationStatus) {
		this.smIntegrationStatus = smIntegrationStatus;
	}
	/**
	 * @return the material
	 */
	public LCSMaterial getMaterial() {
		return material;
	}
	/**
	 * @param material the material to set
	 */
	public void setMaterial(LCSMaterial material) {
		this.material = material;
	}
	/**
	 * @return the attKeyColumnMap
	 */
	public Map<String, String> getAttKeyColumnMap() {
		return attKeyColumnMap;
	}
	/**
	 * @param attKeyColumnMap the attKeyColumnMap to set
	 */
	public void setAttKeyColumnMap(Map<String, String> attKeyColumnMap) {
		this.attKeyColumnMap = attKeyColumnMap;
	}
	/**
	 * @return the attrKeyList
	 */
	public List<?> getAttrKeyList() {
		return attrKeyList;
	}
	/**
	 * @param attrKeyList the attrKeyList to set
	 */
	public void setAttrKeyList(List<?> attrKeyList) {
		this.attrKeyList = attrKeyList;
	}
	/**
	 * @return the attrMappedElementList
	 */
	public List<?> getAttrMappedElementList() {
		return attrMappedElementList;
	}
	/**
	 * @param attrMappedElementList the attrMappedElementList to set
	 */
	public void setAttrMappedElementList(List<?> attrMappedElementList) {
		this.attrMappedElementList = attrMappedElementList;
	}
	/**
	 * @return the materialSupplier
	 */
	public LCSMaterialSupplier getMaterialSupplier() {
		return materialSupplier;
	}
	/**
	 * @param materialSupplier the materialSupplier to set
	 */
	public void setMaterialSupplier(LCSMaterialSupplier materialSupplier) {
		this.materialSupplier = materialSupplier;
	}
	/**
	 * @return the supplier
	 */
	public LCSSupplier getSupplier() {
		return supplier;
	}
	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(LCSSupplier supplier) {
		this.supplier = supplier;
	}
	/**
	 * @return the forScheduleQueue
	 */
	public boolean isForScheduleQueue() {
		return forScheduleQueue;
	}
	/**
	 * @param forScheduleQueue the forScheduleQueue to set
	 */
	public void setForScheduleQueue(boolean forScheduleQueue) {
		this.forScheduleQueue = forScheduleQueue;
	}
	/**
	 * @return the serviceError
	 */
	public boolean isServiceError() {
		return serviceError;
	}
	/**
	 * @param serviceError the serviceError to set
	 */
	public void setServiceError(boolean serviceError) {
		this.serviceError = serviceError;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the lifecycleState
	 */
	public String getLifecycleState() {
		return lifecycleState;
	}
	/**
	 * @param lifecycleState the lifecycleState to set
	 */
	public void setLifecycleState(String lifecycleState) {
		this.lifecycleState = lifecycleState;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdOn
	 */
	public XMLGregorianCalendar getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param ldt the createdOn to set
	 */
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = SMOutBoundHelper.getXMLGregorianCalendarVlaue(createdOn);
	}
	/**
	 * @return the lastUpdated
	 */
	public XMLGregorianCalendar getLastUpdated() {
		return lastUpdated;
	}
	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = SMOutBoundHelper.getXMLGregorianCalendarVlaue(lastUpdated);
	}
	/**
	 * @return the lastUpdatedBy
	 */
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	/**
	 * @param lastUpdatedBy the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	/**
	 * @return the kintZipper
	 */
	public boolean isKintZipper() {
		return kintZipper;
	}
	/**
	 * @param kintZipper the kintZipper to set
	 */
	public void setKintZipper(boolean kintZipper) {
		this.kintZipper = kintZipper;
	}
	/**
	 * @return the constant
	 */
	public SMOutboundWebServiceConstants getConstant() {
		return constant;
	}
	/**
	 * @param constant the constant to set
	 */
	public void setConstant(SMOutboundWebServiceConstants constant) {
		this.constant = constant;
	}
	/**
	 * @return the totalObjCount
	 */
	public int getTotalObjCount() {
		return totalObjCount;
	}
	/**
	 * @param totalObjCount the totalObjCount to set
	 */
	public void setTotalObjCount(int totalObjCount) {
		this.totalObjCount += totalObjCount;
	}
	/**
	 * @return the totalFailCount
	 */
	public int getTotalFailCount() {
		return totalFailCount;
	}
	/**
	 * @param totalFailCount the totalFailCount to set
	 */
	public void setTotalFailCount(int totalFailCount) {
		this.totalFailCount += totalFailCount;
	}
	/**
	 * @return the totalProcessedCount
	 */
	public int getTotalProcessedCount() {
		return totalProcessedCount;
	}
	/**
	 * @param totalProcessedCount the totalProcessedCount to set
	 */
	public void setTotalProcessedCount(int totalProcessedCount) {
		this.totalProcessedCount += totalProcessedCount;
	}
	/**
	 * @return the commonMaterialType
	 */
	public boolean isCommonMaterialType() {
		return commonMaterialType;
	}
	/**
	 * @param commonMaterialType the commonMaterialType to set
	 */
	public void setCommonMaterialType(boolean commonMaterialType) {
		this.commonMaterialType = commonMaterialType;
	}
	/**
	 * @return the pluginHelper
	 */
	public SMMaterialPluginHelper getPluginHelper() {
		return pluginHelper;
	}
	/**
	 * @param pluginHelper the pluginHelper to set
	 */
	public void setPluginHelper(SMMaterialPluginHelper pluginHelper) {
		this.pluginHelper = pluginHelper;
	}
	/**
	 * @return the xmlHelper
	 */
	public SMMaterialXMLHelper getXmlHelper() {
		return xmlHelper;
	}
	/**
	 * @param xmlHelper the xmlHelper to set
	 */
	public void setXmlHelper(SMMaterialXMLHelper xmlHelper) {
		this.xmlHelper = xmlHelper;
	}
	/**
	 * @return the helper
	 */
	public SMMaterialHleper getHelper() {
		return helper;
	}
	/**
	 * @param helper the helper to set
	 */
	public void setHelper(SMMaterialHleper helper) {
		this.helper = helper;
	}
	/**
	 * @return the utill
	 */
	public SMMaterialUtill getUtill() {
		return utill;
	}
	/**
	 * @param utill the utill to set
	 */
	public void setUtill(SMMaterialUtill utill) {
		this.utill = utill;
	}
	/**
	 * @return the logEntryProcessor
	 */
	public SMLogEntryProcessor getLogEntryProcessor() {
		return logEntryProcessor;
	}
	/**
	 * @param logEntryProcessor the logEntryProcessor to set
	 */
	public void setLogEntryProcessor(SMLogEntryProcessor logEntryProcessor) {
		this.logEntryProcessor = logEntryProcessor;
	}
	
}

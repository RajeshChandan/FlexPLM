package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill;


/**
 * SMLVIntegrationBean.java
 *  This class is using to call the methods defined in process class.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMLVIntegrationBean {


	/**
	 * Instantiates a new bean.
	 */
	public SMLVIntegrationBean(){

	}	
	/**
	 * the startTimeInGMT.
	 */
	private String startTimeInGMT;
	/**
	 * the endTimeinLocaGMT.
	 */
	private String endTimeinGMT;
	/**
	 * the lvCreateObjCount.
	 */
	private int lvCreateObjCount;
	/**
	 * the lvFailCount.
	 */
	private int lvFailCount;
	/**
	 * the lvUpdateCount.
	 */
	private int lvUpdateCount;
	/**
	 * BusinessObjectType.
	 */
	private String businessObjectType;
	
	private String requestType;
	
	private String requestId;
	/**
	 * @return the startTimeInGMT
	 * 
	 */
	public String getStartTimeInGMT() {
		return startTimeInGMT;
	}
	/**
	 * @param startTimeInGMT the startTimeInGMT to set
	 */
	public void setStartTimeInGMT(String startTimeInGMT) {
		this.startTimeInGMT = startTimeInGMT;
	}
	/**
	 * @return the endTimeinGMT
	 */
	public String getEndTimeinGMT() {
		return endTimeinGMT;
	}
	/**
	 * @param endTimeinGMT the endTimeinGMT to set
	 */
	public void setEndTimeinGMT(String endTimeinGMT) {
		this.endTimeinGMT = endTimeinGMT;
	}
	/**
	 * @return the lvCreateObjCount
	 */
	public int getLvCreateObjCount() {
		return lvCreateObjCount;
	}
	/**
	 * @param lvCreateObjCount the lvCreateObjCount to set
	 */
	public void setLvCreateObjCount(int lvCreateObjCount) {
		this.lvCreateObjCount += lvCreateObjCount;
	}
	/**
	 * @return the lvFailCount
	 */
	public int getLvFailCount() {
		return lvFailCount;
	}
	/**
	 * @param lvFailCount the lvFailCount to set
	 */
	public void setLvFailCount(int lvFailCount) {
		this.lvFailCount += lvFailCount;
	}
	/**
	 * @return the lvUpdateCount
	 */
	public int getLvUpdateCount() {
		return lvUpdateCount;
	}
	/**
	 * @param lvUpdateCount the lvUpdateCount to set
	 */
	public void setLvUpdateCount(int lvUpdateCount) {
		this.lvUpdateCount += lvUpdateCount;
	}
	/**
	 * @return the businessObjectType
	 */
	public String getBusinessObjectType() {
		return businessObjectType;
	}
	/**
	 * @param businessObjectType the businessObjectType to set
	 */
	public void setBusinessObjectType(String businessObjectType) {
		this.businessObjectType = businessObjectType;
	}
	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}
	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	

}

package com.sportmaster.wc.interfaces.webservices.outbound.feedback.util;


/**
 * SMLVIntegrationBean.java
 *  This class is using to call the methods defined in process class.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMFBIntegrationUtilBean {


	/**
	 * 
	 * Instantiates a new bean.
	 */
	public SMFBIntegrationUtilBean(){

	}	
	/**
	 * the startTimeInGMT.
	 */
	private String fbStartTimeInGMT;
	/**
	 * the endTimeinLocaGMT.
	 */
	private String fbEndTimeinGMT;
	/**
	 * the lvCreateObjCount.
	 */
	private int fbObjCount;
	/**
	 * the lvFailCount.
	 */
	private int fbFailCount;
	/**
	 * fbrequestId.
	 */
	private int fbrequestId;
	/**
	 * @return the fbStartTimeInGMT
	 */
	public String getFbStartTimeInGMT() {
		return fbStartTimeInGMT;
	}
	/**
	 * @param fbStartTimeInGMT the fbStartTimeInGMT to set
	 */
	public void setFbStartTimeInGMT(String fbStartTimeInGMT) {
		this.fbStartTimeInGMT = fbStartTimeInGMT;
	}
	/**
	 * @return the fbEndTimeinGMT
	 */
	public String getFbEndTimeinGMT() {
		return fbEndTimeinGMT;
	}
	/**
	 * @param fbEndTimeinGMT the fbEndTimeinGMT to set
	 */
	public void setFbEndTimeinGMT(String fbEndTimeinGMT) {
		this.fbEndTimeinGMT = fbEndTimeinGMT;
	}
	/**
	 * @return the fbObjCount
	 */
	public int getFbObjCount() {
		return fbObjCount;
	}
	/**
	 * @param fbObjCount the fbObjCount to set
	 */
	public void setFbObjCount(int fbObjCount) {
		this.fbObjCount += fbObjCount;
	}
	/**
	 * @return the fbFailCount
	 */
	public int getFbFailCount() {
		return fbFailCount;
	}
	/**
	 * @param fbFailCount the fbFailCount to set
	 */
	public void setFbFailCount(int fbFailCount) {
		this.fbFailCount += fbFailCount;
	}
	/**
	 * @return the fbrequestId
	 */
	public int getFbrequestId() {
		return fbrequestId;
	}
	/**
	 * @param fbrequestId the fbrequestId to set
	 */
	public void setFbrequestId(int fbrequestId) {
		this.fbrequestId = fbrequestId;
	}

	

}

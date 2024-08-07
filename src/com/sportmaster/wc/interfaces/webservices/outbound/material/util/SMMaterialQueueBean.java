package com.sportmaster.wc.interfaces.webservices.outbound.material.util;

/**
 * SMMaterialQueueBean.java
 * This class has setter and getter method for attribute.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialQueueBean {

	public SMMaterialQueueBean() {
		
	}
	/**
	 * the startTimeInGMT.
	 */
	private String matStartTimeInGMT;
	/**
	 * the endTimeinLocaGMT.
	 */
	private String matEndTimeinGMT;
	/**
	 * the lvCreateObjCount.
	 */
	private int finalTotalObjCount;
	/**
	 * the lvFailCount.
	 */
	private int finalTotalFailCount;
	/**
	 * the lvFailCount.
	 */
	private int finalTotalProcessedCount;
	/**
	 * materialBean.
	 */
	private SMMaterialBean materialBean;
	
	/**
	 * @return the matStartTimeInGMT
	 */
	public String getMatStartTimeInGMT() {
		return matStartTimeInGMT;
	}
	/**
	 * @param matStartTimeInGMT the matStartTimeInGMT to set
	 */
	public void setMatStartTimeInGMT(String matStartTimeInGMT) {
		this.matStartTimeInGMT = matStartTimeInGMT;
	}
	/**
	 * @return the matEndTimeinGMT
	 */
	public String getMatEndTimeinGMT() {
		return matEndTimeinGMT;
	}
	/**
	 * @param matEndTimeinGMT the matEndTimeinGMT to set
	 */
	public void setMatEndTimeinGMT(String matEndTimeinGMT) {
		this.matEndTimeinGMT = matEndTimeinGMT;
	}
	/**
	 * @return the totalObjCount
	 */
	public int getTotalObjCount() {
		return finalTotalObjCount;
	}
	/**
	 * @param totalObjCount the totalObjCount to set
	 */
	public void setTotalObjCount(int totalObjCount) {
		this.finalTotalObjCount += totalObjCount;
	}
	/**
	 * @return the totalFailCount
	 */
	public int getTotalFailCount() {
		return finalTotalFailCount;
	}
	/**
	 * @param totalFailCount the totalFailCount to set
	 */
	public void setTotalFailCount(int totalFailCount) {
		this.finalTotalFailCount += totalFailCount;
	}
	/**
	 * @return the totalProcessedCount
	 */
	public int getTotalProcessedCount() {
		return finalTotalProcessedCount;
	}
	/**
	 * @param totalProcessedCount the totalProcessedCount to set
	 */
	public void setTotalProcessedCount(int totalProcessedCount) {
		this.finalTotalProcessedCount += totalProcessedCount;
	}
	/**
	 * @return the materialBean
	 */
	public SMMaterialBean getMaterialBean() {
		return materialBean;
	}
	/**
	 * @param materialBean the materialBean to set
	 */
	public void setMaterialBean(SMMaterialBean materialBeans) {
		materialBean = materialBeans;
	}
	
	
}

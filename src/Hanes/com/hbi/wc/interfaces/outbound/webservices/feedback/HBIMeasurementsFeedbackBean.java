package com.hbi.wc.interfaces.outbound.webservices.feedback;

import java.io.Serializable;

/**
 * HBIMeasurementsFeedbackBean.java
 * 
 * This class contains all the attributes(fields), getters & setters of the attributes, which are using in LCSMeasurements/LCSPointsOfMeasure data sync feedback from integrated FlexPLM
 * @author Abdul.Patel@Hanes.com
 * @since  May-6-2015
 */
public class HBIMeasurementsFeedbackBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String hbiTransactionId;
	private String hbiFlexObjectClassName;
	private String measurementsFeedbackMessage;
	private String hbiIntegrationStatus;
	
	/**
	 * @return the hbiTransactionId
	 */
	public String getHbiTransactionId() {
		return hbiTransactionId;
	}
	/**
	 * @param hbiTransactionId the hbiTransactionId to set
	 */
	public void setHbiTransactionId(String hbiTransactionId) {
		this.hbiTransactionId = hbiTransactionId;
	}
	/**
	 * @return the hbiFlexObjectClassName
	 */
	public String getHbiFlexObjectClassName() {
		return hbiFlexObjectClassName;
	}
	/**
	 * @param hbiFlexObjectClassName the hbiFlexObjectClassName to set
	 */
	public void setHbiFlexObjectClassName(String hbiFlexObjectClassName) {
		this.hbiFlexObjectClassName = hbiFlexObjectClassName;
	}
	/**
	 * @return the measurementsFeedbackMessage
	 */
	public String getMeasurementsFeedbackMessage() {
		return measurementsFeedbackMessage;
	}
	/**
	 * @param measurementsFeedbackMessage the measurementsFeedbackMessage to set
	 */
	public void setMeasurementsFeedbackMessage(String measurementsFeedbackMessage) {
		this.measurementsFeedbackMessage = measurementsFeedbackMessage;
	}
	/**
	 * @return the hbiIntegrationStatus
	 */
	public String getHbiIntegrationStatus() {
		return hbiIntegrationStatus;
	}
	/**
	 * @param hbiIntegrationStatus the hbiIntegrationStatus to set
	 */
	public void setHbiIntegrationStatus(String hbiIntegrationStatus) {
		this.hbiIntegrationStatus = hbiIntegrationStatus;
	}
}
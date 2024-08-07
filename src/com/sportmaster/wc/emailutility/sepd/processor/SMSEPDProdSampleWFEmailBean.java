package com.sportmaster.wc.emailutility.sepd.processor;

import com.lcs.wc.sample.LCSSample;

import wt.org.WTPrincipal;

/**
 * Bean class for object, required to apply collection.sort, group etc
 * 
 * @author Priya
 *
 */
public class SMSEPDProdSampleWFEmailBean {
	private String seasonName;
	private String productStyle;
	private String colorway;
	private String businessSupplier;
	private String factory;
	private String sampleName;
	private String requestCreator;
	private String supplierSampleStatus;
	private String sampleStatus;
	private WTPrincipal emailUserObj;
	private String emailUser;
	private LCSSample sample;

	/**
	 * Constructor
	 * 
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param requestCreator
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param emailUserObj
	 * @param emailUser
	 * @param sample
	 */
	public SMSEPDProdSampleWFEmailBean(String seasonName, String productStyle, String colorway, String businessSupplier,
			String factory, String sampleName, String supplierSampleStatus, String sampleStatus, String requestCreator,
			WTPrincipal emailUserObj, String emailUser, LCSSample sample) {
		super();
		this.seasonName = seasonName;
		this.productStyle = productStyle;
		this.colorway = colorway;
		this.businessSupplier = businessSupplier;
		this.factory = factory;
		this.sampleName = sampleName;
		this.supplierSampleStatus = supplierSampleStatus;
		this.sampleStatus = sampleStatus;
		this.requestCreator = requestCreator;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
		this.sample = sample;
	}

	public String getSeasonName() {
		return seasonName;
	}

	public String getProductStyle() {
		return productStyle;
	}

	public String getColorway() {
		return colorway;
	}

	public String getBusinessSupplier() {
		return businessSupplier;
	}

	public String getFactory() {
		return factory;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getSupplierSampleStatus() {
		return supplierSampleStatus;
	}

	public String getSampleStatus() {
		return sampleStatus;
	}

	public String getRequestCreator() {
		return requestCreator;
	}

	public WTPrincipal getEmailUserObj() {
		return emailUserObj;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public LCSSample getSample() {
		return sample;
	}

	@Override
	public String toString() {
		return "seasonName=" + seasonName + ", productStyle=" + productStyle + ", colorway=" + colorway
				+ ", businessSupplier=" + businessSupplier + ", factory=" + factory + ", sampleName=" + sampleName
				+ ", supplierSampleStatus=" + supplierSampleStatus + ", sampleStatus=" + sampleStatus
				+ ", requestCreator=" + requestCreator + ", emailUserObj=" + emailUserObj + ", emailUser=" + emailUser
				+ ", sample=" + sample;
	}

}

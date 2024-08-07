package com.sportmaster.wc.emailutility.fpd.processor;

import com.lcs.wc.sample.LCSSample;

import wt.org.WTPrincipal;

/**
 * Bean class for object, required to apply collection.sort, group etc
 * 
 * @author Priya
 *
 */
public class SMFPDProdSampleWFEmailBean {
	private String seasonName;
	private String brand;
	private String style;
	private String colorway;
	private String businessSupplier;
	private String requestName;
	private String sampleSize;
	private String requestCreator;
	private String supplierStatus;
	private WTPrincipal emailUserObj;
	private String emailUser;
	private LCSSample sample;

	/**
	 * Constructor
	 * 
	 * @param seasonName
	 * @param brand
	 * @param style
	 * @param colorway
	 * @param businessSupplier
	 * @param requestName
	 * @param sampleSize
	 * @param requestCreator
	 * @param supplierStatus
	 * @param emailUserObj
	 * @param emailUser
	 * @param sample
	 */
	public SMFPDProdSampleWFEmailBean(String seasonName, String brand, String style, String colorway,
			String businessSupplier, String requestName, String sampleSize, String requestCreator,
			String supplierStatus, WTPrincipal emailUserObj, String emailUser, LCSSample sample) {
		super();
		this.seasonName = seasonName;
		this.brand = brand;
		this.style = style;
		this.colorway = colorway;
		this.businessSupplier = businessSupplier;
		this.requestName = requestName;
		this.sampleSize = sampleSize;
		this.requestCreator = requestCreator;
		this.supplierStatus = supplierStatus;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
		this.sample = sample;
	}

	public String getSeasonName() {
		return seasonName;
	}

	public String getBrand() {
		return brand;
	}

	public String getStyle() {
		return style;
	}

	public String getColorway() {
		return colorway;
	}

	public String getBusinessSupplier() {
		return businessSupplier;
	}

	public String getRequestName() {
		return requestName;
	}

	public String getSampleSize() {
		return sampleSize;
	}

	public String getRequestCreator() {
		return requestCreator;
	}

	public String getSupplierStatus() {
		return supplierStatus;
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
		return "seasonName=" + seasonName + ", brand=" + brand + ", style=" + style + ", colorway=" + colorway
				+ ", businessSupplier=" + businessSupplier + ", requestName=" + requestName + ", sampleSize="
				+ sampleSize + ", requestCreator=" + requestCreator + ", supplierStatus=" + supplierStatus
				+ ", emailUserObj=" + emailUserObj + ", emailUser=" + emailUser + ", sample=" + sample;
	}

}

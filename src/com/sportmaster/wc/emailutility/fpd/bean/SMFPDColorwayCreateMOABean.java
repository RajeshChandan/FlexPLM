package com.sportmaster.wc.emailutility.fpd.bean;

import wt.org.WTUser;

public class SMFPDColorwayCreateMOABean {

	private String seasonName;
	private String brand;
	private String productId;
	private String productName;
	private String newColorwayId;
	private String newColorwayName;
	private WTUser emailUserObj;
	private String emailUser;

	//RCOM108 
	/**
	 * Constructor
	 * @param seasonName
	 * @param brand
	 * @param productId
	 * @param productName
	 * @param newColorwayId
	 * @param newColorwayName
	 * @param emailUserObj
	 * @param emailUser
	 */
	public SMFPDColorwayCreateMOABean(String seasonName, String brand, String productId, String productName, String newColorwayId, String newColorwayName,
			WTUser emailUserObj, String emailUser) {
		super();
		this.seasonName = seasonName;
		this.brand = brand;
		this.productId= productId;
		this.productName = productName;
		this.newColorwayId=newColorwayId;
		this.newColorwayName = newColorwayName;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
	}

	public String getSeasonName() {
		return seasonName;
	}

	public String getBrand() {
		return brand;
	}

	public String getProductId() {
		return productId;
	}
	
	public String getProductName() {
		return productName;
	}

	public String getNewColorwayId() {
		return newColorwayId;
	}
	
	public String getNewColorwayName() {
		return newColorwayName;
	}

	public WTUser getEmailUserObj() {
		return emailUserObj;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public void setProductId(String productId) {
		this.productId=productId;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public void setNewColorwayId(String newColorwayId) {
		this.newColorwayId = newColorwayId;
	}

	public void setNewColorwayName(String newColorwayName) {
		this.newColorwayName = newColorwayName;
	}

	public void setEmailUserObj(WTUser emailUserObj) {
		this.emailUserObj = emailUserObj;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	//RCOM108 
	@Override
	public String toString() {
		return "seasoName=" + seasonName 
				+ ", brand=" + brand 
				+ ", product=" + productName
				+ ", productId=" + productId 
				+ ", colorway="+ newColorwayName 
				+ ", colorwayId="+ newColorwayId
				+ ", emailUserObj=" + emailUserObj 
				+ ", emailUser=" + emailUser;
	}
}

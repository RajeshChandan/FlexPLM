package com.sportmaster.wc.emailutility.sepd.bean;

import wt.org.WTUser;

public class SMSEPDPSCSCancelledBean {

	private String strSeasonName;
	private String strBrand;
	private String strProductId;
	private String strProductName;
	private String strColorwayId;
	private String strColorwayName;
	private String strLevel;
	private WTUser wtEmailUserObj;
	private String strEmailUser;

	//RCOM106 
	/**
	 * Constructor
	 * @param seasonName
	 * @param brand
	 * @param productId
	 * @param productName
	 * @param colorwayId
	 * @param colorwayName
	 * @param level
	 * @param emailUserObj
	 * @param emailUser
	 */
	public SMSEPDPSCSCancelledBean(String seasonName, String brand, String productId, String productName, String colorwayId,  String colorwayName,
			String level, WTUser emailUserObj, String emailUser) {
		super();
		this.strSeasonName = seasonName;
		this.strBrand = brand;
		this.strProductId= productId;
		this.strProductName = productName;
		this.strColorwayId=colorwayId;
		this.strColorwayName =colorwayName;
		this.strLevel=level;
		this.wtEmailUserObj = emailUserObj;
		this.strEmailUser = emailUser;
	}

	
	public String getStrSeasonName() {
		return strSeasonName;
	}

	public String getStrBrand() {
		return strBrand;
	}

	public String getStrProductId() {
		return strProductId;
	}

	public String getStrProductName() {
		return strProductName;
	}

	public String getStrColorwayId() {
		return strColorwayId;
	}

	public String getStrColorwayName() {
		return strColorwayName;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public WTUser getWtEmailUserObj() {
		return wtEmailUserObj;
	}

	public String getStrEmailUser() {
		return strEmailUser;
	}

	public void setStrSeasonName(String strSeasonName) {
		this.strSeasonName = strSeasonName;
	}

	public void setStrBrand(String strBrand) {
		this.strBrand = strBrand;
	}

	public void setStrProductId(String strProductId) {
		this.strProductId = strProductId;
	}

	public void setStrProductName(String strProductName) {
		this.strProductName = strProductName;
	}

	public void setStrColorwayId(String strColorwayId) {
		this.strColorwayId = strColorwayId;
	}

	public void setStrColorwayName(String strColorwayName) {
		this.strColorwayName = strColorwayName;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public void setWtEmailUserObj(WTUser wtEmailUserObj) {
		this.wtEmailUserObj = wtEmailUserObj;
	}

	public void setStrEmailUser(String strEmailUser) {
		this.strEmailUser = strEmailUser;
	}

	//RCOM106
	@Override
	public String toString() {
		return "seasoName=" + strSeasonName 
				+ ", brand=" + strBrand 
				+ ", product=" + strProductName
				+ ", productId=" + strProductId 
				+ ", colorway=" + strColorwayName
				+ ", colorwayId=" + strColorwayId
				+ ", level=" + strLevel
				+ ", emailUserObj=" + wtEmailUserObj 
				+ ", emailUser=" + strEmailUser;
	}
}

package com.sportmaster.wc.emailutility.fpd.processor;

import com.lcs.wc.product.LCSSKU;

import wt.org.WTUser;

/**
 * @author Rajesh Chandan
 *
 */
public class SMFPDProdSeasWFEmailBean {
	private String seasonName;
	private String brand;
	private String product;
	private String productionManagerColorway;
	private WTUser emailUserObj;
	private String emailUser;
	private LCSSKU sku;

	/**
	 * @param seasonName
	 * @param brand
	 * @param product
	 * @param productionManagerColorway
	 * @param emailUserObj
	 * @param emailUser
	 * @param sku
	 */
	public SMFPDProdSeasWFEmailBean(String seasonName, String brand, String product, String productionManagerColorway, WTUser emailUserObj,
			String emailUser, LCSSKU sku) {
		this.seasonName = seasonName;
		this.brand = brand;
		this.product = product;
		this.productionManagerColorway = productionManagerColorway;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
		this.sku = sku;
	}
	/**
	 * @return the seasonName
	 */
	public String getSeasonName() {
		return seasonName;
	}
	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}
	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}
	/**
	 * @return the productionManagerColorway
	 */
	public String getProductionManagerColorway() {
		return productionManagerColorway;
	}
	/**
	 * @return the emailUserObj
	 */
	public WTUser getEmailUserObj() {
		return emailUserObj;
	}
	/**
	 * @return the emailUser
	 */
	public String getEmailUser() {
		return emailUser;
	}
	/**
	 * @return the sku
	 */
	public LCSSKU getSku() {
		return sku;
	}
	@Override
	public String toString() {
		return "SMFPDProdSeasWFEmailBean [seasonName=" + seasonName + ", brand=" + brand + ", product=" + product
				+ ", productionManagerColorway=" + productionManagerColorway + ", emailUserObj=" + emailUserObj + ", emailUser=" + emailUser
				+ ", sku=" + sku + "]";
	}

}

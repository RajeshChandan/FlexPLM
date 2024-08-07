package com.sportmaster.wc.emailutility.sepd.processor;

import wt.org.WTUser;

/**
 * Bean class for object, required to apply collection.sort, group etc
 * @author Priya
 *
 */
public class SMSEPDProdSeasWFEmailBean {
	private String seasoName;
    private String salesNewNess;
    private String brand;
	private String product;
	private WTUser emailUserObj;
	private String emailUser;

    /**
     * Constructor 
     * @param seasoName
     * @param salesNewNess
     * @param brand
     * @param product
     * @param emailUserObj
     * @param emailUser
     */
    public SMSEPDProdSeasWFEmailBean(String seasoName, String salesNewNess, String brand, String product, WTUser emailUserObj, String emailUser) {
        super();
        this.seasoName = seasoName;
        this.salesNewNess = salesNewNess;
        this.brand = brand;
		this.product = product;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
    }


	public String getSeasoName() {
        return seasoName;
    }

    public String getSalesNewNess() {
        return salesNewNess;
    }

    public String getBrand() {
        return brand;
    }
	
	public String getProduct() {
        return product;
    }
	
	public WTUser getEmailUserObj() {
        return emailUserObj;
    }
	
	public String getEmailUser() {
        return emailUser;
    }
	
	@Override
    public String toString() {
        return "seasoName=" + seasoName + ", salesNewNess=" + salesNewNess + ", brand=" + brand + ", product=" + product + ", emailUserObj=" + emailUserObj + ", emailUser=" + emailUser ;
    }
	

}

package com.hbi.etl.dao;


import java.math.BigDecimal;


public class HbiInvTypeXref  implements java.io.Serializable {

	private BigDecimal primarykey;
	private String plmMajCat;
    private String plmMinCat;
    private String invTypeCode;
	public BigDecimal getPrimarykey() {
		return primarykey;
	}
	public void setPrimarykey(BigDecimal primarykey) {
		this.primarykey = primarykey;
	}
	public String getPlmMajCat() {
		return plmMajCat;
	}
	public void setPlmMajCat(String plmMajCat) {
		this.plmMajCat = plmMajCat;
	}
	public String getPlmMinCat() {
		return plmMinCat;
	}
	public void setPlmMinCat(String plmMinCat) {
		this.plmMinCat = plmMinCat;
	}
	public String getInvTypeCode() {
		return invTypeCode;
	}
	public void setInvTypeCode(String invTypeCode) {
		this.invTypeCode = invTypeCode;
	}
    
  
}

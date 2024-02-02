package com.hbi.etl.dao;


import java.math.BigDecimal;


//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;


public class HbiPurchXref  implements java.io.Serializable {


	private BigDecimal primarykey;
	private String PLM_MAJ_CAT;
    private String PLM_MIN_CAT;
    private String LAWSON_MAJ_CAT;
    private String LAWSON_MIN_CAT;
    
    public BigDecimal getPrimarykey() {
        return this.primarykey;
    }
    
    public void setPrimarykey(BigDecimal primarykey) {
        this.primarykey = primarykey;
    }
    
	public String getPLM_MAJ_CAT() {
		return PLM_MAJ_CAT;
	}
	public void setPLM_MAJ_CAT(String pLM_MAJ_CAT) {
		PLM_MAJ_CAT = pLM_MAJ_CAT;
	}
	public String getPLM_MIN_CAT() {
		return PLM_MIN_CAT;
	}
	public void setPLM_MIN_CAT(String pLM_MIN_CAT) {
		PLM_MIN_CAT = pLM_MIN_CAT;
	}
	public String getLAWSON_MAJ_CAT() {
		return LAWSON_MAJ_CAT;
	}
	public void setLAWSON_MAJ_CAT(String lAWSON_MAJ_CAT) {
		LAWSON_MAJ_CAT = lAWSON_MAJ_CAT;
	}
	public String getLAWSON_MIN_CAT() {
		return LAWSON_MIN_CAT;
	}
	public void setLAWSON_MIN_CAT(String lAWSON_MIN_CAT) {
		LAWSON_MIN_CAT = lAWSON_MIN_CAT;
	}
    
    
}

package com.sportmaster.wc.emailutility.sepd.processor;

import wt.org.WTUser;

/**
 * Bean class for object, required to apply collection.sort, group etc
 * @author Narasimha Bandla.
 *
 */
public class SMSEPDImagePageWFEmailBean {
	private String seasonName;
	private String 	docOID;;
    private String imagePageName;
	private WTUser emailUserObj;
	private String emailUser;

	

    /**
     * Constructor 
     * @param seasonName
     * @param docOID
     * @param imagePageName
     * @param emailUserObj
     * @param emailUser
     */
	public SMSEPDImagePageWFEmailBean(String seasoName, String docOID,String imagePageName, WTUser emailUserObj, String emailUser) {
		super();
		this.seasonName = seasoName;
		this.docOID = docOID;;
		this.imagePageName = imagePageName;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
	}


	public String getSeasoName() {
		return seasonName;
	}
	
	public String getDocOID() {
		return docOID;
	}


	public String getImagePageName() {
		return imagePageName;
	}


	public WTUser getEmailUserObj() {
		return emailUserObj;
	}


	public String getEmailUser() {
		return emailUser;
	}


	@Override
	public String toString() {
		return "SMSEPDImagePageWFEmailBean [seasonName=" + seasonName + ", docOID=" + docOID + ", imagePageName="
				+ imagePageName + ", emailUserObj=" + emailUserObj + ", emailUser=" + emailUser + "]";
	}

}

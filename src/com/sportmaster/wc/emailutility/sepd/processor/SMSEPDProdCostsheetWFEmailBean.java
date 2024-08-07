package com.sportmaster.wc.emailutility.sepd.processor;

import com.lcs.wc.sourcing.LCSCostSheet;

import wt.org.WTPrincipal;

/**
 * Bean class for object, required to apply collection.sort, group etc
 * 
 * @author Priya
 *
 */
public class SMSEPDProdCostsheetWFEmailBean {
	private String seasonName;
	private String styleName;
	private String sourceName;
	private String costingStage;
	private Long costsheetNo;
	private String costsheetName;
	private WTPrincipal emailUserObj;
	private String emailUser;
	private LCSCostSheet costsheet;

	/**
	 * Constructor
	 * 
	 * @param seasonName
	 * @param styleName
	 * @param sourceName
	 * @param costingStage
	 * @param costsheetNo
	 * @param costsheetName
	 * @param emailUserObj
	 * @param emailUser
	 * @param costsheet
	 * @return
	 */
	public SMSEPDProdCostsheetWFEmailBean(String seasonName, String styleName, String sourceName, String costingStage,
			Long costsheetNo, String costsheetName, WTPrincipal emailUserObj, String emailUser,
			LCSCostSheet costsheet) {
		super();
		this.seasonName = seasonName;
		this.styleName = styleName;
		this.sourceName = sourceName;
		this.costingStage = costingStage;
		this.costsheetNo = costsheetNo;
		this.costsheetName = costsheetName;
		this.emailUserObj = emailUserObj;
		this.emailUser = emailUser;
		this.costsheet = costsheet;
	}

	public String getSeasonName() {
		return seasonName;
	}

	public String getStyleName() {
		return styleName;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getCostingStage() {
		return costingStage;
	}

	public Long getCostsheetNo() {
		return costsheetNo;
	}

	public String getCostsheetName() {
		return costsheetName;
	}

	public WTPrincipal getEmailUserObj() {
		return emailUserObj;
	}

	public String getEmailUser() {
		return emailUser;
	}

	public LCSCostSheet getCostsheet() {
		return costsheet;
	}

	@Override
	public String toString() {
		return "seasonName=" + seasonName + ", styleName=" + styleName + ", sourceName=" + sourceName
				+ ", costingStage=" + costingStage + ", costsheetNo=" + costsheetNo + ", costsheetName=" + costsheetName
				+ ", emailUserObj=" + emailUserObj + ", emailUser=" + emailUser + ", costsheet=" + costsheet;
	}

}

package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper;


public class SMCareLabelIntegrationBean {



	//Declaring bean variable.
	private String selectedSeasonOid = "";

	//Declaring bean variable.
	private String selectedSeasonName="";

	//Declaring bean variable.
	private String selectedProductName ="";

	//Declaring bean variable.
	private String selectedProductID ="";
	
	/**
	 * Declaring Error Message received in response.
	 */
	private String responseErrorReason="";

	/**
	 * @return the selectedProductID
	 */
	public String getSelectedProductID() {
		return selectedProductID;
	}

	/**
	 * @param selectedProductID the selectedProductID to set
	 */
	public void setSelectedProductID(String selectedProductID) {
		this.selectedProductID = selectedProductID;
	}

	//Declaring bean variable.
	private String selectedBrands ="";

	//Declaring bean variable.
	private String selectedGenders ="";

	//Declaring bean variable.
	private String selectedAges ="";

	//Declaring bean variable.
	private String selectedProject ="";

	//Declaring bean variable.
	private String selectedProductionGroup ="";	

	//Declaring bean variable.
	private String selectedProducctTechnologist ="";
	
	/**
	 * Care Label outbound request ID.
	 */
	private int careLabelRequestID;



	/**
	 * @return the selectedSeasonOid
	 */
	public String getSelectedSeasonOid() {
		return selectedSeasonOid;
	}

	/**
	 * @param selectedSeasonOid the selectedSeasonOid to set
	 */
	public void setSelectedSeasonOid(String selectedSeasonOid) {
		this.selectedSeasonOid = selectedSeasonOid;
	}



	/**
	 * @return the selectedSeasonName
	 */
	public String getSelectedSeasonName() {
		return selectedSeasonName;
	}

	/**
	 * @param selectedSeasonName the selectedSeasonName to set
	 */
	public void setSelectedSeasonName(String selectedSeasonName) {
		this.selectedSeasonName = selectedSeasonName;
	}



	/**
	 * @return the selectedProductName
	 */
	public String getSelectedProductName() {
		return selectedProductName;
	}

	/**
	 * @param selectedProductName the selectedProductName to set
	 */
	public void setSelectedProductName(String selectedProductName) {
		this.selectedProductName = selectedProductName;
	}

	/**
	 * @return the selectedBrands
	 */
	public String getSelectedBrands() {
		return selectedBrands;
	}

	/**
	 * @param selectedBrands the selectedBrands to set
	 */
	public void setSelectedBrands(String selectedBrands) {
		this.selectedBrands = selectedBrands;
	}

	/**
	 * @return the selectedGenders
	 */
	public String getSelectedGenders() {
		return selectedGenders;
	}

	/**
	 * @param selectedGenders the selectedGenders to set
	 */
	public void setSelectedGenders(String selectedGenders) {
		this.selectedGenders = selectedGenders;
	}

	/**
	 * @return the selectedAges
	 */
	public String getSelectedAges() {
		return selectedAges;
	}

	/**
	 * @param selectedAges the selectedAges to set
	 */
	public void setSelectedAges(String selectedAges) {
		this.selectedAges = selectedAges;
	}

	/**
	 * @return the selectedProject
	 */
	public String getSelectedProject() {
		return selectedProject;
	}

	/**
	 * @param selectedProject the selectedProject to set
	 */
	public void setSelectedProject(String selectedProject) {
		this.selectedProject = selectedProject;
	}

	/**
	 * @return the selectedProductionGroupOid
	 */
	public String getSelectedProductionGroup() {
		return selectedProductionGroup;
	}

	/**
	 * @param selectedProductionGroup the selectedProductionGroupOid to set
	 */
	public void setSelectedProductionGroup(String selectedProductionGroup) {
		this.selectedProductionGroup = selectedProductionGroup;
	}



	/**
	 * @return the selectedProducctTechnologist
	 */
	public String getSelectedProducctTechnologist() {
		return selectedProducctTechnologist;
	}

	/**
	 * @param selectedProducctTechnologist the selectedProducctTechnologist to set
	 */
	public void setSelectedProducctTechnologist(String selectedProducctTechnologist) {
		this.selectedProducctTechnologist = selectedProducctTechnologist;
	}

	
	
	/**
	 * @return the careLabelRequestID
	 */
	public int getCareLabelRequestID() {
		return careLabelRequestID;
	}

	/**
	 * @param careLabelRequestID
	 *            the careLabelRequestID to set.
	 */
	public  void setCareLabelRequestID(int careLabelRequestID) {
		this.careLabelRequestID = careLabelRequestID;
	}
	
	
	/**
	 * @return the responseErrorReason
	 */
	public String getResponseErrorReason() {
		return responseErrorReason;
	}

	/**
	 * @param responseErrorReason the responseErrorReason to set
	 */
	public void setResponseErrorReason(String responseErrorReason) {
		this.responseErrorReason = responseErrorReason;
	}

}

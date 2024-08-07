package com.sportmaster.wc.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.db.FlexObject;

/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public class SMMaterialForecastReportBean {
	
	//Declaring bean variable.
	private Collection attList=new ArrayList();
	
	//Declaring bean variable.
	private Collection attRUList=new ArrayList();
	
	//Declaring bean variable.
	private Collection attCNList=new ArrayList();
	
	//Declaring bean variable.
	private Collection attUAList=new ArrayList();

	//Declaring bean variable.
	private Collection attKeyList=new ArrayList();
	
	//Declaring bean variable.
	private Map columns=new HashMap();
	
	//Declaring bean variable.
	private Collection finalColumns=new ArrayList();
	
	//Declaring bean variable.
	private Collection formulaeBasedColKeys=new ArrayList();
	
	//Declaring bean variable.
	private Map columnsMap=new HashMap();
	
	//Declaring bean variable.
	private Collection selectedMaterialTypeOids=new ArrayList();

	//Declaring bean variable.
	private Collection selectedSeasonOids=new ArrayList();

	//Declaring bean variable.
	private Collection selectedMatSupplierOids=new ArrayList();

	//Declaring bean variable.
	private Collection selectedBrands=new ArrayList();
	
	private boolean isNominatedSupplier;
	
	private boolean hasFabricType;
	
	private boolean hasOtherMaterialType;
	
	// Added to display the filter criteria in document object - Start
	private String selectedSeasonDisplay="";
	
	private String selectedMaterialTypeDisplay="";
	
	private String selectedMatSupDisplay="";
	
	private String selectedBrandDisplay="";
	
	private String selectedNominatedDisplay="";
	// Added to display the filter criteria in document object - End

	private Collection<FlexObject> reportData=new ArrayList();

	/**
	 * @return the attList
	 */
	public Collection<String> getAttList() {
		return attList;
	}

	/**
	 * @return the columns
	 */
	public Map<String, TableColumn> getColumns() {
		return columns;
	}

	/**
	 * @return the finalColumns
	 */
	public Collection getFinalColumns() {
		return finalColumns;
	}

	/**
	 * @return the columnsMap
	 */
	public Map<String,TableColumn> getColumnsMap() {
		return columnsMap;
	}

	/**
	 * @param attList the attList to set
	 */
	public void setAttList(Collection attList) {
		this.attList = attList;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Map columns) {
		this.columns = columns;
	}

	/**
	 * @param finalColumns the finalColumns to set
	 */
	public void setFinalColumns(Collection finalColumns) {
		this.finalColumns = finalColumns;
	}

	/**
	 * @param columnsMap the columnsMap to set
	 */
	public void setColumnsMap(Map columnsMap) {
		this.columnsMap = columnsMap;
	}

	/**
	 * @return the selectedMaterialTypeOids
	 */
	public Collection<String> getSelectedMaterialTypeOids() {
		return selectedMaterialTypeOids;
	}

	/**
	 * @return the selectedSeasonOids
	 */
	public Collection<String> getSelectedSeasonOids() {
		return selectedSeasonOids;
	}

	/**
	 * @return the selectedMatSupplierOids
	 */
	public Collection<String> getSelectedMatSupplierOids() {
		return selectedMatSupplierOids;
	}

	/**
	 * @return the selectedBrands
	 */
	public Collection<String> getSelectedBrands() {
		return selectedBrands;
	}

	/**
	 * @return the isNominatedSupplier
	 */
	public boolean isNominatedSupplier() {
		return isNominatedSupplier;
	}

	/**
	 * @return the reportData
	 */
	public Collection<FlexObject> getReportData() {
		return reportData;
	}

	/**
	 * @param selectedMaterialTypeOids the selectedMaterialTypeOids to set
	 */
	public void setSelectedMaterialTypeOids(Collection selectedMaterialTypeOids) {
		this.selectedMaterialTypeOids = selectedMaterialTypeOids;
	}

	/**
	 * @param selectedSeasonOids the selectedSeasonOids to set
	 */
	public void setSelectedSeasonOids(Collection selectedSeasonOids) {
		this.selectedSeasonOids = selectedSeasonOids;
	}

	/**
	 * @param selectedMatSupplierOids the selectedMatSupplierOids to set
	 */
	public void setSelectedMatSupplierOids(Collection selectedMatSupplierOids) {
		this.selectedMatSupplierOids = selectedMatSupplierOids;
	}

	/**
	 * @param selectedBrands the selectedBrands to set
	 */
	public void setSelectedBrands(Collection selectedBrands) {
		this.selectedBrands = selectedBrands;
	}

	/**
	 * @param isNominatedSupplier the isNominatedSupplier to set
	 */
	public void setNominatedSupplier(boolean isNominatedSupplier) {
		this.isNominatedSupplier = isNominatedSupplier;
	}

	/**
	 * @param reportData the reportData to set
	 */
	public void setReportData(Collection<FlexObject> reportData) {
		this.reportData = reportData;
	}

	/**
	 * @return the formulaeBasedColKeys
	 */
	public Collection<String> getFormulaeBasedColKeys() {
		return formulaeBasedColKeys;
	}

	/**
	 * @param formulaeBasedColKeys the formulaeBasedColKeys to set
	 */
	public void setFormulaeBasedColKeys(Collection formulaeBasedColKeys) {
		this.formulaeBasedColKeys = formulaeBasedColKeys;
	}

	/**
	 * @return the attKeyList
	 */
	public Collection getAttKeyList() {
		return attKeyList;
	}

	/**
	 * @param attKeyList the attKeyList to set
	 */
	public void setAttKeyList(Collection attKeyList) {
		this.attKeyList = attKeyList;
	}

	/**
	 * @return the hasFabricType
	 */
	public boolean isHasFabricType() {
		return hasFabricType;
	}

	/**
	 * @param hasFabricType the hasFabricType to set
	 */
	public void setHasFabricType(boolean hasFabricType) {
		this.hasFabricType = hasFabricType;
	}

	/**
	 * @return the attRUList
	 */
	public Collection getAttRUList() {
		return attRUList;
	}

	/**
	 * @return the attCNList
	 */
	public Collection getAttCNList() {
		return attCNList;
	}

	/**
	 * @return the attUAList
	 */
	public Collection getAttUAList() {
		return attUAList;
	}

	/**
	 * @param attRUList the attRUList to set
	 */
	public void setAttRUList(Collection attRUList) {
		this.attRUList = attRUList;
	}

	/**
	 * @param attCNList the attCNList to set
	 */
	public void setAttCNList(Collection attCNList) {
		this.attCNList = attCNList;
	}

	/**
	 * @param attUAList the attUAList to set
	 */
	public void setAttUAList(Collection attUAList) {
		this.attUAList = attUAList;
	}

	/**
	 * @return the hasOtherMaterialType
	 */
	public boolean isHasOtherMaterialType() {
		return hasOtherMaterialType;
	}

	/**
	 * @param hasOtherMaterialType the hasOtherMaterialType to set
	 */
	public void setHasOtherMaterialType(boolean hasOtherMaterialType) {
		this.hasOtherMaterialType = hasOtherMaterialType;
	}

	public void setSelectedSeasonDisplayName(String selectedSeasonDisplay) {
		this.selectedSeasonDisplay = selectedSeasonDisplay;
		
	}
	
	public String getSelectedSeasonDisplayName() {
		return selectedSeasonDisplay;
		
	}
	
	public void setSelectedMaterialTypeDisplayName(String selectedMaterialTypeDisplay) {
		this.selectedMaterialTypeDisplay = selectedMaterialTypeDisplay;
		
	}
	
	public String getSelectedMaterialTypeDisplayName() {
		return selectedMaterialTypeDisplay;
		
	}
	
	public void setSelectedBrandDisplayName(String selectedBrandDisplay) {
		this.selectedBrandDisplay = selectedBrandDisplay;
		
	}
	
	public String getSelectedBrandDisplayName() {
		return selectedBrandDisplay;
		
	}
	
	public void setSelectedMatSupDisplayName(String selectedMatSupDisplay) {
		this.selectedMatSupDisplay = selectedMatSupDisplay;
		
	}
	
	public String getSelectedMatSupDisplayName() {
		return selectedMatSupDisplay;
		
	}
	
	public String getSelectedNominatedDisplay() {
		return selectedNominatedDisplay;
	}

	public void setSelectedNominatedDisplay(String selectedNominatedDisplay) {
		this.selectedNominatedDisplay = selectedNominatedDisplay;
	}
	// Added to display the filter criteria in document object - End
	
}

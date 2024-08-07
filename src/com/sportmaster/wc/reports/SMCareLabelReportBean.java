package com.sportmaster.wc.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.db.FlexObject;
/**
 * The Class SMCareLabelReportBean.
 *
 * @version 'true' 1.0 version number.
 * @author 'true' ITC.
 */
public class SMCareLabelReportBean {

		/** Declaring bean variable attList. */
		private Collection attList=new ArrayList();
		
		/** Declaring bean variable attRUList. */
		private Collection attRUList=new ArrayList();
		
		/** Declaring bean variable attCNList. */
		private Collection attCNList=new ArrayList();
		
		/** Declaring bean variable attUAList. */
		private Collection attUAList=new ArrayList();

		/** Declaring bean variable attKeyList. */
		private Collection attKeyList=new ArrayList();
		
		/** Declaring bean variable columns. */
		private Map columns=new HashMap();
		
		/** Declaring bean variable finalColumns. */
		private Collection finalColumns=new ArrayList();
		
		/** Declaring bean variable columnsMap. */
		private Map columnsMap=new HashMap();
		
		/** Declaring bean variable selectedSeasonOid. */
		private String selectedSeasonOid = "";
				
		/** Declaring bean variable selectedProductName. */
		private Collection selectedProductName =new ArrayList();
		
		/** Declaring bean variable selectedBrands. */
		private Collection selectedBrands =new ArrayList();
		
		/** Declaring bean variable selectedGenders. */
		private Collection selectedGenders =new ArrayList();

		/** Declaring bean variable selectedAges. */
		private Collection selectedAges =new ArrayList();

		/** Declaring bean variable selectedProject. */
		private Collection selectedProject =new ArrayList();

		/** Declaring bean variable selectedProductionGroupOid. */
		private Collection<String> selectedProductionGroupOid =new ArrayList();	
		
		/** Declaring bean variable selectedProducctTechnologist. */
		private String selectedProducctTechnologist ="";	
		
		/** Declaring bean variable reportData. */
		private Collection<FlexObject> reportData=new ArrayList();
		
		/** Declaring bean variable intProductMap. */
		private Map intProductMap = new HashMap();
		
		/** Declaring bean variable intTechnologistMap. */
		private Map intTechnologistMap = new HashMap();
		
		/** Declaring bean variable intProjectMap. */
		private Map intProjectMap = new HashMap();

		/**
		 * @return the attList
		 */
		public Collection<String> getAttList() {
			return attList;
		}

		/**
		 * @param attList the attList to set
		 */
		public void setAttList(Collection attList) {
			this.attList = attList;
		}

		/**
		 * @return the attRUList
		 */
		public Collection getAttRUList() {
			return attRUList;
		}

		/**
		 * @param attRUList the attRUList to set
		 */
		public void setAttRUList(Collection attRUList) {
			this.attRUList = attRUList;
		}

		/**
		 * @return the attCNList
		 */
		public Collection getAttCNList() {
			return attCNList;
		}

		/**
		 * @param attCNList the attCNList to set
		 */
		public void setAttCNList(Collection attCNList) {
			this.attCNList = attCNList;
		}

		/**
		 * @return the attUAList
		 */
		public Collection getAttUAList() {
			return attUAList;
		}

		/**
		 * @param attUAList the attUAList to set
		 */
		public void setAttUAList(Collection attUAList) {
			this.attUAList = attUAList;
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
		 * @return the columns
		 */
		public Map<String, TableColumn> getColumns() {
			return columns;
		}

		/**
		 * @param columns the columns to set
		 */
		public void setColumns(Map columns) {
			this.columns = columns;
		}

		/**
		 * @return the finalColumns
		 */
		public Collection getFinalColumns() {
			return finalColumns;
		}

		/**
		 * @param finalColumns the finalColumns to set
		 */
		public void setFinalColumns(Collection finalColumns) {
			this.finalColumns = finalColumns;
		}

		/**
		 * @return the columnsMap
		 */
		public Map getColumnsMap() {
			return columnsMap;
		}

		/**
		 * @param columnsMap the columnsMap to set
		 */
		public void setColumnsMap(Map columnsMap) {
			this.columnsMap = columnsMap;
		}

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
		 * @return the selectedProductName
		 */
		public Collection<String> getSelectedProductName() {
			return selectedProductName;
		}

		/**
		 * @param selectedProductName the selectedProductName to set
		 */
		public void setSelectedProductName(Collection selectedProductName) {
			this.selectedProductName = selectedProductName;
		}

		/**
		 * @return the selectedBrands
		 */
		public Collection<String> getSelectedBrands() {
			return selectedBrands;
		}

		/**
		 * @param selectedBrands the selectedBrands to set
		 */
		public void setSelectedBrands(Collection selectedBrands) {
			this.selectedBrands = selectedBrands;
		}

		/**
		 * @return the selectedGenders
		 */
		public Collection getSelectedGenders() {
			return selectedGenders;
		}

		/**
		 * @param selectedGenders the selectedGenders to set
		 */
		public void setSelectedGenders(Collection selectedGenders) {
			this.selectedGenders = selectedGenders;
		}

		/**
		 * @return the selectedAges
		 */
		public Collection getSelectedAges() {
			return selectedAges;
		}

		/**
		 * @param selectedAges the selectedAges to set
		 */
		public void setSelectedAges(Collection selectedAges) {
			this.selectedAges = selectedAges;
		}

		/**
		 * @return the selectedProject
		 */
		public Collection getSelectedProject() {
			return selectedProject;
		}

		/**
		 * @param selectedProject the selectedProject to set
		 */
		public void setSelectedProject(Collection selectedProject) {
			this.selectedProject = selectedProject;
		}

		/**
		 * @return the selectedProductionGroupOid
		 */
		public Collection<String> getSelectedProductionGroupOid() {
			return selectedProductionGroupOid;
		}

		/**
		 * @param selectedProductionGroup the selectedProductionGroupOid to set
		 */
		public void setSelectedProductionGroupOid(Collection<String> selectedProductionGroup) {
			this.selectedProductionGroupOid = selectedProductionGroup;
		}

		/**
		 * @return the reportData
		 */
		public Collection<FlexObject> getReportData() {
			return reportData;
		}

		/**
		 * @param reportData the reportData to set
		 */
		public void setReportData(Collection<FlexObject> reportData) {
			this.reportData = reportData;
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
		 * @return the intProductMap
		 */
		public Map getIntProductMap() {
			return intProductMap;
		}

		/**
		 * @param intProductMap the intProductMap to set
		 */
		public void setIntProductMap(Map intProductMap) {
			this.intProductMap = intProductMap;
		}

		/**
		 * @return the intTechnologistMap
		 */
		public Map getIntTechnologistMap() {
			return intTechnologistMap;
		}

		/**
		 * @param intTechnologistMap the intTechnologistMap to set
		 */
		public void setIntTechnologistMap(Map intTechnologistMap) {
			this.intTechnologistMap = intTechnologistMap;
		}

		/**
		 * @return the intProjectMap
		 */
		public Map getIntProjectMap() {
			return intProjectMap;
		}

		/**
		 * @param intProjectMap the intProjectMap to set
		 */
		public void setIntProjectMap(Map intProjectMap) {
			this.intProjectMap = intProjectMap;
		}
}

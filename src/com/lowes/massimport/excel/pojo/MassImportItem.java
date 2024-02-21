package com.lowes.massimport.excel.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MassImportItem {

	private MassImportHeader massImportHeader;
	private int rowNum;
	private String errorMessage;

	/** Product attributes */
	private String productDescription;
	private String modelNumer;

	Map<String, Boolean> booleanProductAttributes = new HashMap<>();
	Map<String, String> stringProductAttributes = new HashMap<>();
	Map<String, Double> floatProductAttributes = new HashMap<>();
	Map<String, Long> numberProductAttributes = new HashMap<>();
	Map<String, Date> dateProductAttributes = new HashMap<>();
	Map<String, Object> objectRefProductAttributes = new HashMap<>();

	/** Sourcingconfig attributes */

	private String vendorRef;
	Map<String, Boolean> booleanSourceAttributes = new HashMap<>();
	Map<String, String> stringSourceAttributes = new HashMap<>();
	Map<String, Double> floatSourcetAttributes = new HashMap<>();
	Map<String, Long> numberSourceAttributes = new HashMap<>();
	Map<String, Date> dateSourceAttributes = new HashMap<>();
	Map<String, Object> objectRefSourceAttributes = new HashMap<>();

	/** Cost sheet attributes */
	private String costSheetName;
	Map<String, Boolean> booleanCostSheetAttributes = new HashMap<>();
	Map<String, String> stringCostSheetAttributes = new HashMap<>();
	Map<String, Double> floatCostSheetAttributes = new HashMap<>();
	Map<String, Long> numberCostSheetAttributes = new HashMap<>();
	Map<String, Date> dateCostSheetAttributes = new HashMap<>();
	Map<String, Object> objectRefCostsheetAttributes = new HashMap<>();

	public MassImportHeader getMassImportHeader() {
		return massImportHeader;
	}

	public void setMassImportHeader(MassImportHeader massImportHeader) {
		this.massImportHeader = massImportHeader;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getModelNumer() {
		return modelNumer;
	}

	public void setModelNumer(String modelNumer) {
		this.modelNumer = modelNumer;
	}

	public Map<String, Boolean> getBooleanProductAttributes() {
		return booleanProductAttributes;
	}

	public void setBooleanProductAttributes(Map<String, Boolean> booleanProductAttributes) {
		this.booleanProductAttributes = booleanProductAttributes;
	}

	public Map<String, String> getStringProductAttributes() {
		return stringProductAttributes;
	}

	public void setStringProductAttributes(Map<String, String> stringProductAttributes) {
		this.stringProductAttributes = stringProductAttributes;
	}

	public Map<String, Double> getFloatProductAttributes() {
		return floatProductAttributes;
	}

	public void setFloatProductAttributes(Map<String, Double> floatProductAttributes) {
		this.floatProductAttributes = floatProductAttributes;
	}

	public Map<String, Long> getNumberProductAttributes() {
		return numberProductAttributes;
	}

	public void setNumberProductAttributes(Map<String, Long> numberProductAttributes) {
		this.numberProductAttributes = numberProductAttributes;
	}

	public Map<String, Date> getDateProductAttributes() {
		return dateProductAttributes;
	}

	public void setDateProductAttributes(Map<String, Date> dateProductAttributes) {
		this.dateProductAttributes = dateProductAttributes;
	}

	public Map<String, Object> getObjectRefProductAttributes() {
		return objectRefProductAttributes;
	}

	public void setObjectRefProductAttributes(Map<String, Object> objectRefProductAttributes) {
		this.objectRefProductAttributes = objectRefProductAttributes;
	}

	public String getVendorRef() {
		return vendorRef;
	}

	public void setVendorRef(String vendorRef) {
		this.vendorRef = vendorRef;
	}

	public Map<String, Boolean> getBooleanSourceAttributes() {
		return booleanSourceAttributes;
	}

	public void setBooleanSourceAttributes(Map<String, Boolean> booleanSourceAttributes) {
		this.booleanSourceAttributes = booleanSourceAttributes;
	}

	public Map<String, String> getStringSourceAttributes() {
		return stringSourceAttributes;
	}

	public void setStringSourceAttributes(Map<String, String> stringSourceAttributes) {
		this.stringSourceAttributes = stringSourceAttributes;
	}

	public Map<String, Double> getFloatSourcetAttributes() {
		return floatSourcetAttributes;
	}

	public void setFloatSourcetAttributes(Map<String, Double> floatSourcetAttributes) {
		this.floatSourcetAttributes = floatSourcetAttributes;
	}

	public Map<String, Long> getNumberSourceAttributes() {
		return numberSourceAttributes;
	}

	public void setNumberSourceAttributes(Map<String, Long> numberSourceAttributes) {
		this.numberSourceAttributes = numberSourceAttributes;
	}



	public Map<String, Object> getObjectRefSourceAttributes() {
		return objectRefSourceAttributes;
	}

	public void setObjectRefSourceAttributes(Map<String, Object> objectRefSourceAttributes) {
		this.objectRefSourceAttributes = objectRefSourceAttributes;
	}

	public String getCostSheetName() {
		return costSheetName;
	}

	public void setCostSheetName(String costSheetName) {
		this.costSheetName = costSheetName;
	}

	public Map<String, Boolean> getBooleanCostSheetAttributes() {
		return booleanCostSheetAttributes;
	}

	public void setBooleanCostSheetAttributes(Map<String, Boolean> booleanCostSheetAttributes) {
		this.booleanCostSheetAttributes = booleanCostSheetAttributes;
	}

	public Map<String, String> getStringCostSheetAttributes() {
		return stringCostSheetAttributes;
	}

	public void setStringCostSheetAttributes(Map<String, String> stringCostSheetAttributes) {
		this.stringCostSheetAttributes = stringCostSheetAttributes;
	}

	public Map<String, Double> getFloatCostSheetAttributes() {
		return floatCostSheetAttributes;
	}

	public void setFloatCostSheetAttributes(Map<String, Double> floatCostSheetAttributes) {
		this.floatCostSheetAttributes = floatCostSheetAttributes;
	}

	public Map<String, Long> getNumberCostSheetAttributes() {
		return numberCostSheetAttributes;
	}

	public void setNumberCostSheetAttributes(Map<String, Long> numberCostSheetAttributes) {
		this.numberCostSheetAttributes = numberCostSheetAttributes;
	}

	public Map<String, Date> getDateCostSheetAttributes() {
		return dateCostSheetAttributes;
	}

	public void setDateCostSheetAttributes(Map<String, Date> dateCostSheetAttributes) {
		this.dateCostSheetAttributes = dateCostSheetAttributes;
	}

	public Map<String, Object> getObjectRefCostsheetAttributes() {
		return objectRefCostsheetAttributes;
	}

	public void setObjectRefCostsheetAttributes(Map<String, Object> objectRefCostsheetAttributes) {
		this.objectRefCostsheetAttributes = objectRefCostsheetAttributes;
	}

	public Map<String, Date> getDateSourceAttributes() {
		return dateSourceAttributes;
	}

	public void setDateSourceAttributes(Map<String, Date> dateSourceAttributes) {
		this.dateSourceAttributes = dateSourceAttributes;
	}

	@Override
	public String toString() {
		return "MassImportItem [massImportHeader=" + massImportHeader + ", rowNum=" + rowNum + ", errorMessage="
				+ errorMessage + ", productDescription=" + productDescription + ", modelNumer=" + modelNumer
				+ ", booleanProductAttributes=" + booleanProductAttributes + ", stringProductAttributes="
				+ stringProductAttributes + ", floatProductAttributes=" + floatProductAttributes
				+ ", numberProductAttributes=" + numberProductAttributes + ", dateProductAttributes="
				+ dateProductAttributes + ", objectRefProductAttributes=" + objectRefProductAttributes + ", vendorRef="
				+ vendorRef + ", booleanSourceAttributes=" + booleanSourceAttributes + ", stringSourceAttributes="
				+ stringSourceAttributes + ", floatSourcetAttributes=" + floatSourcetAttributes
				+ ", numberSourceAttributes=" + numberSourceAttributes + ", dateSourceProductAttributes="
				+ dateSourceAttributes + ", objectRefSourceAttributes=" + objectRefSourceAttributes
				+ ", costSheetName=" + costSheetName + ", booleanCostSheetAttributes=" + booleanCostSheetAttributes
				+ ", stringCostSheetAttributes=" + stringCostSheetAttributes + ", floatCostSheetAttributes="
				+ floatCostSheetAttributes + ", numberCostSheetAttributes=" + numberCostSheetAttributes
				+ ", dateSourceCostSheetAttributes=" + dateCostSheetAttributes + ", objectRefCostsheetAttributes="
				+ objectRefCostsheetAttributes + "]";
	}

}

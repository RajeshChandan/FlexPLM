package com.lowes.massimport.excel.pojo;

import java.util.ArrayList;
import java.util.List;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.supplier.LCSSupplier;

import wt.org.WTPrincipal;

public class MassImportHeader {
	private int productColumnIndex;
	private int sourceColumnIndex;
	private int packageColumnIndex;
	private int costSheetColumnIndex;
	private LCSSeason season;
	private LCSSupplier supplier;
	private LCSProduct rfpProductRef;
	private WTPrincipal principal;
	private List<String> columns = new ArrayList<String>();

	public int getProductColumnIndex() {
		return productColumnIndex;
	}

	public void setProductColumnIndex(int productColumnIndex) {
		this.productColumnIndex = productColumnIndex;
	}

	public int getSourceColumnIndex() {
		return sourceColumnIndex;
	}

	public void setSourceColumnIndex(int sourceColumnIndex) {
		this.sourceColumnIndex = sourceColumnIndex;
	}

	public int getPackageColumnIndex() {
		return packageColumnIndex;
	}

	public void setPackageColumnIndex(int packageColumnIndex) {
		this.packageColumnIndex = packageColumnIndex;
	}

	public int getCostSheetColumnIndex() {
		return costSheetColumnIndex;
	}

	public void setCostSheetColumnIndex(int costSheetColumnIndex) {
		this.costSheetColumnIndex = costSheetColumnIndex;
	}

	public LCSSeason getSeason() {
		return season;
	}

	public void setSeason(LCSSeason season) {
		this.season = season;
	}

	public LCSSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(LCSSupplier supplier) {
		this.supplier = supplier;
	}

	public WTPrincipal getPrincipal() {
		return principal;
	}

	public void setPrincipal(WTPrincipal principal) {
		this.principal = principal;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public LCSProduct getRfpProductRef() {
		return rfpProductRef;
	}

	public void setRfpProductRef(LCSProduct rfpProductRef) {
		this.rfpProductRef = rfpProductRef;
	}

	@Override
	public String toString() {
		return "MassImportHeader [productColumnIndex=" + productColumnIndex + ", sourceColumnIndex=" + sourceColumnIndex
				+ ", packageColumnIndex=" + packageColumnIndex + ", costSheetColumnIndex=" + costSheetColumnIndex
				+ ", season=" + season + ", supplier=" + supplier + ", rfpProductRef=" + rfpProductRef + ", principal="
				+ principal + ", columns=" + columns + "]";
	}	

}

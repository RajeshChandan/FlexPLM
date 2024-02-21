package com.lowes.type.metadata.pojo;

public class EnumEntry {
	
	private String selectionLabel;
	private String selectionValue;

	public EnumEntry(String selectionLabel, String selectionValue) {
		this.selectionLabel = selectionLabel;
		this.selectionValue = selectionValue;
	}

	public String getSelectionLabel() {
		return selectionLabel;
	}

	public void setSelectionLabel(String selectionLabel) {
		this.selectionLabel = selectionLabel;
	}

	public String getSelectionValue() {
		return selectionValue;
	}

	public void setSelectionValue(String selectionValue) {
		this.selectionValue = selectionValue;
	}

	@Override
	public String toString() {
		return "EnumEntry [selectionLabel=" + selectionLabel + ", selectionValue=" + selectionValue + "]";
	}

}

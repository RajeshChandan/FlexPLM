package com.lowes.massimport.excel.pojo;

import java.util.HashMap;
import java.util.Map;

public class MassImportTemplate {

	private Map<String, MassImportHeader> massImportHeader = new HashMap<String, MassImportHeader>();
	private String message;
	private long updatedTime;

	public MassImportHeader getMassImportHeader(String sheetName) {
		return massImportHeader.get(sheetName);
	}
	
	public Map<String, MassImportHeader> getMassImportHeaderMap() {
		return massImportHeader;
	}

	public void setMassImportHeader(String sheetname, MassImportHeader massImportHeader) {
		this.massImportHeader.put(sheetname, massImportHeader);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "MassImportTemplate [massImportHeader=" + massImportHeader + ", message=" + message + ", updatedTime="
				+ updatedTime + "]";
	}

}

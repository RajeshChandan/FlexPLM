package com.lowes.massimport.excel.pojo;

public class MassImportTemplate {

	private MassImportHeader massImportHeader;
	private String message;
	private long updatedTime;

	public MassImportHeader getMassImportHeader() {
		return massImportHeader;
	}

	public void setMassImportHeader(MassImportHeader massImportHeader) {
		this.massImportHeader = massImportHeader;
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

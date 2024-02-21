package com.lowes.type.metadata.pojo;

import java.util.ArrayList;
import java.util.List;

public class EnumMetaData {

	private String enumInternalName;
	private String enumDisplayName;
	private long flexEnumMasterId;
	private List<EnumEntry> enumEntries = new ArrayList<>();

	public String getEnumInternalName() {
		return enumInternalName;
	}

	public void setEnumInternalName(String enumInternalName) {
		this.enumInternalName = enumInternalName;
	}

	public String getEnumDisplayName() {
		return enumDisplayName;
	}

	public void setEnumDisplayName(String enumDisplayName) {
		this.enumDisplayName = enumDisplayName;
	}

	public long getFlexEnumMasterId() {
		return flexEnumMasterId;
	}

	public void setFlexEnumMasterId(long flexEnumMasterId) {
		this.flexEnumMasterId = flexEnumMasterId;
	}

	public List<EnumEntry> getEnumEntries() {
		return enumEntries;
	}

	public void setEnumEntries(List<EnumEntry> enumEntries) {
		this.enumEntries = enumEntries;
	}

	@Override
	public String toString() {
		return "MaterialEnum [enumInternalName=" + enumInternalName + ", enumDisplayName=" + enumDisplayName
				+ ", flexEnumMasterId=" + flexEnumMasterId + ", enumEntries=" + enumEntries + "]";
	}

}

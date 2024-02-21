package com.lowes.type.metadata.pojo;

import java.util.HashMap;
import java.util.Map;

public class TypeAttributesMetaData {

	private String typeDisplayName;
	private String typeInternalName;
	private String typePathId;
	private long flexTypeId;
	private String type;
	private Map<String, AttributesMetaData> attributeMetaDataMap = new HashMap<>();

	public String getTypeDisplayName() {
		return typeDisplayName;
	}

	public void setTypeDisplayName(String typeDisplayName) {
		this.typeDisplayName = typeDisplayName;
	}

	public String getTypeInternalName() {
		return typeInternalName;
	}

	public void setTypeInternalName(String typeInternalName) {
		this.typeInternalName = typeInternalName;
	}

	public String getTypePathId() {
		return typePathId;
	}

	public void setTypePathId(String typePathId) {
		this.typePathId = typePathId;
	}

	public long getFlexTypeId() {
		return flexTypeId;
	}

	public void setFlexTypeId(long flexTypeId) {
		this.flexTypeId = flexTypeId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, AttributesMetaData> getAttributeMetaDataMap() {
		return attributeMetaDataMap;
	}

	public void setAttributeMetaDataMap(Map<String, AttributesMetaData> attributeMetaDataMap) {
		this.attributeMetaDataMap = attributeMetaDataMap;
	}

	@Override
	public String toString() {
		return "PLMTypeMetaData [typeDisplayName=" + typeDisplayName + ", typeInternalName=" + typeInternalName
				+ ", typePathId=" + typePathId + ", flexTypeId=" + flexTypeId + ", type=" + type
				+ ", attributeMetaDataMap=" + attributeMetaDataMap + "]";
	}

}

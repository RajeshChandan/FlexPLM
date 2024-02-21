package com.lowes.type.metadata.pojo;

import java.util.ArrayList;
import java.util.List;

public class AttributesMetaData {
	private String attributeDisplayName;
	private String attributeInternalName;
	private String flexDataType;
	private boolean isRequiredAttribute;
	private String objectRefClass;
	private List<EnumMetaData> enumValueList = new ArrayList<>();

	public String getAttributeDisplayName() {
		return attributeDisplayName;
	}

	public void setAttributeDisplayName(String attributeDisplayName) {
		this.attributeDisplayName = attributeDisplayName;
	}

	public String getAttributeInternalName() {
		return attributeInternalName;
	}

	public void setAttributeInternalName(String attributeInternalName) {
		this.attributeInternalName = attributeInternalName;
	}

	public String getFlexDataType() {
		return flexDataType;
	}

	public void setFlexDataType(String flexDataType) {
		this.flexDataType = flexDataType;
	}

	public boolean isRequiredAttribute() {
		return isRequiredAttribute;
	}

	public void setRequiredAttribute(boolean isRequiredAttribute) {
		this.isRequiredAttribute = isRequiredAttribute;
	}

	public String getObjectRefClass() {
		return objectRefClass;
	}

	public void setObjectRefClass(String objectRefClass) {
		this.objectRefClass = objectRefClass;
	}

	public List<EnumMetaData> getEnumValueList() {
		return enumValueList;
	}

	public void setEnumValueList(List<EnumMetaData> enumValueList) {
		this.enumValueList = enumValueList;
	}

	@Override
	public String toString() {
		return "AttributesMetaData [attributeDisplayName=" + attributeDisplayName + ", attributeInternalName="
				+ attributeInternalName + ", flexDataType=" + flexDataType + ", isRequiredAttribute="
				+ isRequiredAttribute + ", objectRefClass=" + objectRefClass + ", enumValueList=" + enumValueList + "]";
	};

}

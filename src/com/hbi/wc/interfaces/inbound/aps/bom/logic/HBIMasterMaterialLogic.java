package com.hbi.wc.interfaces.inbound.aps.bom.logic;

import java.util.Date;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialLogic;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMasterMaterialLogic.java
 *
 * This class contains a set of functions which are using to build a mapping between APS and PLM attributes, derive/initialize PLM attributes value, initialize new material object, set all
 * attributes value, populate default values such as 'Buy Flag', 'Workflow Process', 'Master Material' flag and other attributes, persisting the newly created material, return from header 
 * @author Abdul.Patel@Hanes.com
 * @since November-27-2017
 */
public class HBIMasterMaterialLogic
{
	private static String masterMaterialTypePath = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIMasterMaterialLogic.masterMaterialTypePath", "Material\\Material SKU");
	private static String materialCodeKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIMasterMaterialLogic.materialCodeKey", "hbiMaterialCode");
	private static String masterMaterialKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIMasterMaterialLogic.masterMaterialKey", "hbiMasterMaterial");
	private static String statusKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIMasterMaterialLogic.statusKey", "hbiStatus");
	
	/**
	 * This function is using to create new master material with the given 'materialName', 'colorCode', 'attributeCode' and 'sizeCode' where the material type path is Material\Material SKU
	 * @param materialName - String
	 * @param colorCode - String
	 * @param attributeCode - String
	 * @param sizeCode - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial createMasterMaterial(String materialName, String colorCode, String attributeCode, String sizeCode) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMasterMaterialLogic.createMasterMaterial(String materialName, String colorCode, String attributeCode, String sizeCode) ###");
		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(masterMaterialTypePath);
		
		//Initializing Material object (creating new Material instance) and populating necessary information (Master Reference, FlexTypePath & Material_Name) to the initialized object
		LCSMaterial materialObj = LCSMaterial.newLCSMaterial();
		materialObj.setMaster(new LCSMaterialMaster());
		materialObj.setFlexType(materialFlexTypeObj);
		
		//Populate materialName, colorCode, attributeCode and sizeCode attributes data (this data is extracted from APS BILL_OF_MTRLS table), this four attributes data is using in search
		materialObj.setValue(materialCodeKey, materialName);
		materialObj.setValue(HBIProperties.hbiColorCodeKey, colorCode);
		materialObj.setValue(HBIProperties.hbiAttrCodeKey, attributeCode);
		materialObj.setValue(HBIProperties.hbiSizeCodeKey, sizeCode);
		
		//Populate 'Master Material' as Yes, because this material is created as a master material from APS data, Buy flag as No and WorkFlow Process as Not Required, as this is a master 
		materialObj.setValue(masterMaterialKey, true);
		materialObj.setValue(HBIProperties.hbiBuyOrNotBuyKey, false);
		materialObj.setValue(HBIProperties.hbiWorkflowProcessKey, "notRequired");
		materialObj.setValue(HBIProperties.hbiMaterialSourceTypeKey, "createdFromBOMLoader");
		materialObj.setValue("hbiMaterialMigratedOn", new Date());
		
		//Calling a function which is using to populate a set of attributes default values (setting attribute default values using custom code due to out of the box API issues)
		populateDefaultAttributeValues(materialObj);
		
		//Calling out of the box function to derive FlexType values (it will take care of default values, derived attributes and calculation formula's) and persist newly created material
		LCSMaterialLogic.deriveFlexTypeValues(materialObj);
		LCSMaterialLogic.setFlexTypedDefaults(materialObj, "MATERIAL", "");
		LCSMaterialHelper.service.saveMaterial(materialObj);
		
		// LCSLog.debug("### END HBIMasterMaterialLogic.createMasterMaterial(String materialName, String colorCode, String attributeCode, String sizeCode) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to populate default values as per the type manager configuration, setting material attribute default values using custom code due to out of the box API issues
	 * @param materialObj - LCSMaterial
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private LCSMaterial populateDefaultAttributeValues(LCSMaterial materialObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMasterMaterialLogic.populateDefaultAttributeValues(LCSMaterial materialObj) ###");
		
		materialObj.setValue("materialColorControlled", "2");
		materialObj.setValue(statusKey, "hbiActive");
		materialObj.setValue(HBIProperties.hbiConversionFactorKey, 1.0000);
		materialObj.setValue(HBIProperties.hbiHemisphereKey, "both");
		materialObj.setValue("hbiSubAssembley", "no");
		materialObj.setValue(HBIProperties.hbiLeadTestingRequiredKey, "no");
		materialObj.setValue(HBIProperties.hbiConversionUsageKey, 1.00);
		
		// LCSLog.debug("### END HBIMasterMaterialLogic.populateDefaultAttributeValues(LCSMaterial materialObj) ###");
		return materialObj;
	}
}
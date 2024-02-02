/**
 * 
 */
package com.hbi.wc.interfaces.inbound.esko;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIProductBOMLogic;
import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialLogic;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author Wipro
 *
 */
public class HBIMasterMaterialPlugin {
	
	/**
     * This is static field represents the key of "Master Material" Boolean attribute on FlexPLM 
     */
    static final String HBI_MASTER_MATERIAL_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.MasterMaterial", "hbiMasterMaterial");
    /**
     * This is static field represents the key of "Master Material Code" Text attribute on FlexPLM
     */
    static final String HBI_MASTER_MATERIAL_CODE_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.MasterMaterialCode", "hbiMasterMatCode");
    
    /**
     * This represents the default supplier name to be used in Master Material
     */
    static final String MASTER_MATERIAL_DEFAULT_SUPPLIER_NAME = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.SupplierName.MasterMaterialDefaultSupplierName", "Color Version");
    
    /**
     * This represents hbiMasterMaterialReference attribute key on FlexPLM to be used in Master Material
     */
    static final String HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.MasterMaterialReferenceKey", "hbiMasterMaterialReference");
    /**
     * This represents Garment Material FlexType to be used in Master Material
     */
    static final String GARMENT_LABEL_MATERIAL_FLEXTYPE = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.GarmentLabelFlexType", "Material\\Garment Label");
    /**
     * This represents hbiMatetrialSeq attribute key on FlexPLM to be used in Master Material
     */
    static final String HBI_MATERIAL_SEQUENCE_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.materialSequenceKey", "hbiMatetrialSeq");
    
    /**
     * Logger
     */
    private static final Logger LOGGER = LogR.getLogger("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin");
    /**
     * This represents notRequired list value key on FlexPLM to be used in Material Workflow Process attribute
     */
    private static final String HBI_WORKFLOW_NOT_REQUIRED_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.workFlowNotRequiredKey", "notRequired");
    /**
     * This represents the default attribute code attribute value to be used in Master Material
     */
    static final String MASTER_MATERIAL_DEFAULT_ATTRIBUTE_CODE_VALUE = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.MasterMaterialDefaultAttributeCode", "-------");
    /**
     * This represents the default size code attribute value to be used in Master Material
     */
    static final String MASTER_MATERIAL_DEFAULT_SIZE_CODE_VALUE = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.MasterMaterialDefaultSizeCode","00");
    /**
     * This represents the default color code attribute value to be used in Master Material
     */
    static final String MASTER_MATERIAL_DEFAULT_COLOR_CODE_VALUE = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.MasterMaterialDefaultColorCode", "000");
    /**
     * This represents hbiGarmentSize attribute key on FlexPLM to be used in Master Material
     */
    static final String HBI_GARMENT_SIZE_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.SizeRange", "hbiGarmentSize");
    /**
     * This represents hbiLabelCountry attribute key on FlexPLM to be used in Master Material
     */
    static final String HBI_LABEL_COUNTRY_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.hbiLabelCountry", "hbiLabelCountry");
    /**
     * This represents hbiCountryCode attribute key on FlexPLM to be used in Master Material
     */
    static final String HBI_COUNTRY_CODE_ATTRIBUTE_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin.hbiCountryCode", "hbiCountryCode");
    
	/**This is the main plugin method called on post persist of Material Creation.
	 * @param args
	 * @throws WTException 
	 * @throws WTPropertyVetoException 
	 */
	public static void checkAndCreateMasterMaterial(WTObject wtObject) throws WTException, WTPropertyVetoException {
		LOGGER.debug(">>>>>>>>>>>>>>START checkAndCreateMasterMaterial()>>>>>>>>>>>>");
		LCSMaterial material = (LCSMaterial) wtObject;
		
		//This is the case if the master material is updated\created. 
		if (material.getValue(HBI_MASTER_MATERIAL_ATTRIBUTE_KEY)!=null && (boolean) material.getValue(HBI_MASTER_MATERIAL_ATTRIBUTE_KEY)) {
			LOGGER.debug("**************isMasterMaterial**********"+material.getName());
			return;
		}
		
		LOGGER.debug("material.getFlexType().getFullName()>>>>>>>>>>>>"+material.getFlexType().getFullName());
		//Garment Material Check
		String materialFlexTypeFullName = material.getFlexType().getFullName(true);
		
		if (GARMENT_LABEL_MATERIAL_FLEXTYPE.startsWith(materialFlexTypeFullName)) {
			
			String masterMaterialCode = (String) material.getValue(HBI_MASTER_MATERIAL_CODE_ATTRIBUTE_KEY);
			LOGGER.debug("masterMaterialCode>>>>>>>>>>>"+masterMaterialCode);
			if (FormatHelper.hasContent(masterMaterialCode)) {
				
				LCSMaterial masterMaterialReference =  (LCSMaterial) material.getValue(HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY);
				
				//Check if correct Master Material Reference already exists
				if (masterMaterialReference!=null && masterMaterialReference.getName().equalsIgnoreCase(masterMaterialCode)) {
					HBIMasterMaterialBOMUtil.addPartMaterialToMasterMaterialBOM(masterMaterialReference, material);
					return;
				}
				
				//check and create Material
				LCSMaterial masterMaterial = findMaterialByNameAndMaterialType( masterMaterialCode, material.getFlexType());
				
				if (masterMaterial!=null && !masterMaterial.equals(masterMaterialReference)) {
					
					LOGGER.debug("Master Material Exists");
					LOGGER.debug("masterMaterialReference>>>>>>>>>>>>>>>>>"+masterMaterialReference);
					
					//Add Part Material to Master Material BOM
					HBIMasterMaterialBOMUtil.addPartMaterialToMasterMaterialBOM(masterMaterial, material);
					
					//Remove the Part Material From the Old Master Material BOM if present.
					HBIMasterMaterialBOMUtil.removePartMaterialFromMasterMaterialBOM(masterMaterialReference, material);
					
					//Set the Master Material attribute to false and set the reference material
					material.setValue(HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY, masterMaterial);
					LCSMaterialLogic.persist(material,false);
					
					
				}else {
					LOGGER.debug(">>>>>>>>>>>>>>Creating Master Material>>>>>>>>>>>>>>>>");
					//Call method to create Master Material
					masterMaterial =createMasterMaterial(material);
					
					//Add the default supplier
					new HBIMaterialDataLoader().validateAndProcessSupplierDataLoadRequest(masterMaterial,MASTER_MATERIAL_DEFAULT_SUPPLIER_NAME, new HashMap<String, Object>() );
					
					LOGGER.debug(">>>>>>>>>>>>>>Creating Master Material BOM>>>>>>>>>>>>>>>>");
					FlexBOMPart masterMaterialBOM = HBIMasterMaterialBOMUtil.createMaterialBOM(HBIMasterMaterialBOMUtil.LABEL_BOM_FLEXTYPE, masterMaterial);
					LOGGER.debug(">>>>>>>>>>>>>>Creating Master Material BOM Link>>>>>>>>>>>>>>>>");
					new HBIProductBOMLogic().createFlexBOMLinkForOwner(masterMaterialBOM, material, HBIMasterMaterialBOMUtil.LABELLING_BOM_SECTION_KEY, 1);
					
					//check if old Master Material Reference is present.
					LOGGER.debug("masterMaterialReference>>>>>>>>>>>>>>>>>"+masterMaterialReference);
					//Remove the Part Material From the Old Master Material BOM if Present
					HBIMasterMaterialBOMUtil.removePartMaterialFromMasterMaterialBOM(masterMaterialReference, material);
					
					//Save the Master Material reference in the current material
					material.setValue(HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY, masterMaterial);
					LCSMaterialLogic.persist(material,false);
				}
				
			}else {
				//Master Material is not present hence dont create material and clear the master material reference if any.
				LCSMaterial masterMaterialReference =  (LCSMaterial) material.getValue(HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY);
					if (masterMaterialReference!=null) {
						material.setValue(HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY, null );
						HBIMasterMaterialBOMUtil.removePartMaterialFromMasterMaterialBOM(masterMaterialReference, material);
						LCSMaterialLogic.persist(material,false);
						
					}
			}
		}
		LOGGER.debug(">>>>>>>>>>>>>>END checkAndCreateMasterMaterial()>>>>>>>>>>>>");
	}
	

	public static LCSMaterial findMaterialByNameAndMaterialType(String materialName, FlexType materialFlexTypeObj) throws WTException {
		
		LCSMaterial materialObj = null;	
		String materialNameDBColumn = FlexTypeCache.getFlexTypeRoot("Material").getAttribute("name").getColumnDescriptorName();

		PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.theObjectIdentifier.id"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialNameDBColumn), materialName, Criteria.EQUALS));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), "1");
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "wrk", "<>"));
 
	    if (materialFlexTypeObj != null) {
	        String flexTypeId = FormatHelper.getNumericObjectIdFromObject(materialFlexTypeObj);
	        statement.appendAndIfNeeded();
	        statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSQuery.TYPED_BRANCH_ID), "?", "="), Long.valueOf(flexTypeId));
	      } 
	    LOGGER.debug(">>>>>>>>>findMaterialByNameAndMaterialType.statement>>>>>>>>>>>>>>"+statement);
	    SearchResults results = LCSMaterialQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().elementAt(0);
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.IDA2A2"));
	  	}
	  	
		return materialObj;
	}
	
	
	/**
	 * @param material
	 * @return Master Material
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static LCSMaterial createMasterMaterial(LCSMaterial material) throws WTException, WTPropertyVetoException {
		LOGGER.debug("### START HBIMasterMaterialPlugin.createMasterMaterial ###");
		LCSMaterial masterMaterial = LCSMaterial.newLCSMaterial();
		String masterMaterialCode = (String) material.getValue(HBI_MASTER_MATERIAL_CODE_ATTRIBUTE_KEY);	
		masterMaterial = (LCSMaterial) material.copyState(masterMaterial);
		masterMaterial.setName(masterMaterialCode);
		masterMaterial.setValue(HBIProperties.materialNameKey,masterMaterialCode);
		//set the default attributes for Master Material 
		masterMaterial.setValue(HBIProperties.hbiColorCodeKey, MASTER_MATERIAL_DEFAULT_COLOR_CODE_VALUE);
		masterMaterial.setValue(HBIProperties.hbiAttrCodeKey, MASTER_MATERIAL_DEFAULT_ATTRIBUTE_CODE_VALUE);
		masterMaterial.setValue(HBIProperties.hbiSizeCodeKey, MASTER_MATERIAL_DEFAULT_SIZE_CODE_VALUE);
		masterMaterial.setValue(HBIProperties.hbiWorkflowProcessKey, HBI_WORKFLOW_NOT_REQUIRED_KEY);
		masterMaterial.setValue(HBIProperties.hbiBuyOrNotBuyKey, false);
		masterMaterial.setValue(HBI_MASTER_MATERIAL_ATTRIBUTE_KEY, true);
		masterMaterial.setValue(HBI_MASTER_MATERIAL_REFERENCE_ATTRIBUTE_KEY, null);
		masterMaterial.setValue(HBI_MASTER_MATERIAL_CODE_ATTRIBUTE_KEY, "");
		masterMaterial.setValue(HBI_LABEL_COUNTRY_ATTRIBUTE_KEY, "");
		
		//set Garment Size & Country attribute to blank for master material for Garment Materials
		if (GARMENT_LABEL_MATERIAL_FLEXTYPE.startsWith(material.getFlexType().getFullName(true))) {
			masterMaterial.setValue(HBI_GARMENT_SIZE_ATTRIBUTE_KEY, "");
			masterMaterial.setValue(HBI_COUNTRY_CODE_ATTRIBUTE_KEY, null);
		}
		
		//set the Material Sequence to null to get a new sequence
		masterMaterial.setValue(HBI_MATERIAL_SEQUENCE_ATTRIBUTE_KEY,null);
		LCSMaterialLogic.deriveFlexTypeValues(masterMaterial);
		LOGGER.debug("masterMaterial>>>>>hbiMatetrialSeq>>>>>>>"+(Long)masterMaterial.getValue(HBI_MATERIAL_SEQUENCE_ATTRIBUTE_KEY));
		LOGGER.debug("### END HBIMasterMaterialPlugin.createMasterMaterial ###");
		
		return LCSMaterialHelper.service.saveMaterial(masterMaterial);
	}
	
	}

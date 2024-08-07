/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.carewash.CareWashManager;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareWash;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareWashComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Composition;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CompositionComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CompositionRU;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CompositionRUComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer1Composition;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer1CompositionComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer1CompositionRU;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer1CompositionRUComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer2Composition;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer2CompositionComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer2CompositionRU;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.Layer2CompositionRUComponent;


/**
 * SMCompositionValues - Functionality .
 * 
 * @author 'true' ITC
 * @version 'true' 1.0 version number
 * @since March 23, 2018
 */
public class SMCompositionValues {

	private static final String COMMA = ",";
	private static final String STRING_DELIM = "[|~*~|]";
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCompositionValues.class);
	/**
	 * Composition Key
	 */
	private static final String compositionKey =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.vrdFiberContent","vrdFiberContent");
	/**
	 * Composition Key
	 */
	private static final String contentSrchKey = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.ContentSearch","vrdContentSearch");
	/**
	 * Compostion RU Key
	 */
	private static final String compositionRUKey = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.smCompositionRU");
	/**
	 * layer 1 key
	 */
	private static final String layer1Key = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.smLayer1");
	/**
	 * layer 2 key
	 */
	private static final String layer2Key = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.smLayer2");
	/**
	 * Care Wash
	 */
	private static final String careWashKey = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.smWashCare");
	/**
	 * compositionTypes.
	 */
	private static final String compositionTypes=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.compositiontypes");




	/**
	 * Constructor.
	 */
	protected SMCompositionValues(){
		//Constructor
	}


	/**
	 * This method to set the composition value.
	 * @param material the LCSMaterial.
	 * @return the Composition.
	 * @throws WTException the exception.
	 */
	public static Composition setComposition(LCSMaterial material) throws WTException{
		boolean attrFound=false;
		String attributeType="";

		Composition composition = new Composition();
		String compositionType;
		String compositionvalues = (String)material.getValue(contentSrchKey);
		LOGGER.debug("Composition >>>>>>>>>>>>" +compositionvalues);
		String matType=material.getFlexType().getFullNameDisplay();
		matType=matType.replace("\\", "_");
		matType=matType.replace("&", "");
		matType=matType.replace(" ", "");
		matType=matType.replace("-", "");
		LOGGER.debug(">>material type>>>>>"+matType);

		//list value format"mattype~attributename"
		List<String> compTypeList=FormatHelper.commaSeparatedListToList(compositionTypes);
		for(String listValue:compTypeList) {
			if("Trims_Hook".equals(matType)) {
				break;
			}
			if(listValue.contains(matType)) {
				attrFound=true;

				attributeType = material.getFlexType().getAttribute(listValue.split("~")[1]).getAttVariableType();

				compositionvalues=(String)material.getValue(listValue.split("~")[1]);

				LOGGER.debug("Flex attribute type>>>>>>>"+attributeType);
				break;
			}
		}
		if(!attrFound) {
			compositionType=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material."+matType.split("_")[0]+"commoncompositiontype");
			attributeType=compositionType.split("~")[0];
			compositionvalues=(String)material.getValue(compositionType.split("~")[1]);
		}

		if("composite".equals(attributeType)) {
			createComposition(compositionvalues, composition, true);
		}else{
			createComposition(compositionvalues, composition, false);
		}



		/*if(iscommonCompositionType){
			createComposition(compositionvalues, composition, false);
		}*/

		return composition;
	}

	/**
	 * creats entry for composition and add values to bean class.
	 * @param compositionvalues - String
	 * @param composition - Composition
	 * @param isComposite - boolean
	 */
	private static void createComposition(String compositionvalues,Composition composition,boolean isComposite) {
		CompositionComponent compositionComp ;

		if(FormatHelper.hasContent(compositionvalues)){
			List<String> compList = FormatHelper.commaSeparatedListToList(compositionvalues.replaceAll(STRING_DELIM, COMMA));

			//set  composition.
			for(String compositions : compList){

				compositionComp =  new CompositionComponent();
				compositionComp.setCompositionPercentage(BigDecimal.valueOf(0.0));
				compositionComp.setCompositionItem(compositions);
				//if attribute type is composite, then adding composition percentage values.
				if(isComposite) {
					compositionComp.setCompositionPercentage(BigDecimal.valueOf(getPercentage(compositions)));
					compositionComp.setCompositionItem(getItem(compositions));
				}
				//adding entry to bean
				composition.getCompositionComponent().add(compositionComp);
			}
		}

	}


	/**
	 * This method set composition RU.
	 * @param material the LCSMaterial.
	 * @return the CompositionRU.
	 * @throws WTException the exception.
	 */

	public static CompositionRU setCompositionRU(LCSMaterial material) throws WTException{

		CompositionRU compositionRU  =  new CompositionRU();
		CompositionRUComponent compositionRUComp ;

		String compositionRUvalues = (String)material.getValue(compositionRUKey);
		LOGGER.debug("Composition RU >>>>>>>>>>>>" +compositionRUvalues);

		if(FormatHelper.hasContent(compositionRUvalues)){
			List<String> compRUList = FormatHelper.commaSeparatedListToList(compositionRUvalues.replaceAll(STRING_DELIM, COMMA));
			//compositionRU  =  new CompositionRU();
			//set composition RU.
			for(String compositionsRU : compRUList){

				compositionRUComp =  new CompositionRUComponent();
				compositionRUComp.setCompositionRUPercentage(BigDecimal.valueOf(getPercentage(compositionsRU)));
				compositionRUComp.setCompositionRUItem(getItem(compositionsRU));
				compositionRU.getCompositionRUComponent().add(compositionRUComp);
			}
		}

		return compositionRU;


	}


	/**
	 * This method set Layer1 composition.
	 * @param material the LCSMaterial.
	 * @return the Layer1Composition.
	 * @throws WTException the exception.
	 */
	public static Layer1Composition setLayer1Composition(LCSMaterial material) throws WTException{

		Layer1Composition layer1Composition = null;
		Layer1CompositionComponent layer1CompositionComponent;

		LCSMaterial smLayer1Obj = (LCSMaterial)material.getValue(layer1Key);

		//if layer object null.
		if(smLayer1Obj == null){
			return layer1Composition;
		}



		String layer1Comp = (String)smLayer1Obj.getValue(compositionKey);
		LOGGER.debug("Layer 1 Composition>>>>>>>>>>>>" +layer1Comp+"--"+compositionKey);

		//if compostion value exist for layer 1 then only data is going.
		if(FormatHelper.hasContent(layer1Comp)){
			List<String> layer1CompList = FormatHelper.commaSeparatedListToList(layer1Comp.replaceAll(STRING_DELIM, COMMA));
			layer1Composition = new Layer1Composition();

			layer1Composition.setLayer1MaterialMDMId(SMCareLabelUtil.getMDMID(smLayer1Obj));
			layer1Composition.setLayer1MaterialPLMId(SMCareLabelUtil.getMaterialPLMID(smLayer1Obj));
			layer1Composition.setLayer1MaterialName(smLayer1Obj.getName());

			//set layer 1 composition value
			for(String layer1compositions : layer1CompList){
				layer1CompositionComponent =  new Layer1CompositionComponent();
				layer1CompositionComponent.setLayer1CompositionPercentage(BigDecimal.valueOf(getPercentage(layer1compositions)));
				layer1CompositionComponent.setLayer1CompositionItem(getItem(layer1compositions));
				layer1Composition.getLayer1CompositionComponent().add(layer1CompositionComponent);
			}
		}

		return layer1Composition;
	}

	/**
	 * This method set the Layer 1 Composition RU.
	 * @param material the LCSMaterial.
	 * @return the Layer1CompositionRU.
	 * @throws WTException the exception.
	 */
	public static Layer1CompositionRU setLayer1CompositionRU(LCSMaterial material) throws WTException{

		Layer1CompositionRU layer1CompositionRU = null;
		Layer1CompositionRUComponent layer1CompositionComponentRU;

		LCSMaterial smLayer1Obj = (LCSMaterial)material.getValue(layer1Key);

		//if layer object null.
		if(smLayer1Obj == null){
			return layer1CompositionRU;
		}



		String layer1RUComp = (String)smLayer1Obj.getValue(compositionRUKey);
		LOGGER.debug("Layer 1 Composition RU >>>>>>>>>>>>" +layer1RUComp);

		if(FormatHelper.hasContent(layer1RUComp)){
			List<String> layer1RUCompList = FormatHelper.commaSeparatedListToList(layer1RUComp.replaceAll(STRING_DELIM, COMMA));
			layer1CompositionRU = new Layer1CompositionRU();

			layer1CompositionRU.setLayer1RUMaterialMDMId(SMCareLabelUtil.getMDMID(smLayer1Obj));
			layer1CompositionRU.setLayer1RUMaterialPLMId(SMCareLabelUtil.getMaterialPLMID(smLayer1Obj));
			layer1CompositionRU.setLayer1RUMaterialName(smLayer1Obj.getName());

			//set layer1 composition
			for(String layer1RUcompositions : layer1RUCompList){

				layer1CompositionComponentRU = new Layer1CompositionRUComponent();
				layer1CompositionComponentRU.setLayer1CompositionRUPercentage(BigDecimal.valueOf(getPercentage(layer1RUcompositions)));
				layer1CompositionComponentRU.setLayer1CompositionRUItem(getItem(layer1RUcompositions));
				layer1CompositionRU.getLayer1CompositionRUComponent().add(layer1CompositionComponentRU);
			}
		}


		return layer1CompositionRU;
	}

	/**
	 * This method set the Layer 2 composition.
	 * @param material the LCSMaterial.
	 * @return the Layer2Composition.
	 * @throws WTException the exception.
	 */

	public static Layer2Composition setLayer2Composition(LCSMaterial material) throws WTException{

		Layer2Composition layer2Composition = null;
		Layer2CompositionComponent layer2CompositionComponent ;
		LCSMaterial smLayer2Obj = (LCSMaterial)material.getValue(layer2Key);

		//if layer object null.
		if(smLayer2Obj == null){
			return layer2Composition;
		}



		String layer2Comp = (String)smLayer2Obj.getValue(compositionKey);
		LOGGER.debug("Layer 2 Composition >>>>>>>>>>>>" +layer2Comp+"--"+compositionKey);

		if(FormatHelper.hasContent(layer2Comp)){
			List<String> layer2CompList = FormatHelper.commaSeparatedListToList(layer2Comp.replaceAll(STRING_DELIM, COMMA));
			layer2Composition = new Layer2Composition();
			layer2Composition.setLayer2MaterialMDMId(SMCareLabelUtil.getMDMID(smLayer2Obj));
			layer2Composition.setLayer2MaterialPLMId(SMCareLabelUtil.getMaterialPLMID(smLayer2Obj));
			layer2Composition.setLayer2MaterialName(smLayer2Obj.getName());
			//set layer1 composition
			for(String layer2compositions : layer2CompList){

				layer2CompositionComponent =  new Layer2CompositionComponent();
				layer2CompositionComponent.setLayer2CompositionPercentage(BigDecimal.valueOf(getPercentage(layer2compositions)));
				layer2CompositionComponent.setLayer2CompositionItem(getItem(layer2compositions));
				layer2Composition.getLayer2CompositionComponent().add(layer2CompositionComponent);
			}
		}

		return layer2Composition;
	}


	/**
	 * This method set the Layer2 Composition RU.
	 * @param material the LCSMaterial.
	 * @return the Layer2CompositionRU.
	 * @throws WTException the exception.
	 */
	public static Layer2CompositionRU setLayer2CompositionRU(LCSMaterial material) throws WTException {

		Layer2CompositionRU layer2CompositionRU =null;
		Layer2CompositionRUComponent layer2CompositionComponentRU ;
		LCSMaterial smLayer2RUObj = (LCSMaterial)material.getValue(layer2Key);

		//if layer object null.
		if(smLayer2RUObj == null){
			return layer2CompositionRU;
		}



		//List 
		String layer2CompRU = (String)smLayer2RUObj.getValue(compositionRUKey);
		LOGGER.debug("Layer 2 Composition RU >>>>>>>>>>>>" +layer2CompRU);

		if(FormatHelper.hasContent(layer2CompRU)){
			List<String> layer2CompListRU = FormatHelper.commaSeparatedListToList(layer2CompRU.replaceAll(STRING_DELIM, COMMA));
			layer2CompositionRU = new Layer2CompositionRU();
			layer2CompositionRU.setLayer2RUMaterialMDMId(SMCareLabelUtil.getMDMID(smLayer2RUObj));
			layer2CompositionRU.setLayer2RUMaterialPLMId(SMCareLabelUtil.getMaterialPLMID(smLayer2RUObj));
			layer2CompositionRU.setLayer2RUMaterialName(smLayer2RUObj.getName());
			//set layer2 composition
			for(String layer2compositionsRU : layer2CompListRU){

				layer2CompositionComponentRU =  new Layer2CompositionRUComponent();
				layer2CompositionComponentRU.setLayer2CompositionRUPercentage(BigDecimal.valueOf(getPercentage(layer2compositionsRU)));
				layer2CompositionComponentRU.setLayer2CompositionRUItem(getItem(layer2compositionsRU));
				layer2CompositionRU.getLayer2CompositionRUComponent().add(layer2CompositionComponentRU);
			}
		}


		return layer2CompositionRU;
	}

	/**
	 * This method to set the care wash.
	 * @param material the LCSMaterial.
	 * @return the CareWash.
	 * @throws WTException the exception.
	 */
	public static CareWash setCareWash(LCSMaterial material) throws WTException{

		CareWash careWash = null;
		CareWashComponent careWashComponent ;

		// CareWashManager cm = new CareWashManager();
		CareWashManager.getIndex(careWashKey);
		String washCarevalue=(String)material.getValue(careWashKey);
		String domain="";
		String identifier="";
		LOGGER.debug("Care Wash Value >>>>>>>>>>>>***" +washCarevalue);

		if(FormatHelper.hasContent(washCarevalue)) {
			char washs[]=washCarevalue.toCharArray();
			careWash = new CareWash();
			for(int i=0;i<washs.length;i++) 
			{
				careWashComponent =  new CareWashComponent();
				domain = CareWashManager.getKey(i);
				identifier = String.valueOf(washs[i]);
				//LOGGER.debug("Care Wash domain >>>>>>>>>>>>" +domain);
				//LOGGER.debug("Care Wash Identifier >>>>>>>>'" +identifier);
				careWashComponent.setDomain(domain);
				careWashComponent.setIdentifier(identifier);
				careWash.getCareWashComponent().add(careWashComponent);

			}
		}

		return careWash;
	}

	/**
	 * The get Item
	 * @param compositions the String.
	 * @return the String.
	 */
	private static String getItem(String compositions) {

		String temp1[] = ((String)compositions).split("%");
		return String.valueOf(temp1[1]).trim();
	}

	/**
	 * The get percentage.
	 * @param compositions the String.
	 * @return the Float.
	 */
	private static Float getPercentage(String compositions) {

		float percetage = 0f;
		String temp2[] = ((String)compositions).split("%");
		if(FormatHelper.hasContent(temp2[0])){
			//percetage = Float.valueOf(Float.parseFloat(temp2[0]));
			percetage = Float.parseFloat(temp2[0]);

		}
		return percetage;

	}
}

/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;
import wt.util.WTException;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.BOMMaterialAttributes;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelReportRequest;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.ProductBOM;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.ProductBOMComponent;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.ProductBOMComponentDestination;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCompositionValues;

/**
 * SMCareLabelRequestProcessor.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since Feb 23, 2018
 */
public class SMCareLabelRequestProcessor {

	private static final String DELIM = "[|~*~|]";

	/**
	 * The Fabric.
	 */

	private static final String FABRIC = "Material\\Fabric";

	/**
	 * Composition Key
	 */
	private static final String contentSrchKey = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.ContentSearch","vrdContentSearch");
	
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER =  Logger.getLogger(SMCareLabelRequestProcessor.class);
	
	/**
	 * Compostion RU Key
	 */
	private static final String compositionRUKey = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.smCompositionRU");



	/**
	 * Constructor.
	 */
	protected SMCareLabelRequestProcessor(){
		//protected constructor.
	}


	/**
	 * This method sets the Product BOM data to bean.
	 * @param careLabelRequest the request.
	 * @param careLabelDatabyProd the List.
	 * @return the ProductBOM.
	 * @throws DatatypeConfigurationException the exception.
	 * @throws WTException the exception.
	 */
	public ProductBOM setProductBOMData(
			CareLabelReportRequest careLabelRequest, List careLabelDatabyProd) throws DatatypeConfigurationException, WTException {

		ProductBOM productBOM = new ProductBOM();
		com.lcs.wc.flexbom.FlexBOMPart bomPart = null;
		//FlexObject eachRowBOMdata=null;
		LCSSKU skuObj = null;
		LCSSourceToSeasonLink sourceseaosnlink=null;
		LCSSKUSeasonLink skuSeasonLink=null;
		LCSSourcingConfig source=null;
		String scOid = "";
		String seasOid = "";
		LCSColor colorObj = null;
		LCSSupplier businessSupp=null;
		LCSSupplier factorySupp=null;
		ProductBOMComponent prodBomComponentBean;

		//Itertor each row of the report row and set to the product BOM data
		//group by Colorway
		Map groupByColorway = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(careLabelDatabyProd, "LCSSKUSEASONLINK.IDA2A2");

		LOGGER.debug("Number of colorways for the product >>>>" +groupByColorway.size());
		Iterator coloBOMItr = groupByColorway.entrySet().iterator();
		while(coloBOMItr.hasNext()){
			Map.Entry colBOMEntry1=(Map.Entry) coloBOMItr.next();


			//Group by BOM Part
			Map colBOMEntry2 = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(((ArrayList)colBOMEntry1.getValue()), "FLEXBOMPART.IDA2A2");
			Iterator coloBOMItr2 = colBOMEntry2.entrySet().iterator();
			LOGGER.debug("Number of BOM part for the Colorway >>>>>>>>>>>" +colBOMEntry2.size());
			while(coloBOMItr2.hasNext()){

				Map.Entry colBOMCol=(Map.Entry) coloBOMItr2.next();
				FlexObject colBOMObj=(FlexObject) ((ArrayList)colBOMCol.getValue()).get(0);

				//initializing the product BOM bean
				productBOM = new ProductBOM();


				//object ID
				scOid = (String)colBOMObj.getData("LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO");
				seasOid = (String)colBOMObj.getData("LCSPRODUCTSEASONLINK.SEASONREVID");

				//Get objects
				skuObj= (LCSSKU)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+colBOMObj.getString("LCSSKU.BRANCHIDITERATIONINFO"));
				bomPart = (com.lcs.wc.flexbom.FlexBOMPart) LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMPart:" +colBOMObj.getString("FlexBOMPart.IDA2A2"));
				skuSeasonLink = (LCSSKUSeasonLink) LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSKUSeasonLink:" +colBOMObj.getString("LCSSKUSEASONLINK.IDA2A2"));
				source=(LCSSourcingConfig)LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:"+scOid);
				colorObj =  (LCSColor)skuObj.getValue("color");

				LOGGER.debug("Start processing for the colorway >>>>>>>>>>> " +skuObj.getName() + " BOM Name >>> " +bomPart.getName());
				//Set BOM level data.
				productBOM.setBOMName(bomPart.getName());
				productBOM.setBOMStatus(colBOMObj.getString(bomPart.getFlexType().getAttribute(SMCareLabelConstants.BOM_STATUS).getSearchResultIndex()));

				//set order destination.
				setOrderDestination(productBOM, colBOMObj, scOid, seasOid);

				//set colorway data.
				productBOM.setColorwayMDMId(SMCareLabelUtil.getMDMID(skuObj));
				productBOM.setColorwayPLMId(SMCareLabelUtil.getColorwayPLMID(skuObj));

				//set colorway color data
				productBOM.setColorwayColor(colorObj.getName());
				productBOM.setColorwayColorMDMId(SMCareLabelUtil.getMDMID(colorObj));

				//Set colorway season data
				productBOM.setColorwaySeasonMDMId(SMCareLabelUtil.getMDMID(skuSeasonLink));
				productBOM.setColorwaySeasonPLMId(SMCareLabelUtil.getColorwaySeasonPLMID(skuSeasonLink));


				//Setting Business supplier.
				businessSupp = (LCSSupplier)source.getValue(SMCareLabelConstants.VENDOR);
				if(businessSupp!=null){
					productBOM.setBusinessSupplierMDMId(SMCareLabelUtil.getMDMID(businessSupp));
					productBOM.setBusinessSupplierPLMId(SMCareLabelUtil.getSupplierPLMID(businessSupp));
					productBOM.setBusinessSupplierName(businessSupp.getName());
				}


				//Setting Factory data.
				sourceseaosnlink = SMCareLabelUtil.getSourcetoSeasonLink(scOid, seasOid);
				factorySupp = (LCSSupplier)sourceseaosnlink.getValue(SMCareLabelConstants.FACTROTY);
				if(factorySupp != null){
					productBOM.setFactoryMDMId(SMCareLabelUtil.getMDMID(factorySupp));
					productBOM.setFactoryPLMId(SMCareLabelUtil.getSupplierPLMID(factorySupp));
					productBOM.setFactoryName(factorySupp.getName());
				}

				//setting BOM last updated date.
				productBOM.setLastUpdated(SMCareLabelUtil.getXMLGregorianCalendarFormat(bomPart.getModifyTimestamp()));

				//Setting BOM Link level data
				Map bomRowsByBranchId= com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(((ArrayList)colBOMCol.getValue()),"FLEXBOMLINK.BRANCHID");
				
				//Map bomRowsByBranchId = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(careLabelDatabyProd, "FLEXBOMLINK.BRANCHID");
				
				Iterator bomRowsItr = bomRowsByBranchId.entrySet().iterator();
				while(bomRowsItr.hasNext()){
					LOGGER.debug("BOM Link size by Colorway >>>>"  +bomRowsByBranchId.size());
					Map.Entry me=(Map.Entry) bomRowsItr.next();	
					
					Collection<FlexObject> bomRowsCol=(Collection<FlexObject>) me.getValue();
					for(FlexObject row:bomRowsCol){
 						prodBomComponentBean = setProductBOMComponent(row);
						productBOM.getProductBOMComponent().add(prodBomComponentBean);
					}				
				
				}
				
				//Set Bom part branchiditerationinfo for 3.8.1.0 build - Start
				productBOM.setBOMPartBranchID(colBOMObj.getString("FLEXBOMPART.BRANCHIDITERATIONINFO"));
				//Set Bom part branchiditerationinfo for 3.8.1.0 build - End

				//setting product BOM to the request.
				careLabelRequest.getProductBOM().add(productBOM);
			}



		}

		return productBOM;
	}

	/**
	 * This method setting order destination multi list.
	 * @param productBOM the ProductBOM.
	 * @param eachRowBOMdata the FlexObject.
	 * @param scOid the String.
	 * @param seasOid the String.
	 * @throws WTException the exception.
	 */
	private void setOrderDestination(
			ProductBOM productBOM, FlexObject eachRowBOMdata, String scOid,
			String seasOid) throws WTException {
		LCSSourceToSeasonLink sourceseaosnlink;

		//get product-season link.
		sourceseaosnlink = SMCareLabelUtil.getSourcetoSeasonLink(scOid, seasOid);
		String srcOrderDestination = eachRowBOMdata.getString(sourceseaosnlink.getFlexType().getAttribute(SMCareLabelConstants.ORDER_DESTINATION).getSearchResultIndex());
		if(FormatHelper.hasContent(srcOrderDestination)){

			List<String>  orderDestinationSourcing = FormatHelper.commaSeparatedListToList(srcOrderDestination.replaceAll(DELIM, ","));

			//set order destination.
			for(String destination : orderDestinationSourcing){
				//LOGGER.info("Order destination for Soure to season  ********  "+destination);
				productBOM.getSmOrderDestination().add(destination);
			}
		}
	}


	/**
	 * This method to set the Additional Care information.
	 * @param bomMatAttribute the BOMMaterialAttributes.
	 * @param eachRowBOMdata the FlexObject.
	 * @param materila the material.
	 * @throws WTException the exception.
	 */
	private void setAdditionalCare(
			BOMMaterialAttributes bomMatAttribute, FlexObject eachRowBOMdata) throws WTException {
		//List<String> additionalCares = new ArrayList<String>();

		String matColoId = eachRowBOMdata.getString("LCSMATERIALCOLOR.IDA2A2");
			
		//if material color not exist.
		if (!FormatHelper.hasContent(matColoId)){
			LOGGER.debug("Material Color Id not found rerun >>>>");
			return ; 
		}

		com.lcs.wc.material.LCSMaterialColor matCol =  (com.lcs.wc.material.LCSMaterialColor)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:" +matColoId);
		
		LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier) LCSQuery
				.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:"
						+ eachRowBOMdata.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO"));
		// When overridden row material supplier is changed without changing the color, the material color attribute value has to be blanked out in overridden row in report
		LCSMaterialSupplier materialSupplierForMatCol = null;
		materialSupplierForMatCol = VersionHelper.latestIterationOf(matCol.getMaterialSupplierMaster());
		if(null != materialSupplierForMatCol && null != materialSupplier && materialSupplierForMatCol.toString().equals(materialSupplier.toString())) {
			//get additional care data.
			String matAdditinalCare="";
			if(matCol != null){
				matAdditinalCare = (String)matCol.getValue(SMCareLabelConstants.ADDITIONALI_CARE);
			}
			if(FormatHelper.hasContent(matAdditinalCare)){

				List<String> additionalCares = FormatHelper.commaSeparatedListToList(matAdditinalCare.replaceAll(DELIM, ","));

				//set additional care.
				for(String addCare : additionalCares){
					bomMatAttribute.getSmAdditionalCareMC().add(addCare);
				}
			}
		}
	}

	/**
	 * This method set the Product BOM level data .
	 * @param eachRowBOMdata the FlexObject.
	 * @return the ProductBOMComponent.
	 * @throws WTException
	 */
	private ProductBOMComponent setProductBOMComponent( FlexObject eachRowBOMdata) throws WTException {

		ProductBOMComponent prodBOMComp = new ProductBOMComponent();


		FlexBOMLink bomLink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" +eachRowBOMdata.getString("FLEXBOMLINK.IDA2A2"));


		String smCompNameAttColumn = bomLink.getFlexType().getAttribute(SMCareLabelConstants.COMPONENET_NAME).getSearchResultIndex();
		String smPlaceAttColumn = bomLink.getFlexType().getAttribute(SMCareLabelConstants.PART_NAME).getSearchResultIndex();

		//setting component name
		if(FormatHelper.hasContent(smCompNameAttColumn) && FormatHelper.hasContent(eachRowBOMdata.getString(smCompNameAttColumn)) ){
			prodBOMComp.setComponentName(eachRowBOMdata.getString(smCompNameAttColumn));
		}

		//setting placement.
		if(FormatHelper.hasContent(smPlaceAttColumn) && FormatHelper.hasContent(eachRowBOMdata.getString(smPlaceAttColumn))){
			prodBOMComp.setPlacement(eachRowBOMdata.getString(smPlaceAttColumn));
		}

		//set BOM Component destination - BOM link data.
		//ProductBOMComponentDestination productBOMComDest = new ProductBOMComponentDestination();
		ProductBOMComponentDestination productBOMComDestBean = setproductBOMLinkData(eachRowBOMdata,bomLink);

		prodBOMComp.getProductBOMComponentDestination().add(productBOMComDestBean);


		return prodBOMComp;
	}



	/**
	 * This method sets data for BOM link.
	 * @param eachRowBOMdata the FlexObject.
	 * @param bomLink the FlexBOMlink.
	 * @return the ProductBOMComponentDestination.
	 * @throws WTException
	 */
	private ProductBOMComponentDestination setproductBOMLinkData(
			FlexObject eachRowBOMdata, FlexBOMLink bomLink) throws WTException {

		ProductBOMComponentDestination productBOMComDest = new ProductBOMComponentDestination();


		String smQuantityAttColumn = bomLink.getFlexType().getAttribute(SMCareLabelConstants.QUANTITY).getSearchResultIndex();
		Double consumtion = eachRowBOMdata.getDouble(smQuantityAttColumn);
		String smPrimaryAttColumn = bomLink.getFlexType().getAttribute(SMCareLabelConstants.PRIMARY).getSearchResultIndex();
		String smPrimaryAltAttColumn = bomLink.getFlexType().getAttribute(SMCareLabelConstants.ALT_PRIMARY).getSearchResultIndex();
		String smCCCAttColumn =bomLink.getFlexType().getAttribute(SMCareLabelConstants.CCC).getSearchResultIndex();

		//set destination variation, if exist.
		productBOMComDest.setDestination(eachRowBOMdata.getString("PRODUCTDESTINATION.DESTINATIONNAME"));

		//set Primary PLM ID
		productBOMComDest.setPrimary(eachRowBOMdata.getBoolean(smPrimaryAttColumn));
		//set Alt Primary PLM ID
		productBOMComDest.setAltPrimary(eachRowBOMdata.getBoolean(smPrimaryAltAttColumn));
		//set cosumption/quantity.
		productBOMComDest.setConsumption(java.math.BigDecimal.valueOf(consumtion));
		//set CCC value.
		productBOMComDest.setContrastColorCombination(eachRowBOMdata.getBoolean(smCCCAttColumn));

		//Set color name, Material - color
		if(FormatHelper.hasContent(eachRowBOMdata.getString("LCSCOLOR.COLORNAME"))){
		productBOMComDest.setColorBOMComponent(eachRowBOMdata.getString("LCSCOLOR.COLORNAME"));
		}

		//Set Colorway Color BOM Componenet. not seeing in the bean class colorwayColorBOMComponent
		LOGGER.debug("Colorway Variation ID >>>>>>>>>>>"+bomLink.getColorDimension());
		LOGGER.debug("Variation name >>>>>>>>>>>"+bomLink.getDimensionName());

		String dimension = bomLink.getDimensionName();
		LCSSKU colorDimension=null;

		if(dimension!=null && ((":SKU").equals(dimension)))//if there is  Colorway variation
		{
			com.lcs.wc.part.LCSPartMaster skuMaster =(com.lcs.wc.part.LCSPartMaster)bomLink.getColorDimension();
			colorDimension = (LCSSKU)com.lcs.wc.util.VersionHelper.latestIterationOf((wt.vc.Mastered)skuMaster);
			productBOMComDest.setColorwayColorBOMComponent((String)colorDimension.getValue("skuName"));

		}




		//Set BOM Material attributes.
		BOMMaterialAttributes bomAttributesBean = setBOMattributes(eachRowBOMdata );
		productBOMComDest.setBOMMaterialAttributes(bomAttributesBean);
		
		//Set Bom Link Branch ID for 3.8.1.0 build - Start
		productBOMComDest.setBOMLinkBranchID(eachRowBOMdata.getString("FLEXBOMLINK.BRANCHID"));
		//Set Bom Link Branch ID for 3.8.1.0 build - End



		return productBOMComDest;
	}



	/**
	 * This method set the BOM material level data.
	 * @param eachRowBOMdata the FlexObject.
	 * @return the BOMMaterialAttributes.
	 * @throws WTException the exception.
	 */
	private BOMMaterialAttributes setBOMattributes(
			FlexObject eachRowBOMdata) throws WTException {

		BOMMaterialAttributes bomAttributes =  new BOMMaterialAttributes();

		LCSMaterial material = (LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" +eachRowBOMdata.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
		
		material = (LCSMaterial) com.lcs.wc.util.VersionHelper.latestIterationOf(material.getMaster());
		//get supplier object from material supplier
		LCSSupplier supplier = SMCareLabelUtil.getSupplier(eachRowBOMdata);

		String smMatTypeAttColumn=material.getFlexType().getAttribute(SMCareLabelConstants.MATERIAL_TYPE).getSearchResultIndex();
		String smUnitOfMeasureAttColumn=material.getFlexType().getAttribute(SMCareLabelConstants.UNIT_MEASURE).getSearchResultIndex();

		//Set Material MDM ID.
		bomAttributes.setMaterialMDMId(SMCareLabelUtil.getMDMID(material));
		//Set Material PLM ID.
		bomAttributes.setMaterialPLMId(SMCareLabelUtil.getMaterialPLMID(material));
		//Set Material Name.
		bomAttributes.setMaterialName(material.getName());
		//set Material Type.
		bomAttributes.setMaterialType(eachRowBOMdata.getString(smMatTypeAttColumn));

		//set supplier MDM ID
		bomAttributes.setMaterialSupplierMDMId(SMCareLabelUtil.getMDMID(supplier));
		//set supplier PLM ID
		bomAttributes.setMaterialSupplierPLMId(SMCareLabelUtil.getSupplierPLMID(supplier));
		//set unit of measure.
		if(FormatHelper.hasContent(smUnitOfMeasureAttColumn) && FormatHelper.hasContent(eachRowBOMdata.getString(smUnitOfMeasureAttColumn))){
			bomAttributes.setUnitOfMeasure(eachRowBOMdata.getString(smUnitOfMeasureAttColumn));
		}
		
		//set managing department for 3.8.1.0 build Start.	
		String smManagingDeprt = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialSupplier").getAttribute(SMCareLabelConstants.MANAGING_DEPARTMENT).getSearchResultIndex();
		
		if(FormatHelper.hasContent(smManagingDeprt) && FormatHelper.hasContent(eachRowBOMdata.getString(smManagingDeprt))){
			bomAttributes.setManagingDepartment(eachRowBOMdata.getString(smManagingDeprt));
		}
		//set managing department for 3.8.1.0 build End.

		//Checking Flex type, setting composition values only for Fabric.
		LOGGER.debug("Material Flex type >>>>>>>>>>" +material.getFlexType().getFullNameDisplay());

		//set all Fabric level data.
		setFabricleveldata(eachRowBOMdata, bomAttributes, material);

		//set all Fabric/Other level data.
		setFabricOtherLevelData(bomAttributes, material);


		return bomAttributes;
	}

	/**
	 * This method sets all Fabric level data.
	 * @param bomAttributes the BOMMaterialAttributes.
	 * @param material the LCSMaterial.
	 * @throws WTException the exception.
	 */
	private void setFabricOtherLevelData(
			BOMMaterialAttributes bomAttributes, LCSMaterial material)
					throws WTException {
		if((material.getFlexType().getFullNameDisplay()).contains("Fabric\\Other")){

			LOGGER.debug(">>>>>>>> Setting values for Fabric/Others material Type >>>>>>>>");
			//set Layer1Composition
			bomAttributes.setLayer1Composition(SMCompositionValues.setLayer1Composition(material));


			//set Layer2Composition
			bomAttributes.setLayer2Composition(SMCompositionValues.setLayer2Composition(material));


			//set Layer1CompositionRU
			bomAttributes.setLayer1CompositionRU(SMCompositionValues.setLayer1CompositionRU(material));


			//set Layer2CompositionRU
			bomAttributes.setLayer2CompositionRU(SMCompositionValues.setLayer2CompositionRU(material));
		}
	}

	/**
	 * The Fabric/Other level data.
	 * @param eachRowBOMdata the FlexObject.
	 * @param bomAttributes the BOMMaterialAttributes.
	 * @param material the LCSMaterial.
	 * @throws WTException the exception.
	 */
	private void setFabricleveldata(FlexObject eachRowBOMdata,
			BOMMaterialAttributes bomAttributes, LCSMaterial material)
					throws WTException {
		LOGGER.debug("setting material composition attribute value");
		System.out.println("contentSrchKey>>>>>>>>>>>>>>>"+contentSrchKey);
		//Set Composition
		if(FormatHelper.hasContent(String.valueOf(material.getValue(contentSrchKey)))){
			bomAttributes.setComposition(SMCompositionValues.setComposition(material));
		}
		
		if(material.getFlexType().getFullName(true).contains(FABRIC) ){

			LOGGER.debug(">>>>>>>> Setting values for Fabric material Type >>>>>>>>");

			String vrdFinish = (String)material.getValue(SMCareLabelConstants.FINISH);
			String laminCoating = (String)material.getValue(SMCareLabelConstants.LAMINATION_COATING);
			
			
			//set Lamination coating.
			if(FormatHelper.hasContent(laminCoating) ){
				
				setLaminationCoating(bomAttributes, laminCoating);
				
			}

			//set Finish.
			if(FormatHelper.hasContent(vrdFinish) ){
				setFinish(bomAttributes, vrdFinish);
			}

			//Set Additional care Material color
			setAdditionalCare(bomAttributes, eachRowBOMdata);

			

			//set CompositionRU
			if(FormatHelper.hasContent(String.valueOf(material.getValue(compositionRUKey)))){
				bomAttributes.setCompositionRU(SMCompositionValues.setCompositionRU(material));
			}

			//set CareWash
			bomAttributes.setCareWash(SMCompositionValues.setCareWash(material));
		}
	}
	
	public static void setComposition() {
		
	}

	/**
	 * This method is set the lamination coating
	 * @param bomAttributes
	 * @param laminCoating
	 */
	private void setLaminationCoating(BOMMaterialAttributes bomAttributes,
			String laminCoating) {
		
			List<String> laminCoatingColl = FormatHelper.commaSeparatedListToList(laminCoating.replaceAll(DELIM, ","));

			//set lamination coating.
			for(String lamCoat : laminCoatingColl){
				bomAttributes.getSmLaminationCoating().add(lamCoat);
			}
		
	}

	
	/**
	 * This method is set the Finish
	 * @param bomAttributes
	 * @param laminCoating
	 */
	private void setFinish(BOMMaterialAttributes bomAttributes,
			String vrdFinish) {
		
			List<String> vrdFinishColl = FormatHelper.commaSeparatedListToList(vrdFinish.replaceAll(DELIM, ","));
			//set Finish.
			for(String finish : vrdFinishColl){
				bomAttributes.getVrdFabricFinish().add(finish);
			}
	  }

}

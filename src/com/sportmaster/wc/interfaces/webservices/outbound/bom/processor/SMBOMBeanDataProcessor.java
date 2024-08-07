/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.processor;

import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.lcs.wc.client.web.TableDataUtil;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.product.ReferencedTypeKeys;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.FlexObjectUtil;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMPart;
import com.sportmaster.wc.interfaces.webservices.bombean.BomLinkVariation;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundWebServiceConstants;

import wt.fc.ObjectIdentifier;
import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author ITC_Infotech.
 *
 */
public class SMBOMBeanDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBOMBeanDataProcessor.class);
	private static final String MATERIAL_NUM = "vrdMaterialNum";
	/**
	 * protected constructor.
	 */
	protected SMBOMBeanDataProcessor(){
		//protected constructor.
	}

	/**
	 * set data on product bean.
	 * @param product - LCSProduct
	 * @param prodInfoRequest - Product
	 * @param psl - LCSProductSeasonLink
	 * @throws WTException - WTException
	 * @throws DatatypeConfigurationException
	 * @throws IOException 
	 * @throws ServiceException 
	 * @throws WTPropertyVetoException 
	 * @throws SQLException 
	 */
	public  BOMPart setDataForBOMPartBean(FlexBOMPart bomPart, BOMPart bomRequest, 
			SMBOMOutboundIntegrationBean bean)
			throws WTException, DatatypeConfigurationException, IOException, WTPropertyVetoException, ServiceException, SQLException {

			
			LOGGER.info("Setting data on bom bean     --------");

			//if (bomPart.getBomType())
			LOGGER.info("BOM Branch ID  set on Bean   >>>>>>>    "+bomPart.getBranchIdentifier());
			bomRequest.setBomPartBranchID(String.valueOf(bomPart.getBranchIdentifier()));

			bomRequest.setBomName(bomPart.getName());
			
			if (FormatHelper.hasContent(String.valueOf(bomPart.getValue(SMBOMOutboundWebServiceConstants.BOM_STATUS)))) {
				
				bomRequest.setBomStatus(
						String.valueOf(bomPart.getValue(SMBOMOutboundWebServiceConstants.BOM_STATUS)));

			}

			bomRequest.setCreatedON(bean.getBomUtill().getXMLGregorianCalendarFormat(bomPart.getCreateTimestamp()));
			//set created by.
			bomRequest.setCreatedBy(bean.getBomProcessor().getBOMCreator(bomPart));
			//set last updated on.
			bomRequest.setLastUpdated(bean.getBomUtill().getXMLGregorianCalendarFormat(bomPart.getModifyTimestamp()));
			//set last updated by.
			bomRequest.setLastUpdatedBy(bean.getBomProcessor().getBOMModifier(bomPart));
			
			setDataForAssociatedObjects(bomPart,bomRequest, bean);

			//bomLinkVariation
			//setBomLinkVariation ();
			bomRequest = processBOMLinksAtSKUSizeLevel(bomPart, bomRequest,  bean);
			
			
			return bomRequest;
		//}
	}

	/**
	 * 
	 * @param bomPart
	 * @param bomRequest
	 * @param bean
	 * @return
	 */

	public BOMPart processBOMLinksAtSKUSizeLevel (FlexBOMPart bomPart,
			BOMPart bomRequest, SMBOMOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException, ServiceException, RemoteException, DatatypeConfigurationException {
		
		//FlexBOMPart bomPart = null;
		LOGGER.debug("****** Inside processBOMLinksAtSKUSizeLevel ******");
		wt.part.WTPartMaster wtpartMaster= (WTPartMaster) bomPart.getOwnerMaster();
		LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(wtpartMaster);
		LOGGER.debug("product name = "+product.getName());
		LOGGER.debug("product version = "+product.getVersionDisplayIdentifier());
		SizingQuery sizingQUery =  new SizingQuery();
		SearchResults psdResults =	sizingQUery.findPSDByProductAndSeason( product) ;
				
		List<String> psdSizeList = null;
		String size = "";
		FlexObject fo = null;
	
		if (psdResults != null) {
		Iterator<?> sizeDefIter = psdResults.getResults().iterator();
		if (sizeDefIter.hasNext()) {
			fo = (com.lcs.wc.db.FlexObject) sizeDefIter.next();
			String psdSizeValues = fo.getString("PRODUCTSIZECATEGORY.SIZEVALUES");
			psdSizeList = Arrays.asList(psdSizeValues.split("\\|\\~\\*\\~\\|"));
				
			LOGGER.debug("PSD size list = "+psdSizeList);	
			
			//set size plm id on the bean
			bean.setSizeDefinitionPLMID(fo.getString("PRODUCTSIZECATEGORY.IDA2A2"));
			}
		}
	
		
		

		
		FlexBOMPart bomPartLatest = (FlexBOMPart)VersionHelper.latestIterationOf(bomPart);
		Collection bomLinkDataColl = fetchAllBOMLinks (bomPartLatest);
		
		List<LCSSKU> colorwayList =  (List<LCSSKU>) LCSSKUQuery.findSKUs(product);		
		LOGGER.debug("colorwayList = "+colorwayList);
		Iterator colorwayIter = colorwayList.iterator();
		
		//Iterate Colorway and PSD Size list and fetch the bomlinks for the combination 
		while (colorwayIter.hasNext()) {
			
			LCSSKU colorway = (LCSSKU) colorwayIter.next();
			colorway = (LCSSKU) VersionHelper.latestIterationOf(colorway);
			String strColorMasterOID = FormatHelper.getNumericObjectIdFromObject(
					(WTObject)colorway.getMaster());
			LOGGER.debug ("Colorway Master oid ="+strColorMasterOID);
			
			bean = setColorwaySeasonInfo(colorway,bean);
			
			if (psdSizeList != null) {
				Iterator psdSizeIter = psdSizeList.iterator();	
				while (psdSizeIter.hasNext()) {
					size = (String) psdSizeIter.next();
					LOGGER.debug ("PSD Size1 :"+size);
					
					//set size info on the bean
					bean.setSizeName(size);
					
					bomRequest = setBomLinkVariation(bomPartLatest,bomLinkDataColl,strColorMasterOID,size, bomRequest,  bean);
				}
				
			}


		}
	
		return bomRequest;
	}
	
	/**
	 * 
	 * @param bomPart
	 * @return
	 * @throws WTException
	 */
	
	public Collection fetchAllBOMLinks (FlexBOMPart bomPart) throws WTException {
		LOGGER.debug("****** Inside fetchAllBOMLinks ******");
		String scMasterId = null;
		String skuMasterId = null;
		String size1 = null;
		String size2 = null;
		String destinationId = null;
		String wipMode = null;
		Date effectiveDate = null;
		boolean dropped = false;
		boolean linkDataOnly = false;
		String dimensionMode = LCSFlexBOMQuery.ALL_DIMENSIONS;
		String skuMode = "";
		String sourceMode = "";
		String sizeMode = "";
		
		Collection bomLinkDataColl= LCSFlexBOMQuery.findFlexBOMData(bomPart,
					scMasterId,skuMasterId, size1,size2, destinationId, wipMode, effectiveDate,
					dropped,linkDataOnly, dimensionMode,skuMode,sourceMode,sizeMode).getResults();
		
			
		LOGGER.debug("All BOM Data collection: "+bomLinkDataColl.size());
		
		return 	bomLinkDataColl;

	}
	
	
	

	
	
	/**
	 * This  method  process all the BO's and gets the corresponding attribute values and 
	 * set it to the bean and send it to PI interface.
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws ServiceException 
	 * @throws RemoteException
	 * @throws DatatypeConfigurationException 
	 * 
	 */	
	public BOMPart setBomLinkVariation(FlexBOMPart bomPart,Collection bomLinkDataColl, String strColorMasterOID, String size,
			BOMPart bomRequest, SMBOMOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException, ServiceException, RemoteException, DatatypeConfigurationException
	{
		LOGGER.debug("**************inside setBOMData method**********************");
				
			if (bomLinkDataColl != null && !bomLinkDataColl.isEmpty())
			{
				bomLinkDataColl = TableDataUtil.concatIndexes(bomLinkDataColl, 
						"FLEXBOMLINK.branchId", "FLEXBOMLINK.dimensionname", "BRANCH_DIM");
				LOGGER.debug("After adding BRANCH_DIM to collection: "+bomLinkDataColl.size());

				// -- Create a collection of topLevelRows and allVariationRows --//
				Map dimensionMap = FlexObjectUtil.groupIntoCollections(bomLinkDataColl, 
						"FLEXBOMLINK.DIMENSIONNAME");
				Map branchDimMap = TableDataUtil.groupIntoCollections(bomLinkDataColl, "BRANCH_DIM");
				LOGGER.debug("branchDimMap === "+branchDimMap.size());

				Collection<FlexObject> topRows = (Collection) dimensionMap.get("");
				
				if (topRows == null) {
					topRows = new ArrayList();
				}
				topRows = SortHelper.sortFlexObjects(topRows, 
						bomPart.getFlexType().getAttribute("section").getSearchResultIndex());
				topRows = SortHelper.sortFlexObjects(topRows, "FLEXBOMLINK.SORTINGNUMBER");
				topRows = SortHelper.sortFlexObjects(topRows, "FLEXBOMLINK.BRANCHID");
				
				LOGGER.debug("Original dimensionMap size = "+dimensionMap.size());
				LOGGER.debug("original topRows size = "+topRows.size());

				

				FlexObject topLevelObj = null;
				FlexObject ovrObj = null;
				String branchId = "";
				Collection colorwayOvrCol = null;
				Collection colorwaySizeOvrCol = null;
				Collection sizeOvrCol = null;
				FlexBOMLink childLink = null;
				
				//This collection is for processing all the top-level,colorway and 
				//colorway-size variations
			
				Iterator<?> topLevelRows = topRows.iterator();
				String idA2A2 = "";
				String strBomId = "";
				int rowCount = 0;
				String ovrSKUMasterOID = "";
				String ovrSize = "";
				
				
				
				// -- Loop the top level rows and fetch the topLevel flex Object 
				// -- Setting BomLinkVariation Array --

				
				while (topLevelRows.hasNext())
				{
					childLink = null;
					boolean overrideExists = false;
					boolean parentProcessed = false;
					boolean childProcessed = false;
					
					topLevelObj = (FlexObject) topLevelRows.next();
					if (topLevelObj != null)
					{
						LOGGER.debug("************************************");
						LOGGER.debug("Processing TOPLEVEL Link ..");
						LOGGER.debug("************************************");
						rowCount++;
						idA2A2 = topLevelObj.getString("FLEXBOMLINK.IDA2A2");
						strBomId = new ObjectIdentifier(FlexBOMLink.class, 
								Long.parseLong(idA2A2)).toString();
						FlexBOMLink parentLink = (FlexBOMLink) LCSQuery.findObjectById(strBomId);
						
						//bomLinkVariation.setBOMLinkBranchID(String.valueOf(parentLink.getBranchId()));

						// -- Fetch the Colorway/Size Variations for this Top-Level Link ---
						branchId = topLevelObj.getString("FLEXBOMLINK.BRANCHID");
						colorwaySizeOvrCol = (Collection<?>) branchDimMap.get(branchId + ":SKU:SIZE1");
						sizeOvrCol = (Collection<?>) branchDimMap.get(branchId + ":SIZE1");
						colorwayOvrCol = (Collection<?>) branchDimMap.get(branchId + ":SKU");
						//LOGGER.debug("topLevel flexobject = "+topLevelObj);
						//LOGGER.debug("Size over Collection is "+sizeOvrCol);
						//LOGGER.debug("colorwaySizeOvrColCollection is "+colorwaySizeOvrCol);
						//LOGGER.debug("colorwayOvrCol Collection is "+colorwayOvrCol);
						LOGGER.debug(" strColorMasterOID == "+strColorMasterOID);
						LOGGER.debug(" size == "+size);
						LOGGER.debug(" branchId == "+branchId);
						
						boolean isColorwayOverrideExists = false;
						FlexBOMLink tempLink = null;
						int sizeCount = 0;
						//--Process only Colorway variation if it exists..
						if (colorwaySizeOvrCol != null)
						{
							Iterator<?> it = colorwaySizeOvrCol.iterator();
							LOGGER.debug("colorwaySizeOvrCol = "+colorwaySizeOvrCol.size());
							
							while (it.hasNext())
							{
								LOGGER.debug("************************************");
								LOGGER.debug("Processing Colorway-Size Overriden Link ..");
								LOGGER.debug("************************************");	
								overrideExists = true;
								childProcessed = false;
								ovrObj = (FlexObject) it.next();
								//LOGGER.debug(" ovrObj == "+ovrObj);
								idA2A2 = ovrObj.getString("FLEXBOMLINK.IDA2A2");
								strBomId = new ObjectIdentifier(FlexBOMLink.class, 
											Long.parseLong(idA2A2)).toString();
								childLink = (FlexBOMLink) LCSQuery
									.findObjectById(strBomId);
								
								ovrSKUMasterOID = ovrObj.getString("FLEXBOMLINK.IDA3E5");
								ovrSize = ovrObj.getString("FLEXBOMLINK.SIZE1");
								LOGGER.debug(" ovrSKUMasterOID == "+ovrSKUMasterOID);
								LOGGER.debug(" strColorMasterOID == "+strColorMasterOID);
								LOGGER.debug(" ovrSize == "+ovrSize);
								LOGGER.debug(" size == "+size);
								if(strColorMasterOID.equals(ovrSKUMasterOID) && size.equals(ovrSize)){
									childProcessed=true;
									tempLink = childLink;
									bomRequest = processBOMLink(childLink, parentLink,bomRequest,tempLink,bean);
									break;
								}
								
							}
						} else if (colorwayOvrCol != null)
						{
							LOGGER.debug("colorwayOvrCol = "+colorwayOvrCol.size());
							Iterator<?> it = colorwayOvrCol.iterator();
							
							while (it.hasNext())
							{
								LOGGER.debug("************************************");
								LOGGER.debug("Processing Colorway Overriden Link ..");
								LOGGER.debug("************************************");								
								ovrObj = (FlexObject) it.next();
								overrideExists = true;
								childProcessed=false;
								// --if the Colorway Variation matches with the Colorway on the 
								//BusinessObject then fetch the overridden row ---
									idA2A2 = ovrObj.getString("FLEXBOMLINK.IDA2A2");
									isColorwayOverrideExists = true;
									LOGGER.debug("isColorwayOverrideExists == "+isColorwayOverrideExists);
									strBomId = new ObjectIdentifier(FlexBOMLink.class, 
											Long.parseLong(idA2A2)).toString();
									childLink = (FlexBOMLink) LCSQuery.findObjectById(strBomId);
																
									ovrSKUMasterOID = ovrObj.getString("FLEXBOMLINK.IDA3E5");
									ovrSize = ovrObj.getString("FLEXBOMLINK.SIZE1");
									LOGGER.debug(" ovrSKUMasterOID == "+ovrSKUMasterOID);
									LOGGER.debug(" strColorMasterOID == "+strColorMasterOID);
									LOGGER.debug(" ovrSize == "+ovrSize);
									LOGGER.debug(" size == "+size);
									if(strColorMasterOID.equals(ovrSKUMasterOID)){	
										//bean = setColorwaySeasonInfo(sku,bean);
										childProcessed=true;
										//Used for processing Colorway-Size link
										tempLink = childLink;
			
										bomRequest = processBOMLink(childLink, parentLink,bomRequest,tempLink,bean);
									}
									
							}
							
						}  
						else if(sizeOvrCol != null){
							Iterator<?> it = sizeOvrCol.iterator();
							LOGGER.debug("sizeOvrCol = "+sizeOvrCol.size());
							while (it.hasNext())
							{
								LOGGER.debug("************************************");
								LOGGER.debug("Processing Size Overriden Link ..");
								LOGGER.debug("************************************");							
								ovrObj = (FlexObject) it.next();
								overrideExists = true;
								childProcessed=false;
								ovrSize = ovrObj.getString("FLEXBOMLINK.SIZE1");
								LOGGER.debug(" ovrSize == "+ovrSize);
								LOGGER.debug(" size == "+size);
								if( size.equals(ovrSize)){							
									idA2A2 = ovrObj.getString("FLEXBOMLINK.IDA2A2");
									strBomId = new ObjectIdentifier(FlexBOMLink.class, 
											Long.parseLong(idA2A2)).toString();
									 childLink = (FlexBOMLink) LCSQuery
									.findObjectById(strBomId);
									 childProcessed=true;
									 bomRequest = processBOMLink(childLink, parentLink,bomRequest,tempLink,bean);
								}
							}
							}else{
							//Here child link is null and processes only parent link
								LOGGER.debug("************************************");
								LOGGER.debug("Processing Parent Link ..");
								LOGGER.debug("************************************");
								parentProcessed = true;
								bomRequest = processBOMLink(childLink, parentLink,bomRequest,null,bean);
						}
						
						//Fetch if Colorway-Size variation exists..		 

						LOGGER.debug("sizeCount = "+sizeCount);
						LOGGER.debug(" overrideExists =" +overrideExists +" parentProcessed = "+parentProcessed+ "childProcessed = "+childProcessed);
						
						if (overrideExists && !parentProcessed && !childProcessed ) {
							LOGGER.debug("Processing parent link in rare case");
							bomRequest = processBOMLink(null, parentLink,bomRequest,null,bean);
						}
												
					}

				}	//END OF TOP-LEVEL ROWS
	
		}	
			return bomRequest;
		
	}
	
		
	/**
	 * 
	 * @param sku
	 * @param bean
	 * @return
	 * @throws WTException
	 */
	
	public SMBOMOutboundIntegrationBean setColorwaySeasonInfo(LCSSKU sku, SMBOMOutboundIntegrationBean bean) throws WTException {
		
		LOGGER.debug("setting Colorway and Colorway Season PLMID and MDMID");
		LOGGER.debug("************************************");
		LCSSeason skuseason = (LCSSeason) VersionHelper.latestIterationOf(sku.getSeasonMaster());
		LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(sku, skuseason);
		LCSSKU skuRevA = (LCSSKU) VersionHelper.getVersion(sku, "A");
		//set sku plmid
		bean.setColorwayPLMId(String.valueOf(skuRevA.getBranchIdentifier()));
		LOGGER.debug("skuRevA="+skuRevA);
		LOGGER.debug("skuRevA SKU MDM ID"+skuRevA.getValue(SMBOMOutboundWebServiceConstants.COLORWAY_MDM_ID));
		// set Material Supplier MDMID
		if (FormatHelper
				.hasContent(String.valueOf(skuRevA.getValue(SMBOMOutboundWebServiceConstants.COLORWAY_MDM_ID)))) {
			LOGGER.debug("skuRevA SKU MDM ID in loop=="+skuRevA.getValue(SMBOMOutboundWebServiceConstants.COLORWAY_MDM_ID));
			bean.setColorwayMDMId(String.valueOf(skuRevA.getValue(SMBOMOutboundWebServiceConstants.COLORWAY_MDM_ID)));
		} else {
			LOGGER.debug("SETTING EMPTYMDMID for SKU");
			bean.setColorwayMDMId(SMBOMOutboundWebServiceConstants.EMPTY_MDM_ID);
		}							
		
				
        //set Colorway-Season plm id.

		if(FormatHelper.hasContent(String.valueOf(ssl.getValue(SMBOMOutboundWebServiceConstants.AD_HOC_PLM_ID_SKU_SEASON_LINK)))){

              LOGGER.debug("Setting SKU Season Link PLM ID from AD HOC PLM ID field *********************");
              bean.setColorwaySeasonPLMID(String.valueOf(ssl.getValue(SMBOMOutboundWebServiceConstants.AD_HOC_PLM_ID_SKU_SEASON_LINK)));

        }else{
              LOGGER.debug("Setting SKU Season Link from SKU Season Link  ******************");
              bean.setColorwaySeasonPLMID(bean.getBomHelper().getColorwayMasterReferenceFromLink(ssl));

        }
		
		//set Colorway-Season MDMID
		if (FormatHelper
				.hasContent(String.valueOf(ssl.getValue(SMBOMOutboundWebServiceConstants.COLORWAY_SEASON_MDM_ID)))) {
			bean.setColorwaySeasonMDMID(String.valueOf(ssl.getValue(SMBOMOutboundWebServiceConstants.COLORWAY_SEASON_MDM_ID)));
		} else {
			bean.setColorwaySeasonMDMID(SMBOMOutboundWebServiceConstants.EMPTY_MDM_ID);
		}							
	
		return bean;
		
	}
	
	
	/**
	 * 	
	 * @param childLink
	 * @param parentLink
	 * @param bomRequest
	 * @param tempLink
	 * @param bean
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws DatatypeConfigurationException
	 */
	public BOMPart processBOMLink(FlexBOMLink childLink, FlexBOMLink parentLink,BOMPart bomRequest,FlexBOMLink tempLink,SMBOMOutboundIntegrationBean bean) 
	throws WTException, WTPropertyVetoException, DatatypeConfigurationException
	{
		LOGGER.debug("START:Processing BOMLinks Child Link="+childLink
				+" ParentLink="+parentLink);


		float floatComsumption = (float)0.0;
		
		boolean childFlagSet = false;
		LCSMaterialMaster childMaterialMaster = null;
		
		FlexBOMLink bomLink = parentLink;
		
		LCSMaterialMaster materialMaster = parentLink.getChild();
		LCSSupplierMaster  supMaster = parentLink.getSupplier();
		String matMasterIDA2A2 = "";
		//LCSColor color = parentLink.getColor();
		

		if(tempLink != null) {
			LCSColor color = tempLink.getColor();
			if (color != null) {
				LOGGER.debug("Temp color == "+color.getName());

				
			}
			floatComsumption = ((Double)tempLink.getValue("quantity")).floatValue();
			bomLink = tempLink;
			
			// -- Fetch the Material from the link and make a PlACEHOLDER check
			childMaterialMaster = childLink.getChild(); 
			matMasterIDA2A2 = FormatHelper.getNumericObjectIdFromObject(childMaterialMaster);
			LCSSupplierMaster childSupMaster = childLink.getSupplier();
			
			LOGGER.debug("childMaterialMaster = "+childMaterialMaster);
			LOGGER.debug("matMasterIDA2A2 = "+matMasterIDA2A2);
			LOGGER.debug("mat PLACEHOLDERID IDA2A2 = "+FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID));
			
			if(childMaterialMaster!= null && !matMasterIDA2A2.equalsIgnoreCase
					(FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID))) {
				LOGGER.debug("Replacing Material and Supplier with Child's data");
				materialMaster = childMaterialMaster; 
				supMaster = childSupMaster;

			}
			
			childFlagSet = true;
		} else 					
		//Replace Child Link's Material and Supplier if it is a Placeholder
		if(childLink!=null) {
			LOGGER.debug("childLink = "+childLink);
			bomLink = childLink;
			floatComsumption = ((Double)bomLink.getValue("quantity")).floatValue();
			LOGGER.debug("floatComsumption = "+floatComsumption);
			
			// -- Fetch the Material from the link and make a PlACEHOLDER check
			childMaterialMaster = childLink.getChild(); 
			matMasterIDA2A2 = FormatHelper.getNumericObjectIdFromObject(childMaterialMaster);
			LCSSupplierMaster childSupMaster = childLink.getSupplier();
			
			LOGGER.debug("childMaterialMaster = "+childMaterialMaster);
			LOGGER.debug("matMasterIDA2A2 = "+matMasterIDA2A2);
			LOGGER.debug("mat PLACEHOLDERID IDA2A2 = "+FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID));
			
			if(childMaterialMaster!= null && !matMasterIDA2A2.equalsIgnoreCase
					(FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID))) {
				LOGGER.debug("Replacing Material and Supplier with Child's data");
				materialMaster = childMaterialMaster; 
				supMaster = childSupMaster;

			}
				childFlagSet = true;			
			} 
			else{
		LCSColor color = bomLink.getColor();
		if (color != null) {
			LOGGER.debug("Parent color == "+color.getName());
			
			}
		floatComsumption = ((Double)bomLink.getValue("quantity")).floatValue();//added on 29
		}

		
		bomRequest = setMessageBean(materialMaster,supMaster,bomLink, floatComsumption, parentLink, childFlagSet, bomRequest,bean);
		LOGGER.debug("END:Processing BOMLinks");
		return bomRequest;
	}
	
	
	
	/**
	 * 
	 * @param materialMaster
	 * @param supMaster
	 * @param childLink
	 * @param parentConumption
	 * @param parentLink
	 * @param checkFlag
	 * @param bomRequest
	 * @param bean
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws DatatypeConfigurationException
	 */
	
	public BOMPart setMessageBean(LCSMaterialMaster materialMaster, LCSSupplierMaster  supMaster, 
			FlexBOMLink childLink, float parentConumption, FlexBOMLink parentLink, boolean checkFlag,BOMPart bomRequest,SMBOMOutboundIntegrationBean bean) 
	throws WTException, WTPropertyVetoException, DatatypeConfigurationException
	{
		LOGGER.debug("START : Setting message bean");
		
		
		/*	
	    "bomLinkBranchID",
	    "consumption",
	    "colorwayPLMId",
	    "colorwayMDMId",
	    "colorwaySeasonPLMID",
	    "colorwaySeasonMDMID",
	    "sizeName",
	    "sizeDefinitionPLMID",
	    "materialPLMId",
	    "materialMDMId",
	    "materialName",
	    "materialSupplierMDMId",
	    "materialSupplierPLMId",
	    "colorType",
	    "colorProviderCatalog", smProviderCatalog
	    "colorStandardRef", vrdColorStdRefNum
	    "colorArtwork",
	    "createdON",
	    "createdBy",
	    "lastUpdated",
	    "lastUpdatedBy"*/
		
		float floatComsumption = parentConumption;
		//Collection materialData = new ArrayList();

		BomLinkVariation bomLinkVariation = null;
		
		
		LOGGER.debug("childLink == "+childLink);
		LOGGER.debug("checkFlag == "+checkFlag);
		String matMasterIDA2A2 = FormatHelper.getNumericObjectIdFromObject(materialMaster);
		LOGGER.debug("matMasterIDA2A2 == "+matMasterIDA2A2);
		LOGGER.debug("mat PLACEHOLDERID IDA2A2 = "+FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID));
		
		if(materialMaster!= null && !matMasterIDA2A2.equalsIgnoreCase(FormatHelper
				.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID))) 
		{			
			// -- Fetching MATERIAL-ID from Material -- 
			LCSMaterial materialObj = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
			LOGGER.debug("materialObj == "+materialObj.getName());
			
			bomLinkVariation = new BomLinkVariation();
			bomLinkVariation.setMaterialName(materialObj.getName());
			bomLinkVariation.setBOMLinkBranchID(String.valueOf(parentLink.getBranchId()));
			bomLinkVariation.setCreatedON(bean.getBomUtill().getXMLGregorianCalendarFormat(parentLink.getCreateTimestamp()));
			//set created by.
			bomLinkVariation.setCreatedBy(bomRequest.getCreatedBy());
			//set last updated on.
			bomLinkVariation.setLastUpdated(bean.getBomUtill().getXMLGregorianCalendarFormat(parentLink.getModifyTimestamp()));
			//set last updated by.
			bomLinkVariation.setLastUpdatedBy(bomRequest.getLastUpdatedBy());
			
			// SM requested to set same value as Material Integration, for material integration we are mapping to Material # sequence for PLMID tag, using the same here.
			//bomLinkVariation.setMaterialPLMId(String.valueOf(materialObj.getBranchIdentifier()));
			if (FormatHelper
					.hasContent(String.valueOf(materialObj.getValue(MATERIAL_NUM)))) {
				bomLinkVariation.setMaterialPLMId(String.valueOf(materialObj.getValue(MATERIAL_NUM)));
			}
			// set Material MDMID
			if (FormatHelper
					.hasContent(String.valueOf(materialObj.getValue(SMBOMOutboundWebServiceConstants.MATERIAL_MDM_ID)))) {
				bomLinkVariation.setMaterialMDMId(String.valueOf(materialObj.getValue(SMBOMOutboundWebServiceConstants.MATERIAL_MDM_ID)));
			} else {
				bomLinkVariation.setMaterialMDMId(SMBOMOutboundWebServiceConstants.EMPTY_MDM_ID);
			}		

			
			bomLinkVariation.setColorwayMDMId(bean.getColorwayMDMId());
			bomLinkVariation.setColorwayPLMId(bean.getColorwayPLMId());
			bomLinkVariation.setColorwaySeasonMDMID(bean.getColorwaySeasonMDMID());
			bomLinkVariation.setColorwaySeasonPLMID(bean.getColorwaySeasonPLMID());
			
			// -- Fetching COLORCODE from LCSColor --
			LCSColor color;
			if(checkFlag){
				color = childLink.getColor();
										LOGGER.debug("child link is "+childLink.getColor());
			}else{
				color = parentLink.getColor();	
					LOGGER.debug("parent link is "+parentLink.getColor());
			}				
			if (color != null) {
				LOGGER.debug("color11 == "+color.getName());
					 	//color = (LCSColor)VersionHelper.latestIterationOf(color);
						String colorFlexTYpe = color.getFlexType().getFullName();
						bomLinkVariation.setColorType(color.getFlexType().getFullNameDisplay());
						LOGGER.debug("color flextype = "+colorFlexTYpe);
						
						if (colorFlexTYpe.contains("Melanges")) {
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_PROVIDER_CATALOG)))) {
								LOGGER.debug("Melanges setColorProviderCatalog");
								bomLinkVariation.setColorProviderCatalog(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_PROVIDER_CATALOG)));
							} 
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_STANDARD_REF)))) {
								LOGGER.debug("Melanges setColorStandardRef");
								bomLinkVariation.setColorStandardRef(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_STANDARD_REF)));
							} 
						}
						
						if (colorFlexTYpe.contains("Artwork")) {
							
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_ARTWORK)))) {
								bomLinkVariation.setColorArtwork(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_ARTWORK)));
							} 
						}
						if (colorFlexTYpe.contains("Metallic")) {
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.METALLIC_COLOR_PROVIDER_CATALOG)))) {
								bomLinkVariation.setColorProviderCatalog(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.METALLIC_COLOR_PROVIDER_CATALOG)));
							} 
							
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_STANDARD_REF)))) {
								bomLinkVariation.setColorStandardRef(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_STANDARD_REF)));
							} 
						}
						if (colorFlexTYpe.contains("Solid")) {
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_PROVIDER_CATALOG)))) {
								LOGGER.debug("Solid setColorProviderCatalog");
								bomLinkVariation.setColorProviderCatalog(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.COLOR_PROVIDER_CATALOG)));
							} 
							if (FormatHelper.hasContent(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.SOLID_COLOR_STANDARD_REF)))) {
								LOGGER.debug("Solid setColorStandardRef");
								bomLinkVariation.setColorStandardRef(String.valueOf(color.getValue(SMBOMOutboundWebServiceConstants.SOLID_COLOR_STANDARD_REF)));
							} 
						}						
				}

					bomLinkVariation.setSizeName(bean.getSizeName());
					bomLinkVariation.setSizeDefinitionPLMID(bean.getSizeDefinitionPLMID());

					float childConsumption = ((Double)childLink.getValue("quantity")).floatValue();
					if (!(childConsumption == 0.0))
					{
						floatComsumption = childConsumption;
					}
					BigDecimal consumption = new BigDecimal(Float.toString(floatComsumption));
					bomLinkVariation.setConsumption(consumption);
					LOGGER.debug("floatComsumption == "+floatComsumption);
				
					//LCSSupplierMaster  supMaster = bomLink.getSupplier();
					// -- If Supplier Master is null then skip the loop..
					
					// -- Fetching 
					LCSSupplier supplier = (LCSSupplier)VersionHelper.latestIterationOf(supMaster); 
					LOGGER.debug("supplier == "+supplier.getName());
					if(supMaster != null)
					{
						LCSMaterialSupplier matSupplier =LCSMaterialSupplierQuery
						.findMaterialSupplier(materialMaster, supMaster);
						bomLinkVariation.setMaterialSupplierPLMId(FormatHelper.getNumericObjectIdFromObject(matSupplier));
						// set Material Supplier MDMID
						if (FormatHelper
								.hasContent(String.valueOf(supplier.getValue(SMBOMOutboundWebServiceConstants.MATERIAL_SUPPLIER_MDM_ID)))) {
							bomLinkVariation.setMaterialSupplierMDMId(String.valueOf(supplier.getValue(SMBOMOutboundWebServiceConstants.MATERIAL_SUPPLIER_MDM_ID)));
						} else {
							bomLinkVariation.setMaterialSupplierMDMId(SMBOMOutboundWebServiceConstants.EMPTY_MDM_ID);
						}

					}
				
		}
		if (bomLinkVariation!=null) {
			LOGGER.debug("Adding bomLinkVariation to BOMPart");
			bomRequest.getBomLinkVariation().add(bomLinkVariation);
		}

		
		return bomRequest;
	}
	
	
	
	/**
	 * @param bomPart
	 * @param bomRequest
	 * @param bean
	 * @throws WTException
	 * @throws DatatypeConfigurationException 
	 */
	public void setDataForAssociatedObjects(FlexBOMPart bomPart,
			BOMPart bomRequest, SMBOMOutboundIntegrationBean bean) throws WTException, DatatypeConfigurationException {
		
		//Fetching Product and ProductSeasonLink
		setDataForSpecSeason(bomPart,bomRequest, bean);
		
		//Fetching Specification, Source and Supplier
		
		setDataForSpecAndSource(bomPart,bomRequest, bean);
		
	}


	/**
	 * @param bomPart
	 * @param bomRequest
	 * @param bean
	 * @throws WTException
	 * @throws DatatypeConfigurationException 
	 */
	public void setDataForSpecSeason(FlexBOMPart bomPart,
			BOMPart bomRequest,SMBOMOutboundIntegrationBean bean) throws WTException, DatatypeConfigurationException {
		
		
		//Fetching Product and ProductSeasonLink
		wt.part.WTPartMaster wtpartMaster= (WTPartMaster) bomPart.getOwnerMaster();
		LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(wtpartMaster);
		
		LCSSeasonMaster seasonMaster = product.getSeasonMaster();
		//LCSSeasonMaster carryOverMaster = product.getCarriedOverFrom();
		//LOGGER.debug(">>>>carryOverMaster>>>"+carryOverMaster);
		LCSSeason currentSeason = null;


		if(seasonMaster != null  /*&& carryOverMaster == null*/){
			currentSeason = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
			LOGGER.debug(">>currentSeason from without carryover>>"+currentSeason.getName());
			bean.setAssociatedSeason(currentSeason);
			//Setting season name
			bomRequest.setSpecSeasonName(currentSeason.getName());
				
			// set SPec Season MDMID
			if (FormatHelper
					.hasContent(String.valueOf(currentSeason.getValue(SMBOMOutboundWebServiceConstants.SEASON_MDM_ID)))) {
				bomRequest.setSpecSeasonMDMID(String.valueOf(currentSeason.getValue(SMBOMOutboundWebServiceConstants.SEASON_MDM_ID)));
			} else {
				bomRequest.setSpecSeasonMDMID(SMBOMOutboundWebServiceConstants.EMPTY_MDM_ID);
			}

		}
		
	}
	

	
	/**
	 * @param bomPart
	 * @param bomRequest
	 * @param bean
	 * @throws WTException
	 * @throws DatatypeConfigurationException 
	 */
	public void setDataForSpecAndSource(FlexBOMPart bomPart,
			BOMPart bomRequest, SMBOMOutboundIntegrationBean bean) throws WTException, DatatypeConfigurationException {
		
		
		FlexSpecToComponentLink specToCompLink=null;
		FlexSpecification specObj = null;
		LCSSourcingConfig sourcingConfig = null;
		
		
		Collection<FlexSpecToComponentLink> specLinks = LCSQuery.getObjectsFromResults(FlexSpecQuery.getSpecToComponentLinksForComponent(bomPart), 
				"OR:com.lcs.wc.specification.FlexSpecToComponentLink:", "FLEXSPECTOCOMPONENTLINK.IDA2A2");


		for(Iterator<FlexSpecToComponentLink> links = specLinks.iterator(); links.hasNext();)
		{	
			specToCompLink = links.next();
			specObj = (FlexSpecification)VersionHelper.latestIterationOf(specToCompLink.getSpecificationMaster());			

			if(specObj != null)
			{
				LOGGER.debug("### Specification for the BOM is  ###" +specObj.getName());
				
				//Setting SpecName
				bomRequest.setSpecName(specObj.getName());
				
				//Setting specID
				bomRequest.setSpecID(String.valueOf(specObj.getBranchIdentifier()));
				
				// set Spec Status
				if (FormatHelper
						.hasContent(String.valueOf(specObj.getValue(SMBOMOutboundWebServiceConstants.SPEC_STATUS)))) {
					bomRequest.setSpecStatus(String.valueOf(specObj.getValue(SMBOMOutboundWebServiceConstants.SPEC_STATUS)));
				} else {
					bomRequest.setSpecStatus("");
				}
				
				//set Spec created on.
				bomRequest.setSpecCreated(bean.getBomUtill().getXMLGregorianCalendarFormat(specObj.getCreateTimestamp()));
				//set Speclast updated on.
				bomRequest.setSpecLastUpdated(bean.getBomUtill().getXMLGregorianCalendarFormat(specObj.getModifyTimestamp()));
				
				//get sourcingConfig from the specification for the BOM
				sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf((LCSSourcingConfigMaster) specObj.getSpecSource());
				LOGGER.debug("### sourcingConfig for the BOM is ###" +sourcingConfig.getName());
				
				//Set Sourcing Name
				bomRequest.setSourcingName(String.valueOf(sourcingConfig.getValue("name")));
				
				//Set Sourcing PLM ID getSourcePLMID
				bomRequest.setSourcingPLMID(bean.getBomHelper().getSourcePLMID(sourcingConfig, bean.getAssociatedSeason()));
				
				// get business supplier.
				LCSSupplier businessSupplier = (LCSSupplier) sourcingConfig
						.getValue(SMBOMOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER);

				if (businessSupplier != null) {
					//Set businessSupplierName
					bomRequest.setBusinessSupplierName(String.valueOf(businessSupplier.getValue("name")));
					
					//Set SUpplier PLM ID
					bomRequest.setBusinessSupplierPLMID(String.valueOf(businessSupplier.getValue(SMBOMOutboundWebServiceConstants.BUSINESS_SUPPLIER_PLM_ID)));
					
					
					// set SPec Season MDMID
					if (FormatHelper
							.hasContent(String.valueOf(businessSupplier.getValue(SMBOMOutboundWebServiceConstants.BUSSINESS_SUPPLIER_MDM_ID)))) {
						bomRequest.setBusinessSupplierMDMID(String.valueOf(businessSupplier.getValue(SMBOMOutboundWebServiceConstants.BUSSINESS_SUPPLIER_MDM_ID)));
					} else {
						bomRequest.setBusinessSupplierMDMID(SMBOMOutboundWebServiceConstants.EMPTY_MDM_ID);
					}
				}
				
			}
		}
	}


}

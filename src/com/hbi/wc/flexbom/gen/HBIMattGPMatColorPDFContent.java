package com.hbi.wc.flexbom.gen;

import java.util.*;

//import com.lcs.wc.bom.LCSBOMQuery;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.product.*;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLinkClientModel;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import wt.part.WTPartMaster;
import com.lcs.wc.part.LCSPart;
import wt.util.*;
import com.lcs.wc.flexbom.gen.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialMaster;

/**
 *
 * @author Manoj From UST
 * @Date Oct 4, 2018, 12:58 PM
 * 
 *
 *       This class is implemented by taking the code from HBIMatColorPDFContent
 *       and modified as per requirement
 * 
 */

public class HBIMattGPMatColorPDFContent extends HBIBOMPDFContentGenerator {

	private static final boolean DEBUG = LCSProperties
			.getBoolean("com.lcs.wc.flexbom.gen.HBIMattGPMatColorPDFContent.verbose");
	private static final int DEBUG_LEVEL = Integer
			.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorPDFContent.verboseLevel", "1"));
	private static final String CLASSNAME = MatColorPDFContent.class.getName();
	public float tableWidthPercent = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorPDFContent.tableWidthPercent", "95.0"))).floatValue();
	// GP teck pack customizations - 09/20/2018
	public static final String BASIC_CUT_AND_SEW_GARMENT = "Product\\BASIC CUT & SEW - GARMENT";
	public static final String GARMENT_BOM_TYPE_PATH = LCSProperties.get("bomreport.colorway.garmentproduct.bomtypes");
	// Multi Spec BOM
	public static final String MULTI_SPEC_BOM = LCSProperties.get("com.hbi.multispecBOM");
	public static final String LABEL_TYPE = "Product\\HBI-SUPPORTING\\LABEL";
	public static final String COLORWAY_BOM_TYPE="BOM\\Materials\\HBI\\Colorway";
	public static final String CARE_CODE_ATT = LCSProperties.get("bomreport.colorway.garmentproduct.hbiCareCodes","hbiCareCodes");
	public static final String CARE_CODE_INST = LCSProperties.get("bomreport.colorway.bo.hbiCareInstructions","hbiCareInstructions");

	
	
	
	

	/** Creates a new instance of MatColorPDFContent */
	public HBIMattGPMatColorPDFContent() {
	}

	/**
	 * gets an Element for insertion into a PDF Document
	 * 
	 * @param params
	 *            A Map of parameters to pass to the Object. This provides the
	 *            means for the calling class to have some "fore" knowledge of
	 *            what implementations are being used and pass appropriate
	 *            parameters.
	 * @param document
	 *            The PDF Document which the content is going to be added to.
	 *            The document is passed in order to provide additional
	 *            information related to the Document itself incase it is not
	 *            provided in the params
	 * @throws WTException
	 *             For any error
	 * @return an Element for insertion into a Document
	 */
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
		if (DEBUG) {
			debug("MatColorPDFContent.getPDFContentCollection");
		}
        

		String seasonMaster_Oid = (String) params.get("SEASONMASTER_ID");
		boolean moveFiberContent=false;
		LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSQuery.findObjectById(seasonMaster_Oid);
		LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
	       if(season!=null ){
               String gpSeasonFType1 = season.getFlexType().getFullName(true);
               
               if ("Season\\Garment\\Activewear".equals(gpSeasonFType1)) {
                   
                   String seasonDivision = (String)season.getValue("hbiBusiness");
                   if((FormatHelper.hasContent(seasonDivision)) && ("baw".equalsIgnoreCase(seasonDivision) ||"gaw".equalsIgnoreCase(seasonDivision) ||
                   "maw".equalsIgnoreCase(seasonDivision) ||"bra".equalsIgnoreCase(seasonDivision) ||"waw".equalsIgnoreCase(seasonDivision))){
                	   moveFiberContent=true;

                   }
               }
           }

		Document document1=document;
		Collection content = new ArrayList();
		Collection spcontent = new ArrayList();
		Collection spcontentNew = new ArrayList();

		Collection sectionPageTitles = new ArrayList();
		FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		// GP teck pack customizations - 09/20/2018
		boolean gpBOMTechpackStatus = isValidateGPBOMTypeForBOMReport(bomPart, params);
		System.out.println("gpBOMTechpackStatus for matcolorMatt"+gpBOMTechpackStatus);
		StringBuffer hbiCareInstructions=new StringBuffer();

        
		if (gpBOMTechpackStatus) {// Start HBI GP tech pack customization -
									// 09/07/18
			Map tparams = new HashMap(params.size() + 2);
			tparams.putAll(params);

			HBIMattGPMatColorGenerator bomDG = new HBIMattGPMatColorGenerator();
			Collection allSkus = (Collection) params.get(BomDataGenerator.COLORWAYS);

			int maxPerPage = ((Integer) params.get(PDFProductSpecificationGenerator2.COLORWAYS_PER_PAGE)).intValue();

			// End HBI GP tech pack customization - 09/07/18

			// Splitting of BOM rows based on max colorways per page
			Collection skusArray = splitItems(allSkus, maxPerPage);

			// Multiple Colorways for Multi Spec -Jey
			Boolean multiSpecBOM = false;
			LCSSourcingConfig sourceM = null;

			if (params.get("multi_source") != null) {
				sourceM = (LCSSourcingConfig) LCSQuery.findObjectById((String) params.get("multi_source"));
				multiSpecBOM = true;
				allSkus = getMultiSpecColorways(params, sourceM);
				skusArray = splitItems(allSkus, maxPerPage);
			}
			// Multiple Colorways for Multi Spec -Jey

			Iterator skusIt = null;

			Collection skusThisRun = new ArrayList();

			Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList()
					.getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
			String section = "";
			String hbilabelstr = "";
			//Changed the loop so that each set of colorways prints all sections together
			if (allSkus.isEmpty()) {
				SingleBOMGenerator singleBom = new SingleBOMGenerator();
				Iterator sectionIter = sections.iterator();
				section = "";
				while (sectionIter.hasNext()) {	
					section = (String) sectionIter.next();
					if(section.equals("hbilabel")) {
						hbilabelstr = "hbilabel";
						break;
					}
					tparams.put(BomDataGenerator.SECTION, section);
					setSectionViewId(tparams);
					singleBom.init(tparams);

					Collection data = singleBom.getBOMData();
					// To get the all columns as in view selected on select
					// techpack page
					Collection columns = singleBom.getTableColumns();
					spcontent.addAll(generatePDFPage(data, columns, document, tparams));
					if (!BOM_ON_SINGLE_PAGE) {
						sectionPageTitles.add(getPageTitleText(tparams));
					}
				}
				if(hbilabelstr.equals("hbilabel")) {
					tparams.put(BomDataGenerator.SECTION, hbilabelstr);
					setSectionViewId(tparams);
					singleBom.init(tparams);

					Collection data = singleBom.getBOMData();
					// To get the all columns as in view selected on select
					// techpack page
					Collection columns = singleBom.getTableColumns();
					if(moveFiberContent){
					spcontentNew.addAll(generatePDFPage(data, columns, document, tparams));
					}
					else{
						spcontent.addAll(generatePDFPage(data, columns, document, tparams));
	
					}
					if (!BOM_ON_SINGLE_PAGE) {
						sectionPageTitles.add(getPageTitleText(tparams));
					}
				}


			} else {

				// skusArray contains the colorways which is displayed in the
				// selection page for that spec
				// multiSpecColorwaysArray contains colorways that belong to
				// that BOM from that source					
				skusIt = skusArray.iterator();
				int skuloop = 0;
				section = "";
				Map colormap=new HashMap();

				while (skusIt.hasNext()) {
					skusThisRun = (Collection) skusIt.next();
					tparams.put(BomDataGenerator.COLORWAYS, skusThisRun);	
					Iterator sectionIter = sections.iterator();	
					while (sectionIter.hasNext()) {
						section = (String) sectionIter.next();
						
						if(section.equals("hbilabel")) {
							hbilabelstr = "hbilabel";
							break;
						}

						//if(!section.equals("hbilabel") || (section.equals("hbilabel") && skuloop + 1 == skusArray.size())) {
							tparams.put(BomDataGenerator.SECTION, section);
							setSectionViewId(tparams);
							LCSProduct prod;

							bomDG = new HBIMattGPMatColorGenerator();
							bomDG.init(tparams);
							Collection data = bomDG.getBOMData();
                           
                            
                            
							if("cutPartSpread".equals(section)) {
								 String garmentUseName=null;
								Iterator dataitr=data.iterator();
								while(dataitr.hasNext()) {
								 FlexObject objData=(FlexObject)dataitr.next();
								 //String garmentUseid=objData.getData("FLEXBOMLINK.NUM6");
								 String garmentUseid=objData.getData("FLEXBOMLINK.idA3A2typeInfoFlexBOMLink");
								 if(FormatHelper.hasContent(garmentUseid)) {
								 LCSLifecycleManaged garmentuse=(LCSLifecycleManaged)LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+garmentUseid);
								  garmentUseName=garmentuse.getName();
							}
								

                              if(FormatHelper.hasContent(garmentUseName)&&garmentUseName.contains("Body")) {
								Iterator skusThisRunitr=skusThisRun.iterator();
								while(skusThisRunitr.hasNext()) {
									String skuid=(String) skusThisRunitr.next();
									String desc=objData.getData(skuid+".COLORNAME");
									
                                 if(FormatHelper.hasContent(desc)) {
                                    if(desc.contains("(D)")) {
                                    	desc=desc.replace("(D)", "");


                                    }
                                    if(desc.contains("(P)")) {
                                    	desc=desc.replace("(P)", "");

                                    }
                                    if(desc.contains("(S)")) {
                                    	desc=desc.replace("(S)", " ");


                                    }
                                    if(desc.contains("(B)")) {
                                    	desc=desc.replace("(B)", "");


                                    }
                                    if(desc.contains("(PFD)")) {
                                    	desc=desc.replace("(PFD)", "");
                                    }
									if(!desc.equals("N/A ( )")) {
									colormap.put(skuid+".COLORNAME", desc.trim());
									}
                                 }
									}
								}
								}
								LCSLog.debug("colormap:::::::::::::"+colormap);
							}

							Collection columns = bomDG.getTableColumns();
							Collection data1 = new ArrayList();

							//--logic added for user story - 98511
							if("garment".equals(section)) {
								Iterator dataitr=data.iterator();
								while(dataitr.hasNext()) {
									Iterator skusThisRunitr=skusThisRun.iterator();

								    FlexObject objData=(FlexObject)dataitr.next();
								    if(FormatHelper.hasContent((String)objData.get("LCSMATERIAL.IDA3MASTERREFERENCE")))
								    {
								    LCSMaterialMaster matMaster=(LCSMaterialMaster)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialMaster:"+objData.get("LCSMATERIAL.IDA3MASTERREFERENCE"));
								   LCSMaterial material=(LCSMaterial)VersionHelper.latestIterationOf(matMaster) ;
								   
							       Collection bomList = new LCSFlexBOMQuery().findBOMPartsForOwner(material);
							       Iterator bomlistitr=bomList.iterator();
							       FlexBOMPart part=null;
							       FlexType materialType=null;
							      
							       while(bomlistitr.hasNext()) {
							        part=(FlexBOMPart) bomlistitr.next();
							     
							        materialType = part.getFlexType().getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);
							     }

								while(skusThisRunitr.hasNext()) {
									String skuid=(String) skusThisRunitr.next();
								    try {
								    	if(FormatHelper.hasContent((String)objData.getData(skuid+".MATERIALCOLORID"))) {
								    	LCSMaterialColor matColor=(LCSMaterialColor)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:"+objData.getData(skuid+".MATERIALCOLORID"));
								    	LCSMaterial mat = (LCSMaterial) matColor.getValue("hbiMaterialColorVersion");
								   
								    	String variantNeeded="";
								    	if(mat!=null)
									 variantNeeded =mat.getName();

									if("VARIANT".equals(variantNeeded)&&part!=null) {
								    	Collection bomData = LCSFindFlexBOMHelper.findBOM(part, objData.getData(skuid+".MATERIALCOLORID"), "", "", "", "", "SINGLE", "", true, materialType, new ArrayList());
									Iterator bomDataItr=bomData.iterator();
									String ColorName=objData.getData(skuid+".COLORNAME");

									
									while(bomDataItr.hasNext()) {
										FlexObject bomObj=(FlexObject)bomDataItr.next();
										String dyecodeinThreadBOM=bomObj.getData("HBIDYECODEDISPLAY");
		
										String threadMatch=null;
										//ColorName.replaceAll(" - VARIANT - VARIANT", "");

										String SearchString=ColorName+" - "+dyecodeinThreadBOM;
										
										
										if(FormatHelper.hasContent(dyecodeinThreadBOM)&&colormap.containsValue(SearchString)) {
											threadMatch=bomObj.getData("MATERIALDESCRIPTION");
											ColorName=ColorName+" "+threadMatch;
											objData.setData(skuid+".COLORNAME",ColorName);

										}
										
									}
									}
								    }	
									
								    } catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}


									}
								data1.add(objData);
								    
								}
								}
								spcontent.addAll(generatePDFPage(data1, columns, document1, tparams));

							}
							else {
								spcontent.addAll(generatePDFPage(data, columns, document1, tparams));

							}

							if (!BOM_ON_SINGLE_PAGE) {
								sectionPageTitles.add(getPageTitleText(tparams));
							}

						//}
					}
					skuloop++;
				}
			
			if(hbilabelstr.equals("hbilabel")) {
				skusIt = skusArray.iterator();
				while (skusIt.hasNext()) {
					skusThisRun = (Collection) skusIt.next();
					tparams.put(BomDataGenerator.COLORWAYS, skusThisRun);
					tparams.put(BomDataGenerator.SECTION, hbilabelstr);
					setSectionViewId(tparams);

					bomDG = new HBIMattGPMatColorGenerator();
					bomDG.init(tparams);
					Collection data = bomDG.getBOMData();
					Collection columns = bomDG.getTableColumns();
					String columnName=FlexTypeCache.getFlexTypeFromPath(COLORWAY_BOM_TYPE).getAttribute(CARE_CODE_ATT).getColumnName();//getVariableName();
					Iterator labelItr =data.iterator();
					while(labelItr.hasNext()) {
						FlexObject dataObject=(FlexObject) labelItr.next();
						String careCodeId=dataObject.getData("FLEXBOMLINK."+columnName.toUpperCase());
						if(FormatHelper.hasContent(careCodeId)){
						LCSLifecycleManaged carecodeBo=(LCSLifecycleManaged)LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+careCodeId);

						if(carecodeBo!=null){
						String code=carecodeBo.getName();
						String instr=(String)carecodeBo.getValue(CARE_CODE_INST);
						if (!hbiCareInstructions.toString().contains(code))
						{
						hbiCareInstructions.append(code+"  :  \n"+instr);
						hbiCareInstructions.append("\n");
						hbiCareInstructions.append("\n");
						}
						}

						}
					}
					

					if(moveFiberContent){
						
						spcontentNew.addAll(generatePDFPage(data, columns, document1, tparams));
	
					}
					else{
						spcontent.addAll(generatePDFPage(data, columns, document1, tparams));
	
						
					}
					if (!BOM_ON_SINGLE_PAGE) {
						sectionPageTitles.add(getPageTitleText(tparams));
					}
				
					if(columns.size() == 1) {
						break;
					}					
				}
				
			}	
			}

			
			PdfPTable fullBOMTable = new PdfPTable(1);
			PdfPTable fullBOMTable1 = new PdfPTable(1);
			PdfPTable e = null;
			PdfPCell cell = null;
			fullBOMTable.setWidthPercentage(tableWidthPercent);
			// Add the BOM Header Attributes
			if (FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER))) {
				debug(2, "FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER) )");
				Collection BomHeaderAtts = (Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_ATTS);
				if (BomHeaderAtts != null && !BomHeaderAtts.isEmpty()) {
					debug(2, "BomHeaderAtts != null && !BomHeaderAtts.isEmpty()");
					if (PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE && BOM_ON_SINGLE_PAGE) {
						debug(2, "PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE");
						for (Iterator HeaderI = BomHeaderAtts.iterator(); HeaderI.hasNext();) {
							e = (PdfPTable) HeaderI.next();
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
						}
					} else {
						debug(2, "NOT--PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE");
						content.add(BomHeaderAtts);

						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));

					}
				}
			}

			// Add BOM Sections
			Collection BOMFooter = (Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_ATTS);
			boolean usingFooter = (BOMFooter != null && !BOMFooter.isEmpty()
					&& FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_FOOTER)));
			if (BOM_ON_SINGLE_PAGE) {
				debug(2, "BOM_ON_SINGLE_PAGE");
				Iterator sci = spcontent.iterator();
				while (sci.hasNext()) {
					e = (PdfPTable) sci.next();
					
					cell = new PdfPCell(e);
					fullBOMTable.addCell(cell);
				}

				// Add Footer
				System.out.println(">>>>>>>>>>"+usingFooter);
				if (usingFooter) {
					debug(2, "usingFooter");
					if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
						for (Iterator footI = BOMFooter.iterator(); footI.hasNext();) {
							e = (PdfPTable) footI.next();
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
						}
						// Add BOM to content
						content.add(fullBOMTable);
						this.pageTitles.add(getPageTitleText(tparams));
					} else {
						// Add BOM to content
						content.add(fullBOMTable);
						this.pageTitles.add(getPageTitleText(tparams));
						// Add Footer to content
						content.addAll(BOMFooter);
						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));

					}
				} else {
					content.add(fullBOMTable);
					this.pageTitles.add(getPageTitleText(tparams));
				}
			} else { // BOM sections different pages
				this.pageTitles.addAll(sectionPageTitles);
				Iterator sci = spcontent.iterator();
				// Add the first section to the fullBOMTable in case we have a
				// Header
				e = (PdfPTable) sci.next();
				cell = new PdfPCell(e);
				fullBOMTable.addCell(cell);
				content.add(fullBOMTable);
				while (sci.hasNext()) {
					e = (PdfPTable) sci.next();
					if (!sci.hasNext() && usingFooter) {
						// Last element && using a footer
						if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
							fullBOMTable = new PdfPTable(1);
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
							for (Iterator footI = BOMFooter.iterator(); footI.hasNext();) {
								e = (PdfPTable) footI.next();
								cell = new PdfPCell(e);
								fullBOMTable.addCell(cell);
							}
							content.add(fullBOMTable);
						} else {
							// Add last element
							content.add(e);
							// Add Footer
							content.addAll(BOMFooter);
							this.pageTitles.addAll(
									(Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));

						}
					} else {
						// Not the last element
						content.add(e);
					}
				} // while
			}
			
			if(moveFiberContent){
				System.out.println("data in final block>>>"+moveFiberContent);
				Iterator sci = spcontentNew.iterator();
				while (sci.hasNext()) {
					e = (PdfPTable) sci.next();
					
					cell = new PdfPCell(e);
					fullBOMTable1.addCell(cell);
					//PdfPCell cell1 = new PdfPCell(pgh.multiFontPara("To do something here to print the Care code description"));
					if(FormatHelper.hasContent(hbiCareInstructions.toString())) {
					PdfPCell cell1 = new PdfPCell(pgh.multiFontPara(hbiCareInstructions.toString()));

					
					fullBOMTable1.addCell(cell1);
					}
				}
				
				// Add Footer
				System.out.println(">>>>>>>>>>"+usingFooter);
				if (usingFooter) {
					debug(2, "usingFooter");
					if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
						for (Iterator footI = BOMFooter.iterator(); footI.hasNext();) {
							e = (PdfPTable) footI.next();
							cell = new PdfPCell(e);
							fullBOMTable1.addCell(cell);
						}
						// Add BOM to content
						content.add(fullBOMTable1);
						this.pageTitles.add(getPageTitleText(tparams));
					} else {
						// Add BOM to content
						content.add(fullBOMTable1);
						this.pageTitles.add(getPageTitleText(tparams));
						// Add Footer to content
						content.addAll(BOMFooter);
						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));

					}
				} else {
					content.add(fullBOMTable1);
					this.pageTitles.add(getPageTitleText(tparams));
				}
				
			}
			else {
				if(FormatHelper.hasContent(hbiCareInstructions.toString())) {

			PdfPCell cell2 = new PdfPCell(pgh.multiFontPara(hbiCareInstructions.toString()));

			
			fullBOMTable.addCell(cell2);
				}
			}

		}
		return content;
	}

	public Collection getMultiSpecColorways(Map params, LCSSourcingConfig source) {

		Collection specificColl = new ArrayList();

		HashMap specificSKUMap = new HashMap();

		try {
			String seasonMaster_Oid = (String) params.get("SEASONMASTER_ID");

			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSQuery.findObjectById(seasonMaster_Oid);
			LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
			LCSSourcingConfigQuery sQuery = new LCSSourcingConfigQuery();

			SearchResults skuSourceSR = sQuery.getSKUSourcingLinkDataForConfig(source, season, true);
			if (skuSourceSR != null && skuSourceSR.getResultsFound() > 0) {
				Collection<FlexObject> skuSourceFOColl = skuSourceSR.getResults();

				for (FlexObject skuSourceFo : skuSourceFOColl) {

					String skumasterId = skuSourceFo.getData("SKUMASTER.IDA2A2");
					String skuMasterName = skuSourceFo.getData("SKUMASTER.NAME");
					specificSKUMap.put(skumasterId, skuMasterName);

				}
			}

			if (!specificSKUMap.isEmpty()) {
				specificColl = specificSKUMap.keySet();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return specificColl;
	}

	/////////////////////////////////////////////////////////////////////////////
	public static void debug(String msg) {
		debug(msg, 1);
	}

	public static void debug(int i, String msg) {
		debug(msg, i);
	}

	public static void debug(String msg, int i) {
		if (DEBUG && i <= DEBUG_LEVEL)
			System.out.println(msg);
	}

	/**
	 * 
	 * @param bomPart
	 * @return
	 * @throws WTException
	 *             GP teck pack customizations
	 *             Select_teck_pack_page_requirements - 09/20/2018
	 */
	public boolean isValidateGPBOMTypeForBOMReport(FlexBOMPart bomPart, Map params) throws WTException {
		boolean bomGPTachPackStatus = true;
		LCSProduct prodObj = null;
		String bomPartFlexTypePath = bomPart.getFlexType().getFullName(true);
//		WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
//		LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(wtPartMaster);
		LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(bomPart.getOwnerMaster());
		if (lcsPart instanceof LCSProduct) {
			prodObj = (LCSProduct) lcsPart;
			String productFlexTypePath = prodObj.getFlexType().getFullName(true);
			if ((BASIC_CUT_AND_SEW_GARMENT.equalsIgnoreCase(productFlexTypePath)||LABEL_TYPE.equalsIgnoreCase(productFlexTypePath))
					&& !Arrays.asList(GARMENT_BOM_TYPE_PATH.split(",")).contains(bomPartFlexTypePath)) {
				debug(" <<<<<< bomPart >>>>>>>" + bomPart.getName());
				bomGPTachPackStatus = false;
			}
		} // Check if user selected season before generating report
		if (!FormatHelper.hasContent((String) params.get("SEASONMASTER_ID"))) {
			bomGPTachPackStatus = false;
			System.out.println(
					"!!!! GPcolorwayReport Printing will be skipped, as no season selected while generating Report for bom ["
							+ bomPart.getName() + "]");
		}
		return bomGPTachPackStatus;
	}
}

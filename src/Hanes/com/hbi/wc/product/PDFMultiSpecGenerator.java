/*
 * PDFMultiSpecGenerator.java
 *
 * Created on December 1, 2006, 2:53 PM
 */

package com.hbi.wc.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wt.doc.WTDocumentMaster;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.util.WTException;

import com.hbi.wc.flexbom.util.HBILabelSpecGetter;
import com.hbi.wc.flexbom.util.HBISpecificationPDFGenUtil;
import com.lcs.wc.client.ApplicationContext;
import com.lcs.wc.client.web.pdf.PDFPageSize;
import com.lcs.wc.construction.LCSConstructionQuery;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.document.FileRenamer;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentClientModel;
import com.lcs.wc.document.LCSDocumentHelper;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.document.ZipGenerator;
import com.lcs.wc.epmstruct.LCSEPMDocumentQuery;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeQueryStatement;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.partstruct.FlexPartToSpecLinkQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.revise.ReviseLogic;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.OrderConfirmation;
import com.lcs.wc.sourcing.OrderConfirmationMaster;
import com.lcs.wc.sourcing.RFQRequest;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.specification.SingleSpecPageUtil;
import com.lcs.wc.util.DeleteFileHelper;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.util.ZipHelper;
import com.lcs.wc.product.PDFProductSpecificationMeasurements2;
import com.lcs.wc.product.ProductDestinationQuery;
/**
 *
 * @author  Chuck
 */
public class PDFMultiSpecGenerator extends com.lcs.wc.product.PDFMultiSpecGenerator {
	public static final String VIEW_PROD[] = LCSProperties.get("com.hbi.defaultView.TPReport").split(",");
    public static final String TYPE_COMP_DELIM = PDFProductSpecificationGenerator2.TYPE_COMP_DELIM;
    private static String splitStr = "\\|\\~\\*\\~\\|";
    private static LinkedHashMap<String, Integer> prodSizeMap = new LinkedHashMap();

	public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
    public static final String BASIC_CUT_AND_SEW_SELLING = LCSProperties.get("com.hbi.specification.SellingProductType", "BASIC CUT & SEW - SELLING");
    public static final String VIEW_GPPROD[] = LCSProperties.get("com.hbi.defaultView.GPTPReport").split(",");
	public static final String VIEW_SPPROD[] = LCSProperties.get("com.hbi.defaultView.SPTPReport").split(",");	
    public static String DEFAULT_PAPER_SIZE = LCSProperties.get("com.lcs.wc.product.PDFMultiSpecGenerator.defaultPaperSize", "LETTER");
	public static final String MULTI_SPEC_BOM = LCSProperties.get("com.hbi.multispecBOM");
	public static final Boolean VIEW_PATTERN_SPEC_IN_CHOOSER = LCSProperties.getBoolean("com.hbi.wc.flexbom.util.HBIPatternSpec.ViewPatternSpecInChooser");
    public static String SPEC_PAGES = "specPages";
    public static String COLORWAYS_DATA = "colorwaydata";
    public static String SIZE1_DATA = "size1data";
    public static String SIZE2_DATA = "size2data";
    public static String DESTINATION_DATA = "destinationdata";
    public static String PAGE_OPTIONS = "pageOptions";
    public static String SELECTED_VIEWS = "viewSelect";
    public static String COLORWAYS_PER_PAGE = "numColorwaysPerPage";
    public static String SIZES_PER_PAGE = "numSizesPerPage";
    public static String USE_SIZE1_SIZE2 = "useSize1Size2";
    public static String SHOW_COLOR_SWATCHES = "showColorSwatches";
    public static String SHOW_MATERIAL_THUMBNAIL = "showMaterialThumbnail";
    public static String PAPER_SIZE = "paperSize";
    public static String USE_LANDSCAPE = "useLandscape";
    public static String UOM = "uom";
    final static String AVAIL_DOCS = "availDocs";
    final static String SECONDARY = "includeSecondaryContent";
    final static String SPECIFICATION = "OR:com.lcs.wc.specification.FlexSpecification:";
    public static String SEASONMASTER_ID = "SEASONMASTER_ID";
    
    public static String CDELIM = HBIPDFProductSpecificationGenerator2.TYPE_COMP_DELIM;
    public static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.product.PDFMultiSpecGenerator.verbose");
    
    /** Creates a new instance of PDFMultiSpecGenerator */
    public PDFMultiSpecGenerator() {
    }
    
    
    ProductHeaderQuery phq = new ProductHeaderQuery();
    
    protected PDFProductSpecificationGenerator2 getGenerator(FlexSpecification spec) throws WTException{
    	Object[] arguments = {spec};
    	PDFProductSpecificationGenerator2 ppsg = (PDFProductSpecificationGenerator2) HBIPDFProductSpecificationGenerator2.getOverrideInstance(arguments);

    	return ppsg;
    }
    
    protected void setGeneratorParams(HBIPDFProductSpecificationGenerator2 ppsg, FlexSpecification spec, Collection specPages, Map params, String outputFolder)throws WTException{
    	if(DEBUG){
	        System.out.println("----------------------------------------------");
	        System.out.println("PDFMultiSpecGenerator - setGeneratorParams");
	        System.out.println("ppsg: " + ppsg);
	        System.out.println("spec: " + spec);
	        System.out.println("specPages: " + specPages);
	        System.out.println("params: " + params);
	        System.out.println("----------------------------------------------");
    	}

        String sourceNumber = "";
        String colorways = "";
        String sizes1 = "";
        String sizes2 = "";
        String destinations = "";
        
        LCSSeason season = null;
        String oid = (String) params.get("oid");
        
        LCSProduct product = (LCSProduct)VersionHelper.getVersion(spec.getSpecOwner(), "A");
        
        if(FormatHelper.hasContent((String) params.get("seasonId") ) ){
        	season = (LCSSeason) LCSQuery.findObjectById((String) params.get("seasonId") );
        }else if(FormatHelper.hasContent(oid) && oid.indexOf("LCSSeason") > -1){
        	season = (LCSSeason) LCSQuery.findObjectById(oid );
        }
        sourceNumber = FormatHelper.getNumericObjectIdFromObject(spec.getSpecSource());
        ppsg.setSources(sourceNumber);
        
        colorways    = getColorways(spec, product, season);
        ppsg.setColorways(colorways);
        
        sizes1       = getSizes1(spec, product);
        if (sizes1!=null && (product != null) && product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) {
			
			
			String[] sizeValuesArr = new String[100];
			sizeValuesArr = sizes1.split(splitStr);
			 
			 
			 for(int i=0; i<sizeValuesArr.length;i++) {
				 prodSizeMap.put(sizeValuesArr[i], i);
			 }
			//End: Used for Sorting based on PSD
		}
        
        String sizeArrayList = "";
        Map Sizemap=new HashMap();
    	StringBuffer values = new StringBuffer();
    	TreeMap <Integer, String> treeMap= new TreeMap();

    	if(product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT) && product.getValue("hbiGarmentSizeTable")!=null){
    	
    		LCSMOATable moaSizingTable = (LCSMOATable) product.getValue("hbiGarmentSizeTable");
    				 
    		Collection<FlexObject> moaSizing_Collection = moaSizingTable.getRows();
    		if(moaSizing_Collection != null && moaSizing_Collection.size() > 0){
    		
    			for(FlexObject moaSizing_FO : moaSizing_Collection){
    				Boolean activeSizeBoolean = moaSizing_FO.getBoolean("HBIACTIVESIZEMOATABLE");
    				if(activeSizeBoolean){
    					String activeSizeStr = moaSizing_FO.getString("HBIPLMPATTERNSIZE");
    					
    					treeMap.put(prodSizeMap.get(activeSizeStr),activeSizeStr);
    				}
    			}
    		}
    	}
    	
     Iterator treemapitr=treeMap.values().iterator();
     while(treemapitr.hasNext()) {
    	 String size=(String)treemapitr.next();
    	 sizeArrayList=sizeArrayList+"|~*~|"+size;
     }

        ppsg.setSize1Sizes(sizeArrayList);
        
        sizes2       = getSizes2(spec, product);
        ppsg.setSize2Sizes(sizes2);
        
        destinations = getDestinations(spec, product);
        ppsg.setDestinations(destinations);
        
        Collection<String> components = getComponentsForSpec(spec, product, specPages, params);
        ppsg.setPages(components);
        
        
        String bomViewStr=buildBOMViews(product,season,components);


        
        ppsg.setPageOptions((String)params.get(PAGE_OPTIONS));
        
        ppsg.setColorwaysPerPage((String)params.get(COLORWAYS_PER_PAGE));
        
        ppsg.setSizesPerPage((String)params.get(SIZES_PER_PAGE));
        
        Collection<?> scgs = SizingQuery.findProductSizeCategoriesForProduct(product).getResults();
        String sizeCatId;
        if (!scgs.isEmpty()) {
            FlexObject firstElement = (FlexObject)scgs.iterator().next();  // only concerned with the first sizing category
            sizeCatId = "OR:com.lcs.wc.sizing.ProductSizeCategory:" + firstElement.getData("PRODUCTSIZECATEGORY.IDA2A2");
        } else {
        	sizeCatId = "";	
        }
        
        ppsg.setProductSizeCatId(sizeCatId);
        
        ppsg.setShowColorSwatch((String)params.get(SHOW_COLOR_SWATCHES));
        ppsg.setShowMatThumbnail((String)params.get(SHOW_MATERIAL_THUMBNAIL));
        ppsg.setUseSize1Size2((String)params.get(USE_SIZE1_SIZE2));
        
        
        if(FormatHelper.hasContent((String)params.get(PAPER_SIZE))){
            ppsg.setPageSize(PDFPageSize.getPageSize((String)params.get(PAPER_SIZE)));
        }
        else{
            ppsg.setPageSize(PDFPageSize.getPageSize(DEFAULT_PAPER_SIZE));
        }
        
        if("false".equals((String)params.get(USE_LANDSCAPE))){
            ppsg.setLandscape(false);
        }
        else{
            ppsg.setLandscape(true);
        }
        
        HashMap params2 = new HashMap();
        params2.put(PDFProductSpecificationMeasurements2.UOM, params.get(UOM));
        if (season != null) {
        	params2.put(SEASONMASTER_ID, FormatHelper.getObjectId((LCSSeasonMaster)season.getMaster()));
        }
        
        boolean includeCADDocuments = "true".equals((String)params.get("includeCADDocuments"));
        if (includeCADDocuments) {
	        Collection<FlexObject> linkData = new LCSEPMDocumentQuery().findAssociatedEPMDocumentsByCriteria(spec, new HashMap<String,String>());
	    	List<String> specLinks = new ArrayList<String>();
	    	
	    	Iterator<FlexObject> linkIter = linkData.iterator();
	       	while (linkIter.hasNext()){
	       		FlexObject flexObject = (FlexObject)linkIter.next();
	       	    specLinks.add("OR:com.lcs.wc.epmstruct.FlexEPMDocToSpecLink:"+flexObject.getString("FLEXEPMDOCTOSPECLINK.IDA2A2"));
	    	}
	        params2.put("SPEC_CAD_DOCS", specLinks);
        }
        
       
        
        boolean includeParts = "true".equals((String)params.get("includeAllParts"));
        if (includeParts) {
	        Collection<FlexObject> partLinkData = new FlexPartToSpecLinkQuery().findAssociatedPartsByCriteria(spec, new HashMap<String,String>());
	    	List<String> partToSpecLinks = new ArrayList<String>();
	    	
	    	Iterator<FlexObject> linkIter = partLinkData.iterator();
	       	while (linkIter.hasNext()){
	       		FlexObject flexObject = (FlexObject)linkIter.next();
	       		partToSpecLinks.add("OR:com.lcs.wc.partstruct.FlexPartToSpecLink:"+flexObject.getString("FlexPartToSpecLink.IDA2A2"));
	    	}
	        params2.put("SPEC_PARTS", partToSpecLinks);
        }
        
        ppsg.setShowIndentedBOM((String)params.get("exportedIndentedBOM"));
        
        ppsg.setPartFilter((String)params.get("partFilter"));
        
		ppsg.setAddlParams(params2);
		

		ppsg.setSpecPageOptions(components);

		ppsg.setShowChangeSince((String)params.get("showChangeSince"));
		
        ppsg.outputLocation = outputFolder;
       // ppsg.setBOMSectionViews((String)params.get(SELECTED_VIEWS));
        ppsg.setBOMSectionViews(bomViewStr);

       
        ppsg.setDestinations("");
        
    
    }

    private String buildBOMViews(LCSProduct product, LCSSeason season, Collection<String> components) throws WTException {
    	HashMap sectionMap=new HashMap();
    	StringBuffer bomViewString =new StringBuffer();
    	//String VIEW_CMN_PROD[] = (appContext.getProductARev().getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) ? VIEW_GPPROD : VIEW_PROD;
    	String VIEW_CMN_PROD[] =VIEW_PROD;
    	if(product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)){
    	VIEW_CMN_PROD= VIEW_GPPROD ;
    	}
    	else if(product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_SELLING)) {
    	VIEW_CMN_PROD= VIEW_SPPROD ;
    	}
    	
    	if(VIEW_CMN_PROD.length > 1) {
    		for(int i=0;i<VIEW_CMN_PROD.length;i++) {				
    			String sectionAndView=VIEW_CMN_PROD[i];
    			String sectionName=sectionAndView.substring(0,sectionAndView.indexOf("|~*~|"));
    			String defViewName=sectionAndView.substring(sectionAndView.indexOf("|~*~|")+5,sectionAndView.length());
    			//By defaut the below view is assigned from property entry for Routing BOMs.
    			//This should be changed for certain seasons when GPReport is selected as the request type.
    			Boolean gpSeasonRoutingBOMView=false;
    			if("Garment Product Routing (Sys)".equalsIgnoreCase(defViewName)){
    				
    				if(season!=null ){
    					String gpSeasonFType = season.getFlexType().getFullName(true);
    					
    					if ("Season\\Garment\\Innerwear".equals(gpSeasonFType)) {
    						
    						String seasonDivision = (String)season.getValue("hbiBusiness");
    						
    						if((FormatHelper.hasContent(seasonDivision)) && ("basicsEIW".equalsIgnoreCase(seasonDivision) ||"biw".equalsIgnoreCase(seasonDivision) ||
    						"giw".equalsIgnoreCase(seasonDivision) ||"miw".equalsIgnoreCase(seasonDivision) ||"wiw".equalsIgnoreCase(seasonDivision))){
    						defViewName="UW Routing View (Sys)";
    						gpSeasonRoutingBOMView=true;
    						}
    					}
    				}
    				
    			}
    	//End User Story 59201 Dec 2019
    	//#79973 - Common Config-GPReport Customs Fiber Code Default View-Development - Start
    			if("Customs Fiber Content (Sys)".equalsIgnoreCase(defViewName)){
    				
    				if(season!=null ){
    					String gpSeasonFType1 = season.getFlexType().getFullName(true);
    					
    					if ("Season\\Garment\\Activewear".equals(gpSeasonFType1)) {
    						String seasonDivision = (String)season.getValue("hbiBusiness");
    						String brand = (String)season.getValue("brand");
    					    defViewName="Colorway Labels";

    						
    						
    					}
    				}
    				
    			}
    		//#79973 - Common Config-GPReport Customs Fiber Code Default View-Development - end
    			if(!sectionMap.containsKey(defViewName)){
    				sectionMap.put(defViewName,sectionName);
    			}
    			else{
    				String sectionNames=(String)sectionMap.get(defViewName);
    				sectionNames=sectionNames+"|~*~|"+sectionName;
    				sectionMap.put(defViewName,sectionNames);				
    			}
    				//cutPartSpread|~*~|Casing View
    		}
    	}
        Vector bomOptions = new Vector(new com.lcs.wc.util.ClassLoadUtil(FileLocation.productSpecBOMProperties2).getKeyList());
 	   Iterator bomOptionsIt = bomOptions.iterator();
 	   String moaDelem="|~*~|";
 	   String secondDelem="-:-";
  		com.lcs.wc.client.ClientContext lcsContext=new com.lcs.wc.client.ClientContext();
  	   Collection reportColumns = new ArrayList();
  	   String defaultViewId = "";
  	   String editBomActivity = "EDIT_BOM";
  	 FlexType bomType=null;
     Collection section = new ArrayList();
		Map viewMap=new HashMap();



 	  while(bomOptionsIt.hasNext()) { 
		   String bomOption = (String)bomOptionsIt.next();
		   Iterator itr= components.iterator();
		   while(itr.hasNext()) {
			   String componentStr=(String)itr.next();
			   if(componentStr.contains("BOM")) {
				   String[] result = componentStr.split("-:-");
				   FlexBOMPart bomPart=(FlexBOMPart)LCSQuery.findObjectById(result[1]);
				   

				    bomType=bomPart.getFlexType();
				   String bomTypeStr=FormatHelper.getObjectId(bomType);
				      ApplicationContext appcontext=new ApplicationContext();
					 reportColumns =lcsContext.getContext().viewCache.getViews(FormatHelper.getObjectId(bomType), editBomActivity);
					 defaultViewId = lcsContext.getContext().viewCache.getDefaultViewId(FormatHelper.getObjectId(bomType), editBomActivity);
					//HBI Code upgarde: Added by Vishal for Hanes Customization for default views sectionMap.put(defViewName,sectionName);
					Collection viewCollection=new Vector();
					viewCollection=lcsContext.getContext().viewCache.getViews(FormatHelper.getObjectId(bomType), editBomActivity);

					Iterator viewIter03=viewCollection.iterator();
					while (viewIter03.hasNext()){
						FlexObject fo=(FlexObject)viewIter03.next();
						String viewID="";
						viewID="OR:com.lcs.wc.report.ColumnList:"+fo.getString("COLUMNLIST.IDA2A2");
						String viewName="";
						viewName = lcsContext.getContext().viewCache.getView(viewID).getDisplayName();
						if(sectionMap.containsKey(viewName)){

							String sectionNames=(String)sectionMap.get(viewName);
							if(sectionNames.indexOf("|~*~|")>-1){
								 StringTokenizer stkToken = new StringTokenizer(sectionNames, "|~*~|");
								 String typePath = "";
								 while(stkToken.hasMoreTokens()){
									 String sectionName = stkToken.nextToken();
									
									 FlexObject foView=new FlexObject();
									 foView.put("VIEWNAME",viewName.trim());
									 foView.put("VIEWID",viewID.trim());
									 foView.put("TYPEID",FormatHelper.getObjectId(bomType));

									 
									 viewMap.put(sectionName,foView);
								}
							}
							else{
								FlexObject foView=new FlexObject();
								foView.put("VIEWNAME",viewName.trim());
								foView.put("VIEWID",viewID.trim());
								 foView.put("TYPEID",FormatHelper.getObjectId(bomType));

								viewMap.put(sectionNames,foView);
								
							}
						}
						
						else {
							
						}
					}
			   
			   AttributeValueList sectionAttList = bomType.getAttribute("section").getAttValueList();
			   section = sectionAttList.getSelectableKeys(lcsContext.getContext().getLocale(), true);
				boolean setDefView=false;

			   Iterator sectionItr = section.iterator();
			   while (sectionItr.hasNext()){
					  String secID = (String)sectionItr.next();
						FlexObject foview=new FlexObject();
						foview=(FlexObject)viewMap.get(secID);

							String defaultViewName="";
							String defaultViewID="";
							if(foview!=null ){
								defaultViewName=(String)foview.getString("VIEWNAME");
								
								defaultViewID=(String)foview.getString("VIEWID");
								String bomNode=null;
								bomNode=bomPart.getFlexType().getFullName();
								
								if("Materials\\HBI\\Garment Sew".equals(bomNode)||"Materials\\HBI\\Garment Cut".equals(bomNode))
							   {
									
									//defaultViewID=defaultViewId.substring(defaultViewId.lastIndexOf(":"));	

									defaultViewID=defaultViewId;
									
									setDefView=true;
								}
								}
							else {
								defaultViewID=defaultViewId;
							}
							Iterator ri = reportColumns.iterator();
							while(ri.hasNext()) {
							FlexObject fobj = (FlexObject)ri.next();
							if(FormatHelper.hasContent(defaultViewID)) {
								defaultViewID=defaultViewID.substring(defaultViewID.lastIndexOf(":"));	

								bomViewString.append("|~*~|"+bomOption + TYPE_COMP_DELIM + FormatHelper.getObjectId(bomType)+ TYPE_COMP_DELIM + secID + TYPE_COMP_DELIM+"ColumnList"+defaultViewID);

								/*if(setDefView) {
									bomViewString.append("|~*~|"+bomOption + TYPE_COMP_DELIM + FormatHelper.getObjectId(bomType)+ TYPE_COMP_DELIM + secID + TYPE_COMP_DELIM+"ColumnList:"+defaultViewID);

								}
								else {
									bomViewString.append("|~*~|"+bomOption + TYPE_COMP_DELIM + FormatHelper.getObjectId(bomType)+ TYPE_COMP_DELIM + secID + TYPE_COMP_DELIM+"ColumnList:"+fobj.getString("COLUMNLIST.IDA2A2"));

								}*/
							}
							
							}
		   }
		   }
 	  }
		   
 	  }
 	
    return bomViewString.toString();
	}

	public String createZipForSpecs(Collection specIds, Map params, String outputFolder) 
        throws WTException
    {
    	if(DEBUG){
	        System.out.println("----------------------------------------------");
	        System.out.println("PDFMultiSpecGenerator - createZipForSpecs");
	        System.out.println("params: 1 " + params);
	        System.out.println("specIds: " + specIds);
	        System.out.println("----------------------------------------------");
    	}
        String seasonId = (String)params.get("oid");
		LCSSeason season = null;
		if(FormatHelper.hasContent(seasonId)){
			if(seasonId.indexOf("RFQRequest:") > -1){
				 if(params.get("currentSeasonId")!=null && FormatHelper.hasContent((String)params.get("currentSeasonId"))){
					  season = (LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:" + (String)params.get("currentSeasonId"));
				 }else{
					 RFQRequest rfq = (RFQRequest)LCSQuery.findObjectById(seasonId);
					 if(rfq.getValue("seasonReference")!=null){
						season = (LCSSeason)rfq.getValue("seasonReference");
					 }
				 }
			}else if(seasonId.indexOf("OrderConfirmation:") > -1){
				 if(params.get("currentSeasonId")!=null && FormatHelper.hasContent((String)params.get("currentSeasonId"))){
					  season = (LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:" + (String)params.get("currentSeasonId"));
				 }else{
					 OrderConfirmation oc = (OrderConfirmation)LCSQuery.findObjectById(seasonId);
					 OrderConfirmationMaster ocMaster = (OrderConfirmationMaster)oc.getMaster();
					 if(ocMaster.getSeasonMaster() != null){
						season = (LCSSeason)VersionHelper.latestIterationOf(ocMaster.getSeasonMaster());
					 }
				 }
			}else{
				season = (LCSSeason)LCSQuery.findObjectById(seasonId);
			}
		}

		LCSSeasonMaster sMaster = null;
		if(season!=null){
			sMaster = season.getMaster();
		}

        String specPagesStr = (String)params.get(SPEC_PAGES);
        Collection<String> specPages = null;
        if(!FormatHelper.hasContent(specPagesStr)){
            specPages = new ArrayList<String>();
        }
        else{
            specPages = MOAHelper.getMOACollection(specPagesStr);
        }
        specPagesStr = (String)params.get(AVAIL_DOCS);
        ArrayList<String> flexTypes = null;
        if (FormatHelper.hasContent(specPagesStr)) {
            Collection<String> availDocs = MOAHelper.getMOACollection(specPagesStr);
            availDocs = LCSDocumentQuery.getAvailDocTypes(availDocs);
            flexTypes = new ArrayList<String>(availDocs.size());
            FlexType flexType = null;
            for (Iterator<?> it = availDocs.iterator(); it.hasNext(); ) {
                specPagesStr = (String)it.next();
                flexType = FlexTypeCache.getFlexTypeFromPath(specPagesStr);
                specPagesStr = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(flexType));
                flexTypes.add(specPagesStr);
            }
        } else {
            flexTypes = new ArrayList<String>();
        }
        specPagesStr = (String)params.get(SECONDARY);
        boolean secondaryContent = "true".equals(specPagesStr);
        specPagesStr = (String)params.get("includeChildSpecs");
        boolean includeChildSpecs = "true".equals(specPagesStr);

        if(DEBUG) {System.out.println("specPages: " + specPages);}

        Iterator<?> ids = LCSQuery.getObjectsFromCollection(specIds).iterator();
        LCSDocumentQuery query = new LCSDocumentQuery();
        HBIPDFProductSpecificationGenerator2 ppsg = null;

        ZipGenerator zipGen = new ZipGenerator();
        zipGen.setIncludeSecondary(secondaryContent);
        ArrayList<String> allZips = new ArrayList<String>();
        String oneZip = null;
        String zipFile = null;
        String pdf = null;
        String childPdf = null;
        Collection<?> childSpecs = null;
        FlexSpecification spec = null;
        FlexSpecification childSpec = null;
        Vector<?> childDocs = null;
        Vector docs = null;
        while(ids.hasNext()){
            try {
                spec = (FlexSpecification)ids.next();
                if(DEBUG) {System.out.println("id: " + FormatHelper.getVersionId(spec));}
                if (includeChildSpecs) {
                    childSpecs = FlexSpecQuery.findSpecToSpecLinks(spec);
                }

                ppsg = (HBIPDFProductSpecificationGenerator2) getGenerator(spec);
                setGeneratorParams(ppsg, spec, specPages, params, outputFolder);
               pdf = ppsg.generateSpec();

                //Fix SPR#2067945 04/21/2011
                String objId = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(spec));
                docs = query.findPartDocAvailReferences(flexTypes, spec);
                if (ReviseLogic.REVISE_DOCUMENT) {
                	Vector<?> descDocs = (Vector<?>)query.findPartDocDescribe(spec, flexTypes);
                	docs.addAll(descDocs);
                }
                ArrayList<String> pdfChildren = new ArrayList<String>();
                if (includeChildSpecs) {
                    for (Iterator<?> it = childSpecs.iterator(); it.hasNext(); ) {
                        FlexObject fo = (FlexObject)it.next();
                        specPagesStr = fo.getData("FLEXSPECIFICATION.IDA2A2");
                        childSpec = (FlexSpecification)LCSQuery.findObjectById(SPECIFICATION  + specPagesStr);
                        childDocs = query.findPartDocAvailReferences(flexTypes, childSpec);
                        docs.addAll(childDocs);
                        if (ReviseLogic.REVISE_DOCUMENT) {
                        	childDocs = (Vector)query.findPartDocDescribe(spec,flexTypes);
                        	docs.addAll(childDocs);
                        }
                        	
                        
                        ppsg = (HBIPDFProductSpecificationGenerator2) getGenerator(childSpec);
                        setGeneratorParams(ppsg, childSpec, specPages, params, outputFolder);
                        childPdf = ppsg.generateSpec();
                        if(DEBUG) System.out.println("finished generating child spec");
                        pdfChildren.add(childPdf);
                    }
                }
                LCSProduct product = (LCSProduct)VersionHelper.getVersion(spec.getSpecOwner(), "A");
                addProductDocs(product, docs, query, flexTypes);
				if(sMaster!=null){
					product = LCSSeasonQuery.getProductForSeason(product, sMaster);
				}
                addProductDocs(product, docs, query, flexTypes);
                
                //temporarily hide while all the functions are ready.
            	/*String epmFilterName = (String)params.get("cadDocFilter");
            	Collection<String> epmIds = null;
            	//find all associated EPMDocuments
				if (includeCADDocuments) {
					epmIds = LCSEPMDocumentHelper.getEPMDocumentsIdBySpec(spec);
				}
				oneZip = zipGen.addToZipFile(docs, epmFilterName, pdfChildren, pdf, epmIds);*/
				oneZip = zipGen.addToZip(docs, pdfChildren, pdf);
                if (zipFile == null) {
                    int indx = oneZip.lastIndexOf(java.io.File.separator);
					String seasonName = "";
					if(season!=null){
						seasonName =  season.getName() + "-";
					}
                    zipFile = oneZip.substring(0, indx + 1) 
                    	+ FormatHelper.formatRemoveProblemFileNameChars(seasonName + "ProductSpecs-" +
                    			wt.session.SessionHelper.getPrincipal().getName())
                    	+ ".zip";
                    zipFile = FileRenamer.rename(zipFile);
                }
                allZips.add(oneZip);
                pdfChildren.add(pdf);
                deletePDFs(pdfChildren);
                
                String documentVault = (String)params.get("documentVault");
                String vaultDocumentTypeId = (String)params.get("vaultDocumentTypeId");
                
                if ("true".equals(documentVault) && FormatHelper.hasContent(vaultDocumentTypeId)){

                	LCSDocumentClientModel documentModel = new LCSDocumentClientModel();
                	FlexType vaultDocumentType = null;
                    try {
                    	       	
                    	if(FormatHelper.hasContent(vaultDocumentTypeId)){
                    		vaultDocumentType = FlexTypeCache.getFlexType(vaultDocumentTypeId);
                    	}
                       
                        Collection<LCSDocument> exisitDocuments  = LCSQuery.getObjectsFromResults(new LCSDocumentQuery().findPartDocReferences(spec), "OR:com.lcs.wc.document.LCSDocument:", "LCSDocument.IDA2A2");
                        LCSDocument exisitDocument = null;
                        String exisitDocumentName = null;
                        String exisitDocumentSequenceNumber = "";
                        int documentSequenceNumber = 0;
                        String documentName = "Tech Pack for " + spec.getName() + "-" + product.getName()+ " - ";
                        Pattern pattern = Pattern.compile("[0-9]*");   
                        Iterator<LCSDocument> exisitDocumentIter = exisitDocuments.iterator();
                        
                        while (exisitDocumentIter.hasNext()){
                        	
                        	exisitDocument = exisitDocumentIter.next();
                        	exisitDocumentName = exisitDocument.getName();
                        	
                        	if (exisitDocumentName.startsWith(documentName)){
                        		if (exisitDocumentName.length() >= exisitDocumentName.lastIndexOf(" - ") + 3){
                        			exisitDocumentSequenceNumber = exisitDocumentName.substring(exisitDocumentName.lastIndexOf(" - ") + 3);
                        			if (pattern.matcher(exisitDocumentSequenceNumber).matches()){
                        				if (Integer.valueOf(exisitDocumentSequenceNumber)>documentSequenceNumber){
                        					documentSequenceNumber = Integer.valueOf(exisitDocumentSequenceNumber);
                        				}
                        			}
                        		}
                        	}
                        }
                        
                        documentSequenceNumber = documentSequenceNumber + 1 ;                        
                        documentModel.setFlexType(vaultDocumentType);
                        documentModel.setValue("name",documentName + documentSequenceNumber);
                        documentModel.save();
                        String otherside = FormatHelper.getObjectId((WTDocumentMaster)(documentModel.getBusinessObject().getMaster()));
                        String newDocRefIds = otherside;
                        
                        Collection<String> docIds = MOAHelper.getMOACollection(newDocRefIds);

                        documentModel.associateContent(oneZip);
                        //Bypass modify access check on the spec by calling the service directly
                        //documentModel.associateDocuments(FormatHelper.getObjectId(spec), docIds);
                        LCSDocumentHelper.service.associateDocuments(FormatHelper.getObjectId(spec), (Vector)docIds);


                   } catch(LCSException e){
                	   e.printStackTrace();
                   }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (zipFile != null) {
            ZipHelper zipHelper = new ZipHelper(zipFile, allZips);
            zipHelper.zip();
        }
        if (params.containsKey("pdfRFQ")) {
            DeleteFileHelper.deleteFile((String)params.get("pdfRFQ"));
        }
        return zipFile;

    }

    private void deletePDFs(ArrayList<String> pdfFiles)
    {
        int size = pdfFiles.size();
        String pdf = null;
        java.io.File file = null;
        for (int i = 0; i < size; i++) {
            pdf = (String)pdfFiles.get(i);
            file = new java.io.File(pdf);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    void addProductDocs(LCSProduct product, Vector docs, LCSDocumentQuery query, 
        ArrayList<String> flexTypes)  throws WTException, java.beans.PropertyVetoException
    {
        if (flexTypes.size() == 0) {
            return;
        }
        String objId = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(product));
        Vector docs1 = query.findPartDocAvailReferences(flexTypes, product);
        docs.addAll(docs1);
        if (ReviseLogic.REVISE_DOCUMENT) {
        	docs1 = (Vector) query.findPartDocDescribe(product, flexTypes);
        	docs.addAll(docs1);
        }
        
        Iterator skus = LCSSKUQuery.findSKUs(product).iterator();
        LCSSKU colorway = null;
        while (skus.hasNext()) {
            colorway = (LCSSKU)skus.next();
            objId = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(colorway));
            docs1 = query.findPartDocAvailReferences(flexTypes, colorway);
            docs.addAll(docs1);
            if (ReviseLogic.REVISE_DOCUMENT) {
            	docs1 = (Vector) query.findPartDocDescribe(colorway, flexTypes);
            	docs.addAll(docs1);
            }
        }
   }
    
    public void createPDFSpecifications(Collection specIds, Map params, String outputFolder) throws WTException{
    	if(DEBUG){
	    	System.out.println("PDFMultiSpecGenerator - createPDFSpecifications");
	        System.out.println("params: " + params);
	        System.out.println("specIds: " + specIds);
    	}
        String specPagesStr = (String)params.get(SPEC_PAGES);
        Collection specPages = null;
        if(!FormatHelper.hasContent(specPagesStr)){
            specPages = new ArrayList();
        }
        else{
            specPages = MOAHelper.getMOACollection(specPagesStr);
        }
        
        if(DEBUG) {System.out.println("specPages: " + specPages);}
        
        Iterator ids = LCSQuery.getObjectsFromCollection(specIds).iterator();
                
        HBIPDFProductSpecificationGenerator2 ppsg = null;
        
        
        while(ids.hasNext()){
            try{
                FlexSpecification spec = (FlexSpecification)ids.next();
                if(DEBUG) {System.out.println("id: " + FormatHelper.getVersionId(spec));}
                
                ppsg = (HBIPDFProductSpecificationGenerator2) getGenerator(spec);
                
                setGeneratorParams(ppsg, spec, specPages, params, outputFolder);
                
                
                
            } catch(Throwable t){
                t.printStackTrace();
            }
        }
    }
    
    public String getSizes1(FlexSpecification spec, LCSProduct product) throws WTException{
        Collection resultVector = SizingQuery.findProductSizeCategoriesForProduct(product).getResults();
        
        if (!resultVector.isEmpty()){
            FlexObject firstElement = (FlexObject)resultVector.iterator().next();  // only concerned with the first sizing category
            return firstElement.getData("PRODUCTSIZECATEGORY.SIZEVALUES");
        }
        return null;
    }
    
    public String getSizes2(FlexSpecification spec, LCSProduct product) throws WTException{
        Collection resultVector = SizingQuery.findProductSizeCategoriesForProduct(product).getResults();
        
        if (!resultVector.isEmpty()){
            FlexObject firstElement = (FlexObject)resultVector.iterator().next();  // only concerned with the first sizing category
            return firstElement.getData("PRODUCTSIZECATEGORY.SIZE2VALUES");
        }
        
        return null;
    }
    
    public String getDestinations(FlexSpecification spec, LCSProduct product) throws WTException{
        String destIds = "";
        Collection results = ProductDestinationQuery.findProductDestinationsforProduct( product.getMaster()).getResults();
        String id = null;
        Iterator i = results.iterator();
        while(i.hasNext()){
            id = ((FlexObject)i.next()).getString("PRODUCTDESTINATION.IDA2A2");
            destIds = destIds + id + MOAHelper.DELIM;
        }
        
        return destIds;
    }
    
    public String getColorways(FlexSpecification spec, LCSProduct product) throws WTException{
    	return getColorways(spec, product, null);
    	
    }
    public String getColorways(FlexSpecification spec, LCSProduct product, LCSSeason season) throws WTException{
        String colorways = "";
        LCSSourcingConfig config = null;
        if(spec != null){
            config = (LCSSourcingConfig)VersionHelper.latestIterationOf((LCSSourcingConfigMaster)spec.getSpecSource());
        }
        Map skuTable = phq.findSKUsMap(product, config, season, true);
        Map reversed = new HashMap();
        Iterator keys = skuTable.keySet().iterator();
        String key = "";
        while(keys.hasNext()){
            key = (String) keys.next();
            reversed.put(skuTable.get(key), key);
        }
        
        Collection sortedSkus = SortHelper.sortStrings(reversed.keySet());
        Iterator skuItr = sortedSkus.iterator();
        String temp = null;
        LCSSKU sku = null;
        String numeric = null;
        while (skuItr.hasNext()) {
            
            temp = (String)skuItr.next();
            
            sku = (LCSSKU)LCSQuery.findObjectById((String)reversed.get(temp));
            
            numeric = FormatHelper.getNumericFromReference(sku.getMasterReference());
            
            colorways = colorways + numeric + MOAHelper.DELIM;
        }
        
        return colorways;
    }

    public String getSources(FlexSpecification spec, LCSProduct product) throws WTException{
        String sources = "";

        Map sourceTable = phq.findSourcingConfigsMap(product, null);
        Map reversed = new HashMap();
        Iterator keys = sourceTable.keySet().iterator();
        String key = null;
        while(keys.hasNext()){
            key = (String) keys.next();
            reversed.put(sourceTable.get(key), key);
        }
        
        Collection sortedSources = SortHelper.sortStrings(reversed.keySet());
        Iterator sourceItr = sortedSources.iterator();
        String temp = null;
        LCSSourcingConfig config = null;
        String numeric = null;
        while (sourceItr.hasNext()) {
            
            temp = (String)sourceItr.next();
            
            config = (LCSSourcingConfig)LCSQuery.findObjectById((String)reversed.get(temp));
            
            numeric = FormatHelper.getNumericFromReference(config.getMasterReference());
            
            sources = sources + numeric + MOAHelper.DELIM;
        }
        
        return sources;
    }
    
    public Collection getComponentsForSpec(FlexSpecification spec, LCSProduct product, Collection specPages)throws WTException{
        return getComponentsForSpec(spec, product, specPages, new HashMap());
    }

    public Collection<String> getComponentsForSpec(FlexSpecification spec, LCSProduct product, Collection specPages, Map params)throws WTException{
        Collection<String> compIds = new ArrayList<String>();
        LCSSeason season=null;
        String oid = (String) params.get("oid");

        if(FormatHelper.hasContent((String) params.get("seasonId") ) ){
        	season = (LCSSeason) LCSQuery.findObjectById((String) params.get("seasonId") );
        }else if(FormatHelper.hasContent(oid) && oid.indexOf("LCSSeason") > -1){
        	season = (LCSSeason) LCSQuery.findObjectById(oid );
        }
        System.out.println("product:::::::::::::::::"+product.getName());
        System.out.println("spec:::::::::::::::::"+spec.getName());
        Collection specComponents = FlexSpecQuery.getSpecToComponentObjectsData(spec);
        specComponents=filterOutImagePages(specComponents);
        specComponents=getOtherSpecComponents(product,season,spec,specComponents);
        specComponents = sortTPComponents(specComponents, new com.lcs.wc.client.ClientContext().getLocale());

        if(FormatHelper.parseBoolean((String)params.get("includeChildSpecs") )){
        	specComponents.addAll(FlexSpecQuery.getChildSpecComponents(spec, false));
        }
        
        Iterator<?> specCompItr = specComponents.iterator();
        while(specCompItr.hasNext()){
            FlexObject fo = (FlexObject)specCompItr.next();

            // We need more consistent on strings. Need a class with all constants to remove cases of "Images pagee" vs "IMAGE_PAGE" or "Measurements" vs "MEASUREMENT"
            String componentType = fo.getString("COMPONENT_TYPE_UNTRANSLATED");
            String componentType2;
        	if (componentType.equalsIgnoreCase("Images Page")) {
            	componentType2 = "IMAGES_PAGE:" + fo.getString("IMAGES_PAGE_TYPE");
        	}
        	else if (componentType.equalsIgnoreCase("Measurements")) {
            	componentType2 = "MEASUREMENT";
        	}
        	else{
	        	componentType2 = componentType;
	        }

        	Iterator<?> specTypeItr = specPages.iterator();
            for(specTypeItr = specPages.iterator(); specTypeItr.hasNext();){
            	String techPackComponentType = (String)specTypeItr.next();
            	if (componentType2.equalsIgnoreCase(techPackComponentType)){
            		compIds.add(componentType + CDELIM + fo.getString("OID"));
            	}
            }
        }
 
        return compIds;
    }
    
    private Collection filterOutImagePages(Collection specComponents) {
		// TODO Auto-generated method stub
    	Iterator specComponentsitr=specComponents.iterator();
    	//while(specComponentsitr.hasNext()){
    		for (Iterator<FlexObject> iterator = specComponents.iterator(); iterator.hasNext(); ) {
				FlexObject allSpecCompfo = (FlexObject)iterator.next();
				String allSpecCompType = allSpecCompfo.getString("COMPONENT_TYPE");
				if("Images Page".equals(allSpecCompType)|| "Pattern Images Page".equals(allSpecCompType)) {
				
					if(!("frontSketch".equals(allSpecCompfo.getString("IMAGES_PAGE_TYPE"))||"techniqueMaps".equals(allSpecCompfo.getString("IMAGES_PAGE_TYPE"))))
					{
						iterator.remove();	
					}
					
				

					}
				
				

				
    	}
		return specComponents;
	}

	private Collection getOtherSpecComponents(LCSProduct product, LCSSeason season, FlexSpecification currentSpec, Collection specComponents) throws WTException {
		Collection allSpecBOM = new ArrayList();

    	if(product!=null){
  		  String gpType = product.getFlexType().getFullName(true);
  		  
  			if ("Product\\BASIC CUT & SEW - GARMENT".equals(gpType)) {
  				SearchResults sr = FlexSpecQuery.findExistingSpecs(product, season, null);
  				if(sr != null && sr.getResultsFound() > 0)
  				{
  					Collection<FlexObject> specsFOColl = sr.getResults();
  					
                      //Added on 16th January 2020 for sorting spec as per Spec Name which is used in sorting multi spec BOM components
  					Collection<FlexObject> allSpecComponents = new ArrayList();
  					for(FlexObject specsFO:specsFOColl){
  						String specID = specsFO.getString("FLEXSPECIFICATION.IDA2A2");
  						FlexSpecification allSpec = (FlexSpecification)LCSQuery.findObjectById("com.lcs.wc.specification.FlexSpecification:"+specID);
  						String allSpecId =FormatHelper.getObjectId(allSpec);
  						
  						String currentSpecId =FormatHelper.getObjectId(currentSpec);
  						if(!allSpecId.equalsIgnoreCase(currentSpecId)){
  							allSpecComponents = FlexSpecQuery.getSpecToComponentObjectsData(allSpec, false);
  							
  							Iterator<?> allSpecComp = allSpecComponents.iterator();
  							while(allSpecComp.hasNext()){
  								FlexObject allSpecCompfo = (FlexObject)allSpecComp.next();
  								String allSpecCompType = allSpecCompfo.getString("COMPONENT_TYPE");
  								if("BOM".equalsIgnoreCase(allSpecCompType)){
  									String BOMCompOid = allSpecCompfo.getString("OID");
  										if(FormatHelper.hasContent(BOMCompOid)){
  										FlexBOMPart bomPart = (FlexBOMPart)LCSQuery.findObjectById(BOMCompOid);
  										String bomFT = bomPart.getFlexType().getFullName(true);
  										
  										if(FormatHelper.hasContent(MULTI_SPEC_BOM)){
  											String[] multiSpecBOMTypeArr=MULTI_SPEC_BOM.split(",");
  											
  											for(String multiSpecBOMType:multiSpecBOMTypeArr){
  												if(multiSpecBOMType.equalsIgnoreCase(bomFT)){
  													LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) allSpec.getSpecSource();
  													LCSSourcingConfig source = (LCSSourcingConfig)VersionHelper.latestIterationOf(sourceMaster);
  													String patternSpecInSource="";
  													if(source.getValue("hbiSpecificationPattern")!=null){
  														patternSpecInSource = (String)source.getValue("hbiSpecificationPattern");
  													}
  													String seasonInSourceStr = "";
  													if(source.getValue("hbiDvlpSeason")!=null){
  														LCSLifecycleManaged seasonInSource = (LCSLifecycleManaged)source.getValue("hbiDvlpSeason");
  														
  														seasonInSourceStr=seasonInSource.getName();
  													}
  													String colorwayInSource="";
  													if(source.getValue("hbiColorwayGroupingName")!=null){
  														colorwayInSource = (String)source.getValue("hbiColorwayGroupingName");
  													}
  													String colorwayGroupingInSource="";
  													if(source.getValue("hbiColorwayGrouping")!=null){
  														LCSLifecycleManaged colorwayGroupingBO = (LCSLifecycleManaged)source.getValue("hbiColorwayGrouping");
  														
  														colorwayGroupingInSource = (String)colorwayGroupingBO.getName();
  													}
  													
  													
  													String sourceFullName = patternSpecInSource+"-"+seasonInSourceStr+"-"+colorwayGroupingInSource+"-"+colorwayInSource;
  													allSpecCompfo.put("source", source);
  													allSpecCompfo.put("MBOM", sourceFullName);
  													allSpecCompfo.put("otherSpecName", allSpec.getName());
  													

  													
  													allSpecBOM.add(allSpecCompfo);
  												}	
  											}
  										}
  									}
  									
  								}
  						
  							}
  						}//To avoid adding components from same spec again
  						
  					}
  				}
  				 //System.out.println("allSpecBOMFO "+allSpecBOM);
  				specComponents.addAll(allSpecBOM);
  			
  				// HBI : For putting other spec BOMS in chooser page - END
  				/* Changed by UST on 2/20/2019 - START
  				** For Pulling of Pattern Product Components automatically into output */
  				if(VIEW_PATTERN_SPEC_IN_CHOOSER) {
  						Collection<?> parents = FlexSpecQuery.findSpecToSpecLinks(currentSpec, null);
  						try {
  					Collection<?> patternSpecs;
  					Collection<FlexObject> patternSpecsNew=new ArrayList<FlexObject>();
  					
				
						patternSpecs = HBISpecificationPDFGenUtil.getParentSpecComponentsForChooser(HBISpecificationPDFGenUtil.getParentSpecId(currentSpec));
						
  					if(patternSpecs != null && specComponents != null) {
  						patternSpecs = HBISpecificationPDFGenUtil.cleanUpDuplicateComponents((ArrayList<FlexObject>) patternSpecs,(ArrayList<FlexObject>) specComponents);

  						Iterator patternSpecsitr=patternSpecs.iterator();
				    	while(patternSpecsitr.hasNext()){
								FlexObject allSpecCompfo = (FlexObject)patternSpecsitr.next();
								String allSpecCompType = allSpecCompfo.getString("COMPONENT_TYPE");
								if( "Pattern Images Page".equals(allSpecCompType)) {
								
										if(!("frontSketch".equals(allSpecCompfo.getString("IMAGES_PAGE_TYPE"))||"patternInformation".equals(allSpecCompfo.getString("IMAGES_PAGE_TYPE")))) {
											patternSpecsNew.add(allSpecCompfo);

										}

									
								}
								else {
									patternSpecsNew.add(allSpecCompfo);
								}
								

								
				    	}
  						
  						specComponents.addAll((ArrayList)patternSpecsNew);
  					}	
  						} catch (WTException e) {
  							// TODO Auto-generated catch block
  							e.printStackTrace();
  						}
  				}
  				/* For Pulling of Pattern Product Components automatically into output  - END */
  				//String masterId = appContext.getProductMasterId();
  				String masterId=FormatHelper.getObjectId(product.getMaster());
                   ArrayList<FlexObject> labelSpecComponents = HBILabelSpecGetter.getlabelProductComponents(masterId); 
                   specComponents.addAll((ArrayList)labelSpecComponents);
  				
  			}//GARMENT
  		}		return specComponents;
	}
    public static Collection sortTPComponents(Collection components, java.util.Locale locale) {
		Collection<FlexObject> sortedComponents = new LinkedList<FlexObject>();
		
		try {
		FlexObject fo = null;
		List<String> compKeyList = new ArrayList<String>();
		Iterator<?> componentsIter = components.iterator();
		Collection frontImageCollection = new ArrayList();
		Collection imageMeasurementCol = new ArrayList();
		Collection imageConstructionCol = new ArrayList();
		Collection imagePlacementCol = new ArrayList();
		Collection imageMarkerCol = new ArrayList();

		Collection imageCollection = new ArrayList();
		Collection imageDevelDetailsCol = new ArrayList();
		Collection imageFitEvalCol = new ArrayList();
		Collection imageCorrectionImageCol = new ArrayList();
		Collection imagePatternInfoCol = new ArrayList();
		
		Collection routingBOMCollection = new ArrayList();
		Collection patternCollection = new ArrayList();
		Collection mColorwayBOMsCollection = new ArrayList();
		Collection mAttributionBOMsCollection = new ArrayList();
		Collection labelBOMsCollection = new ArrayList();
		Collection colorwayBOMsCollection = new ArrayList();
		Collection attributionBOMsCollection = new ArrayList();
		Collection otherBOMsCollection = new ArrayList();
		
		Collection constructionCol = new ArrayList();
		Collection measurementCol = new ArrayList();
		Collection otherCollection = new ArrayList();
		
		Collection pMeasurementCol = new ArrayList();
		Collection pConstructionCol = new ArrayList();
		Collection pImageMeasurementCol = new ArrayList();
		Collection pImageConstructionCol = new ArrayList();
		Collection pImagePlacementCol = new ArrayList();
		Collection pImageMarkerCol = new ArrayList();
		Collection pImageCol = new ArrayList();		
		Collection pFrontImageCol = new ArrayList();
		Collection pImageDevelDetailsCol = new ArrayList();
		Collection pImageFitEvalCol = new ArrayList();
		Collection pImageCorrectionImageCol = new ArrayList();
		Collection pImagePatternInfoCol = new ArrayList();
		
		while(componentsIter.hasNext()){
			fo = (FlexObject) componentsIter.next();
			
			if(fo.getData("COMPONENT_TYPE").contains("Pattern")){
				
				if("Pattern Measurements".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
					pMeasurementCol.add(fo);
				}else if("Pattern Construction".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
					pConstructionCol.add(fo);
				}else if("Pattern Images Page".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
					if("measurements".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImageMeasurementCol.add(fo);
					}else if("construction".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImageConstructionCol.add(fo);
					}else if("markerLayout".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImageMarkerCol.add(fo);
					}else if("placementDetails".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImagePlacementCol.add(fo);
					}else if("frontSketch".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pFrontImageCol.add(fo);
					}else if("developmentDetails".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImageDevelDetailsCol.add(fo);
					}else if("fitEvaluation".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImageFitEvalCol.add(fo);
					}else if("correctionImage".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImageCorrectionImageCol.add(fo);
					}else if("patternInformation".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
						pImagePatternInfoCol.add(fo);						
					}else {
						pImageCol.add(fo);
					}
				}else{
					patternCollection.add(fo);
				}
				
			}else if("Images Page".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
				if("frontSketch".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					frontImageCollection.add(fo);
				}else if("measurements".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imageMeasurementCol.add(fo);
				}else if("construction".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imageConstructionCol.add(fo);
				}else if("markerLayout".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imageMarkerCol.add(fo);
				}else if("placementDetails".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imagePlacementCol.add(fo);
				}
				else if("techniqueMaps".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					pImagePlacementCol.add(fo);
				}
				else if("developmentDetails".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imageDevelDetailsCol.add(fo);
				}else if("fitEvaluation".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imageFitEvalCol.add(fo);
				}else if("correctionImage".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imageCorrectionImageCol.add(fo);
				}else if("patternInformation".equalsIgnoreCase(fo.getData("IMAGES_PAGE_TYPE"))){
					imagePatternInfoCol.add(fo);					
				}else{
					imageCollection.add(fo);
				}
			}else if("BOM".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
				String BOMCompOid = fo.getString("OID");
				
				if(FormatHelper.hasContent(BOMCompOid)){
					FlexBOMPart bomPart = (FlexBOMPart)LCSQuery.findObjectById(BOMCompOid);
					String bomFT = bomPart.getFlexType().getFullName(true);
					if(bomFT.contains("Routing")){
						routingBOMCollection.add(fo);
					}else if(bomFT.contains("Label")){
						labelBOMsCollection.add(fo);
					}else if(bomFT.contains("Colorway") ){
						if(fo.containsKey("MBOM")){
							mColorwayBOMsCollection.add(fo);
						}else{
							colorwayBOMsCollection.add(fo);
						}
						
					}else if(bomFT.contains("Attribution") ){
						if(fo.containsKey("MBOM")){
							mAttributionBOMsCollection.add(fo);
						}else{
							attributionBOMsCollection.add(fo);
						}
					}else{
						otherBOMsCollection.add(fo);
					}
				}
				
	
			}else if("measurements".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
				measurementCol.add(fo);
			}else if("construction".equalsIgnoreCase(fo.getData("COMPONENT_TYPE"))){
				constructionCol.add(fo);
			}else{
				otherCollection.add(fo);
			}

		}//While loop for FO
		
		/* Sort Components based on Component Names Alpha Numerically -- START*/
		frontImageCollection = SortHelper.sortFlexObjects(frontImageCollection,"NAME:ASC");
		pFrontImageCol = SortHelper.sortFlexObjects(pFrontImageCol,"NAME:ASC");
		routingBOMCollection = SortHelper.sortFlexObjects(routingBOMCollection,"NAME:ASC");
		pMeasurementCol = SortHelper.sortFlexObjects(pMeasurementCol,"NAME:ASC");
		pImageMeasurementCol = SortHelper.sortFlexObjects(pImageMeasurementCol,"NAME:ASC");
		measurementCol = SortHelper.sortFlexObjects(measurementCol,"NAME:ASC");
		constructionCol = SortHelper.sortFlexObjects(constructionCol,"NAME:ASC");
		imageMeasurementCol = SortHelper.sortFlexObjects(imageMeasurementCol,"NAME:ASC");
		pConstructionCol = SortHelper.sortFlexObjects(pConstructionCol,"NAME:ASC");
		pImageConstructionCol = SortHelper.sortFlexObjects(pImageConstructionCol,"NAME:ASC");
		imageConstructionCol = SortHelper.sortFlexObjects(imageConstructionCol,"NAME:ASC");
		pImagePlacementCol = SortHelper.sortFlexObjects(pImagePlacementCol,"NAME:ASC");
		imagePlacementCol = SortHelper.sortFlexObjects(imagePlacementCol,"NAME:ASC");
		pImageMarkerCol = SortHelper.sortFlexObjects(pImageMarkerCol,"NAME:ASC");
		imageMarkerCol = SortHelper.sortFlexObjects(imageMarkerCol,"NAME:ASC");
		pImageCol = SortHelper.sortFlexObjects(pImageCol,"NAME:ASC");
		imageCollection = SortHelper.sortFlexObjects(imageCollection,"NAME:ASC");
		patternCollection = SortHelper.sortFlexObjects(patternCollection,"NAME:ASC");
		colorwayBOMsCollection = SortHelper.sortFlexObjects(colorwayBOMsCollection,"NAME:ASC");
		//--Multi Spec Colorway BOMs to be sorted in descending order based on Spec Names 
		// User story HBI - 61593
		mColorwayBOMsCollection = SortHelper.sortFlexObjects(mColorwayBOMsCollection,"OTHERSPECNAME:DESC");
		attributionBOMsCollection = SortHelper.sortFlexObjects(attributionBOMsCollection,"NAME:ASC");
		mAttributionBOMsCollection = SortHelper.sortFlexObjects(mAttributionBOMsCollection,"NAME:ASC");
		otherBOMsCollection = SortHelper.sortFlexObjects(otherBOMsCollection,"NAME:ASC");
		otherCollection = SortHelper.sortFlexObjects(otherCollection,"NAME:ASC");
		labelBOMsCollection = SortHelper.sortFlexObjects(labelBOMsCollection,"NAME:ASC");
		imageDevelDetailsCol = SortHelper.sortFlexObjects(imageDevelDetailsCol,"NAME:ASC");
		imageFitEvalCol = SortHelper.sortFlexObjects(imageFitEvalCol,"NAME:ASC");
		imageCorrectionImageCol = SortHelper.sortFlexObjects(imageCorrectionImageCol,"NAME:ASC");
		imagePatternInfoCol = SortHelper.sortFlexObjects(imagePatternInfoCol,"NAME:ASC");
		pImageDevelDetailsCol = SortHelper.sortFlexObjects(pImageDevelDetailsCol,"NAME:ASC");
		pImageFitEvalCol = SortHelper.sortFlexObjects(pImageFitEvalCol,"NAME:ASC");
		pImageCorrectionImageCol = SortHelper.sortFlexObjects(pImageCorrectionImageCol,"NAME:ASC");
		pImagePatternInfoCol = SortHelper.sortFlexObjects(pImagePatternInfoCol,"NAME:ASC");		
		
		/* Sort Components based on Component Names Alpha Numerically -- END*/
		
		sortedComponents.addAll(frontImageCollection);
		sortedComponents.addAll(pFrontImageCol);
		
		sortedComponents.addAll(routingBOMCollection);
		
		sortedComponents.addAll(measurementCol);
		sortedComponents.addAll(imageMeasurementCol);
		sortedComponents.addAll(pMeasurementCol);
		sortedComponents.addAll(pImageMeasurementCol);
		
		sortedComponents.addAll(constructionCol);
		sortedComponents.addAll(imageConstructionCol);
		sortedComponents.addAll(pConstructionCol);
		sortedComponents.addAll(pImageConstructionCol);
		
		
		sortedComponents.addAll(imagePlacementCol);
		sortedComponents.addAll(pImagePlacementCol);
		
		sortedComponents.addAll(imageMarkerCol);
		sortedComponents.addAll(pImageMarkerCol);
		
		sortedComponents.addAll(imageDevelDetailsCol);
		sortedComponents.addAll(pImageDevelDetailsCol);
		sortedComponents.addAll(imageFitEvalCol);
		sortedComponents.addAll(pImageFitEvalCol);		
		sortedComponents.addAll(imageCorrectionImageCol);
		sortedComponents.addAll(pImageCorrectionImageCol);
		sortedComponents.addAll(imagePatternInfoCol);
		sortedComponents.addAll(pImagePatternInfoCol);		

		sortedComponents.addAll(imageCollection);		
		sortedComponents.addAll(pImageCol);


		sortedComponents.addAll(patternCollection);
		
		
		sortedComponents.addAll(colorwayBOMsCollection);
		sortedComponents.addAll(mColorwayBOMsCollection);
		sortedComponents.addAll(attributionBOMsCollection);
		sortedComponents.addAll(mAttributionBOMsCollection);
		sortedComponents.addAll(otherBOMsCollection);
		
		sortedComponents.addAll(otherCollection);
		sortedComponents.addAll(labelBOMsCollection);
		
		
	    } catch(Exception e){
		   e.printStackTrace();
		   sortedComponents.addAll(components);
	    }
		
		return sortedComponents;		
	}
	public Collection getBOMParts(LCSProduct product, FlexSpecification spec) throws WTException{
        Collection bpIds = new ArrayList();
        Collection bomParts = (new LCSFlexBOMQuery()).findBOMPartsForOwner(product, null, null, spec);
        if(bomParts != null && bomParts.size() > 0){
            Iterator i = bomParts.iterator();
            
            while(i.hasNext()){
                bpIds.add("BOM" + CDELIM + FormatHelper.getVersionId((FlexBOMPart)i.next()));
            }
        }
        return bpIds;
    }
    
    public Collection getConstructionInfo(LCSProduct product, FlexSpecification spec) throws WTException{
        Collection results = LCSConstructionQuery.findConstructionForProduct(product, spec).getResults();
        Collection ms = new ArrayList();
        Iterator i = results.iterator();
        FlexObject obj = null;
        while(i.hasNext()){
            obj = (FlexObject)i.next();
            ms.add("Construction" + CDELIM + "VR:com.lcs.wc.construction.LCSConstructionInfo:" + (String)obj.get("LCSCONSTRUCTIONINFO.BRANCHIDITERATIONINFO"));
        }
        
        return ms;
    }
    
    public Collection getMeasurements(LCSProduct product, FlexSpecification spec) throws WTException{
        Collection results = LCSMeasurementsQuery.findMeasurmentsForProduct(product, spec).getResults();
        Collection ms = new ArrayList();
        Iterator i = results.iterator();
        FlexObject obj = null;
        while(i.hasNext()){
            obj = (FlexObject)i.next();
            ms.add("Measurements" + CDELIM + "VR:com.lcs.wc.measurements.LCSMeasurements:" + (String)obj.get("LCSMEASUREMENTS.BRANCHIDITERATIONINFO"));
        }
        
        return ms;
    }
    
    private Collection getImagePages(LCSProduct product,FlexSpecification spec, String pageType) throws WTException{
        try{

        	if(DEBUG){
    	        System.out.println("----------------------------------------------");
    	        System.out.println("PDFMultiSpecGenerator - getImagePages");
    	        System.out.println("product: " + product);
    	        System.out.println("spec: " + spec);
    	        System.out.println("pageType: " + pageType);
    	        System.out.println("----------------------------------------------");
        	}
        	
        	String productId = FormatHelper.getObjectId(product.getMaster());
            String specId = null;
            if(spec != null){
                specId = FormatHelper.getObjectId(spec.getMaster());            
            }
            
            FlexTypeQueryStatement statement = (new LCSProductQuery()).getProductImagesQuery(productId, specId);
            if(FormatHelper.hasContent(pageType)){
                statement.appendAndIfNeeded();
                statement.appendFlexCriteria("pageType", pageType, Criteria.EQUALS);
            }
            //statement.appendSortBy(new QueryColumn(LCSDocument.class, "att1"));
            statement.appendSortBy(new QueryColumn(LCSDocument.class, "ptc_str_1typeInfoLCSDocument"));
            
            Collection results = LCSQuery.runDirectQuery(statement).getResults();

        	if(DEBUG){
    	        System.out.println("----------------------------------------------");
    	        System.out.println("statement: " + statement);
    	        System.out.println("----------------------------------------------");
    	        System.out.println("results: " + results);
    	        System.out.println("----------------------------------------------");
        	}
            
            
            Iterator i = results.iterator();
            FlexObject obj = null;
            Collection ms = new ArrayList();
            while(i.hasNext()){
                obj = (FlexObject)i.next();
                ms.add("Images Page" + CDELIM + "VR:com.lcs.wc.document.LCSDocument:" + obj.get("LCSDOCUMENT.BRANCHIDITERATIONINFO"));
            }
            
            return ms;
        }catch(Exception e){
            e.printStackTrace();
            throw new WTException(e);
        }
    }
    public static void addComponentToMaps(Map<Object,Object> cmap, Map<Object,String> nMap, FlexObject component){
		String ctype = component.getString("COMPONENT_TYPE_UNTRANSLATED");
		nMap.put(component.get("OID"), component.getString("NAME"));
		
		Collection<String> objs = (Collection<String>)cmap.get(ctype);
		
		if(objs == null){
			objs = new ArrayList<String>();
		}
		
		objs.add(component.getString("OID"));
		cmap.put(ctype, objs);
	}
    
}

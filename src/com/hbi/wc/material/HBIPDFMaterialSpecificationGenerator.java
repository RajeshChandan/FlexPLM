package com.hbi.wc.material;

import com.lcs.wc.util.*;
import com.lcs.wc.material.LCSMaterial;
//import wt.part.WTPartMaster;
import com.lcs.wc.client.*;
import java.util.*;
import wt.util.*;
import java.io.*;
import java.lang.reflect.Constructor;
//import com.lcs.wc.specification.FlexSpecification;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import com.lowagie.text.*;
import com.lcs.wc.client.web.pdf.PDFHeader;
import com.lcs.wc.document.FileRenamer;
import com.lowagie.text.pdf.*;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf .*;
import com.lcs.wc.material.LCSMaterialMaster;
//import java.net.MalformedURLException;
//import java.net.URLEncoder;
//import java.net.URL;

public class HBIPDFMaterialSpecificationGenerator {


	private static final String CLASSNAME = HBIPDFMaterialSpecificationGenerator.class.getName();
    public static final String defaultCharsetEncoding = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset","UTF-8");
	static final String CHARSET_ENCODING = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset", "UTF-8");
    private static String authgwUrl = "";
	protected static final String PDF_OVERRIDE_CLASS = LCSProperties.get("com.hbi.wc.material.ProductPDFSpecificationGenerationClass");
    public static boolean DEBUG = LCSProperties.getBoolean("com.hbi.wc.material.HBIPDFMaterialSpecificationGenerator.verbose");
	private static final boolean USE_RELATIVE_URL = LCSProperties.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator2.useRelativeURL");
    public float sideMargins = (new Float(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "1.0"))).floatValue();
    public float bottomMargin = (new Float(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "5.0"))).floatValue();
	private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.verboseLevel", "1"));
	private static final String HEADER_TEXT = LCSProperties.get("com.hbi.wc.material.specReport.HeaderText");

	static{
		try{
		java.net.URL authgwURL = GatewayServletHelper.buildAuthenticatedURL(new URLFactory());
		authgwUrl =(USE_RELATIVE_URL)?authgwURL.getPath():authgwURL.toString();
	}
            catch(Exception e){
            System.out.println("Error initializing cache for ExcelGeneratorHelper");
            e.printStackTrace();
        }
    }

   private Map<String, Object> params = new HashMap<String, Object>();
   //private HashMap<String,Object> matHeaderFooterClasses = new HashMap<String,Object>(3);
   private String materialName = "";
   public static String MATERIAL_ID = "MATERIAL_ID";
   public static String MATERIAL_NUM_ID = "MATERIAL_NUM_ID";
   public static final String MATERIAL = "Material";
   public static String MATERIAL_MASTER_ID = "MATERIAL_MASTER_ID";
   public static String PAGE_SIZE = "PAGE_SIZE";
   
   private String fileOutName = null;
   private String outputFile = null;
   //private String zipFileName = null
   //private LCSMaterial material = null;
   private LCSMaterialSupplier matSupplier = null;  
   
   String propertyFile = null;
   public static ClassLoadUtil clu = null;
   public Rectangle ps = PageSize.LETTER;
  // public static String PAGE_OPTIONS = "PAGE_OPTIONS";
   HBIPDFMaterialSpecPageHeaderGenerator hmsphg = new HBIPDFMaterialSpecPageHeaderGenerator();
   private PDFHeader pdfHeader = null;

   public Collection<String> pages ;//= new ArrayList();
   public String fontClass = "TABLESECTIONHEADER";
   public static String HEADER_HEIGHT = "HEADER_HEIGHT";
   public float cellHeight = 15.0f;
   float hhFudge = 20.0f + cellHeight;
   public String outputLocation = "";
   public String outputURL = "";
   public String returnURL = "";
   private PdfWriter writer = null;
   public static String LANDSCAPE = "LANDSCAPE";
   public static String PORTRAIT = "PORTRAIT";
   public boolean landscape = false;
   public String orientation = "";
   public static String REPORT_NAME = "REPORT_NAME";

   public static String MATERIALSUPPLIER_ID = "MATERIALSUPPLIER_ID";
   public static String SUPPLIER_MASTER_ID = "SUPPLIER_MASTER_ID";
   public static String SUPPLIER_MASTER_NUM_ID = "SUPPLIER_MASTER_NUM_ID";
   public static String MATERIALSUPPLIER_OBJECT = "MATERIALSUPPLIER_OBJECT";
   //added for ticket 141702-15
   public static String IS_GENERIC = "IS_GENERIC";
   
   private String isGeneric = "false";
   //ended for ticket 141702-15
   PDFGeneratorHelper pgh = new PDFGeneratorHelper();
   
   protected HBIPDFMaterialSpecificationGenerator(){
    	
   }

   /** Creates a new instance of HBIPDFMaterialSpecificationGenerator
     * @param config The specificaiton for which to generate a PDF Specification
     * @throws WTException
     */
	 // added for ticket 141702-15
	public HBIPDFMaterialSpecificationGenerator(LCSMaterialSupplier matSupplier, String isGeneric) throws WTException{
	   this.isGeneric = isGeneric;
	   this.matSupplier = matSupplier;
	   init();
   }
   //ended for ticket 141702-15
   public HBIPDFMaterialSpecificationGenerator(LCSMaterialSupplier matSupplier) throws WTException{
	   this.matSupplier = matSupplier;
	   init();
   }

   public HBIPDFMaterialSpecificationGenerator(String propertyFile) throws WTException{
        this.propertyFile = propertyFile;
        init();
   }

   public static HBIPDFMaterialSpecificationGenerator getOverrideInstance(Object[] arguments) throws WTException {
   		HBIPDFMaterialSpecificationGenerator hpmsg = null;
    	try{
	    	Class pdfGenClass = HBIPDFMaterialSpecificationGenerator.class;
	    	if(FormatHelper.hasContent(PDF_OVERRIDE_CLASS) && !"com.hbi.wc.material.HBIPDFMaterialSpecificationGenerator".equals(PDF_OVERRIDE_CLASS)){
	    		pdfGenClass = Class.forName(PDF_OVERRIDE_CLASS);
	    	}
	    	
	    	Class[] argTypes = new Class[arguments.length];
	    	
	    	for(int i = 0; i < arguments.length;i++){
	    		argTypes[i] = arguments[i].getClass();
	    	}
	    	
	    	Constructor pdfGenConstructor = pdfGenClass.getConstructor(argTypes);
	    	
	    	hpmsg = (HBIPDFMaterialSpecificationGenerator) pdfGenConstructor.newInstance(arguments);
	    	
		}catch (Exception e) {
			e.printStackTrace();
			if( e instanceof WTException){
				throw (WTException)e;
			}else{
				throw new WTException(e);
			}
		}
    
		return hpmsg;
    }


	private void init() throws WTException{ 
		try{
			if(FormatHelper.hasContent(this.propertyFile)){
                clu = new ClassLoadUtil(this.propertyFile);
            }
            else{
                clu = new ClassLoadUtil(FileLocation.productSpecProperties2);
            } 
			String materialSupplierId = FormatHelper.getVersionId(this.matSupplier);    
        //    String matMasterId = FormatHelper.getObjectId((WTPartMaster)this.matSupplier.getMaterialMaster());
			String matMasterId = FormatHelper.getObjectId((LCSMaterialMaster)this.matSupplier.getMaterialMaster());
		    LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById(matMasterId);
			LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
			String materialNumericObjId = FormatHelper.getNumericObjectIdFromObject(material) ; // IDA2A2
			String supplierMasterId = FormatHelper.getObjectId((LCSSupplierMaster)this.matSupplier.getSupplierMaster()); //OR:com.lcs.wc.supplier.LCSSupplierMaster:98215
			String supplierMasterNumericObjId = FormatHelper.getNumericObjectIdFromObject((LCSSupplierMaster)this.matSupplier.getSupplierMaster());
			materialName = materialMaster.getName();
            String userName = ClientContext.getContext().getUser().getName();
            fileOutName = "MaterialSpec_" + materialName + "_" + userName;// + ".pdf";
            fileOutName = FormatHelper.formatRemoveProblemFileNameChars(fileOutName);
            fileOutName = java.net.URLEncoder.encode(fileOutName, defaultCharsetEncoding);
			fileOutName = fileOutName + ".pdf";
			params.put(MATERIAL_NUM_ID, materialNumericObjId);
			params.put(MATERIALSUPPLIER_ID, materialSupplierId);
            params.put(MATERIAL_MASTER_ID, matMasterId);
			params.put(SUPPLIER_MASTER_ID, supplierMasterId);
			params.put(SUPPLIER_MASTER_NUM_ID, supplierMasterNumericObjId);
            params.put(MATERIALSUPPLIER_OBJECT, this.matSupplier);
            params.put(PAGE_SIZE, this.ps);
			params.put(IS_GENERIC, this.isGeneric);
						
            // for header
			//System.out.println("inside generator " + params);
			Object header = clu.getClass("MaterialSpecHeader");
            if(header != null){
                pdfHeader = (PDFHeader)((PDFHeader)header).getPDFHeader(params);
            }
		} catch(Exception e){
          e.printStackTrace();
          throw new WTException(e);
        }
	}
	
	
	

	/** Generates the PDF document for the Spec
     *
     * @throws WTException
     * @return the url to the generated document
     */
    public String generateSpec() throws WTException{
        try{
       	    Locale clientLocale = (Locale)this.params.get("clientLocale");
			//hmsphg.fontClass = this.fontClass;
            Document doc = null;
		    Map<String,Object> tParams = null;
            int x = 0;
			boolean first = true;
            String item = null;
            String key = null;
			// *****************  core place  *******************************************************************************

			key = "Material";
			tParams = new HashMap<String,Object>(params.size()+ 3);
            if(clientLocale!=null)tParams.put("clientLocale",clientLocale);
            tParams.putAll(params);

			if(clu.getParams(key) != null){
                  tParams.putAll(clu.getParams(key));
            }
			//System.out.println("inside generate Spec tParams " + tParams);
			Object obj = clu.getClass(key); 
			PDFContentCollection content = (PDFContentCollection)obj;
			Collection contents = new ArrayList();
			boolean cFirst = true;
            if(first){
               doc = prepareDocument((String)tParams.get("orientation"));
			   //doc = prepareDocument(orientation);
               doc.open();
               first = false;
            }
			else{
				setOrientation((String)tParams.get("orientation"), doc);
				//setOrientation(orientation);
				doc.newPage();
            }
		    try{

				contents = content.getPDFContentCollection(tParams, doc);

		    }catch(Exception e){
				e.printStackTrace();
		    }
            Iterator ci = contents.iterator();
			while(ci.hasNext()){
			    if(!cFirst){
                     doc.newPage();
                }
                else{
                  cFirst = false;
                }
				hmsphg.headerText =(new StringBuilder()).append(HEADER_TEXT).append(" ").append(materialName).toString();
				Element e = (Element)ci.next();
				PdfPTable elementTable = new PdfPTable(1);
				elementTable.setTotalWidth(doc.right() - doc.left());  
				elementTable.setLockedWidth(true);
				if(pdfHeader != null)
				{
					PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
                    titleCell.setBorder(0);
                    elementTable.addCell(titleCell);
					PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
					spacerCell.setFixedHeight(3F); 
					spacerCell.setBorder(0);
					elementTable.addCell(spacerCell);
                  	 //doc.add(elementTable);
                }
				PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                contentCell.setBorder(0);
                elementTable.addCell(contentCell);
                elementTable.setSplitLate(false);
                doc.add(elementTable); 
				x++;
            }
			if(x < 1){
                if(doc == null || !(doc.isOpen())){
                    doc = prepareDocument((String)params.get("orientation"));
                    doc.open();
                }
                doc.add(pgh.multiFontPara(WTMessage.getLocalizedMessage( RB.PRODUCT, "noPagesGeneratedForSpec_MSG", RB.objA ) ));
			 }
             doc.close();

             //outputURL = getURL(outputFile);
			 //return outputURL;
			 return outputFile;

		}catch(Exception e){
			e.printStackTrace();
            throw new WTException(e);
		}
     }

	 private Document prepareDocument(String orientation) throws WTException{
        try{
			 String outDirStr = null;
             if(FormatHelper.hasContent(this.outputLocation)){
                outDirStr = this.outputLocation;
             }else {
                outDirStr = FileLocation.PDFDownloadLocationFiles;
             }
             File outFile = new File(outDirStr, fileOutName);
             outFile = FileRenamer.rename(outFile);
             fileOutName = outFile.getName();
			 //System.out.println("fileOutName"+fileOutName);
             outputFile = outFile.getAbsolutePath();
			 //System.out.println("outputFile"+outputFile);
			 FileOutputStream outStream = new FileOutputStream(outFile);
             Document pdfDoc = new Document();
			 //pdfDoc.setMargins(20F, 20F, 45F, 50F);
			 pdfDoc.setMargins(20F, 20F, 50F, 50F);
             //pdfDoc.setMargins(sideMargins, sideMargins, cellHeight, bottomMargin);
			 //Document pdfDoc = new Document(20F, 20F, 45F, 50F);
             writer = PdfWriter.getInstance(pdfDoc, outStream);
			 writer.setPageEvent(hmsphg);
             setOrientation(orientation, pdfDoc);
             fileOutName = java.net.URLEncoder.encode(fileOutName, defaultCharsetEncoding);
			 //System.out.println("fileOutName1"+fileOutName);
			 if(FormatHelper.hasContent(outputURL)){
                returnURL =  outputURL + fileOutName;
				System.out.println("returnURL"+returnURL);
             }
			 else{
              String filepath = FormatHelper.formatOSFolderLocation(FileLocation.PDFDownloadLocationFiles);
	          returnURL = filepath + fileOutName;
			  //System.out.println("returnURLelse"+returnURL);
             }

			 return pdfDoc;
		 }catch(Exception e){
            e.printStackTrace();
			throw new WTException(e);
		 }
	 }

	 public void setPages(Collection pages){
        this.pages = pages;
     }
	 
	 private void setOrientation(String orientation, Document doc){
        debug("orientation: " + orientation);
	    debug("ps: " + this.ps);
        if(FormatHelper.hasContent(orientation)){
            if(LANDSCAPE.equalsIgnoreCase(orientation)){
                doc.setPageSize(this.ps.rotate());
            }
            else{
                doc.setPageSize(this.ps);
            }
        }
        else{
            if(this.landscape){
                doc.setPageSize(this.ps.rotate());
            }
            else{
                doc.setPageSize(this.ps);
            }
        }
        
        debug("page size for render: " + doc.getPageSize());
    }

	/** sets whether or not the generated document should use landscape orientation
     * @param landscape
     */
	public void setLandscape(boolean landscape){
        this.landscape = landscape;
    }

	public void setOrientation(String orientation){
        this.orientation = orientation;
    }


  /* public String getURL(String pdfFile) throws MalformedURLException, UnsupportedEncodingException, WTException  {

    File file = new File(pdfFile);
    if (DEBUG) System.out.println("\t zip file " + pdfFile);
    String fileName = file.getName();
    fileName = URLEncoder.encode(fileName, CHARSET_ENCODING);
    fileName = fileName.replaceAll("\\+", "%20");
    URL authgwURL = GatewayServletHelper.buildAuthenticatedURL(new URLFactory());
    String authgwUrl = (USE_RELATIVE_URL) ? authgwURL.getPath() : authgwURL.toString();
    String baseURL = authgwUrl + "com.lcs.wc.util.TempContentHttp/viewTempFile/";

    pdfFile = file.getParent() + File.separator + fileName;
    String filepath = FormatHelper.formatFilePathForUrlUse(pdfFile);
    filepath = "&filepath=" + filepath;

    baseURL = baseURL + fileName + "?u8=1" + filepath;
    if (DEBUG) System.out.println("\t URL\n" + baseURL);
    return baseURL;

  } */

   public String getURL() {
        return returnURL;
   }


  /** Gets the path to the PDF file once it is generated
     * @return the path to the PDF file
     */
    public String getFilePath(){
        return this.outputURL;
    }

	/*public static void main(String[] args){

		LCSMaterialSupplier materialSupp = null;
		Collection testpages = new ArrayList();
		testpages.add("Material");
		String MAT_SUPP_ID = "VR:com.lcs.wc.material.LCSMaterialSupplier:156533";
		//	"VR:com.lcs.wc.material.LCSMaterialSupplier:114584";
		//VR:com.lcs.wc.material.LCSMaterialSupplier:114584
		 //VR:com.lcs.wc.material.LCSMaterialSupplier:156533
		try{
			materialSupp = (LCSMaterialSupplier)LCSQuery.findObjectById(MAT_SUPP_ID);
			HBIPDFMaterialSpecificationGenerator hmsg = new HBIPDFMaterialSpecificationGenerator(materialSupp);
			hmsg.setPages(testpages);
			hmsg.generateSpec();

		}catch (Exception e) {
			//throw new WTException(e);
		}
		


	}*/

	public static void debug(String msg){debug(msg, 1); }
    public static void debug(int i, String msg){debug(msg, i); }
    public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) System.out.println(msg);
    }


}// end class
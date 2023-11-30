package com.hbi.stg.extractors;

import com.hbi.etl.dao.HbiEtlTracker;
import com.hbi.etl.dao.HbiMaterial;
import com.hbi.etl.dao.HbiMaterialSupplier;
import com.hbi.etl.dao.HbiMoaObject;
import com.hbi.etl.dao.HbiSupplier;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.mq.WINJAVAMQ;
import com.hbi.etl.util.NewHibernateUtil;
import com.hbi.lawson.export.CreateIC11File;
import com.hbi.lawson.export.CreatePO25File;
import com.hbi.stg.extractors.util.HBIConnectionUtil;
import com.hbi.stg.extractors.util.HbiExtractorUtil;
import com.ibm.mq.MQException;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class STGLawsonExport {

    static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger logger = PLMETLLogger.createInstance(STGLawsonExport.class, logLevel, true);
    HbiExtractorUtil util = new HbiExtractorUtil();
    static String IC11fileLocation = LCSProperties.get("com.hbi.stg.extractors.STGLawsonExport.IC11FileLocation", "C:\\lawsondata\\LAWITMMST.");
    static String PO25fileLocation = LCSProperties.get("com.hbi.stg.extractors.STGLawsonExport.PO25FileLocation", "C:\\lawsondata\\LAWVENAGR.");
    static String ERRFILE = LCSProperties.get("com.hbi.stg.extractors.STGLawsonExport.errorFile", "C:\\lawsondata\\Rejected.");
    File IC11file = null;
    File PO25file = null;
    PrintWriter errfile = null;
    String lineseparator = System.getProperty("line.separator");
    StringBuilder po25builder = new StringBuilder();
    StringBuilder ic11builder = new StringBuilder();
    private boolean createDataFile = LCSProperties.getBoolean("com.hbi.stg.extractors.STGLawsonExport.createDataFile");

    public static void main(String[] args) {

        STGLawsonExport va = new STGLawsonExport();
        try {
            logger.info("Export to Lawson Start.");
            va.extract();
            logger.info("Export to Lawson End.");
            System.exit(0);
        } catch (Exception e) {
            logger.error("Lawson Export Error", e);
            System.exit(1); // Send failure message to Autosys
        }
    }

    public void extract() throws Exception {

        createDataFile = true; //TODO pick this from above property

        //1. FInd the time from which to extract.
        HbiEtlTracker etlTracker = util.getLawsonETLTracker();
        List<HbiEtlTracker> etlTrackerFull = util.getLawsonETLTrackerFullRecords();
        try {

            if (etlTracker == null) {
                logger.info("Warning !!! Cannot find time in HBIETLTRACKER to extract data to lawson");
                return;

            }

            //2. Extract records
            createPO25(etlTracker);

            //3. Create trailer if needed
            createIC11trailer(ic11builder);

            //4. Publish to MQ
			
            publishToMQ(ic11builder, po25builder);
			

            updateEtlTracker(etlTracker, "SUCCESS");
            updateEtlTrackerFullRecords(etlTrackerFull, "SUCCESS");

        } catch (Exception e) {
            updateEtlTracker(etlTracker, "FAIL");

            e.printStackTrace();

            throw new Exception("Error in PLM - APS - LAWSON Export");
        }
    }

    private void createPO25(HbiEtlTracker etlTracker) throws Exception {

        logger.info("STGLawsonExport.createPO25() - Start");

        Session session = null;
        List<HbiMoaObject> moaList = null;
        CreatePO25File createPO25 = null;
        String eFile = ERRFILE + PLMETLLogger.getCurrentTimeStamp() + ".txt";
        errfile = new PrintWriter(new BufferedWriter(new FileWriter(eFile)));
        Transaction txn = null;

        try {
            session = NewHibernateUtil.getSessionFactory().openSession();
            txn = session.beginTransaction();
            logger.info("DB Details = " + session.getSessionFactory().getAllClassMetadata());
            Date extractBeginTime = etlTracker.getLoadercreatetime();
            logger.info("Extract begin Time  = " + extractBeginTime);
            Query query = session.createQuery("FROM HbiMoaObject WHERE OWNERREF='HBIMATERIALSUPPLIER' "
                    + "AND ATTRIBUTEKEY='hbiPurchasing' "
                    + "AND LOADERUPDATETIME >= :extractTime");

            System.out.println("Extract beging Time = " + extractBeginTime);
            //query.setDate("extractTime", extractBeginTime);
            query.setTimestamp("extractTime", extractBeginTime);
            moaList = query.list();
            logger.debug("Query = " + query);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (moaList.size() > 0) {
            logger.info("Found MOA's to extract");
            createPO25 = new CreatePO25File();

        } else {
            return;
        }

        BigDecimal moaOwnerKey = null;
        List<HbiMaterial> materialList = null;
        List<HbiSupplier> supplierList = null;
        List<HbiMaterialSupplier> materialSupplierList = null;
        HbiMaterial material = null;
        HbiMaterialSupplier materialSupplier = null;
        HbiSupplier supplier = null;
        po25builder = new StringBuilder("");
        Query materialQuery = null;
        Query materialSupplierQuery = null;
        Query suppQuery = null;
        //LOOP across all MOA's and get materials to process
        for (HbiMoaObject moaObject : moaList) {
            logger.info("MOA with primarykey " + moaObject.getPrimarykey());

            // Find the material associate to PO 25
            moaOwnerKey = moaObject.getOwnerkey();
            if (moaOwnerKey == null) {
                throw new LCSException("HBIMOAOBJECT OwnerKey cannot be NULL");
            }

            logger.info(" Find Material for Purchasing MOA ");
            //session = NewHibernateUtil.getSessionFactory().openSession();

            materialQuery = session.createQuery("FROM HbiMaterial as A WHERE A.branchiditerationinfo = "
                    + "(SELECT  materialref from HbiMaterialSupplier as B where B.branchiditerationinfo = :moaOwnerKey )");

            materialQuery.setBigDecimal("moaOwnerKey", moaOwnerKey);
            materialList = materialQuery.list();

            if (materialList.size() == 1) {
                material = materialList.get(0);
                logger.info("Material SKU : " + material.getPtc_str_86());

                //Check if this material needs to be interfaced else continue
                materialSupplierQuery = session.createQuery("FROM HbiMaterialSupplier as A WHERE A.active = 1 and A.branchiditerationinfo = :moaOwnerKey ");
               System.out.println("moaOwnerKey>>>>>>>>>>>"+moaOwnerKey);
                materialSupplierQuery.setBigDecimal("moaOwnerKey", moaOwnerKey);
                materialSupplierList = materialSupplierQuery.list();
                
                if (!materialSupplierList.isEmpty()){
                	materialSupplier = materialSupplierList.get(0);
                	}

                if (materialSupplier == null) {
                    logger.info("IC11 EXPORT WARNING !!! : Can't Find material-Supplier associated to  Purchasing MOA IDA2A2 = " + moaObject.getIda2a2());
                    continue;
                } // Since there is no material supplier , which means there would be no purchasing MOA's to export hence skip this materials

                suppQuery = session.createQuery("FROM HbiSupplier as A WHERE A.branchiditerationinfo = :matSupRef )");
                suppQuery.setBigDecimal("matSupRef", materialSupplier.getSupplierref());
                supplierList = suppQuery.list();
               
                if (supplierList.size() == 1) {
                    supplier = supplierList.get(0);
                    logger.info("Supplier Name : " + supplier.getPtc_str_1());
                }
                logger.info("Material Supplier Found : Branch ID = " + materialSupplier.getBranchiditerationinfo());
                logger.info("material.getPtc_str_68 = " + material.getPtc_str_68());
                logger.info("materialSupplier.getDate5 = " + materialSupplier.getPtc_tms_5());
                logger.info("MOA NUM12 = " + moaObject.getPtc_dbl_12());
//Added to check if style code is 6 char or less(for type other than Material SKU) to prevent corrupt Packaging/Casing ESKO data being sent to Lawson.				
				//Code changes by Wipro Upgrade Team
                //if (material.getPtc_str_1().length() > 6 && !"\\20123\\17898602".equals(material.getFlextypeidpath())) {
                //for QA \16052\227276068
                //if (material.getPtc_str_1().length() > 6 && !"\\16052\\227276068".equals(material.getFlextypeidpath())) {
               //for prod
                 if (material.getPtc_str_1().length() > 6 && !"\\16052\\236273254".equals(material.getFlextypeidpath())) {
                    errfile.println("Incorrect Style Code(> 6 chars length) for Material: " + material.getPtc_str_1() + "  Supplier : " + supplier.getPtc_str_1());
                    continue;
                }
                if (moaObject.getPtc_dbl_12().toString().equals("0")) {
                    errfile.println("No Vendor Agreement Information in FlexPLM for Material : " + material.getPtc_str_86() + "  Supplier : " + supplier.getPtc_str_1());
                    continue;
                }
                if (moaObject.getPtc_dbl_10().toString().equals("0")) {
                    errfile.println("Tiered Price is Zero in Purchasing Table in FlexPLM for Material : " + material.getPtc_str_86() + "  Supplier : " + supplier.getPtc_str_1());
                    continue;
                }

				
if (material.getPtc_str_68().equals("true") && materialSupplier.getPtc_tms_5() != null && material.getPtc_str_82().equals("Required")) {

// Validate if material is setup in APS, then only proceed to load to Lawson.  
// Write exceptions to rejected file. 
//Checking null conditions
String flexTypeIdPath = material.getFlextypeidpath();
String hbiStyle =  null;
//Code changes by Wipro Upgrade Team
 //if("\\20123\\17898602".equals(flexTypeIdPath))
//QA 227276068
//if("\\16052\\227276068".equals(flexTypeIdPath))
//for prod
if("\\16052\\236273254".equals(flexTypeIdPath))
	{
           hbiStyle = material.getPtc_str_25();
	}
 else											
	{
		hbiStyle = material.getPtc_str_1().substring(0, Math.min(material.getPtc_str_1().length(), 6));
	}
	String hbiColorCode = material.getPtc_str_12();
    String hbiSizeCode = material.getPtc_str_40();
    String hbiAttrCode = material.getPtc_str_5();

        if (hbiSizeCode != null) {
            hbiSizeCode = hbiSizeCode.trim();
        }
        if (hbiColorCode != null) {
            hbiColorCode = hbiColorCode.trim();
        }
        if (hbiAttrCode != null) {
            hbiAttrCode = hbiAttrCode.trim();
        }

if (hbiColorCode == null || "".equals(hbiColorCode)) 
	{
		hbiColorCode = "000";
    }
if (hbiSizeCode == null || "".equals(hbiSizeCode))
	{
		hbiSizeCode = "00";
    }
if (hbiAttrCode == null || "".equals(hbiAttrCode)) 
	{
		hbiAttrCode = "------";
    }

	hbiColorCode = hbiColorCode.toUpperCase();
    hbiSizeCode = hbiSizeCode.toUpperCase();
    hbiAttrCode = hbiAttrCode.toUpperCase();

                    HBIConnectionUtil connUtil = new HBIConnectionUtil();
                    Connection con = connUtil.connect();
                    Statement st = null;
                    st = con.createStatement();
                    String sql ="select count(*) from PLMSTG.APS_CORP_PURCHASE_SKU "
                            + "where style_cd = '" + hbiStyle
                            + "' and color_cd = '"+ hbiColorCode
                            + "' and size_cd ='"+ hbiSizeCode
                            + "' and attribute_cd = '"+ hbiAttrCode
                            + "' and lw_vendor_no = " + moaObject.getPtc_str_5()
                            + " and CUSTOMS_UOM_CD is not null"
                            + " and PUR_UNIT_OF_MEASURE is not null";  	  
                    logger.info(sql);
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()) {
                        if (rs.getInt(1) == 0) {
                            logger.info("Material is not setup in APS. Not loading to Lawson");
                            errfile.println("Material " + material.getPtc_str_86() + " is not setup in APS correcly for vendor master code: " + moaObject.getPtc_str_5() + ". Not loading to Lawson!");

                        } else {
                            logger.info("Material is setup in APS. Proceeding to load to Lawson");
                            createIC11(etlTracker, material);  //IC11 is needed by lawson before PO25
                            createPO25.buildPO25Record(moaObject, material, po25builder, util);

                            logger.info("PO 25 Record Here " + po25builder.toString());
                        }

                    }
                    rs.close();
                    st.close();
                    connUtil.closeConnection(con);
                } else {
                    logger.info("PO 25 Material Skipped - " + material.getPtc_str_86());
                    continue; // Material does not need to be sent to lawson since buy flag is not yes and the SC Systems Loaded (Act) is not set
                }
            } else {
                logger.info("PO25 EXPORT WARNING !!! : Can't Find material associated to  Purchasing MOA IDA2A2 = " + moaObject.getIda2a2());
                continue;
            }

        }

        txn.commit();
        if (session != null && session.isOpen()) {
            session.flush();
            session.clear();
            session.close();
        }
        if (errfile != null) {
            errfile.close();
        }

        HbiExtractorUtil.sendErrorFiles(eFile);

        logger.info("STGLawsonExport.createPO25() - End");

    }

    private void createIC11(HbiEtlTracker etlTracker, HbiMaterial material) throws Exception {
        logger.info("STGLawsonExport.createIC11() - Start");
        Session session = null;
        CreateIC11File createIC11 = new CreateIC11File();
        List<HbiMaterial> matList = null;

        try {
            session = NewHibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM HbiMaterial WHERE branchiditerationinfo = " + material.getBranchiditerationinfo());
            matList = query.list();

        } finally {

            if (session != null && session.isOpen()) {
                session.flush();
                session.close();
            }
        }

        for (HbiMaterial mat : matList) {
            createIC11.buildIC11Record(mat, ic11builder, util);
        }

        logger.info("STGLawsonExport.createIC11() - End");
    }

    private void createIC11trailer(StringBuilder ic11builder) {
        if (ic11builder != null && ic11builder.length() > 0) {
            logger.info("Creating IC11 trailer");
            CreateIC11File.addTrailer(ic11builder, util);
        } else {
            logger.info("Creating IC11 trailer for zero records.");
                ic11builder.append("ADD0000ITEMZZTRL9999000000000000ITEMMASTER");
		util.addPadding(ic11builder,"",951);
		util.addPadding(ic11builder,"*",1);
        }

    }

    private void publishToMQ(StringBuilder ic11builder, StringBuilder po25builder) throws MQException, IOException {

        if (createDataFile == true) {
            File IC11fileToWrite = getIC11FileToWrite();
            BufferedWriter ic11output = new BufferedWriter(new FileWriter(IC11fileToWrite, true));
            ic11output.write(CreateIC11File.removeDuplicates(ic11builder.toString()));
			ic11output.close();
	
            File PO25fileToWrite = getPO25FileToWrite();
            BufferedWriter po25output = new BufferedWriter(new FileWriter(PO25fileToWrite, true));
            po25output.write(po25builder.toString());
            po25output.close();
        }

        // if (ic11builder != null && po25builder != null && ic11builder.length() > 0 && po25builder.length() > 0) {
        if (ic11builder != null && po25builder != null) {
            WINJAVAMQ mqObj = new WINJAVAMQ();	
			mqObj.IC11(CreateIC11File.removeDuplicates(ic11builder.toString()));
            mqObj.PO25(po25builder.toString());
        }
        logger.info("Records Sent to Lawson");

    }
	
    

    private void updateEtlTracker(HbiEtlTracker etlTracker, String status) {

        if (etlTracker == null) {
            return;
        }

        logger.info("Setting ETLTracker Status = " + status);
        Session session = NewHibernateUtil.getSessionFactory().openSession();
        Transaction txn = session.beginTransaction();
        etlTracker.setLawsonexport(status);
        Date date = new Date();
        java.sql.Timestamp dbtimestamp = new java.sql.Timestamp(date.getTime());
        etlTracker.setLawsonexporttime(dbtimestamp);
        session.saveOrUpdate(etlTracker);
        txn.commit();
        session.close();
    }

    private void updateEtlTrackerFullRecords(List<HbiEtlTracker> hbiETLTracker, String status) {

        if (hbiETLTracker.size() == 0) {
            return;
        }
        HbiEtlTracker etlTracker = null;

        logger.info("Setting ETLTracker Status = " + status);
        Session session = NewHibernateUtil.getSessionFactory().openSession();
        Transaction txn = session.beginTransaction();
        for (int i = 0; i < hbiETLTracker.size(); i++) {
            etlTracker = (HbiEtlTracker) hbiETLTracker.get(i);
            etlTracker.setLawsonexport(status);
            Date date = new Date();
            java.sql.Timestamp dbtimestamp = new java.sql.Timestamp(date.getTime());
            etlTracker.setLawsonexporttime(dbtimestamp);
            session.saveOrUpdate(etlTracker);
        }
        txn.commit();
        session.close();
    }

    private File getIC11FileToWrite() {

        if (IC11file == null) {
            return IC11file = new File(IC11fileLocation + PLMETLLogger.getCurrentTimeStamp() + ".txt");
        } else {
            return IC11file;
        }
    }

    private File getPO25FileToWrite() {

        if (PO25file == null) {
            return PO25file = new File(PO25fileLocation + PLMETLLogger.getCurrentTimeStamp() + ".txt");
        } else {
            return PO25file;
        }
    }

}

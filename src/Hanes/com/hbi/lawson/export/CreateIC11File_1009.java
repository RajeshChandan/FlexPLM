package com.hbi.lawson.export;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.hbi.etl.dao.HbiBusinessObject;
import com.hbi.etl.dao.HbiInvTypeXref;
import com.hbi.etl.dao.HbiMaterial;
import com.hbi.etl.dao.HbiPurchXref;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.NewHibernateUtil;
import com.hbi.stg.extractors.util.HBIConnectionUtil;
import com.hbi.stg.extractors.util.HbiExtractorUtil;
import com.lcs.wc.util.LCSProperties;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.math.*;
import java.util.LinkedHashSet;
import java.util.Arrays;

public class CreateIC11File_1009 {

    static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger log = PLMETLLogger.createInstance(CreateIC11File.class, logLevel, true);

    String lineseparator = System.getProperty("line.separator");
    static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public void buildIC11Record(HbiMaterial material, StringBuilder builder, HbiExtractorUtil util) throws IOException, SQLException {

        Session session = null;
        List<HbiPurchXref> purchXRefList = null;
        List<HbiInvTypeXref> invTypeXRefList = null;
        String purMajCat = "";
        String purMinCat = "";
        String invTypeCode = "";
        Transaction txn = null;
        try {
            session = NewHibernateUtil.getSessionFactory().openSession();
            txn = session.beginTransaction();
//            String majCat = material.getAtt46();
//            String minCat = material.getAtt47();
            
            String majCat = material.getPtc_str_46();
            String minCat = material.getPtc_str_47();
            
            Query xRefQuery = session.createQuery("FROM HbiPurchXref as A WHERE A.PLM_MAJ_CAT =:majCat AND A.PLM_MIN_CAT =:minCat ");
            xRefQuery.setString("majCat", majCat);
            xRefQuery.setString("minCat", minCat);

            //Find Vendor Agreement BO
            purchXRefList = xRefQuery.list();

            Query invTypeQuery = session.createQuery("FROM HbiInvTypeXref as A WHERE A.plmMajCat =:majCat AND A.plmMinCat =:minCat ");
            invTypeQuery.setString("majCat", majCat);
            invTypeQuery.setString("minCat", minCat);

            invTypeXRefList = invTypeQuery.list();

            txn.commit();

        } finally {
            if (session != null && session.isOpen()) {
                session.flush();
                session.clear();
                session.close();
            }
        }
        HbiPurchXref purchXref = purchXRefList.get(0);
        HbiInvTypeXref invTypeXref = invTypeXRefList.get(0);

        if (purchXref != null) {

            purMajCat = purchXref.getLAWSON_MAJ_CAT();
            purMinCat = purchXref.getLAWSON_MIN_CAT();
        }

        if (invTypeXref != null) {
            invTypeCode = invTypeXref.getInvTypeCode();
        }
// Changes for 17 digit SKU. If color_cd,attr_cd and size_cd are default values(000,------,00), send the 6 digit style_cd only.
// Else, send the 17 digit SKU in name field.
        String hbiSKU = null;
		String hbiStyle = null;
		String flexTypeIdPath = material.getFlextypeidpath();
		log.info("flexTypeIdPath =" + flexTypeIdPath);
		//FOR prod \\16052\\236273254
		if(flexTypeIdPath.startsWith("\\16052\\236273254")){
		//if("\\20123\\17898602".equals(flexTypeIdPath)){
		//for QA \16052\227276068
	//if(flexTypeIdPath.startsWith("\16052\227276068")){
			hbiStyle = material.getPtc_str_25();
		}
		else{
			hbiStyle = material.getPtc_str_1();
		}
        String hbiColorCode = material.getPtc_str_12(); 
        String hbiSizeCode = material.getPtc_str_40();
        String hbiAttrCode = material.getPtc_str_5();
        if (hbiSizeCode != null) {
            hbiSizeCode = hbiSizeCode.trim();
			//hbiSizeCode = hbiSizeCode.toUpperCase();
        }
        if (hbiColorCode != null) {
            hbiColorCode = hbiColorCode.trim();
			//hbiColorCode = hbiColorCode.toUpperCase();
        }
        if (hbiAttrCode != null) {
            hbiAttrCode = hbiAttrCode.trim();
			//hbiAttrCode = hbiAttrCode.toUpperCase();
        }

		log.info("hbiStyle =" + hbiStyle);
        log.info("hbiColorCode =" + hbiColorCode);
        log.info("hbiSizeCode =" + hbiSizeCode);
        if ((hbiColorCode == null || "".equals(hbiColorCode) || "000".equals(hbiColorCode))
                && (hbiSizeCode == null || "".equals(hbiSizeCode) || "00".equals(hbiSizeCode))
                && (hbiAttrCode == null || "".equals(hbiAttrCode) || "------".equals(hbiAttrCode))) {

            hbiSKU = String.format("%-6s", hbiStyle);

        } else {
            if (hbiColorCode == null || "".equals(hbiColorCode)) {
                hbiColorCode = "000";
            }
            if (hbiSizeCode == null || "".equals(hbiSizeCode)) {
                hbiSizeCode = "00";
            }
            if (hbiAttrCode == null || "".equals(hbiAttrCode)) {
                hbiAttrCode = "------";
            }

            hbiSKU = String.format("%-6s", hbiStyle)
                    + String.format("%-3s", hbiColorCode)
                    + String.format("%-6s", hbiAttrCode)
                    + String.format("%-2s", hbiSizeCode);
        }

        String stockUOM = "";
        String buyUOM = "";

        if (material.getPtc_str_28() != null) {
            stockUOM = material.getPtc_str_28().toUpperCase(); //Inventory UOM
        } else if (material.getPtc_str_45() != null) {
            stockUOM = material.getPtc_str_45().toUpperCase(); // Usage UOM
        }

        if (material.getPtc_str_7() != null) {
            buyUOM = material.getPtc_str_7().toUpperCase(); //Buy UOM
        }
         // Added logic for Alt Conversion UOM      
        double hbiConvFactorInt = 1.0;
		BigDecimal hbiConvFactorIntNew;
        String hbiConversionFactor = "0000100000";
        if (!buyUOM.equals(stockUOM)) {
            log.info("Buy UOM Not equal to Stock UOM");
            if (material.getPtc_dbl_36() != null) {
                log.info("material.getPtc_dbl_36() = " + material.getPtc_dbl_36());
                log.info("hbiConvFactorInt = " + hbiConvFactorInt);
				hbiConvFactorIntNew=material.getPtc_dbl_36();
                hbiConvFactorInt = hbiConvFactorIntNew.doubleValue();
                DecimalFormat myFormatter = new DecimalFormat("00000.00000");
                hbiConversionFactor = myFormatter.format(hbiConvFactorInt);
                log.info("hbiConversionFactor1 =" + hbiConversionFactor);
                hbiConversionFactor = hbiConversionFactor.replaceAll("\\.", "");
                log.info("hbiConversionFactor =" + hbiConversionFactor);
            }
        }
        // Get the Actual size code from APS_ Size Table View for DESCRIPTION-2B field
        String hbiNumericSizeCode = null;
        if (hbiSizeCode != null) {
            hbiSizeCode = hbiSizeCode.trim();
            HBIConnectionUtil connUtil = new HBIConnectionUtil();
            Connection con = connUtil.connect();
            Statement st = null;
            st = con.createStatement();
            String sql = "select actual_size_no from plmstg.APS_ITEM_SIZE "
                    + "where size_cd = '" + hbiSizeCode + "'";

            log.debug(sql);
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                if (rs.getString("actual_size_no") != null) {

                    double data = Double.parseDouble(rs.getString("actual_size_no"));
                    DecimalFormat df = new DecimalFormat("0.00");
                    hbiNumericSizeCode = df.format(data);
                    log.debug("hbiNumericSizeCode = " + hbiNumericSizeCode);
                }

            }
            rs.close();
            st.close();
            connUtil.closeConnection(con);

        }
		
		String hbiItemDesc = material.getPtc_str_29();
		if (hbiItemDesc != null) {
		// Replace all whitespace blocks with single spaces.
			hbiItemDesc = hbiItemDesc.replaceAll("\\s+"," ");
		}
		log.debug("hbiItemDesc = " + hbiItemDesc);

        builder.append("ADD");
        builder.append("0000FDIG");
        builder.append(" ");

        util.addPadding(builder, hbiSKU.toUpperCase(), 32);  //Name We are making sure all values are in captial letter
		
        util.addPadding(builder, hbiItemDesc, 30);//hbiItemDescription
        
        if ("Fabric".equals(material.getPtc_str_46())) {
            util.addPadding(builder, "", 15); //DESCRIPTION-2A
            util.addPadding(builder, hbiNumericSizeCode != null ? hbiNumericSizeCode : "", 15); //DESCRIPTION-2B // Send numeric size code from APS    
        } else {
            if (hbiItemDesc != null) {
                int len = hbiItemDesc.length();
                if (len > 30) {
                    util.addPadding(builder, hbiItemDesc.substring(30).toUpperCase(), 30); //DESCRIPTION-2 // If description exceeds 30 chars, send it in Desc 2 field
                } else {
                    util.addPadding(builder, "", 30); //DESCRIPTION-2
                }
            } else {
                util.addPadding(builder, "", 30); //DESCRIPTION-2       
            }
        }
        util.addPadding(builder, stockUOM, 4);//STOCK-UOM
        util.addPadding(builder, "", 4);
        util.addPadding(builder, "", 32); //FILLER
        util.addPadding(builder, "", 10); //GENERIC
        util.addPadding(builder, "", 4); //FREIGHT-CLASS
        util.addPadding(builder, "PPV", 4); //SALES-MAJCL
        // FIT-SALES-MINCL
        util.addPadding(builder, purMinCat, 4); //SALES-MINCL
        //TODo inventory major
        util.addPadding(builder, invTypeCode, 4); //INVEN-MAJCL
        // Inventory minor
        if ("Fabric".equals(material.getPtc_str_46()) && hbiAttrCode.startsWith("DYE")) {
            util.addPadding(builder, "PFAB", 4); //INVEN-MINCL  - For Fabric and DYE attribute default to "PFAB".
        }
		else {
            util.addPadding(builder, "DIRM", 4); //INVEN-MINCL        
        }        // purchase major
        util.addPadding(builder, purMajCat, 4); //PURCH-MAJCL
        // purchase minor
        util.addPadding(builder, purMinCat, 4); //PURCH-MINCL
        util.addPadding(builder, "", 34); //FILLER
        util.addPadding(builder, "000000000", 9); //WEIGHT-A + WEIGHT-S
        util.addPadding(builder, "", 1); //NBR-DEC-QTY-A
        util.addPadding(builder, "", 10); //FILLER
        util.addPadding(builder, "4", 1);  //Qty Decimals
        util.addPadding(builder, "", 154);

        System.out.println(dateFormat.format(material.getCreatestampa2()));
        //util.addPadding(builder, dateFormat.format(material.getCreatestampa2()), 8);// Date Added // Leave Blank
        util.addPadding(builder, "", 8);
        util.addPadding(builder, "", 1);
        util.addPadding(builder, "A", 1); // Active Status

        util.addPadding(builder, buyUOM.equals(stockUOM) ? "" : buyUOM, 4); //ALT-UOM-1 - Send BuyUOM if different from StockUOM
        util.addPadding(builder, "", 4); //ALT-UOM-02
        util.addPadding(builder, "", 4); //ALT-UOM-03          
        util.addPadding(builder, "", 28); //FILLER
        util.addPadding(builder, hbiConversionFactor, 10); //ALT-UOM-CONVR-A-01
        util.addPadding(builder, "0000000000", 10); //ALT-UOM-CONVR-A-02
        util.addPadding(builder, "0000000000", 10); //ALT-UOM-CONVR-A-03
        util.addPadding(builder, "", 220); //FILLER
        util.addPadding(builder, "X", 1); //TRACKING-FL-01
        util.addPadding(builder, "X", 1); //TRACKING-FL-02
        util.addPadding(builder, "", 1); //TRACKING-FL-03
        util.addPadding(builder, "", 7); //FILLER
        util.addPadding(builder, "2", 1); //TRANS-FL-01
        util.addPadding(builder, "1", 1); //TRANS-FL-02
        util.addPadding(builder, "", 1); //TRANS-FL-03        
        util.addPadding(builder, "", 7); //FILLER
        util.addPadding(builder, "2", 1); //SELL-FL-01          
        util.addPadding(builder, "1", 1); //SELL-FL-02          
        util.addPadding(builder, "", 1); //SELL-FL-03          
        util.addPadding(builder, "", 7); //FILLER
        util.addPadding(builder, "2", 1); //SELL-PRICE-FL-01
        util.addPadding(builder, "1", 1); //SELL-PRICE-FL-02
        util.addPadding(builder, "", 1); //SELL-PRICE-FL-03
        util.addPadding(builder, "", 7); //FILLER
        util.addPadding(builder, "1", 1); //BUY-FL-01
        util.addPadding(builder, "2", 1); //BUY-FL-02
        util.addPadding(builder, "", 1); //BUY-FL-03
        //util.addPadding(builder,"",1);
        //util.addPadding(builder,"",1);

        util.addPadding(builder, "", 259);
        //util.addPadding(builder,"",1);
        //util.addPadding(builder,"",1);
        util.addPadding(builder, "0*", 2);
        builder.append(lineseparator);

    }

    public static void addTrailer(StringBuilder builder, HbiExtractorUtil util) {


		
		int lCount = removeDuplicates(builder.toString()).split("\\n").length;	
		builder.append("ADD");
        HbiExtractorUtil hbiExtractorUtil = new HbiExtractorUtil();
        String lineCount = hbiExtractorUtil.getLineCount(lCount);
		builder.append("0000ITEMZZTRL9999").append(lineCount).append("000000ITEMMASTER");
        util.addPadding(builder, "", 951);
        util.addPadding(builder, "*", 1);

    }
	
	public static String removeDuplicates(String iC11Build){
			String[] strArr = iC11Build.split("\\n");
			LinkedHashSet<String> set = new LinkedHashSet<String>(Arrays.asList(strArr));
			String[] result = new String[set.size()];
			set.toArray(result);
			StringBuilder res = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				String string = result[i];
				if(i==result.length-1)
					res.append(string).append("\n");
				else
					res.append(string).append("\n");
			}
			
			return res.toString();
	}

}

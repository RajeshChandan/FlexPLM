package com.hbi.lawson.export;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.hbi.etl.dao.HbiBusinessObject;
import com.hbi.etl.dao.HbiMaterial;
import com.hbi.etl.dao.HbiMoaObject;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.NewHibernateUtil;
import com.hbi.stg.extractors.util.HbiExtractorUtil;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;

public class CreatePO25File {

    static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger log = PLMETLLogger.createInstance(CreatePO25File.class, logLevel, true);

    static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    String lineseparator = System.getProperty("line.separator");

    public void buildPO25Record(HbiMoaObject moaObject, HbiMaterial material, StringBuilder builder, HbiExtractorUtil util) throws LCSException {

        System.out.println("CreatePO25File.writePO25Record");
        if (material == null || moaObject == null) {
            return;
        }

        Session session = null;
        Transaction txn = null;
        List<HbiBusinessObject> vendorAgreementList = null;
        try {
            session = NewHibernateUtil.getSessionFactory().openSession();
            txn = session.beginTransaction();
            Query boQuery = session.createQuery("FROM HbiBusinessObject as A WHERE A.ida2a2 = :key ");
            boQuery.setBigDecimal("key", moaObject.getPtc_dbl_12()); //num12 = vendor Agreement Ref

            //Find Vendor Agreement BO
            vendorAgreementList = boQuery.list();
            txn.commit();
        } finally {
            if (session != null && session.isOpen()) {
                session.flush();
                session.clear();
                session.close();
            }
        }

        if (vendorAgreementList.size() != 1) {
        	log.info("For Material: "+material.getPtc_str_1());
        	log.info("Cannot find a unique Vendor Agreement");
        	
            throw new LCSException("CreatePO25File.buildPO25Record() - "+"For Material: "+material.getPtc_str_1()+", Cannot find a unique Vendor Agreement ida2a2 " + moaObject.getPtc_dbl_12() + "\\n"
                    + " MOAOBEJCT IDA2A2 = " + moaObject.getIda2a2());
        }

        HbiBusinessObject VendorAgreement = vendorAgreementList.get(0);
        System.out.println("Vendor Agreement" + VendorAgreement.getPtc_str_1());

        // Changes for 17 digit SKU. If color_cd,attr_cd and size_cd are default values(000,------,00), send the 6 digit style_cd only.
// Else, send the 17 digit SKU in name field.
        String hbiSKU = null;
		String hbiStyle =  null;
		String flexTypeIdPath = material.getFlextypeidpath();
		log.info("flexTypeIdPath =" + flexTypeIdPath);
		
		//FOR prod \\16052\\236273254
		if(flexTypeIdPath.startsWith("\\16052\\236273254")){
		//if(flexTypeIdPath.startsWith("\\20123\\17898602")){
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
        }
        if (hbiColorCode != null) {
            hbiColorCode = hbiColorCode.trim();
        }
        if (hbiAttrCode != null) {
            hbiAttrCode = hbiAttrCode.trim();
        }
		log.info("hbiStyle =" + hbiStyle);
        log.info("hbiColorCode =" + hbiColorCode);
        log.info("hbiSizeCode =" + hbiSizeCode);

        if ((hbiColorCode == null || "".equals(hbiColorCode) || "000".equals(hbiColorCode))
                && (hbiSizeCode == null || "".equals(hbiSizeCode) || "00".equals(hbiSizeCode))
                && (hbiAttrCode == null || "".equals(hbiAttrCode) || "------".equals(hbiAttrCode))) {

            hbiSKU = validateAndReturnHBISKUDefault(hbiStyle);

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

           hbiSKU = validateAndReturnHBISKU(hbiStyle, hbiColorCode, hbiAttrCode, hbiSizeCode);
        }
		
		String hbiItemDesc = material.getPtc_str_29();
		if (hbiItemDesc != null) {
		// Replace all whitespace blocks with single spaces.
			hbiItemDesc = hbiItemDesc.replaceAll("\\s+"," ");
		}
		log.debug("hbiItemDesc = " + hbiItemDesc);
		
        System.out.println("Creating PO25 data");
        if (builder.length() > 0) {
            builder.append(lineseparator); // Apend a line separator only while writing second and later lines
        }
        builder.append("2" + HbiExtractorUtil.delimiter);
        util.addField(builder, hbiItemDesc);  //Item Desc
        util.addField(builder, hbiSKU);//Item
        util.addField(builder, material.getPtc_str_7() != null ? material.getPtc_str_7().toUpperCase() : ""); // UOM
        util.addField(builder, String.valueOf(material.getPtc_dbl_9()));// Base Cost  - hbiStdCost
        util.addField(builder, hbiSKU.toUpperCase());
        util.addField(builder, "");
        util.addField(builder, "");
        util.addField(builder, "");
        util.addField(builder, "");
        util.addField(builder, "");
        util.addField(builder, "");
        util.addField(builder, "");
        util.addField(builder, VendorAgreement.getPtc_str_2() != null ? VendorAgreement.getPtc_str_2().toUpperCase() : ""); //man Agreement ref
        util.addField(builder, VendorAgreement.getPtc_str_3() != null ? VendorAgreement.getPtc_str_3().toUpperCase() : ""); // Name
        util.addField(builder, "");  //ICSEGMENT
        util.addField(builder, ""); //ICFAMILY
        util.addField(builder, ""); //ICCLASS
        util.addField(builder, "");//ICCOMMODITY
        util.addField(builder, ""); //PURCHMAJCL
        util.addField(builder, ""); //PURCHMINCL
        util.addField(builder, ""); //INVENMAJCL
        util.addField(builder, ""); //INVENMINCL
        util.addField(builder, VendorAgreement.getPtc_str_3() != null ? VendorAgreement.getPtc_str_3().toUpperCase() : ""); //USERFIELD1 -- Check vendor Name
        util.addField(builder, VendorAgreement.getPtc_str_5() != null ? VendorAgreement.getPtc_str_5().toUpperCase() : ""); 	//USERFIELD2 Check vendor master code
        util.addField(builder, VendorAgreement.getPtc_str_4() != null ? VendorAgreement.getPtc_str_4().toUpperCase() : ""); 	//USERFIELD3 Vendor Location
        util.addField(builder, ""); //USERFIELD4
        util.addField(builder, ""); //USERFIELD5
        util.addField(builder, ""); //USERFIELDN1
        util.addField(builder, ""); //USERFIELDN2
        util.addField(builder, ""); //USERFIELDN3				
        util.addField(builder, VendorAgreement.getPtc_tms_1() != null ? dateFormat.format(VendorAgreement.getPtc_tms_1()) : ""); //EFFECTIVEDT	Alpha 8
        util.addField(builder, VendorAgreement.getPtc_tms_2() != null ? dateFormat.format(VendorAgreement.getPtc_tms_2()) : ""); //EXPIREDT	Alpha 8
        util.addField(builder, "A"); //LNBRKQAFLAG	Alpha 1
        util.addField(builder, moaObject.getPtc_dbl_11() != null ? moaObject.getPtc_dbl_11().toPlainString() : "");  //LNBRKQTYAMT1	Alpha 13 - Putting tiered Qty
        util.addField(builder, ""); //LNBRKPERCENT1	Alpha 10
        util.addField(builder, moaObject.getPtc_dbl_10() != null ? moaObject.getPtc_dbl_10().toPlainString() : ""); //LNBRKCOST1	Alpha 19 -- Putting Tiered Price 
        util.addField(builder, ""); //LNBRKQTYAMT2	Alpha 13
        util.addField(builder, ""); //LNBRKPERCENT2	Alpha 10
        util.addField(builder, ""); //LNBRKCOST2	Alpha 19
        util.addField(builder, ""); //LNBRKQTYAMT3	Alpha 13
        util.addField(builder, ""); //LNBRKPERCENT3	Alpha 10
        util.addField(builder, "");//LNBRKCOST3	Alpha 19
        util.addField(builder, "");  //LNBRKQTYAMT4	Alpha 13
        util.addField(builder, ""); //LNBRKPERCENT4	Alpha 10
        util.addField(builder, ""); //LNBRKCOST4	Alpha 19
        util.addField(builder, ""); //LNBRKQTYAMT5	Alpha 13
        util.addField(builder, ""); //LNBRKPERCENT5	Alpha 10
        util.addField(builder, ""); //LNBRKCOST5	Alpha 19
        util.addField(builder, ""); //COMMODITY CODE	Alpha 35
        util.addField(builder, ""); //MANUFCODE	Alpha 4
        util.addField(builder, ""); //MANUFDIVISION	Alpha 4
        util.addField(builder, ""); //RELATEDVENITEM	Alpha 32
        util.addField(builder, ""); //	ITEMSORTOPTION	Num 1
        util.addField(builder, ""); //PIVVENITEMDESC	Alpha 30
        util.addField(builder, ""); //COSTOPTION	Alpha 1
        util.addField(builder, ""); //	GTIN	Numeric 14

    }

/**
     * This function is added as a part of field expansion project to handling the expansion of size code and color code from 2 and 3 digits to 3 and 5 digits
     * @param hbiStyle - String
     * @param hbiColorCode - String
     * @param hbiAttrCode - String
     * @param hbiSizeCode - String
     * @return hbiSKU - String
     * @throws WTException
     */
    public static String validateAndReturnHBISKU(String hbiStyle, String hbiColorCode, String hbiAttrCode, String hbiSizeCode)
    {
    	// LCSLog.debug("### START CreateIC11File.validateAndReturnHBISKU(String hbiStyle, String hbiColorCode, String hbiAttrCode, String hbiSizeCode) ###");
    	String hbiSKU = "";
		String delimiter = ".";
		
		if(hbiStyle.length() == 6)
		{
			hbiSKU = String.format("%-6s", hbiStyle);
		}
		else if(hbiStyle.length() == 5)
		{
			hbiSKU = String.format("%-5s", hbiStyle);
		}
		else if(hbiStyle.length() == 4)
		{
			hbiSKU = String.format("%-4s", hbiStyle);
		}
		else if(hbiStyle.length() == 3)
		{
			hbiSKU = String.format("%-3s", hbiStyle);
		}
    		
    	if((hbiColorCode.length() == 3) && (hbiSizeCode.length() == 2))
		{
			hbiSKU = hbiSKU
				+ String.format("%-1s", delimiter)
                + String.format("%-3s", hbiColorCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-6s", hbiAttrCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-2s", hbiSizeCode);
		}
    	else if((hbiColorCode.length() == 3) && (hbiSizeCode.length() == 3))
		{
			hbiSKU = hbiSKU
				+ String.format("%-1s", delimiter)
                + String.format("%-3s", hbiColorCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-6s", hbiAttrCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-3s", hbiSizeCode);
		}
    	else if((hbiColorCode.length() == 4) && (hbiSizeCode.length() == 2))
		{
			hbiSKU = hbiSKU
				+ String.format("%-1s", delimiter)
                + String.format("%-4s", hbiColorCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-6s", hbiAttrCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-2s", hbiSizeCode);
		}
    	else if((hbiColorCode.length() == 4) && (hbiSizeCode.length() == 3))
		{
			hbiSKU = hbiSKU
				+ String.format("%-1s", delimiter)
                + String.format("%-4s", hbiColorCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-6s", hbiAttrCode)
				+ String.format("%-1s", delimiter)
                + String.format("%-3s", hbiSizeCode);
		}
    	else
    	{
    		hbiSKU = hbiSKU
					+ String.format("%-1s", delimiter)
                    + String.format("%-3s", hbiColorCode)
					+ String.format("%-1s", delimiter)
                    + String.format("%-6s", hbiAttrCode)
					+ String.format("%-1s", delimiter)
                    + String.format("%-2s", hbiSizeCode);
    	}
    
    	// LCSLog.debug("### END CreateIC11File.validateAndReturnHBISKU(String hbiStyle, String hbiColorCode, String hbiAttrCode, String hbiSizeCode) ###");
		return hbiSKU;
    }
	
	/**
     * This function is added as a part of field expansion project to handling the padding for default values of material number 6,5,4 and 3 characters.
     * @param hbiStyle - String
     * @param hbiColorCode - String
     * @param hbiAttrCode - String
     * @param hbiSizeCode - String
     * @return hbiSKU - String
     * @throws WTException
     */
    public static String validateAndReturnHBISKUDefault(String hbiStyle)
    {
    	String hbiSKU = "";
		
		if(hbiStyle.length() == 6)
		{
			hbiSKU = String.format("%-6s", hbiStyle);
		}
		else if(hbiStyle.length() == 5)
		{
			hbiSKU = String.format("%-5s", hbiStyle);
		}
		else if(hbiStyle.length() == 4)
		{
			hbiSKU = String.format("%-4s", hbiStyle);
		}
		else if(hbiStyle.length() == 3)
		{
			hbiSKU = String.format("%-3s", hbiStyle);
		}
		
		return hbiSKU;
	}	

}




package com.limited.integration;


import com.extjs.gxt.ui.client.util.Padding;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.measurements.LCSPointsOfMeasure;
import com.lcs.wc.measurements.MeasurementValues;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.MultiCharDelimStringTokenizer;
import com.lcs.wc.util.VersionHelper;
import com.limited.exporter.helper.VSAttExtractHelper;
import com.limited.exporter.helper.VSAttributeHelper;
import com.ptc.core.meta.common.RemoteWorker;
import com.ptc.core.meta.common.RemoteWorkerHandler;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;


public class VSPSDService {
    private static final Logger logger = LogR.getLogger (VSPSDService.class.getName ());
    private static final String USAGE_MSG = "Usage:\n\t Extract Enumeration details"
            + "\n\t\t windchill com.limited.integration.VSPSDService [-u <userName> -p <password> -d <Product Name>]"
            + "\n\t\t example: windchill com.limited.integration.VSPSDService -u wcadmin -p pr0dt3(h -d 10221358";


    public static void main (String[] args) {
        String userName = "";
        String password = "";
        String productName = "";
        boolean stopExecution = true;


        for (int i = 0; i < args.length; i++) {


            switch (args[i]) {
                case "-u":
                    if (i + 1 < args.length) {
                        userName = args[++i];
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        password = args[++i];
                    }
                    break;
                case "-d":
                    if (i + 1 < args.length) {
                        productName = args[++i];
                    }
                    break;
                default:
                    break;
            }
        }


        if (FormatHelper.hasContent (userName)) {
            RemoteMethodServer.getDefault ().setUserName (userName);
            if (FormatHelper.hasContent (password)) {
                RemoteMethodServer.getDefault ().setPassword (password);
                stopExecution = false;
            }
        }
        if (!FormatHelper.hasContent (productName)) {
            stopExecution = true;
        }
        if (!stopExecution) {
            try {
                SessionHelper.manager.getPrincipal ();
                RemoteWorkerHandler.handleRemoteWorker (new VSPSDServiceRemoteWorker (), args);
            } catch (Exception e) {
                logger.error ("", e);
            }
        } else {
            System.out.println (USAGE_MSG);
        }


    }


    void doTheJob (String productName) throws Exception {

        StringBuilder header = new StringBuilder ();
        int headCount = 0;
        VSPSDService servce = new VSPSDService ();
        String wthome = WTProperties.getServerProperties ().getProperty ("wt.home");
        String exportFileStr = wthome + "\\" + "_MeasurementExport_" + System.currentTimeMillis () + ".csv";

        logger.debug ("prodcut name>>>>>" + productName);
        if (FormatHelper.hasContent (productName)) {
            LCSProduct product = servce.getProdcut (productName);

            logger.debug ("prodcut >>>>>>" + product);

            if (product != null) {
                Collection results = LCSMeasurementsQuery.findMeasurmentsForProduct (product).getResults ();
                Iterator i = results.iterator ();
                FlexObject obj = null;
                while (i.hasNext ()) {
                    obj = (FlexObject) i.next ();
                    logger.debug (obj);

                    LCSMeasurements measurement = (LCSMeasurements) LCSQuery
                            .findObjectById ("VR:com.lcs.wc.measurements.LCSMeasurements:"
                                    + (String) obj.get ("LCSMEASUREMENTS.BRANCHIDITERATIONINFO"));

                    measurement = (LCSMeasurements) VersionHelper.latestIterationOf (measurement);
                    if (measurement != null) {
                        String name = measurement.getMeasurementsName ();
                        name = name.replaceAll ("[^a-zA-Z0-9\\s+]", " ");
                        exportFileStr = wthome + "\\" + name + "_MeasurementExport_" + System.currentTimeMillis () + ".csv";
                        try (PrintWriter pw = new PrintWriter (new File (exportFileStr))) {

                            header = new StringBuilder ();
                            header.append ("#Product");
                            header.append (",").append ("Measurment");
                            priftMeasurementHeader (measurement, header);
                            header.append (",").append ("#,Name,Criticality,Section,Section Sort Order,Tol(-),Tol(+)");

                            Collection Size = MOAHelper.getMOACollection (measurement.getSizeRun ());
                            Collection pom = LCSMeasurementsQuery.findPointsOfMeasure (measurement);

                            Iterator var10 = pom.iterator ();
                            Iterator var11 = Size.iterator ();

                            headCount = 0;
                            while (var10.hasNext ()) {

                                LCSPointsOfMeasure var12 = (LCSPointsOfMeasure) var10.next ();
                                StringBuilder sb = new StringBuilder ();

                                sb.append (productName);
                                sb.append (",").append (measurement.getMeasurementsName ());
                                priftMeasurementData (measurement, sb);

                                String var13 = FormatHelper.getObjectId (var12);
                                this.getRecordByOid (var13, sb);
                                Hashtable var16 = var12.getMeasurementValues ();

                                StringTokenizer tokenizer = new MultiCharDelimStringTokenizer (measurement.getSizeRun (), MOAHelper.DELIM);

                                while (tokenizer.hasMoreTokens ()) {
                                    String token = tokenizer.nextToken ();
                                    if (headCount == 0) {
                                        header.append (",").append (token);
                                    }

                                    MeasurementValues var6 = (MeasurementValues) var16.get (token);

                                    if (var6 != null) {
                                        sb.append (",").append (var6.getValue ());
                                    } else {
                                        sb.append (",").append ("");
                                    }
                                }
                                if (headCount == 0) {
                                    logger.debug ("adding header>>>" + header);
                                    pw.println (header);
                                }
                                headCount = 2;
                                pw.println (sb);
                            }
                        }
                    }
                }
            }
        }
    }


    public LCSProduct getProdcut (String prodcutName) throws WTException {
        LCSProduct product = null;
        PreparedQueryStatement pstmt = new PreparedQueryStatement ();
        pstmt.appendFromTable ("LCSProduct");
        pstmt.appendSelectColumn ("LCSProduct", "idA2A2");
        pstmt.appendCriteria (new Criteria ("LCSProduct", "ptc_lng_2typeInfoLCSProduct", prodcutName, Criteria.EQUALS));


        logger.debug ("sql query>>>" + pstmt.getSqlStatement ());

        SearchResults result = LCSQuery.runDirectQuery (pstmt);

        if (result != null) {
            logger.debug ("Total " + result.getResultsFound () + "  Prodcuts Found for prodcut " + prodcutName);


            FlexObject fo = (FlexObject) result.getResults ().firstElement ();
            if (fo != null) {
                String oid = fo.getString ("LCSPRODUCT.idA2A2");
                product = (LCSProduct) LCSQuery.findObjectById ("OR:com.lcs.wc.product.LCSProduct:" + oid);
                product = LCSProductQuery.getProductVersion ((LCSPartMaster) product.getMaster (), "A");
            }
        }
        return product;
    }

    public void priftMeasurementHeader (LCSMeasurements measurement, StringBuilder sb) throws Exception {

        List<String> attrs = Arrays.asList ("gradingMethod", "ltdAdditionalInfo", "ltdApprovalStatus", "ltdBrand", "ltdDescription", "ltdMerchandiseCategory", "ltdTemplateStatusProduct", "measNumber", "name", "uom");

        sb.append (",").append ("Type");
        sb.append (",").append ("Product Size Definition");
        sb.append (",").append ("Size Definition Template");
        sb.append (",").append ("Measurement Template");
        sb.append (",").append ("Grade Rule Template");
        sb.append (",").append ("Default UOM");

        for (String attr : attrs) {
            FlexTypeAttribute fAttr = measurement.getFlexType ().getAttribute (attr);
            sb.append (",").append (fAttr.getAttDisplay ());
        }
        sb.append (",").append ("Size Value");
        sb.append (",").append ("Base Size");
    }

    public void priftMeasurementData (LCSMeasurements measurement, StringBuilder sb) throws Exception {

        sb.append (",").append (measurement.getFlexType ().getTypeDisplayName ());

        if (measurement.getProductSizeCategory () != null) {
            sb.append (",").append (measurement.getProductSizeCategory ().getName ());
            sb.append (",").append (measurement.getProductSizeCategory ().getSizeRange ().getName ());
        } else {
            sb.append (",").append ("");
            sb.append (",").append ("");
        }

        if (measurement.getSourceTemplate () != null) {
            LCSMeasurements sourceTemplate = (LCSMeasurements) VersionHelper.latestIterationOf (measurement.getSourceTemplate ());
            sb.append (",").append (sourceTemplate.getName ());
        } else {
            sb.append (",").append ("");
        }

        if (measurement.getGradings () != null) {
            LCSMeasurements gradingTemplate = (LCSMeasurements) VersionHelper.latestIterationOf (measurement.getGradings ());
            sb.append (",").append (gradingTemplate.getName ());
        } else {
            sb.append (",").append ("");
        }

        sb.append (",").append (measurement.getValue ("uom"));

        List<String> attrs = Arrays.asList ("gradingMethod", "ltdAdditionalInfo", "ltdApprovalStatus", "ltdBrand", "ltdDescription", "ltdMerchandiseCategory", "ltdTemplateStatusProduct", "measNumber", "name", "uom");

        String value = "";
        for (String attr : attrs) {
            value = String.valueOf (measurement.getValue (attr));
            value = String.valueOf (VSAttExtractHelper.getAttributeValues (measurement, null, attr, value));
            if (FormatHelper.hasContent (value)) {
                sb.append (",").append (value);
            } else {
                sb.append (",").append ("");
            }
        }

        value = String.valueOf (measurement.getSizeRun ());
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }
        if (measurement.getProductSizeCategory () != null) {
            value = String.valueOf (measurement.getProductSizeCategory ().getBaseSize ());
            if (FormatHelper.hasContent (value)) {
                sb.append (",").append (value);
            } else {
                sb.append (",").append ("");
            }
        }
    }

    public void getRecordByOid (String var2, StringBuilder sb) throws Exception {
        LCSPointsOfMeasure var4 = (LCSPointsOfMeasure) LCSQuery.findObjectById (var2);
        LCSPointsOfMeasure var5 = var4;
        try {
            var5 = (LCSPointsOfMeasure) VersionHelper.latestIterationOf (var4);
        } catch (Exception var13) {
        }
        String value = "";


        value = String.valueOf (var5.getValue ("number"));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }

        value = String.valueOf (var5.getValue ("measurementName"));
        value = String.valueOf (VSAttExtractHelper.getAttributeValues (var5, null, "measurementName", value));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }

        value = String.valueOf (var5.getValue ("criticalPom"));
        value = String.valueOf (VSAttExtractHelper.getAttributeValues (var5, null, "criticalPom", value));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }

        value = String.valueOf (var5.getValue ("section"));
        value = String.valueOf (VSAttExtractHelper.getAttributeValues (var5, null, "section", value));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }

        value = String.valueOf (var5.getValue ("ltdSectionSortOrder"));
        value = String.valueOf (VSAttExtractHelper.getAttributeValues (var5, null, "ltdSectionSortOrder", value));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }

        value = String.valueOf (var5.getValue ("minusTolerance"));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }

        value = String.valueOf (var5.getValue ("plusTolerance"));
        if (FormatHelper.hasContent (value)) {
            sb.append (",").append (value);
        } else {
            sb.append (",").append ("");
        }
    }
}


class VSPSDServiceRemoteWorker extends RemoteWorker {


    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 9030244136773691640L;


    @Override
    public Object doWork (Object arg0) throws Exception {
        String productName = "";
        String[] args = (String[]) arg0;
        for (int i = 0; i < args.length; i++) {


            switch (args[i]) {
                case "-d":
                    if (i + 1 < args.length) {
                        productName = args[++i];
                    }
                    break;
                default:
                    break;
            }
        }
        new VSPSDService ().doTheJob (productName);
        return null;
    }


}
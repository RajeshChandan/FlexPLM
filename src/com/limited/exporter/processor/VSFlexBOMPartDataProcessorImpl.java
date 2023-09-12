package com.limited.exporter.processor;

import com.lcs.wc.db.*;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LineSheetQuery;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.limited.exporter.VSDataExportBean;
import com.limited.exporter.VSType;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wt.log4j.LogR;
import wt.util.WTException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class VSFlexBOMPartDataProcessorImpl implements VSDataProcessor {
    public static final String LCSMATERIALSUPPLIER_BRANCHIDITERATIONINFO = "LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO";
    private static final Logger logger = LogR.getLogger (VSFlexBOMPartDataProcessorImpl.class.getName ());
    private VSDataExportBean bean;
    private StringBuilder bomHeader;
    private int bomHeaderCount = 0;
    private List<StringBuilder> bomData;
    private StringBuilder bomLinkHeader;
    private int bomLinkHeaderCount = 0;
    private List<StringBuilder> bomLinkData;

    @Override
    public void processData (SearchResults results, VSDataExportBean exportBean) throws FileNotFoundException, WTException {
        bean = exportBean;
        String dateRange = "";
        if (FormatHelper.hasContent (bean.getStartDate ()) && FormatHelper.hasContent (bean.getEndDate ())) {
            dateRange = bean.getStartDate () + "_" + bean.getEndDate () + "_";
        }

        String exportFileStr = bean.getThumbnailLoc () + "\\" + bean.getFlexTypeClass () + "_BOMExport_"
                + dateRange + System.currentTimeMillis () + ".csv";

        String bomLinkExportFileStr = bean.getThumbnailLoc () + "\\" + bean.getFlexTypeClass () + "_BOMLINKExport_"
                + dateRange + System.currentTimeMillis () + ".csv";

        results = findActiveSeason (exportBean);
        for (Object obj : results.getResults ()) {
            FlexObject fo = (FlexObject) obj;
            try {
                LCSSeason season = (LCSSeason) LCSQuery
                        .findObjectById ("OR:" + exportBean.getFlexType () + ":" + fo.getString ("LCSSEASON.IDA2A2"));


                exportFileStr = bean.getThumbnailLoc () + "\\" + bean.getFlexTypeClass () + "_" + season.getName () + "_BOMExport_"
                        + dateRange + System.currentTimeMillis () + ".csv";

                bomLinkExportFileStr = bean.getThumbnailLoc () + "\\" + bean.getFlexTypeClass () + "_" + season.getName () + "_BOMLINKExport_"
                        + dateRange + System.currentTimeMillis () + ".csv";

                try (PrintWriter pw = new PrintWriter (new File (exportFileStr));
                     PrintWriter pwBOMLink = new PrintWriter (new File (bomLinkExportFileStr))) {

                    bomHeader = new StringBuilder ();
                    bomHeader.append ("#bom");
                    bomData = new ArrayList<> ();

                    bomLinkHeader = new StringBuilder ();
                    bomLinkHeader.append ("#bomLink");
                    bomLinkData = new ArrayList<> ();

                    bomLinkHeaderCount = 0;
                    bomHeaderCount = 0;

                    extractSeasonProduct (season);

                    pw.println (bomHeader);
                    for (StringBuilder sb : bomData) {
                        pw.println (sb);
                    }

                    pwBOMLink.println (bomLinkHeader);
                    for (StringBuilder sb : bomLinkData) {
                        pwBOMLink.println (sb);
                    }
                }
            } catch (WTException e) {
                logger.error ("", e);
            }
        }
    }


    private void extractSeasonProduct (LCSSeason season) {

        LineSheetQuery lsq = new LineSheetQuery ();

        try {
            Collection<?> vSeasProd = lsq.runSeasonProductReport (season, false, new HashMap<Object, Object> ());
            logger.debug (
                    "total product found ".concat (String.valueOf (vSeasProd.size ())).concat ("for season >>" + season.getName ()));

            for (Object o : vSeasProd) {
                FlexObject obj = (FlexObject) o;
                String prodID = obj.getString ("LCSPRODUCT.idA2A2");
                LCSProduct prod = (LCSProduct) LCSQuery.findObjectById ("OR:com.lcs.wc.product.LCSProduct:" + prodID);
                prod = (LCSProduct) VersionHelper.latestIterationOf (prod.getMaster ());
                logger.debug ("Product name>>" + prod.getName ());
                extractSeasonProductSpec (prod, season);

            }

        } catch (WTException e) {
            logger.error ("", e);
        }

    }

    private void extractSeasonProductSpec (LCSProduct prod, LCSSeason season) {
        try {
            Collection<?> bomParts;
            SearchResults results = FlexSpecQuery.findSpecsByOwner ((LCSPartMaster) prod.getMaster (), season.getMaster (),
                    null, null);

            logger.debug ("total spec found>>>".concat (String.valueOf (results.getResultsFound ()))
                    .concat ("for product>>>" + prod.getName ()));

            for (Object obj : results.getResults ()) {
                FlexObject fo = (FlexObject) obj;
                String specId = fo.getString ("FlexSpecification.branchIditerationInfo");
                if (FormatHelper.hasContent (specId)) {
                    FlexSpecification spec = (FlexSpecification) LCSQuery
                            .findObjectById ("VR:com.lcs.wc.specification.FlexSpecification:" + specId);
                    if (spec == null) {
                        continue;
                    }
                    logger.debug ("FlexSpecification name>>" + spec.getName ());

                    LCSSourcingConfig config = null;

                    config = (LCSSourcingConfig) VersionHelper
                            .latestIterationOf ((LCSSourcingConfigMaster) spec.getSpecSource ());

                    logger.debug ("LCSSourcingConfig name>>" + config.getName ());


                    bomParts = ( new LCSFlexBOMQuery () ).findBOMPartsForOwner (prod, null, null, spec);
                    logger.debug ("bom count>>>" + bomParts.size ());


                    for (Object obj1 : bomParts) {
                        FlexBOMPart bomPart = (FlexBOMPart) obj1;
                        if (bomPart != null) {
                            StringBuilder sb = new StringBuilder ();
                            printData (prod, season, spec, config, bomPart, sb);
                            bomData.add (sb);
                            bomHeaderCount = 1;

                            JSONArray var2 = findBOMLinks (bomPart);
                            printBOMLink (var2, bean);
                        }

                    }
                }
            }

        } catch (WTException e) {
            logger.error ("", e);
        }
    }

    private JSONArray findBOMLinks (FlexBOMPart var14) {
        JSONArray var2 = new JSONArray ();
        String var6 = "ALL_SKUS";
        String var7 = "ALL_DIMENSIONS";
        String var8 = "ALL_SOURCES";
        String var9 = "";
        String var10 = "";

        try {
            SearchResults var15 = LCSFlexBOMQuery.findFlexBOMData (var14, (String) null, (String) null, (String) null, (String) null, (String) null, var10, (Date) null, false, false, var7, var6, var8, var9);
            Collection<?> var16 = var15.getResults ();
            Iterator<?> var17 = var16.iterator ();

            while (var17.hasNext ()) {
                FlexObject var18 = (FlexObject) var17.next ();
                String var19 = getOid (var18);
                JSONObject var20 = getBOMLinkRecordByOid ("BOM Links", var19);

                String var23 = "";
                if (FormatHelper.hasContent (var18.getString (LCSMATERIALSUPPLIER_BRANCHIDITERATIONINFO))) {
                    var23 = geMStOid (var18); //GET MATERAIL SUPPLIER DATA
                } else {
                    var23 = "";
                }

                var20.put ("materialSupplierOid", var23);
                var20.put ("BRANCH", var18.getString ("FLEXBOMLINK.BRANCHID"));
                var20.put ("DIMENSIONID", var18.getString ("FLEXBOMLINK.DIMENSIONID"));
                var20.put ("supplierName", var18.getString ("LCSSUPPLIERMASTER.SUPPLIERNAME"));
                var20.put ("FlexBOMPart", var14.getValue ("ptcbomPartName"));
                var2.add (var20);
            }
        } catch (Exception var26) {
            logger.error ("", var26);
        }

        return var2;
    }

    private void printData (LCSProduct prod, LCSSeason season, FlexSpecification spec, LCSSourcingConfig config
            , FlexBOMPart bomPart, StringBuilder sb) {

        FlexTyped obj = null;
        List<String> attrs;
        for (String objKey : Arrays.asList ("LCSSeason", "LCSProduct", "FlexSpecification", "LCSSourcingConfig", "FlexBOMPart")) {
            attrs = bean.getObjAttrs ().get (objKey);
            if (prod.getClass ().getName ().contains (objKey)) {
                obj = prod;
            }
            if (season.getClass ().getName ().contains (objKey)) {
                obj = season;
            }
            if (spec.getClass ().getName ().contains (objKey)) {
                obj = spec;
            }
            if (config.getClass ().getName ().contains (objKey)) {
                obj = config;
            }
            if (bomPart.getClass ().getName ().contains (objKey)) {
                obj = bomPart;
            }

            try {
                if (obj != null) {
                    logger.debug (objKey + "<<<<>>>>" + attrs + "<<<<>>>>" + obj.getClass ().getName ());
                    getAttData (obj, attrs, bomHeader, objKey, sb);
                }
            } catch (WTException e) {
                logger.error ("", e);
            }
        }
    }

    private void getAttData (FlexTyped obj, List<String> attrs, StringBuilder header, String preFix, StringBuilder sb) throws WTException {
        String temp = "";
        FlexTypeAttribute var9 = null;
        for (String att : attrs) {
            temp = "";
            var9 = obj.getFlexType ().getAttribute (att);
            String var10 = var9.getAttDisplay ();
            String var11 = var9.getAttKey ();
            if (bomHeaderCount == 0) {
                header.append (",").append (preFix).append ("_").append (var10);
            }
            try {
                temp = String.valueOf (obj.getValue (att));
            } catch (WTException e) {
                logger.error (e.getLocalizedMessage () + "<<>>>>" + att);
            }
            logger.debug (att + "<<<>>>>" + var10 + "<<<<>>>" + var11 + "<<<>>>" + temp);
            sb.append (",").append (temp);
        }
    }

    private void printBOMLink (JSONArray var1, VSDataExportBean bean) {
        StringBuilder sb;
        List<String> attrs = bean.getObjAttrs ().get ("FlexBOMLink");
        for (int i = 0; i < var1.size (); i++) {
            Object obj = var1.get (i);
            if (obj == null) {
                continue;
            }
            JSONObject objects = (JSONObject) obj;

            sb = new StringBuilder ();
            for (int j = 0; j < attrs.size (); j++) {
                String key = attrs.get (j);
                if (bomLinkHeaderCount == 0) {
                    bomLinkHeader.append (",").append (key);
                }
                sb.append (",").append (String.valueOf (objects.get (key)));
            }
            bomLinkData.add (sb);
            bomLinkHeaderCount = 1;
        }

    }

    private String getOid (FlexObject var1) {
        return "OR:com.lcs.wc.flexbom.FlexBOMLink:" + var1.getString ("FLEXBOMLINK.IDA2A2");
    }

    private JSONObject getBOMLinkRecordByOid (String var1, String var2) throws WTException {
        JSONObject var3 = new JSONObject ();
        FlexBOMLink var4 = (FlexBOMLink) LCSQuery.findObjectById (var2);

        FlexBOMLink var5 = var4;
        try {
            var5 = (FlexBOMLink) VersionHelper.latestIterationOf (var4);
        } catch (Exception var13) {
        }
        logger.debug ("bomlink var5>>>" + var5);
        var3.put ("createdOn", FormatHelper.applyFormat (var5.getCreateTimestamp (), "DATE_TIME_STRING_FORMAT"));
        var3.put ("modifiedOn", FormatHelper.applyFormat (var5.getModifyTimestamp (), "DATE_TIME_STRING_FORMAT"));
        var3.put ("typeId", FormatHelper.getObjectId (var5.getFlexType ()));
        var3.put ("flexName", var1);
        var3.put ("oid", var2);
        var3.put ("image", (Object) null);
        var3.put ("ORid", String.valueOf (FormatHelper.getObjectId (var5)));
        var3.put ("typeHierarchyName", var5.getFlexType ().getFullNameDisplay (true));
        var3.put ("IDA2A2", FormatHelper.getNumericObjectIdFromObject (var5));
        String var6 = (String) var3.get ("typeHierarchyName");
        var3.put ("hierarchyName", var6.substring (var6.lastIndexOf ("\\") + 1));
        Collection<?> var7 = var5.getFlexType ().getAllAttributes ();
        Iterator<?> var8 = var7.iterator ();

        while (var8.hasNext ()) {
            FlexTypeAttribute var9 = (FlexTypeAttribute) var8.next ();
            String var10 = var9.getAttKey ();
            try {
                if (var5.getValue (var10) == null) {
                    var3.put (var10, "");
                } else {
                    var3.put (var10, var5.getValue (var10).toString ());
                }
            } catch (Exception var12) {
            }
        }

        return var3;
    }

    public String geMStOid (FlexObject var1) {
        String var2 = var1.getString (LCSMATERIALSUPPLIER_BRANCHIDITERATIONINFO);
        String var3 = "";
        if (var2 != null) {
            var3 = "VR:com.lcs.wc.material.LCSMaterialSupplier:" + var1.getString (LCSMATERIALSUPPLIER_BRANCHIDITERATIONINFO);
        } else {
            var3 = "OR:com.lcs.wc.material.LCSMaterialSupplier:" + var1.getString ("LCSMATERIALSUPPLIER.IDA2A2");
        }

        return var3;
    }

    private SearchResults findActiveSeason (VSDataExportBean bean) throws WTException {

        String typeId = "\\" + VSType.getTypeId (bean.getFlexType ());
        if (FormatHelper.hasContent (bean.getFlexSubType ())) {
            typeId = typeId + "\\" + VSType.getTypeId (bean.getFlexSubType ());
        }

        LCSSeason season = null;
        PreparedQueryStatement statement = new PreparedQueryStatement ();
        statement.appendFromTable (LCSSeason.class);
        statement.appendSelectColumn (new QueryColumn (LCSSeason.class, "thePersistInfo.theObjectIdentifier.id"));

        statement.appendAndIfNeeded ();
        String seasonName = FlexTypeCache.getFlexTypeRoot ("Season").getAttribute ("seasonName").getColumnName ();
        statement.appendCriteria (new Criteria (bean.getFlexTypeClass (), seasonName, bean.getName (), Criteria.EQUALS));

        statement.appendAndIfNeeded ();
        statement.appendCriteria (new Criteria (new QueryColumn (LCSSeason.class, "checkoutInfo.state"), "wrk", "<>"));

        statement.appendAndIfNeeded ();
        statement.appendCriteria (new Criteria (new QueryColumn (LCSSeason.class, "iterationInfo.latest"), "1", "="));

        if (typeId != null) {
            statement.appendAndIfNeeded ();
            statement.appendCriteria (new Criteria (bean.getFlexTypeClass (), "flexTypeIdPath", typeId + "%", Criteria.LIKE));
        }

        SearchResults results = LCSQuery.runDirectQuery (statement);

        return results;

    }
}


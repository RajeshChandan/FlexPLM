package com.limited.exporter.processor;

import com.lcs.wc.db.*;
import com.lcs.wc.document.ImagePagePDFGenerator;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentHelper;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeQueryStatement;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LineSheetQuery;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.DownloadURLHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.limited.exporter.VSDataExportBean;
import com.limited.exporter.VSType;
import com.limited.exporter.helper.VSAttExtractHelper;
import com.limited.exporter.helper.VSPDFHelper;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class VSSpecImagePageDataProcessorImpl implements VSDataProcessor {
    private static final Logger logger = LogR.getLogger (VSSpecImagePageDataProcessorImpl.class.getName ());

    public static final String DEFAULT_ENCODING = LCSProperties.get ("com.lcs.wc.util.CharsetFilter.Charset", "UTF-8");
    private VSDataExportBean bean;

    @Override
    public void processData (SearchResults results, VSDataExportBean exportBean) throws FileNotFoundException, WTException {
        bean = exportBean;
        String dateRange = "";
        if (FormatHelper.hasContent (bean.getStartDate ()) && FormatHelper.hasContent (bean.getEndDate ())) {
            dateRange = bean.getStartDate () + "_" + bean.getEndDate () + "_";
        }

        SearchResults searchResults = findActiveSeason (exportBean);
        for (Object obj : searchResults.getResults ()) {
            FlexObject fo = (FlexObject) obj;
            try {
                LCSSeason season = (LCSSeason) LCSQuery
                        .findObjectById ("OR:" + exportBean.getFlexType () + ":" + fo.getString ("LCSSEASON.IDA2A2"));

                extractSeasonProduct (season);

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

                    List<LCSDocument> imagePages = getImagePages (prod, spec);
                    logger.debug ("Image Pages count>>>" + imagePages.size ());


                    for (LCSDocument obj1 : imagePages) {

                        if (obj1 != null) {

                            Map<String, String> data = new HashMap<> ();

                            printData (prod, season, spec, config, obj1, data);


                            Map<String, byte[]> content = getContent (obj1);

                            String name = "";
                            name = name.concat (prod.getName ()).concat ("_");
                            if (config != null) {
                                name = name.concat (config.getSourcingConfigName ()).concat ("_");
                            }
                            name = name.concat (spec.getName ()).concat ("_");
                            name = name.concat (obj1.getName ());
                            name = name.replaceAll ("[^a-zA-Z0-9_ -]", "");
                            String fileName = bean.getThumbnailLoc () + "\\" + name + "_" + System.currentTimeMillis () + ".pdf";


                            try {
                                new VSPDFHelper ().printPDF (fileName, data, content);
                            } catch (Exception e) {
                                logger.error (spec.getName () + "<<>>>" + obj1.getName () + "<<>>" + e.getLocalizedMessage (), e);
                            }
                        }

                    }
                }
            }

        } catch (WTException e) {
            logger.error ("", e);
        }
    }

    private void printData (LCSProduct prod, LCSSeason season, FlexSpecification spec, LCSSourcingConfig config
            , LCSDocument doc, Map<String, String> data) {

        FlexTyped obj = null;
        List<String> attrs;
        for (String objKey : Arrays.asList ("LCSSeason", "LCSProduct", "FlexSpecification", "LCSSourcingConfig", "FlexBOMPart", "LCSDocument")) {
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
            if (LCSDocument.class.getName ().contains (objKey)) {
                obj = doc;
            }

            try {
                if (obj != null && attrs != null) {
                    getAttData (obj, attrs, objKey, data);
                }
            } catch (WTException e) {
                logger.error ("", e);
            }
        }
    }

    private void getAttData (FlexTyped obj, List<String> attrs, String preFix, Map<String, String> data) throws WTException {
        String temp = "";
        FlexTypeAttribute var9 = null;
        for (String att : attrs) {
            temp = "";
            var9 = obj.getFlexType ().getAttribute (att);
            String var10 = var9.getAttDisplay ();
            String var11 = var9.getAttKey ();
            try {
                temp = String.valueOf (obj.getValue (att));
                temp = String.valueOf (VSAttExtractHelper.getAttributeValues (obj, null, att, temp));
                if (!FormatHelper.hasContent (temp)) {
                    temp = "";
                }
            } catch (WTException | WTPropertyVetoException e) {
                logger.error (e.getLocalizedMessage () + "<<>>>>" + att, e);
            }
            data.put (var10, temp);
        }
    }

    private Map<String, byte[]> getContent (LCSDocument doc) {

        Map<String, byte[]> data = new HashMap<> ();

        LCSDocument var5 = null;
        try {
            var5 = (LCSDocument) ContentHelper.service.getContents (doc);

            List<?> var31 = new ArrayList<> (ContentHelper.getApplicationData (var5));
            URL var29 = null;
            String var30 = "";
            if (!var31.isEmpty ()) {
                for (int var33 = 0; var33 < var31.size (); ++var33) {

                    try {
                        ApplicationData var35 = (ApplicationData) var31.get (var33);
                        InputStream stream = ContentServerHelper.service.findContentStream (var35);
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        ByteArrayOutputStream output = new ByteArrayOutputStream ();
                        while (( bytesRead = stream.read (buffer) ) != -1) {
                            output.write (buffer, 0, bytesRead);
                        }
                        String var26 = var35.getFileName ();
                        data.put (var26, output.toByteArray ());
                    } catch (Exception e) {
                        logger.error (doc.getName () + "<<>>" + e.getLocalizedMessage (), e);
                    }

                }
            }
        } catch (WTException | PropertyVetoException e) {
            logger.error ("", e);
        }
        logger.debug ("data>>>>" + data);
        return data;
    }

    private List<LCSDocument> getImagePages (LCSProduct paramLCSProduct, FlexSpecification paramFlexSpecification) throws
            WTException {
        List<LCSDocument> localArrayList = new ArrayList<> ();
        try {
            String str1 = FormatHelper.getObjectId ((LCSPartMaster) paramLCSProduct.getMaster ());
            String str2 = null;
            if (paramFlexSpecification != null) {
                str2 = FormatHelper.getObjectId (paramFlexSpecification.getMaster ());
            }

            FlexTypeQueryStatement localFlexTypeQueryStatement = new LCSProductQuery ().getProductImagesQuery (str1, str2);

            Collection<?> localVector = LCSQuery.runDirectQuery (localFlexTypeQueryStatement).getResults ();

            Iterator<?> localIterator = localVector.iterator ();
            FlexObject localFlexObject = null;

            while (localIterator.hasNext ()) {
                localFlexObject = (FlexObject) localIterator.next ();
                LCSDocument var11 = (LCSDocument) LCSQuery.findObjectById ("VR:com.lcs.wc.document.LCSDocument:" + localFlexObject.get ("LCSDOCUMENT.BRANCHIDITERATIONINFO"));

                if (var11 != null) {
                    localArrayList.add (var11);
                }
            }


        } catch (Exception localException) {
            localException.printStackTrace ();
        }
        return localArrayList;
    }

    private SearchResults findActiveSeason (VSDataExportBean bean) throws WTException {

        String typeId = "\\" + VSType.getTypeId (bean.getFlexType ());
        if (FormatHelper.hasContent (bean.getFlexSubType ())) {
            typeId = typeId + "\\" + VSType.getTypeId (bean.getFlexSubType ());
        }

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

        statement.appendAndIfNeeded ();
        statement.appendCriteria (new Criteria (bean.getFlexTypeClass (), "flexTypeIdPath", typeId + "%", Criteria.LIKE));

        return LCSQuery.runDirectQuery (statement);


    }
}


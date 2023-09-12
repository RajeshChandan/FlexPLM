package com.limited;

import com.lcs.wc.util.MultiCharDelimStringTokenizer;
import com.limited.exporter.VSDataExportBean;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class test {
    private static final String MOA_DELIM = "|-*-|";

    public static void main (String[] args) {

        System.out.println (new DecimalFormat ("#.####").format (255555.544444));

        Map<Date, Integer> m = new HashMap<Date, Integer> ();

        DateFormat dateFormat = new SimpleDateFormat ("dd-MM-yyyy");

        try {
            m.put (new java.sql.Date (dateFormat.parse ("31-05-2011").getTime ()), 67);
            m.put (new java.sql.Date (dateFormat.parse ("01-06-2011").getTime ()), 89);
            m.put (new java.sql.Date (dateFormat.parse ("10-06-2011").getTime ()), 56);
            m.put (new java.sql.Date (dateFormat.parse ("25-05-2011").getTime ()), 34);

            TreeMap<Date, Integer> m1 = new TreeMap (m);
            DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");

            System.out.println (df.format (m1.lastEntry ().getKey ()));
        } catch (ParseException e) {
            throw new RuntimeException (e);
        }


        String html = "XS*V";
        html = html.replaceAll ("/*", ",");
        System.out.println (html);


        test test = new test ();
        String line = "LCSColor~01-JAN-2021|01-AUG-2021~com.lcs.wc.color.LCSColor|com.limitedbrands.Solid_Color~ltdCode~name~thumbnail|\\\\wnflexmq101\\d$\\temp3\\DataExtraction\\Color\\solid";
        //line = "01-01-2005|31-12-2010~com.lcs.wc.season.LCSSeason~name,status~thumbnail|D";
        //line = "FlexBOMPart~2023 Summer HN SLEEP PINK~com.lcs.wc.season.LCSSeason~LCSSeason-seasonName,ltdSeasonObjectID|-*-|LCSProduct-productName,ltdProductCode|-*-|FlexSpecification-specName,ltdSpecificationObjectID|-*-|LCSSourcingConfig-ltdSourcingConfigObjectID|-*-|FlexBOMPart-ptcbomPartName,ltdStatus,ltdVendorBOMStatus,ltdBOMPartObjectID,pmDescription,pmQuantity,pmUOM,pmTotal|-*-|FlexBOMLink-FlexBOMPart,section,partName,ltdPartType,ltdComponentDescription,materialDescription,supplierName,flexName,placement,hierarchyName,ptcbomPartMarkUp,ltdBOMComments,quantity,rowTotal~thumbnail|\\\\wnflexmq101\\d$\\temp3\\DataExtraction\\Season";

        //line = "Image Page~2009 Fall VSD ACCESSORIES VICTORIA'S SECRET~com.lcs.wc.season.LCSSeason~LCSSeason-seasonName|-*-|LCSProduct-productName|-*-|FlexSpecification-specName|-*-|LCSDocument-name,ptcdocumentName,pageDescription,textLayout,pageType,pageLayout,ltdStatus~thumbnail|D:\\ptc\\Windchill_10.2\\Windchill\\";
        line = "LCSMaterialSupplier~2009 Fall VSD ACCESSORIES VICTORIA'S SECRET~com.lcs.wc.season.LCSSeason~LCSMaterialSupplier-seasonName|-*-|LCSProduct-productName|-*-|FlexSpecification-specName|-*-|LCSDocument-name,ptcdocumentName,pageDescription,textLayout,pageType,pageLayout,ltdStatus~thumbnail|D:\\ptc\\Windchill_10.2\\Windchill\\";

        VSDataExportBean bean = test.readLineData (line);
        System.out.println (bean);
    }

    private VSDataExportBean readLineData (String line) {

        String[] row = line.split ("~");
        VSDataExportBean bean = new VSDataExportBean ();
        for (int i = 0; i < row.length; i++) {
            //logic for object name or date range
            switch (i) {
                case 0:
                    bean.setExtraction (row[i]);
                    break;
                case 1:
                    bean.setName (row[i]);
                    if (row[i].contains ("|")) {
                        bean.setStartDate (row[i].split ("\\|")[0]);
                        bean.setEndDate (row[i].split ("\\|")[1]);
                        bean.setName ("");
                    }
                    break;

                case 2:
                    bean.setFlexType (row[i].split ("\\|")[0]);
                    if (row[i].contains ("|")) {
                        bean.setFlexSubType (row[i].split ("\\|")[1]);
                    }
                    break;

                default:
                    processAttrs (row[i], bean);
                    break;

            }
        }

        return bean;
    }

    private void processAttrs (String subLine, VSDataExportBean bean) {

        if (isThumbnail (subLine)) {
            bean.setThumbnailLoc (getThumbnailLoc (subLine));
        }

        if (!isThumbnail (subLine) && !subLine.contains (MOA_DELIM)) {
            bean.setAttrs (Arrays.asList (subLine.split (",")));
        }

        if (!isThumbnail (subLine) && subLine.contains (MOA_DELIM)) {
            Map<String, List<String>> objAttrsMap = new HashMap<> ();

            StringTokenizer tokenizer = new MultiCharDelimStringTokenizer (subLine, MOA_DELIM);

            while (tokenizer.hasMoreTokens ()) {
                String token = tokenizer.nextToken ();

                String[] var0 = token.split ("-");
                objAttrsMap.put (var0[0], new ArrayList<> (Arrays.asList (var0[1].split (","))));
            }
            bean.setObjAttrs (objAttrsMap);
        }
    }

    private boolean isThumbnail (String str) {
        return str.startsWith ("thumbnail");
    }

    private String getThumbnailLoc (String str) {
        String loc = "";
        if (isThumbnail (str)) {
            loc = str.split ("\\|")[1];
        }
        return loc;
    }
}

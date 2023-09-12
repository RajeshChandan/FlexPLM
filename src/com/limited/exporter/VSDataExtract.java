package com.limited.exporter;

import com.lcs.wc.db.SearchResults;
import com.lcs.wc.util.MultiCharDelimStringTokenizer;
import com.limited.exporter.processor.VSDataProcessor;
import com.limited.exporter.processor.VSFlexBOMPartDataProcessorImpl;
import com.limited.exporter.processor.VSLCSColorDataProcessorImpl;
import com.limited.exporter.processor.VSSpecImagePageDataProcessorImpl;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.io.IOException;
import java.util.*;

public class VSDataExtract {

    private static final String MOA_DELIM = "|-*-|";
    private static final Logger logger = LogR.getLogger (VSDataExtract.class.getName ());

    public static void extractData (String line) throws WTException {
        new VSDataExtract ().execute (line);
    }

    private void execute (String line) throws WTException {
        VSDataExportBean bean = readLineData (line);

        logger.debug ("line data>>>>" + bean.toString ());
        SearchResults results = null;

        try {
            VSDataProcessor processor = null;

            switch (bean.getExtraction ()) {
                case "LCSColor":
                    results = new VSDataUtil ().extractDbRecords (bean);
                    processor = new VSLCSColorDataProcessorImpl ();
                    break;

                case "FlexBOMPart":
                    processor = new VSFlexBOMPartDataProcessorImpl ();
                    break;

                case "Image Page":
                    processor = new VSSpecImagePageDataProcessorImpl ();
                    break;

                default:

                    break;
            }
            if (processor != null) {
                processor.processData (results, bean);
            }
        } catch (IOException e) {
            logger.error ("", e);
        }
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

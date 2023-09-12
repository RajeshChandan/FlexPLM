package com.limited.exporter.processor;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.limited.exporter.VSDataExportBean;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class VSLCSColorDataProcessorImpl implements VSDataProcessor {

    private static final Logger logger = LogR.getLogger(VSLCSColorDataProcessorImpl.class.getName());

    @Override
    public void processData(SearchResults results, VSDataExportBean bean) throws FileNotFoundException, WTException {
        String exportFileStr = bean.getThumbnailLoc() + "\\" + bean.getFlexTypeClass() + "_Export_"
                + bean.getStartDate() + "_" + bean.getEndDate() + "_" + System.currentTimeMillis() + ".csv";
        String temp = "";
        try (PrintWriter pw = new PrintWriter(new File(exportFileStr))) {
            StringBuilder sb = null;
            String header = "#Color Code".concat(",").concat("Color Name").concat("File Name");
            pw.println(header);
            for (Object obj : results.getResults()) {
                FlexObject fo = (FlexObject) obj;
                try {
                    LCSColor color = (LCSColor) LCSQuery
                            .findObjectById("OR:" + bean.getFlexType() + ":" + fo.getString("LCSCOLOR.IDA2A2"));
                    // color = (LCSColor) VersionHelper.latestIterationOf(color);
                    sb = new StringBuilder();

                    for (String attr : bean.getAttrs()) {
                        temp = String.valueOf(color.getValue(attr));
                        sb.append(temp).append(',');
                    }
                    if (bean.getThumbnailLoc() != null && bean.getThumbnailLoc().length() > 2) {
                        sb.append(findThumbnailData(color, bean.getThumbnailLoc()));
                    }

                    pw.println(sb.toString());
                } catch (WTException e) {
                    logger.error("", e);
                }
            }
        }
    }

    public String findThumbnailData(LCSColor var1, String loc) {
        String var2 = "";
        String var6 = "";
        File var15 = null;
        String var16 = "";
        try {
            var6 = var1.getThumbnail();
            logger.debug("getThumbnail>>" + var6);

            if (FormatHelper.hasContent(var6)) {
                logger.debug("it has>>");
                var6 = var6.trim();
                String var12 = var1.getThumbnail();
                String var13 = "/images/";
                int var14 = var12.lastIndexOf(var13);
                if (var14 > -1) {
                    var12 = var12.substring(var14 + var13.length());
                }

                var12 = FileLocation.imageLocation.concat(File.separator).concat(var12);
                logger.debug("image file location>>" + var12);
                var15 = new File(var12);
                var16 = var15.getName();
                if (var15.exists() && !var15.isDirectory()) {
                    logger.debug("FILE EXIST>>>>" + var16);
                    var2 = loc.concat(File.separator).concat("images").concat(File.separator).concat(var16);
                    FileUtils.copyFile(var15, new File(var2));
                    logger.debug("FILE sucessfully copied>>>>" + var2);
                }

            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return var16;
    }
}

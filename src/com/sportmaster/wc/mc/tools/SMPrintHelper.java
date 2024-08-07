package com.sportmaster.wc.mc.tools;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.Collection;

public class SMPrintHelper {

    private static final Logger LOGGER = Logger.getLogger(SMPrintHelper.class);

    public static void printInfoSMTotalUSD(String msg, LCSMOAObject lcsmoaObject) throws WTException {
        double smTotal = SMFormatHelper.getDouble( lcsmoaObject.getValue("smTotal") );
        double smTotalSM = SMFormatHelper.getDouble( lcsmoaObject.getValue("smTotalSM") );
        if (smTotal != 0 || smTotalSM != 0) {
            String section = FormatHelper.format( (String) lcsmoaObject.getValue("smSectionAPD") );
            double smMSPrice = SMFormatHelper.getDouble( lcsmoaObject.getValue("smMSPrice") );
            String smMSCurrency = FormatHelper.format( (String) lcsmoaObject.getValue("smMSCurrency") );
            double smTotalUSD = SMFormatHelper.getDouble( lcsmoaObject.getValue("smTotalUSD") );
            double smTotalUSDSM = SMFormatHelper.getDouble( lcsmoaObject.getValue("smTotalUSDSM") );
            LOGGER.info("CUSTOM>>>>>> " + msg + ": Section: " + section +
                    ", smMSCurrency: " + smMSCurrency + ", smMSPrice: " + smMSPrice + ", smTotal: " +
                    smTotal + ", smTotalSM: " + smTotalSM + ", smTotalUSD: " + smTotalUSD + ", smTotalUSDSM: " + smTotalUSDSM);
        }
    }

    public static void print(String msg, Collection<FlexObject> collection, String attrInternalName) {
        for (FlexObject flexObject : collection) {
            LOGGER.info("CUSTOM>>>>>> " + msg + ": " + attrInternalName + ": " + FormatHelper.format( flexObject.getData(attrInternalName) ));
        }
    }

    public static void print(Exception exception) {
        for (StackTraceElement element : exception.getStackTrace()) {
            LOGGER.error("CUSTOM>>>>>> " + element.toString() );
        }
    }

    public static void print(String msg, Collection collection) {
        for (Object obj : collection) {
            LOGGER.info("CUSTOM>>>>>> " + msg + ": " + obj);
        }
    }
}

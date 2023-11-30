/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl;

import com.hbi.etl.dao.HbiEtlTracker;
import com.hbi.etl.extractors.PLMBusinessObjectExport;
import com.hbi.etl.extractors.PLMColorExport;
import com.hbi.etl.extractors.PLMPaletteExport;
import com.hbi.etl.extractors.PLMColorToPaletteExport;
import com.hbi.etl.extractors.PLMCountryExport;
import com.hbi.etl.extractors.PLMLogEntryQuery;
import com.hbi.etl.extractors.PLMMOAObjectExport;
import com.hbi.etl.extractors.PLMMaterialExport;
import com.hbi.etl.extractors.PLMMaterialSupplierExport;
import com.hbi.etl.extractors.PLMSupplierExport;
import com.hbi.etl.loaders.PLMBusinessObjectLoader;
import com.hbi.etl.loaders.PLMColorLoader;
import com.hbi.etl.loaders.PLMPaletteLoader;
import com.hbi.etl.loaders.PLMColorToPaletteLoader;
import com.hbi.etl.loaders.PLMCountryLoader;
import com.hbi.etl.loaders.PLMETLTrackerLoader;
import com.hbi.etl.loaders.PLMMaterialLoader;
import com.hbi.etl.loaders.PLMMaterialSupplierLoader;
import com.hbi.etl.loaders.PLMMoaObjectLoader;
import com.hbi.etl.loaders.PLMSupplierLoader;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.transformer.PLMETLTransformer;
import com.hbi.etl.util.DatabaseUtil;
import com.hbi.etl.util.PLMETLException;
import com.hbi.etl.util.PropertyUtil;
import com.hbi.stg.extractors.util.HbiExtractorUtil;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.util.LCSProperties;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.session.SessionContext;
import java.io.Serializable;

/**
 *
 * @author UST
 */
public class PLMETLProcessor implements PLMETLFramework, RemoteAccess, Serializable {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMETLProcessor.class, logLevel);
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "Administrator");
	//private static RemoteMethodServer remoteMethodServer;

    //@Override
    public static void exportFull() throws ParseException, PLMETLException {

        etlLogger.info("Start ETLProcessor job in Full mode");
        PropertyUtil propertyUtil = new PropertyUtil();
        String fullModeStartDate = propertyUtil.getDateForFullMode();
        int fullModeRange = propertyUtil.getMonthRangeForFullMode();
        Vector<Object> FMDates = null;
        Date logDate = new Date();
        Date startDate = new Date();
        Date endDate = new Date();
        String objType = "Supplier";
        int objTypeInt=1;
        //int cnt = 0;
        FMDates = propertyUtil.getFullModeDates();
        PLMETLTrackerLoader tLoad = new PLMETLTrackerLoader();
        HbiEtlTracker etlTracker = etlTracker = tLoad.loadTracker("FR", new Timestamp(logDate.getTime()));
        if (FMDates != null) {
            startDate = (Date) FMDates.get(0);
            endDate = (Date) FMDates.get(1);
            objType = (String) FMDates.get(2);
            //Added for codeUpgarde- Due to Java version  1.6
            if(objType.equalsIgnoreCase("Supplier")){
            	objTypeInt = 1;
            }else if(objType.equalsIgnoreCase("BO")){
            	objTypeInt = 2;
            }else if(objType.equalsIgnoreCase("Material")){
            	objTypeInt = 3;
            }else if(objType.equalsIgnoreCase("MaterialSupplier")){
            	objTypeInt = 4;
            }else if(objType.equalsIgnoreCase("Color")){
            	objTypeInt = 5;
            }else if(objType.equalsIgnoreCase("Palette")){
            	objTypeInt = 6;
            }else if(objType.equalsIgnoreCase("ColorToPalette")){
            	objTypeInt = 7;
            }else if(objType.equalsIgnoreCase("Country")){
            	objTypeInt = 8;
            }else if(objType.equalsIgnoreCase("MOA")){
            	objTypeInt = 9;
            }
            
            //
            FMDates = null;
            propertyUtil.setFullModeDates(FMDates);
        } else {
            try {
                startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                etlLogger.info("Truncating TransDB Tables...");
                truncate();
                etlLogger.info("Truncation complete");
            } catch (Exception ex) {
                throw new PLMETLException("Truncate Failed!!", ex, logDate, tLoad, etlTracker);
            }
        }
        
        try {
            switch (objTypeInt) {
               // case "Supplier":
            case 1:
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMSupplierExport plmSupplierExport = new PLMSupplierExport();
                            Vector exportList = plmSupplierExport.exportFull(startDate, endDate);
                            if (!exportList.isEmpty()) {
                                PLMETLTransformer tObj = new PLMETLTransformer();
                                Vector trList = tObj.tranform(exportList, "LCSSupplier");
                                PLMSupplierLoader sLoad = new PLMSupplierLoader();
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("Supplier");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in Supplier Load:", ex, logDate, tLoad, etlTracker);
                        }
                    }
                case 2:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMBusinessObjectExport plmBusinessObjectExport = new PLMBusinessObjectExport();
                            Vector exportList = plmBusinessObjectExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSBusinessObject");
                                PLMBusinessObjectLoader sLoad = new PLMBusinessObjectLoader();
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }

                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("BO");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in BO Load:", ex, logDate, tLoad, etlTracker);
                        }                      
                    }
                case 3:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMMaterialExport plmMaterialExport = new PLMMaterialExport();
                            Vector exportList = plmMaterialExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSMaterial");                             
                                PLMMaterialLoader sLoad = new PLMMaterialLoader();
                               
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("Material");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in Material Load:", ex, logDate, tLoad, etlTracker);
                        }
                    }
                case 4:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }

                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMMaterialSupplierExport plmMaterialSupplierExport = new PLMMaterialSupplierExport();
                            Vector exportList = plmMaterialSupplierExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSMaterialSupplier");                                    
                                PLMMaterialSupplierLoader sLoad = new PLMMaterialSupplierLoader();
                                
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("MaterialSupplier");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in MaterialSupplier Load:", ex, logDate, tLoad, etlTracker);
                        }

                    }
                case 5:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMColorExport plmColorExport = new PLMColorExport();
                            Vector exportList = plmColorExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSColor");                                   
                                PLMColorLoader sLoad = new PLMColorLoader();
                                
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("Color");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in Color Load:", ex, logDate, tLoad, etlTracker);
                        }
                    }
                case 6:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMPaletteExport plmPaletteExport = new PLMPaletteExport();
                            Vector exportList = plmPaletteExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSPalette");                                   
                                PLMPaletteLoader sLoad = new PLMPaletteLoader();
                                
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("Palette");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in Palette Load:", ex, logDate, tLoad, etlTracker);
                        }
                    }
                case 7:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMColorToPaletteExport plmColorToPaletteExport = new PLMColorToPaletteExport();
                            Vector exportList = plmColorToPaletteExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSColorToPalette");                                   
                                PLMColorToPaletteLoader sLoad = new PLMColorToPaletteLoader();
                                
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("ColorToPalette");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in ColorToPalette Load:", ex, logDate, tLoad, etlTracker);
                        }
                    }
                case 8:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMCountryExport plmCountryExport = new PLMCountryExport();
                            Vector exportList = plmCountryExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSCountry");                                   
                                PLMCountryLoader sLoad = new PLMCountryLoader();
                                
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }                         
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("Country");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in Country Load:", ex, logDate, tLoad, etlTracker);
                        }

                    }
                case 9:
                    if (FMDates != null) {
                        startDate = (Date) FMDates.get(0);
                        endDate = (Date) FMDates.get(1);
                        FMDates = null;
                        propertyUtil.setFullModeDates(FMDates);
                    } else {
                        startDate = new SimpleDateFormat("MMddyyyy").parse(fullModeStartDate);
                        endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                    }
                    while (endDate.compareTo(Calendar.getInstance().getTime()) <= 0) {

                        try {
                            if (startDate.compareTo(endDate) == 0) {
                                break;
                            }
                            PLMMOAObjectExport plmMOAObjectExport = new PLMMOAObjectExport();
                            Vector exportList = plmMOAObjectExport.exportFull(startDate, endDate);
                            PLMETLTransformer tObj = new PLMETLTransformer();

                            if (!exportList.isEmpty()) {
                                Vector trList = tObj.tranform(exportList, "LCSMOAObject");                                   
                                PLMMoaObjectLoader sLoad = new PLMMoaObjectLoader();
                               
                                sLoad.load(trList);
                            }
                            startDate = endDate;
                            endDate = propertyUtil.getNewDateForFull(startDate, fullModeRange);
                            if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                endDate = dateFormat.parse(dateFormat.format(new Date()));
                            }
                           
                        } catch (WTException ex) {
                            FMDates = new Vector();
                            FMDates.add(startDate);
                            FMDates.add(endDate);
                            FMDates.add("MOA");
                            propertyUtil.setFullModeDates(FMDates);
                            throw new PLMETLException("Error in MOA Load:", ex, logDate, tLoad, etlTracker);
                        }
                    }
                    break;
            }
            tLoad.updateTracker(etlTracker, new Timestamp(endDate.getTime()), "SUCCESS");

            //PLMETLLogEntry logTr = new PLMETLLogEntry();
            //logTr.logTransaction(new Timestamp(endDate.getTime()), "FR", true);
            propertyUtil.deleteFile();
            System.exit(0);
        } catch (WTException ex) {
            throw new PLMETLException("Error in Full Load:", ex, logDate, tLoad, etlTracker);
        }

    }

    public static void exportIncr() throws Exception, WTException, WTPropertyVetoException {

        etlLogger.info("Start ETLProcessor job in Incr mode");
   /*   Not using Log Entry to get startDate
        PLMLogEntryQuery plmLogEntryQuery = new PLMLogEntryQuery();
        LCSLogEntry lcsLogEntry = plmLogEntryQuery.getLogEntry();
        Date startDate = new Timestamp(lcsLogEntry.getModifyTimestamp().getTime());
             
      */       
        HbiExtractorUtil util = new HbiExtractorUtil();
        HbiEtlTracker deltaTracker = util.getDeltaETLTracker();
        if (deltaTracker == null) {
                etlLogger.info("Warning !!! Cannot find time in HBIETLTRACKER to extract data to Staging");
                return;

            }
       
        
        Date startDate = deltaTracker.getLoaderupdatetime();
        etlLogger.info("Extract begin Time  = " + startDate);
        Date endDate = new Date();       
        PLMETLTrackerLoader tLoad = new PLMETLTrackerLoader();
        HbiEtlTracker etlTracker = tLoad.loadTracker("DE", startDate);

        try {

            PLMSupplierExport plmSupplierExport = new PLMSupplierExport();
            Vector exportListSupplier = plmSupplierExport.exportIncr(startDate, new Timestamp(endDate.getTime())); 
        
            if (!exportListSupplier.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListSupplier, "LCSSupplier");
                PLMSupplierLoader sLoad = new PLMSupplierLoader();
                sLoad.load(trList);
            } else {
                etlLogger.debug("Exported supplier objects for the specified date range is emtpy");
            }
            PLMMaterialExport plmMaterialExport = new PLMMaterialExport();
            Vector exportListMaterial = plmMaterialExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
        
            if (!exportListMaterial.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListMaterial, "LCSMaterial");
                PLMMaterialLoader sLoad = new PLMMaterialLoader();
                
                sLoad.load(trList);
            }
            PLMMaterialSupplierExport plmMaterialSupplierExport = new PLMMaterialSupplierExport();
            Vector exportListMaterialSupp = plmMaterialSupplierExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListMaterialSupp.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListMaterialSupp, "LCSMaterialSupplier");
                PLMMaterialSupplierLoader sLoad = new PLMMaterialSupplierLoader();
               
                sLoad.load(trList);
            }
            PLMColorExport plmColorExport = new PLMColorExport();
            Vector exportListColor = plmColorExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListColor.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListColor, "LCSColor");
                PLMColorLoader sLoad = new PLMColorLoader();
                
                sLoad.load(trList);
            }
            PLMPaletteExport plmPaletteExport = new PLMPaletteExport();
            Vector exportListPalette = plmPaletteExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListPalette.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListPalette, "LCSPalette");
                PLMPaletteLoader sLoad = new PLMPaletteLoader();
                
                sLoad.load(trList);
            }
            PLMColorToPaletteExport plmColorToPaletteExport = new PLMColorToPaletteExport();
            Vector exportListColorToPalette = plmColorToPaletteExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListColorToPalette.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListColorToPalette, "LCSColorToPalette");
                PLMColorToPaletteLoader sLoad = new PLMColorToPaletteLoader();
                
                sLoad.load(trList);
            }
            PLMCountryExport plmCountryExport = new PLMCountryExport();
            Vector exportListCountry = plmCountryExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListCountry.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListCountry, "LCSCountry");
                PLMCountryLoader sLoad = new PLMCountryLoader();
                
                sLoad.load(trList);
            }
            PLMBusinessObjectExport plmBusinessObjectExport = new PLMBusinessObjectExport();
            Vector exportListBusinessObj = plmBusinessObjectExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListBusinessObj.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListBusinessObj, "LCSBusinessObject");
                PLMBusinessObjectLoader sLoad = new PLMBusinessObjectLoader();
                
                sLoad.load(trList);
            }
            PLMMOAObjectExport plmMOAObjectExport = new PLMMOAObjectExport();
            Vector exportListMOAObj = plmMOAObjectExport.exportIncr(startDate, new Timestamp(endDate.getTime()));
            if (!exportListMOAObj.isEmpty()) {
                PLMETLTransformer tObj = new PLMETLTransformer();
                Vector trList = tObj.tranform(exportListMOAObj, "LCSMOAObject");
                PLMMoaObjectLoader sLoad = new PLMMoaObjectLoader();
                
                sLoad.load(trList);
            }

            //PLMETLLogEntry logTr = new PLMETLLogEntry();
            //logTr.logTransaction(new Timestamp(endDate.getTime()), "DE", true);
            tLoad.updateTracker(etlTracker, endDate, "SUCCESS");

        } catch (WTException ex) {
            throw new PLMETLException("Error in Incremental Load:", ex, endDate, tLoad, etlTracker);
        } catch (Exception ex1) {
            throw new PLMETLException("Error in Incremental Load:", ex1, endDate, tLoad, etlTracker);
        }

    }

    @Override
    public String getBeginTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLastSuccessLogEntryTimestamp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String args[]) {

		try{
				String mode = args[0];
 
				MethodContext mcontext = new MethodContext((String) null, (Object) null);
				SessionContext sessioncontext = SessionContext.newContext();
				RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
				//GatewayAuthenticator authenticator = new GatewayAuthenticator();
				//authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
				//authenticator.setRemoteUser("prodadmin");
				//remoteMethodServer.setUserName("prodadmin");
				//remoteMethodServer.setPassword("admin2021"); //.setAuthenticator(authenticator);
				/*Object[] obj = null;
				Class[] cls = null;
				remoteMethodServer.invoke("exportIncr", PLMETLProcessor.class.getName(), null,cls , obj);
					*/	
				
				GatewayAuthenticator auth = new GatewayAuthenticator();
				//auth.setRemoteUser(CLIENT_ADMIN_USER_ID);
				auth.setRemoteUser("prodadmin");
				remoteMethodServer.setAuthenticator(auth);
				
								
			/*if (mode.equals("FR")) {
					//PLMETLProcessor plmETLProcessor = new PLMETLProcessor();
					// plmETLProcessor.exportFull();
					//Class[] argumentClass = {};
					//Object[] argumentObject = {};
					//Updated in 10.1 system-invoking method from RMI
					//remoteMethodServer.invoke("exportFull", null, plmETLProcessor, null, null);
					exportFull();
					System.exit(0);
					}*/
			if (mode.equals("DE")) {
					//PLMETLProcessor plmETLProcessor = new PLMETLProcessor();
					Class[] argumentClass = {};
					Object[] argumentObject = {};
					//Updated in 10.1 system-invoking method from RMI
					//remoteMethodServer.setUserName("prodadmin");
					remoteMethodServer.invoke("exportIncr", "com.hbi.etl.PLMETLProcessor", null, argumentClass, argumentObject);
					//exportIncr();
					System.exit(0);
					}	
		
			}catch (Exception exception) 
				{
					exception.printStackTrace();
					System.exit(1);
				}

    }

    private static void truncate() throws PLMETLException {
        DatabaseUtil dbUtil = new DatabaseUtil();
        dbUtil.truncateTable("PLMSTG.HBISUPPLIER");
        dbUtil.truncateTable("PLMSTG.HBIBUSINESSOBJECT");
        dbUtil.truncateTable("PLMSTG.HBIMATERIAL");
        dbUtil.truncateTable("PLMSTG.HBIMATERIALSUPPLIER");
        dbUtil.truncateTable("PLMSTG.HBICOLOR");
        dbUtil.truncateTable("PLMSTG.HBIPALETTE");
        dbUtil.truncateTable("PLMSTG.HBIPALETTETOCOLORLINK");
        dbUtil.truncateTable("PLMSTG.HBICOUNTRY");
        dbUtil.truncateTable("PLMSTG.HBIMOAOBJECT");
    }

}

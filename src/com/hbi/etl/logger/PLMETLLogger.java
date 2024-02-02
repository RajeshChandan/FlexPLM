/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.logger;

import com.hbi.etl.util.PLMETLException;
import com.lcs.wc.util.LCSProperties;
import java.io.File;
import java.util.Calendar;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Sandeep Kureel
 *
 */
public class PLMETLLogger extends Logger {

    private static String strFileName = "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\ETL.log";
    private static String strLawsonFileName = LCSProperties.get("com.hbi.stg.extractors.STGLawsonExport.logFile", "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\LawsonExport.log");
    private static String strVRDFileName = LCSProperties.get("com.hbi.stg.extractors.VRDExtract.logFile", "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\VRDExtract.log");
    private static FileAppender appender = null;
    public static Logger etlLogger = null;

    /**
     * Constructor
     *
     * @param name
     */
    protected PLMETLLogger(String name) {
        super(name);
    }

    public static Logger createInstance(Class classname, String logLevel) {
        if (etlLogger == null) {
            etlLogger = PLMETLLogger.getLogger(classname, strFileName, logLevel);
        } else {
            etlLogger = PLMETLLogger.getLogger(classname, null, logLevel);
        }
        return etlLogger;
    }

    public static Logger createInstance(Class classname, String logLevel, Boolean isLawson) {
        if (isLawson) {
            strFileName = strLawsonFileName;
        }
        etlLogger = createInstance(classname, logLevel);
        return etlLogger;
    }
    public static Logger createInstance(Class classname, String logLevel, String loggerProj) {
        if (loggerProj.equals("VRDExtract")) {
            strFileName = strVRDFileName;
        }
        etlLogger = createInstance(classname, logLevel);
        return etlLogger;
    }

    /**
     * This is the method which will return the Logger
     *
     * @param classname The classname of the logging Class
     * @param LogFilename Output filename of the Log
     * @param logLevel LEVEL of logging can be DEBUG WARN INFO ERROR FATAL TRACE
     * ALL and if nothing is passed its turned OFF
     * @return
     */

    public static Logger getLogger(Class classname, String LogFilename, String logLevel) {
        Logger logger = null;
        logger = LogManager.getLogger(classname);

        if (LogFilename != null) {
            appender = getCustomAppender(LogFilename);
        }
        logger.addAppender(appender);
        setLevel(logger, logLevel);
        logger.setAdditivity(false);
        return logger;
    }

    public static FileAppender getCustomAppender(String LogFilename) {
        FileAppender appender1 = null;
        String pattern = "%d{ISO8601} %-5p [%t] %c - %m%n";
        try {
            Calendar cal = Calendar.getInstance();
            String dateAndTimeString = getCurrentTimeStamp();
            appender1 = new FileAppender(new PatternLayout(pattern), LogFilename + "_" + dateAndTimeString + ".log", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appender1;
    }

    public static void setLevel(Logger myLogger, String level) {

        if ("TRACE".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.TRACE);
        } else if ("DEBUG".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.DEBUG);
        } else if ("INFO".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.INFO);
        } else if ("WARN".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.WARN);
        } else if ("ERROR".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.ERROR);
        } else if ("FATAL".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.FATAL);
        } else if ("ALL".equalsIgnoreCase(level)) {
            myLogger.setLevel((Level) Level.ALL);
        } else {
            myLogger.setLevel((Level) Level.OFF);  // Turning off the logger		
        }

    }

    public static String getCurrentTimeStamp() {

        Calendar cal = Calendar.getInstance();
        
        int iDay = cal.get(Calendar.DAY_OF_MONTH);
        int iMonth = cal.get(Calendar.MONTH) + 1;
        int iHour = cal.get(Calendar.HOUR_OF_DAY);
        int iYear = cal.get(Calendar.YEAR);
        int iMin = cal.get(Calendar.MINUTE);
        int iSec = cal.get(Calendar.SECOND);
        
        String day = (((iDay < 10) ? "0" : "") + Integer.toString(iDay));
        String month = (((iMonth < 10) ? "0" : "") + Integer.toString(iMonth));
        String hour = (((iHour < 10) ? "0" : "") + Integer.toString(iHour));
        String min = (((iMin < 10) ? "0" : "") + Integer.toString(iMin));
        String sec = (((iSec < 10) ? "0" : "") + Integer.toString(iSec));
 /*       
        String dateAndTimeString = Integer.toString(cal.get(Calendar.MONTH) + 1)
                + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                + Integer.toString(cal.get(Calendar.YEAR))
                + "_"
                + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) 
                + Integer.toString(cal.get(Calendar.MINUTE))
                + Integer.toString(cal.get(Calendar.SECOND));
*/
         String dateAndTimeString = month + day + iYear + "_" + hour + min + sec;
         System.out.println("CurrentTimeStamp = " +dateAndTimeString);
        return dateAndTimeString;

    }
}



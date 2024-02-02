/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hbi.etl.util;

import com.hbi.etl.PLMETLLogEntry;
import com.hbi.etl.dao.HbiEtlTracker;
import com.hbi.etl.loaders.PLMColorLoader;
import com.hbi.etl.loaders.PLMETLTrackerLoader;
import com.hbi.etl.logger.PLMETLLogger;
import com.lcs.wc.util.LCSException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;
import wt.util.WTException;

/**
 *
 * @author UST
 */
public class PLMETLException extends LCSException{
    
    static Logger etlLogger = PLMETLLogger.createInstance(PLMETLException.class, "ERROR");
    
    public PLMETLException(String string) {
        super(string);
    }
   
    public PLMETLException(Throwable thrwbl) {
        super(thrwbl);
        etlLogger.error(thrwbl);
        System.exit(1);
    }
    
    public PLMETLException(String string, Throwable thrwbl) {
        super(thrwbl);
        etlLogger.error(string, thrwbl);
        System.exit(1);
    }    

    public PLMETLException(String string, Throwable thrwbl, Date endDate, PLMETLTrackerLoader tLoad, HbiEtlTracker etlTracker) {
            super(thrwbl);
            tLoad.updateTracker(etlTracker, new Timestamp(endDate.getTime()), "FAILURE");
            PLMETLLogEntry logTr = new PLMETLLogEntry();
            try {
                logTr.logTransaction(new Timestamp(endDate.getTime()), "FR", false);
            } catch (WTException ex1) {
                etlLogger.error(string, ex1);
                System.exit(1);
            }
            etlLogger.error(string, thrwbl);
            System.exit(1);
    }
    
    public PLMETLException() {
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage(); //To change body of generated methods, choose Tools | Templates.
    }
    
}

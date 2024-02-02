/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
//import com.lcs.wc.load.LoadCommon;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
//import wt.method.MethodContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 *
 * @author UST
 */
public class PLMETLLogEntry {

    public void logTransaction(Timestamp cycleTime, String mode, Boolean status) throws WTException {
		
		
		//Commented below two lines in 10.1 system-since invoking from rmi
        //new MethodContext(null, null);
        LCSLogEntry logEntry = LCSLogEntry.newLCSLogEntry();
       // com.lcs.wc.load.LoadCommon.SERVER_MODE = true;
        try {
           // FlexType flextype = (FlexType) LoadCommon..getFlexTypeFromPath("Log Entry\\PLMETLCycle");
        	FlexType flextype = (FlexType) FlexTypeCache.getFlexTypeFromPath("Log Entry\\PLMETLCycle");
            logEntry.setFlexType(flextype);
            logEntry.setValue("hbiMode", mode);
            logEntry.setValue("hbiSuccess", status);
            logEntry.setValue("hbiPLMETLCycleTime", cycleTime);
            LCSLogEntryLogic logLogic = new LCSLogEntryLogic();
            logEntry = (com.lcs.wc.foundation.LCSLogEntry) logLogic.saveLog(logEntry);

        } catch ( WTException ex) {
            Logger.getLogger(PLMETLLogEntry.class.getName()).log(Level.SEVERE, null, ex);
        }catch (WTPropertyVetoException ex) {
        	Logger.getLogger(PLMETLLogEntry.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

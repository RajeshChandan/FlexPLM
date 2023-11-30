/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hbi.etl;
import com.hbi.etl.util.PLMETLException;
import java.text.ParseException;

/**
 *
 * @author UST
 */
public interface PLMETLFramework {
    
   // public String export(String mode);
    //public abstract void exportFull() throws ParseException, PLMETLException;
    //public abstract void exportIncr() throws Exception;
    public String getBeginTime();
    public String getLastSuccessLogEntryTimestamp();
    
}

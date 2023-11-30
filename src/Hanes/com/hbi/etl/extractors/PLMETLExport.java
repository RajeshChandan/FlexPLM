/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hbi.etl.extractors;

import com.hbi.etl.util.PLMETLException;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author UST
 */
public interface PLMETLExport {
    
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate)throws PLMETLException;

    public Vector<Object> exportIncr(Date incrModeStartDate, Date incrModeEndDate)throws PLMETLException;
    
}

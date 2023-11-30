/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.extractors;

import static com.hbi.etl.extractors.PLMColorToPaletteExport.etlLogger;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSPaletteToColorLink;
import com.lcs.wc.util.LCSProperties;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import wt.fc.WTObject;
import wt.util.WTException;

/**
 *
 * @author UST
 */
public class PLMColorToPaletteExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMColorToPaletteExport.class, logLevel);

    @Override
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {
        Vector expObjects = null;

        try {

            PreparedQueryStatement localPreparedQueryStatement = new PreparedQueryStatement();
            localPreparedQueryStatement.appendFromTable(LCSPaletteToColorLink.class);
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "thePersistInfo.theObjectIdentifier.id"));
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "thePersistInfo.markForDelete"));
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "roleAObjectRef.key.id"));
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "roleBObjectRef.key.id"));
            localPreparedQueryStatement.appendAndIfNeeded();
            localPreparedQueryStatement.appendCriteria(new Criteria(new QueryColumn(LCSPaletteToColorLink.class, LCSPaletteToColorLink.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
            localPreparedQueryStatement.appendAndIfNeeded();
            localPreparedQueryStatement.appendCriteria(new Criteria(new QueryColumn(LCSPaletteToColorLink.class, LCSPaletteToColorLink.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
         
            
            etlLogger.debug(localPreparedQueryStatement);
            SearchResults results = LCSQuery.runDirectQuery(localPreparedQueryStatement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException ex) {
            throw new PLMETLException("Error in PaletteToColorLink Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in PaletteToColorLink Object Full Export:", e1);
        }
        return expObjects;
    }

    @Override
    public Vector<Object> exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {
        Vector expObjects = null;

        try {

            PreparedQueryStatement localPreparedQueryStatement = new PreparedQueryStatement();
            localPreparedQueryStatement.appendFromTable(LCSPaletteToColorLink.class);
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "thePersistInfo.theObjectIdentifier.id"));
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "thePersistInfo.markForDelete"));
			//Added in 10.1 upgrade : Missed below two columns in data in 9.2 system
			localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "roleAObjectRef.key.id"));
            localPreparedQueryStatement.appendSelectColumn(new QueryColumn(LCSPaletteToColorLink.class, "roleBObjectRef.key.id"));
            localPreparedQueryStatement.appendAndIfNeeded();
            localPreparedQueryStatement.appendCriteria(new Criteria(new QueryColumn(LCSPaletteToColorLink.class, LCSPaletteToColorLink.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
            localPreparedQueryStatement.appendAndIfNeeded();
            localPreparedQueryStatement.appendCriteria(new Criteria(new QueryColumn(LCSPaletteToColorLink.class, LCSPaletteToColorLink.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
            etlLogger.debug(localPreparedQueryStatement);
            SearchResults results = LCSQuery.runDirectQuery(localPreparedQueryStatement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException ex) {
            throw new PLMETLException("Error in PaletteToColor Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in PaletteToColor Object Incremental Export:", e1);
        }
        return expObjects;
    }
    
}

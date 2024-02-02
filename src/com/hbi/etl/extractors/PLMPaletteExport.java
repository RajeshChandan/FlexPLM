/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.extractors;

import static com.hbi.etl.extractors.PLMPaletteExport.etlLogger;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.util.LCSProperties;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import wt.util.WTException;

/**
 *
 * @author UST
 */
public class PLMPaletteExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMPaletteExport.class, logLevel);

    @Override
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {
        Vector expObjects = null;

        try {

            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSPalette.class);
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "typeDefinitionReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "thePersistInfo.createStamp"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "parentPaletteReference.key.id"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSPalette.class, LCSPalette.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSPalette.class, LCSPalette.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
         
            
            etlLogger.debug(statement);
            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException ex) {
            throw new PLMETLException("Error in Palette Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Palette Object Full Export:", e1);
        }
        return expObjects;
    }

    @Override
    public Vector<Object> exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {
        Vector expObjects = null;

        try {

            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSPalette.class);
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "typeDefinitionReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "thePersistInfo.createStamp"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSPalette.class, "parentPaletteReference.key.id"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSPalette.class, LCSPalette.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSPalette.class, LCSPalette.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
            etlLogger.debug(statement);
            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException ex) {
            throw new PLMETLException("Error in Material Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Material Object Incremental Export:", e1);
        }
        return expObjects;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hbi.etl.extractors;

import java.util.Vector;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import org.apache.log4j.Logger;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.util.LCSProperties;
import java.util.Date;
import wt.util.WTException;

/**
 *
 * @author karamasu
 */
public class PLMColorExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMColorExport.class, logLevel);

    @Override
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {

        Vector expObjects = null;
        try {
                PreparedQueryStatement statement = new PreparedQueryStatement();
                statement.appendFromTable(LCSColor.class);
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "state.state"));
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.classname"));
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.markForDelete"));
		statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, LCSColor.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, LCSColor.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
                etlLogger.debug(statement);
                SearchResults results = LCSQuery.runDirectQuery(statement);
                expObjects = results.getResults();
                etlLogger.debug(expObjects.size());
            
        } catch (WTException e) {
            throw new PLMETLException("Error in Color Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Color Object Full Export:", e1);
        }
        return expObjects;
    
    }

    @Override
    public Vector exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {

        Vector expObjects = null;
        try {
                PreparedQueryStatement statement = new PreparedQueryStatement();
                statement.appendFromTable(LCSColor.class);
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "state.state"));
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.classname"));
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
                statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.markForDelete"));
		statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, LCSColor.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, LCSColor.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
                etlLogger.debug(statement);		
                SearchResults results = LCSQuery.runDirectQuery(statement);
                expObjects = results.getResults();
                etlLogger.debug(expObjects.size());
            
        } catch (WTException e) {
            throw new PLMETLException("Error in Color Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Color Object Incremental Export:", e1);
        }
        return expObjects;
    
    }

}

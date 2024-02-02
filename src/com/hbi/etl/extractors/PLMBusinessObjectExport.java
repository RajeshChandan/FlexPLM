/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.extractors;

import com.hbi.etl.PLMETLProcessor;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.LCSProperties;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import wt.util.WTException;

/**
 *
 * @author karamasu
 */
public class PLMBusinessObjectExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMBusinessObjectExport.class, logLevel);
    @Override
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException{

        etlLogger.info("Start PLMBusinessObject Export in Full Mode");
        Vector expObjects = null;
        try {

            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSLifecycleManaged.class);

            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"));
          //below code changed by Wipro Upgrade Team
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "typeDefinitionReference.key.id"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, LCSLifecycleManaged.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, LCSLifecycleManaged.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
            SearchResults results = LCSQuery.runDirectQuery(statement);
            etlLogger.debug(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException e) {
            throw new PLMETLException("Error in Business Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Business Object Full Export:", e1); //To change body of generated methods, choose Tools | Templates.
        }
        return expObjects;
    }

    @Override
    public Vector exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {

        etlLogger.info("Start PLMBusinessObject Export in Incr Mode");
        Vector expObjects = null;
        try {

            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSLifecycleManaged.class);

            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"));
            //below code changed by Wipro Upgrade Team
            statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "typeDefinitionReference.key.id"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, LCSLifecycleManaged.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, LCSLifecycleManaged.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
            SearchResults results = LCSQuery.runDirectQuery(statement);
            etlLogger.debug(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException e) {
            throw new PLMETLException("Error in Business Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Business Object Incremental Export:", e1);
        }
        return expObjects;
    }

}

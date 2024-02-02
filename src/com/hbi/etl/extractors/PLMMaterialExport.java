/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.extractors;

import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import org.apache.log4j.Logger;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialClientModel;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.util.LCSProperties;
import java.util.Vector;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import wt.introspection.ClassInfo;
import wt.introspection.ColumnDescriptor;
import wt.introspection.WTIntrospector;
import wt.util.WTException;

/**
 *
 * @author UST
 */
public class PLMMaterialExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMMaterialExport.class, logLevel);


    @Override
    public Vector exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {

        Vector expObjects = null;

        try {

            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSMaterial.class);

            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "masterReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDisplay"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "primaryImageURL"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "flexTypeIdPath"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDefinitionReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "versionInfo.identifier.versionId"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSMaterial.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSMaterial.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
         
            
            etlLogger.debug(statement);
            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException ex) {
            throw new PLMETLException("Error in Material Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Material Object Full Export:", e1);
        }
        return expObjects;
    }

    @Override
    public Vector exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {
        Vector expObjects = null;

        try {
        	

            
            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSMaterial.class);
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "masterReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDisplay"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "primaryImageURL"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "flexTypeIdPath"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDefinitionReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "versionInfo.identifier.versionId"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSMaterial.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSMaterial.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
            etlLogger.debug(statement);
            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());
            

        }catch (WTException ex) {
            throw new PLMETLException("Error in Material Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in Material Object Incremental Export:", e1);
        }
        return expObjects;

    }

}

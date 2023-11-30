/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.extractors;

import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.LCSProperties;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import wt.util.WTException;

/**
 *
 * @author UST
 */
public class PLMSupplierExport implements PLMETLExport {

    private static Vector expObjects = null;
    private static Vector list = null;
    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMSupplierExport.class, logLevel);

    @Override
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {

        try {
            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSSupplierMaster.class);
            statement.appendFromTable(LCSSupplier.class);
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "supplierName"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "primaryImageURL"));
           //Commented by Anoop 
		   // statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "securityLabels"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "flexTypeIdPath"));
            statement.appendJoin(new QueryColumn(LCSSupplier.class, "masterReference.key.id"), new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplierMaster.class, "supplierName"), "placeholder", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, LCSSupplier.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, LCSSupplier.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
            //  statement.appendAndIfNeeded();
            // statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "att1"), "30 FOREST CITY US", "="));

            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (Exception e1) {
            throw new PLMETLException("Error in Supplier Object Full Export:", e1);
        }

        return expObjects;
    }

    @Override
    public Vector<Object> exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {

        try {
            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSSupplierMaster.class);
            statement.appendFromTable(LCSSupplier.class);
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "supplierName"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "primaryImageURL"));
			//Commented by Anoop 
            //statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "securityLabels"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "flexTypeIdPath"));
            statement.appendJoin(new QueryColumn(LCSSupplier.class, "masterReference.key.id"), new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplierMaster.class, "supplierName"), "placeholder", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, LCSSupplier.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, LCSSupplier.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
            //  statement.appendAndIfNeeded();
            // statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "att1"), "30 FOREST CITY US", "="));

            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        }  catch (Exception e1) {
            throw new PLMETLException("Error in Supplier Object Incremental Export:", e1);
        }

        return expObjects;
    }
}

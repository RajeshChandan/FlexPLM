/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.extractors;


import com.hbi.etl.extractors.PLMMaterialExport;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import org.apache.log4j.Logger;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.LCSProperties;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import wt.util.WTException;

/**
 *
 * @author karamasu
 */
public class PLMMaterialSupplierExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMMaterialSupplierExport.class, logLevel);

    @Override
    public Vector exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {

        Vector expObjects = null;
        try {

            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSMaterialMaster.class, "MATERIALMASTER");
            statement.appendFromTable(LCSMaterial.class);
            statement.appendFromTable(LCSSupplier.class);
            statement.appendFromTable(LCSSupplierMaster.class);
            statement.appendFromTable(LCSMaterialSupplier.class);
            statement.appendFromTable(LCSMaterialSupplierMaster.class);
           // statement.appendFromTable(FlexType.class);
            statement.appendFromTable("V_LCSMaterialSupplier");

            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "flexTypeIdPath"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "flexTypeReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "active"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplier.class, "masterReference.key.id"), new QueryColumn(LCSMaterialSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "name"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "supplierName"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDisplay"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "partPrimaryImageURL"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "flexTypeReference.key.id"));
            statement.appendSelectColumn(new QueryColumn("V_LCSMaterialSupplier", "materialSupplierName"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, "placeholder"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "iterationInfo.branchId"));

            statement.appendJoin(new QueryColumn("V_LCSMaterialSupplier", "idA2A2"), new QueryColumn(LCSMaterialSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSMaterial.class, "masterReference.key.id"), new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSSupplier.class, "masterReference.key.id"), new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplier.class, "masterReference.key.id"), new QueryColumn(LCSMaterialSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id"), new QueryColumn(LCSSupplier.class, "masterReference.key.id"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id"), new QueryColumn(LCSMaterial.class, "masterReference.key.id"));
           // statement.appendJoin(new QueryColumn(LCSMaterialSupplier.class, "flexTypeReference.key.id"), new QueryColumn(FlexType.class, "thePersistInfo.theObjectIdentifier.id"));
            
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "name"), "material_placeholder", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "active"), "0", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, LCSMaterialSupplier.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, LCSMaterialSupplier.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
            etlLogger.debug(statement);
            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());

        } catch (WTException e) {
            throw new PLMETLException("Error in MaterialSupplier Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in MaterialSupplier Object Full Export:", e1);
        }
        return expObjects;

    }

    @Override
    public Vector exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {

        Vector expObjects = null;
        try {
        	
            PreparedQueryStatement statement = new PreparedQueryStatement();
            statement.appendFromTable(LCSMaterialMaster.class, "MATERIALMASTER");
            statement.appendFromTable(LCSMaterial.class);
            statement.appendFromTable(LCSSupplier.class);
            statement.appendFromTable(LCSSupplierMaster.class);
            statement.appendFromTable(LCSMaterialSupplier.class);
            statement.appendFromTable(LCSMaterialSupplierMaster.class);
            statement.appendFromTable("V_LCSMaterialSupplier");

            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "thePersistInfo.markForDelete"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "flexTypeIdPath"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "typeDefinitionReference.key.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "active"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplier.class, "masterReference.key.id"), new QueryColumn(LCSMaterialSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "name"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "supplierName"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDisplay"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "primaryImageURL"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "typeDefinitionReference.key.id"));
            statement.appendSelectColumn(new QueryColumn("V_LCSMaterialSupplier", "materialSupplierName"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "state.state"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, "placeholder"));
            statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "iterationInfo.branchId"));

            statement.appendJoin(new QueryColumn("V_LCSMaterialSupplier", "idA2A2"), new QueryColumn(LCSMaterialSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSMaterial.class, "masterReference.key.id"), new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSSupplier.class, "masterReference.key.id"), new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplier.class, "masterReference.key.id"), new QueryColumn(LCSMaterialSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id"), new QueryColumn(LCSSupplier.class, "masterReference.key.id"));
            statement.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id"), new QueryColumn(LCSMaterial.class, "masterReference.key.id"));

            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.latest"), "1", "="));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "name"), "material_placeholder", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "active"), "0", "<>"));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, LCSMaterialSupplier.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
            statement.appendAndIfNeeded();
            statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, LCSMaterialSupplier.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
            etlLogger.debug(statement);
            SearchResults results = LCSQuery.runDirectQuery(statement);
            expObjects = results.getResults();
            etlLogger.debug(expObjects.size());
       
        } catch (WTException e) {
            throw new PLMETLException("Error in MaterialSupplier Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in MaterialSupplier Object Incremental Export:", e1);
        }
        return expObjects;
    }

}

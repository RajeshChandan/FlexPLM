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
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.WTSFlexType;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import wt.util.WTException;

/**
 *
 * @author karamasu
 */
public class PLMMOAObjectExport implements PLMETLExport {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    public static final String moaParamStr = LCSProperties.get("com.hbi.etl.extractors.moaobjectstoexport");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMMOAObjectExport.class, logLevel);

    @Override
    public Vector<Object> exportFull(Date fullModeStartDate, Date fullModeEndDate) throws PLMETLException {

        Vector expObjects = null;
        try {
                Collection moaObjectType = MOAHelper.getMOACollection(moaParamStr);
                PreparedQueryStatement statement = new PreparedQueryStatement();
                statement.appendFromTable(LCSMOAObject.class);
                statement.appendFromTable(FlexType.class);
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "thePersistInfo.theObjectIdentifier.id"));
                //statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerAttributeReference.key.classname"));
                //statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerAttributeReference.key.id"));
              //statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerAttributeReference.key.classname"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "role"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerReference.key.classname"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerReference.key.id"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerVersion"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "thePersistInfo.markForDelete"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "flexTypeIdPath"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "typeDefinitionReference.key.id"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "effectLatest"));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, "effectOutDate"), "", "IS NULL"));
                //statement.appendAndIfNeeded();
                //statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, "ownerAttributeReference.key.id"), "105708", "="));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, LCSMOAObject.CREATE_TIMESTAMP), fullModeStartDate, Criteria.GREATER_THAN));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, LCSMOAObject.CREATE_TIMESTAMP), fullModeEndDate, Criteria.LESS_THAN_EQUAL));
                statement.appendJoin(new QueryColumn(LCSMOAObject.class, "typeDefinitionReference.key.id"), new QueryColumn(FlexType.class, "thePersistInfo.theObjectIdentifier.id"));
                //statement.appendAndIfNeeded();
                //statement.appendCriteria(new Criteria(new QueryColumn(FlexType.class, "typeName"), moaObjectType, "in"));
                statement.appendInCriteria(new QueryColumn(FlexType.class, "typeName"), (Collection)moaObjectType);
                SearchResults results = LCSQuery.runDirectQuery(statement);
                etlLogger.debug(statement);
                expObjects = results.getResults();
                //expObjects.add(results.getResults());
                etlLogger.debug(expObjects.size());
           // }
        } catch (WTException e) {
            throw new PLMETLException("Error in MOA Object Full Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e1) {
            throw new PLMETLException("Error in MOA Object Full Export:", e1); //To change body of generated methods, choose Tools | Templates.
        }
        return expObjects;
    }

    @Override
    public Vector exportIncr(Date incrModeStartDate, Date incrModeEndDate) throws PLMETLException {

        Vector expObjects = null;
        try {
                Collection moaObjectType = MOAHelper.getMOACollection(moaParamStr);
                PreparedQueryStatement statement = new PreparedQueryStatement();
                statement.appendFromTable(LCSMOAObject.class);
                statement.appendFromTable(WTTypeDefinition.class);
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "thePersistInfo.theObjectIdentifier.id"));
                //The following lines are commented and role column has been added by Wipro Upgrade team
                //statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerAttributeReference.key.classname"));
                //statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerAttributeReference.key.id"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "role"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerReference.key.classname"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerReference.key.id"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerVersion"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "thePersistInfo.markForDelete"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "flexTypeIdPath"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "typeDefinitionReference.key.id"));
                statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "effectLatest"));

                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, "effectOutDate"), "", "IS NULL"));
                //statement.appendAndIfNeeded();
                //statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, "dropped"), "1", "<>"));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, LCSMOAObject.MODIFY_TIMESTAMP), incrModeStartDate, Criteria.GREATER_THAN));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, LCSMOAObject.MODIFY_TIMESTAMP), incrModeEndDate, Criteria.LESS_THAN_EQUAL));
                statement.appendJoin(new QueryColumn(LCSMOAObject.class, "typeDefinitionReference.key.id"), new QueryColumn(WTTypeDefinition.class, "thePersistInfo.theObjectIdentifier.id"));
                //statement.appendAndIfNeeded();
                //statement.appendCriteria(new Criteria(new QueryColumn(FlexType.class, "typeName"), moaObjectType, "="));
                statement.appendInCriteria(new QueryColumn(WTTypeDefinition.class, "name"), (Collection)moaObjectType);
                //statement.appendCriteria(new Criteria(new QueryColumn(WTTypeDefinition.class, "iterationInfo.latest"), "1", "="));
                SearchResults results = LCSQuery.runDirectQuery(statement);
                etlLogger.debug(statement);
                expObjects = results.getResults();
                etlLogger.debug(expObjects.size());
        
        } catch (WTException e) {
            throw new PLMETLException("Error in MOA Object Incremental Export:", new UnsupportedOperationException("Not supported yet.")); //To change body of generated methods, choose Tools | Templates.
        }catch (Exception e1) {
            throw new PLMETLException("Error in MOA Object Incremental Export:", e1); //To change body of generated methods, choose Tools | Templates.
        }
        return expObjects;
    }

}

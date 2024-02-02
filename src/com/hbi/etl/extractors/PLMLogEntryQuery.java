/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hbi.etl.extractors;

import com.hbi.etl.logger.PLMETLLogger;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.util.LCSProperties;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author karamasu
 */
public class PLMLogEntryQuery {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMLogEntryQuery.class, logLevel);
    private static Vector expObjects = null;
    private static LCSLogEntry lcsLogEntry = null;
    
    public LCSLogEntry getLogEntry() throws Exception {
        
    	FlexType flexType = (FlexType) FlexTypeCache.getFlexTypeFromPath("Log Entry\\PLMETLCycle");
        String columnDescriptorName = flexType.getAttribute("hbiSuccess").getColumnDescriptorName();
        PreparedQueryStatement s2 = new PreparedQueryStatement();
        s2.appendFromTable(LCSLogEntry.class);
        s2.appendFromTable(FlexType.class);
       // s2.appendSelectColumn(new QueryColumn(LCSLogEntry.class, "thePersistInfo.theObjectIdentifier.id"));
        s2.appendSelectColumn(new QueryColumn(LCSLogEntry.class, LCSLogEntry.MODIFY_TIMESTAMP), "MAX");
        s2.appendJoin(new QueryColumn(LCSLogEntry.class, "flexTypeReference.key.id"), new QueryColumn(FlexType.class, "thePersistInfo.theObjectIdentifier.id"));
        s2.appendAndIfNeeded();
        s2.appendCriteria(new Criteria(new QueryColumn(FlexType.class, "typeName"), "PLMETLCycle", "="));
        s2.appendAndIfNeeded();
        
        //commented by retrofit team
        //s2.appendCriteria(new Criteria(new QueryColumn(LCSLogEntry.class,LCSLogEntry.ATT1 ), "true", "="));
        s2.appendCriteria(new Criteria(new QueryColumn(LCSLogEntry.class,columnDescriptorName ), "true", "="));
            
        
        
        
       // s2.appendGroupBy(new QueryColumn(LCSLogEntry.class, "thePersistInfo.theObjectIdentifier.id"));
       
        PreparedQueryStatement statement = new PreparedQueryStatement();
        statement.appendFromTable(LCSLogEntry.class);
        statement.appendFromTable(FlexType.class);
        statement.appendJoin(new QueryColumn(LCSLogEntry.class, "flexTypeReference.key.id"), new QueryColumn(FlexType.class, "thePersistInfo.theObjectIdentifier.id"));
        statement.appendSelectColumn(new QueryColumn(FlexType.class, "typeName"));
        statement.appendSelectColumn(new QueryColumn(FlexType.class, "thePersistInfo.theObjectIdentifier.id"));
        statement.appendSelectColumn(new QueryColumn(LCSLogEntry.class, "thePersistInfo.theObjectIdentifier.id"));
       // statement.appendSortBy(new QueryColumn(LCSLogEntry.class, LCSLogEntry.MODIFY_TIMESTAMP), "desc");
       // statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(FlexType.class, "typeName"), "PLMETLCycle", "="));
        //statement.appendAndIfNeeded();
        statement.appendInCriteria(new QueryColumn(LCSLogEntry.class, LCSLogEntry.MODIFY_TIMESTAMP), s2);
        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(LCSLogEntry.class,columnDescriptorName ), "true", "="));
        
        SearchResults results = LCSQuery.runDirectQuery(statement);
        expObjects = results.getResults();
        etlLogger.debug(expObjects.size());
        etlLogger.debug(statement);
        FlexObject flexObj = (FlexObject) expObjects.get(0);
        lcsLogEntry = (LCSLogEntry) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLogEntry:" + flexObj.getString("LCSLOGENTRY.IDA2A2"));
        return lcsLogEntry;
    }


}

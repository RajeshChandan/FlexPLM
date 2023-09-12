package com.limited.exporter;

import com.lcs.wc.db.*;
import com.lcs.wc.foundation.LCSQuery;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.Collection;
import java.util.Iterator;

public class VSType {
    private static final Logger logger = LogR.getLogger(VSType.class.getName());

    public static String getTypeId(String type) throws WTException {
        String typeId = "";
        SearchResults results = null;
        PreparedQueryStatement statement = new PreparedQueryStatement();// Creating Statement.
        statement.appendFromTable("WTTypeDefinition");
        statement.appendSelectColumn(new QueryColumn("WTTypeDefinition", "BRANCHIDITERATIONINFO"));

        statement.appendCriteria(new Criteria("WTTypeDefinition", "LATESTITERATIONINFO", "1", Criteria.EQUALS));

        statement.appendAnd();
        statement.appendCriteria(new Criteria("WTTypeDefinition", "NAME", type, Criteria.LIKE));

        logger.debug("sql query>>>" + statement.getSqlStatement());

        results = LCSQuery.runDirectQuery(statement);
        Collection<?> data = results.getResults();
        Iterator<?> itr = data.iterator();
        while (itr.hasNext()) {
            FlexObject fo = (FlexObject) itr.next();
            typeId = fo.getString("WTTYPEDEFINITION.BRANCHIDITERATIONINFO");
        }
        logger.debug(type + " type id is " + typeId);
        return typeId;

    }

}

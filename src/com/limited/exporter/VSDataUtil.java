package com.limited.exporter;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

public class VSDataUtil {
    private static final Logger logger = LogR.getLogger (VSDataUtil.class.getName ());

    public SearchResults extractDbRecords (VSDataExportBean bean) throws WTException {

        String typeId = "\\" + VSType.getTypeId (bean.getFlexType ());

        if (FormatHelper.hasContent (bean.getFlexSubType ())) {
            typeId = typeId + "\\" + VSType.getTypeId (bean.getFlexSubType ());
        }

        SearchResults results = null;
        PreparedQueryStatement statement = new PreparedQueryStatement ();// Creating Statement.
        statement.appendFromTable (bean.getFlexTypeClass ());
        statement.appendSelectColumn (new QueryColumn (bean.getFlexTypeClass (), "idA2A2"));

        // add tables
        statement.appendFromTable (bean.getFlexTypeClass ());

        statement.appendAndIfNeeded ();
        statement.appendCriteria (new Criteria (bean.getFlexTypeClass (), "flexTypeIdPath", typeId + "%", Criteria.LIKE));

        if (FormatHelper.hasContent (bean.getStartDate ()) && FormatHelper.hasContent (bean.getStartDate ())) {

            statement.appendAndIfNeeded ();
            statement.appendCriteria (new Criteria (bean.getFlexTypeClass (), "CREATESTAMPA2", bean.getStartDate (),
                    Criteria.GREATER_THAN_EQUAL));

            statement.appendAndIfNeeded ();
            statement.appendCriteria (
                    new Criteria (bean.getFlexTypeClass (), "CREATESTAMPA2", bean.getEndDate (), Criteria.LESS_THAN_EQUAL));
        }

        if (FormatHelper.hasContent (bean.getName ())) {
            FlexType type = FlexTypeCache.getFlexTypeRoot (bean.getFlexTypeClass ().replace ("LCS", ""));
            String colName = type.getAttribute ("name").getColumnName ();
            statement.appendAndIfNeeded ();
            statement.appendCriteria (
                    new Criteria (bean.getFlexTypeClass (), colName, bean.getName (), Criteria.EQUALS));
        }


        logger.debug ("sql query>>>" + statement.getSqlStatement ());

        results = LCSQuery.runDirectQuery (statement);
        logger.debug ("result>>>" + results.getResultsFound ());

        return results;

    }
}

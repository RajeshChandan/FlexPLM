package com.lowes.model.season;

import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonLogic;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowes.web.util.ObjectUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SeasonModel {

    private static final Logger logger = LogR.getLogger(SeasonModel.class.getName());

    public void deleteProduct(LCSSeason season) throws WTException {

        logger.log(Level.INFO, "Delete Season Model Started:- {}", season.getName());
        LCSSeasonLogic seasonLogic = new LCSSeasonLogic();
        seasonLogic.deleteSeason(ObjectUtil.getLatestIteration(season));
        logger.log(Level.INFO, "Delete Season Model Completed :- {}", season.getName());


    }

    public Map<String, LCSSeason> findSeasons(List<Map<String, String>> criteria) throws WTException {
        logger.log(Level.DEBUG, "Find Product function execution Started");

        Map<String, LCSSeason> productMap = new HashMap<>();

        FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Season");
        String oid = FormatHelper.getNumericObjectIdFromObject(productFlexTypeObj);
        logger.log(Level.INFO, "type OID >> {}", oid);

        //Initializing the PreparedQueryStatement, which is using to get LCSSeason object
        PreparedQueryStatement statement = new PreparedQueryStatement();

        statement.appendSelectColumn(new QueryColumn(LCSSeason.class, "iterationInfo.branchId"));

        statement.appendFromTable(LCSSeason.class);

        statement.appendCriteria(new Criteria(new QueryColumn(LCSSeason.class, "typeDefinitionReference.key.branchId"), oid, Criteria.EQUALS));

        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(LCSSeason.class, "iterationInfo.latest"), "1", Criteria.EQUALS));


        criteria.forEach(n -> {
            statement.appendOrIfNeeded();
            statement.appendOpenParen();
            n.forEach((k, v) -> {
                try {
                    statement.appendAndIfNeeded();
                    statement.appendCriteria(new Criteria(new QueryColumn(LCSSeason.class, productFlexTypeObj.getAttribute(k).getColumnDescriptorName()), v, Criteria.EQUALS));
                } catch (WTException e) {
                    logger.error(e);
                }
            });
            statement.appendClosedParen();
        });

        logger.log(Level.INFO, "SQL >>{}", statement.getSqlStatement());
        //Get FlexObject from the SearchResults instance, using to form LCSSeason instance, which is needed to return from function header to calling function
        SearchResults results = LCSQuery.runDirectQuery(statement);
        logger.log(Level.DEBUG, "Result found >>{}", results.getResultsFound());
        if (results.getResultsFound() > 0) {

            results.getResults().forEach((Consumer<Object>) o -> {
                try {
                    FlexObject flexObj = (FlexObject) o;
                    LCSSeason product = (LCSSeason) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSeason:" + flexObj.getString("LCSSeason.BRANCHIDITERATIONINFO"));
                    product = (LCSSeason) VersionHelper.latestIterationOf(product);
                    productMap.put(flexObj.getString("LCSSeason.BRANCHIDITERATIONINFO"), product);
                } catch (WTException e) {
                    logger.error(e);
                }
            });
        }
        logger.log(Level.INFO, "data fetched >>{}", productMap);
        logger.log(Level.DEBUG, "Find Product function execution Completed");
        return productMap;
    }
}

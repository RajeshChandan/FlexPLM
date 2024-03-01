package com.lowes.web.model.product;

import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.season.*;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.*;
import java.util.function.Consumer;

/****
 * Standard Product model class has below functions
 *  - Search Product
 *  - Delete Product
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class ProductModel {

    private static final Logger logger = LogR.getLogger(ProductModel.class.getName());

    /**
     * Query all Seasons link to a product.
     * @param product  - product object
     * @return - list of seasons
     * @throws WTException - WT Exception
     */
    public List<LCSSeason> findSeasons(LCSProduct product) throws WTException {
        logger.log(Level.INFO, "findSeasons() Started:- {}", product.getName());
        List<LCSSeason> seasons = new ArrayList<>();

        Collection<?> results = (new LCSSeasonQuery()).findSeasons(getLatestIteration(product));
        logger.log(Level.DEBUG, "findSeasons query result  {}", results.size());
        results.forEach(obj -> {
            try {
                LCSSeasonMaster seasonMaster = (LCSSeasonMaster) obj;
                LCSSeason season = VersionHelper.latestIterationOf(seasonMaster);
                if (season != null) {
                    seasons.add(season);
                }
            } catch (WTException e) {
                logger.error(e);
            }
        });
        logger.log(Level.INFO, "findSeasons() Executed");
        return seasons;
    }

    /**
     * remove seasons from product (based on input product and seasons).
     * @param product - product object
     * @param seasons - list of seasons
     */
    public void removeProductFromSeason(LCSProduct product, List<LCSSeason> seasons) {
        logger.log(Level.INFO, "removeProductFromSeason() Started:- {} {}", product.getName(), seasons);
        if (Objects.isNull(seasons) || seasons.isEmpty()) {
            logger.log(Level.DEBUG, "seasons is null or empty {} ", seasons);
            return;
        }
        seasons.stream().filter(Objects::nonNull).forEach(season -> {
            try {
                logger.log(Level.INFO, "product  rev:-{} {} {}", product.getVersionInfo().getIdentifier().getValue(), FormatHelper.getVersionId(product), FormatHelper.getObjectId(product));
                LCSSeasonProductLink spl = LCSSeasonQuery.findSeasonProductLink(product, season);
                LCSProduct productSeasonRev = SeasonProductLocator.getProductSeasonRev(spl);
                logger.log(Level.INFO, "productSeasonRev  rev:-{} {} {}", productSeasonRev.getVersionInfo().getIdentifier().getValue(), FormatHelper.getVersionId(productSeasonRev), FormatHelper.getObjectId(productSeasonRev));
                LCSSeasonClientModel seasonModel = new LCSSeasonClientModel();
                seasonModel.load(FormatHelper.getVersionId(season));
                seasonModel.removeProduct(FormatHelper.getVersionId(productSeasonRev));
                logger.log(Level.DEBUG, "removed Product From Season {} {}", product, season);
            } catch (Exception e) {
                logger.error(e);
            }
        });
        logger.log(Level.INFO, "removeProductFromSeason() Executed");
    }

    /**
     * Delete product form PLM System.
     * @param product -  product object
     * @throws WTException - wt Exception
     */
    public void deleteProduct(LCSProduct product) throws WTException {
        logger.log(Level.INFO, "Delete Product Started:- {}", product.getName());
        LCSProduct latest = VersionHelper.latestIterationOf(product.getMaster());
        logger.log(Level.DEBUG, "Delete Product ids :- {} {}", FormatHelper.getVersionId(latest), FormatHelper.getObjectId(latest));
        LCSProductLogic productLogic = new LCSProductLogic();
        productLogic.delete(getLatestIteration(latest), true);
        logger.log(Level.INFO, "Delete Product Completed :- {}", product.getName());
    }

    /**
     * Query product records for input data.
     * @param criteria - json criteria
     * @return - returns map (branchid, productObject)
     * @throws WTException -  wt exception
     */
    public Map<String, LCSProduct> findProduct(List<Map<String, String>> criteria) throws WTException {
        logger.log(Level.DEBUG, "Find Product function execution Started");

        Map<String, LCSProduct> productMap = new HashMap<>();

        FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Product");
        String oid = FormatHelper.getNumericObjectIdFromObject(productFlexTypeObj);
        logger.log(Level.INFO, "type OID >> {}", oid);

        //Initializing the PreparedQueryStatement, which is using to get LCSProduct object based on the given set of parameters(productName and Product FlexType ID Path)
        PreparedQueryStatement statement = new PreparedQueryStatement();

        statement.appendSelectColumn(new QueryColumn(LCSProduct.class, "iterationInfo.branchId"));

        statement.appendFromTable(LCSProduct.class);

        statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "typeDefinitionReference.key.branchId"), oid, Criteria.EQUALS));

        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "iterationInfo.latest"), "1", Criteria.EQUALS));

        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "versionInfo.identifier.versionId"), "A", Criteria.EQUALS));

        criteria.forEach(n -> {
            statement.appendOrIfNeeded();
            statement.appendOpenParen();
            n.forEach((k, v) -> {
                try {
                    statement.appendAndIfNeeded();
                    statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, productFlexTypeObj.getAttribute(k).getColumnDescriptorName()), v, Criteria.EQUALS));
                } catch (WTException e) {
                    logger.error(e);
                }
            });
            statement.appendClosedParen();
        });

        logger.log(Level.INFO, "SQL >>{}", statement.getSqlStatement());
        //Get FlexObject from the SearchResults instance, using to form LCSProduct instance, which is needed to return from function header to calling function
        SearchResults results = LCSQuery.runDirectQuery(statement);
        logger.log(Level.DEBUG, "Result found >>{}", results.getResultsFound());
        if (results.getResultsFound() > 0) {

            results.getResults().forEach((Consumer<Object>) o -> {
                try {
                    FlexObject flexObj = (FlexObject) o;
                    LCSProduct product = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + flexObj.getString("LCSProduct.BRANCHIDITERATIONINFO"));
                    product = (LCSProduct) VersionHelper.latestIterationOf(product);
                    productMap.put(flexObj.getString("LCSProduct.BRANCHIDITERATIONINFO"), product);
                } catch (WTException e) {
                    logger.error(e);
                }
            });
        }
        logger.log(Level.INFO, "data fetched >>{}", productMap);
        logger.log(Level.DEBUG, "Find Product function execution Completed");
        return productMap;
    }

    public LCSProduct getLatestIteration(LCSProduct product) {
        LCSProduct latest = product;
        try {
            latest = (LCSProduct) VersionHelper.latestIterationOf(latest);
        } catch (WTException e) {
            logger.error(e.getLocalizedMessage());
        }
        return latest;
    }

    public LCSSeason getLatestIteration(LCSSeason season) {
        LCSSeason latest = season;
        try {
            latest = (LCSSeason) VersionHelper.latestIterationOf(latest);
        } catch (WTException e) {
            logger.error(e.getLocalizedMessage());
        }
        return latest;
    }
}

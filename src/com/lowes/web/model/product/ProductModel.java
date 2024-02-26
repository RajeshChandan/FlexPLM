package com.lowes.web.model.product;

import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void deleteProduct(LCSProduct product) throws WTException {
        logger.log(Level.INFO, "Delete Product Started:- {}", product.getName());
        LCSProductLogic productLogic = new LCSProductLogic();
        productLogic.delete(product, true);
        logger.log(Level.INFO, "Delete Product Completed :- {}", product.getName());
    }

    public Map<String, LCSProduct> findProduct(List<Map<String, String>> criteria) throws WTException {
        logger.log(Level.DEBUG, "Find Product function execution Started");

        Map<String, LCSProduct> productMap = new HashMap<>();

        FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Product");
        String oid = FormatHelper.getNumericObjectIdFromObject(productFlexTypeObj);

        //Initializing the PreparedQueryStatement, which is using to get LCSProduct object based on the given set of parameters(productName and Product FlexType ID Path)
        PreparedQueryStatement statement = new PreparedQueryStatement();

        statement.appendSelectColumn(new QueryColumn(LCSProduct.class, "iterationInfo.branchId"));

        statement.appendFromTable(LCSProduct.class);

        statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "typeDefinitionReference.key.branchId"), "?", "="), Long.valueOf(oid));

        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "iterationInfo.latest"), "1", Criteria.EQUALS));

        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "versionInfo.identifier.versionId"), "A", Criteria.EQUALS));

        criteria.forEach(n -> {
            n.forEach((k, v) -> {
                try {
                    statement.appendAndIfNeeded();
                    statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, productFlexTypeObj.getAttribute(k).getColumnDescriptorName()), v, Criteria.EQUALS));
                } catch (WTException e) {
                    logger.error(e);
                }
            });
            statement.appendOrIfNeeded();
        });
        logger.log(Level.INFO, "SQL >>{}", statement.getSqlStatement());
        //Get FlexObject from the SearchResults instance, using to form LCSProduct instance, which is needed to return from function header to calling function
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if (results != null && results.getResultsFound() > 0) {

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
        logger.log(Level.DEBUG, "Find Product function execution Completed");
        return productMap;
    }
}

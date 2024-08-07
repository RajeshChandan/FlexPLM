package com.lowes.model;

import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.VersionHelper;
import wt.util.WTException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CostsheetModel implements GenericModel{

    @Override
    public Collection<FlexTyped> getObject(Object obj, String objectName) throws WTException {
        LCSCostSheet costSheet = (LCSCostSheet) obj;
        Collection<FlexTyped> flexTypedCollection = new ArrayList<>();
        if (objectName.equalsIgnoreCase("Sourcing Configuration")) {
            LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(costSheet.getSourcingConfigMaster());
            flexTypedCollection.add(sourcingConfig);
        } else if (objectName.equalsIgnoreCase("Sourcing Configuration to Season")) {
            LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(costSheet.getSourcingConfigMaster());
            Collection<FlexTyped> colSourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLinks(sourcingConfig.getMaster());
            flexTypedCollection.addAll( colSourceToSeasonLink);
        } else if (objectName.equalsIgnoreCase("product to season")) {
            LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(costSheet.getProductMaster());
            LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(costSheet.getSeasonMaster());
            LCSSeasonProductLink seasonProductLink = LCSSeasonQuery.findSeasonProductLink(product,season);
            flexTypedCollection.add(seasonProductLink);
        } else if (objectName.equalsIgnoreCase("product")) {
            LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(costSheet.getProductMaster());
            flexTypedCollection.add(product);
        }
        return flexTypedCollection;
    }
}

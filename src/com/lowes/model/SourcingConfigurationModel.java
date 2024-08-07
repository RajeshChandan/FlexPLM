package com.lowes.model;

import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.VersionHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class SourcingConfigurationModel implements GenericModel {
    @Override
    public Collection<FlexTyped> getObject(Object obj, String objectName) throws WTException {
        LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) obj;
        Collection<FlexTyped> flexTypedCollection = new ArrayList<>();

        if (objectName.equalsIgnoreCase("Cost Sheet")) {

            flexTypedCollection.addAll(LCSCostSheetQuery.getAllCostSheetsForSourcingConfig(sourcingConfig));

        } else if (objectName.equalsIgnoreCase("Product")) {

            LCSProduct product = VersionHelper.latestIterationOf(sourcingConfig.getProductMaster());
            flexTypedCollection.add(product);

        } else if (objectName.equalsIgnoreCase("Season")) {

            Collection<LCSSourceToSeasonLink> colSourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLinks(sourcingConfig.getMaster());
            flexTypedCollection.addAll(getSeasonList(colSourceToSeasonLink));

        } else if (objectName.equalsIgnoreCase("Specification")) {

            flexTypedCollection.addAll( getSpecListFromSource(sourcingConfig));


        } else if (objectName.equalsIgnoreCase("Sourcing Configuration to Season")) {

            Collection<FlexTyped> colSourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLinks(sourcingConfig.getMaster());
            flexTypedCollection.addAll(colSourceToSeasonLink);
        }
        return flexTypedCollection;
    }

    private Collection<? extends FlexTyped> getSpecListFromSource(LCSSourcingConfig sourcingConfig) throws WTException {
        Collection<FlexTyped> specCollection = new ArrayList<>();
        LCSProduct product = VersionHelper.latestIterationOf(sourcingConfig.getProductMaster());
        Collection<LCSSourceToSeasonLink> colSourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLinks(sourcingConfig.getMaster());

        for (LCSSourceToSeasonLink stsl : colSourceToSeasonLink) {
            LCSSeason season = VersionHelper.latestIterationOf(stsl.getSeasonMaster());
            if (Objects.nonNull(season)) {
                SearchResults sr = FlexSpecQuery.findExistingSpecs(product, season, sourcingConfig);
                Iterator<?> itr = (Iterator<?> ) sr.getResults();
                while (itr.hasNext()) {
                    FlexSpecification flexSpecification = (FlexSpecification) itr.next();
                    specCollection.add(flexSpecification);
                }
            }
        }
        return specCollection;
    }

    private Collection<? extends FlexTyped> getSeasonList(Collection<LCSSourceToSeasonLink> colSourceToSeasonLink) throws WTException {
        Collection<FlexTyped> seasonCollection = new ArrayList<>();
        for (LCSSourceToSeasonLink stsl : colSourceToSeasonLink) {
            LCSSeason season = VersionHelper.latestIterationOf(stsl.getSeasonMaster());
            if (Objects.nonNull(season)) {
                seasonCollection.add(season);
            }
        }
        return seasonCollection;
    }
}

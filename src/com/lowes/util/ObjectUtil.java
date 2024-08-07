package com.lowes.util;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import org.apache.logging.log4j.Logger;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.vc.Versioned;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Common utility class
 */
public class ObjectUtil {
    private ObjectUtil() {
    }

    private static final Logger logger = LogR.getLogger(ObjectUtil.class.getName());

    /**
     * To get version reference
     * @param var0 Persistable
     * @return String
     */
    public static String getVR(Persistable var0) {
        String var1 = "";

        try {
            if (WorkInProgressHelper.isWorkingCopy((Workable) var0)) {
                var1 = FormatHelper.getVersionId((RevisionControlled) VersionHelper.getFirstVersion((Versioned) var0));
            } else {
                var1 = FormatHelper.getVersionId((RevisionControlled) var0);
            }
        } catch (WTException var3) {
            logger.error("", var3);
        }

        return var1;
    }

    /**
     * To get Season list from primary Source
     * @param sourcingConfig LCSSourcingConfig
     * @return List<LCSSeason>
     * @throws WTException WTException
     */
    public static List<LCSSeason> getSeasonsForSource(LCSSourcingConfig sourcingConfig) throws WTException {
        List<LCSSeason> seasons = new ArrayList<>();

        if (Objects.isNull(sourcingConfig)) {
            logger.info("Sourcing config input is null");
            return seasons;
        }
        Collection<LCSSourceToSeasonLink> colSourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLinks(sourcingConfig.getMaster());

        for (LCSSourceToSeasonLink stsl : colSourceToSeasonLink) {
            LCSSeason season = VersionHelper.latestIterationOf(stsl.getSeasonMaster());
            if (Objects.nonNull(season)) {
                seasons.add(season);
            }
        }
        return seasons;
    }

    /**
     * To get Season list from Specification
     * @param specification FlexSpecification
     * @return List<LCSSeason>
     * @throws WTException WTException
     */
    public static List<LCSSeason> getSeasonsForSpecification(FlexSpecification specification) throws WTException {
        List<LCSSeason> seasons = new ArrayList<>();

        if (Objects.isNull(specification)) {
            logger.info("flex specification is null");
            return seasons;
        }

        Collection<FlexObject> specSeasonUsed = FlexSpecQuery.specSeasonUsed(specification.getMaster());
        for (FlexObject FlexObject : specSeasonUsed) {
            LCSSeason season = (LCSSeason) LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:" + FlexObject.getString("LCSSEASON.BRANCHIDITERATIONINFO"));
            if (Objects.nonNull(season)) {
                seasons.add(season);
            }
        }

        return seasons;
    }

    /**
     * To get Vendor from Source
     * @param sourcingConfig LCSSourcingConfig
     * @return LCSSupplier
     * @throws WTException WTException
     */
    public static LCSSupplier getVendorFromSource(LCSSourcingConfig sourcingConfig) throws WTException {
        LCSSupplier supplier;
        if (Objects.isNull(sourcingConfig)) {
            logger.info("Sourcing config is null ");
            return null;
        }

        supplier = (LCSSupplier) sourcingConfig.getValue("vendor");
        return supplier;
    }
}

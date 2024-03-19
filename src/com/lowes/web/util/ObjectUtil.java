package com.lowes.web.util;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.VersionHelper;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

public class ObjectUtil {
    private static final Logger logger = LogR.getLogger(ObjectUtil.class.getName());

    public static LCSProduct getLatestIteration(LCSProduct product) {
        LCSProduct latest = product;
        try {
            latest = (LCSProduct) VersionHelper.latestIterationOf(latest);
        } catch (WTException e) {
            logger.error(e.getLocalizedMessage());
        }
        return latest;
    }

    public static LCSSeason getLatestIteration(LCSSeason season) {
        LCSSeason latest = season;
        try {
            latest = (LCSSeason) VersionHelper.latestIterationOf(latest);
        } catch (WTException e) {
            logger.error(e.getLocalizedMessage());
        }
        return latest;
    }
}

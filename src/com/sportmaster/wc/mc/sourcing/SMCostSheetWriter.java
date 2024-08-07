package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Date;
import java.util.StringTokenizer;

public class SMCostSheetWriter {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetWriter.class);

    protected static LCSProductCostSheet resetBooleanValue(LCSProductCostSheet lcsProductCostSheet, String attrName, boolean attrValue) throws WTException, WTPropertyVetoException {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetWriter.resetBooleanValue: (Attribute: '" + attrName + "', value: '" + attrValue +"')");
        FlexType cType = lcsProductCostSheet.getFlexType();
        if(cType.getAttributeKeyList().contains(attrName.toUpperCase())) {
            Object value = lcsProductCostSheet.getValue(attrName);
            if(value != null) {
                if ((Boolean) value != attrValue) {
                    lcsProductCostSheet.setValue(attrName, attrValue);
                    //return (LCSProductCostSheet) PersistenceHelper.manager.save(lcsProductCostSheet);
                }
            }
        }
        return lcsProductCostSheet;
    }

    protected static Date updateEffectiveDate(LCSProductCostSheet lcsProductCostSheet, String effectiveDateDefAttr, String effectiveDateAttr) {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetWriter.updateEffectiveDate: ()");
        Date effectiveDate = null;
        try
        {
            if(FormatHelper.hasContent(effectiveDateDefAttr) && lcsProductCostSheet.getValue(effectiveDateAttr) == null)
            {
                StringTokenizer st = new StringTokenizer(effectiveDateDefAttr,".");
                String attType = st.nextToken();
                String attKey = st.nextToken();
                if("Season".equals(attType)) {
                    LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(lcsProductCostSheet.getSeasonMaster());
                    effectiveDate = (Date) season.getValue(attKey);
                } else if("SC".equals(attType)) {
                    LCSSourcingConfig sourceConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(lcsProductCostSheet.getSourcingConfigMaster());
                    effectiveDate = (Date) sourceConfig.getValue(attKey);
                } else if("Product".equals(attType)) {
                    LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(lcsProductCostSheet.getProductMaster(), "A");
                    effectiveDate = (Date) productRevA.getValue(attKey);
                } else if("Product-Season".equals(attType)) {
                    LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(lcsProductCostSheet.getProductMaster(), "A");
                    LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(lcsProductCostSheet.getSeasonMaster());
                    LCSSeasonProductLink productLink = LCSSeasonQuery.findSeasonProductLink(productRevA,season);
                    effectiveDate = (Date) productLink.getValue(attKey);
                }
                if(effectiveDate != null) {
                    lcsProductCostSheet.setValue(effectiveDateAttr, effectiveDate);
                }
            }
            return (Date) lcsProductCostSheet.getValue(effectiveDateAttr);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return new Date();
    }
}

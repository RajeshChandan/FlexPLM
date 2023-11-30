package com.hbi.wc.skusize;

import org.apache.log4j.Logger;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Locale;

import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.skusize.SKUSize;
import com.lcs.wc.skusize.SKUSizeHelper;
import com.lcs.wc.skusize.SKUSizeMaster;
import com.lcs.wc.skusize.SKUSizeQuery;
import com.lcs.wc.skusize.SKUSizeToSeason;
import com.lcs.wc.skusize.SKUSizeToSeasonMaster;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.util.ACLHelper;

/**
 * @author U89260
 *
 */
public class HBISPAutomationSkuSizeLockPlugin {
	public static final String SKU_ALREADY_SENT = LCSProperties.get("com.hbi.wc.interfaces.outbound.sku.HbiAlreadySent",
			"HbiAlreadySent");

	public static final String SKU_ALREADY_SENT_YES = LCSProperties.get("com.hbi.wc.interfaces.outbound.sku.hbiYes",
			"hbiYes");

	public static final String PRODUCT_LOCK_STATUS = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus", "hbiProductEditLockStatus");

	public static final String PRODUCT_LOCK_STATUS_LOCK = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus.hbiLock", "hbiLock");

	public static final String VALIDATION = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.sku.hbiValidation", "hbiValidation");

	/**
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void lockActiveColorwaySize(WTObject obj) throws WTException, WTPropertyVetoException {

		ClientContext context = ClientContext.getContext();
		boolean isAdmin = context.inGroup("ADMINISTRATORS");
		LCSLog.debug("<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin  Started>>>>>>>>>>>>>>>>>>>>");
		if (obj instanceof SKUSize) {
			SKUSize skusize = (SKUSize) obj;
			SKUSize prevSkusize = (SKUSize) VersionHelper.predecessorOf(skusize);
			SKUSizeMaster skuSizeMaster = (SKUSizeMaster) skusize.getMaster();
			LCSSKU sku = (LCSSKU) VersionHelper.latestIterationOf(skuSizeMaster.getSkuMaster());
			sku = (LCSSKU) VersionHelper.getVersion(sku, "A");
			LCSProduct product = sku.getProduct();
			product = (LCSProduct) VersionHelper.getVersion(product, "A");
			String lockStatus = (String) product.getValue(PRODUCT_LOCK_STATUS);
			String HbiAlreadySent = (String) skusize.getValue(SKU_ALREADY_SENT);

			if (PRODUCT_LOCK_STATUS_LOCK.equals(lockStatus) && prevSkusize != null && !skusize.isActive()
					&& prevSkusize.isActive() && "hbiYes".equals(HbiAlreadySent)) {
				LCSLog.debug("<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin  entered>>>>>>>>>>>>>>>>>>>>" + lockStatus);

				skusize.setValue(VALIDATION, "cannot deactivate - Already transfered to SAP");
				skusize.setActive(true);

			}

			LCSLog.debug("<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin  Ended>>>>>>>>>>>>>>>>>>>>");

		}
		if (obj instanceof SKUSizeToSeason) {
			SKUSizeToSeason skusizetoSeason = (SKUSizeToSeason) obj;
			SKUSizeToSeason prevSkusize = (SKUSizeToSeason) VersionHelper.predecessorOf(skusizetoSeason);
			SKUSizeToSeasonMaster skuSizeMasterseas = (SKUSizeToSeasonMaster) skusizetoSeason.getMaster();
			SKUSizeMaster skuSizeMaster = (SKUSizeMaster) skuSizeMasterseas.getSkuSizeMaster();

			LCSSKU sku = (LCSSKU) VersionHelper.latestIterationOf(skuSizeMaster.getSkuMaster());
			sku = (LCSSKU) VersionHelper.getVersion(sku, "A");
			LCSProduct product = sku.getProduct();
			product = (LCSProduct) VersionHelper.getVersion(product, "A");
			String lockStatus = (String) product.getValue("hbiProductEditLockStatus");
			LCSLog.debug("<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin skusizetoSeason lockStatus>>>>>>>>>>>>>>>>>>>>"
					+ lockStatus);
			LCSLog.debug(
					"<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin skusizetoSeason skusize.isActive()>>>>>>>>>>>>>>>>>>>>"
							+ skusizetoSeason.isActive());
			LCSLog.debug(
					"<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin  skusizetoSeason prevSkusize.isActive()>>>>>>>>>>>>>>>>>>>>"
							+ prevSkusize.isActive());
			SKUSize skusize = (SKUSize) VersionHelper.latestIterationOf(skuSizeMaster);
			String HbiAlreadySent = (String) skusize.getValue(SKU_ALREADY_SENT);

			if (PRODUCT_LOCK_STATUS_LOCK.equals(lockStatus) && prevSkusize != null && !skusizetoSeason.isActive()
					&& prevSkusize.isActive() && SKU_ALREADY_SENT_YES.equals(HbiAlreadySent)) {
				LCSLog.debug("<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin skusizetoSeason entered>>>>>>>>>>>>>>>>>>>>"
						+ lockStatus);
				skusize.setValue(VALIDATION, "cannot deactivate - Already transfered to SAP");
				LCSLogic.persist(skusize);
				skusizetoSeason.setActive(true);

			}

			LCSLog.debug("<<<<<<<<<<<HBISPAutomationSkuSizeLockPlugin  Ended>>>>>>>>>>>>>>>>>>>>");

		}

	}
}

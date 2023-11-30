package com.hbi.wc.sizing;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.ProdSizeCategoryToSeason;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.VersionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.lifecycle.State;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author UST
 *
 */

public class HBISPAutomationPSDSeasonDeleteLock {
	public static final String PRODUCT_LOCK_STATUS = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus", "hbiProductEditLockStatus");

	public static final String PRODUCT_LOCK_STATUS_LOCK = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus.hbiLock", "hbiLock");

	public static final String VALIDATION = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.psd.hbiValidation", "hbiValidation");

	/**
	 * @param object
	 * @throws WTException
	 */
	public static void deleteLockForPSDtoSeason(WTObject object) throws WTException {
		ProdSizeCategoryToSeason psdToSeason = (ProdSizeCategoryToSeason) object;

		LCSSeasonProductLink splink = SeasonProductLocator.getSeasonProductLink(
				(LCSProduct) VersionHelper.latestIterationOf(psdToSeason.getSizeCategoryMaster().getProductMaster()));

		LCSProduct product = SeasonProductLocator.getProductSeasonRev(splink);
		if(product.getFlexType().getFullName().contains("SELLING")){
		product = (LCSProduct) VersionHelper.getVersion(product, "A");
		String status = (String) product.getValue(PRODUCT_LOCK_STATUS);
		
		

		if (PRODUCT_LOCK_STATUS_LOCK.equals(status)) {
			throw new LCSException("You cannot remove the PSD as this Product already transfered to SAP");
		}
		}
	}
}

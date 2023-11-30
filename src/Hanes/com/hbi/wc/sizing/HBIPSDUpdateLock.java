package com.hbi.wc.sizing;

import java.util.Set;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author UST
 *
 */
public class HBIPSDUpdateLock {
	
	public static final String PRODUCT_LOCK_STATUS = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus", "hbiProductEditLockStatus");

	public static final String PRODUCT_LOCK_STATUS_LOCK = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus.hbiLock", "hbiLock");

	public static final String VALIDATION = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.psd.hbiValidation", "hbiValidation");

	/**
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void lockpsdupdate(WTObject obj) throws WTException, WTPropertyVetoException {

		if (obj instanceof ProductSizeCategory) {
			ProductSizeCategory psd = (ProductSizeCategory) obj;
			if (psd.getProductMaster() != null) {
				LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(psd.getProductMaster());

				product = (LCSProduct) VersionHelper.getVersion(product, "A");
				if(product.getFlexType().getFullName().contains("SELLING")){

				String lockStatus = (String) product.getValue(PRODUCT_LOCK_STATUS);

				if (PRODUCT_LOCK_STATUS_LOCK.equals(lockStatus)) {
					ProductSizeCategory oldpsd = (ProductSizeCategory) VersionHelper.predecessorOf(psd);
					//Code Changes by wipro team for Incident 739569 -START
//					if (oldpsd != null && !(psd.getSizeValues().contains(oldpsd.getSizeValues()))) {
//						psd.setValue(VALIDATION,
//								"You can only add new sizes but you cannot remove the size which is sent already");
//						psd.setSizeValues(oldpsd.getSizeValues());
//					}
					if (oldpsd != null && !(MOAHelper.getMOASet(psd.getSizeValues()).containsAll(MOAHelper.getMOASet(oldpsd.getSizeValues())))) {
						psd.setValue(VALIDATION,
								"You can only add new sizes but you cannot remove the size which is sent already");
						psd.setSizeValues(oldpsd.getSizeValues());
					}
					//Code Changes by wipro team for Incident 739569 -END
				}

			}
			}
		}
	}
}

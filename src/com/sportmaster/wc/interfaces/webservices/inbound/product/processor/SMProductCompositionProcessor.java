package com.sportmaster.wc.interfaces.webservices.inbound.product.processor;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;

/**
 * @author BSC
 *
 */
public class SMProductCompositionProcessor {

	private static final Logger logger = Logger.getLogger(SMProductCompositionProcessor.class);

	/**
	 * this function copy composition from PS to P If all conditions are true. If at
	 * least 1 cond.is false – do not copy.
	 *
	 * @param psl
	 */
	public void processComposiotnTransfer(LCSProductSeasonLink psl) {

		boolean doCheckin = false;
		boolean conditionA = false;
		boolean conditionB = false;
		boolean conditionC = false;
		LCSProduct p = null;

		try {
			// check first trigger
			if (!validateTrigger(psl)) {
				logger.debug("product season is not valid for Composition transfer");
				return;
			}


			// VALIDATE Composition on Product season is filled in
			if (FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductInboundWebServiceConstants.PSL_UPPER)))
					&& FormatHelper
					.hasContent(String.valueOf(psl.getValue(SMProductInboundWebServiceConstants.PSL_LINNING)))
					&& FormatHelper
					.hasContent(String.valueOf(psl.getValue(SMProductInboundWebServiceConstants.PSL_SOLE)))) {
				conditionB = true;
			}

			// product from product season.
			p = SeasonProductLocator.getProductARev(psl);
			p = (LCSProduct) VersionHelper.latestIterationOf(p);// getting latest version

			// validate Composition P is empty
			if (!FormatHelper.hasContent(String.valueOf(p.getValue(SMProductInboundWebServiceConstants.PROD_UPPER)))
					&& !FormatHelper
					.hasContent(String.valueOf(p.getValue(SMProductInboundWebServiceConstants.PROD_LINNING)))
					&& !FormatHelper
					.hasContent(String.valueOf(p.getValue(SMProductInboundWebServiceConstants.PROD_SOLE)))) {
				conditionA = true;
			}
			logger.debug("Blocked Value >>>>>"+psl.getValue(SMProductInboundWebServiceConstants.PSL_BLOCKED));

			// validate ‘Blocked’ checkbox = YES.
			if ("true".equalsIgnoreCase(String.valueOf(psl.getValue(SMProductInboundWebServiceConstants.PSL_BLOCKED)))) {
				conditionC = true;
			}
			logger.debug("Condition A >>"+conditionA+"<<<Condition B >>"+conditionB+"<<<Condition C >>"+conditionC);
			// if all 3 condition are valid
			if (conditionA && conditionB && conditionC) {

				if (!VersionHelper.isCheckedOut(p)) {
					p = (LCSProduct) VersionHelper.checkout(p);// checking out the object
					doCheckin = true;
				}
				logger.debug("Transfering Composition to Porduct");
				// transfer composition to product
				p.setValue(SMProductInboundWebServiceConstants.PROD_UPPER,
						psl.getValue(SMProductInboundWebServiceConstants.PSL_UPPER));
				p.setValue(SMProductInboundWebServiceConstants.PROD_LINNING, psl.getValue(SMProductInboundWebServiceConstants.PSL_LINNING));
				p.setValue(SMProductInboundWebServiceConstants.PROD_SOLE, psl.getValue(SMProductInboundWebServiceConstants.PSL_SOLE));

				// saving product object
				p = (LCSProduct) LCSLogic.persist(p, true);
				logger.debug("COmosition sucessfully transfered to Porduct");
				// checking out product again
				if (doCheckin && VersionHelper.isCheckedOut(p)) {
					p = (LCSProduct) WorkInProgressHelper.service.checkin(p, "");
					LCSLogic.loadMethodContextCache(p);
				}
				logger.debug("copied composition from PS");
			}
		} catch (WTException | WTPropertyVetoException e) {
			logger.error("Error:-", e);
		}
	}

	/**
	 * validate trigger for bulk order style total value.
	 *
	 * @param psl
	 * @return
	 * @throws WTException
	 */
	public boolean validateTrigger(LCSProductSeasonLink psl) throws WTException {

		boolean valid = false;
		// validate product type
		if (psl.getFlexType().getFullName().startsWith("FPD")) {

			int bulkTotal = FormatHelper
					.parseInt(String.valueOf(psl.getValue(SMProductInboundWebServiceConstants.BULK_ORDER_STYLE_TOTAL)));
			// validate BULK ORDER-STYLE-TOTAL value is > 100
			if (bulkTotal > 1000) {
				valid = true;
			}
		}

		return valid;
	}

}

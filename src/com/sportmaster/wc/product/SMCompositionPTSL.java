package com.sportmaster.wc.product;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;

public class SMCompositionPTSL {

	/**
	 * PSL_COPY_TO_PRODUCTLEVEL.
	 */
	private static final String PSL_COPY_TO_PRODUCTLEVEL = LCSProperties
			.get("com.sportmastercompositionCustomisation.smPSCopyToProductlevel");

	/**
	 * PSL_SOLE.
	 */
	private static final String PSL_SOLE = LCSProperties.get("com.sportmastercompositionCustomisation.smPSSole");
	/**
	 * PSL_LINING.
	 */
	private static final String PSL_LINING = LCSProperties.get("com.sportmastercompositionCustomisation.smPSLining");
	/**
	 * PSL_UPPER.
	 */
	private static final String PSL_UPPER = LCSProperties.get("com.sportmastercompositionCustomisation.smPSUpper");
	/**
	 * PROD_SOLE.
	 */
	private static final String PROD_SOLE = LCSProperties.get("com.sportmastercompositionCustomisation.smProdSole");
	/**
	 * PROD_LINING.
	 */
	private static final String PROD_LINING = LCSProperties.get("com.sportmastercompositionCustomisation.smProdLining");
	/**
	 * PROD_UPPER.
	 */
	private static final String PROD_UPPER = LCSProperties.get("com.sportmastercompositionCustomisation.smProdUpper");
	/**
	 * logger.
	 */
	private static final Logger logger = LogR.getLogger(SMCompositionPTSL.class.getName());
	/**
	 * constructor.
	 */
	private SMCompositionPTSL() {
	}

	/**
	 * Transferring of composition from (PS) to (P).
	 *
	 * @param wtObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void compostionTransfer(WTObject wtObj) throws WTException, WTPropertyVetoException {
		logger.debug("#### Product season link composition transfer customisation - STARTS ####");
		LCSProductSeasonLink ps;
		boolean copyToProduct = false;

		logger.debug("triggered object>>>" + wtObj);
		if (wtObj instanceof LCSProductSeasonLink || wtObj instanceof LCSSeasonProductLink) {

			try {
				ps = (LCSProductSeasonLink) wtObj;
				if (ps.getEffectSequence() > 0) {
					copyToProduct = FormatHelper.parseBoolean(String.valueOf(ps.getValue(PSL_COPY_TO_PRODUCTLEVEL)));
					logger.debug("Copy to product on PS--->" + ps.getValue(PSL_COPY_TO_PRODUCTLEVEL) + "<<---->>" + copyToProduct);

					if (copyToProduct) {

						updateProduct(ps);
						logger.debug("copied composition from PS");
						// setting copy product level back to false.
						ps.setValue(PSL_COPY_TO_PRODUCTLEVEL, "false");
						LCSLogic.persist(ps, true);
						logger.debug("settting PSL_COPY_TO_PRODUCTLEVEL back to false" + PSL_COPY_TO_PRODUCTLEVEL);

					}
				}

			} catch (WTException | WTPropertyVetoException e) {
				logger.error("ERROR OCCURED:", e);
				throw e;
			}
		}
		logger.debug("#### Product season link composition transfer customisation - ENDS ####");
	}

	private static void updateProduct(LCSProductSeasonLink ps) throws WTException, WTPropertyVetoException {

		logger.debug("Inside SMCompositionPTSL updateProduct() - starts");
		boolean doCheckin = false;
		LCSProduct p = SeasonProductLocator.getProductARev(ps);

		if (!VersionHelper.isCheckedOut(p)) {
			// getting latest version
			p = (LCSProduct) VersionHelper.latestIterationOf(p);
			// checking out the object
			p = (LCSProduct) VersionHelper.checkout(p);
			doCheckin = true;
			logger.debug("object is checked out >>>" + doCheckin);
		}

		logger.debug("Updating compostion on product");
		p.setValue(PROD_UPPER, ps.getValue(PSL_UPPER));
		p.setValue(PROD_LINING, ps.getValue(PSL_LINING));
		p.setValue(PROD_SOLE, ps.getValue(PSL_SOLE));

		p = (LCSProduct) LCSLogic.persist(p, true);
		logger.debug("updated compostion on product");

		if (doCheckin && VersionHelper.isCheckedOut(p)) {
			p = (LCSProduct) WorkInProgressHelper.service.checkin(p, "");
			LCSLogic.loadMethodContextCache(p);
			logger.debug("OBJECT is checked in now");
		}
		logger.debug("Inside SMCompositionPTSL updateProduct() - ends");
	}

}

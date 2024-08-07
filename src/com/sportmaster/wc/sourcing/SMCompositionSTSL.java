package com.sportmaster.wc.sourcing;

import java.util.Objects;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLinkMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMCompositionSTSL {

	/**
	 * ERROR_OCCURED.
	 */
	private static final String ERROR_OCCURED = "ERROR OCCURED:";
	/**
	 * FALSE.
	 */
	private static final String FALSE = "false";
	/**
	 * SUPPLIER_COMPOSITIONDOESNOTMATCH_PRODUCTCOMPOSITION.
	 */
	private static final String SUPPLIER_COMPOSITIONDOESNOTMATCH_PRODUCTCOMPOSITION = LCSProperties
			.get("com.sportmastercompositionCustomisation.smSupplierCompositiondoesnotmatchProductcomposition");

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
	 * STSL_SOLE.
	 */
	private static final String STSL_SOLE = LCSProperties.get("com.sportmastercompositionCustomisation.smSole");
	/**
	 * STSL_LINING.
	 */
	private static final String STSL_LINING = LCSProperties.get("com.sportmastercompositionCustomisation.smLining");
	/**
	 * STSL_UPPER.
	 */
	private static final String STSL_UPPER = LCSProperties.get("com.sportmastercompositionCustomisation.smUpper");
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
	 * PSL_BLOCKED.
	 */
	private static final String PSL_BLOCKED = LCSProperties.get("com.sportmastercompositionCustomisation.smPSBlocked");
	/**
	 * SOURCING_STATUS.
	 */
	private static final String SOURCING_STATUS = LCSProperties
			.get("com.sportmastercompositionCustomisation.vrdSourcingStatus");
	/**
	 * logger object.
	 */
	private static final Logger logger = LogR.getLogger(SMCompositionSTSL.class.getName());

	/**
	 * constructor.
	 */
	private SMCompositionSTSL() {}


	/**
	 * Transferring of composition from (PSS) to (PS).
	 *
	 * @param wtObj
	 * @throws LCSException
	 * @throws WTPropertyVetoException
	 */
	public static void compostionTransfer(WTObject wtObj) throws WTPropertyVetoException, LCSException {
		logger.debug("#### Product Source season link composition transfer customisation - starts ####");
		LCSProduct p;
		LCSProductSeasonLink ps;
		LCSSourceToSeasonLink stsl;
		LCSSourceToSeasonLink preSTSL;
		String temp = "";
		boolean conditionA = false;
		boolean conditionB = false;
		boolean conditionC = false;
		boolean conditionD = false;

		if (wtObj instanceof LCSSourceToSeasonLink) {

			try {
				stsl = (LCSSourceToSeasonLink) wtObj;
				preSTSL = (LCSSourceToSeasonLink) VersionHelper.predecessorOf(stsl);
				logger.debug("STSL  -->>>"+stsl);
				logger.debug("pre stsl >>>"+preSTSL);
				//returning as composition is not changed.
				if(!isComposiitonChanged(stsl, preSTSL)) {
					logger.debug("#### Product Source season link composition transfer customisation END - AS sstl compostion not changed####");
					return;
				}
				
				temp = (String) stsl.getValue(SOURCING_STATUS);
				if (FormatHelper.hasContent(temp) && !"vrdInactive".equals(temp)) {
					conditionA = true;
				}

				ps = (LCSProductSeasonLink) SeasonProductLocator
						.getSeasonProductLink((LCSSourceToSeasonLinkMaster) stsl.getMaster());

				logger.info("blcoked balue>>>" + ps.getValue(PSL_BLOCKED));
				// First time PS - Blocked UI shows No, but DB has the value as empty, and next time the value sets as False
				// To allow update STSL when PS Blocked has blank value, added formathelper check.
				if (!FormatHelper.hasContent(String.valueOf(ps.getValue(PSL_BLOCKED))) || FALSE.equals(String.valueOf(ps.getValue(PSL_BLOCKED)))) {
					conditionB = true;
				}

				if (stsl.isPrimarySTSL()) {
					conditionC = true;
				}
				p = SeasonProductLocator.getProductARev(ps);
				p = (LCSProduct) VersionHelper.latestIterationOf(p);
				conditionD = validatecontradict(p, stsl);

				validateComposition(ps, stsl, conditionA, conditionB, conditionC, conditionD);

			} catch (WTException e) {
				logger.error(ERROR_OCCURED, e);
				throw new LCSException(e.getLocalizedMessage());
			} catch (WTPropertyVetoException e) {
				logger.error(ERROR_OCCURED, e);
				throw e;
			}
		}
		logger.debug("#### Product Source season link composition transfer customisation - ends ####");
	}


	/**
	 * @param ps
	 * @param stsl
	 * @param conditionA
	 * @param conditionB
	 * @param conditionC
	 * @param conditionD
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws LCSException
	 */
	private static void validateComposition(LCSProductSeasonLink ps, LCSSourceToSeasonLink stsl, boolean conditionA,
			boolean conditionB, boolean conditionC, boolean conditionD)
			throws WTPropertyVetoException, WTException, LCSException {
		logger.debug("value>>>>" + conditionA + conditionB + conditionC + conditionD);
		if (allValid(conditionA, conditionB)) {
			updateCompostion(conditionC, conditionD, stsl, ps);
		} else {
			updateMassage(conditionA, conditionB);
		}
	}
	/**
	 * validated composition value changed or not.
	 * @param stsl
	 * @param preSTSL
	 * @return
	 * @throws WTException
	 */
	private static boolean isComposiitonChanged(LCSSourceToSeasonLink stsl, LCSSourceToSeasonLink preSTSL) throws WTException {
		boolean isChanged = false;
		boolean preComposition=false;
		boolean curentCompostion = false;
		logger.debug("previous composition---->>"+ preSTSL.getValue(STSL_UPPER) +"--"+preSTSL.getValue(STSL_LINING)+"--"+preSTSL.getValue(STSL_SOLE));
		logger.debug("Curent composition--->>"+ stsl.getValue(STSL_UPPER) +">>"+stsl.getValue(STSL_LINING)+">>"+stsl.getValue(STSL_SOLE));
		
		if (hasContent(stsl.getValue(STSL_UPPER)) && hasContent(stsl.getValue(STSL_LINING))
				&& hasContent(stsl.getValue(STSL_SOLE))) {
			curentCompostion = true;
		}
		if (hasContent(preSTSL.getValue(STSL_UPPER)) && hasContent(preSTSL.getValue(STSL_LINING))
				&& hasContent(preSTSL.getValue(STSL_SOLE))) {
			preComposition = true;
		}
		logger.debug("curentCompostion value >>>"+curentCompostion);
		logger.debug("preComposition value >>>"+preComposition);
		if(curentCompostion && !preComposition) {
			isChanged = true;
		}else if(!curentCompostion && preComposition) {
			isChanged = true;
		}else if(allNotValid(curentCompostion,preComposition)) {
			logger.debug("compostion on SSTL AND PRODUCT is empty");
		}else if (!stsl.getValue(STSL_UPPER).equals(preSTSL.getValue(STSL_UPPER))
				|| !stsl.getValue(STSL_LINING).equals(preSTSL.getValue(STSL_LINING))
				|| !stsl.getValue(STSL_SOLE).equals(preSTSL.getValue(STSL_SOLE))) {
			logger.debug("SSTL Composiiton is changed !!!");
			isChanged = true;
		}
		
		return isChanged;
	}
	private static void updateCompostion(boolean conditionC, boolean conditionD, LCSSourceToSeasonLink stsl,
			LCSProductSeasonLink ps) throws WTPropertyVetoException, WTException {
		
		if (conditionC && conditionD) {

			logger.info(">>>ss>>>>" + stsl.getValue(STSL_UPPER) + "<><><>" + stsl.getValue(STSL_UPPER) + "<><><>"
					+ stsl.getValue(STSL_SOLE) + "<<<<>");
			logger.debug("Copying Composition to Product Season!!!");
			ps.setValue(PSL_UPPER, stsl.getValue(STSL_UPPER));
			ps.setValue(PSL_LINING, stsl.getValue(STSL_LINING));
			ps.setValue(PSL_SOLE, stsl.getValue(STSL_SOLE));
			LCSLogic.persist(ps, true);
			logger.debug("copied Composition to Product Season!!!");

		} else {
			logger.debug("Condition C or D is " + conditionD);
			if (conditionD) {
				logger.debug("setting "+SUPPLIER_COMPOSITIONDOESNOTMATCH_PRODUCTCOMPOSITION+" to false");
				stsl.setValue(SUPPLIER_COMPOSITIONDOESNOTMATCH_PRODUCTCOMPOSITION, false);
			} else {
				logger.debug("setting "+SUPPLIER_COMPOSITIONDOESNOTMATCH_PRODUCTCOMPOSITION+" to true");
				stsl.setValue(SUPPLIER_COMPOSITIONDOESNOTMATCH_PRODUCTCOMPOSITION, true);
			}
		}
	}
	/**
	 * validates Composition PSS does not contradict Composition P (in case
	 * Composition P is filled in) .
	 *
	 * @param p
	 * @param stsl
	 * @return
	 * @throws WTException
	 */
	private static boolean validatecontradict(LCSProduct p,
			LCSSourceToSeasonLink stsl) throws WTException {
		boolean valid = false;
		logger.debug("Product Composition :--upper value--" + p.getValue(PROD_UPPER) + "<<<lining value>>>>" + p.getValue(PROD_LINING)
				+ "<<<<SOLE value>>>>>" + p.getValue(PROD_SOLE));
		logger.debug("Product SOURCE SEAOSN Composition :--upper-val-" + stsl.getValue(STSL_UPPER) + "<<<lining val>>>>"
				+ stsl.getValue(STSL_LINING) + "<<<<SOLE>val >>>>" + stsl.getValue(STSL_SOLE));

		if (!FormatHelper.hasContent(String.valueOf(p.getValue(PROD_UPPER)))
				&& !FormatHelper
				.hasContent(String.valueOf(p.getValue(PROD_LINING)))
				&& !FormatHelper
				.hasContent(String.valueOf(p.getValue(PROD_SOLE)))) {

			return true;
		}
		if (String.valueOf(stsl.getValue(STSL_UPPER))
				.equals(String.valueOf(p.getValue(PROD_UPPER)))
				&& String.valueOf(stsl.getValue(STSL_LINING))
				.equals(String.valueOf(p.getValue(PROD_LINING)))
				&& String.valueOf(stsl.getValue(STSL_SOLE))
				.equals(String.valueOf(p.getValue(PROD_SOLE)))) {
			valid = true;
		}
		return valid;

	}
	private static void updateMassage(boolean conditionA, boolean conditionB) throws LCSException {

		if (conditionA && !conditionB) {
			throw new LCSException("Composition for this Product is freezed by SM");
		} else if (!conditionA && conditionB) {
			throw new LCSException("Supplier is cancelled on season");
		} else if (allNotValid(conditionA, conditionB)) {
			throw new LCSException("Supplier is cancelled on season or Composition for this Product is freezed by SM");
		}

	}

	public static String getErrMassage(LCSSourceToSeasonLink stsl) throws WTException {
		
		logger.debug("#### START - Compostion get error message plugin");
		LCSProduct p;
		LCSProductSeasonLink ps;
		LCSSourceToSeasonLink preSTSL;
		String temp = "";
		String returnMsg = "";
		boolean condition1 = false;
		boolean condition2 = false;
		boolean condition3 = false;
		boolean condition4 = false;

		try {
			preSTSL = (LCSSourceToSeasonLink) VersionHelper.predecessorOf(stsl);
			logger.debug("STSL  -->>>"+stsl);
			logger.debug("pre stsl >>>"+preSTSL);
			//returning as composition is not changed.
			if(!isComposiitonChanged(stsl, preSTSL)) {
				return "";
			}
			temp = (String) stsl.getValue(SOURCING_STATUS);
			if (FormatHelper.hasContent(temp) && !"vrdInactive".equals(temp)) {
				condition1 = true;
			}

			ps = (LCSProductSeasonLink) SeasonProductLocator
					.getSeasonProductLink((LCSSourceToSeasonLinkMaster) stsl.getMaster());
			logger.info("blcoked balue>>>" + ps.getValue(PSL_BLOCKED));
			// First time PS - Blocked UI shows No, but DB has the value as empty, and next time the value sets as False
			// To allow update STSL when PS Blocked has blank value, added formathelper check.
			if (!FormatHelper.hasContent(String.valueOf(ps.getValue(PSL_BLOCKED))) || FALSE.equals(String.valueOf(ps.getValue(PSL_BLOCKED)))) {
				condition2 = true;
			}

			if (stsl.isPrimarySTSL()) {
				condition3=true;
			}
			p = SeasonProductLocator.getProductARev(ps);
			p = (LCSProduct) VersionHelper.latestIterationOf(p);
			if (!FormatHelper.hasContent(String.valueOf(p.getValue(PROD_UPPER)))
					&& !FormatHelper.hasContent(String.valueOf(p.getValue(PROD_LINING)))
					&& !FormatHelper.hasContent(String.valueOf(p.getValue(PROD_SOLE)))) {
				condition4 = true;
			}

			if (String.valueOf(stsl.getValue(STSL_UPPER)).equals(String.valueOf(p.getValue(PROD_UPPER)))
					&& String.valueOf(stsl.getValue(STSL_LINING)).equals(String.valueOf(p.getValue(PROD_LINING)))
					&& String.valueOf(stsl.getValue(STSL_SOLE)).equals(String.valueOf(p.getValue(PROD_SOLE)))) {
				condition4 = true;
			}
			logger.debug("Product Composition :--upper--" + p.getValue(PROD_UPPER) + "<<<lining>>>>"
					+ p.getValue(PROD_LINING) + "<<<<SOLE>>>>>" + p.getValue(PROD_SOLE));
			logger.debug("Product SOURCE SEAOSN Composition :--upper--" + stsl.getValue(STSL_UPPER) + "<<<lining>>>>"
					+ stsl.getValue(STSL_LINING) + "<<<<SOLE>>>>>" + stsl.getValue(STSL_SOLE));
			logger.debug("value>>>>" + condition1 + condition2 + condition3 + condition4);
			
			if (condition1 && condition2 && anyNotValid(condition3, condition4)) {
				returnMsg = getMessage(condition4);
			}

		} catch (WTException e) {
			logger.error(ERROR_OCCURED, e);
			throw e;
		}
		logger.debug("#### START - Compostion get error message plugin");
		return returnMsg;
		
	}

	/**
	 * validates boolean filed return massage.
	 *
	 * @param var1
	 * @return
	 */
	private static String getMessage(boolean var1) {
		String str = "";
		if (!var1) {
			str = "Entered composition contradicts product composition fixed in the previous collection. Please contact Sportmaster.";
		}

		return str;

	}

	/**
	 * validate boolean field is not valid.
	 *
	 * @param var1
	 * @param var2
	 * @return
	 */
	private static boolean anyNotValid(boolean var1, boolean var2) {

		boolean var3 = false;
		if (!var1 || !var2) {
			var3 = true;
		}

		return var3;

	}

	/**
	 * validate all boolean field is not valid.
	 *
	 * @param var1
	 * @param var2
	 * @return
	 */
	private static boolean allNotValid(boolean var1, boolean var2) {

		boolean var3 = false;
		if (!var1 && !var2) {
			var3 = true;
		}

		return var3;

	}

	/**
	 * validate all boolean field is valid.
	 *
	 * @param var1
	 * @param var2
	 * @return
	 */
	private static boolean allValid(boolean var1, boolean var2) {

		boolean var3 = false;
		if (var1 && var2) {
			var3 = true;
		}

		return var3;

	}
private static final boolean hasContent(final Object obj) {
	String str = String.valueOf(obj);
	return Objects.nonNull(str) && FormatHelper.hasContent(str);
}
}

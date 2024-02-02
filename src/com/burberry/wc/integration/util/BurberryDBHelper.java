package com.burberry.wc.integration.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


import wt.util.WTException;

import com.burberry.wc.integration.exception.InvalidInputException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.util.FormatHelper;

/**
 * A utility class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Start Date, End Date, Season name is Primary input to fetch
 * detail from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryDBHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryDBHelper.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * STR_QUESTION.
	 */
	private static final String STR_QUESTION = "?";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	private static final String STR_VAL_ZERO="0";

	private BurberryDBHelper() {

	}

	/**
	 * Method pars input @param and execute DB query and return the search.
	 * result
	 * 
	 * @param argKeys
	 * @return
	 * @throws ParseException
	 * @throws WTException
	 * @throws InvalidInputException
	 */
	public static Collection<?> getProducts(final Date startdate, Date enddate,
			final String seasons) throws ParseException, WTException,
			InvalidInputException {

		String methodName = "getProducts() ";
		logger.debug(methodName + " reading query param");
		logger.debug(methodName + " startdate: " + startdate + ", enddate: " + enddate + ", seasons: " + seasons);
		
		// call product from db method
		return getProductFromDB(startdate, enddate, seasons);

	}

	/**
	 * Return collection of Flex object.
	 * 
	 * @param startdate
	 * @param enddate
	 * @param season
	 * @return
	 * @throws WTException
	 * @throws InvalidInputException
	 */
	private static Collection<?> getProductFromDB(final Date startdate,
			final Date enddate, final String season) throws WTException,
			InvalidInputException {

		String methodName = "getProductFromDB() ";
		
		Collection<?> result = null;

		// allow user as administrator to access data from DB
		//SessionHelper.manager.setAdministrator();
		final FlexType ftype = FlexTypeCache
				.getFlexTypeRootByClass((LCSSKUSeasonLink.class).getName());

		final StringTokenizer seasonToken = (season != null && !season.trim().isEmpty()) ? new StringTokenizer(
				season, "|") : null;
		logger.debug(methodName + " seasonToken: " + seasonToken);
		
		// season Values are passed to get the 
		// corresponding key value to run in the query
		final List<String> seasonKeys = new ArrayList<String>();
		if (seasonToken != null) {
			while (seasonToken.hasMoreTokens()) {
				// method called to get corresponding key value for season
				// attribute
				final String seasonValue = getKeyForSeason(
						seasonToken.nextToken(), ftype);
				if (FormatHelper.hasContent(seasonValue)) {
					logger.debug(methodName + "season key for season-param value: " + seasonValue);
					seasonKeys.add(seasonValue);
				}
			}
			logger.debug(methodName + " seasonKeys: " + seasonKeys);
			
			if (seasonKeys.isEmpty()) {
				throw new InvalidInputException(
						BurConstant.STR_NO_SEASON_TYPE_FOUND);
			}
		}
		logger.debug(methodName + "Executing DB querry");
		
		// execute query using above parameters
		result = LCSQuery.runDirectQuery(
				createDBQuerry(startdate, enddate, seasonKeys, ftype))
				.getResults();
		logger.debug(methodName + "result " + result);
		logger.error(methodName + "DB querry executed. Result returned");
		
		//return
		return result;
	}

	/**
	 * @param season
	 * @param ftype
	 * @return
	 * @throws WTException
	 */
	private static String getKeyForSeason(final String season,
			final FlexType ftype) throws WTException {

		String methodName = "getKeyForSeason() ";
		String listValueKey = "";
		
		// get value list for colourway season
		final AttributeValueList attValueList = ftype.getAttribute(
				BurConstant.BUR_SEASON).getAttValueList();
		logger.debug(methodName + "Colourway Season attValueList: " + attValueList.getAllValues(null));
		
		// get all keys for value list
		final Collection<String> keys = attValueList.getKeys();
		//
		for (final String key : keys) {
			final String value = attValueList.getValue(key, null);
			logger.debug(methodName + "attValueList key: " + value);
			logger.debug(methodName + "season param value: " + season);
			// check if colourway season list value contains season param value
			if (season.equalsIgnoreCase(value)) {
				listValueKey = key;
				logger.debug(methodName + "season param matches with Colourway Season attValueList");
				break;
			}
		}
		// return
		return listValueKey;
	}

	/**
	 * Responsible to create PreparedQueryStatement with given parameter.
	 * 
	 * @param startdate
	 * @param enddate
	 * @param season
	 * @param ftype
	 * @return
	 * @throws WTException
	 */
	private static PreparedQueryStatement createDBQuerry(final Date startdate,
			final Date enddate, final List<String> season, final FlexType ftype)
			throws WTException {

		logger.debug("creating PreparedQueryStatement with input.");
		
		int i = 0;
		final PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.setDistinct(true);
		
		// append tables
		statement.appendFromTable(LCSProduct.class);
		statement.appendFromTable(LCSSKU.class);
		statement.appendFromTable(LCSSKUSeasonLink.class);
		statement.appendFromTable(LCSProductSeasonLink.class);
		
		// add select columns
		statement.appendSelectColumn(new QueryColumn(LCSProduct.class,
				BurConstant.LCS_PRODUCT_OBJECTIDENTIFIERID));
		statement.appendOpenParen();
		/*
		 * checking if product was updated between given dates
		 */
		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
				BurConstant.MODIFY_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
				BurConstant.MODIFY_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		statement.appendClosedParen();
		statement.appendOr();
		/*
		 * checking if colorway was updated between given dates
		 */
		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSKU.class,
				BurConstant.MODIFY_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSKU.class,
				BurConstant.MODIFY_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		statement.appendClosedParen();
		statement.appendOr();
		/*
		 * checking if colorway season was updated between given dates
		 */
		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.MODIFY_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.MODIFY_STAMP), enddate,
				Criteria.LESS_THAN_EQUAL));
		statement.appendClosedParen();
		statement.appendOr();
		/*
		 * checking if product season was updated between given dates
		 */
		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.MODIFY_STAMP),
				startdate, Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.MODIFY_STAMP), enddate,
				Criteria.LESS_THAN_EQUAL));
		statement.appendClosedParen();
		statement.appendClosedParen();
		statement.appendAnd();
		if (!season.isEmpty()) {
			statement.appendOpenParen();
			for (final String seasonName : season) {
				if (i > 0) {
					statement.appendOr();
				}
				statement.appendCriteria(
						new Criteria(new QueryColumn(
								BurConstant.LCSSKUSEASONLINK, ftype
										.getAttribute(BurConstant.BUR_SEASON)
										.getColumnName()), STR_QUESTION,
								STR_EQUAL), seasonName);
				i++;
			}
			statement.appendClosedParen();
			statement.appendAnd();
		}
	
		statement
				.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
						Criteria.EQUALS));
		statement.appendAnd();
		statement
				.appendCriteria(new Criteria(new QueryColumn(LCSSKU.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
						Criteria.EQUALS));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.EFFECTLATEST), STR_VAL_ONE,
				Criteria.EQUALS));
		//start of fix for BURBERRY-1063		
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(
				BurConstant.LCSSKUSEASONLINK, BurConstant.SEASONREMOVED),
				STR_VAL_ZERO, Criteria.EQUALS));
		//end of fix for BURBERRY-1063
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
				STR_VAL_ONE, Criteria.EQUALS));
		//start of fix for BURBERRY-1063
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.SEASONREMOVED),
				STR_VAL_ZERO, Criteria.EQUALS));
		//end of fix for BURBERRY-1063
		statement.appendAnd();
		statement
				.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
						Criteria.EQUALS));
		statement.appendAnd();
		statement
				.appendCriteria(new Criteria(new QueryColumn(LCSSKU.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
						Criteria.EQUALS));
		statement.appendJoin(BurConstant.LCSPRODUCT, BurConstant.BRANCHID,
				BurConstant.LCSSKU, BurConstant.PRODUCTAREVID);
		statement.appendJoin(BurConstant.LCSSKU,
				BurConstant.IDA3MASTERREFEREN_CE, BurConstant.LCSSKUSEASONLINK,
				BurConstant.SKUMASTERID);
		statement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID);
		statement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE, BurConstant.LCSSKUSEASONLINK,
				BurConstant.PRODUCTMASTERID);

		logger.info("PreparedQueryStatement  created  successfully.."
				+ statement.toString());
		return statement;
	}
}

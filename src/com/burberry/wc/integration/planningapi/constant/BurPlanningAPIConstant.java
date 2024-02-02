package com.burberry.wc.integration.planningapi.constant;

import com.lcs.wc.util.LCSProperties;

public final class BurPlanningAPIConstant {
	
	/**
	 * BurPlanningAPIConstant.
	 */
	private BurPlanningAPIConstant() {

	}

	/**
	 * STR_PLANNING_VALID_OBJECTS.
	 */
	public static final String STR_PLANNING_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.planningapi.validObjects");
	/**
	 * PLANNING_API_LOG_ENTRY_FLEXTYPE.
	 */
	public static final String PLANNING_API_LOG_ENTRY_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.planningapi.logentry");
	/**
	 * STR_ERROR_MSG_PLANNING_API.
	 */
	public static final String STR_ERROR_MSG_PLANNING_API = LCSProperties
			.get("com.burberry.integration.planningapi.errormessage");
	
	/**
	 * PLAN_ATT.
	 */
	public static final String PLAN_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.jsonattributes.plan");
	
	/**
	 * PLAN_DETAIL_ATT.
	 */
	public static final String PLAN_DETAIL_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.jsonattributes.planDetail");
	
	/**
	 * SEASON_ATT.
	 */
	public static final String SEASON_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.jsonattributes.season");
	
	/**
	 * JSON_PLAN_ATT.
	 */
	public static final String JSON_PLAN_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.jsonattributes.mapping.plan");
	
	/**
	 * JSON_PLAN_DETAIL_ATT.
	 */
	public static final String JSON_PLAN_DETAIL_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.jsonattributes.mapping.planDetail");
	
	/**
	 * JSON_SEASON_ATT.
	 */
	public static final String JSON_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.jsonattributes.mapping.season");
	
	/**
	 * SYSTEM_JSON_PLAN_ATT.
	 */
	public static final String SYSTEM_JSON_PLAN_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.system.jsonattributes.mapping.plan");
	
	/**
	 * SYSTEM_JSON_PLAN_DETAIL_ATT.
	 */
	public static final String SYSTEM_JSON_PLAN_DETAIL_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.system.jsonattributes.mapping.planDetail");
	
	/**
	 * SYSTEM_JSON_SEASON_ATT.
	 */
	public static final String SYSTEM_JSON_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.planningapi.system.jsonattributes.mapping.season");
	
	/**
	 * PLAN_ATT_REQ.
	 */
	public static final String PLAN_ATT_REQ = LCSProperties
			.get("com.burberry.integration.planningapi.requiredattributes.plan");
	
	/**
	 * PLAN_DETAIL_ATT_REQ.
	 */
	public static final String PLAN_DETAIL_ATT_REQ = LCSProperties
			.get("com.burberry.integration.planningapi.requiredattributes.planDetail");
	
	/**
	 * SEASON_ATT_REQ.
	 */
	public static final String SEASON_ATT_REQ = LCSProperties
			.get("com.burberry.integration.planningapi.requiredattributes.season");
	
	/**
	 * PLAN_ATT_IGNORE.
	 */
	public static final String PLAN_ATT_IGNORE = LCSProperties
			.get("com.burberry.integration.planningapi.ignoreattributes.plan");
	
	/**
	 * PLAN_DETAIL_ATT_IGNORE.
	 */
	public static final String PLAN_DETAIL_ATT_IGNORE = LCSProperties
			.get("com.burberry.integration.planningapi.ignoreattributes.planDetail");
	
	/**
	 * SEASON_ATT_IGNORE.
	 */
	public static final String SEASON_ATT_IGNORE = LCSProperties
			.get("com.burberry.integration.planningapi.ignoreattributes.season");
	/**
	 * FLEXPLAN.
	 */
	public static final String FLEXPLAN = "FlexPlan";
	/**
	 * PLANLINEITEM.
	 */
	public static final String PLANLINEITEM = "PlanLineItem";
	/**
	 * PLANMASTERREF.
	 */
	public static final String PLANMASTERREF = "planReference.key.id";
	/**
	 * PREV_IDA2A2.
	 */
	public static final Object PREV_IDA2A2 = "prevIda2a2";
	/**
	 * PLAN_BRANCHID.
	 */
	public static final String PLAN_IDA2A2 = "FLEXPLAN.IDA2A2";
	/**
	 * BRANCHID.
	 */
	public static final String BRANCHID = "branchId";
	/**
	 * SORTINGNUMBER.
	 */
	public static final String SORTINGNUMBER = "sortingnumber";
	/**
	 * PARENTID.
	 */
	public static final String PARENTID = "parentId";

	/**
	 * VRD_PREV_PLAN.
	 */
	public static final String VRD_PREV_PLAN = LCSProperties.get("com.burberry.integration.planningapi.vrdprevplan");

	/**
	 * BUR_PREV_PLAN.
	 */
	public static final String BUR_PREV_PLAN = LCSProperties.get("com.burberry.integration.planningapi.burprevplan");
	/**
	 * ACCESSORIES_TYPE.
	 */
	public static final String ACCESSORIES_TYPE = LCSProperties.get("com.burberry.integration.planningapi.acessoriestype");
	/**
	 * BUR_PLAN_STATUS.
	 */
	public static final String BUR_PLAN_STATUS = LCSProperties.get("com.burberry.integration.planningapi.burplanstatus");
	/**
	 * VRD_PLAN_STATUS.
	 */
	public static final String VRD_PLAN_STATUS = LCSProperties.get("com.burberry.integration.planningapi.vrdplanstatus");
	/**
	 * PLAN_ID.
	 */
	public static final String PLAN_ID = "VR:com.lcs.wc.planning.FlexPlan:";
	

}

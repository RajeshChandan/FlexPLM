package com.sportmaster.wc.reports;

import wt.util.WTException;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;

/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public final class SMMaterialForecastReportFabricHelper {

	private static final org.apache.log4j.Logger LOGGER=Logger.getLogger("MFDRLOG");

	private static final String FABRIC_MATERIAL = "FABRIC";

	private static final String LCSMATERIALSUPPLIER_FABRIC = "FABRIC_SUPPLIER";

	 
	/**
	 * Constant LCS_MATERIAL.
	 */
	private static final String LCS_MATERIAL = "LCSMaterial";
	 
	private static final String LATEST_ITERATION_INFO = "iterationInfo.latest";
 
	/**
	 * Constant MASTER_REFERENCE_KEY_ID.
	 */
	private static final String MASTER_REFERENCE_KEY_ID = "masterReference.key.id";
 
	
	/**
	 * Constant THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID.
	 */
	private static final String THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID = "thePersistInfo.theObjectIdentifier.id";

	/**
	 * @param pqs
	 * @return
	 * @throws WTException
	 */
	public static PreparedQueryStatement  appendFabricMaterialType(
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException {
		LOGGER.debug("appending fabric material type column");
		pqs.appendSelectColumn(new QueryColumn(FABRIC_MATERIAL,LCSMaterial.class, MASTER_REFERENCE_KEY_ID));
		pqs.appendSelectColumn( new QueryColumn(FABRIC_MATERIAL, LCSMaterial.class,THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendJoin(new QueryColumn(LCS_MATERIAL,LCSMaterial.class, MASTER_REFERENCE_KEY_ID+"(+)"), new QueryColumn(FABRIC_MATERIAL, LCSMaterial.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FABRIC_MATERIAL,LCSMaterial.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		return pqs;
	}
	
	/**
	 * @param pqs
	 * @return
	 * @throws WTException
	 */
	public static PreparedQueryStatement appendFabricSupplierType(
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException {
		LOGGER.debug("appending fabric material supplier type column");

		pqs.appendFromTable(LCSMaterialSupplier.class,LCSMATERIALSUPPLIER_FABRIC);
		pqs.appendSelectColumn(new QueryColumn(LCSMATERIALSUPPLIER_FABRIC,LCSMaterialSupplier.class, "state.state"));
		pqs.appendSelectColumn(new QueryColumn(LCSMATERIALSUPPLIER_FABRIC,LCSMaterialSupplier.class, "iterationInfo.branchId"));

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplier.class, MASTER_REFERENCE_KEY_ID+"(+)"),
				new QueryColumn(LCSMATERIALSUPPLIER_FABRIC, LCSMaterialSupplier.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSMATERIALSUPPLIER_FABRIC,LCSMaterialSupplier.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		
		return pqs;
	}


}

package com.hbi.wc.migration.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import com.hbi.wc.load.sploader.HBISPBomUtil_old;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSKUSourcingLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.season.LCSSeasonMaster;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * 
 * @author Manoj Konakalla
 * @Date June-30-2019
 * 
 */
public class HBIColorwaySourcingUtility implements RemoteAccess {
	private static final String SELLING_PRODUCT = LCSProperties.get("com.hbi.wc.load.sploader.product.type",
			"Product\\BASIC CUT & SEW - SELLING");
	private static final String SP_UNIQUE_KEY = LCSProperties.get("com.hbi.wc.load.sploader.product.uniqueKey",
			"hbiPLMNo");
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;

	
	public static void main(String[] args) {
		System.out.println("### START HBIColorwaySourcingUtility.main() ###");

		try {
			if (args.length != 1) {
				System.out.println("windchill com.hbi.wc.migration.loader.HBIColorwaySourcingUtility <input>");
			}

			remoteMethodServer = RemoteMethodServer.getDefault();
			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			Class<?> argTypes[] = { String.class };
			String argValues[] = { args[0] };
			remoteMethodServer.invoke("enableSkuSource", "com.hbi.wc.migration.loader.HBIColorwaySourcingUtility", null,
					argTypes, argValues);
			System.exit(0);
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(1);
		}

		System.out.println("### END HBIColorwaySourcingUtility.main() ###");
	}

	@SuppressWarnings("rawtypes")
	public static void enableSkuSource(String input) throws WTException {
		if ("all".equals(input)) {
			Collection<LCSProduct> sps= getAllSPs();
			Iterator itr = sps.iterator();
			while(itr.hasNext()){
				LCSProduct sp = (LCSProduct) itr.next();
				HBISPBomUtil_old.debug("SP NAME :: ["+sp.getName()+"]");	
				try{
				enableSkuSource(sp);
				}catch(Exception e){
					String error = "!!!! Exception occured for Product [" + sp.getName() + "]";
					HBISPBomUtil_old.debug("error"+error);
					// ERROR Report 
					HBISPBomUtil_old.exception(error, e);
				}
			}
		} else {
			getAllSPs(input);
		}
	}

	/**
	 * @param sp
	 * @throws WTException
	 * @throws WTPropertyVetoException 
	 */
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	private static void enableSkuSource(LCSProduct sp) throws WTException, WTPropertyVetoException {
		
		Collection<LCSSKU> skuObjects = sp.findSKUObjects();
		HBISPBomUtil_old.debug("!!!! Total Colorways [" +skuObjects.size()+"], Found on SP :: ["+sp.getName()+"]");
		
		Iterator itr = skuObjects.iterator();
		while(itr.hasNext()){
			LCSSKU sku = (LCSSKU) itr.next();
			HBISPBomUtil_old.debug("!!!! Started Processing of SKU  :: ["+sku.getName()+"]");
	//		WTPartMaster prodMaster = (WTPartMaster) sp.getMaster();
//			WTPartMaster seasonMaster = (WTPartMaster) season.getMaster();
			LCSSeason season = HBISPBomUtil_old.getSPSeason(sp);
			LCSPartMaster prodMaster = (LCSPartMaster) sp.getMaster();
			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) season.getMaster();
			HBISPBomUtil_old.debug("!!!! Season fetched  :: ["+season.getName()+"]");
			LCSSourcingConfig srcCfg = LCSSourcingConfigQuery.getPrimarySource(prodMaster, seasonMaster);
			LCSSKUSourcingLink ssl = new LCSSourcingConfigQuery().getSKUSourcingLink(srcCfg, sku, season);
			if(ssl !=null){
			ssl.setActive(true);
			LCSLogic.persist(ssl,true);
			}else{
				HBISPBomUtil_old.exception("!!!! ERROR Processing of SKU  :: ["+sku.getName()+"]",new Exception("Error"));
				
			}
			HBISPBomUtil_old.debug("!!!! Completed Processing of SKU  :: ["+sku.getName()+"]");
		}
		HBISPBomUtil_old.debug("!!!! Completed Enabling SKU Source to Season for SP :: ["+sp.getName()+"]");
	}

	private static void getAllSPs(String input) throws WTException {
		String products[] = input.split(",");
		for(String product :products){
			LCSProduct sp = getProductBySapKey(product);
			try{
				enableSkuSource(sp);
				}catch(Exception e){
					String error = "!!!! Exception occured for Product [" + sp.getName() + "]";
					HBISPBomUtil_old.debug("error"+error);
					// ERROR Report 
					HBISPBomUtil_old.exception(error, e);
				}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Collection<LCSProduct> getAllSPs() throws WTException {
		FlexType prdType = FlexTypeCache.getFlexTypeFromPath(SELLING_PRODUCT);

		Collection<LCSProduct> sps = new ArrayList();
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("prodarev", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria("product", "flexTypeIdPath", prdType.getTypeIdPath(), Criteria.EQUALS));
		stmt.appendClosedParen();

		HBISPBomUtil_old.debug("stmt::{" + stmt.toString() + "}");
		Collection<FlexObject> output = new ArrayList();
		output = LCSQuery.runDirectQuery(stmt).getResults();
		HBISPBomUtil_old.debug("size::[ " + output.size() + " ]");
		Iterator itr = output.iterator();
		while (itr.hasNext()) {
			FlexObject obj = (FlexObject) itr.next();
			LCSProduct sp = (LCSProduct) LCSQuery
					.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
			if (sp != null) {
				sps.add(sp);
			}

		}
		return sps;

	}
	
	/**
	 * @param product_key
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LCSProduct getProductBySapKey(String product_key) throws WTException {

        String hbiPLMNo_DB_Col = FlexTypeCache.getFlexTypeFromPath(SELLING_PRODUCT).getAttribute(SP_UNIQUE_KEY).getVariableName();
        LCSProduct product = null;
        PreparedQueryStatement stmt = new PreparedQueryStatement();
        stmt.appendFromTable("LCSProduct", "product");
        stmt.appendSelectColumn("product", "ida2a2");
        stmt.appendOpenParen();
        stmt.appendCriteria(new Criteria("product", hbiPLMNo_DB_Col, product_key, Criteria.EQUALS));
        stmt.appendAnd();
        stmt.appendCriteria(new Criteria("product", "latestIterationInfo", "1", Criteria.EQUALS));
        stmt.appendAnd();
        stmt.appendCriteria(new Criteria("product", "versionida2versioninfo", "A", Criteria.EQUALS));
        stmt.appendClosedParen();

        HBISPBomUtil_old.debug("stmt::{" + stmt.toString()+"}");
        Collection<FlexObject> output  = new ArrayList();
        output = LCSQuery.runDirectQuery(stmt).getResults();
        HBISPBomUtil_old.debug("size::[ " + output.size()+" ]");
        if (output.size() == 1) {
             FlexObject obj = (FlexObject) output.iterator().next();
             product = (LCSProduct) LCSQuery
                     .findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
             HBISPBomUtil_old.debug("******Selling Product [ "+product.getName()+" ] found with SAP_KEY/PLM NO ["+product_key+"]");
             return product;
        }else{
        	HBISPBomUtil_old.debug("!!!!! No SP Product found with SAP_KEY/PLM NO ["+product_key+"]");
        }
        return product;
    }
}

	
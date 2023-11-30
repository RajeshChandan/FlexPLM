package com.vrd.costsheet;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.specification.FlexSpecMaster;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.ptc.core.meta.common.RemoteWorker;
import com.ptc.core.meta.common.RemoteWorkerHandler;

import wt.method.RemoteMethodServer;

public class CostSheetBOMUtil {
	
	public static String BOMRefAttrName = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMReference", "vrdBOMReference");
	public static String BOMEnableAttrName = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMRollUp", "vrdDoBOMRollup");
	private LCSProductCostSheet cs = null;
	private LCSSeason ssn = null;
	private LCSProduct prodA = null;
	private FlexSpecification spec = null;
	
	public CostSheetBOMUtil(String objectId)		throws Exception {
		LCSPartMaster productMaster = null;
		FlexSpecMaster specMaster = null;
		LCSSeasonMaster ssnMaster = null;
		
		if (objectId.indexOf("LCSProductCostSheet") > 0)	{
			cs = (LCSProductCostSheet) LCSCostSheetQuery.findObjectById(objectId);		
			specMaster = cs.getSpecificationMaster();
			ssnMaster = cs.getSeasonMaster();
		}
		else if (objectId.indexOf("FlexSpecToSeasonLink") > 0)	{
			FlexSpecToSeasonLink specLink = (FlexSpecToSeasonLink) LCSQuery.findObjectById(objectId);
			ssnMaster = specLink.getSeasonMaster();
			specMaster = specLink.getSpecificationMaster();						
		}
		spec = null != specMaster ? (FlexSpecification) VersionHelper.latestIterationOf(specMaster) : null;
		productMaster = (LCSPartMaster) spec.getSpecOwner();
		prodA = null != productMaster ? SeasonProductLocator.getProductARev(productMaster) : null;
		ssn = null != ssnMaster ? (LCSSeason)VersionHelper.latestIterationOf(ssnMaster) : null;		
	}
	
	public String getBOMs()	throws Exception {		
		return null != spec ? getBOMs(spec).toString() : "";
	}
	
	private JSONArray getBOMs(FlexSpecification spec)		throws Exception {
		Collection<FlexBOMPart> boms = FlexSpecQuery.getSpecComponents(spec,"BOM");
		JSONArray jsonArr = new JSONArray();
		for (FlexBOMPart bom: boms)		{
			if (!"LABOR".equals(bom.getBomType())) {
				JSONObject json = new JSONObject();
				json.put("id", FormatHelper.getVersionId(bom));
				json.put("name", bom.getName());
				jsonArr.put(json);
			}
		}
		return jsonArr;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try	{
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			rms.setUserName("wcadmin");
			rms.setPassword("ptc");
			
			RemoteWorkerHandler.handleRemoteWorker(new CostSheetBOMUtilRemoteWorker(), "OR:com.lcs.wc.specification.FlexSpecToSeasonLink:"+args[0]);
		}
		catch (Exception e)	{
			e.printStackTrace();
		}
		System.exit(0);
	}

}

class CostSheetBOMUtilRemoteWorker extends RemoteWorker	{

	@Override
	public Object doWork(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		CostSheetBOMUtil util = new CostSheetBOMUtil((String) arg0);
		String s = util.getBOMs();
		LCSLog.debug("VRD>>>>> CostSheetBOMUtil.CostSheetBOMUtilRemoteWorker: BOMs = "+s);
		return s;
	}
	
}

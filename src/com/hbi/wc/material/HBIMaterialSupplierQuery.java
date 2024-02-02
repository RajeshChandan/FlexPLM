package com.hbi.wc.material;

import com.lcs.wc.client.*;
import com.lcs.wc.client.web.*;
import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.load.*;
import com.lcs.wc.util.LCSProperties;
import java.io.Externalizable;
import javax.servlet.ServletRequest;
import wt.part.WTPartMaster;
import wt.util.WTException;
import com.lcs.wc.material.*;
import wt.org.WTUser;
import java.util.*;
import com.lcs.wc.report.*;
import wt.fc.*;
import wt.part.WTPartMaster;
import wt.query.*;
import wt.vc.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.foundation.LCSPluginManager;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;



public class HBIMaterialSupplierQuery extends LCSQuery implements Externalizable {

	  public static String MATERIAL_NUM_ID = "MATERIAL_NUM_ID";
      public static String SUPPLIER_MASTER_NUM_ID = "SUPPLIER_MASTER_NUM_ID";


    public HBIMaterialSupplierQuery() {

    }

	public SearchResults findMaterialSupplierAttributes(Map map, FlexType flextype, Collection collection, FiltersList filterslist) throws WTException  {


   		//FlexTypeAttribute nameAtt = material.getFlexType().getAttribute("name");
        FlexTypeAttribute flextypeattribute = FlexTypeCache.getFlexTypeRoot("Material").getAttribute("name");
   		//FlexTypeAttribute flextypeattribute = FlexTypeCache.getFlexTypeFromPath("Material\\Accessories");
		PreparedQueryStatement preparedquerystatement = new PreparedQueryStatement();
		preparedquerystatement.appendFromTable("LCSMATERIALMASTER", "MATERIALMASTER");
		preparedquerystatement.appendFromTable("LCSMATERIAL");
		preparedquerystatement.appendFromTable("LCSSUPPLIER");
		preparedquerystatement.appendFromTable("LCSSUPPLIERMASTER");
		preparedquerystatement.appendFromTable("LCSMATERIALSUPPLIER");
		preparedquerystatement.appendFromTable("LCSMATERIALSUPPLIERMASTER");
		preparedquerystatement.appendFromTable("V_LCSMATERIALSUPPLIER");
		preparedquerystatement.appendSelectColumn("MATERIALMASTER", "NAME");
		preparedquerystatement.appendSelectColumn("LCSSUPPLIERMASTER", "SUPPLIERNAME");
		preparedquerystatement.appendSelectColumn("LCSSUPPLIERMASTER", "IDA2A2");
		preparedquerystatement.appendSelectColumn("MATERIALMASTER", "IDA2A2");
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", "BRANCHIDITERATIONINFO");
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", flextypeattribute.getColumnName());
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", "IDA2A2");
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", "TYPEDISPLAY");
		//preparedquerystatement.appendSelectColumn("LCSMATERIAL", "PARTPRIMARYIMAGEURL");
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", "PRIMARYIMAGEURL");
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", "STATESTATE");
		//preparedquerystatement.appendSelectColumn("LCSMATERIAL", "IDA3A11");
		preparedquerystatement.appendSelectColumn("LCSMATERIAL", "IDA2TYPEDEFINITIONREFERENCE");
		preparedquerystatement.appendSelectColumn("V_LCSMATERIALSUPPLIER", "MATERIALSUPPLIERNAME");
		preparedquerystatement.appendSelectColumn("LCSMATERIALSUPPLIER", "BRANCHIDITERATIONINFO");
		preparedquerystatement.appendSelectColumn("LCSMATERIALSUPPLIER", "STATESTATE");
		preparedquerystatement.appendSelectColumn("LCSSUPPLIER", "STATESTATE");
		preparedquerystatement.appendSelectColumn("LCSMATERIALSUPPLIERMASTER", "IDA2A2");
		preparedquerystatement.appendSelectColumn("LCSMATERIALSUPPLIER", "IDA2A2");
		preparedquerystatement.appendSelectColumn("LCSSUPPLIER", "BRANCHIDITERATIONINFO");
		preparedquerystatement.appendJoin("V_LCSMATERIALSUPPLIER", "IDA2A2", "LCSMATERIALSUPPLIER", "IDA2A2");
		preparedquerystatement.appendJoin("LCSMATERIAL", "IDA3MASTERREFERENCE", "MATERIALMASTER", "IDA2A2");
		preparedquerystatement.appendJoin("LCSSUPPLIER", "IDA3MASTERREFERENCE", "LCSSUPPLIERMASTER", "IDA2A2");
		preparedquerystatement.appendJoin("LCSMATERIALSUPPLIER", "IDA3MASTERREFERENCE", "LCSMATERIALSUPPLIERMASTER", "IDA2A2");
		preparedquerystatement.appendJoin("LCSMATERIALSUPPLIERMASTER", "IDA3B6", "LCSSUPPLIER", "IDA3MASTERREFERENCE");
		preparedquerystatement.appendJoin("LCSMATERIALSUPPLIERMASTER", "IDA3A6", "LCSMATERIAL", "IDA3MASTERREFERENCE");
		preparedquerystatement.appendAndIfNeeded();
		preparedquerystatement.appendCriteria(new Criteria("LCSMATERIAL", "STATECHECKOUTINFO", "wrk", "<>"));
		preparedquerystatement.appendAndIfNeeded();
		preparedquerystatement.appendCriteria(new Criteria("LCSMATERIAL", "LATESTITERATIONINFO", "1", "="));
		preparedquerystatement.appendAndIfNeeded();
		preparedquerystatement.appendCriteria(new Criteria("LCSSUPPLIER", "STATECHECKOUTINFO", "wrk", "<>"));
		preparedquerystatement.appendAndIfNeeded();
		preparedquerystatement.appendCriteria(new Criteria("LCSSUPPLIER", "LATESTITERATIONINFO", "1", "="));
		preparedquerystatement.appendAndIfNeeded();
		preparedquerystatement.appendCriteria(new Criteria("LCSMATERIALSUPPLIER", "STATECHECKOUTINFO", "wrk", "<>"));
		preparedquerystatement.appendAndIfNeeded();
		preparedquerystatement.appendCriteria(new Criteria("MATERIALMASTER", "NAME", "material_placeholder", "<>"));

		preparedquerystatement.appendAndIfNeeded();
		//addPossibleSearchCriteria("LCSMATERIAL", "IDA2A2", "" + "156714", preparedquerystatement); //  156379 :
		addPossibleSearchCriteria("LCSMATERIAL", "IDA2A2", "" + map.get(MATERIAL_NUM_ID), preparedquerystatement); //  156379 :
		//material*******:com.lcs.wc.material.LCSMaterial:156714
		//numericObjId*********:156714
		//addPossibleSearchCriteria("LCSMATERIALSUPPLIERMASTER", "IDA3B6", "" + "98215", preparedquerystatement);  // supplier master
		addPossibleSearchCriteria("LCSMATERIALSUPPLIERMASTER", "IDA3B6", "" + map.get(SUPPLIER_MASTER_NUM_ID), preparedquerystatement);  // supplier master

		//supplierMasterId********:OR:com.lcs.wc.supplier.LCSSupplierMaster:98215
		//getNumericObjectIdFromObject********:98215
		FlexTypeGenerator flexg = new FlexTypeGenerator();
		flexg.setScope("MATERIAL");
        flexg.setLevel(null);
		try{
			flexg.appendHardColumns(preparedquerystatement, flextype, "LCSMATERIAL", false, false);
		}catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		}
		/*preparedquerystatement.appendFromTable("FLEXTYPE");
		preparedquerystatement.appendJoin("LCSMATERIAL", "IDA3A11", "FLEXTYPE", "IDA2A2");
		preparedquerystatement.appendSelectColumn("FLEXTYPE", "TYPENAME");
		preparedquerystatement.appendSelectColumn("FLEXTYPE", "IDA2A2");
		preparedquerystatement.appendSelectColumn("FLEXTYPE", "TYPEIDPATH");*/
		preparedquerystatement.appendFromTable("WTTYPEDEFINITION");
		preparedquerystatement.appendJoin("LCSMATERIAL", "IDA2TYPEDEFINITIONREFERENCE", "WTTYPEDEFINITION", "IDA2A2");
		preparedquerystatement.appendSelectColumn("WTTYPEDEFINITION", "NAME");
		preparedquerystatement.appendSelectColumn("WTTYPEDEFINITION", "IDA2A2");
		//preparedquerystatement.appendSelectColumn("WTTYPEDEFINITION", "TYPEIDPATH");
        flexg.addFlexTypeCriteria(preparedquerystatement, flextype, null);
		preparedquerystatement = flexg.createSearchResultsQueryColumns(collection, flextype, preparedquerystatement);
		flexg.setScope("MATERIAL-SUPPLIER");
        flexg.setLevel(null);
		try{
			 flexg.appendHardColumns(preparedquerystatement, flextype, "LCSMATERIALSUPPLIER", false, false);
		}catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		}
		flexg.addFlexTypeCriteria(preparedquerystatement, flextype, null);
		preparedquerystatement = flexg.createSearchResultsQueryColumns(collection, flextype, preparedquerystatement);
		flexg.setScope(null);
        flexg.setLevel(null);
		//preparedquerystatement = flexg.generateSearchCriteria(FlexTypeCache.getFlexTypeFromPath(MATERIAL_SUPPLIER_ROOT_TYPE), preparedquerystatement, criteria);
		preparedquerystatement = flexg.createSearchResultsQueryColumns(collection, FlexTypeCache.getFlexTypeFromPath("Supplier"), preparedquerystatement);
		SearchResults searchresults = null;
		searchresults = runDirectQuery(preparedquerystatement);

		return searchresults;

	}// end method	
	

 }// class end
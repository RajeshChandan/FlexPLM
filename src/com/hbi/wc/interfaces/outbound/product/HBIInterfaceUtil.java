package com.hbi.wc.interfaces.outbound.product;

import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;

import wt.util.WTException;

public class HBIInterfaceUtil {
	
	public LCSLifecycleManaged getLifecycleManagedByNameType(String key,String searchString, String businessObjectTypePath) throws WTException
	{
		LCSLifecycleManaged businessObject = null;	
		//System.out.println(">>>>>>>>>>>before businessObjectTypePath>>>>>>"+businessObjectTypePath);
		//businessObjectTypePath = "SAPTeamTemplate_BusinessObject";
		//System.out.println(">>>>>>>>>>> after businessObjectTypePath>>>>>>"+businessObjectTypePath);
		FlexType businessObjFlexType = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		//System.out.println(">>>>>>>>>>> after businessObjectTypePath>>>>>>"+businessObjFlexType.getFullName());
		//System.out.println(">>>>>>>>>>> after businessObjectTypePath>>>>>>"+businessObjFlexType.getFullNameDisplay());
		
		//System.out.println("1: businessObjFlexType.getAttribute(key).getColumnDescriptorName() "+businessObjFlexType.getAttribute(key).getColumnDescriptorName());
		//System.out.println("2: key "+key);
		//System.out.println("3: businessObjectTypePath "+businessObjectTypePath);
		
		Map<String, String> criteriaMap = new HashMap<String, String>();
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendAndIfNeeded();
		/*statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, businessObjFlexType.getAttribute(key).getColumnDescriptorName()), "?", Criteria.EQUALS),
				searchString);*/
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, businessObjFlexType.getAttribute(key).getColumnDescriptorName()), "?", Criteria.EQUALS),
				searchString);
		
		//statement.appendAndIfNeeded();
		//statement.appendCriteria(new Criteria(new QueryColumn("LCSLifecycleManaged", "IDA3A8"), "?", Criteria.EQUALS),FormatHelper.getNumericObjectIdFromObject(businessObjFlexType));
		//statement.appendCriteria(new Criteria(new QueryColumn("LCSLifecycleManaged", "BRANCHIDA2TYPEDEFINITIONREFE"), "?", Criteria.EQUALS),FormatHelper.getNumericObjectIdFromObject(businessObjFlexType));
		//statement.appendCriteria(new Criteria(new QueryColumn("LCSLifecycleManaged", "flexTypeIdPath"),businessObjFlexType.getTypeIdPath(),Criteria.EQUALS));
	
		//System.out.println("################# "+statement);
		SearchResults results = LCSQuery.runDirectQuery(statement);
		//System.out.println("#########results "+results.getResultsFound());

		//
		if(results != null && results.getResultsFound() > 0)
		{
			//System.out.println(">>>>>>>>>>>>>>>>>>+ results.getResults().firstElement"+results.getResults().firstElement());
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>flexObj>>>>>>>>>>>>>>>"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
			businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
		}
		return businessObject;
	}

	

}

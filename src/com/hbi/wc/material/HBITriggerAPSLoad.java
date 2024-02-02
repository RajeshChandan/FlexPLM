package com.hbi.wc.material;

import java.io.*;
import com.lcs.wc.util.*;
import java.util.*;
import java.text.*;
import com.lcs.wc.moa.*;
import com.lcs.wc.foundation.*;
import com.lcs.wc.material.*;
import wt.fc.*;
import wt.util.*;
import com.lcs.wc.util.VersionHelper;
import wt.fc.PersistenceHelper;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialQuery;
import java.lang.*;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;

import com.lcs.wc.flextype.*;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.db.*;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;


public class HBITriggerAPSLoad 
{
	public static final String purchMOATypeName = "Multi-Object\\Purchasing Materials MOA";
	public static final String interfaceFlag = "Yes";
	public static void updatePurchMOA(WTObject hbiObj) throws LCSException, WTException,WTPropertyVetoException,ParseException
	{
		LCSMaterialSupplier hbiMatSupObj = (LCSMaterialSupplier) hbiObj;
		LCSMaterialSupplier hbiPrevMatSupObj=(LCSMaterialSupplier) VersionHelper.predecessorOf(hbiMatSupObj); 
		LCSMaterialSupplier hbilatestMatSupObj=(LCSMaterialSupplier) VersionHelper.latestIterationOf(hbiMatSupObj);
		Date prevSCLoadedDate = (Date) hbiPrevMatSupObj.getValue("hbiSupplyChainLoadPMAct");
		Date latestSCLoadedDate = (Date) hbilatestMatSupObj.getValue("hbiSupplyChainLoadPMAct");
		Date blankDate=new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/mm/dd");
		blankDate=format.parse("0001/01/01");
		if(latestSCLoadedDate!=null)
		{
			if(prevSCLoadedDate==null)
			{
				prevSCLoadedDate=blankDate;
			}
			
			if((latestSCLoadedDate.compareTo(prevSCLoadedDate)>0 ))
			{
				FlexType purchMOAType = null;
				SearchResults results = null; 
				try
				{
					FlexType materialSupplierType = hbilatestMatSupObj.getFlexType();
					FlexTypeAttribute colAtt = materialSupplierType.getAttribute("hbiPurchasing");
					purchMOAType = FlexTypeCache.getFlexTypeFromPath(purchMOATypeName);
					String sortKey = "LCSMOAObject.branchId";
					System.out.println("Sort Key"+sortKey);
					PreparedQueryStatement moaQueryStmt = LCSMOAObjectQuery.findMOACollectionDataQuery(hbilatestMatSupObj, colAtt, sortKey, false);
					SearchResults searchresults = LCSQuery.runDirectQuery(moaQueryStmt);
					System.out.println("results -> " + searchresults);
					Vector vec = searchresults.getResults();
					System.out.println("Size of vector"+vec.size());
					for(int i=0;i < vec.size();i++)
					{
						FlexObject flexObject = (FlexObject)vec.elementAt(i);
						LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById((new StringBuilder()).append("OR:com.lcs.wc.moa.LCSMOAObject:").append(flexObject.getString("LCSMOAObject.IDA2A2")).toString());
						if(moaObject != null)
						{
							moaObject.setValue("hbiInterfaceFlag",interfaceFlag);
							PersistenceHelper.manager.save(moaObject);
						}
					}
				
				}
			
				catch(Exception e)
				{
					System.out.println(e);
				} 
			
			}
		}
		
	}	

 }




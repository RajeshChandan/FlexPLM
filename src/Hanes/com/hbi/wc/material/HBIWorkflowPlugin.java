package com.hbi.wc.material;

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

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialColorLogic;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.wf.WFHelper;

import com.lcs.wc.flextype.*;

public class HBIWorkflowPlugin {

	public static void setLCOnWorkflowProcess(WTObject hbiMatObj) throws Exception {
		if (hbiMatObj instanceof LCSMaterial){
			LCSMaterial hbiMaterial = (LCSMaterial) hbiMatObj;
			hbiMaterial = (LCSMaterial)VersionHelper.latestIterationOf(hbiMaterial);	
			Folder hbiCheckedout = null;
			//LCSMaterial hbiMaterialPrev = null;
			State hbiLCState = null;
			String hbiWFLatest = null;
			FlexType type = null;
			type = hbiMaterial.getFlexType();
			String strTypeName = type.getFullName();
			//System.out.println("Type Name : "+strTypeName);
			//added the below condition for 232219-16 for excluding Casing and Packaging and its subtype from setting LC
			
			if(!(strTypeName.contains("Casing")||strTypeName.contains("Packaging")))
			{
			
			
				hbiWFLatest = (String) hbiMaterial.getValue("hbiWorkflowProcess");
				//hbiMaterialPrev = (LCSMaterial) VersionControlHelper.getPredecessor(hbiMaterial).getObject();
				//String hbiWFPrev = (String) hbiMaterialPrev.getValue("hbiWorkflowProcess");
				if (WorkInProgressHelper.isCheckedOut((Workable) hbiMaterial))
				{
					if(hbiWFLatest != null)
					{
						if (hbiWFLatest.equalsIgnoreCase("required"))
						{
							hbiLCState = State.toState("IN-DEVELOPMENT");
							WorkInProgressHelper.service.checkin(hbiMaterial, "");
							hbiMaterial = (LCSMaterial) LifeCycleHelper.service.setLifeCycleState(hbiMaterial, hbiLCState);
							hbiCheckedout = WorkInProgressHelper.service.getCheckoutFolder();
							WorkInProgressHelper.service.checkout(hbiMaterial, hbiCheckedout, "").getWorkingCopy();
						}
					}
				}
				else 
				{
					if(hbiWFLatest != null)
					{
						if (hbiWFLatest.equalsIgnoreCase("required")){
							hbiLCState = State.toState("IN-DEVELOPMENT");
							hbiMaterial = (LCSMaterial) LifeCycleHelper.service.setLifeCycleState(hbiMaterial, hbiLCState);
						}
					}
				}	
			}
		}
	}
	public static void setLCTemplateOnMatSup(WTObject hbiObj) throws Exception {
		String hbiWFLatest = null;
		String concatSupName = null;
		if (hbiObj instanceof LCSMaterialSupplier){
			LCSMaterialSupplier hbiMatSupObj = (LCSMaterialSupplier) hbiObj;
			LCSMaterial hbiMatObj = (LCSMaterial) com.lcs.wc.util.VersionHelper.latestIterationOf(hbiMatSupObj.getMaterialMaster());
			LCSSupplier hbiSupObj = (LCSSupplier) com.lcs.wc.util.VersionHelper.latestIterationOf(hbiMatSupObj.getSupplierMaster());
			//System.out.println("Identity======================================"+hbiSupObj.getIdentity());
			//System.out.println("getName()======================================"+hbiSupObj.getName());
			//System.out.println("HBI Item Code======================================"+hbiMatObj.getValue("name"));
			String hbiItemCode = hbiMatObj.getValue("name").toString();
			String hbiTBD = " - (TBD)";
			String hbiColorVersion = "Color Version";
			if(hbiItemCode != null)
				concatSupName = hbiItemCode + hbiTBD;
			//System.out.println("concatSupName======================================"+concatSupName);
			String matSupIdentity = (String) hbiMatSupObj.getIdentity();
			String supIdentity = (String) hbiSupObj.getIdentity();
			hbiWFLatest = (String) hbiMatObj.getValue("hbiWorkflowProcess");
			String buyerGrpStr = (String) hbiMatObj.getValue("hbiBuyerGroup");
			WTContainerRef hbiWTContainerRef = hbiMatObj.getContainerReference();
			LifeCycleTemplate hbiMSLCTemplate = null;
			hbiMSLCTemplate = LifeCycleHelper.service.getLifeCycleTemplate("HBI Material Supplier Development LC",hbiWTContainerRef);
			if(matSupIdentity != null && (!(matSupIdentity.equals(concatSupName))) && (!(supIdentity.equals(hbiColorVersion))))
			{
				if(hbiWFLatest != null)
				{
					 //Code modified by Wipro Team
					if ( FormatHelper.hasContent(buyerGrpStr) && hbiWFLatest.equalsIgnoreCase("required")){
					//if (hbiWFLatest.equalsIgnoreCase("required") && (!buyerGrpStr.equals(" "))){
						LifeCycleHelper.setLifeCycle((LifeCycleManaged) hbiMatSupObj, hbiMSLCTemplate);
					}
				}
			}
		}
	}
}
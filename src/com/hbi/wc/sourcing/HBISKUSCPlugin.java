package com.hbi.wc.sourcing;

import com.lcs.wc.sourcing.LCSSKUSourcingLink;

import wt.fc.WTObject;
import wt.fc.Persistable;
import wt.util.WTException;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;

public class HBISKUSCPlugin {

public static void setSKUSourcingLinkInActiveOnSCCreate(WTObject obj )throws Exception
{
	if(!(obj instanceof LCSSKUSourcingLink))
		throw new WTException("HBISKUSCPlugin.setSKUSourcingLinkActiveOnSCCreate: object must be a LCSSKUSourcingLink instance");
	LCSSKUSourcingLink link = (LCSSKUSourcingLink)obj;

	if(link!=null)
	{
		Persistable persistable = null;
		link.setActive(false);
		persistable = new LCSSourcingConfigLogic().saveSKUSourcingLink(link);
	}
	
}

}
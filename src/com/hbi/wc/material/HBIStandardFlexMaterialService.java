package com.hbi.wc.material;

import com.lcs.wc.sourcing.LCSSourcingConfig;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class HBIStandardFlexMaterialService extends StandardManager  implements HBIFlexMaterialService, Serializable {


	private static final String RESOURCE = "com.lcs.wc.specification.specificationResource";
	private static final String CLASSNAME = HBIStandardFlexMaterialService.class.getName();

	public static HBIStandardFlexMaterialService newHBIStandardFlexMaterialService() throws WTException  {
		HBIStandardFlexMaterialService instance = new HBIStandardFlexMaterialService();
		instance.initialize();
		return instance;
	}

	public String generateHbiMaterialTechPack(String timeToLive, String mSupplierId, String isGenericMatSpec ) throws WTException {
		Transaction tr = null;
		String url = null;
		try {
			tr = new Transaction();
			tr.start();
			//System.out.println("inside Standard Service" + isGenericMatSpec);
			//FlexSpecLogic logic = new FlexSpecLogic();
			HBIFlexMaterialLogic logic = new HBIFlexMaterialLogic();
			// Changed for ticket 141702-15 
			url = logic.generateMaterialTechPackImpl(timeToLive, mSupplierId, isGenericMatSpec );
			//System.out.println("inside Standard Service  url " + url);
			tr.commit();
			tr = null;
		} finally {
			if (tr != null) {
			tr.rollback();
		}
		}
		//System.out.println("inside Standard Service  url " + url);
		return url;
  }


} //end class 
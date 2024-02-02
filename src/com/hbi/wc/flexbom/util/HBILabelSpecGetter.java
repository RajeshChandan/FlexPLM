package com.hbi.wc.flexbom.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.util.FormatHelper;

import wt.util.WTException;

public class HBILabelSpecGetter {
    public static final String TYPE_COMP_DELIM = PDFProductSpecificationGenerator2.TYPE_COMP_DELIM;

	public static ArrayList<FlexObject> getlabelProductComponents(String masterId) throws WTException {
		
		ArrayList<FlexObject> labelSpecComponents = new ArrayList<FlexObject>(); 
	    Collection parentLinks = LCSProductQuery.getLinkedProducts(masterId, false, true);
	    Collection productBOMs = new ArrayList();
	    Iterator i = parentLinks.iterator();

	  while(i.hasNext()){
        FlexObject fobj = (FlexObject)i.next();
		String linktype=(String)fobj.get("PRODUCTTOPRODUCTLINK.LINKTYPE");
		if("Label-Garment".equals(linktype)){
		LCSProduct lp=(LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(String)fobj.get("PARENTPRODUCT.BRANCHIDITERATIONINFO"));
		    productBOMs = new LCSFlexBOMQuery().findBOMObjects(lp, null, null, "MAIN");
		
				Iterator labeliterator=productBOMs.iterator();
				while(labeliterator.hasNext()){
				FlexObject bomobj=new FlexObject();
				 FlexBOMPart labelBOM=(FlexBOMPart)labeliterator.next();
									String lid="BOM" + TYPE_COMP_DELIM + "VR:com.lcs.wc.flexbom.FlexBOMPart:"+FormatHelper.getNumericVersionIdFromObject(labelBOM);
									bomobj.setData("OID","VR:com.lcs.wc.flexbom.FlexBOMPart:"+FormatHelper.getNumericVersionIdFromObject(labelBOM));
									bomobj.setData("NAME",(String)labelBOM.getValue("name"));
								   bomobj.setData("COMPONENT_TYPE","BOM");
								    bomobj.setData("COMPONENT_TYPE_UNTRANSLATED","BOM");

								   
						labelSpecComponents.add(bomobj);
									
				//listOfComp.put(lid,(String)labelBOM.getValue("name"));
				}
      

		}
        
    }

		return labelSpecComponents;

	}
	
}

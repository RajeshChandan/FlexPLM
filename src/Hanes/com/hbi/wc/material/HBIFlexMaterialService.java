package com.hbi.wc.material;

import com.lcs.wc.sourcing.LCSSourcingConfig;
import java.util.Collection;
import java.util.Map;
import wt.fc.WTObject;
import wt.method.RemoteInterface;
import wt.part.WTPartMaster;
import wt.util.WTException;


//RemoteInterface
public abstract interface HBIFlexMaterialService {
// Changed  for ticket 141702-15
  public abstract String generateHbiMaterialTechPack(String paramString1, String paramString2, String isGenericMatSpec) throws WTException;


}// end class
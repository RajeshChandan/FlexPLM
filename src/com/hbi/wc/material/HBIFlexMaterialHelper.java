package com.hbi.wc.material;

import java.beans.PropertyChangeEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import wt.introspection.PropertyDisplayName;
import wt.util.WTPropertyVetoException;


public class HBIFlexMaterialHelper implements Externalizable {


  private static final String RESOURCE = "com.lcs.wc.specification.specificationResource";
  private static final String CLASSNAME = HBIFlexMaterialHelper.class.getName();

  //private static HBIFlexMaterialServiceFwd service = new HBIFlexMaterialServiceFwd();
  private static HBIFlexMaterialService service = new HBIFlexMaterialServiceFwd();
  //private static HBIFlexSpecService service ;
  static final long serialVersionUID = 1L;
  public static final long EXTERNALIZATION_VERSION_UID = 957977401221134810L;

  public HBIFlexMaterialHelper(){

  }

  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeLong(957977401221134810L);
  }

  public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
    long readSerialVersionUID = input.readLong();
    if (readSerialVersionUID == 957977401221134810L) {
      return;
    }
    throw new InvalidClassException(CLASSNAME, "Local class not compatible: stream classdesc externalizationVersionUID=" + readSerialVersionUID + " local class externalizationVersionUID=" + 957977401221134810L);
  }

  public static HBIFlexMaterialService getService() {
    return service;
  }

  public static void setService(HBIFlexMaterialService a_Service) throws WTPropertyVetoException {
    serviceValidate(a_Service);
    service = a_Service;
  }

  private static void serviceValidate(HBIFlexMaterialService a_Service) throws WTPropertyVetoException {
    if (a_Service == null) {
      Object[] args = { new PropertyDisplayName(CLASSNAME, "service") };
      throw new WTPropertyVetoException("wt.introspection.introspectionResource", "22", args, new PropertyChangeEvent(HBIFlexMaterialHelper.class, "service", service, a_Service));
    }
  }

}// end class
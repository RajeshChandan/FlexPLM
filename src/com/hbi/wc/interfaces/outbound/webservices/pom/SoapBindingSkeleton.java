/**
 * SoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hbi.wc.interfaces.outbound.webservices.pom;

public class SoapBindingSkeleton implements com.hbi.wc.interfaces.outbound.webservices.pom.IEService, org.apache.axis.wsdl.Skeleton {
    private com.hbi.wc.interfaces.outbound.webservices.pom.IEService impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hbiPointsOfMeasureBean"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.pom", "com.hbi.wc.interfaces.outbound.webservices.pom.HBIPointsOfMeasureBean"), com.hbi.wc.interfaces.outbound.webservices.pom.ComHbiWcInterfacesOutboundWebservicesPomHBIPointsOfMeasureBean.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("hbiPointsOfMeasureTask", _params, new javax.xml.namespace.QName("", "Collection"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.pom", "hbiPointsOfMeasureTask"));
        _oper.setSoapAction("urn:ie-soap:com.hbi.wc.interfaces.outbound.webservices.pom!hbiPointsOfMeasureTask");
        _myOperationsList.add(_oper);
        if (_myOperations.get("hbiPointsOfMeasureTask") == null) {
            _myOperations.put("hbiPointsOfMeasureTask", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("hbiPointsOfMeasureTask")).add(_oper);
    }

    public SoapBindingSkeleton() {
        this.impl = new com.hbi.wc.interfaces.outbound.webservices.pom.SoapBindingImpl();
    }

    public SoapBindingSkeleton(com.hbi.wc.interfaces.outbound.webservices.pom.IEService impl) {
        this.impl = impl;
    }
    public java.lang.String hbiPointsOfMeasureTask(com.hbi.wc.interfaces.outbound.webservices.pom.ComHbiWcInterfacesOutboundWebservicesPomHBIPointsOfMeasureBean hbiPointsOfMeasureBean) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.hbiPointsOfMeasureTask(hbiPointsOfMeasureBean);
        return ret;
    }

}

/**
 * SoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hbi.wc.interfaces.outbound.webservices.measurements;

public class SoapBindingSkeleton implements com.hbi.wc.interfaces.outbound.webservices.measurements.IEService, org.apache.axis.wsdl.Skeleton {
    private com.hbi.wc.interfaces.outbound.webservices.measurements.IEService impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "hbiMeasurementsBean"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.measurements", "com.hbi.wc.interfaces.outbound.webservices.measurements.HBIMeasurementsBean"), com.hbi.wc.interfaces.outbound.webservices.measurements.ComHbiWcInterfacesOutboundWebservicesMeasurementsHBIMeasurementsBean.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("hbiMeasurementsTask", _params, new javax.xml.namespace.QName("", "Collection"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.measurements", "hbiMeasurementsTask"));
        _oper.setSoapAction("urn:ie-soap:com.hbi.wc.interfaces.outbound.webservices.measurements!hbiMeasurementsTask");
        _myOperationsList.add(_oper);
        if (_myOperations.get("hbiMeasurementsTask") == null) {
            _myOperations.put("hbiMeasurementsTask", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("hbiMeasurementsTask")).add(_oper);
    }

    public SoapBindingSkeleton() {
        this.impl = new com.hbi.wc.interfaces.outbound.webservices.measurements.SoapBindingImpl();
    }

    public SoapBindingSkeleton(com.hbi.wc.interfaces.outbound.webservices.measurements.IEService impl) {
        this.impl = impl;
    }
    public java.lang.String hbiMeasurementsTask(com.hbi.wc.interfaces.outbound.webservices.measurements.ComHbiWcInterfacesOutboundWebservicesMeasurementsHBIMeasurementsBean hbiMeasurementsBean) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.hbiMeasurementsTask(hbiMeasurementsBean);
        return ret;
    }

}

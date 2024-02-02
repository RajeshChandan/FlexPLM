/**
 * IESoapServletLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hbi.wc.interfaces.outbound.webservices.measurements;

public class IESoapServletLocator extends org.apache.axis.client.Service implements com.hbi.wc.interfaces.outbound.webservices.measurements.IESoapServlet {

    public IESoapServletLocator() {
    }


    public IESoapServletLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IESoapServletLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IESoapPort
    private java.lang.String IESoapPort_address = "https://c9plm.hanes.com/Windchill/Hanes-FlexPLM/servlet/RPC?VERSION=1.1&URN=http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.measurements";

    public java.lang.String getIESoapPortAddress() {
        return IESoapPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IESoapPortWSDDServiceName = "IESoapPort";

    public java.lang.String getIESoapPortWSDDServiceName() {
        return IESoapPortWSDDServiceName;
    }

    public void setIESoapPortWSDDServiceName(java.lang.String name) {
        IESoapPortWSDDServiceName = name;
    }

    public com.hbi.wc.interfaces.outbound.webservices.measurements.IEService getIESoapPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IESoapPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIESoapPort(endpoint);
    }

    public com.hbi.wc.interfaces.outbound.webservices.measurements.IEService getIESoapPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.hbi.wc.interfaces.outbound.webservices.measurements.SoapBindingStub _stub = new com.hbi.wc.interfaces.outbound.webservices.measurements.SoapBindingStub(portAddress, this);
            _stub.setPortName(getIESoapPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIESoapPortEndpointAddress(java.lang.String address) {
        IESoapPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.hbi.wc.interfaces.outbound.webservices.measurements.IEService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.hbi.wc.interfaces.outbound.webservices.measurements.SoapBindingStub _stub = new com.hbi.wc.interfaces.outbound.webservices.measurements.SoapBindingStub(new java.net.URL(IESoapPort_address), this);
                _stub.setPortName(getIESoapPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("IESoapPort".equals(inputPortName)) {
            return getIESoapPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.measurements", "IESoapServlet");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/com.hbi.wc.interfaces.outbound.webservices.measurements", "IESoapPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IESoapPort".equals(portName)) {
            setIESoapPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

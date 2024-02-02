/**
 * IESoapServlet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hbi.wc.interfaces.outbound.webservices.measurements;

public interface IESoapServlet extends javax.xml.rpc.Service {
    public java.lang.String getIESoapPortAddress();

    public com.hbi.wc.interfaces.outbound.webservices.measurements.IEService getIESoapPort() throws javax.xml.rpc.ServiceException;

    public com.hbi.wc.interfaces.outbound.webservices.measurements.IEService getIESoapPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

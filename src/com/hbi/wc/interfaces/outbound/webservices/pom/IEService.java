/**
 * IEService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hbi.wc.interfaces.outbound.webservices.pom;

public interface IEService extends java.rmi.Remote {

    /**
     * DESCRIPTION:
     * This task process points of measure changes(creating new PointsOfMeasure/updating
     * an existing PointsOfMeasure) based on the request from FlexPLM9.2
     * to FlexPLM10.1
     * INPUTS:
     * HBIPointsOfMeasureBean hbiPointsOfMeasureBean:	Bean representation
     * of the requested PointsOfMeasure
     * EXCEPTION:
     */
    public java.lang.String hbiPointsOfMeasureTask(com.hbi.wc.interfaces.outbound.webservices.pom.ComHbiWcInterfacesOutboundWebservicesPomHBIPointsOfMeasureBean hbiPointsOfMeasureBean) throws java.rmi.RemoteException;
}

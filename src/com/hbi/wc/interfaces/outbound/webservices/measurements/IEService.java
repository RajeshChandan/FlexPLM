/**
 * IEService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hbi.wc.interfaces.outbound.webservices.measurements;

public interface IEService extends java.rmi.Remote {

    /**
     * DESCRIPTION:
     * This task process measurement changes(creating new Measurement/updating
     * an existing Measurement) based on the request from FlexPLM 9.2 (source
     * FlexPLM) to FlexPLM 10.1 (target FlexPLM)
     * INPUTS:
     * HBIMeasurementsBean hbiMeasurementsBean:	Bean representation of the
     * requested Measurement
     * EXCEPTION:
     */
    public java.lang.String hbiMeasurementsTask(com.hbi.wc.interfaces.outbound.webservices.measurements.ComHbiWcInterfacesOutboundWebservicesMeasurementsHBIMeasurementsBean hbiMeasurementsBean) throws java.rmi.RemoteException;
}

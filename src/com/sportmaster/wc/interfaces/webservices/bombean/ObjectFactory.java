
package com.sportmaster.wc.interfaces.webservices.bombean;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sportmaster.wc.interfaces.webservices.bomBean package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _BomRequestResponse_QNAME = new QName("http://www.sportmaster.ru/plmbom", "bomRequestResponse");
    private final static QName _BomRequest_QNAME = new QName("http://www.sportmaster.ru/plmbom", "bomRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sportmaster.wc.interfaces.webservices.bomBean
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BOMRequestResponse }
     * 
     */
    public BOMRequestResponse createBOMRequestResponse() {
        return new BOMRequestResponse();
    }

    /**
     * Create an instance of {@link BOMRequest }
     * 
     */
    public BOMRequest createBOMRequest() {
        return new BOMRequest();
    }

    /**
     * Create an instance of {@link BOMPart }
     * 
     */
    public BOMPart createBOMPart() {
        return new BOMPart();
    }

    /**
     * Create an instance of {@link BomLinkVariation }
     * 
     */
    public BomLinkVariation createBomLinkVariation() {
        return new BomLinkVariation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BOMRequestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sportmaster.ru/plmbom", name = "bomRequestResponse")
    public JAXBElement<BOMRequestResponse> createBomRequestResponse(BOMRequestResponse value) {
        return new JAXBElement<BOMRequestResponse>(_BomRequestResponse_QNAME, BOMRequestResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BOMRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sportmaster.ru/plmbom", name = "bomRequest")
    public JAXBElement<BOMRequest> createBomRequest(BOMRequest value) {
        return new JAXBElement<BOMRequest>(_BomRequest_QNAME, BOMRequest.class, null, value);
    }

}

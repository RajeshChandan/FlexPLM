
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sportmaster.wc.interfaces.webservices.carelabelbean package. 
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

    private final static QName _CareLabelReportRequest_QNAME = new QName("http://www.sportmaster.ru/plmcarelabel", "careLabelReportRequest");
    private final static QName _CareLabelReportRequestResponse_QNAME = new QName("http://www.sportmaster.ru/plmcarelabel", "careLabelReportRequestResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sportmaster.wc.interfaces.webservices.carelabelbean
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CareLabelReportRequest }
     * 
     */
    public CareLabelReportRequest createCareLabelReportRequest() {
        return new CareLabelReportRequest();
    }

    /**
     * Create an instance of {@link CareLabelReportRequestResponse }
     * 
     */
    public CareLabelReportRequestResponse createCareLabelReportRequestResponse() {
        return new CareLabelReportRequestResponse();
    }

    /**
     * Create an instance of {@link Layer1Composition }
     * 
     */
    public Layer1Composition createLayer1Composition() {
        return new Layer1Composition();
    }

    /**
     * Create an instance of {@link Composition }
     * 
     */
    public Composition createComposition() {
        return new Composition();
    }

    /**
     * Create an instance of {@link Layer1CompositionRUComponent }
     * 
     */
    public Layer1CompositionRUComponent createLayer1CompositionRUComponent() {
        return new Layer1CompositionRUComponent();
    }

    /**
     * Create an instance of {@link BOMProductSeasonAttributes }
     * 
     */
    public BOMProductSeasonAttributes createBOMProductSeasonAttributes() {
        return new BOMProductSeasonAttributes();
    }

    /**
     * Create an instance of {@link CompositionRUComponent }
     * 
     */
    public CompositionRUComponent createCompositionRUComponent() {
        return new CompositionRUComponent();
    }

    /**
     * Create an instance of {@link Layer2CompositionComponent }
     * 
     */
    public Layer2CompositionComponent createLayer2CompositionComponent() {
        return new Layer2CompositionComponent();
    }

    /**
     * Create an instance of {@link Layer1CompositionComponent }
     * 
     */
    public Layer1CompositionComponent createLayer1CompositionComponent() {
        return new Layer1CompositionComponent();
    }

    /**
     * Create an instance of {@link ProductBOMComponentDestination }
     * 
     */
    public ProductBOMComponentDestination createProductBOMComponentDestination() {
        return new ProductBOMComponentDestination();
    }

    /**
     * Create an instance of {@link BOMMaterialAttributes }
     * 
     */
    public BOMMaterialAttributes createBOMMaterialAttributes() {
        return new BOMMaterialAttributes();
    }

    /**
     * Create an instance of {@link CompositionRU }
     * 
     */
    public CompositionRU createCompositionRU() {
        return new CompositionRU();
    }

    /**
     * Create an instance of {@link Layer1CompositionRU }
     * 
     */
    public Layer1CompositionRU createLayer1CompositionRU() {
        return new Layer1CompositionRU();
    }

    /**
     * Create an instance of {@link BOMProductAttributes }
     * 
     */
    public BOMProductAttributes createBOMProductAttributes() {
        return new BOMProductAttributes();
    }

    /**
     * Create an instance of {@link CompositionComponent }
     * 
     */
    public CompositionComponent createCompositionComponent() {
        return new CompositionComponent();
    }

    /**
     * Create an instance of {@link Layer2Composition }
     * 
     */
    public Layer2Composition createLayer2Composition() {
        return new Layer2Composition();
    }

    /**
     * Create an instance of {@link Layer2CompositionRUComponent }
     * 
     */
    public Layer2CompositionRUComponent createLayer2CompositionRUComponent() {
        return new Layer2CompositionRUComponent();
    }

    /**
     * Create an instance of {@link ProductBOMComponent }
     * 
     */
    public ProductBOMComponent createProductBOMComponent() {
        return new ProductBOMComponent();
    }

    /**
     * Create an instance of {@link CareWashComponent }
     * 
     */
    public CareWashComponent createCareWashComponent() {
        return new CareWashComponent();
    }

    /**
     * Create an instance of {@link Layer2CompositionRU }
     * 
     */
    public Layer2CompositionRU createLayer2CompositionRU() {
        return new Layer2CompositionRU();
    }

    /**
     * Create an instance of {@link CareWash }
     * 
     */
    public CareWash createCareWash() {
        return new CareWash();
    }

    /**
     * Create an instance of {@link ProductBOM }
     * 
     */
    public ProductBOM createProductBOM() {
        return new ProductBOM();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CareLabelReportRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sportmaster.ru/plmcarelabel", name = "careLabelReportRequest")
    public JAXBElement<CareLabelReportRequest> createCareLabelReportRequest(CareLabelReportRequest value) {
        return new JAXBElement<CareLabelReportRequest>(_CareLabelReportRequest_QNAME, CareLabelReportRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CareLabelReportRequestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sportmaster.ru/plmcarelabel", name = "careLabelReportRequestResponse")
    public JAXBElement<CareLabelReportRequestResponse> createCareLabelReportRequestResponse(CareLabelReportRequestResponse value) {
        return new JAXBElement<CareLabelReportRequestResponse>(_CareLabelReportRequestResponse_QNAME, CareLabelReportRequestResponse.class, null, value);
    }

}

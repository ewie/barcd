//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: [TEXT REMOVED by com.google.code.maven-replacer-plugin]
//


package de.tu_chemnitz.mi.barcd.xml.binding;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.tu_chemnitz.mi.barcd.xml.binding package. 
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

    private final static QName _Job_QNAME = new QName("http://www.tu-chemnitz.de/informatik/mi/barcd", "job");
    private final static QName _Frame_QNAME = new QName("http://www.tu-chemnitz.de/informatik/mi/barcd", "frame");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.tu_chemnitz.mi.barcd.xml.binding
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FrameElement }
     * 
     */
    public FrameElement createFrameElement() {
        return new FrameElement();
    }

    /**
     * Create an instance of {@link JobElement }
     * 
     */
    public JobElement createJobElement() {
        return new JobElement();
    }

    /**
     * Create an instance of {@link SourceChoiceElement }
     * 
     */
    public SourceChoiceElement createSourceChoiceElement() {
        return new SourceChoiceElement();
    }

    /**
     * Create an instance of {@link RangeElement }
     * 
     */
    public RangeElement createRangeElement() {
        return new RangeElement();
    }

    /**
     * Create an instance of {@link RegionElement }
     * 
     */
    public RegionElement createRegionElement() {
        return new RegionElement();
    }

    /**
     * Create an instance of {@link ImageSequenceSourceElement }
     * 
     */
    public ImageSequenceSourceElement createImageSequenceSourceElement() {
        return new ImageSequenceSourceElement();
    }

    /**
     * Create an instance of {@link ResourceElement }
     * 
     */
    public ResourceElement createResourceElement() {
        return new ResourceElement();
    }

    /**
     * Create an instance of {@link BarcodesElement }
     * 
     */
    public BarcodesElement createBarcodesElement() {
        return new BarcodesElement();
    }

    /**
     * Create an instance of {@link VideoStreamSourceElement }
     * 
     */
    public VideoStreamSourceElement createVideoStreamSourceElement() {
        return new VideoStreamSourceElement();
    }

    /**
     * Create an instance of {@link BarcodeElement }
     * 
     */
    public BarcodeElement createBarcodeElement() {
        return new BarcodeElement();
    }

    /**
     * Create an instance of {@link ImageCollectionSourceElement }
     * 
     */
    public ImageCollectionSourceElement createImageCollectionSourceElement() {
        return new ImageCollectionSourceElement();
    }

    /**
     * Create an instance of {@link RegionsElement }
     * 
     */
    public RegionsElement createRegionsElement() {
        return new RegionsElement();
    }

    /**
     * Create an instance of {@link SourceElement }
     * 
     */
    public SourceElement createSourceElement() {
        return new SourceElement();
    }

    /**
     * Create an instance of {@link SnapshotSourceElement }
     * 
     */
    public SnapshotSourceElement createSnapshotSourceElement() {
        return new SnapshotSourceElement();
    }

    /**
     * Create an instance of {@link PointElement }
     * 
     */
    public PointElement createPointElement() {
        return new PointElement();
    }

    /**
     * Create an instance of {@link PointsElement }
     * 
     */
    public PointsElement createPointsElement() {
        return new PointsElement();
    }

    /**
     * Create an instance of {@link UrlTemplateElement }
     * 
     */
    public UrlTemplateElement createUrlTemplateElement() {
        return new UrlTemplateElement();
    }

    /**
     * Create an instance of {@link VideoDeviceSourceElement }
     * 
     */
    public VideoDeviceSourceElement createVideoDeviceSourceElement() {
        return new VideoDeviceSourceElement();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JobElement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.tu-chemnitz.de/informatik/mi/barcd", name = "job")
    public JAXBElement<JobElement> createJob(JobElement value) {
        return new JAXBElement<JobElement>(_Job_QNAME, JobElement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FrameElement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.tu-chemnitz.de/informatik/mi/barcd", name = "frame")
    public JAXBElement<FrameElement> createFrame(FrameElement value) {
        return new JAXBElement<FrameElement>(_Frame_QNAME, FrameElement.class, null, value);
    }

}

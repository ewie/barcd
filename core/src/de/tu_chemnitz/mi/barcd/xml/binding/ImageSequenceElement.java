//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.10 at 09:13:08 AM CET 
//


package de.tu_chemnitz.mi.barcd.xml.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImageSequenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageSequenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="template" type="{http://www.tu-chemnitz.de/informatik/mi/barcd}URLTemplateType"/>
 *         &lt;element name="range" type="{http://www.tu-chemnitz.de/informatik/mi/barcd}RangeType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageSequenceType", propOrder = {

})
public class ImageSequenceElement {

    @XmlElement(required = true)
    protected URLTemplateElement template;
    @XmlElement(required = true)
    protected RangeElement range;

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link URLTemplateElement }
     *     
     */
    public URLTemplateElement getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link URLTemplateElement }
     *     
     */
    public void setTemplate(URLTemplateElement value) {
        this.template = value;
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link RangeElement }
     *     
     */
    public RangeElement getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     * 
     * @param value
     *     allowed object is
     *     {@link RangeElement }
     *     
     */
    public void setRange(RangeElement value) {
        this.range = value;
    }

}

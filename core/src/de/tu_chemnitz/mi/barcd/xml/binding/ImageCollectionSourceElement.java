//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: [TEXT REMOVED by com.google.code.maven-replacer-plugin]
//


package de.tu_chemnitz.mi.barcd.xml.binding;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImageCollectionSourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageCollectionSourceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.tu-chemnitz.de/informatik/mi/barcd}SeekableSourceType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="image" type="{http://www.tu-chemnitz.de/informatik/mi/barcd}ResourceType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageCollectionSourceType", propOrder = {
    "image"
})
public class ImageCollectionSourceElement
    extends SeekableSourceElement
{

    @XmlElement(required = true)
    protected List<ResourceElement> image;

    /**
     * Gets the value of the image property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the image property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceElement }
     * 
     * 
     */
    public List<ResourceElement> getImage() {
        if (image == null) {
            image = new ArrayList<ResourceElement>();
        }
        return this.image;
    }

}
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: [TEXT REMOVED by com.google.code.maven-replacer-plugin]
//


package de.tu_chemnitz.mi.barcd.xml.binding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BarcodeFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BarcodeFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="aztec-code"/>
 *     &lt;enumeration value="codabar"/>
 *     &lt;enumeration value="code-39"/>
 *     &lt;enumeration value="code-93"/>
 *     &lt;enumeration value="code-128"/>
 *     &lt;enumeration value="data-matrix"/>
 *     &lt;enumeration value="ean-8"/>
 *     &lt;enumeration value="ean-13"/>
 *     &lt;enumeration value="interleaved-2-of-5"/>
 *     &lt;enumeration value="maxicode"/>
 *     &lt;enumeration value="pdf417"/>
 *     &lt;enumeration value="qr-code"/>
 *     &lt;enumeration value="gs1-databar-omnidirectional"/>
 *     &lt;enumeration value="gs1-databar-expanded"/>
 *     &lt;enumeration value="upc-a"/>
 *     &lt;enumeration value="upc-e"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BarcodeFormat")
@XmlEnum
public enum BarcodeFormat {

    @XmlEnumValue("aztec-code")
    AZTEC_CODE("aztec-code"),
    @XmlEnumValue("codabar")
    CODABAR("codabar"),
    @XmlEnumValue("code-39")
    CODE_39("code-39"),
    @XmlEnumValue("code-93")
    CODE_93("code-93"),
    @XmlEnumValue("code-128")
    CODE_128("code-128"),
    @XmlEnumValue("data-matrix")
    DATA_MATRIX("data-matrix"),
    @XmlEnumValue("ean-8")
    EAN_8("ean-8"),
    @XmlEnumValue("ean-13")
    EAN_13("ean-13"),
    @XmlEnumValue("interleaved-2-of-5")
    INTERLEAVED_2_OF_5("interleaved-2-of-5"),
    @XmlEnumValue("maxicode")
    MAXICODE("maxicode"),
    @XmlEnumValue("pdf417")
    PDF_417("pdf417"),
    @XmlEnumValue("qr-code")
    QR_CODE("qr-code"),
    @XmlEnumValue("gs1-databar-omnidirectional")
    GS_1_DATABAR_OMNIDIRECTIONAL("gs1-databar-omnidirectional"),
    @XmlEnumValue("gs1-databar-expanded")
    GS_1_DATABAR_EXPANDED("gs1-databar-expanded"),
    @XmlEnumValue("upc-a")
    UPC_A("upc-a"),
    @XmlEnumValue("upc-e")
    UPC_E("upc-e"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    BarcodeFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BarcodeFormat fromValue(String v) {
        for (BarcodeFormat c: BarcodeFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
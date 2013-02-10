package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public enum BarcodeType {
    AZTEC_CODE("aztec-code"),
    CODABAR("codabar"),
    CODE_39("code-39"),
    CODE_93("code-93"),
    CODE_128("code-128"),
    DATA_MATRIX("data-matrix"),
    EAN_8("ean-8"),
    EAN_13("ean-13"),
    GS1_DATABAR_EXPANDED("gs1-databar-expanded"),
    GS1_DATABAR_OMNIDIRECTIONAL("gs1-databar-omnidirectional"),
    INTERLEAVED_2_OF_5("interleaved-2-of-5"),
    MAXICODE("maxicode"),
    PDF417("pdf417"),
    QR_CODE("qr-code"),
    UPC_A("upc-a"),
    UPC_E("upc-e"),
    
    UNKNOWN("unknown");

    private final String value;

    BarcodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BarcodeType fromValue(String v) {
        for (BarcodeType c: BarcodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
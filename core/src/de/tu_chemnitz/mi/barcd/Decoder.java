package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Decoder {
    /**
     * Decode a single barcode (the first found) in an image.
     *
     * @param image the image eventually containing a barcode
     *
     * @return the decoded barcode, null if no barcode was found, could be
     *   read or could be decoded
     */
    public Barcode decode(BufferedImage image);

    /**
     * Decode multiple barcodes in an image.
     *
     * @param image the image eventually containing barcodes
     *
     * @return the decoded barcodes, null if no barcode was found, could be read
     *   or could be decoded
     */
    public Barcode[] decodeMultiple(BufferedImage image);
}

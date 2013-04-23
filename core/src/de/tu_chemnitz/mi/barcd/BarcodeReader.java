package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface BarcodeReader {
    /**
     * Read a single barcode (the first found) in an image.
     *
     * @param image the image eventually containing a barcode
     *
     * @return the read barcode, null if none was found or could not be decoded
     */
    public Barcode read(BufferedImage image);

    /**
     * Read multiple barcodes in an image.
     *
     * @param image the image eventually containing barcodes
     *
     * @return the read barcodes, null if none were found or could not be decoded
     */
    public Barcode[] readMultiple(BufferedImage image);
}

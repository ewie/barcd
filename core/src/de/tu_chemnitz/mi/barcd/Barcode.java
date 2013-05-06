/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import de.tu_chemnitz.mi.barcd.geometry.Point;

/**
 * A decoded barcode.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Barcode {
    private final BarcodeType type;

    private final String text;

    private final byte[] bytes;

    private final Point[] anchorPoints;

    /**
     * Create a barcode.
     *
     * @param type the barcode type
     * @param text the decoded text
     * @param bytes the raw byte data encoded by the barcode
     * @param anchorPoints the anchor points detected and used by the reader
     */
    public Barcode(BarcodeType type, String text, byte[] bytes, Point[] anchorPoints) {
        this.type = type;
        this.text = text;
        this.bytes = bytes;
        this.anchorPoints = anchorPoints;
    }

    /**
     * @return the barcode type
     */
    public BarcodeType getType() {
        return type;
    }

    /**
     * @return the decoded text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the raw byte data encoded by the barcode, null if not applicable
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Get the points detected and used by the barcode reader.
     *
     * Usually 2 points for 1-dimensional barcodes, describing the scanline
     * across the barcodes.
     *
     * For 3-dimensional barcodes there's usually one anchor point per marker,
     * e.g. the 3 corner blocks of a QR-Code.
     *
     * @return the anchor points
     */
    public Point[] getAnchorPoints() {
        return anchorPoints;
    }
}
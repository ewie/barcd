package de.tu_chemnitz.mi.barcd;

import de.tu_chemnitz.mi.barcd.geometry.Point;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Barcode {
    private BarcodeType type;
    
    private String text;
    
    private byte[] raw;
    
    private Point[] anchorPoints;
    
    public Barcode(BarcodeType type, String text, byte[] raw, Point[] anchorPoints) {
        this.type = type;
        this.text = text;
        this.raw = raw;
        this.anchorPoints = anchorPoints;
    }
    
    public BarcodeType getType() {
        return type;
    }
    
    public String getText() {
        return text;
    }
    
    public byte[] getRaw() {
        return raw;
    }
    
    public Point[] getAnchorPoints() {
        return anchorPoints;
    }
}
package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Decoder {
    public Barcode decode(BufferedImage image);
    
    public Barcode[] decodeMultiple(BufferedImage image);
}

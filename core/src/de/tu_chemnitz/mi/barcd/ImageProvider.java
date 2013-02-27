package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * Provides a sequence of images.
 * 
 * An instance of this class is meant for a single iteration.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface ImageProvider {
    public boolean hasMore();
    
    public BufferedImage consume() throws ImageProviderException;
}

package de.tu_chemnitz.mi.barcd;

/**
 * Encapsulates information about an image source.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Source {
    public ImageProvider getImageProvider() throws ImageProviderException;
}

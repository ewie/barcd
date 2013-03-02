package de.tu_chemnitz.mi.barcd;

/**
 * Encapsulates information about an image source.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class Source {
    public static final int INITIAL_FRAME_NUMBER = 0;

    public ImageProvider getImageProvider()
        throws ImageProviderException
    {
        return getImageProvider(INITIAL_FRAME_NUMBER);
    }

    public abstract ImageProvider getImageProvider(int initialFrameNumber)
        throws ImageProviderException;
}

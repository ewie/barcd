package de.tu_chemnitz.mi.barcd;

/**
 * A source is responsible for creating an {@link ImageProvider} based on
 * implementation-specific properties.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class Source {
    public static final int INITIAL_FRAME_NUMBER = 0;

    /**
     * Create an image provider starting at the very first frame.
     *
     * @return an image provider starting at the very first frame
     *
     * @throws ImageProviderException if the image provider could not be created
     */
    public ImageProvider createImageProvider()
        throws ImageProviderException
    {
        return createImageProvider(INITIAL_FRAME_NUMBER);
    }

    /**
     * Create an image provider starting at a given frame.
     *
     * @param initialFrameNumber number of the first frame to provide
     *
     * @return an image provider starting at the given frame
     *
     * @throws ImageProviderException if the image provider could not be created
     */
    public abstract ImageProvider createImageProvider(int initialFrameNumber)
        throws ImageProviderException;
}

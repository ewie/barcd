package de.tu_chemnitz.mi.barcd.source;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.BufferedImageProvider;

/**
 * An image source using a collection of {@link BufferedImage}.
 *
 * Instances of this class cannot be persisted.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class BufferedImageSource extends Source {
    private final Iterable<BufferedImage> images;

    public BufferedImageSource(Iterable<BufferedImage> images) {
        this.images = images;
    }

    @Override
    public BufferedImageProvider createImageProvider(int initialFrameNumber)
        throws ImageProviderException
    {
        Iterator<BufferedImage> it = images.iterator();
        while (initialFrameNumber --> 0) {
            it.next();
        }
        return new BufferedImageProvider(it);
    }
}

package de.tu_chemnitz.mi.barcd.source;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.SeekableSource;
import de.tu_chemnitz.mi.barcd.provider.BufferedImageProvider;

/**
 * An image source using a collection of {@link BufferedImage}.
 * 
 * Instances of this class cannot be persisted.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class BufferedImageSource extends SeekableSource {
    private Collection<BufferedImage> images;

    public BufferedImageSource(Collection<BufferedImage> images, int initialFrameNumber) {
        super(initialFrameNumber);
        this.images = images;
    }

    public BufferedImageSource(Collection<BufferedImage> images) {
        this(images, 0);
    }

    @Override
    public BufferedImageProvider getImageProvider()
        throws ImageProviderException
    {
        Iterator<BufferedImage> it = images.iterator();
        int i = getInitialFrameNumber();
        while (i --> 0) {
            it.next();
        }
        return new BufferedImageProvider(it);
    }
}

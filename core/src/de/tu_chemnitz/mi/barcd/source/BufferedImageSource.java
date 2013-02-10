package de.tu_chemnitz.mi.barcd.source;

import java.awt.image.BufferedImage;
import java.util.Collection;

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
public class BufferedImageSource implements Source {
    private Collection<BufferedImage> images;

    public BufferedImageSource(Collection<BufferedImage> images) {
        this.images = images;
    }

    @Override
    public BufferedImageProvider getImageProvider()
        throws ImageProviderException
    {
        return new BufferedImageProvider(images.iterator());
    }
}

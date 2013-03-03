package de.tu_chemnitz.mi.barcd.provider;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProvider;
import de.tu_chemnitz.mi.barcd.ImageProviderException;

/**
 * Provides a sequence of {@link BufferedImage}.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class BufferedImageProvider implements ImageProvider {
    private Iterator<BufferedImage> images;

    /**
     * @param images an iterator over all images
     */
    public BufferedImageProvider(Iterator<BufferedImage> images) {
        this.images = images;
    }

    @Override
    public boolean hasMore() {
        return images.hasNext();
    }

    @Override
    public BufferedImage consume()
        throws ImageProviderException
    {
        BufferedImage image = images.next();
        if (image == null) {
            throw new ImageProviderException("next image is null");
        }
        return image;
    }
}

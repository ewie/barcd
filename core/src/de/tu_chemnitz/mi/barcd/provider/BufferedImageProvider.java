/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
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
    private final Iterator<BufferedImage> images;

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
        return images.next();
    }
}

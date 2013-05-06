/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.util.Iterator;

/**
 * Provides a sequence of images in {@link Iterator}-like fashion. Does not
 * extend {@link Iterator} so {@link #consume()} can throw a checked exception.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface ImageProvider {
    /**
     * Check if the image provider has more images.
     *
     * @return true if there are more images
     */
    public boolean hasMore();

    /**
     * Consume the next image.
     *
     * @return the next image
     *
     * @throws ImageProviderException
     */
    public BufferedImage consume() throws ImageProviderException;
}

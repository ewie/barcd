/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * Converts images to grayscale.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Grayscaler {
    /**
     * Converts the given image to gray scale.
     *
     * @param image the image to convert
     *
     * @return the gray scaled image
     */
    public BufferedImage convertToGrayscale(BufferedImage image);
}

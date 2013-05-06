/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * A region extractor is responsible for identifying image regions.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface RegionExtractor {
    /**
     * Extract disjoint regions from an image.
     *
     * @param image the image
     *
     * @return the extracted regions
     */
    public abstract Region[] extractRegions(BufferedImage image);
}
/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.provider;

import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.ImageProvider;
import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;

/**
 * Provides a sequence of images backed up by a {@link FrameReader}.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoImageProvider implements ImageProvider {
    private final FrameReader frameReader;

    /**
     * @param frameReader a frame reader
     */
    public VideoImageProvider(FrameReader frameReader) {
        this.frameReader = frameReader;
    }

    @Override
    public boolean hasMore() {
        return frameReader.hasMoreFrames();
    }

    @Override
    public BufferedImage consume()
        throws ImageProviderException
    {
        try {
            return frameReader.getNextFrame();
        } catch (FrameReaderException ex) {
            throw new ImageProviderException("could not consume next video frame", ex);
        }
    }
}

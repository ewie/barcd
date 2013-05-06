/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

/**
 * Reads the frames of a video source.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface FrameReader {
    /**
     * @return the frame width
     */
    public int getWidth();

    /**
     * @return the frame height
     */
    public int getHeight();

    /**
     * Read the next frame.
     *
     * @return the image of the next frame
     *
     * @throws FrameReaderException
     */
    public BufferedImage getNextFrame() throws FrameReaderException;

    /**
     * @return the current frame number
     */
    public int getCurrentFrameNumber();

    /**
     * @return true if there are more frames
     */
    public boolean hasMoreFrames();
}

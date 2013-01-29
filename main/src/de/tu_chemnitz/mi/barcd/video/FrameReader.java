package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

/**
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

    public void setWidth(int width) throws FrameReaderException;
    
    public void setHeight(int height) throws FrameReaderException;
    
    public void setWidthAndHeight(int width, int height) throws FrameReaderException;
    
    /**
     * Read the next frame.
     * 
     * @return
     * 
     * @throws FrameReaderException
     */
    public BufferedImage getNextFrame() throws FrameReaderException;
    
    /**
     * Skip the next frames.
     * 
     * @param count the number of frames to skip
     * 
     * @throws FrameReaderException
     */
    public void skipFrames(int count) throws FrameReaderException;
    
    /**
     * @return the current frame number
     */
    public int getCurrentFrameNumber();
    
    /**
     * Set the frame number of the next frame to be read.
     * 
     * @param frameNumber
     * 
     * @throws FrameReaderException
     */
    public void setFrameNumber(int frameNumber) throws FrameReaderException;
}

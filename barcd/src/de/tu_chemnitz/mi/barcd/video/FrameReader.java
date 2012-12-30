package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

public interface FrameReader {
    public int width();
    
    public int height();

    public void setWidth(int width) throws FrameReaderException;
    
    public void setHeight(int height) throws FrameReaderException;
    
    public void setWidthAndHeight(int width, int height) throws FrameReaderException;
    
    public BufferedImage nextFrame() throws FrameReaderException;
    
    public void skipFrames(int count) throws FrameReaderException;
}

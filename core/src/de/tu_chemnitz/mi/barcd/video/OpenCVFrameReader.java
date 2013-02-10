package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A frame reader using OpenCV (through JavaCV) as backend. Allows capturing
 * frames from a device (e.g. a webcam) or a file (local and remote (e.g. via
 * HTTP)).
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class OpenCVFrameReader implements FrameReader {
    private OpenCVFrameGrabber frameGrabber;
    
    protected OpenCVFrameReader(OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        this.frameGrabber = frameGrabber;
        try {
            // TODO start frameGrabber explicitly
            this.frameGrabber.start();
        } catch(Exception ex) {
            throw new FrameReaderException(ex);
        }
    }
    
    @Override
    public int getCurrentFrameNumber() {
        return frameGrabber.getFrameNumber();
    }
    
    @Override
    public void setFrameNumber(int frameNumber)
        throws FrameReaderException
    {
        try {
            frameGrabber.setFrameNumber(frameNumber);
        } catch (Exception ex) {
            throw new FrameReaderException(ex);
        }
    }
    
    @Override
    public int getLengthInFrames() {
        return frameGrabber.getLengthInFrames();
    }
    
    @Override
    public boolean isSeekable() {
        return isFinite();
    }
    
    @Override
    public boolean isFinite() {
        return getLengthInFrames() > -1;
    }

    @Override
    public int getWidth() {
        return this.frameGrabber.getImageWidth();
    }

    @Override
    public int getHeight() {
        return this.frameGrabber.getImageHeight();
    }

    @Override
    public BufferedImage getNextFrame()
        throws FrameReaderException
    {
        IplImage image = readFrame();
        return image.getBufferedImage();
    }

    @Override
    public void skipFrames(int count)
        throws FrameReaderException
    {
        setFrameNumber(getCurrentFrameNumber() + count);
    }
    
    private IplImage readFrame()
        throws FrameReaderException
    {
        try {
            return this.frameGrabber.grab();
        } catch (Exception ex) {
            throw new FrameReaderException(ex);
        }
    }
}

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
public class OpenCVFrameReader implements FrameReader {
    private OpenCVFrameGrabber frameGrabber;
    
    /**
     * Create a frame reader using a device.
     * 
     * @param deviceId the number denoting the device (0 = 1st device, 1 = 2nd device, etc.)
     * @return
     * 
     * @throws FrameReaderException
     */
    public static OpenCVFrameReader openDevice(int deviceId)
        throws FrameReaderException
    {
        return new OpenCVFrameReader(new OpenCVFrameGrabber(deviceId));
    }
    
    /**
     * Open a local or remote file.
     *
     * @param url the file's URL
     * 
     * @return
     * 
     * @throws FrameReaderException
     */
    public static OpenCVFrameReader open(String url)
        throws FrameReaderException
    {
        return new OpenCVFrameReader(new OpenCVFrameGrabber(url));
    }
    
    private OpenCVFrameReader(OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        this.frameGrabber = frameGrabber;
        try {
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
    public void setWidth(int width)
        throws FrameReaderException
    {
        setWidthAndHeight(width, getHeight());
    }

    @Override
    public void setHeight(int height)
        throws FrameReaderException
    {
        setWidthAndHeight(getWidth(), height);
    }
    
    @Override
    public void setWidthAndHeight(int width, int height)
        throws FrameReaderException
    {
        this.frameGrabber.setImageWidth(width);
        this.frameGrabber.setImageHeight(height);
        try {
            // Restart the stream so the new width and height take effect.
            this.frameGrabber.restart();
        } catch(Exception ex) {
            throw new FrameReaderException(ex);
        }
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

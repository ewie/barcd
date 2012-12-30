package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OpenCVFrameReader implements FrameReader {
    private OpenCVFrameGrabber frameGrabber;
    
    public static OpenCVFrameReader openDevice(int deviceId)
        throws FrameReaderException
    {
        return new OpenCVFrameReader(new OpenCVFrameGrabber(deviceId));
    }
    
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
    public void setWidth(int width)
        throws FrameReaderException
    {
        setWidthAndHeight(width, height());
    }

    @Override
    public void setHeight(int height)
        throws FrameReaderException
    {
        setWidthAndHeight(width(), height);
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
    public int width() {
        return this.frameGrabber.getImageWidth();
    }

    @Override
    public int height() {
        return this.frameGrabber.getImageHeight();
    }

    @Override
    public BufferedImage nextFrame()
        throws FrameReaderException
    {
        IplImage image = readFrame();
        return image.getBufferedImage();
    }

    @Override
    public void skipFrames(int count)
        throws FrameReaderException
    {
        while (count --> 0) readFrame();
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

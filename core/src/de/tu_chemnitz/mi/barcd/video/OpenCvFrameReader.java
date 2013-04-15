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
public abstract class OpenCvFrameReader implements FrameReader {
    protected final OpenCVFrameGrabber frameGrabber;

    protected OpenCvFrameReader(OpenCVFrameGrabber frameGrabber)
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
    public int getWidth() {
        return frameGrabber.getImageWidth();
    }

    @Override
    public int getHeight() {
        return frameGrabber.getImageHeight();
    }

    @Override
    public BufferedImage getNextFrame()
        throws FrameReaderException
    {
        IplImage image = readFrame();
        return image.getBufferedImage();
    }

    private IplImage readFrame()
        throws FrameReaderException
    {
        try {
            return frameGrabber.grab();
        } catch (Exception ex) {
            throw new FrameReaderException(ex);
        }
    }
}

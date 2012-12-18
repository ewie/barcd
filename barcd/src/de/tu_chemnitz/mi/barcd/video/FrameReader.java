package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

import au.notzed.jjmpeg.AVFrame;
import au.notzed.jjmpeg.PixelFormat;
import au.notzed.jjmpeg.exception.AVIOException;
import au.notzed.jjmpeg.exception.AVInvalidCodecException;
import au.notzed.jjmpeg.exception.AVInvalidStreamException;
import au.notzed.jjmpeg.io.JJMediaReader;
import au.notzed.jjmpeg.io.JJMediaReader.JJReaderVideo;

/**
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class FrameReader {
    private String sourcePath;
    private JJMediaReader mediaReader;
    private JJReaderVideo videoReader;
    
    public FrameReader(String sourcePath)
        throws FrameReaderException
    {
        this.sourcePath = sourcePath;
        try {
            this.mediaReader = new JJMediaReader(sourcePath);
            this.videoReader = this.mediaReader.openFirstVideoStream();
            this.videoReader.setOutputFormat(PixelFormat.PIX_FMT_BGRA);
        } catch (AVIOException ex) {
            throw new FrameReaderException(ex);
        } catch (AVInvalidStreamException ex) {
            throw new FrameReaderException(ex);
        } catch (AVInvalidCodecException ex) {
            throw new FrameReaderException(ex);
        }
    }
    
    public String getSourcePath() {
        return this.sourcePath;
    }
    
    public int getWidth() {
        return this.videoReader.getWidth();
    }
    
    public int getHeight() {
        return this.videoReader.getHeight();
    }
    
    public int getFrameNumber() {
        return this.videoReader.getFrame().getCodedPictureNumber();
    }
    
    public double getFrameRateNumerator() {
        return this.videoReader.getStream().getRFrameRate().getNum();
    }
    
    public double getFrameRateDenominator() {
        return this.videoReader.getStream().getRFrameRate().getDen();
    }
    
    public long getFrameCount() {
        return this.videoReader.getStream().getNBFrames();
    }
    
    public long getDuration() {
        return this.videoReader.getStream().getDuration();
    }
    
    public void skipFrame()
    {
        readNextFrame();
    }
    
    public void skipFrame(int count) {
        while (count-- != 0) skipFrame();
    }
    
    public BufferedImage nextFrame()
    {
        readNextFrame();
        return currentFrameToBufferedImage();
    }
    
    private AVFrame readNextFrame() {
        if (this.mediaReader.readFrame() == null) {
            return null;
        }
        return this.videoReader.getOutputFrame();
    }
    
    private BufferedImage currentFrameToBufferedImage()
    {
        BufferedImage image = this.videoReader.createImage();
        this.videoReader.getOutputFrame(image);
        return image;
    }
}

package de.tu_chemnitz.mi.barcd.video;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import au.notzed.jjmpeg.AVCodec;
import au.notzed.jjmpeg.AVCodecContext;
import au.notzed.jjmpeg.AVFormatContext;
import au.notzed.jjmpeg.AVFrame;
import au.notzed.jjmpeg.AVInputFormat;
import au.notzed.jjmpeg.AVPacket;
import au.notzed.jjmpeg.AVStream;
import au.notzed.jjmpeg.exception.AVDecodingError;
import au.notzed.jjmpeg.exception.AVIOException;
import au.notzed.jjmpeg.io.JJFileInputStream;

/**
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class FrameReaderSlow {
    private final int PROBE_SIZE = 4096;
    
    private String sourcePath;
    private AVFormatContext formatContext;

    private AVCodecContext codecContext;

    private AVStream stream;
    
    public FrameReaderSlow(String sourcePath)
        throws FrameReaderException
    {
        this.sourcePath = sourcePath;
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourcePath);
        } catch (FileNotFoundException ex) {
            throw new FrameReaderException("could not open input stream", ex);
        }
        
        JJFileInputStream jjfis = JJFileInputStream.create(fis);
        AVInputFormat inputFormat = jjfis.probeInput(sourcePath, 0, PROBE_SIZE);
        if (inputFormat == null) {
            throw new FrameReaderException("could not probe input stream");
        }
        this.formatContext = AVFormatContext.openInputStream(jjfis, sourcePath, inputFormat);
        int streamCount = this.formatContext.getNBStreams();
        for (int i = 0; i < streamCount; ++i) {
            AVStream s = this.formatContext.getStreamAt(i);
            this.codecContext = s.getCodec();
            if (this.codecContext.getCodecType() == AVCodecContext.AVMEDIA_TYPE_VIDEO) {
                this.stream = s;
                break;
            }
        }
        
        if (this.stream == null) {
            throw new FrameReaderException("could not find a video stream");
        }
        
        AVCodec codec = AVCodec.findDecoder(this.codecContext.getCodecID());
        
        if (codec == null) {
            throw new FrameReaderException("could not find the required video codec");
        }
        
        try {
            this.codecContext.open(codec);
        } catch (AVIOException ex) {
            throw new FrameReaderException("could not open codec", ex);
        }
    }
    
    public String getSourcePath() {
        return this.sourcePath;
    }
    
    public int getWidth() {
        return this.codecContext.getWidth();
    }
    
    public int getHeight() {
        return this.codecContext.getHeight();
    }
    
    public int getFrameNumber() {
        return this.codecContext.getFrameNumber();
    }
    
    public double getFrameRateNumerator() {
        return this.stream.getRFrameRate().getNum();
    }
    
    public double getFrameRateDenominator() {
        return this.stream.getRFrameRate().getNum();
    }
    
    public long getFrameCount() {
        return this.stream.getNBFrames();
    }
    
    public long getDuration() {
        return this.stream.getDuration();
    }
    
    public void seekFrame(long frameNumber) {
        this.formatContext.seekFrame(this.stream.getIndex(), frameNumber, AVFormatContext.AVSEEK_FLAG_FRAME);
    }
    
    public long getStartTime() {
        return this.stream.getStartTime();
    }
    
    public void skipFrame()
        throws FrameReaderException
    {
        try {
            decodeNextFrame();
        } catch (AVDecodingError ex) {
            throw new FrameReaderException("could not decode next frame", ex);
        }
    }
    
    public void skipFrame(int count)
        throws FrameReaderException
    {
        try {
            while (count-- != 0) decodeNextFrame();
        } catch (AVDecodingError ex) {
            throw new FrameReaderException("could not decode next frame", ex);
        }
    }
    
    public Frame nextFrame()
        throws FrameReaderException
    {
        AVFrame frame = null;
        
        try {
            frame = decodeNextFrame();
        } catch (AVDecodingError ex) {
            throw new FrameReaderException("could not decode next frame", ex);
        }
        
        return new Frame(frame, this.codecContext);
    }
    
    private AVFrame decodeNextFrame()
        throws AVDecodingError
    {
        AVPacket packet = AVPacket.create();
        AVFrame frame = AVFrame.create();
        while (this.formatContext.readFrame(packet) >= 0) {
            if (packet.getStreamIndex() == this.stream.getIndex()) {
                if (this.codecContext.decodeVideo(frame, packet)) {
                    break;
                }
            }
        }
        return frame;
    }
}

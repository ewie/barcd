package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IMetaData;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class FrameReader {
    private IContainer container;
    private int streamIndex = -1;
    private IStreamCoder decoder = null;
    private IPacket packet;
    private IConverter converter;
    private IVideoResampler resampler;
    private int outputHeight;
    private int outputWidth;
    
    public static FrameReader openDevice(String deviceName, String driverName)
        throws FrameReaderException
    {
        IContainer container = IContainer.make();
        IContainerFormat format = IContainerFormat.make();
        if (format.setInputFormat(driverName) < 0) {
            throw new FrameReaderException(String.format("could not open device %s with driver %s", deviceName, driverName));
        }

        int retval = container.open(deviceName, IContainer.Type.READ, format);
        
        if (retval < 0) {
        	IError error = IError.make(retval);
            throw new FrameReaderException("could not open device: " + deviceName + " ; Error: " + error.getDescription());
        }
        return new FrameReader(container);
    }
    
    public static FrameReader open(String url)
        throws FrameReaderException
    {
        IContainer container = IContainer.make();
        if (container.open(url, IContainer.Type.READ, null) < 0) {
            throw new FrameReaderException("could not open URL: " + url);
        }
        return new FrameReader(container);
    }
    
    private FrameReader(IContainer container)
        throws FrameReaderException
    {
        this.container = container;
        findVideoStream();
        this.packet = IPacket.make();
        setOutputSize(this.decoder.getWidth(), this.decoder.getHeight());
        if (this.decoder.getPixelType() != IPixelFormat.Type.BGR24) {
            this.resampler = IVideoResampler.make(
                this.decoder.getWidth(),
                this.decoder.getHeight(),
                IPixelFormat.Type.BGR24,
                this.decoder.getWidth(),
                this.decoder.getHeight(),
                this.decoder.getPixelType());
            if (this.resampler == null) {
                throw new FrameReaderException("could not create video resampler");
            }
        }
    }
        
    private void findVideoStream()
        throws FrameReaderException
    {
        int numStreams = this.container.getNumStreams();
        for (int i = 0; i < numStreams; ++i) {
            IStream stream = this.container.getStream(i);
            IStreamCoder decoder = stream.getStreamCoder();
            if (decoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                this.streamIndex = i;
                this.decoder = decoder;
                break;
            }
        }
        
        if (this.streamIndex == -1) {
            throw new FrameReaderException("could not find video stream");
        }
        
        if (this.decoder.open(null, null) < 0) {
            throw new FrameReaderException("could not open video decoder");
        }
    }
    
    private void createResampler()
        throws FrameReaderException
    {
        if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
            throw new FrameReaderException("you must install the GPL version of Xuggler");
        }
        
        this.resampler = IVideoResampler.make(
            this.outputWidth,
            this.outputHeight,
            IPixelFormat.Type.BGR24,
            this.decoder.getWidth(),
            this.decoder.getHeight(),
            this.decoder.getPixelType());
        
        if (this.resampler == null) {
            throw new FrameReaderException("could not create video resampler");
        }
    }
    
    /**
     * @param width
     * @param height
     * @throws FrameReaderException
     */
    public void setOutputSize(int width, int height)
        throws FrameReaderException
    {
        if (this.outputHeight != height || this.outputWidth != width) {
            this.outputHeight = height;
            this.outputWidth = width;
            createResampler();
        }
    }
    
    public int outputWidth() {
        return this.outputWidth;
    }
    
    public int outputHeight() {
        return this.outputHeight;
    }
    
    public BufferedImage nextFrame()
        throws FrameReaderException
    {
        IVideoPicture picture = resamplePicture(readFrame());
        if (picture.getPixelType() != IPixelFormat.Type.BGR24) {
            throw new FrameReaderException("could not decode video as BGR24");
        }
        return convertToImage(picture);
    }
    
    public void skipFrames(int count)
        throws FrameReaderException
    {
        while (count --> 0) readFrame();
    }
    
    private IVideoPicture readFrame()
        throws FrameReaderException
    {
        while (this.container.readNextPacket(this.packet) >= 0) {
            if (packet.getStreamIndex() == this.streamIndex) {
                int offset = 0;
                IVideoPicture picture = IVideoPicture.make(
                    this.decoder.getPixelType(),
                    this.decoder.getWidth(),
                    this.decoder.getHeight());
                
                while (offset < packet.getSize()) {
                    int bytesDecoded = this.decoder.decodeVideo(picture, packet, offset);
                    
                    if (bytesDecoded < 0) {
                        throw new FrameReaderException("error while decoding video");
                    }
                    offset += bytesDecoded;
                    
                    if (picture.isComplete()) {
                        return picture;
                    }
                }
            }
        }
        
        throw new FrameReaderException("no more frames");
    }
    
    private BufferedImage convertToImage(IVideoPicture picture) {
        if (this.converter == null) {
            this.converter = ConverterFactory.createConverter(
                ConverterFactory.XUGGLER_BGR_24, picture);
        }
        return this.converter.toImage(picture);
    }
    
    private IVideoPicture resamplePicture(IVideoPicture picture)
        throws FrameReaderException
    {
        if (this.resampler == null) return picture;
        IVideoPicture resampledPicture = IVideoPicture.make(
            resampler.getOutputPixelFormat(),
            resampler.getOutputWidth(),
            resampler.getOutputHeight());
        if (this.resampler.resample(resampledPicture, picture) < 0) {
            throw new FrameReaderException("could not resample video picture");
        }
        return resampledPicture;
    }
}

package de.tu_chemnitz.mi.barcd.video;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class XugglerFrameReader implements FrameReader {
    private IContainer container;
    private int streamIndex = -1;
    private IStreamCoder decoder = null;
    private IPacket packet;
    private IConverter converter;
    private IVideoResampler resampler;
    private int height;
    private int width;
    
    public static XugglerFrameReader openDevice(String deviceName, String driverName)
        throws FrameReaderException
    {
        IContainer container = IContainer.make();
        IContainerFormat format = IContainerFormat.make();
        if (format.setInputFormat(driverName) < 0) {
            throw new FrameReaderException(String.format("could not open device %s with driver %s", deviceName, driverName));
        }

        int retval = container.open(deviceName, IContainer.Type.READ, format);
        
        if (container.open(deviceName, IContainer.Type.READ, format) < 0) {
        	IError error = IError.make(retval);
            throw new FrameReaderException("could not open device: " + deviceName + " ; Error: " + error.getDescription());
        }
        return new XugglerFrameReader(container);
    }
    
    public static XugglerFrameReader open(String url)
        throws FrameReaderException
    {
        IContainer container = IContainer.make();
        if (container.open(url, IContainer.Type.READ, null) < 0) {
            throw new FrameReaderException("could not open URL: " + url);
        }
        return new XugglerFrameReader(container);
    }
    
    private XugglerFrameReader(IContainer container)
        throws FrameReaderException
    {
        this.container = container;
        findVideoStream();
        this.packet = IPacket.make();
        setWidthAndHeight(this.decoder.getWidth(), this.decoder.getHeight());
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
            this.width,
            this.height,
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
     * 
     * @throws FrameReaderException
     */
    @Override
    public void setWidth(int width)
        throws FrameReaderException
    {
        setWidthAndHeight(width, this.height);
    }
    
    /**
     * @param height
     * 
     * @throws FrameReaderException
     */
    @Override
    public void setHeight(int height)
        throws FrameReaderException
    {
        setWidthAndHeight(this.width, height);
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public BufferedImage nextFrame()
        throws FrameReaderException
    {
        IVideoPicture picture = resamplePicture(readFrame());
        if (picture.getPixelType() != IPixelFormat.Type.BGR24) {
            throw new FrameReaderException("could not decode video as BGR24");
        }
        return convertToImage(picture);
    }

    @Override
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
    
    @Override
    public void setWidthAndHeight(int width, int height)
        throws FrameReaderException
    {
        if (width != this.width || height != this.height) {
            this.width = width;
            this.height = height;
            createResampler();
        }
    }
}

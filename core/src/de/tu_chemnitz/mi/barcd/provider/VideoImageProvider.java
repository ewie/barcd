package de.tu_chemnitz.mi.barcd.provider;

import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.ImageProvider;
import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;

/**
 * Provides a sequence of images backed up by a {@link FrameReader}.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoImageProvider implements ImageProvider {
    private FrameReader frameReader;

    public VideoImageProvider(FrameReader frameReader) {
        this.frameReader = frameReader;
    }
    
    public FrameReader getFrameReader() {
        return frameReader;
    }

    @Override
    public boolean hasMore() {
        // FIXME (frameReader.getCurrentFrameNumber() + 1) to assure condition
        return !frameReader.isFinite() || (frameReader.getCurrentFrameNumber() + 1) < frameReader.getLengthInFrames();
    }

    @Override
    public BufferedImage getNext()
        throws ImageProviderException
    {
        try {
            return frameReader.getNextFrame();
        } catch (FrameReaderException ex) {
            throw new ImageProviderException(ex);
        }
    }
}

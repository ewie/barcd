package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collection;

import de.tu_chemnitz.mi.barcd.source.BufferedImageSource;
import de.tu_chemnitz.mi.barcd.source.ImageCollectionSource;
import de.tu_chemnitz.mi.barcd.source.ImageSequenceSource;
import de.tu_chemnitz.mi.barcd.source.ImageServiceSource;
import de.tu_chemnitz.mi.barcd.source.VideoDeviceSource;
import de.tu_chemnitz.mi.barcd.source.VideoStreamSource;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequence;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class SourceFactory {
    public VideoDeviceSource newVideoDeviceSource(int deviceId) {
        return new VideoDeviceSource(deviceId);
    }
    
    public BufferedImageSource newBufferedImageSource(Collection<BufferedImage> images) {
        return new BufferedImageSource(images);
    }
    
    public ImageSequenceSource newImageSequenceSource(TemplatedURLSequence url) {
        return new ImageSequenceSource(url);
    }
    
    public ImageServiceSource newImageServiceSource(URL url) {
        return new ImageServiceSource(url);
    }
    
    public ImageCollectionSource newImageCollectionSource(Collection<URL> urls) {
        return new ImageCollectionSource(urls);
    }
    
    public VideoStreamSource newVideoStreamSource(URL url) {
        return new VideoStreamSource(url);
    }
}

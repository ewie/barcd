package de.tu_chemnitz.mi.barcd.source;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.VideoImageProvider;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;
import de.tu_chemnitz.mi.barcd.video.OpenCVDeviceFrameReader;

/**
 * An image source using a camera device addressed through its ID.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoDeviceSource implements Source {
    private int deviceId;
    
    public VideoDeviceSource(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    @Override
    public VideoImageProvider getImageProvider()
        throws ImageProviderException
    {
        try {
            FrameReader fr = OpenCVDeviceFrameReader.open(deviceId);
            return new VideoImageProvider(fr);
        } catch (FrameReaderException ex) {
            throw new ImageProviderException("cannot create image provider", ex);
        }
    }
}

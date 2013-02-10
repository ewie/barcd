package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.VideoImageProvider;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;
import de.tu_chemnitz.mi.barcd.video.OpenCVFileFrameReader;

/**
 * An image source using a video resource accessed through its URL.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoStreamSource implements Source {
    private URL url;
    
    public VideoStreamSource(URL url) {
        this.url = url;
    }
    
    public URL getURL() {
        return url;
    }
    
    @Override
    public VideoImageProvider getImageProvider()
        throws ImageProviderException
    {
        try {
            OpenCVFileFrameReader fr = OpenCVFileFrameReader.open(url);
            return new VideoImageProvider(fr);
        } catch (FrameReaderException ex) {
            throw new ImageProviderException(ex);
        }
    }

}

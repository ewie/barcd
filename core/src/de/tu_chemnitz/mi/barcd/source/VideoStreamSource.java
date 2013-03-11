package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.VideoImageProvider;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;
import de.tu_chemnitz.mi.barcd.video.OpenCvFileFrameReader;

/**
 * An image source using a video resource accessed through its URL.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoStreamSource extends Source {
    private URL url;

    public VideoStreamSource(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public VideoImageProvider createImageProvider(int initialFrameNumber)
        throws ImageProviderException
    {
        try {
            OpenCvFileFrameReader fr = OpenCvFileFrameReader.open(url);
            fr.setFrameNumber(initialFrameNumber);
            return new VideoImageProvider(fr);
        } catch (FrameReaderException ex) {
            throw new ImageProviderException(ex);
        }
    }
}

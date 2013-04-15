package de.tu_chemnitz.mi.barcd.provider;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;

import de.tu_chemnitz.mi.barcd.ImageProvider;
import de.tu_chemnitz.mi.barcd.ImageProviderException;

/**
 * A sequence of images retrieved from a sequence of URLs with one URL per
 * image.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RemoteImageProvider implements ImageProvider {
    private final Iterator<URL> urls;

    /**
     * @param urls an iterator over all image URLs
     */
    public RemoteImageProvider(Iterator<URL> urls) {
        this.urls = urls;
    }

    @Override
    public boolean hasMore() {
        return urls.hasNext();
    }

    @Override
    public BufferedImage consume()
        throws ImageProviderException
    {
        try {
            return ImageIO.read(urls.next());
        } catch (IOException ex) {
            throw new ImageProviderException(ex);
        }
    }
}

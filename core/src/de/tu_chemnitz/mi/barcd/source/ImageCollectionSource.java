package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.SeekableSource;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;

/**
 * An image source using a collection of images accessed through their
 * respective URLs.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageCollectionSource extends SeekableSource {
    private Collection<URL> urls;

    public ImageCollectionSource(Collection<URL> urls, int initialFrameNumber) {
        super(initialFrameNumber);
        this.urls = urls;
    }

    public ImageCollectionSource(Collection<URL> urls) {
        this(urls, 0);
    }
    
    public Collection<URL> getURLs() {
        return urls;
    }
    
    @Override
    public RemoteImageProvider getImageProvider()
        throws ImageProviderException
    {
        Iterator<URL> it = urls.iterator();
        int i = getInitialFrameNumber();
        while (i --> 0) {
            it.next();
        }
        return new RemoteImageProvider(it);
    }
}

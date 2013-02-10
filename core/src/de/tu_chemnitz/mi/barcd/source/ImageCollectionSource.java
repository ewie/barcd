package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;
import java.util.Collection;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;

/**
 * An image source using a collection of images accessed through their
 * respective URLs.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageCollectionSource implements Source {
    private Collection<URL> urls;

    public ImageCollectionSource(Collection<URL> urls) {
        this.urls = urls;
    }
    
    public Collection<URL> getURLs() {
        return urls;
    }
    
    @Override
    public RemoteImageProvider getImageProvider()
        throws ImageProviderException
    {
        return new RemoteImageProvider(urls.iterator());
    }
}

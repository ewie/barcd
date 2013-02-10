package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;

public class ImageServiceSource implements Source {
    private final URL url;
    
    public ImageServiceSource(URL url) {
        this.url = url;
    }
    
    public URL getURL() {
        return url;
    }

    @Override
    public RemoteImageProvider getImageProvider()
        throws ImageProviderException
    {
        return new RemoteImageProvider(new Iterator<URL>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public URL next() {
                return url;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        });
    }
}

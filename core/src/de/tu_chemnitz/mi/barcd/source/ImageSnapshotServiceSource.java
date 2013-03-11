package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;

/**
 * An image source using a web service which serves a new image on every
 * request, e.g. get the current image of a surveillance camera accessible
 * through a network.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageSnapshotServiceSource extends Source {
    private final URL url;

    public ImageSnapshotServiceSource(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public RemoteImageProvider createImageProvider(int initialFrameNumber)
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

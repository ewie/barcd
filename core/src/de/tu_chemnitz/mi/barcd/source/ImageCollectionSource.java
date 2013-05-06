/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.source;

import java.net.URL;
import java.util.Iterator;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;

/**
 * An image source using a collection of images accessed through their
 * respective URLs.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageCollectionSource extends Source {
    private final Iterable<URL> urls;

    public ImageCollectionSource(Iterable<URL> urls) {
        this.urls = urls;
    }

    public Iterable<URL> getUrls() {
        return urls;
    }

    @Override
    public RemoteImageProvider createImageProvider(int initialFrameNumber)
        throws ImageProviderException
    {
        Iterator<URL> it = urls.iterator();
        while (initialFrameNumber --> 0) {
            it.next();
        }
        return new RemoteImageProvider(it);
    }
}

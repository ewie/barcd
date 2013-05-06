/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.source;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;
import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;
import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequenceIterator;

/**
 * An image source using a templated URL.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageSequenceSource extends Source {
    private final TemplatedUrlSequence sequence;

    public ImageSequenceSource(TemplatedUrlSequence sequence) {
        this.sequence = sequence;
    }

    public TemplatedUrlSequence getUrlSequence() {
        return sequence;
    }

    @Override
    public RemoteImageProvider createImageProvider(int initialFrameNumber)
        throws ImageProviderException
    {
        TemplatedUrlSequenceIterator it = sequence.iterator();
        while (initialFrameNumber --> 0) {
            it.next();
        }
        return new RemoteImageProvider(it);
    }
}

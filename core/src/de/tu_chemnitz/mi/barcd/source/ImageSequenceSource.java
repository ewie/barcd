package de.tu_chemnitz.mi.barcd.source;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.SeekableSource;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequence;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequenceIterator;

/**
 * An image source using a templated URL.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageSequenceSource extends SeekableSource {
    private TemplatedURLSequence sequence;
    
    public ImageSequenceSource(TemplatedURLSequence sequence, int initialFrameNumber) {
        super(initialFrameNumber);
        this.sequence = sequence;
    }
    
    public ImageSequenceSource(TemplatedURLSequence sequence) {
        this(sequence, 0);
    }
    
    public TemplatedURLSequence getSequence() {
        return sequence;
    }
    
    @Override
    public RemoteImageProvider getImageProvider()
        throws ImageProviderException
    {
        TemplatedURLSequenceIterator it = sequence.iterator();
        int i = getInitialFrameNumber();
        while (i --> 0) {
            it.next();
        }
        return new RemoteImageProvider(it);
    }
}

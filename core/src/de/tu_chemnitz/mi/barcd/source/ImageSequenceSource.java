package de.tu_chemnitz.mi.barcd.source;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequence;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequenceIterator;

/**
 * An image source using a templated URL.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageSequenceSource extends Source {
    private TemplatedURLSequence sequence;
    
    public ImageSequenceSource(TemplatedURLSequence sequence) {
        this.sequence = sequence;
    }
    
    public TemplatedURLSequence getSequence() {
        return sequence;
    }
    
    @Override
    public RemoteImageProvider getImageProvider(int initialFrameNumber)
        throws ImageProviderException
    {
        TemplatedURLSequenceIterator it = sequence.iterator();
        while (initialFrameNumber --> 0) {
            it.next();
        }
        return new RemoteImageProvider(it);
    }
}

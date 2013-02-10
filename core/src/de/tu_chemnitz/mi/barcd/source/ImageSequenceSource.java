package de.tu_chemnitz.mi.barcd.source;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.RemoteImageProvider;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequence;

/**
 * An image source using a templated URL.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageSequenceSource implements Source {
    private TemplatedURLSequence sequence;
    
    public ImageSequenceSource(TemplatedURLSequence sequence) {
        this.sequence = sequence;
    }
    
    public TemplatedURLSequence getSequence() {
        return sequence;
    }
    
    @Override
    public RemoteImageProvider getImageProvider()
        throws ImageProviderException
    {
        return new RemoteImageProvider(sequence.iterator());
    }
}

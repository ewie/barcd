package de.tu_chemnitz.mi.barcd.app;

import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.Extractor.FrameHandler;
import de.tu_chemnitz.mi.barcd.ImageProviderException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ExtractionWorker extends Worker {
    private Extractor extractor;
    
    public ExtractionWorker(Extractor extractor) {
        this.extractor = extractor;
    }
    
    public void setFrameHandler(FrameHandler frameHandler) {
        extractor.setFrameHandler(frameHandler);
    }
    
    /**
     * @throws ImageProviderException 
     */
    @Override
    public void work()
        throws ImageProviderException
    {
        while (!isTerminated() && extractor.hasMoreImages()) {
            extractor.processNextImage();
        }
    }
}

package de.tu_chemnitz.mi.barcd.app;

import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.ExtractorException;
import de.tu_chemnitz.mi.barcd.FrameHandler;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ExtractionWorker extends Worker {
    private final Extractor extractor;

    public ExtractionWorker(Extractor extractor) {
        this.extractor = extractor;
    }

    public void setFrameHandler(FrameHandler frameHandler) {
        extractor.setFrameHandler(frameHandler);
    }

    /**
     * @throws ExtractorException
     */
    @Override
    public void work()
        throws ExtractorException
    {
        while (!shouldTerminate() && extractor.hasMoreImages()) {
            extractor.processNextImage();
        }
    }
}

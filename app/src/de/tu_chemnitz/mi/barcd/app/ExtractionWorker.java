/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

import de.tu_chemnitz.mi.barcd.ExtractionHandler;
import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.ExtractorException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ExtractionWorker extends Worker {
    private final Extractor extractor;

    public ExtractionWorker(Extractor extractor) {
        this.extractor = extractor;
    }

    public void setExtractionHandler(ExtractionHandler frameHandler) {
        extractor.setExtractionHandler(frameHandler);
    }

    /**
     * @throws ExtractorException
     */
    @Override
    protected void work()
        throws ExtractorException
    {
        while (!shouldTerminate() && extractor.hasMoreImages()) {
            extractor.processNextImage();
        }
    }
}

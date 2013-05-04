package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface ExtractionHandler {
    /**
     * Handle the given extraction and image.
     *
     * @param extraction the extraction to handle
     * @param image the source image
     */
    public void handleExtraction(Extraction extraction, BufferedImage image);
}

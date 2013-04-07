package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface ImageEnhancer {
    /**
     * Enhance the given image.
     *
     * @param image the image to enhance
     *
     * @return the enhanced image
     *
     * @throws ImageEnhancerException if the image could not be enhanced
     */
    public BufferedImage enhanceImage(BufferedImage image)
        throws ImageEnhancerException;
}

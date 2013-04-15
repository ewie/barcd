package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.image.ImproveImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DefaultImageEnhancer implements ImageEnhancer {
    private final ImproveImage improve = new ImproveImage();

    @Override
    public BufferedImage enhanceImage(BufferedImage image)
        throws ImageEnhancerException
    {
        try {
            return improve.checkImage(image);
        } catch (Exception ex) {
            throw new ImageEnhancerException(ex);
        }
    }
}

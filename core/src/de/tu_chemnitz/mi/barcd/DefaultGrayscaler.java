package de.tu_chemnitz.mi.barcd;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The default grayscaler uses {@link Graphics2D#drawImage} to convert images
 * to grayscale. This method is blazingly fast but does not conform to the
 * human perception of luminance.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DefaultGrayscaler implements Grayscaler {
    @Override
    public BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = out.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return out;
    }
}

package de.tu_chemnitz.mi.barcd.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * An operator to scale {@link BufferedImage} instances.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ScalingOperator {
    /**
     * Scale an image to a given width. The new scaled image height is chosen so
     * the aspect ratio will be preserved.
     *
     * @param image the image to scale
     * @param width the new image width
     *
     * @return the scaled image
     */
    public BufferedImage apply(BufferedImage image, int width) {
        int height = (image.getHeight() * width) / image.getWidth();

        BufferedImage out = new BufferedImage(width, height, image.getType());

        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, out.getWidth(), out.getHeight(), null);
        g.dispose();

        return out;
    }
}

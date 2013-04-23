package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface FrameHandler {
    /**
     * Handle the given frame and image.
     *
     * @param frame the frame to handle
     * @param image the frame image
     */
    public void handleFrame(Frame frame, BufferedImage image);
}

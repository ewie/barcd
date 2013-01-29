package de.tu_chemnitz.mi.barcd.image;

import java.awt.Rectangle;

/**
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class DelegatedLuminanceImage implements LuminanceImage {
    private LuminanceImage image;
    
    private Rectangle roi;
    
    public DelegatedLuminanceImage(LuminanceImage image) {
        this(image, 0, 0, image.getWidth(), image.getHeight());
    }
    
    public DelegatedLuminanceImage(LuminanceImage image, int x, int y, int width, int height) {
        this.image = image;
        this.roi = new Rectangle(x, y, width, height);
    }
    
    public LuminanceImage getDelegatedImage() {
        return image;
    }
    
    public Rectangle getROI() {
        return new Rectangle(roi);
    }
    
    @Override
    public int getWidth() {
        return roi.width;
    }

    @Override
    public int getHeight() {
        return roi.height;
    }

    @Override
    public int getIntensityAt(int x, int y) {
        return image.getIntensityAt(x + roi.x, y + roi.y);
    }
    
    @Override
    public int getValueAt(int x, int y) {
        return image.getValueAt(x + roi.x, y + roi.y);
    }

    @Override
    public void setIntensityAt(int x, int y, int v) {
        image.setIntensityAt(x + roi.x, y + roi.y, v);
    }

    @Override
    public void setValueAt(int x, int y, int v) {
        image.setValueAt(x + roi.x, y + roi.y, v);
    }
}

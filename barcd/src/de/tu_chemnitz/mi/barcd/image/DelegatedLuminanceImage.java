package de.tu_chemnitz.mi.barcd.image;

import java.awt.Rectangle;

/**
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class DelegatedLuminanceImage implements LuminanceImage {
    private LuminanceImage image;
    
    private Rectangle roi;
    
    public DelegatedLuminanceImage(LuminanceImage image) {
        this(image, 0, 0, image.width(), image.height());
    }
    
    public DelegatedLuminanceImage(LuminanceImage image, int x, int y, int width, int height) {
        this.image = image;
        this.roi = new Rectangle(x, y, width, height);
    }
    
    public LuminanceImage image() {
        return image;
    }
    
    public Rectangle roi() {
        return new Rectangle(roi);
    }
    
    @Override
    public int width() {
        return roi.width;
    }

    @Override
    public int height() {
        return roi.height;
    }

    @Override
    public int intensityAt(int x, int y) {
        return image.intensityAt(x + roi.x, y + roi.y);
    }
    
    @Override
    public int valueAt(int x, int y) {
        return image.valueAt(x + roi.x, y + roi.y);
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

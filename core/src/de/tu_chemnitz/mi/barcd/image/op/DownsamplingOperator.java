package de.tu_chemnitz.mi.barcd.image.op;

import de.tu_chemnitz.mi.barcd.image.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.Operator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DownsamplingOperator implements Operator {
    private int factor;

    public DownsamplingOperator(int factor) {
        if (factor <= 1) {
            throw new IllegalArgumentException("expecting downsampling factor > 1");
        }
        this.factor = factor;
    }
    
    public int getFactor() {
        return this.factor;
    }
    
    public LuminanceImage apply(LuminanceImage in) {
        int width = in.getWidth() / this.factor;
        int height = in.getHeight() / this.factor;
        BufferedLuminanceImage out = new BufferedLuminanceImage(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                //out.setLuminanceAt(x, y, in.luminanceAt(x * this.factor, y * this.factor));
                out.setValueAt(x, y, in.getValueAt(x * factor, y * factor));
            }
        }
        return out;
    }
}

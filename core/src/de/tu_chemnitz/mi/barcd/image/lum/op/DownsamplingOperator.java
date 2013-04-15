package de.tu_chemnitz.mi.barcd.image.lum.op;

import de.tu_chemnitz.mi.barcd.image.lum.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.lum.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.lum.Operator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class DownsamplingOperator implements Operator {
    private final int factor;

    public DownsamplingOperator(int factor) {
        if (factor <= 1) {
            throw new IllegalArgumentException("expecting downsampling factor > 1");
        }
        this.factor = factor;
    }

    public int getFactor() {
        return factor;
    }

    @Override
    public LuminanceImage apply(LuminanceImage in) {
        int width = in.getWidth() / factor;
        int height = in.getHeight() / factor;
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

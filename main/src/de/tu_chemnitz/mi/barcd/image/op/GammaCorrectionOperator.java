package de.tu_chemnitz.mi.barcd.image.op;

import de.tu_chemnitz.mi.barcd.image.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.Operator;

/**
 * An operator performing gamma correction.
 * 
 * Depending on the gamma value an image is either darkened (gamma < 1) or
 * brightened (gamma > 1).
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class GammaCorrectionOperator implements Operator {
    private double gamma;
    
    /**
     * @param gamma the gamma value to be used by this operator
     */
    public GammaCorrectionOperator(double gamma) {
        this.gamma = gamma;
    }
    
    /**
     * @return the gamma value used by the operator
     */
    public double getGamma() {
        return this.gamma;
    }

    @Override
    public LuminanceImage apply(LuminanceImage in) {
        int w = in.getWidth();
        int h = in.getHeight();
        double g = 1 / this.gamma;
        BufferedLuminanceImage out = new BufferedLuminanceImage(w, h);
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double value = Math.pow(in.getIntensityAt(x, y) / (double) LuminanceImage.MAX_INTENSITY, g) * LuminanceImage.MAX_INTENSITY;
                out.setIntensityAt(x, y, (int) value);
            }
        }
        return out;
    }
}

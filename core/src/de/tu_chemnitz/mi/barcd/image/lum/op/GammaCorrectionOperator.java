package de.tu_chemnitz.mi.barcd.image.lum.op;

import de.tu_chemnitz.mi.barcd.image.lum.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.lum.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.lum.Operator;

/**
 * An operator performing gamma correction.
 *
 * Depending on the gamma value an image is either darkened (gamma < 1) or
 * brightened (gamma > 1).
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class GammaCorrectionOperator implements Operator {
    private final double gamma;

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
        return gamma;
    }

    @Override
    public LuminanceImage apply(LuminanceImage in) {
        int w = in.getWidth();
        int h = in.getHeight();
        double g = 1 / gamma;
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

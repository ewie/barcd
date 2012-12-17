package de.tu_chemnitz.mi.barcd.image;

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
        LuminanceImage out = new LuminanceImage(w, h);
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                double value = Math.pow(in.getValueAt(x, y) / (double) LuminanceImage.MAX_VALUE, g) * LuminanceImage.MAX_VALUE;
                out.setValueAt(x, y, (int) value);
            }
        }
        return out;
    }
}

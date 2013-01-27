package de.tu_chemnitz.mi.barcd.image.op;

import de.tu_chemnitz.mi.barcd.image.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.Operator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class GammaDenoisingOperator implements Operator {
    private double gamma;
    private double[] directLookup;
    private double[] inverseLookup;
    
    public GammaDenoisingOperator(double gamma) {
        this.gamma = gamma;
        createLookupTable();
    }
    
    public double gamma() {
        return this.gamma;
    }
    
    public LuminanceImage apply(LuminanceImage in) {
        int width = in.width();
        int height = in.height();
        
        BufferedLuminanceImage out = new BufferedLuminanceImage(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int v = (int) (lookupDirect(in.intensityAt(x, y)) * LuminanceImage.MAX_INTENSITY);
                int w = (int) (lookupInverse(v) * LuminanceImage.MAX_INTENSITY);
                out.setIntensityAt(x, y, w);
            }
        }
        return out;
    }
    
    private void createLookupTable() {
        double max = LuminanceImage.MAX_INTENSITY;
        int size = LuminanceImage.MAX_INTENSITY - LuminanceImage.MIN_INTENSITY + 1;
        this.directLookup = new double[size];
        this.inverseLookup = new double[size];
        double igamma = 1 / this.gamma;
        
        for (int i = 0; i < size; ++i) {
            this.directLookup[i] = Math.pow(i / max, this.gamma);
            this.inverseLookup[i] = Math.pow(i / max, igamma);
        }
    }
    
    private double lookupDirect(int value) {
        return this.directLookup[value];
    }
    
    private double lookupInverse(int value) {
        return this.inverseLookup[value];
    }
}
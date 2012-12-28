package de.tu_chemnitz.mi.barcd.image;

public class GammaDenoisingOperator implements Operation {
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
        
        LuminanceImage out = new LuminanceImage(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int v = (int) (lookupDirect(in.valueAt(x, y)) * LuminanceImage.MAX_VALUE);
                int w = (int) (lookupInverse(v) * LuminanceImage.MAX_VALUE);
                out.setValueAt(x, y, w);
            }
        }
        return out;
    }
    
    private void createLookupTable() {
        double max = LuminanceImage.MAX_VALUE;
        int size = LuminanceImage.MAX_VALUE - LuminanceImage.MIN_VALUE + 1;
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
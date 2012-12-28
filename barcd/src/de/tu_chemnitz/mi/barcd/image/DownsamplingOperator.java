package de.tu_chemnitz.mi.barcd.image;

public class DownsamplingOperator implements Operation {
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
        int width = in.width() / this.factor;
        int height = in.height() / this.factor;
        LuminanceImage out = new LuminanceImage(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                out.setValueAt(x, y, in.valueAt(x * this.factor, y * this.factor));
            }
        }
        return out;
    }
}

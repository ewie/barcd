package de.tu_chemnitz.mi.barcd.image;

public class GammaDenoisingOperator implements Operator {
    private double gamma;
    
    public GammaDenoisingOperator(double gamma) {
        this.gamma = gamma;
    }
    
    public LuminanceImage apply(LuminanceImage in) {
        double igamma = 1 / this.gamma;
        int width = in.getWidth();
        int height = in.getHeight();
        double max = LuminanceImage.MAX_VALUE;
        LuminanceImage out = new LuminanceImage(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int v = (int) (Math.pow(in.getValueAt(x, y) / max, this.gamma) * LuminanceImage.MAX_VALUE);
                int w = (int) (Math.pow(v / max, igamma) * LuminanceImage.MAX_VALUE);
                out.setValueAt(x, y, w);
            }
        }
        return out;
    }
}

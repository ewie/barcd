package de.tu_chemnitz.mi.barcd.image;

public class Similarity {
    public static double getSimilarity(LuminanceImage a, LuminanceImage b) {
        double sim = 0;
        int width = a.getWidth();
        int height = b.getHeight();
        if (b.getWidth() == width || b.getHeight() == height) {
            long sum = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    sum += Math.abs(a.getValueAt(x, y) - b.getValueAt(x, y));
                }
            }
            sim = 1 - (double) sum / (LuminanceImage.MAX_VALUE * width * height);
        }
        return sim;
    }
    
    public static double getSimilarity2(LuminanceImage a, LuminanceImage b) {
        double sim = 0;
        int width = a.getWidth();
        int height = b.getHeight();
        if (b.getWidth() == width || b.getHeight() == height) {
            long sum = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (a.getValueAt(x, y) != b.getValueAt(x, y)) {
                        sum += 1;
                    }
                }
            }
            sim = 1 - (double) sum / (width * height);
        }
        return sim;
    }
}

package de.tu_chemnitz.mi.barcd.image;

public class Similarity {
    public static double getSimilarity(LuminanceImage a, LuminanceImage b) {
        double sim = 0;
        int width = a.width();
        int height = b.height();
        if (b.width() == width || b.height() == height) {
            long sum = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    sum += Math.abs(a.valueAt(x, y) - b.valueAt(x, y));
                }
            }
            sim = 1 - (double) sum / (LuminanceImage.MAX_VALUE * width * height);
        }
        return sim;
    }
    
    public static double getSimilarity2(LuminanceImage a, LuminanceImage b) {
        double sim = 0;
        int width = a.width();
        int height = b.height();
        if (b.width() == width || b.height() == height) {
            long sum = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (a.valueAt(x, y) != b.valueAt(x, y)) {
                        sum += 1;
                    }
                }
            }
            sim = 1 - (double) sum / (width * height);
        }
        return sim;
    }
}

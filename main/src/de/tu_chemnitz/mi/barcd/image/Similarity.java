package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Similarity {
    public static double getSimilarity(BufferedLuminanceImage a, BufferedLuminanceImage b) {
        double sim = 0;
        int width = a.getWidth();
        int height = b.getHeight();
        if (b.getWidth() == width || b.getHeight() == height) {
            long sum = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    sum += Math.abs(a.getIntensityAt(x, y) - b.getIntensityAt(x, y));
                }
            }
            sim = 1 - (double) sum / (BufferedLuminanceImage.MAX_INTENSITY * width * height);
        }
        return sim;
    }
    
    public static double getSimilarity2(BufferedLuminanceImage a, BufferedLuminanceImage b) {
        double sim = 0;
        int width = a.getWidth();
        int height = b.getHeight();
        if (b.getWidth() == width || b.getHeight() == height) {
            long sum = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (a.getIntensityAt(x, y) != b.getIntensityAt(x, y)) {
                        sum += 1;
                    }
                }
            }
            sim = 1 - (double) sum / (width * height);
        }
        return sim;
    }
}

package de.tu_chemnitz.mi.barcd.image;

/**
 * A threshold selector using the mean of all pixel values.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class MeanValueThresholdSelector implements GlobalThresholdSelector {
    @Override
    public int getThreshold(LuminanceImage input) {
        int width = input.width();
        int height = input.height();
        long mean = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                mean += input.valueAt(x, y);
            }
        }
        return (int) (mean / (width * height));
    }
}
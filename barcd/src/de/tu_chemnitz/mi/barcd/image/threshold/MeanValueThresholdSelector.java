package de.tu_chemnitz.mi.barcd.image.threshold;

import de.tu_chemnitz.mi.barcd.image.GlobalThresholdSelector;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;

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
                mean += input.intensityAt(x, y);
            }
        }
        return (int) (mean / (width * height));
    }
}
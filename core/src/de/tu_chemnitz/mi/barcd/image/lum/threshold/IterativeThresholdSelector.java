package de.tu_chemnitz.mi.barcd.image.lum.threshold;

import de.tu_chemnitz.mi.barcd.image.lum.GlobalThresholdSelector;
import de.tu_chemnitz.mi.barcd.image.lum.LuminanceImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class IterativeThresholdSelector implements GlobalThresholdSelector {
    private static final int MAX_ROUNDS = 10;

    @Override
    public int getThreshold(LuminanceImage input) {
        int width = input.getWidth();
        int height = input.getHeight();

        int threshold = 128;

        for (int i = 0; i < MAX_ROUNDS; ++i) {
            int fgAvg = 0;
            int bgAvg = 0;
            int fgCount = 0;
            int bgCount = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int p = input.getIntensityAt(x, y);
                    if (p < threshold) {
                        bgAvg += p;
                        bgCount += 1;
                    } else {
                        fgAvg += p;
                        fgCount += 1;
                    }
                }
            }
            int newThreshold = (bgCount == 0 ? 0 : (bgAvg / bgCount) + fgCount == 0 ? 0 : (fgAvg / fgCount)) / 2;
            if (newThreshold == threshold) {
                break;
            }
            threshold = newThreshold;
        }

        return threshold;
    }
}

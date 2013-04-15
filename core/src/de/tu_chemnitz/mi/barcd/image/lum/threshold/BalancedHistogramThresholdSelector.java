package de.tu_chemnitz.mi.barcd.image.lum.threshold;

import de.tu_chemnitz.mi.barcd.image.lum.GlobalThresholdSelector;
import de.tu_chemnitz.mi.barcd.image.lum.LuminanceImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class BalancedHistogramThresholdSelector implements GlobalThresholdSelector {
    @Override
    public int getThreshold(LuminanceImage input) {
        int[] hist = new int[256];
        int width = input.getWidth();
        int height = input.getHeight();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                hist[input.getIntensityAt(x, y)] += 1;
            }
        }

        int start = 0;
        int end = 0;

        for (int i = 0; i < 0x100; ++i) {
            if (hist[i] > 0 && start == 0) start = i;
            if (hist[i] > 0) end = i;
        }

        int middle = (start + end) / 2;
        int lweight = getWeight(hist, start, middle);
        int rweight = getWeight(hist, middle, end);

        while (start <= end) {
            if (lweight < rweight) {
                rweight -= hist[end--];
                if ((start + end) / 2 < middle) {
                    rweight += hist[middle];
                    lweight -= hist[middle--];
                }
            } else if (lweight >= rweight) {
                lweight -= hist[start++];
                if ((start + end) / 2 > middle) {
                    lweight += hist[middle + 1];
                    rweight -= hist[middle + 1];
                    middle++;
                }
            }
        }

        return middle;
    }

    private int getWeight(int[] hist, int a, int b) {
        int weight = 0;
        for (int i = a; i <= b; ++i) {
            weight += hist[i];
        }
        return weight;
    }
}

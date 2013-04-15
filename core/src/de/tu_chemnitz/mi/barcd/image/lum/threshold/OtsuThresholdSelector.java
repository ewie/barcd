package de.tu_chemnitz.mi.barcd.image.lum.threshold;

import de.tu_chemnitz.mi.barcd.image.lum.GlobalThresholdSelector;
import de.tu_chemnitz.mi.barcd.image.lum.LuminanceImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class OtsuThresholdSelector implements GlobalThresholdSelector {
    @Override
    public int getThreshold(LuminanceImage input) {
        int[] hist = getHistogram(input);
        double[] prob = getProbabilities(hist);

        int max = 255;
        //for (int i = 0; i < hist.length; ++i) {
        //    if (hist[i] > max) max = hist[i];
        //}

        int threshold = 0;
        double smax = 0;

        for (int t = 0; t <= max; ++t) {
            double w1 = getClassProbability(prob, 0, t),
                   m1 = getClassMean(hist, prob, 0, t),
                   w2 = getClassProbability(prob, t + 1, max),
                   m2 = getClassMean(hist, prob, t + 1, max);
            double s = w1 * w2 * (m1 - m2) * (m1 - m2);
            if (s > smax) {
                smax = s;
                threshold = t;
            }
        }

        return threshold;
    }

    private int[] getHistogram(final LuminanceImage input) {
        int[] hist = new int[256];
        int width = input.getWidth();
        int height = input.getHeight();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                hist[input.getIntensityAt(x, y)] += 1;
            }
        }

        return hist;
    }

    private double getClassProbability(double[] prob, int a, int b) {
        double w = 0;
        for (int i = a; i <= b; ++i) {
            w += prob[i];
        }
        return w;
    }

    private double getClassMean(int[] hist, double[] prob, int a, int b) {
        double m = 0;
        for (int i = a; i <= b; ++i) {
            m += prob[i] * hist[i];
        }
        return m;
    }

    private double[] getProbabilities(final int[] hist) {
        double[] prob = new double[hist.length];
        double n = 0;
        for (int i = 0; i < hist.length; ++i) {
            n += hist[i];
        }
        for (int i = 0; i < hist.length; ++i) {
            prob[i] = hist[i] / n;
        }
        return prob;
    }
}

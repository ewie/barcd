package de.tu_chemnitz.mi.barcd.image.lum;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class GlobalBinarizer implements Binarizer {
    private static final int VALUE = BufferedLuminanceImage.MAX_INTENSITY;

    private GlobalThresholdSelector thresholder;

    public GlobalBinarizer(GlobalThresholdSelector thresholder) {
        this.thresholder = thresholder;
    }

    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int threshold = thresholder.getThreshold(input);

        int width = input.getWidth();
        int height = input.getHeight();

        LuminanceImage output = new BufferedLuminanceImage(width, height);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                output.setIntensityAt(x, y, input.getIntensityAt(x, y) > threshold ? VALUE : 0);
            }
        }

        return output;
    }
}

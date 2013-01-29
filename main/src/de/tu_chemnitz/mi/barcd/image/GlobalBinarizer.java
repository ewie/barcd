package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class GlobalBinarizer implements Binarizer {
    final int VALUE = BufferedLuminanceImage.MAX_INTENSITY;
    
    private GlobalThresholdSelector thresholder;
    
    public GlobalBinarizer(GlobalThresholdSelector thresholder) {
        this.thresholder = thresholder;
    }
    
    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int threshold = this.thresholder.getThreshold(input);
        
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

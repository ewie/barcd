package de.tu_chemnitz.mi.barcd.image;

public class GlobalBinarizer implements Binarizer {
    final int VALUE = 0xff;
    
    private GlobalThresholdSelector thresholder;
    
    public GlobalBinarizer(GlobalThresholdSelector thresholder) {
        this.thresholder = thresholder;
    }
    
    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int threshold = this.thresholder.getThreshold(input);
        
        int width = input.width();
        int height = input.height();
        
        LuminanceImage output = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                output.setValueAt(x, y, input.valueAt(x, y) > threshold ? VALUE : 0);
            }
        }
        
        return output;
    }
}

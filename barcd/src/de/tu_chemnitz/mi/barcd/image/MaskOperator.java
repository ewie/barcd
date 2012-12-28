package de.tu_chemnitz.mi.barcd.image;

public class MaskOperator implements Operation {
    private LuminanceImage mask;
    
    public MaskOperator(LuminanceImage mask) {
        this.mask = mask;
    }

    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int width = input.width();
        int height = input.height();
        
        LuminanceImage output = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                output.setValueAt(x, y, input.valueAt(x, y) & mask.valueAt(x, y));
            }
        }
        
        return output;
    }
}
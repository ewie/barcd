package de.tu_chemnitz.mi.barcd.image.op;

import de.tu_chemnitz.mi.barcd.image.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.Operator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class MaskOperator implements Operator {
    private LuminanceImage mask;
    
    public MaskOperator(LuminanceImage mask) {
        this.mask = mask;
    }

    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        
        BufferedLuminanceImage output = new BufferedLuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                output.setIntensityAt(x, y, input.getIntensityAt(x, y) & mask.getIntensityAt(x, y));
            }
        }
        
        return output;
    }
}
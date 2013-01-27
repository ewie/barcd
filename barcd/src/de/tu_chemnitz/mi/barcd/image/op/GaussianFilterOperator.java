package de.tu_chemnitz.mi.barcd.image.op;

import de.tu_chemnitz.mi.barcd.image.KernelOperator;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;

/**
 * A kernel realizing a Gaussian filter.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class GaussianFilterOperator implements KernelOperator {
    private ConvolutionOperator kernel;
    
    /**
     * Create a square Gaussian filter.
     * 
     * @param size the kernel's width and height
     * @param sigma the standard deviation of the Gaussian distribution
     */
    public GaussianFilterOperator(int size, double sigma) {
        double kernel[][] = new double[size][size];
        double d = 1.0 / Math.sqrt(2 * Math.PI * sigma * sigma);
        int tx = -size / 2;
        int ty = -size / 2;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                kernel[x][y] = d * Math.exp(-((x + tx) * (x + tx) + (y + ty) * (y + ty)) / (2 * sigma * sigma));
            }
        }
        d = kernel[0][0];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                kernel[x][y] /= d;
            }
        }
        this.kernel = new ConvolutionOperator(kernel);
    }
    
    @Override
    public int getWidth() {
        return this.kernel.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.kernel.getHeight();
    }
    
    @Override
    public LuminanceImage apply(LuminanceImage input) {
        return kernel.apply(input);
    }
}

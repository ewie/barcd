package de.tu_chemnitz.mi.barcd.image.lum.op;

import de.tu_chemnitz.mi.barcd.image.lum.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.lum.KernelOperator;
import de.tu_chemnitz.mi.barcd.image.lum.LuminanceImage;

/**
 * A generic kernel operator.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class ConvolutionOperator implements KernelOperator {
    private double[][] weights;
    private int width;
    private int height;
    
    public ConvolutionOperator(double[][] weights) {
        this.weights = weights;
        this.width = this.height = this.weights[0].length;
    }
    
    /**
     * @param width the kernel width
     * @param height the kernel height
     * @param weights the kernel weights in row-major order
     */
    public ConvolutionOperator(int width, int height, double[] weights) {
        this.width = width;
        this.height = height;
        this.weights = new double[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.weights[i][j] = weights[i + j * width];
            }
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Apply the kernel to an image by convolution.
     * 
     * @param input the image to process
     * 
     * @return the result of the convolution as a new image
     */
    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        
        BufferedLuminanceImage output = new BufferedLuminanceImage(width, height);
        int a = this.width - this.width / 2 + 1;
        int b = this.height - this.height / 2 + 1;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                double k = 0;
                double c = 0;
                
                for (int i = 0; i < this.width; ++i) {
                    for (int j = 0; j < this.height; ++j) {
                        int u = x + i - a;
                        int v = y + j - b;
                        if (u >= 0 && u < width && v >= 0 && v < height) {
                            int p = input.getIntensityAt(u, v);
                            k += p * this.weights[i][j];
                            c += this.weights[i][j];
                        }
                    }
                }
                
                if (c != 0) k /= c;
                
                output.setValueAt(x, y, (int) k);
            }
        }
        
        return output;
    }
}

package de.tu_chemnitz.mi.barcd.image.op;

import de.tu_chemnitz.mi.barcd.image.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.KernelOperator;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;

/**
 * An operator to perform image dilation.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class DilationOperator implements KernelOperator {
    private boolean[][] weights;
    int width;
    int height;
    
    /**
     * Create a square dilation operator with all weights set to true.
     * 
     * @param size the kernel width and height
     */
    public DilationOperator(int size) {
        this(size, size);
    }
    
    /**
     * Create a rectangular dilation operator with all weights set to true.
     * 
     * @param width the kernel width
     * @param height the kernel height
     */
    public DilationOperator(int width, int height) {
        this.width = width;
        this.height = height;
        this.weights = new boolean[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.weights[i][j] = true;
            }
        }
    }
    
    /**
     * Create a rectangular dilation operator with custom weights.
     * 
     * @param width the kernel width
     * @param height the kernel height
     * @param weights the weights
     */
    public DilationOperator(int width, int height, boolean[] weights) {
        this.width = width;
        this.height = height;
        this.weights = new boolean[width][height];
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
     * Perform the dilation on a given image.
     * 
     * @param input the image to dilate
     * 
     * @return the dilation of the input image
     */
    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int width = input.width();
        int height = input.height();
        int a = this.width - this.width / 2 + 1;
        int b = this.height - this.height / 2 + 1;
        
        BufferedLuminanceImage output = new BufferedLuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int k = 0;
                for (int i = 0; i < this.width; ++i) {
                    for (int j = 0; j < this.height; ++j) {
                        if (this.weights[i][j]) {
                            int u = x + i - a;
                            int v = y + j - b;
                            if (u >= 0 && u < width && v >= 0 && v < height) {
                                int t = input.intensityAt(u, v);
                                if (t > k) k = t;
                            }
                        }
                    }
                }
                output.setIntensityAt(x, y, k);
            }
        }
        
        return output;
    }
}
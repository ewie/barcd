package de.tu_chemnitz.mi.barcd.image;

/**
 * A generic kernel operator.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class ConvolutionOperator implements KernelOperation {
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
     * @param kernel the kernel weights in row-major order
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
        int width = input.width();
        int height = input.height();
        
        LuminanceImage output = new LuminanceImage(width, height);
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
                            int p = input.valueAt(u, v);
                            k += p * this.weights[i][j];
                            c += this.weights[i][j];
                        }
                    }
                }
                
                if (c != 0) k /= c;
                
                output.setValueAt(x, y, (int) Math.round(k));
            }
        }
        return output;
    }
}

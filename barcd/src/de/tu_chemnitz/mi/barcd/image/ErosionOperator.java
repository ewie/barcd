package de.tu_chemnitz.mi.barcd.image;

/**
 * An operator to perform image erosion.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class ErosionOperator implements KernelOperator {
    private boolean[][] weights;
    int width;
    int height;
    
    public ErosionOperator(int width, int height) {
        this.width = width;
        this.height = height;
        this.weights = new boolean[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.weights[i][j] = true;
            }
        }
    }
    
    public ErosionOperator(int width, int height, boolean[] kernel) {
        this.width = width;
        this.height = height;
        this.weights = new boolean[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.weights[i][j] = kernel[i + j * width];
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

    @Override
    public LuminanceImage apply(LuminanceImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        int a = this.width - this.width / 2 + 1;
        int b = this.height - this.height / 2 + 1;
        
        LuminanceImage output = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int k = Integer.MAX_VALUE;
                for (int i = 0; i < this.width; ++i) {
                    for (int j = 0; j < this.height; ++j) {
                        if (this.weights[i][j]) {
                            int u = x + i - a;
                            int v = y + j - b;
                            if (u >= 0 && u < width && v >= 0 && v < height) {
                                int t = input.getValueAt(u, v);
                                if (t < k) k = t;
                            }
                        }
                    }
                }
                output.setValueAt(x, y, k);
            }
        }
        
        return output;
    }
}
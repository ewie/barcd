package de.tu_chemnitz.mi.barcd.image;

/**
 * An operator to perform image dilation.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DilationOperator {
    private int width;
    private int height;
    
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
    }
    
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    
    /**
     * Perform the dilation on a given image.
     * 
     * @param pixels the row-major pixel data to dilate
     * @param int width the image width
     * @param int height the image height
     * 
     * @return the dilation of the input image
     */
    public int[] apply(int[] pixels, int width, int height) {
        // Border padding depending on the window size.
        int a = this.width - this.width / 2 - 1;
        int b = this.height - this.height / 2 - 1;
        
        int[] out = new int[pixels.length];
        
        // The column maximums of the current window. When the window moves one
        // column to the right. The maximum of the left window column will be
        // overwritten with the maximum of the right window column.
        int[] max = new int[this.width];
        
        // Image width and height minus border.
        int w = width - a;
        int h = height - b;
        
        // TODO currently ignores the border
        for (int y = b; y < h; ++y) {
            // Set up the maximums for the current row. Calculate the column
            // maximums without the rightmost window column.
            for (int u = 0; u < this.width - 1; ++u) {
                int k = 0;
                for (int j = 0; j < this.height; ++j) {
                    int v = y + j - b;
                    int t = pixels[v * width + u];
                    if (t > k) k = t;
                }
                max[u % this.width] = k;
            }
            
            // For every pixel in the current row calculate the maximum of the
            // rightmost window column and override the maximum of the leftmost
            // window column which is no longer needed. Then select the maximum
            // of all columns.
            for (int x = a; x < w; ++x) {
                // Maximum of the rightmost window column.
                int k = 0;
                int u = x + a;
                for (int j = 0; j < this.height; ++j) {
                    int v = y + j - b;
                    int t = pixels[v * width + u];
                    if (t > k) k = t;
                }
                max[u % this.width] = k;
                
                // Maximum of all column maximums.
                k = 0;
                for (int i = 0; i < max.length; ++i) {
                    if (max[i] > k) k = max[i];
                }
                out[x + y * width] = k;
            }
        }
        
        return out;
    }
}

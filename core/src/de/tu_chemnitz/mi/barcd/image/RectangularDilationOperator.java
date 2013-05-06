package de.tu_chemnitz.mi.barcd.image;

/**
 * An operator to perform image dilation with a rectangular window.
 *
 * TODO The implementation currently ignores pixels on the border (~ width/2 on
 *   the left and right, ~ height/2 on the top and bottom). The effect is
 *   negligible if the window is just a few pixels wide and high.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RectangularDilationOperator implements DilationOperator {
    private final int windowWidth;
    private final int windowHeight;

    /**
     * Create a square dilation operator with the given window width and height.
     *
     * @param size the window width and height
     */
    public RectangularDilationOperator(int size) {
        this(size, size);
    }

    /**
     * Create a rectangular dilation operator with a given window width and
     * height.
     *
     * @param width the window width
     * @param height the window height
     */
    public RectangularDilationOperator(int width, int height) {
        windowWidth = width;
        windowHeight = height;
    }

    /**
     * @return the width of the dilation window
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * @return the height of the dilation window
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    @Override
    public int[] dilate(int[] pixels, int width, int height) {
        // Border padding depending on the window size.
        int a = windowWidth - windowWidth / 2 - 1;
        int b = windowHeight - windowHeight / 2 - 1;

        int[] out = new int[pixels.length];

        // The column maximums of the current window. When the window moves one
        // column to the right. The maximum of the left window column will be
        // overwritten with the maximum of the right window column.
        int[] max = new int[windowWidth];

        // Image width and height minus border.
        int w = width - a;
        int h = height - b;

        for (int y = b; y < h; ++y) {
            // Set up the maximums for the current row. Calculate the column
            // maximums without the rightmost window column.
            for (int u = 0; u < windowWidth - 1; ++u) {
                int k = 0;
                for (int j = 0; j < windowHeight; ++j) {
                    int v = y + j - b;
                    int t = pixels[v * width + u];
                    if (t > k) k = t;
                }
                max[u % windowWidth] = k;
            }

            // For every pixel in the current row calculate the maximum of the
            // rightmost window column and override the maximum of the leftmost
            // window column which is no longer needed. Then select the maximum
            // of all columns.
            for (int x = a; x < w; ++x) {
                // Maximum of the rightmost window column.
                int k = 0;
                int u = x + a;
                for (int j = 0; j < windowHeight; ++j) {
                    int v = y + j - b;
                    int t = pixels[v * width + u];
                    if (t > k) k = t;
                }
                max[u % windowWidth] = k;

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

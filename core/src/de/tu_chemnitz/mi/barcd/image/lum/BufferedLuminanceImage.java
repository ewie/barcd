package de.tu_chemnitz.mi.barcd.image.lum;

/**
 * A gray-scale representation of an image.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public class BufferedLuminanceImage implements LuminanceImage {
    private int width;
    private int height;
    private byte[][] values;
    
    /**
     * Create a luminance image without data.
     * 
     * @param width
     * @param height
     */
    public BufferedLuminanceImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.values = new byte[width][height];
    }
    
    /**
     * @param width
     * @param height
     * @param values the luminance values in row-major order, expecting
     *   (width * height) elements
     */
    public BufferedLuminanceImage(int width, int height, int[] values) {
        this(width, height);
        if (values.length < width * height) {
            throw new IllegalArgumentException(
                String.format("expecting %d values for a %dx%d image",
                    width * height, width, height));
        }
        for (int i = 0, y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                this.setIntensityAt(x, y, values[i++]);
            }
        }
    }
    
    /**
     * @return the image's width
     */
    @Override
    public int getWidth() {
        return this.width;
    }
    
    /**
     * @return the image's height
     */
    @Override
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Get a pixel's luminance value.
     * 
     * @param x
     * @param y
     * 
     * @return the luminance of the pixel at (x, y) in the range of [MIN_VALUE,
     *         MAX_VALUE]
     */
    @Override
    public int getIntensityAt(int x, int y) {
        return this.values[x][y] & MAX_INTENSITY;
    }

    @Override
    public int getValueAt(int x, int y) {
        return values[x][y];
    }
    
    /**
     * Set a pixel's luminance value. Ensures the value is in the range of
     * [MIN_VALUE, MAX_VALUE].
     * 
     * @param x
     * @param y
     * @param value
     */
    @Override
    public void setIntensityAt(int x, int y, int value) {
        this.values[x][y] = (byte) Math.max(MIN_INTENSITY, Math.min(value, MAX_INTENSITY));
    }

    @Override
    public void setValueAt(int x, int y, int value) {
        values[x][y] = (byte) value;
    }
}

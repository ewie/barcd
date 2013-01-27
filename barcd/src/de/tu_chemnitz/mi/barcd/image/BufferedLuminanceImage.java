package de.tu_chemnitz.mi.barcd.image;

import java.awt.image.BufferedImage;

/**
 * A gray-scale representation of an image.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class BufferedLuminanceImage implements LuminanceImage {
    public static interface Grayscaler {
        public int convert(int argb);
    }
    
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
    public int width() {
        return this.width;
    }
    
    /**
     * @return the image's height
     */
    @Override
    public int height() {
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
    public int intensityAt(int x, int y) {
        return this.values[x][y] & MAX_INTENSITY;
    }

    @Override
    public int valueAt(int x, int y) {
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
    
    /**
     * Convert the image to a {@link BufferedImage} with type TYPE_INT_ARGB.
     * 
     * @return
     */
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[this.width * this.height];
        int offset = 0;
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                int v = this.intensityAt(x, y);
                pixels[offset++] = 0xff000000 | (v << 16) | (v << 8) | v;
            }
        }
        image.setRGB(0, 0, this.width, this.height, pixels, 0, this.width);
        return image;
    }
    
    /**
     * Convert a {@link BufferedImage} to its gray-scale representation.
     * 
     * @param image
     * @param grayscaler
     * 
     * @return
     */
    public static BufferedLuminanceImage fromBufferedImage(BufferedImage image, Grayscaler grayscaler) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, new int[width * height], 0, width);
        int[] values = new int[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            values[i] = grayscaler.convert(pixels[i]);
        }
        return new BufferedLuminanceImage(width, height, values);
    }
}

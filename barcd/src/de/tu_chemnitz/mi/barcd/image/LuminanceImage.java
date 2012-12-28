package de.tu_chemnitz.mi.barcd.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * A gray-scale representation of an image.
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class LuminanceImage {
    public static interface Grayscaler {
        public int convert(int argb);
    }
    
    public static final int MIN_VALUE = 0x00;
    public static final int MAX_VALUE = 0xff;
    
    private int width;
    private int height;
    private byte[][] values;
    
    /**
     * Create a luminance image without data.
     * 
     * @param width
     * @param height
     */
    public LuminanceImage(int width, int height) {
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
    public LuminanceImage(int width, int height, int[] values) {
        this(width, height);
        if (values.length < width * height) {
            throw new IllegalArgumentException(
                String.format("expecting %d values for a %dx%d image",
                    width * height, width, height));
        }
        for (int i = 0, y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                this.setValueAt(x, y, values[i++]);
            }
        }
    }
    
    /**
     * @return the image's width
     */
    public int width() {
        return this.width;
    }
    
    /**
     * @return the image's height
     */
    public int height() {
        return this.height;
    }
    
    /**
     * Get a pixel's luminance value.
     * 
     * @param x
     * @param y
     * 
     * @return the luminance of the pixel at (x, y)
     */
    public int valueAt(int x, int y) {
        return this.values[x][y] & MAX_VALUE;
    }
    
    /**
     * Set a pixel's luminance value.
     * 
     * @param x
     * @param y
     * @param value
     */
    public void setValueAt(int x, int y, int value) {
        this.values[x][y] = (byte) Math.max(MIN_VALUE, Math.min(value, MAX_VALUE));
    }
    
    /**
     * Convert the image to a {@link BufferedImage} with type {@link
     * BufferedImage.TYPE_BYTE_GRAY}.
     * 
     * @return
     */
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        int[] pixels = new int[this.width * this.height];
        for (int y = 0; y < this.height; ++y) {
            int offset = y * this.width ;
            for (int x = 0; x < this.width; ++x) {
                pixels[offset + x] = this.valueAt(x, y);
            }
        }
        raster.setPixels(0, 0, this.width, this.height, pixels);
        return image;
    }
    
    public static LuminanceImage fromBufferedImage(BufferedImage image, Grayscaler grayscaler) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, new int[width * height], 0, width);
        int[] values = new int[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            values[i] = grayscaler.convert(pixels[i]);
        }
        return new LuminanceImage(width, height, values);
    }
}

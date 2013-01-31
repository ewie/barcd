package de.tu_chemnitz.mi.barcd.image;

import java.awt.image.BufferedImage;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class LuminanceImageConverter {
    public static class ColorConverter {
        public int rgb2lum(int argb) {
            int r = (argb >> 16) & 0xff;
            int g = (argb >> 8) & 0xff;
            int b = argb & 0xff;
            return
                (r + g + g + b) / 4
            //    (r * 299 + g * 587 + b * 114) / 1000
            //    (r * 2126 + g * 7152 + b * 722) / 10000
            ;
        }
        
        public int lum2rgb(int lum) {
            return 0xff000000 | (lum << 16) | (lum << 8) | lum;
        }
    }
    
    private ColorConverter converter;
    
    /**
     * Construct an image converter using the default color converter.
     */
    public LuminanceImageConverter() {
        this(new ColorConverter());
    }
    
    /**
     * @param converter a custom color converter (may override default
     *   conversion methods)
     */
    public LuminanceImageConverter(ColorConverter converter) {
        this.converter = converter;
    }
    
    /**
     * Convert a {@link LuminanceImage} to a {@link BufferedImage} using
     * {@link ColorConverter#lum2rgb(int)}.
     * 
     * @param in the image to convert
     * 
     * @return the {@link BufferedImage} with image type {@link BufferedImage#TYPE_INT_ARGB}
     */
    public BufferedImage toBufferedImage(LuminanceImage in) {
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[in.getWidth() * in.getHeight()];
        int offset = 0;
        for (int y = 0; y < in.getHeight(); ++y) {
            for (int x = 0; x < in.getWidth(); ++x) {
                int v = in.getIntensityAt(x, y);
                pixels[offset++] = converter.lum2rgb(v);
            }
        }
        out.setRGB(0, 0, in.getWidth(), in.getHeight(), pixels, 0, in.getWidth());
        return out;
    }
    
    /**
     * Convert a {@link BufferedImage} to a {@link BufferedLuminanceImage} using
     * {@link ColorConverter#rgb2lum(int)}.
     * 
     * @param in the image to convert
     * 
     * @return the image representing the input's luminance
     */
    public LuminanceImage toLuminanceImage(BufferedImage in) {
        int width = in.getWidth();
        int height = in.getHeight();
        int[] pixels = in.getRGB(0, 0, width, height, new int[width * height], 0, width);
        int[] values = new int[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            values[i] = converter.rgb2lum(pixels[i]);
        }
        return new BufferedLuminanceImage(width, height, values);
    }
}

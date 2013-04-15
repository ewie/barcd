package de.tu_chemnitz.mi.barcd.image.lum;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
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

    private final ColorConverter converter;

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
     * Convert a {@link LuminanceImage} to a true color {@link BufferedImage}
     * using {@link ColorConverter#lum2rgb(int)}.
     *
     * @param in the image to convert
     *
     * @return the {@link BufferedImage} with image type
     *   {@link BufferedImage#TYPE_INT_ARGB}
     */
    public BufferedImage toTrueColorBufferedImage(LuminanceImage in) {
        int w = in.getWidth();
        int h = in.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[w * h];
        int offset = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int v = in.getIntensityAt(x, y);
                pixels[offset++] = converter.lum2rgb(v);
            }
        }
        out.setRGB(0, 0, w, h, pixels, 0, w);
        return out;
    }

    /**
     * Convert a {@link LuminanceImage} to a gray-scale {@link BufferedImage}
     * using {@link LuminanceImage#getIntensityAt(int, int)}.
     *
     * @param in the image to convert
     *
     * @return the {@link BufferedImage} with image type
     *   {@link BufferedImage#TYPE_BYTE_GRAY}
     */
    public BufferedImage toBufferedImage(LuminanceImage in) {
        int w = in.getWidth();
        int h = in.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = out.getRaster();
        int[] pixels = new int[w * h];
        int offset = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                pixels[offset++] = in.getIntensityAt(x, y);
            }
        }
        raster.setPixels(0, 0, w, h, pixels);
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

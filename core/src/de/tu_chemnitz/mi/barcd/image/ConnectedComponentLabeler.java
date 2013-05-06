/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.image;

import de.tu_chemnitz.mi.barcd.util.UnionFind;

/**
 * The connected component labeler assigns each pixel of an image a label. Two
 * pixels have the same label if they are connected (4-connectivity) and have
 * the same value.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ConnectedComponentLabeler {
    private static final int BACKGROUND_VALUE = 0;

    public static final int BACKGROUND_LABEL = 0;

    /**
     * Perform connected-component-labeling on the given image data. Assigns all
     * pixels that belong to the background (value 0) the same label (0).
     *
     * @param pixels the pixels in row-major order with at least {@code
     *   width * height} elements
     * @param width the image width
     * @param height the image height
     *
     * @return an array with {@code width * height} labels in the same order as
     *   the pixels
     */
    public int[] process(int[] pixels, int width, int height) {
        return process(pixels, width, height, BACKGROUND_VALUE);
    }

    /**
     * Perform connected-component-labeling on the given image data. Assigns the
     * same label (0) to all background pixels.
     *
     * @param pixels the pixels in row-major order with at least {@code
     *   width * height} elements
     * @param width the image width
     * @param height the image height
     * @param backgroundValue the value of pixels to be considered background
     *
     * @return an array with {@code width * height} labels in the same order as
     *   the pixels
     */
    public int[] process(int[] pixels, int width, int height, int backgroundValue) {
        // The labels are automatically initialized to zero. So every pixel is
        // initially labeled as background.
        int[] labels = new int[width * height];

        // The value of the next label. Increment manually. Value 0 is used
        // implicitly for background pixels.
        int nextLabel = 1;

        // Use the union-find algorithm to keep track of equivalent labels.
        UnionFind eq = new UnionFind(width * height);

        // Assign a new label to the first pixel if it's not part of the
        // background.
        if (pixels[0] != backgroundValue) {
            pixels[0] = nextLabel++;
        }

        // Process the first row (w/o the first pixel) comparing each pixel
        // with the pixel to its left.
        for (int x = 1; x < width; ++x) {
            int v = pixels[x];
            if (v == backgroundValue) continue;
            if (v == pixels[x - 1]) {
                labels[x] = labels[x - 1];
            } else {
                labels[x] = nextLabel++;
            }
        }

        // Process the first row (w/o the first pixel) comparing each pixel
        // with the pixel to its top.
        for (int y = 1; y < height; ++y) {
            int v = pixels[y * width];
            if (v == backgroundValue) continue;
            if (v == pixels[(y - 1) * width]) {
                labels[y * width] = labels[(y - 1) * width];
            } else {
                labels[y * width] = nextLabel++;
            }
        }

        // Row-wise process all remaining pixels comparing a pixel's value with
        // pixel to the left and top.
        for (int y = 1; y < height; ++y) {
            for (int x = 1; x < width; ++x) {
                int v = pixels[x + y * width];
                if (v == backgroundValue) continue;

                int vn = pixels[x + (y - 1) * width];
                int vw = pixels[(x - 1) + y * width];

                if (v == vn && v == vw) {
                    // If two labels are equivalent assign the smaller one
                    // (the one assigned earlier).
                    int ln = labels[x + (y - 1) * width];
                    int lw = labels[(x - 1) + y * width];
                    labels[x + y * width] = Math.min(ln, lw);
                    eq.union(lw, ln);
                    eq.union(ln, lw);
                } else if (v == vn) {
                    labels[x + y * width] = labels[x + (y - 1) * width];
                } else if (v == vw) {
                    labels[x + y * width] = labels[(x - 1) + y * width];
                } else {
                    labels[x + y * width] = nextLabel++;
                }
            }
        }

        // Resolve equivalent labels.
        for (int i = 0; i < pixels.length; ++i) {
            if (pixels[i] == backgroundValue) {
                continue;
            }
            labels[i] = eq.find(labels[i]);
        }

        return labels;
    }
}

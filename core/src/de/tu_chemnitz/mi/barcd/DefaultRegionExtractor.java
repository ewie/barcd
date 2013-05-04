package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.image.ConnectedComponentLabeler;
import de.tu_chemnitz.mi.barcd.image.DilationOperator;
import de.tu_chemnitz.mi.barcd.image.RectangularDilationOperator;
import de.tu_chemnitz.mi.barcd.image.ScalingOperator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DefaultRegionExtractor implements RegionExtractor {
    private static final int PROCESSING_IMAGE_WIDTH = 1000;

    private final ConvolveOp edgeOperatorX;
    private final ConvolveOp edgeOperatorY;
    private final DilationOperator dilationOperator;
    private final ScalingOperator scalingOperator = new ScalingOperator();

    /**
     * Create a default region extractor with custom dilation operator.
     *
     * @param dilationOperator the dilation operator to use
     */
    public DefaultRegionExtractor(DilationOperator dilationOperator) {
        this.dilationOperator = dilationOperator;

        // Robert's operator
        edgeOperatorX = new ConvolveOp(new Kernel(2, 2, new float[] {
            1,  0,
            0, -1
        }));

        edgeOperatorY = new ConvolveOp(new Kernel(2, 2, new float[] {
             0, 1,
            -1, 0
        }));

        // Sobel operator
//        edgeOperatorX = new ConvolveOp(new Kernel(3, 3, new float[] {
//            -1, -2, -1,
//             0,  0,  0,
//             1,  2,  1
//        }));
//
//        edgeOperatorY = new ConvolveOp(new Kernel(3, 3, new float[] {
//             -1, 0, 1,
//             -2, 0, 2,
//             -1, 0, 1
//        }));

        // Scharr operator
//        edgeOperatorX = new ConvolveOp(new Kernel(3, 3, new float[] {
//            -3, -10, -3,
//             0,   0,  0,
//             3,  10,  3
//        }));
//
//        edgeOperatorY = new ConvolveOp(new Kernel(3, 3, new float[] {
//             -3, 0,  3,
//            -10, 0, 10,
//             -3, 0,  3
//        }));

        // Prewitt operator
//        edgeOperatorX = new ConvolveOp(new Kernel(3, 3, new float[] {
//            -1, -1, -1,
//             0,  0,  0,
//             1,  1,  1
//        }));
//
//        edgeOperatorY = new ConvolveOp(new Kernel(3, 3, new float[] {
//             -1, 0, 1,
//             -1, 0, 1,
//             -1, 0, 1
//        }));
    }

    /**
     * Create a default region extractor with default dilation operator (5x5).
     */
    public DefaultRegionExtractor() {
        this(new RectangularDilationOperator(5, 5));
    }

    @Override
    public Region[] extractRegions(BufferedImage image) {
        double scalingFactor = 1;
        if (image.getWidth() > PROCESSING_IMAGE_WIDTH) {
            scalingFactor = (double) image.getWidth() / PROCESSING_IMAGE_WIDTH;
            image = scalingOperator.apply(image, PROCESSING_IMAGE_WIDTH);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[] g = extractEdges(image.getData());
        int[] s = performSegmentation(g, width, height);
        return createRegions(s, width, height, scalingFactor);
    }

    private int[] extractEdges(Raster input) {
        int width = input.getWidth();
        int height = input.getHeight();

        WritableRaster rx = edgeOperatorX.createCompatibleDestRaster(input);
        WritableRaster ry = edgeOperatorY.createCompatibleDestRaster(input);

        // The gradient values should be in the interval [-255, 255] but
        // ConvolveOp#filter() probably uses the absolute value [0,255]
        // thereby losing any information about the gradient's direction.
        edgeOperatorX.filter(input, rx);
        edgeOperatorY.filter(input, ry);

        int[] px = rx.getPixels(0, 0, width, height, (int[]) null);
        int[] py = ry.getPixels(0, 0, width, height, (int[]) null);

        int[] pxy = new int[px.length];

        for (int i = 0; i < px.length; ++i) {
            // TODO evaluate how to combine both gradient images
            //pxy[i] = Math.max(px[i], py[i]);
            pxy[i] = Math.min(px[i] + py[i], 255);
        }

        return pxy;
    }

    private int[] performSegmentation(int[] in, int w, int h) {
        int[] p = dilationOperator.apply(in, w, h);

        long mean = 0;
        for (int i = 0; i < p.length; ++i) {
            mean += p[i];
        }
        mean /= p.length;
        for (int i = 0; i < p.length; ++i) {
            p[i] = p[i] > mean ? 255 : 0;
        }

        return p;
    }

    private Region[] createRegions(int[] input, int width, int height, double scalingFactor) {
        ConnectedComponentLabeler ccl = new ConnectedComponentLabeler();
        int[] labels = ccl.process(input, width, height);

        Map<Integer, List<Point>> labelPointsMap = new HashMap<Integer, List<Point>>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int label = labels[x + y * width];

                // Skip background components.
                if (label == ConnectedComponentLabeler.BACKGROUND_LABEL) {
                    continue;
                }

                if (!labelPointsMap.containsKey(label)) {
                    labelPointsMap.put(label, new LinkedList<Point>());
                }

                // Get the correct coordinates by applying the scaling factor.
                Point p = new Point(x * scalingFactor, y * scalingFactor);
                labelPointsMap.get(label).add(p);
            }
        }

        Region[] regions = new Region[labelPointsMap.size()];

        double scalingFactor2 = scalingFactor * scalingFactor;

        int i = 0;
        for (List<Point> points : labelPointsMap.values()) {
            // Because we used a down-scaled image to extract a region's
            // coordinates the number of coordinates is smaller than the number
            // of coordinates if the image in its original size had been used.
            // Therefore we have to scale the number of generating coordinates
            // by the squared scaling factor (the vertical and horizontal
            // scaling factors are the same).
            int generatingPointCount = (int) (points.size() * scalingFactor2);
            regions[i++] = Region.createFromPoints(points, generatingPointCount);
        }

        return regions;
    }
}
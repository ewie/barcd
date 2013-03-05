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
import de.tu_chemnitz.mi.barcd.image.ScalingOperator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DefaultRegionExtractor implements RegionExtractor {
    private static final int PROCESSING_IMAGE_WIDTH = 1000;

    private ConvolveOp gx;
    private ConvolveOp gy;
    private DilationOperator dilate;
    private ScalingOperator scale = new ScalingOperator();

    public DefaultRegionExtractor() {
        gx = new ConvolveOp(new Kernel(2, 2, new float[] {
            1,  0,
            0, -1
        }));

        gy = new ConvolveOp(new Kernel(2, 2, new float[] {
             0, 1,
            -1, 0
        }));

        dilate = new DilationOperator(5, 5);
    }

    @Override
    public Region[] extractRegions(BufferedImage image) {
        double scalingFactor = (double) image.getWidth() / PROCESSING_IMAGE_WIDTH;
        image = scale.apply(image, PROCESSING_IMAGE_WIDTH);
        int[] g = gradient(image.getData());
        int[] s = segment(g, image.getWidth(), image.getHeight());
        return regions(s, image.getWidth(), image.getHeight(), scalingFactor);
    }

    private int[] gradient(Raster input) {
        int width = input.getWidth();
        int height = input.getHeight();

        WritableRaster rx = gx.createCompatibleDestRaster(input);
        WritableRaster ry = gy.createCompatibleDestRaster(input);

        gx.filter(input, rx);
        gy.filter(input, ry);

        int[] px = rx.getPixels(0, 0, width, height, (int[]) null);
        int[] py = ry.getPixels(0, 0, width, height, (int[]) null);

        int[] pxy = new int[px.length];

        for (int i = 0; i < px.length; ++i) {
            pxy[i] = px[i] + py[i];
        }

        return pxy;
    }

    private int[] segment(int[] in, int w, int h) {
        int[] p = dilate.apply(in, w, h);

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

    private Region[] regions(int[] input, int width, int height, double scalingFactor) {
        ConnectedComponentLabeler ccl = new ConnectedComponentLabeler();
        int[] labels = ccl.process(input, width, height);

        Map<Integer, List<Point>> labelPointsMap = new HashMap<Integer, List<Point>>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int label = labels[x + y * width];
                if (label == 0) continue;
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
            // coordinates the number of coordinates is smaller than if had used
            // image in its original size. Therefore we have to scale the
            // number of generating coordinates by the squared scaling factor
            // (the vertical and horizontal scaling factors are the same).
            int generatingPointCount = (int) (points.size() * scalingFactor2);
            regions[i++] = Region.createFromPoints(points, generatingPointCount);
        }

        return regions;
    }
}
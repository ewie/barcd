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

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RegionExtractor {
    private ConvolveOp gx;
    private ConvolveOp gy;
    private DilationOperator dilate;

    public RegionExtractor() {
        this.gx = new ConvolveOp(new Kernel(2, 2, new float[] {
            1,  0,
            0, -1
        }));
        
        this.gy = new ConvolveOp(new Kernel(2, 2, new float[] {
             0, 1,
            -1, 0
        }));

        this.dilate = new DilationOperator(5, 5);
    }
    
    public Region[] detect(BufferedImage input) {
        int[] g = gradient(input.getData());
        int[] s = segment(g, input.getWidth(), input.getHeight());
        return regions(s, input.getWidth(), input.getHeight());
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
    
    private Region[] regions(int[] input, int width, int height) {
        ConnectedComponentLabeler ccl = new ConnectedComponentLabeler();
        int[][] labels = ccl.process(input, width, height);
        
        Map<Integer, List<Point>> labelPointsMap = new HashMap<Integer, List<Point>>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int label = labels[x][y];
                if (label == 0) continue;
                if (!labelPointsMap.containsKey(label)) {
                    labelPointsMap.put(label, new LinkedList<Point>());
                }
                labelPointsMap.get(label).add(new Point(x, y));
            }
        }
        
        Region[] regions = new Region[labelPointsMap.size()];
        
        int i = 0;
        for (List<Point> points : labelPointsMap.values()) {
            regions[i++] = Region.createFromPoints(points);
        }
        
        return regions;
    }
}
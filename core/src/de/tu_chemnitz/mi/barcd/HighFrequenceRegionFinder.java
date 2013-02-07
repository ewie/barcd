package de.tu_chemnitz.mi.barcd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.image.Binarizer;
import de.tu_chemnitz.mi.barcd.image.BufferedLuminanceImage;
import de.tu_chemnitz.mi.barcd.image.ConnectedComponentLabeler;
import de.tu_chemnitz.mi.barcd.image.GlobalBinarizer;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.op.ConvolutionOperator;
import de.tu_chemnitz.mi.barcd.image.op.DilationOperator;
import de.tu_chemnitz.mi.barcd.image.op.GaussianFilterOperator;
import de.tu_chemnitz.mi.barcd.image.threshold.MeanValueThresholdSelector;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class HighFrequenceRegionFinder {
    public static interface RegionFilter {
        public boolean filter(Region region);
    }

    private ConvolutionOperator sobelx;
    private ConvolutionOperator sobely;
    private GaussianFilterOperator gaussian;
    private DilationOperator dilation;

    public HighFrequenceRegionFinder() {
        /*
        this.sobelx = new ConvolutionOperator(3, 3, new double[] {
            -1,  0,  1,
            -1,  0,  1,
            -1,  0,  1
        });
        
        this.sobely = new ConvolutionOperator(3, 3, new double[] {
            -1, -1, -1,
             0,  0,  0,
             1,  1,  1
        });
        */
        
        /*
        this.sobelx = new ConvolutionOperator(3, 1, new double[] {
            -1,  0,  1
        });
        
        this.sobely = new ConvolutionOperator(1, 3, new double[] {
            -1,
             0,
             1
        });
        */
        
        /**/
        this.sobelx = new ConvolutionOperator(2, 2, new double[] {
            1,  0,
            0, -1
        });
        
        this.sobely = new ConvolutionOperator(2, 2, new double[] {
             0, 1,
            -1, 0
        });
        /**/

        this.dilation = new DilationOperator(5, 5);
        
        this.gaussian = new GaussianFilterOperator(9, 10);
    }
    
    public Region[] detect(LuminanceImage input, RegionFilter filter) {
        return regions(segment(gradient(input)), filter);
    }
    
    private LuminanceImage gradient(LuminanceImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        
        LuminanceImage gx = sobelx.apply(input);
        LuminanceImage gy = sobely.apply(input);
        
        LuminanceImage gxy = new BufferedLuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int vx = gx.getValueAt(x, y);
                int vy = gy.getValueAt(x, y);
                gxy.setIntensityAt(x, y, Math.abs(vx) + Math.abs(vy));
            }
        }
        return gxy;
    }
    
    private LuminanceImage segment(LuminanceImage in) {
        LuminanceImage dxy = dilation.apply(in);
        Binarizer bin = new GlobalBinarizer(new MeanValueThresholdSelector());
        LuminanceImage b = bin.apply(dxy);
        return b;
    }
    
    private Region[] regions(LuminanceImage input, RegionFilter filter) {
        int width = input.getWidth();
        int height = input.getHeight();
        
        ConnectedComponentLabeler ccl = new ConnectedComponentLabeler();
        int[][] labels = ccl.process(input);
        
        Map<Integer, List<Point>> regions = new HashMap<Integer, List<Point>>();
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int label = labels[x][y];
                if (label == 0) continue;
                if (!regions.containsKey(label)) {
                    regions.put(label, new LinkedList<Point>());
                }
                regions.get(label).add(new Point(x, y));
            }
        }
        
        List<Region> filteredRegions = new LinkedList<Region>();

        for (List<Point> coords : regions.values()) {
            Region region = Region.createFromPoints(coords.toArray(new Point[coords.size()]));
            if (filter == null || filter.filter(region)) {
                filteredRegions.add(region);
            }
        }

        return filteredRegions.toArray(new Region[filteredRegions.size()]);
    }
}
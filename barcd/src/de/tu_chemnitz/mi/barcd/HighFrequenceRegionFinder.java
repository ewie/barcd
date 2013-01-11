package de.tu_chemnitz.mi.barcd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.geometry.Region;
import de.tu_chemnitz.mi.barcd.image.Binarizer;
import de.tu_chemnitz.mi.barcd.image.ConnectedComponentLabeler;
import de.tu_chemnitz.mi.barcd.image.ConvolutionOperator;
import de.tu_chemnitz.mi.barcd.image.DilationOperator;
import de.tu_chemnitz.mi.barcd.image.GaussianFilter;
import de.tu_chemnitz.mi.barcd.image.GlobalBinarizer;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.MeanValueThresholdSelector;

public class HighFrequenceRegionFinder {
    public static interface RegionFilter {
        public boolean filter(Region region);
    }

    private ConvolutionOperator sobelx;
    private ConvolutionOperator sobely;
    private GaussianFilter gaussian;
    private DilationOperator dilation;
    
    public HighFrequenceRegionFinder() {
        /*
        this.sobelx = new ConvolutionOperator(3, 3, new double[] {
            -3,  0,  3,
            -10,  0,  10,
            -3,  0,  3
        });
        
        this.sobely = new ConvolutionOperator(3, 3, new double[] {
            -3, -10, -3,
             0,  0,  0,
             3,  10,  3
        });
        */
        
        /**/
        this.sobelx = new ConvolutionOperator(3, 1, new double[] {
            -1,  0,  1
        });
        
        this.sobely = new ConvolutionOperator(1, 3, new double[] {
            -1,
             0,
             1
        });
        /**/

        this.dilation = new DilationOperator(3, 3);
        
        this.gaussian = new GaussianFilter(3, 10);
    }
    
    public Region[] detect(LuminanceImage input, RegionFilter filter) {
        int width = input.width();
        int height = input.height();
        
        LuminanceImage gx = this.sobelx.apply(input);
        LuminanceImage gy = this.sobely.apply(input);
        
        LuminanceImage gxy = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                gxy.setValueAt(x, y, gx.valueAt(x, y) + gy.valueAt(x, y));
            }
        }
        
        LuminanceImage dxy = this.gaussian.apply(this.dilation.apply(gxy));
        
        Binarizer bin = new GlobalBinarizer(new MeanValueThresholdSelector());
        
        LuminanceImage bxy = bin.apply(dxy);
        
        ConnectedComponentLabeler cc = new ConnectedComponentLabeler();
        int[][] labels = cc.process(bxy);
        
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
            Region region = new Region(coords);
            if (filter == null || filter.filter(region)) {
                filteredRegions.add(region);
            }
        }
        
        return filteredRegions.toArray(new Region[filteredRegions.size()]);
    }
}
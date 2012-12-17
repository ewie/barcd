package de.tu_chemnitz.mi.barcd;

import java.util.LinkedList;

import de.tu_chemnitz.mi.barcd.image.Binarizer;
import de.tu_chemnitz.mi.barcd.image.ConvolutionOperator;
import de.tu_chemnitz.mi.barcd.image.DilationOperator;
import de.tu_chemnitz.mi.barcd.image.GlobalBinarizer;
import de.tu_chemnitz.mi.barcd.image.LuminanceImage;
import de.tu_chemnitz.mi.barcd.image.MeanValueThresholdSelector;

public class HighFrequenceRegionFinder {
    public static interface RegionFilter {
        public boolean filter(RectangularRegion region);
    }
    
    private ConvolutionOperator sobelx;
    private ConvolutionOperator sobely;
    //private GaussianFilter gaussian;
    private DilationOperator dilation;
    
    public HighFrequenceRegionFinder() {
        /*
        this.sobelx = new GenericKernelOperator(3, 3, new double[] {
            -1,  0,  1,
            -2,  0,  2,
            -1,  0,  1
        });
        
        this.sobely = new GenericKernelOperator(3, 3, new double[] {
            -1, -2, -1,
             0,  0,  0,
             1,  2,  1
        });
        */
        
        this.sobelx = new ConvolutionOperator(3, 1, new double[] {
            -1,  0,  1
        });
        
        this.sobely = new ConvolutionOperator(1, 3, new double[] {
            -1,
             0,
             1
        });

        this.dilation = new DilationOperator(3, 3);
        
        //this.gaussian = new GaussianFilter(7, 10);
    }
    
    public RectangularRegion[] detect(LuminanceImage input, RegionFilter filter) {
        long t = System.currentTimeMillis();
        
        LuminanceImage gx = this.sobelx.apply(input);
        LuminanceImage gy = this.sobely.apply(input);

        int width = input.getWidth();
        int height = input.getHeight();

        LuminanceImage dxy = new LuminanceImage(width, height);
        LuminanceImage dyx = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                dxy.setValueAt(x, y,
                    Math.abs(gx.getValueAt(x, y)) - Math.abs(gy.getValueAt(x, y)));
                dyx.setValueAt(x, y,
                    Math.abs(gy.getValueAt(x, y)) - Math.abs(gx.getValueAt(x, y)));
            }
        }
        
        //ShowImages.showWindow(gx.toBufferedImage(), "gx");

        LuminanceImage dxy_g = dxy; //this.gaussian.apply(dxy);
        LuminanceImage dyx_g = dyx; //this.gaussian.apply(dyx);

        LuminanceImage dxy_d = this.dilation.apply(dxy_g);
        LuminanceImage dyx_d = this.dilation.apply(dyx_g);

        Binarizer bin = new GlobalBinarizer(new MeanValueThresholdSelector());
        
        LuminanceImage dxy_b = bin.apply(dxy_d);
        LuminanceImage dyx_b = bin.apply(dyx_d);

        LuminanceImage b = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                b.setValueAt(x, y, dxy_b.getValueAt(x, y) | dyx_b.getValueAt(x, y));
            }
        }

        /*
        ShowImages.showWindow(dxy_b.toBufferedImage(), "xy");
        ShowImages.showWindow(dyx_b.toBufferedImage(), "yx");
        ShowImages.showWindow(b.toBufferedImage(), "b");
        */
        
        ConnectedComponentsFinder re = new ConnectedComponentsFinder();
        RectangularRegion[] regionsx = re.process(dxy_b);
        RectangularRegion[] regionsy = re.process(dyx_b);
        RectangularRegion[] regionsxy = re.process(b);
        
        LinkedList<RectangularRegion> filteredRegions = new LinkedList<RectangularRegion>();

        for (int i = 0; i < regionsx.length; ++i) {
            if (filter.filter(regionsx[i])) {
                filteredRegions.add(regionsx[i]);
            }
        }
        
        for (int i = 0; i < regionsy.length; ++i) {
            if (filter.filter(regionsy[i])) {
                filteredRegions.add(regionsy[i]);
            }
        }
        
        for (int i = 0; i < regionsxy.length; ++i) {
            if (filter.filter(regionsxy[i])) {
                filteredRegions.add(regionsxy[i]);
            }
        }
        
        LinkedList<RectangularRegion> combinedRegions = new LinkedList<RectangularRegion>();
        
        while (!filteredRegions.isEmpty()) {
            RectangularRegion r = filteredRegions.removeFirst();
            RectangularRegion q = null;
            
            for (RectangularRegion rr : filteredRegions) {
                if (r.intersects(rr) || rr.intersects(r)) {
                    q = rr;
                    break;
                }
            }
            
            if (q == null) {
                combinedRegions.add(r);
            } else {
                filteredRegions.remove(q);
                filteredRegions.add(r.combine(q));
            }
        }
        
        System.out.println(System.currentTimeMillis() - t);
        
        return combinedRegions.toArray(new RectangularRegion[combinedRegions.size()]);
    }
}
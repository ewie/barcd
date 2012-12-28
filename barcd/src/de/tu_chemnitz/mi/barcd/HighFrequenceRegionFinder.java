package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import com.xuggle.xuggler.demos.VideoImage;

import boofcv.gui.image.ShowImages;

import de.tu_chemnitz.mi.barcd.geometry.ConnectedComponentsFinder;
import de.tu_chemnitz.mi.barcd.geometry.Region;
import de.tu_chemnitz.mi.barcd.image.Binarizer;
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
        
        this.gaussian = new GaussianFilter(7, 10);
    }
    
    public Region[] detect(LuminanceImage input, RegionFilter filter, List<BufferedImage> images) {
        int width = input.width();
        int height = input.height();
        
        LuminanceImage gx = this.sobelx.apply(input);
        LuminanceImage gy = this.sobely.apply(input);

        LuminanceImage dxy = new LuminanceImage(width, height);
        LuminanceImage dyx = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int vx = Math.abs(gx.valueAt(x, y));
                int vy = Math.abs(gy.valueAt(x, y));
                dxy.setValueAt(x, y, vx - vy);
                dyx.setValueAt(x, y, vy - vx);
            }
        }
        
        LuminanceImage dxy_g = dxy; //this.gaussian.apply(dxy);
        LuminanceImage dyx_g = dyx; //this.gaussian.apply(dyx);

        LuminanceImage dxy_d = this.dilation.apply(dxy_g);
        LuminanceImage dyx_d = this.dilation.apply(dyx_g);

        Binarizer bin = new GlobalBinarizer(new MeanValueThresholdSelector());
        
        LuminanceImage dxy_b = bin.apply(dxy_d);
        LuminanceImage dyx_b = bin.apply(dyx_d);

        /*
        LuminanceImage b = new LuminanceImage(width, height);
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                b.setValueAt(x, y, dxy_b.valueAt(x, y) | dyx_b.valueAt(x, y));
            }
        }
        */

        /*
        ShowImages.showWindow(dxy_b.toBufferedImage(), "xy");
        ShowImages.showWindow(dyx_b.toBufferedImage(), "yx");
        ShowImages.showWindow(b.toBufferedImage(), "b");
        */
        BufferedImage rc1 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage rc2 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        ConnectedComponentsFinder re = new ConnectedComponentsFinder();
        Region[] regionsx = re.process(dxy_b, rc1);
        Region[] regionsy = re.process(dyx_b, rc2);
        /*
        Region[] regionsxy = re.process(b);
        */
        
        LinkedList<Region> filteredRegions = new LinkedList<Region>();

        for (int i = 0; i < regionsx.length; ++i) {
            if (filter != null && filter.filter(regionsx[i])) {
                filteredRegions.add(regionsx[i]);
            }
        }
        
        for (int i = 0; i < regionsy.length; ++i) {
            if (filter != null && filter.filter(regionsy[i])) {
                filteredRegions.add(regionsy[i]);
            }
        }

        /*
        for (int i = 0; i < regionsxy.length; ++i) {
            if (filter != null && filter.filter(regionsxy[i])) {
                filteredRegions.add(regionsxy[i]);
            }
        }
        */
        
        /*
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
        */

        images.add(gx.toBufferedImage());
        images.add(gy.toBufferedImage());
        images.add(dxy.toBufferedImage());
        images.add(dyx.toBufferedImage());
        images.add(dxy_d.toBufferedImage());
        images.add(dyx_d.toBufferedImage());
        images.add(dxy_b.toBufferedImage());
        images.add(dyx_b.toBufferedImage());
        images.add(rc1);
        images.add(rc2);
        
        //return combinedRegions.toArray(new RectangularRegion[combinedRegions.size()]);
        return filteredRegions.toArray(new Region[filteredRegions.size()]);
    }
}
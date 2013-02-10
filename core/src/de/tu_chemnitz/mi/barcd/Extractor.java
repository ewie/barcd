package de.tu_chemnitz.mi.barcd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.util.ImageDisplay;
import de.tu_chemnitz.mi.barcd.util.RegionHash;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Extractor {
    private RegionExtractor hfr = new RegionExtractor();
    
    private RegionFilter regionFilter = new RegionFilter() {
        @Override
        public boolean select(Region region) {
            double cov = region.getCoverage();
            if (cov < 0.5) {
                return false;
            }
            OrientedRectangle rect = region.getOrientedRectangle();
            if (rect.getWidth() < 20 || rect.getHeight() < 20) {
                return false;
            }
            double dis = region.getDiscrepancy();
            if (dis < 0.85) {
                return false;
            }
            return true;
        }
    };

    private ImageDisplay display = new ImageDisplay();
    
    private Job job;

    private ImageProvider provider;
    
    private RegionHash rhash = new RegionHash(10, 400);
    
    private Decoder decoder = new ZXingBarcodeDecoder();
    
    private int epoch = 0;

    public Extractor(Job job)
        throws ImageProviderException
    {
        this.job = job;
        this.provider = job.getImageProvider();
    }
    
    public Job getJob() {
        return job;
    }
    
    public void setRegionFilter(RegionFilter filter) {
        this.regionFilter = filter;
    }
    
    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }
    
    public boolean hasMoreImages() {
        return provider.hasMore();
    }
    
    public void processNextImage()
        throws ImageProviderException
    {
        BufferedImage image = consumeNextImage();
        BufferedImage lum = createGrayscale(image);
        Region[] regions = hfr.detect(lum);
        
        // XXX
        displayRegions(image, regions);
        
        Frame frame = job.createFrame();
        
        for (Region r : regions) {
            if (!regionFilter.select(r)) continue;
            frame.addRegion(r);
            rhash.insert(r, epoch);
        }
        
        // TODO improve image regions
        
        Barcode[] barcodes = decoder.decodeMultiple(image);
        
        if (barcodes != null) {
            System.out.printf("found %d barcodes\n", barcodes.length);
            for (Barcode barcode : barcodes) {
                Point p = barcode.getAnchorPoints()[0];
                Region r = rhash.find(p, epoch);
                if (r == null) {
                    // TODO handle decoded barcodes not captured by any region
                } else {
                    r.setBarcode(barcode);
                }
            }
        }
        
        epoch += 1;
    }
    
    private void displayRegions(BufferedImage image, Region[] regions) {
        // Copy the image.
        BufferedImage im = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = im.createGraphics();
        g.drawImage(image, 0, 0, null);
        
        for (Region r : regions) {
            if (!regionFilter.select(r)) continue;
            
            Polygon polygon = new Polygon();
            for (Point p : r.getConvexPolygon().getPoints()) {
                polygon.addPoint((int) p.getX(), (int) p.getY());
            }
            // TODO let Region assure coverage is in 0..1
            float cov = (float) Math.max(0, Math.min(r.getCoverage(), 1));
            g.setColor(new Color(1f, 0f, 0f, cov));
            g.fillPolygon(polygon);
        }
        
        g.dispose();
        display.setImage(im);
    }
    
    private BufferedImage consumeNextImage()
        throws ImageProviderException
    {
        BufferedImage image = provider.getNext();
        // TODO make size configurable
        if (image.getWidth() > 1000) {
            image = scaleImage(image, 1000, (image.getHeight() * 1000) / image.getWidth());
        }
        return image;
    }
    
    private BufferedImage createGrayscale(BufferedImage in) {
        if (in.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            return in;
        }
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = out.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
        return out;
    }
    
    private BufferedImage scaleImage(BufferedImage in, int width, int height) {
        if (in.getWidth() == width && in.getHeight() == height) {
            return in;
        }
        BufferedImage out = new BufferedImage(width, height, in.getType());
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(in, 0, 0, width, height, null);
        g.dispose();
        return out;
    }
}

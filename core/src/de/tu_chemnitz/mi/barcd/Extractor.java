package de.tu_chemnitz.mi.barcd;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.util.RegionHash;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Extractor {
    public static interface FrameHandler {
        public void handleFrame(Frame frame);
    }
    
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

    private Job job;

    private ImageProvider provider;
    
    private RegionHash rhash = new RegionHash(10, 400);
    
    private Decoder decoder = new ZXingBarcodeDecoder();
    
    private int epoch = 0;

    private FrameHandler frameHandler;

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
    
    public void setFrameHandler(FrameHandler frameHandler) {
        this.frameHandler = frameHandler;
    }
    
    public boolean hasMoreImages() {
        return provider.hasMore();
    }
    
    public void processNextImage()
        throws ImageProviderException
    {
        BufferedImage image = provider.consume();
        BufferedImage lum = createGrayscale(image);
        Region[] regions = hfr.detect(lum);
        
        Frame frame = job.createFrame();
        
        frame.setImage(image);
        
        for (Region r : regions) {
            if (!regionFilter.select(r)) continue;
            frame.addRegion(r);
            rhash.insert(r, epoch);
        }
        
        // TODO improve image regions
        
        Barcode[] barcodes = decoder.decodeMultiple(image);
        
        if (barcodes != null) {
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
        
        reportFrame(frame);
    }
    
    private void reportFrame(Frame frame) {
        if (frameHandler != null) {
            frameHandler.handleFrame(frame);
        }
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
}

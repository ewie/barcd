package de.tu_chemnitz.mi.barcd;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.util.RegionHashTable;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Extractor {
    public static interface FrameHandler {
        public void handleFrame(Frame frame, BufferedImage image);
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

    private RegionHashTable rhash = new RegionHashTable(10, 400);

    private Decoder decoder = new ZXingBarcodeDecoder();

    private FrameHandler frameHandler;

    public Extractor(Job job)
        throws ImageProviderException
    {
        this.job = job;
        provider = job.getImageProvider();
    }

    public Job getJob() {
        return job;
    }

    public void setRegionFilter(RegionFilter filter) {
        regionFilter = filter;
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

    /**
     * @throws ImageProviderException if the next image is null
     */
    public void processNextImage()
        throws ImageProviderException
    {
        BufferedImage image = provider.consume();

        if (image == null) {
            throw new ImageProviderException("next image is null");
        }

        BufferedImage lum = createGrayscale(image);
        Region[] regions = hfr.detect(lum);

        Frame frame = job.createFrame();

        for (Region r : regions) {
            if (!regionFilter.select(r)) continue;
            frame.addRegion(r);
            rhash.insert(r);
        }

        // TODO improve image regions

        Barcode[] barcodes = decoder.decodeMultiple(image);

        if (barcodes != null) {
            for (Barcode barcode : barcodes) {
                Point p = barcode.getAnchorPoints()[0];
                Region r = rhash.find(p);
                if (r == null) {
                    frame.addRegionlessBarcode(barcode);
                } else {
                    r.setBarcode(barcode);
                }
            }
        }

        rhash.clear();

        reportFrame(frame, image);
    }

    private void reportFrame(Frame frame, BufferedImage image) {
        if (frameHandler != null) {
            frameHandler.handleFrame(frame, image);
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

package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;

import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.util.RegionHashTable;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Extractor {
    public static interface FrameHandler {
        public void handleFrame(Frame frame, BufferedImage image);
    }

    private RegionExtractor regionExtractor = new DefaultRegionExtractor();

    private RegionSelector regionSelector = new DefaultRegionSelector();

    private Job job;

    private ImageProvider imageProvider;

    private RegionHashTable regionHashTable = new RegionHashTable(10, 400);

    private Decoder decoder = new ZXingBarcodeDecoder();

    private FrameHandler frameHandler;

    private Grayscaler grayscaler;

    /**
     * @param job the job to process
     *
     * @throws ImageProviderException if the image provider could no be created
     */
    public Extractor(Job job)
        throws ImageProviderException
    {
        this.job = job;
        imageProvider = job.getImageProvider();
    }

    /**
     * @return the job processed by this extractor
     */
    public Job getJob() {
        return job;
    }

    /**
     * Set the grayscaler.
     *
     * @param grayscaler the grayscaler
     */
    public void setGrayscaler(Grayscaler grayscaler) {
        this.grayscaler = grayscaler;
    }

    /**
     * Set the region extractor.
     *
     * @param regionExtractor
     */
    public void setRegionExtractor(RegionExtractor regionExtractor) {
        this.regionExtractor = regionExtractor;
    }

    /**
     * Set a region filter.
     *
     * @param regionSelector
     */
    public void setRegionSelector(RegionSelector regionSelector) {
        this.regionSelector = regionSelector;
    }

    /**
     * Set the barcode decoder.
     *
     * @param decoder
     */
    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    /**
     * Register a frame handler.
     *
     * @param frameHandler
     */
    public void setFrameHandler(FrameHandler frameHandler) {
        this.frameHandler = frameHandler;
    }

    /**
     * @return true if the underlying image provider has more images
     */
    public boolean hasMoreImages() {
        return imageProvider.hasMore();
    }

    /**
     * Process the next image returned by the image provider.
     *
     * @throws ImageProviderException if the next image is null
     */
    public void processNextImage()
        throws ExtractorException
    {
        BufferedImage image;
        try {
            image = imageProvider.consume();
        } catch (ImageProviderException ex) {
            throw new ExtractorException(ex);
        }

        if (image == null) {
            throw new ExtractorException("next image is null");
        }

        BufferedImage lum = grayscaler.convertToGrayscale(image);
        Region[] regions = regionExtractor.extractRegions(lum);

        Frame frame = job.createFrame();

        for (Region r : regions) {
            if (regionSelector.selectRegion(r)) {
                frame.addRegion(r);
                regionHashTable.insert(r);
            }
        }

        // TODO improve image regions

        Barcode[] barcodes = decoder.decodeMultiple(image);

        if (barcodes != null) {
            for (Barcode barcode : barcodes) {
                Region r = null;

                // Try each anchor point until we find region containing it.
                Point[] pp = barcode.getAnchorPoints();
                for (Point p : pp) {
                    r = regionHashTable.find(p);
                    if (r != null) {
                        break;
                    }
                }

                if (r == null) {
                    frame.addRegionlessBarcode(barcode);
                } else {
                    r.setBarcode(barcode);
                }
            }
        }

        regionHashTable.clear();

        reportFrame(frame, image);
    }

    /**
     * Report a frame along with an image if the there's a registered frame
     * handler.
     *
     * @param frame the frame to report
     * @param image the image to pass along with the frame
     */
    private void reportFrame(Frame frame, BufferedImage image) {
        if (frameHandler != null) {
            frameHandler.handleFrame(frame, image);
        }
    }
}

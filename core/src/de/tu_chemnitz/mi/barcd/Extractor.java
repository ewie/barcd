package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import de.tu_chemnitz.mi.barcd.geometry.AxisAlignedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.geometry.Vector;
import de.tu_chemnitz.mi.barcd.util.RegionHashTable;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Extractor {
    private final Job job;

    private final ImageProvider imageProvider;

    private ExtractionHandler extractionHandler;

    private RegionExtractor regionExtractor = new DefaultRegionExtractor();

    private Filter<Region> regionFilter = new DefaultRegionFilter();

    private final RegionHashTable regionHashTable = new RegionHashTable(10, 400);

    private BarcodeReader barcodeReader = new DefaultBarcodeReader();

    private Grayscaler grayscaler = new DefaultGrayscaler();

    private ImageEnhancer enhancer = new DefaultImageEnhancer();

    /**
     * @param job the job to process
     *
     * @throws ExtractorException if the image provider could not be created
     */
    public Extractor(Job job)
        throws ExtractorException
    {
        this.job = job;
        try {
            imageProvider = job.createImageProvider();
        } catch (ImageProviderException ex) {
            throw new ExtractorException("could not create image provider", ex);
        }
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
     * Set the region filter.
     *
     * @param regionFilter
     */
    public void setRegionFilter(Filter<Region> regionFilter) {
        this.regionFilter = regionFilter;
    }

    /**
     * Set the barcode reader.
     *
     * @param barcodeReader
     */
    public void setBarcodeReader(BarcodeReader barcodeReader) {
        this.barcodeReader = barcodeReader;
    }

    /**
     * Set the extraction handler.
     *
     * @param extractionHandler
     */
    public void setExtractionHandler(ExtractionHandler extractionHandler) {
        this.extractionHandler = extractionHandler;
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
     * @throws ExtractorException
     *   if the next image could not be consumed or is null
     */
    public void processNextImage()
        throws ExtractorException
    {
        BufferedImage image = consumeNextImage();
        Region[] regions = extractRegions(image);
        Extraction extraction = createExtraction(regions, image);
        reportExtraction(extraction, image);
    }

    /**
     * Consume the next image from the image provider.
     *
     * @return the next image
     *
     * @throws ExtractorException if the image provider can not provide the next
     *   image
     */
    private BufferedImage consumeNextImage()
        throws ExtractorException
    {
        BufferedImage image;

        try {
            image = imageProvider.consume();
        } catch (ImageProviderException ex) {
            throw new ExtractorException("could not consume next image", ex);
        }

        if (image == null) {
            throw new ExtractorException("next image is null");
        }

        return image;
    }

    /**
     * Extract regions from the given source image.
     *
     * @param image the source image
     *
     * @return all extracted regions
     */
    private Region[] extractRegions(BufferedImage image) {
        BufferedImage lum = grayscaler.convertToGrayscale(image);
        return regionExtractor.extractRegions(lum);
    }

    /**
     * Create an extraction for the given regions and source image.
     *
     * @param regions the regions extracted from the source image
     * @param image the source image
     *
     * @return a new extraction
     */
    private Extraction createExtraction(Region[] regions, BufferedImage image) {
        Extraction extraction = job.createExtraction();

        Iterable<Region> selection = filterRegions(regions);

        for (Region region : selection) {
            regionHashTable.insert(region);
            extraction.addRegion(region);
            processRegionBarcode(region, image);
        }

        processAllBarcodes(extraction, image);
        regionHashTable.clear();

        return extraction;
    }

    /**
     * Filter the given regions. Uses the region filter if it's set, otherwise
     * no filter is applied and all regions will be returned.
     *
     * @param regions the regions to filter
     *
     * @return the filtered regions
     */
    private Iterable<Region> filterRegions(Region[] regions) {
        Iterable<Region> iterable = Arrays.asList(regions);
        if (regionFilter != null) {
            iterable = regionFilter.filter(iterable);
        }
        return iterable;
    }

    /**
     * Process the eventual barcode within the given region.
     *
     * @param region the region may containing a barcode
     * @param image the image from which the region has been extracted
     */
    private void processRegionBarcode(Region region, BufferedImage image) {
        BufferedImage sub = createRegionImage(region, image);

        BufferedImage enhanced;
        try {
            enhanced = enhancer.enhanceImage(sub);
        } catch (ImageEnhancerException ex) {
            // TODO pass original image to ZXing
            return;
        }

        Barcode barcode = barcodeReader.readBarcode(enhanced);

        if (barcode != null) {
            region.setBarcode(barcode);
        }
    }

    /**
     * Process all barcodes found within the entire source image. This will add
     * any barcodes not covered by a region as region-less barcode to the given
     * extraction.
     *
     * @param extraction the extraction
     * @param image the source image
     */
    private void processAllBarcodes(Extraction extraction, BufferedImage image) {
        Barcode[] barcodes = barcodeReader.readMultipleBarcodes(image);

        if (barcodes != null) {
            for (Barcode barcode : barcodes) {
                Region region = null;

                // Try each anchor point until we find a region containing it.
                Point[] points = barcode.getAnchorPoints();
                for (Point p : points) {
                    region = regionHashTable.find(p);
                    if (region != null) {
                        break;
                    }
                }

                if (region == null) {
                    extraction.addRegionlessBarcode(barcode);
                } else {
                    region.setBarcode(barcode);
                }
            }
        }
    }

    /**
     * Get the image within the region's axis aligned bounding rectangle.
     *
     * @param region the region
     * @param image the frame image from which the region originates
     *
     * @return the region's sub image
     */
    private BufferedImage createRegionImage(Region region, BufferedImage image) {
        OrientedRectangle rect = createRegionRectangleWithQuietZone(region);

        AxisAlignedRectangle aar = AxisAlignedRectangle.createFromPolygon(rect);
        Point min = aar.getMin();
        Point max = aar.getMax();

        // Ensure the axis aligned rectangle lies within the frame image.
        int xmin = Math.max((int) min.getX(), 0);
        int ymin = Math.max((int) min.getY(), 0);
        int xmax = Math.min((int) max.getX(), image.getWidth() - 1);
        int ymax = Math.min((int) max.getY(), image.getHeight() - 1);

        int width = xmax - xmin;
        int height = ymax - ymin;

        return image.getSubimage(xmin, ymin, width, height);
    }

    /**
     * Create an expanded version of a region's oriented rectangle to provide
     * a quiet zone.
     *
     * @param region the region
     *
     * @return the region's expanded oriented rectangle
     */
    private OrientedRectangle createRegionRectangleWithQuietZone(Region region) {
        OrientedRectangle rect = region.getOrientedRectangle();
        Point[] points = rect.getVertices();

        // Let the size of the quiet zone depend on the rectangle's width.
        double quietZoneWidth = rect.getWidth() / 10;

        // The displacement vectors along the rectangle's width and height used
        // to add a quiet zone.
        Vector dw = new Vector(points[1], points[0]).applyLength(quietZoneWidth);
        Vector dh = new Vector(points[3], points[0]).applyLength(quietZoneWidth);

        // Move the rectangle's origin outwards.
        Point origin = points[0].translate(dw).translate(dh);

        // The vectors along the rectangle's width and height.
        Vector vw = new Vector(points[0], points[1]);
        Vector vh = new Vector(points[0], points[3]);

        // Add the quiet zone's width to the rectangle's width and height.
        vw = vw.applyLength(vw.getLength() + dw.getLength() * 2);
        vh = vh.applyLength(vh.getLength() + dw.getLength() * 2);

        return new OrientedRectangle(origin, vw, vh);
    }

    /**
     * Report an extraction along with the source image if an extraction handler
     * is set.
     *
     * @param extraction the extraction to report
     * @param image the image to pass along with the frame
     */
    private void reportExtraction(Extraction extraction, BufferedImage image) {
        if (extractionHandler != null) {
            extractionHandler.handleExtraction(extraction, image);
        }
    }
}

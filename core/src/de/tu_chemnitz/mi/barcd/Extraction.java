package de.tu_chemnitz.mi.barcd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Provides all regions and barcodes extracted from a certain frame.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Extraction {
    private final int frameNumber;

    private final Collection<Region> regions;

    private final Collection<Barcode> regionlessBarcodes;

    /**
     * Create an extraction with frame number, regions and region-less barcodes.
     *
     * @param frameNumber the frame number
     * @param regions the regions within this extraction
     * @param regionlessBarcodes the barcodes within this extraction not
     *   captured by regions
     */
    public Extraction(int frameNumber, Collection<Region> regions, Collection<Barcode> regionlessBarcodes) {
        this.frameNumber = frameNumber;
        if (regions == null) {
            this.regions = new LinkedList<Region>();
        } else {
            this.regions = regions;
        }
        if (regionlessBarcodes == null) {
            this.regionlessBarcodes = new LinkedList<Barcode>();
        } else {
            this.regionlessBarcodes = regionlessBarcodes;
        }
    }

    /**
     * Create an extraction with a frame number and no regions or region-less
     * barcodes.
     *
     * @param frameNumber the frame number
     */
    public Extraction(int frameNumber) {
        this(frameNumber, null, null);
    }

    /**
     * Get the frame number of this extraction's respective frame.
     *
     * @return the frame number
     */
    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * Get all regions of this extraction.
     *
     * @return the regions
     */
    public Collection<Region> getRegions() {
        return regions;
    }

    /**
     * Add a region.
     *
     * @param region the region to add
     */
    public void addRegion(Region region) {
        regions.add(region);
    }

    /**
     * Add a region-less barcode.
     *
     * @param barcode the barcode to add
     */
    public void addRegionlessBarcode(Barcode barcode) {
        regionlessBarcodes.add(barcode);
    }

    /**
     * @return the region-less barcodes
     */
    public Collection<Barcode> getRegionlessBarcodes() {
        return regionlessBarcodes;
    }

    /**
     * Get all barcodes (regions-less and associated with a region).
     *
     * @return an unmodifiable collection of all barcodes
     */
    public Collection<Barcode> getBarcodes() {
        HashSet<Barcode> barcodes = new HashSet<Barcode>();
        return Collections.unmodifiableCollection(getBarcodes(barcodes));
    }

    /**
     * Get all barcodes (region-less and associated with a region) and put them
     * into the given collection.
     *
     * @param barcodes the collection to receive all barcodes
     *
     * @return the given collection
     */
    public Collection<Barcode> getBarcodes(Collection<Barcode> barcodes) {
        getRegionBarcodes(barcodes);
        barcodes.addAll(regionlessBarcodes);
        return barcodes;
    }

    /**
     * Get all region barcodes.
     *
     * @return an unmodifiable collection of all region barcodes
     */
    public Collection<Barcode> getRegionBarcodes() {
        ArrayList<Barcode> barcodes = new ArrayList<Barcode>(regions.size());
        return Collections.unmodifiableCollection(getRegionBarcodes(barcodes));
    }

    /**
     * Get all region barcodes and put them into the given collection.
     *
     * @param barcodes the collection to receive the barcodes
     *
     * @return the given collection.
     */
    public Collection<Barcode> getRegionBarcodes(Collection<Barcode> barcodes) {
        for (Region r : regions) {
            Barcode barcode = r.getBarcode();
            if (barcode != null) {
                barcodes.add(r.getBarcode());
            }
        }
        return barcodes;
    }
}

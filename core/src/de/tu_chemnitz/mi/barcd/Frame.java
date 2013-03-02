package de.tu_chemnitz.mi.barcd;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Frame {
    private int number;
    
    private Collection<Region> regions;

    private BufferedImage image;
    
    private Collection<Barcode> regionlessBarcodes;

    /**
     * @param number the frame number
     * @param regions the regions within this frame
     * @param regionlessBarcodes the barcodes within this frame not captured by regions
     */
    public Frame(int number, Collection<Region> regions, Collection<Barcode> regionlessBarcodes) {
        this.number = number;
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
    
    public Frame(int number) {
        this(number, null, null);
    }

    public int getNumber() {
        return number;
    }
    
    public Collection<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void addRegionlessBarcode(Barcode barcode) {
        regionlessBarcodes.add(barcode);
    }
    
    public Collection<Barcode> getRegionlessBarcodes() {
        return regionlessBarcodes;
    }
    
    public Collection<Barcode> getBarcodes() {
        HashSet<Barcode> barcodes = new HashSet<Barcode>();
        return Collections.unmodifiableCollection(getBarcodes(barcodes));
    }
    
    public Collection<Barcode> getBarcodes(Collection<Barcode> barcodes) {
        getRegionBarcodes(barcodes);
        barcodes.addAll(regionlessBarcodes);
        return barcodes;
    }
    
    public Collection<Barcode> getRegionBarcodes() {
        ArrayList<Barcode> barcodes = new ArrayList<Barcode>(regions.size());
        return Collections.unmodifiableCollection(getRegionBarcodes(barcodes));
    }
    
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

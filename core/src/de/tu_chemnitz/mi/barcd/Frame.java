package de.tu_chemnitz.mi.barcd;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Frame {
    private int number;
    
    private Collection<Region> regions;
    
    public Frame(int number, Collection<Region> regions) {
        this.number = number;
        if (regions == null) {
            this.regions = new LinkedList<Region>();
        } else {
            this.regions = regions;
        }
    }
    
    public Frame(int number) {
        this(number, null);
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
}

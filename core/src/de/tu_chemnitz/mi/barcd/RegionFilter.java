package de.tu_chemnitz.mi.barcd;

/**
 * Provides a condition to select regions.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface RegionFilter {
    /**
     * Decides if a region should be selected or rejected.
     *
     * @param region the region to filter
     *
     * @return true to select the region, false to reject it
     */
    public boolean select(Region region);
}

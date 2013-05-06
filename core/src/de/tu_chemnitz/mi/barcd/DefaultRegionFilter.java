/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DefaultRegionFilter implements Filter<Region> {
    @Override
    public Iterable<Region> filter(Iterable<Region> regions) {
        return new FilteredIterable<Region>(regions, new RegionPredicate());
    }

    /**
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    private static class RegionPredicate implements Predicate<Region> {
        @Override
        public boolean isTrue(Region region) {
            if (region.getCoverage() < 0.5) {
                return false;
            }
            OrientedRectangle rect = region.getOrientedRectangle();
            if (rect.getWidth() < 20 || rect.getHeight() < 20) {
                return false;
            }
            return true;
        }
    }
}

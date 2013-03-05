package de.tu_chemnitz.mi.barcd;

import de.tu_chemnitz.mi.barcd.geometry.OrientedRectangle;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class DefaultRegionSelector implements RegionSelector {
    @Override
    public boolean selectRegion(Region region) {
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
}

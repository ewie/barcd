package de.tu_chemnitz.mi.barcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tu_chemnitz.mi.barcd.image.LuminanceImage;

/**
 * Extract regions from a gray-scale image. A region is a set of of neighbouring
 * pixels with the same value.
 * <p>
 * Uses the algorithm described in:
 * http://www.cis.rit.edu/class/simg782.old/finding_connected_components.pdf
 * 
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
public class ConnectedComponentsFinder {
    /**
     * @param input
     * @return the extracted regions
     */
    public RectangularRegion[] process(LuminanceImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        
        int label = 0;
        
        // An array of labels assigned to pixels with the corresponding
        // coordinates.
        int[][] labels = new int[width][height];
        
        // Keep track of equivalent labels by mapping a label to an earlier
        // assigned label by mapping the later label to an earlier label if
        // both are to be considered equivalent.
        Map<Integer, Integer> eq = new HashMap<Integer, Integer>();
        
        if (input.getValueAt(0, 0) > 0) {
            labels[0][0] = ++label;
        }
        
        for (int x = 1; x < width; ++x) {
            int v = input.getValueAt(x, 0);
            if (v > 0) {
                if (v == input.getValueAt(x-1, 0)) {
                    labels[x][0] = labels[x-1][0];
                } else {
                    labels[x][0] = ++label;
                }
            }
        }
        
        for (int y = 1; y < height; ++y) {
            int v = input.getValueAt(0, y);
            if (v > 0) {
                if (v == input.getValueAt(0, y-1)) {
                    labels[0][y] = labels[0][y-1];
                } else {
                    labels[0][y] = ++label;
                }
            }
            for (int x = 1; x < width; ++x) {
                // The current pixel.
                int p = input.getValueAt(x, y);
                
                // The pixel left of the current pixel.
                int s = input.getValueAt(x-1, y);
                
                // The pixel above the current pixel.
                int t = input.getValueAt(x, y-1);
                
                if (p == s) {
                    if (p == t) {
                        if (labels[x-1][y] == labels[x][y-1]) {
                            labels[x][y] = labels[x][y-1];
                        } else {
                            int l1 = Math.min(labels[x-1][y], labels[x][y-1]);
                            int l2 = Math.max(labels[x-1][y], labels[x][y-1]);
                            labels[x][y] = l1;
                            eq.put(l2, l1);
                        }
                    } else {
                        labels[x][y] = labels[x-1][y];
                    }
                } else {
                    if (p == t) {
                        labels[x][y] = labels[x][y-1];
                    } else {
                        labels[x][y] = ++label;
                    }
                }
            }
        }
        
        Map<Integer, List<Coordinate>> regions = new HashMap<Integer, List<Coordinate>>();
        
        // Resolve equivalent regions by relabelling all pixels whose label is
        // marked as equivalent to some other label.
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // The labels of equivalent regions may form a tree (a label is
                // equivalent to a label which itself is equivalent to another
                // label, etc.). So we have to traverse said list up to its root
                // (the label not equivalent to any other label).
                int l = labels[x][y];
                while (eq.containsKey(l)) l = eq.get(l);
                
                labels[x][y] = l;
                
                // Skip the background region.
                if (input.getValueAt(x, y) == 0) continue;
                
                if (!regions.containsKey(labels[x][y])) {
                    regions.put(labels[x][y], new ArrayList<Coordinate>());
                }
                regions.get(labels[x][y]).add(new Coordinate(x, y));
            }
        }
        
        RectangularRegion[] finalRegions = new RectangularRegion[regions.size()];
        
        // Finally construct the region objects from each set of coordinates.
        int i = 0;
        for (int l : regions.keySet()) {
            finalRegions[i++] = RectangularRegion.fromCoordinates(regions.get(l));
        }
        
        return finalRegions;
    }
}
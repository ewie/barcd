package de.tu_chemnitz.mi.barcd.util;

import de.tu_chemnitz.mi.barcd.Region;
import de.tu_chemnitz.mi.barcd.geometry.AxisAlignedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;

/**
 * A hash table to store regions and retrieve a region containing a certain
 * point.
 *
 * The implementation is based on
 *   "Optimized Spatial Hashing for Collision Detection of Deformable Objects"
 *   Matthias Teschner, Bruno Heidelberger, Matthias Müller, Danat Pomeranets,
 *     Markus Gross
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RegionHashTable {
    /**
     * The actual hash table storing the head of each linked list of references.
     */
    private Reference[] table;

    /**
     * The hash table's epoch.
     */
    private int epoch = 0;

    private double resolutionX;
    private double resolutionY;

    /**
     * Create a region hash with an x- and y-resolution and a minimum hash table
     * size.
     *
     * @param resolutionX the resolution of the x-dimension
     * @param resolutionY the resolution of the y-dimension
     * @param minTableSize the minimum size of the hash table
     */
    public RegionHashTable(double resolutionX, double resolutionY, int minTableSize) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        minTableSize = computePrimeGreaterOrEqual(minTableSize);
        table = new Reference[minTableSize];
    }

    /**
     * Create a region hash with an equal x- and y-resolution and minimum hash
     * table size.
     *
     * @param resolution the resolution of the x- and y-dimension
     * @param minTableSize the minimum size of the hash table
     */
    public RegionHashTable(double resolution, int minTableSize) {
        this(resolution, resolution, minTableSize);
    }

    /**
     * Remove all currently inserted regions.
     */
    public void clear() {
        // Actually don't remove the regions (actually the references) but delay
        // this until the next insert. In this case we just have to increment
        // the hash table's epoch.
        epoch += 1;
    }

    /**
     * Insert a region.
     *
     * @param region the region to insert
     */
    public void insert(Region region) {
        AxisAlignedRectangle rect = region.getAxisAlignedRectangle();
        Point min = rect.getMin();
        Point max = rect.getMax();
        int[] dmin = discretize(min);
        int[] dmax = discretize(max);
        for (int x = dmin[0]; x <= dmax[0]; ++x) {
            for (int y = dmin[1]; y <= dmax[1]; ++y) {
                int h = computeIndex(x, y);
                Reference ref = new Reference(region, epoch);
                if (table[h] == null) {
                    table[h] = ref;
                } else {
                    // Discard all entries if the head reference's epoch is
                    // smaller than the current epoch.
                    if (table[h].epoch < epoch) {
                        table[h] = null;
                    }
                    // Make the new reference the head and the old head its
                    // successor.
                    ref.next = table[h];
                    table[h] = ref;
                }
            }
        }
    }

    /**
     * Find the region containing a given point.
     *
     * @param p the point used to find a region
     *
     * @return the region containing the given point, null if no such region
     *   could be found
     */
    public Region find(Point p) {
        int[] d = discretize(p);
        int h = computeIndex(d[0], d[1]);
        Reference ref = table[h];
        while (ref != null) {
            if (ref.epoch == epoch && ref.region.contains(p)) {
                return ref.region;
            }
            ref = ref.next;
        }
        return null;
    }

    /**
     * Compute the discrete coordinates based on the x- and y-resolution.
     *
     * @param p the point to discretize
     *
     * @return an array with two elements x and y
     */
    private int[] discretize(Point p) {
        return new int[] {
            (int) (p.getX() / resolutionX),
            (int) (p.getY() / resolutionY)
        };
    }

    private static final long P = 73856093;
    private static final long Q = 19349663;

    /**
     * Compute the hash table index for two integers.
     *
     * @param x
     * @param y
     *
     * @return the hash table index
     */
    private int computeIndex(int x, int y) {
        return (int) (computeHash(x, y) % table.length);
    }

    /**
     * Compute the hash key of two integers.
     *
     * @param x
     * @param y
     *
     * @return the hash key
     */
    private static long computeHash(int x, int y) {
        return (x * P) ^ (y * Q);
    }

    /**
     * Compute the prime number greater or equal a given integer.
     *
     * @param p the minimum integer
     *
     * @return the prime number greater or equal p
     */
    private static int computePrimeGreaterOrEqual(int p) {
        // Assure we use an odd integer.
        p += (p + 1) % 2;
        // Try every odd integer.
        while (!isPrime(p)) {
            p += 2;
        }
        return p;
    }

    /**
     * Check if an integer is prime.
     *
     * @param p the integer to check
     *
     * @return true if prime
     */
    private static boolean isPrime(int p) {
        if (p < 2) return false;
        if (p <= 3) return true;
        if (p % 2 == 0) return false;
        int n = (int) Math.sqrt(p);
        for (int i = 3; i < n; i += 2) {
            if (p % i == 0) return false;
        }
        return true;
    }

    /**
     * A reference to a region inserted into the hash table.
     *
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    private static class Reference {
        /**
         * The referenced region.
         */
        public Region region;

        /**
         * Pointer to the next reference (of a different region) forming a
         * linked list
         */
        public Reference next;

        /**
         * The hash table's epoch at the time the reference has been inserted.
         */
        public int epoch;

        /**
         * @param region the region to reference
         * @param epoch the epoch of the hash table
         */
        public Reference(Region region, int epoch) {
            this.region = region;
            this.epoch = epoch;
        }
    }
}

package de.tu_chemnitz.mi.barcd.util;

import de.tu_chemnitz.mi.barcd.Region;
import de.tu_chemnitz.mi.barcd.geometry.AxisAlignedRectangle;
import de.tu_chemnitz.mi.barcd.geometry.Point;

public class RegionHash {
    private static class Reference {
        public Region region;
        
        public Reference next;
        
        public int epoch;
        
        public Reference(Region region, int epoch) {
            this.region = region;
            this.epoch = epoch;
        }
    }
    
    private double resolutionX;
    private double resolutionY;
    
    private Reference[] table;
    
    public RegionHash(double resolutionX, double resolutionY, int size) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        size = getPrimeGreaterOrEqual(size);
        table = new Reference[size];
    }
    
    public RegionHash(double resolution, int size) {
        this(resolution, resolution, size);
    }
    
    public void insert(Region region, int epoch) {
        AxisAlignedRectangle rect = region.getAxisAlignedRectangle();
        Point min = rect.getMin();
        Point max = rect.getMax();
        int[] dmin = discretize(min);
        int[] dmax = discretize(max);
        for (int x = dmin[0]; x <= dmax[0]; ++x) {
            for (int y = dmin[1]; y <= dmax[1]; ++y) {
                int h = getIndex(x, y);
                Reference ref = new Reference(region, epoch);
                if (table[h] == null) {
                    table[h] = ref;
                } else {
                    if (table[h].epoch < epoch) {
                        table[h] = null;
                    }
                    ref.next = table[h];
                    table[h] = ref;
                }
            }
        }
    }
    
    public Region find(Point p, int epoch) {
        int[] d = discretize(p);
        int h = getIndex(d[0], d[1]);
        Reference ref = table[h];
        while (ref != null) {
            if (ref.epoch == epoch && ref.region.contains(p)) {
                return ref.region;
            }
            ref = ref.next;
        }
        return null;
    }
    
    private int[] discretize(Point p) {
        return new int[] {
            (int) (p.getX() / resolutionX),
            (int) (p.getY() / resolutionY)
        };
    }

    private static final long P = 73856093;
    private static final long Q = 19349663;
    private static final long R = 83492791;
    
    private int getIndex(int x, int y) {
        return (int) (computeHash(x, y) % table.length);
    }
    
    private static long computeHash(int x, int y) {
        return (x * P) ^ (y * Q);
    }
    
    private static int getPrimeGreaterOrEqual(int p) {
        while (!isPrime(p)) {
            p += 1;
        }
        return p;
    }
    
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
}

package de.tu_chemnitz.mi.barcd.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RectangularRegion {
    private int x;
    private int y;
    private int width;
    private int height;
    private double coverage;
    
    /**
     * @param coords the coordinates spanning the region
     */
    public static RectangularRegion fromCoordinates(Point[] coords) {
        return fromCoordinates(Arrays.asList(coords));
    }
    
    /**
     * @param coords the coordinates spanning the region
     */
    public static RectangularRegion fromCoordinates(List<Point> coords) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point coord : coords) {
            int x = coord.getX();
            int y = coord.getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        int width = maxX - minX;
        int height = maxY - minY;
        return new RectangularRegion(minX, minY, width, height, (double) coords.size() / (width * height));
    }
    
    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param coverage
     */
    public RectangularRegion(int x, int y, int width, int height, double coverage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.coverage = Math.max(0, Math.min(coverage, 1));
    }
    
    /**
     * Get the area described by axis-aligned bounding rectangle of this
     * regions.
     * 
     * @return the area in number of pixels
     */
    public int getArea() {
        return this.width * this.height;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getSize() {
        return 4;
    }

    public Point[] getCoordinates() {
        Point[] coords = new Point[4];
        coords[0] = new Point(this.x, this.y);
        coords[1] = new Point(this.x + width, this.y);
        coords[2] = new Point(this.x + width, this.y + height);
        coords[3] = new Point(this.x, this.y + height);
        return coords;
    }
    
    /**
     * The ratio between the region's area and the number of coordinates
     * spanning the region.
     * 
     * A value of 1 means the region is spanned by all coordinates contained
     * by the axis-aligned bounding rectangle.
     * 
     * The smaller the value the sparser the region, e.g. there are holes or
     * tails.
     * 
     * @return the ratio between 0 and 1
     */
    public double getCoverage() {
        return this.coverage;
    }
    
    public boolean contains(int x, int y) {
        return x >= this.x && x <= (this.x + this.width) && y >= this.y && y <= (this.y + this.height);
    }
    
    public boolean contains(Point p) {
        return contains(p.getX(), p.getY());
    }
    
    public boolean intersects(RectangularRegion other) {
        return contains(other.x,               other.y)
            || contains(other.x + other.width, other.y)
            || contains(other.x + other.width, other.y + other.height)
            || contains(other.x              , other.y + other.height);
    }
    
    public RectangularRegion getIntersection(RectangularRegion other) {
        RectangularRegion small = getArea() < other.getArea() ? this : other;
        RectangularRegion large = small == this ? other : this;
        
        int flag = 0;
        // 0x1 --- 0x2
        //  |       |
        // 0x4 --- 0x8
        flag |= large.contains(small.x,               small.y)                ? 0x1 : 0;
        flag |= large.contains(small.x + small.width, small.y)                ? 0x2 : 0;
        flag |= large.contains(small.x,               small.y + small.height) ? 0x4 : 0;
        flag |= large.contains(small.x + small.width, small.y + small.height) ? 0x8 : 0;
        
        int x, y, width, height;
        
        switch (flag) {
        case 0xf:
            // The intersection region is identical with the smaller region.
            // Use the maximum coverage of both regions.
            x = small.x;
            y = small.y;
            width = small.width;
            height = small.height;
            break;
            
        case 0x1: // 0001
            //return new Region(small.x, small.y, (large.x + large.width - small.x), (large.y + large.height - small.y), Math.max(large.coverage, small.coverage));
            x = small.x;
            y = small.y;
            width = large.x + large.width - small.x;
            height = large.y + large.height - small.y;
            break;
        case 0x2: // 0010
            //return new Region(large.x, small.y, (small.x - large.x), (large.y + large.height - small.y), Math.max(large.coverage, small.coverage));
            x = large.x;
            y = small.y;
            width = small.x + small.width - large.x;
            height = large.y + large.height - small.y;
            break;
        case 0x4: // 0100
            //return new Region(small.x, large.y, (large.x + large.width - small.x), (large.y - (small.y + small.height)), Math.max(large.coverage, small.coverage));
            x = small.x;
            y = large.y;
            width = large.x + large.width - small.x;
            height = small.y + small.height - large.y;
            break;
        case 0x8: // 1000
            //return new Region(large.x, large.y, (large.x - (small.x + small.width)), (large.y - (small.y + small.height)), Math.max(large.coverage, small.coverage));
            x = large.x;
            y = large.y;
            width = small.x + small.width - large.x;
            height = small.y + small.height - large.y;
            break;
            
        case 0x3: // 0011
            //return new Region(small.x, small.y, small.width, large.y + large.height - small.y, Math.max(large.coverage, small.coverage));
            x = small.x;
            y = small.y;
            width = small.width;
            height = large.y + large.height - small.y;
            break;
        case 0x5: // 0101
            //return new Region(small.x, small.y, large.x + large.width - small.x, small.height, Math.max(large.coverage, small.coverage));
            x = small.x;
            y = small.y;
            width = large.x + large.width - small.x;
            height = small.height;
            break;
        case 0xa: // 1010
            //return new Region(large.x, small.y, small.x + small.width - large.x, small.height, Math.max(large.coverage, small.coverage));
            x = large.x;
            y = small.y;
            width = small.x + small.width - large.x;
            height = small.height;
            break;
        case 0xc: // 1100
            //return new Region(small.x, large.y, small.width, small.y + small.height - large.y, Math.max(large.coverage, small.coverage));
            x = small.x;
            y = large.y;
            width = small.width;
            height = small.y + small.height - large.y;
            break;
            
        default:
            return null;
        }
        
        return new RectangularRegion(x, y, width, height, Math.max(large.coverage, small.coverage));
    }
    
    public RectangularRegion combine(RectangularRegion other) {
        int minX = Math.min(this.x, other.x);
        int minY = Math.min(this.y, other.y);
        int maxX = Math.max(this.x + this.width, other.x + other.width);
        int maxY = Math.max(this.y + other.height, other.y + other.height);
        
        int combinedWidth = maxX - minX;
        int combinedHeight = maxY - minY;
        
        RectangularRegion intersection = getIntersection(other);
        
        double combinedCoverage;
        
        if (intersection == null) {
            combinedCoverage = (getArea() * this.coverage + other.getArea() * other.coverage) / (combinedWidth * combinedHeight);
        } else {
            combinedCoverage = ((getArea() - intersection.getArea()) * this.coverage
                             + (other.getArea() - intersection.getArea()) * other.coverage
                             + intersection.getArea() * intersection.coverage) / (combinedWidth * combinedHeight);
        }
        
        return new RectangularRegion(minX, minY, combinedWidth, combinedHeight, combinedCoverage);
    }
}
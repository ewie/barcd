package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A rectangle whose parallel pairs of sides are aligned with the X or Y axes.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class AxisAlignedRectangle extends Rectangle {
    private Point min;
    private Point max;
    
    public AxisAlignedRectangle(Point min, Point max) {
        this.min = min;
        this.max = max;
    }
    
    /**
     * @return the rectangle's vertex with minimum X- and Y-coordinate
     */
    public Point getMin() {
        return min;
    }
    
    /**
     * @return the rectangle's vertex with maximum X- and Y-coordinate
     */
    public Point getMax() {
        return max;
    }
    
    @Override
    public double getWidth() {
        return max.getX() - min.getX();
    }
    
    @Override
    public double getHeight() {
        return max.getY() - min.getY();
    }
    
    @Override
    public Point[] getPoints() {
        return new Point[] {
            min,
            new Point(max.getX(), min.getY()),
            max,
            new Point(min.getX(), max.getY())
        };
    }
    
    @Override
    public boolean contains(Point p) {
        double x = p.getX();
        double y = p.getY();
        return x >= min.getX() && x <= max.getX()
            && y >= min.getY() && y <= max.getY();
    }
    
    /**
     * Create an axis aligned rectangle enclosing the given polygon.
     * 
     * @param polygon
     * 
     * @return the enclosing axis aligned rectangle
     */
    public static AxisAlignedRectangle createFromPolygon(ConvexPolygon polygon) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        
        Point[] coords = polygon.getPoints();
        
        for (int i = 0; i < coords.length; ++i) {
            double x = coords[i].getX();
            double y = coords[i].getY();
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        
        return new AxisAlignedRectangle(
            new Point(minX, minY),
            new Point(maxX, maxY));
    }
}

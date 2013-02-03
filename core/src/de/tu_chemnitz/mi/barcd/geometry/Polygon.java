package de.tu_chemnitz.mi.barcd.geometry;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Polygon {
    /**
     * @return the points forming the polygon's boundary in clockwise order
     */
    public Point[] getPoints();
    
    /**
     * @return the area enclosed by the polygon
     */
    public double computeArea();
}

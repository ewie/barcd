package de.tu_chemnitz.mi.barcd.geometry;

/**
 * An n-sided convex polygon.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class ConvexPolygon {
    /**
     * @return the points forming the polygon's boundary in clockwise order
     */
    public abstract Point[] getPoints();

    /**
     * @return the area enclosed by the polygon
     */
    public double computeArea() {
        Point[] points = getPoints();
        int area = 0;
        for (int i = 0; i < points.length; ++i) {
            Point p = points[i];
            Point q = points[(i+1) % points.length];
            area += q.getX() * p.getY() - p.getX() * q.getY();
        }
        return Math.abs(area / 2);
    }

    /**
     * Test if the polygon contains a certain point.
     *
     * Taken from {@link http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html}
     *
     * @param p the point to test
     *
     * @return true if the polygon contains the point
     */
    public boolean contains(Point p) {
        Point[] points = getPoints();
        int n = points.length;
        double x = p.getX();
        double y = p.getY();
        boolean contains = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = points[i].getX();
            double yi = points[i].getY();
            double xj = points[j].getX();
            double yj = points[j].getY();
            if ((yi > y) != (yj > y) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                contains = !contains;
            }
        }
        return contains;
    }
}

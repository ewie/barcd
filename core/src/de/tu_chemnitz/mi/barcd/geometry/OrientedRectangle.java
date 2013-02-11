package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A rectangle which is rotated by a certain angle.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class OrientedRectangle extends Rectangle {
    private Point p;
    private Vector v;
    private Vector w;
    
    public OrientedRectangle(Point p, Point q, Point r) {
        this.p = p;
        v = new Vector(p, q);
        w = new Vector(p, r);
        if (Math.abs(v.getAngle(w)) > 1e-5) {
            throw new IllegalArgumentException("the given points must form a right angle");
        }
    }
    
    /**
     * @return the angle of rotation
     */
    public double getRotationAngle() {
        return v.getAngle(new Vector(1, 0));
    }
    
    @Override
    public double getWidth() {
        return v.getLength();
    }
    
    @Override
    public double getHeight() {
        return w.getLength();
    }
    
    @Override
    public Point[] getPoints() {
        Point q = p.translate(v);
        Point r = q.translate(w);
        Point s = p.translate(w);
        return new Point[] { p, q, r, s };
    }
    
    /**
     * Create the oriented rectangle with minimum area enclosing the given
     * polygon.
     * 
     * @param polygon
     * 
     * @return the enclosing oriented rectangle
     */
    public static OrientedRectangle createFromPolygon(ConvexPolygon polygon) {
        Point[] hull = polygon.getPoints();
        
        // The vertices of the minimum area rectangle.
        double[] boxx = new double[3];
        double[] boxy = new double[3];
        
        double minArea = Double.MAX_VALUE;
        
        // The inverse rotation angle of the minimum area rectangle.
        double invAngle = 0;
        
        // Compute the minimum area enclosing rectangle using an approach
        // similar to rotation calipers. But in contrast to rotation a set of
        // calipers around the polygon we rotate the polygon for each edge so
        // the current edge coincides with the x-axis. We then compute the
        // axis-aligned bounding rectangle and remember it as the (rotated)
        // minimum are rectangle. Because the polygons are usually made up of
        // just a few (empirical evidence: ~20 vertices) vertices this method is
        // efficient enough.
        for (int i = 0; i < hull.length; ++i) {
            Point p = hull[i];
            Point q = hull[(i+1) % hull.length];
            double dx = q.getX() - p.getX();
            double dy = q.getY() - p.getY();
            double angle = Math.acos(dx / Math.sqrt(dx*dx + dy*dy));
            double c = Math.cos(angle);
            double s = Math.sin(angle);
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < hull.length; ++j) {
                Point r = hull[j];
                double x = r.getX() * c + r.getY() * -s;
                double y = r.getX() * s + r.getY() * c;
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }
            double area = (maxX - minX) * (maxY - minY);
            if (area < minArea) {
                minArea = area;
                invAngle = -angle;
                boxx[0] = minX;
                boxx[1] = maxX;
                boxx[2] = minX;
                boxy[0] = minY;
                boxy[1] = minY;
                boxy[2] = maxY;
            }
        }
        
        // Rotate the minimum area rectangle back using the inverse angle.
        double c = Math.cos(invAngle);
        double s = Math.sin(invAngle);
        Point[] box = new Point[3];
        for (int i = 0; i < 3; ++i) {
            box[i] = new Point(
                boxx[i] * c + boxy[i] * -s,
                boxx[i] * s + boxy[i] * c);
        }
        
        return new OrientedRectangle(box[0], box[1], box[2]);
    }
}

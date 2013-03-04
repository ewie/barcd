package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A 2-dimensional point.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Point {
    private double x;
    private double y;

    /**
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Translate the point by a given vector.
     *
     * @param v the translation vector
     *
     * @return a new point with the translated coordinates
     */
    public Point translate(Vector v) {
        return new Point(x + v.getX(), y + v.getY());
    }
}
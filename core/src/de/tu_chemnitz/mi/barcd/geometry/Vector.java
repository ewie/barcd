package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A 2-dimensional vector.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Vector {
    private final double x;
    private final double y;

    /**
     * @param x the x-component
     * @param y the y-component
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a vector from one point to another.
     *
     * @param p the start of the vector
     * @param q the end of the vector
     */
    public Vector(Point p, Point q) {
        this(q.getX() - p.getX(), q.getY() - p.getY());
    }

    /**
     * @return the x-component
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y-component
     */
    public double getY() {
        return y;
    }

    /**
     * @return the vector's length
     */
    public double getLength() {
        return Math.sqrt(inner(this));
    }

    /**
     * Compute the angle between this and a given vector.
     *
     * @param other the other vector
     *
     * @return the angle between the two vectors in radiant
     */
    public double computeAngle(Vector other) {
        return inner(other) / (getLength() * other.getLength());
    }

    /**
     * Compute the inner product of this and a given vector
     *
     * @param other the other vector
     *
     * @return the inner product of the two vectors
     */
    private double inner(Vector other) {
        return x * other.x + y * other.y;
    }
}

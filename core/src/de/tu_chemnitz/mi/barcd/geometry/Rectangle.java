/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A rectangular 4-sided polygon.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class Rectangle extends ConvexPolygon {
    /**
     * @return the rectangle's width
     */
    public abstract double getWidth();

    /**
     * @return the rectangle's height
     */
    public abstract double getHeight();

    @Override
    public double computeArea() {
        return getWidth() * getHeight();
    }

    @Override
    public final int getSize() {
        return 4;
    }
}

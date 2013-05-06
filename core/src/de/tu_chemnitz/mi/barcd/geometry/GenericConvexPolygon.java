/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.geometry;

/**
 * A generic n-sides convex polygon.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class GenericConvexPolygon extends ConvexPolygon {
    private final Point[] vertices;

    /**
     * Create a convex polygon from some vertices.
     *
     * @param vertices the convex polygon's vertices
     */
    public GenericConvexPolygon(Point[] vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("create a convex polygon with at least 3 vertices");
        }
        this.vertices = vertices;
    }

    @Override
    public Point[] getVertices() {
        return vertices;
    }
}

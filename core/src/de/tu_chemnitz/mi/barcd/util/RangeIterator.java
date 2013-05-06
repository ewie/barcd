/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.util;

import java.util.Iterator;

/**
 * The iterator for a {@link Range}.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RangeIterator implements Iterator<Long> {
    private final Range range;
    private long index = 0;

    /**
     * @param range
     */
    public RangeIterator(Range range) {
        this.range = range;
    }

    @Override
    public boolean hasNext() {
        return index < range.getLength();
    }

    @Override
    public Long next() {
        return range.get(index++);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
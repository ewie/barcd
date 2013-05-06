/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.util;

/**
 * A range of non-negative longs.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Range implements Iterable<Long> {
    private final long start;
    private final long end;
    private final long step;
    private final long length;

    /**
     * Create a range with a custom step size.
     *
     * @param start the inclusive non-negative start
     * @param end the exclusive non-negative end
     * @param step the step size, must not be zero
     */
    public Range(long start, long end, long step) {
        if (step == 0) {
            throw new IllegalArgumentException("step must not be zero");
        }

        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("start and step must be non-negative");
        }

        if (start < end && step < 0) {
            throw new IllegalArgumentException("step must be positive");
        } else if (start > end && step > 0) {
            throw new IllegalArgumentException("step must be negative");
        }

        this.start = start;
        this.end = end;
        this.step = step;

        long len = (end - start) / step;

        if ((end - start) % step != 0) {
            len += 1;
        }

        length = len;
    }
    /**
     * Create a range for every integer in [start, end) or (end, start].
     *
     * The step size will be 1 if start > end or -1 if start < end.
     *
     * @param start the inclusive non-negative start
     * @param end the exclusive non-negative end
     */
    public Range(long start, long end) {
        this(start, end, start < end ? 1 : -1);
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the exclusive end
     */
    public long getEnd() {
        return end;
    }

    /**
     * @return the step size
     */
    public long getStep() {
        return step;
    }

    /**
     * @return the number of integers within the range
     */
    public long getLength() {
        return length;
    }

    /**
     * Get the integer at a given zero-based index.
     *
     * @param index the index
     *
     * @return the integer at the given index
     */
    public long get(long index) {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException(
                String.format("index must be in %d..%d", 0, length));
        }
        return start + step * index;
    }

    @Override
    public RangeIterator iterator() {
        return new RangeIterator(this);
    }
}

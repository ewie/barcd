package de.tu_chemnitz.mi.barcd.util;

/**
 * A range of integers.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Range implements Iterable<Integer> {
    private final int start;
    private final int end;
    private final int step;
    private final int length;

    /**
     * Create a range with a custom step size.
     *
     * @param start the inclusive start
     * @param end the exclusive end
     * @param step the step size, must not be zero
     */
    public Range(int start, int end, int step) {
        if (step == 0) {
            throw new IllegalArgumentException("step must not be zero");
        }

        if (start < end && step < 0) {
            throw new IllegalArgumentException("step must be positive");
        } else if (start > end && step > 0) {
            throw new IllegalArgumentException("step must be negative");
        }

        this.start = start;
        this.end = end;
        this.step = step;

        int len = (end - start) / step;

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
     * @param start the inclusive start
     * @param end the exclusive end
     */
    public Range(int start, int end) {
        this(start, end, start < end ? 1 : -1);
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the exclusive end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @return the step size
     */
    public int getStep() {
        return step;
    }

    /**
     * @return the number of integers within the range
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the integer at a given zero-based index.
     *
     * @param index the index
     *
     * @return the integer at the given index
     */
    public int get(int index) {
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

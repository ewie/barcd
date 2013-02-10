package de.tu_chemnitz.mi.barcd.util;

/**
 * The iterator for a {@link Range}.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class RangeIterator implements java.util.Iterator<Integer> {
    private Range range;
    private int index = 0;

    public RangeIterator(Range range) {
        this.range = range;
    }

    @Override
    public boolean hasNext() {
        return index < range.getLength();
    }

    @Override
    public Integer next() {
        return range.get(index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
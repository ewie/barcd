package de.tu_chemnitz.mi.barcd.util;

/**
 * A range of integers.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Range implements Iterable<Integer> {
    private int start;
    private int end;
    private int step;
    private int length;

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
        this.length = (end - start) / step + ((end - start) % step == 0 ? 0 : 1);
    }
    
    public Range(int start, int end) {
        this(start, end, start < end ? 1 : -1);
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
    
    public int getStep() {
        return step;
    }
    
    public int getLength() {
        return length;
    }
    
    public int get(int index) {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException(
                String.format("index must be in %d..%d", 0, length));
        }
        return start + step * index;
    }
    
    public RangeIterator iterator() {
        return new RangeIterator(this);
    }
}

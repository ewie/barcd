/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator providing only those items of an underlying iterator for which a
 * predicate holds true.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 *
 * @param <T> type of items
 */
public class FilteredIterator<T> implements Iterator<T> {
    private Iterator<T> iterator;

    private Predicate<T> predicate;

    /**
     * The item got via {@link #peek()}.
     */
    private T peekedItem;

    /**
     * @param iterator the underlying iterator
     * @param predicate the predicate to be evaluated for each item in the
     *   underlying iterator
     */
    public FilteredIterator(Iterator<T> iterator, Predicate<T> predicate) {
        this.predicate = predicate;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return peek() != null;
    }

    @Override
    public T next() {
        T region = consume();
        if (region == null) {
            throw new NoSuchElementException();
        }
        return region;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Consume the next item for which the predicate holds true.
     *
     * @return the next item or null if no item fulfils the predicate
     */
    private T consume() {
        T region = peek();
        peekedItem = null;
        return region;
    }

    /**
     * Get the next item for which the predicate holds true without consuming
     * it.
     *
     * @return the next item or null if no item fulfils the predicate
     */
    private T peek() {
        if (peekedItem == null) {
            while (iterator.hasNext()) {
                peekedItem = iterator.next();
                if (predicate.isTrue(peekedItem)) {
                    break;
                }
            }
        }
        return peekedItem;
    }
}

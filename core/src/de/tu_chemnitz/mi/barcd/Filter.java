/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 *
 * @param <T> type of items the filter operates on
 */
public interface Filter<T> {
    /**
     * Create a filtered view of the given values.
     *
     * @param values the values to filter
     *
     * @return the filtered view
     */
    public Iterable<T> filter(Iterable<T> values);
}

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

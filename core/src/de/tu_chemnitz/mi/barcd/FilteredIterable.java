package de.tu_chemnitz.mi.barcd;

import java.util.Iterator;

/**
 * An iterable filtering the items of an underlying iterable using a predicate.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 *
 * @param <T> type of items
 */
public class FilteredIterable<T> implements Iterable<T> {
    /**
     * The underlying iterable.
     */
    private Iterable<T> iterable;

    /**
     * The predicate evaluating each item.
     */
    private Predicate<T> predicate;

    /**
     * @param iterable the underlying iterable
     * @param predicate the predicate to be used to select items from the
     *   underlying iterable
     */
    public FilteredIterable(Iterable<T> iterable, Predicate<T> predicate) {
        this.iterable = iterable;
        this.predicate = predicate;
    }

    @Override
    public Iterator<T> iterator() {
        return new FilteredIterator<T>(iterable.iterator(), predicate);
    }
}

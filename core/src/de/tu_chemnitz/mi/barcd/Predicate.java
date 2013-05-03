package de.tu_chemnitz.mi.barcd;

/**
 * A predicate in the mathematical sense.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 *
 * @param <T> type of values the predicate evaluates
 */
public interface Predicate<T> {
    /**
     * Check if the predicate holds true on the given value.
     *
     * @param value the value to check
     *
     * @return true if the predicate holds true
     */
    public boolean isTrue(T value);
}

package de.tu_chemnitz.mi.barcd.app;

/**
 * A collection of utility methods.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Util {
    /**
     * Pad a string with SP characters (U+0020) to a given length on the right.
     *
     * @param s the string to pad
     * @param n the number of characters the string should contain after padding
     *
     * @return the padded string
     */
    public static String padRight(String s, int n) {
        n = Math.max(n, 1);
        String fmt = "%-" + n  + "s";
        return String.format(fmt, s);
    }
}

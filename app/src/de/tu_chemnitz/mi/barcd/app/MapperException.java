/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class MapperException extends Exception {
    private static final long serialVersionUID = 1L;

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(String message) {
        this(message, null);
    }

    public MapperException(Throwable cause) {
        this(null, cause);
    }

    public MapperException() {
        this(null, null);
    }
}

/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class SerializerException extends Exception {
    private static final long serialVersionUID = 1L;

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerException(String message) {
        this(message, null);
    }

    public SerializerException(Throwable cause) {
        this(null, cause);
    }

    public SerializerException() {
        this(null, null);
    }
}

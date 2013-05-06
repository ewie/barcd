/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class TerminalException extends Exception {
    private static final long serialVersionUID = 1L;

    public TerminalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TerminalException(String message) {
        this(message, null);
    }

    public TerminalException(Throwable cause) {
        this(null, cause);
    }

    public TerminalException() {
        this(null, null);
    }
}

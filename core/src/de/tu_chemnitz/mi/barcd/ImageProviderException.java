/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageProviderException extends Exception {
    private static final long serialVersionUID = 1L;

    public ImageProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageProviderException(String message) {
        this(message, null);
    }

    public ImageProviderException(Throwable cause) {
        this(null, cause);
    }

    public ImageProviderException() {
        this(null, null);
    }
}

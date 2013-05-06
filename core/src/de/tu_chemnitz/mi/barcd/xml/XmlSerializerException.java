/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.xml;

import de.tu_chemnitz.mi.barcd.SerializerException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class XmlSerializerException extends SerializerException {
    private static final long serialVersionUID = 1L;

    public XmlSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlSerializerException(String message) {
        this(message, null);
    }

    public XmlSerializerException(Throwable cause) {
        this(null, cause);
    }

    public XmlSerializerException() {
        this(null, null);
    }
}

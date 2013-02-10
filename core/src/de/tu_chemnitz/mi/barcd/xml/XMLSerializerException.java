package de.tu_chemnitz.mi.barcd.xml;

import de.tu_chemnitz.mi.barcd.SerializerException;

public class XMLSerializerException extends SerializerException {
    private static final long serialVersionUID = 1L;

    public XMLSerializerException() {
        this(null, null);
    }
    
    public XMLSerializerException(String message) {
        this(message, null);
    }
    
    public XMLSerializerException(Throwable cause) {
        this(null, cause);
    }

    public XMLSerializerException(String message, Throwable cause) {
        super(message, cause);
    }
}

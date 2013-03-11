package de.tu_chemnitz.mi.barcd.xml;

import de.tu_chemnitz.mi.barcd.SerializerException;

public class XmlSerializerException extends SerializerException {
    private static final long serialVersionUID = 1L;

    public XmlSerializerException() {
        this(null, null);
    }
    
    public XmlSerializerException(String message) {
        this(message, null);
    }
    
    public XmlSerializerException(Throwable cause) {
        this(null, cause);
    }

    public XmlSerializerException(String message, Throwable cause) {
        super(message, cause);
    }
}

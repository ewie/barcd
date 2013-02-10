package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class SerializerException extends Exception {
    private static final long serialVersionUID = 1L;

    public SerializerException() {
        this(null, null);
    }
    
    public SerializerException(String message) {
        this(message, null);
    }
    
    public SerializerException(Throwable cause) {
        this(null, cause);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }
}

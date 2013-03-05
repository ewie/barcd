package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ExtractorException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExtractorException() {
        super();
    }

    public ExtractorException(String message) {
        super(message);
    }

    public ExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractorException(Throwable cause) {
        super(cause);
    }
}

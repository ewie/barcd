package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
class ApplicationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super( cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
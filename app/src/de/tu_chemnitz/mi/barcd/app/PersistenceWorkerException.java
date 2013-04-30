package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class PersistenceWorkerException extends Exception {
    private static final long serialVersionUID = 1L;

    public PersistenceWorkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceWorkerException(String message) {
        this(message, null);
    }

    public PersistenceWorkerException(Throwable cause) {
        this(null, cause);
    }

    public PersistenceWorkerException() {
        this(null, null);
    }
}

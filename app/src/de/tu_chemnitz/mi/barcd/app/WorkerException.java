package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class WorkerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WorkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkerException(String message) {
        this(message, null);
    }

    public WorkerException(Throwable cause) {
        this(null, cause);
    }

    public WorkerException() {
        this(null, null);
    }
}

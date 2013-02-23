package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class WorkerException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public WorkerException() {
        super();
    }
    
    public WorkerException(String message) {
        super(message);
    }
    
    public WorkerException(Throwable cause) {
        super(cause);
    }
    
    public WorkerException(String message, Throwable cause) {
        super(message, cause);
    }
}

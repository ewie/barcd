package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class TerminalException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public TerminalException() {
        super();
    }
    
    public TerminalException(String message) {
        super(message);
    }
    
    public TerminalException(Throwable cause) {
        super(cause);
    }
    
    public TerminalException(String message, Throwable cause) {
        super(message, cause);
    }
}

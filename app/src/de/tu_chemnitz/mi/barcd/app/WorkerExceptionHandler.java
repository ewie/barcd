package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface WorkerExceptionHandler {
    public void handleException(Worker worker, Exception exception);
}

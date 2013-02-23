package de.tu_chemnitz.mi.barcd.app;

import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.ImageProviderException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Worker implements Runnable {
    public static interface ExceptionHandler {
        public void handle(Exception exception);
    }
    
    private Extractor extractor;
    
    private ExceptionHandler exceptionHandler;
    
    private boolean terminated;

    public Worker(Extractor extractor) {
        this.extractor = extractor;
        terminated = false;
    }
    
    public void teminate() {
        terminated = true;
    }
    
    public void setExceptionHandler(ExceptionHandler handler) {
        this.exceptionHandler = handler;
    }

    /**
     * @throws RuntimeException
     *   if the extractor throws an exception and no exception handler is set
     */
    @Override
    public void run() {
        try {
            while (!terminated) {
                extractor.processNextImage();
            }
        } catch (ImageProviderException ex) {
            if (exceptionHandler == null) {
                throw new RuntimeException(ex);
            } else {
                exceptionHandler.handle(ex);
            }
        }
    }
}

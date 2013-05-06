/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

/**
 * An implementation of {@link Runnable} allowing exceptions to be thrown which
 * will then be wrapped in a {@link WorkerException}. Because {@link Runnable}
 * cannot throw exceptions from {@link Runnable#run()}.
 *
 * Also allows the routine to be terminated from the outside.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class Worker implements Runnable {
    private boolean terminate = false;

    private WorkerExceptionHandler exceptionHandler;

    /**
     * Set the handler to process exceptions thrown by {@link #work()}.
     *
     * @param exceptionHandler the exception handler
     */
    public void setExceptionHandler(WorkerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Signal the worker to terminate on the next possible occasion.
     */
    public void terminate() {
        terminate = true;
    }

    /**
     * Indicates if the worker should terminate after checking this condition.
     *
     * @return true if the worker should terminate
     */
    protected boolean shouldTerminate() {
        return terminate;
    }

    /**
     * Invokes {@link #work()} and delegates any thrown exception to the
     * registered exception handler.
     *
     * @throws RuntimeException if no exception handler is set
     */
    @Override
    public final void run() {
        try {
            work();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    /**
     * Implements the actual worker routine.
     *
     * @throws Exception
     */
    protected abstract void work() throws Exception;

    private void handleException(Exception exception) {
        if (exceptionHandler == null) {
            throw new WorkerException(exception);
        }
        exceptionHandler.handleException(this, exception);
    }
}

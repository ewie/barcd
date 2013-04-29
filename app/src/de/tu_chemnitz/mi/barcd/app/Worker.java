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
        } catch (WorkerException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WorkerException(ex);
        }
    }

    /**
     * Implements the actual worker routine.
     *
     * @throws Exception
     *
     * @see Runnable#run
     */
    protected abstract void work() throws Exception;
}

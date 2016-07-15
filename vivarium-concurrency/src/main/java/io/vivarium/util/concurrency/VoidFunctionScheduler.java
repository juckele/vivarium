package io.vivarium.util.concurrency;

import com.google.common.base.Preconditions;

/**
 * The VoidFunctionScheduler can be used to execute a VoidFunction (presumably with side effects) periodically, or when
 * requested external input. External requests to execute the VoidFunction will cause the VoidFunction to immediately
 * re-execute if they come in during the VoidFunction execution. Periodic re-runs will not exhibit this queue behavior
 * by design.
 */
public class VoidFunctionScheduler implements StartableStoppable
{
    // Dependencies
    private final VoidFunction _function;
    private final long _repeatEveryInMS;

    // State variables
    private boolean _running = false;
    private boolean _stopped = false;
    private boolean _functionExecuting;
    private boolean _functionQueued;

    // Helper thread
    PeroidicFunctionExecutor _helperThread;

    public VoidFunctionScheduler(VoidFunction function, long repeatEveryInMS)
    {
        _function = function;
        _repeatEveryInMS = repeatEveryInMS;
        _helperThread = new PeroidicFunctionExecutor();
    }

    /**
     * Starts the scheduler
     */
    @Override
    public synchronized void start()
    {
        Preconditions.checkNotNull(_stopped);
        _running = true;
        _functionExecuting = false;
        _functionQueued = false;
        _helperThread.start();
    }

    /**
     * Stops the scheduler
     */
    @Override
    public synchronized void stop()
    {
        _running = false;
        _stopped = true;
        _functionExecuting = false;
        _functionQueued = false;
    }

    /**
     * This method causes the object to immediately start the function, or queue the function to start as soon as the
     * current execution is completed.
     */
    public synchronized void execute()
    {
        startFunctionExecute(true);
    }

    /**
     * This method causes the object to immediately start the function, or if the function is currently executing,
     * potentially queue another execution of the function.
     *
     * @param force
     *            Whether the function should be forced to execute. If this value is false, and the function is
     *            currently executing, this method call will be ignore. If this value is true, and function is currently
     *            executing, another function execution will be queued up to execute as soon as the current execution
     *            completes. If another function execution is already queued, this parameter has no effect and this
     *            method call will be ignored.
     */
    private synchronized void startFunctionExecute(boolean force)
    {
        // If the scheduler is not running, exit immediately.
        if (!_running)
        {
            return;
        }

        // If an external event has triggered this function execution, we want to redo the function execution as soon as
        // the current one ends, so queue another function execution.
        if (force && _functionExecuting)
        {
            _functionQueued = true;
        }

        // Start an function execution if we don't have one executing already. We do this in a thread to keep the
        // calling thread from blocking for the whole duration.
        if (!_functionExecuting)
        {
            _functionExecuting = true;
            new FunctionThread().start();
        }
    }

    /**
     * Marks the function as no longer executing, and consumes the function execution queue, starting another function
     * execution if required.
     */
    private synchronized void endFunctionExecute()
    {
        // We're done executing the function.
        _functionExecuting = false;

        // If an function has been queued while we were executing, immediately start a new function execution.
        if (_functionQueued)
        {
            _functionQueued = false;
            startFunctionExecute(false);
        }
    }

    /**
     * Triggers enforcement periodically.
     */
    private class PeroidicFunctionExecutor extends Thread
    {
        @Override
        public void run()
        {
            while (_running)
            {
                try
                {
                    startFunctionExecute(false);
                    Thread.sleep(_repeatEveryInMS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes enforcement in a thread to allow other threads to not block. The startEnforce and endEnforce methods are
     * synchronized, so this class allows the startEnforce method to return and prevent the caller from blocking.
     */
    private class FunctionThread extends Thread
    {
        @Override
        public void run()
        {
            _function.execute();
            endFunctionExecute();
        }
    }
}

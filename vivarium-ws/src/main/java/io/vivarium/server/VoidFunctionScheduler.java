/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

/**
 * The VoidFunctionScheduler can be used to execute a VoidFunction (presumably with side effects) periodically, or when
 * requested external input. External requests to execute the VoidFunction will cause the VoidFunction to immediately
 * re-execute if they come in during the VoidFunction execution. Periodic re-runs will not exhibit this queue behavior
 * by design.
 */
public class VoidFunctionScheduler
{
    // Dependencies
    private final VoidFunction _enforcer;
    private final long _enforceTimeGapInMS;

    // State variables
    private boolean _running;
    private boolean _enforceRunning;
    private boolean _enforceQueued;

    // Helper thread
    PeroidicEnforcer _helperThread;

    public VoidFunctionScheduler(VoidFunction enforcer, long enforceTimeGapInMS)
    {
        _enforcer = enforcer;
        _enforceTimeGapInMS = enforceTimeGapInMS;
        _helperThread = new PeroidicEnforcer();
    }

    /**
     * Starts the scheduler
     */
    public void start()
    {
        _running = true;
        _enforceRunning = false;
        _enforceQueued = false;
        _helperThread.start();
    }

    /**
     * This method causes the object to immediately start enforcement, or queue enforcement to start as soon as the
     * current pass is completed.
     */
    public synchronized void enforce()
    {
        startEnforce(true);
    }

    /**
     * This method causes the object to immediately start enforcement, or if enforcement is currently running,
     * potentially queue another pass of enforcement.
     *
     * @param force
     *            Whether enforcement should be forced to run. If this value is false, and the enforcement is currently
     *            running, this method call will be ignore. If this value is true, and enforcement is currently running,
     *            another enforcement pass will be queued up to run as soon as the current pass completes. If another
     *            enforcement pass is already queued, this parameter has no effect and this method call will be ignored.
     */
    private synchronized void startEnforce(boolean force)
    {
        // If an external event has triggered this enforce, we want to redo the enforce as soon as the current one ends,
        // so queue another enforce action.
        if (force && _enforceRunning)
        {
            _enforceQueued = true;
        }

        // Start an enforce action if we don't have one running. We do this in a thread to keep the calling thread from
        // blocking for the whole duration.
        if (!_enforceRunning)
        {
            _enforceRunning = true;
            new ReactiveEnforcer().start();
        }
    }

    /**
     * Marks enforcement as no longer running, and consumes the enforcement queue, starting another enforcement pass if
     * required.
     */
    private synchronized void endEnforce()
    {
        // We're done running the enforce.
        _enforceRunning = false;

        // If an enforce has been queued while we were enforcing, immediately start a new enforce
        if (_enforceQueued)
        {
            _enforceQueued = false;
            startEnforce(false);
        }
    }

    /**
     * Triggers enforcement periodically.
     */
    private class PeroidicEnforcer extends Thread
    {
        @Override
        public void run()
        {
            while (_running)
            {
                try
                {
                    startEnforce(false);
                    Thread.sleep(_enforceTimeGapInMS);
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
    private class ReactiveEnforcer extends Thread
    {
        @Override
        public void run()
        {
            _enforcer.execute();
            endEnforce();
        }
    }
}

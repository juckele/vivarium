package com.johnuckele.vivarium.visualization.animation;

public abstract class SchedulingDelegate
{
    protected static final long PAUSE_SLEEP = 25;
    protected static final long ANIMATION_SLEEP = 25;
    protected static final long SIMULATION_SLEEP = 1000;

    protected boolean _paused;
    protected boolean _started;

    public final void start(Visualizer visualizer)
    {
        registerVisualizer(visualizer);
        if (!_started)
        {
            _started = true;
            startFirstTime();
        }
        else
        {
            _paused = false;
        }
    }

    protected abstract void registerVisualizer(Visualizer visualizer);

    public abstract void startFirstTime();

    public void stop()
    {
        _paused = true;
    }
}

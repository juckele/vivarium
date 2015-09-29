package com.johnuckele.vivarium.visualization.animation;

public class ThreadScheduler extends SchedulingDelegate
{
    private SimulationThread _simulationThread;

    private AnimationThread _animationThread;

    public ThreadScheduler()
    {
        _simulationThread = new SimulationThread();
        _animationThread = new AnimationThread();
    }

    @Override
    public void startFirstTime()
    {
        _simulationThread.start();
        _animationThread.start();
    }

    private class AnimationThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    if (_paused)
                    {
                        Thread.sleep(PAUSE_SLEEP);
                    }
                    _visualizer.renderWorld();
                    Thread.sleep(ANIMATION_SLEEP);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    private class SimulationThread extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    if (_paused)
                    {
                        Thread.sleep(PAUSE_SLEEP);
                    }
                    _visualizer.tickWorld();
                    Thread.sleep(SIMULATION_SLEEP);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

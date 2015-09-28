package com.johnuckele.vivarium.visualization.animation;

import com.johnuckele.vivarium.core.World;

public class Visualizer
{
    private static final long PAUSE_SLEEP = 25;
    private static final long ANIMATION_SLEEP = 25;
    private static final long SIMULATION_SLEEP = 1000;

    private boolean _paused;
    private boolean _started;
    // Simulation variables
    private SimulationThread _simulationThread;
    private World _world;

    // Animation variables
    private AnimationThread _animationThread;
    private GraphicalSystem _graphicalSystem;

    public Visualizer(World world, GraphicalSystem graphicalSystem)
    {
        _world = world;
        _graphicalSystem = graphicalSystem;
        _simulationThread = new SimulationThread();
        _animationThread = new AnimationThread();
    }

    public void start()
    {
        if (!_started)
        {
            _started = true;
            _simulationThread.start();
            _animationThread.start();
        }
        else
        {
            _paused = false;
        }
    }

    public void stop()
    {
        _paused = true;
    }

    public World getWorld()
    {
        return _world;
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
                    WorldRenderer.renderWorld(_graphicalSystem, _world, _world, 0);
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
                    _world.tick();
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

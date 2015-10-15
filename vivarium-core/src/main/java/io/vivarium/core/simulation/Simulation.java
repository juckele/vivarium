package io.vivarium.core.simulation;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.vivarium.core.World;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Simulation extends VivariumObject
{
    @SerializedParameter
    private World _world;
    @SerializedParameter
    private ArrayList<Hook> _hooks = new ArrayList<Hook>();
    @SerializedParameter
    private boolean _completed = false;

    public Simulation()
    {
    }

    public Simulation(World world)
    {
        _world = world;
    }

    public void addHook(Hook hook)
    {
        _hooks.add(hook);
    }

    public void runUntilCompletion()
    {
        while (!_completed)
        {
            _world.tick();
            applyHooks();
        }
    }

    public void runForUpTo(long maxTicks)
    {
        long runTicks = 0;
        while (runTicks < maxTicks && !_completed)
        {
            _world.tick();
            applyHooks();
            runTicks++;
        }
    }

    public void runForUpTo(long timePeriod, TimeUnit timeUnit)
    {
        long maxMilliseconds = TimeUnit.MILLISECONDS.convert(timePeriod, timeUnit);
        long startTime = System.currentTimeMillis();
        long runTime = 0;
        while (runTime < maxMilliseconds && !_completed)
        {
            _world.tick();
            applyHooks();
            runTime = System.currentTimeMillis() - startTime;
        }
    }

    public void runForUpTo(long maxTicks, long timePeriod, TimeUnit timeUnit)
    {
        long runTicks = 0;
        long maxMilliseconds = TimeUnit.MILLISECONDS.convert(timePeriod, timeUnit);
        long startTime = System.currentTimeMillis();
        long runTime = 0;
        while (runTicks < maxTicks && runTime < maxMilliseconds && !_completed)
        {
            _world.tick();
            applyHooks();
            runTicks++;
            runTime = System.currentTimeMillis() - startTime;
        }
    }

    private void applyHooks()
    {
        for (int i = 0; i < _hooks.size(); i++)
        {
            Hook hook = _hooks.get(i);
            hook.apply(this, _world);
        }
    }

    public void completeSimulation()
    {
        _completed = true;
    }

    public boolean isCompleted()
    {
        return _completed;
    }

    public World getWorld()
    {
        return _world;
    }

    @Override
    public void finalizeSerialization()
    {
    }

}

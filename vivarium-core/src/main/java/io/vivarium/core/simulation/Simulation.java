/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core.simulation;

import java.util.concurrent.TimeUnit;

import io.vivarium.core.World;

public class Simulation
{
    public static void runForUpTo(World world, long maxTicks)
    {
        long runTicks = 0;
        while (runTicks < maxTicks)
        {
            world.tick();
            runTicks++;
        }
    }

    public static void runForUpTo(World world, long timePeriod, TimeUnit timeUnit)
    {
        long maxMilliseconds = TimeUnit.MILLISECONDS.convert(timePeriod, timeUnit);
        long startTime = System.currentTimeMillis();
        long runTime = 0;
        while (runTime < maxMilliseconds)
        {
            world.tick();
            runTime = System.currentTimeMillis() - startTime;
        }
    }

    public static void runForUpTo(World world, long maxTicks, long timePeriod, TimeUnit timeUnit)
    {
        long runTicks = 0;
        long maxMilliseconds = TimeUnit.MILLISECONDS.convert(timePeriod, timeUnit);
        long startTime = System.currentTimeMillis();
        long runTime = 0;
        while (runTicks < maxTicks && runTime < maxMilliseconds)
        {
            world.tick();
            runTicks++;
            runTime = System.currentTimeMillis() - startTime;
        }
    }
}

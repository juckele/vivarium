/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core.simulation;

import io.vivarium.core.World;
import io.vivarium.serialization.SerializedParameter;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class TickLimitHook extends Hook
{
    @SerializedParameter
    private int _tickLimit;

    @SuppressWarnings("unused") // Used by serialization engine
    private TickLimitHook()
    {
    }

    public TickLimitHook(int tickLimit)
    {
        _tickLimit = tickLimit;
    }

    @Override
    public void apply(Simulation simulation, World world)
    {
        if (world.getTickCounter() >= _tickLimit)
        {
            simulation.completeSimulation();
        }
    }

}

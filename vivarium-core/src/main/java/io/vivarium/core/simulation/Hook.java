package io.vivarium.core.simulation;

import io.vivarium.core.World;
import io.vivarium.serialization.MapSerializer;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class Hook implements MapSerializer
{
    @Override
    public void finalizeSerialization()
    {
    }

    public abstract void apply(Simulation simulation, World world);
}

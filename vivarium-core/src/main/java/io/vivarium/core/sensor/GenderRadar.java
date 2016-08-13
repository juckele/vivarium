package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class GenderRadar extends Sensor
{
    public GenderRadar()
    {
        super(1);
    }

    @Override
    public void performSensing(World w, double[] inputs, int index, int r, int c, Creature creature)
    {
        inputs[index] = creature.getIsFemale() ? 1 : 0;
    }

    @Override
    public void finalizeSerialization()
    {
    }
}

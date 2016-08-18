package io.vivarium.core.sensor;

import io.vivarium.core.Creature;
import io.vivarium.core.World;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class HealthSensor extends Sensor
{
    public HealthSensor()
    {
        super(1);
    }

    @Override
    public void performSensing(World w, double[] inputs, int index, int r, int c, Creature creature)
    {
        inputs[index] = ((double) creature.getHealth()) / creature.getCreatureBlueprint().getMaximumHealth();
    }

    @Override
    public void finalizeSerialization()
    {
    }
}
